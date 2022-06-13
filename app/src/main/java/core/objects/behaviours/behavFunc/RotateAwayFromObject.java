package core.objects.behaviours.behavFunc;

import org.joml.Vector3f;

import core.objects.GameObject;
import functions.OtherConstants;

public class RotateAwayFromObject
{
    public static void main(GameObject firstObject, GameObject secondObject)
    {
        Vector3f dirToObject = new Vector3f(secondObject.position).sub(firstObject.position);
        firstObject.rotation.y = (float)Math.atan2(-dirToObject.x, -dirToObject.z)* OtherConstants.RAD2DEG;
    }
}
