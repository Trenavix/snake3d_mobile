package functions;

public class OtherConstants
{
    public final static float DEG2RAD = 3.14159f / 180.0f;
    public final static float RAD2DEG = 180.0f / 3.14159f;
    public final static int bytesInFloat = 4;
    public final static int bytesInUInt32 = 4;
    //Vertex attributes:
    //Pos.x, Pos.y, Pos.z, UV.u, UV.v, R, G, B, A, Norm.x, Norm.y, Norm.z, Weight
    public final static int vertexElements = 14;
    public final static int vertUVOffset = 3;
    public final static int vertColorOffset = 5;
    public final static int vertNormalOffset = 9;
    public final static int vertWeightOffset = 12;
    public final static int vertBoneOffset = 13;
    public final static int polygonType_TRIANGLE=0;
    public final static int polygonType_LINE=1;
    public final static double nanoSecondsIn60FPS = 16666666.66666666667;
}
