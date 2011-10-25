package ontology.similarity;

import java.util.*;

import org.semanticweb.owlapi.model.*;
import parser.OntologyManager;

public class WBzard {

    /*enumeration types for the match of classes*/
    public enum Match {

        SAMECLASS, EQUIVALENTCLASS, SUPERCLASS, SUBCLASS, INTERSECTINGCLASS, NONINTERSECTING, EXCLUSIVEPROPERTIES, COMMONPROPERTIES, OWLCLASSNOTFOUND
    }
    private String physicalURI = null;
    private OntologyManager loader = null;

    /**
     * constructor
     * load ontology, given the filename
     * @param fileName
     */
    public WBzard(String fileName) {
        /*Read the ontology from the disk*/
        // final 
//		 URL fileURL = ClassLoader.getSystemResource(fileName);
//		physicalURI = fileURL.toString();
        physicalURI = fileName;

        loader = OntologyManager.getInstance(physicalURI);
    }

    /*method which calls the methods and determins the relationship between the two classes */
    public Match matchClasses(OWLClass class1, OWLClass class2) {

        if (class1 == null || class2 == null) {
            return Match.OWLCLASSNOTFOUND;
        } else if (class1.equals(class2)) {
            return Match.SAMECLASS;
        } else if (checkSuperClass(class1, class2)) {
            return Match.SUPERCLASS;
        } else if (checkSubClass(class1, class2)) {
            return Match.SUBCLASS;
        } else if (checkEquivalent(class1, class2)) {
            return Match.EQUIVALENTCLASS;
        } else if (checkIntersect(class1, class2)) {
            return Match.INTERSECTINGCLASS;
        } else {
            return Match.NONINTERSECTING;
        }
    }

    /*This method Checks for intersection
     * 
     */
    public boolean checkIntersect(OWLClass class1, OWLClass class2) {
        return loader.areIntersecting(class1, class2);
    }
    /**/

    public Set<OWLDataProperty> checkExclusiveDataProperties(OWLClass class1, OWLClass class2) {
        Set<OWLDataProperty> propertyClass1 = new HashSet<OWLDataProperty>();
        Set<OWLDataProperty> propertyClass2 = new HashSet<OWLDataProperty>();
        Set<OWLDataProperty> exPropertyClass1 = new HashSet<OWLDataProperty>();


        propertyClass1 = loader.getDataProperties(class1);
//		System.out.println(propertyClass1.size());
        propertyClass2 = loader.getDataProperties(class2);

        for (OWLDataProperty concept1Property : propertyClass1) {
            if (!propertyClass2.contains(concept1Property)) {
                exPropertyClass1.add(concept1Property);
                //System.out.println(concept1Property.getURI().toString());
            }
        }
        return exPropertyClass1;
    }
    
    public Set<IRI> checkCommonProperties(OWLClass class1, OWLClass class2) {
        Set<IRI> prop = new HashSet<IRI>();

        //  Set<OWLDataProperty>  concept1Property=loader.getDataProperties(class1);
        for (OWLDataProperty conceptProperty : loader.getDataProperties(class1)) {
            for (OWLDataProperty concept2Property : loader.getDataProperties(class2)) {

                if (concept2Property.getIRI().equals(conceptProperty.getIRI())) {
                    prop.add(concept2Property.getIRI());
                    //System.out.println("prop"+prop);
                    break;
                }
            }
            //System.out.println(conceptProperty.getURI().toString());
        }

        for (OWLObjectProperty concept11Property : loader.getObjectProperties(class1)) {
            for (OWLObjectProperty concept2Property : loader.getObjectProperties(class2)) {

                if (concept2Property.getIRI().equals(concept11Property.getIRI())) {
                    prop.add(concept2Property.getIRI());
                    //System.out.println("prop"+prop);
                    break;
                }

            }
        }
        return prop;
    }

    /*This method Checks if the class2 is the superclass of class1
     * 
     */
    public boolean checkSuperClass(OWLClass class1, OWLClass class2) {
        
        Set<OWLClass> supers = new HashSet<OWLClass>();
        
        for (Set<OWLClass> set : loader.getSuperClasses(class1)) {
            supers.addAll(set);
        }
        
        for (OWLClass cls : supers) {
            if (cls.getIRI().equals(class2.getIRI())) {
                return true;
            }
        }
        return false;
    }
    /*This method Checks if the class2 is the subclass of class1
     * 
     */

    public boolean checkSubClass(OWLClass class1, OWLClass class2) {
        
        Set<OWLClass> supers = new HashSet<OWLClass>();
        
        for (Set<OWLClass> set : loader.getSuperClasses(class2)) {
            supers.addAll(set);
        }
        
        for (OWLClass cls : supers) {
            if (cls.getIRI().equals(class1.getIRI())) {
                return true;
            }
        }
        return false;
    }
    /*This method Checks if the class1 is the equivalent to class2
     * 
     */

    public boolean checkEquivalent(OWLClass class1, OWLClass class2) {
        for (OWLClass cls : loader.getEquivalentClasses(class1)) {
            if (cls.getIRI().equals(class2.getIRI())) {
                return true;
            }
        }
        return false;
    }

    /* Print out all of the classes which are referenced in the ontology
     * And extracting the name of the concept form the URI
     * 
     */
    public OWLClass getConceptClass(String concept) {
        return loader.getConceptClass(concept);
    }

    /*check the match between the two classes 
     * that is same class,superclass,subclass,equivalent class,intersection
     */
    public Match compare2concepts(String concept1, String concept2) {
        OWLClass class1 = getConceptClass(concept1);
        OWLClass class2 = getConceptClass(concept2);
        Match compareResult = null;
        if (class2 != null && class1 != null) {
            compareResult = matchClasses(class1, class2);
        }

        return compareResult;
    }

    public Set<IRI> getCommonProperty(String concept1, String concept2) {
        OWLClass class1 = getConceptClass(concept1);
        OWLClass class2 = getConceptClass(concept2);
        Set<IRI> commonPropertySet = null;

        if (class2 != null && class1 != null) {
            commonPropertySet = checkCommonProperties(class1, class2);
        }
        return commonPropertySet;
    }

    public Set<OWLDataProperty> getExclusiveProperty(String concept1, String concept2) {
        OWLClass class1 = getConceptClass(concept1);
        OWLClass class2 = getConceptClass(concept2);

        Set<OWLDataProperty> exclusivePropertySet = null;
        if (class2 != null && class1 != null) {

            exclusivePropertySet = checkExclusiveDataProperties(class1, class2);
        }
        return exclusivePropertySet;
    }

    /**
     * for test
     * @param args
     */
    public static void main(String[] args) {
        WBzard ontology = new WBzard("LSDIS_Finance.owl");
        Match tempResult = ontology.compare2concepts("name", "name");
    }
}