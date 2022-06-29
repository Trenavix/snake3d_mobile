package functions.conversions;

public class Mesh32CMD
{
    //Every command is followed by 24 bits representing data size following the command. Always in bytes
    //EXCEPT Commands 0,1,2, information below for their size parameter
    public static final byte START = (byte)0xFF; //Header command
    public static final byte END = (byte)0xFE; //End of file
    //Special size: Vertex count, 4 bytes per attribute * AttribCount (typically 48 bytes per vert)
    public static final byte VertexList = 0;
    //Special size: Polygon count, PolygonIndexCount*4 bytes per index (uint32) (typically 12 bytes per triangle)
    public static final byte PolygonIndexList = 1;
    //Special size: Texel count-1 (Bpp * texel count). i.e. 4096x4096 = 0xFFFFFF, 0x0 = 1 texel
    public static final byte TextureData = 2;
    public static final byte VertexAttribCount = 3; //How many attributes per vertex
    public static final byte PolygonIndexCount = 4; //How many indices per polygon
    //BYTES per pixel(-1). Default 4 (3 in raw data), max is 256 (0xFF).
    //Allows 1,073,741,824 max 4Bpp texels to be loaded if loading in as 256Bpp (32768x32768 or 32K)
    public static final byte TextureBpp = 5;
    public static final byte Material = 6; //Create new Material with String Name
    public static final byte Texture = 7; //Prepare new Texture with grid size to add to last material. Must follow with data
    //Set up texture clamping/mirroring in one byte
    //1st bit: S clamp, 2nd bit: T clamp, 3rd bit: R clamp
    //4th bit: S mirror, 5th bit: T mirror, 6th bit: R mirror
    public static final byte TextureWrap = 8;
    public static final byte Mat_AmbientColor = 9; //Followed by 3 bytes OR 3 floats: R G B
    public static final byte Mat_DiffuseColor = 0x0A; //Followed by 3 bytes OR 3 floats: R G B
    public static final byte Mat_SpecularColor = 0x0B; //Followed by 3 bytes OR 3 floats: R G B
    public static final byte Mat_Shininess = 0x0C; //Shine factor for specular (1 float)
    public static final byte Mat_CullFace = 0x0D; //Cull polygons containing this material: 0 back-face, 1 no cull, 2 front-face
    public static final byte TextureMapping = 0x0E; //Enable env mapping or automatic UV generation (0 none, 1 env, 2 static)
    public static final byte TextureScrollS = 0x0F; //Scroll factor setting on S axis, default is 0
    public static final byte TextureScrollT = 0x10; //Scroll factor setting on T axis, default is 0
    public static final byte PolygonFillType = 0x11; //Fill type: 0 fill, 1 GL_Line
    public static final byte TextureFiltering = 0x12; //0 linear, 1 nearest neighbour
    public static final byte CollisionVertices = 0x13; //Vertex Data for Collision
    public static final byte CollisionPolygons = 0x14; //Polygon Collision Data (Type, Indices)
    public static final byte CollisionOctreeLevel = 0x15; //How many splits to do for octree processing (0 for none)
    public static final byte CollisionBoundingBox = 0x16; //Main bounding box (x/y/z min and x/y/z max, 6xFloat)
    public static final byte Billboard = 0x17; //Enable billboarding on current bone
    public static final byte BoneCount = 0x18; //specify how many bones to be in model
    public static final byte Bone = 0x19; //Create new bone with ID (ushort) and enter this bone
    public static final byte PopBone = 0x1A; //Exit current bone and return to parent bone
    public static final byte BoneMtx = 0x1B; //Set current bone's matrix (16 floats, mat4x4)
    public static final byte BoneName = 0x1C; //Set current bone's matrix (16 floats, mat4x4)
    public static final byte Animation = 0x1C; //Add animation to list of animations
    public static final byte KeyFrame = 0x1D; //Add keyframe to current animation (UInt32 frame)
    public static final byte KeyRotation = 0x1E; //Add pivot to current keyframe (BoneID, vec3 transform)
    public static final byte KeyTranslation = 0x1F; //Add pivot to current keyframe (BoneID, vec3 transform)
    public static final byte KeyScale = 0x20; //Add pivot to current keyframe (BoneID, vec3 transform)
}
