package suggest;

import java.util.*;
import ontologyManager.OntologyManager;
import suggest.calculator.DmScore;
import suggest.calculator.FnScore;
import suggest.calculator.PeScore;
import util.*;

/**
 * 
 * A class that provides method for suggestion of the next Web service Operation(), 
 * by calculating sub scores for Data mediation, Functionality and Preconditions & effects score 
 * 
 * @author Rui Wang
 * @author Alok DHamanaskar
 * @see LICENSE (MIT style license file).
 * 
 */
public class ForwardSuggest {

    // Number of Maximum suggeststed operation
    //TODO: Currently unused, Ideally will have to be calculated by looking at distribution of scores
    private int maxSuggest = 5;

    // Default Weights for sub-scores, reweighted later
    private double weightDm = 0.3333333333333333333;
    private double weightFn = 0.3333333333333333333;
    private double weightPe = 0.3333333333333333333;

    //Path-based data mediation result for all candidateOPs: map[candidateOP, mapPath]
    //mapPath is a map for input of an candidateOP: map [path of the input of candidateOP, matched path in output&globalInput of workflow]
    private Map<WebServiceOpr, Map<WebServiceOprScore_type, WebServiceOprScore_type>> dmResults = new HashMap<WebServiceOpr, Map<WebServiceOprScore_type, WebServiceOprScore_type>>();

    //Results for StructureBased Data mediation
    private Map<WebServiceOpr, List<IODG>> homeoDmResults = new HashMap<WebServiceOpr, List<IODG>>();

    /**
     * get a complete structure-based data mediation results for all
     * candidateOPs subtree homeomorphism map[candidateOP, list of input nodes]
     * the input nodes record the matched node and score
     *
     * @return the homeoDmResults
     */
    public Map<WebServiceOpr, List<IODG>> getHomeoDmResults() {
        return homeoDmResults;
    }

    /**
     * Returns the data matching result for all candidateOPs.
     *
     * @return the dmResults
     */
    public Map<WebServiceOpr, Map<WebServiceOprScore_type, WebServiceOprScore_type>> getDmResults() {
        return dmResults;
    }

    /**
     * Calculate and return functionality sub-score (FnScore) depending upon how well the desired
     * functionality entered by the user aligns with the functionality of candidate operation
     *
     * @param preferOp The desired Functionality entered by the user for the
     *                 next step, can be keywords / IRI for concept in the ontology
     * @param op The candidate Web service operation for which the functionality sub-score has to be calculated
     * @param owlFileName The location of the Ontology file (Can be Relative location in the system or a URI of the web)
     * @return the functionality sub-score as double value
     */
    double getFnScore(String preferOp, WebServiceOpr op, String owlFileName) {
        double fnScore = 0;
        if (preferOp != null && op != null) {
            FnScore fs = new FnScore();
            fnScore = fs.calculateFnScore(preferOp, op, owlFileName);
        }
        return fnScore;
    }//getFnScore

    /**
     * Calculate and returns DataMediation sub-score for "A" candidate operation considering ALL the 
     * operations currently for the Workflow, Can use Path Based, Leaf Based or Structure based Data Mediation Algorithms.
     * 
     * It is currently using Path based Data mediation Algorithm. Might have to be modified for Backward / Bidirectional Suggestions
     *
     * @param workflowOPs List of operations Currently in the Workflow
     * @param candidateOP The Candidate operation for which Data mediation sub-score has to be calculated
     * @param owlFileName The location of the Ontology file (Can be Relative location
     * in the system or a URI of the web)
     * @return DataMediation Sub-score
     */
    double getDmScore(List<WebServiceOpr> workflowOPs, WebServiceOpr candidateOP, String owlFileName, List <String> globalIps) {
        
        double dmScore = 0;
 
       if (workflowOPs != null && candidateOP != null) {
            DmScore ds = new DmScore();

            //path-based data mediation
            dmScore = ds.calculatePathDmScore(workflowOPs, candidateOP, owlFileName, globalIps);
            dmResults.put(candidateOP, ds.getDmResults());
               
            //structure-based data mediation(subtree homeomorphism)
            //ds.calculateHomeoDmScore(workflowOPs, candidateOP, owlFileName);
            //homeoDmResults.put(candidateOP, ds.getHomeoDmResult());

            //leaf-based data mediation
            //dmScore = ds.calculateLeafDmScore(workflowOPs, candidateOP, owlFileName);
            //dmResults.put(candidateOP, ds.getDmResults());
        }
        return dmScore;
    }
    
    /**
     * Calculate and returns DataMediation sub-score for "A" candidate operation considering ALL the 
     * operations currently for the Workflow, p-Homomorphism mapping algorithm.
     * 
     * @param workflowOPs List of operations Currently in the Workflow
     * @param candidateOP The Candidate operation for which Data mediation sub-score has to be calculated
     * @param owlFileName The location of the Ontology file (Can be Relative location
     * in the system or a URI of the web)
     * @return DataMediation Sub-score
     */
    double getDmScorepHom(List<WebServiceOpr> workflowOPs, WebServiceOpr candidateOP, String owlFileName) {
        
        double dmScore = 0;
 
       if (workflowOPs != null && candidateOP != null) {
            DmScore ds = new DmScore();

            //p-Homomorphism based DM
            dmScore = ds.calculatepHomDmScore(workflowOPs, candidateOP, owlFileName);
            //dmResults.put(candidateOP, ds.getDmResults());
        }
        return dmScore;
    }
    
    /**
     * Calculates and returns PreConditions and effects sub-score. Currently not used / supported by SSE
     *
     * @param initState initial state file name, can be null
     * @param workflowOPs all operations in current Workflow, allow some
     * operations have no effect
     * @param candidateOP candidate operation, if no precondition, score=0
     * @return
     */
    private double getPeScore(String initState, List<WebServiceOpr> workflowOPs, WebServiceOpr candidateOP) {
        double peScore = 0;

        if (workflowOPs != null && candidateOP != null) {
            PeScore ps = new PeScore();
            peScore = ps.calculatePeScore(initState, workflowOPs, candidateOP);
        }

        return peScore;
    }

    /**
     * The operation returns a list of Suggested Operations for the NEXT step in the Workflow, 
     * given the operations that are currently in the Workflow and a list of candidate operations. 
     * 
     * The suggested operations are ranked based of the DataMediation and Functionality Subscores. 
     * The current version of ForwardSuggest.suggestNextService() is using Pathbased-DataMediation to
     * calculate DataMediation Subscore, also PreConditions and Effects is currently not accounted for.
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
    public List<WebServiceOprScore> suggestNextService(List<WebServiceOpr> workflowOPs,
            List<WebServiceOpr> candidateOPs, String preferOp, String owlURI, String initState) 
    {
        return suggestNextServiceGlobalIps(workflowOPs, candidateOPs, preferOp, owlURI, initState, new ArrayList<String>());
        
    }//suggestNextService

    
        public List<WebServiceOprScore> suggestNextServiceGlobalIps(List<WebServiceOpr> workflowOPs,
            List<WebServiceOpr> candidateOPs, String preferOp, String owlURI, String initState, List <String> globalIps) {
        
        //code for  {CandidateOps} - {workflowOps}
        List<WebServiceOpr> FilteredcandidateOPs = new ArrayList<WebServiceOpr>(candidateOPs);
        for (WebServiceOpr c : candidateOPs)
            for (WebServiceOpr w : workflowOPs)
            {
                if (w.getOperationName().equalsIgnoreCase(c.getOperationName()) && w.getWsDescriptionDoc().equalsIgnoreCase(c.getWsDescriptionDoc())) 
                {
                    FilteredcandidateOPs.remove(c);
                }
            }//for
        //-------------------------------------------------------

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
            weightDm = 0.7;
            weightFn = 0.3;
            weightPe = 0;
        }

        // The list of suggested operations to be sorted and returned
        List<WebServiceOprScore> suggestionList = new ArrayList<WebServiceOprScore>();

        // For each of the Candidate Operations
        for (WebServiceOpr op : FilteredcandidateOPs) {
            // datamediation score
            double dmScore = 0;
            // functionality score
            double fnScore = 0;
            // pre-condition and effect score
            double peScore = 0;
            // final score
            double score = 0;

            dmScore = this.getDmScore(workflowOPs, op, owlURI, globalIps);
            
            if (preferOp != null) {
                fnScore = this.getFnScore(preferOp, op, owlURI);
            }

            // Uncomment when accounting for preconditions and effects
            //if (initState != null) 
            //    peScore = this.getPeScore(initState, workflowOPs, op);

            score = this.weightDm * dmScore + this.weightFn * fnScore
                    + this.weightPe * peScore;

            WebServiceOprScore opScore = new WebServiceOprScore(op.getOperationName(), op.getWsDescriptionDoc(), score);
            opScore.setDmScore(dmScore);
            opScore.setFnScore(fnScore);
            opScore.setPeScore(peScore);
            opScore.setExtraInfo(op.getExtraInfo());

            //----------------------------------------------------------------
            // Finding the matched path
            Map<WebServiceOprScore_type, WebServiceOprScore_type> matchedPaths = dmResults.get(op);
            Set<WebServiceOprScore_type> ipPaths = matchedPaths.keySet();
                    
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
     * The operation returns a list of Suggested Operations for the NEXT step in the Workflow, 
     * given the operations that are currently in the Workflow and a list of candidate operations. 
     * 
     * DM Subscores is calculate using pHomomorphism Mapping
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
    
    public List<WebServiceOprScore> suggestNextServicepHom(List<WebServiceOpr> workflowOPs,
            List<WebServiceOpr> candidateOPs, String preferOp, String owlURI, String initState) 
    {

        if (preferOp != null) {
            if (preferOp.length() == 0) {
                preferOp = null;
            }
        }

        if (workflowOPs == null || candidateOPs == null) {
            return null;
        }
                
        //code for  {CandidateOps} - {workflowOps}
        List<WebServiceOpr> FilteredcandidateOPs = new ArrayList<WebServiceOpr>(candidateOPs);
        for (WebServiceOpr c : candidateOPs)
            for (WebServiceOpr w : workflowOPs)
            {
                if (w.getOperationName().equalsIgnoreCase(c.getOperationName()) && w.getWsDescriptionDoc().equalsIgnoreCase(c.getWsDescriptionDoc())) 
                {
                    FilteredcandidateOPs.remove(c);
                }
            }//for
        //-------------------------------------------------------

        
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

        List<WebServiceOprScore> suggestionList = new ArrayList<WebServiceOprScore>();

        // For each of the Candidate Operations
        for (WebServiceOpr op : candidateOPs) {
            double dmScore = 0;
            double fnScore = 0;
            double peScore = 0;
            double score = 0;

            dmScore = this.getDmScorepHom(workflowOPs, op, owlURI);
            
            if (preferOp != null) {
                fnScore = this.getFnScore(preferOp, op, owlURI);
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
    }//suggestNextServicepHom
    
    public static void main(String[] args) {
        //Test code
        //For Testing this Use test.TestFwdSuggest 
    }
}
