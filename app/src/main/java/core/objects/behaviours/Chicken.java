package core.objects.behaviours;

import org.joml.Vector3f;

import core.Scene;
import core.collision.Collision;
import core.objects.BehavObject;
import core.objects.PlayerObject;
import functions.OtherConstants;

public class Chicken
{
    public static void main(BehavObject object, Scene currentScene)
    {
        Vector3f newPos = new Vector3f(object.position);
        PlayerObject player = currentScene.getPlayer();
        Vector3f dirToPlayer = new Vector3f(player.position).sub(newPos);
        dirToPlayer.normalize();
        dirToPlayer.mul(0.02f);
        newPos.sub(dirToPlayer); //move away from player on x/z
        newPos.sub(0,0.02f, 0);
        Collision.collisionCheck(currentScene, object, newPos);
        object.rotation.y = (float)Math.atan2(-dirToPlayer.x, -dirToPlayer.z)* OtherConstants.RAD2DEG;
    }
}
