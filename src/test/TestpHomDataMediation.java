
package test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import suggest.calculator.DmScore;
import util.DebuggingUtils;
import util.WebServiceOpr;
import util.WebServiceOprScore_type;


/**
 *
 * @author Alok Dhamanaskar
 * Test Class for testing Path based DataMediation
 */
public class TestpHomDataMediation {
    
    public static String wublast  = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/wublast.sawsdl";
    public static String clustalW = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/clustalw2.sawsdl";
   public static String filerSeq  = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/FilterSequencesWS.sawsdl";
    
    public static String ontology = "owl/webService.owl";//"http://obi-webservice.googlecode.com/svn/trunk/ontology/webService.owl";//   
    
    public static void main (String[] args) {
            
        WebServiceOpr candidateop = new WebServiceOpr("run",clustalW);
        List<WebServiceOpr> workflowOps = new ArrayList<WebServiceOpr>();
        workflowOps.add(new WebServiceOpr("run", wublast));
        workflowOps.add(new WebServiceOpr("getResult", wublast));
        workflowOps.add(new WebServiceOpr("filterByEval",filerSeq));
        
        DmScore dm = new DmScore();
        Double Score = dm.calculatepHomDmScore(workflowOps, candidateop, ontology);
        System.out.println("\n\nDMScore = " + Score + "\n\n");
        
        Score = dm.calculatePathDmScore(workflowOps, candidateop, ontology);
        System.out.println("\n\nDMScore = " + Score + "\n\n");
        
    }// main ends
}