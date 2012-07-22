
package ontologySimilarity;

import org.semanticweb.owlapi.model.OWLClass;

/**
 * Interface that defines methods supported by the Ontology Similarity module
 * 
 * @author Alok Dhamanaskar
 * @see    LICENSE (MIT style license file).
 */
public interface OntologySimilarity {
    
    /**
     * Method that, given 2 OWL classes from the same Ontology computes a similarity score
     * @param class1 OWLClass1
     * @param class2 OWLClass1
     * @param owlURI URI for the Ontology File, Can be a local file / URL
     * @return A similarity Score between O and 1
     */
    public double getConceptSimScore(OWLClass class1, OWLClass class2, String owlURI);

    /**
     * Method that, given IRI for 2 OWL classes from the same Ontology computes a similarity score
     * @param class1 IRI for OWLClass1
     * @param class2 IRI for OWLClass2
     * @param owlURI URI for the Ontology File, Can be a local file / URL
     * @return A similarity Score between O and 1    
     */
    public double getConceptSimScore(String class2, String class1, String owlURI);

    
}//OntologySimilarity
