
package pHomomorphism;

import java.util.*;
import static java.lang.System.out;

/**
 *
 * The class has methods to calculate max Cardinality Mapping.
 * 
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file).
 * 
 */
public class MaxCardinality {

    private List<Match> mapping = new ArrayList<Match>();
    private final boolean debug = true;

    /**
     * Returns the p-Hom Mapping between G1 and G2
     * @return the mapping as a List of Match <node1, node2>
     */
    public List<Match> getMapping() {
        return mapping;
    }//get Mapping

    /**
     * Sets the p-Hom Mapping between G1 and G2
     * @param mapping the mapping as a List of Match <node1, node2>
     */
    public void setMapping(List<Match> mapping) {
        //Deep Copy
        List<Match> newMapping = new ArrayList<Match>();
        for (Match m : mapping) {
            newMapping.add(new Match(m.g1node, m.g2node));
        }
        this.mapping = newMapping;
    }//set Mapping

    public MaxCardinality() {
    }//constructor

    /**
     * Calculates p-Hom Mapping from Nodes of G1 to G2. 
     * i.e. The Direction of p-Homomorphism is G1 p-HOM G2 When Matching Web Service Input-Outputs :
     * G1 -> IODAG for Input (ofCandidateOp) and G2 -> IODAG for Output (ofWorkflowOp)
     *
     * @param G1 Adjacency Matrix Representation of Graph1
     * @param G2 Adjacency Matrix Representation of Graph2
     * @param mappingScores Matrix that stores Match scores for every node v in
     * G1 to every node v' in graph V2
     * @param threshHold value between 0-1 s.t. v-v' matches scoring above it will only be considered
     */
    public void calcMaxCardMapping(Boolean[][] G1, Boolean[][] G2, double[][] mappingScores, double threshHold) 
    {
        //Create Adjacency Matrix for G1
        H1adjacency H1adj = new H1adjacency(G1);
        if(debug) out.println("H1 Adjacency List:\n" + "Prev List : " + H1adj.getPrev() + "\nPost List : " + H1adj.getPost() + "\n");
       
        //Create List H of Candidate Matches
        HGoodMinus H = new HGoodMinus(mappingScores, threshHold);
        if(debug) Util.printH(H);
        
        /*
         * Note : Set the Matrix for G2 to represent Closure of the Graph G2. At
         * this point it seems like, doing this would make the algorithm
         * equivalent to Homeomorphism. Un-comment it to confining it to
         * Homomorphism
         */
        Boolean[][] H2 = TransitiveClosure.closure(G2);
        if(debug) TransitiveClosure.print(H2);
        
        while (H.getSize() > this.getMapping().size()) 
        {
            Combination c = greedyMatch(H1adj, H2, H);
            List<Match> map = c.getMatches();
            List<Match> conflicts = c.getConflicts();

            //For Debugging
            if (debug){
                out.println("Macthes Found:");
                for (Match m : map) out.println(m.g1node + " --> " + m.g2node);
                out.println("Conflicts that will be Removed:");
                for (Match m : conflicts) out.println(m.g1node + " --> " + m.g2node);
            }

            //Remove Conflicting pairs
            for (Match m : conflicts) {
                if (H.getGood().containsKey(m.g1node)) 
                    H.getGood().get(m.g1node).remove(m.g2node);
            }
            setMapping((map.size() > getMapping().size()) ? map : getMapping());
        }//while

    }//calcMaxCardMapping    

    /**
     * Takes the current matching list as input a calculates p-Hom mapping from G1 to G2
     * 
     * @param H1adj Adjacency List for matrix G1
     * @param H2 Matrix representation for graph G2 
     * @param H List of candidate Matches
     * @return The mappings for Matched nodes
     */
    private Combination greedyMatch(H1adjacency H1adj, Boolean[][] H2, HGoodMinus H) {

        if (debug) out.println("\n---------------------------------\nEntering GREEDY MATCH\n---------------------------------\n");
        
        if (H.getSize() == 0)
            return new Combination();

        Integer G1node = 0;
        Integer G2node = 0;
        boolean flag = true;
        
        //Setting the Candidate Match
        Set<Integer> G1nodes = H.getGood().keySet();
        ArrayList<Integer> G2nodes = new ArrayList<Integer>();
        for (Integer i : G1nodes) {
            G1node = i;
            G2nodes = H.getGood().get(G1node);
            if (!G2nodes.isEmpty()) {
                G2node = G2nodes.get(0);
                flag = false;
                break;
            }//if
        }//for

        if (flag) return new Combination();
        
        if (debug)Util.printH("\nMatch Selected " + G1node + "-->" + G2node , H);
        
        G2nodes.remove(G2node);
        H.getMinus().get(G1node).addAll(G2nodes);
        H.setGood(G1node, new ArrayList<Integer>());

        if (debug) Util.printH("H before trimming",H);

        H = TrimMatching.trimPosibleMatches(G1node, G2node, H1adj, H2, H);
        
        if (debug) Util.printH("H After trimming ",H);
        
        HGoodMinus Ha = new HGoodMinus();
        HGoodMinus Hb = new HGoodMinus();

        //Splitting H into Ha and Hb
        for (Integer v : H.getGood().keySet()) {

            if (!H.getGood().get(v).isEmpty()) {
                ArrayList<Integer> t = new ArrayList<Integer>();
                for(Integer i : H.getGood().get(v))
                    t.add(i);
                Ha.setGood(v, t);
                Ha.setMinus(v, new ArrayList<Integer>());
            }
            if (!H.getMinus().get(v).isEmpty()) {

                ArrayList<Integer> t = new ArrayList<Integer>();
                for(Integer i : H.getMinus().get(v))
                    t.add(i);
                Hb.setGood(v, t);
                Hb.setMinus(v, new ArrayList<Integer>());
            }
        }//for

        //Recursive call
        Combination mappingA = greedyMatch(H1adj, H2, Ha);
        Combination mappingB = greedyMatch(H1adj, H2, Hb);
        Combination mappingOut = new Combination();
        
        Match m = new Match(G1node, G2node);
        mappingA.addAmatch(m);
        mappingB.addAconflict(m);

        int mapAsize =  mappingA.getMatches().size();
        int mapBsize =  mappingB.getMatches().size();

        int conAsize =  mappingA.getConflicts().size();
        int conBsize =  mappingB.getConflicts().size();

        if (mapAsize > mapBsize) {
            mappingOut.setMatches(mappingA.getMatches());
        } else {
            mappingOut.setMatches(mappingB.getMatches());
        }

        if (conAsize > conBsize) {
            mappingOut.setConflicts(mappingA.getConflicts());
        } else {
            mappingOut.setConflicts(mappingB.getConflicts());
        }
        
        if (debug) out.println("-----------------------------\nExiting Greedy Match\n(-----------------------------");
        if (debug) Util.printMatches(mappingOut);
        return mappingOut;

    }//greedyMatch

    
    public static void main(String[] args) {
        //Test code
        //Test 1
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


        MaxCardinality maxCard = new MaxCardinality();
//        maxCard.calcMaxCardMapping(G1, G2, mat, threshHold);
//        List<Match> op = maxCard.getMapping();
//        out.println("Final Mapping");
//        for (Match m : op) 
//            System.out.println(m.g1node + " --> " + m.g2node);
//        
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
//        maxCard.calcMaxCardMapping(G1, G2, mat, threshHold);
//        List<Match> op1 = maxCard.getMapping();
//        out.println("Final Mapping");
//        for (Match m : op1) 
//            System.out.println(m.g1node + " --> " + m.g2node);
//                
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
            /*1*/{0.0, 0.3, 0.3, 0.1},
            /*2*/{0.2, 0.3, 0.2, 0.1},
            /*3*/{0.2, 0.4, 0.2, 0.3},
            /*4*/{0.3, 0.5, 0.3, 0.8}
        };
        maxCard.calcMaxCardMapping(G1, G2, mat, threshHold);
        List<Match> op2 = maxCard.getMapping();
        out.println("Final Mapping");
        for (Match m : op2) 
            System.out.println(m.g1node + " --> " + m.g2node);
        
    }//main
}//MaxCardinality
