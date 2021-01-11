package id3;

import java.util.ArrayList;

public class ID3BranchNode extends ID3Node
{
    public ID3Attribute attribute;
    public ArrayList<ID3Node> children;
    public ID3BranchNode(ID3Attribute attribute)
    {
        this.attribute = attribute;
        children = new ArrayList<ID3Node>();
    }
    public boolean isLeaf()
    {
        return false;
    }
}
