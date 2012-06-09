package ontologyManager;

import java.io.*;
import static java.lang.System.out;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

/**
 * @author Yung Long le, 
 * @see LICENSE (MIT style license file).
 * 
 * This class is used to load OWL files or URL's into memory.  Clients of this class
 * must handle the following classes from the OWLAPI: OWLClass, OWLDataProperty and
 * OWLObjectProperty.
 * 
 */
public class OntologyManager {

    /**
     * The singleton instance of the OntologyManager class
     */
    private static Map<String, OWLOntology> ontologyInstances = new HashMap<String, OWLOntology>();
    
    private static Map<String, OntologyManager> ontologyManagerInstances = new HashMap<String, OntologyManager>();
    
    private static final String base = "";

    
    /**
     * The ontology manager which is used to be load ontology(owl file).
     */
    private OWLOntologyManager manager;
    
    /**
     * The ontology to be loaded.
     */
    private OWLOntology ontology;
    
    public  Map<String, OWLOntology> getOntologyInstances()
    {
        return ontologyInstances;
    }
    
    public static OntologyManager getInstance(String owlURI) {
        
        OntologyManager manager = null;
        
        if (ontologyManagerInstances.containsKey(owlURI)) {
            manager = ontologyManagerInstances.get(owlURI);
        } else {
            try {
                manager = new OntologyManager(owlURI);
                ontologyManagerInstances.put(owlURI, manager);
            } catch (IOException ex) {
                Logger.getLogger(OntologyManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (OWLOntologyCreationException ex) {
                Logger.getLogger(OntologyManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return manager;
        
    }
    
    /**
     * The HashMap for increasing the performance.
     */
    private HashMap<IRI, OWLClass> OwlClaz = new HashMap<IRI, OWLClass>();
    private HashMap<IRI, ArrayList<Set<OWLClass>>> SuperClaz = new HashMap<IRI, ArrayList<Set<OWLClass>>>();
    private HashMap<IRI, ArrayList<Set<OWLClass>>> SubClaz = new HashMap<IRI, ArrayList<Set<OWLClass>>>();
    private HashMap<IRI, Set<OWLDataProperty>> OwlDataProps = new HashMap<IRI, Set<OWLDataProperty>>();
    private HashMap<IRI, Set<OWLObjectProperty>> OwlObjProps = new HashMap<IRI, Set<OWLObjectProperty>>();
    private HashMap<String, Integer> Cardinality = new HashMap<String, Integer>();

    /***********************************************************************************
     * Construct an ontology loader by obtaining a OWLOntologyManager which, as
     * the name suggests, manages ontology. An ontology is unique within an
     * ontology manager. To load multiple copies of an ontology, multiple
     * managers would have to be used. To parse the owl file we need a reasoner
     * to analyze or infer the owl file. We need to construct the reasoner. For
     * owlapi, the reasoner is incomplete. Right now, we use the HermiT
     * reasoner.
     * 
     * @param String:
     *            file path of the owl file.
     * @throws IOException
     */
    public OntologyManager(String filename) throws IOException,
            OWLOntologyCreationException {

        try {
            
            // Create our ontology manager in the usual way.
            manager = OWLManager.createOWLOntologyManager();

            //Check if the Ontology Location is a URL or a local file

            if (filename.contains("http:") || filename.contains("HTTP:") || filename.contains("www.") || filename.contains("WWW.")) 
            {
                URL u = new URL(filename);
                IRI iri;
                try {
                    iri = IRI.create(u);
                    //load Ontology from IRI
                    System.out.println("Now loading owl file from IRI: " + filename);
                    ontology = manager.loadOntology(iri);
                } catch (URISyntaxException ex) {
                    Logger.getLogger(OntologyManager.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else 
            {
                // load ontology on local file
                File inputOntologyFile = new File(base + filename);

                // Now load the local copy
                System.out.println("Now loading owl file from " + base + filename);

                ontology = manager.loadOntologyFromOntologyDocument(inputOntologyFile);

            }
            
            String ontID = ontology.getOntologyID().toString();
            
            int length = ontID.length();
            int lastindx = ontID.lastIndexOf("http");
            
            if (ontID.split("http").length > 2) {
                ontologyInstances.put(ontID.substring(lastindx, length - 1), ontology);
            } else {
                ontologyInstances.put(ontID.substring(lastindx, length - 1), ontology);
            }

            // load the import ontology from web
            Set<OWLOntology> importsOnts = ontology.getImports();
            Iterator<OWLOntology> iterator = importsOnts.iterator();
            while (iterator.hasNext()) {
                OWLOntologyManager importManager = OWLManager.createOWLOntologyManager();
                ontID = iterator.next().getOntologyID().toString();
                length = ontID.length();
                lastindx = ontID.lastIndexOf("http");
                if (ontID.split("http").length > 2) {
                    System.out.println("Now loading owl file from " + ontID.substring(lastindx, length - 1));
                    OWLOntology importOnt = importManager.loadOntologyFromOntologyDocument(IRI.create(ontID.substring(lastindx, length - 1)));
                    ontologyInstances.put(ontID.substring(lastindx, length - 1), importOnt);
                } else {
                    System.out.println("Now loading owl file from " + ontID.substring(1, length - 1));
                    OWLOntology importOnt = importManager.loadOntologyFromOntologyDocument(IRI.create(ontID.substring(1, length - 1)));
                    ontologyInstances.put(ontID.substring(lastindx, length - 1), importOnt);
                }
            }

            // add to OwlClaz HashMap
            Iterator<OWLOntology> ont = ontologyInstances.values().iterator();
            while (ont.hasNext()) {
                for (OWLClass cls : ont.next().getClassesInSignature(true)) {
                    if (!OwlClaz.containsKey(cls.getIRI())) {
                        OwlClaz.put(cls.getIRI(), cls);
                    }
                }
            }
            System.out.println("total class size = " + OwlClaz.size());


        } catch (UnsupportedOperationException exception) {
            System.out.println("Unsupported reasoner operation.");
        }

    } // OntologyManager Constructor

    /*****************************************************************************************
     * pull out all of the classes which are referenced in the ontology And
     * extracting the name of the concept form the URI find the ontology class
     * whose name is the given conceptName
     * 
     * @param conceptName
     *            : the concept class for searching
     * @return OWLClass: the class which matches the input name, if not return null
     */
    public OWLClass getConceptClass(String iri) {
        if (iri == null) {
            return null;
        }
        if (OwlClaz.get(IRI.create(iri)) == null) {
            return null;
        } else {
            return OwlClaz.get(IRI.create(iri));
        }
    }

    /***********************************************************************************
     * Get the OWL classes intersect each other .
     * 
     * @param class1
     *            : the first OWLclass for checking intersecting.
     * @param class2
     *            : the second OWLclass for checking intersecting.
     * @return boolean: if the classes intersect if it is not an owl nothing
     */
    public boolean areIntersecting(OWLClass class1, OWLClass class2) {
        OWLDataFactoryImpl classfac = new OWLDataFactoryImpl();
        Set<OWLClassExpression> set = new HashSet<OWLClassExpression>();
        set.add(class1);
        set.add(class2);
        OWLObjectIntersectionOf obj = classfac.getOWLObjectIntersectionOf(set);
        OWLAnonymousClassExpression object = (OWLAnonymousClassExpression) obj;
        return !object.isOWLNothing();
    }

    /***********************************************************************************
     * Get the prefix to the ontology in the parser.
     * 
     * @return The prefix.
     */
    public OWLOntology getOntology(String ontologyID) {
        return ontologyInstances.get(ontologyID);
    }
    
    public OWLOntology getOntology() {
        return ontology;
    }

    /***********************************************************************************
     * Get the classes equivalent to the class cls.
     * 
     * @param cls
     *            : the class equivalent to this class.
     * @return Set<OWLClass>: The classes equivalent to the class cls.
     */
    public Set<OWLClass> getEquivalentClasses(OWLClass cls) {
        HashSet<OWLClass> visited = new HashSet<OWLClass>();
        visited.add(cls);

        HashSet<OWLClass> equivs = new HashSet<OWLClass>();
        if (cls == null) {
            return equivs;
        }
        Iterator<OWLOntology> ont = ontologyInstances.values().iterator();
        while (ont.hasNext()) {
            Set<OWLClassExpression> _equivs = cls.getEquivalentClasses(ont.next());

            
            for (OWLClassExpression cd : _equivs) {
                if (!cd.isAnonymous()) {
                    equivs.add(cd.asOWLClass());
                }
            } // for 
        }
        for (OWLClass equiv : equivs) {
            if (!visited.contains(equiv)) {
                equivs.addAll(getEquivalentClasses(visited, equiv));
            }
        }
        
        // add itself to set of equivalent classes
        equivs.add(cls);
        
        return equivs;
    } // getEquivalentClasses

    public Set<OWLClass> getEquivalentClasses(HashSet<OWLClass> visited, OWLClass cls) {
        visited.add(cls);
        Set<OWLClass> equivs = new HashSet<OWLClass>();
        if (cls == null) {
            return equivs;
        }
        Iterator<OWLOntology> ont = ontologyInstances.values().iterator();
        while (ont.hasNext()) {
            Set<OWLClassExpression> _equivs = cls.getEquivalentClasses(ont.next());
            for (OWLClassExpression cd : _equivs) {
                if (!cd.isAnonymous()) {
                    equivs.add(cd.asOWLClass());
                }
            } // for 
        }
        for (OWLClass equiv : equivs) {
            if (!visited.contains(equiv)) {
                equivs.addAll(getEquivalentClasses(visited, equiv));
            }
        }
        return equivs;
    } // getEquivalentClasses

    /**
     * Return true if cls2 is equivalent to cls1
     * @param cls1
     * @param cls2
     * @return 
     */
    public boolean hasEquivalentClass(OWLClass cls1, OWLClass cls2) {
        return (getEquivalentClasses(cls1).contains(cls2));
    }

    /***********************************************************************************
     * Get the super classes of the class cls.
     * 
     * @param cls
     *            the class for searching super classes.
     * @return Set<OWLClass>: The super classes of the class cls.
     */
    public Set<OWLClass> getDirectSuperClasses(OWLClass cls) {
        //no inferred
        Set<OWLClass> supers = new HashSet<OWLClass>();
        if (cls == null) {
            return supers;
        }
        Iterator<OWLOntology> ont = ontologyInstances.values().iterator();
        while (ont.hasNext()) {
            Set<OWLClassExpression> _supers = cls.getSuperClasses(ont.next());
            if (_supers != null) {
                for (OWLClassExpression cd : _supers) {
                    if (!cd.isAnonymous()) {
                        supers.add(cd.asOWLClass());
                    }
                } // for 
            }
        }
        return supers;
    } // getDirectSuperClasses

    /**
     * Return super OWLRestriction
     * @param cls
     * @return Set<OWLClassExpression>: super OWLRestriction
     */
    public static Set<OWLClassExpression> getDirectSuperExpression(OWLClass cls) {
    	Set<OWLClassExpression> supers = new HashSet<OWLClassExpression>();
        Iterator<OWLOntology> ont = ontologyInstances.values().iterator();
        while (ont.hasNext()) {
        	Set<OWLClassExpression> _supers = cls.getSuperClasses(ont.next());
            for (OWLClassExpression ce : _supers) {
                if (ce.isAnonymous()) {
                    supers.add(ce);
                }
            } // for 
        }	
        return supers;
    } // getDirectSuperExpression

    /**
     * Returns if OWLClass2 is a super class of OWLClass1
     * @param cls1
     * @param cls2
     * @return 
     */
    public boolean hasSuperClass(OWLClass cls1, OWLClass cls2) 
    {
        
        ArrayList<Set<OWLClass>> class1SuprClList = getSuperClasses(cls1);
        Set<OWLClass> class1SuprClSet = new HashSet<OWLClass>();

        for(Set<OWLClass> ss : class1SuprClList)
            for(OWLClass c : ss)
                class1SuprClSet.add(c);
                
        return (class1SuprClSet.contains(cls2));
    }//hasSuperClass

    /***********************************************************************************
     * Get the super classes of the class cls in hierarchy.
     * 
     * @param owlCls
     *            the class for searching super classes.
     * @return ArrayList<Set<OWLClass>>: The super classes of the class owlCls.
     */
    public ArrayList<Set<OWLClass>> getSuperClasses(OWLClass cls) {
        ArrayList<Set<OWLClass>> outCeptSuperClz = new ArrayList<Set<OWLClass>>();
        if (cls == null) {
            return outCeptSuperClz;
        }
        if (SuperClaz.containsKey(cls.getIRI())) 
        {
            return SuperClaz.get(cls.getIRI());
        } 
        else 
        {
            // get every super class of outConceptClass
            Set<OWLClass> temp = getDirectSuperClasses(cls);
            while (!temp.isEmpty()) 
            {
                Set<OWLClass> nextLevel = new HashSet<OWLClass>();
                for (OWLClass owlCls : temp) {
                    Set<OWLClass> temp1 = getDirectSuperClasses(owlCls);
                    if (!temp1.isEmpty()) {
                        Iterator<OWLClass> iterator = temp1.iterator();
                        while (iterator.hasNext()) {
                            nextLevel.add(iterator.next());
                        }
                    }
                }
                outCeptSuperClz.add(temp);
                temp = nextLevel;
            }
            SuperClaz.put(cls.getIRI(), outCeptSuperClz);
            return outCeptSuperClz;
        }
    } // getSuperClasses

    /***********************************************************************************
     * Get the sub classes of the class cls.
     * 
     * @param cls
     *            the class for searching sub classes.
     * @return Set<OWLClass>: The sub classes of the class cls.
     */
    public Set<OWLClass> getDirectSubClasses(OWLClass cls) {
        Set<OWLClass> subs = new HashSet<OWLClass>();
        if (cls == null) {
            return subs;
        }
        Iterator<OWLOntology> ont = ontologyInstances.values().iterator();
        while (ont.hasNext()) {
            Set<OWLClassExpression> _sub = cls.getSubClasses(ont.next());
            if (_sub != null) {
                for (OWLClassExpression cd : _sub) {
                    if (!cd.isAnonymous()) {
                        subs.add(cd.asOWLClass());
                    }
                } // for 
            }
        }
        return subs;
    } // getDirectSubClasses

    /***********************************************************************************
     * Get the sub classes of the class cls in hierarchy.
     * 
     * @param owlCls
     *            the class for searching sub classes.
     * @return ArrayList<Set<OWLClass>>: The sub classes of the class owlCls.
     */
    public ArrayList<Set<OWLClass>> getSubClasses(OWLClass cls) {
        ArrayList<Set<OWLClass>> outCeptSubClz = new ArrayList<Set<OWLClass>>();
        if (cls == null) {
            return outCeptSubClz;
        }
        if (SubClaz.containsKey(cls.getIRI())) {
            return SubClaz.get(cls.getIRI());
        } else {
            // get every super class of outConceptClass
            Set<OWLClass> temp = getDirectSubClasses(cls);
            while (temp.size() != 0) {
                Set<OWLClass> nextLevel = new HashSet<OWLClass>();
                for (OWLClass owlCls : temp) {
                    Set<OWLClass> temp1 = getDirectSubClasses(owlCls);
                    if (temp1.size() != 0) {
                        Iterator<OWLClass> iterator = temp1.iterator();
                        while (iterator.hasNext()) {
                            nextLevel.add(iterator.next());
                        }
                    }
                }
                outCeptSubClz.add(temp);
                temp = nextLevel;
            }
            SubClaz.put(cls.getIRI(), outCeptSubClz);
            return outCeptSubClz;
        }
    } // getSubClasses

    /***********************************************************************************
     * Get the data properties of the class cls.
     * 
     * @param cls
     *            the class for searching data properties.
     * @return Set<OWLDataProperty>: The data properties of the class cls.
     */
    public Set<OWLDataProperty> getDataProperties(OWLClass cls) {
        HashSet<OWLClass> visited = new HashSet<OWLClass>();
        visited.add(cls);

        Set<OWLDataProperty> props = new HashSet<OWLDataProperty>();
        if (cls == null) {
            return props;
        }
        if (OwlDataProps.containsKey(cls.getIRI())) {
            return OwlDataProps.get(cls.getIRI());
        } else {
            Iterator<OWLOntology> ont = ontologyInstances.values().iterator();
            while (ont.hasNext()) {
                OWLOntology onto = ont.next();

                ArrayList<Set<OWLClass>> superList = getSuperClasses(cls);
                for (OWLDataProperty p : onto.getDataPropertiesInSignature(true)) {
                    if (p.getDomains(onto).contains(cls)) {
                        props.add(p);
                    } // if
                    // get super classes to check OwlDataProperties
                    for (Set<OWLClass> clz : getSuperClasses(cls)) {
                        for (OWLClass c : clz) {
                            if (p.getDomains(onto).contains(c)) {
                                props.add(p);
                            } // if
                            props.addAll(getDataProperties(c));
                        } // for
                    } // for
                } // for

                // get all super classes's data property by OWLRestriction
                for (int i = superList.size() - 1; i >= 0; i--) {
                    for (OWLClass c : superList.get(i)) {
                        if (!visited.contains(c)) {
                            //out.println("start with = " + c);
                            props.addAll(getDataProperties(visited, c));
                        }
                    } // for
                } // for

                // define OWLRestriction class type name
                ArrayList<String> classTypeNames = new ArrayList<String>();
                classTypeNames.add("datamaxcardinality");
                classTypeNames.add("dataexactcardinality");
                classTypeNames.add("datamincardinality");
                classTypeNames.add("dataSomeValuesFrom");
                classTypeNames.add("dataHasValue");
                classTypeNames.add("dataAllValuesFrom");

                // use super restriction to get property
                Set<OWLClassExpression> supers = cls.getSuperClasses(onto);
                props = nestedSeekDataProperty(visited, supers, classTypeNames, cls, props);

                // use equivalent restriction to get property
                Set<OWLClassExpression> equivs = cls.getEquivalentClasses(onto);
                props = nestedSeekDataProperty(visited, equivs, classTypeNames, cls, props);

            } // while ontology
            OwlDataProps.put(cls.getIRI(), props);
            return props;
        }
    } // getDataProperties

    public Set<OWLDataProperty> getDataProperties(HashSet<OWLClass> visited, OWLClass cls) {
        // visited pattern
        visited.add(cls);

        Set<OWLDataProperty> props = new HashSet<OWLDataProperty>();
        if (cls == null) {
            return props;
        }
        //if (OwlObjPropsCache.containsKey(cls.getIRI())){
        //	return OwlObjPropsCache.get(cls.getIRI());
        //}else {
        Iterator<OWLOntology> ont = ontologyInstances.values().iterator();
        while (ont.hasNext()) {
            OWLOntology onto = ont.next();
            //out.println("ont = " + onto);
            ArrayList<Set<OWLClass>> superList = getSuperClasses(cls);
            // check OWLClass hierarchy to get object properties by domain
            for (OWLDataProperty p : onto.getDataPropertiesInSignature(true)) {
                if (p.getDomains(onto).contains(cls)) {
                    //out.println("property:" + p + " in domain of cls = " + cls);
                    props.add(p);
                } // if
                // get super classes to check OwlObjectProperties
                for (int i = superList.size() - 1; i >= 0; i--) {
                    for (OWLClass c : superList.get(i)) {
                        if (p.getDomains(onto).contains(c)) {
                            //out.println("property:" + p + " in domain of super cls = " + c);
                            props.add(p);
                        } // if
                    }
                }
            } // for

            // get all super classes's object property by OWLRestriction
            for (int i = superList.size() - 1; i >= 0; i--) {
                for (OWLClass c : superList.get(i)) {
                    if (!visited.contains(c)) {
                        //out.println("start with = " + c);
                        props.addAll(getDataProperties(visited, c));
                    }
                } // for
            } // for

            // define OWLRestriction class type name
            ArrayList<String> classTypeNames = new ArrayList<String>();
            classTypeNames.add("datamaxcardinality");
            classTypeNames.add("dataexactcardinality");
            classTypeNames.add("datamincardinality");
            classTypeNames.add("dataSomeValuesFrom");
            classTypeNames.add("dataHasValue");
            classTypeNames.add("dataAllValuesFrom");

            // use super restriction to get property
            Set<OWLClassExpression> supers = cls.getSuperClasses(onto);
            props = nestedSeekDataProperty(visited, supers, classTypeNames, cls, props);

            // use equivalent restriction to get property
            Set<OWLClassExpression> equivs = cls.getEquivalentClasses(onto);
            props = nestedSeekDataProperty(visited, equivs, classTypeNames, cls, props);

        } // while ontology
        //}
        //OwlObjPropsCache.put(cls.getIRI(), props);
        return props;

    } // getDataProperties

    @SuppressWarnings("rawtypes")
    public Set<OWLDataProperty> nestedSeekDataProperty(HashSet<OWLClass> visited, Set<OWLClassExpression> parents, ArrayList<String> classTypeNames, OWLClass cls, Set<OWLDataProperty> props) {
        for (OWLClassExpression parentPart : parents) {
            if (parentPart.isAnonymous()) {
                boolean nestedExpression = true;
                ArrayList<OWLClassExpression> parentPartList = new ArrayList<OWLClassExpression>();
                parentPartList.add(parentPart);
                ArrayList<OWLClassExpression> nextParentPartList = new ArrayList<OWLClassExpression>();
                int num = 0;
                while (nestedExpression) {
                    nestedExpression = false;
                    for (OWLClassExpression parentPart1 : parentPartList) {
                        //out.println(num + "parentPart = " + parentPart1);
                        Iterator<OWLClassExpression> childPart = null;
                        if (parentPart1.getClassExpressionType().getName().equalsIgnoreCase("ObjectIntersectionOf")) {
                            childPart = parentPart1.asConjunctSet().iterator();
                        } else if (parentPart1.getClassExpressionType().getName().equalsIgnoreCase("ObjectUnionOf")) {
                            childPart = parentPart1.asDisjunctSet().iterator();
                        } else {
                            childPart = parentPart1.asDisjunctSet().iterator();
                        }
                        while (childPart.hasNext()) {
                            OWLClassExpression _childPart = childPart.next();
                            //out.println(num + "_childPart = " + _childPart);
                            for (int i = 0; i < classTypeNames.size(); i++) {
                                if (!_childPart.isAnonymous()) {
                                    if (!getSuperClasses(cls).contains(_childPart.asOWLClass())) {
                                        if (!visited.contains(_childPart.asOWLClass())) {
                                            //out.println("start with = " + _childPart.asOWLClass());
                                            props.addAll(getDataProperties(visited, _childPart.asOWLClass()));
                                        }
                                    }
                                    break;
                                }
                                if (_childPart.getClassExpressionType().getName().equalsIgnoreCase(classTypeNames.get(i))) {
                                    props.add((OWLDataProperty) ((OWLRestriction) _childPart).getProperty());
                                    //out.println("Add property = " + (OWLDataProperty) ((OWLRestriction) _childPart).getProperty());
                                    // store cardinality
                                    String propIRI = ((OWLDataProperty) ((OWLRestriction) _childPart).getProperty()).toString();
                                    String key = cls.getIRI().toString() + propIRI.substring(1, propIRI.length() - 1);
                                    if (_childPart.getClassExpressionType().getName().equalsIgnoreCase("datamaxcardinality")) {
                                        Cardinality.put(key, ((OWLDataMaxCardinality) _childPart).getCardinality());
                                    } else if (_childPart.getClassExpressionType().getName().equalsIgnoreCase("dataexactcardinality")) {
                                        Cardinality.put(key, ((OWLDataExactCardinality) _childPart).getCardinality());
                                    } else if (_childPart.getClassExpressionType().getName().equalsIgnoreCase("datamincardinality")) {
                                        Cardinality.put(key, ((OWLDataMinCardinality) _childPart).getCardinality());
                                    }
                                    // check nested ObjectIntersectionOf
                                    OWLClassExpression nextParent = seekNestedIntersection(_childPart);
                                    //out.println("nextParent = " + nextParent);
                                    if (nextParent != null) {
                                        nestedExpression = true;
                                        nextParentPartList = new ArrayList<OWLClassExpression>();
                                        nextParentPartList.add(nextParent);
                                    }
                                    break;
                                }
                            } // for
                        } // while
                        num++;
                    }
                    parentPartList = nextParentPartList;
                } // while
            } // if
        }
        return props;
    }

    /***********************************************************************************
     * Get the object properties of the class cls.
     * 
     * @param cls
     *            the class for searching object properties.
     * @return Set<OWLObjectProperty>: The object properties of the class cls.
     */
    public Set<OWLObjectProperty> getObjectProperties(OWLClass cls) {
        HashSet<OWLClass> visited = new HashSet<OWLClass>();
        visited.add(cls);

        Set<OWLObjectProperty> props = new HashSet<OWLObjectProperty>();
        if (cls == null) {
            return props;
        }
        if (OwlObjProps.containsKey(cls.getIRI())) {
            return OwlObjProps.get(cls.getIRI());
        } else {
            Iterator<OWLOntology> ont = ontologyInstances.values().iterator();
            while (ont.hasNext()) {
                OWLOntology onto = ont.next();
                //out.println("ont = " + onto);
                ArrayList<Set<OWLClass>> superList = getSuperClasses(cls);
                // check OWLClass hierarchy to get object properties by domain
                for (OWLObjectProperty p : onto.getObjectPropertiesInSignature(true)) {
                    if (p.getDomains(onto).contains(cls)) {
                        //out.println("property:" + p + " in domain of cls = " + cls);
                        props.add(p);
                    } // if
                    // get super classes to check OwlObjectProperties
                    for (int i = superList.size() - 1; i >= 0; i--) {
                        for (OWLClass c : superList.get(i)) {
                            if (p.getDomains(onto).contains(c)) {
                                //out.println("property:" + p + " in domain of super cls = " + c);
                                props.add(p);
                            } // if
                        }
                    }
                } // for

                // get all super classes's object property by OWLRestriction
                for (int i = superList.size() - 1; i >= 0; i--) {
                    for (OWLClass c : superList.get(i)) {
                        if (!visited.contains(c)) {
                            //out.println("start with = " + c);
                            props.addAll(getObjectProperties(visited, c));
                        }
                    } // for
                } // for


                // define OWLRestriction class type name
                ArrayList<String> classTypeNames = new ArrayList<String>();
                classTypeNames.add("objectmaxcardinality");
                classTypeNames.add("objectexactcardinality");
                classTypeNames.add("objectmincardinality");
                classTypeNames.add("ObjectSomeValuesFrom");
                classTypeNames.add("ObjectHasValue");
                classTypeNames.add("ObjectAllValuesFrom");

                // use super restriction to get property
                Set<OWLClassExpression> supers = cls.getSuperClasses(onto);
                props = nestedSeekObjectProperty(visited, supers, classTypeNames, cls, props);

                // use equivalent restriction to get property
                Set<OWLClassExpression> equivs = cls.getEquivalentClasses(onto);
                props = nestedSeekObjectProperty(visited, equivs, classTypeNames, cls, props);

            } // while ontology
        }
        OwlObjProps.put(cls.getIRI(), props);
        return props;

    } // getObjectProperties

    public Set<OWLObjectProperty> getObjectProperties(HashSet<OWLClass> visited, OWLClass cls) {
        // visited pattern
        visited.add(cls);

        Set<OWLObjectProperty> props = new HashSet<OWLObjectProperty>();
        if (cls == null) {
            return props;
        }
        //if (OwlObjPropsCache.containsKey(cls.getIRI())){
        //	return OwlObjPropsCache.get(cls.getIRI());
        //}else {
        Iterator<OWLOntology> ont = ontologyInstances.values().iterator();
        while (ont.hasNext()) {
            OWLOntology onto = ont.next();
            //out.println("ont = " + onto);
            ArrayList<Set<OWLClass>> superList = getSuperClasses(cls);
            // check OWLClass hierarchy to get object properties by domain
            for (OWLObjectProperty p : onto.getObjectPropertiesInSignature(true)) {
                if (p.getDomains(onto).contains(cls)) {
                    //out.println("property:" + p + " in domain of cls = " + cls);
                    props.add(p);
                } // if
                // get super classes to check OwlObjectProperties
                for (int i = superList.size() - 1; i >= 0; i--) {
                    for (OWLClass c : superList.get(i)) {
                        if (p.getDomains(onto).contains(c)) {
                            //out.println("property:" + p + " in domain of super cls = " + c);
                            props.add(p);
                        } // if
                    }
                }
            } // for

            // get all super classes's object property by OWLRestriction

            for (int i = superList.size() - 1; i >= 0; i--) {
                for (OWLClass c : superList.get(i)) {
                    if (!visited.contains(c)) {
                        //out.println("start with = " + c);
                        props.addAll(getObjectProperties(visited, c));
                    }
                } // for
            } // for

            // define OWLRestriction class type name
            ArrayList<String> classTypeNames = new ArrayList<String>();
            classTypeNames.add("objectmaxcardinality");
            classTypeNames.add("objectexactcardinality");
            classTypeNames.add("objectmincardinality");
            classTypeNames.add("ObjectSomeValuesFrom");
            classTypeNames.add("ObjectHasValue");
            classTypeNames.add("ObjectAllValuesFrom");

            // use super restriction to get property
            Set<OWLClassExpression> supers = cls.getSuperClasses(onto);
            props = nestedSeekObjectProperty(visited, supers, classTypeNames, cls, props);

            // use equivalent restriction to get property
            Set<OWLClassExpression> equivs = cls.getEquivalentClasses(onto);
            props = nestedSeekObjectProperty(visited, equivs, classTypeNames, cls, props);

        } // while ontology
        //}
        //OwlObjPropsCache.put(cls.getIRI(), props);
        return props;

    } // getObjectProperties

    @SuppressWarnings("rawtypes")
    public Set<OWLObjectProperty> nestedSeekObjectProperty(HashSet<OWLClass> visited, Set<OWLClassExpression> parents, ArrayList<String> classTypeNames, OWLClass cls, Set<OWLObjectProperty> props) {
        for (OWLClassExpression parentPart : parents) {
            if (parentPart.isAnonymous()) {
                boolean nestedExpression = true;
                ArrayList<OWLClassExpression> parentPartList = new ArrayList<OWLClassExpression>();
                parentPartList.add(parentPart);
                ArrayList<OWLClassExpression> nextParentPartList = new ArrayList<OWLClassExpression>();
                int num = 0;
                while (nestedExpression) {
                    nestedExpression = false;
                    for (OWLClassExpression parentPart1 : parentPartList) {
                        //out.println("cls = " + cls + ", " + num + "parentPart = " + parentPart1);
                        Iterator<OWLClassExpression> childPart = null;
                        if (parentPart1.getClassExpressionType().getName().equalsIgnoreCase("ObjectIntersectionOf")) {
                            childPart = parentPart1.asConjunctSet().iterator();
                        } else if (parentPart1.getClassExpressionType().getName().equalsIgnoreCase("ObjectUnionOf")) {
                            childPart = parentPart1.asDisjunctSet().iterator();
                        } else {
                            childPart = parentPart1.asDisjunctSet().iterator();
                        }
                        while (childPart.hasNext()) {
                            OWLClassExpression _childPart = childPart.next();
                            //out.println(num + "_childPart = " + _childPart);            	
                            for (int i = 0; i < classTypeNames.size(); i++) {
                                if (!_childPart.isAnonymous()) {
                                    if (!getSuperClasses(cls).contains(_childPart.asOWLClass())) {
                                        if (!visited.contains(_childPart.asOWLClass())) {
                                            //out.println("start with = " + _childPart.asOWLClass());
                                            props.addAll(getObjectProperties(visited, _childPart.asOWLClass()));
                                        }
                                    }
                                    break;
                                }
                                if (_childPart.getClassExpressionType().getName().equalsIgnoreCase(classTypeNames.get(i))) {
                                    props.add((OWLObjectProperty) ((OWLRestriction) _childPart).getProperty());
                                    //out.println("Add property = " + (OWLObjectProperty) ((OWLRestriction) _childPart).getProperty());
                                    // store cardinality
                                    String propIRI = ((OWLObjectProperty) ((OWLRestriction) _childPart).getProperty()).toString();
                                    String key = cls.getIRI().toString() + propIRI.substring(1, propIRI.length() - 1);
                                    if (_childPart.getClassExpressionType().getName().equalsIgnoreCase("objectmaxcardinality")) {
                                        Cardinality.put(key, ((OWLObjectMaxCardinality) _childPart).getCardinality());
                                    } else if (_childPart.getClassExpressionType().getName().equalsIgnoreCase("objectexactcardinality")) {
                                        Cardinality.put(key, ((OWLObjectExactCardinality) _childPart).getCardinality());
                                    } else if (_childPart.getClassExpressionType().getName().equalsIgnoreCase("objectmincardinality")) {
                                        Cardinality.put(key, ((OWLObjectMinCardinality) _childPart).getCardinality());
                                    }
                                    //check nested ObjectIntersectionOf
                                    OWLClassExpression nextParent = seekNestedIntersection(_childPart);
                                    //out.println("nextParent = " + nextParent);
                                    if (nextParent != null) {
                                        nestedExpression = true;
                                        nextParentPartList = new ArrayList<OWLClassExpression>();
                                        nextParentPartList.add(nextParent);
                                    }
                                    break;
                                }
                            } // for
                        } // while
                        num++;
                    }
                    parentPartList = nextParentPartList;
                } // while
            } // if
        } // for
        return props;
    }

    /***********************************************************************************
     * Check if the object properties are inverse functional
     * 
     * @param prop
     *            : the OWLProperty for checking.
     * @return boolean: if the property is inverse functional if it is not an
     *         owl nothing
     */
    @SuppressWarnings("rawtypes")
    public boolean isInverseFunctional(OWLProperty prop) {
        if (prop.isOWLDataProperty()) {
            return false;
        }
        Iterator<OWLOntology> ont = ontologyInstances.values().iterator();
        while (ont.hasNext()) {
            if (prop.asOWLObjectProperty().isInverseFunctional(ont.next())) {
                return true;
            }
        }
        return false;
    }

    /***********************************************************************************
     * Check if the property is functional
     * 
     * @param prop
     *            : the OWLProperty for checking.
     * @return boolean: if the property is functional
     */
    @SuppressWarnings("rawtypes")
    public boolean isFunctional(OWLProperty prop) {
        Iterator<OWLOntology> ont = ontologyInstances.values().iterator();
        while (ont.hasNext()) {
            if (prop.isFunctional(ont.next())) {
                return true;
            }
        }
        return false;
    }

    /***********************************************************************************
     * Combination of both the properties set.
     * 
     * @param cls
     *            the class the class for searching all Owl properties.
     * @return Set<OWLProperty>: The set of the class that include the object
     *         property and data property.
     */
    @SuppressWarnings("rawtypes")
    public Set<OWLProperty> getProperties(OWLClass cls) {
        Set<OWLDataProperty> data_prop = this.getDataProperties(cls);
        Set<OWLObjectProperty> obj_prop = this.getObjectProperties(cls);
        Set<OWLProperty> result_set = new HashSet<OWLProperty>();
        result_set.addAll(obj_prop);
        result_set.addAll(data_prop);
        return result_set;
    } // getProperties

    /***********************************************************************************
     * Get the Cardinality of instance of the class cls.
     * 
     * @param cls
     *            the class for searching cardinality.
     * @return Integer: The exact number(Cardinality) of the class.
     */
    @SuppressWarnings("rawtypes")
    public int getCardinality(OWLProperty prop, OWLClass cls) {
        int val = 0;
        if (cls == null || prop == null) {
            return val;
        }
        getProperties(cls);
        String key = cls.getIRI().toString() + prop.getIRI().toString();
        if (Cardinality.containsKey(key)) {
            return Cardinality.get(key);
        } else {
            return val;
        }
    } // getCardinality

    /***********************************************************************************
     * Print out all of the classes which are referenced in the ontology along
     * with their data and object properties.
     */
    public void printClasses() {

        Iterator<OWLClass> claz = OwlClaz.values().iterator();
        while (claz.hasNext()) {
            out.println(claz.next().getIRI());
        }
        //Node<OWLClass> topNode = reasoner.getTopClassNode();
        //print(topNode, reasoner, 0);

    } // printClasses

    /***********************************************************************************
     * Get the domains of the object property
     * 
     * @param prop
     *            the owl properties that is included by the class.
     * @return Set<OWLClass>: The set of the class that include the object
     *         property.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Set<OWLClass> getDomains(OWLProperty prop) {
        Set<OWLClass> owl_class = new HashSet<OWLClass>();
        if (prop == null) {
            return owl_class;
        }
        Iterator<OWLOntology> ont = ontologyInstances.values().iterator();
        while (ont.hasNext()) {
            Set<OWLClassExpression> owl_desc = prop.getDomains(ont.next());
            for (OWLClassExpression desc : owl_desc) {
                if (!desc.isAnonymous()) {
                    owl_class.add(desc.asOWLClass());
                }
            }
        }
        return owl_class;

    } // getDomains

    /***********************************************************************************
     * Get the range of the object properties
     * 
     * @param prop
     *            the object property that is included by the class.
     * @return Set<OWLClass>: The set of the class that include the object
     *         property.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Set<OWLClass> getRanges(OWLProperty prop) {
        Set<OWLClass> owl_class = new HashSet<OWLClass>();
        if (prop == null) {
            return owl_class;
        }
        Iterator<OWLOntology> ont = ontologyInstances.values().iterator();
        while (ont.hasNext()) {
            Set<OWLPropertyRange> owl_desc = prop.getRanges(ont.next());
            for (OWLPropertyRange desc : owl_desc) {
                
                for(OWLClass c : desc.getClassesInSignature())
                {
                    owl_class.add(c);
                }
                for (OWLDataProperty dprop : desc.getDataPropertiesInSignature()) {
                    owl_class.add(dprop.asOWLClass());
                }
                
                for (OWLObjectProperty oprop : desc.getObjectPropertiesInSignature()) {
                    owl_class.add(oprop.asOWLClass());
                }
                
            }
        }
        return owl_class;
    } // getRanges


    /**
     * Returns the local class name
     * @param cls
     * @return 
     */
    public String getLocalClassName(OWLClass cls) {
        return (cls.getIRI().toString().contains("#") ? cls.getIRI().toString().split("#")[1] : cls.getIRI().toString());
    }

    /**
     * Returns the local property name
     * @param prop
     * @return 
     */
    @SuppressWarnings("rawtypes")
    public String getLocalPropertyName(OWLProperty prop) {
        return (prop.getIRI().toString().contains("#") ? prop.getIRI().toString().split("#")[1] : prop.getIRI().toString());
    }

    /**
     * Returns the local class label
     * @param cls
     * @return String: label
     */
    public String getClassLabel(OWLClass cls) 
    {
        String classLabel = "";
        if (cls == null) return classLabel;
        
        OWLDataFactory df = manager.getOWLDataFactory();
        OWLAnnotationProperty label =  df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
        
        //Itrate over all the ontologies loaded, as the class can be present in an 
        //imported Ontology that has different Name Space
        for (OWLOntology o : OntologyManager.ontologyInstances.values())
            for (OWLAnnotation annotation : cls.getAnnotations(o, label)) 
            {
                if (annotation.getValue() instanceof OWLLiteral) 
                {
                    OWLLiteral val = (OWLLiteral) annotation.getValue();
                    classLabel = val.getLiteral();
                    if(classLabel != null || !classLabel.equals("")) break;
                }//if
            }//for
        
        return classLabel;
    
    } // getClassLabel

    public String getPropertyLabel(OWLProperty p) 
    {

        String propLabel = "";        
        if (p == null) return propLabel;

        OWLDataFactory df = manager.getOWLDataFactory();
        IRI labelIRI = OWLRDFVocabulary.RDFS_LABEL.getIRI();
        OWLAnnotationProperty label =  df.getOWLAnnotationProperty(labelIRI);
        
        for (OWLOntology o : OntologyManager.ontologyInstances.values()) {
            for (OWLAnnotation annotation : p.getAnnotations(o, label)) {
                if (annotation.getValue() instanceof OWLLiteral) {
                    OWLLiteral val = (OWLLiteral) annotation.getValue();
                    propLabel = val.getLiteral();
                }
            }
        }//for
        
        if (propLabel == null || propLabel.equals("")) propLabel = p.getIRI().toString();
        
        return propLabel;
    } // getPropertyLabel
    
    /**
     * Returns the Intersection part of OWLClassExpression
     * @param parent
     * @return OWLClassExpression: result
     */
    public OWLClassExpression seekNestedIntersection(OWLClassExpression parent) {
        OWLClassExpression result = null;
        for (OWLClassExpression childPart : parent.getNestedClassExpressions()) {
            if (childPart.getClassExpressionType().getName().equalsIgnoreCase("ObjectIntersectionOf")
                    || childPart.getClassExpressionType().getName().equalsIgnoreCase("ObjectUnionOf")) {
                result = childPart;
            }
        }
        return result;
    }
    
    public String getConceptName(String iri) {

        if (iri.contains("#")) {
            return iri.split("#")[1];
        } else {
            String[] parts = iri.split("/");
            return parts[parts.length - 1];
        }

    }

    /**
     * Returns the value of the annotation Property 'Definition' for the Given OWL class
     * @param OWLClass
     * @return 
     */
    public String getDefinition(OWLClass cls) {

        String definition = "";
        String annoProp;

        //Reading Annotation Property from the Properties file
        Properties prop = new Properties();
        try{
            prop.load(getClass().getResourceAsStream("ontologySimilarity.properties"));
            annoProp = prop.getProperty("definition");
        }//try
        catch(Exception e)
        {
            System.out.println("Could not load Properties file : "+e);
            annoProp = "http://purl.obolibrary.org/obo/IAO_0000115";
        }//catch
        if (annoProp == null || annoProp.equals(""))
            annoProp = "http://purl.obolibrary.org/obo/IAO_0000115";
        //----------------------------------------------------------------------
        
        OWLDataFactory df = manager.getOWLDataFactory();
        OWLAnnotationProperty label = df.getOWLAnnotationProperty(IRI.create(annoProp));

        for (OWLAnnotation annotation : cls.getAnnotations(ontology, label)) {
            if (annotation.getValue() instanceof OWLLiteral) {
                OWLLiteral val = (OWLLiteral) annotation.getValue();
                definition = val.getLiteral();
            }
        }
        return definition;
    } // getDefinition

        /**
     * Returns the value of the annotation Property 'Definition' for the Given OWL Property
     * @param OWLClass
     * @return 
     */
    public String getDefinition(OWLProperty p) {

        String definition = "";
        String annoProp;

        //Reading Annotation Property from the Properties file
        Properties prop = new Properties();
        try{
            prop.load(getClass().getResourceAsStream("ontologySimilarity.properties"));
            annoProp = prop.getProperty("definition");
        }//try
        catch(Exception e)
        {
            System.out.println("Could not load Properties file : "+e);
            annoProp = "http://purl.obolibrary.org/obo/IAO_0000115";
        }//catch
        if (annoProp == null || annoProp.equals(""))
            annoProp = "http://purl.obolibrary.org/obo/IAO_0000115";
        //----------------------------------------------------------------------
        
        OWLDataFactory df = manager.getOWLDataFactory();
        OWLAnnotationProperty label = df.getOWLAnnotationProperty(IRI.create(annoProp));

        for (OWLOntology o : OntologyManager.ontologyInstances.values()) 
            for (OWLAnnotation annotation : p.getAnnotations(o, label)) 
            {
                if (annotation.getValue() instanceof OWLLiteral) 
                {
                    OWLLiteral val = (OWLLiteral) annotation.getValue();
                    definition = val.getLiteral();
                }
            }
        if (definition == null) definition = "";
        
        return definition;
    } // getDefinition
    
    /**
     * Returns the value of the annotation Property 'Usage' for the Given OWL class
     * 
     * @param OWLCLass
     * @return 
     */
    public String getClassUsage(OWLClass cls) {

        String rval = "";
        String annoProp;

        //Reading Annotation Property from the Properties file
        Properties prop = new Properties();
        try{
            prop.load(getClass().getResourceAsStream("ontologySimilarity.properties"));
            annoProp = prop.getProperty("usage");
        }//try
        catch(Exception e)
        {
            System.out.println("Could not load Properties file : "+e);
            annoProp = "http://purl.obolibrary.org/obo/IAO_0000112";
        }//catch
        if (annoProp == null || annoProp.equals(""))
            annoProp = "http://purl.obolibrary.org/obo/IAO_0000112";
        //----------------------------------------------------------------------

        OWLDataFactory df = manager.getOWLDataFactory();
        OWLAnnotationProperty usage = df.getOWLAnnotationProperty(IRI.create(annoProp));

        for (OWLAnnotation annotation : cls.getAnnotations(ontology, usage)) {
            if (annotation.getValue() instanceof OWLLiteral) {
                OWLLiteral val = (OWLLiteral) annotation.getValue();
                rval = val.getLiteral();
            }
        }
        return rval;
    } // getDefinition
    
        
    /**
     * Returns the value of the annotation Property 'Description' for the Given OWL class
     * 
     * @param OWLCLass
     * @return The Description
     */
    public String getClassDescription(OWLClass cls) {

        String classDescription = "";
        String annoProp;

        //Reading Annotation Property from the Properties file
        Properties prop = new Properties();
        try{
            prop.load(getClass().getResourceAsStream("ontologySimilarity.properties"));
            annoProp = prop.getProperty("description");
        }//try
        catch(Exception e)
        {
            System.out.println("Could not load Properties file : "+e);
            annoProp = "http://purl.org/dc/elements/1.1/description";
        }//catch
        if (annoProp == null || annoProp.equals(""))
            annoProp = "http://purl.org/dc/elements/1.1/description";
        //----------------------------------------------------------------------
        OWLDataFactory df = manager.getOWLDataFactory();
        OWLAnnotationProperty description = df.getOWLAnnotationProperty(IRI.create(annoProp));

        for (OWLAnnotation annotation : cls.getAnnotations(ontology, description)) {
            if (annotation.getValue() instanceof OWLLiteral) {
                OWLLiteral val = (OWLLiteral) annotation.getValue();
                classDescription = val.getLiteral();
            }
        }
        return classDescription;
    } // getDefinition
    
    /***********************************************************************************
     * Main method for testing.
     * 
     * @param args The command-line arguments.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // load owl file
        OntologyManager discoveryMgr = null;
        long start = System.currentTimeMillis();
        try {

            discoveryMgr = new OntologyManager("http://obi-webservice.googlecode.com/svn/trunk/ontology/webService.owl");
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("latency = " + (end - start));
        System.out.println();

        // get concept class by name
        start = System.currentTimeMillis();
        //OWLClass test = discoveryMgr.getConceptClass("http://www.xfront.com/owl/ontologies/camera/#Money");
        //OWLClass test = discoveryMgr.getConceptClass("http://www.co-ode.org/ontologies/pizza/pizza.owl#Food");
        //OWLClass test = discoveryMgr.getConceptClass("http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza");
        //OWLClass test = discoveryMgr.getConceptClass("http://www.co-ode.org/ontologies/pizza/pizza.owl#InterestingPizza");
        //OWLClass test = discoveryMgr.getConceptClass("http://www.co-ode.org/ontologies/pizza/pizza.owl#American");
        //OWLClass test = discoveryMgr.getConceptClass("http://www.co-ode.org/ontologies/pizza/pizza.owl#SpicyPizzaEquivalent");
        //OWLClass test = discoveryMgr.getConceptClass("http://www.co-ode.org/ontologies/pizza/pizza.owl#FishTopping");
        //OWLClass test = discoveryMgr.getConceptClass("http://www.co-ode.org/ontologies/pizza/pizza.owl#HotGreenPepperTopping");
        //OWLClass test = discoveryMgr.getConceptClass("http://www.ifomis.org/bfo/1.1/snap#Continuant");
        //OWLClass test = discoveryMgr.getConceptClass("http://purl.obolibrary.org/obo/OBI_0000011");
        OWLClass test = discoveryMgr.getConceptClass("http://purl.obolibrary.org/obo/OBI_0200036");
        //OWLClass test = discoveryMgr.getConceptClass("http://purl.obolibrary.org/obo/IAO_0000400");
        //OWLClass test1 = discoveryMgr.getConceptClass("http://www.ifomis.org/bfo/1.1/snap#SpatialRegion");


        end = System.currentTimeMillis();
        System.out.println("test class = " + test);
        System.out.println("test class' label = " + discoveryMgr.getClassLabel(test));
        System.out.println("latency = " + (end - start));
        System.out.println();

        // get direct super classes
        start = System.currentTimeMillis();
        Set<OWLClass> Directsupers = discoveryMgr.getDirectSuperClasses(test);
        end = System.currentTimeMillis();
        out.println("Direct supers class size = " + Directsupers.size());
        for (OWLClass cls : Directsupers) {
            System.out.println("   direct super cls = " + cls);
        }
        System.out.println("latency = " + (end - start));
        System.out.println();

        // get super class
        start = System.currentTimeMillis();
        ArrayList<Set<OWLClass>> Supers = discoveryMgr.getSuperClasses(test);
        end = System.currentTimeMillis();
        int num = 0;
        for (Set<OWLClass> clz : Supers) {
            num = num + clz.size();
        } // for
        out.println("Supers class size = " + num);
        for (Set<OWLClass> clz : Supers) {
            for (OWLClass c : clz) {
                out.println("   super class = " + c);
            } // for
        } // for
        System.out.println("latency = " + (end - start));
        System.out.println();

        // get direct sub classes
        start = System.currentTimeMillis();
        Set<OWLClass> directSubs = discoveryMgr.getDirectSubClasses(test);
        end = System.currentTimeMillis();
        out.println("Direct sub class size = " + directSubs.size());
        for (OWLClass cls : directSubs) {
            System.out.println("   direct sub cls = " + cls);
        }
        System.out.println("latency = " + (end - start));
        System.out.println();

        // get sub class
        start = System.currentTimeMillis();
        ArrayList<Set<OWLClass>> Subs = discoveryMgr.getSubClasses(test);
        end = System.currentTimeMillis();
        num = 0;
        for (Set<OWLClass> clz : Subs) {
            num = num + clz.size();
        } // for
        out.println("Sub class size = " + num);
        for (Set<OWLClass> clz : Subs) {
            for (OWLClass c : clz) {
                out.println("   Sub class = " + c);
            } // for
        } // for

        System.out.println("latency = " + (end - start));
        System.out.println();

        // get the equivalent classes
        start = System.currentTimeMillis();
        Set<OWLClass> equivs = discoveryMgr.getEquivalentClasses(test);
        end = System.currentTimeMillis();
        out.println("equivs class size = " + equivs.size());
        for (OWLClass cls : equivs) {
            System.out.println("   equiv cls = " + cls);
        }
        System.out.println("latency = " + (end - start));
        System.out.println();

        // getDataProperties of the class
        start = System.currentTimeMillis();
        Set<OWLDataProperty> dataProperties = discoveryMgr.getDataProperties(test);
        end = System.currentTimeMillis();
        out.println("data properties size = " + dataProperties.size());
        System.out.println("latency = " + (end - start));
        System.out.println();

        // Cardinality
        for (OWLDataProperty dataProp : dataProperties) {
            System.out.println("   DataProperty = " + dataProp);
            // get the getCardinality of the data property and concept class
            out.println("      class cardinality = " + discoveryMgr.getCardinality(dataProp, test));
        }
        System.out.println("latency = " + (end - start));
        System.out.println();


        // getObjectProperties of the class
        start = System.currentTimeMillis();
        Set<OWLObjectProperty> objProperties = discoveryMgr.getObjectProperties(test);
        end = System.currentTimeMillis();
        out.println("object properties size = " + objProperties.size());
        System.out.println("latency = " + (end - start));

        //start = System.currentTimeMillis();
        //Set<OWLObjectProperty> objProperties1 = discoveryMgr.getObjectProperties(test1);
        //end = System.currentTimeMillis();
        //out.println("object properties1 size = " + objProperties1.size());
        //System.out.println("latency = " + (end - start));

        // Cardinality
        start = System.currentTimeMillis();
        for (OWLObjectProperty objProp : objProperties) {
            System.out.println("   ObjectProperty = " + objProp);
            // get the getCardinality of the object property and concept class
            out.println("      class cardinality = " + discoveryMgr.getCardinality(objProp, test));
        }
        end = System.currentTimeMillis();
        System.out.println("latency = " + (end - start));
        System.out.println();

        // getDomains of the class's object properties
        start = System.currentTimeMillis();
        for (OWLObjectProperty objProp : objProperties) {
            System.out.println("ObjectProperty = " + objProp);
            Set<OWLClass> domClazz = discoveryMgr.getDomains(objProp);
            for (OWLClass cls : domClazz) {
                System.out.println("   domain cls = " + cls);
            }
        }
        end = System.currentTimeMillis();
        System.out.println("latency = " + (end - start));
        System.out.println();

        // getRanges of the class's object properties
        start = System.currentTimeMillis();
        for (OWLObjectProperty objProp : objProperties) {
            System.out.println("ObjectProperty = " + objProp);
            Set<OWLClass> ranClazz = discoveryMgr.getRanges(objProp);
            for (OWLClass cls : ranClazz) {
                System.out.println("   range cls = " + cls);
            }
        }
        end = System.currentTimeMillis();
        System.out.println("latency = " + (end - start));
        System.out.println();


        // check the the class's object properties are functional or not
        for (OWLObjectProperty objProp : objProperties) {
            System.out.println("ObjectProperty = " + objProp);
            System.out.println("   is functional ? : "
                    + discoveryMgr.isFunctional(objProp));
        }
        System.out.println();

        // check the the class's object properties are inverse functional or not
        for (OWLObjectProperty objProp : objProperties) {
            System.out.println("ObjectProperty = " + objProp);
            System.out.println("   is inverse functional ? : "
                    + discoveryMgr.isInverseFunctional(objProp));
        }
        System.out.println();

        // print all class
        //discoveryMgr.printClasses();

    } // main
} // OntoLoader

