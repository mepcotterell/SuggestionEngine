/**
 * 
 */
package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Rui Wang
 *
 */
public class OpWsdlScore extends OpWsdl implements Comparable<OpWsdlScore>{

	//rank score of this operation
	private double score;
        private double dmScore;
        private double fnScore;
        private double peScore;
	
	/**
	 * @return the score
	 */
	public double getScore() {
            return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}

	
/* able to compare double or int
 * (non-Javadoc)
 * @see java.lang.Comparable#compareTo(java.lang.Object)
 */
@Override
	public int compareTo(OpWsdlScore o) {
//            int compare = 0;
//    double result = this.getScore()-o.getScore();
//    if (result > 0){
//            compare =1;
//    }
//    else if(result <1){
//            compare = -1;
//    }
//    return  compare;
    
            Double a = getScore();
            return a.compareTo(o.getScore());
    
	}
	
	/**
	 * @param op
	 * @param wsdl
	 * @param score
	 */
	public OpWsdlScore(String op, String wsdl, double score) {
		super(op, wsdl);
		this.score = score;
		
	}
	


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<OpWsdlScore> currentProcess = new ArrayList<OpWsdlScore>();
		currentProcess.add(new OpWsdlScore("runWUBlast", "wsdl/1/WSWUBlast.wsdl", 0.3));
		currentProcess.add(new OpWsdlScore("getIds", "wsdl/1/WSWUBlast.wsdl", 0.4));
		currentProcess.add(new OpWsdlScore("array2string", "wsdl/1/WSConverter.wsdl",0.6));		
		currentProcess.add(new OpWsdlScore("fetchBatch", "wsdl/1/WSDbfetchDoclit.wsdl", 0));
		currentProcess.add(new OpWsdlScore("run", "wsdl/1/clustalw2.wsdl",0.5));
		currentProcess.add(new OpWsdlScore("getResult", "wsdl/1/clustalw2.wsdl",1));		
		currentProcess.add(new OpWsdlScore("base64toString", "wsdl/1/WSConverter.wsdl",0.7));
		
		Collections.sort(currentProcess);
		for (OpWsdlScore op : currentProcess){
			System.out.println(op.getScore());
		}
	}

    /**
     * @return the dmScore
     */
    public double getDmScore() {
        return dmScore;
    }

    /**
     * @param dmScore the dmScore to set
     */
    public void setDmScore(double dmScore) {
        this.dmScore = dmScore;
    }

    /**
     * @return the fnScore
     */
    public double getFnScore() {
        return fnScore;
    }

    /**
     * @param fnScore the fnScore to set
     */
    public void setFnScore(double fnScore) {
        this.fnScore = fnScore;
    }

    /**
     * @return the peScore
     */
    public double getPeScore() {
        return peScore;
    }

    /**
     * @param peScore the peScore to set
     */
    public void setPeScore(double peScore) {
        this.peScore = peScore;
    }

	


}
