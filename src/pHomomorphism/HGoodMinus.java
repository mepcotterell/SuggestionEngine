
package pHomomorphism;

import java.util.ArrayList;
import java.util.HashMap;
import static java.lang.System.out;

/**
 *
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file). 
 */
public class HGoodMinus {
    
    private HashMap<Integer, ArrayList<Integer>> good  = new HashMap<Integer, ArrayList<Integer>>();
    private HashMap<Integer, ArrayList<Integer>> minus = new HashMap<Integer, ArrayList<Integer>>();

    /**
     * Constructor that populates the good and Minus HashMaps which together represent
     * the H list in the algorithm. The key-set for both the maps need to be same at every point
     * 
     * @param mappingScores Matrix that stores matching scores for every pair of vertices in the graph v - v'
     * @param threshHold The threshold value, matches below which go into minus list and rest in the good list
     */
    public HGoodMinus(double[][] mappingScores, double threshHold) {

        // mappingScores.length will give the height (Vertical)
        // mappingScores[0].length will give the length (Horizontal)
        for(int y=0; y < mappingScores.length; y++)
        {
            ArrayList<Integer> tempGood = new ArrayList<Integer>();
            ArrayList<Integer> tempMinus = new ArrayList<Integer>();
            
            for (int x=0; x < mappingScores[0].length; x++)
            {
                if(mappingScores[y][x] < threshHold)
                    tempMinus.add(x);
                else
                    tempGood.add(x);
            }//Inner for
            
            good.put(y, tempGood);
            minus.put(y, tempMinus);
        }//Outer For
        
    }//HGoodMinus constructor

    public HGoodMinus() {
        
    }

    public int getSize()
    {
        return (good.size() > minus.size()) ?  minus.size() : good.size();
    }
    
    /**
     * @return the good
     */
    public HashMap<Integer, ArrayList<Integer>> getGood() {
        return good;
    }

    /**
     * @param good the good to set
     */
    public void setGood(HashMap<Integer, ArrayList<Integer>> good) 
    {
        //Deep Copy
        HashMap<Integer, ArrayList<Integer>> newHash = new HashMap<Integer, ArrayList<Integer>>();
        for (Integer i : good.keySet())
            newHash.put(new Integer(i), new ArrayList<Integer>(good.get(i)));
        this.good = newHash;
    }//setGood
    
    void setGood(Integer G1node, ArrayList<Integer> arrayList) {
        this.good.put(G1node, arrayList);
    }

    /**
     * @return the minus
     */
    public HashMap<Integer, ArrayList<Integer>> getMinus() {
        return minus;
    }

    /**
     * @param minus the minus to set
     */
    public void setMinus(HashMap<Integer, ArrayList<Integer>> minus) {
        //Deep Copy
        HashMap<Integer, ArrayList<Integer>> newHash = new HashMap<Integer, ArrayList<Integer>>();
        for (Integer i : minus.keySet())
            newHash.put(new Integer(i), new ArrayList<Integer>(minus.get(i)));
        this.minus = newHash;
    }//set Minus
    
    public void setMinus(int G1node, ArrayList<Integer> G2nodes) {
        this.minus.put(G1node, G2nodes);
    }
    
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
        
        HGoodMinus test =  new HGoodMinus(mat, 0.4);
            out.println("G1\t|--Good--\t|--Minus--");        
        for(int i = 0; i < test.getSize(); i++)
        {
            out.println(i + "\t" + test.getGood().get(i) + "\t\t" + test.getMinus().get(i));
        }
     
    }//main


}//HGoodMinus
