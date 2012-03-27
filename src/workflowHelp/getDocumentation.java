
package workflowHelp;

import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import org.jdom.Namespace;
import org.semanticweb.owlapi.model.OWLClass;
import parser.OntologyManager;
import parser.SchemaParser;

/**
 *
 * @author Alok Dhamanaskar
 * @see    LICENSE (MIT style license file).
 * 
 */
public class getDocumentation
{
    /**
     * The function returns all available documentation for the given parameter in the WSDL.
     * It returns Documentation from the WSDL file as well as the Ontology
     * 
     * @param WSDLURL : URL to the WSDL file
     * @param paramName : Name of the Parameter from WSDL for which to find documentation
     * @param owlURI : The URL of the Ontology file
     * @return A list of available documentation including Ontological Definition, Description and 
     *         Usage as well as WSDL Documentation
     */
    
    public static List<String> getParamInfo(String WSDLURL,String paramName, String owlURI)
    {
        List<String> DocList = new ArrayList<String>();
        Element paramElement= null;
        Namespace nameSpace = null;
        Namespace sawsdlNS  = Namespace.getNamespace("sawsdl", "http://www.w3.org/ns/sawsdl");
        SchemaParser schemaParser = new SchemaParser();
        String paramIRI="";
        
        List<Element> schemaList =  schemaParser.getSchemaElemList(WSDLURL);
        for(Element e : schemaList)
        {
            nameSpace = e.getNamespace();
            paramElement = schemaParser.getElementElemOfSchema(paramName, e);
        }
        
        if (paramElement.getChild("annotation", nameSpace)!=null)
            if (paramElement.getChild("annotation",nameSpace).getChild("documentation",nameSpace) !=null)
                if(!paramElement.getChild("annotation",nameSpace).getChild("documentation",nameSpace).getText().equals(""))
                    DocList.add("Documentation:" + paramElement.getChild("annotation",nameSpace).getChild("documentation",nameSpace).getText());
        
        org.jdom.Attribute attribute = paramElement.getAttribute("modelReference",sawsdlNS);

        if(attribute!=null)
            if (!attribute.getValue().equals(""))
            {
                try{
                        paramIRI = attribute.getValue();
                        OntologyManager parser = OntologyManager.getInstance(owlURI);
                        OWLClass conceptClass = parser.getConceptClass(paramIRI);
                        String OntoDef = parser.getClassDefinition(conceptClass);
                        String usage = parser.getClassUsage(conceptClass);
                        String description = parser.getClassDescription(conceptClass);
                        
                        if (!OntoDef.equals(""))
                            DocList.add("Definition:" + OntoDef);
                        if (!usage.equals(""))
                            DocList.add("Usage:" + usage);
                        if (!description.equals(""))
                            DocList.add("Description:" + description);
                    }// try ends
                    catch(Exception e)
                    {
                        System.out.println("Exception occured when getting the Class "
                                + "in the Ontology for given parameter: " + e);                
                    }
            }// if ends
        
        return DocList;
    }// Method Ends
    
    public static void main(String[] args)
    {
        System.out.println(getParamInfo("http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/wublast.sawsdl", "jobId", "/home/alok/Desktop/SuggestionEngine/webService.owl"));
    
    }
}
