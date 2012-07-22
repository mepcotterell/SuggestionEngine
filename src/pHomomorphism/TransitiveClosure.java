
package pHomomorphism;
import static java.lang.System.out;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * 
 * Class to calculate Transitive closure of a directed Graph using Warshall's algorithm in cubic time
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file). 
 * @see http://www.sroede.nl/pages/warshall/Default.aspx
 * 
 */
public class TransitiveClosure {

    /**
     * Calculates and returns Transitive closure of a directed Graph using Warshall's algorithm in cubic time
     * @param Matrix
     * @return 
     */
    public static Boolean[][] closure(Boolean[][] Matrix) 
    {
        
        int len = Matrix.length ;
        Boolean[][] adj = new Boolean[len][len];
        try{
            //Copy matrix into adj : deep copy
            for (int i = 0; i < len; i++) 
                System.arraycopy(Matrix[i], 0, adj[i], 0, len);

            for (int k = 0; k < len; k++) {
                for (int i = 0; i < len; i++) {
                    for (int j = 0; j < len; j++) {
                        adj[i][j] = (adj[i][j] || (adj[i][k] && adj[k][j]));
                    }//innermost for
                }//inner for
            }//outer for            
        }//try
        catch(ArrayIndexOutOfBoundsException e)
        {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "Adjacency Matrix for Graph2 has Missing Values, Mapping Might not be correct..!!{0}", e);
        }//catch
        catch(Exception e)
        {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "Unexpected Error Occurred, Mapping Might not be correct..!!{0}", e);
        }//catch
        finally{
            return adj;
        }//Finally

    }//closure
    
    /**
     * Method for printing the Matrix, used only in debugging
     * @param G2 Matrix
     */
    public static void print(Boolean[][] G2)
    {
        out.println("Printing Closure");
        for (int i = 0; i < G2.length; i++) {
            for (int j = 0; j < G2.length; j++) {
                int t = (G2[i][j] == true) ? 1 : 0;
                out.print("  " + t);
            }//inner for
            out.println();
        }//outer for
    }//print
    
    public static void main(String args[])
    {
        //Test code
       Boolean[][] G2 = {
          // 1,      2,     3,    4
       /*1*/{false, true,  false, true},
       /*2*/{false, false, true,  false},
       /*3*/{false, false, false, false},
       /*4*/{false, false, false, false}
        };   
       
        for (int i = 0; i < G2.length; i++) 
        {
            for (int j = 0; j < G2.length; j++)
            {
                int t = (G2[i][j] == true)? 1 : 0;
                out.print("  " + t);
            }//inner for
            out.println();
        }//outer for
       
        G2 = closure(G2);
        out.println("Closure");
        for (int i = 0; i < G2.length; i++) 
        {
            for (int j = 0; j < G2.length; j++)
            {
                int t = (G2[i][j] == true)? 1 : 0;
                out.print("  " + t);
            }//inner for
            out.println();
        }//outer for
        
    }//main
}