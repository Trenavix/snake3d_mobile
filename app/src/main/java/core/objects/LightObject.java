package core.objects;

import android.opengl.GLES30;

import org.joml.Vector3f;

import core.Renderer;
import graphics.Shader;

public class LightObject extends GameObject
{
    public float linear;
    public float exponent;
    public int lightIndex;
    public float constant;
    Vector3f ambColor = new Vector3f(0.5f,0.5f,0.5f);
    Vector3f difColor = new Vector3f(1.0f,1.0f,1.0f);
    public LightObject(Vector3f position, Vector3f rotation, float linear, float exponent, float constant, int lightIndex, int sceneIndex)
    {
        super(null, position, rotation, linear, exponent, sceneIndex);
        this.linear = linear;
        this.exponent = exponent;
        this.lightIndex = lightIndex;
        this.constant = constant;
        this.status = Status.STARTUP;
    }
    public void setAmbientColor(Vector3f newAmbient)
    {
        ambColor = newAmbient;
    }
    public void setDiffuseColor(Vector3f newDiffuse)
    {
        difColor = newDiffuse;
    }

    public void drawObject()
    {
        if
            (this.status == Status.DEAD ||
            Shader.currentLightCount >= Shader.MAX_POINT_LIGHTS)
                return;
        Shader.currentLightCount++;
        this.lightIndex =Shader.currentLightCount-1;
        GLES30.glUniform1f(Shader.GL_PtLightAttenConstUniLocations[lightIndex], this.constant);
        GLES30.glUniform3f(Shader.GL_PtLightAmbientUniLocations[lightIndex], ambColor.x, ambColor.y, ambColor.z);
        GLES30.glUniform3f(Shader.GL_PtLightDiffuseUniLocations[lightIndex], difColor.x, difColor.y, difColor.z);
        GLES30.glUniform3f(Shader.GL_PtLightPosUniLocations[lightIndex], this.position.x, this.position.y, this.position.z);
        GLES30.glUniform1f(Shader.GL_PtLightAttenLinearUniLocations[lightIndex], this.linear);
        GLES30.glUniform1f(Shader.GL_PtLightAttenExpUniLocations[lightIndex], this.exponent);
    }

    public void delete()
    {
        this.status = Status.DEAD;
        GLES30.glUniform1f(Shader.GL_PtLightAttenExpUniLocations[lightIndex], 1000.0f);
    }
}
