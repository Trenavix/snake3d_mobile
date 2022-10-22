package core.collision;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.LinkedList;

import core.Scene;
import core.objects.GameObject;
import graphics.Mesh;
import graphics.Model;

public class Collision
{
    public static boolean spheresCollide(Vector3f pt1, Vector3f pt2, float radius1, float radius2)
    {
        Vector3f vecDif = new Vector3f(pt1);
        vecDif.sub(pt2);
        float dif = vecDif.length();
        return (dif <= (radius1 + radius2));
    }

    public static Vector3f rayIntersectsTriangle(Vector3f rayOrigin, Vector3f rayDestination, Vector3f[] vertices)
    {
        final float EPSILON = 0.0000001f;
        Vector3f rayVector = new Vector3f(rayDestination).sub(rayOrigin);
        Vector3f edge1 = new Vector3f(vertices[1]).sub(vertices[0]);
        Vector3f edge2 = new Vector3f(vertices[2]).sub(vertices[0]);
        Vector3f h = new Vector3f(rayVector).cross(edge2);
        float a, f, u, v;
        a = edge1.dot(h);
        if (a > -EPSILON && a < EPSILON)
            return null;    // This ray is parallel to this triangle.
        f = 1.0f / a;
        Vector3f s = new Vector3f(rayOrigin).sub(vertices[0]);
        u = f * (s.dot(h));
        if (u < 0.0 || u > 1.0)
            return null;
        Vector3f q = new Vector3f(s).cross(edge1);
        v = f * rayVector.dot(q);
        if (v < 0.0 || u + v > 1.0)
            return null;
        // At this stage we can compute t to find out where the intersection point is on the line.
        float t = f * edge2.dot(q);
        if (t < EPSILON)
            return null; // This means that there is a line intersection but not a ray intersection.
        Vector3f hitPoint = new Vector3f(rayOrigin).lerp(rayDestination, t);
        float distance = new Vector3f(hitPoint).sub(rayOrigin).length();
        if (distance > rayVector.length())
            return null;
        return hitPoint;
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

    public static boolean testSurface(ArrayList<CollisionTriangle> triangles, GameObject object, Vector3f trajectory, float radius)
    {
        Vector3f posCopy = object.position;
        float biggerRadius = radius*1.01f; //inflate radius slightly to pass tests
        Vector3f newTrajectory = new Vector3f(trajectory);
        for(CollisionTriangle triangle : triangles)
        {
            //here we go..
            Vector3f movement = new Vector3f(newTrajectory).sub(posCopy);
            float movementLength = movement.length();
            if(triangle.getCentroidPos().sub(posCopy).length() > movementLength+biggerRadius+triangle.getMaxRadius()) continue;
            Vector3f surfNorm = triangle.getSurfaceNormal();
            if(surfNorm.y < surfNorm.x || surfNorm.y < surfNorm.z) continue; //If very vertical, not ground
            Vector3f[] vertices = triangle.getVertices();
            Vector3f triPt = triangle.closestPoint(newTrajectory);
            float dotProduct = movement.dot(surfNorm);
            if(dotProduct >= 0) continue; //If intercept is coming from behind the triangle, ignore
            if(triPt != null)
            {
                Vector3f objectToTriDistance = new Vector3f(triPt).sub(newTrajectory);
                float distance = objectToTriDistance.length();
                if(distance <= radius)
                    return true;
            }
            Vector3f intercept = rayIntersectsTriangle(posCopy, newTrajectory, vertices);
            if(intercept != null)
            {
                Vector3f normal = triangle.getSurfaceNormal();
                if(normal.y >= normal.x && normal.y >= normal.z) return true;
            }
        }
        return false;
    }

    public static Vector3f testTriangles(ArrayList<CollisionTriangle> triangles, GameObject object, Vector3f trajectory, float radius)
    {
        Vector3f posCopy = object.position;
        float biggerRadius = radius*1.01f; //inflate radius slightly to pass tests
        Vector3f newTrajectory = new Vector3f(trajectory);
        for(CollisionTriangle triangle : triangles)
        {
            //here we go..
            Vector3f movement = new Vector3f(newTrajectory).sub(posCopy);
            float movementLength = movement.length();
            if(triangle.getCentroidPos().sub(posCopy).length() > movementLength+biggerRadius+triangle.getMaxRadius()) continue;
            Vector3f surfNorm = triangle.getSurfaceNormal();
            Vector3f[] vertices = triangle.getVertices();
            Vector3f triPt = triangle.closestPoint(newTrajectory);
            float dotProduct = movement.dot(surfNorm);
            if(dotProduct >= 0) continue; //If intercept is coming from behind the triangle, ignore
            if(triPt != null)
            {
                Vector3f objectToTriDistance = new Vector3f(triPt).sub(newTrajectory);
                float distance = objectToTriDistance.length();
                if(distance <= radius)
                {
                    newTrajectory = new Vector3f(triPt);
                    newTrajectory.add(new Vector3f(surfNorm).mul(biggerRadius));
                    return newTrajectory; //Skip the intercept check if this check passed
                }
            }
            Vector3f intercept = rayIntersectsTriangle(posCopy, newTrajectory, vertices);
            if(intercept == null) continue; //if no intercept, next iteration
            newTrajectory = new Vector3f(intercept);
            newTrajectory.add(new Vector3f(surfNorm).mul(biggerRadius));
            return newTrajectory; //Skip the intercept check if this check passed
        }
        return null;
    }

    public static boolean onSurface(Scene scene, GameObject object)
    {
        float radius = object.getInteractionRadius();
        Vector3f trajectory = new Vector3f(object.position).sub(new Vector3f(0, radius, 0));
        ArrayList<Integer> levelMeshIndices = scene.getLevelModelIndices();
        ArrayList<Model> levelMeshes = scene.getAllModels();
        for(Integer idx : levelMeshIndices)
        {
            CollisionMesh collision = levelMeshes.get(idx).collision;
            if(collision == null) continue; //If no collision, skip this mesh
            for(CollisionNode node : collision.nodes)
            {
                if(!node.isObjectInNode(object, radius)) continue; //if not in node, skip this node
                ArrayList<CollisionTriangle> triangles = node.triangles;
                boolean hitSurface = testSurface(triangles, object, trajectory, radius);
                if(hitSurface)
                    return true;
            }
        }
        return false;
    }
    public static GameObject collisionCheck(Scene scene, GameObject object, Vector3f trajectory)
    {
        ArrayList<Integer> levelMeshIndices = scene.getLevelModelIndices();
        ArrayList<Model> levelMeshes = scene.getAllModels();
        float moveLength = new Vector3f(trajectory).sub(object.position).length();
        for(Integer idx : levelMeshIndices)
        {
            CollisionMesh collision = levelMeshes.get(idx).collision;
            if(collision == null) continue; //If no collision, skip this mesh
            for(CollisionNode node : collision.nodes)
            {
                if(!node.isObjectInNode(object, moveLength)) continue; //if not in node, skip this node
                ArrayList<CollisionTriangle> triangles = node.triangles;
                boolean hittingTris = true;
                while(hittingTris)
                {
                    Vector3f newTrajectory = Collision.testTriangles(triangles, object, trajectory, object.getInteractionRadius());
                    if(newTrajectory != null)
                    {
                        trajectory = newTrajectory;
                    }
                    else hittingTris = false;
                }
            }
        }
        //TODO: Object collision
        LinkedList<GameObject> objects = scene.getObjects();
        float objectRadius = object.getInteractionRadius();
        for(GameObject sceneObject : objects)
        {
            if(object.equals(sceneObject)) continue; //current object!
            float sceneObjectRadius = sceneObject.getInteractionRadius();
            if(Collision.spheresCollide(object.position, sceneObject.position, objectRadius, sceneObjectRadius))
            {
                //System.out.println("You touched the object");
                object.setNewPosition(trajectory);
                return sceneObject;
            }
        }
        object.setNewPosition(trajectory);
        return null;
    }
}
