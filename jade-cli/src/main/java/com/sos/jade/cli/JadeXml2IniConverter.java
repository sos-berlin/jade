package com.sos.jade.cli;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import sos.util.SOSString;

public class JadeXml2IniConverter {
	
	private static Logger logger	= Logger.getLogger(JadeXml2IniConverter.class);
	private static final String CHARSET = "UTF-8";
	private static final String CLASSNAME = JadeXml2IniConverter.class.getSimpleName();
	private static final int EXIT_CODE_ON_SUCCESS = 0;
	private static final int EXIT_CODE_ON_ERROR = 99;
	
	private int _countNotificationFragments = 0;
	private int _countMailServerFragments = 0;
	private int _countProtocolFragments = 0;
	private int _countProfiles = 0;
	private int _countWarnings = 0;
	
	private boolean _hasGlobalSection = false;
	private BufferedWriter _writer = null;
	private HashMap<String,String> _jumpIncludes;
	private HashMap<String,LinkedHashMap<String,String>> _mailFragments;
	private HashMap<String,LinkedHashMap<String,String>> _mailServerFragments;
	
	private String _profileJumpInclude = null;
	
	/**
	 * 
	 * @param args
	 */
	public static void main (String[] args){
				
		if(args.length < 3){
			System.out.println("Arguments:");
			System.out.println("     1 - required - Schema file path");
			System.out.println("     2 - required - Xml file path");
			System.out.println("     3 - required - Ini file path");
			System.out.println("     4 - optional - log4j.properties file path");
			System.out.println("e.g.:");
			System.out.println(String.format("%s \"C:/Temp/schema.xsd\" \"C:/Temp/jade_settings.xml\" \"C:/Temp/jade_settings.ini\"",CLASSNAME));
			System.out.println(String.format("%s \"C:/Temp/schema.xsd\" \"C:/Temp/jade_settings.xml\" \"C:/Temp/jade_settings.ini\" \"C:/Temp/log4j.properties\"",CLASSNAME));
			
			System.exit(EXIT_CODE_ON_ERROR);
		}
		
		String log4j = null;
		int exitCode = EXIT_CODE_ON_SUCCESS;
		
		String schemaFile = args[0];
		String xmlFile = args[1];
		String iniFile = args[2];
		
		if(args.length > 3){
			log4j = args[3];
		}
		
		try {
			setLogger(log4j);
			logger.info("Arguments:");
			logger.info(String.format("  %s",schemaFile));
			logger.info(String.format("  %s",xmlFile));
			logger.info(String.format("  %s",iniFile));
			
			JadeXml2IniConverter converter = new JadeXml2IniConverter();
			converter.proccess(schemaFile,xmlFile,iniFile);
			
			logger.info("");
			logger.info("Summary:");
			logger.info(String.format("    %s General",converter.hasGlobalSection() ? "1" : "0"));
			logger.info(String.format("    %s Protocol Fragments",converter.getCountProtocolFragments()));
			logger.info(String.format("    %s Notification Fragments",converter.getCountNotificationFragments()));
			logger.info(String.format("    %s MailServer Fragments",converter.getCountMailServerFragments()));
			logger.info(String.format("    %s Profiles",converter.getCountProfiles()));
			if(converter.getCountWarnings() > 0){
				logger.info("");
				logger.info(String.format("    !!! Converted with %s warnings",converter.getCountWarnings()));
			}
			
			File f = new File(iniFile);
			if(f.length() == 0){
				f.deleteOnExit();
				throw new Exception(String.format("Converted file %s is empty and will be deleted",iniFile));
			}
			
		
		} catch (Exception e) {
			exitCode = EXIT_CODE_ON_ERROR;
			logger.error(e);
		}
		System.exit(exitCode);
	} 
	
	/**
	 * 
	 * @param schemaFilePath
	 * @param xmlFilePath
	 * @param iniFilePath
	 * @throws Exception
	 */
	public void proccess(String schemaFilePath,String xmlFilePath, String iniFilePath) throws Exception{
		//@TODO URL schema
		InputSource schemaSource = new InputSource(schemaFilePath);
		InputSource xmlSource = new InputSource(xmlFilePath);
		
		XPath schemaXpath =  XPathFactory.newInstance().newXPath();
		schemaXpath.setNamespaceContext(getSchemaNamespaceContext());
		XPath xmlXpath =  XPathFactory.newInstance().newXPath();
		
		XPathExpression schemaExpression = schemaXpath.compile("/xs:schema");
		XPathExpression xmlExpression = xmlXpath.compile("/Configurations");
		
		Node schemaRoot = (Node) schemaExpression.evaluate(schemaSource,XPathConstants.NODE);
		Node xmlRoot = (Node) xmlExpression.evaluate(xmlSource, XPathConstants.NODE);
		
		if(schemaRoot == null){
			throw new Exception(String.format("\"xs:schema\" element not found in the schema file %s",schemaFilePath));
		}
		if(xmlRoot == null){
			throw new Exception(String.format("\"Configurations\" element not found in the xml file %s",xmlFilePath));
		}
		
		try{
			_writer = new BufferedWriter(new OutputStreamWriter(
			          new FileOutputStream(iniFilePath),CHARSET));
			
			_jumpIncludes = new HashMap<String, String>();
			_mailFragments = new HashMap<String, LinkedHashMap<String,String>>();
			_mailServerFragments = new HashMap<String, LinkedHashMap<String,String>>();
			
			handleMailServerFragments(schemaXpath,schemaRoot,xmlXpath,xmlRoot);
			
			handleGeneral(schemaXpath, schemaRoot, xmlXpath, xmlRoot);
			handleProtocolFragments(schemaXpath,schemaRoot,xmlXpath,xmlRoot);
			handleNotificationFragments(schemaXpath,schemaRoot,xmlXpath,xmlRoot);
			handleProfiles(schemaXpath, schemaRoot, xmlXpath, xmlRoot);
		}
		catch(Exception ex){
			throw ex;
		}
		finally{
			if(_writer != null){
				_writer.close();
				_writer = null;
			}
		}
	}
	
	public int getCountNotificationFragments(){
		return _countNotificationFragments;
	}
	
	public int getCountProtocolFragments(){
		return _countProtocolFragments;
	}
	
	public int getCountProfiles(){
		return _countProfiles;
	}
	
	public boolean hasGlobalSection(){
		return _hasGlobalSection;
	}
	
	public int getCountWarnings(){
		return _countWarnings;
	}
	
	public int getCountMailServerFragments(){
		return _countMailServerFragments;
	}
	
	/**
	 * 
	 * @param xpathSchema
	 * @param schemaRoot
	 * @param xpathXml
	 * @param xmlRoot
	 * @throws Exception
	 */
	private void handleGeneral(XPath xpathSchema,Node schemaRoot,XPath xpathXml,Node xmlRoot) throws Exception{
		XPathExpression expression = xpathXml.compile("./General");
		Node general = (Node) expression.evaluate(xmlRoot, XPathConstants.NODE);
		if (general == null || !general.hasChildNodes()) {
			return;
		}
		_hasGlobalSection = true;
		
		NodeList childs = general.getChildNodes();
		String section = "[globals]";
		writeLine(section);
		logger.info(String.format("write %s",section));
		
		for (int i = 0; i< childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if(child.getAttributes().getNamedItem("ref") != null){
					String refName = child.getAttributes().getNamedItem("ref").getNodeValue();
					if(_mailServerFragments.containsKey(refName)){
						writeParams(_mailServerFragments.get(refName));
						writeNewLine();
					}
					continue;
				}
				
				handleChildNodes(xpathSchema, schemaRoot, xpathXml, xmlRoot,child.getNodeName(), child, 0);
				writeNewLine();
			}
		}
	}
	
	/**
	 * 
	 * @param xpathSchema
	 * @param schemaRoot
	 * @param xpathXml
	 * @param xmlRoot
	 * @throws Exception
	 */
	private void handleProtocolFragments(XPath xpathSchema,Node schemaRoot,XPath xpathXml,Node xmlRoot) throws Exception{
		
		XPathExpression expression = xpathXml.compile("./Fragments/ProtocolFragments");
		Node fragments = (Node) expression.evaluate(xmlRoot, XPathConstants.NODE);
		if (fragments == null) {
			throw new Exception(String.format("\"%s/Fragments/ProtocolFragments\" element not found",
					xmlRoot.getNodeName()));
		}
			
		NodeList childs = fragments.getChildNodes();
		int childCount = 0;
		for (int i = 0; i< childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if(!child.hasAttributes()){
					throw new Exception(String.format("attributes not found. node = %s",child.getNodeName()));
				}
				Node attrName = child.getAttributes().getNamedItem("name");
				if(attrName == null){
					throw new Exception(String.format("attribute \"name\" not found. node = %s",child.getNodeName()));
				}
				
				if(childCount == 0){
					if(!_hasGlobalSection){
						writeNewLine();
					}
				}
				else{
					writeNewLine();
				}
				String sectionName = getProtocolFragmentName(child);
				String section = "["+sectionName+"]";
				writeLine(section);
				logger.info(String.format("write %s",section));
				_countProtocolFragments++;
				
				XPathExpression ex = xpathSchema.compile(String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter",
							child.getNodeName()));
				NodeList flatParameters = (NodeList)ex.evaluate(schemaRoot,XPathConstants.NODESET);
				if(flatParameters != null){
					for (int j = 0; j < flatParameters.getLength(); j++) {
						Node fp = flatParameters.item(j);
						if(fp.getAttributes().getNamedItem("name") != null && fp.getAttributes().getNamedItem("value") != null){
							String param = formatParameter(fp.getAttributes().getNamedItem("name").getNodeValue(),fp.getAttributes().getNamedItem("value").getNodeValue());
							writeLine(param);
							if(j == 0){
								writeNewLine();
							}
						}
					}
				}
				
				String prefix = "";
				if(child.getNodeName().toLowerCase().startsWith("jump")){
					prefix = "jump_";
				}
				
				handleProtocolFragmentsChildNodes(xpathSchema, schemaRoot, xpathXml, xmlRoot, child,sectionName,0,0,prefix,"");
				childCount++;
			}
		}
	}
	
	/**
	 * 
	 * @param xpathSchema
	 * @param schemaRoot
	 * @param xpathXml
	 * @param xmlRoot
	 * @throws Exception
	 */
	private void handleNotificationFragments(XPath xpathSchema,Node schemaRoot,XPath xpathXml,Node xmlRoot) throws Exception{
		
		XPathExpression expression = xpathXml.compile("./Fragments/NotificationFragments");
		Node fragments = (Node) expression.evaluate(xmlRoot, XPathConstants.NODE);
		if (fragments == null) {
			return;
		}
			
		NodeList childs = fragments.getChildNodes();
		for (int i = 0; i< childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if(!child.hasAttributes()){
					throw new Exception(String.format("attributes not found. node = %s",child.getNodeName()));
				}
				Node attrName = child.getAttributes().getNamedItem("name");
				if(attrName == null){
					throw new Exception(String.format("attribute \"name\" not found. node = %s",child.getNodeName()));
				}
				
				String mailFragment = child.getAttributes().getNamedItem("name").getNodeValue();
				logger.info(String.format("found Notification Fragment \"%s\"",mailFragment));
				_countNotificationFragments++;
				LinkedHashMap<String,String> mailFragmentParams = new LinkedHashMap<String, String>();
				
				XPathExpression ex = xpathSchema.compile(String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter",
							child.getNodeName()));
				NodeList flatParameters = (NodeList)ex.evaluate(schemaRoot,XPathConstants.NODESET);
				if(flatParameters != null){
					for (int j = 0; j < flatParameters.getLength(); j++) {
						Node fp = flatParameters.item(j);
						
						if(fp.getAttributes().getNamedItem("name") != null && fp.getAttributes().getNamedItem("value") != null){
							mailFragmentParams.put(fp.getAttributes().getNamedItem("name").getNodeValue(),fp.getAttributes().getNamedItem("value").getNodeValue());
						}
					}
				}
				
				mailFragmentParams = handleNotificationFragmentsChildNodes(xpathSchema, schemaRoot, xpathXml, xmlRoot, child,mailFragmentParams,0);
				
				_mailFragments.put(mailFragment, mailFragmentParams);
			}
		}
	}

	/**
	 * 
	 * @param xpathSchema
	 * @param schemaRoot
	 * @param xpathXml
	 * @param xmlRoot
	 * @throws Exception
	 */
	private void handleMailServerFragments(XPath xpathSchema,Node schemaRoot,XPath xpathXml,Node xmlRoot) throws Exception{
		XPathExpression expression = xpathXml.compile("./Fragments/MailServerFragments");
		Node fragments = (Node) expression.evaluate(xmlRoot, XPathConstants.NODE);
		if (fragments == null) {
			return;
		}
			
		NodeList childs = fragments.getChildNodes();
		for (int i = 0; i< childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if(!child.hasAttributes()){
					throw new Exception(String.format("attributes not found. node = %s",child.getNodeName()));
				}
				Node attrName = child.getAttributes().getNamedItem("name");
				if(attrName == null){
					throw new Exception(String.format("attribute \"name\" not found. node = %s",child.getNodeName()));
				}
				
				String mailFragment = child.getAttributes().getNamedItem("name").getNodeValue();
				logger.info(String.format("found MailServer Fragment \"%s\"",mailFragment));
				_countMailServerFragments++;
				LinkedHashMap<String,String> mailFragmentParams = new LinkedHashMap<String, String>();
				
				XPathExpression ex = xpathSchema.compile(String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter",
							child.getNodeName()));
				NodeList flatParameters = (NodeList)ex.evaluate(schemaRoot,XPathConstants.NODESET);
				if(flatParameters != null){
					for (int j = 0; j < flatParameters.getLength(); j++) {
						Node fp = flatParameters.item(j);
						
						if(fp.getAttributes().getNamedItem("name") != null && fp.getAttributes().getNamedItem("value") != null){
							mailFragmentParams.put(fp.getAttributes().getNamedItem("name").getNodeValue(),fp.getAttributes().getNamedItem("value").getNodeValue());
						}
					}
				}
				
				mailFragmentParams = handleNotificationFragmentsChildNodes(xpathSchema, schemaRoot, xpathXml, xmlRoot, child,mailFragmentParams,0);
				_mailServerFragments.put(mailFragment, mailFragmentParams);
			}
		}
	}

	/**
	 * 
	 * @param xpathSchema
	 * @param schemaRoot
	 * @param xpathXml
	 * @param xmlRoot
	 * @throws Exception
	 */
	private void handleProfiles(XPath xpathSchema,Node schemaRoot,XPath xpathXml,Node xmlRoot) throws Exception{
		
		XPathExpression expression = xpathXml.compile("./Profiles/Profile");
		NodeList profiles = (NodeList) expression.evaluate(xmlRoot, XPathConstants.NODESET);
		if (profiles == null) {
			throw new Exception(String.format("\"%s/Profiles/Profile\" elements not found",
					xmlRoot.getNodeName()));
		}
		
		for (int i = 0; i< profiles.getLength(); i++) {
			Node profile = profiles.item(i);
			
			if(!profile.hasAttributes()){
				throw new Exception(String.format("attributes not found. node = %s",profile.getNodeName()));
			}
			Node attrProfileId = profile.getAttributes().getNamedItem("profile_id");
			if(attrProfileId == null){
				throw new Exception(String.format("attribute \"profile_id\" not found. node = %s",profile.getNodeName()));
			}
			
			writeNewLine();
			String sectionName = getProfileName(profile,attrProfileId);
			String section = "["+sectionName+"]"; 
			writeLine(section);
			logger.info(String.format("write %s",section));
			_countProfiles++;
			_profileJumpInclude = null;
			
			NodeList childs = profile.getChildNodes();
			for (int j = 0; j< childs.getLength(); j++) {
				Node child = childs.item(j);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					if(child.getNodeName().toLowerCase().equals("operation")){
						handleProfileOperation(xpathSchema, schemaRoot, xpathXml, xmlRoot, child,sectionName,"",0,0,"","");
					}
					else if(child.getNodeName().toLowerCase().equals("client")){
						writeNewLine();
						handleChildNodes(xpathSchema, schemaRoot, xpathXml, xmlRoot,child.getNodeName(), child,0);
					}
					else if(child.getNodeName().toLowerCase().equals("jobscheduler")){
						writeNewLine();
						handleChildNodes(xpathSchema, schemaRoot, xpathXml, xmlRoot,child.getNodeName(), child,0);
					}
					else if(child.getNodeName().toLowerCase().equals("notifications")){
						writeNewLine();
						handleProfileNotification(xpathSchema, schemaRoot, xpathXml, xmlRoot,child.getNodeName(), child,0);
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param xpathSchema
	 * @param schemaRoot
	 * @param xpathXml
	 * @param xmlRoot
	 * @param parentNodeName
	 * @param xmlNode
	 * @param level
	 * @throws Exception
	 */
	private void handleChildNodes(XPath xpathSchema,Node schemaRoot,XPath xpathXml,Node xmlRoot,String parentNodeName,Node xmlNode,int level) throws Exception{
		if(xmlNode.hasChildNodes()){
			level++;
			
			NodeList childs = xmlNode.getChildNodes();
			for (int i = 0; i< childs.getLength(); i++) {
				Node child = childs.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					if(child.getAttributes().getNamedItem("ref") != null){
						//
						continue;
					}
					String xpath = String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter[1]",child.getNodeName());
					
					XPathExpression ex = xpathSchema.compile(xpath);
					Node fp = (Node)ex.evaluate(schemaRoot,XPathConstants.NODE);
					if(fp != null){
						if(fp.getAttributes().getNamedItem("name") != null){
							String name = fp.getAttributes().getNamedItem("name").getNodeValue();
							String value = getParameterValue(fp,child);
							writeLine(formatParameter(name, value));
						}
					}
					handleChildNodes(xpathSchema, schemaRoot, xpathXml, xmlRoot,parentNodeName, child,level);
				}
			}
		}
	}

	private void writeNotification2Profile(String parentNodeName,LinkedHashMap<String,String> values) throws Exception{
		String prefix = "";
		if(parentNodeName.equals("OnSuccess")){
			prefix = "mail_on_success_";
		}
		else if(parentNodeName.equals("OnError")){
			prefix = "mail_on_error_";
		}
		else if(parentNodeName.equals("OnEmptyFiles")){
			prefix = "mail_on_empty_files_";
		}
		
		for(Entry<String, String> entry : values.entrySet()){
			writeLine(formatParameter(prefix+entry.getKey(),entry.getValue()));
		}
		writeNewLine();
	}
	
	/**
	 * 
	 * @param values
	 * @throws Exception
	 */
	private void writeParams(LinkedHashMap<String,String> values) throws Exception{
		for(Entry<String, String> entry : values.entrySet()){
			writeLine(formatParameter(entry.getKey(),entry.getValue()));
		}
	}
	
	private void handleProfileNotification(XPath xpathSchema,Node schemaRoot,XPath xpathXml,Node xmlRoot,String parentNodeName,Node xmlNode,int level) throws Exception{
		if(xmlNode.hasChildNodes()){
			level++;
			
			NodeList childs = xmlNode.getChildNodes();
			for (int i = 0; i< childs.getLength(); i++) {
				Node child = childs.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					if(child.getAttributes().getNamedItem("ref") != null){
						String refName = child.getAttributes().getNamedItem("ref").getNodeValue();
						if(child.getNodeName().equals("MailFragmentRef")){
							if(_mailFragments.containsKey(refName)){
								writeNotification2Profile(child.getParentNode().getNodeName(),_mailFragments.get(refName));
							}
						}
						else if(child.getNodeName().equals("MailServerFragmentRef")){
							if(_mailServerFragments.containsKey(refName)){
								writeParams(_mailServerFragments.get(refName));
							}
						}
						continue;
					}
					String xpath = String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter[1]",child.getNodeName());
					
					XPathExpression ex = xpathSchema.compile(xpath);
					Node fp = (Node)ex.evaluate(schemaRoot,XPathConstants.NODE);
					if(fp != null){
						if(fp.getAttributes().getNamedItem("mail_name") != null){
							String name = fp.getAttributes().getNamedItem("mail_name").getNodeValue();
							String value = getParameterValue(fp,child);
							writeLine(formatParameter(name, value));
						}
						else if(fp.getAttributes().getNamedItem("name") != null){
							String name = fp.getAttributes().getNamedItem("name").getNodeValue();
							String value = getParameterValue(fp,child);
							writeLine(formatParameter(name, value));
						}
					}
					handleProfileNotification(xpathSchema, schemaRoot, xpathXml, xmlRoot,parentNodeName, child,level);
				}
			}
		}
	}

	/**
	 * 
	 * @param xpathSchema
	 * @param schemaRoot
	 * @param xpathXml
	 * @param xmlRoot
	 * @param xmlNode
	 * @param sectionName
	 * @param level
	 * @param prefixLevel
	 * @param parentPrefix
	 * @param childPrefix
	 * @throws Exception
	 */
	private void handleProtocolFragmentsChildNodes(XPath xpathSchema,Node schemaRoot,XPath xpathXml,Node xmlRoot,Node xmlNode,String sectionName,int level,int prefixLevel,String parentPrefix,String childPrefix) throws Exception{
		if(xmlNode.hasChildNodes()){
			level++;
			
			NodeList childs = xmlNode.getChildNodes();
			for (int i = 0; i< childs.getLength(); i++) {
				Node child = childs.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					if(child.getAttributes().getNamedItem("ref") != null){
						//System.out.println(child.getNodeName()+" = "+child.getAttributes().getNamedItem("ref").getNodeValue());
						if(child.getNodeName().equals("JumpFragmentRef")){
							String include = getProtocolFragmentInclude(xpathXml, xmlRoot, child,"jump_");
							_jumpIncludes.put(sectionName,include);
						}
						else{
							String include = getProtocolFragmentInclude(xpathXml, xmlRoot, child,"");
							if(include != null){
								writeNewLine();
								writeLine(include);
								writeNewLine();
							}
						}
						continue;
					}
					String xpath = String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter[1]",child.getNodeName());
					
					XPathExpression ex = xpathSchema.compile(xpath);
					Node fp = (Node)ex.evaluate(schemaRoot,XPathConstants.NODE);
					if(fp != null){
						
						if(fp.getAttributes().getNamedItem("name") != null){
							
							if(level <= prefixLevel){
								prefixLevel = 0;
								childPrefix = "";
							}
														
							String name = fp.getAttributes().getNamedItem("name").getNodeValue();
							if(childPrefix.length() > 0 && !name.toLowerCase().startsWith(childPrefix)){
								name = childPrefix+name;
							}
							
							if(parentPrefix.length() > 0 && !parentPrefix.equals("jump_") && !name.toLowerCase().startsWith(parentPrefix)){
								name = parentPrefix+name;
							}
							String value = getParameterValue(fp,child);
							
							writeLine(formatParameter(name, value));
						}
						
					}
					if(child.getNodeName().toLowerCase().startsWith("proxy")){
						childPrefix = "proxy_";
						prefixLevel = level;
					}					
					handleProtocolFragmentsChildNodes(xpathSchema, schemaRoot, xpathXml, xmlRoot, child,sectionName,level,prefixLevel,parentPrefix,childPrefix);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param xpathSchema
	 * @param schemaRoot
	 * @param xpathXml
	 * @param xmlRoot
	 * @param xmlNode
	 * @param sectionName
	 * @param operation
	 * @param level
	 * @param prefixLevel
	 * @param parentPrefix
	 * @param childPrefix
	 * @throws Exception
	 */
	private void handleProfileOperation(XPath xpathSchema,Node schemaRoot,XPath xpathXml,Node xmlRoot,Node xmlNode,String sectionName,String operation,int level,int prefixLevel,String parentPrefix,String childPrefix) throws Exception{
		if(xmlNode.hasChildNodes()){
			level++;
			
			NodeList childs = xmlNode.getChildNodes();
			for (int i = 0; i< childs.getLength(); i++) {
				Node child = childs.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					if(level <= prefixLevel){
						prefixLevel = 0;
						childPrefix = "";
						if(parentPrefix.length() == 0){
							writeNewLine();
						}
					}
					if(child.getAttributes().getNamedItem("ref") != null){
						String include = getProtocolFragmentInclude(xpathXml, xmlRoot, child,childPrefix);
						if(include != null){
							String[] arr = include.split("=");
							if(arr.length == 2){
								String includeName = arr[1].trim();
								if(_jumpIncludes.containsKey(includeName)){
									String jumpInclude = _jumpIncludes.get(includeName); 
									if(operation.equals("copyfrominternet") || operation.equals("copytointernet")){
										if(_profileJumpInclude == null){
											writeLine(jumpInclude);
											writeNewLine();
											_profileJumpInclude = jumpInclude;
										}
										else{
											logger.warn(String.format("Profile [%s]: include of \"%s\" skipped(\"%s\" already included)",sectionName,jumpInclude.replaceAll(" ",""),_profileJumpInclude.replaceAll(" ","")));
											_countWarnings++;
										}
									}
									else{
										logger.warn(String.format("Profile [%s]: include of \"%s\" skipped(jump host with operation \"%s\" is not implemented)",sectionName,jumpInclude.replaceAll(" ",""),operation));
										_countWarnings++;
									}
								}		
							}
							writeLine(include);
						}
					}
					String xpath = String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter[1]",child.getNodeName());
					
					XPathExpression ex = xpathSchema.compile(xpath);
					Node fp = (Node)ex.evaluate(schemaRoot,XPathConstants.NODE);
					if(fp != null){
						if(fp.getAttributes().getNamedItem("name") != null){
							String name = fp.getAttributes().getNamedItem("name").getNodeValue();
							Node suppressPrefix = fp.getAttributes().getNamedItem("suppress_prefix");
							if(suppressPrefix != null && suppressPrefix.getNodeValue().equalsIgnoreCase("true")){
								
							}
							else{
								if(childPrefix.length() > 0 && !name.toLowerCase().startsWith(childPrefix)){
									name = childPrefix+name;
								}
								if(parentPrefix.length() > 0 && !name.toLowerCase().startsWith(parentPrefix)){
									name = parentPrefix+name;
								}
							}
							String value = getParameterValue(fp, child);
							StringBuffer addition = null;
							if(fp.getAttributes().getNamedItem("value") != null){
								// the next flat parameters after first.
								// e.g.: LocalSource
								//  <FlatParameter name="protocol" value="local"/>
								//  <FlatParameter name="host" value="localhost"/>
								addition = new StringBuffer();
								Node sibling = fp.getNextSibling();
								while (sibling != null) {
									
									if(sibling.getNodeType() == Node.ELEMENT_NODE && sibling.getAttributes() != null && sibling.getAttributes().getNamedItem("name") != null && sibling.getAttributes().getNamedItem("value") != null){
										String addName = sibling.getAttributes().getNamedItem("name").getNodeValue();
										if(childPrefix.length() > 0 && !addName.toLowerCase().startsWith(childPrefix)){
											addName = childPrefix+addName;
										}
										if(addition.length() > 0){
											addition.append(System.getProperty("line.separator"));
										}
										addition.append(formatParameter(addName, sibling.getAttributes().getNamedItem("value").getNodeValue()));
									}
									sibling = sibling.getNextSibling();
							     }
							}
							
							if(level == 1 && name.equals("operation")){
								value = getProfileOperationValue(xpathXml, child,sectionName,value);
								operation = value;
							}
							
							writeLine(formatParameter(name, value));
							if(addition != null && addition.length() > 0){
								writeLine(addition.toString());
							}
						}
					}
					
					if(child.getNodeName().toLowerCase().endsWith("source")){
						childPrefix = "source_";
						prefixLevel = level;
					}
					else if(child.getNodeName().toLowerCase().endsWith("target")){
						childPrefix = "target_";
						prefixLevel = level;
					}
					
					if(level == 1){
						writeNewLine();
					}
					
					handleProfileOperation(xpathSchema, schemaRoot, xpathXml, xmlRoot, child,sectionName,operation,level,prefixLevel,parentPrefix,childPrefix);
				}
			}
		}
	}

	/**
	 * 
	 * @param xpathXml
	 * @param node
	 * @param sectionName
	 * @param value
	 * @return
	 * @throws Exception
	 */
	private String getProfileOperationValue(XPath xpathXml,Node node,String sectionName,String value) throws Exception{
		if((value.equals("copy") || value.equals("move")) && _jumpIncludes.size() > 0){
			String xpath = String.format(".//*[contains(local-name(),'Source')]/*[string-length(@ref)!=0]");
			XPathExpression ex = xpathXml.compile(xpath);
			Node fp = (Node)ex.evaluate(node,XPathConstants.NODE);
			if(fp != null){
				for(Entry<String, String> entry : _jumpIncludes.entrySet()){
					//protocol_fragment_https@https_fragment
					String[] arr = entry.getKey().split("@");
					if(arr.length > 1){
						String fragmentXmlName = arr[1].trim();
						if(fragmentXmlName.equals(fp.getAttributes().getNamedItem("ref").getNodeValue())){
							return "copyfrominternet";
						}
					}
				}
			}
				
			xpath = String.format(".//*[contains(local-name(),'Target')]/*[string-length(@ref)!=0]");
			ex = xpathXml.compile(xpath);
			fp = (Node)ex.evaluate(node,XPathConstants.NODE);
			if(fp != null){
				for(Entry<String, String> entry : _jumpIncludes.entrySet()){
					//protocol_fragment_https@https_fragment
					String[] arr = entry.getKey().split("@");
					if(arr.length > 1){
						String fragmentXmlName = arr[1].trim();
						if(fragmentXmlName.equals(fp.getAttributes().getNamedItem("ref").getNodeValue())){
							return "copytointernet";
						}
					}
				}
			}
		}
		return value;
	}
	
	/**
	 * 
	 * @param xpathSchema
	 * @param schemaRoot
	 * @param xpathXml
	 * @param xmlRoot
	 * @param xmlNode
	 * @param mailFragmentParams
	 * @param level
	 * @return
	 * @throws Exception
	 */
	private LinkedHashMap<String,String> handleNotificationFragmentsChildNodes(XPath xpathSchema,Node schemaRoot,XPath xpathXml,Node xmlRoot,Node xmlNode,LinkedHashMap<String,String> mailFragmentParams,int level) throws Exception{
		if(xmlNode.hasChildNodes()){
			level++;
			
			NodeList childs = xmlNode.getChildNodes();
			for (int i = 0; i< childs.getLength(); i++) {
				Node child = childs.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					String xpath = String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter[1]",child.getNodeName());
					XPathExpression ex = xpathSchema.compile(xpath);
					Node fp = (Node)ex.evaluate(schemaRoot,XPathConstants.NODE);
					if(fp != null){
						if(fp.getAttributes().getNamedItem("mail_name") != null){
							mailFragmentParams.put(fp.getAttributes().getNamedItem("mail_name").getNodeValue(), getParameterValue(fp,child));
						}
						else if(fp.getAttributes().getNamedItem("name") != null){
							mailFragmentParams.put(fp.getAttributes().getNamedItem("name").getNodeValue(), getParameterValue(fp,child));
						}
					}
					mailFragmentParams = handleNotificationFragmentsChildNodes(xpathSchema, schemaRoot, xpathXml, xmlRoot, child,mailFragmentParams,level);
				}
			}
		}
		return mailFragmentParams;
	}
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	private String getProtocolFragmentName(Node node){
		StringBuffer sb = new StringBuffer("protocol_fragment_");
		sb.append(node.getNodeName().toLowerCase().replace("fragment",""));
		sb.append("@");
		sb.append(node.getAttributes().getNamedItem("name").getNodeValue());
		return sb.toString();
	}
	
	/**
	 * 
	 * @param node
	 * @param attr
	 * @return
	 */
	private String getProfileName(Node node,Node attr){
		/**
		StringBuffer sb = new StringBuffer(node.getNodeName().toLowerCase());
		sb.append("@");
		sb.append(attr.getNodeValue());
		return sb.toString();*/
		return attr.getNodeValue();
	}
	
	/**
	 * 
	 * @param xpathXml
	 * @param xmlRoot
	 * @param node
	 * @param prefix
	 * @return
	 * @throws Exception
	 */
	private String getProtocolFragmentInclude(XPath xpathXml,Node xmlRoot,Node node,String prefix) throws Exception{
		String include = null;
		
		String ref = node.getAttributes().getNamedItem("ref").getNodeValue();
		//JumpFragmentRef -> JumpFragment
		String name = node.getNodeName().substring(0,node.getNodeName().length()-3);
		String xpath = String.format("./Fragments/ProtocolFragments/%s[@name='%s']",name,ref);
		XPathExpression ex = xpathXml.compile(xpath);
		Node fp = (Node)ex.evaluate(xmlRoot,XPathConstants.NODE);
		if(fp == null){
			//not found
			include = formatParameter(prefix+"include",ref);
			logger.warn(String.format("Node %s[@ref = %s]. Not found referenced Fragments/ProtocolFragments/%s[@name='%s'].", 
					node.getNodeName(),
					ref,
					name,
					ref));
			_countWarnings++;
		}
		else{
			include = formatParameter(prefix+"include",getProtocolFragmentName(fp));
		}
		return include;
	}
	
	/**
	 * 
	 * @param flatParameter
	 * @param node
	 * @return
	 */
	private String getParameterValue(Node flatParameter,Node node){
		String value = "";
		if(flatParameter.getAttributes().getNamedItem("value") == null){
			if(node.getFirstChild() == null){
				//value = ", !!! Value from XML is NULL";
			}
			else{
				//Value from XML;
				value = node.getFirstChild().getNodeValue();
				Node oppositeValue = flatParameter.getAttributes().getNamedItem("opposite_value");
				if(oppositeValue != null && oppositeValue.getNodeValue().equals("true")){
					value = value.equalsIgnoreCase("true") ? "false" : "true";
				}
			}
	
		}
		else{
			//Value from Schema appInfo";
			value = flatParameter.getAttributes().getNamedItem("value").getNodeValue();
		}
		return value;
	}
	
	/**
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	private String formatParameter(String name, String value){
		return String.format("%-35s = %s",name,value);
	}
	
	/**
	 * 
	 * @param line
	 * @throws Exception
	 */
	private void writeLine(String line) throws Exception{
		_writer.write(line);
		_writer.newLine();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void writeNewLine() throws Exception{
		_writer.newLine();
	}
	
	/**
	 * 
	 * @param path
	 * @throws Exception
	 */
	private static void setLogger(String path) throws Exception{
		if(!SOSString.isEmpty(path)){
			File file = new File(path);
			if (file.isFile() && file.canRead()) {
				PropertyConfigurator.configure(file.getCanonicalPath());
			}
		}
		if( !Logger.getRootLogger().getAllAppenders().hasMoreElements() ) {
			BasicConfigurator.configure();
		}
		logger = Logger.getRootLogger();
	}
	
	/**
	 * 
	 * @return
	 */
	private NamespaceContext getSchemaNamespaceContext(){
		return new NamespaceContext() {
			
			@Override
			public Iterator<String> getPrefixes(String namespaceURI) {
				return null;
			}
			
			@Override
			public String getPrefix(String namespaceURI) {
				return "xs";
			}
			
			@Override
			public String getNamespaceURI(String prefix) {
				return "http://www.w3.org/2001/XMLSchema";
			}
		};
	}
}
