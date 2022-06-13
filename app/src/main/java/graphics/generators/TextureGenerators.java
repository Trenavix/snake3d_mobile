package graphics.generators;

import android.graphics.Bitmap;

import graphics.Texture;

public class TextureGenerators
{
    public static Texture singlePixel(int color)
    {
        Bitmap texImage = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        texImage.setPixel(0, 0, color);
        Texture[] texture = new Texture[1];
        return new Texture(texImage);
    }
}
