
package pHomomorphism;

import java.util.*;

/**
 *
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file). 
 */
public class MaxCardinality {
    
    private List<match> mapping = new ArrayList<match>();
    private List<match> I = new ArrayList<match>();
    public MaxCardinality() {
    }
    
    /**
     * Calculates Mapping from Nodes of G1 to G2.
     * i.e. The Direction of p-Homomorphism is : G1 p-HOM G2 
     * When Matching Web Service Input-Outputs : G1 -> IODAG for Input (ofCandidateOp) and  G2 -> IODAG for Output (ofWorkflowOp)
     * 
     * @param G1 Adjacency Matrix Representation of Graph1
     * @param G2 Adjacency Matrix Representation of Graph2
     * @param mappingScores Matrix that stores Match scores for every node v in G1 to every node v' in graph V2
     * @param threshHold A threshold value between 0-1, v-v' matches scoring below threshold will not be considered
     */
    public void calcMaxCardMapping(Boolean[][] G1, Boolean[][] G2, double[][] mappingScores, double threshHold)
    {
        H1adjacency H1adj = new H1adjacency(G1);
        
        HGoodMinus H = new HGoodMinus(mappingScores, threshHold);
        
        /* Note : Set the Matrix for G2 to represent Closure of the Graph G2. At this point it seems like,
         * doing this would make the algorithm equivalent to Homeomorphism. 
         * Un-comment it to confining it to Homomorphism
         */      
        //Boolean[][] H2 = H2Closure.closure(G2);
        
        while ( H.getSize() > getMapping().size() )
        {
            combination c = greedyMatch(H1adj, G2, H);
            List<match> map = c.matches;
            List<match> conflicts = c.conflicts;
            
//            for (match m : conflicts)
//            {
//                H.getGood().get(m.g1node).remove(m.g2node);
//            }
            
            setMapping((map.size() > getMapping().size()) ? map : getMapping());
        }//while
        
        
        
    }//calcMaxCardMapping    


    private combination greedyMatch(H1adjacency H1adj, Boolean[][] H2, HGoodMinus H) {    

        if (H.getSize() == 0)
        {
            ArrayList<match> empty = new ArrayList<match>();
            combination c = new combination(empty, empty);
            return c;
        }

         int G1node = 0;
         Set<Integer> G1nodes = H.getGood().keySet();
         for (int i : G1nodes)
         {
             G1node = i; break;
         }
         ArrayList<Integer> G2nodes = H.getGood().get(G1node);
         int G2node = G2nodes.get(0);
         
         G2nodes.remove(0);
         H.setMinus(G1node, G2nodes);
         H.setGood(G1node, new ArrayList<Integer>());
         
        //H = trimMatching(G1node, G2node, H1adj, H2, H);
         HGoodMinus Ha = new HGoodMinus();
         HGoodMinus Hb = new HGoodMinus();
         
         for(int v : H.getGood().keySet())
         {
             
             if(!H.getGood().get(v).isEmpty())
             {
                 ArrayList<Integer> t = H.getGood().get(v);
                 Ha.setGood(v,t);
                 Ha.setMinus(v, new ArrayList<Integer>());
             }
             if(!H.getMinus().get(v).isEmpty())
             {
                 ArrayList<Integer> t = H.getMinus().get(v);
                 Hb.setGood(v,t);
                 Hb.setMinus(v, new ArrayList<Integer>());
             }               
         }//for
                  
        combination mappingA = greedyMatch(H1adj, H2, Ha);
        combination mappingB = greedyMatch(H1adj, H2, Hb);
        combination mappingOut = new combination();
                 
         int mapAsize = (mappingA.getMatches().isEmpty()) ? 0 : mappingA.getMatches().size();
         int mapBsize = (mappingB.getMatches().isEmpty()) ? 0 : mappingB.getMatches().size();
         
         int conAsize = (mappingA.getConflicts().isEmpty()) ? 0 : mappingA.getConflicts().size();
         int conBsize = (mappingB.getConflicts().isEmpty()) ? 0 : mappingB.getConflicts().size();

         if(mapAsize + 1 > mapBsize)
         {
             match m = new match (G1node,G2node);
             mappingA.matches.add(m);
             mappingOut.setMatches(mappingA.getMatches());
         }
         else
         {
             mappingOut.setMatches(mappingB.getMatches());
         }
         
         if(conAsize > conBsize + 1)
         {
             mappingOut.setConflicts(mappingA.getConflicts());         
         }
         else
         {
             match m = new match (G1node,G2node);
             mappingB.conflicts.add(m);
             mappingOut.setConflicts(mappingB.getConflicts());          
         }
         
         return mappingOut;

    }//greedyMatch

    private HGoodMinus trimMatching(int G1node, int G2node, H1adjacency H1adj, Boolean[][] H2, HGoodMinus H) {

        
        HGoodMinus Hnew = new HGoodMinus();
        Hnew.setGood(H.getGood());
        Hnew.setMinus(H.getMinus());
        
        ArrayList<Integer> H1Prev = H1adj.getPrev().get(G1node);
        Set<Integer> H_values = H.getGood().keySet();
        ArrayList<Integer> H1Post = H1adj.getPost().get(G1node);
        
        ArrayList<Integer> HnH1Prev = util.intersection(H1Prev,H_values); 
        ArrayList<Integer> HnH1Post = util.intersection(H1Post,H_values);         
        
        for (int i : HnH1Post)
        {
            for(int j : H.getGood().get(i))
                if(H2[j][G2node] == false)
                {
                    ArrayList<Integer> temp = H.getGood().get(i);
                    temp.remove(j);
//                    Hnew.getGood().get(i).remove(j);
//                    Hnew.getMinus().get(i).add(j);
                }
        }//outer for 
        
        for (int i : HnH1Prev) 
        {
            for (int j : H.getGood().get(i)) 
            {
                if (H2[G2node][j] == false) 
                {
                    ArrayList<Integer> temp = H.getGood().get(i);
                    temp.remove(j);
//                    Hnew.getGood().get(i).remove(j);
//                    Hnew.getMinus().get(i).add(j);
                }
            }
        }//outer for
        
        return Hnew;
    }//trimMatching

    /**
     * @return the mapping
     */
    public List<match> getMapping() {
        return mapping;
    }

    /**
     * @param mapping the mapping to set
     */
    public void setMapping(List<match> mapping) {
        this.mapping = mapping;
    }
    
    public class match
    {
        int g1node;
        int g2node;

        public match(int G1node, int G2node) {
            g1node = G1node;
            g2node = G2node;
        }
    }//Match
    
    public class combination
    {
        private List<match> matches;
        private List<match> conflicts;

        private combination(ArrayList<match> m, ArrayList<match> c) {
            this.matches = m;
            this.conflicts = c;
        }

        private combination() {
        }

        /**
         * @return the matches
         */
        public List<match> getMatches() {
            return matches;
        }

        /**
         * @param matches the matches to set
         */
        public void setMatches(List<match> matches) {
            this.matches = matches;
        }

        /**
         * @return the conflicts
         */
        public List<match> getConflicts() {
            return conflicts;
        }

        /**
         * @param conflicts the conflicts to set
         */
        public void setConflicts(List<match> conflicts) {
            this.conflicts = conflicts;
        }
    
    }
        
    public static void main(String[] args)
    {
        //Test code
        Boolean[][] G1 = {
          //   1,     2,     3,     4,     5,     6
       /*1*/{false, true,  true,  false, false, false},
       /*2*/{false, false, false, true,  true,  false},
       /*3*/{false, false, false, false, false, true},
       /*4*/{false, false, false, false, false, false},            
       /*5*/{false, false, false, false, false, false},            
       /*6*/{false, false, false, false, false, false},            
        };
         
        Boolean[][] G2 = {
          //   1,     2,     3,     4
       /*1*/{false, true,  false, true},
       /*2*/{false, false, true,  false},
       /*3*/{false, false, false, false},
       /*4*/{false, false, false, false},
        };       
        
        double[][] mat = {
         //G1\G2-> 1,   2,   3,   4
            /*1*/{0.6, 0.4, 0.3, 0.2},
            /*2*/{0.6, 0.8, 0.3, 0.2},
            /*3*/{0.2, 0.3, 0.2, 0.3},
            /*4*/{0.4, 0.3, 0.7, 0.4},           
            /*5*/{0.3, 0.3, 0.3, 0.8},
            /*6*/{0.2, 0.2, 0.2, 0.15},           
        };
        
        double threshhold = 0.4;
        
        MaxCardinality maxCard =  new MaxCardinality();
        maxCard.calcMaxCardMapping(G1, G2, mat, threshhold);
        List <match> op = maxCard.getMapping();
        
    
    }//main

    
}//MaxCardinality
