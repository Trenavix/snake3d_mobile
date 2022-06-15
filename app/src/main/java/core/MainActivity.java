package core;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.example.Snake3D.R;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity
{
    private GLSurfaceView mGLSurfaceView;
    private float previousX;
    private float previousY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        AssetLoader.setAssets(this.getAssets());
        setContentView(R.layout.activity_main);
        mGLSurfaceView = (GLSurfaceView)findViewById(R.id.glSurfaceView1);;
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(new Renderer());

        mGLSurfaceView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent e) //touchme
            {
                float x = e.getX();
                float y = e.getY();
                switch (e.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        float dx = x - previousX;
                        float dy = y - previousY;
                        Renderer.currentScene.getCamera().updateOrientation(dx, dy);
                }
                previousX = x;
                previousY = y;
                return true;
            }
        });
        JoystickView joystick = findViewById(R.id.joystickView);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                Renderer.joyStickAngle = angle;
                Renderer.joyStickMag = strength/500.f;
            }
        }, 16); //Every 16ms input is checked (a little over 60/second)

    }

    public void onPause()
    {
        super.onPause();
        mGLSurfaceView.onPause();
    }
    public void onResume()
    {
        super.onResume();
        mGLSurfaceView.onResume();
    }
}