
package pHomomorphism;


import static java.lang.System.out;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file).
 * Yet to be implemented
 */
public class PHomMaxSimilarity implements PHomomorphism {
    
    public PHomMaxSimilarity()
    {
    
    }
            

    @Override
    public double calculatepHomSimScore(Boolean[][] G1, Boolean[][] G2, double[][] mappingScores, double threshHold, double[] w) {
        
        return 0;
    }

    @Override
    public List<Match> getMapping() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getRawScore() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getWeightedSimScore() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getWeightedScore() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double calculatepHomSimScore(Boolean[][] G1, Boolean[][] G2, double[][] mappingScores, double threshHold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double calculatepHomSimScore(HashMap<Integer, ArrayList<Integer>> G1, HashMap<Integer, ArrayList<Integer>> G2, double[][] mappingScores, double threshHold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double calculatepHomSimScore(HashMap<Integer, ArrayList<Integer>> G1, HashMap<Integer, ArrayList<Integer>> G2, double[][] mappingScores, double threshHold, double[] w) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
