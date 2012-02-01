/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import suggest.ForwardSuggest;
import util.OpWsdl;
import util.OpWsdlScore;

/**
 *
 * @author mepcotterell
 */
public class Test {
    
    public static List<List<Double>> pvals = new ArrayList<List<Double>>();
    
    public static String wublast = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/wsdlfiles/evaluate/obi/wublast.wsdl";
    public static String wsdbfetch = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/wsdlfiles/evaluate/obi/WSDbfetchDoclit.wsdl";
    public static String clustalW = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/clustalw2_FullyAnnotated.wsdl";
    
//    public static String wublast = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/wsdlfiles/evaluate/edam/wublast.wsdl";
//    public static String wsdbfetch = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/wsdlfiles/evaluate/edam/WSDbfetchDoclit.wsdl";
//    public static String clustalW = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/wsdlfiles/evaluate/edam/clustalw2.wsdl";
   
    public static String ontology = "owl/obi.owl";
//    public static String ontology = "edam.owl";
    
    public static void main (String[] args) {
        
        class Results {
            Map<String, OpWsdlScore> test1 = new HashMap<String, OpWsdlScore>();
            Map<String, OpWsdlScore> test2 = new HashMap<String, OpWsdlScore>();
            Map<String, OpWsdlScore> test3 = new HashMap<String, OpWsdlScore>();
            Map<String, OpWsdlScore> test4 = new HashMap<String, OpWsdlScore>();
        }
        
        Results results = new Results();
        
        // Specify a desired functionality or operation name
        String desiredOps = "";//retrieve sequences";
        //String desiredOps = "http://purl.obolibrary.org/obo/obi.owl#Class_40";
        
        List<OpWsdl> candidateOpsOBI = new ArrayList<OpWsdl>();
        candidateOpsOBI.add(new OpWsdl("getParameters", wublast));
        candidateOpsOBI.add(new OpWsdl("getParameterDetails", wublast));
        candidateOpsOBI.add(new OpWsdl("getResult", wublast));
        candidateOpsOBI.add(new OpWsdl("getResultTypes", wublast));
        candidateOpsOBI.add(new OpWsdl("getStatus", wublast));
        candidateOpsOBI.add(new OpWsdl("getStyleInfo", wsdbfetch));
        candidateOpsOBI.add(new OpWsdl("getFormatStyles", wsdbfetch));
        candidateOpsOBI.add(new OpWsdl("fetchData", wsdbfetch));
        candidateOpsOBI.add(new OpWsdl("getSupportedFormats", wsdbfetch));
        candidateOpsOBI.add(new OpWsdl("getDatabaseInfo", wsdbfetch));
        candidateOpsOBI.add(new OpWsdl("fetchBatch", wsdbfetch));
        candidateOpsOBI.add(new OpWsdl("getSupportedDBs", wsdbfetch));
        candidateOpsOBI.add(new OpWsdl("getFormatInfo", wsdbfetch));
        candidateOpsOBI.add(new OpWsdl("getSupportedStyles", wsdbfetch));
        candidateOpsOBI.add(new OpWsdl("getDatabaseInfoList", wsdbfetch));
        candidateOpsOBI.add(new OpWsdl("getDbFormats", wsdbfetch));
        //candidateOpsOBI.add(new OpWsdl("run", clustalW));
        
        List<OpWsdl> workflowOpsOBI = new ArrayList<OpWsdl>();
        workflowOpsOBI.add(new OpWsdl("run", wublast));
        
        //workflowOpsOBI.add(new OpWsdl("fetchBatch", wsdbfetch));
        
        System.out.println();
        System.out.println("--------------------------------------------------");
        System.out.println("TEST - OBI");
        System.out.println("Case 1: There is only one operation on the workflow Blast.run\n------------------------------------\n");
        ForwardSuggest sugg2 = new ForwardSuggest();
        List<OpWsdlScore> suggestOpList2 = sugg2.getSuggestServices(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);
        for (OpWsdlScore suggestion: suggestOpList2) {
            results.test1.put(suggestion.getOpName(), suggestion);
            //System.out.println(suggestion.getOpName() + "\t" + suggestion.getScore() + "\t" + suggestion.getDmScore() + "\t" + suggestion.getFnScore() + "\t" + suggestion.getPeScore() + "\n");
            System.out.println(suggestion.getOpName() + "\t" + suggestion.getScore() + "\n");
        }
        
        workflowOpsOBI.add(new OpWsdl("getResult", wublast));
        desiredOps = "retrieve sequences";
        suggestOpList2 = sugg2.getSuggestServices(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);        
        System.out.println();   
        System.out.println("\nCase 2\n Workflow has two Operations Added\nBlast.run -> Blast.getResult--------------------------------------------------");
        System.out.println();
        for (OpWsdlScore suggestion: suggestOpList2) {
            results.test1.put(suggestion.getOpName(), suggestion);
            //System.out.println(suggestion.getOpName() + "\t" + suggestion.getScore() + "\t" + suggestion.getDmScore() + "\t" + suggestion.getFnScore() + "\t" + suggestion.getPeScore() + "\n");
            System.out.println(suggestion.getOpName() + "\t" + suggestion.getScore() + "\n");
        }

        //workflowOpsOBI.add(new OpWsdl("run", clustalW));
        workflowOpsOBI.add(new OpWsdl("fetchBatch", wsdbfetch));
        desiredOps = "global multiple sequence alignment";
        suggestOpList2 = sugg2.getSuggestServices(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);        
        System.out.println();
        System.out.println("\nCase 3\n Workflow has three Operations Added\nBlast.run -> Blast.getResult -> FetchBatch\n--------------------------------------------------");
        System.out.println();
        
        for (OpWsdlScore suggestion: suggestOpList2) {
            results.test1.put(suggestion.getOpName(), suggestion);
            //System.out.println(suggestion.getOpName() + "\t" + suggestion.getScore() + "\t" + suggestion.getDmScore() + "\t" + suggestion.getFnScore() + "\t" + suggestion.getPeScore() + "\n");
            System.out.println(suggestion.getOpName() + "\t" + suggestion.getScore() + "\n");
        }
    
        workflowOpsOBI.add(new OpWsdl("run", clustalW));
        //workflowOpsOBI.add(new OpWsdl("fetchBatch", wsdbfetch));
        desiredOps = "";
        suggestOpList2 = sugg2.getSuggestServices(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);        
        System.out.println();
        System.out.println("\nCase 4\n Workflow has three Operations Added\nBlast.run -> Blast.getResult -> WSDBFetch.FetchBatch -> ClustalW.run\n--------------------------------------------------");
        System.out.println();
        
        for (OpWsdlScore suggestion: suggestOpList2) {
            results.test1.put(suggestion.getOpName(), suggestion);
            //System.out.println(suggestion.getOpName() + "\t" + suggestion.getScore() + "\t" + suggestion.getDmScore() + "\t" + suggestion.getFnScore() + "\t" + suggestion.getPeScore() + "\n");
            System.out.println(suggestion.getOpName() + "\t" + suggestion.getScore() + "\n");
        }       
         
    }
    
}
