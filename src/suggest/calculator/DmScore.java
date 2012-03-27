
package suggest.calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dataMediator.LeafMediation;
import dataMediator.PathRank;
import dataMediator.TreeHomeomorphism;

import util.IODG;
import util.OpWsdl;
import util.OpWsdlPathScore_type;

/**
 * @author Rui
 *
 */
public class DmScore {

    /**
     * path-based data mediation result for input of an candidateOP: map <inputPaths, matchedOutputPaths>
     */
    private Map<OpWsdlPathScore_type, OpWsdlPathScore_type> dmResults = null;
    
    /**
     * structure-based data mediation result
     * use subtree homeomorphism algorithm
     * for input of an candidateOP
     * is a list of IODG, which recorded matched node and score
     */
    private List<IODG> homeoDmResult = null;

    /**a list of IODG, which recorded matched node and score
     * @return the homeoDmResult
     */
    public List<IODG> getHomeoDmResult() 
    {
        return homeoDmResult;
    } // getHomeoDmResult

    /**to get a complete data mediation result
     * map <inputPaths, matchedOutputPaths>
     * inputPaths has every input path and its matching score, isRequired(1,0.6,0.2), isInput(0,1)
     * matchedOutputPaths has output paths match each input path, isSafeMath(0,1)
     * @return the dmResults
     */
    public Map<OpWsdlPathScore_type, OpWsdlPathScore_type> getDmResults() 
    {
        return dmResults;
    } // getDmResults

    /**structure-based data mediation score for the candidate operation
     * apply subtree homeomorphism to data mediation
     * @param workflowOPs
     * @param candidateOP
     * @param owlFileName
     * @return
     */
    public double calculateHomeoDmScore(List<OpWsdl> workflowOPs, OpWsdl candidateOP, String owlFileName) 
    {
        double dmScore = 0;
        TreeHomeomorphism homeo = new TreeHomeomorphism();
        List<IODG> homeoResult = homeo.dm(workflowOPs, candidateOP, owlFileName);
        dmScore = homeoResult.get(homeoResult.size() - 1).getScore();
        homeoDmResult = homeoResult;
        return dmScore;
    } // calculateHomeoDmScore

    /** path-based data mediation score for a candidate operation
     * @param workflowOPs    operations in current workflow, topologic order
     * @param candidateOP   one candidate operation
     * @param owlURI
     * @return score of data mediation for the candidate operation (how input of candidate operation will be fed)
     */
    public double calculatePathDmScore(List<OpWsdl> workflowOPs, OpWsdl candidateOP, String owlURI) 
    {

        double dmScore = 0;

        Map<OpWsdlPathScore_type, OpWsdlPathScore_type> dmOneopResult = PathRank.dataMediation(workflowOPs, candidateOP, owlURI);

        if (dmOneopResult != null) {

            dmResults = dmOneopResult;

            Set<OpWsdlPathScore_type> candidateOPpaths = dmOneopResult.keySet();

            boolean allUnknown = true;

            for (OpWsdlPathScore_type path : candidateOPpaths) {
                if (path.isRequired() != OpWsdlPathScore_type.unknown) {
                    allUnknown = false;
                    break;
                }
            }


            if (!allUnknown) {
                int nRequire = 0;
                int nOptional = 0;

                for (OpWsdlPathScore_type path : candidateOPpaths) {			//path.isRequired() will panel the score of the not required input path (0.1), or unknown (0.8)
                    if (path.isRequired() == 1) {
                        nRequire = nRequire + 1;
                    } else {
                        nOptional = nOptional + 1;
                    }

                    dmScore = dmScore + path.getScore() * path.isRequired();

                }

                //different weight for required input, optional, unknown, they actually are defined in class OpWsdlPathScore_type.
                dmScore = dmScore / ((OpWsdlPathScore_type.require * nRequire) + (OpWsdlPathScore_type.optional * nOptional));

            } else {
                for (OpWsdlPathScore_type path : candidateOPpaths) {
                    dmScore = dmScore + path.getScore();
                }
                //weight of every path is 1/n
                dmScore = dmScore / candidateOPpaths.size();
            }
        }

        return dmScore;

    } // calculatePathDmScore

    /**leaf-based data mediation score for a candidate operation
     * @param workflowOPs  operations in current workflow, topologic order
     * @param candidateOP   one candidate operation
     * @param owlFileName
     * @return                  score of leaf-based data mediation for the candidate operation
     */
    public double calculateLeafDmScore(List<OpWsdl> workflowOPs, OpWsdl candidateOP, String owlFileName) 
    {
        double dmScore = 0;
        LeafMediation lm = new LeafMediation();
        Map<OpWsdlPathScore_type, OpWsdlPathScore_type> dmOneopResult = lm.dm(workflowOPs, candidateOP, owlFileName);
        if (dmOneopResult != null) {
            dmResults = dmOneopResult;
            Set<OpWsdlPathScore_type> candidateOPpaths = dmOneopResult.keySet();

            for (OpWsdlPathScore_type path : candidateOPpaths) {

                dmScore = dmScore + path.getScore();
            }

            dmScore = dmScore / candidateOPpaths.size();
        }

        return dmScore;
        
    } // calculateLeafDmScore

    /**
     * @param args
     */
    public static void main(String[] args) {
        DmScore test = new DmScore();
        OpWsdl wublastOp = new OpWsdl("runWUBlast", "wsdl/8/WSWUBlast.wsdl");
        OpWsdl getidsOp = new OpWsdl("getIds", "wsdl/8/WSWUBlast.wsdl");
        List<OpWsdl> opList = new ArrayList<OpWsdl>();
        opList.add(wublastOp);

//		double score = test.calculateDmScore(opList,getidsOp,"owl/obi.owl");
//		Map<OpWsdlPathScore_type, OpWsdlPathScore_type> pathDm = test.getDmResults();
//		for (OpWsdlPathScore_type p: pathDm.keySet()){
//			OpWsdlPathScore_type match = pathDm.get(p);
//			System.out.println(p.getPath().get(0).getAttribute("name")+"--------------" + p.getScore()+"--------------"+ match.getPath().get(0).getAttribute("name"));
//			
//		}

//		double score = test.calculateHomeoDmScore(opList, getidsOp, "owl/obi.owl");
//		List<IODG> homeoDm = test.getHomeoDmResult();
//		System.out.println(score);
//		for (IODG node: homeoDm){
//		System.out.println(node.getNode().getAttributeValue("name")+"--------------" + node.getScore()+"--------------"+ node.getMatchNode().getAttributeValue("name"));
//		}

        double score = test.calculateLeafDmScore(opList, getidsOp, "owl/obi.owl");
        
        Map<OpWsdlPathScore_type, OpWsdlPathScore_type> pathDm = test.getDmResults();
        
        for (OpWsdlPathScore_type p : pathDm.keySet()) {
            OpWsdlPathScore_type match = pathDm.get(p);
        } // for

    }
}
