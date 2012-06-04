package ontologyManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static java.lang.System.*;

import org.semanticweb.owlapi.model.*;

/***************************************************************************************
 * This class is used to get the OWLClassRestriction, OWLproperty, OWLClass which are
 * defined in super OWLClassExpression as OWLRestrictions.
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

    /***********************************************************************************
     * Main method for testing.
     * 
     * @param args
     *            The command-line arguments.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        // load owl file
        OntologyManager discoveryMgr = OntologyManager.getInstance("obi.owl");

        out.println();

        OWLClass test = discoveryMgr.getConceptClass("http://purl.obolibrary.org/obo/webService.owl#Class_0013");
        out.println("test OWLClass = " + test);
        out.println("test OWLClass label = " + discoveryMgr.getClassLabel(test));
        out.println();

        Set<OWLClassExpression> supers = OntologyManager.getDirectSuperExpression(test);
        for (OWLClassExpression express : supers) {
            Restriction Res = new Restriction(discoveryMgr, express);
            out.println("OWLClassExpression = " + express);
            out.println("classTypeName = " + Res.getClassTypeName());
            out.println("Res.prop = " + discoveryMgr.getPropertyLabel(Res.getProp()));
            out.print("Res.cls = ");
            for (OWLClass cls : Res.getCls()) {
                out.print(discoveryMgr.getClassLabel(cls) + ", ");
            }
            out.println();
            out.println("Res.nextRestriction = " + Res.getNextRestriction());
            out.println();
        }
    }

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
}