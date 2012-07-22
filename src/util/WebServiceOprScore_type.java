package util;

import java.util.List;
import org.jdom.Element;

/**
 * 
 * A class that represents Input/Output paths, score for that path, Web service and operation it belongs to. 
 * @author Rui Wang
 * @author ALok Dhamanaskar
 * @see LICENSE (MIT style license file).
 * 
 */
public class WebServiceOprScore_type extends WebServiceOprScore implements Comparable<WebServiceOprScore> {

    private boolean isInput = false;
    private List<Element> path = null;

    private double isSemSafeMatchOutpath = 0;
    private double isTypeSafeMatchOutpath = 0;
    
    public static  double require = 1;
    public static double unknown = 0.8;
    public static double optional = 0.1;

    /**
     * Setter for isSemSafeMatchOutpath
     * @param isSemSafeMatchOutpath 
     */
    public void setIsSemSafeMatchOutpath(double isSemSafeMatchOutpath) {
        this.isSemSafeMatchOutpath = isSemSafeMatchOutpath;
    }

    /**Getter for isSemSafeMatchOutpath
     * @return isSemSafeMatchOutpath
     */
    public double getIsSemSafeMatchOutpath() {
        return isSemSafeMatchOutpath;
    }

    /**
     * Setter for setIsTypeSafeMatchOutpath
     * @param setIsTypeSafeMatchOutpath t
     */
    public void setIsTypeSafeMatchOutpath(double isTypeSafeMatchOutpath) {
        this.isTypeSafeMatchOutpath = isTypeSafeMatchOutpath;
    }

    /**Getter for isTypeSafeMatchOutpath
     * @return isTypeSafeMatchOutpath
     */
    public double getIsTypeSafeMatchOutpath() {
        return isTypeSafeMatchOutpath;
    }

    /**
     * Returns a boolean value, true if it is input path and false if output path
     * @return isInput as a Boolean value
     */
    public boolean isInput() {
        return isInput;
    }

    /**
     * Returns the path as List<Element>
     * @return path
     */
    public List<Element> getPath() {
        return path;
    }
    /**
     * Returns the path as List<Element>
     * @return path
     */
    public void setPath(List<Element> p) {
        this.path = p;
    }
    /**
     * Checks if the path is required or not
     * @return  Double value 1 for required, 0.2 for not required and 0.8: unknown
     */
    public double isRequired() {
        
        double required = this.unknown;
        Element leafElem = path.get(0);

        // Check to see if any parent node is optional
        for (int i = 1; i < path.size(); i++) {
            // get a parent element
            Element parent = path.get(i);

            if (parent.getAttribute("nillable") != null) {
                boolean nillable = parent.getAttributeValue("nillable").equals("true");            
                if (nillable)
                    return this.optional;
            }// if ends
        }// for ends
        
        
        // All parent Nodes are either Required or not Specified
        if (leafElem.getAttribute("nillable") == null && leafElem.getAttribute("minOccurs") == null) {
            required = this.unknown;
        }


        if (leafElem.getAttribute("nillable") != null) {
            boolean nillable = leafElem.getAttributeValue("nillable").equals("true");            
            
            if (nillable) {
                // if the element is nillable then...
                required = this.optional;
            } else {
                // if the element isn't nillable then..
                if (leafElem.getAttribute("minOccurs") != null) { 
                    // if minOccurs is present
                    if (leafElem.getAttributeValue("minOccurs").equals("0")) {
                        // if minOccurs = 0 and the element is not nillable
                        required = this.optional;
                    } else {
                        // if minOccurs > 0 and the element is not nillable
                        required = this.require;
                    } // if
                } else {
                    // if minOccurs is not present and the element is not nillable
                    required = this.require;
                } // if
                
            } // if
            
        } else {
            // if nillable isn't there at all
            required = this.require;
        } // if

        return required;
    }

    /**
     * Constructor to create a WebServiceOprScore_type which would basically denote an input/output path
     * 
     * @param op Operation Name
     * @param wsdl Web service Description document location
     * @param path Input / output path as a list of elements
     * @param isInput Boolean value that determines if its an input / output path
     */
    public WebServiceOprScore_type(String op, String wsdl, List<Element> path, boolean isInput) {
        //score = -1 means it is not calculated yet
        super(op, wsdl, -1);
        this.isInput = isInput;
        this.path = path;
    }

}//class ends
