package functions.conversions;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import functions.OtherConstants;
import graphics.Material;
import graphics.Mesh;

import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector3f;

//Experimental and deprecated class!
//Please convert models to Mesh32 beforehand!
//This will dramatically increase scene startup times to convert the models

public class OBJ2Mesh
{
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Mesh convertOBJToMesh(String[] obj, Material[] textures, float scale, float interactionRadius, Vector3f defaultNormal)
    {
        ArrayList<float[]> originalVertices = new ArrayList<float[]>();
        ArrayList<float[]> originalUVs = new ArrayList<float[]>();
        ArrayList<float[]> originalColors = new ArrayList<float[]>();
        ArrayList<float[]> originalNormals = new ArrayList<float[]>();
        ArrayList<Float> newVerts = new ArrayList<Float>();
        Map<Integer, float[]> newVertsGrouped = new HashMap<Integer, float[]>();
        //Two dimensional array list for texture index (col) and tri indices (row)
        ArrayList<Integer>[] newIndices = new ArrayList[textures.length];
        for(int i=0; i<newIndices.length; i++) newIndices[i] = new ArrayList<Integer>();
        int currentIndex = 0;
        for(int i=0; i<obj.length; i++)
        {
            String[] line = obj[i].trim().split("\\s+"); // split it up by spaces
            switch(line[0])
            {
                case "g": //TODO: group?
                    break;
                case "f": //Index
                    for(int j=1; j<4; j++) //for each "word", or index
                    {
                        String[] idx = line[j].split("/"); //split at the /, OBJ format vtx/uv idx
                        float[] pos = originalVertices.get(Integer.parseInt(idx[0])-1); //1 indexed to 0 indexed
                        float[] uv = originalUVs.get(Integer.parseInt(idx[1])-1); //1 indexed to 0 indexed
                        float[] normal = new float[3];
                        float[] vtx = new float[OtherConstants.vertexElements];
                        if(idx.length > 2)
                            normal = originalNormals.get(Integer.parseInt(idx[2])-1);
                        else normal = new float[]{defaultNormal.x, defaultNormal.y, defaultNormal.z};
                        float[] color = new float[]{1f, 1f, 1f, 1f}; //1 indexed to 0 indexed
                        if(i < obj.length-1) //Make sure it is not the last line
                        {
                            String[] nextLine = obj[i+1].trim().split("\\s+"); // split it up by spaces
                            if(nextLine[0].equals("#fvcolorindex"))
                                color = originalColors.get(Integer.parseInt(nextLine[j])-1); //1 indexed to be 0 indexed
                        }
                        for(int k=0; k < 3; k++)
                            vtx[k] = pos[k];
                        for(int k=0; k < 2; k++)
                            vtx[k+OtherConstants.vertUVOffset] = uv[k];
                        for(int k=0; k < 4; k++)
                            vtx[k+OtherConstants.vertColorOffset] = color[k];
                        for(int k=0; k < 3; k++)
                            vtx[k+OtherConstants.vertNormalOffset] = normal[k]; //TODO: Normals
                        Integer vtxIdx;
                        if(newVertsGrouped.size() > 100000) vtxIdx = checkForExistingVertexParallel(newVertsGrouped, vtx);
                        else vtxIdx = checkForExistingVertex(newVerts, vtx);
                        if(vtxIdx == null)
                        {
                            for(float data: vtx) newVerts.add(data); //Add new vertex
                            vtxIdx = (newVerts.size()/OtherConstants.vertexElements)-1;
                            newVertsGrouped.put(vtxIdx, vtx);
                        }
                        newIndices[currentIndex].add(vtxIdx);
                    }
                    break;
                case "o": //TODO: object
                    break;
                case "usemtl": //Use Material
                    internalLoop:
                    {
                        for(int j=0; j<textures.length; j++)
                            if(textures[j].getName().equals(line[1]))
                                { currentIndex = j; break internalLoop; } //i++ and skip adding index below
                    }
                    break;
                case "v": //vertex
                    float[] vtx = new float[3];
                    for(int j = 0; j < 3; j++)
                        vtx[j] = Float.parseFloat(line[j+1])*scale; //+1 to skip the v
                    originalVertices.add(vtx);
                    break;
                case"#vcolor":
                    float[] color = new float[]{1.f,1.f,1.f,1.f};
                    int size = line.length-1; //remove 1st word, sometimes alpha is present, sometimes not
                    for(int j = 0; j < size; j++)
                        color[j] = Float.parseFloat(line[j+1])/255.f; //+1 to skip the v
                    originalColors.add(color);
                    break;
                case "vn":
                    float[] normal = new float[3];
                    for(int j = 0; j < 3; j++)
                        normal[j] = Float.parseFloat(line[j+1]); //+1 to skip the v
                    originalNormals.add(normal);
                    break;
                case "vt": //texCoords
                    float[] uv = new float[2];
                    uv[0] = Float.parseFloat(line[1]);
                    uv[1] = -Float.parseFloat(line[2]);
                    originalUVs.add(uv);
                    break;
            }
        }

        float[] newVertsAsFloat = ArrayUtils.toPrimitive(newVerts.toArray(new Float[0]), 0.0F);
        int[][] returnIndices = new int[newIndices.length][]; // Convert 2D arraylist to 2D array
        for(int i =0; i<newIndices.length; i++)
        {
            returnIndices[i] = ArrayUtils.toPrimitive(newIndices[i].toArray(new Integer[0]), 0);
        }
        //return new Mesh(newVertsAsFloat, returnIndices,textures, OtherConstants.polygonType_TRIANGLE, null, interactionRadius);
        return null; //Not updating support
    }

    public static String[] objGetTexturePaths(String[] mtl, String[] objTexNames)
    {
        String[] texFileNames = new String[objTexNames.length];
        for(int i=0; i<objTexNames.length; i++)
        {
            boolean materialFound = false;
            for (String s : mtl)
            {
                if(s.equals("newmtl " + objTexNames[i]))
                    materialFound = true;
                if(materialFound)
                {
                    String[] line = s.trim().split("\\s+");
                    if (line[0].equals("map_Kd"))
                    {
                        texFileNames[i] = line[1];
                        Log.e("TexNames", "Texture Found " + texFileNames[i]);
                        Log.e("TexNames", "Texture Count " + texFileNames.length);
                        break;
                    }
                }
            }
        }
        if (texFileNames[0] == null) Log.e("TexNames", "No Textures found");
        return texFileNames;
    }
    public static String[] objGetTextureNames(String[] obj)
    {
        ArrayList<String> objTexNames = new ArrayList<String>();
        for (String s : obj)
        {
            String[] line = s.trim().split("\\s+"); // split it up by spaces
            if (line[0].equals("usemtl"))
                if (!objTexNames.contains(line[1]))
                    objTexNames.add(line[1]);
        }
        String[] returnArray = new String[objTexNames.size()];
        objTexNames.toArray(returnArray);
        return returnArray;
    }

    private static Integer checkForExistingVertex(ArrayList<Float> vertices, float[] vertex)
    {
        mainLoop: for(int i=0; i< vertices.size(); i+=OtherConstants.vertexElements)
        {
            for(int j=0; j<OtherConstants.vertexElements; j++)
            {
                if(vertex[j] != vertices.get(i+j)) continue mainLoop; //If any value does not match, skip
            }
            return i/OtherConstants.vertexElements;
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static Integer checkForExistingVertexParallel(Map<Integer, float[]> groupedVerts, float[] vertex)
    {
        Optional<Map.Entry<Integer, float[]>> op = groupedVerts
                .entrySet()
                .parallelStream()
                .filter(vert -> Arrays.equals(vert.getValue(), vertex))
                .findAny();
        return op.map(Map.Entry::getKey).orElse(null);
    }
}
