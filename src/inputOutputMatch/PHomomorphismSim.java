
package inputOutputMatch;

import java.util.*;
import org.jdom.Element;
import pHomomorphism.PHomMaxCardinality;
import pHomomorphism.PHomomorphism;
import parser.IOasGraph;
import util.DebuggingUtils;

import util.NodeType;
import util.WebServiceOpr;

/**
 * 
 * Provides methods for calculation of PHomomorphism Similarity Score
 * 
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file). 
 */
public class PHomomorphismSim {
    
    List<Element> G1ElementList = new ArrayList<Element>();
    HashMap<Integer, ArrayList<Integer>> adjList = new HashMap<Integer, ArrayList<Integer>>();
    Map<Integer,Element> GraphElementMap = new HashMap<Integer, Element>(); 
    List<Element> G2ElementList = new ArrayList<Element>();
    
    /** 
     * Returns the dmScore calculated using pHomomorphism Package
     * 
     * @param workflowOPs List of operations Currently in the Workflow
     * @param candidateOP The Candidate operation for which Data mediation sub-score has to be calculated
     * @param owlFileName The location of the Ontology file (Can be Relative location
     *                      in the system or a URI of the web)
     * @return a Data mediation Score
     */
    public double dataMediation
                    (List<WebServiceOpr> workflowOPs, WebServiceOpr nextOP, String owlURI) {
        
        double dmscore = 0.0;
        
        Boolean[][] G1Mat; //Will store Graph for Input of candidateOP (nextOP)
        Boolean[][] G2Mat; //Will store Graph for Output of last workflowOP
        double threshHold = 0.6;
        double[][] scoringMatrix;
        double[] w = null; // An array to store weights for all the nodes in Graph G1Mat of the input of candidateOP

                
        if(workflowOPs.isEmpty() || nextOP == null)
        {
            return dmscore;
        }//if
        else
        {
            //Last Workflow Op
            WebServiceOpr workflowOp = workflowOPs.get(workflowOPs.size()-1);
            
            IOasGraph input = new IOasGraph();
            G1Mat = input.asGraph(nextOP, "input");
            adjList = input.getAdjList();
            G1ElementList.addAll(input.getGraphElementMap().values());
            GraphElementMap = input.getGraphElementMap();
            w = new double[adjList.size()];
            
            //Weighting nodes of Graph 1                 
            //Scheme 1 : Weight of the Parent Node = Generalized mean of Weights of its Child Nodes
            for (Integer i = adjList.size()-1; i >= 0; i--)
            {
                if(adjList.get(i).isEmpty())
                {
                    Element e = GraphElementMap.get(i);
                    w[i] = isRequired(e);
                }
                else
                {
                    double[] dub = new double[adjList.get(i).size()];
                    int dubI = 0;
                    for(Integer c : adjList.get(i))
                    {
                        dub[dubI++] = w[c];
                    }
                    w[i] = DebuggingUtils.generalizedMean(dub, 2);
                }
            }//for
            
            //Scheme 2
//            w[0] = 1;
//            for(Integer i : adjList.keySet())
//            {
//                if(!adjList.get(i).isEmpty())
//                {
//                    for (Integer ii : adjList.get(i))
//                    {
//                        Element e = GraphElementMap.get(ii);
//                        w[ii] = w[i] * isRequired(e);
//                    
//                    }//inner for
//                }//if
//            
//            }//outer for
            
            IOasGraph output = new IOasGraph();
            G2Mat = output.asGraph(workflowOp, "output");
            G2ElementList.addAll(output.getGraphElementMap().values());
            
            scoringMatrix = new double[G1ElementList.size()][G2ElementList.size()];
            
            for(int i=0; i< G1ElementList.size(); i++)
                for(int j=0; j< G2ElementList.size(); j++)
                {
                    boolean leaf = true;
                    for(int x = 0; x < G1Mat[0].length; x++)
                    { 
                        if (G1Mat[i][x] == true)
                        {
                            leaf = false;
                            break;
                        }                   
                    }
                    if (leaf)
                        scoringMatrix[i][j] = PathRank.compare2node(G1ElementList.get(i), G2ElementList.get(j), owlURI, NodeType.LEAF_NODE);
                    else
                        scoringMatrix[i][j] = PathRank.compare2node(G1ElementList.get(i), G2ElementList.get(j), owlURI, NodeType.NON_LEAF_NODE);
                
                    //Global Inputs
                }


            
            PHomomorphism pHom =  new PHomMaxCardinality();
            dmscore = pHom.calculatepHomSimScore(G1Mat, G2Mat, scoringMatrix, threshHold, w);

        }//else
        
        return dmscore;
    } // dataMediation

   
    public static void main(String[] args)
    {
        //Test code
        PHomomorphismSim phom = new PHomomorphismSim();
    
    }
    
    /**
     * Method to check if a given element is required or not.
     * 
     * @param Element
     * @return 
     */
    public double isRequired(Element e) {
        
        double required = 1.0;
        double optional = 0.1;
        

            if (e.getAttribute("nillable") != null) 
            {
                //nillable is present
                boolean nillable = e.getAttributeValue("nillable").equals("true");            
                if (nillable) return optional;
                else 
                {
                    //nillable is false
                    if (e.getAttribute("minOccurs") != null) 
                    { 
                        // if minOccurs is present
                        if (e.getAttributeValue("minOccurs").equals("0")) 
                        {
                            //element is not nillable but minOccurs = 0 
                            return optional;
                        } 
                        else 
                        {
                            // if minOccurs > 0 and the element is not nillable
                            return required;
                        } // if
                    } 
                    else 
                    {
                        // nillable is false and if minOccurs is not present
                        return required;
                    }
                
                }//else
            }//if
            else
            {
                //nillable is not present
                if (e.getAttribute("minOccurs") != null) 
                { 
                    // if minOccurs is present
                    if (e.getAttributeValue("minOccurs").equals("0")) 
                    {
                        //element is not nillable but minOccurs = 0 
                        return optional;
                    } 
                    else 
                    {
                        // if minOccurs > 0 and the element is not nillable
                        return required;
                    } 
                } 
                else 
                {
                    // nillable is not present and if minOccurs is not present
                    return optional;
                } 
            
            }//else
    }//isRequired


    
}
