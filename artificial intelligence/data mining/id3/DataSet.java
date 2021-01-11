package id3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class DataSet
{
    public ArrayList<ID3Attribute> attributes;
    public ArrayList<ID3DataRow> data;
    private DataSet()
    {
        attributes = new ArrayList<ID3Attribute>();
        data = new ArrayList<ID3DataRow>();
    }
    private DataSet(ArrayList<ID3Attribute> a)
    {
        attributes = new ArrayList<ID3Attribute>(a);
        data = new ArrayList<ID3DataRow>();
    }
    
    public int findIndex(ID3Attribute attribute)
    {
       for (int i=0; i<attributes.size(); ++i)
       {
           if (attributes.get(i) == attribute)
           {
               return i;
           }
       }
       return -1;
    }
    
    public void removeAttribute(ID3Attribute a)
    {
        int index = -1;
        for (int i=0; i<attributes.size(); ++i)
        {
            if (attributes.get(i) == a)
            {
                index = i;
                break;
            }
        }
        
        if (index >= 0)
        {
            removeAttribute(index);
        }
    }
    
    public void removeAttribute(int index)
    {
        attributes.remove(index);
        for (int i=0; i<data.size(); ++i)
        {
            data.get(i).values.remove(index);
        }
    }
    
    public int[] getCounts(int attribute_index)
    {
        ID3Attribute attribute = attributes.get(attribute_index);
        int[] r = new int[attribute.values.size()];
        for (int i=0; i<data.size(); ++i)
        {
            r[data.get(i).values.get(attribute_index)]++;
        }
        return r;
    }
    
    public int[][] getCounts(int attribute_to_split, int attribute_to_count)
    {
        ID3Attribute to_split = attributes.get(attribute_to_split);
        ID3Attribute to_count = attributes.get(attribute_to_count);
        int[][] r = new int[to_split.values.size()][to_count.values.size()];
        for (int i=0; i<data.size(); ++i)
        {
            ID3DataRow dr = data.get(i);
            int split_value = dr.values.get(attribute_to_split);
            int count_value = dr.values.get(attribute_to_count);
            r[split_value][count_value]++;
        }
        return r;
    }
    
    
    public ArrayList<DataSet> split(int attribute_index)
    {
        ID3Attribute attribute = attributes.get(attribute_index);
        ArrayList<DataSet> r = new ArrayList<DataSet>(attribute.values.size());
        for (int i=0; i<attribute.values.size(); ++i)
        {
            r.add(new DataSet(attributes));
        }
        
        for (int i=0; i<data.size(); ++i)
        {
            ID3DataRow dr = data.get(i);
            r.get(dr.values.get(attribute_index)).data.add(new ID3DataRow(dr));
        }
        
        for (int i=0; i<r.size(); ++i)
        {
            r.get(i).removeAttribute(attribute_index);
        }
        
        return r;
    }
    
    
    
    public static DataSet loadFromCSV(String path)
    {
        DataSet r = new DataSet();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(new File(path)));
            
            String[] s_var = br.readLine().split(",");
            for (int i=0; i<s_var.length; ++i)
            {
                r.attributes.add(new ID3Attribute(s_var[i]));
            }
            
            String line;
            while ((line = br.readLine()) != null)
            {
                ID3DataRow dr = new ID3DataRow();
                String[] s_val = line.split(",");
                for (int i=0; i<s_val.length; ++i)
                {
                    dr.values.add(r.attributes.get(i).lookUpOrAddValue(s_val[i]));
                }
                r.data.add(dr);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return r;
    }
    
    
    public void print()
    {
        for (int i=0; i<attributes.size(); ++i)
        {
            System.out.print(attributes.get(i).name);
            if (i < attributes.size()-1)
            {
                System.out.print(",");
            }
        }
        System.out.println();
        
        for (int i=0; i<data.size(); ++i)
        {
            ID3DataRow dr = data.get(i);
            for (int j=0; j<dr.values.size(); ++j)
            {
                System.out.print(attributes.get(j).values.get(dr.values.get(j)));
                if (j < dr.values.size() - 1)
                {
                    System.out.print(",");
                }
            }
            System.out.println();
        }
        
        //System.out.println("variables:");
        //for (int i=0; i<attributes.size(); ++i)
        //{
        //    System.out.println("    "+attributes.get(i));
        //}
        //System.out.println("data:");
        //for (int i=0; i<data.size(); ++i)
        //{
        //    System.out.println("    "+data.get(i).toString(attributes));
        //}
    }
    

}
