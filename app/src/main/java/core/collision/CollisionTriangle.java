package core.collision;
import org.joml.Vector3f;

import java.util.ArrayList;

import functions.OtherConstants;

public class CollisionTriangle
{
    private Vector3f[] vertices;
    private Vector3f[] originTri;
    private int type;
    private Vector3f centroidPos;
    private Vector3f surfaceNormal;
    private float maxRadius;
    private Vector3f[] AABB;
    public CollisionTriangle(int[] vertexIndices, int type, float[] meshVertices)
    {
        if(vertexIndices.length != 3) resizeVertexArrayTo3(vertexIndices);
        this.type = type;
        this.vertices = new Vector3f[3];
        this.originTri = new Vector3f[3];
        for(int i=0; i<3; i++)
            vertices[i] = new Vector3f(
                    meshVertices[vertexIndices[i]* OtherConstants.vertexElements], //x coord
                    meshVertices[vertexIndices[i]* OtherConstants.vertexElements+1], //y coord
                    meshVertices[vertexIndices[i]* OtherConstants.vertexElements+2]); //z coord
        this.surfaceNormal = calculateNormal(vertices[0], vertices[1], vertices[2]);
        this.centroidPos = calculateCentroid(vertices);
        for(int i=0; i<3; i++) originTri[i] = new Vector3f(vertices[i]).sub(centroidPos);
        this.maxRadius = calculateLargestRadius();
        this.AABB = calculateBoundingBox();
    }
    public CollisionTriangle(int idx1, int idx2, int idx3, int type, float[] meshVertices)
    {
        this.type = type;
        this.vertices = new Vector3f[3];
        for(int i=0; i<3; i++)
            vertices[i] = new Vector3f(
                    meshVertices[idx1], //x coord
                    meshVertices[idx2], //y coord
                    meshVertices[idx3]); //z coord
        this.AABB = calculateBoundingBox();
    }
    public CollisionTriangle(short type, Vector3f vertex1, Vector3f vertex2, Vector3f vertex3)
    {
        this.type = type;
        this.vertices = new Vector3f[]{vertex1, vertex2, vertex3};
        this.surfaceNormal = calculateNormal(vertices[0], vertices[1], vertices[2]);
        this.centroidPos = calculateCentroid(vertices);
        this.originTri = new Vector3f[3];
        for(int i=0; i<3; i++) originTri[i] = new Vector3f(vertices[i]).sub(centroidPos);
        this.maxRadius = calculateLargestRadius();
        this.AABB = calculateBoundingBox();
    }

    private float calculateLargestRadius()
    {
        float radius = 0.0f;
        for(Vector3f vertex : originTri)
            if(vertex.length() > radius)
                radius = vertex.length();
        return radius;
    }

    public Vector3f[] getVertices()
    {
        return this.vertices;
    }
    public Vector3f[] getAABB()
    {
        return this.AABB;
    }
    public Vector3f[] getVerticesAsVectorCopies()
    {
        Vector3f[] newVerts = new Vector3f[vertices.length];
        for(int i=0; i<vertices.length; i++)
            newVerts[i] = new Vector3f(vertices[i]);
        return newVerts;
    }
    public Vector3f getSurfaceNormal() { return new Vector3f(surfaceNormal); }

    void resizeVertexArrayTo3(int[] array)
    {
        int[] newArray = new int[3];
        int size = array.length;
        for(int i=0; i<3; i++)
        {
            if(i <size) newArray[i] = array[i];
            else newArray[i] = 0;
        }
        array = newArray;
    }
    public static Vector3f calculateNormal(Vector3f v0, Vector3f v1, Vector3f v2)
    {
        Vector3f u = new Vector3f(v2).sub(v0);
        Vector3f w = new Vector3f(v1).sub(v0);
        Vector3f n = new Vector3f(u).cross(w);
        n.normalize();
        if (n.length() == 0)
        {
            System.out.println("It's NaN!");
            return new Vector3f(0,1,0);
        }
        return n.mul(-1.0f);
    }
    private Vector3f calculateCentroid(Vector3f[] verts)
    {
        Vector3f total = new Vector3f(verts[0]);
        for (int i = 1; i < 3; i++) total.add(verts[i]);
        return total.div(3.0f);
    }
    Vector3f getCentroidPos(){return new Vector3f(this.centroidPos); }
    float getMaxRadius() {return this.maxRadius; }

    private Vector3f[] calculateBoundingBox()
    {
        Vector3f placeholderVertex = this.vertices[0];
        Vector3f min = new Vector3f(placeholderVertex);
        Vector3f max = new Vector3f(placeholderVertex);
        for(Vector3f vertex : this.vertices)
        {
            if(vertex.x < min.x) min.x = vertex.x;
            else if(vertex.x > max.x) max.x = vertex.x;
            if(vertex.y < min.y) min.y = vertex.y;
            else if(vertex.y > max.y) max.y = vertex.y;
            if(vertex.z < min.z) min.z = vertex.z;
            else if(vertex.z > max.z) max.z = vertex.z;
        }
        return new Vector3f[]{min, max};
    }

    public Vector3f closestPoint(Vector3f p)
    {
        Vector3f a = new Vector3f(vertices[0]);
        Vector3f b = new Vector3f(vertices[1]);
        Vector3f c = new Vector3f(vertices[2]);
        // Find the normal to the plane: n = (b - a) x (c - a)
        Vector3f n = b.sub(a, new Vector3f()).cross(c.sub(a, new Vector3f()));

        // Normalize normal vector
        float nLen = n.length();
        if (nLen < 1.0e-30)
            return null;  // Triangle is degenerate
        else
            n.mul(1.0f / nLen);

        //    Project point p onto the plane spanned by a->b and a->c.
        //
        //    Given a plane
        //
        //        a : point on plane
        //        n : *unit* normal to plane
        //
        //    Then the *signed* distance from point p to the plane
        //    (in the direction of the normal) is
        //
        //        dist = p . n - a . n
        //
        float dist = p.dot(n) - a.dot(n);

        // Project p onto the plane by stepping the distance from p to the plane
        // in the direction opposite the normal: proj = p - dist * n
        Vector3f proj = p.add(n.mul(-dist, new Vector3f()), new Vector3f());

        // Find out if the projected point falls within the triangle -- see:
        // http://blackpawn.com/texts/pointinpoly/default.html

        // Compute edge vectors
        float v0x = c.x - a.x;
        float v0y = c.y - a.y;
        float v0z = c.z - a.z;
        float v1x = b.x - a.x;
        float v1y = b.y - a.y;
        float v1z = b.z - a.z;
        float v2x = proj.x - a.x;
        float v2y = proj.y - a.y;
        float v2z = proj.z - a.z;

        // Compute dot products
        float dot00 = v0x * v0x + v0y * v0y + v0z * v0z;
        float dot01 = v0x * v1x + v0y * v1y + v0z * v1z;
        float dot02 = v0x * v2x + v0y * v2y + v0z * v2z;
        float dot11 = v1x * v1x + v1y * v1y + v1z * v1z;
        float dot12 = v1x * v2x + v1y * v2y + v1z * v2z;

        // Compute barycentric coordinates (u, v) of projection point
        float denom = (dot00 * dot11 - dot01 * dot01);
        if (Math.abs(denom) < 1.0e-30)
            return null; // Triangle is degenerate
        float invDenom = 1.0f / denom;
        float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

        // Check barycentric coordinates
        if ((u >= 0) && (v >= 0) && (u + v < 1))
            // Nearest orthogonal projection point is in triangle
            return proj;
        else
            // Nearest orthogonal projection point is outside triangle
            return null;
    }
}
