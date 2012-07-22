
package parser;

import static java.lang.System.out;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Element;
import org.jdom.Namespace;

import util.WebServiceOpr;

/**
 * 
 * The class that supports methods to represent the XML input output as graph (matrix / adjacency list)
 * 
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file). 
 * 
 */
public class IOasGraph {
        
    private static Namespace xsdNS = Namespace.getNamespace("xsd", "http://www.w3.org/2001/XMLSchema");
    private static Namespace wsdlNS = Namespace.getNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");
    private Map<Integer,Element> GraphElementMap = new HashMap<Integer, Element>(); 
    private HashMap<Integer, ArrayList<Integer>> adjList = new HashMap<Integer, ArrayList<Integer>>();

    /**
     * Returns the List of Elements in the Input / output graph
     * @return AdjacencyList
     */    
    public  Map<Integer,Element> getGraphElementMap()
    {
        return GraphElementMap;
    }//getGraphElementList
    
    /**
     * Returns the Adjacency List Representation of the Input / output graph
     * @return AdjacencyList
     */
    public HashMap<Integer, ArrayList<Integer>>  getAdjList()
    {
        return adjList;
    }//getAdjList
    
    /**
     * The asGraph Method creates the Adjacency Matrix representation for an Input / Output of a Web service
     * 
     * @param op Web service Operation
     * @param ioType "input" returns Graph of input of the operation while "output" 
     * returns graph for output of the operation
     * @return The adjacency Matrix that represents the Input / Output
     */	
    public Boolean[][] asGraph(WebServiceOpr op, String ioType) {
            
            SawsdlParser sawsdlp = new SawsdlParser();
            SchemaParser sp1 = new SchemaParser();

            //Get the message Element
            Element MsgEle = null;
            if (ioType.equalsIgnoreCase("input"))
                MsgEle = sawsdlp.getInMsElem(op.getWsDescriptionDoc(), op.getOperationName());
            else if (ioType.equalsIgnoreCase("output"))
                MsgEle = sawsdlp.getOutMsElem(op.getWsDescriptionDoc(), op.getOperationName());
            
            if (MsgEle == null)
            {
               String log = "Msg Element not found for " + ioType + " of operation " 
                        + op.getOperationName() + " in the WSDL " + op.getWsDescriptionDoc();
                Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, log);
                return null;
            }
     
            List<Element> partList = MsgEle.getChildren("part", wsdlNS);
            if(partList.isEmpty())
            {
                out.println("Message Element is Empty / has no children");
            }
            else
            {
                if(partList.size() == 1)
                {
                    //Get Root Element for the Graph / IODAG
                    Element part = partList.get(0);
                    String t = part.getAttributeValue("element");
                    Element rootElem = sp1.getElementFromSchema(op.getWsDescriptionDoc(),t);
                    
                    if(rootElem == null)
                    {
                        String log = "Root Element not found for " + ioType + " of operation " 
                                   + op.getOperationName() + " in the WSDL " + op.getWsDescriptionDoc();
                        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, log);
                        return null;
                    }
                    
                    //Create Queue
                    Queue<Element> EleQ = new ArrayDeque<Element>();

                    //Add root element to Queue
                    EleQ.add(rootElem);

                    Integer x = -1, y = 0;
                    int[][] mat = new int[10][10];
                    
                    while(!EleQ.isEmpty())
                    {
                        ArrayList<Integer> temp = new ArrayList<Integer>();
                        Element e = EleQ.poll();
                        adjList.put(++x, new ArrayList<Integer>());
                        GraphElementMap.put(x, e);                        
                        
//                        out.print(e.getAttributeValue("name")+ x + "-> ");
                        
                        List<Element> children = sp1.getNextLevelElements(e);
                        if (children.isEmpty() || children == null)
                        {

                        }
                        
                        else
                        {
                            for (Iterator<Element> it = children.iterator(); it.hasNext();) 
                            {
                                Element xx = it.next();
                                EleQ.add(xx);
                                
                                temp.add(++y);
//                                out.print(y +", ");
                            }
                        }//else
                        
                        
//                        out.println();
                        adjList.put(x, temp);
                        
                    }//while
                
                    //Initialize Matrix to false
                    Boolean[][] adjMatrix = new Boolean[adjList.size()][adjList.size()];
                    for (Integer i = 0; i < adjList.size(); i++) 
                        for (Integer j = 0; j < adjList.size(); j++) 
                            adjMatrix[i][j] = false;

                    //Populate with values from Adjacency List
                    for (Integer i = 0; i < adjList.size(); i++) 
                        for (Integer m : adjList.get(i)) 
                            adjMatrix[i][m] = true;
                    
                    return adjMatrix;
                }//if
                else
                {
                    //There are more than one parts to the message
                    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE,"Case Not Handled : There are more than one parts to the message");
                    for (Element part:partList)
                    {
                    
                    }//for
                    return null;
                }//else
            }//else
            
            return null;
            
	}//asGraph
        
        
    public static void main (String[] args)
    {
        //Test Code
        WebServiceOpr op = new WebServiceOpr("protdistDefaultParameters", "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/wsPhylipProtDist.sawsdl");
        Boolean[][] adjMat = new IOasGraph().asGraph(op, "input");
        //pHomomorphism.Util.printMatches(null);
        
        
//        double[] mean = {0.3, 0.3, 0.3, 1};
//        double m = 3.0;
//        double sum = Util.generalizedMean(mean, m);
//        
//        out.println(sum);
        
    }//main
    

  
}//IOasGraph
