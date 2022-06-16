package core.objects.behaviours.behavFunc;
import org.joml.Vector3f;

import core.objects.GameObject;

//This class combines several main functions into a single class for all objects to easily access

public class ObjectFunctions
{
    static public void RotateX(GameObject object, float angle)
        { Rotations.RotateX(object, angle); }
    static public void RotateY(GameObject object, float angle)
        { Rotations.RotateY(object, angle); }
    static public void RotateZ(GameObject object, float angle)
        { Rotations.RotateZ(object, angle); }
    static public void RotateXYZ(GameObject object, Vector3f angles)
        { Rotations.RotateXYZ(object, angles); }
    static public void RotateYTowardObject(GameObject object, GameObject towardObject)
        { Rotations.RotateYTowardObject(object, towardObject); }
    public static void RotateYAwayFromObject(GameObject object, GameObject towardObject)
        { Rotations.RotateYAwayFromObject(object, towardObject);}
    public static Vector3f DirectionNormalToObject(GameObject first, GameObject second)
    { return Directions.DirectionNormalToObject(first, second); }
}
