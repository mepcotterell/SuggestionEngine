
package pHomomorphism;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * A class to represent output of Greedy Match
 * 
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file).
 * 
 */
public class Combination {

    private List<Match> matches;
    private List<Match> conflicts;

    /**
     * Constructors that Sets the Matches nd conflicts
     * @param m
     * @param c 
     */
    public Combination(ArrayList<Match> m, ArrayList<Match> c) {
        this.matches = m;
        this.conflicts = c;
    }//Constructor

    public Combination() {
        this.matches   = new ArrayList<Match>();
        this.conflicts = new ArrayList<Match>();
    }//constructor

    /**
     * Returns a list of matches
     * @return the matches
     */
    public List<Match> getMatches() {
        return matches;
    }//getMatches

    /**
     * Sets the matches
     * @param matches the matches to set
     */
    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }//setMatches

    /**
     * Add A match if it already doesn't exist, to the list of matches
     * @param match to be added
     */
    public void addAmatch(Match m) {
        for (Match mat : this.matches)
                if (mat.g1node == m.g1node)
                    if (mat.g2node == m.g2node)
                        return;
        this.matches.add(m);
    }//addAmatch
    
    /**
     * Returns the list of conflicts
     * @return the conflicts
     */
    public List<Match> getConflicts() {
        return conflicts;
    }//getConflicts

    /**
     * Sets the list of conflicts
     * @param conflicts the conflicts to set
     */
    public void setConflicts(List<Match> conflicts) {
        this.conflicts = conflicts;
    }

    /**
     * Add A conflict if it already doesn't exist, to the list of conflicts
     * @param conflict to be added
     */
    public void addAconflict(Match m) {
        for (Match mat : this.conflicts)
            if (mat.g1node == m.g1node)
                if (mat.g2node == m.g2node)
                    return;
        this.conflicts.add(m);
    }//addAconflict

    public static void main(String[] args)
    {
        //Test COde
        Combination c = new Combination();
        
        Match m = new Match(1, 2);
        c.addAmatch(m);
        m = new Match(1, 3);
        c.addAconflict(m);
        
        m = new Match(2, 2);
        c.addAmatch(m);
        
    }//main

}//Combination