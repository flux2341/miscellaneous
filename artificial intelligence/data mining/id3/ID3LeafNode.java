package id3;
public class ID3LeafNode extends ID3Node
{
    public ID3Attribute attribute;
    public int value;
    public ID3LeafNode(ID3Attribute attribute, int value)
    {
        this.attribute = attribute;
        this.value = value;
    }
    public boolean isLeaf()
    {
        return true;
    }
}