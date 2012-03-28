package util;

import java.util.List;

import org.jdom.Element;

/**
 * @author Rui
 *
 */
public class OpWsdlPathScore_type extends OpWsdlScore implements Comparable<OpWsdlScore> {

    private boolean isInput = false;
    private List<Element> path = null;
    private double isSemSafeMatchOutpath = 0;
    private double isTypeSafeMatchOutpath = 0;
    public static double require = 1;
    public static double unknown = 0.8;
    public static double optional = 0.1;

    /**
     * @param isSemSafeMatchOutpath the isSemSafeMatchOutpath to set
     */
    public void setIsSemSafeMatchOutpath(double isSemSafeMatchOutpath) {
        this.isSemSafeMatchOutpath = isSemSafeMatchOutpath;
    }

    /**getter if this path is semantic safe as an output path to map to the input
     * @return the isSemSafeMatchOutpath
     */
    public double getIsSemSafeMatchOutpath() {
        return isSemSafeMatchOutpath;
    }

    /**
     * @param isTypeSafeMatchOutpath the isTypeSafeMatchOutpath to set
     */
    public void setIsTypeSafeMatchOutpath(double isTypeSafeMatchOutpath) {
        this.isTypeSafeMatchOutpath = isTypeSafeMatchOutpath;
    }

    /**getter if this path is xsd type safe as an output path to map to the input
     * @return the isTypeSafeMatchOutpath
     */
    public double getIsTypeSafeMatchOutpath() {
        return isTypeSafeMatchOutpath;
    }

    /**
     * @return the isInput
     */
    public boolean isInput() {
        return isInput;
    }

    /**
     * @return the path
     */
    public List<Element> getPath() {
        return path;
    }

    /**check whether the leaf of the path is required input
     * @return  1: required, 0.2: not required, 0.8: unknown
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
            
                if (nillable) {
                    return this.optional;
                } // if
                
            } // if
             
        } // for
        
        
        // Below this line means that all parent nodes are required
        
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
     * @param op
     * @param wsdl
     * @param path
     * @param isInput
     */
    public OpWsdlPathScore_type(String op, String wsdl, List<Element> path, boolean isInput) {
        //score = -1 means it is not calculated yet
        super(op, wsdl, -1);
        this.isInput = isInput;
        this.path = path;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
    }
}
