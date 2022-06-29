package core.objects.behaviours;

import static functions.Buffers.floatArrayToBuffer;

import android.opengl.GLES30;
import android.opengl.Matrix;

import org.joml.Vector3f;

import java.util.Arrays;

import core.Scene;
import core.objects.BehavObject;
import core.objects.LightObject;
import core.objects.Status;
import graphics.BoneNode;
import graphics.Mesh;
import graphics.Shader;

public class AnimatedModel
{
    static float[][] originalBones;
    public static void main(BehavObject object, Scene currentScene) throws ClassNotFoundException
    {
        Mesh mesh = object.getModelReference().meshes.get(0);
        BoneNode root = mesh.bonesListed[0];
        if(object.status == Status.STARTUP)
        {
            originalBones = new float[mesh.bonesListed.length][];
            for(int i = 0; i<mesh.bonesListed.length; i++)
                originalBones[i] = Arrays.copyOf(mesh.bonesListed[i].transformMtx, 16);
        }
        for(int i =0; i< originalBones.length; i++) //reset original bones
            mesh.bonesListed[i].transformMtx = Arrays.copyOf(originalBones[i], 16);
        //First bone
        Matrix.rotateM(mesh.bonesListed[1].transformMtx, 0, 90.0f, -1.0f, 0.0f, 0.0f);
        Matrix.rotateM(mesh.bonesListed[1].transformMtx, 0, 180.0f, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(mesh.bonesListed[1].transformMtx, 0, 0, 3.0f, 0);
        Matrix.rotateM(mesh.bonesListed[3].transformMtx, 0, 90.0f, -0f, 0.0f, -1.0f);
        Matrix.translateM(mesh.bonesListed[3].transformMtx, 0, 0.8f, -0.8f, 0);
        Matrix.rotateM(mesh.bonesListed[11].transformMtx, 0, 17.0f+10.0f*(float)Math.sin(Shader.time/50.0f), 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mesh.bonesListed[12].transformMtx, 0, 30.0f*(float)Math.sin(Shader.time/50.0f), 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mesh.bonesListed[13].transformMtx, 0, 30.0f*(float)Math.sin(Shader.time/50.0f), 0.0f, 1.0f, 0.0f);
        root.stackTransformMatrices();
    }


}
