/**
 * 
 */
package suggest.calculator;

import java.util.ArrayList;
import java.util.List;

import parser.WsdlSparser;
import processMediator.pe.KBMS;

import util.OpWsdl;
import util.Timer;

/**
 * @author Rui
 * 
 */
public class PeScore {

	/**check whether current state entails precondition of candidate operation
	 * given initial state to initialize current state, 
	 * and then update current state with effect of operations in the given workflow iteratively 
	 * then
	 * test if current state entail precondition of candidate operation, 
	 * return 1 
	 * else return 0 
	 * if candidate op has no precondition, return 0
	 * 
	 * @param initState    initial state file name, can be null
	 * @param workflowOPs    all operations in current workflow, allow some operations have no effect
	 * @param candidateOP    candidate operation, if no precondition, score=0
	 * @return
	 */
	public double calculatePeScore(String initState, List<OpWsdl> workflowOPs,
			OpWsdl candidateOP) {
		
		if (workflowOPs == null || candidateOP == null ){
		return 0;	
		}
		
		WsdlSparser wsdls = new WsdlSparser();
		String precondition = wsdls.getOpPrecondition(candidateOP.getOpName(),
				candidateOP.getWsdlName());
		
		if (precondition == null)
			return 0;
		
		double peScore = 0;		
		KBMS state;
		if (initState==null) {
			//if no initial state, score=0 or not? 
//			state = new KBMS();
			return 0;
		} else {

			state = new KBMS(initState);
		}

		if (workflowOPs != null) {
			//compute current state for the given workflow
			for (OpWsdl opW : workflowOPs) {
				String effect = wsdls.getOpEffect(opW.getOpName(), opW
						.getWsdlName());
				//we assume state of every step will entail the precondition of succeeding op, so we only warning if not entail
				String currentPre = wsdls.getOpPrecondition(opW.getOpName(),
						opW.getWsdlName());
				if (!state.isEntail(currentPre)) {
					//strict checking states of current process, can be loosen as only warning without affect pe score
					System.out.println("Spe score is set to 0: in current process, one state does not entail precondition of succeeding op = " + opW.getOpName());
					return 0;
				}
					// if any of op in workflow has no effect,
				// current state may be a subset of what it should be, 
				//then less likely to entail the precondition of candidate op
			if (effect!=null) {
					state.updateState(effect);

				}
			}
		}

		
		if (state.isEntail(precondition)) {
			peScore = 1;
		}

		return peScore;
	}

	/**
	 * constructor
	 */
	public PeScore() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Timer.startTimer();
		PeScore test = new PeScore();
		String iniState = "pl/initialState.pl";
//		OpWsdl cop = new OpWsdl("getIds", "wsdl/2/WSWUBlast.wsdl");
//		OpWsdl cop = new OpWsdl("array2string", "wsdl/2/WSConverter.wsdl");
		OpWsdl cop = new OpWsdl("fetchBatch", "wsdl/2/WSDbfetchDoclit.wsdl");
//		currentProcess.add(new OpWsdl("run", "wsdl/2/clustalw2.wsdl"));
//		currentProcess.add(new OpWsdl("getResult", "wsdl/2/clustalw2.wsdl"));
		
		List<OpWsdl> ops = new ArrayList<OpWsdl>();
		ops.add(new OpWsdl("runWUBlast", "wsdl/2/WSWUBlast.wsdl"));
		ops.add(new OpWsdl("getIds", "wsdl/2/WSWUBlast.wsdl"));
		ops.add(new OpWsdl("array2string", "wsdl/2/WSConverter.wsdl"));

		
		
		double score = test.calculatePeScore(iniState, ops, cop);
		System.out.println(score);
		Timer.endTimer();
	}

}
