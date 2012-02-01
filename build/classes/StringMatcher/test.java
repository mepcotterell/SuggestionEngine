/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StringMatcher;

/**
 *
 * @author alok
 */
public class test {
    
    public static void main(String args[])
    {
        String s1="To change this template, choose Tools Templates";
        String s2 ="changing this template requires choosing tools template";
        double t = CompareDefination.getSimilarity(s1, s2);
        System.out.println("def to def : "+t);
       
        t = CompareTermDef.getSimilarity("template", s2);
        System.out.println("term to def : "+t);

        t = CompareTerm.getSimilarity("template", "testing");
        System.out.println("term to term : "+t);
       
    }
}
