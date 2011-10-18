/**
 * 
 */
package suggest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.semanticweb.owlapi.model.*;

import parser.OntologyManager;

import suggest.calculator.DmScore;
import suggest.calculator.FnScore;
import suggest.calculator.PeScore;
import util.EvUtils;
import util.IODG;
import util.OpWsdl;
import util.OpWsdlPathScore_type;
import util.OpWsdlScore;
import util.Timer;

/**
 * @author Rui Wang
 * 
 */
public class ForwardSuggest {
    
    // number of suggests will return
    private int maxSuggest = 5;
    // data mediation score weight
    private double weightDm = 0.3333333333333333333;
    // fundtionality score weight
    private double weightFn = 0.3333333333333333333;
    // precondition-effect score weight
    private double weightPe = 0.3333333333333333333;
    // score has to be greater than this threshold to be suggested
    private double scoreThreshold = 0.0;
    //path-based data mediation result for all candidateOPs: map[candidateOP, mapPath]
    //mapPath is a map for input of an candidateOP: map [path of the input of candidateOP, matched path in output&globalInput of workflow]
    private Map<OpWsdl, Map<OpWsdlPathScore_type, OpWsdlPathScore_type>> dmResults = new HashMap<OpWsdl, Map<OpWsdlPathScore_type, OpWsdlPathScore_type>>();
    
    /**
     * structure-based data mediation results for all candidateOPs
     * subtree homeomorphism
     * map[candidateOP, list of input nodes]
     * the input nodes record the matched node and score
     */
    private Map<OpWsdl, List<IODG>> homeoDmResults = new HashMap<OpWsdl, List<IODG>>();

    /**get a complete structure-based data mediation results for all candidateOPs
     * subtree homeomorphism
     * map[candidateOP, list of input nodes]
     * the input nodes record the matched node and score
     * 
     * @return the homeoDmResults
     */
    public Map<OpWsdl, List<IODG>> getHomeoDmResults() {
        return homeoDmResults;
    }

    /**to get a complete path-based/leaf-base data mediation result
     * for all candidateOPs:map[candidateOP, mapPath]
     * mapPath is a map for input of an candidateOP: map <inputPaths, matchedOutputPaths>
     * inputPaths has every input path and its matching score, isRequired(1,0.6,0.2), isInput(0,1)
     * matchedOutputPaths has output paths match each input path, isSafeMath(0,1)
     * @return the dmResults
     */
    public Map<OpWsdl, Map<OpWsdlPathScore_type, OpWsdlPathScore_type>> getDmResults() {
        return dmResults;
    }

    /**get matching score of user prefered operation v.s candidate operation
     * @param preferOp        user prefered operation, concept in an ontology
     * @param op           candidate operation (name + wsdl)
     * @param owlFileName       owl ontology file name
     * @return
     */
    private double getFnScore(String preferOp, OpWsdl op, String owlFileName) {

        double fnScore = 0;
        if (preferOp != null && op != null) {
            FnScore fs = new FnScore();
            fnScore = fs.calculateFnScore(preferOp, op, owlFileName);
        }

        return fnScore;

    }

    /**score based on data mediation, given owl file name
     * @param workflowOPs
     * @param candidateOP
     * @param owlFileName
     * @return
     */
    private double getDmScore(List<OpWsdl> workflowOPs, OpWsdl candidateOP, String owlFileName) 
    {
        double dmScore = 0;
    
        if (workflowOPs != null && candidateOP != null) {
            DmScore ds = new DmScore();

            //path-based data mediation
            dmScore = ds.calculatePathDmScore(workflowOPs, candidateOP, owlFileName);
            dmResults.put(candidateOP, ds.getDmResults());

            //structure-based data mediation(subtree homeomorphism)
//			dmScore = ds.calculateHomeoDmScore(workflowOPs, candidateOP, owlFileName);
//			homeoDmResults.put(candidateOP, ds.getHomeoDmResult());

            //leaf-based data mediation
//			dmScore = ds.calculateLeafDmScore(workflowOPs, candidateOP, owlFileName);
//			dmResults.put(candidateOP, ds.getDmResults());
        }
        return dmScore;
    }


    /**
     * check whether current state entails precondition of candidate operation
     * @param initState          initial state file name, can be null
     * @param workflowOPs     all operations in current workflow, allow some operations have no effect
     * @param candidateOP    candidate operation, if no precondition, score=0
     * @return   
     */
    private double getPeScore(String initState, List<OpWsdl> workflowOPs, OpWsdl candidateOP) {
        double peScore = 0;

        if (workflowOPs != null && candidateOP != null) {
            PeScore ps = new PeScore();
            peScore = ps.calculatePeScore(initState, workflowOPs, candidateOP);
        }

        return peScore;
    }

    /**
     * given operations in current workflow, all candidated operations return a
     * list of suggested operations and their rank score, size of the list is no
     * more than the value of "maxSuggest"
     * 
     * @param workflowOPs
     * @param candidateOPs
     * @return
     */
    public List<OpWsdlScore> getSuggestServices(List<OpWsdl> workflowOPs,
            List<OpWsdl> candidateOPs, String preferOp, String owlURI, String initState) {

        if (preferOp != null) {
            if (preferOp.length() == 0) {
                preferOp = null;
            }
        }

        if (workflowOPs == null || candidateOPs == null) {
            return null;
        }


        OntologyManager om = OntologyManager.getInstance(owlURI);
        OWLOntology onModel = om.getOntology(owlURI);

        //adjust weight, so final scores are comparable for different level annotation
        if (preferOp == null) {
            if (initState == null) {
                weightDm = 1;
            } else {
                weightDm = 0.5;
                weightPe = 0.5;
            }
        } else if (initState == null) {
            weightDm = 0.5;
            weightFn = 0.5;
        }

        List<OpWsdlScore> suggestionList = new ArrayList<OpWsdlScore>();
        for (OpWsdl op : candidateOPs) {
            // datamediation score
            double dmScore = 0;
            // functionality score
            double fnScore = 0;
            // pre-condition and effect score
            double peScore = 0;
            // final score
            double score = 0;


            dmScore = this.getDmScore(workflowOPs, op, owlURI);

            if (preferOp != null) {
                fnScore = this.getFnScore(preferOp, op, owlURI);
            }

            if (initState != null) {
                peScore = this.getPeScore(initState, workflowOPs, op);

            }

            score = this.weightDm * dmScore + this.weightFn * fnScore
                    + this.weightPe * peScore;

            OpWsdlScore opScore = new OpWsdlScore(op.getOpName(), op.getWsdlName(), score);
            opScore.setDmScore(dmScore);
            opScore.setFnScore(fnScore);
            opScore.setPeScore(peScore);
            opScore.setExtraInfo(op.getExtraInfo());
            suggestionList.add(opScore);

        }

        Collections.sort(suggestionList, Collections.reverseOrder());

        return suggestionList;
    }

    /**given two owl files: one to annotate operations, one to annotate input/output
     * given operations in current workflow, all candidated operations return a
     * list of suggested operations and their rank score
     * 
     * @param workflowOPs
     * @param candidateOPs
     * @param preferOp
     * @param opOwlURI
     * @param msgOwlURI
     * @param initState
     * @return
     */
    public List<OpWsdlScore> getSuggestServicesWith2owl(List<OpWsdl> workflowOPs,
            List<OpWsdl> candidateOPs, String preferOp, String opOwlURI, String msgOwlURI, String initState) {

        if (workflowOPs == null || candidateOPs == null) {
            return null;
        }

        if (preferOp != null) {
            if (preferOp.length() == 0) {
                preferOp = null;
            }
        }
        
        OntologyManager opOm = OntologyManager.getInstance(opOwlURI);
        OWLOntology opOnModel = opOm.getOntology(opOwlURI);

        OntologyManager msgOm = OntologyManager.getInstance(msgOwlURI);
        OWLOntology msgOnModel = msgOm.getOntology(msgOwlURI);

        //adjust weight, so final scores are comparable for different level annotation
        if (preferOp == null) {
            if (initState == null) {
                weightDm = 1;
            } else {
                weightDm = 0.5;
                weightPe = 0.5;
            }
        } else if (initState == null) {
            weightDm = 0.5;
            weightFn = 0.5;
        }

        List<OpWsdlScore> suggestionList = new ArrayList<OpWsdlScore>();
        for (OpWsdl op : candidateOPs) {
            // datamediation score
            double dmScore = 0;
            // functionality score
            double fnScore = 0;
            // pre-condition and effect score
            double peScore = 0;
            // final score
            double score = 0;



//			Timer.startTimer();
            dmScore = this.getDmScore(workflowOPs, op, msgOwlURI);
//			Timer.endTimer();
//			dmScore = this.getDmScore(workflowOPs, op, owlFileName);

//			Timer.startTimer();
            if (preferOp != null) {
                fnScore = this.getFnScore(preferOp, op, opOwlURI);
//				fnScore = this.getFnScore(preferOp, op, owlFileName);
            }
//			Timer.endTimer();

//			Timer.startTimer();
            if (initState != null) {
                peScore = this.getPeScore(initState, workflowOPs, op);

            }
//			Timer.endTimer();

            score = this.weightDm * dmScore + this.weightFn * fnScore
                    + this.weightPe * peScore;
//			System.out.println(op.getOpName()+"="+score+"*****************" + op.getWsdlName());
//			System.out.println("dm="+dmScore);
//			System.out.println();
//			System.out.println("fn="+fnScore);
//			System.out.println("pe="+peScore);

//			if (score >= scoreThreshold) {
            OpWsdlScore opScore = new OpWsdlScore(op.getOpName(), op.getWsdlName(), score);

            opScore.setExtraInfo(op.getExtraInfo());
            suggestionList.add(opScore);
//			}
        }
        Collections.sort(suggestionList, Collections.reverseOrder());
//		if (suggestionList.size() > maxSuggest) {
//			suggestionList = suggestionList.subList(0, maxSuggest);
//
//		}
        return suggestionList;
    }

    /**given two owl files: one to annotate operations, one to annotate input/output
     * given operations in current workflow, all candidated operations 
     * save 7 result files: dm, fn, pe, dmfn, dmpe, fnpe, dmfnpe (list of ranked operations and their rank score)
     * 
     * @param workflowOPs
     * @param candidateOPs
     * @return
     */
    public void fileSuggestServicesWith2owl(List<OpWsdl> workflowOPs,
            List<OpWsdl> candidateOPs, String preferOp, String opOwlURI, String msgOwlURI, String initState, String saveFileName) {

        if (workflowOPs == null || candidateOPs == null) {
            return;
        }

        OntologyManager opOm = OntologyManager.getInstance(opOwlURI);
        OWLOntology opOnModel = opOm.getOntology(opOwlURI);

        OntologyManager msgOm = OntologyManager.getInstance(msgOwlURI);
        OWLOntology msgOnModel = msgOm.getOntology(msgOwlURI);

        //adjust weight, so final scores are comparable for different level annotation
        if (preferOp == null) {
            if (initState == null) {
                weightDm = 1;
            } else {
                weightDm = 0.5;
                weightPe = 0.5;
            }
        } else if (initState == null) {
            weightDm = 0.5;
            weightFn = 0.5;
        }

        List<OpWsdlScore> suggestion0List = new ArrayList<OpWsdlScore>();
        List<OpWsdlScore> suggestionDmList = new ArrayList<OpWsdlScore>();
        List<OpWsdlScore> suggestionFnList = new ArrayList<OpWsdlScore>();
        List<OpWsdlScore> suggestionPeList = new ArrayList<OpWsdlScore>();
        List<OpWsdlScore> suggestionDmfnList = new ArrayList<OpWsdlScore>();
        List<OpWsdlScore> suggestionDmpeList = new ArrayList<OpWsdlScore>();
        List<OpWsdlScore> suggestionFnpeList = new ArrayList<OpWsdlScore>();
        List<OpWsdlScore> suggestionDmfnpeList = new ArrayList<OpWsdlScore>();
        for (OpWsdl op : candidateOPs) {
            // datamediation score
            double dmScore = 0;
            // functionality score
            double fnScore = 0;
            // pre-condition and effect score
            double peScore = 0;
            // final score
            double dmfnScore = 0;
            double dmpeScore = 0;
            double fnpeScore = 0;
            double dmfnpeScore = 0;


//			Timer.startTimer();
            dmScore = this.getDmScore(workflowOPs, op, msgOwlURI);
//			Timer.endTimer();
//			dmScore = this.getDmScore(workflowOPs, op, owlFileName);

//			Timer.startTimer();
            if (preferOp != null) {
                fnScore = this.getFnScore(preferOp, op, opOwlURI);
//				fnScore = this.getFnScore(preferOp, op, owlFileName);
            }
//			Timer.endTimer();

//			Timer.startTimer();
            if (initState != null) {
                peScore = this.getPeScore(initState, workflowOPs, op);

            }
//			Timer.endTimer();

            dmfnScore = (dmScore + fnScore) * 0.5;
            dmpeScore = (dmScore + peScore) * 0.5;
            fnpeScore = (fnScore + peScore) * 0.5;
            dmfnpeScore = this.weightDm * dmScore + this.weightFn * fnScore
                    + this.weightPe * peScore;
//			System.out.println(op.getOpName()+"="+score+"*****************" + op.getWsdlName());
//			System.out.println("dm="+dmScore);
//			System.out.println();
//			System.out.println("fn="+fnScore);
//			System.out.println("pe="+peScore);

//			if (score >= scoreThreshold) {
            OpWsdlScore op0Score = new OpWsdlScore(op.getOpName(), op.getWsdlName(), 0);
            OpWsdlScore opDmScore = new OpWsdlScore(op.getOpName(), op.getWsdlName(), dmScore);
            OpWsdlScore opFnScore = new OpWsdlScore(op.getOpName(), op.getWsdlName(), fnScore);
            OpWsdlScore opPeScore = new OpWsdlScore(op.getOpName(), op.getWsdlName(), peScore);
            OpWsdlScore opDmfnScore = new OpWsdlScore(op.getOpName(), op.getWsdlName(), dmfnScore);
            OpWsdlScore opDmpeScore = new OpWsdlScore(op.getOpName(), op.getWsdlName(), dmpeScore);
            OpWsdlScore opFnpeScore = new OpWsdlScore(op.getOpName(), op.getWsdlName(), fnpeScore);
            OpWsdlScore opDmfnpeScore = new OpWsdlScore(op.getOpName(), op.getWsdlName(), dmfnpeScore);
            suggestion0List.add(op0Score);
            suggestionDmList.add(opDmScore);
            suggestionFnList.add(opFnScore);
            suggestionPeList.add(opPeScore);
            suggestionDmfnList.add(opDmfnScore);
            suggestionDmpeList.add(opDmpeScore);
            suggestionFnpeList.add(opFnpeScore);
            suggestionDmfnpeList.add(opDmfnpeScore);
//			}
        }
        Collections.sort(suggestion0List, Collections.reverseOrder());
        Collections.sort(suggestionDmList, Collections.reverseOrder());
        Collections.sort(suggestionFnList, Collections.reverseOrder());
        Collections.sort(suggestionPeList, Collections.reverseOrder());
        Collections.sort(suggestionDmfnList, Collections.reverseOrder());
        Collections.sort(suggestionDmpeList, Collections.reverseOrder());
        Collections.sort(suggestionFnpeList, Collections.reverseOrder());
        Collections.sort(suggestionDmfnpeList, Collections.reverseOrder());

        EvUtils prt = new EvUtils();
//		prt.printFile(candidateOPs, suggestion0List, saveFileName, "nothing");
        prt.printFile(candidateOPs, suggestionDmList, saveFileName, "dm2/1");
//		prt.printFile(candidateOPs, suggestionFnList, saveFileName, "fn3");
//		prt.printFile(candidateOPs, suggestionPeList, saveFileName, "pe6");
        prt.printFile(candidateOPs, suggestionDmfnList, saveFileName, "dmfn5/4");
        prt.printFile(candidateOPs, suggestionDmpeList, saveFileName, "dmpe8/7");
//		prt.printFile(candidateOPs, suggestionFnpeList, saveFileName, "fnpe9");
        prt.printFile(candidateOPs, suggestionDmfnpeList, saveFileName, "dmfnpe11/10");
//		if (suggestionList.size() > maxSuggest) {
//			suggestionList = suggestionList.subList(0, maxSuggest);
//
//		}
        //return suggestionList;
    }

    /**
     * constructor
     */
    public ForwardSuggest() {
    }

    /**
     * test
     * 
     * @param args
     */
    public static void main(String[] args) {
        Timer.startTimer();
        ForwardSuggest test = new ForwardSuggest();
        OpWsdl wublastOp = new OpWsdl("runWUBlast", "wsdl/2/WSWUBlast.wsdl");
//		OpWsdl getidsOp = new OpWsdl("getIds","wsdl/3/WSWUBlast.wsdl");
//		OpWsdl getXmlFormatsOp = new OpWsdl("getXmlFormats","wsdl/3/WSWUBlast.wsdl");

        List<OpWsdl> workflowOpList = new ArrayList<OpWsdl>();
        List<OpWsdl> candidateOpList = new ArrayList<OpWsdl>();
        candidateOpList.add(new OpWsdl("getResults", "wsdl/2/WSWUBlast.wsdl"));
        candidateOpList.add(new OpWsdl("getResultTypes", "wsdl/2/clustalw2.wsdl"));

        workflowOpList.add(wublastOp);
//		candidateOpList.add(getidsOp);
//		candidateOpList.add(getXmlFormatsOp);
//		Ev3 ev = new Ev3();
//		candidateOpList= ev.getCandidateOp();

//		List<OpWsdlScore> suggestions = test.getSuggestServices(workflowOpList,candidateOpList,"getid", "owl/obi.owl","pl/initialState.pl" );
//		List<OpWsdlScore> suggestions = test.getSuggestServices(workflowOpList,candidateOpList,"getid", "owl/obi.owl",null );
        List<OpWsdlScore> suggestions = test.getSuggestServices(workflowOpList, candidateOpList, null, "owl/obi.owl", null);

//		List<OpWsdlScore> suggestions = test.getSuggestServices(workflowOpList,candidateOpList,null, null,null );
        System.out.println(suggestions.get(0).getOpName() + "----best----" + suggestions.get(0).getScore());
        System.out.println(suggestions.get(1).getOpName() + "----2nd----" + suggestions.get(1).getScore());

        Timer.endTimer();
    }
}
