package core.objects;

import android.opengl.GLES30;
import android.opengl.Matrix;

import core.Renderer;
import core.Scene;
import graphics.Mesh;
import graphics.Shader;
import graphics.Utilities;

import org.joml.Vector3f;
import org.joml.Vector4f;

import static functions.Buffers.floatArrayToBuffer;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;

public class GameObject extends Entity
{
    Vector4f angularVector = new Vector4f(0,0,0,0);
    private Mesh mesh;
    public Vector3f rotation;
    public float scale;
    private float radius; //interaction radius i.e. core.collision
    LinkedList<GameObject> subObjects;
    public int meshSceneIndex;
    public Status status;

    public GameObject(Mesh mesh, Vector3f position, Vector3f rotation, float scale, float radius, int sceneIndex)
    {
        super(position);
        this.mesh = mesh;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.radius = radius;
        this.subObjects = new LinkedList<GameObject>();
        this.meshSceneIndex = sceneIndex;
        this.status = Status.LIVE;
    }
    private void setMeshIndex(Scene scene, int idx) { this.meshSceneIndex = idx; }
    public void placeObjectInWorld()
    {
        float objectScale = scale;
        if(this.mesh.billboard)
        {
            GLES30.glUniform3f(Shader.GL_billboardPosUniLocation, this.position.x, this.position.y, this.position.z);
        }
        Matrix.translateM(Renderer.worldMatrix, 0, position.x, position.y, position.z);
        Matrix.scaleM(Renderer.worldMatrix, 0, objectScale, objectScale, objectScale);
        Utilities.rotateMatrix3Axes(Renderer.worldMatrix, this.rotation);
        GLES30.glUniformMatrix4fv(
                Shader.GL_worldMatrixLocation,
                1,
                false,
                floatArrayToBuffer(Renderer.worldMatrix, true)
        );
    }

    public void drawObject()
    {
        float[] storeMatrix = Arrays.copyOf(Renderer.worldMatrix, Renderer.worldMatrix.length); //store copy
        drawObjectMesh();
        for(GameObject subObject : getSubObjects()) //draw all subObjects
            subObject.drawObject();
        Renderer.worldMatrix = storeMatrix; //restore
    }

    public void drawObjectMesh()
    {
        placeObjectInWorld();
        //if(mesh.billboard) mesh.rotation = this.rotation;
        mesh.drawMesh();
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
    public Mesh getMeshReference() {return mesh; }
    public void delete(){ this.status = Status.DEAD; }
    public void setNewPosition(Vector3f newPos) {position = newPos;}
    public void setMeshFromScene(int index, Scene scene)
    {
        this.mesh = scene.getMesh(index);
        this.meshSceneIndex = index;
    }
}
