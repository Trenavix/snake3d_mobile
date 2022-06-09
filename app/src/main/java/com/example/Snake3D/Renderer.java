package com.example.Snake3D;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.lang.reflect.InvocationTargetException;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import core.AssetLoader;
import core.objects.PlayerObject;
import core.Scene;
import graphics.Camera;
import graphics.Shader;
import graphics.Utilities;

import org.joml.Vector2f;
import org.joml.Vector3f;

import static functions.Buffers.*;
import static functions.OtherConstants.*;
public class Renderer implements GLSurfaceView.Renderer
{
    static long frameTime = System.nanoTime();
    static float joyStickAngle = 0.f;
    static float joyStickMag = 0.f;
    public static double frameTimeRatio;
    static Scene currentScene;
    //set up global matrices
    float[] worldMatrix = new float[16];
    float[] viewMatrix = new float[16];
    float[] projMatrix = new float[16];
    int GL_worldMatrixLocation; //GL Matrix locations
    int GL_viewMatrixLocation;
    int GL_projMatrixLocation;
    static int GL_texUniLocation;
    static int GL_alphaTestUniLocation;
    public static Camera cam = new Camera(new Vector3f(0,0,0), new Vector2f(4.725f, 0), 0.005f, 3.f);//start looking backward (-1.5*pi)

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        Shader.makeprogram();
        GLES30.glEnableVertexAttribArray(Shader.positionhandle);
        GLES30.glEnableVertexAttribArray(Shader.vertUVhandle);
        GLES30.glEnableVertexAttribArray(Shader.vertcolorhandle);
        int program = Shader.program;
        GL_worldMatrixLocation = GLES30.glGetUniformLocation(program, "mWorld");
        GL_viewMatrixLocation = GLES30.glGetUniformLocation(program, "mView");
        GL_projMatrixLocation = GLES30.glGetUniformLocation(program, "mProj");
        GL_texUniLocation = GLES30.glGetUniformLocation(program, "u_Texture");
        GL_alphaTestUniLocation = GLES30.glGetUniformLocation(program, "alpha_Threshold");
        GLES30.glEnable(GLES30.GL_CULL_FACE);
        genBuffers();
        try {
            prepareScene();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void genBuffers()
    {
        int[] bufferIDs = new int[2];
        IntBuffer vboBufferIDs = intArrayToBuffer(bufferIDs);
        GLES30.glGenBuffers(2, vboBufferIDs);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboBufferIDs.get(0));
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, vboBufferIDs.get(1));
    }

    public void prepareScene() throws ClassNotFoundException //before we begin per-frame, setup the scene and buffers etc
    {
        GLES30.glEnable(GLES30.GL_TEXTURE_2D);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        currentScene = new Scene(AssetLoader.readFileAsText("scenes/scene0.scene"));
        GLES30.glDepthMask(true);
        GLES30.glDepthFunc( GLES30.GL_LEQUAL );
        GLES30.glEnable(GLES30.GL_BLEND);
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);
        GLES30.glLineWidth(3.5f); //for any wireframe models
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        //Worldview initialisation
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glViewport(0,0,width,height);
        Matrix.setIdentityM(worldMatrix, 0);
        float aspectRatio = (float) width / height;
        Utilities.perspectiveFrustrum(projMatrix, 45, aspectRatio, 0.1f, 1000.0f);
        GLES30.glUniformMatrix4fv(GL_worldMatrixLocation, 1, false, floatArrayToBuffer(worldMatrix, true));
        GLES30.glUniformMatrix4fv(GL_projMatrixLocation, 1, false, floatArrayToBuffer(projMatrix, true));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void onDrawFrame(GL10 gl)
    {
        long newTime = System.nanoTime();
        long deltaTime = newTime - frameTime;
        frameTimeRatio = deltaTime/ nanoSecondsIn60FPS;
        frameTime = newTime;
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        if(currentScene.getPlayer() != null) cam.followPos(currentScene.getPlayer().position, viewMatrix); //If player exists, 3rd person cam
        else //otherwise 1st person
        {
            cam.lookAt = Utilities.orientationToDirectionVector(cam.rotation);
            cam.lookAt.add(cam.position);
            Matrix.setLookAtM(viewMatrix, 0,
                    cam.position.x, cam.position.y, cam.position.z,
                    cam.lookAt.x, cam.lookAt.y, cam.lookAt.z,
                    0.0f, 1.0f, 0.0f);
        }
        GLES30.glUniformMatrix4fv(GL_viewMatrixLocation, 1, false, floatArrayToBuffer(viewMatrix, true));
        Matrix.setIdentityM(worldMatrix, 0);
        GLES30.glUniformMatrix4fv(GL_worldMatrixLocation, 1, false, floatArrayToBuffer(worldMatrix, true));
        GLES30.glUniform1i(GL_texUniLocation, 0);
        try {
            currentScene.drawScene(worldMatrix, GL_worldMatrixLocation, GL_alphaTestUniLocation, cam.position);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        move();
    }

    private static void move()
    {
        PlayerObject currentPlayer = null;
        if(currentScene != null) currentPlayer = currentScene.getPlayer();
        if(currentPlayer != null)
        {
            currentPlayer.moveForward(joyStickAngle, joyStickMag*(float)frameTimeRatio, cam.position, currentScene);
            //currentPlayer.moveForward(90.f, currentPlayer.getSpeed(), cam.position, cam.rotation, currentScene);
            return;
        }
        /* //First person camera usage below, for debugging?
        //Do not ask how this works because I cannot answer
        joyStickAngle *= OtherConstants.DEG2RAD; //Convert deg to rads
        Vector3f forward = Utilities.orientationToDirectionVector(cam.rotation);
        float dx = (float)Math.cos(joyStickAngle); //x value of analog stick
        float dy = (float)Math.sin(joyStickAngle); //y value of analog stick
        Vector3f right = new Vector3f(-forward.z, 0, forward.x); //lol idk why it works
        Vector3f offset = new Vector3f(0.f, 0f, 0.f);
        offset = offset.add(right.mul(dx));
        offset = offset.add(forward.mul(dy));
        offset = offset.normalize();
        offset = offset.mul(joyStickMag);
        cam.position.add(offset);*/
    }
}


