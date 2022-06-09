package core.objects;

import static functions.Buffers.floatArrayToBuffer;

import android.opengl.GLES30;
import android.opengl.Matrix;

import org.joml.Vector3f;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import graphics.Mesh;
import graphics.Utilities;

public class BehavObject extends GameObject
{
    Class behavClass;
    public BehavObject(int modelIdx, Vector3f position, Vector3f rotation, float scale, float radius, String behaviour)
            throws ClassNotFoundException
    {
        super(modelIdx, position, rotation, scale, radius);
        try {this.behavClass = Class.forName("core.objects.behaviours."+behaviour);}
        catch(ClassNotFoundException e) {e.printStackTrace();}
    }

    public void placeObjectInWorld(float[] worldMatrix, int GL_worldMatrixLocation, boolean use3AxisRotation)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Method behaviour = behavClass.getMethod("main", BehavObject.class);
        behaviour.invoke(null, this);
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
}
