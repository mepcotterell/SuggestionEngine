package pHomomorphism;

import java.util.ArrayList;
import java.util.Set;
import static java.lang.System.out;
import java.util.List;

/**
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file). The class provides
 * A class that has random useful functions mostly used for debugging
 */
public class Util {

    /**
     * Returns the intersection of two lists passed to it.
     *
     * @param List1 as an ArrayList
     * @param List2 as an ArrayList
     * @return List
     */
    public static ArrayList<Integer> intersection(ArrayList<Integer> List1, ArrayList<Integer> List2) 
    {
        ArrayList<Integer> intersection = new ArrayList<Integer>();
        if (List1 != null && List2 != null) 
            for (int i : List1) 
                if (List2.contains(i)) 
                    intersection.add(i);

        return intersection;
    }//intersection

    /**
     * Returns the intersection of list and a Set passed to it.
     *
     * @param List1 as ArrayList
     * @param List2 as a set
     * @return List
     */
    static ArrayList<Integer> intersection(ArrayList<Integer> List1, Set<Integer> List2) {
        ArrayList<Integer> intersection = new ArrayList<Integer>();
        if (List1 != null && List2 != null) 
            if ((!List1.isEmpty()) && (!List2.isEmpty())) 
                for (Integer i : List1) 
                    if (List2.contains(i))
                        intersection.add(i);

        return intersection;
    }//intersection

    /**
     * Prints the H list of candidate Matches
     * @param H 
     */
    public static void printH(HGoodMinus H) {
        out.println("H\t|--Good--\t|--Minus--");
        for (Integer i : H.getGood().keySet()) {
            out.println(i + "\t" + H.getGood().get(i) + "\t\t" + H.getMinus().get(i));
        }
        out.println();
    }//printH
    
    /**
     * Prints the H list of candidate Matches
     * @param H 
     */    
    static void printH(String msg, HGoodMinus H) {
        out.println(msg);
        printH(H);
    }

    /**
     * Prints the output of Greedy Match
     * @param H 
     */       
    public static void printMatches(Combination cc) {
        out.println("Matches : ");
        for (Match i : cc.getMatches()) {
            out.println(i.g1node + "-->" + i.g2node);
        }

        out.println("Conflicts : ");
        for (Match i : cc.getConflicts()) {
            out.println(i.g1node + "-->" + i.g2node);
        }
    }//printMatches
    
    static void printMatches(List<Match> mapping) {
         
        out.println("Matches : ");
        for (Match i : mapping) {
            out.println(i.g1node + "-->" + i.g2node);
        }
    }

    public static void main(String[] args) {
        //Test code
        ArrayList<Integer> List1 = new ArrayList<Integer>();
        ArrayList<Integer> List2 = new ArrayList<Integer>();
        List1.add(1);
        List1.add(2);
        List1.add(4);
        List1.add(4);

        List2.add(1);
        List2.add(3);
        List2.add(4);

        out.println(intersection(List1, List2));

    }//main



}