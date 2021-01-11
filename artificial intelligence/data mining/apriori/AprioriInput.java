package apriori;

import java.util.ArrayList;
import java.util.Collections;

public class AprioriInput
{
    public ArrayList<String> names;
    public ArrayList<AprioriItemSet> sets;
    public double minimum_support, minimum_confidence;
    public AprioriInput(String data, double minimum_support, double minimum_confidence)
    {
        sets = new ArrayList<>();
        names = new ArrayList<>();
        
        this.minimum_support = minimum_support;
        this.minimum_confidence = minimum_confidence;
        
        String[] split = data.split("\n");
        for (int i=0; i<split.length; ++i)
        {
            //split[i] = split[i].replaceAll("[^A-Za-z,]", "");
            //String[] elements = split[i].split(",");
            
            String[] elements = split[i].split(" ");
            
            if (elements.length == 0)
            {
                System.out.println("error parsing line "+i+": "+split[i]);
                continue;
            }
            
            AprioriItemSet set = new AprioriItemSet();
            
            for (int j=0; j<elements.length; ++j)
            {
                if (elements[j].equals(""))
                {
                    System.out.println("error parsing element "+j+" of line "+i);
                    continue;
                }
                
                int index = names.indexOf(elements[j]);
                if (index < 0)
                {
                    names.add(elements[j]);
                    index = names.size()-1;
                }
                
                set.addItem(index);
            }
            
            if (set.size() > 0)
            {
                sets.add(set);
            }
        }
    }
    public AprioriInput(ArrayList<String> item_names, int n_sets, int min_per_set, int max_per_set, double minimum_support, double minimum_confidence)
    {
        names = item_names;
        sets = new ArrayList<>(n_sets);
        
        this.minimum_support = minimum_support;
        this.minimum_confidence = minimum_confidence;
        
        ArrayList<Integer> indices = new ArrayList<>(names.size());
        
        for (int i=0; i<names.size(); ++i)
        {
            indices.add(i);
        }
        
        for (int i=0; i<n_sets; ++i)
        {
            Collections.shuffle(indices);
            int length = (int)(min_per_set+Math.random()*(max_per_set-min_per_set));
            sets.add(new AprioriItemSet(indices.subList(0,length)));
        }
    }
    public double calculateSupport(AprioriItemSet set)
    {
        int r = 0;
        for (int i=0; i<sets.size(); ++i)
        {
            if (sets.get(i).contains(set))
            {
                r++;
            }
        }
        return ((double)(r))/sets.size();
    }
    public String toString()
    {
        StringBuilder r = new StringBuilder();
        for (int i=0;; ++i)
        {
            r.append(sets.get(i).toString(names));
            if (i < sets.size() - 1)
            {
                r.append('\n');
            }
            else
            {
                break;
            }
        }
        return r.toString();
    }
}
