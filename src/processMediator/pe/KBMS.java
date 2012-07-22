
package processMediator.pe;

import java.util.Hashtable;

import jpl.PrologException;
import jpl.Query;

/**knowledge base management system, provides methods to query knowledge base or 
 * update knowledge base, Currently not used
 * @author Rui
 * Summer 2010
 *
 */
public class KBMS {
	
	/**
	 * update current state (prolog KB) with given effect (prolog assert)
	 * @param effect
	 * @return true if success, else false
	 */
	public boolean updateState(String effect){
		boolean status=false;
		try{
		Query q = new Query(effect);
//		System.out.println(  q.toString());
			status = true;
//		System.out.println("Successfully updated current state!" );
//		Query q1 = new Query ("listing.");
		while ( q.hasMoreSolutions() ){
			Hashtable s = q.nextSolution();
//			System.out.println(s.keySet()+"========>"+  s.values());
		}
//		q1.close();
		q.close();
		}
		catch(PrologException e){
			System.out.println( "Failed updating current state!" );
//			System.out.println(e);
			return false;
		}
		return status;
		
	}
	
	/**
	 * check whether the given precondition (prolog statement) is entailed by current state, which is a KB presented by prolog
	 * not true or not exist both are considered not entailed
	 * @param precondition   precondition described as prolog query
	 * @return true if entailed, false if not
	 */
	public boolean isEntail(String precondition){
		boolean entail =false;
		try {
		Query q = new Query(precondition);
//		System.out.println(  q.toString());
		if (q.hasSolution()){
			entail = true;
//			System.out.println("Given precondition is entailed by the current state!");
//			q = new Query("listing");
			while ( q.hasMoreSolutions() ){
				Hashtable s = q.nextSolution();
//				System.out.println(s.keySet()+"========>"+  s.values());
			}
		}
		else {
			System.out.println("False: Given precondition is NOT entailed by the current state!");
		}
		 q.close();
		 }
		catch(PrologException e){
//			System.out.println(e);
			System.out.println("Exception: Given precondition is NOT entailed by the current state!");
			
			return false;
		}
		
		
		return entail;
	}

	/**
	 * constructor
	 * load initial state prolog file
	 */
	public KBMS(String initStateFilePath) {
            String fileUrl =Thread.currentThread().getContextClassLoader().getResource(initStateFilePath).toString();
//            String t = "consult('"+initStateFilePath+"')";
String t = "consult('"+fileUrl+"')";
		Query q = new Query(t);
		if ( !q.hasSolution() ){
			System.out.println( "Failed loading initial state!" );
//			System.exit( 1 );
		}
//		else{
//		System.out.println(  "Succeeded loading initial state!" );
//		}
		q.close();
	}

	/**
	 * constructor without initial prolog file
	 */
	public KBMS (){
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		KBMS test = new KBMS("pl/initialState.pl");
//		test.isEntail("false");
		
		System.out.println("--------------------blast--------------");
		/*===================================
		wublast
		*/
		String preBlast = "hasEmail,hasDb(Db),hasSequence(Seq),hasProgram(Program),(isBlastp(Program);isBlastn(Program);isBlastx(Program);isTblastn(Program)).";
			//,(isBlastp(Program);isBlastn(Program);isBlastx(Program);isTblastn(Program)).";
		String effBlast = "hasWublastjobid(wublastjobid)).";
		test.isEntail(preBlast);
		test.updateState(effBlast);
//		test.isEntail("hasEmail,hasDb(Db),hasSequence(Seq),hasProgram(Program),(Program==blastp)->((Seq==proteinSeq),(Db==proteinDb));(Program==(blastn;tblastx))->(not(Seq==proteinSeq),not(Db==proteinDb));(Program==blastx)->(not(Seq==proteinSeq),(Db==proteinDb));(Program==tblastn)->((Seq==proteinSeq),not(Db==proteinDb)).");
//		test.updateState("assertz(hasBlastJobid).");
		System.out.println("-----------------getIds-----------------");

		/*===================================
		getIds
		*/
//		test.isEntail("hasBlastJobid.");
//		test.updateState("assertz(hasBlastHitIds(blasthitid)), assertz(isStringArray(blasthitid)),assertz(isHomolog(blasthitid)).");
		System.out.println("-------------------array2string---------------");

		/*===================================
		converter
		*/
		
//		test.isEntail("isStringArray(Input).");
//		test.updateState("assertz((isCommaString(convertedids)))." );
//				test.updateState("assertz((isHomolog(convertedids):- (isHomolog(Input),isStringArray(Input)))).");
		System.out.println("----------------fetchBatch------------------");

		/*===================================
		fetchBatch
		*/
//		test.isEntail("isCommaString(Ids),hasDb(Db).");
//		test.updateState("assertz(hasSequence(fetchbatchoutsequence)), assert(isString(fetchbatchoutsequence)). " );
//		test.updateState("(assertz(isHomolog(fetchbatchoutsequence):- (isHomolog(Ids),isCommaString(Ids)))).");

		
		
		

//		System.out.println("--------------clustalw--------------------");
//
//		
//		/*===================================
//		clustalw   
//		*/
//		test.isEntail("hasEmail,hasSequence(ClustalwInputSeq),isString(ClustalwInputSeq),(isHomolog(ClustalwInputSeq);(isMSA(ClustalwInputSeq))).");
//		test.updateState("assertz(hasClustalwJobid).");
//
//		System.out.println("----------------poll------------------");
//
//		/*===================================
//		 poll
//		*/
//		test.isEntail("hasClustalwJobid,hasType(Resulttype).");
//		test.updateState("assertz(isByte(clustalwTree)). ");
//				test.updateState("assertz(isTree(clustalwTree):- (hasSequence(ClustalwInputSeq),isMSA(ClustalwInputSeq),hasType(Resulttype),(Resulttype == toolph))). assertz(isMSA(clustalwMSA):- (hasSequence(ClustalwInputSeq),isHomolog(ClustalwInputSeq),hasType(Resulttype),(Resulttype == toolaln))). assertz(hasSequence(clustalwMSA):- isMSA(clustalwMSA), not(isTree(clustalwTree))).");
//		System.out.println("---------------byte2string-------------------");
//
//		/*===============================
//		byte2string
//		*/
//		test.isEntail("isByte(Input).");
////		test.updateState("(isHomolog(Input),isByte(Input)) -> (assertz(isHomolog(convertedHomologString)),assertz(hasSequence(convertedHomologString)), assertz(isString(convertedHomologString))). " );
//		test.updateState("(isMSA(Input),isByte(Input)) -> (assertz(isMSA(convertedMSAString)),assertz(hasSequence(convertedMSAString)),assertz(isString(convertedMSAString))); " +
//				"((isTree(Input),isByte(Input)) -> (assertz(isTree(convertedTreeString)),assertz(isString(convertedTreeString)))).");
		
				
				
		
//		
//		
//		
//	System.out.println("---------------clustalw-------------------");
//
//		
//		/*===================================
//		clustalw   
//		*/
//		test.isEntail("hasEmail,hasSequence(ClustalwInputSeq),isString(ClustalwInputSeq),(isHomolog(ClustalwInputSeq);(isMSA(ClustalwInputSeq))).");
////		test.updateState("assertz(hasClustalwJobid).");
//
//		System.out.println("--------------poll--------------------");
//
//		/*===================================
//		 poll
//		*/
//		test.isEntail("hasClustalwJobid,hasType(Resulttype).");
////		test.updateState("((hasSequence(ClustalwInputSeq),isMSA(ClustalwInputSeq),hasType(Resulttype),(Resulttype == toolph))-> (assertz(isTree(clustalwTree)), assertz(isByte(clustalwTree)))); ((hasSequence(ClustalwInputSeq),isHomolog(ClustalwInputSeq),hasType(Resulttype),(Resulttype == toolaln))-> (assertz(hasSequence(clustalwMSA)), assertz(isMSA(clustalwMSA)),assertz(isByte(clustalwMSA)))).");
//		System.out.println("----------------byte2string------------------");
//
//		/*===============================
//		byte2string
//		*/
//		test.isEntail("isByte(Input).");
////		test.updateState("(isHomolog(Input),isByte(Input)) -> (assertz(isHomolog(convertedHomologString)),assertz(hasSequence(convertedHomologString)), assertz(isString(convertedHomologString))). " );
////		test.updateState("(isMSA(Input),isByte(Input)) -> (assertz(isMSA(convertedMSAString)),assertz(hasSequence(convertedMSAString)),assertz(isString(convertedMSAString))); ((isTree(Input),isByte(Input)) -> (assertz(isTree(convertedTreeString)),assertz(isString(convertedTreeString)))).");
//		
//		
//
//		System.out.println("---------------clustalw-------------------");
//
//			
//			/*===================================
//			clustalw   
//			*/
//			test.isEntail("hasEmail,hasSequence(ClustalwInputSeq),isString(ClustalwInputSeq),(isHomolog(ClustalwInputSeq);(isMSA(ClustalwInputSeq))).");
////			test.updateState("assertz(hasClustalwJobid).");
//
//			System.out.println("--------------poll--------------------");
//
//			/*===================================
//			 poll
//			*/
//			test.isEntail("hasClustalwJobid,hasType(Resulttype).");
////			test.updateState("((hasSequence(ClustalwInputSeq),isMSA(ClustalwInputSeq),hasType(Resulttype),(Resulttype == toolph))-> (assertz(isTree(clustalwTree)), assertz(isByte(clustalwTree)))); ((hasSequence(ClustalwInputSeq),isHomolog(ClustalwInputSeq),hasType(Resulttype),(Resulttype == toolaln))-> (assertz(hasSequence(clustalwMSA)), assertz(isMSA(clustalwMSA)),assertz(isByte(clustalwMSA)))).");
//			System.out.println("----------------byte2string------------------");
//
//			/*===============================
//			byte2string
//			*/
//			test.isEntail("isByte(Input).");
////			test.updateState("(isHomolog(Input),isByte(Input)) -> (assertz(isHomolog(convertedHomologString)),assertz(hasSequence(convertedHomologString)), assertz(isString(convertedHomologString))). " );
////			test.updateState("(isMSA(Input),isByte(Input)) -> (assertz(isMSA(convertedMSAString)),assertz(hasSequence(convertedMSAString)),assertz(isString(convertedMSAString))); ((isTree(Input),isByte(Input)) -> (assertz(isTree(convertedTreeString)),assertz(isString(convertedTreeString)))).");
//			
//			
		
//		System.out.println("----------------------------------");
		//final goal
//		test.isEntail("isTree(X), isString(X).");
	
	}

}
