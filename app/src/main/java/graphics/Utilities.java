package graphics;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.Matrix;

import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import functions.OtherConstants;

import static functions.OtherConstants.*;

public class Utilities
{
    public static void perspectiveFrustrum(float[] matrix, float fov, float aspect, float zNear, float zFar) {
        float fH = (float) (Math.tan(fov / 360.0 * Math.PI) * zNear);
        float fW = fH * aspect;
        Matrix.frustumM(matrix, 0, -fW, fW, -fH, fH, zNear, zFar);
    }

    public static void rotateMatrix3Axes(float[] worldMatrix, Vector3f rotations)
    {
        Matrix.rotateM(worldMatrix, 0, rotations.x, 1.f, 0.f, 0.f);
        Matrix.rotateM(worldMatrix, 0, rotations.y, 0.f, 1.f, 0.f);
        Matrix.rotateM(worldMatrix, 0, rotations.z, 0.f, 0.f, 1.f);
    }
    public static Vector3f orientationToDirectionVector(Vector2f rotations)
    {
        return new Vector3f(
                (float)(Math.cos(rotations.x) * Math.cos(rotations.y)),
                (float)Math.sin(rotations.y),
                (float)(Math.sin(rotations.x) * Math.cos(rotations.y))
        );
    }

    public static Vector4f vectorNormToAngularVector(Vector3f norm)
    {
        Vector3f crossVec = new Vector3f(0.f,1.f,0.f); //unitY
        crossVec.cross(norm);
        float theta = (float)Math.acos(norm.dot(0.f,1.f,0.f));
        return new Vector4f(crossVec, theta*RAD2DEG);
    }

    public static Vector3f angularVectorTo3AxisRotation(Vector4f angularvec)
    {
        float theta = angularvec.w;
        return new Vector3f
                (
                        (angularvec.z * theta),
                        (angularvec.y * theta),
                        (angularvec.x * theta)
                );
    }

    public static Vector3f vectorNormTo3AxisRotation(Vector3f vecnorm)
    {
        Vector4f angularvec = vectorNormToAngularVector(vecnorm);
        return angularVectorTo3AxisRotation(angularvec);
    }
    public int[] twoDimensionArrayToSingle(int[][] array)
    {
        int buf = 0;
        for(int i=0; i<array.length; i++)
            buf +=  array[i].length;//get entire length
        int[] newArray = new int[buf];
        buf=0;
        for(int i=0; i<array.length; i++) //combine 2d array axes into one
        {
            for(int j=0; j < array[i].length;j++)
            {
                newArray[buf] = array[i][j];
                buf++;
            }
        }
        return newArray;
    }

    public static Vector3f vectorAverage(ArrayList<Vector3f> vectors)
    {
        Vector3f combo = new Vector3f(vectors.get(0));
        for(int i=1; i< vectors.size(); i++)
            combo.add(vectors.get(i)); //add them all together
        combo.div(vectors.size()); //average them
        return combo;
    }
}
