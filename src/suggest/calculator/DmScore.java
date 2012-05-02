
package suggest.calculator;

import dataMediator.PathRank;
import java.util.List;
import java.util.Map;
import java.util.Set;
import util.WebServiceOprScore_type;
import util.WebServiceOpr;

/**
 * @author Rui Wang
 * @see LICENSE (MIT style license file).
 */
public class DmScore {

    //path-based data mediation result for input of an candidateOP: map <inputPaths, matchedOutputPaths>
    private Map<WebServiceOprScore_type, WebServiceOprScore_type> dmResults = null;
    
    /**to get a complete data mediation result
     * map <inputPaths, matchedOutputPaths>
     * inputPaths has every input path and its matching score, isRequired(1,0.6,0.2), isInput(0,1)
     * matchedOutputPaths has output paths match each input path, isSafeMath(0,1)
     * @return the dmResults
     */
    public Map<WebServiceOprScore_type, WebServiceOprScore_type> getDmResults() 
    {
        return dmResults;
    } // getDmResults



    /** path-based data mediation score for a candidate operation
     * 
     * @param workflowOPs List of operations Currently in the Workflow
     * @param candidateOP The Candidate operation for which Data mediation sub-score has to be calculated
     * @param owlFileName The location of the Ontology file (Can be Relative location
     * in the system or a URI of the web)
     * @return score of data mediation for the candidate operation (how input of candidate operation will be fed)
     */
    public double calculatePathDmScore(List<WebServiceOpr> workflowOPs, WebServiceOpr candidateOP, String owlURI) 
    {
        double dmScore = 0;
        Map<WebServiceOprScore_type, WebServiceOprScore_type> dmOneopResult = PathRank.dataMediation(workflowOPs, candidateOP, owlURI);

        if (dmOneopResult != null) {
            dmResults = dmOneopResult;
            Set<WebServiceOprScore_type> candidateOPpaths = dmOneopResult.keySet();
            boolean allUnknown = true;

            for (WebServiceOprScore_type path : candidateOPpaths) {
                if (path.isRequired() != WebServiceOprScore_type.unknown) {
                    allUnknown = false;
                    break;
                }
            }


            if (!allUnknown) {
                int nRequire = 0;
                int nOptional = 0;

                for (WebServiceOprScore_type path : candidateOPpaths) {		
                    //path.isRequired() will panel the score of the not required input path (0.1), or unknown (0.8)
                    if (path.isRequired() == 1) {
                        nRequire = nRequire + 1;
                    } else {
                        nOptional = nOptional + 1;
                    }

                    dmScore = dmScore + path.getScore() * path.isRequired();

                }

                //different weight for required input, optional, unknown, they actually are defined in class WebServiceOprScore_type.
                dmScore = dmScore / ((WebServiceOprScore_type.require * nRequire) + (WebServiceOprScore_type.optional * nOptional));

            } else {
                for (WebServiceOprScore_type path : candidateOPpaths) {
                    dmScore = dmScore + path.getScore();
                }
                //weight of every path is 1/n
                dmScore = dmScore / candidateOPpaths.size();
            }
        }

        return dmScore;

    } // calculatePathDmScore

//    /**leaf-based data mediation score for a candidate operation
//     * @param workflowOPs  operations in current workflow, topologic order
//     * @param candidateOP   one candidate operation
//     * @param owlFileName
//     * @return                  score of leaf-based data mediation for the candidate operation
//     */
//    public double calculateLeafDmScore(List<WebServiceOpr> workflowOPs, WebServiceOpr candidateOP, String owlFileName) 
//    {
//        double dmScore = 0;
//        LeafMediation lm = new LeafMediation();
//        Map<OpWsdlPathScore_type, WebServiceOprScore_type> dmOneopResult = lm.dm(workflowOPs, candidateOP, owlFileName);
//        if (dmOneopResult != null) {
//            dmResults = dmOneopResult;
//            Set<OpWsdlPathScore_type> candidateOPpaths = dmOneopResult.keySet();
//
//            for (WebServiceOprScore_type path : candidateOPpaths) {
//
//                dmScore = dmScore + path.getScore();
//            }
//
//            dmScore = dmScore / candidateOPpaths.size();
//        }
//
//        return dmScore;
//        
//    } // calculateLeafDmScore

//    /**structure-based data mediation score for the candidate operation
//     * apply subtree homeomorphism to data mediation
//     * @param workflowOPs
//     * @param candidateOP
//     * @param owlFileName
//     * @return
//     */
//    public double calculateHomeoDmScore(List<WebServiceOpr> workflowOPs, WebServiceOpr candidateOP, String owlFileName) 
//    {
//        double dmScore = 0;
//        TreeHomeomorphism homeo = new TreeHomeomorphism();
//        List<IODG> homeoResult = homeo.dm(workflowOPs, candidateOP, owlFileName);
//        dmScore = homeoResult.get(homeoResult.size() - 1).getScore();
//        homeoDmResult = homeoResult;
//        return dmScore;
//    } // calculateHomeoDmScore

    public static void main(String[] args) {

        

    }
}
