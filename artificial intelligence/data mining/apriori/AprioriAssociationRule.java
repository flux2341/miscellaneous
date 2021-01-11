package apriori;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AprioriAssociationRule
{
    public AprioriItemSet conditional, consequent;
    public double support, confidence;
    
    public AprioriAssociationRule(AprioriItemSet conditional,
                                  AprioriItemSet consequent)
    {
        this(conditional, consequent, -1.0, -1.0);
    }
    public AprioriAssociationRule(AprioriItemSet conditional,
                                  AprioriItemSet consequent,
                                  double support, double confidence)
    {
        this.conditional = conditional;
        this.consequent = consequent;
        this.support = support;
        this.confidence = confidence;
    }
    
    public boolean equals(AprioriAssociationRule rule)
    {
        return conditional.equals(rule.conditional)
                && consequent.equals(rule.consequent);
    }
    
    @Override
    public boolean equals(Object rule)
    {
        return this.equals((AprioriAssociationRule)(rule));
    }
    
    public String toString(ArrayList<String> names)
    {
        DecimalFormat df = new DecimalFormat("#.##");
        return conditional.toString(names) + " -> " + consequent.toString(names)
                +" ["+Apriori.formatPercentage(support)+", "+Apriori.formatPercentage(confidence)+"]";
    }
}
