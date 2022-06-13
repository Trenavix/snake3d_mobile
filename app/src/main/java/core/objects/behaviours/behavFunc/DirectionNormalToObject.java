package core.objects.behaviours.behavFunc;

import org.joml.Vector3f;

import core.objects.GameObject;

public class DirectionNormalToObject
{
    public static Vector3f main(GameObject first, GameObject second)
    {
        return new Vector3f(first.position).sub(second.position).normalize();
    }
}
