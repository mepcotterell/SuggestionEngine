
package test;

import java.text.DecimalFormat;
import java.util.List;
import util.WebServiceOprScore;
import static java.lang.System.out;

/**
 * This class provides methods for classifying the Suggested Operations into High, 
 * Medium and low.
 * 
 * @author Michael Cotterell
 * @author Alok Dhamanaskar
 * @see LICENSE (MIT style license file). 
 * 
 */
public class TestMetrics {
    
    /**
     * Method For printing the sub scores for suggested a Operation
     * @param suggestion 
     */
    private static void printOp (WebServiceOprScore suggestion) {
        String[] ww = suggestion.getWsDescriptionDoc().split("/");
        String wsName = ww[ww.length -1].replace("sawsdl", "");
       	DecimalFormat twoDForm = new DecimalFormat("#.##");
       
        out.println(wsName +suggestion.getOperationName() + "-" + twoDForm.format(suggestion.getScore()) );
    } // printOp
    
    public static void printMetrics (List<WebServiceOprScore> list) 
    {
        printMetrics(list, 0.55);
    } // printMetrics

        
    public static void printList (List<WebServiceOprScore> list) 
    {
        for (WebServiceOprScore suggestion: list) {
        printOp(suggestion);
        }

    } // printMetrics
    
    
       
    /**
     * Classify the Suggested Operations as high medium and low and print them.
     * 
     * @param list of Suggested operations
     * @param factor Controls how high the high should be
     * 
     */
    public static void printMetrics (List<WebServiceOprScore> list, double factor) 
    {
        // Determine the mean, min, and max
        double sum = 0.0;
        double min = 1;
        double max = 0;
        
        for (WebServiceOprScore suggestion: list) {
            sum += suggestion.getScore();
            if (suggestion.getScore() > max) max = suggestion.getScore();
            if (suggestion.getScore() < min) min = suggestion.getScore();
        } // for
        
        double mean = sum / (double) list.size();

        // Determine the variance and standard deviation
        sum = 0.0;
        for (WebServiceOprScore suggestion: list) {
            sum += Math.pow(suggestion.getScore() - mean, 2);
        } // for
        
        double variance = sum / (double) list.size();
        double stddev   = Math.sqrt(variance);
        double xStd = (max- mean)/stddev;
        double upLimit = (factor * xStd) * stddev;
        
        
        out.println();
        out.println("METRICS");
        out.println("---------------------------------------------------");
        out.println("Mean     = " + mean);
        out.println("Min      = " + min);
        out.println("Max      = " + max);
        out.println("Variance = " + variance);
        out.println("Std.Dev. = " + stddev);
        out.println("---------------------------------------------------");
        out.println("H        = (" + ( max - upLimit) + ", " + max + "]");
        out.println("M        = [" + (mean) + ", " + ( max - upLimit) + "]");
        out.println("L        = [" + min + ", " + (mean) + ")");
        out.println("---------------------------------------------------");
        out.println();
        out.println("GROUPS");
        out.println("---------------------------------------------------");
        
        

        for (WebServiceOprScore suggestion: list) {
            if (suggestion.getScore() > max - upLimit) printOp (suggestion);
        } // for
        out.println("---------------------------------------------------");
        for (WebServiceOprScore suggestion: list) {
            if (suggestion.getScore() >= mean && suggestion.getScore() <= max - upLimit) printOp (suggestion);
        } // for
        out.println("---------------------------------------------------");
        for (WebServiceOprScore suggestion: list) {
            if (suggestion.getScore() < mean) printOp (suggestion);
        } // for
        out.println("---------------------------------------------------");
        out.println();
        
    } // printMetrics
    
   
} // TestMetrics
