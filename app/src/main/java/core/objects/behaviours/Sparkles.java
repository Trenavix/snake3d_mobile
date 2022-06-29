package core.objects.behaviours;

import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.InvocationTargetException;

import core.Renderer;
import core.Scene;
import core.objects.BehavObject;
import core.objects.GameObject;
import core.objects.LightObject;
import core.objects.Status;
import graphics.Mesh;
import graphics.Shader;

public class Sparkles
{
    public static void main(BehavObject object, Scene currentScene) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException
    {
        Mesh mesh = object.getModelReference().meshes.get(0);
        mesh.billboard = true;
        mesh.setScale(new Vector3f(object.scale));
        mesh.rotation.x = -(float)Shader.time/12.f;
        if(object.status == Status.STARTUP)
        {
            currentScene.addModel(object.getModelReference().cloneModel());
            int meshCount = currentScene.meshCount();
            object.setMeshFromScene(currentScene.meshCount()-1, currentScene);
            //mesh.depthBufferWritingEnabled = false;
            mesh.drawOnTopOfAllGeometry = true;
            mesh.setEnvColor(new Vector4f(1.0f,1.0f,1.0f, 1.0f));
            object.getSubObjects().add(new LightObject(object.position, new Vector3f(), 0.5f, 0.1f, 0.5f, 0, 0));
            LightObject light = (LightObject)object.getSubObjects().getFirst();
            //light.setAmbientColor(new Vector3f(-0.2f, -1.2f, -1.9f));
            //light.setDiffuseColor(new Vector3f(0.2f, -0.6f, -1.25f));
            object.getSubObjects().add(new GameObject(object.getModelReference(), new Vector3f(), null, 1.0f, object.getInteractionRadius(), meshCount));
            object.getSubObjects().getLast().position.z = 0.1f;
            object.getSubObjects().add(new GameObject(object.getModelReference(), new Vector3f(), null, 1.0f, object.getInteractionRadius(), meshCount));
            object.getSubObjects().getLast().position.z = -0.1f;
            object.getSubObjects().add(new GameObject(object.getModelReference(), new Vector3f(), null, 1.0f, object.getInteractionRadius(), meshCount));
            object.getSubObjects().getLast().position.x = 0.1f;
            object.getSubObjects().add(new GameObject(object.getModelReference(), new Vector3f(), null, 1.0f, object.getInteractionRadius(), meshCount));
            object.getSubObjects().getLast().position.x = -0.1f;
            object.getSubObjects().add(new GameObject(object.getModelReference(), new Vector3f(), null, 1.0f, object.getInteractionRadius(), meshCount));
            object.getSubObjects().getLast().position.y = 0.1f;
            return;
        }
        mesh.envColor.sub(new Vector4f(0.f,0.f,0.f,0.01f*(float)Renderer.frameTimeRatio));
        for(GameObject subObject: object.getSubObjects())
        {
            if(subObject.getClass().equals(LightObject.class))
            {
                LightObject light = (LightObject)subObject;
                light.exponent+=0.07f* Renderer.frameTimeRatio;
                light.linear +=0.07f* Renderer.frameTimeRatio;
                light.constant+=0.07f*Renderer.frameTimeRatio;
                light.status = Status.LIVE;
                if(light.exponent >= 10.0f) { light.delete();return;}
                continue; //skip the rest for the light object
            }
            Vector3f dirNorm = new Vector3f(subObject.position).normalize();
            subObject.position.add(dirNorm.mul(0.05f*(float)Renderer.frameTimeRatio));
        }
        if(mesh.envColor.w < 0.0001)
        {
            object.delete();
        }
    }
}
