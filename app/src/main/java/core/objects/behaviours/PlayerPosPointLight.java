package core.objects.behaviours;

import org.joml.Vector3f;

import core.Scene;
import core.objects.BehavObject;
import core.objects.LightObject;
import core.objects.Status;
import graphics.Shader;

public class PlayerPosPointLight
{
    public static void main(BehavObject object, Scene currentScene) throws ClassNotFoundException
    {
        Vector3f playerPos = currentScene.getPlayer().position;
        if(object.status == Status.STARTUP)
        {
            LightObject light = new LightObject(playerPos, new Vector3f(), 0.7f, 0.5f, 0.5f, 0, 0);
            light.setAmbientColor(new Vector3f(0.10f, 0.15f, 0.45f));
            light.setDiffuseColor(new Vector3f(0.2f, 0.4f, 1.0f));
            object.getSubObjects().add(light);
            Shader.currentLightCount++; //IMPORTANT: On light initiation you need to add, the count methods won't detect
        }
        object.getSubObjects().getFirst().position = playerPos;
    }
}
