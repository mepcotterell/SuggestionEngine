package ontology.similarity;

import java.util.ArrayList;
import java.util.HashSet;

import java.util.Set;
import org.semanticweb.owlapi.model.*;
import parser.OntologyManager;

public class CoverageSimilarity {

    public static final double LAMDA_1 = 0.25;    
    public static final double LAMDA_2 = 0.50;
    public static final double LAMDA_3 = 0.75;

    /** Returns the length of the path between two concepts in the ontology.
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
    
    public static double getCvrgSimScore(OWLClass Cst, OWLClass Ccs, String owlURI)
    {

        OntologyManager parser = OntologyManager.getInstance(owlURI);

        OWLClass PofCcs = new ArrayList<OWLClass>(parser.getSuperClasses(Ccs).get(0)).get(0);
               
        int x = 0;
        double value = 0.0;

        // Case 1: The two classes are equivalent
        if (parser.getEquivalentClasses(Cst).contains(Ccs)) {

            value = 1.0;

        // Case 2: This class is an ancestor of that class.
        } else if (parser.getSuperClasses(Ccs).contains(Cst)) {

            x = getConceptPathLength(Cst, Ccs, owlURI);
            value = Math.pow(Math.E, -LAMDA_1 * x);
        
        // Case 3: That class is an ancestor of this class.
        } else if (parser.getSuperClasses(Cst).contains(Ccs)) {
        
            x = getConceptPathLength(Cst, Ccs, owlURI);
            value = Math.pow(Math.E, -LAMDA_2 * x);
        
        // Case 4: Everything else
        } else {

            x = getConceptPathLength(Cst, Ccs, owlURI);
            value = Math.pow(Math.E, -LAMDA_3 * x);
            
        } // if
        
        return value;
        
    } // getCvrgSimScore

} // CoverageSimilarity
