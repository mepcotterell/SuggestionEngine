
package suggest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import util.WebServiceOpr;
import util.WebServiceOprScore;

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
	public List<WebServiceOprScore> getSuggestServices(List<WebServiceOpr> workflowPrefixOPs, List<WebServiceOpr> workflowSuffixOPs,
			List<WebServiceOpr> candidateOPs, String preferOp, String owlFileName, String initState) {
		
		if (workflowPrefixOPs == null || workflowSuffixOPs==null ||candidateOPs ==null){
			return null;
		}
				
		//reuse forward suggest
		ForwardSuggest prefixSuggest = new ForwardSuggest();		
		List<WebServiceOprScore> prefixDmScores = prefixSuggest.suggestNextService(workflowPrefixOPs, candidateOPs, preferOp, owlFileName, initState);
		prefixDmResults = prefixSuggest.getDmResults();
		
		//reuse backward suggest, to adjust initial state for the suffix, currently null
		BackwardSuggest suffixSuggest = new BackwardSuggest();
		List<WebServiceOprScore> suffixDmScores = suffixSuggest.getSuggestServices(workflowSuffixOPs, candidateOPs, preferOp, owlFileName, null);
		suffixDmResults = suffixSuggest.getDmResults();
		
		//avg forward and backward score
		List<WebServiceOprScore> biDmScores = new ArrayList<WebServiceOprScore>();
		for(WebServiceOprScore pre: prefixDmScores){
			for(WebServiceOprScore suf: suffixDmScores){
				if (pre.getOperationName().equals(suf.getOperationName()) && pre.getWsDescriptionDoc().equals(suf.getWsDescriptionDoc())){
					WebServiceOprScore biScore = new WebServiceOprScore(pre.getOperationName(), pre.getWsDescriptionDoc(),(pre.getScore()+suf.getScore())/2);
					biDmScores.add(biScore);
					break;
				}
			}
		}
		Collections.sort(biDmScores, Collections.reverseOrder());
		return biDmScores;
	}
	
	public static void main(String[] args) {

	}

}
