
package suggest;

import java.util.*;
import util.WebServiceOpr;
import util.WebServiceOprScore;
import util.WebServiceOprScore_type;

/**
 * @author Rui Wang
 *
 */
public class BackwardSuggest {
	
	//data mediation result for all candidateOPs: map[candidateOP, mapPath]
	//mapPath is a map for input of an candidateOP: map [path of the input of candidateOP, matched path in output&globalInput of workflow]
	private Map<WebServiceOpr, Map<WebServiceOprScore_type, WebServiceOprScore_type>> dmResults = new HashMap<WebServiceOpr, Map<WebServiceOprScore_type, WebServiceOprScore_type>>();
	
	
	/**to get a complete data mediation result
	 * for all candidateOPs:map[candidateOP, mapPath]
	 * mapPath is a map for input of an candidateOP: map <inputPaths, matchedOutputPaths>
	 * inputPaths has every input path and its matching score, isRequired(1,0.6,0.2), isInput(0,1)
	 * matchedOutputPaths has output paths match each input path, isSafeMath(0,1)
	 * @return the dmResults
	 */
	public Map<WebServiceOpr, Map<WebServiceOprScore_type, WebServiceOprScore_type>> getDmResults() {
		return dmResults;
	}	

	/**
	 * given operations in current workflow, all candidated operations return a
	 * list of suggested operations and their rank score, 
	 * 
	 * 
	 * @param workflowOPs
	 * @param candidateOPs
	 * @return
	 */
	public List<WebServiceOprScore> getSuggestServices(List<WebServiceOpr> workflowOPs,
			List<WebServiceOpr> candidateOPs, String preferOp, String owlFileName, String initState) {
		//initState may have to adjust, one initState for every candidate op?
		
		if (workflowOPs == null || candidateOPs ==null){
			return null;
		}
		
		List<WebServiceOprScore> suggestionList = new ArrayList<WebServiceOprScore>();

		//reuse some of forward method
		ForwardSuggest suggest = new ForwardSuggest();
		
		//each candate op is a one op process in forwardsuggest, first op of workflow is candidate op in forwardsuggest
		List<WebServiceOpr> candidateForward = new ArrayList<WebServiceOpr>();
			candidateForward.add(workflowOPs.get(0));
		for (WebServiceOpr op : candidateOPs){
			List<WebServiceOpr> processForward = new ArrayList<WebServiceOpr>();
			processForward.add(op);
			List<WebServiceOprScore> oneSuggestionList = new ArrayList<WebServiceOprScore>();			
			
			oneSuggestionList = suggest.suggestNextService(processForward, candidateForward, preferOp, owlFileName, initState);
			WebServiceOprScore result = new WebServiceOprScore(op.getOperationName(), op.getWsDescriptionDoc(), oneSuggestionList.get(0).getScore());
			suggestionList.add(result);
			
			//update data mediation results
			dmResults.putAll( suggest.getDmResults());
		}
		Collections.sort(suggestionList, Collections.reverseOrder());
		return suggestionList;
	}


	public static void main(String[] args) {

	}

}
