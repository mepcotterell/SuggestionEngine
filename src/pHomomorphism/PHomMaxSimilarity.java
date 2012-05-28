
package pHomomorphism;


import static java.lang.System.out;
import java.util.List;

/**
 *
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file). 
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

}
