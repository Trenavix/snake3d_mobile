package core;

import android.opengl.GLES30;
import android.opengl.Matrix;
import android.os.Build;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;

import core.objects.BehavObject;
import core.objects.GameObject;
import core.objects.PlayerObject;
import core.objects.Status;
import functions.DoubleObject;
import functions.conversions.Mesh32ToMesh;
import graphics.Camera;
import graphics.Mesh;
import graphics.Model;
import graphics.Shader;
import graphics.generators.MeshGenerators;

import org.joml.Vector2f;
import org.joml.Vector3f;

import static functions.Buffers.floatArrayToBuffer;

import androidx.annotation.RequiresApi;

public class Scene
{
    private ArrayList<Model> models = new ArrayList<Model>();
    private DoubleObject masterVtxIdxBuffers;
    private Integer playerObjectIndex;
    private LinkedList<GameObject> objects = new LinkedList<>();
    private ArrayList<GameObject> pendingObjects = new ArrayList<>();
    private ArrayList<Integer> levelModelIndices = new ArrayList<Integer>();
    private Integer BGModelIndex;
    private final int alphaModelIndex = 1;
    public Camera cam = new Camera(new Vector3f(0,0,0), new Vector2f(4.725f, 0), 0.005f, 3.f);//start looking backward (-1.5*pi)
    public boolean paused = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Scene(String[] sceneScript) throws ClassNotFoundException
    {
        for(String string : sceneScript)
        {
            String[] line = string.trim().split("\\s+"); // split it up by spaces
            switch(line[0]) //First "word" in each line (command)
            {
                case "BG_model":
                    BGModelIndex = Integer.parseInt(line[1]);
                    models.get(BGModelIndex).meshes.get(0).depthBufferWritingEnabled = false;
                    break;
                case "level_model":
                    int levelModelIdx = Integer.parseInt(line[1]);
                    levelModelIndices.add(levelModelIdx);
                    break;
                case "mesh32":
                    byte[] mesh32Data = AssetLoader.readFileAsByteArray(line[1]);
                    float meshScale = Float.parseFloat(line[2]);
                    Model mesh32 = Mesh32ToMesh.Mesh32ToModel(mesh32Data, meshScale);
                    mesh32.loadTexturesIntoGL();
                    models.add(mesh32);
                    break;
                case "model_grid": //generate Grid mesh
                    float gridWidth = Float.parseFloat(line[1]);
                    float gridHeight = Float.parseFloat(line[2]);
                    short partitions_x = Short.parseShort(line[3]);
                    short partitions_y = Short.parseShort(line[4]);
                    float offset_y = Float.parseFloat(line[5]);
                    int color = (int)Long.parseLong(line[6], 16);
                    Model gridMesh = MeshGenerators.generateGridMesh(gridWidth, gridHeight, partitions_x, partitions_y, offset_y, color);
                    gridMesh.loadTexturesIntoGL();
                    models.add(gridMesh);
                    objects.add(new GameObject(models.get(models.size()-1), new Vector3f(), new Vector3f(), 1.0f, 1.0f, models.size()));
                    break;
                case "behavObject":
                case "object":
                    int meshIdx = Integer.parseInt(line[1]);
                    Vector3f pos = new Vector3f(Float.parseFloat(line[2]), Float.parseFloat(line[3]), Float.parseFloat(line[4]));
                    Vector3f rot = new Vector3f(Float.parseFloat(line[5]), Float.parseFloat(line[6]), Float.parseFloat(line[7]));
                    float objectScale = Float.parseFloat(line[8]);
                    float objectRadius = Float.parseFloat(line[9]);
                    if(line.length > 10) objects.add(new BehavObject(models.get(meshIdx), pos, rot, objectScale, objectRadius, models.size(), line[10]));
                    else objects.add(new GameObject(models.get(meshIdx), pos, rot, objectScale, objectRadius, models.size()));
                    break;
                case "player":
                    int playerMeshIdx = Integer.parseInt(line[1]);
                    Vector3f playerPos = new Vector3f(Float.parseFloat(line[2]), Float.parseFloat(line[3]), Float.parseFloat(line[4]));
                    Vector3f playerRot = new Vector3f(Float.parseFloat(line[5]), Float.parseFloat(line[6]), Float.parseFloat(line[7]));
                    float playerScale = Float.parseFloat(line[8]);
                    objects.add(new PlayerObject(models.get(playerMeshIdx), playerPos, playerRot, playerScale, 0.07f, 0.3f, models.size()));
                    playerObjectIndex = objects.size()-1; //last index
                    break;
                //TODO: Implement more commands in scene script language
            }
        }
        processObjectsSingleThreaded(); //Allows initialising behavObjects TO INITIALISE before the master buffer generates
        masterVtxIdxBuffers = functions.Buffers.combineAllMeshBuffers(models);
        functions.Buffers.swapVertexBuffers(
                (float[])masterVtxIdxBuffers.firstObject, (int[])masterVtxIdxBuffers.secondObject);
    }

    public void addModel(Model newModel) { models.add(newModel); }
    public Model getModel(int meshIndex) { return models.get(meshIndex); }
    public ArrayList<Model> getAllModels() { return models; }
    public PlayerObject getPlayer() {return (PlayerObject)objects.get(playerObjectIndex); }
    public ArrayList<Integer> getLevelModelIndices() {return levelModelIndices; }
    public LinkedList<GameObject> getObjects() {return objects; }
    public int meshCount() {return models.size();}

    public void drawScene() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        addPendingObjects();
        //PROCESS OBJECTS IN PARALLEL FIRST
        processObjectsParallel();
        Shader.updateLightCountInGL();
        //NOW DRAW THE SCENE WITH OBJECTS
        Shader.currentLightCount = 0;
        GLES30.glUniform1f(Shader.GL_alphaTestUniLocation, 0.0f); //alphaTest disable
        drawBackground();
        for(int i = 0; i< levelModelIndices.size(); i++) //Draw level models
        {
            //if(i == alphaModelIndex) continue; //Alpha model
            getModel(levelModelIndices.get(i)).drawModel();
        }
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glDepthRangef(0.f, 1.f);
        GLES30.glDepthMask(true);
        GLES30.glDepthFunc(GLES30.GL_LESS);
        for(GameObject object : objects)
            object.drawObject();
        /*if(levelModelIndices.size() > 1) //Alpha level model
        {
            Matrix.setIdentityM(Renderer.worldMatrix, 0);
            GLES30.glDisable(GLES30.GL_CULL_FACE);
            getModel(levelModelIndices.get(alphaModelIndex)).drawModel();
            GLES30.glEnable(GLES30.GL_CULL_FACE);
        }*/
        GLES30.glUniform1f(Shader.GL_alphaTestUniLocation, 0.1f);
        GarbageCollectionRoutine();
    }

    public Camera getCamera()
    {return this.cam; }

    private void processObjectsParallel()
    {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {return;}
        objects.parallelStream().forEach((object) ->
        {
            Class objectClass = object.getClass();
            if(objectClass.equals(BehavObject.class))
            {
                try
                {
                    ((BehavObject)object).runBehaviour(this);
                }
                catch (NoSuchMethodException e) {e.printStackTrace();}
                catch (InvocationTargetException e) {e.printStackTrace();}
                catch (IllegalAccessException e) { e.printStackTrace(); }
            }
        });
    }
    private void processObjectsSingleThreaded()
    {
        for(GameObject object : objects)
        {
            Class objectClass = object.getClass();
            if(objectClass.equals(BehavObject.class))
            {
                try
                {
                    ((BehavObject)object).runBehaviour(this);
                }
                catch (NoSuchMethodException e) {e.printStackTrace();}
                catch (InvocationTargetException e) {e.printStackTrace();}
                catch (IllegalAccessException e) { e.printStackTrace(); }
            }
        }
    }

    private void GarbageCollectionRoutine()
    {
        //Object removal and mesh garbage collection below:
        for(int i=0; i<objects.size(); i++)
            if(objects.get(i).status == Status.DEAD)
            {
                Model objModel = objects.get(i).getModelReference();
                objects.remove(i);
                for(GameObject object: objects)
                    if(object.getModelReference().equals(objModel)) return;
                int trashIdx = models.indexOf(objModel);
                models.remove(trashIdx); //delete mesh
                for(GameObject object : objects)
                    if(object.meshSceneIndex > trashIdx) object.meshSceneIndex--;
            }
    }

    private void drawBackground()
    {
        Matrix.translateM(Renderer.worldMatrix, 0, cam.position.x, cam.position.y, cam.position.z); //Place BG at cameraPos
        GLES30.glUniformMatrix4fv(Shader.GL_modelMatrixLocations[0], 1, false, floatArrayToBuffer(Renderer.worldMatrix, true)); //update worldMtx
        if(BGModelIndex != null) getModel(BGModelIndex).drawModel(); //draw BG
        Matrix.setIdentityM(Renderer.worldMatrix, 0); //reset worldMtx (start of frame-build)
        GLES30.glUniformMatrix4fv(Shader.GL_modelMatrixLocations[0], 1, false, floatArrayToBuffer(Renderer.worldMatrix, true));
    }

    private void addPendingObjects()
    {
        if(pendingObjects.size() >0) //Add all pending objects
        {
            for(GameObject object : pendingObjects)
                objects.addLast(object);
            pendingObjects = new ArrayList<GameObject>();
        }
    }

    public void addObject(GameObject newObject)
    {
        pendingObjects.add(newObject);
    }
}
