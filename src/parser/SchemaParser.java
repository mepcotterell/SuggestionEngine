package parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

/**
 * @author Rui Wang
 * @author Alok Dhamanaskar
 * @see LICENSE (MIT style license file).
 * 
 * Parser for Parsing XML Schema (XSD)
 *
 */
public class SchemaParser {
    private static Namespace xsdNS = Namespace.getNamespace("xsd", "http://www.w3.org/2001/XMLSchema");
    private static Namespace wsdlNS = Namespace.getNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");

    /**
     * Returns if an Element with a given name Exists in the given schema
     *
     * @param elementName Name of te element to look for
     * @param schemaElem The schema Element
     * @return the Element if found else null
     */
    public Element getElementFromSchema(String elementName, Element schemaElem) 
    {
        Element elementElem = null;
        String elementPrefix = "";

        if (elementName.contains(":")) {
            elementPrefix = elementName.split(":")[0];
            elementName = elementName.split(":")[1];
        }

        Element rootElem = schemaElem.getDocument().getRootElement();
        //in case schema prefix is s, xs or xsd
        if (rootElem.getNamespace("xsd") != null) {
            xsdNS = rootElem.getNamespace("xsd");
        } else if (rootElem.getNamespace("xs") != null) {
            xsdNS = rootElem.getNamespace("xs");

        } else if (rootElem.getNamespace("s") != null) {
            xsdNS = rootElem.getNamespace("s");
        } else {
            System.out.println("Warning: the prefix of this schema is not xsd or xs or s, parser may fail!");
        }

        String xsdPrefix = xsdNS.getPrefix();
        if (elementPrefix.equals("") || rootElem.getNamespace(elementPrefix).getURI().equals(schemaElem.getAttributeValue("targetNamespace")) || rootElem.getNamespace(elementPrefix).getURI().equals(schemaElem.getAttributeValue("tns")) ) 
        {
            try {
                String q = "//"
                        + xsdPrefix
                        + ":schema/descendant::"
                        + xsdPrefix
                        + ":*[@name=\""
                        + elementName
                        + "\"]";
                Element tempElem = (Element) (XPath.selectSingleNode(schemaElem, q));
                elementElem = tempElem;
            } catch (JDOMException e) {
                e.printStackTrace();
            }
        }//if

                    
            if (elementElem == null) {
                String q = "//"
                        + xsdPrefix
                        + ":schema/descendant::"
                        + xsdPrefix
                        + ":complexType[@name=\""
                        + elementName
                        + "\"]";
                try 
                {
                    Element tempElem = (Element) (XPath.selectSingleNode(schemaElem, q));
                    elementElem = tempElem;
                }//try
                catch (JDOMException e) 
                {
                    e.printStackTrace();
                }//catch


            }
            


        return elementElem;
    }//getElementFromSchema

    
    /**
     * Returns if an Element with a given name Exists in the Document (WSDL)
     *
     * @param WsDoc URI of the Description document
     * @param schemaElem Name of the Element
     * @return the Element if found else null
     */
    public Element getElementFromSchema(String WsDoc, String schemaElem) 
    {
        Element rootElem = null;

        List<Element> schemas = this.getSchemaElemList(WsDoc);
        for (Element e : schemas) 
        {
            rootElem = this.getElementFromSchema(schemaElem, e);
            if (rootElem != null) 
                break;
        }//for

        return rootElem;
    }//getElementFromSchema


    /**
     * Given name of an element and root element of the WSDL, returns the element
     *
     * @param elementFullname
     * @param rootElem
     * @return
     */
    public Element getElementElemOfRoot(String elementFullname, Element rootElem) 
    {
        List<Element> schemaList = this.getSchemaElemList(rootElem);
        String elementPrefix = "";
        String elementName = elementFullname;
        Element elementElem = null;

        if (elementFullname.contains(":")) 
        {
            elementPrefix = elementFullname.split(":")[0];
            elementName = elementFullname.split(":")[1];
        }//if

        for (Element schema : schemaList) 
            if (rootElem.getNamespace(elementPrefix).getURI().equals(schema.getAttributeValue("targetNamespace"))) 
            {
                elementElem = this.getElementFromSchema(elementFullname, schema);
                break;
            }//if

        return elementElem;
    }//getElementElemOfRoot


    /**
     * Given a schema element and complexType prefix and name, returns the
     * complexType element in the schema, if not found returns null.
     *
     * @param complextypePrefix
     * @param complextypeName
     * @param rootElem
     * @return
     */
    public Element getComplextypeOfRoot(String complextypePrefix, String complextypeName, Element rootElem) 
    {
        List<Element> schemaList = this.getSchemaElemList(rootElem);
        Element complextypeElem = null;

        for (Element schema : schemaList) {
            if (complextypePrefix == null || complextypePrefix.equals("")) {
                complextypeElem = this.getComplextypeOfSchema(complextypePrefix, complextypeName, schema);

            } else {
                if (rootElem.getNamespace(complextypePrefix).getURI().equals(schema.getAttributeValue("targetNamespace"))) {
                    complextypeElem = this.getComplextypeOfSchema(complextypePrefix, complextypeName, schema);
                    break;
                }
            }
        }
        return complextypeElem;
    }//getComplextypeOfRoot

    /**
     * Given a schema element and complexType prefix and name, returns the
     * complexType element in the schema, if not found returns null.
     *
     * @param complextypePrefix
     * @param complextypeName
     * @param schemaElem
     * @return
     */
    public Element getComplextypeOfSchema(String complextypePrefix, String complextypeName, Element schemaElem) 
    {

        Element rootElem = schemaElem.getDocument().getRootElement();
        //in case schema prefix is s, xs or xsd
        if (rootElem.getNamespace("xsd") != null) {
            xsdNS = rootElem.getNamespace("xsd");
        } else if (rootElem.getNamespace("xs") != null) {
            xsdNS = rootElem.getNamespace("xs");

        } else if (rootElem.getNamespace("s") != null) {
            xsdNS = rootElem.getNamespace("s");
        } else {
            System.out.println("Warning: the prefix of this schema is not xsd or xs or s, parser may fail!");
        }

        String xsdPrefix = xsdNS.getPrefix();

        if (complextypePrefix != null && !complextypePrefix.equals("")) {
            //make sure the namespace of complextype prefix is same as schema targetnamespace
            if (rootElem.getNamespace(complextypePrefix).getURI().equals(schemaElem.getAttributeValue("targetNamespace"))) {
                try {
                    Element tempElem = (Element) (XPath.selectSingleNode(schemaElem, "//"
                            + xsdPrefix
                            + ":schema/descendant::"
                            + xsdPrefix
                            + ":complexType[@name=\""
                            + complextypeName
                            + "\"]"));
                    return tempElem;

                } catch (JDOMException e) {
                    e.printStackTrace();
                }
            }
        } else {//no complextype prefix given, just search the complextype name in the given schema element
            try {
                Element tempElem = (Element) (XPath.selectSingleNode(schemaElem, "//"
                        + xsdPrefix
                        + ":schema/descendant::"
                        + xsdPrefix
                        + ":complexType[@name=\""
                        + complextypeName
                        + "\"]"));
                return tempElem;

            } catch (JDOMException e) {
                e.printStackTrace();
            }
        }
        //nothing found
        return null;
    }//getComplextypeOfSchema

   
    /**
     * Given WSDL file name, retrieve all the schema Elements present.
     *
     * @param fileName
     * @return
     */
    public List<Element> getSchemaElemList(String fileName) 
    {
        List<Element> schemaElemList = new ArrayList();
        //get root element of xml file
        Element root = getRootElem(fileName);
        schemaElemList = getSchemaElemList(root);

        return schemaElemList;
    }//getSchemaElemList
    
    /**
     * 
     *
     * @param Element
     * @return
     */
    public double isRequired(Element e) 
    {
        Element child = e;
        Element parent = e.getParentElement();
        while(parent != null)
        {
            //if parent is optional
            //return optional
            //else
            {
                parent = parent.getParentElement();
            }
        }//while
        //return child.isrequired
        return 0.0;
    }//isRequired

    /**
     * Given WSDL root element, retrieve all the schema Elements
     *
     * @param rootElem
     * @return
     */
    public List<Element> getSchemaElemList(Element rootElem) 
    {
        //to do: handle import xsd
        List<Element> schemaElemList = new ArrayList();
        schemaElemList = rootElem.getChild("types", wsdlNS).getChildren("schema", xsdNS);

        String preXsdNs = schemaElemList.get(0).getNamespacePrefix();
        if (!preXsdNs.equals("")) 
            xsdNS = Namespace.getNamespace(preXsdNs, "http://www.w3.org/2001/XMLSchema");

        return schemaElemList;
    }

    /**
     * Returns the root element for the given document.
     *
     * @param fileName URI of the Document (WSDL)
     * @return root element
     */
    private Element getRootElem(String fileName) 
    {
        //String fileUrl=ClassLoader.getSystemResource(fileName).toString();
        //Thread.currentThread().getContextClassLoader().getResource(fileName).toString();
        String fileUrl = fileName;
        SAXBuilder sbuilder = new SAXBuilder();
        Element root = null;

        try {
            Document doc = sbuilder.build(fileUrl);

            //Document doc = sbuilder.build(fileName);
            //get root element of xml file
            root = doc.getRootElement();

        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (root.getNamespace("xsd") != null) {
            xsdNS = root.getNamespace("xsd");
        } else if (root.getNamespace("s") != null) {
            xsdNS = root.getNamespace("s");

        } else if (root.getNamespace("xs") != null) {
            xsdNS = root.getNamespace("xs");
        } else {
            System.out.println("Warning: the prefix of this schema is not xsd or s or xs!");
        }
        return root;
    }//getRootElement

    /**
     * Returns all the child xsd:Element(s) for a given element
     * @param e Element
     * @return List of child elements
     */
    List<Element> getNextLevelElements(Element e) 
    {
        List<Element> children = new ArrayList<Element>();
        if (e.getName().equalsIgnoreCase("element"))
        {
            String type = e.getAttributeValue("type");
            if(type!= null)
            {
                //Type is present
                if(type.contains(":"))
                {
                    type = type.split(":")[0];
                    if(type.equalsIgnoreCase("xsd") || type.equalsIgnoreCase("xs") || type.equalsIgnoreCase("xs"))
                    {
                        return children;
                    }
                    else 
                    {
                        Element next = this.getElementFromSchema(e.getAttributeValue("type"), e);
                        if(next!=null)
                        return this.getNextLevelElements(next);
                    }
                }
                else
                {
                    Element next = this.getElementFromSchema(e.getAttributeValue("type"), e);
                    return this.getNextLevelElements(next);
                }            
            }
            else
            {
                //type is not present at all
                Element next = e.getChild("complexType", xsdNS);
                if (next != null)
                    return this.getNextLevelElements(next);
                else
                    return children;
            }
        }
        else if(e.getName().equalsIgnoreCase("complextype"))
        {
            if(!e.getChildren().isEmpty())
            {
                for (Iterator it = e.getChildren().iterator(); it.hasNext();) 
                {
                    Element c = (Element) it.next();
                    children.addAll(this.getNextLevelElements(c));
                }
                return children;
            }
        }        
        else if(e.getName().equalsIgnoreCase("annotation") || e.getName().equalsIgnoreCase("documentation"))
        {
            //ignore
            return children;
        }
        else
        {
            if(!e.getChildren().isEmpty())
            {
                for (Iterator it = e.getChildren().iterator(); it.hasNext();) 
                {
                    Element c = (Element) it.next();
                    if(c.getName().equalsIgnoreCase("element"))
                        children.add(c);
                    else
                        children.addAll(this.getNextLevelElements(c));                        
                }        
                return children;
            }
            else
            {
                return children;
            }
        }
        return children;
    }//getNextLevelElements    
    
    public SchemaParser() {
    }//constructor

    public static void main(String[] args) {
        //Test Code
    }
    
}//SchemaParses
