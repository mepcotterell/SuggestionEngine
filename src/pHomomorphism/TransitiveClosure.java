
package pHomomorphism;
import static java.lang.System.out;

/**
 *
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file). 
 * @see http://www.sroede.nl/pages/warshall/Default.aspx
 * Class to calculate Transitive closure of a directed Graph using Warshall's algorithm in cubic time
 * 
 */
public class TransitiveClosure {

    /**
     * Calculates and returns Transitive closure of a directed Graph using Warshall's algorithm in cubic time
     * @param Matrix
     * @return 
     */
    public static Boolean[][] closure(Boolean[][] Matrix) {
        
        int len = Matrix.length ;
        Boolean[][] adj = new Boolean[len][len];
        
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
        return adj;
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