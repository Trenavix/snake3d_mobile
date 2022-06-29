package graphics;

import android.opengl.Matrix;

import java.util.ArrayList;

public class BoneNode
{
    public float[] inverseOrigin;
    public float[] transformMtx;
    public final int ID;
    public ArrayList<BoneNode> subBones;
    public BoneNode parent;
    public String name;
    public BoneNode(int ID, BoneNode parent)
    {
        this.ID = ID;
        this.subBones = new ArrayList<>();
        this.parent = parent;
        this.transformMtx = new float[16];
        this.inverseOrigin = new float[16];
        Matrix.setIdentityM(transformMtx, 0);
    }

    public void invertAllOrigins()
    {
        float[] newMtx = new float[16];
        Matrix.invertM(
                newMtx, 0,
                this.inverseOrigin, 0);
        this.inverseOrigin = newMtx;
        for(BoneNode subNode : this.subBones)
        {
            subNode.invertAllOrigins();
        }
    }

    public void stackOrigins()
    {
        for(BoneNode subNode : this.subBones)
        {
            float[] newMtx = new float[16];
            Matrix.multiplyMM(newMtx, 0, this.inverseOrigin, 0, subNode.inverseOrigin, 0);
            subNode.inverseOrigin = newMtx;
            subNode.stackOrigins();
        }
    }

    public void stackTransformMatrices()
    {
        for(BoneNode subNode : this.subBones)
        {
            float[] newMtx = new float[16];
            Matrix.multiplyMM(newMtx, 0, this.transformMtx, 0, subNode.transformMtx, 0);
            subNode.transformMtx = newMtx;
            subNode.stackTransformMatrices();
        }
    }

}
