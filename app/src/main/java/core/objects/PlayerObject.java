package core.objects;

import android.opengl.GLES30;
import android.opengl.Matrix;
import android.os.Build;

import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Arrays;

import core.Scene;
import core.collision.Collision;
import functions.OtherConstants;
import graphics.Mesh;
import graphics.Model;
import graphics.Shader;
import graphics.Utilities;
import core.Renderer;

import static functions.Buffers.floatArrayToBuffer;

import androidx.annotation.RequiresApi;

public class PlayerObject extends GameObject
{
    float fixedSpeed;
    int maxPathSize = 1;
    public PlayerObject(Model model, Vector3f position, Vector3f rotation, float scale, float speed, float radius, int sceneIndex)
    {
        super(model, position, rotation, scale, radius, sceneIndex);
        this.fixedSpeed = speed;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void moveForward(float angle, float magnitude, Vector3f cameraPosition, Scene currentScene) throws ClassNotFoundException {
        //Do not ask how this works because I cannot answer
        angle *= OtherConstants.DEG2RAD; //Convert deg to rads
        Vector3f forward = new Vector3f();
        position.sub(cameraPosition, forward);
        forward.normalize();
        Vector3f autoMove = (new Vector3f(forward).mul(new Vector3f(1,0,1)).normalize().mul(fixedSpeed));
        autoMove.mul((float)Renderer.frameTimeRatio);
        float dx = (float)Math.cos(angle); //x value of analog stick
        float dy = (float)Math.sin(angle); //y value of analog stick
        Vector3f right = new Vector3f(-forward.z, 0, forward.x); //lol idk why it works
        Vector3f offset = new Vector3f();
        offset.add(right.mul(dx));
        offset.add(new Vector3f(0, dy, 0));
        offset.normalize();
        Vector3f trajectory = new Vector3f(offset).mul(magnitude).add(position);
        if(magnitude <= 0.0f) trajectory = new Vector3f(position); //ignore everything if no analog input
        trajectory.add(autoMove);
        Vector3f direction = new Vector3f(trajectory).sub(position).normalize();
        angularVector = Utilities.vectorNormToAngularVector(direction, new Vector3f(0,0,1));
        Collision.collisionCheck(currentScene, this, trajectory);
    }

    public void setNewPosition(Vector3f trajectory)
    {
        Vector3f offset; int currentPathSize = subObjects.size();
        float radius = getInteractionRadius();
        if(currentPathSize >0)
        {
            offset = new Vector3f(trajectory).sub(subObjects.getLast().position);
            double length = Math.abs(offset.length());
            if(length < getInteractionRadius()) //Only store new path marker if last was > radius
            {
                position = trajectory; return;
            }
            int steps = (int)Math.round(length/radius);
            for(int i = 0; i <= steps; i++)
                addToPath(position.lerp(trajectory, (float)i/(float)steps, offset));
        }
        else addToPath(position); //always add path if current path is 0
        position = trajectory;
    }

    private void addToPath(Vector3f pos)
    {
        if(subObjects.size() > maxPathSize) subObjects.removeFirst();
        subObjects.add(new PlayerObject(getModelReference(), pos, rotation, scale, fixedSpeed, getInteractionRadius(), meshSceneIndex));
        subObjects.getLast().setAngularVector(angularVector);
    }

    public float getSpeed(){ return fixedSpeed; }
    public Vector4f getAngularVector() {return angularVector; }
    public void setPathSize(int newSize) {maxPathSize = newSize; }
    public int getPathSize() {return this.maxPathSize;}
    public void placeObjectInWorld()
    {
        Matrix.setIdentityM(Renderer.worldMatrix, 0);
        float objectScale = scale;
        Matrix.translateM(Renderer.worldMatrix, 0, position.x, position.y, position.z);
        Matrix.scaleM(Renderer.worldMatrix, 0, objectScale, objectScale, objectScale);
        Matrix.rotateM(Renderer.worldMatrix, 0, angularVector.w, angularVector.x, angularVector.y, angularVector.z);
        GLES30.glUniformMatrix4fv(
                Shader.GL_modelMatrixLocations[0],
                1,
                false,
                floatArrayToBuffer(Renderer.worldMatrix, true)
        );
    }
    public void drawObject()
    {
        float[] storeMatrix = Arrays.copyOf(Renderer.worldMatrix, Renderer.worldMatrix.length); //store copy
        drawObjectModel();
        for(GameObject subObject : getSubObjects()) //draw all subObjects
            subObject.drawObjectModel();
        Renderer.worldMatrix = storeMatrix; //restore
    }

    public void drawObjectModel()
    {
        placeObjectInWorld();
        getModelReference().drawModel();
    }
}
