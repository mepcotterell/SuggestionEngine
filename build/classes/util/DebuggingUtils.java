/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.List;
import java.util.Set;
import org.jdom.Element;

/**
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
}
