
package ontologySimilarity;

import java.util.*;
import ontologyManager.OntologyManager;
import ontologyManager.Restriction;
import org.semanticweb.owlapi.model.*;

/**
 * 
 * This class supports methods required for calculation of Range similarity sub-subScore.
 * 
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file). 
 * 
 */
public class PropertyRangeSimilarity { 

    /**
     * Calculates the Range similarity sub score. Takes into account restrictions on these properties.
     * @param Property1
     * @param Property2
     * @param OWLclass1
     * @param OWLclass2
     * @param owlURI
     * @return The range similarity sub Subscore
     */   
    static double rangeSim(OWLProperty prop1, OWLProperty prop2, OWLClass class1, OWLClass class2, String owlURI) 
    {
        OntologyManager parser = OntologyManager.getInstance(owlURI);

        Set<OWLClass> prop1RangeSet = PropertyRangeSimilarity.getRanges(prop1, class1, owlURI);
        Set<OWLClass> prop2RangeSet = PropertyRangeSimilarity.getRanges(prop2, class2, owlURI);

        Double rangeSim = 0.0;
        
        //If range for either of the properties is not specified
        if( (prop1RangeSet.isEmpty() && !prop2RangeSet.isEmpty()) || (!prop1RangeSet.isEmpty() && prop2RangeSet.isEmpty()) )
            return 0;
        //If range for neither of the properties is specified
        if (prop1RangeSet.isEmpty() && prop2RangeSet.isEmpty()) 
            return 1;
        
        if (prop1.isOWLDataProperty() && prop2.isOWLDataProperty()) 
        {
            //Both are data properties
            double sum = 0.0;
            double n = prop1RangeSet.size() * prop2RangeSet.size();

            for (OWLClass prop1RangeCls : prop1RangeSet) 
            {
                for (OWLClass prop2RangeCls : prop2RangeSet) 
                {
                    String left = parser.getLocalClassName(prop1RangeCls);
                    String right = parser.getLocalClassName(prop2RangeCls);

                    if(left.equalsIgnoreCase(right)){
                        sum += 1;
                    } else if (right.equalsIgnoreCase("long") && left.equalsIgnoreCase("decimal")) {
                        sum += .75;
                    } else if (right.equalsIgnoreCase("long") && left.equalsIgnoreCase("int")) {
                        sum += 1;
                    } else if (right.equalsIgnoreCase("int") && left.equalsIgnoreCase("string")) {
                        sum += 1;
                    } else if (right.equalsIgnoreCase("int") && left.equalsIgnoreCase("long")) {
                        sum += .66;
                    } else if (right.equalsIgnoreCase("int") && (left.equalsIgnoreCase("decimal") || left.equalsIgnoreCase("float") || left.equalsIgnoreCase("double"))) {
                        sum += .33;
                    } else if (right.equalsIgnoreCase("string") && (left.equalsIgnoreCase("decimal") || left.equalsIgnoreCase("float") || left.equalsIgnoreCase("double"))){
                        sum += .5;
                    } else {
                        sum += 0;
                    } // if

                }// inner for
            }//Outer for

            rangeSim = sum / n;
        }//else if 
        else if (prop1.isOWLObjectProperty() && prop2.isOWLObjectProperty()) 
        {
            double common = 0.0;

            for (OWLClass c1 : prop1RangeSet) 
            {
                for (OWLClass c2 : prop2RangeSet) 
                    if (c1.getIRI().toString().equalsIgnoreCase(c2.getIRI().toString())) 
                        common++;
            }// for

            double propST = prop1RangeSet.size();

            if (propST == 0) 
                rangeSim = 1.0;
            else
                rangeSim = common / propST;            
        
        }//else if
        else 
        {
            //Properties are of different types
            rangeSim = 0.0;
        }//else
        return rangeSim;

    }//rangeSim

    
    /**
     * Returns Ranges for a particular property w.r.t to a particular class. 
     * If Restrictions on Properties exist their ranges would be considered instead
     * @param PWLproperty
     * @param owlClass
     * @param owlURI
     * @return Set of OWLclasses in the ranges for the property
     */    
    public static Set<OWLClass> getRanges(OWLProperty prop, OWLClass owlClass, String owlURI) 
    {
        Set<OWLClass> ranges = new HashSet<OWLClass>();
        OntologyManager parser = OntologyManager.getInstance(owlURI);

        if (prop == null) 
            return ranges;
        
        List<Restriction> restrOnProp = Restriction.getRestrictionsOnProp(owlClass, prop, owlURI);
        if (restrOnProp.isEmpty())
        {
            //There are no restrictions on the property
            ranges = parser.getRanges(prop);
        }//if
        else
        {
            //There is atleast one restriction on this property
            for (Restriction r : restrOnProp)
            {
                for (OWLClass cl : r.getCls())
                    ranges.add(cl);
            }//for
        }//else

        return ranges;
    } // getRanges
    

}//PropertyRangeSimilarity
