/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.text.DecimalFormat;
import java.util.List;
import util.OpWsdlScore;

/**
 * This class provides methods for displaying different metrics on a list of 
 * OpWsdlScore
 * @author Michael Cotterell
 */
public class TestMetrics {
    
    private static void printOp (OpWsdlScore suggestion) {
        String[] ww = suggestion.getWsdlName().split("/");
        String wsName = ww[ww.length -1].replace("sawsdl", "");
       	DecimalFormat twoDForm = new DecimalFormat("#.##");
        
        System.out.println(wsName +suggestion.getOpName() + "--" + twoDForm.format(suggestion.getScore()) );
    } // printOp
    
    public static void printMetrics (List<OpWsdlScore> list) {
        
        // Determine the mean, min, and max
        double sum = 0.0;
        double min = 1;
        double max = 0;
        
        for (OpWsdlScore suggestion: list) {
            sum += suggestion.getScore();
            if (suggestion.getScore() > max) max = suggestion.getScore();
            if (suggestion.getScore() < min) min = suggestion.getScore();
        } // for
        
        double mean = sum / (double) list.size();

        // Determine the variance and standard deviation
        sum = 0.0;
        for (OpWsdlScore suggestion: list) {
            sum += Math.pow(suggestion.getScore() - mean, 2);
        } // for
        
        double variance = sum / (double) list.size();
        double stddev   = Math.sqrt(variance);
        double xStd = (max- mean)/stddev;
        double upLimit = (0.5 * xStd) * stddev;
        
        
        System.out.println();
        System.out.println("METRICS");
        System.out.println("---------------------------------------------------");
        System.out.println("Mean     = " + mean);
        System.out.println("Min      = " + min);
        System.out.println("Max      = " + max);
        System.out.println("Variance = " + variance);
        System.out.println("Std.Dev. = " + stddev);
        System.out.println("---------------------------------------------------");
        System.out.println("H        = (" + ( max - upLimit) + ", " + max + "]");
        System.out.println("M        = [" + (mean) + ", " + ( max - upLimit) + "]");
        System.out.println("L        = [" + min + ", " + (mean) + ")");
        System.out.println("---------------------------------------------------");
        System.out.println();
        System.out.println("GROUPS");
        System.out.println("---------------------------------------------------");
        
        

        for (OpWsdlScore suggestion: list) {
            if (suggestion.getScore() > max - upLimit) printOp (suggestion);
        } // for
        System.out.println("---------------------------------------------------");
        for (OpWsdlScore suggestion: list) {
            if (suggestion.getScore() >= mean && suggestion.getScore() <= max - upLimit) printOp (suggestion);
        } // for
        System.out.println("---------------------------------------------------");
        for (OpWsdlScore suggestion: list) {
            if (suggestion.getScore() < mean) printOp (suggestion);
        } // for
        System.out.println("---------------------------------------------------");
        System.out.println();
        
    } // printMetrics
    
} // TestMetrics
