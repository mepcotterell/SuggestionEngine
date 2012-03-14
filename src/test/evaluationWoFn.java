
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
public class evaluationWoFn {
    
    public static List<List<Double>> pvals = new ArrayList<List<Double>>();
    
    public static String wublast = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/wublast.sawsdl";
    public static String ncbiblast = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/ncbiblast.sawsdl";
    public static String psiblast = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/psiblast.sawsdl";    
    public static String wsdbfetch = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/WSDbfetch.sawsdl";
    public static String clustalW = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/clustalw2.sawsdl";
    public static String tcoffee = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/tcoffee.sawsdl";
    public static String wsconverter = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/WSConverter.sawsdl";
    public static String fasta = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/fasta.sawsdl";
    public static String muscle = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/muscle.sawsdl";
    public static String filterSeq= "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/FilterSequencesWS.sawsdl";
    public static String phylip= "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/phylip.sawsdl";

    
    public static String ontology = "owl/obi.owl";
    
    
    public static void main (String[] args) {
        
        class Results {
            Map<String, OpWsdlScore> test1 = new HashMap<String, OpWsdlScore>();
            Map<String, OpWsdlScore> test2 = new HashMap<String, OpWsdlScore>();
            Map<String, OpWsdlScore> test3 = new HashMap<String, OpWsdlScore>();
            Map<String, OpWsdlScore> test4 = new HashMap<String, OpWsdlScore>();
        }
        

        String desiredOps = "";
        
        List<OpWsdl> candidateOpsOBI = new ArrayList<OpWsdl>();

        candidateOpsOBI.add(new OpWsdl("filterByEvalScore", filterSeq));
        candidateOpsOBI.add(new OpWsdl("filterByEval", filterSeq));
        
        candidateOpsOBI.add(new OpWsdl("PhylipProtdist", phylip));
        candidateOpsOBI.add(new OpWsdl("PhylipNeighbor", phylip)); 
        
        candidateOpsOBI.add(new OpWsdl("array2string", wsconverter));
        candidateOpsOBI.add(new OpWsdl("base64toString", wsconverter));        
        
        candidateOpsOBI.add(new OpWsdl("getParameters", wublast));
        candidateOpsOBI.add(new OpWsdl("getParameterDetails", wublast));
        candidateOpsOBI.add(new OpWsdl("getResult", wublast));
        candidateOpsOBI.add(new OpWsdl("getResultTypes", wublast));
        candidateOpsOBI.add(new OpWsdl("getStatus", wublast));
        candidateOpsOBI.add(new OpWsdl("run", wublast));
        
        candidateOpsOBI.add(new OpWsdl("getParameters", ncbiblast));
        candidateOpsOBI.add(new OpWsdl("getParameterDetails", ncbiblast));
        candidateOpsOBI.add(new OpWsdl("getResult", ncbiblast));
        candidateOpsOBI.add(new OpWsdl("getResultTypes", ncbiblast));
        candidateOpsOBI.add(new OpWsdl("getStatus", ncbiblast));
        candidateOpsOBI.add(new OpWsdl("run", ncbiblast));
        
        candidateOpsOBI.add(new OpWsdl("getParameters", psiblast));
        candidateOpsOBI.add(new OpWsdl("getParameterDetails", psiblast));
        candidateOpsOBI.add(new OpWsdl("getResult", psiblast));
        candidateOpsOBI.add(new OpWsdl("getResultTypes", psiblast));
        candidateOpsOBI.add(new OpWsdl("getStatus", psiblast));
        candidateOpsOBI.add(new OpWsdl("run", psiblast));
        
        candidateOpsOBI.add(new OpWsdl("getParameters", clustalW));
        candidateOpsOBI.add(new OpWsdl("getParameterDetails", clustalW));
        candidateOpsOBI.add(new OpWsdl("getResult", clustalW));
        candidateOpsOBI.add(new OpWsdl("getResultTypes", clustalW));
        candidateOpsOBI.add(new OpWsdl("getStatus", clustalW));
        candidateOpsOBI.add(new OpWsdl("run", clustalW));        

        candidateOpsOBI.add(new OpWsdl("getParameters", tcoffee));
        candidateOpsOBI.add(new OpWsdl("getParameterDetails", tcoffee));
        candidateOpsOBI.add(new OpWsdl("getResult", tcoffee));
        candidateOpsOBI.add(new OpWsdl("getResultTypes", tcoffee));
        candidateOpsOBI.add(new OpWsdl("getStatus", tcoffee));
        candidateOpsOBI.add(new OpWsdl("run", tcoffee));        
        
        candidateOpsOBI.add(new OpWsdl("getParameters", muscle));
        candidateOpsOBI.add(new OpWsdl("getParameterDetails", muscle));
        candidateOpsOBI.add(new OpWsdl("getResult", muscle));
        candidateOpsOBI.add(new OpWsdl("getResultTypes", muscle));
        candidateOpsOBI.add(new OpWsdl("getStatus", muscle));
        candidateOpsOBI.add(new OpWsdl("run", muscle));        
        
        candidateOpsOBI.add(new OpWsdl("getParameters", fasta));
        candidateOpsOBI.add(new OpWsdl("getParameterDetails", fasta));
        candidateOpsOBI.add(new OpWsdl("getResult", fasta));
        candidateOpsOBI.add(new OpWsdl("getResultTypes", fasta));
        candidateOpsOBI.add(new OpWsdl("getStatus", fasta));
        candidateOpsOBI.add(new OpWsdl("run", fasta));        
        
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

      
        List<OpWsdl> workflowOpsOBI = new ArrayList<OpWsdl>();
        workflowOpsOBI.add(new OpWsdl("run", wublast));   
        desiredOps = "";

        System.out.println();
        System.out.println("----------------------evaluationWoFn----------------------------");
        System.out.println("TEST - OBI");
        System.out.println("Case 1: There is only one operation on the workflow Blast.run\n------------------------------------\n");
        ForwardSuggest sugg2 = new ForwardSuggest();
        List<OpWsdlScore> suggestOpList2 = sugg2.getSuggestServices(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);       
        TestMetrics.printMetrics(suggestOpList2);
        
        workflowOpsOBI.add(new OpWsdl("getResult", wublast));
        desiredOps = "";
        suggestOpList2 = sugg2.getSuggestServices(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);        
        System.out.println();   
        System.out.println("\nCase 2\n Workflow has two Operations Added\nBlast.run -> Blast.getResult--------------------------------------------------");
        System.out.println();
        TestMetrics.printMetrics(suggestOpList2);
        
        workflowOpsOBI.add(new OpWsdl("filterByEvalScore", filterSeq));   
        desiredOps = ""; 
        suggestOpList2 = sugg2.getSuggestServices(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);        
        System.out.println();
        System.out.println("\nCase 3\n Workflow has three Operations Added\nBlast.run -> Blast.getResult -> Filter Sequences\n--------------------------------------------------");
        System.out.println();    
        TestMetrics.printMetrics(suggestOpList2);
    
        workflowOpsOBI.add(new OpWsdl("run", clustalW));
        desiredOps = "";
        suggestOpList2 = sugg2.getSuggestServices(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);        
        System.out.println();
        System.out.println("\nCase 4\n Workflow has three Operations Added\nBlast.run -> Blast.getResult -> Filter Sequences -> ClustalW.run\n--------------------------------------------------");
        System.out.println();        
        TestMetrics.printMetrics(suggestOpList2);
      
        workflowOpsOBI.add(new OpWsdl("getResult", clustalW));
        desiredOps = "";
        suggestOpList2 = sugg2.getSuggestServices(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);        
        System.out.println();
        System.out.println("\nCase 5\n Workflow has three Operations Added\nBlast.run -> Blast.getResult -> Filter Sequences -> ClustalW.run -> ClustalW.getResult\n--------------------------------------------------");
        System.out.println();        
        TestMetrics.printMetrics(suggestOpList2);

        workflowOpsOBI.add(new OpWsdl("PhylipProtdist", phylip));
        desiredOps = "";
        suggestOpList2 = sugg2.getSuggestServices(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);        
        System.out.println();
        System.out.println("\nCase 5\n Workflow has three Operations Added\nBlast.run -> Blast.getResult -> Filter Sequences -> ClustalW.run -> ClustalW.getResult -> PhylipProtdist\n--------------------------------------------------");
        System.out.println();        
        TestMetrics.printMetrics(suggestOpList2);
        
    }
    
}
