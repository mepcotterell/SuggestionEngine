package betterSemantics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import parser.OntologyManager;

/**
 *
 * @author Alok Dhamanaskar
 * @see    LICENSE (MIT style license file).
 * 
 */
public class suggestInputValues
{
    private static HashMap<String,String> values = new HashMap<String, String>();

    public suggestInputValues(String paramIRI, String owlURI, String WSDLURL)
    {
        OntologyManager parser = OntologyManager.getInstance(owlURI);
        OWLClass conceptClass = parser.getConceptClass(paramIRI);
        
        getIndividuals(conceptClass, parser);
        getDirectSubClasses(conceptClass,parser);    
    }
    
    public HashMap<String,String> getInputValues()
    {
        return values;
    }
            
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
                values.put(label,"SubClass");
            else if(!className.equals(""))
                values.put(className,"SubClass");
        }
    }

    private static void getIndividuals(OWLClass conceptClass, OntologyManager parser)
    {
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
            }
            values.put(name,"individuals");
        }
    }
    
    public static void main (String[] args)
    {
        suggestInputValues s = new suggestInputValues("http://purl.obolibrary.org/obo/OBIws_0000096", "/home/alok/Desktop/SuggestionEngine/webService.owl","");       
        System.out.println(s.getInputValues());
    }
    
}
