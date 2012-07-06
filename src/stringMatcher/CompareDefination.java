
package stringMatcher;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
/**
 * This algorithm is based on Dice Algorithm
 * Reference: http://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Dice's_coefficient
 * @author Akshay Choche
 * @see LICENSE (MIT style license file).
 * @version 1.0
 */
public class CompareDefination {
    /**
     * This method computes the similarity between two definitions using Dice Algorithm
     * @param s1 Definition to be compared
     * @param s2 Definition to be compared
     * @return The Similarity score
     */
    public static double getSimilarity(String s1, String s2){
        if(s1 == null || s2 == null) return 0;
        s1 = RemoveStopWords.removeStop(s1);
        s2 = RemoveStopWords.removeStop(s2);
        double totcombigrams = 0;
        Set nx = new HashSet();
        Set ny = new HashSet();
        Set intersection = null;
        for (int i=0; i < s1.length()-1; i++) {
                char x1 = s1.charAt(i);
                char x2 = s1.charAt(i+1);
                String tmp = Character.toString(x1) + Character.toString(x2);
                nx.add(tmp);
        }
        for (int j=0; j < s2.length()-1; j++) {
                char y1 = s2.charAt(j);
                char y2 = s2.charAt(j+1);
                String tmp = Character.toString(y1) + Character.toString(y2);
                ny.add(tmp);
        }

        intersection = new TreeSet(nx);
        intersection.retainAll(ny);
        totcombigrams = intersection.size();
        
        double sim = (2*totcombigrams) / (nx.size()+ny.size());
        
        if (Double.isNaN(sim)) 
            return 0.00;
        else
            return sim;
    }//getSimilarity
}
