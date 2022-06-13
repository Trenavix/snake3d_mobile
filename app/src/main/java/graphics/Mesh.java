package graphics;

import static functions.Buffers.floatArrayToBuffer;
import static functions.OtherConstants.bytesInFloat;

import android.opengl.GLES30;
import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.Arrays;

import org.joml.Vector3f;

import core.collision.CollisionTriangle;
import functions.OtherConstants;

public class Mesh
{
    public int polygonType; //See OtherConstants for types (ie TRIANGLE)
    private final float[] vertices;
    private final int[][] splitIndices; //2D array; Column: texture, Row: Indices
    private final int[] masterIndices;
    private final int[] masterIndexOffsets;
    private final int[] indicesPerMaterial;
    private  Material[] materials;
    private final int[] textureIDs;
    public ArrayList<CollisionTriangle> colTriangles = new ArrayList<CollisionTriangle>();
    private Vector3f translation = new Vector3f(0.f, 0.f, 0.f);
    private Vector3f scale = new Vector3f(1.f, 1.f, 1.f);
    private Vector3f rotation = new Vector3f(0.f, 0.f, 0.f);
    private ArrayList<Mesh> subMeshes = new ArrayList<Mesh>();
    private final float interactionRadius; //For future use in bones (i.e. hit-boxes)

    public Mesh(float[] vertices, int[][] indices, Material[] materials, int polygonType, int[][] collisionIndices, float interactionRadius)
    {
        this.vertices = vertices;
        this.splitIndices = indices;
        this.materials = materials;
        this.polygonType = polygonType;
        this.interactionRadius = interactionRadius;
        textureIDs = new int[materials.length];
        masterIndexOffsets = new int[splitIndices.length];
        int buf = 0;
        for(int i=0; i<splitIndices.length; i++)
        {
            int subLength = splitIndices[i].length;
            masterIndexOffsets[i] = buf;
            buf +=  subLength;//get entire length
        }
        masterIndices = new int[buf];
        this.indicesPerMaterial = new int[masterIndexOffsets.length];
        for(int i=0; i<indicesPerMaterial.length; i++)
        {
            if(i+1 >= indicesPerMaterial.length)
                indicesPerMaterial[i] = (getMasterIndices().length - masterIndexOffsets[i]);
            else indicesPerMaterial[i] = (masterIndexOffsets[i+1] - masterIndexOffsets[i]);
        }
        buf = 0;
        for(int i=0; i<splitIndices.length; i++) //combine 2d array axes into one
        {
            for(int j=0; j < splitIndices[i].length;j++)
            {
                masterIndices[buf] = splitIndices[i][j];
                buf++;
            }
            if(collisionIndices == null && materials[i].collisionType != 0) //skip type 0
                for(int j=0; j<splitIndices[i].length; j+=3)
                {
                    int[] colIndices = {splitIndices[i][j], splitIndices[i][j+1], splitIndices[i][j+2]};
                    CollisionTriangle newColTri = new CollisionTriangle(colIndices, materials[i].collisionType, vertices);
                    colTriangles.add(newColTri);
                }
        }
        if(collisionIndices != null)
            for(int i=0; i<collisionIndices.length; i++)
                for(int j=0;j<collisionIndices[i].length; j+=3) //3 per tri
                    colTriangles.add(
                        new CollisionTriangle(
                            collisionIndices[i][j],
                            collisionIndices[i][j+1],
                            collisionIndices[i][j+2],
                            i, vertices));
    }

    public Mesh(float[] vertices, int[] indices, Material[] materials, int[] materialIndexOffsets, int polygonType)
    {
        this.vertices = vertices;
        this.masterIndices = indices;
        this.materials = materials;
        this.polygonType = polygonType;
        this.masterIndexOffsets = materialIndexOffsets;
        this.interactionRadius = 0.0f;
        this.textureIDs = new int[materials.length];
        this.splitIndices = null; //die
        this.indicesPerMaterial = new int[masterIndexOffsets.length];
        for(int i=0; i<indicesPerMaterial.length; i++)
        {
            if(i+1 >= indicesPerMaterial.length)
                indicesPerMaterial[i] = (getMasterIndices().length - masterIndexOffsets[i]);
            else indicesPerMaterial[i] = (masterIndexOffsets[i+1] - masterIndexOffsets[i]);
        }
    }

    private void splitToMasterIndices()
    {

    }

    public void translate(Vector3f translateVector)
    {
        translation = translation.add(translateVector);
    }
    public void loadTexturesIntoGL()
    {
        for(int i=0; i<textureIDs.length; i++)
            textureIDs[i] = materials[i].getTextures().get(0).loadMaterialIntoGL(textureIDs, i);
    }
    public void addSubMesh(Mesh subMesh)
    {
        subMeshes.add(subMesh);
    }
    public void rotate(Vector3f rotateVector)
    {
        rotation = rotation.add(rotateVector);
    }
    public void scale(Vector3f scaleVector) { scale = scale.add(scaleVector); }
    public float[] getVertices()
    {
        return Arrays.copyOf(vertices, vertices.length);
    }
    public float[] getVerticesDirect() {return vertices;}
    public int[][] getSplitIndices()
    {
        return Arrays.copyOf(splitIndices, splitIndices.length);
    }
    public int[] getMasterIndices()
    {
        return Arrays.copyOf(masterIndices, masterIndices.length);
    }
    public int[] getMasterIndicesDirect() {return masterIndices; }
    public Material[] getMaterials()
    {
        return materials;
    }
    public int getTextureID(int index) { return textureIDs[index]; }
    public int getMasterIndexOffset(int index) { return masterIndexOffsets[index]; }
    public Mesh getSubMesh(int idx) {return subMeshes.get(idx);}
    public int getSubMeshSize() {return subMeshes.size();}
    public void removeFirstSubMesh() {if(subMeshes.size() > 0) subMeshes.remove(0);}
    public void deleteAllSubMeshes(){subMeshes = new ArrayList<Mesh>();}
    public Mesh getLastSubMesh() {return subMeshes.get(subMeshes.size()-1);}
    public void addOffsetToIndices(int offset)
    {
        for(int i=0; i<masterIndices.length; i++)
            masterIndices[i] += offset;
    }
    public void addToIndexOffsets(int offset)
    {
        for(int i=0; i<masterIndexOffsets.length; i++)
            masterIndexOffsets[i] += offset;
    }

    public void setMeshMatrix(float[] worldMatrix, int GL_worldMatrixLocation)
    {
        Matrix.translateM(worldMatrix, 0, translation.x, translation.y, translation.z);
        Matrix.scaleM(worldMatrix, 0, scale.x, scale.y, scale.z);
        Utilities.rotateMatrix3Axes(worldMatrix, rotation);
        GLES30.glUniformMatrix4fv(GL_worldMatrixLocation, 1, false, floatArrayToBuffer(worldMatrix, true));
    }
    public void drawMesh(float[] worldMatrix, int GL_worldMatrixLocation)
    {
        setMeshMatrix(worldMatrix, GL_worldMatrixLocation);
        for(int i = 0; i<materials.length; i++)
        {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, getTextureID(i));
            materials[i].setMaterialPropertiesInGL();
            if(polygonType == OtherConstants.polygonType_TRIANGLE)
                GLES30.glDrawElements(GLES30.GL_TRIANGLES, indicesPerMaterial[i], GLES30.GL_UNSIGNED_INT, getMasterIndexOffset(i)* bytesInFloat);
            else if(polygonType == OtherConstants.polygonType_LINE)
                GLES30.glDrawElements(GLES30.GL_LINES, indicesPerMaterial[i], GLES30.GL_UNSIGNED_INT, getMasterIndexOffset(i)* bytesInFloat);
        }
        if(subMeshes.size() > 0) //draw all subMeshes
            for(Mesh sub : subMeshes)
                sub.drawMesh(worldMatrix, GL_worldMatrixLocation);
    }
}