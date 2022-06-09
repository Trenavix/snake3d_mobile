package graphics;

import android.graphics.Bitmap;

public class Material extends Texture
{
    int collisionType;
    String name;
    public Material(Bitmap bmp, String name, int collisionType)
    {
        super(bmp);
        this.collisionType = collisionType;
        this.name = name;
    }
    public String getName() {return name; }
}
