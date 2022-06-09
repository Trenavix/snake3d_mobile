package functions;

import static functions.OtherConstants.bytesInFloat;
import static functions.OtherConstants.vertColorOffset;
import static functions.OtherConstants.vertUVOffset;
import static functions.OtherConstants.vertexElements;

import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import graphics.Shader;

public class Buffers {
    public static FloatBuffer floatArrayToBuffer(float[] array, boolean flip)
    {
        FloatBuffer floatbuff = ByteBuffer.allocateDirect(array.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        floatbuff.put(array).position();
        if(flip) {floatbuff.flip();}
        return floatbuff;
    }
    public static ShortBuffer shortArrayToBuffer(short[] array)
    {
        ShortBuffer shortbuff = ByteBuffer.allocateDirect(array.length*2).order(ByteOrder.nativeOrder()).asShortBuffer();
        shortbuff.put(array).position();
        shortbuff.flip();
        return shortbuff;
    }
    public static ByteBuffer byteArrayToBuffer(byte[] array)
    {
        ByteBuffer bytebuff = ByteBuffer.allocateDirect(array.length).order(ByteOrder.nativeOrder());
        bytebuff.put(array).position();
        bytebuff.flip();
        return bytebuff;
    }
    public static IntBuffer intArrayToBuffer(int[] array)
    {
        IntBuffer intbuff = ByteBuffer.allocateDirect(array.length*4).order(ByteOrder.nativeOrder()).asIntBuffer();
        intbuff.put(array).position();
        intbuff.flip();
        return intbuff;
    }
    public static void swapVertexBuffers(float[] vertices, int[] indices)
    {
        FloatBuffer vertBuffer = floatArrayToBuffer(vertices, true);
        IntBuffer indexData = intArrayToBuffer(indices);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertices.length*bytesInFloat, vertBuffer, GLES30.GL_STATIC_DRAW);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, indices.length*bytesInFloat, indexData, GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(Shader.positionhandle, 3, GLES30.GL_FLOAT, false, vertexElements*bytesInFloat, 0);
        GLES30.glVertexAttribPointer(Shader.vertUVhandle, 2, GLES30.GL_FLOAT, false, vertexElements*bytesInFloat, vertUVOffset *bytesInFloat);
        GLES30.glVertexAttribPointer(Shader.vertcolorhandle, 4, GLES30.GL_FLOAT, false, vertexElements*bytesInFloat, vertColorOffset*bytesInFloat);
    }
}
