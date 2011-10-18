/**
 * 
 */
package suggest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.OpWsdl;
import util.OpWsdlPathScore_type;
import util.OpWsdlScore;

/**
 * @author Rui Wang
 *
 */
public class BackwardSuggest {
	
	
	
	//data mediation result for all candidateOPs: map[candidateOP, mapPath]
	//mapPath is a map for input of an candidateOP: map [path of the input of candidateOP, matched path in output&globalInput of workflow]
	private Map<OpWsdl, Map<OpWsdlPathScore_type, OpWsdlPathScore_type>> dmResults = new HashMap<OpWsdl, Map<OpWsdlPathScore_type, OpWsdlPathScore_type>>();
	
	
	/**to get a complete data mediation result
	 * for all candidateOPs:map[candidateOP, mapPath]
	 * mapPath is a map for input of an candidateOP: map <inputPaths, matchedOutputPaths>
	 * inputPaths has every input path and its matching score, isRequired(1,0.6,0.2), isInput(0,1)
	 * matchedOutputPaths has output paths match each input path, isSafeMath(0,1)
	 * @return the dmResults
	 */
	public Map<OpWsdl, Map<OpWsdlPathScore_type, OpWsdlPathScore_type>> getDmResults() {
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
	public List<OpWsdlScore> getSuggestServices(List<OpWsdl> workflowOPs,
			List<OpWsdl> candidateOPs, String preferOp, String owlFileName, String initState) {
		//initState may have to adjust, one initState for every candidate op?
		
		if (workflowOPs == null || candidateOPs ==null){
			return null;
		}
		
		List<OpWsdlScore> suggestionList = new ArrayList<OpWsdlScore>();

		//reuse some of forward method
		ForwardSuggest suggest = new ForwardSuggest();
		
		//each candate op is a one op process in forwardsuggest, first op of workflow is candidate op in forwardsuggest
		List<OpWsdl> candidateForward = new ArrayList<OpWsdl>();
			candidateForward.add(workflowOPs.get(0));
		for (OpWsdl op : candidateOPs){
			List<OpWsdl> processForward = new ArrayList<OpWsdl>();
			processForward.add(op);
			List<OpWsdlScore> oneSuggestionList = new ArrayList<OpWsdlScore>();			
			
			oneSuggestionList = suggest.getSuggestServices(processForward, candidateForward, preferOp, owlFileName, initState);
			OpWsdlScore result = new OpWsdlScore(op.getOpName(), op.getWsdlName(), oneSuggestionList.get(0).getScore());
			suggestionList.add(result);
			
			//update data mediation results
			dmResults.putAll( suggest.getDmResults());
		}
		
		Collections.sort(suggestionList, Collections.reverseOrder());
//		if (suggestionList.size() > maxSuggest) {
//			suggestionList = suggestionList.subList(0, maxSuggest);
//
//		}
		
		return suggestionList;
	}
	
	/**
	 * given 2 owl files, one to annotate operation, one to annotate input/output
	 * given operations in current workflow, all candidated operations return a
	 * list of suggested operations and their rank score, 
	 * 
	 * @param workflowOPs
	 * @param candidateOPs
	 * @param preferOp
	 * @param opOwlFileName
	 * @param msgOwlFileName
	 * @param initState
	 * @return
	 */
	public List<OpWsdlScore> getSuggestServices2owl(List<OpWsdl> workflowOPs,
			List<OpWsdl> candidateOPs, String preferOp, String opOwlFileName, String msgOwlFileName, String initState) {
		//initState may have to adjust, one initState for every candidate op?
		
		if (workflowOPs == null || candidateOPs ==null){
			return null;
		}
		
		List<OpWsdlScore> suggestionList = new ArrayList<OpWsdlScore>();

		//reuse some of forward method
		ForwardSuggest suggest = new ForwardSuggest();
		
		//each candate op is a one op process in forwardsuggest, first op of workflow is candidate op in forwardsuggest
		List<OpWsdl> candidateForward = new ArrayList<OpWsdl>();
			candidateForward.add(workflowOPs.get(0));
		for (OpWsdl op : candidateOPs){
			List<OpWsdl> processForward = new ArrayList<OpWsdl>();
			processForward.add(op);
			List<OpWsdlScore> oneSuggestionList = new ArrayList<OpWsdlScore>();			
			
			oneSuggestionList = suggest.getSuggestServicesWith2owl(processForward, candidateForward, preferOp, opOwlFileName, msgOwlFileName, initState);
			OpWsdlScore result = new OpWsdlScore(op.getOpName(), op.getWsdlName(), oneSuggestionList.get(0).getScore());
			suggestionList.add(result);
			
			//update data mediation results
			dmResults.putAll( suggest.getDmResults());
		}
		
		Collections.sort(suggestionList, Collections.reverseOrder());
//		if (suggestionList.size() > maxSuggest) {
//			suggestionList = suggestionList.subList(0, maxSuggest);
//
//		}
		
		return suggestionList;
	}
	
	/**
	 * 
	 */
	public BackwardSuggest() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
