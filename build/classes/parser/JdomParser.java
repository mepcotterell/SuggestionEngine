/**
 * 
 */
package parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.*;
//import org.jdom.Element;
import org.jdom.filter.ElementFilter;
//import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

/**
 * 
 *
 */
public class JdomParser {
	
	private static Namespace xsdNS = Namespace.getNamespace("http://www.w3.org/2001/XMLSchema");
	private static Namespace wsdlNS  = Namespace.getNamespace("http://schemas.xmlsoap.org/wsdl/");
	public static Namespace sawsdlNS  = Namespace.getNamespace("http://www.w3.org/ns/sawsdl");
	
	
	/**
	 * given wsdl file name, retrive whole schema Element
	 * @param fileName
	 * @return
	 */
	public static Element getSchemaElem(String fileName){
//		String fileUrl=ClassLoader.getSystemResource(fileName).toString();
//		String fileUrl=fileName;
		 String fileUrl =Thread.currentThread().getContextClassLoader().getResource(fileName).toString();
                
SAXBuilder sbuilder = new SAXBuilder();
Element schemaEle=null;
		
		try{
			Document doc = sbuilder.build(fileUrl);
			
			//get root element of xml file
			Element root = doc.getRootElement(); 	
			
			//defind namespace for requested elements
			//Namespace xsdNS = root.getNamespace("xsd");
			xsdNS = Namespace.getNamespace("http://www.w3.org/2001/XMLSchema");
			wsdlNS = root.getNamespace("wsdl");
			sawsdlNS = root.getNamespace("sawsdl");
			
			schemaEle=root.getChild("types", wsdlNS).getChild("schema", xsdNS);
			String preXsdNs=schemaEle.getNamespacePrefix();
			xsdNS = Namespace.getNamespace(preXsdNs,"http://www.w3.org/2001/XMLSchema");
			//System.out.println("prefix--"+preXsdNs);
		}catch(JDOMException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
		
		return schemaEle;
	}
	
	/**
	 * given element name for xmlschema if it's complex type, then return a list of all sub element name
	 * @param fileName
	 * @param eleName
	 * @return
	 */
	public static List<String> getSubelemList(String fileName, String eleName){
		Element schemaEle=getSchemaElem(fileName);
		String preXsdNs=schemaEle.getNamespacePrefix();
		List<String> subEleNameList=new ArrayList<String>();
		List<String> subEleXpathList=new ArrayList<String>();
		try{
			//XPath xpath=XPath.newInstance("/");
			//XPath xpath=XPath.newInstance("//xsd:schema/descendant::xsd:element[@name=\"symbol\"]");
			//Element elem=(Element)(XPath.selectSingleNode(schemaEle,"//"+preXsdNs+":schema/descendant::"+preXsdNs+":element[@name=\""+eleName+"\"]"));
			//System.out.println(elem.getAttributeValue("type"));
			//System.out.println(elem.getChild("complexType", xsdNS));
			
			subEleNameList.add(eleName);
			subEleXpathList.add("");
			int subEleLiSize=subEleNameList.size();
			int j=0;
			while(j<subEleLiSize){
				String elemName=subEleNameList.get(j);
				Element elem=(Element)(XPath.selectSingleNode(schemaEle,"//"+preXsdNs+":schema/descendant::"+preXsdNs+":element[@name=\""+elemName+"\"]"));
				
			//complexType is child of element
			if (elem.getChild("complexType", xsdNS)!=null){
			
			List<Element> subEleList=XPath.selectNodes(elem,"//"+preXsdNs+":element[@name=\""+elemName+"\"]/descendant::"+preXsdNs+":element");
			//System.out.println(subEleList);
			for(int i=0;i<subEleList.size();i++){
				Element ele=(Element)subEleList.get(i);
				//System.out.println(ele.getAttributeValue("name"));
				subEleNameList.add(ele.getAttributeValue("name"));
				subEleXpathList.add(subEleXpathList.get(j)+"/"+ele.getAttributeValue("name"));
			}			
			subEleNameList.remove(j);
			subEleXpathList.remove(j);
			subEleLiSize=subEleNameList.size();
			continue;
			
			}//complexType is outside of element
			else if(elem.getAttributeValue("type")!=null){
				String typeName=elem.getAttributeValue("type");
				if (typeName.split(":")[0].equalsIgnoreCase(elem.getNamespacePrefix())){
					j=j+1;
					continue;
				}
				typeName=typeName.split(":")[1];
				Element typeElem=(Element)(XPath.selectSingleNode(schemaEle,"//"+preXsdNs+":schema/descendant::"+preXsdNs+":complexType[@name=\""+typeName+"\"]"));
				//System.out.println("type="+typeName);
				//if found standalone complexType for the given element
				if(typeElem!=null){					
					List<Element> subEleList=XPath.selectNodes(elem,"//"+preXsdNs+":complexType[@name=\""+typeName+"\"]/descendant::"+preXsdNs+":element");
					//System.out.println(subEleList);
					if(subEleList==null){
						j=j+1;
						continue;
					}
					for(int i=0;i<subEleList.size();i++){
						Element ele=(Element)subEleList.get(i);
						//System.out.println(ele.getAttributeValue("name"));
						subEleNameList.add(ele.getAttributeValue("name"));
						subEleXpathList.add(subEleXpathList.get(j)+"/"+ele.getAttributeValue("name"));
					}
					
					subEleNameList.remove(j);
					subEleXpathList.remove(j);
					subEleLiSize=subEleNameList.size();
					continue;
				}
				
			}
			
			j=j+1;
			}
			}catch(JDOMException e){
			e.printStackTrace();
		}
			
			//System.out.println(subEleXpathList);
			//System.out.println(subEleNameList.size());
		return subEleNameList;
	}
	
	/**
	 * get the subelement and xpath map of given element in the given wsdl-schema file
	 * @param fileName
	 * @param eleName
	 * @return
	 */
	public static Map<String, String> getSubelemXpathMap(String fileName, String eleName){
		Element schemaEle=getSchemaElem(fileName);
		String preXsdNs=schemaEle.getNamespacePrefix();
		List<String> subEleNameList=new ArrayList<String>();
		List<String> subEleXpathList=new ArrayList<String>();
		try{			
			subEleNameList.add(eleName);
			subEleXpathList.add("");
			int subEleLiSize=subEleNameList.size();
			int j=0;
			while(j<subEleLiSize){
				String elemName=subEleNameList.get(j);
				Element elem=(Element)(XPath.selectSingleNode(schemaEle,"//"+preXsdNs+":schema/descendant::"+preXsdNs+":element[@name=\""+elemName+"\"]"));
				
			//complexType is child of element
			if (elem.getChild("complexType", xsdNS)!=null){
			
			List<Element> subEleList=XPath.selectNodes(elem,"//"+preXsdNs+":element[@name=\""+elemName+"\"]/descendant::"+preXsdNs+":element");
			//System.out.println(subEleList);
			for(int i=0;i<subEleList.size();i++){
				Element ele=(Element)subEleList.get(i);
				//System.out.println(ele.getAttributeValue("name"));
				subEleNameList.add(ele.getAttributeValue("name"));
				subEleXpathList.add(subEleXpathList.get(j)+"/"+ele.getAttributeValue("name"));
			}			
			subEleNameList.remove(j);
			subEleXpathList.remove(j);
			subEleLiSize=subEleNameList.size();
			continue;
			
			}//complexType is outside of element
			else if(elem.getAttributeValue("type")!=null){
				String typeName=elem.getAttributeValue("type");
				if (typeName.split(":")[0].equalsIgnoreCase(elem.getNamespacePrefix())){
					j=j+1;
					continue;
				}
				typeName=typeName.split(":")[1];
				Element typeElem=(Element)(XPath.selectSingleNode(schemaEle,"//"+preXsdNs+":schema/descendant::"+preXsdNs+":complexType[@name=\""+typeName+"\"]"));
				//System.out.println("type="+typeName);
				//if found standalone complexType for the given element
				if(typeElem!=null){					
					List<Element> subEleList=XPath.selectNodes(elem,"//"+preXsdNs+":complexType[@name=\""+typeName+"\"]/descendant::"+preXsdNs+":element");
					//System.out.println(subEleList);
					if(subEleList==null){
						j=j+1;
						continue;
					}
					for(int i=0;i<subEleList.size();i++){
						Element ele=(Element)subEleList.get(i);
						//System.out.println(ele.getAttributeValue("name"));
						subEleNameList.add(ele.getAttributeValue("name"));
						subEleXpathList.add(subEleXpathList.get(j)+"/"+ele.getAttributeValue("name"));
					}
					
					subEleNameList.remove(j);
					subEleXpathList.remove(j);
					subEleLiSize=subEleNameList.size();
					continue;
				}
				
			}
			
			j=j+1;
			}
			}catch(JDOMException e){
			e.printStackTrace();
		}
			
			//System.out.println(subEleXpathList);
			//System.out.println(subEleNameList.size());
			Map<String, String> eleXpathMap=new HashMap<String, String>();
			for (int i=0;i<subEleNameList.size();i++){
				eleXpathMap.put(subEleNameList.get(i), subEleXpathList.get(i));
			}
		return eleXpathMap;
	}
	
	/**
	 * get the subelement-NODE and xpath map of given element in the given wsdl-schema file
	 * 
	 * @param fileName
	 * @param eleName  given element name should be unique in the schema, if not, program will handle the first element with the given name
	 * @return
	 */
	public static Map<Element, String> getSubelemNodeXpathMap(String fileName, String eleName){
		Element schemaEle=getSchemaElem(fileName);
		String preXsdNs=schemaEle.getNamespacePrefix();
		List<Element> subEleNodeList=new ArrayList<Element>();
		List<String> subEleXpathList=new ArrayList<String>();
		try{
			Element elem=(Element)(XPath.selectSingleNode(schemaEle,"//"+preXsdNs+":schema/descendant::"+preXsdNs+":element[@name=\""+eleName+"\"]"));
			
			subEleNodeList.add(elem);
			subEleXpathList.add("");
			int subEleLiSize=subEleNodeList.size();
			int j=0;
			while(j<subEleLiSize){				
				Element elemNode=subEleNodeList.get(j);
				//System.out.println(elemNode.getAttributeValue("name"));
			//complexType is child of element
			if (elemNode.getChild("complexType", xsdNS)!=null){
				Iterator<Element> nodeIt=elemNode.getChild("complexType", xsdNS).getDescendants(new ElementFilter("element"));
				List<Element> subEleList=new ArrayList<Element>();
				while(nodeIt.hasNext()){
					subEleList.add(nodeIt.next());
				}
			subEleNodeList.addAll(subEleList);
			for(int i=0;i<subEleList.size();i++){
				Element ele=(Element)subEleList.get(i);
				subEleXpathList.add(subEleXpathList.get(j)+"/"+ele.getAttributeValue("name"));
			}			
			subEleNodeList.remove(j);
			subEleXpathList.remove(j);
			subEleLiSize=subEleNodeList.size();
			continue;
			
			}//complexType is outside of element
			else if(elemNode.getAttributeValue("type")!=null){
				String typeName=elemNode.getAttributeValue("type");
				//System.out.println("type="+typeName);
				if (typeName.split(":")[0].equalsIgnoreCase(preXsdNs)){
					j=j+1;
					continue;
				}
				typeName=typeName.split(":")[1];
				Element typeElem=(Element)(XPath.selectSingleNode(schemaEle,"//"+preXsdNs+":schema/descendant::"+preXsdNs+":complexType[@name=\""+typeName+"\"]"));
				
				//if found standalone complexType for the given element
				if(typeElem!=null){					
					List<Element> subEleList=XPath.selectNodes(schemaEle,"//"+preXsdNs+":complexType[@name=\""+typeName+"\"]/descendant::"+preXsdNs+":element");
					//System.out.println(subEleList);
					if(subEleList==null){
						j=j+1;
						continue;
					}
					subEleNodeList.addAll(subEleList);
					for(int i=0;i<subEleList.size();i++){
						Element ele=(Element)subEleList.get(i);
						//System.out.println(ele.getAttributeValue("name"));
						//subEleNameList.add(ele.getAttributeValue("name"));
						subEleXpathList.add(subEleXpathList.get(j)+"/"+ele.getAttributeValue("name"));
					}
					
					//subEleNameList.remove(j);
					subEleXpathList.remove(j);
					//subEleLiSize=subEleNameList.size();
					subEleNodeList.remove(j);					
					subEleLiSize=subEleNodeList.size();
					continue;
				}
				
			}
			
			j=j+1;
			}
			}catch(JDOMException e){
			e.printStackTrace();
		}		
			
			Map<Element, String> eleXpathMap=new HashMap<Element, String>();
			for (int i=0;i<subEleNodeList.size();i++){
				eleXpathMap.put(subEleNodeList.get(i), subEleXpathList.get(i));
			}
		return eleXpathMap;
	}
	
	/**
	 * get the subelement-NODE list of given element in the given wsdl-schema file
	 * 
	 * @param fileName
	 * @param eleName eleName  given element name should be unique in the schema, if not, program will handle the first element with the given name
	 * @return
	 */
	public static List<Element> getSubelemNodeList(String fileName, String eleName){
		Element schemaEle=getSchemaElem(fileName);
		String preXsdNs=schemaEle.getNamespacePrefix();
		List<Element> subEleNodeList=new ArrayList<Element>();
		
		try{			
			Element elem=(Element)(XPath.selectSingleNode(schemaEle,"//"+preXsdNs+":schema/descendant::"+preXsdNs+":element[@name=\""+eleName+"\"]"));
			
			subEleNodeList.add(elem);
		
			int subEleLiSize=subEleNodeList.size();
			int j=0;
			while(j<subEleLiSize){
				//String elemName=subEleNameList.get(j);
				Element elemNode=subEleNodeList.get(j);
			//complexType is child of element
			if (elemNode.getChild("complexType", xsdNS)!=null){
				Iterator<Element> nodeIt=elemNode.getChild("complexType", xsdNS).getDescendants(new ElementFilter("element"));
				List<Element> subEleList=new ArrayList<Element>();
				while(nodeIt.hasNext()){
					subEleList.add(nodeIt.next());
				}
			
			subEleNodeList.addAll(subEleList);
						
			subEleNodeList.remove(j);
			subEleLiSize=subEleNodeList.size();
			continue;
			
			}//complexType is outside of element
			else if(elemNode.getAttributeValue("type")!=null){
				String typeName=elemNode.getAttributeValue("type");
				if (typeName.split(":")[0].equalsIgnoreCase(preXsdNs)){
					j=j+1;
					continue;
				}
				typeName=typeName.split(":")[1];
				Element typeElem=(Element)(XPath.selectSingleNode(schemaEle,"//"+preXsdNs+":schema/descendant::"+preXsdNs+":complexType[@name=\""+typeName+"\"]"));
				//System.out.println("type="+typeName);
				//if found standalone complexType for the given element
				if(typeElem!=null){					
					List<Element> subEleList=XPath.selectNodes(schemaEle,"//"+preXsdNs+":complexType[@name=\""+typeName+"\"]/descendant::"+preXsdNs+":element");
					//System.out.println(subEleList);
					if(subEleList==null){
						j=j+1;
						continue;
					}
					subEleNodeList.addAll(subEleList);
					subEleNodeList.remove(j);					
					subEleLiSize=subEleNodeList.size();
					continue;
				}
				
			}
			
			j=j+1;
			}
			}catch(JDOMException e){
			e.printStackTrace();
		}		
		return subEleNodeList;
	}
	
	/**
	 * get first node element of given xpath
	 * @param wsdlFile
	 * @param xpath
	 * @return
	 */
	public static org.jdom.Element getNode(String wsdlFile, String xpath){
            		 String fileUrl =Thread.currentThread().getContextClassLoader().getResource(wsdlFile).toString();

		SAXBuilder sbuilder = new SAXBuilder();
//		String prePlnkNs=null;
		org.jdom.Element root=null;
//		String nsPrefix ;
		try{
		//Document doc = sbuilder.build(new FileInputStream(fileName)); 
			org.jdom.Document doc = sbuilder.build(fileUrl);
			
			//get root element of xml file
			root = doc.getRootElement(); 
//			Namespace ns=root.getNamespace();
			//System.out.println(root.getChild("partnerLinks", ns));
//			 nsPrefix = root.getNamespacePrefix();
			// System.out.println(root.getNamespace());
//			List<Namespace> nss = root.getAdditionalNamespaces();
//			for(Namespace ns: nss){
//				System.out.println("ns uri is " + ns.getURI());
//				System.out.println("ns prefix is " + ns.getPrefix());
//			}
		}catch(JDOMException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
		//System.out.println("the root name is " + root.getName());
		//List attributes = root.getAttributes();
		org.jdom.Element node = null;
		//String nsPrefix = root.getNamespacePrefix();
		try {
			node=(org.jdom.Element)org.jdom.xpath.XPath.selectSingleNode(root,
 xpath);
			//node=(org.jdom.Element)org.jdom.xpath.XPath.selectSingleNode(root,
			//		 "/partnerLinks");
			//System.out.println(nsPrefix);
		} catch (JDOMException e) {
			
			e.printStackTrace();
		}
		
		return node;		
	}
	
	/**
	 * 
	 */
	public JdomParser() {
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//JdomParser test=new JdomParser();
		//System.out.println(JdomParser.getElementConcept("stockquote.wsdl", "symbol"));
		
		//System.out.println(JdomParser.getSubelemList("stockquote.wsdl", "string"));

		//String wsdlFile ="SynchronousSample.bpel";
	//	Element element = getNode(wsdlFile,
	//	 "/import");
		 //"/partnerLink");
		 //"[@name=\"SynchronousSample\"]");
		//System.out.println(element);
		
	}

	

}
