package stringMatcher;

/**
 * This algorithm is based on Levenshtein's Algorithm
 * References: http://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance
 * @author Akshay Choche
 * @version 1.0
 * @see LICENSE (MIT style license file).
 */
public class CompareTerm {
    
        /**
         * This method returns the minimum number between a, b and c
         * @param a Number to be compared
         * @param b Number to be compared
         * @param c Number to be compared
         * @return Return the maximum of number between a, b and c
         */
        private static int minimum(int a, int b, int c) {
                return Math.min(Math.min(a, b), c);
        }//minimum

        /**
         * This Algorithm return the number of characters that need to be changed in str1 to make it equal to str2
         * @param str1 One of the terms to be compared
         * @param str2 One of the terms to be compared
         * @return return the number of characters that need to be changed in str1 to make it equal to str2
         */
        public static int getScore(CharSequence str1, CharSequence str2) {
                int[][] distance = new int[str1.length() + 1][str2.length() + 1];

                for (int i = 0; i <= str1.length(); i++)
                        distance[i][0] = i;
                for (int j = 0; j <= str2.length(); j++)
                        distance[0][j] = j;

                for (int i = 1; i <= str1.length(); i++)
                        for (int j = 1; j <= str2.length(); j++)
                                distance[i][j] = minimum(
                                                distance[i - 1][j] + 1,
                                                distance[i][j - 1] + 1,
                                                distance[i - 1][j - 1]
                                                                + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0
                                                                                : 1));

                return distance[str1.length()][str2.length()];
        }//getScore

        /**
         * This Algorithm computes the similarity score between two terms
         * @param term1 One of the terms to be compared
         * @param term2 One of the terms to be compared
         * @return the similarity score between two terms
         */
        public static double getSimilarity(String term1, String term2){
            if(term1 == null || term2 == null)  return 0;
            return (1 - (CompareTerm.getScore(term1, term2)/(float) Math.max(term1.length(), term2.length())));
        }//getSimilarity
}
