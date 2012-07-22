
package pHomomorphism;

import static java.lang.System.out;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * This class Implements all the methods of the interface PHomomorphism.
 * 
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE(MIT style license file). 
 * 
 */
public class PHomMaxCardinality implements PHomomorphism {
    
    private List<Match> mapping = new ArrayList<Match>();
    private double rawScore = 0.0;
    private double weightedScore = 0.0;
    private double weightedSimScore = 0.0;

        
    /**
     * Score that serves as a similarity measure between G1 and G2 as 
     * it also considers how close are the two nodes that are mapped.
     * @return the weightedSimScore
     */
    @Override
    public double getWeightedSimScore() {
        return weightedSimScore;
    }//weightedSimScore


    /**
     * Weighted fraction of number of nodes in G1 mapped to nodes in G2; 
     * Does not consider how closely are the nodes matched, once the value is above the specified threshold.
     * @return the weightedScore
     */
    @Override
    public double getWeightedScore() {
        return weightedScore;
    }//getWeightedScore
    
    /**
     * Returns the Mapping between nodes of Graph1 and Graph2
     * 
     * @return List of matches <G1node, G2node>
     */
    @Override
    public List<Match> getMapping()
    {
        return mapping;
    }//getMapping
    
    /**
     * Returns the Raw Score which represents fraction of nodes of Graph1 
     * that could be mapped to nodes in Graph2
     * 
     * @return RawScore
     */
    @Override
    public double getRawScore() {
        return rawScore;
    }//getRawScore
    
    /**
     * Calculates p-Hom Mapping from Nodes of G1 to G2. 
     * i.e. The Direction of p-Homomorphism is G1 p-HOM G2
     * Tries to map nodes of Graph G1 to nodes in Graph G2
     *
     * @param G1 Adjacency Matrix Representation of Graph1
     * @param G2 Adjacency Matrix Representation of Graph2
     * @param mappingScores Matrix that stores Match scores for every node v in
     * G1 to every node v' in graph V2
     * @param w An array that stores weights of the nodes in Graph 1
     * @param threshHold value between 0-1 s.t. v-v' matches scoring above it will only be considered
     * 
     * @return weightedScore
     */
    @Override
    public double calculatepHomSimScore(Boolean[][] G1, Boolean[][] G2, double[][] mappingScores, double threshHold, double[] w) 
    {
        //Validating Inputs
        if (Double.isNaN(threshHold) || threshHold <= 0 || threshHold > 1)
        {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "Value of threshold shuold be between 0 and 1; Setting threshold to default of 0.5 ..!!");
            threshHold = 0.5;
        }
        if(G1 == null || G2 == null || mappingScores == null || w==null)
        {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Matrices G1, G2, mappingScores or array w cannot be null, program cant Continue..!");
            return -1;
        }            
        if(G1.length != G1[0].length)
        {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Matrix G1 needs to be a square Matrix, program cant Continue..!");
            return -1;
        }
        if(G2.length != G2[0].length)
        {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Matrix G2 needs to be a square Matrix, program cant Continue..!");
            return -1;
        }
        if(mappingScores.length != G1.length || mappingScores[0].length != G2.length)
        {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "The Mapping Scores matrix is of incorrectOrder, program cant Continue..!!");
            return -1;        
        }
        if(w.length != G1.length)
        {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "Size of weights array does not match with the size of Graph 1, All weights set to 1!!");
            double[] wNew = new double[G1.length];
            for(int i=0 ; i< G1.length; i++)
                wNew[i] = 1;
            w=wNew;
        }
        
        MaxCardinality maxCard = new MaxCardinality();
        maxCard.calcMaxCardMapping(G1, G2, mappingScores, threshHold);
        mapping = maxCard.getMapping();
        
        double numr  = 0.0;
        double numrSim  = 0.0;
        double denom = 0.0;
        
        for(Match m : mapping)
        {
            Integer i = m.g1node;
            numrSim  += (w[i] * mappingScores[i][m.g2node]);
            numr  += w[i];
        }//for
        
        for(int j=0; j< w.length; j++)
            denom+= w[j];

        //Weighted fraction of no. of nodes in G1 mapped to nodes in G2; 
        weightedScore = numr / denom;        
        
        // Score that serves as a similarity measure between G1 and G2
        weightedSimScore = numrSim / denom;

        //Raw Score Represents fraction of nodes of Graph1 that could be mapped to nodes in Graphe
        rawScore = (double) mapping.size() / (double) G1.length;
        
        return weightedScore;
    }//calculatepHomSimScore
    
    /**
     * Calculates p-Hom Mapping from Nodes of G1 to G2. 
     * i.e. The Direction of p-Homomorphism is G1 p-HOM G2
     * Tries to map nodes of Graph G1 to nodes in Graph G2
     *
     * @param G1 Adjacency Matrix Representation of Graph1
     * @param G2 Adjacency Matrix Representation of Graph2
     * @param mappingScores Matrix that stores Match scores for every node v in
     * G1 to every node v' in graph V2
     * @param threshHold value between 0-1 s.t. v-v' matches scoring above it will only be considered
     * 
     * @return rawScore
     */        
    @Override
    public double calculatepHomSimScore(Boolean[][] G1, Boolean[][] G2, double[][] mappingScores, double threshHold) 
    {
        //Validating Inputs
        if (Double.isNaN(threshHold) || threshHold <= 0 || threshHold > 1)
        {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "Value of threshold shuold be between 0 and 1; Setting threshold to default of 0.5 ..!!");
            threshHold = 0.5;
        }
        if(G1 == null || G2 == null || mappingScores == null)
        {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Matrices G1, G2, mappingScores or array w cannot be null, program cant Continue..!");
            return -1;
        }         
        if(G1.length != G1[0].length)
        {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Matrix G1 needs to be a square Matrix, program cant Continue..!");
            return -1;
        }
        if(G2.length != G2[0].length)
        {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Matrix G2 needs to be a square Matrix, program cant Continue..!");
            return -1;
        }
        if(mappingScores.length != G1.length || mappingScores[0].length != G2.length)
        {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "The Mapping Scores matrix is of incorrectOrder, program cant Continue..!!");
            return -1;        
        }

        MaxCardinality maxCard = new MaxCardinality();
        maxCard.calcMaxCardMapping(G1, G2, mappingScores, threshHold);
        mapping = maxCard.getMapping();
        
        //Raw Score Represents fraction of nodes of Graph1 that could be mapped to nodes in Graphe
        rawScore = (double) mapping.size() / (double) G1.length;
        
        return rawScore;
    }//calculatepHomSimScore
    
    /**
     * Calculates p-Hom Mapping from Nodes of G1 to G2. 
     * i.e. The Direction of p-Homomorphism is G1 p-HOM G2
     * Tries to map nodes of Graph G1 to nodes in Graph G2
     *
     * @param G1 Adjacency List Representation of Graph1
     * @param G2 Adjacency List Representation of Graph2
     * @param mappingScores Matrix that stores Match scores for every node v in
     * G1 to every node v' in graph V2
     * @param threshHold value between 0-1 s.t. v-v' matches scoring above it will only be considered
     * 
     * @return rawScore
     */         
    @Override
    public double calculatepHomSimScore
            (HashMap<Integer, ArrayList<Integer>> G1, HashMap<Integer, ArrayList<Integer>> G2, double[][] mappingScores, double threshHold) 
    {
        Boolean[][] G1mat = new Boolean[G1.size()][G1.size()];
        Boolean[][] G2mat = new Boolean[G2.size()][G2.size()];
        
        //Initialize Matrix G1
        for (Integer i = 0; i < G1.size(); i++) 
            for (Integer j = 0; j < G1.size(); j++) 
                G1mat[i][j] = false;

        //Populate G1 with values from Adjacency List
        for (Integer i = 0; i < G1.size(); i++) 
            for (Integer m : G1.get(i)) 
                G1mat[i][m] = true;
        
        //Initialize Matrix G2
        for (Integer i = 0; i < G2.size(); i++) 
            for (Integer j = 0; j < G2.size(); j++) 
                G2mat[i][j] = false;

        //Populate G2 with values from Adjacency List
        for (Integer i = 0; i < G2.size(); i++) 
            for (Integer m : G2.get(i)) 
                G2mat[i][m] = true;        
        
        return this.calculatepHomSimScore(G1mat, G2mat, mappingScores, threshHold);
        
    }//calculatepHomSimScore
    
    /**
     * Calculates p-Hom Mapping from Nodes of G1 to G2. 
     * i.e. The Direction of p-Homomorphism is G1 p-HOM G2
     * Tries to map nodes of Graph G1 to nodes in Graph G2
     *
     * @param G1 Adjacency List Representation of Graph1
     * @param G2 Adjacency List Representation of Graph2
     * @param mappingScores Matrix that stores Match scores for every node v in
     * G1 to every node v' in graph V2
     * @param w An array that stores weights of the nodes in Graph 1
     * @param threshHold value between 0-1 s.t. v-v' matches scoring above it will only be considered
     * 
     * @return weightedScore
     */
    @Override
    public double calculatepHomSimScore
            (HashMap<Integer, ArrayList<Integer>> G1, HashMap<Integer, ArrayList<Integer>> G2, double[][] mappingScores, double threshHold, double[] w) 
    {
        Boolean[][] G1mat = new Boolean[G1.size()][G1.size()];
        Boolean[][] G2mat = new Boolean[G2.size()][G2.size()];
        
        //Initialize Matrix G1
        for (Integer i = 0; i < G1.size(); i++) 
            for (Integer j = 0; j < G1.size(); j++) 
                G1mat[i][j] = false;

        //Populate G1 with values from Adjacency List
        for (Integer i = 0; i < G1.size(); i++) 
            for (Integer m : G1.get(i)) 
                G1mat[i][m] = true;
        
        //Initialize Matrix G2
        for (Integer i = 0; i < G2.size(); i++) 
            for (Integer j = 0; j < G2.size(); j++) 
                G2mat[i][j] = false;

        //Populate G2 with values from Adjacency List
        for (Integer i = 0; i < G2.size(); i++) 
            for (Integer m : G2.get(i)) 
                G2mat[i][m] = true;        
        
        return this.calculatepHomSimScore(G1mat, G2mat, mappingScores, threshHold, w);
        
    }//calculatepHomSimScore
    
    public static void main(String[] args)
    {
        //Test Code
        //For more Sample Runs Refer to Test.java
              Boolean[][] G1 = {
            //      0,     1,     2,     3,     4,     5
            /*0*/{false, true,  true,  false, false, false},
            /*1*/{false, false, false, true,  true,  false},
            /*2*/{false, false, false, false, false, true},
            /*3*/{false, false, false, false, false, false},
            /*4*/{false, false, false, false, false, false},
            /*5*/{false, false, false, false, false, false}
        };

        Boolean[][] G2 = {
            //     0,      1,     2,     3
            /*0*/{false, true,  false, true},
            /*1*/{false, false, true,  false},
            /*2*/{false, false, false, false},
            /*3*/{false, false, false, false}
        };

        double[][] mat = {
         //G1\G2-> 0,   1,   2,   3
            /*0*/{0.6, 0.4, 0.3, 0.2},
            /*1*/{0.6, 0.8, 0.3, 0.2},
            /*2*/{0.2, 0.3, 0.2, 0.3},
            /*3*/{0.4, 0.3, 0.7, 0.4},
            /*4*/{0.3, 0.3, 0.3, 0.8},
            /*5*/{0.2, 0.2, 0.2, 0.15}
        };
        
       Boolean[][] x = null;

        double threshHold = 0.5;
        double[] w = {0.5, 0.7, 0.2, 1.0, 0.2, 0.2};
        PHomomorphism p = new PHomMaxCardinality();
        p.calculatepHomSimScore(G1, G2, mat, threshHold, w);

        out.println("Raw Score                   = " + p.getRawScore());
        out.println("Weighted Score              = " + p.getWeightedScore());
        out.println("Weighted Similarity measure = " + p.getWeightedSimScore());
        out.println();
        Util.printMatches(p.getMapping());
        
    }//main

}//PHomMaxCardinality
