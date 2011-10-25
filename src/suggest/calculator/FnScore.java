package suggest.calculator;

import org.semanticweb.owlapi.model.*;
import parser.OntologyManager;

import ontology.similarity.ConceptSimilarity;
import parser.SawsdlParser;
import uk.ac.shef.wit.simmetrics.similaritymetrics.*;
import util.OpWsdl;
import util.Timer;

/**
 * @author Rui , alok
 *
 */
public class FnScore {
	
	
	/**given owl file name
	 * given the user prefered operation concept, candidate operation, owl file name, 
	 * return the matching score for the candidate operation v.s prefered operation
	 * preferOp v.s op's name or modelreference (if preferOp starts with http://)
	 * @param preferOp
	 * @param op
	 * @param owlFileName
	 * @return
	 */
	public double calculateFnScore (String preferOp, OpWsdl op, String owlFileName){
		double fnScore = 0;
                String owlURI = "owl/obi.owl";        
                OntologyManager parser = OntologyManager.getInstance(owlURI);
		//penality for only syntax match
		double penality = 0.5;
		if (preferOp==null || op==null){
			
			return fnScore;
		}
		
		SawsdlParser sawsdl = new SawsdlParser();
		String opMr = sawsdl.getOpModelreference(op.getOpName(), op.getWsdlName());
		//user did not give a uri but a normal string, or no annotation(mordelreference) on the operation
		//user's prefered operation name v.s candidate operation's name
		if (!preferOp.startsWith("http://")|| opMr == null)
                {
                    String tempPreferOp = preferOp;
                    if(preferOp.startsWith("http://"))
                    {
                            OWLClass preferopClass = parser.getConceptClass(preferOp);
                            tempPreferOp = parser.getClassLabel(preferopClass);
                    }
                    //operation name's syntax similarity
                    QGramsDistance mc = new QGramsDistance();
                    String t = op.getOpName();
                    fnScore = penality * mc.getSimilarity(tempPreferOp, op.getOpName());
		}
		
		else
                {		
                    //user's prefered operation concept v.s candidate operation's concept annotation
                    if (preferOp.equalsIgnoreCase(opMr))
                    {
			fnScore = 1;
                    }
                    else
                    {
                        if (!opMr.startsWith("http://"))
                        {
                                    QGramsDistance mc = new QGramsDistance();                              
                                    fnScore = mc.getSimilarity(preferOp, op.getOpName());
                        }
			else if(owlFileName!=null)
                        {		
                            OWLClass cls1 = parser.getConceptClass(preferOp);                            
                            OWLClass cls2 = parser.getConceptClass(opMr);
        
                            fnScore = ConceptSimilarity.getConceptSimScore(cls1, cls2, owlURI);
                            //fnScore = cs.getConceptSimScore(opMr, preferOp, owlFileName);
                        }
                    }
		}
		return fnScore;
		
	}
	
	/**
	 * constructor
	 */
	public FnScore() {

	}

	/**for test
	 * @param args
	 */
	public static void main(String[] args) {
		Timer.startTimer();
		FnScore test = new FnScore();
//		double score = test.calculateFnScore("http://purl.obolibrary.org/obo/obi.owl#Class_4", new OpWsdl("runWUBlast","wsdl/3/WSWUBlast.wsdl"), "owl/obi.owl");
		double score = test.calculateFnScore("http://purl.obolibrary.org/obo/obi.owl#array2string", new OpWsdl("array2string","wsdl/3/WSConverter.wsdl"), "owl/obi.owl");
//		double score = test.calculateFnScore("http://purl.obolibrary.org/obo/obi.owl#getIds", new OpWsdl("getIds","wsdl/3/WSWUBlast.wsdl"), "owl/obi.owl");

		
		System.out.println(score);
		Timer.endTimer();

	}

}
