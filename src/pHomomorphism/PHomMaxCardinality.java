
package pHomomorphism;


import static java.lang.System.out;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file). 
 */
public class PHomMaxCardinality implements PHomomorphism {
    
    private List<Match> mapping = new ArrayList<Match>();

    @Override
    public List<Match> getMapping()
    {
        return mapping;
    }//getMapping
    
    @Override
    public double calculatepHomSimScore(Boolean[][] G1, Boolean[][] G2, double[][] mappingScores, double threshHold, double[] w) {
        
        MaxCardinality maxCard = new MaxCardinality();
        maxCard.calcMaxCardMapping(G1, G2, mappingScores, threshHold);
        mapping = maxCard.getMapping();
        double rawScore = (double) mapping.size() / (double) G1.length;
        
        return rawScore;
    }//calculatepHomSimScore
    
    public static void main(String[] args)
    {
              Boolean[][] G1 = {
            //      1,     2,     3,     4,     5,     6
            /*1*/{false, true,  true,  false, false, false},
            /*2*/{false, false, false, true,  true,  false},
            /*3*/{false, false, false, false, false, true},
            /*4*/{false, false, false, false, false, false},
            /*5*/{false, false, false, false, false, false},
            /*6*/{false, false, false, false, false, false}
        };

        Boolean[][] G2 = {
            //     1,     2,     3,     4
            /*1*/{false, true,  false, true},
            /*2*/{false, false, true,  false},
            /*3*/{false, false, false, false},
            /*4*/{false, false, false, false}
        };

        double[][] mat = {
         //G1\G2-> 1,   2,   3,   4
            /*1*/{0.6, 0.4, 0.3, 0.2},
            /*2*/{0.6, 0.8, 0.3, 0.2},
            /*3*/{0.2, 0.3, 0.2, 0.3},
            /*4*/{0.4, 0.3, 0.7, 0.4},
            /*5*/{0.3, 0.3, 0.3, 0.8},
            /*6*/{0.2, 0.2, 0.2, 0.15}
        };

        double threshHold = 0.4;
        double[] w = new double[10];
        PHomomorphism p = new PHomMaxCardinality();
        
        out.println(p.calculatepHomSimScore(G1, G2, mat, threshHold, w));
        Util.printMatches(p.getMapping());
        
        
    }//main

}
