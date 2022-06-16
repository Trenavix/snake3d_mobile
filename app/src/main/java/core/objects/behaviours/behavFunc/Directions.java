package core.objects.behaviours.behavFunc;

import org.joml.Vector3f;

import core.objects.GameObject;

public class Directions
{
    public static Vector3f DirectionNormalToObject(GameObject first, GameObject second)
    {
        return new Vector3f(first.position).sub(second.position).normalize();
    }
}
