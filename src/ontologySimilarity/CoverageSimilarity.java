package ontologySimilarity;

import ontologyManager.OntologyManager;
import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.*;
import static java.lang.System.out;

/**
 * 
 * The class has methods required to calculate Coverage Similarity sub-score.
 * 
 * @author Michael Cotterell
 * @author Alok Dhamanaskar
 * 
 * @see LICENSE (MIT style license file). 
 * 
 */

public class CoverageSimilarity {

    public static final double LAMDA_1 = 0.25;    
    public static final double LAMDA_2 = 0.50;
    public static final double LAMDA_3 = 0.75;

    /** 
     * Returns the length of the path between two concepts in the ontology.
     * @param Cst The first class
     * @param Ccs The second class
     * @param owlURI The String URI to the OWL ontology
     * @return 
     */
    public static int getConceptPathLength (OWLClass Cst, OWLClass Ccs, String owlURI) 
    {    
        OntologyManager parser = OntologyManager.getInstance(owlURI);
        
        // The combined super classes
        Set<OWLClass> supers = new HashSet<OWLClass>();
        
        // Only the super classes that they have in common
        Set<OWLClass> common = new HashSet<OWLClass>();
        
        // Onlt the classes in the path
        Set<OWLClass> path   = new HashSet<OWLClass>();
        
        // Get the super classes for each class
        Set<OWLClass> cstSupers = new HashSet<OWLClass>();
        
        for (Set<OWLClass> set: parser.getSuperClasses(Cst)) {
            cstSupers.addAll(set);
        } // for
        
        Set<OWLClass> ccsSupers = new HashSet<OWLClass>();
        
        for (Set<OWLClass> set: parser.getSuperClasses(Ccs)) {
            ccsSupers.addAll(set);
        } // for
        
        // Determine which classes the two sets have in common
        for (OWLClass cls1: cstSupers) {
            for (OWLClass cls2: ccsSupers) {
                if (cls1.equals(cls2)) common.add(cls1);
            } //for
        } // for
        
        // Determine the set of combined super classes
        supers.addAll(cstSupers);        
        supers.addAll(ccsSupers);       
        
        // Derive the path set using the supers set and the common set
        path.addAll(supers);
        path.removeAll(common);
        
        return path.size() + 1;
        
    } // getConceptPathLength
    
    /**
     * Returns the Coverage Similarity sub-score depending upon the relative position of the 
     * two terms in the Ontology.
     * @param owlClass1
     * @param owlClass2
     * @param owlURI
     * @return 
     */
    public static double getCvrgSimScore(OWLClass owlClass1, OWLClass owlClass2, String owlURI)
    {

        OntologyManager parser = OntologyManager.getInstance(owlURI);
        int x = getConceptPathLength(owlClass1, owlClass2, owlURI);
        double value;


        if (parser.getEquivalentClasses(owlClass1).contains(owlClass2)) 
        {
           // Case 1: The two classes are equivalent
            value = 1.0;
        }
        else if (parser.hasSuperClass(owlClass2, owlClass1)) 
        {
            // Case 2: This class is an ancestor of that class.
            value = Math.pow(Math.E, -LAMDA_1 * x);
        }
        else if (parser.hasSuperClass(owlClass1, owlClass2)) 
        {
            // Case 3: That class is an ancestor of this class.
            value = Math.pow(Math.E, -LAMDA_1 * x);
        } 
        else 
        {
            //Case 4: Everything else
            value = Math.pow(Math.E, -LAMDA_2 * x);
        }// else
        
        return value;
        
    } // getCvrgSimScore
    
    public static void main(String[] args)
    {
        //Test code
        String class1 = "http://purl.obolibrary.org/obo/OBIws_0000040";
        String class2 = "http://purl.obolibrary.org/obo/OBIws_0000036";
        String owlURI = "http://obi-webservice.googlecode.com/svn/trunk/ontology/webService.owl";
        OntologyManager parser = OntologyManager.getInstance(owlURI);
        
        OWLClass cls1 = parser.getConceptClass(class1);
        OWLClass cls2 = parser.getConceptClass(class2);
        
        out.println("CoverageSimilarity score between "
                + parser.getClassLabel(cls1) + " and " +parser.getClassLabel(cls2) + " = "
                + CoverageSimilarity.getCvrgSimScore(cls1, cls2, owlURI));
    
    }//main

} // CoverageSimilarity
