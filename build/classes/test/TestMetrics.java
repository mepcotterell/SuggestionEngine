/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

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
        System.out.println(wsName +suggestion.getOpName() + "\t" + suggestion.getScore());
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
        
        System.out.println();
        System.out.println("METRICS");
        System.out.println("---------------------------------------------------");
        System.out.println("Mean     = " + mean);
        System.out.println("Min      = " + min);
        System.out.println("Max      = " + max);
        System.out.println("Variance = " + variance);
        System.out.println("Std.Dev. = " + stddev);
        System.out.println("---------------------------------------------------");
        System.out.println("H        = (" + (mean + stddev) + ", " + max + "]");
        System.out.println("M        = [" + (mean - stddev) + ", " + (mean + stddev) + "]");
        System.out.println("L        = [" + min + ", " + (mean - stddev) + ")");
        System.out.println("---------------------------------------------------");
        System.out.println();
        System.out.println("GROUPS");
        System.out.println("---------------------------------------------------");
        for (OpWsdlScore suggestion: list) {
            if (suggestion.getScore() > mean + stddev) printOp (suggestion);
        } // for
        System.out.println("---------------------------------------------------");
        for (OpWsdlScore suggestion: list) {
            if (suggestion.getScore() >= mean - stddev || suggestion.getScore() <= mean + stddev) printOp (suggestion);
        } // for
        System.out.println("---------------------------------------------------");
        for (OpWsdlScore suggestion: list) {
            if (suggestion.getScore() < mean - stddev) printOp (suggestion);
        } // for
        System.out.println("---------------------------------------------------");
        System.out.println();
        
    } // printMetrics
    
} // TestMetrics
