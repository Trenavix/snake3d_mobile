package core.collision;

import android.os.Build;

import org.joml.Vector3f;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import core.objects.BehavObject;
import core.objects.GameObject;

public class CollisionNode
{
    public ArrayList<CollisionTriangle> triangles = new ArrayList<>();
    private Vector3f[] AABB = new Vector3f[2];
    private Vector3f centrePt;
    private Vector3f extent;
    float radius; //For spherical overlaps instead of AABB
    public CollisionNode(Vector3f minBB, Vector3f maxBB)
    {
        this.AABB[0] = minBB;
        this.AABB[1] = maxBB;
        this.centrePt = new Vector3f(minBB).add(maxBB).div(2.0f); //average
        this.extent = new Vector3f(maxBB).sub(centrePt);
        this.radius = extent.length();
    }

    public void calculateNodeTriangles(ArrayList<CollisionTriangle> tris)
    {
        for(CollisionTriangle triangle : tris)
            if(BoxesIntersect(triangle, this.AABB))
                triangles.add(triangle);
    }

    private boolean BoxesIntersect(CollisionTriangle triangle, Vector3f[] aabb)
    {
        Vector3f[] TriBB = triangle.getAABB();
        if(isAABBOverlappingXAxis(aabb, TriBB)
            && isAABBOverlappingYAxis(aabb, TriBB)
            && isAABBOverlappingZAxis(aabb, TriBB))
            return true;
        return false;
    }

    public boolean isObjectInNode(GameObject object, float moveLength)
    {
        float radius = object.getInteractionRadius()+moveLength;
        /*Vector3f[] objectBox = new Vector3f[]
                {
                        new Vector3f(extentDir).mul(radius).add(object.position),
                        new Vector3f(extentDir).mul(-radius).add(object.position)
                };
        return isAABBOverlappingXAxis(objectBox, this.AABB)
                && isAABBOverlappingYAxis(objectBox, this.AABB)
                && isAABBOverlappingZAxis(objectBox, this.AABB);*/
        return isSphereInBoxSphere(object.position, radius);
    }

    private boolean isSphereInBoxSphere(Vector3f pt, float radius)
    {
        float distance = new Vector3f(pt).sub(this.centrePt).length();
        return distance <= radius + this.radius;
    }

    private static boolean isAABBOverlappingXAxis(Vector3f[] box1,Vector3f[] box2)
    { return (box1[1].x >= box2[0].x && box2[1].x >= box1[0].x);}
    private static boolean isAABBOverlappingYAxis(Vector3f[] box1,Vector3f[] box2)
    { return (box1[1].y >= box2[0].y && box2[1].y >= box1[0].y);}
    private static boolean isAABBOverlappingZAxis(Vector3f[] box1,Vector3f[] box2)
    { return (box1[1].z >= box2[0].z && box2[1].z >= box1[0].z);}

    //TODO: Implement the true triangle-AABB testing algorithm
    /*private boolean Intersects(CollisionTriangle triangle, Vector3f[] aabb, Vector3f extents)
    {
        Vector3f[] verts = triangle.getVerticesAsVectors();

        // Convert AABB to center-extents form
        Vector3f c = this.centrePt;
        Vector3f e = extents;

        // Translate the triangle as conceptually moving the AABB to origin
        // This is the same as we did with the point in triangle test
        for(Vector3f vert : verts) vert.sub(c);

        // Compute the edge vectors of the triangle  (ABC)
        // That is, get the lines between the points as vectors
        Vector3f f0 = new Vector3f(verts[1]).sub(verts[0]); // B - A
        Vector3f f1 = new Vector3f(verts[2]).sub(verts[1]); // C - B
        Vector3f f2 = new Vector3f(verts[0]).sub(verts[2]); // A - C

        // Compute the face normals of the AABB, because the AABB
        // is at center, and of course axis aligned, we know that
        // it's normals are the X, Y and Z axis.
        Vector3f u0 = new Vector3f(1.0f, 0.0f, 0.0f);
        Vector3f u1 = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f u2 = new Vector3f(0.0f, 0.0f, 1.0f);

        // There are a total of 13 axis to test!

        // We first test against 9 axis, these axis are given by
        // cross product combinations of the edges of the triangle
        // and the edges of the AABB. You need to get an axis testing
        // each of the 3 sides of the AABB against each of the 3 sides
        // of the triangle. The result is 9 axis of seperation
        // https://awwapp.com/b/umzoc8tiv/

        // Compute the 9 axis
        Vector3 axis_u0_f0 = Vector3.Cross(u0, f0);
        Vector3 axis_u0_f1 = Vector3.Cross(u0, f1);
        Vector3 axis_u0_f2 = Vector3.Cross(u0, f2);

        Vector3 axis_u1_f0 = Vector3.Cross(u1, f0);
        Vector3 axis_u1_f1 = Vector3.Cross(u1, f1);
        Vector3 axis_u1_f2 = Vector3.Cross(u2, f2);

        Vector3 axis_u2_f0 = Vector3.Cross(u2, f0);
        Vector3 axis_u2_f1 = Vector3.Cross(u2, f1);
        Vector3 axis_u2_f2 = Vector3.Cross(u2, f2);

        // Testing axis: axis_u0_f0
        // Project all 3 vertices of the triangle onto the Seperating axis
        float p0 = Vector3.Dot(v0, axis_u0_f0);
        float p1 = Vector3.Dot(v1, axis_u0_f0);
        float p2 = Vector3.Dot(v2, axis_u0_f0);
        // Project the AABB onto the seperating axis
        // We don't care about the end points of the prjection
        // just the length of the half-size of the AABB
        // That is, we're only casting the extents onto the
        // seperating axis, not the AABB center. We don't
        // need to cast the center, because we know that the
        // aabb is at origin compared to the triangle!
        float r = e.X * Math.Abs(Vector3.Dot(u0, axis_u0_f0)) +
                e.Y * Math.Abs(Vector3.Dot(u1, axis_u0_f0)) +
                e.Z * Math.Abs(Vector3.Dot(u2, axis_u0_f0));
        // Now do the actual test, basically see if either of
        // the most extreme of the triangle points intersects r
        // You might need to write Min & Max functions that take 3 arguments
        if (Max(-Max(p0, p1, p2), Min(p0, p1, p2)) > r) {
            // This means BOTH of the points of the projected triangle
            // are outside the projected half-length of the AABB
            // Therefore the axis is seperating and we can exit
            return false;
        }

        // Repeat this test for the other 8 seperating axis
        // You may wish to make some kind of a helper function to keep
        // things readable
        TODO: 8 more SAT tests

        // Next, we have 3 face normals from the AABB
        // for these tests we are conceptually checking if the bounding box
        // of the triangle intersects the bounding box of the AABB
        // that is to say, the seperating axis for all tests are axis aligned:
        // axis1: (1, 0, 0), axis2: (0, 1, 0), axis3 (0, 0, 1)
        TODO: 3 SAT tests
        // Do the SAT given the 3 primary axis of the AABB
        // You already have vectors for this: u0, u1 & u2

        // Finally, we have one last axis to test, the face normal of the triangle
        // We can get the normal of the triangle by crossing the first two line segments
        Vector3 triangleNormal = Vector3.Cross(f0, f1);
        TODO: 1 SAT test

        // Passed testing for all 13 seperating axis that exist!
        return true;
    }*/
}
