
package ontologySimilarity;

import java.util.logging.Level;
import java.util.logging.Logger;
import ontologyManager.OntologyManager;
import stringMatcher.*;
import org.semanticweb.owlapi.model.*;
import uk.ac.shef.wit.simmetrics.similaritymetrics.*;

/**
 * @author Rui Wang, Michael Cotterell, Alok Dhamanaskar 
 * @see LICENSE (MIT style license file). 
 * 
 * Class That either contains / invokes methods required to calculate concept similarity and its sub scores
 */
public class ConceptSimilarity {
    
    private final static Logger log = Logger.getLogger(OntologySimilarityImpl.class.getName());

    /** 
     * Computes the syntactic similarity between two concepts in the ontology, considers labels, classNames and definitions.
     * @param owlClass1
     * @param owlClass2
     * @return Similarity score between 0-1
     */
    public static double synSim (OWLClass owlClass1, OWLClass owlClass2, String owlURI)
    {
        double labelWt = 0.8;
        double classNameWt = 0.2;
        
        OntologyManager parser = OntologyManager.getInstance(owlURI);
        
        QGramsDistance mc = new QGramsDistance();

        String className1 = parser.getConceptName(owlClass1.getIRI().toString());
        String className2 = parser.getConceptName(owlClass2.getIRI().toString());
        
        String class1Label = parser.getClassLabel(owlClass1);
        String class2Label = parser.getClassLabel(owlClass2);
        
        //For Some ontologies Class names serve as Labels
        if(class1Label == null || class1Label.equals("") || class2Label == null || class2Label.equals("") )
        {
            log.log(Level.INFO, "Labels not Present for either of the the classes, Labels would be weighted lower");
            labelWt = 0.2;
            classNameWt = 0.8;
        }//if
        
        String class1Definition = parser.getClassDefinition(owlClass1);
        String class2Definition = parser.getClassDefinition(owlClass2);
        
        //Qgrams Similarity For the Labels and Names
        double scoreName = mc.getSimilarity(className1, className2);
        double scoreLabel = mc.getSimilarity(class1Label, class2Label);
        double scoreDef = CompareDefination.getSimilarity(class1Definition, class2Definition);

        double scoreTerm = (classNameWt * scoreName) + (labelWt * scoreLabel);

        double score = (scoreDef * 0.7) + (scoreTerm * 0.3);
        
        return score;
        
    }// synSim
    
    /**
     * Invokes different modules to calculate Sub-scores required to calculate Concept similarity score
     * @param owlClass1
     * @param owlClass2
     * @param owlURI
     * @return Concept similarity score
     */
    public static double getConceptSimScore(OWLClass owlClass1, OWLClass owlClass2, String owlURI) 
    {
        double weightSyn  = 0.2;
        double weightProp = 0.4; 
        double weightCvrg = 0.4;
            
        // syntactic similarity
        double synScore = ConceptSimilarity.synSim(owlClass1, owlClass2, owlURI);
        
        // property similarity
        double propScore = PropertySimilarity.getPropertySimScore(owlClass1, owlClass2, owlURI);

        // coverage similarity
        double cvrgScore = CoverageSimilarity.getCvrgSimScore(owlClass1, owlClass2, owlURI);
        
        // weighted sum
        double score = (weightSyn * synScore) + (weightProp * propScore) + (weightCvrg * cvrgScore);
        
        return score;
        
    } // getConceptSimScore
    

    public static void main(String[] args) 
    {
        //Test Code
        String class1 = "http://purl.obolibrary.org/obo/OBIws_0000100";
        String class2 = "http://purl.obolibrary.org/obo/OBIws_0000034";
        String owlURI = "http://obi-webservice.googlecode.com/svn/trunk/ontology/view/webService.owl";        
        OntologyManager parser = OntologyManager.getInstance(owlURI);
        
        OWLClass cls1 = parser.getConceptClass(class1);
        OWLClass cls2 = parser.getConceptClass(class2);
        
        double score = ConceptSimilarity.getConceptSimScore(cls1, cls2, owlURI);
        
        System.out.println("overall concept similarity score = " + score);
        
    } // main
}//ConceptSimilarity
