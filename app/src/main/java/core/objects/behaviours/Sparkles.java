package core.objects.behaviours;

import static functions.Buffers.floatArrayToBuffer;

import android.opengl.GLES30;
import android.opengl.Matrix;

import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.InvocationTargetException;

import core.Renderer;
import core.Scene;
import core.collision.Collision;
import core.objects.BehavObject;
import core.objects.GameObject;
import core.objects.PlayerObject;
import core.objects.behaviours.behavFunc.DirectionNormalToObject;
import functions.OtherConstants;
import graphics.Material;
import graphics.Mesh;
import graphics.Shader;
import graphics.Utilities;

public class Sparkles
{
    public static void main(BehavObject object, Scene currentScene) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException
    {
        Mesh mesh = object.getMeshReference();
        mesh.billboard = true;
        mesh.setScale(new Vector3f(object.scale));

        mesh.rotation.x = -(float)Shader.time/25.f;
        //object.rotation.y += (float)Shader.time/25.f;
        //Material material = object.getMeshReference().getMaterials()[0];
        if(object.startUpState)
        {
            currentScene.addMesh(object.getMeshReference().cloneMesh());
            int meshCount = currentScene.meshCount();
            object.setMeshFromScene(currentScene.meshCount()-1, currentScene);
            mesh.depthBufferWritingEnabled = false;
            mesh.setEnvColor(new Vector4f(1.0f,1.0f,1.0f, 1.0f));
            object.getSubObjects().add(new GameObject(object.getMeshReference(), new Vector3f(), new Vector3f(), 1.0f, object.getInteractionRadius(), meshCount));
            object.getSubObjects().getLast().position.z = 0.1f;
            object.getSubObjects().add(new GameObject(object.getMeshReference(), new Vector3f(), new Vector3f(), 1.0f, object.getInteractionRadius(), meshCount));
            object.getSubObjects().getLast().position.z = -0.1f;
            object.getSubObjects().add(new GameObject(object.getMeshReference(), new Vector3f(), new Vector3f(), 1.0f, object.getInteractionRadius(), meshCount));
            object.getSubObjects().getLast().position.x = 0.1f;
            object.getSubObjects().add(new GameObject(object.getMeshReference(), new Vector3f(), new Vector3f(), 1.0f, object.getInteractionRadius(), meshCount));
            object.getSubObjects().getLast().position.x = -0.1f;
            object.getSubObjects().add(new GameObject(object.getMeshReference(), new Vector3f(), new Vector3f(), 1.0f, object.getInteractionRadius(), meshCount));
            object.getSubObjects().getLast().position.y = 0.1f;
            return;
        }
        mesh.envColor.sub(new Vector4f(0.f,0.f,0.f,0.01f*(float)Renderer.frameTimeRatio));
        for(GameObject subObject: object.getSubObjects())
        {
            Vector3f dirNorm = new Vector3f(subObject.position).normalize();
            subObject.position.add(dirNorm.mul(0.05f*(float)Renderer.frameTimeRatio));
        }
        if(mesh.envColor.w < 0.0001)
        {
            object.delete();
        }

    }
}
