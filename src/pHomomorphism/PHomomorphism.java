
package pHomomorphism;

import java.util.List;

/**
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file). 
 */

public interface PHomomorphism {
    
     public double calculatepHomSimScore
             (Boolean[][] G1, Boolean[][] G2, double[][] mappingScores, double threshHold, double[] w);
     
     public List<Match> getMapping();

    
}
