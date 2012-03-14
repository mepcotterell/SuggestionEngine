package suggest.calculator;

import java.util.logging.Level;
import java.util.logging.Logger;
import mepcotterell.SimpleCache;
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
                String owlURI = "/home/alok/Desktop/SuggestionEngineWS/owl/obi.owl";        
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
                    String tempOpName = op.getOpName();
                    if(preferOp.startsWith("http://"))
                    {
                         OWLClass preferopClass = parser.getConceptClass(preferOp);
                         tempPreferOp = parser.getClassLabel(preferopClass);
                    }
                    if (opMr != null)
                    if (opMr.startsWith("http://") && owlFileName!=null)
                    {
                            OWLClass Oprcls = parser.getConceptClass(opMr);
                            tempOpName = parser.getClassLabel(Oprcls);
                            
                    }
                    //operation name's syntax similarity
                    QGramsDistance mc = new QGramsDistance();
                    String t = op.getOpName();
                    fnScore = penality * mc.getSimilarity(tempPreferOp, tempOpName);
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
                                    if (preferOp!=null && op.getOpName()!=null)
                                    fnScore = mc.getSimilarity(preferOp, op.getOpName());
                                    else
                                        fnScore = 0;
                        }
			else if(owlFileName!=null)
                        {		
                            
                            OWLClass cls1 = parser.getConceptClass(preferOp);                            
                            OWLClass cls2 = parser.getConceptClass(opMr);

        
                            if (cls1 == null || cls2 == null) {
                                fnScore = 0;
                                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "One of the concepts doesn''t seem to exist. cls1 = {0} ({1}), cls2 = {2} ({3})", new Object[]{cls1, preferOp, cls2, opMr});
                            } // if
                            
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
