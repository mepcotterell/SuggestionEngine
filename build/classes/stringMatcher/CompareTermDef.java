
package stringMatcher;

import java.util.StringTokenizer;

/**
 * This class is used to compare a Definition with a Term and return a score
 * @author Akshay Choche
 * @version 1.0
 * @see LICENSE (MIT style license file).
 */
public class CompareTermDef {

    /**
     * This method is used to score a term with definition
     * @param term Is the term to be compared
     * @param defination Is the definition to be compared
     * @return Returns the similarity score
     */
    public static double getSimilarity(String term, String defination){
        double score = 0;
        defination = RemoveStopWords.removeStop(defination);
        term = RemoveStopWords.removeStop(term);        
        StringTokenizer ter = new StringTokenizer(term);
        int tokenTer = ter.countTokens();
        for(int cnt = 0; cnt<tokenTer; cnt++){
            double intermediate_score = 0;
            String termword = ter.nextToken();
            termword = termword.toLowerCase();
            StringTokenizer def = new StringTokenizer(defination);
            while(def.hasMoreTokens()){
                String defWord = def.nextToken();
                defWord = defWord.toLowerCase();
                double newscore = CompareTerm.getSimilarity(termword, defWord);
                if(newscore > intermediate_score){
                    intermediate_score = newscore;
                }//if
            }//while
            score += intermediate_score;
        }//for
        score /= tokenTer;
        return score;
    }//getSimilarity    
}//CompareTermDef
