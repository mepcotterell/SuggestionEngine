/**
 * 
 */
package suggest.calculator;

import org.semanticweb.owlapi.model.*;
import parser.OntologyManager;

import ontology.similarity.ConceptSimilarity;
import parser.SawsdlParser;
//import stringMatch.impl.NGramSimilarity;
//import stringMatch.impl.StringMetrics;
import uk.ac.shef.wit.simmetrics.*;
import uk.ac.shef.wit.simmetrics.similaritymetrics.*;
import util.OpWsdl;
import util.Timer;

/**
 * @author Rui
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
		//penality for only syntax match
		double penality = 0.5;
		if (preferOp==null || op==null){
			
			return fnScore;
		}
		
		SawsdlParser sawsdl = new SawsdlParser();
		String opMr = sawsdl.getOpModelreference(op.getOpName(), op.getWsdlName());
		//user did not give a uri but a normal string, or no annotation(mordelreference) on the operation
		//user's prefered operation name v.s candidate operation's name
		if (!preferOp.startsWith("http://")|| opMr == null){
			if (preferOp.equalsIgnoreCase(op.getOpName())){
				fnScore = penality*1;
			}
			else{
				String tempPreferOp = preferOp;
				if(preferOp.startsWith("http://")){
					if (preferOp.contains("#")){
						tempPreferOp = preferOp.substring(preferOp.lastIndexOf("#")+1);
					}
					else {
						tempPreferOp = preferOp.substring(preferOp.lastIndexOf("/")+1);
					}
					
				}
				//operation name's syntax similarity
//				NGramSimilarity ngs =  new NGramSimilarity();
//				fnScore = ngs.getMatchScore(preferOp, op.getOpName());
//				StringMetrics sm = new StringMetrics();
                                QGramsDistance mc = new QGramsDistance();
//				fnScore =penality * sm.getMatchScore(tempPreferOp, op.getOpName());
                                fnScore = penality * mc.getSimilarity(tempPreferOp, op.getOpName());
			}
		}
		
		else{
		
		//user's prefered operation concept v.s candidate operation's concept annotation
		if (preferOp.equalsIgnoreCase(opMr)){
			fnScore = 1;
		}
		else
		{
			if (!opMr.startsWith("http://")){
//				StringMetrics sm = new StringMetrics();
                                QGramsDistance mc = new QGramsDistance();                              
//				fnScore =sm.getMatchScore(preferOp, op.getOpName());
                                fnScore = mc.getSimilarity(preferOp, op.getOpName());
			}
			else if(owlFileName!=null){		
			
			ConceptSimilarity cs = new ConceptSimilarity();
//			fnScore = cs.getConceptSimScore(opMr, preferOp, owlFileName);
		}}
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
//		String t = "jfaeioeoi/";
//		System.out.println(t.substring(t.lastIndexOf("/")+1));

	}

}
