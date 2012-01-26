/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package StringMatcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * This class is used for preprocess a string and removing all stop words from them
 *
 * @author Akshay Choche
 * @version 1.0
 */
public class RemoveStopWords {

    static ArrayList<String> stopWords = new ArrayList<String>();

    public static void populateStopWords(){
        if(stopWords.isEmpty()){
            File Stop = new File("/home/alok/Desktop/SuggestionEngine/src/StringMatcher/stop.txt");
            try {
                Scanner stopscanner = new Scanner(Stop);
                while (stopscanner.hasNext()) {
                    String temp = stopscanner.nextLine();
                    temp = temp.trim();
                    stopWords.add(temp);
                }
            } catch (FileNotFoundException e) {
                System.out.println("File not found.");
                e.printStackTrace();
            }
        }
    }//populateStopWords
    
    public static String removeStop(String temp){
        populateStopWords();
        String result = "";
        StringTokenizer stop = new StringTokenizer(temp);
        String word = "";        
        while(stop.hasMoreTokens()){
            word = stop.nextToken();
            word = word.replaceAll("[`~!@#$%^&*()_+={}:;\"',<.>?/]", "");
            word = word.replace("[", "");
            word = word.replace("]", "");            
            if(!stopWords.contains(word.toLowerCase())){
                result = result +" "+ word;
            }
        }
        return result;
    }//removeStop

    public static void main(String[] args){        
        System.out.println("Program [The BLAST program to be used for the Sequence Similarity Search.]exp -" +": " + RemoveStopWords.removeStop("Program [The BLAST program to be used for the Sequence Similarity Search.] exp -"));
        System.out.println("The status of the job (FINISHED, ERROR, RUNNING, NOT_FOUND or FAILURE)"+": " + RemoveStopWords.removeStop("The status of the job (FINISHED, ERROR, RUNNING, NOT_FOUND or FAILURE)"));
    }//main
}//RemoveStopWords
