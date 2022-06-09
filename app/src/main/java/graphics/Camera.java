package graphics;
import android.opengl.Matrix;

import org.joml.Vector3f;
import org.joml.Vector2f;

import core.objects.Entity;

public class Camera extends Entity
{
    Vector2f orientation;
    public Vector3f lookAt;
    public Vector2f rotation;
    float sensitivity;
    float distancing;
    private final static float maxVertAngle = 1.56f;
    public Camera(Vector3f position, Vector2f orientation, float sensitivity, float distancing)
    {
        super(position);
        this.orientation = orientation;
        this.sensitivity = sensitivity;
        this.lookAt = new Vector3f(0,0,0);
        this.rotation = new Vector2f(0,0);
        this.distancing = distancing;
    }

    public void followPos(Vector3f pos, float[] viewMatrix)
    {
        lookAt = pos;
        position = Utilities.orientationToDirectionVector(rotation);
        position.mul(-1.0f*distancing);
        position.add(lookAt);
        Matrix.setLookAtM(viewMatrix, 0,
                position.x, position.y, position.z,
                lookAt.x, lookAt.y, lookAt.z,
                0.0f, 1.0f, 0.0f);
    }

    public void rotateCameraToPos(Vector3f pos)
    {

    }

    public void updateOrientation(float x, float y)
    {
        rotation.x += x*sensitivity;
        rotation.y -= y*sensitivity;
        if(rotation.y > maxVertAngle) rotation.y = maxVertAngle;
        else if (rotation.y < -maxVertAngle) rotation.y = -maxVertAngle;
    }
}
