package util;

/**
 * Class With Methods to generate a geometric series of desired length
 *
 * @author Rui Wang
 * @see LICENSE (MIT style license file).
 */
public class GeometricSeries {

    public GeometricSeries() {
    }

    /**
     * Create geometric series to generate weights. 
     * s=a(1-q^)/(1-q) a2=q*a1 here we define: s=1, q=1/2
     * 
     * @param total length of the Geometric series to be generated
     * @return 
     */
    public double[] getWeights(int total) {

        double q = 0.5;

        double[] weights = new double[total];

        weights[0] = (1 - q) / (1 - Math.pow(q, total));

        for (int i = 0; i < total - 1; i++) {
            weights[i + 1] = q * weights[i];
        }

        return weights;
    }

    public static void main(String[] args) {
        GeometricSeries test = new GeometricSeries();
        double[] w1 = test.getWeights(2);
        System.out.println(w1[0]);
        System.out.println(w1[1]);


    }
}
