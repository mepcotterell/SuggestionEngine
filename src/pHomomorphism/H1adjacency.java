
package pHomomorphism;

import java.util.ArrayList;
import java.util.HashMap;
import static java.lang.System.out;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * The class creates and stores previous and post adjacency lists for a given DAG
 * 
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file). 
 * 
 */
public class H1adjacency {
    
    private HashMap<Integer, ArrayList<Integer>> prev  = new HashMap<Integer, ArrayList<Integer>>();
    private HashMap<Integer, ArrayList<Integer>> post = new HashMap<Integer, ArrayList<Integer>>();

    /**
     * Populates prev and post adjacency lists for every node in graph G1.
     * 
     * @param G1 Takes an Adjacency Matrix representation of a Directed Graph
     */
    public H1adjacency(Boolean[][] G1) {
        // mappingScores.length will give the height (Vertical)
        // mappingScores[0].length will give the length (Horizontal)
        try{
            for(int y=0; y < G1.length; y++)
            {
                ArrayList<Integer> tempPost = new ArrayList<Integer>();
                for (int x=0; x < G1.length; x++)
                {
                    if(G1[y][x]) tempPost.add(x);
                }//Inner for
                post.put(y, tempPost);
            }//Outer For

            for(int y=0; y < G1.length; y++)
            {
                ArrayList<Integer> tempPrev = new ArrayList<Integer>();
                for (int x=0; x < G1.length; x++)
                {
                    if(G1[x][y]) tempPrev.add(x);
                }//Inner for

                prev.put(y, tempPrev);
            }//Outer For        
        }//try
        catch(ArrayIndexOutOfBoundsException e)
        {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "Adjacency Matrix for Graph1 has Missing Values, Mapping Might not be correct..!!{0}", e);
        }//catch
        catch(Exception e)
        {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "Unexpected Error Occurred, Mapping Might not be correct..!!{0}", e);
        }//catch

    }//H1adjacency

    /**
     * Returns the Prev Adjacency List for all nodes
     * @return the prev adjacency list
     */
    public HashMap<Integer, ArrayList<Integer>> getPrev() {
        return prev;
    }//getPrev

    /**
     * Sets the Prev Adjacency List for all nodes
     * @param prev 
     */
    public void setPrev(HashMap<Integer, ArrayList<Integer>> prev) {
        this.prev = prev;
    }//setPrev

    /**
     * Returns the post Adjacency List for all nodes
     * @return the post
     */
    public HashMap<Integer, ArrayList<Integer>> getPost() {
        return post;
    }//getPost

    /**
     * Sets the post Adjacency List for all nodes
     * @param post the post to set
     */
    public void setPost(HashMap<Integer, ArrayList<Integer>> post) {
        this.post = post;
    }//setPost
    
    public static void main(String args[])
    {
        //Test code
       Boolean[][] G2 = {
          // 1,      2,     3,    4
       /*1*/{false, true,  false, true},
       /*2*/{false, false, true,  false},
       /*3*/{false, false, false, false},
       /*4*/{false, false, false, false},
        };   
       
       H1adjacency h1 = new H1adjacency(G2);

        for (int i = 0; i < G2.length; i++) 
        {
            for (int j = 0; j < G2.length; j++)
            {
                int t = (G2[i][j] == true)? 1 : 0;
                out.print("  " + t);
            }//inner for
            out.println();
        }//outer for

       out.println();
       out.println("Post List : " + h1.getPost());
       out.println("Prev List : " + h1.getPrev());
 
    }//main
    
}// H1adjacency
