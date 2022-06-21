package graphics;

import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.joml.Vector2f;
import org.w3c.dom.Text;

import functions.Buffers;

public class Texture
{
    final static int bytesInPixel = 4; //RGBA32
    TextureWrapMode wrapMode;
    private int ID;
    private int width;
    private int height;
    public Vector2f scrollFactors = new Vector2f();
    private byte[] rawData;
    public Texture(Bitmap bmp) //genTex via bitmap
    {
        this.width = bmp.getWidth();
        this.height = bmp.getHeight();
        byte[] texDataBytes = new byte[width*height*bytesInPixel];
        for(int i=0; i<height; i++)
        {
            for(int j=0; j<width; j++)
            {
                int idx =(i*width*bytesInPixel)+(j*bytesInPixel);
                int px = bmp.getPixel(j, i);
                texDataBytes[idx] = (byte)((px >> 16) & 0xFF); //R
                texDataBytes[idx+1] = (byte)((px >> 8) & 0xFF); //G
                texDataBytes[idx+2] = (byte)(px & 0xFF); //B
                texDataBytes[idx+3] = (byte)(px >> 24); //A
            }
        }
        this.rawData = texDataBytes;
        this.wrapMode = new TextureWrapMode((byte)0);
    }
    public Texture(int width, int height, byte[] data, byte wrapMode)
    {
        this.width = width;
        this.height = height;
        this.rawData = data;
        this.wrapMode = new TextureWrapMode(wrapMode);
    }
    public Texture(int width, int height, byte[] data)
    {
        this.width = width;
        this.height = height;
        this.rawData = data;
        this.wrapMode = new TextureWrapMode((byte)0);
    }
    public int getWidth()
    {
        return width;
    }
    public int getHeight()
    {
        return height;
    }
    public byte[] getRawData()
    {
        return rawData;
    }
    public void setSphereMapping()
    {
        GLES30.glUniform1i(Shader.GL_sphereMappingUniLocation, GLES30.GL_TRUE);
        GLES30.glTexParameteri(ID, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(ID, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(ID, GLES30.GL_TEXTURE_WRAP_R, GLES30.GL_CLAMP_TO_EDGE);
    }
    public void setStandardMapping()
    {
        GLES30.glUniform1i(Shader.GL_sphereMappingUniLocation, GLES30.GL_FALSE);
        setWrappingInGL();
    }
    public void setWrappingInGL()
    {
        //S Mode
        if (this.wrapMode.clampS)
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        else if (this.wrapMode.mirrorS)
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_MIRRORED_REPEAT);
        else GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
        //T Mode
        if (wrapMode.clampT)
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        else if (wrapMode.mirrorT)
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_MIRRORED_REPEAT);
        else GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
        //R Mode
        if (wrapMode.clampR)
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_R, GLES30.GL_CLAMP_TO_EDGE);
        else if (wrapMode.mirrorR)
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_R, GLES30.GL_MIRRORED_REPEAT);
        else GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_R, GLES30.GL_REPEAT);
    }
    public void setWrappingValue(byte wrapMode)
    {
        this.wrapMode = new TextureWrapMode(wrapMode);
    }
    public int loadMaterialIntoGL(int[] textureIDs, int ID)
    {
        GLES30.glGenTextures(1, textureIDs, ID);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIDs[ID]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        byte[] white = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
        GLES30.glTexImage2D
                (
                        GLES30.GL_TEXTURE_2D,
                        0,
                        GLES30.GL_RGBA,
                        this.getWidth(), this.getHeight(),
                        0,
                        GLES30.GL_RGBA,
                        GLES30.GL_UNSIGNED_BYTE,
                        Buffers.byteArrayToBuffer(this.getRawData())
                );
        this.ID = textureIDs[ID];
        return textureIDs[ID];
    }
    public void setRawData(byte[] data)
    {
        rawData = data;
    }
}
