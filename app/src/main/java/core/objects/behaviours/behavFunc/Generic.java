package core.objects.behaviours.behavFunc;

import org.joml.Vector3f;

import core.Renderer;
import core.Scene;
import core.collision.Collision;
import core.objects.GameObject;
import functions.OtherConstants;

public class Generic
{
    public static final float terminalVelocity = -0.2f;
    public static final float gravityFactor = -0.01f;

    public static void gravitateObject(Scene scene, GameObject object)
    {
        if(Collision.onSurface(scene, object) && object.velocity.y <= 0)
            { object.velocity.y = 0; return; }
        float verticalVelocity = object.velocity.y + gravityFactor;
        //Take whichever value is greater (less negative):
        object.velocity.y = Math.max(verticalVelocity, terminalVelocity);
    }
}
