package ontologyManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static java.lang.System.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;

/**
 * This class is used to get the OWLClassRestriction, OWLproperty, OWLClass which are
 * defined in super OWLClassExpression as OWLRestrictions.
 * 
 * @author Yung Long Lee
 * @see LICENSE (MIT style license file). 
 * 
 * 
 */
public class Restriction {

    private Set<OWLClass> cls = new HashSet<OWLClass>();
    private String classTypeName = "";
    private OWLProperty prop;
    private OWLClassExpression nextRestriction;
    private ArrayList<String> classTypeNames = new ArrayList<String>();

    @SuppressWarnings("rawtypes")
    public Restriction(OntologyManager manager, OWLClassExpression express) {

        // define classtype name
        classTypeNames.add("objectmaxcardinality");
        classTypeNames.add("objectexactcardinality");
        classTypeNames.add("objectmincardinality");
        classTypeNames.add("ObjectSomeValuesFrom");
        classTypeNames.add("ObjectHasValue");
        classTypeNames.add("ObjectAllValuesFrom");

        if (express.isAnonymous()) {
            for (int i = 0; i < classTypeNames.size(); i++) {
                if (express.getClassExpressionType().getName().equalsIgnoreCase(classTypeNames.get(i))) {
                    classTypeName = classTypeNames.get(i);

                    prop = ((OWLObjectProperty) ((OWLRestriction) express).getProperty());
                    //out.println("property = " + prop);

                    //check nested ObjectIntersectionOf
                    nextRestriction = seekIntersectionOrUnion(express);
                    //out.println("nextRestriction = " + nextRestriction);

                    if (nextRestriction == null) {
                        Iterator<OWLClassExpression> childPart = express.getNestedClassExpressions().iterator();
                        while (childPart.hasNext()) {
                            OWLClassExpression _childPart = childPart.next();
                            if (!_childPart.isAnonymous()) {
                                cls.add(_childPart.asOWLClass());
                            }
                        }
                    } else if (nextRestriction.getClassExpressionType().getName().equalsIgnoreCase("ObjectUnionOf")) {
                        Iterator<OWLClassExpression> childPart = nextRestriction.getNestedClassExpressions().iterator();
                        while (childPart.hasNext()) {
                            OWLClassExpression _childPart = childPart.next();
                            if (!_childPart.isAnonymous()) {
                                cls.add(_childPart.asOWLClass());
                            }
                        } // while
                    }
                }
            } // for
        }
    } // constructor

    public OWLClassExpression seekIntersectionOrUnion(OWLClassExpression parent) {
        OWLClassExpression result = null;
        for (OWLClassExpression childPart : parent.getNestedClassExpressions()) {
            if (childPart.getClassExpressionType().getName().equalsIgnoreCase("ObjectIntersectionOf")
                    || childPart.getClassExpressionType().getName().equalsIgnoreCase("ObjectUnionOf")) {
                result = childPart;
                return result;
            }
        }
        return result;
    }

    /**
     * Returns Restrictions on all the properties defined for this class
     * @param OWLClass
     * @param owlURI
     * @return Set of restrictions
     */
    public static Set<Restriction> getRestrictions(OWLClass cls, String owlURI) {
        OntologyManager parser = OntologyManager.getInstance(owlURI);

        Set<OWLClassExpression> clsSupers = OntologyManager.getDirectSuperExpression(cls);
        Set<Restriction> clsRestictions = new HashSet<Restriction>();

        for (OWLClassExpression clsExpress : clsSupers) {
            Restriction res = new Restriction(parser, clsExpress);
            clsRestictions.add(res);
        } // for

        return clsRestictions;

    } // getRestrictions
    
    /**
     * Returns a list of restriction defined on a property for the given class
     * 
     * @param OWLclass
     * @param property
     * @param owlURI
     * @return List of restrictions
     */
    public static List<Restriction> getRestrictionsOnProp(OWLClass clas, OWLProperty prop, String owlURI)
    {
        OntologyManager parser = OntologyManager.getInstance(owlURI);
        List<Restriction> restrList = new ArrayList<Restriction>();
        
        Set<OWLClassExpression> supers = OntologyManager.getDirectSuperExpression(clas);
        for (OWLClassExpression express : supers) 
        {
            Restriction res = new Restriction(parser, express);            
            if(res.getProp() == prop)
                restrList.add(res);
        }//for
        
        return restrList;
    }//GetRestrictions on Property

    public static void main(String[] args) throws IOException 
    {
        //Test code
        String class1 = "http://purl.obolibrary.org/obo/OBIws_0000084";
        String owlURI = "http://obi-webservice.googlecode.com/svn/trunk/ontology/webService.owl";
        OntologyManager parser = OntologyManager.getInstance(owlURI);

        out.println();

        OWLClass test = parser.getConceptClass(class1);
        out.println("test OWLClass = " + test);
        out.println("test OWLClass label = " + parser.getClassLabel(test));
        out.println();

        Set<OWLClassExpression> supers = OntologyManager.getDirectSuperExpression(test);
        for (OWLClassExpression express : supers) {
            Restriction Res = new Restriction(parser, express);
            out.println("OWLClassExpression = " + express);
            out.println("classTypeName = " + Res.getClassTypeName());
            out.println("Res.prop = " + parser.getPropertyLabel(Res.getProp()));
            out.print("Res.cls = ");
            for (OWLClass cls : Res.getCls()) {
                out.print(parser.getClassLabel(cls) + ", ");
            }
            out.println();
            out.println("Res.nextRestriction = " + Res.getNextRestriction());
            out.println();
        }
        
        
        Set<OWLProperty> class1PropSet = parser.getProperties(test); 
        List<OWLProperty> class1PropList = new ArrayList<OWLProperty>(class1PropSet);
        int class1propSize = class1PropSet.size();
        out.println("no Prop = " + class1propSize);
        for(OWLProperty p : class1PropList)
        {
            int i= 0;
            out.println("Prop = " + parser.getPropertyLabel(p) + "\n Restrictions on Property ->");
            for (OWLClassExpression express : supers) 
            {
                Restriction Res = new Restriction(parser, express);            
                if(Res.getProp() == p)
                {
                    out.println(++i + " restType " + Res.getClassTypeName());
                    for (OWLClass cls : Res.getCls()) 
                        out.print(parser.getClassLabel(cls) + ", ");
                }//if
            }//for
            out.println("\n-------------------------------------------------------\n");
        }//outer for
        
    }//Main

    /**
     * @return the cls
     */
    public Set<OWLClass> getCls() {
        return cls;
    }

    /**
     * @return the classTypeName
     */
    public String getClassTypeName() {
        return classTypeName;
    }

    /**
     * @return the prop
     */
    public OWLProperty getProp() {
        return prop;
    }

    /**
     * @return the nextRestriction
     */
    public OWLClassExpression getNextRestriction() {
        return nextRestriction;
    }

    /**
     * @return the classTypeNames
     */
    public ArrayList<String> getClassTypeNames() {
        return classTypeNames;
    }
}//Restrictions