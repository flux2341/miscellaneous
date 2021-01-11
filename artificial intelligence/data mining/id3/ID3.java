package id3;

import java.util.ArrayList;

public class ID3
{
    public static void main(String[] args)
    {
        DataSet ds = DataSet.loadFromCSV("D:\\school\\2013 fall\\cs634\\ID3\\data.csv");
        //ds.print();
        System.out.println();
        
        int label_index = 1;
        System.out.println("label: "+ds.attributes.get(label_index));
        
        ID3Node n = runID3(ds, label_index);
        print(n);
        
        
        //System.out.println("VVVVVVVV");
        //System.out.println(6.0/15.0*I(1,5)+6.0/15.0*I(5,1)+3.0/15.0*I(3,0));
        
    }
    
    
    public static void print(ID3Node n)
    {
        System.out.print("if ");
        print(n, "");
    }
    public static void print(ID3Node n, String indent)
    {
        if (n.isLeaf())
        {
            ID3LeafNode ln = (ID3LeafNode)(n);
            System.out.println(", "+ln.attribute.name+" is "+ln.attribute.values.get(ln.value));
            System.out.print("if ");
        }
        else
        {
            ID3BranchNode bn = (ID3BranchNode)(n);
            for (int i=0; i<bn.children.size(); ++i)
            {
                ID3Node child = bn.children.get(i);
                System.out.print(bn.attribute.name+" is "+bn.attribute.values.get(i));
                if (child == null)
                {
                    System.out.println(indent+", null");
                    System.out.print("if ");
                }
                else
                {
                    if (!bn.children.get(i).isLeaf())
                        System.out.print(" and ");
                    print(bn.children.get(i),"");
                }
            }
        }
    }
    
    
    
    public static ID3Node runID3(DataSet ds, int attribute_index)
    {
        System.out.println("data ------");
        ds.print();
        System.out.println("-----------");
        
        
        ID3Attribute attribute = ds.attributes.get(attribute_index);
        if (attribute.values.size() != 2)
        {
            return null;
        }
        
        if (ds.data.size() == 0)
        {
            System.out.println("terminating: null");
            System.out.println();
            return null;
        }
        
        
        
        int[] counts = ds.getCounts(attribute_index);
        if (counts[0] == 0)
        {
            System.out.println("terminating: "+attribute.name+"="+attribute.values.get(1));
            System.out.println();
            return new ID3LeafNode(attribute, 1);
        }
        if (counts[1] == 0)
        {
            System.out.println("terminating: "+attribute.name+"="+attribute.values.get(0));
            System.out.println();
            return new ID3LeafNode(attribute, 0);
        }
        
        if (ds.attributes.size() == 0)
        {
            return new ID3LeafNode(null, -1);
        }
        
        int split_index = -1;
        double max_gain = -1;
        for (int i=0; i<ds.attributes.size(); ++i)
        {
            if (i == attribute_index) continue;
            
            double gain = Gain(counts, i, attribute_index, ds);
            //System.out.println(gain);
            if (split_index < 0 || gain > max_gain)
            {
                split_index = i;
                max_gain = gain;
            }
        }
        
        
        System.out.println();
        ID3Attribute split_attribute = ds.attributes.get(split_index);
        System.out.println("splitting on "+split_attribute.name);
        System.out.println();
        System.out.println();
        ArrayList<DataSet> split = ds.split(split_index);
        ID3BranchNode r = new ID3BranchNode(ds.attributes.get(split_index));
        for (int i=0; i<split_attribute.values.size(); ++i)
        {
            System.out.println("running ID3 for "+split_attribute.name+":"+split_attribute.values.get(i));
            r.children.add(runID3(split.get(i), split.get(i).findIndex(attribute)));
        }
        return r;
    }
    
    
    
    
    
    private static final double inv_log2 = 1.0/Math.log(2);
    public static double log2(double x)
    {
        return Math.log(x)*inv_log2;
    }
    
    
    public static double I(int p, int n)
    {
        if (p == 0 || n == 0)
        {
            return 0;
        }
        double sum = p + n;
        double pr = p/sum;
        double nr = n/sum;
        return -pr*log2(pr) - nr*log2(nr);
    }
    
    public static double Gain(int[] tc, int attribute_to_split, int attribute_to_count, DataSet ds)
    {
        double ipn = I(tc[0], tc[1]);
        int[][] sc = ds.getCounts(attribute_to_split, attribute_to_count);
        
        ID3Attribute to_split = ds.attributes.get(attribute_to_split);
        ID3Attribute to_count = ds.attributes.get(attribute_to_count);
        System.out.println("finding gain for "+to_split.name);
        System.out.print("x,");
        for (int i=0; i<to_count.values.size(); ++i)
        {
            System.out.print(to_count.values.get(i));
            if (i < to_count.values.size() - 1)
            {
                System.out.print(",");
            }
        }
        System.out.println();
        for (int i=0; i<sc.length; ++i)
        {
            System.out.println(to_split.values.get(i) + "," + sc[i][0] + ","+sc[i][1]);
        }
        
        System.out.println("I("+tc[0]+","+tc[1]+") = "+ipn);
        
        double ea = 0.0f;
        System.out.print("E = ");
        double denom = 1.0/(tc[0] + tc[1]);
        for (int i=0; i<sc.length; ++i)
        {
            ea += (sc[i][0] + sc[i][1])*denom*I(sc[i][0], sc[i][1]);
            System.out.print("("+sc[i][0]+"+"+sc[i][1]+")/("+tc[0]+"+"+tc[1]+")*I("+sc[i][0]+","+sc[i][1]+")");
            if (i < sc.length - 1)
            {
                System.out.print(" + ");
            }
        }
        System.out.println(") = "+ea);
        System.out.println("gain: = "+(ipn - ea));
        System.out.println();
        return ipn - ea;
    }
}
