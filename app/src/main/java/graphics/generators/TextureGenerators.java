package graphics.generators;

import android.graphics.Bitmap;

import java.util.ArrayList;

import graphics.Material;
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

    public static Material[] generateSingleMaterial(int color)
    {
        Material[] texList = new Material[1];
        ArrayList<Texture> texture = new ArrayList<>();
        texture.add(TextureGenerators.singlePixel(color));
        texList[0] = new Material(texture, "null",0);
        return texList;
    }

}
