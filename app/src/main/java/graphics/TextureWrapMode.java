package graphics;

import android.opengl.GLES30;

public class TextureWrapMode
{
    public boolean clampS;
    public boolean clampT;
    public boolean clampR;
    public boolean mirrorS;
    public boolean mirrorT;
    public boolean mirrorR;
    public TextureWrapMode(byte wrapModeByte)
    {
        int wrapMode = (wrapModeByte & 0xFF);
        //first bit S clamp
        if ((wrapMode & 1) == 1) this.clampS = true;
        else this.clampS = false;
        //second bit T clamp
        if (((wrapMode >> 1) & 1) == 1) this.clampT = true;
        else this.clampT = false;
        //third bit R clamp
        if (((wrapMode >> 2) & 1) == 1) this.clampR = true;
        else this.clampR = false;
        //fourth bit S mirror
        if (((wrapMode >> 3) & 1) == 1) this.mirrorS = true;
        else this.mirrorS = false;
        //fifth bit T mirror
        if (((wrapMode >> 4) & 1) == 1) this.mirrorT = true;
        else this.mirrorT = false;
        //sixth bit R mirror
        if (((wrapMode >> 5) & 1) == 1) this.mirrorR = true;
        else this.mirrorR = false;
    }
}
