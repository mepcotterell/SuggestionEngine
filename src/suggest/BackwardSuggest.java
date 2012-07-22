package suggest;

import java.util.*;
import ontologyManager.OntologyManager;
import util.*;

/**
 * 
 * A class that provides method for suggestion of the previous Web service Operation(), 
 * by calculating sub scores for Data mediation, Functionality and Preconditions & effects score.
 * 
 * @author Alok DHamanaskar
 * @author Rui Wang
 * @see LICENSE (MIT style license file).
 * 
 * 
 */
public class BackwardSuggest {

    // Number of Maximum suggeststed operation
    //TODO: Currently unused, Ideally will have to be calculated by looking at distribution of scores
    private int maxSuggest = 5;

    // Default Weights for sub-scores, reweighted later
    private double weightDm = 0.3333333333333333333;
    private double weightFn = 0.3333333333333333333;
    private double weightPe = 0.3333333333333333333;

    //Path-based data mediation result for all candidateOPs: map[candidateOP, mapPath]
    //mapPath is a map for input of an candidateOP: map [path of the input of candidateOP, matched path in output&globalInput of workflow]
    //This should be read reverse in case it has to 
    private Map<WebServiceOpr, Map<WebServiceOprScore_type, WebServiceOprScore_type>> dmResults = new HashMap<WebServiceOpr, Map<WebServiceOprScore_type, WebServiceOprScore_type>>();


    /**
     * Returns the data matching result for all candidateOPs.
     *
     * @return the dmResults
     */
    public Map<WebServiceOpr, Map<WebServiceOprScore_type, WebServiceOprScore_type>> getDmResults() {
        return dmResults;
    }

    /**
     * The operation returns a list of Suggested Operations for the Previous step in the Workflow, 
     * given the operations that are currently in the Workflow and a list of candidate operations. 
     * Uses path-based data matching algorithms.
     * 
     * @param workflowOPs List of Web service Operations currently in the Workflow
     * @param candidateOPs List of Candidate operations to suggest the next operation from.
     * @param preferOp The desired Functionality entered by the user for the next step, 
     *                 can be keywords / IRI for concept in the ontology
     * @param owlURI The location of the Ontology file (Can be Relative location in the system 
     *               or a URI of the web)
     * @param initState Utilized for calculating Pre-conditions and effects subScore, currently unused
     * @return Returns list of OpWSDLScore that basically stores all the Subscores, for details see <code>util.WebServiceOprScore</code>
     */
    public List<WebServiceOprScore> suggestPrevServices(List<WebServiceOpr> workflowOPs,
            List<WebServiceOpr> candidateOPs, String preferOp, String owlURI, String initState,  List<String> globalInputs ) {

        if (preferOp != null) {
            if (preferOp.length() == 0) {
                preferOp = null;
            }
        }

        if (workflowOPs == null || candidateOPs == null) {
            return null;
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
        
        ForwardSuggest fwdSugg = new ForwardSuggest();
                
        // For each of the Candidate Operations
        for (WebServiceOpr op : candidateOPs) {
            double dmScore = 0;
            double fnScore = 0;
            double peScore = 0;
            double score = 0;
            
            List<WebServiceOpr> workflowForward = new ArrayList<WebServiceOpr>();
            workflowForward.add(op);

            //Uses the data-mediation from ForwardSuggest, hence the first workflow op becomes candidate op
            //and the current candidateOp becomes workflowOp so that everyhing else renains same
            //getDmScore(workflowOPs, op, owlURI);
            dmScore = fwdSugg.getDmScore(workflowForward, workflowOPs.get(workflowOPs.size()-1), owlURI, globalInputs);
            dmResults = fwdSugg.getDmResults();
            
            
            if (preferOp != null) {

                fnScore = fwdSugg.getFnScore(preferOp, op, owlURI);
            }

            score = this.weightDm * dmScore + this.weightFn * fnScore
                    + this.weightPe * peScore;

            WebServiceOprScore opScore = new WebServiceOprScore(op.getOperationName(), op.getWsDescriptionDoc(), score);
            opScore.setDmScore(dmScore);
            opScore.setFnScore(fnScore);
            opScore.setPeScore(peScore);
            opScore.setExtraInfo(op.getExtraInfo());

            //----------------------------------------------------------------
            // Finding the matched path, adjusted accordingly for BackwardSuggest
            Map<WebServiceOprScore_type, WebServiceOprScore_type> matchedPaths = dmResults.get( workflowOPs.get(workflowOPs.size()-1));
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

            suggestionList.add(opScore);

        }// For ends
        Collections.sort(suggestionList, Collections.reverseOrder());

        return suggestionList;
    }


    /**
     * The operation returns a list of Suggested Operations for the Previous step in the Workflow, 
     * given the operations that are currently in the Workflow and a list of candidate operations. 
     * Uses pHomomorphism data matching algorithms.
     * 
     * @param workflowOPs List of Web service Operations currently in the Workflow
     * @param candidateOPs List of Candidate operations to suggest the next operation from.
     * @param preferOp The desired Functionality entered by the user for the next step, 
     *                 can be keywords / IRI for concept in the ontology
     * @param owlURI The location of the Ontology file (Can be Relative location in the system 
     *               or a URI of the web)
     * @param initState Utilized for calculating Pre-conditions and effects subScore, currently unused
     * @return Returns list of OpWSDLScore that basically stores all the Subscores, for details see <code>util.WebServiceOprScore</code>
     */    
    public List<WebServiceOprScore> suggestPrevServicespHom
            (List<WebServiceOpr> workflowOPs, List<WebServiceOpr> candidateOPs, String preferOp, String owlURI, String initState)
    {

        if (preferOp != null) {
            if (preferOp.length() == 0) {
                preferOp = null;
            }
        }

        if (workflowOPs == null || candidateOPs == null) {
            return null;
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
        
        ForwardSuggest fwdSugg = new ForwardSuggest();
                
        // For each of the Candidate Operations
        for (WebServiceOpr op : candidateOPs) {
            double dmScore = 0;
            double fnScore = 0;
            double peScore = 0;
            double score = 0;
            
            List<WebServiceOpr> workflowForward = new ArrayList<WebServiceOpr>();
            workflowForward.add(op);

            //Uses the data-mediation from ForwardSuggest, hence the first workflow op becomes candidate op
            //and the current candidateOp becomes workflowOp so that everyhing else renains same
            //getDmScore(workflowOPs, op, owlURI);
            dmScore = fwdSugg.getDmScorepHom(workflowForward, workflowOPs.get(workflowOPs.size()-1), owlURI);
            
            if (preferOp != null) {

                fnScore = fwdSugg.getFnScore(preferOp, op, owlURI);
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
            i++;
            suggestionList20.add(s);
            if (i > 19) break;
        }
        
        return suggestionList;
    }
    
    public static void main(String[] args) {
        //Test code
        //For Testing this Use test.TestBkwdSuggest class
    }
}
