package graphics.meshGenerators;

import graphics.VertexIndexBatch;

public class SampleVertices
{
    public static final float[] triVertices =
    {
            // X, Y, Z              U, V        R, G, B, A
            -0.5f, -0.5f, -.0f,      0.f, 0.f,     1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.5f, -.0f,        0.f, 0.f,     0.0f, 1.0f, 0.0f, 1.0f,
            0.5f, -0.5f, -.0f,       0.f, 0.f,     0.0f, 0.0f, 1.0f, 1.0f
    };
    public static final int[] triIndices = { 0, 1, 2 };

    public static final float[] boxVertices =
    {
            // X, Y, Z              U, V        R, G, B, A
            // Top
            -1.0f, 1.0f, -1.0f,   0.f, 0.f,     1.0f, 1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,   0.f, 1.f,      1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,    1.f, 1.f,      1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, -1.0f,   1.f, 0.f,      1.0f, 1.0f, 1.0f, 1.0f,

            // Left
            -1.0f, 1.0f, 1.0f,    1.f, 1.f,     1.0f, 1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,   0.f, 1.f,     0f, 0f, 0f, 1.0f,
            -1.0f, -1.0f, -1.0f,  0.f, 0.f,     0f, 0f, 0f, 1.0f,
            -1.0f, 1.0f, -1.0f,   1.f, 0.f,     1.0f, 1.0f, 1.0f, 1.0f,

            // Right
            1.0f, 1.0f, 1.0f,    1.f, 1.f,     1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,   0.f, 1.f,     0, 0, 0, 1.0f,
            1.0f, -1.0f, -1.0f,  0.f, 0.f,     0, 0, 0, 1.0f,
            1.0f, 1.0f, -1.0f,   1.f, 0.f,     1.0f, 1.0f, 1.0f, 1.0f,

            // Front
            1.0f, 1.0f, 1.0f,    1.f, 1.f,     1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,   1.f, 0.f,     0, 0.0f, 0, 1.0f,
            -1.0f, -1.0f, 1.0f,  0.f, 0.f,     0, 0.0f, 0, 1.0f,
            -1.0f, 1.0f, 1.0f,   0.f, 1.f,     1.0f, 1.0f, 1.0f, 1.0f,

            // Back
            1.0f, 1.0f, -1.0f,    1.f, 1.f,    1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, -1.0f, -1.0f,   1.f, 0.f,     0.0f, 0, 0, 1.0f,
            -1.0f, -1.0f, -1.0f,  0.f, 0.f,     0.0f, 0, 0, 1.0f,
            -1.0f, 1.0f, -1.0f,   0.f, 1.f,     1.0f, 1.0f, 1.0f, 1.0f,

            // Bottom
            -1.0f, -1.0f, -1.0f,  0.f, 0.f,     0, 0, 0, 1.0f,
            -1.0f, -1.0f, 1.0f,   0.f, 1.f,     0, 0, 0, 1.0f,
            1.0f, -1.0f, 1.0f,    1.f, 1.f,     0, 0, 0, 1.0f,
            1.0f, -1.0f, -1.0f,   1.f, 1.f,     0, 0, 0, 1.0f,
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

    public VertexIndexBatch getBoxBatch()
    {
        return new VertexIndexBatch(boxVertices, boxIndices);
    }
    public VertexIndexBatch getTriBatch()
    {
        return new VertexIndexBatch(triVertices, triIndices);
    }

}
