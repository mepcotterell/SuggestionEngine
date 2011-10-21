/**
 * 
 */
package util;

import util.GeometricSeries;
import util.GeometricSeries;

/**
 * apply geometric series to generate weights
 * @author Rui Wang
 *
 */
public class GeometricSeries {

	
	
	/**
	 * 
	 */
	public GeometricSeries() {
	}

	/* (non-Javadoc)
	 * @see util.interfaces.GeometricSeries#getWeights(int)
	 * 
	 * apply geometric series to generate weights
	 * s=a(1-q^)/(1-q)
	 * a2=q*a1
	 * here we define: s=1, q=1/2
	 */
	public double[] getWeights(int total) {
		
		double q = 0.5;
		
		double [] weights = new double [total];
                
		weights [0]= (1-q)/(1-Math.pow(q, total));
                
		for (int i = 0; i<total-1; i++){
			weights[i+1]=q*weights[i];
		}

		return weights;
	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GeometricSeries test =new GeometricSeries();
		double[] w1 = test.getWeights(2);
		System.out.println(w1[0]);
		System.out.println(w1[1]);

		
	}

}
