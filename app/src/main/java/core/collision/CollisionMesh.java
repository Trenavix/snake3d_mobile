package core.collision;

import android.os.Build;

import org.joml.Vector3f;

import java.util.ArrayList;

public class CollisionMesh
{
    private ArrayList<CollisionTriangle> triangles;
    private Vector3f[] boundingBox;
    public ArrayList<CollisionNode> nodes;
    public CollisionMesh(ArrayList<CollisionTriangle> triangles, int octreeLevel)
    {
        this.triangles = triangles;
        if(triangles == null || triangles.size() < 1) return;
        this.boundingBox = calculateBoundingBox(this.triangles);
        generateNodes(octreeLevel);
        if(this.nodes == null) return;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) return;
        nodes.parallelStream().forEach((node) ->
        {
            node.calculateNodeTriangles(triangles);
        });
    }
    //TODO: Constructor that includes bounding box in the inputs (saves loadup time)

    private Vector3f[] calculateBoundingBox(ArrayList<CollisionTriangle> tris)
    {
        if(tris.size() < 1) return null;
        Vector3f placeholderVertex = tris.get(0).getVertices()[0];
        Vector3f min = new Vector3f(placeholderVertex);
        Vector3f max = new Vector3f(placeholderVertex);
        for(CollisionTriangle tri: tris)
        {
            Vector3f[] verts = tri.getVertices();
            for(Vector3f vertex : verts)
            {
                if(vertex.x < min.x) min.x = vertex.x;
                else if(vertex.x > max.x) max.x = vertex.x;
                if(vertex.y < min.y) min.y = vertex.y;
                else if(vertex.y > max.y) max.y = vertex.y;
                if(vertex.z < min.z) min.z = vertex.z;
                else if(vertex.z > max.z) max.z = vertex.z;
            }
        }
        return new Vector3f[]{min, max};
    }

    private void generateNodes(int octreeLevel)
    {
        nodes = new ArrayList<CollisionNode>();
        int splits = octreeLevel+1;
        Vector3f partition3D =
                new Vector3f(this.boundingBox[1]).sub(this.boundingBox[0]).div(splits);
        for(int z = 0; z < splits; z++)
        {
            for(int y = 0; y < splits; y++)
            {
                for(int x=0; x < splits; x++)
                {
                    Vector3f minBB = new Vector3f(this.boundingBox[0]);
                    minBB.add(
                            new Vector3f(
                                    partition3D.x*x,
                                    partition3D.y*y,
                                    partition3D.z*z));
                    this.nodes.add(new CollisionNode(
                            minBB,
                            new Vector3f(minBB).add(partition3D)));
                }
            }
        }
    }
}
