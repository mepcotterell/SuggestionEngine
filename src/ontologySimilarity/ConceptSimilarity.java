
package ontologySimilarity;

import java.util.logging.Level;
import java.util.logging.Logger;
import ontologyManager.OntologyManager;
import stringMatcher.*;
import org.semanticweb.owlapi.model.*;
import uk.ac.shef.wit.simmetrics.similaritymetrics.*;
import static java.lang.System.out;

/**
 * 
 * Class That either contains / invokes methods required to calculate concept similarity and its sub scores
 * @author Rui Wang, Michael Cotterell, Alok Dhamanaskar 
 * @see LICENSE (MIT style license file). 
 * 
 */
public class ConceptSimilarity {
    
    //Logger For this class
    private static final  Logger log = Logger.getLogger(OntologySimilarityImpl.class.getName());
    //To disply Steps in Calculation of Sub-scores set debug to Level.INFO else set to Level.FINE
    static final Level debug = OntologySimilarityImpl.debug;


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
        
        String class1Definition = parser.getDefinition(owlClass1);
        String class2Definition = parser.getDefinition(owlClass2);
        
        //Qgrams Similarity For the Labels and Names
        double scoreName = mc.getSimilarity(className1, className2);
        double scoreLabel = mc.getSimilarity(class1Label, class2Label);
        double scoreDef = CompareDefination.getSimilarity(class1Definition, class2Definition);

        double scoreTerm = (classNameWt * scoreName) + (labelWt * scoreLabel);

        double score = (scoreDef * 0.7) + (scoreTerm * 0.3);
        log.log(debug, "Syntactic Similarity Score : " + score);

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
        double weightSyn  = 0.35;
        double weightProp = 0.25; 
        double weightCvrg = 0.3;       
        
        if (owlClass1.getIRI() == owlClass2.getIRI())
            return 1.0;
    
        // syntactic similarity
        double synScore = ConceptSimilarity.synSim(owlClass1, owlClass2, owlURI);

        // property similarity
        double propScore = PropertySimilarity.getPropertySimScore(owlClass1, owlClass2, owlURI);

        // coverage similarity
        double cvrgScore = CoverageSimilarity.getCvrgSimScore(owlClass1, owlClass2, owlURI);

        // weighted sum
        double score = (weightSyn * synScore) + (weightProp * propScore) + (weightCvrg * cvrgScore);
        
        return score;
        
    }// getConceptSimScore
    

    public static void main(String[] args) 
    {
        //Test Code
        String class1 = "http://purl.obolibrary.org/obo/OBIws_0000073";
        String class2 = "http://purl.obolibrary.org/obo/OBIws_0000027";
        String owlURI = "http://obi-webservice.googlecode.com/svn/trunk/ontology/webService.owl";
        OntologyManager parser = OntologyManager.getInstance(owlURI);
        
        OWLClass cls1 = parser.getConceptClass(class1);
        OWLClass cls2 = parser.getConceptClass(class2);

        out.println("Finding Concept similarity between\nClass 1 - " + parser.getClassLabel(cls1) +" : "+class1
                +"\nClass 2 - "+ parser.getClassLabel(cls2)+" : "+class2);
        
        double score = ConceptSimilarity.getConceptSimScore(cls1, cls2, owlURI);
        out.println("Overall Concept Similarity score = " + score);

//        out.println("\n-----------------------------------------------------------\n");
//        
//        class1 = "http://purl.obolibrary.org/obo/OBIws_0000078";
//        class2 = "http://purl.obolibrary.org/obo/OBI_0000973";
//        cls1 = parser.getConceptClass(class1);
//        cls2 = parser.getConceptClass(class2);
//
//        out.println("Finding Concept similarity between\nClass 1 - " + parser.getClassLabel(cls1) +" : "+class1
//                +"\nClass 2 - "+ parser.getClassLabel(cls2)+" : "+class2);
//        
//        score = ConceptSimilarity.getConceptSimScore(cls1, cls2, owlURI);
//        out.println("Overall Concept Similarity score = " + score);
//
//        out.println("\n-----------------------------------------------------------\n");
//        
//        class1 = "http://purl.obolibrary.org/obo/OBIws_0000078";
//        class2 = "http://purl.obolibrary.org/obo/OBIws_0000097";
//        cls1 = parser.getConceptClass(class1);
//        cls2 = parser.getConceptClass(class2);
//
//        out.println("Finding Concept similarity between\nClass 1 - " + parser.getClassLabel(cls1) +" : "+class1
//                +"\nClass 2 - "+ parser.getClassLabel(cls2)+" : "+class2);
//        
//        score = ConceptSimilarity.getConceptSimScore(cls1, cls2, owlURI);
//        out.println("Overall Concept Similarity score = " + score);
//        
//        out.println("\n-----------------------------------------------------------\n");
//        
//        class1 = "http://purl.obolibrary.org/obo/OBIws_0000081";
//        class2 = "http://purl.obolibrary.org/obo/OBIws_0000097";
//        cls1 = parser.getConceptClass(class1);
//        cls2 = parser.getConceptClass(class2);
//
//        out.println("Finding Concept similarity between\nClass 1 - " + parser.getClassLabel(cls1) +" : "+class1
//                +"\nClass 2 - "+ parser.getClassLabel(cls2)+" : "+class2);
//        
//        score = ConceptSimilarity.getConceptSimScore(cls1, cls2, owlURI);
//        out.println("Overall Concept Similarity score = " + score);
        
       
    } // main
}//ConceptSimilarity
