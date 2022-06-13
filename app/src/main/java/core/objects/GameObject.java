package core.objects;

import android.opengl.GLES30;
import android.opengl.Matrix;

import graphics.Mesh;
import graphics.Utilities;

import org.joml.Vector3f;
import org.joml.Vector4f;

import static functions.Buffers.floatArrayToBuffer;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

public class GameObject extends Entity
{
    Vector4f angularVector = new Vector4f(0,0,0,0);
    private Mesh mesh;
    public Vector3f rotation;
    public float scale;
    private float radius; //interaction radius i.e. core.collision
    LinkedList<GameObject> subObjects;
    boolean deleteFromScene = false;

    public GameObject(Mesh mesh, Vector3f position, Vector3f rotation, float scale, float radius)
    {
        super(position);
        this.mesh = mesh;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.radius = radius;
        this.subObjects = new LinkedList<GameObject>();
    }
    public void placeObjectInWorld(float[] worldMatrix, int GL_worldMatrixLocation) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Matrix.setIdentityM(worldMatrix, 0);
        float objectScale = scale;
        Matrix.translateM(worldMatrix, 0, position.x, position.y, position.z);
        Matrix.scaleM(worldMatrix, 0, objectScale, objectScale, objectScale);
        Utilities.rotateMatrix3Axes(worldMatrix, rotation);
        GLES30.glUniformMatrix4fv(GL_worldMatrixLocation, 1, false, floatArrayToBuffer(worldMatrix, true));
    }

    public void drawObject(float[] worldMatrix, int GL_worldMatrixLocation) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        placeObjectInWorld(worldMatrix, GL_worldMatrixLocation);
        mesh.drawMesh(worldMatrix, GL_worldMatrixLocation);
        for(GameObject subObject : getSubObjects())
            subObject.drawObject(worldMatrix, GL_worldMatrixLocation);
    }
    public float getInteractionRadius()
    {
        return radius*scale;
    }
    public void setAngularVector(Vector4f newVector) {angularVector = newVector; }
    public LinkedList<GameObject> getSubObjects() {return subObjects; }
    public Mesh getMeshReference() {return mesh; }
    public void delete(){ this.deleteFromScene = true; }
    public boolean isReadyForDeletion() { return deleteFromScene; }
    public void setNewPosition(Vector3f newPos) {position = newPos;}
}
