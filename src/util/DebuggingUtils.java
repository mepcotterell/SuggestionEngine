
package util;

import java.util.List;
import org.jdom.Attribute;
import org.jdom.Element;

/**
 *
 * Contains Utility Functions for debugging like Printing i/o paths 
 * 
 * @author Alok Dhamanaskar
 * @see    LICENSE (MIT style license file).
 * 
 */

public class DebuggingUtils
{
    public static void printCollection(List<String> s)
    {
        for (String str : s)
        {
            System.out.println(str);
        
        }
    }

    /**
     * Print all the paths for an input or output.
     * 
     * @param nextOpInPaths A list of Paths (List of Elements)
     * @param WSDLName WSDL Name
     * @param OpName Operation Name
     * @param type 
     */
    public static void printPaths(List<List<Element>> nextOpInPaths, String WSDLName, String OpName, String type)
    {
    
        System.out.println("All Paths for " + type + " : " +WSDLName +"-" + OpName);
        for ( List<Element> e : nextOpInPaths)
        {
            for ( Element e1 : e)        
            {
                System.out.print(e1.getAttribute("name"));
                System.out.print(e1.getAttribute("nillable"));
                System.out.println();
            }
            System.out.println("-----------------------------------------");
        }
    }

       
    /**
     * Print the path for an input or output
     * @param Paths List of elements
     */
    public static void printPath(List<Element> Paths)
    {
            for ( Element e1 : Paths)        
            {
                Attribute name = e1.getAttribute("name");
                //Attribute nillable = e1.getAttribute("nillable");
                System.out.print(name.getValue() + "--");
                //System.out.print(nillable.getValue());
            }
            System.out.println();

    }
    
    /**
     * The method calculates generalized mean for a given array of (n) values and an Exponent Value(m).
     * mean = ((1/n)SUM(Xi))^(1/m)
     * m = 0 Geometric mean,
     * m = 1 Arithmetic mean,
     * m = 3 Quadratic Mean (Root mean Square)
     * 
     * @param values for which mean has to be calculated
     * @param exponent
     * @return the mean
     */
    public static double generalizedMean(double[] values, double exponent )
    {
        double mean = 0.0;

        for (int i = 0; i < values.length; i++)
            mean += Math.pow(values[i], exponent);

        mean = mean / (double) values.length;
        mean = Math.pow(mean, 1.0 / exponent);

        return mean;
    }//generalizedMean
    
    public static void main(String[] args)
    {
        System.out.println("mean = " + generalizedMean(new double[]{0.3,0.3,1}, 3));
    
    }//main
    
}
