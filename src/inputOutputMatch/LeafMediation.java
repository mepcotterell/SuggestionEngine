package inputOutputMatch;

//import java.lang.reflect.Array;
import util.NodeType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

//import ontologySimilarity.WBzard;
//import ontologySimilarity.WBzard.Match;

import parser.LeafDmParser;
import parser.MsIODGparser;
import parser.ParseWSDL;
import parser.JdomParser;
import parser.SawsdlParser;
import util.IODG;
import util.WebServiceOpr;
import util.WebServiceOprScore_type;

import org.semanticweb.owlapi.model.*;

/**
 * Currently not used / supported.
 * 
 * @author Rui Wang
 */
public class LeafMediation {
//
//	/**doc/literal
//	 * get the bottom element name of input of given operation
//	 * @param fileName
//	 * @param opName
//	 * @return
//	 */
//	private List<String> getOpInBottomEleNameList(String fileName, String opName){
//		//all bottom level elements name list 
//		//for the (input--->message--->parts--->)element--->sub elements,
//		//if not complex type, no sub element, then add itself
//		List<String> partsEleNameList=null;
//		ParseWSDL wsdlparser=new ParseWSDL();
//		try{
//		partsEleNameList=wsdlparser.getOpInMsElemName(fileName, opName);		
//	}catch(Exception e){
//		e.printStackTrace();
//	}	
//	List<String> bottomElemList=new ArrayList<String>();		
//	for(int i=0;i<partsEleNameList.size();i++){
//	List<String> tempSubelemL=JdomParser.getSubelemList(fileName, partsEleNameList.get(i));
//	if(tempSubelemL!=null){
//		bottomElemList.addAll(tempSubelemL);
//	}
//	else bottomElemList.add(partsEleNameList.get(i));		
//	}	
//	
//	return bottomElemList;
//	}
//	
//	/**doc/literal
//	 * get the bottom element node list of input of given operation
//	 * 
//	 * @param fileName
//	 * @param opName
//	 * @return
//	 */
//	public List<Element> getOpInBottomEleNodeList(String fileName, String opName){
//		//all bottom level elements name list 
//		//for the (input--->message--->parts--->)element--->sub elements,
//		//if not complex type, no sub element, then add itself
//		List<String> partsEleNameList=null;
//		ParseWSDL wsdlparser=new ParseWSDL();
//		try{
//		partsEleNameList=wsdlparser.getOpInMsElemName(fileName, opName);		
//	}catch(Exception e){
//		e.printStackTrace();
//	}		
//	List<Element> bottomElemList=new ArrayList<Element>();		
//	if (partsEleNameList==null){
//		SawsdlParser sp =new SawsdlParser();
//		Element msElem = sp.getOutMsElem(fileName, opName);
//		bottomElemList.add(msElem);
//	}else{
//	for(int i=0;i<partsEleNameList.size();i++){
//	List<Element> tempSubelemL=JdomParser.getSubelemNodeList(fileName, partsEleNameList.get(i));
//	if(tempSubelemL!=null){
//		bottomElemList.addAll(tempSubelemL);
//	}
//	else System.out.println("Mediation.java--getOpInBottomEleNodeList warning:no element name="+partsEleNameList.get(i));		
//	}	
//	}
//	return bottomElemList;
//	}
//	
///**
// * get the bottom element name of output of given operation
// * @param fileName
// * @param opName
// * @return
// */
//private List<String> getOpOutBottomEleNameList(String fileName, String opName){
//	//all bottom level elements name list 
//	//for the (output--->message--->parts--->)element--->sub elements,
//	//if not complex type, no sub element, then add itself
//		List<String> partsEleNameList=null;
//		ParseWSDL wsdlparser=new ParseWSDL();
//		try{
//		partsEleNameList=wsdlparser.getOpOutMsElemName(fileName, opName);		
//	}catch(Exception e){
//		e.printStackTrace();
//	}	
//	List<String> bottomElemList=new ArrayList<String>();		
//	for(int i=0;i<partsEleNameList.size();i++){
//	List<String> tempSubelemL=JdomParser.getSubelemList(fileName, partsEleNameList.get(i));
//	
//	if(tempSubelemL!=null){
//		bottomElemList.addAll(tempSubelemL);
//	}
//	else bottomElemList.add(partsEleNameList.get(i));		
//	}	
//	
//	return bottomElemList;
//	}
//	
///**
// * get the bottom element node list of output of given operation
// * @param fileName
// * @param opName
// * @return
// */
//public List<Element> getOpOutBottomEleNodeList(String fileName, String opName){
//	//all bottom level elements name list 
//	//for the (output--->message--->parts--->)element--->sub elements,
//	//if not complex type, no sub element, then add itself
//		List<String> partsEleNameList=null;
//		ParseWSDL wsdlparser=new ParseWSDL();
//		try{
//		partsEleNameList=wsdlparser.getOpOutMsElemName(fileName, opName);		
//	}catch(Exception e){
//		e.printStackTrace();
//	}	
//	List<Element> bottomElemList=new ArrayList<Element>();	
//	if (partsEleNameList==null){
//		SawsdlParser sp =new SawsdlParser();
//		Element msElem = sp.getOutMsElem(fileName, opName);
//		bottomElemList.add(msElem);
//	}else{
//	for(int i=0;i<partsEleNameList.size();i++){
//	List<Element> tempSubelemL=JdomParser.getSubelemNodeList(fileName, partsEleNameList.get(i));
//	
//	if(tempSubelemL!=null){
//		bottomElemList.addAll(tempSubelemL);
//	}
//	else System.out.println("Mediation.java--getOpOutBottomEleNodeList warning:no element name="+partsEleNameList.get(i));		
//	}	
//	}
//	return bottomElemList;
//	}
//
///**no
// * get input bottom element-Xpath map of given operation in given wsdl file
// * @param fileName
// * @param opName
// * @return
// */
//private Map<String, String> getOpInBottomEleNameXpathMap(String fileName, String opName){
//	//all bottom level elements name list 
//	//for the (input--->message--->parts--->)element--->sub elements,
//	//if not complex type, no sub element, then add itself
//	List<String> partsEleNameList=null;
//	ParseWSDL wsdlparser=new ParseWSDL();
//	try{
//	partsEleNameList=wsdlparser.getOpInMsElemName(fileName, opName);		
//}catch(Exception e){
//	e.printStackTrace();
//}	
//
//Map<String, String> bottomElemXpathMap=new HashMap<String, String>();		
//for(int i=0;i<partsEleNameList.size();i++){
//	Map<String, String> tempSubelemXpathMap=JdomParser.getSubelemXpathMap(fileName, partsEleNameList.get(i));
//
//if(tempSubelemXpathMap!=null){
//	bottomElemXpathMap.putAll(tempSubelemXpathMap);
//}
//else bottomElemXpathMap.put(partsEleNameList.get(i),"/");		
//}	
//
//return bottomElemXpathMap;
//}
//
///**
// * get input bottom elementNOde-Xpath map of given operation in given wsdl file
// * @param fileName
// * @param opName
// * @return
// */
//public Map<Element, String> getOpInBottomEleNodeXpathMap(String fileName, String opName){
//	//all bottom level elements name list 
//	//for the (input--->message--->parts--->)element--->sub elements,
//	//if not complex type, no sub element, then add itself
//	List<String> partsEleNameList=null;
//	ParseWSDL wsdlparser=new ParseWSDL();
//	try{
//	partsEleNameList=wsdlparser.getOpInMsElemName(fileName, opName);		
//}catch(Exception e){
//	e.printStackTrace();
//}	
//
//Map<Element, String> bottomElemXpathMap=new HashMap<Element, String>();		
//for(int i=0;i<partsEleNameList.size();i++){
//	Map<Element, String> tempSubelemXpathMap=JdomParser.getSubelemNodeXpathMap(fileName, partsEleNameList.get(i));
//
//if(tempSubelemXpathMap!=null){
//	bottomElemXpathMap.putAll(tempSubelemXpathMap);
//}
//else System.out.println("Mediation.java--getOpInBottomEleNodeXpathMap warning:no element name="+partsEleNameList.get(i));		
//		
//}	
//
//return bottomElemXpathMap;
//}
//
//
///**
// * get output bottom element-Xpath map of given operation in given wsdl file
// * @param fileName
// * @param opName
// * @return
// */
//private Map<String, String> getOpOutBottomEleNameXpathMap(String fileName, String opName){
////all bottom level elements name list 
////for the (output--->message--->parts--->)element--->sub elements,
////if not complex type, no sub element, then add itself
//	List<String> partsEleNameList=null;
//	ParseWSDL wsdlparser=new ParseWSDL();
//	try{
//	partsEleNameList=wsdlparser.getOpOutMsElemName(fileName, opName);		
//}catch(Exception e){
//	e.printStackTrace();
//}	
//
//Map<String, String> bottomElemXpathMap=new HashMap<String, String>();		
//for(int i=0;i<partsEleNameList.size();i++){
//	Map<String, String> tempSubelemXpathMap=JdomParser.getSubelemXpathMap(fileName, partsEleNameList.get(i));
//
//if(tempSubelemXpathMap!=null){
//	bottomElemXpathMap.putAll(tempSubelemXpathMap);
//}
//else bottomElemXpathMap.put(partsEleNameList.get(i),"/");		
//}	
//
//return bottomElemXpathMap;
//}
//
///**
// * get output bottom elementNode-Xpath map of given operation in given wsdl file
// * @param fileName
// * @param opName
// * @return
// */
//public Map<Element, String> getOpOutBottomEleNodeXpathMap(String fileName, String opName){
////all bottom level elements name list 
////for the (output--->message--->parts--->)element--->sub elements,
////if not complex type, no sub element, then add itself
//	List<String> partsEleNameList=null;
//	ParseWSDL wsdlparser=new ParseWSDL();
//	try{
//	partsEleNameList=wsdlparser.getOpOutMsElemName(fileName, opName);		
//}catch(Exception e){
//	e.printStackTrace();
//}	
//
//Map<Element, String> bottomElemXpathMap=new HashMap<Element, String>();		
//for(int i=0;i<partsEleNameList.size();i++){
//	Map<Element, String> tempSubelemXpathMap=JdomParser.getSubelemNodeXpathMap(fileName, partsEleNameList.get(i));
//
//if(tempSubelemXpathMap!=null){
//	bottomElemXpathMap.putAll(tempSubelemXpathMap);
//}
//else System.out.println("Mediation.java--getOpOutBottomEleNodeXpathMap warning:no element name="+partsEleNameList.get(i));		
//}	
//
//return bottomElemXpathMap;
//}
//
//	/**
//	 * given file name, element name, retrive back its modelReference concept
//	 * if not exist, return null;
//	 * @param fileName
//	 * @param eleName
//	 * @return
//	 */
//	public static String getElementConcept(String fileName, String eleName){
//		Element schemaEle=JdomParser.getSchemaElem(fileName);
//		String preXsdNs=schemaEle.getNamespacePrefix();	
//		String concept=null;			
//			//System.out.println("prefix--"+preXsdNs);
//			try{
//			XPath xpath=XPath.newInstance("//"+preXsdNs+":schema/descendant::"+preXsdNs+":element[@name=\""+eleName+"\"]");
//			//XPath xpath=XPath.newInstance("//xsd:schema/descendant::xsd:element[@name=\"symbol\"]");
//			Element elem=(Element)(xpath.selectSingleNode(schemaEle));
//			if (elem!=null){
//				String temp=elem.getAttributeValue("modelReference", JdomParser.sawsdlNS);
//				//System.out.println(elem.getAttributeValue("name"));
//				//System.out.println(temp);
//				if(temp!=null){
//			concept = temp.split("#")[1];
//			//System.out.println("schema11---"+elem.getAttributeValue("modelReference", sawsdlNS));
//			//System.out.println("schema---"+schemaEle.getChild("element", xsdNS).getChild("complexType", xsdNS).getChild("sequence", xsdNS).getChild("element", xsdNS).getAttributeValue("modelReference", sawsdlNS));
//				}
//				}
//			}catch(JDOMException e){
//			e.printStackTrace();
//		}
//		
//		return concept;
//	}
//
//	/**yes
//	 * get the concept annotation of the given element node
//	 * @param node
//	 * @return
//	 */
//	public String getNodeConcept(Element node){
//		String concept=null;
//		if (node!=null){
//			String temp=node.getAttributeValue("modelReference", JdomParser.sawsdlNS);
//			//System.out.println(elem.getAttributeValue("name"));
//			//System.out.println(temp);
//			if(temp!=null){
//				concept = temp.split("#")[1];
//		//System.out.println("schema11---"+elem.getAttributeValue("modelReference", sawsdlNS));
//		//System.out.println("schema---"+schemaEle.getChild("element", xsdNS).getChild("complexType", xsdNS).getChild("sequence", xsdNS).getChild("element", xsdNS).getAttributeValue("modelReference", sawsdlNS));
//			}
//			else System.out.println("Mediation.java--getNodeConcept:no annotation in the node="+node.getAttributeValue("name"));
//			}
//		else System.out.println("Mediation.java--getNodeConcept: empty node");
//		return concept;
//	}
//	
//	/**no
//	 * operation-->input--->message--->parts--->element--->sub elements-->annotation concepts
//	 * if not complex type (no sub element), then find annotation of element itself
//	 *  if no annotation of the element, then <element, null>
//	 *  
//	 * @param fileName
//	 * @param opName
//	 * @return  Map<element,concept>
//	 */
//	private Map<String, String> getInputBottomEleConceptMap(String fileName, String opName){
//		List<String> inBottomEleList=new ArrayList<String>();
////		ParseWSDL wsdlparser=new ParseWSDL();	
//			inBottomEleList=getOpInBottomEleNameList(fileName, opName);		
//	
//	Map<String, String> eleConceptMap=getBottomEleConceptMap(fileName,inBottomEleList);	
//	return eleConceptMap;
//	}
//	
//
//	/**no
//	 * given list of element name of all parts
//	 * element--->sub elements-->annotation concepts
//	 * if not complex type (no sub element), then find annotation of element itself
//	 * if no annotation of the element, then <element, null>
//	 * 
//	 * @param fileName
//	 * @param partsEleNameList
//	 * @return   Map<element,concept>
//	 */
//	private Map<String, String> getBottomEleConceptMap(String fileName, List<String> bottomElemList){
//
//	Map<String, String> bottomEleConceptMap=new HashMap<String, String>();	
//	
//	for(int i=0;i<bottomElemList.size();i++){
//		String concept=getElementConcept(fileName,bottomElemList.get(i));
//		if(concept==null){
//			System.out.println("Mediation.java:the element--"+bottomElemList.get(i)+" has no annotation");
//						
//		}
//		bottomEleConceptMap.put(bottomElemList.get(i),concept);
//	}
//	return bottomEleConceptMap;
//	}
//	
//	/**
//	 * operation-->output--->message--->parts--->element--->sub elements-->annotation concepts
//	 * if not complex type (no sub element), then find annotation of element itself
//	 * 
//	 * @param fileName
//	 * @param opName
//	 * @return  Map<element,concept>
//	 */
//	private Map<String, String> getOutputBottomEleConceptMap(String fileName, String opName){
//		List<String> outBottomEleList=null;
////		ParseWSDL wsdlparser=new ParseWSDL();
//	
//			outBottomEleList=getOpOutBottomEleNameList(fileName, opName);		
//		
//	Map<String, String> eleConceptMap=getBottomEleConceptMap(fileName,outBottomEleList);	
//	
//	
//	return eleConceptMap;
//	}
//	
//	
//	
//	/**no
//	 * given annotated two sawsdl files
//	 * find the input output elements of fileA which matche input elements of fileB
//	 * return matched elements map<fileBinputElement, matchedfileAoutputElement>
//	 * 
//	 * @param ontoFilename
//	 * @param fileA
//	 * @param opA
//	 * @param fileB
//	 * @param opB
//	 * @return
//	 */
//	private Map<String, String> getMatchedOp1outputToOp2inputMap(String ontoFilename,String fileA, String opA, String fileB, String opB){
//		Map<String, String> outputMap=getOutputBottomEleConceptMap(fileA, opA);
//		//System.out.println(outputMap);
//		Map<String, String> inputMap=getInputBottomEleConceptMap(fileB, opB);
//		//System.out.println(inputMap);
//		
//		Object[] arrayA=outputMap.keySet().toArray();
//		Object[] arrayB=inputMap.keySet().toArray();
//		
//		Map<String, String> matchedEleMap=new HashMap<String, String>();
//		
//		
//		for(int i=0;i<arrayB.length;i++){
//			String tempInconcept=inputMap.get(arrayB[i]);
//			//System.out.println(i+" input "+arrayB[i]+" annotation="+tempInconcept);
//			if(tempInconcept==null){
//				
//				continue;
//			}
//			//System.out.println("#fileA output="+arrayA.length);
//			for(int j=0;j<arrayA.length;j++){				
//				String tempOutconcept=outputMap.get(arrayA[j].toString());
//				//System.out.println(j+" output "+arrayA[j]+" annotation="+tempOutconcept);
//				if(tempOutconcept==null){
//					continue;
//				}
//				WBzard ontology=new WBzard(ontoFilename);
//				if(tempOutconcept.equalsIgnoreCase(tempInconcept)){
//					//System.out.println("matched concepts--"+tempOutconcept+"=="+tempInconcept);
//					matchedEleMap.put(arrayB[i].toString(), arrayA[j].toString());
//					//System.out.println(matchedEleMap);
//					continue;
//				}
//				Match tempResult=ontology.compare2concepts(tempOutconcept, tempInconcept);
//				//System.out.println(arrayB[i].toString()+" vs "+arrayA[j].toString()+"="+tempResult);
//				if(tempResult==null){
//					
//					continue;
//				}
//				if(tempResult.equals(Match.SAMECLASS)||tempResult.equals(Match.EQUIVALENTCLASS)){
//					matchedEleMap.put(arrayB[i].toString(), arrayA[j].toString());
//				}
//			}
//			
//		}
//		
//		//System.out.println(matchedEleMap);
//			return matchedEleMap;	
//	}
//	
//	/**
//	 * get the matched xpath map<inputXpathOffileB, outputXpathOffileA>
//	 * if no match then <fileBinputXpath, null>
//	 * 
//	 * @param ontoFilename
//	 * @param fileA
//	 * @param opA
//	 * @param fileB
//	 * @param opB
//	 * @return
//	 */
//	public Map<String, String> matchedInputOutputXpathMap(String ontoFilename,String fileA, String opA, String fileB, String opB){
//		
//		//input of file B
//		Map<Element, String> inputNodeXpathMap=this.getOpInBottomEleNodeXpathMap(fileB, opB);
//		//output of file A
//		Map<Element, String> outputNodeXpathMap=this.getOpOutBottomEleNodeXpathMap(fileA, opA);
//		//result xpath map<inputXpathOfA, outputXpathOfB>
//		Map<String, String> matchedXpathMap=new HashMap<String, String>();
//		//input/output element nodes array
//		Element[] inputNodes=inputNodeXpathMap.keySet().toArray(new Element[0]);
//		Element[] outputNodes=outputNodeXpathMap.keySet().toArray(new Element[0]);
//		
//		WBzard ontology=new WBzard(ontoFilename);
//		
//		//match process
//		for(int i=0;i<inputNodes.length;i++){			
//			Element tempInputEle=inputNodes[i];		
//			String tempInputXpath=inputNodeXpathMap.get(tempInputEle);
//			//if no match then <inputXpath, null>
//			matchedXpathMap.put(tempInputXpath, null);	
//			String tempInputConcept=this.getNodeConcept(tempInputEle);
//			if(tempInputConcept==null){
//				
//				continue;
//			}			
//			for(int j=0;j<outputNodes.length;j++){	
//				Element tempOutputEle=outputNodes[j];				
//				String tempOutputConcept=this.getNodeConcept(tempOutputEle);
//				if(tempOutputConcept==null){					
//					continue;
//				}
//				String tempOutputXpath=outputNodeXpathMap.get(tempOutputEle);
//				if(tempOutputConcept.equalsIgnoreCase(tempInputConcept)){
//					matchedXpathMap.put(tempInputXpath, tempOutputXpath);
//					continue;
//				}
//				Match tempResult=ontology.compare2concepts(tempOutputConcept, tempInputConcept);
//				if(tempResult==null){					
//					continue;
//				}
//				if(tempResult.equals(Match.SAMECLASS)||tempResult.equals(Match.EQUIVALENTCLASS)||tempResult.equals(Match.SUPERCLASS)){
//					matchedXpathMap.put(tempInputXpath, tempOutputXpath);
//				}
//			}
//			
//		}
//		return matchedXpathMap;
//	}
//	
//	/**no
//	 * given annotated two sawsdl files
//	 * find the input output elements of fileA which matche input elements of fileB
//	 * return matched elements xpath map<fileBinputElementXpath, matchedfileAoutputElementXpath>
//	 * 
//	 * @param ontoFilename
//	 * @param fileA
//	 * @param opA
//	 * @param fileB
//	 * @param opB
//	 * @return
//	 */
//	private Map<String, String> getMatchedXpathMap(String ontoFilename,String fileA, String opA, String fileB, String opB){
//	Map<String, String> matchedEleMap=this.getMatchedOp1outputToOp2inputMap(ontoFilename, fileA, opA, fileB, opB);
//	Map<String, String> outputEleXpathMap=this.getOpOutBottomEleNameXpathMap(fileA, opA);
//	Map<String, String> inputEleXpathMap=this.getOpInBottomEleNameXpathMap(fileB, opB);
//	Map<String, String> matchedXpathMap=new HashMap<String, String>();
//	Iterator<String> itMatch=matchedEleMap.keySet().iterator();
//	//System.out.println(matchedEleMap);
//	//System.out.println(outputEleXpathMap);
//	//System.out.println(inputEleXpathMap);
//	while(itMatch.hasNext()){
//		String tempInName=itMatch.next();
//		String tempOutName=matchedEleMap.get(tempInName);
//		String tempOutXpath=outputEleXpathMap.get(tempOutName);
//		String tempInXpath=inputEleXpathMap.get(tempInName);
//		matchedXpathMap.put(tempInXpath,tempOutXpath);
//		//System.out.println(tempOutName+"--"+tempInName);
//		//System.out.println(tempOutXpath+"--"+tempInXpath);
//	}
//	return matchedXpathMap;
//	}
//	
//	/**
//	 * given annotated two sawsdl files
//	 * find the missed input elements of fileB which has no matched elements to input of fileA
//	 * return list of missed input element names of fileB
//	 * 
//	 * @param ontoFilename
//	 * @param fileA
//	 * @param opA
//	 * @param fileB
//	 * @param opB
//	 * @return
//	 */
//	public List<String> getMissedEleList(String ontoFilename,String fileA, String opA, String fileB, String opB){
//		List<String> missedEleList=new ArrayList<String>();
//		//ParseWSDL wsdlparser=new ParseWSDL();
//		List<String> inBottomEleList=getOpInBottomEleNameList(fileB, opB);
//		missedEleList.addAll(inBottomEleList);
//		
//		Map<String, String> matchedEleMap=getMatchedOp1outputToOp2inputMap(ontoFilename,fileA,opA, fileB, opB);
//		missedEleList.removeAll(matchedEleMap.keySet());
//		
//		return missedEleList;
//	}
//	
//	/**
//	 * given annotated two sawsdl files
//	 * find the missed input elements of fileB which has no matched elements to input of fileA
//	 * return the missed element-xpath map 
//	 * @param ontoFilename
//	 * @param fileA
//	 * @param opA
//	 * @param fileB
//	 * @param opB
//	 * @return
//	 */
//	public Map<String, String> getMissedEleXpathMap(String ontoFilename,String fileA, String opA, String fileB, String opB){
//		List<String> missedEleList=this.getMissedEleList(ontoFilename, fileA, opA, fileB, opB);
//		Map<String, String> inputEleXpathMap=this.getOpInBottomEleNameXpathMap(fileB, opB);
//		Map<String, String> missedEleXpathMap=new HashMap<String, String>();
//		for(int i=0;i<missedEleList.size();i++){
//			String tempMissedEle=missedEleList.get(i);
//			missedEleXpathMap.put(tempMissedEle, inputEleXpathMap.get(tempMissedEle));
//		}
//		return missedEleXpathMap;
//	}
//	
//
//	/**given workflow operation, and candidate operation
//	 * compare the output of the last operation in the workflow with the input of the candidate operation
//	 * return a map[node&scoreOfinput, node&scoreOfoutput]
//	 * to reuse the WebServiceOprScore_type, node is presented as a list(path) with one node
//	 * @param workflowOPs
//	 * @param candidateOP
//	 * @param owlFileName
//	 * @return
//	 */
//	public Map<WebServiceOprScore_type, WebServiceOprScore_type> dm(List<WebServiceOpr> workflowOPs, WebServiceOpr candidateOP, String owlURI){
//		if(workflowOPs == null || candidateOP == null){
//			System.out.println("LeafMediation.dm: warning: given empty workflow or candidiate operation");
//			return null;
//		}
//		
//		WebServiceOpr lastOp = workflowOPs.get(workflowOPs.size()-1);
////		List<Element>  lastopNodeList= this.getOpOutBottomEleNodeList(lastOp.getWsdlName(), lastOp.getOpName());
////		List<Element> candidateNodeList = this.getOpInBottomEleNodeList(candidateOP.getWsdlName(), candidateOP.getOpName());
//		
//		LeafDmParser ldp = new LeafDmParser();
//		List<Element>  lastopNodeList= ldp.getOutBottomElemList(lastOp.getWsDescriptionDoc(), lastOp.getOperationName());
//		List<Element> candidateNodeList = ldp.getInBottomElemList(candidateOP.getWsDescriptionDoc(), candidateOP.getOperationName());
//		if (lastopNodeList==null ||lastopNodeList.isEmpty() ||candidateNodeList==null|| candidateNodeList.isEmpty()){
//			System.out.println("LeafMediation.dm: warning: given operation has no input or output");
//			return null;
//		}
//		
//		Map<WebServiceOprScore_type, WebServiceOprScore_type> inMatchMap = new HashMap<WebServiceOprScore_type, WebServiceOprScore_type>();
//		PathRank pk = new PathRank();		
//		for (Element inNode: candidateNodeList){			
//			List<WebServiceOprScore_type> outList = new ArrayList<WebServiceOprScore_type>();
//			for(Element outNode:lastopNodeList){
//				double s = pk.compare2node(outNode, inNode, owlURI, NodeType.LEAF_NODE);
//				//a singlton path with only one node, to reuse the WebServiceOprScore_type
//				List<Element> nodePath = new ArrayList<Element>();
//				nodePath.add(outNode);
//				WebServiceOprScore_type outNodeScore = new WebServiceOprScore_type(lastOp.getWsDescriptionDoc(), lastOp.getOperationName(),nodePath, false);
//				outNodeScore.setScore(s);
//				outList.add(outNodeScore);
//			}
//			WebServiceOprScore_type match = Collections.max(outList);
//			//a singlton path with only one node, to reuse the WebServiceOprScore_type
//			List<Element> inNodePath = new ArrayList<Element>();
//			inNodePath.add(inNode);
//			WebServiceOprScore_type inNodeScore = new WebServiceOprScore_type(candidateOP.getWsDescriptionDoc(), candidateOP.getOperationName(),inNodePath, true);
//			inNodeScore.setScore(match.getScore());
//			inMatchMap.put(inNodeScore, match);
//		}
//		return inMatchMap;
//		
//	}
//	
//	public LeafMediation() {
//		
//	}
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		LeafMediation test=new LeafMediation();
//		/*Map temp=test.getOutputBottomEleConceptMap("CompanyInfo.wsdl", "getInfo");
//		Map tempIn=test.getInputBottomEleConceptMap("CompanyInfo.wsdl", "getInfo");
//		System.out.println("input bottom element="+tempIn);
//		System.out.println("output bottom element="+temp);*/
//		/*
//		Map matchedEleMap=test.getMatchedOp1outputToOp2inputMap("LSDIS_Finance.owl", "CompanyInfo.wsdl", "getInfo", "stockquote.wsdl", "GetQuote");
//		List missedEle=test.getMissedEleList("LSDIS_Finance.owl", "CompanyInfo.wsdl", "getInfo", "stockquote.wsdl", "GetQuote");
//		System.out.println("matched elements: "+matchedEleMap);
//		System.out.println("missed input elements="+missedEle);*/
//		
//		//Map matchedXpathMap=test.getMatchedXpathMap("LSDIS_Finance.owl", "CompanyInfo.wsdl", "getInfo", "stockquote.wsdl", "GetQuote");
//		//Map missedEleXpathMap=test.getMissedEleXpathMap("LSDIS_Finance.owl", "CompanyInfo.wsdl", "getInfo", "stockquote.wsdl", "GetQuote");
//		/*Map matchedXpathMap=test.getMatchedXpathMap("so.owl", "NewWuBlast.wsdl","wuBlast", "NewGeneByLocation.wsdl", "invoke");
//		Map missedEleXpathMap=test.getMissedEleXpathMap("so.owl", "NewWuBlast.wsdl","wuBlast", "NewGeneByLocation.wsdl", "invoke");
//		System.out.println("matched element fileBInputxpath--fileAOutputxpath: "+matchedXpathMap);
//		System.out.println("missed input elements--xpath="+missedEleXpathMap);
//		*/
////		Map<String, String> matchXpathMap=test.matchedInputOutputXpathMap("owl/obi.owl", "wsdl/8/WSWuBlast.wsdl","runWUBlast", "wsdl/8/WSWuBlast.wsdl", "getIds");
////		System.out.println("matched element fileBInputxpath--fileAOutputxpath: " + matchXpathMap);
//		
//		
//		
//	}

}
