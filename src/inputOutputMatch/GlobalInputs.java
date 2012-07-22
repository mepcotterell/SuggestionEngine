
package inputOutputMatch;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import ontologySimilarity.OntologySimilarityImpl;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * 
 * Calculate the matchScore for an input with any of the Global Inputs
 * 
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file). 
 *
 */

public class GlobalInputs { 
    
    private static Namespace sawsdlNS = Namespace.getNamespace("sawsdl", "http://www.w3.org/ns/sawsdl");

    //Creating a Logger for this class
    private static final Logger log  = Logger.getLogger(GlobalInputs.class.getName());
    //To disply Steps in Calculation of Sub-scores set debug to Level.INFO else set to Level.FINE
    private static final Level debug = Level.FINE;

    static double globalInputMatchScore(Element leaf, List<String> globalIps, String OWLuri) 
    {
        double maxMatchScore = 0.0;
        double matchScore = 0.0;
        
        if (leaf == null || globalIps == null || globalIps.isEmpty())
            return maxMatchScore;

        String leafModelRef = leaf.getAttributeValue("modelReference", sawsdlNS);
        boolean leafMFflag = true;
        
        if (leafModelRef == null || leafModelRef.equals(""))
            leafMFflag = false;
        
        if (leafMFflag)
        {
            //modelreference exist
            for (String global : globalIps)
            {
                if (global.contains("http"))
                {
                    //global input is a model reference
                    ontologySimilarity.OntologySimilarity os = new OntologySimilarityImpl();
                    matchScore = os.getConceptSimScore(global, leafModelRef, OWLuri);
                    if (matchScore > maxMatchScore) maxMatchScore = matchScore;
                    
                }//if
                else
                {
                    //Global input is a key word : 
                    //May be compare the global input as String to onto Lable or perhaps definition
                
                }//else
            }//for
        
        }//if
        else
        {
            //model reference does not exist
            String leafName = leaf.getAttributeValue("name");
            for (String global : globalIps)
            {
                if (global.contains("http"))
                {
                    //global input is a model reference

                }//if
                else
                {
                    //may be compare the global input as String to onto Lable or perhaps definition
                
                }//else
            }//for
        
        
        }//else
        
        //Penalize Score from Global Inputs
        return maxMatchScore * 0.7;
    }//globalInputMatchScore
    
    
    public static void main(String[] args)
    {
        //Test code
  
    
    }//main


}//GlobalInputs
