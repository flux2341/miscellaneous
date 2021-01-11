package apriori;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Apriori
{
    
    public static String[] fruits = {"apple", "orange", "strawberry", "blueberry",
                              "wolfberry", "rasberry", "blackcurrant", "blackberry",
                              "tayberry", "gooseberry", "lingonberry", "bilberry",
                              "plum", "mango", "papaya", "banana", "watermelon",
                              "pomegranate", "fig", "lemon", "lime", "jackfruit",
                              "melon", "pear", "peach", "cranberry", "grape"};
    
    public static String[] gemstones = {"turquoise", "hematite", "tigerseye", "quartz",
                                 "pyrite", "malachite", "obsidian", "ruby", "agate",
                                 "jasper", "amethyst", "emerald", "opal", "pearl",
                                 "beryl", "sapphire", "topaz", "diamond", "onyx",
                                 "turquoise", "variscite", "tanzanite"};
    public static String[] generic = {"A", "B", "C", "D", "E", "F", "G", "H",
                                        "I", "J", "K", "L", "M", "N", "O",
                                        "P", "Q", "R", "S", "T", "U", "V",
                                        "W", "X", "Y", "Z"};
    
    public static void main(String[] args)
    {
        MainWindow main_window = new MainWindow();
        main_window.setSize(200, 200);
        main_window.setVisible(true);
    }
    
    
    public static String formatPercentage(double p)
    {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(p*100.0)+'%';
    }
    
    
    
    public static AprioriAssociationRule calculateSupportAndConfidence(AprioriItemSet condition, AprioriItemSet consequent, AprioriInput input)
    {
        AprioriAssociationRule r = new AprioriAssociationRule(condition, consequent, 0.0, 0.0);
        
        for (int i=0; i<input.sets.size(); ++i)
        {
            if (input.sets.get(i).contains(condition))
            {
                r.confidence++;
                if (input.sets.get(i).contains(consequent))
                {
                    r.support++;
                }
            }
        }
        
        r.confidence = r.support/r.confidence;
        r.support /= input.sets.size();
        
        return r;
    }
    
    
    public static ArrayList<AprioriItemSet> findFrequentItemSets(AprioriInput input)
    {
        ArrayList<AprioriItemSet> output = new ArrayList<>();
        
        ArrayList<AprioriItemSet> checked_sets = new ArrayList<>();
        for (int i=0; i<input.names.size(); ++i)
        {
            AprioriItemSet single_set = new AprioriItemSet();
            single_set.addItem(i);
            
            findFrequentItemSets(single_set, input, output, checked_sets);
        }
        
        return output;
    }
    
    
    public static void findFrequentItemSets(AprioriItemSet set, AprioriInput input, ArrayList<AprioriItemSet> output, ArrayList<AprioriItemSet> checked_sets)
    {
        //System.out.println("evaluating set "+set.toString(input.names));
        
        double support = input.calculateSupport(set);
        
        //System.out.println("    support: "+support);
        
        checked_sets.add(set);
        
        if (support >= input.minimum_support)
        {
            output.add(set);
            
            // generate supersets
            for (int i=0; i<input.names.size(); ++i)
            {
                if (!set.contains(i))
                {
                    AprioriItemSet superset = new AprioriItemSet(set);
                    superset.addItem(i);
                    
                    if (!checked_sets.contains(superset))
                    {
                        findFrequentItemSets(superset, input, output, checked_sets);
                    }
                }
            }
        }
        //else
        //{
        //    System.out.println("    stopping");
        //}
    }
    
    
    
    public static ArrayList<AprioriAssociationRule> findAssociationRules(ArrayList<AprioriItemSet> frequent_item_sets, AprioriInput input)
    {
        ArrayList<AprioriAssociationRule> checked_rules = new ArrayList<>();
        ArrayList<AprioriAssociationRule> output = new ArrayList<>();
        
        for (int i=0; i<frequent_item_sets.size(); ++i)
        {
            findAssociationRules(new AprioriItemSet(), frequent_item_sets.get(i), input, output, checked_rules);
        }
        return output;
    }
    
    public static void findAssociationRules(AprioriItemSet conditional, AprioriItemSet consequent, AprioriInput input, ArrayList<AprioriAssociationRule> output, ArrayList<AprioriAssociationRule> checked_rules)
    {
        if (consequent.size() == 0)
        {
            return;
        }
        
        if (conditional.size() > 0)
        {
            AprioriAssociationRule rule = calculateSupportAndConfidence(conditional, consequent, input);
            checked_rules.add(rule);
            if (rule.confidence > input.minimum_confidence)
            {
                output.add(rule);
            }
        }
        
        
        for (int i=0; i<consequent.size(); ++i)
        {
            AprioriItemSet new_conditional = new AprioriItemSet(conditional);
            AprioriItemSet new_consequent = new AprioriItemSet(consequent);
            
            new_conditional.addItem(new_consequent.items.remove(i));
            findAssociationRules(new_conditional, new_consequent, input, output, checked_rules);
        }
    }
}
