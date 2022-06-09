package graphics;

import android.opengl.GLES30;
import android.util.Log;

public class Shader
{
    private final static String vertcode =
            "attribute vec3 vertPos;"+
            "attribute vec2 vertUV;"+
            "attribute vec4 vertColor;"+
            "varying vec4 fragColor;"+
            "varying vec2 fragUV;"+
            "uniform mat4 mWorld;"+
            "uniform mat4 mView;"+
            "uniform mat4 mProj;"+
            "void main()"+
            "{"+
            "fragColor = vertColor;"+
            "fragUV = vertUV;"+
            "gl_Position = mProj * mView * mWorld * vec4(vertPos, 1.0);"+
            "}";
    private final static String fragcode =
            "precision mediump float;"+
            "uniform float alpha_Threshold;"+
            "uniform sampler2D u_Texture;"+
            "varying vec4 fragColor;"+
            "varying vec2 fragUV;"+
            "void main()"+
            "{"+
            "gl_FragColor = fragColor * texture2D(u_Texture, fragUV);"+
            "if(gl_FragColor.a <= alpha_Threshold)"+
            "discard;"+ //AlphaTest
            "}";

    public static int program;

    public static int positionhandle;
    public static int vertcolorhandle;
    public static int vertUVhandle;

    public static void makeprogram()
    {
        int vertexshader = loadshader(GLES30.GL_VERTEX_SHADER, vertcode);
        int fragmentshader = loadshader(GLES30.GL_FRAGMENT_SHADER, fragcode);

        program = GLES30.glCreateProgram();
        GLES30.glAttachShader(program, vertexshader);
        GLES30.glAttachShader(program, fragmentshader);
        GLES30.glLinkProgram(program);

        positionhandle = GLES30.glGetAttribLocation(program, "vertPos");
        vertUVhandle = GLES30.glGetAttribLocation(program, "vertUV");
        vertcolorhandle = GLES30.glGetAttribLocation(program, "vertColor");

        GLES30.glUseProgram(program);
    }

    private static int loadshader(int type, String shadertext)
    {
        int shader = GLES30.glCreateShader(type);

        GLES30.glShaderSource(shader, shadertext);
        GLES30.glCompileShader(shader);
        final int[] compileStatus = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0);
        Log.e("Shaders","Shader Compilation Status: " + GLES30.glGetShaderInfoLog(shader)); //For debugging compilation errors
        return shader;
    }
}
