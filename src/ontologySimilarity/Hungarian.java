
/****************************************************************************
 * @author  John Miller
 * Translated from C code from Assignment Problem and Hungarian Algorithm 
 * @see     http://www.topcoder.com/tc?module=Static&d1=tutorials&d2=hungarianAlgorithm
 * @version 1.0
 * @date    Thu Jul 14 11:51:50 EDT 2011
 * @see     LICENSE (MIT style license file).
 */

package ontologySimilarity;

import java.util.Queue;
import java.util.ArrayDeque;

import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.System.out;
import static java.util.Arrays.fill;

/****************************************************************************
 * This is an O(n^3) implementation of the the Hungarian algorithm (or Kuhn-Munkres
 * algorithm).  Find the maximum cost set of pairings between m x-nodes (workers)
 * and n y-nodes (jobs) such that each worker is assigned to one job and each
 * job has at most one worker assigned.
 * It solves the maximum-weighted bipartite graph matching problem.
 * Caveat: only works if m <= n (i.e., there is at least one job for every worker).
 */
public class Hungarian
{
    private static final int NA = -1;   // Not Assigned
    private static final int NO = -2;   // None Possible

    private static double [][] cost;    // cost matrix: cost[x][y] = cost of assigning worker to job
    private static double [] lx;        // labels of x-nodes (workers)
    private static double [] ly;        // labels of y-nodes (jobs)
    private static double [] slack;     // slack[y] = lx[x] + lx[y] - cost[x][y]
 
    private static int [] slackX;       // slackX[y] = x-node used for computing slack[y]
    private static int [] prev;         // array for memorizing alternating paths
    private static int [] xy;           // xy[x] = y-node matched with x
    private static int [] yx;           // yx[y] = x-node matched with y

    private static int m;               // m workers (x-nodes)
    private static int n;               // n jobs (y-nodes)
    private static int nMatch;          // number of nodes in current matching
    private static int maxMatch;        // maximum number of matches needed
    private static int x;               // current row (x-node)
    private static int y;               // current column (y_node)

    private static boolean [] xSet;     // set of x-nodes
    private static boolean [] ySet;     // set of y-nodes

    private static Queue <Integer> qu;  // queue for Breadth First Search (BFS)

    /************************************************************************
     * Initialize cost labels for x-nodes by setting them to the largest cost
     * on any incident edge (largest value in row). If feasible, this is the
     * optimal solution, otherwise it is an upper bound.
     */
    private static void initLabels ()
    {
        try{
        for (x = 0; x < m; x++) {
            for (y = 0; y < n; y++) lx[x] = max (lx[x], cost[x][y]);
            //out.println ("intLabels: lx[" + x + "] = " + lx[x]);
        } // for
        }
        catch(Exception e)
        {
//            System.out.println("inside initLabels..!!");
        
        }
    } // initLabels

    /************************************************************************
     * Update the cost labels for both x-nodes and y-nodes.
     */
    private static void updateLabels ()
    {
        double delta = POSITIVE_INFINITY;           // initialize delta to infinity
        for (y = 0; y < n; y++)                     // calculate delta using slack
            if (! ySet[y]) delta = min (delta, slack[y]);
        for (x = 0; x < m; x++)                     // update X labels
            if (xSet[x]) lx[x] -= delta;
        for (y = 0; y < n; y++)                     // update Y labels
            if (ySet[y]) ly[y] += delta;
        for (y = 0; y < n; y++)                     // update slack array
            if (! ySet[y]) slack[y] -= delta;
    } // updateLabels

    /************************************************************************
     * Add new edges to the tree and update slack.
     * @param x      current x-node
     * @param prevX  previous x-node before x in the alternating path,
     *               so we add edges (prevX, xy[x]), (xy[x], x)
     */
    private static void addToTree (int x, int prevX) 
    {
        xSet[x] = true;                      // add x to xSet
        prev[x] = prevX;                     // we need this when augmenting

        for (y = 0; y < n; y++) {            // update slack, because we add new node to xSet
            if (lx[x] + ly[y] - cost[x][y] < slack[y]) {
                slack[y]  = lx[x] + ly[y] - cost[x][y];
                slackX[y] = x;
            } // if
        } // for
    } // addToTree

    /************************************************************************
     * Find a root (an unparied x-node) and compute slack values for y-nodes. 
     */
    private static void findRootSetSlack ()
    {
        for (x = 0; x < m; x++) {
            if (xy[x] == NA) {
                qu.add (x);
                prev[x] = NO;           // root is first => no previous x-node
                xSet[x] = true;
                break;
            } // if
        } // for

        for (y = 0; y < n; y++) {       // initialize the slack array
            slack[y]  = lx[x] + ly[y] - cost[x][y];
            slackX[y] = x;
            //out.println ("findRootSetSlack: slack[" + y + "] = " + slack[y] + ", slackX[" + y + "] = " + slackX[y]);
        } // for
    } // findRootSetSlack

    /************************************************************************
     *  Reverse the edges along the augmenting path starting with edge (x, y).
     */
    private static void reverseEdges ()
    {
        for (int cx = x, cy = y, ty; cx != NO; cx = prev[cx], cy = ty) {
            ty     = xy[cx];
            yx[cy] = cx;
            xy[cx] = cy;
        } // for
    } // reverseEdges

    /************************************************************************
     * A recursive procedure to find augmenting paths to improve the assignments.
     * Terminate when nMatch == maxMatch.
     */
    private static void augment ()
    {
        //out.println ("augment: nMatch = " + nMatch + " need " + maxMatch);
        fill (xSet, false);                        // initialize xSet to empty
        fill (ySet, false);                        // initialize ySet to empty
        fill (prev, NA);                           // initialize prev to NA (for alternating tree)

        findRootSetSlack ();
    try{
        while (true) {                                    // main cycle
            while (! qu.isEmpty ()) {                     // building tree with BFS cycle
                x = qu.poll ();                           // current x-node from queue

                for (y = 0; y < n; y++) {                 // iterate through all edges in equality graph
                    if (cost[x][y] == lx[x] + ly[y] && ! ySet[y]) {
                        if (yx[y] == NA) break;           // exposed x-node => augmenting path exists
                        ySet[y] = true;                   // else just add y to ySet
                        qu.add (yx[y]);                   // add x-node yx[y] matched with y to the queue
                        addToTree (yx[y], x);             // add edges (x, y) and (y, yx[y]) to the tree
                    } // if
                } // for

                if (y < n) break;                         // augmenting path found!
            } // while
            if (y < n) break;                             // augmenting path found!

            updateLabels ();                              // augmenting path not found, so improve labeling
            qu.clear ();                                  // empty the queue

            for (y = 0; y < n; y++) {      
                if (! ySet[y] && slack[y] == 0) {
                    if (yx[y] == NA) {                    // exposed x-node => augmenting path exists
                        x = slackX[y];
                        break;
                    } else {
                        ySet[y] = true;                   // else just add y to ySet
                        if (! xSet[yx[y]]) {
                            qu.add (yx[y]);               // add node yx[y] matched with y to the queue
                            addToTree (yx[y], slackX[y]); // add edges (x, y) and (y, yx[y]) to the tree
                        } // if
                    } // if
                } // if
            } // for

            if (y < n) break;                             // augmenting path found!
        } // while
    
        nMatch++;                                 // increment number of nodes in matching
        reverseEdges ();                          // reverse edges along augmenting path
        if (nMatch < maxMatch) augment ();        // try to find another augmenting path
                }
        catch(Exception e)
        {
//            System.out.println("inside augment..!!");
        
        }
        
        
    } // augment

    /************************************************************************
     * The main procedure to find initial pairings and then find augmenting
     * paths to improve the pairings/assignments.
     * @param  _cost  the cost/weight matrix
     * @return total  the total cost
     */
    public static double hungarian (double [][] _cost)
    {
        cost      = _cost;
        m         = cost.length;
        n         = cost[0].length;
        if (m > n) { out.println ("hungarian: error - m = " + m + " > n = " + n); return NA; }
        lx        = new double [m];
        ly        = new double [n];
        slack     = new double [n];
        slackX    = new int [n];
        xy        = new int [m]; fill (xy, NA);
        yx        = new int [n]; fill (yx, NA);
        prev      = new int [m];
        xSet      = new boolean [m];
        ySet      = new boolean [n];
        qu        = new ArrayDeque <Integer> (m);
        nMatch    = 0;                          // number of nodes in current matching
        maxMatch  = min (n, m);                 // number of matches needed

        try{
        
        initLabels ();                          // initialize cost labels for the x-nodes
        augment    ();                             // recursively find augmenting paths

        //out.println ("------------------------------------");
        double total = 0;                       // cost/weight of the optimal matching
        for (x = 0; x < m; x++) {               // form answer -
            total += cost[x][xy[x]];            // using values from x-side
            //out.println ("cost (" + x + ", " + xy[x] + ") = " + cost[x][xy[x]]);
        } // for
        //out.println ("------------------------------------");
        return total;
        }
        catch(Exception e)
        {
            System.out.println("In Hungarian "  );
//            System.out.println("m =" + m + "---n=" + n );
            return 0;
        
        }
        
    } // hungarian

    /************************************************************************
     * The main method used to set up assignment problems.
     * @param args  the command-line arguments
     */
    public static void main (String [] args)
    {
        // m = 3 workers, n = 3 jobs
        double [][] cost1 = { { 1, 4, 5 },
                              { 5, 7, 6 },
                              { 5, 8, 8 } };

        // m = 5 workers, n = 5 jobs
        double [][] cost2 = { { 10, 19,  8, 15, 19 },
                              { 10, 18,  7, 17, 19 },
                              { 13, 16,  9, 14, 19 },
                              { 12, 19,  8, 18, 19 },
                              { 14, 17, 10, 19, 19 } };

        // m = 3 workers, n = 4 jobs
        double [][] cost3 = { { 1, 4, 5, 2 },
                              { 5, 7, 6, 2 },
                              { 5, 8, 8, 9 } };

        // m = 4 workers, n = 3 jobs => error, not enough jobs for workers
        double [][] cost4 = { { 1, 4, 5 },
                              { 5, 7, 6 },
                              { 2, 2, 9 },
                              { 5, 8, 8 } };

        out.println ("optimal cost1 = " + hungarian (cost1));
        out.println ("optimal cost2 = " + hungarian (cost2));
        out.println ("optimal cost3 = " + hungarian (cost3));
        out.println ("optimal cost4 = " + hungarian (cost4));
    } // main

} // Hungarian class

