
package util;

import java.util.List;
import org.jdom.Element;

/**
 * Currently not Used
 * @author Rui Wang
 * @see LICENSE (MIT style license file).
 *
 */
public class IODG {
	
	private Element node = null;
	private List<IODG> children = null;
	
	/**
	 * from this node to the lowest leaf
	 * leaf is level 1
	 */
	private int level;
	private Element matchNode = null;
	/**
	 * the operation name and wsdl name of this node, and the matching score between this node and its matchNode
	 */
	private WebServiceOprScore opWsdlScore= null; 

	/**
	 * used for dfs algorithm
	 * -1(not visit), 
	 * -2 (visit but no add to result), 
	 * -3(add to result)
	 * or used to index this node position in the postorder node list [0..n]
	 */
	private int dfsVisit=-1;
	
	/**
	 * @param dfsVisit the dfsVisit to set
	 */
	public void setDfsVisit(int dfsVisit) {
		this.dfsVisit = dfsVisit;
	}

	/**
	 * @return the dfsVisit
	 */
	public int getDfsVisit() {
		return dfsVisit;
	}

	/**
	 * @return the children
	 */
	public List<IODG> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<IODG> children) {
		this.children = children;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @return the matchNode
	 */
	public Element getMatchNode() {
		return matchNode;
	}

	/**
	 * @param matchNode the matchNode to set
	 */
	public void setMatchNode(Element matchNode) {
		this.matchNode = matchNode;
	}

	/**
	 * @return the node
	 */
	public Element getNode() {
		return node;
	}

	/**
	 * @return the op name
	 */
	public String getOp() {
		return opWsdlScore.getOperationName();
	}

	/**
	 * @return the Wsdl name
	 */
	public String getWsdl() {
		return opWsdlScore.getWsDescriptionDoc();
	}
	/**
	 * @return the opWsdlScore
	 */
	public double getScore() {
		return opWsdlScore.getScore();
	}
	
	/**set score
	 * @param score
	 */
	public void setScore(double score) {
		this.opWsdlScore.setScore(score);
	}
	
	/**
	 * constructor full
	 */
	public IODG(Element node, WebServiceOpr opWsdl, List<IODG> children, int level) {
		this.node = node;
		this.children = children;
		//initially, the matching score set to negative
		this.opWsdlScore = new WebServiceOprScore(opWsdl.getOperationName(),opWsdl.getWsDescriptionDoc(),-10);
		this.level =level;
	}
	
	/**
	 * constructor simple
	 */
	public IODG(Element node, WebServiceOpr opWsdl) {
		this.node = node;
		//initially, the matching score set to negative
		this.opWsdlScore = new WebServiceOprScore(opWsdl.getOperationName(),opWsdl.getWsDescriptionDoc(),-10);
	}

	/**for test
	 * @param args
	 */
	public static void main(String[] args) {


	}

}
