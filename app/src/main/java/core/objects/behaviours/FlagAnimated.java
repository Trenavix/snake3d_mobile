package core.objects.behaviours;

import org.joml.Vector3f;

import core.Scene;
import core.objects.BehavObject;
import core.objects.Status;
import graphics.Mesh;
import graphics.Model;
import graphics.Shader;
import graphics.generators.MeshGenerators;

public class FlagAnimated
{
    public static void main(BehavObject object, Scene currentScene) throws ClassNotFoundException
    {
        if(object.status == Status.STARTUP)
        {
            Model flag = MeshGenerators.generateFlagMesh(0xFFFFFFFF);
            flag.loadTexturesIntoGL();
            flag.getMaterials()[0].cullBackFace = false;
            currentScene.addModel(flag);
            object.setMeshFromScene(currentScene.meshCount()-1, currentScene);
        }
        Vector3f translation = new Vector3f(0.0f, 0.0f, 3.0f*(float)Math.sin(Shader.time/10.f));
        object.getModelReference().meshes.get(0).setTranslation(translation);
    }
}
