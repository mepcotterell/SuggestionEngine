/**
 * 
 */
package parser;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import util.IODG;
import util.WebServiceOpr;

/**
 * @author Rui
 *
 */
public class LeafDmParser {
	
	/**given a message element, return all the bottom element of it
	 * @param msElem
	 * @param opWsdl
	 * @return
	 */
	private List<Element> getMsBottomElemList(Element msElem, WebServiceOpr opWsdl){
		if(msElem==null || opWsdl==null){
			System.out.println("LeafMediation.getMsBottomElemList:warning: given empty message element or opWsdl");
			return null;
		}
		MsIODGparser mp = new MsIODGparser();
		List<IODG> allNodesList = mp.getMsPostorderNodeList(msElem, opWsdl);
		List<Element> bottomElemList = new ArrayList<Element>();
		for(IODG node: allNodesList){
			if(node.getLevel()==1){
				bottomElemList.add(node.getNode());
			}
		}
		return bottomElemList;
	}
	
	/**given wsdl and operation name
	 * return a list of all the bottom element of the input of the operation
	 * @param wsdlName
	 * @param opName
	 * @return
	 */
	public List<Element> getInBottomElemList(String wsdlName, String opName){
		SawsdlParser sp = new SawsdlParser();
		Element inMsElem = sp.getInMsElem(wsdlName, opName);
		List<Element> inBottomElemList = this.getMsBottomElemList(inMsElem, new WebServiceOpr(opName, wsdlName));
		return inBottomElemList;
		
	}

	/**given wsdl and operation name
	 * return a list of all the bottom element of the output of the operation
	 * @param wsdlName
	 * @param opName
	 * @return
	 */
	public List<Element> getOutBottomElemList(String wsdlName, String opName){
		SawsdlParser sp = new SawsdlParser();
		Element outMsElem = sp.getOutMsElem(wsdlName, opName);
		List<Element> outBottomElemList = this.getMsBottomElemList(outMsElem, new WebServiceOpr(opName, wsdlName));
		return outBottomElemList;
		
	}
	
	/**constructor
	 * 
	 */
	public LeafDmParser() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
