package graphics;

import android.opengl.GLES30;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

import graphics.generators.TextureGenerators;

public class Material
{
    int collisionType; //Possibly a placeholder until CollisionMesh class
    String name;
    ArrayList<Texture> textures;
    Vector3f ambientColor = new Vector3f(0.5f,0.5f, 0.5f);
    Vector3f diffuseColor = new Vector3f(0.5f,0.5f, 0.5f);
    float specularIntensity = 0.0f;
    float shininess = 0.0f;
    private boolean sphereMap = false;
    private Vector2f scrollFactors = new Vector2f();
    public Material(ArrayList<Texture> textures, String name, int collisionType)
    {
        this.collisionType = collisionType;
        this.name = name;
        this.textures = textures;
    }
    public String getName() {return name; }
    public void setMaterialPropertiesInGL()
    {
        Texture mainTexture = textures.get(0);
        Shader.specularIntensity = this.specularIntensity;
        Shader.materialShininess = this.shininess;
        if(sphereMap) mainTexture.setSphereMapping();
        else mainTexture.setStandardMapping();
        GLES30.glUniform1f(Shader.GL_texScrollSLocation, mainTexture.scrollFactors.x);
        GLES30.glUniform1f(Shader.GL_texScrollTLocation, mainTexture.scrollFactors.y);
        GLES30.glUniform3f(Shader.GL_ambientColorUniLocation,
                this.ambientColor.x, this.ambientColor.y, this.ambientColor.z);
        GLES30.glUniform3f(Shader.GL_diffuseColorUniLocation,
                this.diffuseColor.x, this.diffuseColor.y, this.diffuseColor.z);
    }
    public void setAmbientColor(Vector3f newColor) { this.ambientColor = newColor; }
    public void setDiffuseColor(Vector3f newColor) { this.diffuseColor = newColor; }
    public void setSpecularIntensity(float newIntensity) {this.specularIntensity = newIntensity; }
    public void setShininess(float newShine) {this.shininess = newShine; }
    public void setTextureScroll(Vector2f scrollFactors) {this.scrollFactors = scrollFactors;}

    public ArrayList<Texture> getTextures() { return textures; }
    public void addTexture(Texture newTexture)
    {
        textures.add(newTexture);
    }
    public Texture getTexture(int idx)
    {
        return textures.get(idx);
    }
    public Texture getLastTexture()
    {
        if(textures.size() < 1) return null;
        return textures.get(textures.size()-1);
    }
    public void sphereMapping(boolean enabled)
    {
        this.sphereMap = enabled;
    }
}

