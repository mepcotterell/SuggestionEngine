
package test;

import java.util.ArrayList;
import java.util.List;
import suggest.calculator.DmScore;
import util.WebServiceOpr;

/**
 * 
 * Test Class for testing P-Homomorphism input-output Matching
 * 
 * @author Alok Dhamanaskar
 * @see LICENSE (MIT style license file). 
 * 
 */
public class TestpHomDataMediation {
    
    public static String wublast  = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/wublast.sawsdl";
    public static String clustalW = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/clustalw2.sawsdl";
    public static String filerSeq  = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/FilterSequencesWS.sawsdl";
    
    public static String ontology = "http://obi-webservice.googlecode.com/svn/trunk/ontology/webService.owl";//   
    
    public static void main (String[] args) {
            
        WebServiceOpr candidateop = new WebServiceOpr("run",clustalW);
        List<WebServiceOpr> workflowOps = new ArrayList<WebServiceOpr>();
        workflowOps.add(new WebServiceOpr("run", wublast));
        workflowOps.add(new WebServiceOpr("getResult", wublast));
        workflowOps.add(new WebServiceOpr("filterByEvalScore",filerSeq));
        
        DmScore dm = new DmScore();
        Double Score = dm.calculatepHomDmScore(workflowOps, candidateop, ontology);
        System.out.println("\n\nDMScore = " + Score + "\n\n");
        
    }// main ends
}