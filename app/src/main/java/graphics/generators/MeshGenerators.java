package graphics.generators;

import static functions.OtherConstants.polygonType_LINE;

import android.graphics.Bitmap;

import java.util.ArrayList;

import graphics.Material;
import graphics.Mesh;
import graphics.Texture;
import graphics.VertexIndexBatch;

public class MeshGenerators
{
    public static Mesh generateGridMesh(float width, float height, short partitions_x, short partitions_y, float position_y, int color)
    {
        VertexIndexBatch grid = SampleVertices.generateGridPoints
                (width, height, partitions_x, partitions_y, position_y);
        Material[] texList = new Material[1];
        ArrayList<Texture> texture = new ArrayList<>();
        texture.add(TextureGenerators.singlePixel(color));
        texList[0] = new Material(texture, "null",0);
        int[] idxOffsets = new int[]{0};
        return  new Mesh(grid.vertices, grid.indices, texList, idxOffsets, polygonType_LINE);
    }
}
