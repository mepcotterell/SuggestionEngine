
package pHomomorphism;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * All the interactions with this package from outside are specified in this interface
 * 
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file). 
 * 
 * 
 */

public interface PHomomorphism {
    
    /**
     * Method to Calculate pHom Similarity Score between Graphs G1 and G2 
     * 
     * @param G1 Adjacency Matrix Representation of Graph1
     * @param G2 Adjacency Matrix Representation of Graph2
     * @param mappingScores Matrix that stores Match scores for every node v in
     * G1 to every node v' in graph V2
     * @param w An array that stores weights of the nodes in Graph 1
     * @param threshHold value between 0-1 s.t. v-v' matches scoring above it will only be considered
     * @return weightedScore
     */
     public double calculatepHomSimScore
             (Boolean[][] G1, Boolean[][] G2, double[][] mappingScores, double threshHold, double[] w);
     
         
    /**
     * Method to Calculate pHom Similarity Score between Graphs G1 and G2, without considering Weighting
     * 
     * @param G1 Adjacency Matrix Representation of Graph1
     * @param G2 Adjacency Matrix Representation of Graph2
     * @param mappingScores Matrix that stores Match scores for every node v in
     * G1 to every node v' in graph V2
     * @param threshHold value between 0-1 s.t. v-v' matches scoring above it will only be considered
     * @return rawScore
     */
     public double calculatepHomSimScore
             (Boolean[][] G1, Boolean[][] G2, double[][] mappingScores, double threshHold);
     
    /**
     * Method to Calculate pHom Similarity Score between Graphs G1 and G2, without considering Weighting
     * 
     * @param G1 Adjacency List Representation of Graph1
     * @param G2 Adjacency List Representation of Graph2
     * @param mappingScores Matrix that stores Match scores for every node v in
     * G1 to every node v' in graph V2
     * @param threshHold value between 0-1 s.t. v-v' matches scoring above it will only be considered
     * @return rawScore
     */
     public double calculatepHomSimScore
             (HashMap<Integer, ArrayList<Integer>> G1,HashMap<Integer, ArrayList<Integer>> G2, double[][] mappingScores, double threshHold);         

         
     /**
     * Method to Calculate pHom Similarity Score between Graphs G1 and G2, without considering Weighting
     * 
     * @param G1 Adjacency List Representation of Graph1
     * @param G2 Adjacency List Representation of Graph2
     * @param mappingScores Matrix that stores Match scores for every node v in
     * G1 to every node v' in graph V2
     * @param threshHold value between 0-1 s.t. v-v' matches scoring above it will only be considered
     * @param w An array that stores weights of the nodes in Graph 1
     * @return weightedScore
     */
     public double calculatepHomSimScore
             (HashMap<Integer, ArrayList<Integer>> G1,HashMap<Integer, ArrayList<Integer>> G2, double[][] mappingScores, double threshHold, double[] w);         

     
    /**
     * Returns the Raw Score which represents fraction of nodes of Graph1 
     * that could be mapped to nodes in Graph2
     * 
     * @return RawScore
     */
     public double getRawScore();
     
    /**
     * Score that serves as a similarity measure between G1 and G2 as 
     * it also considers how close are the two nodes that are mapped.
     * @return the weightedSimScore
     */

    public double getWeightedSimScore();

    /**
     * Weighted fraction of number of nodes in G1 mapped to nodes in G2; 
     * Does not consider how closely are the nodes matched, once the value is above the specified threshold.
     * @return the weightedScore
     */
    public double getWeightedScore();
    
    /**
     * Returns the Mapping between nodes of Graph1 and Graph2
     * 
     * @return List of matches <G1node, G2node>
     */
     public List<Match> getMapping();

    
}//interface PHomomorphism



