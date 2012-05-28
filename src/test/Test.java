
package test;

import java.util.*;
import suggest.BackwardSuggest;
import suggest.ForwardSuggest;
import util.DebuggingUtils;
import util.MatchedIOPaths;
import util.WebServiceOpr;
import util.WebServiceOprScore;
import util.WebServiceOprScore_type;

/**
 *
 * @author mepcotterell
 */
public class Test {
    
    public static List<List<Double>> pvals = new ArrayList<List<Double>>();
    
    public static String wublast     = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/wublast.sawsdl";
    public static String ncbiblast   = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/ncbiblast.sawsdl";
    public static String psiblast    = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/psiblast.sawsdl";    
    public static String wsdbfetch   = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/WSDbfetch.sawsdl";
    public static String wsconverter = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/WSConverter.sawsdl";
    public static String filerSeq    = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/FilterSequencesWS.sawsdl";//FilterSequencesWS.sawsdl";
    public static String clustalW    = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/clustalw2.sawsdl";
    public static String tcoffee     = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/tcoffee.sawsdl";
    public static String fasta       = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/fasta.sawsdl";
    public static String muscle      = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/muscle.sawsdl";
    
    public static String ontology = "owl/webService.owl";
    //"http://obi-webservice.googlecode.com/svn/trunk/ontology/webService.owl";   
    
    public static void main (String[] args) {
        
        class Results {
            Map<String, WebServiceOprScore> test1 = new HashMap<String, WebServiceOprScore>();
            Map<String, WebServiceOprScore> test2 = new HashMap<String, WebServiceOprScore>();
            Map<String, WebServiceOprScore> test3 = new HashMap<String, WebServiceOprScore>();
            Map<String, WebServiceOprScore> test4 = new HashMap<String, WebServiceOprScore>();
        }
        
        Results results = new Results();
        
        // Specify a desired functionality or operation name
        String desiredOps = "";//retrieve sequences";
        //String desiredOps = "http://purl.obolibrary.org/obo/obi.owl#Class_40";
        
        List<WebServiceOpr> candidateOpsOBI = new ArrayList<WebServiceOpr>();

//        candidateOpsOBI.add(new WebServiceOpr("filterByEvalScore", filerSeq));
//        candidateOpsOBI.add(new WebServiceOpr("filterByEval", filerSeq));
//        
//        candidateOpsOBI.add(new WebServiceOpr("array2string", wsconverter));
//        candidateOpsOBI.add(new WebServiceOpr("base64toString", wsconverter));   
        
//        candidateOpsOBI.add(new WebServiceOpr("getParameters", wublast));
//        candidateOpsOBI.add(new WebServiceOpr("getParameterDetails", wublast));
        candidateOpsOBI.add(new WebServiceOpr("getResult", wublast));
//        candidateOpsOBI.add(new WebServiceOpr("getResultTypes", wublast));
   //     candidateOpsOBI.add(new WebServiceOpr("getStatus", wublast));

                
//        candidateOpsOBI.add(new WebServiceOpr("getParameters", ncbiblast));
//        candidateOpsOBI.add(new WebServiceOpr("getParameterDetails", ncbiblast));
//        candidateOpsOBI.add(new WebServiceOpr("getResult", ncbiblast));
//        candidateOpsOBI.add(new WebServiceOpr("getResultTypes", ncbiblast));
//        candidateOpsOBI.add(new WebServiceOpr("getStatus", ncbiblast));
//        candidateOpsOBI.add(new WebServiceOpr("run", ncbiblast));
//        
//        candidateOpsOBI.add(new WebServiceOpr("getParameters", psiblast));
//        candidateOpsOBI.add(new WebServiceOpr("getParameterDetails", psiblast));
//        candidateOpsOBI.add(new WebServiceOpr("getResult", psiblast));
//        candidateOpsOBI.add(new WebServiceOpr("getResultTypes", psiblast));
//        candidateOpsOBI.add(new WebServiceOpr("getStatus", psiblast));
//        candidateOpsOBI.add(new WebServiceOpr("run", psiblast));
//
//        candidateOpsOBI.add(new WebServiceOpr("getStyleInfo", wsdbfetch));
//        candidateOpsOBI.add(new WebServiceOpr("getFormatStyles", wsdbfetch));
//        candidateOpsOBI.add(new WebServiceOpr("fetchData", wsdbfetch));
//        candidateOpsOBI.add(new WebServiceOpr("getSupportedFormats", wsdbfetch));
//        candidateOpsOBI.add(new WebServiceOpr("getDatabaseInfo", wsdbfetch));
//        candidateOpsOBI.add(new WebServiceOpr("fetchBatch", wsdbfetch));
//        candidateOpsOBI.add(new WebServiceOpr("getSupportedDBs", wsdbfetch));
//        candidateOpsOBI.add(new WebServiceOpr("getFormatInfo", wsdbfetch));
//        candidateOpsOBI.add(new WebServiceOpr("getSupportedStyles", wsdbfetch));
//        candidateOpsOBI.add(new WebServiceOpr("getDatabaseInfoList", wsdbfetch));
//        candidateOpsOBI.add(new WebServiceOpr("getDbFormats", wsdbfetch));
//
//        candidateOpsOBI.add(new WebServiceOpr("getParameters", clustalW));
//        candidateOpsOBI.add(new WebServiceOpr("getParameterDetails", clustalW));
//        candidateOpsOBI.add(new WebServiceOpr("getResult", clustalW));
//        candidateOpsOBI.add(new WebServiceOpr("getResultTypes", clustalW));
//        candidateOpsOBI.add(new WebServiceOpr("getStatus", clustalW));
//        candidateOpsOBI.add(new WebServiceOpr("run", clustalW));        
//
//        candidateOpsOBI.add(new WebServiceOpr("getParameters", tcoffee));
//        candidateOpsOBI.add(new WebServiceOpr("getParameterDetails", tcoffee));
//        candidateOpsOBI.add(new WebServiceOpr("getResult", tcoffee));
//        candidateOpsOBI.add(new WebServiceOpr("getResultTypes", tcoffee));
//        candidateOpsOBI.add(new WebServiceOpr("getStatus", tcoffee));
//        candidateOpsOBI.add(new WebServiceOpr("run", tcoffee));        
//        
//        candidateOpsOBI.add(new WebServiceOpr("getParameters", muscle));
//       candidateOpsOBI.add(new WebServiceOpr("getParameterDetails", muscle));
//        candidateOpsOBI.add(new WebServiceOpr("getResult", muscle));
//        candidateOpsOBI.add(new WebServiceOpr("getResultTypes", muscle));
//        candidateOpsOBI.add(new WebServiceOpr("getStatus", muscle));
//        candidateOpsOBI.add(new WebServiceOpr("run", muscle));        
//        
//        candidateOpsOBI.add(new WebServiceOpr("getParameters", fasta));
//        candidateOpsOBI.add(new WebServiceOpr("getParameterDetails", fasta));
//        candidateOpsOBI.add(new WebServiceOpr("getResult", fasta));
//        candidateOpsOBI.add(new WebServiceOpr("getResultTypes", fasta));
//        candidateOpsOBI.add(new WebServiceOpr("getStatus", fasta));
//        candidateOpsOBI.add(new WebServiceOpr("run", fasta));    
        
        List<WebServiceOpr> workflowOpsOBI = new ArrayList<WebServiceOpr>();
        //workflowOpsOBI.add(new WebServiceOpr("filterByEval", filerSeq));
        workflowOpsOBI.add(new WebServiceOpr("run", wublast));
        //workflowOpsOBI.add(new WebServiceOpr("getResultTypes", wublast));
        
        System.out.println();
        System.out.println("--------------------------------------------------");
        System.out.println("TEST - OBI");
        System.out.println("Case 1: There is only one operation on the workflow Blast.run\n------------------------------------\n");
        //desiredOps = "multiple sequence alignment";//http://purl.obolibrary.org/obo/OBIws_0000063
        ForwardSuggest sugg2 = new ForwardSuggest();
        
        
        BackwardSuggest suggB = new BackwardSuggest();
        //List<WebServiceOprScore> suggestOpList2 = suggB.suggestPrevServices(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);
        
        List<WebServiceOprScore> suggestOpList2 = sugg2.suggestNextService(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);
                    System.out.println("\n");
        for (WebServiceOprScore suggestion: suggestOpList2) {
            results.test1.put(suggestion.getOperationName(), suggestion);
            String[] ww = suggestion.getWsDescriptionDoc().split("/");
            String wsName = ww[ww.length -1].replace("sawsdl", "");

            System.out.println(wsName+ suggestion.getOperationName() + "\tTotal=" + suggestion.getScore() + "\tDm=" + suggestion.getDmScore() + "\tFn=" + suggestion.getFnScore() + "\tPe=" + suggestion.getPeScore());
            System.out.println();
            List<MatchedIOPaths.PathMatches> mps = suggestion.getMatchedPathsIp().getMatchedPaths();
            //System.out.println(mps + "\n");
            for(MatchedIOPaths.PathMatches mp : mps)
            {
                System.out.print("\t" + mp.getIpName() + "-->" + mp.getOpName() + " With confidence "+ mp.getConfidenceLevel() + "%\n");
            }
            System.out.println("\n");
        }
        

        
//        workflowOpsOBI.add(new WebServiceOpr("getResult", wublast));
//        desiredOps = "retrieve sequences";
//        suggestOpList2 = sugg2.suggestNextService(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);        
//        System.out.println();   
//        System.out.println("\nCase 2\n Workflow has two Operations Added\nBlast.run -> Blast.getResult--------------------------------------------------");
//        System.out.println();
//        for (WebServiceOprScore suggestion: suggestOpList2) {
//            results.test1.put(suggestion.getOpName(), suggestion);
//            //System.out.println(suggestion.getOpName() + "\t" + suggestion.getScore() + "\t" + suggestion.getDmScore() + "\t" + suggestion.getFnScore() + "\t" + suggestion.getPeScore() + "\n");
//            System.out.println(suggestion.getOpName() + "\t" + suggestion.getScore() + "\n");
//        }
//
//        //workflowOpsOBI.add(new WebServiceOpr("run", clustalW));
//        workflowOpsOBI.add(new WebServiceOpr("fetchBatch", wsdbfetch));
//        desiredOps = "global multiple sequence alignment";
//        suggestOpList2 = sugg2.suggestNextService(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);        
//        System.out.println();
//        System.out.println("\nCase 3\n Workflow has three Operations Added\nBlast.run -> Blast.getResult -> FetchBatch\n--------------------------------------------------");
//        System.out.println();
//        
//        for (WebServiceOprScore suggestion: suggestOpList2) {
//            results.test1.put(suggestion.getOpName(), suggestion);
//            //System.out.println(suggestion.getOpName() + "\t" + suggestion.getScore() + "\t" + suggestion.getDmScore() + "\t" + suggestion.getFnScore() + "\t" + suggestion.getPeScore() + "\n");
//            System.out.println(suggestion.getOpName() + "\t" + suggestion.getScore() + "\n");
//        }
//    
//        workflowOpsOBI.add(new WebServiceOpr("run", clustalW));
//        //workflowOpsOBI.add(new WebServiceOpr("fetchBatch", wsdbfetch));
//        desiredOps = "";
//        suggestOpList2 = sugg2.suggestNextService(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);        
//        System.out.println();
//        System.out.println("\nCase 4\n Workflow has three Operations Added\nBlast.run -> Blast.getResult -> WSDBFetch.FetchBatch -> ClustalW.run\n--------------------------------------------------");
//        System.out.println();
//        
//        for (WebServiceOprScore suggestion: suggestOpList2) {
//            results.test1.put(suggestion.getOpName(), suggestion);
//            //System.out.println(suggestion.getOpName() + "\t" + suggestion.getScore() + "\t" + suggestion.getDmScore() + "\t" + suggestion.getFnScore() + "\t" + suggestion.getPeScore() + "\n");
//            System.out.println(suggestion.getOpName() + "\t" + suggestion.getScore() + "\n");
//        }       
    }
    
}
