/**
 * 
 */
package suggest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import util.OpWsdl;
import util.OpWsdlScore;

/**
 * @author Rui Wang
 *
 */
public class BidirectionSuggest {
	
	//data mapping detail between prefix--candidate---suffix
	private Map<?, ?> prefixDmResults;
	private Map<?, ?> suffixDmResults;
	
	
	/**
	 * @return the prefixDmResults
	 */
	public Map<?, ?> getPrefixDmResults() {
		return prefixDmResults;
	}



	/**
	 * @return the suffixDmResults
	 */
	public Map<?, ?> getSuffixDmResults() {
		return suffixDmResults;
	}



	/**given two separated workflow fragment, suggest an operation in between them
	 * @param workflowPrefixOPs       a list of operations in workflow prefix
	 * @param workflowSuffixOPs       a list of operations in workflow suffix
	 * @param candidateOPs            a list of candidate operations
	 * @param preferOp
	 * @param owlFileName
	 * @param initState   initial state file for the state before workflow prefix
	 * @return the score of every candidate operation
	 */
	public List<OpWsdlScore> getSuggestServices(List<OpWsdl> workflowPrefixOPs, List<OpWsdl> workflowSuffixOPs,
			List<OpWsdl> candidateOPs, String preferOp, String owlFileName, String initState) {
		
		if (workflowPrefixOPs == null || workflowSuffixOPs==null ||candidateOPs ==null){
			return null;
		}
				
		//reuse forward suggest
		ForwardSuggest prefixSuggest = new ForwardSuggest();		
		List<OpWsdlScore> prefixDmScores = prefixSuggest.getSuggestServices(workflowPrefixOPs, candidateOPs, preferOp, owlFileName, initState);
		prefixDmResults = prefixSuggest.getDmResults();
		
		//reuse backward suggest, to adjust initial state for the suffix, currently null
		BackwardSuggest suffixSuggest = new BackwardSuggest();
		List<OpWsdlScore> suffixDmScores = suffixSuggest.getSuggestServices(workflowSuffixOPs, candidateOPs, preferOp, owlFileName, null);
		suffixDmResults = suffixSuggest.getDmResults();
		
		//avg forward and backward score
		List<OpWsdlScore> biDmScores = new ArrayList<OpWsdlScore>();
		for(OpWsdlScore pre: prefixDmScores){
			for(OpWsdlScore suf: suffixDmScores){
				if (pre.getOpName().equals(suf.getOpName()) && pre.getWsdlName().equals(suf.getWsdlName())){
					OpWsdlScore biScore = new OpWsdlScore(pre.getOpName(), pre.getWsdlName(),(pre.getScore()+suf.getScore())/2);
					biDmScores.add(biScore);
					break;
				}
			}
		}
		Collections.sort(biDmScores, Collections.reverseOrder());
		return biDmScores;
	}
	
	/**given two separated workflow fragment, suggest an operation in between them
	 * 
	 * @param workflowPrefixOPs
	 * @param workflowSuffixOPs
	 * @param candidateOPs
	 * @param preferOp
	 * @param opOwlFileName
	 * @param msgOwlFileName
	 * @param initState
	 * @return
	 */
	public List<OpWsdlScore> getSuggestServices2owl(List<OpWsdl> workflowPrefixOPs, List<OpWsdl> workflowSuffixOPs,
			List<OpWsdl> candidateOPs, String preferOp, String opOwlFileName, String msgOwlFileName, String initState) {
		
		if (workflowPrefixOPs == null || workflowSuffixOPs==null ||candidateOPs ==null){
			return null;
		}
				
		//reuse forward suggest
		ForwardSuggest prefixSuggest = new ForwardSuggest();		
		List<OpWsdlScore> prefixDmScores = prefixSuggest.getSuggestServicesWith2owl(workflowPrefixOPs, candidateOPs, preferOp, opOwlFileName, msgOwlFileName, initState);
		prefixDmResults = prefixSuggest.getDmResults();
		
		//reuse backward suggest, to adjust initial state for the suffix, currently null
		BackwardSuggest suffixSuggest = new BackwardSuggest();
		List<OpWsdlScore> suffixDmScores = suffixSuggest.getSuggestServices2owl(workflowSuffixOPs, candidateOPs, preferOp, opOwlFileName, msgOwlFileName, null);
		suffixDmResults = suffixSuggest.getDmResults();
		
		//avg forward and backward score
		List<OpWsdlScore> biDmScores = new ArrayList<OpWsdlScore>();
		for(OpWsdlScore pre: prefixDmScores){
			for(OpWsdlScore suf: suffixDmScores){
				if (pre.getOpName().equals(suf.getOpName()) && pre.getWsdlName().equals(suf.getWsdlName())){
					OpWsdlScore biScore = new OpWsdlScore(pre.getOpName(), pre.getWsdlName(),(pre.getScore()+suf.getScore())*0.5);
					biDmScores.add(biScore);
					break;
				}
			}
		}
		Collections.sort(biDmScores, Collections.reverseOrder());
		return biDmScores;
	}

	/**
	 * constructor
	 */
	public BidirectionSuggest() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
