
package util;

/**
 *  
 * A class that represents a single Web service operation, by storing the operation name 
 *
 * and the WSDL/ WADL location. Include setters and getters for the same.
 * @author Rui Wang
 * @author Alok Dhamanaskar (alokd@uga.edu)
 * @see LICENSE (MIT style license file).
 * 
 * @param operationName Name of the Operation in The WSDL/WADL
 * @param wsDescriptionDoc Name / Location of the Web service Description Document (WSDL/WADL)
 * @param extraInfo Some extra info, not sure if needed / used  
 * @param wsDocType Stores the type of the Web service Document (WSDL / WADL)
 */
public class WebServiceOpr
{
    private String operationName = null;
    private String wsDescriptionDoc = null;
    private String extraInfo = null;
    private String wsDocType = null;

    /**
     * Returns any additional Info about the WSDL/WADL if added
     * @return the AdditionalInfo
     */
    public String getExtraInfo()
    {
        return extraInfo;
    }

    /**
     * Returns the Name of the Operation in the WSDL/WADL
     * @return the Name of the Operation
     */
    public String getOperationName()
    {
        return operationName;
    }

    /**
     * Returns the Name / Location of the WSDL/WADL document
     * @return the Name of the WSDL
     */
    public String getWsDescriptionDoc()
    {
        return wsDescriptionDoc;
    }

    /**
     * Returns the type of Web service description Document
     * @return type of Web service description Document
     */
    public String getWsDocType()
    {
        return wsDocType;
    }    
    
    /**
    * Constructor to set the operation Name, the WSDL / WADL location and any Extra info
    * @param op Name of the Operation in the WSDL / WADL
    * @param wsdl Location of the WSDL / WADL Document
    * @param extra Any extra info about the WSDL / WADL
    */    
    public WebServiceOpr(String op, String wsdl, String extra)
    {
        this(op, wsdl);
        this.extraInfo = extra;
    }
    
    /**
    * Constructor to set the operation Name and the WSDL / WADL location
    * @param op Name of the Operation in the WSDL / WADL
    * @param wsdl Location of the WSDL / WADL Document
    */
    public WebServiceOpr(String op, String wsdl)
    {
        this.operationName = op;
        this.wsDescriptionDoc = wsdl;
    }

    /**
     * Sets any additional Info about the WSDL/WADL
     * 
     * @param extraInfo the extraInfo to set
     */
    public void setExtraInfo(String extraInfo)
    {
        this.extraInfo = extraInfo;
    }

    /**
     * Sets the type of Web service description Document
     * @param docType type of Web service description Document
     */
    public void setWsDocType(String docType)
    {
        this.wsDocType = docType;
    }    

    /**
     * Compares the WebServiceOpr object the function is called for with the other WebServiceOpr object
     * passed to it, by comparing both WSDL/WADL respective Paths and the respective operation names 
     * 
     * @param WebServiceOpr
     * @return True/False
     */
    public boolean compareTo(WebServiceOpr other)
    {
        if (this.getOperationName().compareTo(other.getOperationName()) != 0)
            return false;

        if (this.getWsDescriptionDoc().compareTo(other.getWsDescriptionDoc()) != 0)
            return false;

        return true;
    }
}
