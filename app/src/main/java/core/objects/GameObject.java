package core.objects;

import android.opengl.GLES30;
import android.opengl.Matrix;

import graphics.Utilities;

import org.joml.Vector3f;
import org.joml.Vector4f;

import static functions.Buffers.floatArrayToBuffer;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

public class GameObject extends Entity
{
    Vector4f angularVector = new Vector4f(0,0,0,0);
    private int modelIdx;
    public Vector3f rotation;
    public float scale;
    private float radius; //interaction radius i.e. core.collision
    LinkedList<GameObject> subObjects;

    public GameObject(int modelIdx, Vector3f position, Vector3f rotation, float scale, float radius)
    {
        super(position);
        this.modelIdx = modelIdx;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.radius = radius;
        this.subObjects = new LinkedList<GameObject>();
    }
    public void placeObjectInWorld(float[] worldMatrix, int GL_worldMatrixLocation, boolean use3AxisRotation) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Matrix.setIdentityM(worldMatrix, 0);
        Vector3f pos = position;
        Vector3f rot = rotation;
        float objectScale = scale;
        Matrix.translateM(worldMatrix, 0, pos.x, pos.y, pos.z);
        Matrix.scaleM(worldMatrix, 0, objectScale, objectScale, objectScale);
        if (use3AxisRotation) Utilities.rotateMatrix3Axes(worldMatrix, rot);
        else Matrix.rotateM(worldMatrix, 0, angularVector.w, angularVector.x, angularVector.y, angularVector.z);
        GLES30.glUniformMatrix4fv(GL_worldMatrixLocation, 1, false, floatArrayToBuffer(worldMatrix, true));
    }
    public int getModelIdx()
    {
        return modelIdx;
    }
    public float getInteractionRadius()
    {
        return radius;
    }
    public void setAngularVector(Vector4f newVector) {angularVector = newVector; }
    public LinkedList<GameObject> getSubObjects() {return subObjects; }
}
