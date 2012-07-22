package suggest;

import java.util.*;
import ontologyManager.OntologyManager;
import util.MatchedIOPaths;
import util.WebServiceOpr;
import util.WebServiceOprScore;
import util.WebServiceOprScore_type;

/**
 * 
 * A class that provides methods for suggestion of an intermediate Web service Operation(), 
 * by calculating sub scores for Data matching, Functionality and Preconditions & effects score.
 * 
 * @author Alok Dhamanaskar
 * @author Rui Wang
 * @see LICENSE (MIT style license file). 
 *
 */
public class BidirectionSuggest {
    private int maxSuggest = 5;
    // Default Weights for sub-scores, reweighted later
    private double weightDm = 0.3333333333333333333;
    private double weightFn = 0.3333333333333333333;
    private double weightPe = 0.3333333333333333333;


    //data mapping detail between prefix--candidate---suffix
    private Map<WebServiceOpr, Map<WebServiceOprScore_type, WebServiceOprScore_type>> prefixDmResults;
    private Map<WebServiceOpr, Map<WebServiceOprScore_type, WebServiceOprScore_type>> suffixDmResults;

    /**
     * Returns the prefix data matching result for all candidateOPs.
     *
     * @return the dmResults
     */
    public Map<WebServiceOpr, Map<WebServiceOprScore_type, WebServiceOprScore_type>> getPrefixDmResults() {
        return prefixDmResults;
    }

    /**
     * Returns the suffix data matching result for all candidateOPs.
     *
     * @return the dmResults
     */
    public Map<WebServiceOpr, Map<WebServiceOprScore_type, WebServiceOprScore_type>> getSuffixDmResults() {
        return suffixDmResults;
    }

    /**
     * 
     * The operation returns a list of Suggested Operations for an intermediate step in the Workflow, 
     * given the operations that are currently in the Workflow (a pre fix list and a Suffix list) and 
     * a list of candidate operations. Uses path-based input-output matching algorithm
     *
     * @param workflowPrefixOPs List of prefix Web service Operations currently in the Workflow
     * @param workflowSuffixOPs List of postfix Web service Operations currently in the Workflow
     * @param candidateOPs List of Candidate operations to suggest the next operation from.
     * @param preferOp The desired Functionality entered by the user for the next step, 
     *                 can be keywords / IRI for concept in the ontology
     * @param owlURI The location of the Ontology file (Can be Relative location in the system 
     *               or a URI of the web)
     * @param initState Utilized for calculating Pre-conditions and effects subScore, currently unused
     * @return Returns list of OpWSDLScore that basically stores all the Subscores, for details see <code>util.WebServiceOprScore</code>
     */
    public List<WebServiceOprScore> suggestServices(List<WebServiceOpr> workflowPrefixOPs, List<WebServiceOpr> workflowSuffixOPs,
            List<WebServiceOpr> candidateOPs, String preferOp, String owlURI, String initState) {

        if (workflowPrefixOPs == null || workflowSuffixOPs == null || candidateOPs == null) {
            return null;
        }
        if (preferOp != null) {
            if (preferOp.length() == 0) {
                preferOp = null;
            }
        }

        OntologyManager instance = OntologyManager.getInstance(owlURI);

        //Adjusting weight, if Pre-Conditions and Effect is considered, they have to be re-weighted
        if (preferOp == null) {
            weightDm = 1;
            weightPe = 0;
            weightFn = 0;
        } else {
            weightDm = 0.5;
            weightFn = 0.5;
            weightPe = 0;
        }

        // The list of suggested operations to be sorted and returned
        List<WebServiceOprScore> suggestionList = new ArrayList<WebServiceOprScore>();

        ForwardSuggest fwdSugg1 = new ForwardSuggest();

        // For each of the Candidate Operations
        for (WebServiceOpr op : candidateOPs) {
            double dmScore = 0;
            double dmScoreFwd = 0;
            double dmScoreBck = 0;
            double fnScore = 0;
            double peScore = 0;
            double score = 0;

            List<WebServiceOpr> workflowForward = new ArrayList<WebServiceOpr>();
            workflowForward.add(op);

            //Uses the data-mediation from ForwardSuggest, hence the first workflow op becomes candidate op
            //and the current candidateOp becomes workflowOp so that everyhing else renains same
            //getDmScore(workflowOPs, op, owlURI);
            dmScoreBck = fwdSugg1.getDmScore(workflowForward, workflowSuffixOPs.get(0), owlURI, new ArrayList<String>());
            suffixDmResults = fwdSugg1.getDmResults();
            
            ForwardSuggest fwdSugg2 = new ForwardSuggest();
            dmScoreFwd = fwdSugg2.getDmScore(workflowPrefixOPs, op, owlURI, new ArrayList<String>());
            prefixDmResults = fwdSugg2.getDmResults();
            
            dmScore = (dmScoreBck + dmScoreFwd) / 2;

            if (preferOp != null) {
                fnScore = fwdSugg1.getFnScore(preferOp, op, owlURI);
            }

            score = this.weightDm * dmScore + this.weightFn * fnScore
                    + this.weightPe * peScore;

            WebServiceOprScore opScore = new WebServiceOprScore(op.getOperationName(), op.getWsDescriptionDoc(), score);
            opScore.setDmScore(dmScore);
            opScore.setFnScore(fnScore);
            opScore.setPeScore(peScore);
            opScore.setExtraInfo(op.getExtraInfo());
            //----------------------------------------------------------------
            // Finding the matched path for Outputs of Candidateop
            Map<WebServiceOprScore_type, WebServiceOprScore_type> matchedPaths = suffixDmResults.get(workflowSuffixOPs.get(0));
            Set<WebServiceOprScore_type> ipPaths = matchedPaths.keySet();
                    
            MatchedIOPaths mps = new MatchedIOPaths();

            for (WebServiceOprScore_type a : ipPaths) {
                
                mps.setMatchedWsOpr(matchedPaths.get(a).getOperationName());
                mps.setMatchedoprWsDoc(matchedPaths.get(a).getWsDescriptionDoc());
                mps.setWsoOpr(a.getOperationName());
                mps.setWsDoc(a.getWsDescriptionDoc());
                mps.addMatchedPaths(
                        matchedPaths.get(a).getPath().get(0).getAttributeValue("name"), 
                        a.getPath().get(0).getAttributeValue("name"), 
                        a.getScore()
                        );
            }
            mps.sort();
            opScore.setMatchedPathsOp(mps);

            
            //----------------------------------------------------------------
            // Finding the matched path for Inputs of Candidate OP
            matchedPaths.clear();
            matchedPaths = prefixDmResults.get(op);
            ipPaths.clear();
            ipPaths = matchedPaths.keySet();
                    
            MatchedIOPaths mpsIn = new MatchedIOPaths();

            for (WebServiceOprScore_type a : ipPaths) {
                
                mpsIn.setMatchedWsOpr(a.getOperationName());
                mpsIn.setMatchedoprWsDoc(a.getWsDescriptionDoc());
                mpsIn.setWsoOpr(matchedPaths.get(a).getOperationName());
                mpsIn.setWsDoc(matchedPaths.get(a).getWsDescriptionDoc());
                mpsIn.addMatchedPaths(
                        a.getPath().get(0).getAttributeValue("name"), 
                        matchedPaths.get(a).getPath().get(0).getAttributeValue("name"), 
                        a.getScore()
                        );
            }
            mpsIn.sort();
            opScore.setMatchedPathsIp(mpsIn);
            
            
            suggestionList.add(opScore);

        }// For ends				

        Collections.sort(suggestionList, Collections.reverseOrder());
        return suggestionList;
    }
    
    /**
     * 
     * The operation returns a list of Suggested Operations for an intermediate step in the Workflow, 
     * given the operations that are currently in the Workflow (a pre fix list and a Suffix list) and 
     * a list of candidate operations. Uses p-homomorphism input-output matching algorithm
     *
     * @param workflowPrefixOPs List of prefix Web service Operations currently in the Workflow
     * @param workflowSuffixOPs List of postfix Web service Operations currently in the Workflow
     * @param candidateOPs List of Candidate operations to suggest the next operation from.
     * @param preferOp The desired Functionality entered by the user for the next step, 
     *                 can be keywords / IRI for concept in the ontology
     * @param owlURI The location of the Ontology file (Can be Relative location in the system 
     *               or a URI of the web)
     * @param initState Utilized for calculating Pre-conditions and effects subScore, currently unused
     * @return Returns list of OpWSDLScore that basically stores all the Subscores, for details see <code>util.WebServiceOprScore</code>
     */
    public List<WebServiceOprScore> suggestServicespHom(List<WebServiceOpr> workflowPrefixOPs, List<WebServiceOpr> workflowSuffixOPs,
            List<WebServiceOpr> candidateOPs, String preferOp, String owlURI, String initState) {

        if (workflowPrefixOPs == null || workflowSuffixOPs == null || candidateOPs == null) {
            return null;
        }
        if (preferOp != null) {
            if (preferOp.length() == 0) {
                preferOp = null;
            }
        }

        OntologyManager instance = OntologyManager.getInstance(owlURI);

        //Adjusting weight, if Pre-Conditions and Effect is considered, they have to be re-weighted
        if (preferOp == null) {
            weightDm = 1;
            weightPe = 0;
            weightFn = 0;
        } else {
            weightDm = 0.5;
            weightFn = 0.5;
            weightPe = 0;
        }

        // The list of suggested operations to be sorted and returned
        List<WebServiceOprScore> suggestionList = new ArrayList<WebServiceOprScore>();

        ForwardSuggest fwdSugg1 = new ForwardSuggest();

        // For each of the Candidate Operations
        for (WebServiceOpr op : candidateOPs) {
            double dmScore = 0;
            double dmScoreFwd = 0;
            double dmScoreBck = 0;
            double fnScore = 0;
            double peScore = 0;
            double score = 0;

            List<WebServiceOpr> workflowForward = new ArrayList<WebServiceOpr>();
            workflowForward.add(op);

            //Uses the data-mediation from ForwardSuggest, hence the first workflow op becomes candidate op
            //and the current candidateOp becomes workflowOp so that everyhing else renains same
            //getDmScore(workflowOPs, op, owlURI);
            dmScoreBck = fwdSugg1.getDmScorepHom(workflowForward, workflowSuffixOPs.get(0), owlURI);
            
            ForwardSuggest fwdSugg2 = new ForwardSuggest();
            dmScoreFwd = fwdSugg2.getDmScorepHom(workflowPrefixOPs, op, owlURI);
            
            dmScore = (dmScoreBck + dmScoreFwd) / 2;

            if (preferOp != null) {
                fnScore = fwdSugg1.getFnScore(preferOp, op, owlURI);
            }

            score = this.weightDm * dmScore + this.weightFn * fnScore
                    + this.weightPe * peScore;

            WebServiceOprScore opScore = new WebServiceOprScore(op.getOperationName(), op.getWsDescriptionDoc(), score);
            opScore.setDmScore(dmScore);
            opScore.setFnScore(fnScore);
            opScore.setPeScore(peScore);
            opScore.setExtraInfo(op.getExtraInfo());
            
            
            suggestionList.add(opScore);

        }// For ends				

        Collections.sort(suggestionList, Collections.reverseOrder());
        
         List<WebServiceOprScore> suggestionList20 = new ArrayList<WebServiceOprScore>();
         int i = 0;
        for (WebServiceOprScore s : suggestionList)
        {
            suggestionList20.add(s);
            if (i > 19) break;
        }
        
        return suggestionList;
    }

    public static void main(String[] args) {
        //Test code
        //For Testing this Use test.TestBidrSuggest 
    }
}
