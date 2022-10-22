package core.objects;

import android.opengl.GLES30;
import android.opengl.Matrix;

import core.Renderer;
import core.Scene;
import graphics.Mesh;
import graphics.Model;
import graphics.Shader;
import graphics.Utilities;

import org.joml.Vector3f;
import org.joml.Vector4f;

import static functions.Buffers.floatArrayToBuffer;

import java.util.Arrays;
import java.util.LinkedList;

public class GameObject extends Entity
{
    Vector4f angularVector = new Vector4f(0,0,0,0);
    private Model model;
    public Vector3f rotation;
    public Vector3f previousPosition;
    public Vector3f velocity = new Vector3f();
    public float scale;
    private float radius; //interaction radius i.e. core.collision
    LinkedList<GameObject> subObjects;
    public int meshSceneIndex;
    public Status status;

    public GameObject(Model model, Vector3f position, Vector3f rotation, float scale, float radius, int sceneIndex)
    {
        super(position);
        this.model = model;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.radius = radius;
        this.subObjects = new LinkedList<GameObject>();
        this.meshSceneIndex = sceneIndex;
        this.previousPosition = position;
        this.status = Status.LIVE;
    }
    private void setMeshIndex(Scene scene, int idx) { this.meshSceneIndex = idx; }
    public void placeObjectInWorld()
    {
        Matrix.translateM(Renderer.worldMatrix, 0, position.x, position.y, position.z);
        if(scale != 1.0f) Matrix.scaleM(Renderer.worldMatrix, 0, scale, scale, scale);
        if(rotation != null) Utilities.rotateMatrix3Axes(Renderer.worldMatrix, this.rotation);
        GLES30.glUniformMatrix4fv(
                Shader.GL_modelMatrixLocations[0],
                1,
                false,
                floatArrayToBuffer(Renderer.worldMatrix, true)
        );
    }

    public void drawObject()
    {
        float[] storeMatrix = Arrays.copyOf(Renderer.worldMatrix, Renderer.worldMatrix.length); //store copy
        drawObjectModel();
        for(GameObject subObject : getSubObjects()) //draw all subObjects
            subObject.drawObject();
        Renderer.worldMatrix = storeMatrix; //restore
    }

    public void drawObjectModel()
    {
        placeObjectInWorld();
        //if(mesh.billboard) mesh.rotation = this.rotation;
        model.drawModel();
    }

    public void swapObject(GameObject newObject, Scene scene)
    {
        scene.addObject(newObject);
        this.delete();
    }

    public float getInteractionRadius()
    {
        return radius*scale;
    }
    public void setAngularVector(Vector4f newVector) {angularVector = newVector; }
    public LinkedList<GameObject> getSubObjects() {return subObjects; }
    public Model getModelReference() {return model; }
    public void delete(){ this.status = Status.DEAD; }
    public void setNewPosition(Vector3f newPos)
    {
        previousPosition = new Vector3f(position);
        position = newPos;
    }
    public void setMeshFromScene(int index, Scene scene)
    {
        this.model = scene.getModel(index);
        this.meshSceneIndex = index;
    }
}
