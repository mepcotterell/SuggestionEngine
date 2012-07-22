
package test.evaluation;

import java.util.ArrayList;
import java.util.List;
import suggest.ForwardSuggest;
import test.TestMetrics;
import util.WebServiceOpr;
import util.WebServiceOprScore;

/**
 * Class to test the Forward Suggestions capability using p-Homomorphism 
 * input-output matching algorithm.
 * 
 * @author Alok Dhamanaskar
 * @see LICENSE (MIT style license file). 
 * 
 */

public class FSuggPHomtxtFn {

    public static List<List<Double>> pvals = new ArrayList<List<Double>>();
    
    public static String wublast     = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/wublast.sawsdl";
    public static String ncbiblast   = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/ncbiblast.sawsdl";
    public static String psiblast    = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/psiblast.sawsdl";    
    public static String fasta       = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/fasta.sawsdl";

    public static String clustalW    = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/clustalw2.sawsdl";
    public static String tcoffee     = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/tcoffee.sawsdl";
    public static String clustalo    = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/clustalo.sawsdl"; 
    public static String muscle      = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/muscle.sawsdl";    
    
    public static String wsdbfetch   = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/WSDbfetch.sawsdl";
    public static String wsconverter = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/WSConverter.sawsdl";
    public static String filerSeq    = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/FilterSequencesWS.sawsdl";
  
    public static String signalp     = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/signalp.sawsdl";
    public static String iprscan     = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/iprscan.sawsdl";
    public static String phobius     = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/phobius.sawsdl";
    
    public static String wsProtDist  = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/wsPhylipProtDist.sawsdl"; 
    public static String wsConsense  = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/wsPhylipConsense.sawsdl"; 
    public static String wsProtPars  = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/wsPhylipProtPars.sawsdl"; 
    public static String wsNeighbor  = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/wsPhylipNeighbor.sawsdl";
    public static String clustlPhylogeny  = "http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/clustalw2_phylogeny.sawsdl";
            
    public static String ontology = "owl/obiws.owl";//"http://obi-webservice.googlecode.com/svn/trunk/ontology/webService.owl";   
    
    public static void main (String[] args) {
        
        // Specify a desired functionality or operation name
        String desiredOps = "";
        
        List<WebServiceOpr> candidateOpsOBI = new ArrayList<WebServiceOpr>();

        candidateOpsOBI.add(new WebServiceOpr("filterByEvalScore", filerSeq));
        candidateOpsOBI.add(new WebServiceOpr("filterByEvalScoreCSV", filerSeq));
        
        candidateOpsOBI.add(new WebServiceOpr("decode", wsconverter));
        candidateOpsOBI.add(new WebServiceOpr("encode", wsconverter));   
        candidateOpsOBI.add(new WebServiceOpr("csvtoArray", wsconverter));   
        candidateOpsOBI.add(new WebServiceOpr("arraytoCSV", wsconverter));   
               
        candidateOpsOBI.add(new WebServiceOpr("getStyleInfo", wsdbfetch));
        candidateOpsOBI.add(new WebServiceOpr("getFormatStyles", wsdbfetch));
        candidateOpsOBI.add(new WebServiceOpr("fetchData", wsdbfetch));
        candidateOpsOBI.add(new WebServiceOpr("getSupportedFormats", wsdbfetch));
        candidateOpsOBI.add(new WebServiceOpr("getDatabaseInfo", wsdbfetch));
        candidateOpsOBI.add(new WebServiceOpr("fetchBatch", wsdbfetch));
        candidateOpsOBI.add(new WebServiceOpr("getSupportedDBs", wsdbfetch));
        candidateOpsOBI.add(new WebServiceOpr("getFormatInfo", wsdbfetch));
        candidateOpsOBI.add(new WebServiceOpr("getSupportedStyles", wsdbfetch));
        candidateOpsOBI.add(new WebServiceOpr("getDatabaseInfoList", wsdbfetch));
        candidateOpsOBI.add(new WebServiceOpr("getDbFormats", wsdbfetch));

        //----------------------------------------------------------------------
        
        candidateOpsOBI.add(new WebServiceOpr("getParameters", wublast));
        candidateOpsOBI.add(new WebServiceOpr("getParameterDetails", wublast));
        candidateOpsOBI.add(new WebServiceOpr("getResult", wublast));
        candidateOpsOBI.add(new WebServiceOpr("getResultTypes", wublast));
        candidateOpsOBI.add(new WebServiceOpr("getStatus", wublast));
        candidateOpsOBI.add(new WebServiceOpr("run", wublast));

        candidateOpsOBI.add(new WebServiceOpr("getParameters", ncbiblast));
        candidateOpsOBI.add(new WebServiceOpr("getParameterDetails", ncbiblast));
        candidateOpsOBI.add(new WebServiceOpr("getResult", ncbiblast));
        candidateOpsOBI.add(new WebServiceOpr("getResultTypes", ncbiblast));
        candidateOpsOBI.add(new WebServiceOpr("getStatus", ncbiblast));
        candidateOpsOBI.add(new WebServiceOpr("run", ncbiblast));
        
        candidateOpsOBI.add(new WebServiceOpr("getParameters", psiblast));
        candidateOpsOBI.add(new WebServiceOpr("getParameterDetails", psiblast));
        candidateOpsOBI.add(new WebServiceOpr("getResult", psiblast));
        candidateOpsOBI.add(new WebServiceOpr("getResultTypes", psiblast));
        candidateOpsOBI.add(new WebServiceOpr("getStatus", psiblast));
        candidateOpsOBI.add(new WebServiceOpr("run", psiblast));
              
        candidateOpsOBI.add(new WebServiceOpr("getParameters", fasta));
        candidateOpsOBI.add(new WebServiceOpr("getParameterDetails", fasta));
        candidateOpsOBI.add(new WebServiceOpr("getResult", fasta));
        candidateOpsOBI.add(new WebServiceOpr("getResultTypes", fasta));
        candidateOpsOBI.add(new WebServiceOpr("getStatus", fasta));
        candidateOpsOBI.add(new WebServiceOpr("run", fasta));    

        //----------------------------------------------------------------------
        
        candidateOpsOBI.add(new WebServiceOpr("getParameters", clustalW));
        candidateOpsOBI.add(new WebServiceOpr("getParameterDetails", clustalW));
        candidateOpsOBI.add(new WebServiceOpr("getResult", clustalW));
        candidateOpsOBI.add(new WebServiceOpr("getResultTypes", clustalW));
        candidateOpsOBI.add(new WebServiceOpr("getStatus", clustalW));
        candidateOpsOBI.add(new WebServiceOpr("run", clustalW));        

        candidateOpsOBI.add(new WebServiceOpr("getParameters", tcoffee));
        candidateOpsOBI.add(new WebServiceOpr("getParameterDetails", tcoffee));
        candidateOpsOBI.add(new WebServiceOpr("getResult", tcoffee));
        candidateOpsOBI.add(new WebServiceOpr("getResultTypes", tcoffee));
        candidateOpsOBI.add(new WebServiceOpr("getStatus", tcoffee));
        candidateOpsOBI.add(new WebServiceOpr("run", tcoffee));        
        
        candidateOpsOBI.add(new WebServiceOpr("getParameters", muscle));
        candidateOpsOBI.add(new WebServiceOpr("getParameterDetails", muscle));
        candidateOpsOBI.add(new WebServiceOpr("getResult", muscle));
        candidateOpsOBI.add(new WebServiceOpr("getResultTypes", muscle));
        candidateOpsOBI.add(new WebServiceOpr("getStatus", muscle));
        candidateOpsOBI.add(new WebServiceOpr("run", muscle));        
                
        candidateOpsOBI.add(new WebServiceOpr("getParameters", clustalo));
        candidateOpsOBI.add(new WebServiceOpr("getParameterDetails", clustalo));
        candidateOpsOBI.add(new WebServiceOpr("getResult", clustalo));
        candidateOpsOBI.add(new WebServiceOpr("getResultTypes", clustalo));
        candidateOpsOBI.add(new WebServiceOpr("getStatus", clustalo));
        candidateOpsOBI.add(new WebServiceOpr("run", clustalo)); 

        //----------------------------------------------------------------------
        
        candidateOpsOBI.add(new WebServiceOpr("fetchResult", signalp));
        candidateOpsOBI.add(new WebServiceOpr("pollQueue", signalp));
        candidateOpsOBI.add(new WebServiceOpr("runService", signalp));  
        
        candidateOpsOBI.add(new WebServiceOpr("getParameters", iprscan));
        candidateOpsOBI.add(new WebServiceOpr("getParameterDetails", iprscan));
        candidateOpsOBI.add(new WebServiceOpr("getResult", iprscan));
        candidateOpsOBI.add(new WebServiceOpr("getResultTypes", iprscan));
        candidateOpsOBI.add(new WebServiceOpr("getStatus", iprscan));
        candidateOpsOBI.add(new WebServiceOpr("run", iprscan));

        candidateOpsOBI.add(new WebServiceOpr("getParameters", phobius));
        candidateOpsOBI.add(new WebServiceOpr("getParameterDetails", phobius));
        candidateOpsOBI.add(new WebServiceOpr("getResult", phobius));
        candidateOpsOBI.add(new WebServiceOpr("getResultTypes", phobius));
        candidateOpsOBI.add(new WebServiceOpr("getStatus", phobius));
        candidateOpsOBI.add(new WebServiceOpr("run", phobius));       
        
        //----------------------------------------------------------------------

        candidateOpsOBI.add(new WebServiceOpr("retrieveNeighborResult", wsNeighbor));
        candidateOpsOBI.add(new WebServiceOpr("runNeighbor", wsNeighbor));
        candidateOpsOBI.add(new WebServiceOpr("getStatus", wsNeighbor));
        candidateOpsOBI.add(new WebServiceOpr("runNeighborDefaultParam", wsNeighbor));
        
        candidateOpsOBI.add(new WebServiceOpr("consenseNonRootedTrees", wsConsense));
        candidateOpsOBI.add(new WebServiceOpr("consenseRootedTrees", wsConsense));
        candidateOpsOBI.add(new WebServiceOpr("getStatus", wsConsense));
        candidateOpsOBI.add(new WebServiceOpr("retrieveConsenseResult", wsConsense));       
        
        candidateOpsOBI.add(new WebServiceOpr("protdist", wsProtDist));
        candidateOpsOBI.add(new WebServiceOpr("protdistDefaultParameters", wsProtDist));
        candidateOpsOBI.add(new WebServiceOpr("getStatus", wsProtDist));
        candidateOpsOBI.add(new WebServiceOpr("retrieveProtDistResult", wsProtDist));       

        candidateOpsOBI.add(new WebServiceOpr("getStatus", wsProtPars));
        candidateOpsOBI.add(new WebServiceOpr("runProtPars", wsProtPars));
        candidateOpsOBI.add(new WebServiceOpr("retrieveProtParsResult", wsProtPars));

        candidateOpsOBI.add(new WebServiceOpr("getParameters", clustlPhylogeny));
        candidateOpsOBI.add(new WebServiceOpr("getParameterDetails", clustlPhylogeny));
        candidateOpsOBI.add(new WebServiceOpr("getResult", clustlPhylogeny));
        candidateOpsOBI.add(new WebServiceOpr("getResultTypes", clustlPhylogeny));
        candidateOpsOBI.add(new WebServiceOpr("getStatus", clustlPhylogeny));
        candidateOpsOBI.add(new WebServiceOpr("run", clustlPhylogeny));       
        
        //----------------------------------------------------------------------
        List<WebServiceOpr> workflowOpsOBI = new ArrayList<WebServiceOpr>();
     
        workflowOpsOBI.add(new WebServiceOpr("run", wublast));
        desiredOps = "filter sequences";
        ForwardSuggest sugg2 = new ForwardSuggest();
        List<WebServiceOprScore> suggestOpList2 = sugg2.suggestNextServicepHom(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);

        System.out.println("\n");
        System.out.println("--------------------------------------------------");
        System.out.println("Suggestion for Step 2: WUBlast.run -> ??");
        System.out.println("--------------------------------------------------");
        TestMetrics.printMetrics(suggestOpList2, 0.45);

        
        workflowOpsOBI.add(new WebServiceOpr("getResult", wublast));
        desiredOps = "filter sequences";
        suggestOpList2 = sugg2.suggestNextServicepHom(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);
        System.out.println("\n");
        System.out.println("--------------------------------------------------");
        System.out.println("Suggestion for Step 3: WUBlast.run -> WUBlast.getResult -> ??");
        System.out.println("--------------------------------------------------");
        TestMetrics.printMetrics(suggestOpList2, 0.45);
        
        
        workflowOpsOBI.add(new WebServiceOpr("filterByEvalScore", filerSeq));
        desiredOps = "align multiple sequences";
        suggestOpList2 = sugg2.suggestNextServicepHom(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);
        System.out.println("\n");
        System.out.println("--------------------------------------------------");
        System.out.println("Suggestion for Step 4: WUBlast.run -> WUBlast.getResult -> FilterSequences -> ?");
        System.out.println("--------------------------------------------------");
        TestMetrics.printMetrics(suggestOpList2, 0.45);

                
        workflowOpsOBI.add(new WebServiceOpr("run", clustalW));
        desiredOps = "construct phylogenetic trees";
        suggestOpList2 = sugg2.suggestNextServicepHom(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);
        System.out.println("\n");
        System.out.println("--------------------------------------------------");
        System.out.println("Suggestion for Step 5: WUBlast.run -> WUBlast.getResult -> FilterSequences ->");
        System.out.println("clustalW.run -> ??");
        System.out.println("--------------------------------------------------");
        TestMetrics.printMetrics(suggestOpList2, 0.45);

                
        workflowOpsOBI.add(new WebServiceOpr("getResult", clustalW));
        suggestOpList2 = sugg2.suggestNextServicepHom(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);
        System.out.println("\n");
        System.out.println("--------------------------------------------------");
        System.out.println("Suggestion for Step 6: WUBlast.run -> WUBlast.getResult -> FilterSequences ->");
        System.out.println("clustalW.run -> clustalW.getResult -> ??");
        System.out.println("--------------------------------------------------");
        TestMetrics.printMetrics(suggestOpList2, 0.45);

                
        workflowOpsOBI.add(new WebServiceOpr("protdistDefaultParameters", wsProtDist));
        suggestOpList2 = sugg2.suggestNextServicepHom(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);
        System.out.println("\n");
        System.out.println("--------------------------------------------------");
        System.out.println("Suggestion for Step 7: WUBlast.run -> WUBlast.getResult -> FilterSequences ->");
        System.out.println("clustalW.run -> clustalW.getResult -> protdistDefaultParameters -> ??");
        System.out.println("--------------------------------------------------");
        TestMetrics.printMetrics(suggestOpList2, 0.45);


        
        workflowOpsOBI.add(new WebServiceOpr("retrieveProtDistResult", wsProtDist));
        suggestOpList2 = sugg2.suggestNextServicepHom(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);
        System.out.println("\n");
        System.out.println("--------------------------------------------------");
        System.out.println("Suggestion for Step 8: WUBlast.run -> WUBlast.getResult -> FilterSequences ->");
        System.out.println("clustalW.run -> clustalW.getResult -> protdistDefaultParameters -> retrieveProtDistResult -> ??");
        System.out.println("--------------------------------------------------");
        TestMetrics.printMetrics(suggestOpList2, 0.45);

               
        workflowOpsOBI.add(new WebServiceOpr("runNeighborDefaultParam", wsNeighbor));
        suggestOpList2 = sugg2.suggestNextServicepHom(workflowOpsOBI, candidateOpsOBI, desiredOps, ontology, null);
        System.out.println("\n");
        System.out.println("--------------------------------------------------");
        System.out.println("Suggestion for Step 9: WUBlast.run -> WUBlast.getResult -> FilterSequences ->");
        System.out.println("clustalW.run -> clustalW.getResult -> protdistDefaultParameters -> retrieveProtDistResult -> "
                + "retrieveProtDistResult -> wsPhylipNeighbor.runNeighborDefaultParam -> ");
        System.out.println("--------------------------------------------------");
        TestMetrics.printMetrics(suggestOpList2, 0.45);

        
    }// Main ends
    
}
