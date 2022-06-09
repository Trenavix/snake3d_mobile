package core;

import android.content.res.AssetManager;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import graphics.Material;
import graphics.Texture;

public class AssetLoader
{
    private static AssetManager assets;
    public static String[] readFileAsText(String path)
    {
        ArrayList<String> file = new ArrayList<String>();
        try
        {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(assets.open(path)));
            String line = bReader.readLine();
            while (line != null)
            {
                file.add(line);
                line = bReader.readLine();
            }
            bReader.close();
        }
        catch (IOException e) { e.printStackTrace(); }
        return file.toArray(new String[0]);
    }

    public static Texture loadTextureFromPath(String path)
    {
        try { return new Texture(BitmapFactory.decodeStream(assets.open(path))); }
        catch (IOException e) { e.printStackTrace(); }
        return null;
    }
    public static Material loadMaterialFromPath(String path, String name, int collisionType)
    {
        try { return new Material(BitmapFactory.decodeStream(assets.open(path)), name, collisionType); }
        catch (IOException e) { e.printStackTrace(); }
        return null;
    }
    public static void setAssets(AssetManager newAssets)
    {
        assets = newAssets;
    }
}
