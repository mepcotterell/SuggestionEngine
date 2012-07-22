
package pHomomorphism;

/**
 *
 * Class to represent a candidate match between two Graphs.
 * 
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file). 
 * 
 */
public class Match {

    Integer g1node;
    Integer g2node;

    /**
     * Constructor that sets up a candidate match Between G1node in Graph 1 with G2node in Graph2
     * @param G1node
     * @param G2node 
     */
    public Match(Integer G1node, Integer G2node) {
        g1node = G1node;
        g2node = G2node;
    }//constructor
    
}//Match
