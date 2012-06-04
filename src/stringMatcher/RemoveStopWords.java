
package stringMatcher;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;


/**
 * This class is used for preprocess a string and removing all stop words from them
 *
 * @author Akshay Choche
 * @version 1.0
 * @see LICENSE (MIT style license file).
 */
public class RemoveStopWords
{

    static List<String> stopWords = (List<String>) Arrays.asList(StopWords.stopWords);

    public static String removeStop(String temp)
    {
        String result = "";
        StringTokenizer stop = new StringTokenizer(temp);
        String word = "";
        while (stop.hasMoreTokens())
        {
            word = stop.nextToken();
            word = word.replaceAll("[`~!@#$%^&*()_+={}:;\"',<.>?/]", "");
            if (!stopWords.contains(word.toLowerCase()))
            {
                result = result + " " + word;
            }   
        }
        return result;
    }//removeStop

    public static void main(String[] args)
    {
        System.out.println("Program [ The BLAST program to be used for the Sequence Similarity Search.]exp -" + ": " + RemoveStopWords.removeStop("Program [ The BLAST program to be used for the Sequence Similarity Search.] exp -"));
        System.out.println("The status of the job (FINISHED, ERROR, RUNNING, NOT_FOUND or FAILURE)" + ": " + RemoveStopWords.removeStop("The status of the job (FINISHED, ERROR, RUNNING, NOT_FOUND or FAILURE)"));
    }//main
}//RemoveStopWords
