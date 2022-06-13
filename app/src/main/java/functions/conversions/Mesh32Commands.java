package functions.conversions;

public class Mesh32Commands
{
    //Every command is followed by 24 bits representing data size following the command. Always in bytes
    //EXCEPT Commands 0,1,2, information below for their size parameter
    public static final byte CMD_START = (byte)0xFF; //Header command
    public static final byte CMD_END = (byte)0xFE; //End of file
    //Special size: Vertex count, 4 bytes per attribute * AttribCount (typically 48 bytes per vert)
    public static final byte CMD_VertexList = 0;
    //Special size: Polygon count, PolygonIndexCount*4 bytes per index (uint32) (typically 12 bytes per triangle)
    public static final byte CMD_PolygonIndexList = 1;
    //Special size: Texel count-1 (Bpp * texel count). i.e. 4096x4096 = 0xFFFFFF, 0x0 = 1 texel
    public static final byte CMD_TextureData = 2;
    public static final byte CMD_VertexAttribCount = 3; //How many attributes per vertex
    public static final byte CMD_PolygonIndexCount = 4; //How many indices per polygon
    //BYTES per pixel(-1). Default 4 (3 in raw data), max is 256 (0xFF).
    //Allows 1,073,741,824 max 4Bpp texels to be loaded if loading in as 256Bpp
    public static final byte CMD_Tex_Bpp = 5;
    public static final byte CMD_Material = 6; //Create new Material with String Name
    public static final byte CMD_Texture = 7; //Prepare new Texture with grid size to add to last material. Must follow with data
    //Set up texture clamping/mirroring in one byte
    //1st bit: S clamp, 2nd bit: T clamp
    //3rd bit: S Mirror, 4th bit: T Mirror
    public static final byte CMD_TextureClampMirror = 8;
    public static final byte CMD_Mat_AmbientColor = 9;
    public static final byte CMD_Mat_DiffuseColor = 0x0A;
    public static final byte CMD_Mat_SpecularColor = 0x0B;
    public static final byte CMD_Mat_Shininess = 0x0C; //Shine factor for specular
    public static final byte CMD_Mat_CullFace = 0x0D; //Cull polygons containing this material: 0 backface, 1 no cull, 2 frontface
    public static final byte CMD_TextureMapping = 0x0E; //Enable env mapping or automatic UV generation (0 none, 1 env, 2 static)
    public static final byte CMD_Tex_ScrollS = 0x0F; //Scroll factor setting on S axis, default is 0
    public static final byte CMD_Tex_ScrollT = 0x10; //Scroll factor setting on T axis, default is 0
    public static final byte CMD_PolygonFillType = 0x11; //Fill type: 0 fill, 1 GL_Line
    public static final byte CMD_TextureFiltering = 0x12; //0 linear, 1 nearest neighbour
    public static final byte CMD_CollisionVertices = 0x13; //Vertex Data for Collision
    public static final byte CMD_CollisionPolygons = 0x14; //Polygon Collision Data (Type, Indices)
}
