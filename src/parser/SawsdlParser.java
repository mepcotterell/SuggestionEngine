
package parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

/**
 * @author Rui Wang
 * Spring 2010
 */
public class SawsdlParser {

    protected static Namespace xsdNS = Namespace.getNamespace("xsd", "http://www.w3.org/2001/XMLSchema");
    protected static Namespace wsdlNS = Namespace.getNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");
    protected static Namespace sawsdlNS = Namespace.getNamespace("sawsdl", "http://www.w3.org/ns/sawsdl");

    /**
     * given wsdl file name, retrive whole schema Element
     * @param fileName
     * @return
     */
    public List<Element> getSchemaElemList(String fileName) {
        List<Element> schemaElemList = new ArrayList();

        //get root element of xml file
        Element root = getRootElem(fileName);
        schemaElemList = getSchemaElemList(root);

        return schemaElemList;
    }

    /**
     * given wsdl root element, retrive whole schema Element
     * @param rootElem
     * @return
     */
    public List<Element> getSchemaElemList(Element rootElem) {
        //to do: handle import xsd

        List<Element> schemaElemList = new ArrayList();

        schemaElemList = rootElem.getChild("types", wsdlNS).getChildren("schema", xsdNS);

        String preXsdNs = schemaElemList.get(0).getNamespacePrefix();
        if (preXsdNs != "") {
            xsdNS = Namespace.getNamespace(preXsdNs, "http://www.w3.org/2001/XMLSchema");
        }

        return schemaElemList;
    }

    /**
     * given sawsdl/wsdl file name and operation name, return output message element
     * @param fileName
     * @param opName
     * @return
     */
    public Element getOutMsElem(String fileName, String opName) {
        Element opElem = this.getOpNameElemMap(fileName).get(opName);
        if (opElem == null) {
            return null;
        }
        Element outElem = opElem.getChild("output", wsdlNS);
        String outMsName = outElem.getAttributeValue("message");
        if (outMsName.contains(":")) {
            outMsName = outMsName.split(":")[1];
        }
        List<Element> msElemList = opElem.getDocument().getRootElement().getChildren("message", wsdlNS);
        Element outMsElem = null;
        for (Element msElem : msElemList) {
            if (msElem.getAttributeValue("name").equals(outMsName)) {
                outMsElem = msElem;
                break;
            }
        }
        return outMsElem;
    }
    
    /**
     * given sawsdl/wsdl file name and operation name, return input message element
     * @param fileName
     * @param opName
     * @return
     */
    public Element getInMsElem(String fileName, String opName) {
        Element opElem = this.getOpNameElemMap(fileName).get(opName);
        Element inElem ;
        
    
        try{
        
        if (opElem.getChild("input", wsdlNS) != null)
            inElem = opElem.getChild("input", wsdlNS);
        else
            return null;
        }
        catch (Exception e)
        {
            inElem = null;
        
        }
        
        String inMsName = inElem.getAttributeValue("message");
        if (inMsName.contains(":")) {
            inMsName = inMsName.split(":")[1];
        }
        List<Element> msElemList = opElem.getDocument().getRootElement().getChildren("message", wsdlNS);
        Element inMsElem = null;
        for (Element msElem : msElemList) {
            if (msElem.getAttributeValue("name").equals(inMsName)) {
                inMsElem = msElem;
                break;
            }
        }
        return inMsElem;
    }

    /**
     * given sawsdl/wsdl file name, return all the operations in a Map<opName, opElem>
     * @param fileName
     * @return
     */
    public Map<String, Element> getOpNameElemMap(String fileName) {
        Element root = this.getRootElem(fileName);
        //System.out.println(fileName);
        List<Element> opElemList = root.getChild("portType", wsdlNS).getChildren("operation", wsdlNS);
        Map<String, Element> opNameElemMap = new HashMap<String, Element>();
        for (Element opElem : opElemList) {
            opNameElemMap.put(opElem.getAttributeValue("name"), opElem);
        }
        return opNameElemMap;
    }

    /**
     * given operation name and wsdl file full path, return the operation element
     * @param opName
     * @param fileName
     * @return
     */
    public Element getOpElemByName(String opName, String fileName) {
        if (opName == null || fileName == null) {
            return null;
        } else {
            return this.getOpNameElemMap(fileName).get(opName);
        }
    }

    /**given operation name and sawsdl file full path, return the value of modelReference
     * @param opName
     * @param fileName
     * @return
     */
    public String getOpModelreference(String opName, String fileName) {
        Element opElem = this.getOpElemByName(opName, fileName);
        if (opElem == null) {
            return null;
        } else {
            Element attrExtensionsElem = opElem.getChild("attrExtensions", sawsdlNS);
            if (attrExtensionsElem == null) {
                return null;
            } else {
                return attrExtensionsElem.getAttributeValue("modelReference", sawsdlNS);
            }
        }
    }

    /**
     * given sawsdl/wsdl file name, return root element
     * @param fileName
     * @return
     */
    public Element getRootElem(String fileName) {
//        String filePath = Thread.currentThread().getContextClassLoader().getResource(fileName).toString();
//      System.out.println("fileUrl: " + fileUrl);

        // cache the files
        String filePath = util.SimpleCache.getInstance().get(fileName).getAbsolutePath();

        
        SAXBuilder sbuilder = new SAXBuilder();
        Element root = null;

        try {
//			Document doc = sbuilder.build(fileUrl);
//			Document doc = sbuilder.build(fileName);
            Document doc = sbuilder.build(filePath);
//			get root element of xml file
            root = doc.getRootElement();

        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (root.getNamespace("xsd") != null) {
            xsdNS = root.getNamespace("xsd");
        } else if (root.getNamespace("xs") != null) {
            xsdNS = root.getNamespace("xs");

        } else if (root.getNamespace("s") != null) {
            xsdNS = root.getNamespace("s");
        } else {
            System.out.println("Warning: the prefix of this schema is not xsd or xs or s parser may fail!");
        }
        return root;
    }

    /**
     * 
     */
    public SawsdlParser() {
    }

    /**
     * for test
     * @param args
     */
    public static void main(String[] args) {
        SawsdlParser testSP = new SawsdlParser();
//		Element rootElem = testSP.getRootElem("wsdl/picr.wsdl");
//		Element rootElem = testSP.getRootElem("wsdl/CompanyInfo.wsdl");
//		Element schema = testSP.getSchemaElemList(rootElem).get(0);
//		String schemaPrefix = schema.getNamespacePrefix();
//		System.out.println(schema.getName());
        //System.out.println(testSP.getRootElem("wsdl/picr.wsdl").getName());
//		System.out.println(testSP.getOpNameElemMap("wsdl/picr.wsdl").keySet());
        System.out.println(testSP.getInMsElem("wsdl/picr.wsdl", "getMappedDatabaseNames").getQualifiedName());
//		System.out.println(testSP.getOutMsElem("wsdl/picr.wsdl", "getMappedDatabaseNames").getQualifiedName());
//		System.out.println(testSP.xsdNS);
//		String elemName = "impl:getUPIForAccession";
//		try {					
//			if (rootElem.getNamespace(elemName.split(":")[0]).getURI().equals(schema.getAttributeValue("targetNamespace"))){
////				String schemaPrefix = schema.getNamespacePrefix();
////				System.out.println("start");
//
//				if (schemaPrefix==""){
////					System.out.println(schema.getChildren());
////					System.out.println(XPath.selectSingleNode(schema,"//xsd:element"));
//					Element tempElem=(Element)(XPath.selectSingleNode(schema,"//xsd:schema/descendant::xsd:element[@name=\""+elemName.split(":")[1]+"\"]"));
//					System.out.println(tempElem.getAttributeValue("name"));
//				}
//				else {
//					Element tempElem=(Element)(XPath.selectSingleNode(schema,"//"+schemaPrefix+":schema/descendant::"+schemaPrefix+":element[@name=\""+elemName.split(":")[1]+"\"]"));
//					System.out.println("wrong");
//				}
////				break;
//			}
//			else {
//				System.out.println(rootElem.getNamespace(elemName.split(":")[0]).getURI());
//			System.out.println(schema.getAttributeValue("targetNamespace"));
//			System.out.println(rootElem.getNamespace(elemName.split(":")[0]).getURI().toString().equals(schema.getAttributeValue("targetNamespace").toString()));
//			
//			}
//		} catch (JDOMException e) {
//			
//			e.printStackTrace();
//		}

    }
}
