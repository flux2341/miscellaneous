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
        //MainWindow main_window = new MainWindow();
        //main_window.setSize(200, 200);
        //main_window.setVisible(true);
        
        
        AprioriInput input = new AprioriInput("Apple Banana Cake\n"
                                            +"Doughnut Pear Apple\n"
                                            +"Apple Cake Soda\n"
                                            +"Doughnut Pear Soda\n"
                                            +"Banana Soda Cake\n"
                                            +"Apple Cake Pear Doughnut\n"
                                            +"Doughnut Pear Cake\n"
                                            +"Cake Banana\n"
                                            +"Cake Apple Pear Doughnut\n"
                                            +"Soda Banana\n", -1, -1);
        
        
        System.out.print("variables: ");
        for (int i=0; i<input.names.size(); ++i)
        {
            System.out.print(input.names.get(i)+" ");
        }
        System.out.println();
        
        ArrayList<AprioriItemSet> sets = findFrequentItemSets(input);
        //System.out.println(sets.size());
        
        int size2=0, size3=0;
        AprioriItemSet lrgg2=null;
        AprioriItemSet lrg=null;
        
        for (int i=0; i<sets.size(); ++i)
        {
            AprioriItemSet set = sets.get(i);
            if (set.size() == 2)
            {
                ++size2;
            }
            else if (set.size() == 3)
            {
                ++size3;
            }
            
            if (set.size() >= 2)
            {
                if (lrgg2 == null || input.calculateSupport(set) > input.calculateSupport(lrgg2))
                {
                    lrgg2 = set;
                }
            }
            
            if (lrg == null || input.calculateSupport(set) > input.calculateSupport(lrg))
            {
                lrg = set;
            }
        }
        
        System.out.println(sets.size());
        System.out.println(size2);
        System.out.println(size3);
        System.out.println((lrgg2 == null)? "null": lrgg2.toString(input.names) +" "+ input.calculateSupport(lrgg2));
        System.out.println((lrg == null)? "null": lrg.toString(input.names) + input.calculateSupport(lrg));
        
        
        AprioriAssociationRule same_confidence;
        AprioriAssociationRule greater_confidence;
        AprioriAssociationRule smaller_confidence;
        
        ArrayList<AprioriAssociationRule> rules = findAssociationRules(sets, input);
        //for (int i=0; i<rules.size(); ++i)
        //{
        //    AprioriAssociationRule rule_a = rules.get(i);
        //    if (rule.conditional.items.size() == 1
        //            && rule.consequent.items.size() == 1
        //            && rule.support > 0.0 && rule.confidence > 0.0)
        //    {
        //        
        //    }
        //}
        
        
        
        

        
        
        for (int i=0; i<input.names.size(); ++i)
        {
            for (int j=0; j<input.names.size(); ++j)
            {
                if (i == j) continue;
                
                AprioriAssociationRule ra = findRule(i, j, rules);
                if (ra == null) continue;
                
                AprioriAssociationRule rb = findRule(j, i, rules);
                if (rb == null) continue;
                
                if (ra.confidence <= 0 || rb.confidence <= 0) continue;
                
                if (ra.confidence == rb.confidence)
                {
                    System.out.println("A = B");
                    System.out.println("    "+ra.toString(input.names));
                    System.out.println("    "+rb.toString(input.names));
                }
                else if (ra.confidence > rb.confidence)
                {
                    System.out.println("A < B");
                    System.out.println("    "+ra.toString(input.names));
                    System.out.println("    "+rb.toString(input.names));
                }
                else if (ra.confidence < rb.confidence)
                {
                    System.out.println("A > B");
                    System.out.println("    "+ra.toString(input.names));
                    System.out.println("    "+rb.toString(input.names));
                }
                
                
            }
        }
        
        for (int i=0; i<rules.size(); ++i)
        {
            AprioriAssociationRule ra = rules.get(i);
            for (int j=0; j<rules.size(); ++j)
            {
                if (i == j) continue;
                
                AprioriAssociationRule rb = rules.get(j);
                if (ra.equals(rb))
                {
                    System.out.println(ra.toString(input.names));
                    System.out.println(rb.toString(input.names));
                }
            }
        }
        
        
//        int n_unique_conditionals = 0;
//        ArrayList<AprioriItemSet> seen_conditionals = new ArrayList<AprioriItemSet>();
//        
//        int n_unique_consequents = 0;
//        ArrayList<AprioriItemSet> seen_consequents = new ArrayList<AprioriItemSet>();
//        
//        for (int i=0; i<rules.size(); ++i)
//        {
//            AprioriAssociationRule rule = rules.get(i);
//            
//            if (!seen_conditionals.contains(rule.conditional))
//            {
//                seen_conditionals.add(rule.conditional);
//                n_unique_conditionals++;
//            }
//            
//            if (!seen_consequents.contains(rule.consequent))
//            {
//                seen_consequents.add(rule.consequent);
//                n_unique_consequents++;
//            }
//            //System.out.println(rule.conditional.items.size() + " " + rule.consequent.items.size());
//            //System.out.println(rules.get(i).toString(input.names));
//        }
        //System.out.println(n_unique_conditionals);
        //System.out.println(n_unique_consequents);
        //System.out.println(rules.size());
        
    }
    
    
    private static AprioriAssociationRule findRule(int a, int b, ArrayList<AprioriAssociationRule> rules)
    {
        for (int i=0; i<rules.size(); ++i)
        {
            AprioriAssociationRule rule = rules.get(i);
            if (rule.conditional.items.contains(a) && rule.consequent.items.contains(b)
                    && rule.conditional.items.size() == 1 && rule.consequent.items.size() == 1)
            {
                return rule;
            }
        }
        return null;
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
            
            if (!checked_rules.contains(rule) && rule.confidence > input.minimum_confidence)
            {
                output.add(rule);
                checked_rules.add(rule);
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
