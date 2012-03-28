package workflowHelp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jdom.Element;
import org.jdom.Namespace;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import parser.OntologyManager;
import parser.SchemaParser;

/**
 *
 * @author Alok Dhamanaskar
 * @see    LICENSE (MIT style license file).
 * 
 */
public class suggestInputValues
{
    private static List<String> values = new ArrayList<String>();

    /**
     * A constructor for suggestInputValues class that given the name of the parameter, 
     * URL for the WSDL file and URL for the Ontology file gets the list of possible input 
     * values by looking up individuals & direct subclasses. 
     * Use getInputValues() to get the actual values.
     * 
     * @param WSDLURL : URL for the WSDL file from which the parameter is.
     * @param paramName : name of the parameter to suggest values for
     * @param owlURI : URI for the owl file
     * 
     */
    public suggestInputValues(String WSDLURL, String paramName, String owlURI)
    {
        
        String paramIRI = "";
        
        Element paramElement= null;
        Namespace sawsdlNS  = Namespace.getNamespace("sawsdl", "http://www.w3.org/ns/sawsdl");
        SchemaParser schemaParser = new SchemaParser();
        
        try{
            List<Element> schemaList =  schemaParser.getSchemaElemList(WSDLURL);
            for(Element e : schemaList)
                paramElement = schemaParser.getElementElemOfSchema(paramName, e);
            org.jdom.Attribute attribute = paramElement.getAttribute("modelReference",sawsdlNS);

            if(attribute!=null)
                if (!attribute.getValue().equals(""))
                {
                    try{
                        paramIRI = attribute.getValue();
                        OntologyManager parser = OntologyManager.getInstance(owlURI);
                        OWLClass conceptClass = parser.getConceptClass(paramIRI);
                        getIndividuals(conceptClass, parser);
                        getDirectSubClasses(conceptClass, parser);
                    }
                    catch(Exception e)
                    {
                        System.out.println("Exception Occurred when getting the class in the Ontology "
                                + "for the requested param :" + e);
                    }
                }//if ends        
            }//Outer try ends
            catch(java.lang.NullPointerException e)
            {
                System.out.println("The Web service document could not be found at the given address\n" + e);
                values.add("Unexpected Error Occurred check server log for Details !");
            }
            catch(Exception e)
            {    
                System.out.println("Following Exception Occurred: " + e);
                values.add("Unexpected Error Occurred check server log for Details !");    
            } 
        
    }//Method ends
    
    /**
     * Returns the list of InputValues calculated earlier
     * @return  List<String> of possible input values, calculated using individuals / direct sub-classes
     */
    public List<String> getInputValues()
    {
        return values;
    }
            
    /**
     * 
     * Finds out the direct sub-classes for a given class in an Ontology
     * 
     * @param conceptClass : The class in the ontology, as an object of OWLClass to find subclasses for 
     * @param parser : Object of ontology Manager
     */
    private static void getDirectSubClasses(OWLClass conceptClass, OntologyManager parser)
    {
        Set<OWLClass> subclasses = parser.getDirectSubClasses(conceptClass);
        String label = "";
        String className = "";
        
        for (OWLClass c : subclasses)
        {
            label = parser.getClassLabel(c);
            className = parser.getConceptName(c.getIRI().toString());
            if (!label.equals(""))
                values.add(label);
            else if(!className.equals(""))
                values.add(className);
        }//for ends
    }//method ends

    /**
     * 
     * Finds out the Individuals for a given class in an Ontology
     * 
     * @param conceptClass : The class in the ontology, as an object of OWLClass to find subclasses for 
     * @param parser : Object of ontology Manager
     */
    private static void getIndividuals(OWLClass conceptClass, OntologyManager parser)
    {
        try{
            OWLOntology ontology = parser.getOntology();
            Set<OWLIndividual> individuals = new HashSet<OWLIndividual>();
            individuals = conceptClass.getIndividuals(ontology);

            for(OWLIndividual i : individuals)
            {
                String name = i.toStringID();
                String[] temp = new String [10];
                if (name.contains("#"))
                {
                       temp = name.split("#");
                       name = temp[1];
                }// if ends
                values.add(name);
            }//for ends
        }// try ends
        catch(Exception e)
        {
            System.out.println("Exception Occured when getting the Individuals: " + e);
        }
    }// method ends
    
    public static void main (String[] args)
    {
        suggestInputValues s = new suggestInputValues("http://mango.ctegd.uga.edu/jkissingLab/SWS/Wsannotation/resources/wublast.sawsdl","program", "/home/alok/Desktop/SuggestionEngine/webService.ow");       

        util.debuggingUtils.printCollection(s.getInputValues());
    }
    
}
