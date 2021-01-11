package id3;

import java.util.ArrayList;

public class ID3Attribute
{
    public String name;
    public ArrayList<String> values;
    public ID3Attribute(String name)
    {
        this.name = name;
        values = new ArrayList<String>();
    }
    
    public int lookUpValue(String value)
    {
        return values.indexOf(value);
    }
    public int addValue(String value)
    {
        values.add(value);
        return values.size()-1;
    }
    public int lookUpOrAddValue(String value)
    {
        int r = lookUpValue(value);
        if (r < 0)
        {
            r = addValue(value);
        }
        return r;
    }
    
    public String toString()
    {
        String r = name + " {";
        for (int i=0; i<values.size(); ++i)
        {
            r += values.get(i);
            if (i < values.size() - 1)
            {
                r += ",";
            }
        }
        return r + "}";
    }
}
