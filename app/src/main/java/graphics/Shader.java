package graphics;

import android.opengl.GLES30;
import android.util.Log;

import org.joml.Vector2f;
import org.joml.Vector3f;

import core.AssetLoader;

public class Shader
{
    private final static String vertCode =
            AssetLoader.readFileAsSingleString("shaders/shader.vert");
    private final static String fragCode =
            AssetLoader.readFileAsSingleString("shaders/shader.frag");

    public static int program;
    public static int vertPosHandle;
    public static int vertUVHandle;
    public static int vertColorHandle;
    public static int vertNormalHandle;

    public static int GL_worldMatrixLocation; //GL Matrix locations
    public static int GL_viewMatrixLocation;
    public static int GL_projMatrixLocation;
    public static int GL_texUniLocation;
    public static int GL_specularIntensityUniLocation;
    public static int GL_ambientColorUniLocation;
    public static int GL_diffuseColorUniLocation;
    public static int GL_lightDirUniLocation;
    public static int GL_shininessUniLocation;
    public static int GL_texScrollSLocation;
    public static int GL_texScrollTLocation;
    public static int GL_timeLocation;
    public static int GL_alphaTestUniLocation;
    public static int GL_sphereMappingUniLocation;
    public static int GL_envColorUniLocation;
    public static int GL_billboardUniLocation;
    public static int GL_billboardPosUniLocation;
    public static int GL_billboardScaleUniLocation;
    public static int GL_billboardRotationUniLocation;
    public static Vector3f lightDirection = new Vector3f(-0.0f, -1.0f, -0.0f);
    public static Vector3f ambientColor = new Vector3f(0.5f, 0.5f, 0.5f);
    public static Vector3f diffuseColor = new Vector3f(1.0f, 1.0f, 1.0f);
    public static float specularIntensity = 0.5f;
    public static float materialShininess = 1.0f;
    public static double time = 0;


    public static void makeProgram()
    {
        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertCode);
        int fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragCode);

        program = GLES30.glCreateProgram();
        GLES30.glAttachShader(program, vertexShader);
        GLES30.glAttachShader(program, fragmentShader);
        GLES30.glLinkProgram(program);

        vertPosHandle = GLES30.glGetAttribLocation(program, "vertPos");
        vertUVHandle = GLES30.glGetAttribLocation(program, "vertUV");
        vertColorHandle = GLES30.glGetAttribLocation(program, "vertColor");
        vertNormalHandle = GLES30.glGetAttribLocation(program, "vertNormal");
        GLES30.glUseProgram(program);
    }

    public static void makeShaderProgram()
    {
        Shader.makeProgram();
        GLES30.glEnableVertexAttribArray(Shader.vertPosHandle);
        GLES30.glEnableVertexAttribArray(Shader.vertUVHandle);
        GLES30.glEnableVertexAttribArray(Shader.vertColorHandle);
        GLES30.glEnableVertexAttribArray(Shader.vertNormalHandle);
        int program = Shader.program;
        GL_worldMatrixLocation = GLES30.glGetUniformLocation(program, "mWorld");
        GL_viewMatrixLocation = GLES30.glGetUniformLocation(program, "mView");
        GL_projMatrixLocation = GLES30.glGetUniformLocation(program, "mProj");
        GL_texUniLocation = GLES30.glGetUniformLocation(program, "u_Texture");
        GL_ambientColorUniLocation = GLES30.glGetUniformLocation(program, "u_Light.ambientColor");
        GL_diffuseColorUniLocation = GLES30.glGetUniformLocation(program, "u_Light.diffuseColor");
        GL_lightDirUniLocation = GLES30.glGetUniformLocation(program, "u_Light.direction");
        GL_specularIntensityUniLocation = GLES30.glGetUniformLocation(program, "u_MatSpecularIntensity");
        GL_shininessUniLocation = GLES30.glGetUniformLocation(program, "u_Shininess");
        GL_texScrollSLocation = GLES30.glGetUniformLocation(program, "u_TexScrollS");
        GL_texScrollTLocation = GLES30.glGetUniformLocation(program, "u_TexScrollT");
        GL_timeLocation = GLES30.glGetUniformLocation(program, "u_Time");
        GL_alphaTestUniLocation = GLES30.glGetUniformLocation(program, "alpha_Threshold");
        GL_sphereMappingUniLocation = GLES30.glGetUniformLocation(program, "sphereMapping");
        GL_envColorUniLocation = GLES30.glGetUniformLocation(program, "u_EnvColor");
        GL_billboardUniLocation = GLES30.glGetUniformLocation(program, "u_Billboard");
        GL_billboardPosUniLocation = GLES30.glGetUniformLocation(program, "u_Billboard_Position");
        GL_billboardScaleUniLocation = GLES30.glGetUniformLocation(program, "u_Billboard_Scale");
        GL_billboardRotationUniLocation = GLES30.glGetUniformLocation(program, "u_Billboard_Rotation");
    }

    private static int loadShader(int type, String shaderText)
    {
        int shader = GLES30.glCreateShader(type);

        GLES30.glShaderSource(shader, shaderText);
        GLES30.glCompileShader(shader);
        final int[] compileStatus = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0);
        Log.e("shaders","Shader Compilation Status: " + GLES30.glGetShaderInfoLog(shader)); //For debugging compilation errors
        return shader;
    }
}
