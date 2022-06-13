package core;

import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public static String readFileAsSingleString(String path)
    {
        String[] file = readFileAsText(path);
        String singleString = "";
        for(String line: file)
            singleString += line;
        return singleString;
    }

    public static byte[] readFileAsByteArray(String path)
    {
        try
        {
            InputStream is = assets.open(path);
            int size = is.available();
            byte[] array = new byte[size];
            is.read(array);
            return array;
        }
        catch (IOException e) { e.printStackTrace(); }
        return null;
    }

    public static Texture loadTextureFromPath(String path)
    {
        try { return new Texture(BitmapFactory.decodeStream(assets.open(path))); }
        catch (IOException e) { e.printStackTrace(); }
        return null;
    }
    public static Material loadMaterialFromPath(String path, String name, int collisionType)
    {
        ArrayList<Texture> texture = new ArrayList<Texture>();
        try { texture.add(new Texture(BitmapFactory.decodeStream(assets.open(path)))); }
        catch (IOException e) { e.printStackTrace(); return null; }
        return new Material(texture, name, collisionType);
    }

    public static void setAssets(AssetManager newAssets)
    {
        assets = newAssets;
    }
}
