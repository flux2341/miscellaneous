package id3;

import java.util.ArrayList;

public class ID3DataRow
{
    public ArrayList<Integer> values;
    public ID3DataRow()
    {
        values = new ArrayList<Integer>();
    }
    public ID3DataRow(ID3DataRow r)
    {
        values = new ArrayList<Integer>(r.values);
    }
    
    public String toString(ArrayList<ID3Attribute> v)
    {
        String r = "{";
        for (int i=0; i<values.size(); ++i)
        {
            r += v.get(i).values.get(values.get(i));
            if (i < values.size() - 1)
            {
                r += ",";
            }
        }
        return r + "}";
    }

}
