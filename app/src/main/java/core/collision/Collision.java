package core.collision;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.joml.Vector3f;

import java.util.ArrayList;

public class Collision
{
    public static boolean spheresCollide(Vector3f pt1, Vector3f pt2, float radius1, float radius2)
    {
        Vector3f vecDif = new Vector3f(pt1);
        vecDif.sub(pt2);
        float dif = vecDif.length();
        return (dif <= (radius1 + radius2));
    }

    static float SignedVolume(Vector3f a, Vector3f b, Vector3f c, Vector3f d)
    {
        Vector3f B = new Vector3f(b);
        B.sub(a);
        Vector3f C = new Vector3f(c);
        C.sub(a);
        B.cross(C);
        Vector3f D = new Vector3f(d);
        D.sub(a);
        float dot = D.dot(B);
        return (1.0f / 6.0f) * dot;
    }

    static boolean SameSign(float a, float b)
    {
        return a*b >= 0.0f;
    }

    public static Vector3f crossedPlane(Vector3f point1, Vector3f point2, Vector3f triVertices[])
    {
        Vector3f pt1 = new Vector3f(point1);
        Vector3f pt2 = new Vector3f(point2); //Make copies
        float vol1 = SignedVolume(pt1, triVertices[0], triVertices[1], triVertices[2]);
        float vol2 = SignedVolume(pt2, triVertices[0], triVertices[1], triVertices[2]);
        if (SameSign(vol1, vol2)) return null; //Stop operation if first requirement is false
        vol1 = SignedVolume(pt1, pt2, triVertices[0], triVertices[1]);
        vol2 = SignedVolume(pt1, pt2, triVertices[1], triVertices[2]);
        if (!SameSign(vol1, vol2)) return null; //Stop operation if second requirement is false
        vol1 = SignedVolume(pt1, pt2, triVertices[2], triVertices[0]);

        if (SameSign(vol1, vol2)) //Third requirement is the deal breaker
        {
            Vector3f triVert1 = new Vector3f(triVertices[1]);
            Vector3f triVert2 = new Vector3f(triVertices[2]);
            Vector3f N = triVert1.sub(triVertices[0]).cross(triVert2.sub(triVertices[0])); //lol java, good luck reading this
            float t = -1.0f*N.dot(pt1.sub(triVertices[0])) / N.dot(pt2.sub(point1)); //point1 very important, NOT pt1. JOML...
            Vector3f dif = new Vector3f(point2);
            dif.sub(point1);
            dif.mul(t);
            dif.add(point1);
            return dif;
        }
        return null;
    }

    public static Vector3f ClosestSpherePoint(Vector3f spherePos, float sphereRad, Vector3f point)
    {
        // First, get a vector from the sphere to the point
        Vector3f sphereToPoint = new Vector3f(point).sub(spherePos);
        // Normalize that vector
        sphereToPoint.normalize();
        // Adjust it's length to point to edge of sphere
        sphereToPoint.mul(sphereRad);
        // Translate into world space
        //Vector3f worldPoint = new Vector3f(spherePos).add(sphereToPoint);
        // Return new point
        return sphereToPoint;
    }

    public static Float sphereToTri(CollisionTriangle tri, Vector3f point, float radius)
    {
        Vector3f vertices[] = tri.getVerticesAsVectors();
        Vector3f distance = new Vector3f(point).sub(tri.getSurfaceNormal()); //distance between pt and tri
        float distanceToSurface = tri.getSurfaceNormal().dot(distance);
        //distanceToSurface = distanceToSurface / tri.getSurfaceNormal().dot(tri.getSurfaceNormal());
        boolean touching = (distanceToSurface < radius) && (distanceToSurface > 0.f);
        if(touching) return distanceToSurface;
        else return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Vector3f testTriangles(ArrayList<CollisionTriangle> triangles, Vector3f posCopy, Vector3f trajectory, float radius)
    {
        float biggerRadius = radius*1.05f; //inflate radius slightly to pass tests
        Vector3f newTrajectory = new Vector3f(trajectory);
        for(CollisionTriangle triangle : triangles)
        {
            //here we go..
            Vector3f surfNorm = triangle.getSurfaceNormal();
            Vector3f[] vertices = triangle.getVerticesAsVectors();
            Vector3f triPt = triangle.closestPoint(newTrajectory);
            Vector3f movement = new Vector3f(newTrajectory).sub(posCopy);
            float dotProduct = movement.dot(surfNorm);
            if(dotProduct >= 0) continue; //If intercept is coming from behind the triangle, ignore
            if(triPt != null)
            {
                Vector3f playerToTriDistance = new Vector3f(triPt).sub(newTrajectory);
                float distance = playerToTriDistance.length();
                if(distance < radius)
                {
                    newTrajectory = new Vector3f(triPt);
                    newTrajectory.add(new Vector3f(surfNorm).mul(biggerRadius));
                    return newTrajectory; //Skip the intercept check if this check passed
                }
            }
            Vector3f intercept = Collision.crossedPlane(posCopy, newTrajectory, vertices);
            if(intercept != null)
            {
                newTrajectory = new Vector3f(intercept);
                newTrajectory.add(new Vector3f(surfNorm).mul(biggerRadius));
                return newTrajectory; //Skip the intercept check if this check passed
            }
        }
        return null;
    }

}
