package betterSemantics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import parser.OntologyManager;

/**
 *
 * @author Alok Dhamanaskar
 * @see    LICENSE (MIT style license file).
 * 
 */
public class suggestInputValues
{
    
    private static ArrayList<String> suggestValues(String paramIRI, String owlURI)
    {
        ArrayList values = new ArrayList<String>();
        
        OntologyManager parser = OntologyManager.getInstance(owlURI);
        OWLClass conceptClass = parser.getConceptClass(paramIRI);
        
        values.addAll(getDirectSubClasses(conceptClass,owlURI));
        values.addAll(getIndividuals(conceptClass,owlURI));        
        return values;
    }

    private static ArrayList getDirectSubClasses(OWLClass conceptClass, String owlURI)
    {
        ArrayList values = new ArrayList<String>();
        OntologyManager parser = OntologyManager.getInstance(owlURI);
        Set<OWLClass> subclasses = parser.getDirectSubClasses(conceptClass);
        String label = "";
        
        for (OWLClass c : subclasses)
        {
            label = parser.getClassLabel(c);
            if (!label.equals(""))
                values.add(label);
        }
        return values;
    }

    private static Collection getIndividuals(OWLClass conceptClass,String owlURI)
    {
        ArrayList values = new ArrayList<String>();
        OntologyManager parser = OntologyManager.getInstance(owlURI);
        //parser
        return values;
    }
    
    public static void main (String[] args)
    {
    
    
    
    }
    
    
}
