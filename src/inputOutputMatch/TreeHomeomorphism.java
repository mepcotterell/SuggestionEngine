
package inputOutputMatch;

import util.NodeType;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import ontologySimilarity.Hungarian;
import parser.MsIODGparser;
import parser.SawsdlParser;

import util.IODG;
import util.WebServiceOpr;

import org.semanticweb.owlapi.model.*;

/**
 * 
 * The class is currently not used by SSE. 
 * 
 * @author Rui Wang
 *
 */
public class TreeHomeomorphism {

    private static double negativeUnlimit = -100;
    //how to decide penalty for deleting v (node in text)? here set small, lean to allow deleting node
    private static double penalty = -0.001;
    /**
     * all the matching score between all nodes of pattern and text tree
     */
    private double[][] rscores;

    /**
     * @return the rscores
     */
    public double[][] getRscores() {
        return rscores;
    }

    /**compute the RScore between given nodes(IODG) v and u
     * @param v
     * @param u
     * @param owlURI
     * @return
     */
    private double[] computeRscore(IODG v, IODG u, String owlURI) {
        double[] indexScore = new double[2];
        int k;
        int l;
        if (u.getLevel() == 1) {
            k = 0;
        } else {
            k = u.getChildren().size();
        }
        if (v.getLevel() == 1) {
            l = 0;
        } else {
            l = v.getChildren().size();
        }

        double assignScore;
        double bestvChildscore = 0;
        if (k > l) {
            assignScore = negativeUnlimit;
        } else if (k == 0) {
            assignScore = 0;

        } else {
            List<IODG> uChildren = u.getChildren();
            List<IODG> vChildren = v.getChildren();
            double[][] matrix = new double[l][k];
            for (int i = 0; i < l; i++) {
                for (int j = 0; j < k; j++) {
                    matrix[i][j] = rscores[vChildren.get(i).getDfsVisit()][uChildren.get(j).getDfsVisit()];
                }

            }

            //make score range [0,1], so divide k
            assignScore = Hungarian.hungarian(matrix) / k;
        }

        List<IODG> vChildren = v.getChildren();
        for (int i = 0; i < l; i++) {
            double vchild2uscore = rscores[vChildren.get(i).getDfsVisit()][u.getDfsVisit()];
            if (bestvChildscore < vchild2uscore) {
                bestvChildscore = vchild2uscore;
                indexScore[0] = vChildren.get(i).getDfsVisit();
            }
        }

        PathRank pk = new PathRank();
        //make score range [0,1], so divide 2
        double choice1 = (assignScore + pk.compare2node(v.getNode(), u.getNode(), owlURI, NodeType.NON_LEAF_NODE)) / 2.0;
        double choice2 = bestvChildscore + penalty;
        if (choice1 > choice2) {
            indexScore[0] = v.getDfsVisit();
            indexScore[1] = choice1;
        } else {
            indexScore[1] = choice2;
        }
        return indexScore;
    }

    /**given pattern tree and text tree all in postorder node list, 
     * return the pattern node list with matching node and score recorded
     * @param postorderText
     * @param postorderPattern
     * @param owlURI
     * @return
     */
    public List<IODG> findHomeo(List<IODG> postorderText, List<IODG> postorderPattern, String owlURI) {

        rscores = new double[postorderText.size()][postorderPattern.size()];

        for (int i = 0; i < postorderPattern.size(); i++) {
            IODG u = postorderPattern.get(i);
            //mark the index to the node
            u.setDfsVisit(i);
            double bestScore4u = negativeUnlimit;
            int bestIndex4u = -1;
            for (int j = 0; j < postorderText.size(); j++) {
                double[] indexScore = new double[2];
                indexScore[0] = j;
                IODG v = postorderText.get(j);
                //mark the index to the node
                v.setDfsVisit(j);
                if (u.getLevel() == 1) {
                    if (v.getLevel() == 1) {
                        PathRank pk = new PathRank();
                        rscores[j][i] = pk.compare2node(v.getNode(), u.getNode(), owlURI, NodeType.NON_LEAF_NODE);
                    } else {
                        indexScore = this.computeRscore(v, u, owlURI);
                        rscores[j][i] = indexScore[1];
                    }
                } else {
                    if (u.getLevel() > v.getLevel()) {
                        //-100 negative unlimited
                        rscores[j][i] = negativeUnlimit;
                    } else {
                        indexScore = this.computeRscore(v, u, owlURI);
                        rscores[j][i] = indexScore[1];
                    }
                }
                if (bestScore4u <= rscores[j][i]) {
                    bestScore4u = rscores[j][i];
                    bestIndex4u = (int) indexScore[0];
                }
            }

            //record best match node and score for every pattern node
            u.setScore(bestScore4u);
            u.setMatchNode(postorderText.get(bestIndex4u).getNode());
        }
//		double [][] scores = new double[postorderText.size()][postorderPattern.size()];

        return postorderPattern;

    }

    /**given workflow operations in topological order and candidate operation
     * invoke subtree homeomorphism data mediation
     * return the list of all input nodes of candidate operation, which have matched node and score recorded
     * 
     * @param workflowOPs
     * @param candidateOP
     * @param owlURI
     * @return
     */
    public List<IODG> dm(List<WebServiceOpr> workflowOPs, WebServiceOpr candidateOP, String owlURI) {
        
        SawsdlParser sp = new SawsdlParser();
        Element inElem = sp.getInMsElem(candidateOP.getWsDescriptionDoc(), candidateOP.getOperationName());
        
        MsIODGparser mp = new MsIODGparser();
        List<IODG> postorderInNodes = new ArrayList<IODG>();
        
        postorderInNodes.addAll(mp.getMsPostorderNodeList(inElem, candidateOP));
        Element globalInElem = sp.getInMsElem(workflowOPs.get(0).getWsDescriptionDoc(), workflowOPs.get(0).getOperationName());
        List<IODG> postorderOutNodes = new ArrayList<IODG>();
        postorderOutNodes.addAll(mp.getMsPostorderNodeList(globalInElem, workflowOPs.get(0)));
        for (WebServiceOpr op : workflowOPs) {
            Element tempOutElem = sp.getOutMsElem(op.getWsDescriptionDoc(), op.getOperationName());
            postorderOutNodes.addAll(mp.getMsPostorderNodeList(tempOutElem, op));

        }
        List<IODG> result = this.findHomeo(postorderOutNodes, postorderInNodes, owlURI);
        return result;
    }

    /**constructor
     * 
     */
    public TreeHomeomorphism() {
    }

    /**for test
     * @param args
     */
    public static void main(String[] args) {
    }
}
