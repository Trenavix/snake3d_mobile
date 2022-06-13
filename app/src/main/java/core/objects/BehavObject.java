package core.objects;

import static functions.Buffers.floatArrayToBuffer;

import android.opengl.GLES30;
import android.opengl.Matrix;

import org.joml.Vector3f;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import core.Scene;
import graphics.Mesh;
import graphics.Utilities;

public class BehavObject extends GameObject
{
    Class behavClass;
    public BehavObject(Mesh mesh, Vector3f position, Vector3f rotation, float scale, float radius, String behaviour)
            throws ClassNotFoundException
    {
        super(mesh, position, rotation, scale, radius);
        try {this.behavClass = Class.forName("core.objects.behaviours."+behaviour);}
        catch(ClassNotFoundException e) {e.printStackTrace();}
    }

    public void placeObjectInWorld(float[] worldMatrix, int GL_worldMatrixLocation)

    {
        Matrix.setIdentityM(worldMatrix, 0);
        Vector3f pos = position;
        Vector3f rot = rotation;
        float objectScale = scale;
        Matrix.translateM(worldMatrix, 0, pos.x, pos.y, pos.z);
        Matrix.scaleM(worldMatrix, 0, objectScale, objectScale, objectScale);
        Utilities.rotateMatrix3Axes(worldMatrix, rot);
        GLES30.glUniformMatrix4fv(GL_worldMatrixLocation, 1, false, floatArrayToBuffer(worldMatrix, true));
    }
    public void runBehaviour(Scene scene) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Method behaviour = behavClass.getMethod("main", BehavObject.class, Scene.class);
        behaviour.invoke(null, this, scene);
    }
}
