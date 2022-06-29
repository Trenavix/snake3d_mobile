package core.objects.behaviours;

import org.joml.Vector3f;

import core.Scene;
import core.collision.Collision;
import core.objects.BehavObject;
import core.objects.GameObject;
import core.objects.PlayerObject;
import core.objects.behaviours.behavFunc.ObjectFunctions;

public class Collectible
{
    public static void main(BehavObject object, Scene currentScene) throws ClassNotFoundException
    {
        ObjectFunctions.RotateY(object, 4.0f);
        Vector3f newPos = new Vector3f(object.position);
        PlayerObject player = currentScene.getPlayer();
        Vector3f movement = ObjectFunctions.DirectionNormalToObject(object, player);
        movement.mul(-0.02f); //away from player slightly
        newPos.sub(movement);
        newPos.sub(0, 0.02f, 0); //fall slightly
        GameObject hitObject = Collision.collisionCheck(currentScene, object, newPos);
        if(hitObject != null)
        {
            if(hitObject.getClass().equals(PlayerObject.class)) //If player is hit
            {
                player.setPathSize(player.getPathSize()+1);
                object.swapObject(
                        new BehavObject(
                                currentScene.getModel(4), //TODO: Dedicated scene mesh indices (4 is sparkle)
                                new Vector3f(object.position),
                                new Vector3f(),
                                0.4f,
                                0.0f,4,
                                "Sparkles"),
                        currentScene);
            }
        }
    }
}
