
package util;

import java.io.FileInputStream;
import java.util.Properties;

/**
 *
 * @author Alok Dhamanaskar
 * @see    LICENSE (MIT style license file).
 * 
 */
public class PropertyFileManager
{
    static String propertiesFilePath = "/home/alok/Desktop/SuggestionEngine/src/SSE.properties";

    //Returns the value for a property (that is passed as a String to it)
    public static String getValueFromProperty(String property){
           try{

                //Creating an object of java.util.Properties
                Properties prop = new Properties();
                //Loading the properties file as a Input file
                prop.load(new FileInputStream(propertiesFilePath));
                //Using getProperty method defined in java.util.Properties
                //to read the value for a particular key
                String value = prop.getProperty(property);
                return value;
           }
           catch(Exception e){
                 return "Property-Value pair NotFound in the globalVar.properties file";
           }
       }

}
