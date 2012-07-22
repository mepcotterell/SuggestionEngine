
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
 * Test Class for testing Path based input-output Matching.
 * 
 * @author Alok Dhamanaskar
 * @see LICENSE (MIT style license file). 
 * 
 */

public class TestPathBasedDataMediation 
{
    
    public static String wublast  = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/wublast.sawsdl";
    public static String clustalW = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/clustalw2.sawsdl";
    public static String filerSeq  = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/FilterSequencesWS.sawsdl";
    
    public static String ontology = "http://obi-webservice.googlecode.com/svn/trunk/ontology/webService.owl"; 
    
    public static void main (String[] args) {
            
        WebServiceOpr candidateop = new WebServiceOpr("getResult",wublast);
        List<WebServiceOpr> workflowOps = new ArrayList<WebServiceOpr>();
        workflowOps.add(new WebServiceOpr("run", wublast));
        workflowOps.add(new WebServiceOpr("getResultTypes", wublast));
        
        DmScore dm = new DmScore();
        Double Score = dm.calculatePathDmScore(workflowOps, candidateop, ontology, new ArrayList<String>());
        
        System.out.println("\n\nDMScore = " + Score + "\n\n");
        
        Map<WebServiceOprScore_type, WebServiceOprScore_type> matchedPaths= dm.getDmResults();
        
        Set<WebServiceOprScore_type> ipPaths = matchedPaths.keySet();

        for(WebServiceOprScore_type a: ipPaths)
        {
            System.out.print("InputPath - >");
            DebuggingUtils.printPath(a.getPath());
            System.out.print("MatchedTo - >");
            DebuggingUtils.printPath(matchedPaths.get(a).getPath());
            System.out.println("Confidence of: " + a.getScore());
            System.out.println("----------------------------------------");
        }
    }// main ends
}//TestPathBasedDataMediation ends
