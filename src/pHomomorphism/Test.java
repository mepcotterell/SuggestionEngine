
package pHomomorphism;


import static java.lang.System.out;

/**
 * 
 * A class to test the functionality provided by this package.
 * 
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file). 
 * 
 */
public class Test {

    
    public static void main(String[] args)
    {
     
        //Test code
        //Test 1 : Passed Against Manual Calculations
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
        double[] w = {0.5, 0.7, 0.2, 1.0, 0.2, 0.2};

        PHomomorphism phom = new PHomMaxCardinality();
        phom.calculatepHomSimScore(G1, G2, mat, threshHold, w);

        out.println("Test 1\n"
                + "-------------------------------------------------------------");
        out.println("Graph 1 : ");
        Util.printGraph(G1);
        out.println("\nGraph 2 : ");
        Util.printGraph(G2);
        out.println("Raw Score                   = " + phom.getRawScore());
        out.println("Weighted Score              = " + phom.getWeightedScore());
        out.println("Weighted Similarity measure = " + phom.getWeightedSimScore());
        out.println();
        Util.printMatches(phom.getMapping());
        
        //----------------------------------------------------------------------
        //Test 2 : Passed
 
        threshHold = 0.55;
        G1 = new Boolean[][] {
            {false, true , true , false},
            {false, false, false, true },
            {false, false, false, false},
            {false, false, false, false},    };
        
        G2 = new Boolean[][] {
            {false, true , true},
            {false, false, false},
            {false, false, false}          
    };
        
        mat = new double[][]{
         //G1\G2-> 0,   1,   2, 
            /*0*/{0.8, 0.5, 0.6},
            /*1*/{0.5, 0.4, 0.9},
            /*2*/{0.4, 0.9, 0.5},
            /*3*/{0.4, 0.3, 0.3},
        };
        
        double[] w1 ={.5, .8, 0.7, .9}; 
        w = w1;
        
        phom = new PHomMaxCardinality();
        phom.calculatepHomSimScore(G1, G2, mat, threshHold, w);

        out.println("\n\n-------------------------------------------------------------\n"
                + "Test 2\n"
                + "-------------------------------------------------------------");
        out.println("Graph 1 : ");
        Util.printGraph(G1);
        out.println("\nGraph 2 : ");
        Util.printGraph(G2);
        out.println("Raw Score                   = " + phom.getRawScore());
        out.println("Weighted Score              = " + phom.getWeightedScore());
        out.println("Weighted Similarity measure = " + phom.getWeightedSimScore());
        out.println();
        Util.printMatches(phom.getMapping());
        
        //----------------------------------------------------------------------
        //----------------------------------------------------------------------
        //Test 3 : Passed for Different variations of mat
 
        threshHold = 0.6;
        G1 = new Boolean[][] {
        //     0,    1,      2,     3,     4
        /*0*/{false, true , true , false, false},
        /*1*/{false, false, false, true,  true },
        /*2*/{false, false, false, false, false},
        /*3*/{false, false, false, false, false},
        /*4*/{false, false, false, false, false},
        };
        
        G2 = new Boolean[][] {
            {false, true , true , false},
            {false, false, false, true},
            {false, false, false, false},
            {false, false, false, false}            
    };
        
        mat = new double[][]{
         //G1\G2-> 0,   1,   2,   3
            /*0*/{0.9, 0.7, 0.3, 0.2},
            /*1*/{0.0, 0.8, 0.3, 0.1},
            /*2*/{0.2, 0.3, 0.2, 0.1},
            /*3*/{0.2, 0.4, 0.9, 0.3},
            /*4*/{0.3, 0.5, 0.3, 0.8}
        };

                
        double[] w2 ={.5, .8, 0.7, .9, .8}; 
        w = w2;
        
        phom = new PHomMaxCardinality();
        phom.calculatepHomSimScore(G1, G2, mat, threshHold,w);

        out.println("\n\n-------------------------------------------------------------\n"
                + "Test 3\n"
                + "-------------------------------------------------------------");
        out.println("Graph 1 : ");
        Util.printGraph(G1);
        out.println("\nGraph 2 : ");
        Util.printGraph(G2);
        out.println("Raw Score                   = " + phom.getRawScore());
        out.println("Weighted Score              = " + phom.getWeightedScore());
        out.println("Weighted Similarity measure = " + phom.getWeightedSimScore());
        out.println();
        Util.printMatches(phom.getMapping());
        
        //----------------------------------------------------------------------
        
        
    }//main
    
    
}
