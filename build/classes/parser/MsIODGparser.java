
package parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.ElementFilter;

import util.IODG;
import util.WebServiceOpr;

/**
 * @author Rui
 * 
 */
public class MsIODGparser {
	private static Namespace xsdNS = Namespace.getNamespace("xsd","http://www.w3.org/2001/XMLSchema");
	private static Namespace wsdlNS  = Namespace.getNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");	
	
	/**given message element and op name and wsdl name, return the post opder nodes of visiting message 
	 * the message node will be the last node of the result list
	 * @param messageElem
	 * @param opWsdl
	 * @return
	 */
	public List<IODG> getMsPostorderNodeList (Element messageElem, WebServiceOpr opWsdl){
		if (messageElem == null || opWsdl==null){
			return null;
		}
		
		return this.getPostorderNodeList(this.messageIODG(messageElem, opWsdl));
	}
	
	/**postorder traverse a tree with given root node
	 * use dfs algorithm just in case it not a tree(more than one father, or even cycle).
	 * the root will be the last node of the result list
	 * DfsVisit indicate noVisitRecord=-1, vistedNoRecord = -2, visitRecored = -3
	 * @param rootIODG
	 * @return
	 */
	private List<IODG> getPostorderNodeList (IODG rootIODG){
		List<IODG> postOrderNodeList = new ArrayList<IODG>();
		if (rootIODG != null && rootIODG.getDfsVisit() == -1){
			rootIODG.setDfsVisit(-2);
			List<IODG> children = rootIODG.getChildren();
			if (children!=null && !children.isEmpty()){
				for(IODG child:children ){
					if (child.getDfsVisit()==-1){
						List<IODG> subTreeNodes = getPostorderNodeList(child);
						postOrderNodeList.addAll(subTreeNodes);
					}
				}
			}
			rootIODG.setDfsVisit(-3);
			postOrderNodeList.add(rootIODG);
		} else return null;
		
		return postOrderNodeList;
	}
	
	
	/**a parser to generate a list of directed rooted graph (mostly tree) for a given complexType
	 * if the complexType has no subelement, return null.
	 * case 1: has descendent <element>
	 * case 2: array attribute wsdl:arrayType= has element (wublast service)<xsd:attribute ref="soapenc:arrayType" wsdl:arrayType="tns:data[]"/>
	 * case 3: empty sequence, no <element> at all-------return null
	 * @param complextypeElem
	 * @param opWsdl
	 * @return
	 */
	private List<IODG> complextypeIODG (Element complextypeElem, WebServiceOpr opWsdl){
		if (!complextypeElem.getName().equalsIgnoreCase("complexType")) {
			System.out.println("complextypeIODG warning: given element is not a complexType, return null");
			return null;
		}
		
		
		Element rootElem = complextypeElem.getDocument().getRootElement();		
		//in case schema prefix is s, xs or xsd
		if (rootElem.getNamespace("xsd") != null) {
			xsdNS = rootElem.getNamespace("xsd");
		} else if (rootElem.getNamespace("xs") != null) {
			xsdNS = rootElem.getNamespace("xs");
		 
	} else if (rootElem.getNamespace("s") != null) {
		xsdNS = rootElem.getNamespace("s");
	}else
			System.out
					.println("Warning: the prefix of this schema is not xsd or xs or s, parser may fail!");
		
		List<IODG> eleIODGlist = null;
		if (complextypeElem.getDescendants(new ElementFilter("element", xsdNS)).hasNext()){
			// case 1: has decendent <element>
			Iterator<Element> eleIt = complextypeElem.getDescendants(new ElementFilter("element", xsdNS));
			while(eleIt.hasNext()){
				IODG eleIODG = this.elementIODG(eleIt.next(),opWsdl);
				eleIODGlist = new ArrayList<IODG>();
				eleIODGlist.add(eleIODG);	
			}
			return eleIODGlist;
		}else if(complextypeElem.getDescendants(new ElementFilter("attribute", xsdNS)).hasNext()){
			// case 2: array attribute wsdl:arrayType= has element (wublast
			// service)<xsd:attribute ref="soapenc:arrayType" wsdl:arrayType="tns:data[]"/>
			Iterator<Element> attIt = complextypeElem.getDescendants(new ElementFilter("attribute", xsdNS));
			while(attIt.hasNext()){
				Element attElem = attIt.next();
				String arrayValue = null;
			//System.out.println(wsdlNS +"here--------------------------------------------------------------------------------");
				if (attElem.getAttributeValue("arrayType", wsdlNS)!= null){
					 arrayValue = attElem.getAttributeValue("arrayType", wsdlNS);

				}
				if (arrayValue != null && arrayValue.contains(":")){
					//array is complextype array
					if (!arrayValue.split(":")[0].equalsIgnoreCase(xsdNS.getPrefix())){
						SchemaParser sp = new SchemaParser();
						Element arrayComplextypeElem = sp.getComplextypeOfRoot(arrayValue.split(":")[0], arrayValue.split(":")[1], rootElem);
						if(arrayComplextypeElem!=null){
							List<IODG> complextypeIODG = this.complextypeIODG(arrayComplextypeElem, opWsdl);
							if (complextypeIODG!=null || !complextypeIODG.isEmpty()){
								eleIODGlist = new ArrayList<IODG>();
								eleIODGlist.addAll(complextypeIODG);								
								return eleIODGlist;
							}
						}
					}
						
					}
				}
			}
		//case 3, return null
		return eleIODGlist;
	}

	/**a parser to generate a directed rooted graph (mostly tree) for a given element
	 * @param elementElem
	 * @param opWsdl
	 * @return
	 */
	private IODG elementIODG (Element elementElem, WebServiceOpr opWsdl){
		if (!elementElem.getName().equalsIgnoreCase("element")) {
			System.out.println("elementIODG warning: given element is not a element, return null");
			return null;
		}
		
		IODG elementNode = new IODG(elementElem, opWsdl);
		
		Element rootElem = elementElem.getDocument().getRootElement();		
		//in case schema prefix is s, xs or xsd
		if (rootElem.getNamespace("xsd") != null) {
			xsdNS = rootElem.getNamespace("xsd");
		} else if (rootElem.getNamespace("xs") != null) {
			xsdNS = rootElem.getNamespace("xs");
		 
	} else if (rootElem.getNamespace("s") != null) {
		xsdNS = rootElem.getNamespace("s");
	}else
			System.out
					.println("Warning: the prefix of this schema is not xsd or xs or s, parser may fail!");
		
		if (elementElem.getChild("complexType", xsdNS) != null) {
			Element complextypeElem = elementElem.getChild("complexType", xsdNS);
			List<IODG> eleIODGlist = this.complextypeIODG(complextypeElem, opWsdl);
			if (eleIODGlist != null && !eleIODGlist.isEmpty()){
				elementNode.setChildren(eleIODGlist);
				List<Integer> levelList = new ArrayList<Integer>();
				for (IODG child: eleIODGlist){
					levelList.add(child.getLevel());
				}
				elementNode.setLevel(1+Collections.max(levelList));
			}else{//complextype has no subelement, this element is a leaf
				elementNode.setLevel(1);
			}
			
		} else if (elementElem.getAttribute("type") != null) {// type =*:*
			// complexType --------tree
			// or simpleType, ------leaf
			// or type=xsd:*-----leaf
			String typeValue = elementElem.getAttributeValue("type");
			String typePre = "";
			String typeName = typeValue;
			if (typeValue.contains(":")){
				typePre = typeValue.split(":")[0];
				typeName = typeValue.split(":")[1];
			} 
		
			if (typePre.equalsIgnoreCase(xsdNS.getPrefix())){//element has xsd type, is a leaf
				elementNode.setLevel(1);
				return elementNode;
			}
			else {//typepre is "" or not xsd, check if is complex type
				SchemaParser sp = new SchemaParser();
				Element complextypeElem = sp.getComplextypeOfRoot(typePre, typeName, rootElem);
				if(complextypeElem==null){//no complextype, maybe simpletype, part is a leaf
					elementNode.setLevel(1);
					return elementNode;
				}else{
					List<IODG> complextypeIODG = this.complextypeIODG(complextypeElem, opWsdl);
					if (complextypeIODG==null ||complextypeIODG.isEmpty()){
						elementNode.setLevel(1);
						return elementNode;
					}else{
						elementNode.setChildren(complextypeIODG);
						List<Integer> levelList = new ArrayList<Integer>();
						for (IODG child: complextypeIODG){
							levelList.add(child.getLevel());
						}
						elementNode.setLevel(1+Collections.max(levelList));
						return elementNode;
					}
				}
			}
		}
		else{//this element is a leaf: no complextype child and no type attribute
			elementNode.setLevel(1);
		}
		
		return elementNode;
	}
	
	/**a parser to generate a directed rooted graph (mostly tree) for a given part
	 * @param partElem
	 * @param opWsdl
	 * @return
	 */
	private IODG partIODG (Element partElem, WebServiceOpr opWsdl){
		if (!partElem.getName().equalsIgnoreCase("part")) {
			System.out.println("partIODG warning: given element is not a part, return null");
			return null;
		}
		
		Element rootElem = partElem.getDocument().getRootElement();		
		//in case schema prefix is s, xs or xsd
		if (rootElem.getNamespace("xsd") != null) {
			xsdNS = rootElem.getNamespace("xsd");
		} else if (rootElem.getNamespace("xs") != null) {
			xsdNS = rootElem.getNamespace("xs");
		 
	} else if (rootElem.getNamespace("s") != null) {
		xsdNS = rootElem.getNamespace("s");
	}else
			System.out
					.println("Warning: the prefix of this schema is not xsd or xs or s, parser may fail!");
	
		IODG partNode = new IODG(partElem,opWsdl);
		SchemaParser sp = new SchemaParser();
        //part has type (rpc/encoded)
		if (partElem.getAttribute("type") != null){
			String typeValue = partElem.getAttributeValue("type");
			String typePre = "";
			String typeName = typeValue;
			if (typeValue.contains(":")){
				typePre = typeValue.split(":")[0];
				typeName = typeValue.split(":")[1];
			} 
		
			if (typePre.equalsIgnoreCase(xsdNS.getPrefix())){//part has xsd type, is a leaf
				partNode.setLevel(1);
				return partNode;
			}
			else {//typepre is "" or not xsd, check if is complex type
				Element complextypeElem = sp.getComplextypeOfRoot(typePre, typeName, rootElem);
				if(complextypeElem==null){//no complextype, maybe simpletype, part is a leaf
					partNode.setLevel(1);
					return partNode;
				}else{
					List<IODG> complextypeIODG = this.complextypeIODG(complextypeElem, opWsdl);
					if (complextypeIODG==null || complextypeIODG.isEmpty()){
						partNode.setLevel(1);
						return partNode;
					}else{
						partNode.setChildren(complextypeIODG);
						List<Integer> levelList = new ArrayList<Integer>();
						for (IODG child: complextypeIODG){
							levelList.add(child.getLevel());
						}
						partNode.setLevel(1+Collections.max(levelList));
						return partNode;
					}
				}
			}
			
		}
		//part has element (doc/literal)
		else if (partElem.getAttribute("element") != null){
			String elementValue = partElem.getAttributeValue("element");
			Element elementElem = sp.getElementElemOfRoot(elementValue, rootElem);
			if(elementElem==null){
				partNode.setLevel(1);
				System.out.println("partIODG warning: part refered to an element not found, return as leaf");
				return partNode;
			}else{
				IODG elementIODG = this.elementIODG(elementElem, opWsdl);
				if (elementIODG != null){
				List<IODG> children = new ArrayList<IODG>();
				children.add(elementIODG);
				partNode.setChildren(children);
				partNode.setLevel(1+elementIODG.getLevel());
				return partNode;
				}else{
					partNode.setLevel(1);
					System.out.println("partIODG warning: part refered to an element unable to drive a tree, return as leaf");
					return partNode;
				}
				
			}
		}
		
		//no type, no element, this part is leaf
		return partNode;
	}
	
	/**a parser to generate a directed rooted graph (mostly tree) for a given message 
	 * @param messageElem
	 * @param opWsdl
	 * @return root node of IODG type for the tree
	 */
	public IODG messageIODG (Element messageElem, WebServiceOpr opWsdl){
		if (!messageElem.getName().equalsIgnoreCase("message")) {
			System.out.println("messageIODG warning: given element is not a message, return null");
			return null;
		}
			
		IODG messageNode = new IODG(messageElem, opWsdl);
		if (messageElem.getChildren("part", wsdlNS) == null ||messageElem.getChildren("part", wsdlNS).isEmpty()){
			messageNode.setLevel(1);
			return messageNode;
		}
		else{
			List<Element> parts = messageElem.getChildren("part", wsdlNS);
			List<IODG> partIODGlist = new ArrayList<IODG>();
			List<Integer> levelList = new ArrayList<Integer>();
			for (Element part : parts){
//				System.out.println(part);
//				System.out.println(part.getAttributeValue("name"));
				IODG partNode=this.partIODG(part, opWsdl);
				partIODGlist.add(partNode);
				levelList.add(partNode.getLevel());
//				System.out.println(partNode.getLevel());
			}			
			messageNode.setChildren(partIODGlist);
			messageNode.setLevel(1+Collections.max(levelList));
		}
		 
		return messageNode;
	}
	
	/**
	 * constructor
	 */
	public MsIODGparser() {

	}

	/**for test
	 * @param args
	 */
	public static void main(String[] args) {
            


	}

}
