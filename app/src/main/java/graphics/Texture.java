package graphics;

import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.os.Build;

import androidx.annotation.RequiresApi;

import functions.Buffers;

public class Texture
{
    final static int bytesInPixel = 4; //RGBA32
    private int width;
    private int height;
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
    public static int loadTextureIntoGL(Texture tex, int[] textureIDs, int ID)
    {
        GLES30.glGenTextures(1, textureIDs, ID);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIDs[ID]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexImage2D
                (
                        GLES30.GL_TEXTURE_2D,
                        0,
                        GLES30.GL_RGBA,
                        tex.getWidth(), tex.getHeight(),
                        0,
                        GLES30.GL_RGBA,
                        GLES30.GL_UNSIGNED_BYTE,
                        Buffers.byteArrayToBuffer(tex.getRawData())
                );
        //if (textures[0] == 0) throw new RuntimeException("Error loading texture.");
        return textureIDs[ID];
    }
}
