
package ontologySimilarity;

import ontologyManager.OntologyManager;
import static java.lang.System.out;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.semanticweb.owlapi.model.OWLClass;

/**
 * 
 * The class that implements the OntologySimilarity similarity interface.
 * 
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file). 
 * 
 */
public class OntologySimilarityImpl implements OntologySimilarity {

    private final static Logger log = Logger.getLogger(OntologySimilarityImpl.class.getName());
    //To disply Steps in Calculation of Sub-scores set debug to Level.INFO else set to Level.FINE
    static final Level debug = Level.FINE;
        
    /**
     * Method that, given 2 OWL classes from the same Ontology computes a similarity score
     * @param class1 OWLClass1
     * @param class2 OWLClass1
     * @param owlURI URI for the Ontology File, Can be a local file / URL
     * @return A similarity Score between O and 1
     */
    @Override
    public double getConceptSimScore(OWLClass class1, OWLClass class2, String owlURI) 
    {
        if(class1 == null || class2 == null)
        {
            log.log(Level.WARNING, "Class1 or Class2 cannot be null; Concept Similarity score returned 0");
            return 0.0;
        }//if
        else
        {
            return ConceptSimilarity.getConceptSimScore(class1, class2, owlURI);
        }//else
    
    }//getConceptSimScore
    

    /**
     * Method that, given IRI for 2 OWL classes from the same Ontology computes a similarity score
     * @param class1 IRI for OWLClass1
     * @param class2 IRI for OWLClass2
     * @param owlURI URI for the Ontology File, Can be a local file / URL
     * @return A similarity Score between O and 1    
     */        
    @Override
    public double getConceptSimScore(String class1, String class2, String owlURI)
    {
        if( owlURI == null || owlURI.trim().equals(""))
        {
            log.log(Level.WARNING, "Location of ontology file is null/empty Concept Similarity score returned 0");
            return 0.0;
        }//if
        else if(class1 == null || class2 == null  || class1.trim().equals("") || class2.trim().equals(""))
        {
            log.log(Level.WARNING, "URI for Class1/Class2 is null/empty, Concept Similarity score returned 0");
            return 0.0;        
        }//else if
        else
        {
            OntologyManager parser = OntologyManager.getInstance(owlURI);

            OWLClass OWLclass1 = parser.getConceptClass(class1.trim());
            OWLClass OWLclass2 = parser.getConceptClass(class2.trim());

            if(OWLclass1 == null)
            {
                log.log(Level.WARNING, "No class found for IRI ("+class1+") in Ontology "+owlURI+" \nConcept Similarity score returned 0");
                return 0.0;
            }//if
            else if(OWLclass2 == null)
            {
                log.log(Level.WARNING, "No class found for IRI ("+class2+"); in Ontology "+owlURI+" \nConcept Similarity score returned 0");
                return 0.0;            
            }//else if
            else
            {
                return this.getConceptSimScore(OWLclass1, OWLclass2, owlURI);        
            }//else

        }//else

    }//getConceptSimScore
    
    public static void main(String[] args)
    {
        //Test code
        
        OntologySimilarity OntSim= new OntologySimilarityImpl();
        //Test Code
        String class1 = "http://purl.obolibrary.org/obo/OBIws_0000175";
        String class2 = "http://purl.obolibrary.org/obo/OBIws_0000027";
        String owlURI = "owl/obiws.owl";
        OntologyManager parser = OntologyManager.getInstance(owlURI);
        
        OWLClass cls1 = parser.getConceptClass(class1);
        OWLClass cls2 = parser.getConceptClass(class2);

        out.println("Finding Concept similarity between\nClass 1 - " + parser.getClassLabel(cls1) +" : "+class1
                +"\nClass 2 - "+ parser.getClassLabel(cls2)+" : "+class2);
        
        double score = OntSim.getConceptSimScore(class1, class2, owlURI);
        out.println("Overall Concept Similarity score = " + score);

        out.println("\n-----------------------------------------------------------\n");
        
        class1 = "http://purl.obolibrary.org/obo/OBIws_0000078";
        class2 = "http://purl.obolibrary.org/obo/OBI_0000973";
        cls1 = parser.getConceptClass(class1);
        cls2 = parser.getConceptClass(class2);

        out.println("Finding Concept similarity between\nClass 1 - " + parser.getClassLabel(cls1) +" : "+class1
                +"\nClass 2 - "+ parser.getClassLabel(cls2)+" : "+class2);
        
        score = OntSim.getConceptSimScore(cls1, cls2, owlURI);
        out.println("Overall Concept Similarity score = " + score);

        out.println("\n-----------------------------------------------------------\n");
        
        class1 = "http://purl.obolibrary.org/obo/OBIws_0000078";
        class2 = "http://purl.obolibrary.org/obo/OBIws_0000097";
        cls1 = parser.getConceptClass(class1);
        cls2 = parser.getConceptClass(class2);

        out.println("Finding Concept similarity between\nClass 1 - " + parser.getClassLabel(cls1) +" : "+class1
                +"\nClass 2 - "+ parser.getClassLabel(cls2)+" : "+class2);
        
        score = OntSim.getConceptSimScore(cls1, cls2, owlURI);
        out.println("Overall Concept Similarity score = " + score);
        
        out.println("\n-----------------------------------------------------------\n");
        
        class1 = "http://purl.obolibrary.org/obo/OBIws_0000081";
        class2 = "http://purl.obolibrary.org/obo/OBIws_0000084";
        cls1 = parser.getConceptClass(class1);
        cls2 = parser.getConceptClass(class2);

        out.println("Finding Concept similarity between\nClass 1 - " + parser.getClassLabel(cls1) +" : "+class1
                +"\nClass 2 - "+ parser.getClassLabel(cls2)+" : "+class2);
        
        score = OntSim.getConceptSimScore(cls1, cls2, owlURI);
        out.println("Overall Concept Similarity score = " + score);
        
    }//main


}//OntologySimilarity
