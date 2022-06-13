package core;

import android.opengl.GLES30;
import android.opengl.Matrix;
import android.os.Build;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import core.objects.BehavObject;
import core.objects.GameObject;
import core.objects.PlayerObject;
import functions.DoubleObject;
import functions.conversions.Mesh32ToMesh;
import functions.conversions.OBJ2Mesh;
import graphics.Material;
import graphics.Mesh;
import graphics.Shader;
import graphics.generators.MeshGenerators;

import org.joml.Vector3f;

import static functions.Buffers.floatArrayToBuffer;

import androidx.annotation.RequiresApi;

public class Scene
{
    private ArrayList<Mesh> meshes = new ArrayList<Mesh>();
    private DoubleObject masterVtxIdxBuffers;
    private Integer playerObjectIndex;
    private ArrayList<GameObject> objects = new ArrayList<GameObject>();
    private ArrayList<Integer> levelMeshIndices = new ArrayList<Integer>();
    private Integer backgroundMeshIndex;
    private final int alphaMeshIndex = 1;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Scene(String[] sceneScript) throws ClassNotFoundException
    {
        for(String string : sceneScript)
        {
            String[] line = string.trim().split("\\s+"); // split it up by spaces
            switch(line[0]) //First "word" in each line (command)
            {
                case "BG_model":
                    backgroundMeshIndex = Integer.parseInt(line[1]);
                    break;
                case "level_model":
                    int levelMeshIdx = Integer.parseInt(line[1]);
                    levelMeshIndices.add(levelMeshIdx);
                    break;
                case "model_obj": //'Load obj file as mesh'
                    String[] objFile = AssetLoader.readFileAsText(line[1]);
                    String[] mtlFile = AssetLoader.readFileAsText(line[2]);
                    int defaultCollisionType = Integer.parseInt(line[4]);
                    String[] materialNames = OBJ2Mesh.objGetTextureNames(objFile);
                    String[] textureFileNames = OBJ2Mesh.objGetTexturePaths(mtlFile, materialNames);
                    Material[] textures = new Material[textureFileNames.length];
                    for(int i =0; i<textures.length; i++)
                        textures[i] = AssetLoader.loadMaterialFromPath(line[3]+textureFileNames[i], materialNames[i], defaultCollisionType); //nonsolid type 0
                    float scale = 1.0f;
                    if(line.length > 6) scale = Float.parseFloat(line[6]); //optional scale in script
                    Mesh newMesh = OBJ2Mesh.convertOBJToMesh(objFile, textures, scale, Float.parseFloat(line[5]), new Vector3f(Shader.lightDirection).mul(-1.0f));
                    newMesh.loadTexturesIntoGL();
                    meshes.add(newMesh);
                    break;
                case "mesh32":
                    byte[] mesh32Data = AssetLoader.readFileAsByteArray(line[1]);
                    float meshScale = Float.parseFloat(line[2]);
                    Mesh mesh32 = Mesh32ToMesh.Mesh32ToMesh(mesh32Data, meshScale);
                    mesh32.loadTexturesIntoGL();
                    meshes.add(mesh32);
                    break;
                case "model_grid": //generate Grid mesh
                    float gridWidth = Float.parseFloat(line[1]);
                    float gridHeight = Float.parseFloat(line[2]);
                    short partitions_x = Short.parseShort(line[3]);
                    short partitions_y = Short.parseShort(line[4]);
                    float offset_y = Float.parseFloat(line[5]);
                    int color = (int)Long.parseLong(line[6], 16);
                    Mesh gridMesh = MeshGenerators.generateGridMesh(gridWidth, gridHeight, partitions_x, partitions_y, offset_y, color);
                    gridMesh.loadTexturesIntoGL();
                    meshes.add(gridMesh);
                    objects.add(new GameObject(meshes.get(meshes.size()-1), new Vector3f(), new Vector3f(), 1.0f, 1.0f));
                    break;
                case "behavObject":
                case "object":
                    int meshIdx = Integer.parseInt(line[1]);
                    Vector3f pos = new Vector3f(Float.parseFloat(line[2]), Float.parseFloat(line[3]), Float.parseFloat(line[4]));
                    Vector3f rot = new Vector3f(Float.parseFloat(line[5]), Float.parseFloat(line[6]), Float.parseFloat(line[7]));
                    float objectScale = Float.parseFloat(line[8]);
                    float objectRadius = Float.parseFloat(line[9]);
                    if(line.length > 10) objects.add(new BehavObject(meshes.get(meshIdx), pos, rot, objectScale, objectRadius, line[10]));
                    else objects.add(new GameObject(meshes.get(meshIdx), pos, rot, objectScale, objectRadius));
                    break;
                case "player":
                    int playerMeshIdx = Integer.parseInt(line[1]);
                    Vector3f playerPos = new Vector3f(Float.parseFloat(line[2]), Float.parseFloat(line[3]), Float.parseFloat(line[4]));
                    Vector3f playerRot = new Vector3f(Float.parseFloat(line[5]), Float.parseFloat(line[6]), Float.parseFloat(line[7]));
                    float playerScale = Float.parseFloat(line[8]);
                    objects.add(new PlayerObject(meshes.get(playerMeshIdx), playerPos, playerRot, playerScale, 0.07f, 0.3f));
                    playerObjectIndex = objects.size()-1; //last index
                    break;
                //TODO: Implement more commands in scene script language
            }
        }
        masterVtxIdxBuffers = functions.Buffers.combineAllMeshBuffers(meshes);
        functions.Buffers.swapVertexBuffers(
                (float[])masterVtxIdxBuffers.firstObject, (int[])masterVtxIdxBuffers.secondObject);
    }

    public void addMesh(Mesh newMesh) { meshes.add(newMesh); }
    public Mesh getMesh(int meshIndex) { return meshes.get(meshIndex); }
    public ArrayList<Mesh> getAllMeshes() { return meshes; }
    public PlayerObject getPlayer() {return (PlayerObject)objects.get(playerObjectIndex); }
    public ArrayList<Integer> getLevelMeshIndices() {return levelMeshIndices; }
    public ArrayList<GameObject> getObjects() {return objects; }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void drawScene(float[] worldMatrix, int GL_worldMatrixLocation, int GL_alphaTestUniLocation, Vector3f camPosition) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Matrix.setIdentityM(worldMatrix, 0);
        GLES30.glUniform1f(GL_alphaTestUniLocation, 0.0f); //alphaTest disable
        GLES30.glDepthMask(false); //BG Model //Skip depthbuffer writing for BG
        Matrix.translateM(worldMatrix, 0, camPosition.x, camPosition.y, camPosition.z); //Place BG at cameraPos
        GLES30.glUniformMatrix4fv(GL_worldMatrixLocation, 1, false, floatArrayToBuffer(worldMatrix, true)); //update worldMtx
        if(backgroundMeshIndex != null) getMesh(backgroundMeshIndex).drawMesh(worldMatrix, GL_worldMatrixLocation); //draw BG
        Matrix.setIdentityM(worldMatrix, 0); //reset worldMtx
        GLES30.glUniformMatrix4fv(GL_worldMatrixLocation, 1, false, floatArrayToBuffer(worldMatrix, true));
        GLES30.glDepthMask(true);
        for(int i=0; i<levelMeshIndices.size(); i++) //Draw level models
        {
            if(i == alphaMeshIndex) continue; //Alpha model
            getMesh(levelMeshIndices.get(i)).drawMesh(worldMatrix, GL_worldMatrixLocation);
        }
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glDepthRangef(0.f, 1.f);
        GLES30.glDepthMask(true);
        GLES30.glDepthFunc(GLES30.GL_LESS);
        objects.parallelStream().forEach((object) ->
        {
            if(object.getClass().equals(BehavObject.class))
            {
                try {((BehavObject)object).runBehaviour(this);}
                catch (NoSuchMethodException e) {e.printStackTrace();}
                catch (InvocationTargetException e) {e.printStackTrace();}
                catch (IllegalAccessException e) { e.printStackTrace(); }
            }
        });
        for(int i=0; i<objects.size(); i++)
        {
            //runBehav linearly in cancelled code below, or run in parallel above
            //if(objects.get(i).getClass().equals(BehavObject.class))
                //((BehavObject)objects.get(i)).runBehaviour(this);
            objects.get(i).drawObject(worldMatrix, GL_worldMatrixLocation);
        }

        Matrix.setIdentityM(worldMatrix, 0); //Reset worldMtx after last object!
        GLES30.glUniformMatrix4fv(GL_worldMatrixLocation, 1, false, floatArrayToBuffer(worldMatrix, true));
        GLES30.glUniform1f(GL_alphaTestUniLocation, 0.1f);
        if(levelMeshIndices.size() > 1)
        {
            GLES30.glDisable(GLES30.GL_CULL_FACE);
            getMesh(levelMeshIndices.get(alphaMeshIndex)).drawMesh(worldMatrix, GL_worldMatrixLocation);
            GLES30.glEnable(GLES30.GL_CULL_FACE);
        }
        for(int i=0; i<objects.size(); i++)
            if(objects.get(i).isReadyForDeletion())
                objects.remove(i);
    }
}
