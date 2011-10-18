/**
 * 
 */
package util;

/**
 * @author Rui Wang
 *
 */
public class OpWsdl {

	private String opName = null;
	private String wsdlName = null;
        private String extraInfo = null;
        
        /**
	 * @return the opName
	 */
	public String getExtraInfo() {
		return extraInfo;
	}
        
	/**
	 * @return the opName
	 */
	public String getOpName() {
		return opName;
	}

	
	/**
	 * @return the wsdlName
	 */
	public String getWsdlName() {
		return wsdlName;
	}

	
	/**
	 * 
	 */
	public OpWsdl(String op, String wsdl, String extra) {
            this(op, wsdl);
            this.extraInfo = extra;
	}
        
        public OpWsdl(String op, String wsdl) {
		this.opName = op;
		this.wsdlName = wsdl;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

    /**
     * @param extraInfo the extraInfo to set
     */
    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }
    
    
    public boolean compareTo(OpWsdl other)
    {
        if (this.getOpName().compareTo(other.getOpName())!=0 ) return false;
        
        if (this.getWsdlName().compareTo(other.getWsdlName())!=0 ) return false;

        return true;
    }        
            

}
