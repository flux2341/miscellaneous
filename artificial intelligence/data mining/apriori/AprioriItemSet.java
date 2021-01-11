package apriori;

import java.util.ArrayList;
import java.util.List;

public class AprioriItemSet
{
    public ArrayList<Integer> items;
    public AprioriItemSet()
    {
        items = new ArrayList<>();
    }
    public AprioriItemSet(List<Integer> items)
    {
        this.items = new ArrayList<>(items);
    }
    public AprioriItemSet(AprioriItemSet set)
    {
        this(set.items);
    }
    
    public void addItem(int i)
    {
        items.add(i);
    }
    
    public int size()
    {
        return items.size();
    }
    
    public boolean contains(AprioriItemSet set)
    {
        for (int i=0; i<set.items.size(); ++i)
        {
            if (!this.contains(set.items.get(i)))
            {
                return false;
            }
        }
        return true;
    }
    public boolean contains(Integer item)
    {
        return items.contains(item);
    }
    
    public boolean equals(AprioriItemSet set)
    {
        return this.contains(set) && set.contains(this); // so lazy
    }
    
    @Override
    public boolean equals(Object set)
    {
        return this.equals((AprioriItemSet)(set));
    }
    
    public String toString(ArrayList<String> names)
    {
        StringBuilder b = new StringBuilder();
        for (int i=0;; ++i)
        {
            b.append(names.get(items.get(i)));
            if (i < items.size() - 1)
            {
                b.append(' ');
            }
            else
            {
                break;
            }
        }
        return b.toString();
    }
}
