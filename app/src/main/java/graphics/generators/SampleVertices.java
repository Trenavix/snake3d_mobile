package graphics.generators;

import static functions.OtherConstants.vertColorOffset;
import static functions.OtherConstants.vertexElements;

import graphics.VertexIndexBatch;

public class SampleVertices
{
    public static final float[] triVertices =
    {
            // X, Y, Z              U, V        R, G, B, A                  Weight
            -0.5f, -0.5f, -.0f,      0.f, 0.f,     1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.5f, -.0f,        0.f, 0.f,     0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
            0.5f, -0.5f, -.0f,       0.f, 0.f,     0.0f, 0.0f, 1.0f, 1.0f, 0.0f
    };
    public static final int[] triIndices = { 0, 1, 2 };

    public static final float[] boxVertices =
    {
            // X, Y, Z              U, V        R, G, B, A                  Weight
            // Top
            -1.0f, 1.0f, -1.0f,   0.f, 0.f,     1.0f, 1.0f, 1.0f, 1.0f,     0.0f,
            -1.0f, 1.0f, 1.0f,   0.f, 1.f,      1.0f, 1.0f, 1.0f, 1.0f,     0.0f,
            1.0f, 1.0f, 1.0f,    1.f, 1.f,      1.0f, 1.0f, 1.0f, 1.0f,     0.0f,
            1.0f, 1.0f, -1.0f,   1.f, 0.f,      1.0f, 1.0f, 1.0f, 1.0f,     0.0f,

            // Left
            -1.0f, 1.0f, 1.0f,    1.f, 1.f,     1.0f, 1.0f, 1.0f, 1.0f,     0.0f,
            -1.0f, -1.0f, 1.0f,   0.f, 1.f,     0f, 0f, 0f, 1.0f,           0.0f,
            -1.0f, -1.0f, -1.0f,  0.f, 0.f,     0f, 0f, 0f, 1.0f,           0.0f,
            -1.0f, 1.0f, -1.0f,   1.f, 0.f,     1.0f, 1.0f, 1.0f, 1.0f,     0.0f,

            // Right
            1.0f, 1.0f, 1.0f,    1.f, 1.f,     1.0f, 1.0f, 1.0f, 1.0f,      0.0f,
            1.0f, -1.0f, 1.0f,   0.f, 1.f,     0, 0, 0, 1.0f,               0.0f,
            1.0f, -1.0f, -1.0f,  0.f, 0.f,     0, 0, 0, 1.0f,               0.0f,
            1.0f, 1.0f, -1.0f,   1.f, 0.f,     1.0f, 1.0f, 1.0f, 1.0f,      0.0f,

            // Front
            1.0f, 1.0f, 1.0f,    1.f, 1.f,     1.0f, 1.0f, 1.0f, 1.0f,      0.0f,
            1.0f, -1.0f, 1.0f,   1.f, 0.f,     0, 0.0f, 0, 1.0f,            0.0f,
            -1.0f, -1.0f, 1.0f,  0.f, 0.f,     0, 0.0f, 0, 1.0f,            0.0f,
            -1.0f, 1.0f, 1.0f,   0.f, 1.f,     1.0f, 1.0f, 1.0f, 1.0f,      0.0f,

            // Back
            1.0f, 1.0f, -1.0f,    1.f, 1.f,    1.0f, 1.0f, 1.0f, 1.0f,      0.0f,
            1.0f, -1.0f, -1.0f,   1.f, 0.f,     0.0f, 0, 0, 1.0f,           0.0f,
            -1.0f, -1.0f, -1.0f,  0.f, 0.f,     0.0f, 0, 0, 1.0f,           0.0f,
            -1.0f, 1.0f, -1.0f,   0.f, 1.f,     1.0f, 1.0f, 1.0f, 1.0f,     0.0f,

            // Bottom
            -1.0f, -1.0f, -1.0f,  0.f, 0.f,     0, 0, 0, 1.0f,              0.0f,
            -1.0f, -1.0f, 1.0f,   0.f, 1.f,     0, 0, 0, 1.0f,              0.0f,
            1.0f, -1.0f, 1.0f,    1.f, 1.f,     0, 0, 0, 1.0f,              0.0f,
            1.0f, -1.0f, -1.0f,   1.f, 1.f,     0, 0, 0, 1.0f,              0.0f,
    };
    public static final int[] boxIndices =
    {
            // Top
            0, 1, 2,
            0, 2, 3,

            // Left
            5, 4, 6,
            6, 4, 7,

            // Right
            8, 9, 10,
            8, 10, 11,

            // Front
            13, 12, 14,
            15, 14, 12,

            // Back
            16, 17, 18,
            16, 18, 19,

            // Bottom
            21, 20, 22,
            22, 20, 23
    };

    public static float[] flagVertices =
    {
            // X, Y, Z              U, V        R, G, B, A                 Normals                Weight
            // First square
            1.0f, 1.0f, 1.0f,    1.f, 1.f,     1.0f, 1.0f, 1.0f, 1.0f,     0.0f, 0.0f, 1.0f,      0.05f,
            1.0f, -1.0f, 1.0f,   1.f, 0.f,     0.5f, 0.5f, 0.5f, 1.0f,     0.0f, 0.0f, 1.0f,      0.05f,
            -0.0f, -1.0f, 1.0f,  0.f, 0.f,     0.5f, 0.5f, 0.5f, 1.0f,     0.0f, 0.0f, 1.0f,      0.0f,
            -0.0f, 1.0f, 1.0f,   0.f, 1.f,     1.0f, 1.0f, 1.0f, 1.0f,     0.0f, 0.0f, 1.0f,      0.0f,

            // Second square
            2.0f, 1.0f, 1.0f,    1.f, 1.f,     1.0f, 1.0f, 1.0f, 1.0f,     0.0f, 0.0f, 1.0f,      0.1f,
            2.0f, -1.0f, 1.0f,   1.f, 0.f,     0.5f, 0.5f, 0.5f, 1.0f,     0.0f, 0.0f, 1.0f,      0.1f,
            1.0f, -1.0f, 1.0f,  0.f, 0.f,      0.5f, 0.5f, 0.5f, 1.0f,     0.0f, 0.0f, 1.0f,      0.05f,
            1.0f, 1.0f, 1.0f,   0.f, 1.f,      1.0f, 1.0f, 1.0f, 1.0f,     0.0f, 0.0f, 1.0f,      0.05f,

            // Third square
            3.0f, 1.0f, 1.0f,    1.f, 1.f,     1.0f, 1.0f, 1.0f, 1.0f,     0.0f, 0.0f, 1.0f,      0.2f,
            3.0f, -1.0f, 1.0f,   1.f, 0.f,     0.5f, 0.5f, 0.5f, 1.0f,     0.0f, 0.0f, 1.0f,      0.2f,
            2.0f, -1.0f, 1.0f,  0.f, 0.f,      0.5f, 0.5f, 0.5f, 1.0f,     0.0f, 0.0f, 1.0f,      0.1f,
            2.0f, 1.0f, 1.0f,   0.f, 1.f,      1.0f, 1.0f, 1.0f, 1.0f,     0.0f, 0.0f, 1.0f,      0.1f,

            // Fourth square
            4.0f, 1.0f, 1.0f,    1.f, 1.f,     1.0f, 1.0f, 1.0f, 1.0f,     0.0f, 0.0f, 1.0f,      0.4f,
            4.0f, -1.0f, 1.0f,   1.f, 0.f,     0.5f, 0.5f, 0.5f, 1.0f,     0.0f, 0.0f, 1.0f,      0.4f,
            3.0f, -1.0f, 1.0f,  0.f, 0.f,      0.5f, 0.5f, 0.5f, 1.0f,     0.0f, 0.0f, 1.0f,      0.2f,
            3.0f, 1.0f, 1.0f,   0.f, 1.f,      1.0f, 1.0f, 1.0f, 1.0f,     0.0f, 0.0f, 1.0f,      0.2f,

            // Fifth square
            5.0f, 1.0f, 1.0f,    1.f, 1.f,     1.0f, 1.0f, 1.0f, 1.0f,     0.0f, 0.0f, 1.0f,      0.8f,
            5.0f, -1.0f, 1.0f,   1.f, 0.f,     0.5f, 0.5f, 0.5f, 1.0f,     0.0f, 0.0f, 1.0f,      0.8f,
            4.0f, -1.0f, 1.0f,  0.f, 0.f,      0.5f, 0.5f, 0.5f, 1.0f,     0.0f, 0.0f, 1.0f,      0.4f,
            4.0f, 1.0f, 1.0f,   0.f, 1.f,      1.0f, 1.0f, 1.0f, 1.0f,     0.0f, 0.0f, 1.0f,      0.4f,
    };

    public static final int[] flagIndices =
            {
                    // First
                    0, 1, 2,
                    0, 2, 3,

                    // Second
                    5, 4, 6,
                    6, 4, 7,

                    // Third
                    8, 9, 10,
                    8, 10, 11,

                    // Fourth
                    13, 12, 14,
                    15, 14, 12,

                    // Fifth
                    16, 17, 18,
                    16, 18, 19,
            };

    public static VertexIndexBatch getBoxBatch()
    {
        return new VertexIndexBatch(boxVertices, boxIndices);
    }
    public static VertexIndexBatch getTriBatch()
    {
        return new VertexIndexBatch(triVertices, triIndices);
    }
    public static VertexIndexBatch getFlagBatch() { return new VertexIndexBatch(flagVertices, flagIndices); }
    public static VertexIndexBatch generateGridPoints(float width, float height, short partitions_x, short partitions_y, float position_y)
    {
        float partitionwidth = width / partitions_x;
        float partitionheight = height / partitions_y;
        partitions_x++; partitions_y++; //For border purposes
        int pointcount = ((partitions_x)*2) + ((partitions_y)*2);
        int[] indices = new int[pointcount];
        float[] vertices = new float[pointcount*vertexElements]; //floats per vertex
        for(int i=0; i<pointcount; i++) //Enum all indices
        {
            indices[i] = i;
        }
        for(int i=0; i<partitions_x; i++)
        {
            int idx = i*vertexElements*2; //floats per vertex, steps of two
            float x = (partitionwidth * i) - (width/2);
            vertices[idx] = x; //x
            vertices[idx+1] = position_y; //y
            vertices[idx+2] = height / 2; //z
            for(int j=vertColorOffset; j<vertexElements; j++) vertices[idx+j] = 0.5f; //colour values
            vertices[idx+vertexElements] = x; //x #2
            vertices[idx+vertexElements+1] = position_y; //y
            vertices[idx+vertexElements+2] = -height / 2; //z #2
            for(int j=vertexElements+vertColorOffset; j<vertexElements*2; j++) vertices[idx+j] = 0.5f; //colour values
        }
        for(int i=0; i<partitions_y; i++)
        {
            int idx = (partitions_x*2*vertexElements) + (i*vertexElements*2); //y begins after the x vertices
            float y = (partitionheight * i) - (height / 2);
            vertices[idx] = width / 2; //x
            vertices[idx+1] = position_y; //y
            vertices[idx+2] = y; //z
            for(int j=vertColorOffset; j<vertexElements; j++) vertices[idx+j] = 0.5f; //colour values
            vertices[idx+vertexElements] = -width / 2; //x #2
            vertices[idx+vertexElements+1] = position_y; //y
            vertices[idx+vertexElements+2] = y; //z #2
            for(int j=vertexElements+vertColorOffset; j<vertexElements*2; j++) vertices[idx+j] = 0.5f; //colour values
        }
        return new VertexIndexBatch(vertices, indices);
    }
}
