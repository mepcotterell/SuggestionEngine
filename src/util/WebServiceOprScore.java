
package util;

/**
 * 
 * A class that represents scores and sub-scores (DataMediation, Functionality and Preconditons & effects)
 * for a particular Web service operation, along with getters, setters and comparable for the same
 * 
 * @author Rui Wang
 * @author Alok Dhamanaskar
 * @see LICENSE (MIT style license file).
 *  
 * @param score The score assigned to the candidate Web service operation which is a weighted sum of the Subscores 
 * @param dmScore The data Mediation Score
 * @param fnScore The functionality Score that denotes how well the desired functionality aligns 
 *                with the functionality of this operation 
 * @param peScore The pre-conditions and effects score: Currently not used/assigned
 * 
 */
public class WebServiceOprScore extends WebServiceOpr implements Comparable<WebServiceOprScore>
{
    private double score;
    private double dmScore;
    private double fnScore;
    private double peScore;
  
    private MatchedIOPaths matchedPathsIp;
    private MatchedIOPaths matchedPathsOp;
         

    /**
     * Returns the score assigned to this operation which is the weighted sum of sub scores
     * @return Returns the score as a double
     */
    public double getScore()
    {
        return score;
    }

    /**
     * Sets the score assigned to this operation which is the weighted sum of sub scores
     * @param score The score to set as a double value
     */
    public void setScore(double score)
    {
        this.score = score;
    }

    /* able to compare double or int
     * (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(WebServiceOprScore o)
    {
        Double a = getScore();
        return a.compareTo(o.getScore());
    }

    /**
     * Constructor that sets Operation Name, WSDL/WADL path and the score
     * 
     * @param op Name of the Web  service operation
     * @param wsdl Location of the WSDL / WADL document
     * @param score The score assigned to this operation
     */
    public WebServiceOprScore(String op, String wsdl, double score)
    {
        super(op, wsdl);
        this.score = score;
    }

    /**
     * Returns the data mediation sub-score assigned to this operation
     * @return The data mediation sub-score as a double value
     */
    public double getDmScore()
    {
        return dmScore;
    }

    /**
     * Sets the data mediation sub-score assigned to this operation
     * @param dmScore The data mediation sub-score to set as a double value
     */
    public void setDmScore(double dmScore)
    {
        this.dmScore = dmScore;
    }

    /**
     * Returns the functionality sub-score assigned to this operation
     * @return The functionality sub-score as a double value
     */
    public double getFnScore()
    {
        return fnScore;
    }

    /**
     * Sets the functionality sub-score assigned to this operation
     * @param fnScore The functionality sub-score as a double value
     */
    public void setFnScore(double fnScore)
    {
        this.fnScore = fnScore;
    }

    /**
     * Returns the pre-condition & effects sub-score assigned to this operation
     * @return The pre-condition & effects sub-score as a double value
     */
    public double getPeScore()
    {
        return peScore;
    }

    /**
     * Sets the pre-condition & effects sub-score assigned to this operation
     * @param peScore The pre-condition & effects sub-score as a double value
     */
    public void setPeScore(double peScore)
    {
        this.peScore = peScore;
    }

    /**
     * Returns the matched paths for Input.
     * Used in ForwardSuggest and BidirectionalSuggest
     * @return the matchedPathsIp
     */
    public MatchedIOPaths getMatchedPathsIp() {
        return matchedPathsIp;
    }

    /**
     * Sets the matched paths for Input.
     * Used in ForwardSuggest and BidirectionalSuggest
     * @param matchedPathsIp the matchedPathsIp to set
     */
    public void setMatchedPathsIp(MatchedIOPaths mIps) {
        this.matchedPathsIp = mIps;
    }

    /**
     * Returns the matched paths for Output.
     * Used in BackwardSuggest and BidirectionalSuggest
     * @return the matchedPathsOp
     */
    public MatchedIOPaths getMatchedPathsOp() {
        return matchedPathsOp;
    }

    /**
     * Sets the matched paths for Output.
     * Used in BackwardSuggest and BidirectionalSuggest
     * @param matchedPathsOp the matchedPathsOp to set
     */
    public void setMatchedPathsOp(MatchedIOPaths mOps) {
        this.matchedPathsOp = mOps;
    }

}
