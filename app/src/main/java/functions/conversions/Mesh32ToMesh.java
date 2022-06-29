package functions.conversions;

import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

import core.collision.CollisionTriangle;
import functions.OtherConstants;
import graphics.BoneNode;
import graphics.Material;
import graphics.Mesh;
import graphics.Model;
import graphics.Texture;

public class Mesh32ToMesh
{
    private static final byte stepParamByteCount = 4;
    private static int vertexAttribCount = OtherConstants.vertexElements;//13 by default
    private static int vertexByteCount = vertexAttribCount*OtherConstants.bytesInFloat;
    private static int verticesPerPolygon = 3;//3 by default
    private static int indexBytesPerPolygon = verticesPerPolygon*OtherConstants.bytesInFloat;
    private static int bytesPerPixel = 4;//4 by default (RGBA32)
    private static BoneNode currentBone;
    private static BoneNode[] allBones;
    private static int boneIDIncrement;
    public static Model Mesh32ToModel(byte[] mesh32, float scale)
    {
        boneIDIncrement = 0;
        ArrayList<Float> vertexData = new ArrayList<>();
        ArrayList<Material> materials = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();
        ArrayList<CollisionTriangle> collisionTris = new ArrayList<>();
        ArrayList<Vector3f> collisionVerts = new ArrayList<>();
        ArrayList<Integer> materialIndexOffsets = new ArrayList<>();;
        int stepBytes = 0;
        for(int i=0; i<mesh32.length; i+=stepBytes)
        {
            byte cmd = mesh32[i];
            int step =  ((mesh32[i+1] & 0xFF) << 16) | ((mesh32[i+2] & 0xFF) << 8) | (mesh32[i+3] & 0xFF);
            stepBytes = getStep(cmd, step);
            i+=stepParamByteCount; //Move to data offset of command (step param is 3 bytes long)
            switch(cmd)
            {
                case Mesh32CMD.START:
                    break;
                case Mesh32CMD.VertexAttribCount:
                    vertexAttribCount = (mesh32[i] & 0xFF);
                    vertexByteCount = vertexAttribCount * OtherConstants.bytesInFloat;
                    break;
                case Mesh32CMD.TextureBpp:
                    bytesPerPixel = (mesh32[i] & 0xFF)+1;
                    break;
                case Mesh32CMD.VertexList:
                    for(int j=0; j< step; j++) //For each vertex
                    {
                        for(int k=0;k<vertexAttribCount; k++) //for each float/attribute
                        {
                            byte[] floatBytes = new byte[OtherConstants.bytesInFloat];
                            for(int l =0; l<OtherConstants.bytesInFloat; l++)
                                floatBytes[l] = mesh32[i+(j*vertexByteCount)+(4*k)+l];
                            Float f = ByteBuffer.wrap(floatBytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                            if( k < OtherConstants.vertUVOffset)
                                f *= scale;
                            vertexData.add(f); //Add every individual float/attribute
                        }
                        if(vertexAttribCount < OtherConstants.vertexElements)
                        {
                            for (int k=0; k < (OtherConstants.vertexElements - vertexAttribCount); k++)
                                vertexData.add(0.0f); //Padding for missing attributes i.e. weights
                        }
                    }
                    break;
                case Mesh32CMD.Material:
                    materialIndexOffsets.add(indices.size()); //create new index offset for new material
                    String matName = "";
                    for(int j=0; j<step; j++)
                        matName += (char)mesh32[i+j];
                    materials.add(new Material(new ArrayList<Texture>(), matName, 0));
                    break;
                case Mesh32CMD.Texture:
                    int width = readShortFromBytes(mesh32, i)+1;
                    int height = readShortFromBytes(mesh32, i+2)+1;
                    materials.get(materials.size()-1).addTexture(new Texture(width, height, null));
                    break;
                case Mesh32CMD.TextureData:
                    Texture currentTexture = materials.get(materials.size()-1).getLastTexture();
                    byte[] texData = new byte[bytesPerPixel*currentTexture.getWidth()* currentTexture.getHeight()];
                    for(int j=0; j<texData.length; j++)
                        texData[j] = mesh32[i+j];
                    currentTexture.setRawData(texData);
                    break;
                case Mesh32CMD.PolygonIndexCount:
                    verticesPerPolygon = (mesh32[i] & 0xFF)+1;
                    indexBytesPerPolygon = verticesPerPolygon * OtherConstants.bytesInUInt32;
                    break;
                case Mesh32CMD.PolygonIndexList:
                    for(int j=0; j< step; j++) //For every polygon
                        for(int k=0; k<verticesPerPolygon; k++) //For every index
                            indices.add
                                    (readIntFromBytes
                                        (
                                              mesh32,
                                        i+
                                              (j*indexBytesPerPolygon)+
                                              (k*OtherConstants.bytesInUInt32)
                                        )
                                    );

                    break;
                case Mesh32CMD.TextureMapping:
                    if(mesh32[i] == 1) materials.get(materials.size()-1).sphereMapping(true);
                    break;
                case Mesh32CMD.TextureScrollS:
                    materials.get(materials.size()-1).getLastTexture().scrollFactors.x =
                            readFloatFromBytes(mesh32, i);
                    break;
                case Mesh32CMD.TextureScrollT:
                    materials.get(materials.size()-1).getLastTexture().scrollFactors.y =
                            readFloatFromBytes(mesh32, i);
                    break;
                case Mesh32CMD.Mat_AmbientColor:
                    if(step  == 3)
                    {
                        float R = (mesh32[i] & 0xFF) / 255.0f;
                        float G = (mesh32[i+1] & 0xFF) / 255.0f;
                        float B = (mesh32[i+2] & 0xFF) / 255.0f;
                        materials.get(materials.size()-1).setAmbientColor(new Vector3f(R, G, B));
                        break;
                    }
                    float R = readFloatFromBytes(mesh32, i);
                    float G = readFloatFromBytes(mesh32, i+OtherConstants.bytesInFloat);
                    float B = readFloatFromBytes(mesh32, i+(OtherConstants.bytesInFloat*2));
                    materials.get(materials.size()-1).setAmbientColor(new Vector3f(R, G, B));
                    break;
                case Mesh32CMD.Mat_DiffuseColor:
                    if(step == 3)
                    {
                        float R_d = (mesh32[i] & 0xFF) / 255.0f;
                        float G_d = (mesh32[i+1] & 0xFF) / 255.0f;
                        float B_d = (mesh32[i+2] & 0xFF) / 255.0f;
                        materials.get(materials.size()-1).setDiffuseColor(new Vector3f(R_d, G_d, B_d));
                        break;
                    }
                    float R_d = readFloatFromBytes(mesh32, i);
                    float G_d = readFloatFromBytes(mesh32, i+OtherConstants.bytesInFloat);
                    float B_d = readFloatFromBytes(mesh32, i+(OtherConstants.bytesInFloat*2));
                    materials.get(materials.size()-1).setDiffuseColor(new Vector3f(R_d, G_d, B_d));
                    break;
                case Mesh32CMD.CollisionVertices:
                    for(int j=i; j<i+step; j+=(3*4)) //for every vertex offset
                    {
                        float[] vertex = new float[3];
                        for(int k=0; k<3; k++) //for every float
                        {
                            byte[] floatBytes = new byte[OtherConstants.bytesInFloat];
                            for(int l =0; l<OtherConstants.bytesInFloat; l++)
                                floatBytes[l] = mesh32[j+(k*4)+l]; //j+2 to skip type ushort
                            vertex[k] = ByteBuffer.wrap(floatBytes).order(ByteOrder.LITTLE_ENDIAN).getFloat()*scale;
                        }
                        Vector3f vectorVtx = new Vector3f(vertex[0], vertex[1], vertex[2]);
                        collisionVerts.add(vectorVtx);
                    }
                    break;
                case Mesh32CMD.CollisionPolygons:
                    for(int j=i; j<i+step; j+= 14) //for every polygon offset
                    {
                        short colType = (short)readShortFromBytes(mesh32, j);
                        int[] colIndices = new int[3];
                        for(int k=0; k<3; k++)
                            colIndices[k] = readIntFromBytes(mesh32, j+2+(k*4)); //+2 to skip colType
                        collisionTris.add(new CollisionTriangle(
                                colType,
                                collisionVerts.get(colIndices[0]),
                                collisionVerts.get(colIndices[1]),
                                collisionVerts.get(colIndices[2])
                                ));
                    }
                    break;
                case Mesh32CMD.TextureWrap:
                    materials.get(materials.size()-1).getLastTexture().setWrappingValue(mesh32[i]);
                    break;
                case Mesh32CMD.BoneCount:
                    allBones = new BoneNode[readShortFromBytes(mesh32, i)];
                    break;
                case Mesh32CMD.Bone:
                    if(currentBone == null)
                    {
                        currentBone = new BoneNode(boneIDIncrement, null);
                        allBones[0] = currentBone;
                        break;
                    }
                    boneIDIncrement++;
                    BoneNode subBone = new BoneNode(boneIDIncrement, currentBone);
                    allBones[subBone.ID] = subBone;
                    currentBone.subBones.add(subBone);
                    currentBone = subBone;
                    break;
                case Mesh32CMD.PopBone:
                    currentBone = currentBone.parent;
                    break;
                case Mesh32CMD.BoneMtx: //Set the raw bone matrix values
                    for(int j = 0; j < currentBone.transformMtx.length; j++)
                    {
                        currentBone.transformMtx[j] =
                                readFloatFromBytes(
                                        mesh32,
                                        i+(OtherConstants.bytesInFloat*j));
                        if(j < 15 && j >11)
                            currentBone.transformMtx[j] *= scale; //Raw coords in matrix affected by scale
                    }
                    currentBone.inverseOrigin = Arrays.copyOf(currentBone.transformMtx, 16);
                    break;
                case Mesh32CMD.BoneName:
                    currentBone.name = "";
                    for(int j=0; j<step; j++)
                        currentBone.name += (char)mesh32[i+j];
                    break;
                case Mesh32CMD.END: //END Conversion
                    float[] vertArray =
                            ArrayUtils.toPrimitive(vertexData.toArray(new Float[0]), 0.0F);
                    int[] indexArray = ArrayUtils.toPrimitive(indices.toArray(new Integer[0]), 0);
                    Material[] matArray = materials.toArray(new Material[0]);
                    int[] offsetArray = ArrayUtils.toPrimitive(materialIndexOffsets.toArray(new Integer[0]), 0);
                    Model newModel = new Model(vertArray, matArray, collisionTris, 2);
                    Mesh newMesh;
                    if(allBones != null && allBones.length > 0)
                    {
                        allBones[0].stackOrigins();
                        allBones[0].invertAllOrigins();
                        ArrayList<BoneNode> boneList = new ArrayList<>();
                        boneList.add(allBones[0]);
                        newMesh = new Mesh(newModel, indexArray, offsetArray, 0, boneList, allBones);
                        allBones = null; //Reset bones
                    }
                    else newMesh = new Mesh(newModel, indexArray, offsetArray, 0);
                    newModel.addMesh(newMesh);
                    return newModel;
            }
        }
        return null;
    }

    private static int getStep(byte command, int value)
    {
        if(command >= 3)//default step: byte offset
            return value;
        else if(command == 0) //Vertex count to byte offset
            value *= vertexAttribCount * OtherConstants.bytesInFloat;
        else if(command == 1) //Polygon count to byte offset
            value *= verticesPerPolygon * OtherConstants.bytesInUInt32;
        else if(command == 2)
            value = (value+1)*bytesPerPixel;
        return value;
    }

    private static int readShortFromBytes(byte[] bytes, int offset)
    {
        return (bytes[offset] << 8)
                | (bytes[offset+1] & 0xFF);
    }

    private static int readIntFromBytes(byte[] bytes, int offset)
    {
        return (bytes[offset] << 24)
                | (bytes[offset+1] << 16)
                | (bytes[offset+2] << 8)
                | (bytes[offset+3] & 0xFF);
    }
    private static float readFloatFromBytes(byte[] bytes, int offset)
    {
        byte[] floatBytes = new byte[]{bytes[offset], bytes[offset+1], bytes[offset+2], bytes[offset+3]};
        return ByteBuffer.wrap(floatBytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }
}
