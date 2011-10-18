/**
 * 
 */
package ontology.similarity;

import parser.OntologyManager;

import org.semanticweb.owlapi.model.*;

import uk.ac.shef.wit.simmetrics.similaritymetrics.*;

/**
 * @author Rui Wang, Michael Cotterell
 * 
 */
public class ConceptSimilarity {

    /** Given a concept IRI, return the OWLClass
     * @param conceptIRI     full URI (not only name) of a concept
     * @return  the OWLClass object 
     */
    public static OWLClass getConceptClass(String conceptIRI, String owlURI) 
    {
        OWLClass conceptClass = null;
        OntologyManager parser = OntologyManager.getInstance(owlURI);
        conceptClass = parser.getConceptClass(conceptIRI);
        return conceptClass;
    } // getConceptClass

    /** Given ontology file name, check if the output annotated with 
     *  outConceptIRI is safe to be fed into input annotated with inConceptIRI.
     *  safe: same, equivalent or input is ancestor of output
     * @param inConceptIRI
     * @param outConceptIRI
     * @param owlURI
     * @return 0 not safe, 1 safe
     */
    public static int conceptMapSafe(String inConceptIRI, String outConceptIRI, String owlURI) 
    {
        OntologyManager parser = OntologyManager.getInstance(owlURI);

        int safe = 0;
        
        // not given concept
        if (inConceptIRI == null || outConceptIRI == null || !inConceptIRI.contains("http://") || !outConceptIRI.contains("http://")) {
            return 0;
        } // if

        OWLClass inConceptClass = getConceptClass(inConceptIRI, owlURI);
        OWLClass outConceptClass = getConceptClass(inConceptIRI, owlURI);

        if (inConceptClass == null || outConceptClass == null) {
            return 0;
        } // if

        // semantically safe: same, equivallent, or input is ancestor of output
        if (parser.hasSuperClass(outConceptClass, inConceptClass) || inConceptClass.getIRI().equals(outConceptClass.getIRI()) || parser.hasEquivalentClass(inConceptClass, outConceptClass)) {
            safe = 1;
        } // if
        
        return safe;
        
    } // conceptMapSafe
    

    /** Computes the syntactic similarity between two concepts in the ontology.
     * @param Pst
     * @param Pcs
     * @return
     */
    public static double synSim (OWLClass Pst, OWLClass Pcs, String owlURI) 
    {
        OntologyManager parser = OntologyManager.getInstance(owlURI);
        
        double wName = 0.2;
        double wLabel = 0.8;

        QGramsDistance mc = new QGramsDistance();

        String PstLocalName = parser.getConceptName(Pst.getIRI().toString());
        String PcsLocalName = parser.getConceptName(Pcs.getIRI().toString());
        
        String PstLabel = parser.getClassLabel(Pst);
        String PcsLabel = parser.getClassLabel(Pcs);
        
        // TODO: Need to compare definitions.
        
        double scoreName = mc.getSimilarity(PstLocalName, PcsLocalName);
        double scoreLabel = mc.getSimilarity(PstLabel, PcsLabel);

        double score = (wName * scoreName) + (wLabel * scoreLabel);
        
        return score;
        
    } // synSim
    
    /*
     * @see
     * ontology.interfaces.SimilarityFactory#getConceptSimScore(java.lang.String
     * , java.lang.String)
     */
    public static double getConceptSimScore(OWLClass inConceptClass, OWLClass outConceptClass, String owlURI) 
    {
        double weightSyn  = 0.2;
        double weightProp = 0.4; 
        double weightCvrg = 0.4;
            
        // syntactic similarity
        double synScore = ConceptSimilarity.synSim(inConceptClass, outConceptClass, owlURI);
        
        // if either of the annotations is not class, return only 
        // weight*syntactic score of comparing two concept names
        if (inConceptClass == null || outConceptClass == null) {
            return weightSyn * synScore;
        } // if
        
        // property similarity
        double propScore = PropertySimilarity.getPropertySimScore(inConceptClass, outConceptClass, owlURI);

        // coverage similarity
        double cvrgScore = CoverageSimilarity.getCvrgSimScore(inConceptClass, outConceptClass, owlURI);
        
        // weighted sum
        double score = (weightSyn * synScore) + (weightProp * propScore) + (weightCvrg * cvrgScore);
        
        return score;
        
    } // getConceptSimScore
    

    /** For testing
     * @param args
     */
    public static void main(String[] args) 
    {
        String owlURI = "obi.owl";
        
        OntologyManager parser = OntologyManager.getInstance(owlURI);
        
        ConceptSimilarity cs = new ConceptSimilarity();
        
        String concept1 = "http://purl.obolibrary.org/obo/webService.owl#Class_0013";
        String concept2 = "http://purl.obolibrary.org/obo/obi.owl#Class_34";
        
        OWLClass cls1 = parser.getConceptClass(concept1);
        OWLClass cls2 = parser.getConceptClass(concept2);
        
        double score = cs.getConceptSimScore(cls1, cls2, owlURI);
        
        System.out.println("overall concept similarity score = " + score);
        
    } // main
}
