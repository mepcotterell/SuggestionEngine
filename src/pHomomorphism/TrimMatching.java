
package pHomomorphism;

import java.util.ArrayList;
import java.util.Set;

/**
 *
 * Implements TrimMatching part of the pHomomorphism algorithm that given a match (G1node, G2node)
 * Removed bad matches from H 
 * 
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file). 
 * 
 */
public class TrimMatching 
{
    /**
     * Removed bad matches from H assuming (G1node, G2node) is a match.
     * @param G1node Node from Graph G1 matched to G2node
     * @param G2node Node from Graph G2 matched to G1node
     * @param H1adj Adjacency List for matrix G1
     * @param H2 Matrix Representation for Graph G2
     * @param H List of candidate Matches
     * @return A trimmed H
     */
    public static HGoodMinus trimPosibleMatches(Integer G1node, Integer G2node, H1adjacency H1adj, Boolean[][] H2, HGoodMinus H) 
    {
        HGoodMinus Hnew = new HGoodMinus();
        //Deep copy
        Hnew.setGood(H.getGood());
        Hnew.setMinus(H.getMinus());
        
        ArrayList<Integer> H1Prev = H1adj.getPrev().get(G1node);
        Set<Integer> H_values = H.getGood().keySet();
        ArrayList<Integer> H1Post = H1adj.getPost().get(G1node);
        
        ArrayList<Integer> HnH1Prev = Util.intersection(H1Prev,H_values); 
        ArrayList<Integer> HnH1Post = Util.intersection(H1Post,H_values); 
  
        for (Integer i : HnH1Prev) 
        {
            for (Integer j : H.getGood().get(i)) 
                if (H2[j][G2node] == false) 
                {
                    Hnew.getGood().get(i).remove(j);
                    Hnew.getMinus().get(i).add(j);
                }//if
        }//outer for
        
        for (Integer i : HnH1Post) 
        {
            for (Integer j : H.getGood().get(i)) 
                if (H2[G2node][j] == false) 
                {
                    Hnew.getGood().get(i).remove(j);
                    Hnew.getMinus().get(i).add(j);
                }//if
        }//outer for 

        return Hnew;
    }//trimPosibleMatches

        
    public static void main(String[] args)
    {
        //Test code
        Boolean[][] G1 = {
          //   0,     1,     2,     3,     4,     5, 
       /*0*/{false, true,  true,  false, false, false},
       /*1*/{false, false, false, true,  true,  false},
       /*2*/{false, false, false, false, false, true},
       /*3*/{false, false, false, false, false, false},            
       /*4*/{false, false, false, false, false, false},            
       /*5*/{false, false, false, false, false, false},            
        };
         
        Boolean[][] G2 = {
          //   0,     1,     2,     3,
       /*0*/{false, true,  false, true},
       /*1*/{false, false, true,  false},
       /*2*/{false, false, false, false},
       /*3*/{false, false, false, false},
        };       
        
        double[][] mat = {
         //G1\G2-> 0,   1,   2,   3
            /*0*/{0.6, 0.4, 0.3, 0.2},
            /*1*/{0.6, 0.8, 0.3, 0.2},
            /*2*/{0.2, 0.3, 0.2, 0.3},
            /*3*/{0.4, 0.3, 0.7, 0.4},           
            /*4*/{0.3, 0.3, 0.3, 0.8},
            /*5*/{0.2, 0.2, 0.2, 0.15},           
        };
        
        double threshHold = 0.4;
        
        int G1node = 1;
        int G2node = 1;
                
        H1adjacency H1adj = new H1adjacency(G1);
        HGoodMinus H = new HGoodMinus(mat, threshHold);
        Boolean[][] H2 = TransitiveClosure.closure(G2);
        
        Util.printH("Before Trim",H);        
        
        H = trimPosibleMatches(G1node, G2node, H1adj, H2, H);
        
        Util.printH("After Trim",H);
        
    }//main


}//TrimMatching
