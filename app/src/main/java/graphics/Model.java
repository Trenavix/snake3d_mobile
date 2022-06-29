package graphics;

import java.util.ArrayList;
import java.util.Arrays;

import core.collision.CollisionMesh;
import core.collision.CollisionTriangle;

public class Model
{
    private final float[] vertices;
    public Material[] materials;
    private final int[] textureIDs;
    public ArrayList<Mesh> meshes;
    public CollisionMesh collision;
    public Model(float[] vertices, Material[] materials, ArrayList<CollisionTriangle> colTris, int octreeLevel)
    {
        this.vertices = vertices;
        this.materials = materials;
        this.textureIDs = new int[materials.length];
        this.meshes = new ArrayList<>();
        if(colTris != null && colTris.size() >1)
        {
            this.collision = new CollisionMesh(colTris, octreeLevel);
        }
    }
    public Model(float[] vertices, Material[] materials, ArrayList<BoneNode> bones, BoneNode[] bonesListed)
    {
        this.vertices = vertices;
        this.materials = materials;
        this.textureIDs = new int[materials.length];
        this.meshes = new ArrayList<>();
    }
    public Model(float[] vertices, Material[] materials)
    {
        this.vertices = vertices;
        this.materials = materials;
        this.textureIDs = new int[materials.length];
        this.meshes = new ArrayList<>();
    }
    public Model(float[] vertices, Material[] materials, Mesh mesh)
    {
        this.vertices = vertices;
        this.materials = materials;
        this.textureIDs = new int[materials.length];
        this.meshes = new ArrayList<>();
        this.meshes.add(mesh);
    }
    public Model(float[] vertices, Material[] materials, ArrayList<Mesh> meshes)
    {
        this.vertices = vertices;
        this.materials = materials;
        this.textureIDs = new int[materials.length];
        this.meshes = meshes;
    }

    public void addMesh(Mesh newMesh)
    {
        this.meshes.add(newMesh);
    }
    public void drawModel()
    {
        for(Mesh mesh : meshes)
            mesh.drawMesh();
    }

    public void loadTexturesIntoGL()
    {
        for(int i=0; i<textureIDs.length; i++)
            textureIDs[i] = materials[i].getTextures().get(0).loadMaterialIntoGL(textureIDs, i);
    }

    public Material[] getMaterials()
    {
        return materials;
    }
    public int getTextureID(int index) { return textureIDs[index]; }
    public float[] getVertices()
    {
        return Arrays.copyOf(vertices, vertices.length);
    }
    public float[] getVerticesDirect() {return vertices;}
    public Model cloneModel()
    {
        ArrayList<Mesh> newMeshes = new ArrayList<>();
        for(Mesh oldMesh : meshes)
            newMeshes.add(oldMesh.cloneMesh());
        return new Model(this.vertices, this.materials, newMeshes);
    }
}

