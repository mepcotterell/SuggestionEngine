
package util;

import java.util.*;

/**
 * This class stores the matched input-output paths
 * 
 * @author Alok Dhamanaskar 
 * @see LICENSE (MIT style license file). 
 * 
 */
public class MatchedIOPaths  {

    private String matchedWsOP;
    private String matchedoprWsDoc;
    private String WsOp;
    private String WsDoc;
    private List<MatchedIOPaths.PathMatches> matchedPaths = new ArrayList<MatchedIOPaths.PathMatches>();
    
    /**
     * Constructor to set Web service and operation name
     * 
     * @param WsName The WsDocumentation Location / URL
     * @param opName Name of the operation
     */
    MatchedIOPaths(String WsName, String opName)
    {
        matchedoprWsDoc = WsName;
        matchedWsOP = opName;
    }

    public MatchedIOPaths() {

    }

    /**
     * Return the operation name, with which paths are matched
     * @return The matched web service operation
     */
    public String getMatchedWsOpr() {
        return matchedWsOP;
    }

    /**
     * Set the operation name, with which paths are matched
     * @param The matched web service operation name to set
     */
    public void setMatchedWsOpr(String matchedWsOpr) {
        this.matchedWsOP = matchedWsOpr;
    }

    /**
     * Returns the WsDocumentation Location / URL
     * @return the matched WsDocumentation Location / URL
     */
    public String getMatchedoprWsDoc() {
        return matchedoprWsDoc;
    }

    /**
     * Sets the WsDocumentation Location / URL
     * @param  the matched WsDocumentation Location / URL
     */
    public void setMatchedoprWsDoc(String atchedoprWsDoc) {
        this.matchedoprWsDoc = atchedoprWsDoc;
    }

    /**
     * Adds a input - output path match and its confidence level
     *
     * @param ip Name of the input Element matched
     * @param op Name of the output Element matched
     * @param conf the confidence level for the match
     */
    public void addMatchedPaths(String ip, String op, Double conf) {
              
        if (ip == null)
            ip = "na";
        if (op == null)
            op = "na";
        if (conf == null)
            conf = 0.00;

        conf = (double)Math.round(conf * 10000) / 100;

        
        String wsName = "";
        String wsDoc = "";
        
        String[] ww = matchedoprWsDoc.split("/");
        if (matchedoprWsDoc.contains("/"))
             wsName = ww[ww.length -1];

        wsName = wsName.replace("sawsdl", "");
        wsName = wsName.replace("SAWSDL", "");
        wsName = wsName.replace("WSDL", "");
        wsName = wsName.replace("wsdl", "");        
        wsName += matchedWsOP + ":" + ip;
        //-----------------------------------
        ww = WsDoc.split("/");
        if (WsDoc.contains("/"))
             wsDoc = ww[ww.length -1];

        wsDoc = wsDoc.replace("sawsdl", "");
        wsDoc = wsDoc.replace("SAWSDL", "");
        wsDoc = wsDoc.replace("WSDL", "");
        wsDoc = wsDoc.replace("wsdl", "");        
        wsDoc += WsOp + ":" + op;

        MatchedIOPaths.PathMatches p = new MatchedIOPaths.PathMatches(wsName, wsDoc, conf);
        matchedPaths.add(p);    
    }

    /**
     * Returns a list of objects of class PathMatches
     * @return the matchedPaths
     */
    public List<MatchedIOPaths.PathMatches> getMatchedPaths() {
        return matchedPaths;
    }

    /**
     * Sets matchedPaths to a list of objects of class PathMatches
     * @param matchedPaths the matchedPaths to set
     */
    public void setMatchedPaths(List<MatchedIOPaths.PathMatches> matchedPaths) {
        this.matchedPaths = matchedPaths;
    }
    
    /**
     * Sort the list of PathMatches based on the confidence value
     * 
     */
    public void sort()
    {
        Collections.sort(matchedPaths, Collections.reverseOrder());
    }

    /**
    * Set the Web service Operation name
    * @param operationName 
    */
    public void setWsoOpr(String operationName) {
        this.WsOp = operationName;
    }
    
    /**
     * Set web service description document
     * @param wsDescriptionDoc 
     */
    public void setWsDoc(String wsDescriptionDoc) {
        this.WsDoc = wsDescriptionDoc;
    }
    
    /**
     * Returns the Web service Operation name
     * 
     */
    public String getWsoOpr() {
        return this.WsOp;
    }

    /**
     * Returns the Web service documentation URL
     * 
     * @return 
     */
    public String getWsDoc() {
        return this.WsDoc;
    }

    /**
     * @author Alok Dhamanaskar
     * An internal class that represents / stores a single (input-output) 
     * match along with the confidence level
     * 
     */
    public class PathMatches implements Comparable<PathMatches>
    {
        private String ipName;
        private String opName;
        private double confidenceLevel;

        /**
         * Constructor for the class PathMatches to set input name output, name and the confidence level
         * 
         * @param ip Name of the input Element matched
         * @param op Name of the output Element matched
         * @param conf the confidence level for the match
         */
        private PathMatches(String ip, String op, double conf) {
            ipName = ip;
            opName = op;
            confidenceLevel = conf;
        }

        /**
         * Returns the name of the input
         * @return the input name
         */
        public String getIpName() {
            return ipName;
        }

        /**
         * Sets the input name
         * @param ipName the name of input to set
         */
        public void setIpName(String ipName) {
            this.ipName = ipName;
        }

        /**
         * Returns the name of the output
         * @return the output name
         */
        public String getOpName() {
            return opName;
        }

        /**
         * Sets the output name
         * @param opName the name of output to set
         */
        public void setOpName(String opName) {
            this.opName = opName;
        }

        /**
         * Returns the confidence level for the match
         * @return the confidenceLevel
         */
        public double getConfidenceLevel() {
            return confidenceLevel;
        }

        /**
         * Sets the confidence level for the match
         * @param confidenceLevel the confidenceLevel to set
         */
        public void setConfidenceLevel(double confidenceLevel) {
            this.confidenceLevel = confidenceLevel;
        }

        @Override
        public int compareTo(PathMatches o) {
        Double a = getConfidenceLevel();
        return a.compareTo(o.getConfidenceLevel());
        }
        
    }// Class PathMatches ends
   
}// class MatchedIOPaths ends
