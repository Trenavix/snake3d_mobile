package graphics;

import static functions.Buffers.floatArrayToBuffer;
import static functions.OtherConstants.bytesInFloat;

import android.opengl.GLES30;
import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.Arrays;

import org.joml.Vector3f;
import org.joml.Vector4f;

import core.Renderer;
import core.collision.CollisionMesh;
import core.collision.CollisionTriangle;
import functions.OtherConstants;

public class Mesh
{
    public int polygonType; //See OtherConstants for types (ie TRIANGLE)
    private final float[] vertices;
    private final int[] masterIndices;
    private final int[] masterIndexOffsets;
    private final int[] indicesPerMaterial;
    private  Material[] materials;
    private final int[] textureIDs;
    public CollisionMesh collision;
    private Vector3f translation = new Vector3f(0.f, 0.f, 0.f);
    private Vector3f scale = new Vector3f(1.f, 1.f, 1.f);
    public Vector3f rotation = new Vector3f(0.f, 0.f, 0.f);
    private ArrayList<Mesh> subMeshes = new ArrayList<Mesh>();
    private final float interactionRadius; //For future use in bones (i.e. hit-boxes)
    public Vector4f envColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);//General purpose color
    public boolean billboard = false;
    public boolean depthBufferWritingEnabled = true;
    public boolean drawOnTopOfAllGeometry = false;
    float[] transformMtx = new float[16];

    public Mesh(float[] vertices, int[] indices, Material[] materials, int[] materialIndexOffsets, int polygonType, ArrayList<CollisionTriangle> colTris, int octreeLevel)
    {
        this.vertices = vertices;
        this.masterIndices = indices;
        this.materials = materials;
        this.polygonType = polygonType;
        this.masterIndexOffsets = materialIndexOffsets;
        this.interactionRadius = 0.0f;
        this.textureIDs = new int[materials.length];
        if(colTris != null && colTris.size() >1)
        {
            this.collision = new CollisionMesh(colTris, octreeLevel);
        }
        this.indicesPerMaterial = new int[masterIndexOffsets.length];
        for(int i=0; i<indicesPerMaterial.length; i++)
        {
            if(i+1 >= indicesPerMaterial.length)
                indicesPerMaterial[i] = (getMasterIndices().length - masterIndexOffsets[i]);
            else indicesPerMaterial[i] = (masterIndexOffsets[i+1] - masterIndexOffsets[i]);
        }
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
        this.collision = null;
        this.indicesPerMaterial = new int[masterIndexOffsets.length];
        for(int i=0; i<indicesPerMaterial.length; i++)
        {
            if(i+1 >= indicesPerMaterial.length)
                indicesPerMaterial[i] = (getMasterIndices().length - masterIndexOffsets[i]);
            else indicesPerMaterial[i] = (masterIndexOffsets[i+1] - masterIndexOffsets[i]);
        }
    }

    private Mesh(float[] vertices, int[] indices, Material[] materials, int[] materialIndexOffsets, int polygonType, int[] textureIDs, int[] indicesPerMaterial)
    {
        this.vertices = vertices;
        this.masterIndices = indices;
        this.materials = materials;
        this.polygonType = polygonType;
        this.masterIndexOffsets = materialIndexOffsets;
        this.interactionRadius = 0.0f;
        this.textureIDs = textureIDs;
        this.indicesPerMaterial = indicesPerMaterial;
    }

    public void translate(Vector3f translateVector)
    {
        translation = translation.add(translateVector);
    }
    public void setTranslation(Vector3f newTranslation)
    {
        translation = newTranslation;
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
    public void setScale(Vector3f scaleVector) { scale = scaleVector; }
    public float[] getVertices()
    {
        return Arrays.copyOf(vertices, vertices.length);
    }
    public float[] getVerticesDirect() {return vertices;}
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

    public void setMeshMatrix()
    {
        //First, push the provisional matrix to world matrix (parent node/mesh matrix)
        GLES30.glUniformMatrix4fv(Shader.GL_worldMatrixLocation, 1, false, floatArrayToBuffer(Renderer.worldMatrix, true));
        Matrix.setIdentityM(transformMtx, 0);
        if(billboard)
        {
            //TODO: Make this a provisional matrix push when weights are implemented
            GLES30.glUniform1i(Shader.GL_billboardUniLocation, GLES30.GL_TRUE);
            GLES30.glUniform2f(Shader.GL_billboardScaleUniLocation, scale.x, scale.y);
            GLES30.glUniform3f(Shader.GL_billboardPosUniLocation, translation.x, translation.y, translation.z);
            GLES30.glUniform1f(Shader.GL_billboardRotationUniLocation, this.rotation.x);
            GLES30.glUniformMatrix4fv(Shader.GL_worldMatrixLocation, 1, false, floatArrayToBuffer(Renderer.worldMatrix, true));
            return;
        }
        GLES30.glUniform1i(Shader.GL_billboardUniLocation, GLES30.GL_FALSE);
        Matrix.translateM(transformMtx, 0, translation.x, translation.y, translation.z);
        Utilities.rotateMatrix3Axes(transformMtx, rotation);
        Matrix.scaleM(transformMtx, 0, scale.x, scale.y, scale.z);
        //Now pass the transformMtx to GL Shader:
        GLES30.glUniformMatrix4fv(Shader.GL_weightMatrixLocation, 1, false, floatArrayToBuffer(transformMtx, true));
    }

    public void setEnvColor(Vector4f newColor) { this.envColor = newColor; }

    public void drawMesh()
    {
        setMeshMatrix();
        if(!depthBufferWritingEnabled) GLES30.glDepthMask(false);
        else GLES30.glDepthMask(true);
        if(drawOnTopOfAllGeometry) GLES30.glDepthFunc(GLES30.GL_ALWAYS);
        else GLES30.glDepthFunc(GLES30.GL_LESS);
        GLES30.glUniform4f(Shader.GL_envColorUniLocation,
                this.envColor.x, this.envColor.y, this.envColor.z, this.envColor.w);
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
                sub.drawMesh();
    }
    public Mesh cloneMesh()
    {
        return new Mesh(
                this.getVertices(),
                this.masterIndices,
                this.materials,
                Arrays.copyOf(this.masterIndexOffsets, this.masterIndexOffsets.length),
                polygonType,
                this.textureIDs,
                this.indicesPerMaterial);
    }

    private void provisionalMtxPush()
    {

    }
}