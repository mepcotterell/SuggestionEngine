package parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.filter.ElementFilter;
import org.jdom.xpath.XPath;

import parser.SawsdlParser;
import util.OpWsdl;

/**
 * @author Rui Wang
 * 
 */
public class DmParser {

    private static Namespace xsdNS = Namespace.getNamespace("xsd", "http://www.w3.org/2001/XMLSchema");
    private static Namespace wsdlNS = Namespace.getNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");
    private static Namespace sawsdlNS = Namespace.getNamespace("sawsdl", "http://www.w3.org/ns/sawsdl");
//	private static Namespace soapencNS = Namespace
//	.getNamespace("soapenc", "http://schemas.xmlsoap.org/soap/encoding/");

    /**
     * given an element, return a list of paths in his element
     * @param elem
     * @return
     */
    public List<List<Element>> getPathsList(Element elem) {
        List<List<Element>> pathsList = new ArrayList<List<Element>>();
        Element rootElem = elem.getDocument().getRootElement();
//		wsdlNS = rootElem.getNamespace("wsdl");
//		sawsdlNS = rootElem.getNamespace("sawsdl");
        if (rootElem.getNamespace("xsd") != null) {
            xsdNS = rootElem.getNamespace("xsd");
        } else if (rootElem.getNamespace("xs") != null) {
            xsdNS = rootElem.getNamespace("xs");

        } else if (rootElem.getNamespace("s") != null) {
            xsdNS = rootElem.getNamespace("s");
        } else {
            System.out.println("Warning: the prefix of this schema is not xsd or xs or s, parser may fail!");
        }
        // get all schemas
        SawsdlParser sp = new SawsdlParser();
        List<Element> schemaList = sp.getSchemaElemList(rootElem);
        String schemaPrefix = xsdNS.getPrefix();

        if (elem.getName().equalsIgnoreCase("message")) {
            List<Element> parts = elem.getChildren("part", wsdlNS);
            for (Element part : parts) {
                pathsList.addAll(getPathsList(part));
            }
            if (pathsList.isEmpty()) {
                List<Element> p = new ArrayList<Element>();
                p.add(elem);
                pathsList.add(p);
            } else {
                for (List<Element> path : pathsList) {
                    path.add(elem);
                }
            }
        } else if (elem.getName().equalsIgnoreCase("complexType")) {
            // case 1: has decendent <element>
            // case 2: array attribute wsdl:arrayType= has element (wublast
            // service)<xsd:attribute ref="soapenc:arrayType" wsdl:arrayType="tns:data[]"/>
            // case 3: empty sequence, no <element> at all-------do nothing, return empty pathsList []
            // to do case 4: extension/restriction base= another type
            // ----complexType(step in for elements), xsd type(--end recursive),
            // simpleType(ignore--end recursive, do nothing)
//			System.out.println(elem.getDescendants(new ElementFilter("attribute", xsdNS)).hasNext()+"---------------------------");
            if (elem.getDescendants(new ElementFilter("element", xsdNS)).hasNext()) {
                // case 1: has decendent <element>
                Iterator<Element> eleIt = elem.getDescendants(new ElementFilter("element", xsdNS));
                while (eleIt.hasNext()) {
                    pathsList.addAll(getPathsList(eleIt.next()));
                }
            } else if (elem.getDescendants(new ElementFilter("attribute", xsdNS)).hasNext()) {
                // case 2: array attribute wsdl:arrayType= has element (wublast
                // service)<xsd:attribute ref="soapenc:arrayType" wsdl:arrayType="tns:data[]"/>
                Iterator<Element> attIt = elem.getDescendants(new ElementFilter("attribute", xsdNS));
                while (attIt.hasNext()) {
                    Element attElem = attIt.next();
                    String arrayValue = null;
                    //System.out.println(wsdlNS +"here--------------------------------------------------------------------------------");
                    if (attElem.getAttributeValue("arrayType", wsdlNS) != null) {
                        arrayValue = attElem.getAttributeValue("arrayType", wsdlNS);

                    }
                    if (arrayValue != null && arrayValue.contains(":")) {

                        if (!arrayValue.split(":")[0].equalsIgnoreCase(xsdNS.getPrefix())) {
                            //array element is complextype

                            for (Element schema : schemaList) {
                                if (rootElem.getNamespace(arrayValue.split(":")[0]).getURI().equals(
                                        schema.getAttributeValue("targetNamespace"))) {
                                    if (schemaPrefix.equals("")) {

                                        try {
                                            Element tempElem = (Element) (XPath.selectSingleNode(
                                                    schema,
                                                    "//schema/descendant::complexType[@name=\""
                                                    + arrayValue.split(":")[1].replace("[]", "")
                                                    + "\"]"));
                                            if (tempElem != null) {
                                                pathsList.addAll(getPathsList(tempElem));

                                            }
                                        } catch (JDOMException e) {
                                            e.printStackTrace();
                                        }
                                    } else {

                                        try {
                                            Element tempElem = (Element) (XPath.selectSingleNode(
                                                    schema,
                                                    "//"
                                                    + schemaPrefix
                                                    + ":schema/descendant::"
                                                    + schemaPrefix
                                                    + ":complexType[@name=\""
                                                    + arrayValue.split(":")[1].replace("[]", "")
                                                    + "\"]"));
                                            if (tempElem != null) {
                                                pathsList.addAll(getPathsList(tempElem));

                                            }
                                        } catch (JDOMException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    break;
                                }

                            }

                        }
                    }
                }

            }

        } else if (elem.getName().equalsIgnoreCase("element")) {
            // case 1: has attribute type= ;
            // case 2: has child complexType(recursive complexType) or
            // simpleType (we ignore--end recursive here since simpleType does
            // not contain element)
            // to do: case 3: ref= another element
            // for case 1, type= xsd:* or another complexType or simpleType (we
            // ignore--end recursive here since simpleType does not contain
            // element)

            if (elem.getChild("complexType", xsdNS) != null) {
//				System.out.println(wsdlNS +"here--------------------------------------------------------------------------------");

                pathsList.addAll(getPathsList(elem.getChild("complexType",
                        xsdNS)));
                if (pathsList.isEmpty()) {
                    List<Element> p = new ArrayList<Element>();
                    p.add(elem);
                    pathsList.add(p);
                } else {
                    for (List<Element> path : pathsList) {
                        path.add(elem);
                    }
                }
            } else if (elem.getAttribute("type") != null) {// type =*:*
                // complexType or
                // simpleType, or
                // type=xsd:*
                String typeValue = elem.getAttributeValue("type");
                //// type=complexType/simpleType , name space is same as schema and root targetnamespace
                if (!typeValue.contains(":")) {
//					System.out
//							.println("Warning: this element type value has no prefix!---------"
//									+ elem.getAttributeValue("name"));

//					rootElem.getAttributeValue("targetNamespace")

                    for (Element schema : schemaList) {
                        //this case should only have one schema whose targetnamespace=root.targetnamespace

                        if (schemaPrefix.equals("")) {

                            try {
                                Element tempElem = (Element) (XPath.selectSingleNode(
                                        schema,
                                        "//schema/descendant::complexType[@name=\""
                                        + typeValue
                                        + "\"]"));
                                if (tempElem != null) {
                                    pathsList.addAll(getPathsList(tempElem));
                                    if (pathsList.isEmpty()) {
                                        List<Element> p = new ArrayList<Element>();
                                        p.add(elem);
                                        pathsList.add(p);
                                    } else {
                                        for (List<Element> path : pathsList) {
                                            path.add(elem);
                                        }
                                    }
                                } else {
                                    // otherwise this element is a
                                    // simpleType (no element inside, we
                                    // ignore, end recursive)
                                    List<Element> elePath = new ArrayList<Element>();
                                    elePath.add(elem);
                                    pathsList.add(elePath);
                                }
                            } catch (JDOMException e) {
                                e.printStackTrace();
                            }
                        } else {

                            try {
                                Element tempElem = (Element) (XPath.selectSingleNode(
                                        schema,
                                        "//"
                                        + schemaPrefix
                                        + ":schema/descendant::"
                                        + schemaPrefix
                                        + ":complexType[@name=\""
                                        + typeValue
                                        + "\"]"));
                                if (tempElem != null) {
                                    pathsList.addAll(getPathsList(tempElem));
                                    if (pathsList.isEmpty()) {
                                        List<Element> p = new ArrayList<Element>();
                                        p.add(elem);
                                        pathsList.add(p);
                                    } else {
                                        for (List<Element> path : pathsList) {
                                            path.add(elem);
                                        }
                                    }
                                } else {
                                    // otherwise this element is a
                                    // simpleType (no element inside, we
                                    // ignore, end recursive)
                                    List<Element> elePath = new ArrayList<Element>();
                                    elePath.add(elem);
                                    pathsList.add(elePath);
                                }
                            } catch (JDOMException e) {
                                e.printStackTrace();
                            }

                        }
                        break;

                    }
//					List<Element> elePath = new ArrayList<Element>();
//					elePath.add(elem);
//					pathsList.add(elePath);

                } else {
                    String typePrefix = typeValue.split(":")[0];
                    if (!typePrefix.equalsIgnoreCase(schemaPrefix)) {
                        // type=*:complexType/simpleType

                        for (Element schema : schemaList) {
                            if (rootElem.getNamespace(typePrefix).getURI().equals(
                                    schema.getAttributeValue("targetNamespace"))) {
                                if (schemaPrefix.equals("")) {

                                    try {
                                        Element tempElem = (Element) (XPath.selectSingleNode(
                                                schema,
                                                "//schema/descendant::complexType[@name=\""
                                                + typeValue.split(":")[1]
                                                + "\"]"));
                                        if (tempElem != null) {
                                            pathsList.addAll(getPathsList(tempElem));
                                            if (pathsList.isEmpty()) {
                                                List<Element> p = new ArrayList<Element>();
                                                p.add(elem);
                                                pathsList.add(p);
                                            } else {
                                                for (List<Element> path : pathsList) {
                                                    path.add(elem);
                                                }
                                            }
                                        } else {
                                            // otherwise this element is a
                                            // simpleType (no element inside, we
                                            // ignore, end recursive)
                                            List<Element> elePath = new ArrayList<Element>();
                                            elePath.add(elem);
                                            pathsList.add(elePath);
                                        }
                                    } catch (JDOMException e) {
                                        e.printStackTrace();
                                    }
                                } else {

                                    try {
                                        Element tempElem = (Element) (XPath.selectSingleNode(
                                                schema,
                                                "//"
                                                + schemaPrefix
                                                + ":schema/descendant::"
                                                + schemaPrefix
                                                + ":complexType[@name=\""
                                                + typeValue.split(":")[1]
                                                + "\"]"));
                                        if (tempElem != null) {
                                            pathsList.addAll(getPathsList(tempElem));
                                            if (pathsList.isEmpty()) {
                                                List<Element> p = new ArrayList<Element>();
                                                p.add(elem);
                                                pathsList.add(p);
                                            } else {
                                                for (List<Element> path : pathsList) {
                                                    path.add(elem);
                                                }
                                            }
                                        } else {
                                            // otherwise this element is a
                                            // simpleType (no element inside, we
                                            // ignore, end recursive)
                                            List<Element> elePath = new ArrayList<Element>();
                                            elePath.add(elem);
                                            pathsList.add(elePath);
                                        }
                                    } catch (JDOMException e) {
                                        e.printStackTrace();
                                    }

                                }
                                break;
                            }

                        }

                    } else {
                        // type=xsd:* end recursive
                        List<Element> elePath = new ArrayList<Element>();
                        elePath.add(elem);
                        pathsList.add(elePath);
                    }
                }
            } else {// simpleType as child, end recursive
                List<Element> elePath = new ArrayList<Element>();
                elePath.add(elem);
                pathsList.add(elePath);
            }

        } else if (elem.getName().equalsIgnoreCase("part")) {
            // 1. element = *:*(recursive invoke element)
            // 2. type = xsd:*(end) or type=*:*(recursive invoke complexType,
            // ignore simpleType--end recursive)

            if (elem.getAttribute("element") != null) {

                String elemName = elem.getAttributeValue("element");
                for (Element schema : schemaList) {
                    if (rootElem.getNamespace(elemName.split(":")[0]).getURI().equalsIgnoreCase(schema.getAttributeValue("targetNamespace"))) {
                        if (schemaPrefix.equals("")) {

                            try {
                                Element tempElem = (Element) (XPath.selectSingleNode(
                                        schema,
                                        "//schema/descendant::element[@name=\""
                                        + elemName.split(":")[1]
                                        + "\"]"));
                                pathsList.addAll(getPathsList(tempElem));

                            } catch (JDOMException e) {
                                e.printStackTrace();
                            }
                        } else {

                            try {
                                Element tempElem = (Element) (XPath.selectSingleNode(schema, "//"
                                        + schemaPrefix
                                        + ":schema/descendant::"
                                        + schemaPrefix
                                        + ":element[@name=\""
                                        + elemName.split(":")[1]
                                        + "\"]"));
                                pathsList.addAll(getPathsList(tempElem));
                            } catch (JDOMException e) {
                                e.printStackTrace();
                            }

                        }
                        if (pathsList.isEmpty()) {
                            List<Element> p = new ArrayList<Element>();
                            p.add(elem);
                            pathsList.add(p);
                        } else {
                            for (List<Element> path : pathsList) {
                                path.add(elem);
                            }
                        }
                        break;
                    }

                }
            } else if (!elem.getAttributeValue("type").split(":")[0].equalsIgnoreCase(schemaPrefix)) {
                // not xsd type, not element,may be simpleType or complexType
                // complex types which allow elements in their content and may
                // carry attributes, and simple types which cannot have element
                // content and cannot carry attributes
                // since no element in simpleType, here we will ignore
                // simpleType
                String partType = elem.getAttributeValue("type");
                if (partType.contains(":")) {
                    String partTypePrefix = partType.split(":")[0];

                    for (Element schema : schemaList) {
                        if (rootElem.getNamespace(partTypePrefix).getURI().equals(
                                schema.getAttributeValue("targetNamespace"))) {
                            if (schemaPrefix.equals("")) {

                                try {
                                    Element tempElem = (Element) (XPath.selectSingleNode(
                                            schema,
                                            "//schema/descendant::complexType[@name=\""
                                            + partType.split(":")[1]
                                            + "\"]"));
                                    if (tempElem != null) {
                                        pathsList.addAll(getPathsList(tempElem));
                                        if (pathsList.isEmpty()) {
                                            List<Element> p = new ArrayList<Element>();
                                            p.add(elem);
                                            pathsList.add(p);
                                        } else {
                                            for (List<Element> path : pathsList) {
                                                path.add(elem);
                                            }
                                        }
                                    } else {
                                        // otherwise this part is a simpleType
                                        // (no element inside, we ignore, end
                                        // recursive)
                                        List<Element> partPath = new ArrayList<Element>();
                                        partPath.add(elem);
                                        pathsList.add(partPath);
                                    }
                                } catch (JDOMException e) {
                                    e.printStackTrace();
                                }
                            } else {

                                try {
                                    Element tempElem = (Element) (XPath.selectSingleNode(schema, "//"
                                            + schemaPrefix
                                            + ":schema/descendant::"
                                            + schemaPrefix
                                            + ":complexType[@name=\""
                                            + partType.split(":")[1]
                                            + "\"]"));
                                    if (tempElem != null) {
                                        pathsList.addAll(getPathsList(tempElem));
                                        if (pathsList.isEmpty()) {
                                            List<Element> p = new ArrayList<Element>();
                                            p.add(elem);
                                            pathsList.add(p);
                                        } else {
                                            for (List<Element> path : pathsList) {
                                                path.add(elem);
                                            }
                                        }
                                    } else {
                                        // otherwise this part is a simpleType
                                        // (no element inside, we ignore, end
                                        // recursive)
                                        List<Element> partPath = new ArrayList<Element>();
                                        partPath.add(elem);
                                        pathsList.add(partPath);
                                    }
                                } catch (JDOMException e) {
                                    e.printStackTrace();
                                }

                            }
                            break;
                        }

                    }
                } else {
                    System.out.println("Warning: this part type has no prefix!");
                    List<Element> elePath = new ArrayList<Element>();
                    elePath.add(elem);
                    pathsList.add(elePath);
                }
            } else {
                // case: type=xsd:*, end recursive
                List<Element> partPath = new ArrayList<Element>();
                partPath.add(elem);
                pathsList.add(partPath);
            }
        }

        List<List<Element>> updatedPathsList = new ArrayList<List<Element>>();
        
        for (List<Element> path : pathsList) {
            
            List<Element> toRemove = new ArrayList<Element>();
            
            for (Element e : path) {
                
                boolean isMessage = e.getName().equalsIgnoreCase("message");
                boolean isPart    = e.getName().equalsIgnoreCase("part");
                
                if (isMessage || isPart ) {
                    toRemove.add(e);
                } // if
                
            } // for
            
            path.removeAll(toRemove);
            
            updatedPathsList.add(path);
            
        } // for
        
        //return pathsList;
        
        return updatedPathsList;

    }

    /**
     * constructor
     */
    public DmParser() {
    }

    /**
     * for test
     * 
     * @param args
     */
    public static void main(String[] args) {
//		List testL = new ArrayList();
//		List testList = new ArrayList();
//		testL.addAll(testList);
//		System.out.println("pass" +testL);

        SawsdlParser sp = new SawsdlParser();
//		Element inElem = sp.getInMsElem("wsdl/WSWUBlast.wsdl", "runWUBlast");
//		Element inElem = sp.getInMsElem("wsdl/picr.wsdl", "getUPIForAccession");
//		Element inElem = sp.getInMsElem("wsdl/CompanyInfo.wsdl", "getInfo");
//		Element inElem = sp.getInMsElem("wsdl/WSDbfetch.wsdl", "fetchBatch");
//		Element inElem = sp.getInMsElem("wsdl/stockquote.wsdl", "GetQuote");
//		Element inElem = sp.getInMsElem("wsdl/NewGeneByLocation.wsdl", "invoke");
////////////////////////////////////////////////////////////////////////////
//		Element inElem = sp.getInMsElem("wsdl/1/WSWUBlast.wsdl", "blastp");
//		Element inElem = sp.getOutMsElem("wsdl/1/WSWUBlast.wsdl", "blastp");
//		Element inElem = sp.getInMsElem("wsdl/1/WSWUBlast.wsdl", "blastn");
//		Element inElem = sp.getOutMsElem("wsdl/1/WSWUBlast.wsdl", "blastn");
//		Element inElem = sp.getInMsElem("wsdl/1/WSWUBlast.wsdl", "getOutput");
//		Element inElem = sp.getOutMsElem("wsdl/1/WSWUBlast.wsdl", "getOutput");
//		Element inElem = sp.getInMsElem("wsdl/1/WSWUBlast.wsdl", "getXML");
//		Element inElem = sp.getOutMsElem("wsdl/1/WSWUBlast.wsdl", "getXML");
//		Element inElem = sp.getInMsElem("wsdl/1/WSWUBlast.wsdl", "runWUBlast");
//		Element inElem = sp.getOutMsElem("wsdl/1/WSWUBlast.wsdl", "runWUBlast");
//		Element inElem = sp.getInMsElem("wsdl/1/WSWUBlast.wsdl", "checkStatus");
//		Element inElem = sp.getOutMsElem("wsdl/1/WSWUBlast.wsdl", "checkStatus");
//		Element inElem = sp.getInMsElem("wsdl/1/WSWUBlast.wsdl", "poll");
//		Element inElem = sp.getOutMsElem("wsdl/1/WSWUBlast.wsdl", "poll");
//		Element inElem = sp.getInMsElem("wsdl/1/WSWUBlast.wsdl", "getResults");
//		Element inElem = sp.getOutMsElem("wsdl/1/WSWUBlast.wsdl", "getResults");
//		Element inElem = sp.getInMsElem("wsdl/1/WSWUBlast.wsdl", "getIds");
//		Element inElem = sp.getOutMsElem("wsdl/1/WSWUBlast.wsdl", "getIds");
//		Element inElem = sp.getInMsElem("wsdl/1/WSWUBlast.wsdl", "getMatrices");
//		Element inElem = sp.getOutMsElem("wsdl/1/WSWUBlast.wsdl", "getMatrices");
//		Element inElem = sp.getInMsElem("wsdl/1/WSWUBlast.wsdl", "getPrograms");
//		Element inElem = sp.getOutMsElem("wsdl/1/WSWUBlast.wsdl", "getPrograms");

        List<OpWsdl> candidateOp = new ArrayList<OpWsdl>();
        candidateOp.add(new OpWsdl("blastp", "wsdl/1/WSWUBlast.wsdl"));
        candidateOp.add(new OpWsdl("blastn", "wsdl/1/WSWUBlast.wsdl"));
        candidateOp.add(new OpWsdl("getOutput", "wsdl/1/WSWUBlast.wsdl"));
        candidateOp.add(new OpWsdl("getXML", "wsdl/1/WSWUBlast.wsdl"));
        candidateOp.add(new OpWsdl("runWUBlast", "wsdl/1/WSWUBlast.wsdl"));
        candidateOp.add(new OpWsdl("checkStatus", "wsdl/1/WSWUBlast.wsdl"));
        candidateOp.add(new OpWsdl("checkStatus", "wsdl/1/WSWUBlast.wsdl"));
        candidateOp.add(new OpWsdl("poll", "wsdl/1/WSWUBlast.wsdl"));
        candidateOp.add(new OpWsdl("getResults", "wsdl/1/WSWUBlast.wsdl"));
        candidateOp.add(new OpWsdl("getMatrices", "wsdl/1/WSWUBlast.wsdl"));
        candidateOp.add(new OpWsdl("getPrograms", "wsdl/1/WSWUBlast.wsdl"));
        candidateOp.add(new OpWsdl("getDatabases", "wsdl/1/WSWUBlast.wsdl"));
        candidateOp.add(new OpWsdl("getSort", "wsdl/1/WSWUBlast.wsdl"));
        candidateOp.add(new OpWsdl("getStats", "wsdl/1/WSWUBlast.wsdl"));
        candidateOp.add(new OpWsdl("getXmlFormats", "wsdl/1/WSWUBlast.wsdl"));
        candidateOp.add(new OpWsdl("getSensitivity", "wsdl/1/WSWUBlast.wsdl"));
        candidateOp.add(new OpWsdl("getFilters", "wsdl/1/WSWUBlast.wsdl"));
        candidateOp.add(new OpWsdl("polljob", "wsdl/1/WSWUBlast.wsdl"));
        candidateOp.add(new OpWsdl("doWUBlast", "wsdl/1/WSWUBlast.wsdl"));
        //19	
        candidateOp.add(new OpWsdl("fetchBatch", "wsdl/1/WSDbfetchDoclit.wsdl"));
        candidateOp.add(new OpWsdl("fetchData", "wsdl/1/WSDbfetchDoclit.wsdl"));
        candidateOp.add(new OpWsdl("getDatabaseInfo", "wsdl/1/WSDbfetchDoclit.wsdl"));
        candidateOp.add(new OpWsdl("getDatabaseInfoList", "wsdl/1/WSDbfetchDoclit.wsdl"));
        candidateOp.add(new OpWsdl("getDbFormats", "wsdl/1/WSDbfetchDoclit.wsdl"));
        candidateOp.add(new OpWsdl("getFormatInfo", "wsdl/1/WSDbfetchDoclit.wsdl"));
        candidateOp.add(new OpWsdl("getFormatStyles", "wsdl/1/WSDbfetchDoclit.wsdl"));
        candidateOp.add(new OpWsdl("getStyleInfo", "wsdl/1/WSDbfetchDoclit.wsdl"));
        candidateOp.add(new OpWsdl("getSupportedDBs", "wsdl/1/WSDbfetchDoclit.wsdl"));
        candidateOp.add(new OpWsdl("getSupportedFormats", "wsdl/1/WSDbfetchDoclit.wsdl"));
        candidateOp.add(new OpWsdl("getSupportedStyles", "wsdl/1/WSDbfetchDoclit.wsdl"));
//19+11=30		
        candidateOp.add(new OpWsdl("getParameters", "wsdl/1/clustalw2.wsdl"));
        candidateOp.add(new OpWsdl("getParameterDetails", "wsdl/1/clustalw2.wsdl"));
        candidateOp.add(new OpWsdl("run", "wsdl/1/clustalw2.wsdl"));
        candidateOp.add(new OpWsdl("getStatus", "wsdl/1/clustalw2.wsdl"));
        candidateOp.add(new OpWsdl("getResultTypes", "wsdl/1/clustalw2.wsdl"));
        candidateOp.add(new OpWsdl("getResult", "wsdl/1/clustalw2.wsdl"));
//30+6=36
        candidateOp.add(new OpWsdl("array2string", "wsdl/1/WSConverter.wsdl"));
        candidateOp.add(new OpWsdl("base64toString", "wsdl/1/WSConverter.wsdl"));
//36+2=38
        candidateOp.add(new OpWsdl("getParameters", "wsdl/1/tcoffee.wsdl"));
        candidateOp.add(new OpWsdl("getParameterDetails", "wsdl/1/tcoffee.wsdl"));
        candidateOp.add(new OpWsdl("run", "wsdl/1/tcoffee.wsdl"));
        candidateOp.add(new OpWsdl("getStatus", "wsdl/1/tcoffee.wsdl"));
        candidateOp.add(new OpWsdl("getResultTypes", "wsdl/1/tcoffee.wsdl"));
        candidateOp.add(new OpWsdl("getResult", "wsdl/1/tcoffee.wsdl"));
//38+6=44		
        candidateOp.add(new OpWsdl("getParameters", "wsdl/1/ncbiblast.wsdl"));
        candidateOp.add(new OpWsdl("getParameterDetails", "wsdl/1/ncbiblast.wsdl"));
        candidateOp.add(new OpWsdl("run", "wsdl/1/ncbiblast.wsdl"));
        candidateOp.add(new OpWsdl("getStatus", "wsdl/1/ncbiblast.wsdl"));
        candidateOp.add(new OpWsdl("getResultTypes", "wsdl/1/ncbiblast.wsdl"));
        candidateOp.add(new OpWsdl("getResult", "wsdl/1/ncbiblast.wsdl"));
//44+6=50		


//		Element inElem = sp.getInMsElem(candidateOp.get(38).getWsdlName(), candidateOp.get(43).getOpName());
        Element inElem = sp.getOutMsElem(candidateOp.get(35).getWsdlName(), candidateOp.get(35).getOpName());
//		System.out.println(inElem.getName());
        DmParser pr = new DmParser();
        List<List<Element>> pList = pr.getPathsList(inElem);
        for (List<Element> p : pList) {
            for (Element ele : p) {
                System.out.println(ele.getAttributeValue("name"));

            }
            System.out.println("========================================");
        }
    }
}
