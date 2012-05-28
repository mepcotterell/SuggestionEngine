
package dataMediator;

import static java.lang.System.out;
import java.util.*;
import org.jdom.Element;
import org.jdom.Namespace;
import pHomomorphism.PHomMaxCardinality;
import pHomomorphism.PHomomorphism;
import parser.DmParser;
import parser.IOasGraph;
import parser.SawsdlParser;

import util.NodeType;
import util.WebServiceOpr;
import util.WebServiceOprScore_type;

/**
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file). 
 */
public class PHomomorphismSim {
    
    
    private static Namespace xsdNS = Namespace.getNamespace("http://www.w3.org/2001/XMLSchema");
    private static Namespace wsdlNS = Namespace.getNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");
    private static Namespace sawsdlNS = Namespace.getNamespace("sawsdl", "http://www.w3.org/ns/sawsdl");
    List<Element> Graph1 = new ArrayList<Element>();
    List<Element> Graph2 = new ArrayList<Element>();
    
    

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
        
        Boolean[][] G1 = null; //Will store Graph for Input of candidateOP
        Boolean[][] G2 = null; //Will store Graph for Output of last workflowOP
        double threshHold = 0.6;
        double[][] scoringMatrix;
        double[] w = null; // An array to store weights for all the nodes in Graph G1 of the input of candidateOP

                
        if(workflowOPs.isEmpty())
        {
            return dmscore;
        }
        else
        {
            //Last Workflow Op
            WebServiceOpr workflowOp = workflowOPs.get(workflowOPs.size()-1);
            IOasGraph input = new IOasGraph();
            G1 = input.asGraph(nextOP, "input");
            Graph1 = input.getGraphElementList();
            
            IOasGraph output = new IOasGraph();
            G2 = output.asGraph(workflowOp, "output");
            Graph2 = output.getGraphElementList();
            
            scoringMatrix = new double[Graph1.size()][Graph2.size()];
            
            for(int i=0; i< Graph1.size(); i++)
                for(int j=0; j< Graph2.size(); j++)
                    scoringMatrix[i][j] = PathRank.compare2node(Graph1.get(i), Graph2.get(j), owlURI, NodeType.NON_LEAF_NODE);
            
            PHomomorphism pHom =  new PHomMaxCardinality();
            dmscore = pHom.calculatepHomSimScore(G1, G2, scoringMatrix, threshHold, w);

        }
        
        return dmscore;
    } // dataMediation

   
    public static void main(String[] args)
    {
        //Test code
        PHomomorphismSim phom = new PHomomorphismSim();
    
    }


}
