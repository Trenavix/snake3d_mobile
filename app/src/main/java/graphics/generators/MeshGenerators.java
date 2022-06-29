package graphics.generators;

import static functions.OtherConstants.polygonType_LINE;
import static functions.OtherConstants.polygonType_TRIANGLE;

import android.graphics.Bitmap;

import java.util.ArrayList;

import graphics.Material;
import graphics.Mesh;
import graphics.Model;
import graphics.Texture;
import graphics.VertexIndexBatch;

public class MeshGenerators
{
    public static Model generateGridMesh(float width, float height, short partitions_x, short partitions_y, float position_y, int color)
    {
        VertexIndexBatch grid = SampleVertices.generateGridPoints
                (width, height, partitions_x, partitions_y, position_y);
        Material[] texList = TextureGenerators.generateSingleMaterial(color); //white
        int[] idxOffsets = new int[]{0};
        Model newModel = new Model(grid.vertices, texList);
        newModel.addMesh(new Mesh(newModel, grid.indices, idxOffsets, polygonType_LINE));
        return newModel;
    }

    public static Model generateFlagMesh(int color)
    {
        VertexIndexBatch flag = SampleVertices.getFlagBatch();
        Material[] texList = TextureGenerators.generateSingleMaterial(color); //white
        int[] idxOffsets = new int[]{0};
        Model newModel = new Model(flag.vertices, texList);
        newModel.addMesh(new Mesh(newModel, flag.indices, idxOffsets, polygonType_TRIANGLE));
        return newModel;
    }
}
