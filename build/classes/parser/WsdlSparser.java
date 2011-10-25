/**
 * 
 */
package parser;

import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * @author Rui
 *
 */
public class WsdlSparser extends SawsdlParser {

	private static Namespace wssemNS = Namespace.getNamespace("wssem","http://lsdis.cs.uga.edu/projects/meteor-s/wsdl-s/examples/WSSemantics.xsd");

	/**given operation name and wsdl-s file name, return precondition of the operation
	 * @param opName
	 * @param wsdlsName
	 * @return
	 */
//	@SuppressWarnings("unchecked")
	public String getOpPrecondition (String opName, String wsdlsName){
		Element opElem = super.getOpElemByName(opName, wsdlsName);
		if (opElem == null){
			return null;
		}
		List<Element> docElemList = opElem.getChildren("documentation", wsdlNS);
		if(docElemList == null || docElemList.isEmpty()) {
			return null;
		}
		else{
			for(Element docElem:docElemList){
//			System.out.println(docElem.getChildren());
		Element preconditionElem = docElem.getChild("precondition", wssemNS);
		if(preconditionElem != null) {
					return preconditionElem.getAttributeValue("expression", wssemNS);

		}	
			}
			}
		return null;
		}
	
	/**given operation name and wsdl-s file name, return effect of the operation
	 * @param opName
	 * @param wsdlsName
	 * @return
	 */
	public String getOpEffect (String opName, String wsdlsName){
		Element opElem = super.getOpElemByName(opName, wsdlsName);
		if (opElem == null){
			return null;
		}
		List<Element> docElemList = opElem.getChildren("documentation", wsdlNS);
		if(docElemList == null || docElemList.isEmpty()) {
			return null;
		}
		else{
			for(Element docElem:docElemList){
		
		Element effectElem = docElem.getChild("effect", wssemNS);
		if(effectElem != null) {
					return effectElem.getAttributeValue("expression", wssemNS);

		}

	}}
		return null;
	}
	/**
	 * 
	 */
	public WsdlSparser() {
		super();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WsdlSparser test = new WsdlSparser();
		String pre = test.getOpPrecondition("getIds", "wsdl/8/WSWUBlast.wsdl");
		System.out.println(pre);

	}

}
