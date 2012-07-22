
package suggest.calculator;

import inputOutputMatch.PHomomorphismSim;
import inputOutputMatch.PathRank;
import java.util.List;
import java.util.Map;
import java.util.Set;
import util.WebServiceOpr;
import util.WebServiceOprScore_type;

/**
 * 
 * Provides methods for invoking pathbased and pHomomorphism input-output matching 
 * algorithms to calculate input-output matching sub-scores
 * 
 * @author Rui Wang
 * @author Alok Dhamanaskar
 * @see LICENSE (MIT style license file).
 * 
 */
public class DmScore {

    //The result for path-based Data Mediation as a Map<IPpathsOFcandidateOP, OPpathsOFworkflowOps>
    private Map<WebServiceOprScore_type, WebServiceOprScore_type> dmResults = null;
    
    /**Returns the DataMediation Result as a Map < IPpathsOFcandidateOP, OPpathsOFworkflowOps>
     * Which has the matched path for every input Path of the Candidate operation
     * 
     * @return The DataMediation Results < IPpathsOFcandidateOP, OPpathsOFworkflowOps>
     */
    public Map<WebServiceOprScore_type, WebServiceOprScore_type> getDmResults() 
    {
        return dmResults;
    }

    /** Calculates the DataMediation score for a candidate operation, using the Path-based data-mediation algorithm.
     * Invokes PathRank.dataMediation() that returns the best matched path for every Input 
     * path of the candidate operation and calculates the DM score for the candidate operation by weighting it 
     * depending on the elements in the path are required / Optional
     * 
     * @param workflowOPs List of operations Currently in the Workflow
     * @param candidateOP The Candidate operation for which Data mediation sub-score has to be calculated
     * @param owlFileName The location of the Ontology file (Can be Relative location
     * in the system or a URI of the web)
     * @return score of data mediation for the candidate operation (how input of candidate operation will be fed)
     */
    public double calculatePathDmScore(List<WebServiceOpr> workflowOPs, WebServiceOpr candidateOP, String owlURI, List <String> globalIps) 
    {
        double dmScore = 0;
        boolean allUnknown = true;
        //Map<ipPathsOFcandidateOP,matchedopPathsofWorkflowOP>PathRank
        Map<WebServiceOprScore_type, WebServiceOprScore_type> dmOneopResult = PathRank.dataMediation(workflowOPs, candidateOP, owlURI,globalIps);

        if (dmOneopResult != null) {
            dmResults = dmOneopResult;
            //Since the key part of the map "ipPathsOFcandidateOP" have the details of the scores, and if they are Required / Optional / Unspecified
            Set<WebServiceOprScore_type> candidateOPpaths = dmOneopResult.keySet();
            
            // Find if Requires / not required info is available for any of the paths of the input of candidate op
            for (WebServiceOprScore_type path : candidateOPpaths) {
                if (path.isRequired() != WebServiceOprScore_type.unknown) {
                    allUnknown = false;
                    break;
                }
            }

            if (!allUnknown) 
            {
                int noOFRequirePaths = 0;
                int noOFOptionalPaths = 0;

                for (WebServiceOprScore_type path : candidateOPpaths) {
                    // If the path is unknown / required it is counted as REQUIRED else optional
                    if (path.isRequired() < 0.8)
                        noOFOptionalPaths = noOFOptionalPaths + 1;
                    else
                        noOFRequirePaths = noOFRequirePaths + 1;

                    dmScore = dmScore + path.getScore() * path.isRequired();
                }
                dmScore = dmScore / ((WebServiceOprScore_type.require * noOFRequirePaths) + (WebServiceOprScore_type.optional * noOFOptionalPaths));
            } //If ends 
            else 
            {
                //No Path Info available, hence the Dmscore will be average DmScore for all paths
                for (WebServiceOprScore_type path : candidateOPpaths) 
                    dmScore = dmScore + path.getScore();

                //weight of every path is 1/n
                dmScore = dmScore / candidateOPpaths.size();
            }//else ends
        }//If (dmOneopResult != null) ends

        return dmScore;

    } // calculatePathDmScore

    /** Calculates the DataMediation score for a candidate operation, using the pHomomorphism.
     * 
     * @param workflowOPs List of operations Currently in the Workflow
     * @param candidateOP The Candidate operation for which Data mediation sub-score has to be calculated
     * @param owlFileName The location of the Ontology file (Can be Relative location
     * in the system or a URI of the web)
     * @return score of data mediation for the candidate operation (how input of candidate operation will be fed)
     */
    public double calculatepHomDmScore(List<WebServiceOpr> workflowOPs, WebServiceOpr candidateOP, String owlURI) 
    {
        double dmScore = 0;
        
        PHomomorphismSim phom = new PHomomorphismSim();
        dmScore = phom.dataMediation(workflowOPs, candidateOP, owlURI);
        return dmScore;

    } // calculatepHomDmScore

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
//
//    /**structure-based data mediation score for the candidate operation
//     * apply subtree homeomorphism to data mediation
//     * @param workflowOPs
//     * @param candidateOP
//     * @param owlFileName
//     * @return
//     */
//    public double calculateHomeoDmScore(List<WebServiceOpr> workflowOPs, WebServiceOpr candidateOP, String owlFileName) 
//    {
//        double dmScore;
//        TreeHomeomorphism homeo = new TreeHomeomorphism();
//        List<IODG> homeoResult = homeo.dm(workflowOPs, candidateOP, owlFileName);
//        dmScore = homeoResult.get(homeoResult.size() - 1).getScore();
//        //homeoDmResult = homeoResult;
//        return dmScore;
//    } // calculateHomeoDmScore

}
