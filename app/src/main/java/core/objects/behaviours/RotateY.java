package core.objects.behaviours;

import org.joml.Vector3f;

import core.Renderer;
import core.Scene;
import core.collision.Collision;
import core.objects.BehavObject;
import core.objects.PlayerObject;
import core.objects.behaviours.behavFunc.DirectionNormalToObject;
import core.objects.behaviours.behavFunc.RotateAwayFromObject;

public class RotateY
{
    public static void main(BehavObject object, Scene currentScene)
    {
        Vector3f newPos = new Vector3f(object.position);
        PlayerObject player = currentScene.getPlayer();
        Vector3f dirToPlayer = DirectionNormalToObject.main(object, player);
        dirToPlayer.mul(-0.02f);
        newPos.sub(dirToPlayer); //move away from player on x/z
        newPos.sub(0, 0.02f, 0);
        Collision.collisionCheck(currentScene, object, newPos);
        //RotateAwayFromObject.main(object, player);
        object.rotation.y += 4.0f* Renderer.frameTimeRatio;
    }
}