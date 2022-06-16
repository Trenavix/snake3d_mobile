package core.objects.behaviours.behavFunc;

import org.joml.Vector3f;

import core.Renderer;
import core.objects.GameObject;
import functions.OtherConstants;

public class Rotations
{
    public static void RotateX(GameObject object, float rotation)
    {
        object.rotation.x += rotation* Renderer.frameTimeRatio;
    }

    public static void RotateY(GameObject object, float rotation)
    {
        object.rotation.y += rotation* Renderer.frameTimeRatio;
    }
    public static void RotateZ(GameObject object, float rotation)
    {
        object.rotation.z += rotation* Renderer.frameTimeRatio;
    }
    public static void RotateXYZ(GameObject object, Vector3f rotation)
    {
        object.rotation.add(rotation.mul((float)Renderer.frameTimeRatio));
    }
    public static void RotateYTowardObject(GameObject object, GameObject towardObject)
    {
        Vector3f dirToObject = new Vector3f(towardObject.position).sub(object.position);
        object.rotation.y = (float)Math.atan2(dirToObject.x, dirToObject.z)* OtherConstants.RAD2DEG;
    }
    public static void RotateYAwayFromObject(GameObject object, GameObject towardObject)
    {
        Vector3f dirToObject = new Vector3f(towardObject.position).sub(object.position);
        object.rotation.y = (float)Math.atan2(-dirToObject.x, -dirToObject.z)* OtherConstants.RAD2DEG;
    }
}