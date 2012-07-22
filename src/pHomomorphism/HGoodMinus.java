
package pHomomorphism;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Class with functions to Store, Retrieve and Manipulate the H list in the pHomorphism algorithm 
 * 
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file). 
 * 
 */
public class HGoodMinus {
    
    private HashMap<Integer, ArrayList<Integer>> good;
    private HashMap<Integer, ArrayList<Integer>> minus;

    /**
     * Constructor that populates the good and Minus HashMaps which together represent
     * the H list in the algorithm. The key-set for both the maps need to be same at every point
     * 
     * @param mappingScores Matrix that stores matching scores for every pair of vertices in the graph v - v'
     * @param threshHold The threshold value, matches below which go into minus list and rest in the good list
     */
    public HGoodMinus(double[][] mappingScores, double threshHold) {

        good  = new HashMap<Integer, ArrayList<Integer>>();
        minus = new HashMap<Integer, ArrayList<Integer>>();

        try{
            // mappingScores.length will give the height (Vertical)
            // mappingScores[0].length will give the length (Horizontal)
            for(int y=0; y < mappingScores.length; y++)
            {
                ArrayList<Integer> tempGood = new ArrayList<Integer>();
                ArrayList<Integer> tempMinus = new ArrayList<Integer>();

                for (int x=0; x < mappingScores[0].length; x++)
                {
                    if(mappingScores[y][x] > threshHold)
                        tempGood.add(x);
                }//Inner for

                good.put(y, tempGood);
                minus.put(y, tempMinus);
            }//Outer For
        }//try
        catch(ArrayIndexOutOfBoundsException e)
        {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "mappingScores Matrix has Missing Values, Mapping Might not be correct..!!{0}", e);
        }//catch
        catch(Exception e)
        {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "Unexpected Error Occurred, Mapping Might not be correct..!!{0}", e);
        }//catch

    }//HGoodMinus constructor

    public HGoodMinus() {
        good  = new HashMap<Integer, ArrayList<Integer>>();
        minus = new HashMap<Integer, ArrayList<Integer>>();
    }//constructor

    /**
     * Returns the Size of H, in terms of vertices for which possible candidate matches exist
     * @return size
     */
    public int getSize() {
        int size = 0;
        for (Integer I : this.good.keySet())
            if (!this.good.get(I).isEmpty())
                size++;
        return size;
    }//getSize
    
    /**
     * Returns the Good (Candidate) matches for all v in H
     * @return good as a HashMap<Integer, ArrayList<Integer>> 
     */
    public HashMap<Integer, ArrayList<Integer>> getGood() {
        return good;
    }//getGood

    /**
     * Sets the Good (Candidate) matches for all v in H
     * @param good as a HashMap<Integer, ArrayList<Integer>> 
     */
    public void setGood(HashMap<Integer, ArrayList<Integer>> gd) {
        //Deep Copy
        HashMap<Integer, ArrayList<Integer>> newGood = new HashMap<Integer, ArrayList<Integer>>();
        for (Integer i : gd.keySet())
            newGood.put(new Integer(i), new ArrayList<Integer>(gd.get(i)));
        this.good = newGood;
    }//setGood
    
    /**
     * Sets the Good (Candidate) matches for a particular v in H
     * @param G1node the node in H for which to set the Good matches
     * @param arrayList Possible matches in G2 to update for v
     */
    void setGood(Integer G1node, ArrayList<Integer> G2nodes) {
        this.good.put(G1node, G2nodes);
    }//setGood

    /**
     * Returns the NOT Good (Candidate) matches for all v in H
     * @return the minus
     */
    public HashMap<Integer, ArrayList<Integer>> getMinus() {
        return minus;
    }//getMinus

    /**
     * Sets the Not Good (Candidate) matches for all v in H
     * @param minus the minus to set as a HashMap<Integer, ArrayList<Integer>> 
     */
    public void setMinus(HashMap<Integer, ArrayList<Integer>> min) {
        //Deep Copy
        HashMap<Integer, ArrayList<Integer>> newMinus = new HashMap<Integer, ArrayList<Integer>>();
        for (Integer i : min.keySet())
            newMinus.put(new Integer(i), new ArrayList<Integer>(min.get(i)));
        this.minus = newMinus;
    }//set Minus
    
    /**
     * 
     * Sets the NOT Good (Candidate) matches for a particular v in H
     * @param G1node the node in H for which to set the NOT Good matches
     * @param G2nodes Possible matches in G2 to update for v
     */
    public void setMinus(int G1node, ArrayList<Integer> G2nodes) {
        this.minus.put(G1node, G2nodes);
    }//set Minus
    
    public static void main(String args[])
    {
        //Test code
        double[][] mat = {
         //G1\G2-> 1,   2,   3,   4
            /*1*/{0.6, 0.4, 0.3, 0.2},
            /*2*/{0.6, 0.8, 0.3, 0.2},
            /*3*/{0.2, 0.3, 0.2, 0.3},
            /*4*/{0.4, 0.3, 0.7, 0.4},           
            /*5*/{0.3, 0.3, 0.3, 0.8},
            /*6*/{0.2, 0.2, 0.2, 0.15},           
        };
        
        HGoodMinus H =  new HGoodMinus(mat, 0.4);
        Util.printH(H);
        System.out.println("Size = " + H.getSize());
     
    }//main


}//HGoodMinus
