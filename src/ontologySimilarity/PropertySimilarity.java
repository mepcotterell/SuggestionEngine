package ontologySimilarity;

import ontologyManager.OntologyManager;
import static ontologySimilarity.Hungarian.hungarian;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.shef.wit.simmetrics.similaritymetrics.*;

import org.semanticweb.owlapi.model.*;
import stringMatcher.CompareDefination;

/**
 * 
 * The class has methods required to calculate Property similarity between two concepts in the Ontology
 * 
 * @author Rui Wang, Michael Cotterell, Alok Dhamanaskar
 * 
 */
public class PropertySimilarity {

    private final static Logger log = Logger.getLogger(OntologySimilarityImpl.class.getName());
    static final Level debug = ConceptSimilarity.debug;

    /** 
     * Returns the cardinality similarity between two properties.
     * @param Pst
     * @param Pcs
     * @return
     */
    static double cardinalitySim (OWLProperty Pst, OWLProperty Pcs, String owlURI)
    {
        OntologyManager parser = OntologyManager.getInstance(owlURI);

        Set<OWLClass> PstDomainSet = parser.getDomains(Pst);
        Set<OWLClass> PcsDomainSet = parser.getDomains(Pcs);

        if ((PstDomainSet != null && PcsDomainSet != null) && (!PstDomainSet.isEmpty() && !PcsDomainSet.isEmpty())) {

            OWLClass Cst = new ArrayList<OWLClass>(PstDomainSet).get(0);
            int cardPst = parser.getCardinality(Pst, Cst);

            OWLClass Ccs = new ArrayList<OWLClass>(PcsDomainSet).get(0);
            int cardPcs = parser.getCardinality(Pcs, Ccs);

            if (cardPst == cardPcs || (parser.isFunctional(Pst) && parser.isFunctional(Pcs))) {
                return 1;
            } else if (cardPst < cardPcs) {
                return .9;
            } else {
                return .7;
            } // if
            
        } else {
            return 0;
        } // if
        
    } // cardinalitySim

       
    /** Return the matching score between two properties.
     * @param prop1
     * @param prop2
     * @param class1
     * @param class2
     * @return
     */
    private static double propMatch(OWLProperty prop1, OWLProperty prop2, OWLClass class1, OWLClass class2, String owlURI) 
    {
        double cardSim;
        double rangeSim;
        double synSim;
        double propMatchScore;
        OntologyManager parser = OntologyManager.getInstance(owlURI);
        
        //Calculating SynSim 
        //Will be 1, if its the same Property, but that doesnt Mean propertyMatch = 1
        synSim = synSim(prop1, prop2, owlURI);
        rangeSim = PropertyRangeSimilarity.rangeSim(prop1, prop2, class1,class2, owlURI);

        propMatchScore = (0.5 * rangeSim) + (0.5 * synSim);

        //I think the only place considering cardinality would make sence is 
        //when comparing P1 - P2 s.t. both have restrictions on them
        cardSim = cardinalitySim(prop1, prop2, owlURI);
        
        log.log(debug,"Matching properties \nProp 1 : " + parser.getPropertyLabel(prop1) + 
                "\nProp2 : " +parser.getPropertyLabel(prop2) + " - Matched with : " + propMatchScore);

        return propMatchScore;

    } // propMatch

    /** 
     * Returns the property similarity between two OWL classes.
     * @param OWLclass1
     * @param OWLclass2
     * @return Property Similarity sub score
     */
    public static double getPropertySimScore(OWLClass class1, OWLClass class2, String owlURI) 
    {
        OntologyManager parser = OntologyManager.getInstance(owlURI);
        log.log(debug, "Calculating Property Similarity");

        //Getting properties of class 1
        Set<OWLProperty> class1PropSet = parser.getProperties(class1); 
        List<OWLProperty> class1PropList = new ArrayList<OWLProperty>(class1PropSet);
        int class1propSize = class1PropSet.size();
 
        //------------------------------------------------------------------------------
        String tempLog = "";
        for (OWLProperty p : class1PropList )
            if(parser.getPropertyLabel(p)==null || parser.getPropertyLabel(p).equals(""))
                tempLog += p.toString() + "\n";
            else
            tempLog+=parser.getPropertyLabel(p) + "\n";
        log.log(debug, "Properties of class 1\n" + tempLog);
        //------------------------------------------------------------------------------        
        
        //Getting properties of class 2        
        Set<OWLProperty> class2PropSet = parser.getProperties(class2);
        List<OWLProperty> class2PropList = new ArrayList<OWLProperty>(class2PropSet);
        int class2PropSize = class2PropSet.size();
        
        //------------------------------------------------------------------------------        
        tempLog = "";
        for (OWLProperty p : class2PropList )
            if(parser.getPropertyLabel(p)==null || parser.getPropertyLabel(p).equals(""))
                tempLog += p.toString() + "\n";
            else
            tempLog+=parser.getPropertyLabel(p) + "\n";
        log.log(debug, "Properties of class 2\n" + tempLog);   
        //------------------------------------------------------------------------------        
                
        if (class1propSize == 0 || class2PropSize == 0)
            return 0;

        double size = (class1propSize < class2PropSize)? class1propSize : class2PropSize; 
        
        double[][] matrix;
        
        if (class1propSize > class2PropSize) 
        {
            matrix = new double[class2PropSize][class1propSize];
            for (int i = 0; i < class2PropList.size(); i++) 
            {
                OWLProperty p1 = class2PropList.get(i);
                for (int j = 0; j < class1PropList.size(); j++) 
                {
                    OWLProperty p2 = class1PropList.get(j);
                    double value = propMatch(p1, p2, class1, class2, owlURI);
                    matrix[i][j] = value;
                }//inner for
            }//outer for
        }//if
        else 
        {
            matrix = new double[class1propSize][class2PropSize];
            for (int i = 0; i < class1PropList.size(); i++) 
            {
                OWLProperty p1 = class1PropList.get(i);
                for (int j = 0; j < class2PropList.size(); j++) 
                {
                    OWLProperty p2 = class2PropList.get(j);
                    double value = propMatch(p1, p2, class1, class2, owlURI);
                    matrix[i][j] = value;
                } // for
            } // for
        }//else

        return hungarian(matrix) / size;
        
    }// getPropertySimScore

      
    /** 
     * Calculates and returns a score denoting the syntactic similarity between two properties.
     * @param property1
     * @param property2
     * @return Syntactic similarity Score
     */
     static double synSim(OWLProperty prop1, OWLProperty prop2, String owlURI) 
    {
        if (prop1.getIRI().equals(prop2.getIRI()))
            return 1.00;
        OntologyManager parser = OntologyManager.getInstance(owlURI);
        
        QGramsDistance mc = new QGramsDistance();

        String prop1Label = parser.getPropertyLabel(prop1);
        String prop2Label = parser.getPropertyLabel(prop2);
        
        String prop1Def = parser.getDefinition(prop1);
        String prop2Def = parser.getDefinition(prop2);
        
        double scoreDef = CompareDefination.getSimilarity(prop1Def, prop2Def);       
        double scoreLabel = mc.getSimilarity(prop1Label, prop2Label);
        
        if (prop1Def.equals("") && prop2Def.equals(""))
        {
            //Both the properties do not have a definition
            return scoreLabel;
        }
        else
        {
            return (0.3 * scoreLabel) + (0.7 * scoreDef);
        }
        
    }//synSim
    
} // PropertySimilarity
