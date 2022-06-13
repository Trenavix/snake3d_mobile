package graphics;

import android.opengl.GLES30;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;

import graphics.generators.TextureGenerators;

public class Material
{
    int collisionType; //Possibly a placeholder until CollisionMesh class
    String name;
    ArrayList<Texture> textures;
    Vector3f ambientColor = new Vector3f(0.5f,0.5f, 0.5f);
    Vector3f diffuseColor = new Vector3f(1.0f,1.0f, 1.0f);
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
        Shader.ambientColor = this.ambientColor;
        Shader.diffuseColor = this.diffuseColor;
        Shader.specularIntensity = this.specularIntensity;
        Shader.materialShininess = this.shininess;
        if(sphereMap) mainTexture.setSphereMapping();
        else mainTexture.setStandardMapping();
        GLES30.glUniform1f(Shader.GL_texScrollSLocation, mainTexture.scrollFactors.x);
        GLES30.glUniform1f(Shader.GL_texScrollTLocation, mainTexture.scrollFactors.y);
    }
    public void setAmbientColor(Vector3f newColor) { ambientColor = newColor; }
    public void setDiffuseColor(Vector3f newColor) { diffuseColor = newColor; }
    public void setSpecularIntensity(float newIntensity) {specularIntensity = newIntensity; }
    public void setShininess(float newShine) {shininess = newShine; }
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

