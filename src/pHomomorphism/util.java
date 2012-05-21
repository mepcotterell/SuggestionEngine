
package pHomomorphism;

import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file). 
 */
public class util {
    
/**
 * Returns the intersection of two lists passed to it.
 * @param List1
 * @param List2
 * @return 
 */
    public static ArrayList<Integer> intersection(ArrayList<Integer> List1, ArrayList<Integer> List2) {
        ArrayList<Integer> intersection =  new ArrayList<Integer>();
        
        for (int i : List1)
            if(List2.contains(i))
                intersection.add(i);
        
        return intersection;
    }
    
    public static void main(String[] args)
    {
        //Test code
        ArrayList<Integer> List1 =  new ArrayList<Integer>();
        ArrayList<Integer> List2 =  new ArrayList<Integer>();
        List1.add(1);
        List1.add(2);
        List1.add(4);
        List1.add(4);
        
        List2.add(1);
        List2.add(3);
        List2.add(4);        
        
        System.out.println(intersection(List1, List2));
        
    }

    static ArrayList<Integer> intersection(ArrayList<Integer> List1, Set<Integer> List2) {
        ArrayList<Integer> intersection =  new ArrayList<Integer>();
        
        for (int i : List1)
            if(List2.contains(i))
                intersection.add(i);
        
        return intersection;    }

}
