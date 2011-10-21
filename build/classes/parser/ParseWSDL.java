/**
 * 
 */
package parser;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.*;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;



import parser.JdomParser;


/**
 * 
 *
 */
public class ParseWSDL {
	
	/**
	 * return definition part of the wsdl file
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	private Definition getWsdlDef(String fileName) throws Exception{
		String fileUrl =Thread.currentThread().getContextClassLoader().getResource(fileName).toString();
//		String fileUrl=ClassLoader.getSystemResource(fileName).toString();
//		String fileUrl=fileName;
		
		WSDLFactory factory= WSDLFactory.newInstance();
		WSDLReader reader=factory.newWSDLReader();
		Definition def=reader.readWSDL(fileUrl); 
		//System.out.println("Definition--------"+def);		
		return def;
	}
	
	/**
	 * given operation name, and definition part of wsdl file
	 * get Operation
	 * @param def
	 * @param opName
	 * @return
	 */
	private Operation getDefOp(Definition def, String opName){
		Map<?, PortType> portTypes=def.getPortTypes();
		//one portType in one wsdl
		PortType pt=portTypes.get(portTypes.keySet().iterator().next());
		Operation op=pt.getOperation(opName,null,null);
		return op;
	}
	
	/**
	 * get all operation names of given wsdl file
	 * @param fileName
	 * @return
	 */
	public List<String> getAllOpName(String fileName){
		List<String> opNameList=new ArrayList<String>();
		
		Definition df=null;
		try {
			df = this.getWsdlDef(fileName);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		Map portTypes=df.getPortTypes();
		//one portType in one wsdl
		PortType pt=(PortType)portTypes.get(portTypes.keySet().iterator().next());
		List<Operation> opList=pt.getOperations();
		//System.out.println(opList);
		for(int i=0;i<opList.size();i++){
			//System.out.println(opList.get(i).getName());
			opNameList.add(opList.get(i).getName());			
		}		
		return opNameList;
	}
	
	/**only work for doc/literal
	 * Given message , get the list of element name of all parts of the message
	 * @param ms
	 * @return
	 */
	private List<String> getMsElemName(Message ms){
		Map<?, Part> partsMap=ms.getParts();
		if(partsMap==null){
			return null;
		}
		Iterator<?> it=partsMap.keySet().iterator();
		List<String> msEleNameList=new ArrayList<String>();
		while(it.hasNext()){
			Part pa= partsMap.get(it.next());
//			System.out.println(pa.getElementName());
//			String temp=pa.getName();
			String temp=pa.getElementName().getLocalPart();
			msEleNameList.add(temp);
		}
		//System.out.println(temp);
		return msEleNameList;
	}
	
	/**
	 * filename-->message-->part--->element namespace uri
	 * @param fileName
	 * @return
	 */
	public String getMsEleNSUri(String fileName){
		
		Definition df=null;
		try {
			df = this.getWsdlDef(fileName);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		Map messageMap=df.getMessages();
		Message tempMs=(Message)messageMap.get(messageMap.keySet().iterator().next());
		Map partsMap=tempMs.getParts();
		if(partsMap==null){
			return null;
		}
		Iterator it=partsMap.keySet().iterator();
		Part pa=(Part)partsMap.get(it.next());
		String NSuri=pa.getElementName().getNamespaceURI();
		return NSuri;
	}
	/**
	 * get name list of all parts of given message
	 * @param ms
	 * @return
	 */
	private List<String> getMsPartNameList(Message ms){
		Map partsMap=ms.getParts();
		Iterator it=partsMap.keySet().iterator();
		List<String> msPartNameList=new ArrayList<String>();
		while(it.hasNext()){
			Part pa=(Part)partsMap.get(it.next());
		String temp=pa.getName();
		msPartNameList.add(temp);
		}
		//System.out.println(temp);
		return msPartNameList;
	}
	
	
	
	private Message getOpOutMessage(Operation op){
		
		Message ms=op.getOutput().getMessage();
		//String temp=ms.toString();//many portTypes??
		//System.out.println(temp);
		return ms;
		
	}	
	
	private Message getOpInMessage(Operation op){
		
		Message ms=op.getInput().getMessage();
		//String temp=ms.toString();//many portTypes??
		//System.out.println(temp);
		return ms;
	}
	
	/**
	 * given operation name, get a list of the element name of all parts of 
	 * message of input of the operation
	 * 
	 * @param fileName
	 * @param opName
	 * @return
	 * @throws Exception
	 */
	public List<String> getOpInMsElemName(String fileName, String opName)throws Exception{
		
		Definition df=this.getWsdlDef(fileName);
		return getMsElemName(this.getDefOp(df,opName).getInput().getMessage());
	}
	
	/**
	 * get the name list of all parts of input of given operation
	 * @param fileName
	 * @param opName
	 * @return
	 * @throws Exception
	 */
	public List<String> getOpInMsPartNameList(String fileName, String opName)throws Exception{
		
		Definition df=this.getWsdlDef(fileName);
		return getMsPartNameList(this.getDefOp(df,opName).getInput().getMessage());
	}
	
	/**
	 * get the name list of all parats of output of given operation
	 * @param fileName
	 * @param opName
	 * @return
	 * @throws Exception
	 */
	public List<String> getOpOutMsPartNameList(String fileName, String opName)throws Exception{
		
		Definition df=this.getWsdlDef(fileName);
		return getMsPartNameList(this.getDefOp(df,opName).getOutput().getMessage());
	}
	
	/**
	 * given operation name, get a list of the element name of all parts of 
	 * message of output of the operation
	 * @param fileName
	 * @param opName
	 * @return
	 * @throws Exception
	 */
	public List<String> getOpOutMsElemName(String fileName, String opName)throws Exception{
		//String fileUrl=ClassLoader.getSystemResource(fileName).toString();
		Definition df=this.getWsdlDef(fileName);
		return getMsElemName(this.getDefOp(df,opName).getOutput().getMessage());
	}

	/**
	 * create a new wsdl file with given file name&definition
	 * @param outputFileName
	 * @param def
	 * @throws WSDLException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	private void createWsdl(String outputFileName, Definition def) throws WSDLException, IOException, URISyntaxException{
//		String fileUrl=ClassLoader.getSystemResource(outputFileName).toString();
                String fileUrl =Thread.currentThread().getContextClassLoader().getResource(outputFileName).toString();
		WSDLFactory factory= WSDLFactory.newInstance();
		final WSDLWriter writer = factory.newWSDLWriter();
		final Writer sink = new FileWriter(new File(new URI(fileUrl)));
		writer.writeWSDL(def, sink);
	}
	
	
	/**
	 * update the target wsdl input part element name as fileA input part element name
	 * and ouput part element as fileB output part element name
	 * @param fileNameA
	 * @param opA
	 * @param fileNameB
	 * @param opB
	 * @param outFileName
	 * @param opOutFile
	 */
	public void updatePartElement(String fileNameA, String opA, String fileNameB, String opB, String outFileName, String opOutFile){
		
		//String fileAUrl=ClassLoader.getSystemResource(fileNameA).toString();
		//String fileBUrl=ClassLoader.getSystemResource(fileNameB).toString();
		//String outfileUrl=ClassLoader.getSystemResource(outFileName).toString();
		
		//get input part of wsdlA, output part of wsdlB
		List<String> inPartsEleNameListA=null;
		List<String> outPartsEleNameListB=null;
		try{
			inPartsEleNameListA=getOpInMsElemName(fileNameA, opA);	
			outPartsEleNameListB=getOpOutMsElemName(fileNameB, opB);	
	}catch(Exception e){
		e.printStackTrace();
	}	
	//assuming only one part for each message
	String inPartEleNameA=inPartsEleNameListA.get(0);
	String outPartEleNameB=outPartsEleNameListB.get(0);
	
	//update part element of target wsdl file
	Definition df=null;
	try {
		df = this.getWsdlDef(outFileName);
	} catch (Exception e) {
		
		e.printStackTrace();
	}
	//get output and input part of target wsdl
	Map tempInMap=this.getDefOp(df,opOutFile).getInput().getMessage().getParts();
	Map tempOutMap=this.getDefOp(df,opOutFile).getOutput().getMessage().getParts();
	Part inPart=(Part)tempInMap.get(tempInMap.keySet().iterator().next());
	Part outPart=(Part)tempOutMap.get(tempOutMap.keySet().iterator().next());
	QName inEleQname=inPart.getElementName();
	QName outEleQname=outPart.getElementName();
	//update element name of parts
	inPart.setElementName(new QName(inEleQname.getNamespaceURI(),inPartEleNameA,inEleQname.getPrefix()));
	outPart.setElementName(new QName(outEleQname.getNamespaceURI(),outPartEleNameB,outEleQname.getPrefix()));
	//write back to target wsdl file
	try {
		this.createWsdl(outFileName, df);
	} catch (WSDLException e) {
		
		e.printStackTrace();
	} catch (IOException e) {
		
		e.printStackTrace();
	} catch (URISyntaxException e) {
		
		e.printStackTrace();
	}
	}
	
	/**
	 * get input part of given wsdl & operation
	 * @param fileName
	 * @param opName
	 * @return
	 */
	private Map getInPart(String fileName, String opName){
		//String fileUrl=ClassLoader.getSystemResource(fileName).toString();	
		Definition df=null;
		try {
			df = this.getWsdlDef(fileName);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return this.getDefOp(df,opName).getInput().getMessage().getParts();
	}
	
	/**
	 * get output part of given wsdl & operation
	 * @param fileName
	 * @param opName
	 * @return
	 */
	private Map getOutPart(String fileName, String opName){
		//String fileUrl=ClassLoader.getSystemResource(fileName).toString();	
		Definition df=null;
		try {
			df = this.getWsdlDef(fileName);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return this.getDefOp(df,opName).getOutput().getMessage().getParts();
	}
	
	/**
	 * 
	 */
	public ParseWSDL() {
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		
		ParseWSDL test=new ParseWSDL();
		/*List temp=test.getOpOutMsElemName("stockquote.wsdl", "GetQuote");
		List tempIn=test.getOpInMsElemName("stockquote.wsdl", "GetQuote");		
		//Definition df=test.getWsdlDef("stockquote.wsdl");		
		//	test.getMsElemName(test.getDefOp(df,"GetQuote").getInput().getMessage());
		System.out.println("input element="+tempIn);
		System.out.println("output element="+temp);*/
		/*
		List tempIn1=test.getOpInBottomEleNameList("stockquote.wsdl", "GetQuote");
		List tempOut=test.getOpOutBottomEleNameList("stockquote.wsdl", "GetQuote");
		System.out.println("input bottom element="+tempIn1);
		System.out.println("output bottom element="+tempOut);*/
		
		//System.out.println(test.getAllOpName("stockquote.wsdl"));
		
		System.out.println(test.getOpOutMsPartNameList("stockquote.wsdl", "GetQuote"));
		System.out.println(test.getMsEleNSUri("stockquote.wsdl"));
		
	}

}
