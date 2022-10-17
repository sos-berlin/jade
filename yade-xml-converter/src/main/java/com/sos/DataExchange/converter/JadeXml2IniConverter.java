package com.sos.DataExchange.converter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import sos.util.SOSString;

public class JadeXml2IniConverter {

    public static final String NEW_LINE = "\r\n";
    public static final String ELEMENT_NAME_ROOT = "Configurations";

    private static final Logger LOGGER = LoggerFactory.getLogger(JadeXml2IniConverter.class);
    private static final String CHARSET = "UTF-8";
    private static final String CLASSNAME = JadeXml2IniConverter.class.getSimpleName();
    private static final int EXIT_CODE_ON_SUCCESS = 0;
    private static final int EXIT_CODE_ON_ERROR = 99;
    private static final String SCHEMA_ATTRIBUTE_NAME = "name";
    private static final String SCHEMA_ATTRIBUTE_VALUE = "value";
    private static final String SCHEMA_ATTRIBUTE_SUPPRESS_PREFIX = "suppress_prefix";
    private static final String SCHEMA_ATTRIBUTE_OPPOSITE_VALUE = "opposite_value";
    private static final String SCHEMA_ATTRIBUTE_DEFAULT_VALUE = "default";
    private static final String SCHEMA_ATTRIBUTE_MAIL_NAME = "mail_name";
    private static final String SCHEMA_ATTRIBUTE_ALTERNATIVE_NAME = "alternative_name";
    private int _countNotificationMailFragments = 0;
    private int _countAlternativeFragments = 0;
    private int _countNotificationBackgroundServiceFragments = 0;
    private int _countMailServerFragments = 0;
    private int _countCredentialStoreFragments = 0;
    private int _countProtocolFragments = 0;
    private int _countProfiles = 0;
    private int _countWarnings = 0;
    private boolean _hasGlobalSection = false;
    private Writer _writer = null;
    private HashMap<String, String> _jumpIncludes;
    private HashMap<String, String> _credentialStoreIncludes;
    private HashMap<String, LinkedHashMap<String, String>> _mailFragments;
    private HashMap<String, LinkedHashMap<String, String>> _mailServerFragments;
    private XPath _xpathSchema;
    private XPath _xpathXml;
    private Node _rootSchema;
    private Node _rootXml;
    private String _profileJumpInclude = null;
    private boolean _mainMethodCalled = false;

    private enum Fragment {
        protocolFragment, alternativeFragment, notificationFragment, credentialStoreFragment, mailServerFragment, backgroundServiceFragment
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            LOGGER.info("Arguments:");
            LOGGER.info("     1 - required - XSD Schema location");
            LOGGER.info("     2 - required - XML file path");
            LOGGER.info("     3 - required - INI file path");
            LOGGER.info("     4 - optional - log4j2.xml file path");
            LOGGER.info("e.g.:");
            LOGGER.info(String.format(
                    "%s \"http://www.sos-berlin.com/schema/jade/JADE_configuration_v1.0.xsd\" \"C:/Temp/jade_settings.xml\" \"C:/Temp/jade_settings.ini\"",
                    CLASSNAME));
            LOGGER.info(String.format(
                    "%s \"http://www.sos-berlin.com/schema/jade/JADE_configuration_v1.0.xsd\" \"C:/Temp/jade_settings.xml\" \"C:/Temp/jade_settings.ini\" "
                            + "\"C:/Temp/log4j2.xml\"", CLASSNAME));
            System.exit(EXIT_CODE_ON_ERROR);
        }
        String log4j = null;
        int exitCode = EXIT_CODE_ON_SUCCESS;
        String schemaFile = args[0];
        String xmlFile = args[1];
        String iniFile = args[2];
        if (args.length > 3) {
            log4j = args[3];
        }
        try {
            setLogger(log4j);
            LOGGER.info("Arguments:");
            for (int i = 0; i < args.length; i++) {
                LOGGER.info(String.format("  %s", args[i]));
            }
            JadeXml2IniConverter converter = new JadeXml2IniConverter();
            converter.setMainMethodCalled(true);
            converter.process(schemaFile, xmlFile, iniFile);
            LOGGER.info("");
            LOGGER.info("Summary:");
            LOGGER.info(String.format("    %s General", converter.hasGlobalSection() ? "1" : "0"));
            LOGGER.info(String.format("    %s Protocol Fragments", converter.getCountProtocolFragments()));
            LOGGER.info(String.format("    %s Alternative Fragments", converter.getCountAlternativeFragments()));
            LOGGER.info(String.format("    %s Notification MailFragments", converter.getCountNotificationMailFragments()));
            LOGGER.info(String.format("    %s Notification BackgroundServiceFragments", converter.getCountNotificationBackgroundServiceFragments()));
            LOGGER.info(String.format("    %s CredentialStore Fragments", converter.getCountCredentialStoreFragments()));
            LOGGER.info(String.format("    %s MailServer Fragments", converter.getCountMailServerFragments()));
            LOGGER.info(String.format("    %s Profiles", converter.getCountProfiles()));
            if (converter.getCountWarnings() > 0) {
                LOGGER.info("");
                LOGGER.info(String.format("    !!! Converted with %s warnings", converter.getCountWarnings()));
            }
            File f = new File(iniFile);
            if (f.length() == 0) {
                f.deleteOnExit();
                throw new Exception(String.format("Converted file %s is empty and will be deleted", iniFile));
            }
        } catch (Exception e) {
            exitCode = EXIT_CODE_ON_ERROR;
            LOGGER.error(e.getMessage(), e);
        }
        System.exit(exitCode);
    }

    public void process(String schemaFilePath, String xmlFilePath, String iniFilePath) throws Exception {
        process(new InputSource(schemaFilePath), new InputSource(xmlFilePath), xmlFilePath, iniFilePath, null);
    }

    public void process(InputSource schemaSource, String xmlFilePath, String iniFilePath) throws Exception {
        process(schemaSource, new InputSource(xmlFilePath), xmlFilePath, iniFilePath, null);
    }

    public byte[] process(InputSource schemaSource, InputSource xmlSource, StringBuilder header) throws Exception {
        return process(schemaSource, xmlSource, null, null, header);
    }

    public byte[] process(InputSource schemaSource, InputSource xmlSource, String xmlFilePath, String iniFilePath, StringBuilder header)
            throws Exception {
        String method = "process";
        boolean write2file = (xmlFilePath != null && iniFilePath != null);
        if (write2file) {
            LOGGER.debug(String.format("[%s]xmlFilePath=%s, iniFilePath=%s", method, xmlFilePath, iniFilePath));
        }
        init(schemaSource, xmlSource, xmlFilePath);
        try {
            if (write2file) {
                _writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(iniFilePath), CHARSET));
            } else {
                _writer = new StringWriter();
            }
            _jumpIncludes = new HashMap<String, String>();
            _credentialStoreIncludes = new HashMap<String, String>();
            _mailFragments = new HashMap<String, LinkedHashMap<String, String>>();
            _mailServerFragments = new HashMap<String, LinkedHashMap<String, String>>();

            if (header != null) {
                _writer.write(header.toString());
                writeNewLine();
            }
            handleMailServerFragments();
            handleGeneral();
            handleProtocolFragments();
            handleNotificationMailFragments();
            handleNotificationBackgroundServiceFragments();
            handleCredentialStoreFragments();
            handleProfiles();

            if (write2file) {
                return new byte[0];
            } else {
                return _writer.toString().getBytes(CHARSET);
            }
        } catch (Exception ex) {
            throw new Exception(String.format("[%s]%s", method, ex.toString()), ex);
        } finally {
            if (_writer != null) {
                _writer.close();
                _writer = null;
            }
        }
    }

    private void init(InputSource schemaSource, InputSource xmlSource, String xmlFilePath) throws Exception {
        String method = "init";
        _xpathSchema = XPathFactory.newInstance().newXPath();
        _xpathSchema.setNamespaceContext(getSchemaNamespaceContext());
        _xpathXml = XPathFactory.newInstance().newXPath();
        XPathExpression schemaExpression = _xpathSchema.compile("/xs:schema");
        XPathExpression xmlExpression = _xpathXml.compile("/" + ELEMENT_NAME_ROOT);

        Document schemaDoc = getXmlFileDocument(schemaSource);
        _rootSchema = (Node) schemaExpression.evaluate(schemaDoc, XPathConstants.NODE);

        Document xmlDoc = getXmlFileDocument(xmlSource, xmlFilePath);
        _rootXml = (Node) xmlExpression.evaluate(xmlDoc, XPathConstants.NODE);
        if (_rootSchema == null) {
            throw new Exception(String.format("[%s]\"xs:schema\" element not found in the schema file", method));
        }
        if (_rootXml == null) {
            if (xmlFilePath == null) {
                throw new Exception(String.format("[%s]\"%s\" element not found", method, ELEMENT_NAME_ROOT));
            }
            throw new Exception(String.format("[%s][%s]\"%s\" element not found", method, xmlFilePath, ELEMENT_NAME_ROOT));
        }
    }

    private Document getXmlFileDocument(InputSource xmlSource) throws Exception {
        return getXmlFileDocument(xmlSource, null);
    }

    private Document getXmlFileDocument(InputSource xmlSource, String xmlFile) throws Exception {
        if (xmlFile != null) {
            String normalized = xmlFile.toLowerCase();
            if (_mainMethodCalled && !normalized.startsWith("http://") && !normalized.startsWith("https://")) {
                String ud = System.getProperty("user.dir");
                if (!SOSString.isEmpty(ud)) {
                    xmlFile = (Paths.get(ud).resolve(xmlFile)).toString();
                }
            }
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setXIncludeAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(xmlSource);
    }

    public int getCountNotificationMailFragments() {
        return _countNotificationMailFragments;
    }

    public int getCountNotificationBackgroundServiceFragments() {
        return _countNotificationBackgroundServiceFragments;
    }

    public int getCountProtocolFragments() {
        return _countProtocolFragments;
    }

    public int getCountProfiles() {
        return _countProfiles;
    }

    public boolean hasGlobalSection() {
        return _hasGlobalSection;
    }

    public int getCountWarnings() {
        return _countWarnings;
    }

    public int getCountMailServerFragments() {
        return _countMailServerFragments;
    }

    public int getCountCredentialStoreFragments() {
        return _countCredentialStoreFragments;
    }

    public int getCountAlternativeFragments() {
        return _countAlternativeFragments;
    }

    private void handleGeneral() throws Exception {
        String method = "handleGeneral";
        LOGGER.debug(String.format("%s", method));

        XPathExpression expression = _xpathXml.compile("./General");
        Node general = (Node) expression.evaluate(_rootXml, XPathConstants.NODE);
        if (general == null || !general.hasChildNodes()) {
            return;
        }
        _hasGlobalSection = true;
        NodeList childs = general.getChildNodes();
        String section = "[globals]";
        writeLine(section);

        log(String.format("write %s", section));
        for (int i = 0; i < childs.getLength(); i++) {
            Node child = childs.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if ("notifications".equals(child.getNodeName().toLowerCase()) && child.hasChildNodes()) {
                    for (int j = 0; j < child.getChildNodes().getLength(); j++) {
                        Node nChild = child.getChildNodes().item(j);
                        if (nChild.getNodeType() == Node.ELEMENT_NODE && nChild.getAttributes().getNamedItem("ref") != null) {
                            String refName = nChild.getAttributes().getNamedItem("ref").getNodeValue();
                            if ("MailServerFragmentRef".equals(nChild.getNodeName())) {
                                if (_mailServerFragments.containsKey(refName)) {
                                    writeParams(_mailServerFragments.get(refName));
                                    writeNewLine();
                                }
                            } else if ("BackgroundServiceFragmentRef".equals(nChild.getNodeName())) {
                                String include = getFragmentInclude(nChild, "");
                                if (include != null) {
                                    writeLine(include);
                                }
                            }
                            continue;
                        }
                    }
                }
                if ("SystemPropertyFiles".equals(child.getNodeName())) {
                    String val = getMergedChildsEntry(child, "system_property_files");
                    if (!SOSString.isEmpty(val)) {
                        writeLine(val);
                        writeNewLine();
                    }
                } else {
                    handleChildNodes(child.getNodeName(), child, 0);
                    writeNewLine();
                }
            }
        }
    }

    private String getMergedChildsEntry(Node parent, String defaultParamName) {
        return getMergedChildsEntry(parent, defaultParamName, null);
    }

    private String getMergedChildsEntry(Node parent, String defaultParamName, String paramNamePrefix) {
        String method = "getMergedChildsEntry";
        LOGGER.debug(method);

        StringBuilder sb = new StringBuilder();
        NodeList childs = parent.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            Node child = childs.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String val = child.getFirstChild() == null ? child.getNodeValue() : getCdata(child);
                if (!SOSString.isEmpty(val)) {
                    if (sb.length() > 0) {
                        sb.append(";");
                    }
                    sb.append(val.trim());
                }
            }
        }
        String paramName = defaultParamName;
        if (sb.length() > 0) {
            try {
                String xpath = String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter[1]", parent.getNodeName());
                XPathExpression ex = _xpathSchema.compile(xpath);
                Node fp = (Node) ex.evaluate(_rootSchema, XPathConstants.NODE);
                if (fp != null) {
                    if (fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null) {
                        paramName = fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue();
                    }
                }
            } catch (Exception ex) {
                LOGGER.warn(String.format("error on parse flat parameter for \"%s\": %s", parent.getNodeName(), ex.toString()));
            }
        }
        String formatParamName = paramNamePrefix == null ? paramName : paramNamePrefix + paramName;
        return sb.length() == 0 ? null : formatParameter(formatParamName, sb.toString());
    }

    private void handleProtocolFragments() throws Exception {
        String method = "handleProtocolFragments";
        LOGGER.debug(method);

        XPathExpression expression = _xpathXml.compile("./Fragments/ProtocolFragments");
        Node fragments = (Node) expression.evaluate(_rootXml, XPathConstants.NODE);
        if (fragments == null) {
            throw new Exception(String.format("\"%s/Fragments/ProtocolFragments\" element not found", _rootXml.getNodeName()));
        }
        NodeList childs = fragments.getChildNodes();
        int childCount = 0;
        for (int i = 0; i < childs.getLength(); i++) {
            Node child = childs.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (!child.hasAttributes()) {
                    throw new Exception(String.format("attributes not found. node = %s", child.getNodeName()));
                }
                Node attrName = child.getAttributes().getNamedItem("name");
                if (attrName == null) {
                    throw new Exception(String.format("attribute \"name\" not found. node = %s", child.getNodeName()));
                }
                if (childCount == 0) {
                    if (!_hasGlobalSection) {
                        writeNewLine();
                    }
                } else {
                    writeNewLine();
                }
                String sectionName = getFragmentName(Fragment.protocolFragment, child);
                String section = "[" + sectionName + "]";
                writeLine(section);

                log(String.format("write %s", section));
                _countProtocolFragments++;
                XPathExpression ex = _xpathSchema.compile(String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter", child
                        .getNodeName()));
                NodeList flatParameters = (NodeList) ex.evaluate(_rootSchema, XPathConstants.NODESET);
                if (flatParameters != null) {
                    for (int j = 0; j < flatParameters.getLength(); j++) {
                        Node fp = flatParameters.item(j);
                        if (fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null && fp.getAttributes().getNamedItem(
                                SCHEMA_ATTRIBUTE_VALUE) != null) {
                            String param = formatParameter(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue(), fp.getAttributes()
                                    .getNamedItem(SCHEMA_ATTRIBUTE_VALUE).getNodeValue());
                            writeLine(param);
                            if (j == 0) {
                                writeNewLine();
                            }
                        }
                    }
                }
                String prefix = "";
                if (child.getNodeName().toLowerCase().startsWith("jump")) {
                    prefix = "jump_";
                }
                handleProtocolFragmentsChildNodes(child, sectionName, 0, 0, prefix, "");
                childCount++;
            }
        }
    }

    private void handleCredentialStoreFragments() throws Exception {
        String method = "handleCredentialStoreFragments";
        LOGGER.debug(method);

        XPathExpression expression = _xpathXml.compile("./Fragments/CredentialStoreFragments");
        Node fragments = (Node) expression.evaluate(_rootXml, XPathConstants.NODE);
        if (fragments == null) {
            return;
        }
        NodeList childs = fragments.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            Node child = childs.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (!child.hasAttributes()) {
                    throw new Exception(String.format("attributes not found. node = %s", child.getNodeName()));
                }
                Node attrName = child.getAttributes().getNamedItem("name");
                if (attrName == null) {
                    throw new Exception(String.format("attribute \"name\" not found. node = %s", child.getNodeName()));
                }
                writeNewLine();
                String sectionName = getFragmentName(Fragment.credentialStoreFragment, child);
                String section = "[" + sectionName + "]";
                writeLine(section);

                log(String.format("write %s", section));
                _countCredentialStoreFragments++;
                XPathExpression ex = _xpathSchema.compile(String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter", child
                        .getNodeName()));
                NodeList flatParameters = (NodeList) ex.evaluate(_rootSchema, XPathConstants.NODESET);
                if (flatParameters != null) {
                    for (int j = 0; j < flatParameters.getLength(); j++) {
                        Node fp = flatParameters.item(j);
                        if (fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null && fp.getAttributes().getNamedItem(
                                SCHEMA_ATTRIBUTE_VALUE) != null) {
                            String param = formatParameter(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue(), fp.getAttributes()
                                    .getNamedItem(SCHEMA_ATTRIBUTE_VALUE).getNodeValue());
                            writeLine(param);
                            if (j == 0) {
                                writeNewLine();
                            }
                        }
                    }
                }
                handleChildNodes(child.getNodeName(), child, 0);
            }
        }
    }

    private void handleNotificationBackgroundServiceFragments() throws Exception {
        String method = "handleNotificationBackgroundServiceFragments";
        LOGGER.debug(method);

        XPathExpression expression = _xpathXml.compile("./Fragments/NotificationFragments/BackgroundServiceFragment");
        NodeList fragments = (NodeList) expression.evaluate(_rootXml, XPathConstants.NODESET);
        if (fragments == null) {
            return;
        }
        for (int i = 0; i < fragments.getLength(); i++) {
            Node child = fragments.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (!child.hasAttributes()) {
                    throw new Exception(String.format("attributes not found. node = %s", child.getNodeName()));
                }
                Node attrName = child.getAttributes().getNamedItem("name");
                if (attrName == null) {
                    throw new Exception(String.format("attribute \"name\" not found. node = %s", child.getNodeName()));
                }
                writeNewLine();
                String sectionName = getFragmentName(Fragment.backgroundServiceFragment, child);
                String section = "[" + sectionName + "]";
                writeLine(section);

                log(String.format("write %s", section));
                _countNotificationBackgroundServiceFragments++;
                writeMainFlatParameters(child);
                handleChildNodes(child.getNodeName(), child, 0);
            }
        }
    }

    private void writeMainFlatParameters(Node child) throws Exception {
        XPathExpression ex = _xpathSchema.compile(String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter", child
                .getNodeName()));
        NodeList params = (NodeList) ex.evaluate(_rootSchema, XPathConstants.NODESET);
        if (params != null) {
            for (int i = 0; i < params.getLength(); i++) {
                Node fp = params.item(i);
                if (fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null && fp.getAttributes().getNamedItem(
                        SCHEMA_ATTRIBUTE_VALUE) != null) {
                    String param = formatParameter(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue(), fp.getAttributes()
                            .getNamedItem(SCHEMA_ATTRIBUTE_VALUE).getNodeValue());
                    writeLine(param);
                    if (i == 0) {
                        writeNewLine();
                    }
                }
            }
        }
    }

    private void handleNotificationMailFragments() throws Exception {
        String method = "handleNotificationMailFragments";
        LOGGER.debug(method);

        XPathExpression expression = _xpathXml.compile("./Fragments/NotificationFragments/MailFragment");
        NodeList fragments = (NodeList) expression.evaluate(_rootXml, XPathConstants.NODESET);
        if (fragments == null) {
            return;
        }
        NodeList childs = fragments;
        for (int i = 0; i < childs.getLength(); i++) {
            Node child = childs.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (!child.hasAttributes()) {
                    throw new Exception(String.format("attributes not found. node = %s", child.getNodeName()));
                }
                Node attrName = child.getAttributes().getNamedItem("name");
                if (attrName == null) {
                    throw new Exception(String.format("attribute \"name\" not found. node = %s", child.getNodeName()));
                }
                String mailFragment = child.getAttributes().getNamedItem("name").getNodeValue();

                log(String.format("found Notification MailFragment \"%s\"", mailFragment));
                _countNotificationMailFragments++;
                LinkedHashMap<String, String> mailFragmentParams = new LinkedHashMap<String, String>();
                XPathExpression ex = _xpathSchema.compile(String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter", child
                        .getNodeName()));
                NodeList flatParameters = (NodeList) ex.evaluate(_rootSchema, XPathConstants.NODESET);
                if (flatParameters != null) {
                    for (int j = 0; j < flatParameters.getLength(); j++) {
                        Node fp = flatParameters.item(j);
                        if (fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null && fp.getAttributes().getNamedItem(
                                SCHEMA_ATTRIBUTE_VALUE) != null) {
                            mailFragmentParams.put(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue(), fp.getAttributes()
                                    .getNamedItem(SCHEMA_ATTRIBUTE_VALUE).getNodeValue());
                        }
                    }
                }
                mailFragmentParams = handleNotificationMailFragmentsChildNodes(child, mailFragmentParams, 0);
                _mailFragments.put(mailFragment, mailFragmentParams);
            }
        }
    }

    private void handleMailServerFragments() throws Exception {
        String method = "handleMailServerFragments";
        LOGGER.debug(method);

        XPathExpression expression = _xpathXml.compile("./Fragments/MailServerFragments");
        Node fragments = (Node) expression.evaluate(_rootXml, XPathConstants.NODE);
        if (fragments == null) {
            return;
        }
        NodeList childs = fragments.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            Node child = childs.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (!child.hasAttributes()) {
                    throw new Exception(String.format("attributes not found. node = %s", child.getNodeName()));
                }
                Node attrName = child.getAttributes().getNamedItem("name");
                if (attrName == null) {
                    throw new Exception(String.format("attribute \"name\" not found. node = %s", child.getNodeName()));
                }
                String mailFragment = child.getAttributes().getNamedItem("name").getNodeValue();

                log(String.format("found MailServer Fragment \"%s\"", mailFragment));
                _countMailServerFragments++;
                LinkedHashMap<String, String> mailFragmentParams = new LinkedHashMap<String, String>();
                XPathExpression ex = _xpathSchema.compile(String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter", child
                        .getNodeName()));
                NodeList flatParameters = (NodeList) ex.evaluate(_rootSchema, XPathConstants.NODESET);
                if (flatParameters != null) {
                    for (int j = 0; j < flatParameters.getLength(); j++) {
                        Node fp = flatParameters.item(j);
                        if (fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null && fp.getAttributes().getNamedItem(
                                SCHEMA_ATTRIBUTE_VALUE) != null) {
                            mailFragmentParams.put(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue(), fp.getAttributes()
                                    .getNamedItem(SCHEMA_ATTRIBUTE_VALUE).getNodeValue());
                        }
                    }
                }
                mailFragmentParams = handleNotificationMailFragmentsChildNodes(child, mailFragmentParams, 0);
                _mailServerFragments.put(mailFragment, mailFragmentParams);
            }
        }
    }

    private void handleProfiles() throws Exception {
        String method = "handleProfiles";
        LOGGER.debug(method);

        XPathExpression expression = _xpathXml.compile("./Profiles/Profile");
        NodeList profiles = (NodeList) expression.evaluate(_rootXml, XPathConstants.NODESET);
        if (profiles == null) {
            throw new Exception(String.format("\"%s/Profiles/Profile\" elements not found", _rootXml.getNodeName()));
        }
        for (int i = 0; i < profiles.getLength(); i++) {
            Node profile = profiles.item(i);
            if (!profile.hasAttributes()) {
                throw new Exception(String.format("attributes not found. node = %s", profile.getNodeName()));
            }
            Node attrProfileId = profile.getAttributes().getNamedItem("profile_id");
            if (attrProfileId == null) {
                throw new Exception(String.format("attribute \"profile_id\" not found. node = %s", profile.getNodeName()));
            }
            writeNewLine();
            String sectionName = getProfileName(profile, attrProfileId);
            String section = "[" + sectionName + "]";
            writeLine(section);

            log(String.format("write %s", section));
            _countProfiles++;
            _profileJumpInclude = null;
            NodeList childs = profile.getChildNodes();
            for (int j = 0; j < childs.getLength(); j++) {
                Node child = childs.item(j);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    if ("operation".equalsIgnoreCase(child.getNodeName())) {
                        handleProfileOperation(child, sectionName, "", 0, 0, "", "");
                        handleProfileAlternativeOperation(child, sectionName);
                    } else if ("client".equalsIgnoreCase(child.getNodeName())) {
                        writeNewLine();
                        handleChildNodes(child.getNodeName(), child, 0);
                    } else if ("logging".equalsIgnoreCase(child.getNodeName())) {
                        writeNewLine();
                        handleChildNodes(child.getNodeName(), child, 0);
                    } else if ("jobscheduler".equalsIgnoreCase(child.getNodeName())) {
                        writeNewLine();
                        handleChildNodes(child.getNodeName(), child, 0);
                    } else if ("notifications".equalsIgnoreCase(child.getNodeName())) {
                        writeNewLine();
                        handleProfileNotification(child.getNodeName(), child, 0);
                    } else if ("notificationtriggers".equalsIgnoreCase(child.getNodeName())) {
                        writeNewLine();
                        handleProfileNotification(child.getNodeName(), child, 0);
                    } else if ("systempropertyfiles".equalsIgnoreCase(child.getNodeName())) {
                        writeNewLine();
                        String val = getMergedChildsEntry(child, "system_property_files");
                        if (!SOSString.isEmpty(val)) {
                            writeLine(val);
                        }
                    }
                }
            }
        }
    }

    private void handleProfileAlternativeOperation(Node operationNode, String sectionName) throws Exception {
        String xpath = String.format(".//*[contains(local-name(),'Alternative')]");
        XPathExpression ex = _xpathXml.compile(xpath);
        NodeList nodes = (NodeList) ex.evaluate(operationNode, XPathConstants.NODESET);
        if (nodes != null) {
            for (int i = 0; i < nodes.getLength(); i++) {
                writeNewLine();
                Node alternative = nodes.item(i);
                String prefix = alternative.getNodeName().contains("Target") ? "target_" : "source_";
                xpath = String.format(".//*[1]");
                ex = _xpathXml.compile(xpath);
                Node firstChild = (Node) ex.evaluate(alternative, XPathConstants.NODE);
                if (firstChild == null) {
                    LOGGER.warn(String.format("\"%s\": first child not found on \"%s\" element", sectionName, alternative.getNodeName()));
                    _countWarnings++;
                    continue;
                }
                if (firstChild.getAttributes() != null && firstChild.getAttributes().getNamedItem("ref") != null) {
                    String elementName = firstChild.getNodeName().substring(0, firstChild.getNodeName().length() - 3);
                    String refName = firstChild.getAttributes().getNamedItem("ref").getNodeValue();
                    xpath = String.format("./Fragments/ProtocolFragments/%s[@name='%s']", elementName, refName);
                    ex = _xpathXml.compile(xpath);
                    Node protocolFragment = (Node) ex.evaluate(_rootXml, XPathConstants.NODE);
                    if (protocolFragment == null) {
                        LOGGER.warn(String.format("\"%s\": not found Protocol Fragment \"%s\" for %s", sectionName, xpath, alternative
                                .getNodeName()));
                        _countWarnings++;
                        writeLine(formatParameter("alternative_" + prefix + "include", refName));
                    } else {
                        writeLine(formatParameter("alternative_" + prefix + "include", getFragmentName(Fragment.protocolFragment, protocolFragment)));
                    }
                    _countAlternativeFragments++;
                }
                handleAlternativeProtocolFragments(alternative, 0, 0, "alternative_" + prefix, "");
            }
        }
    }

    private void handleChildNodes(String parentNodeName, Node xmlNode, int level) throws Exception {
        if (xmlNode.hasChildNodes()) {
            level++;
            NodeList childs = xmlNode.getChildNodes();
            for (int i = 0; i < childs.getLength(); i++) {
                Node child = childs.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    if (child.getAttributes().getNamedItem("ref") != null) {
                        continue;
                    }
                    String xpath = String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter[1]", child.getNodeName());
                    XPathExpression ex = _xpathSchema.compile(xpath);
                    Node fp = (Node) ex.evaluate(_rootSchema, XPathConstants.NODE);
                    if (fp != null && fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null) {
                        String name = fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue();
                        String value = getParameterValue(fp, child);
                        writeLine(formatParameter(name, value));
                    }
                    handleChildNodes(parentNodeName, child, level);
                }
            }
        }
    }

    private void writeNotification2Profile(String parentNodeName, LinkedHashMap<String, String> values) throws Exception {
        String prefix = "";
        if ("OnSuccess".equals(parentNodeName)) {
            prefix = "mail_on_success_";
        } else if ("OnError".equals(parentNodeName)) {
            prefix = "mail_on_error_";
        } else if ("OnEmptyFiles".equals(parentNodeName)) {
            prefix = "mail_on_empty_files_";
        }
        for (Entry<String, String> entry : values.entrySet()) {
            writeLine(formatParameter(prefix + entry.getKey(), entry.getValue()));
        }
    }

    private void writeParams(LinkedHashMap<String, String> values) throws Exception {
        for (Entry<String, String> entry : values.entrySet()) {
            writeLine(formatParameter(entry.getKey(), entry.getValue()));
        }
    }

    private void handleProfileNotification(String parentNodeName, Node xmlNode, int level) throws Exception {
        if (xmlNode.hasChildNodes()) {
            level++;
            NodeList childs = xmlNode.getChildNodes();
            for (int i = 0; i < childs.getLength(); i++) {
                Node child = childs.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    if (child.getAttributes().getNamedItem("ref") != null) {
                        String refName = child.getAttributes().getNamedItem("ref").getNodeValue();
                        if ("MailFragmentRef".equals(child.getNodeName())) {
                            if (_mailFragments.containsKey(refName)) {
                                writeNotification2Profile(child.getParentNode().getNodeName(), _mailFragments.get(refName));
                            }
                        } else if ("MailServerFragmentRef".equals(child.getNodeName())) {
                            if (_mailServerFragments.containsKey(refName)) {
                                writeParams(_mailServerFragments.get(refName));
                            }
                        } else if ("BackgroundServiceFragmentRef".equals(child.getNodeName())) {
                            String include = getFragmentInclude(child, "");
                            if (include != null) {
                                writeLine(include);
                            }
                        }
                        continue;
                    }
                    String xpath = String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter[1]", child.getNodeName());
                    XPathExpression ex = _xpathSchema.compile(xpath);
                    Node fp = (Node) ex.evaluate(_rootSchema, XPathConstants.NODE);
                    if (fp != null) {
                        if (fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_MAIL_NAME) != null) {
                            String name = fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_MAIL_NAME).getNodeValue();
                            String value = getParameterValue(fp, child);
                            writeLine(formatParameter(name, value));
                        } else if (fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null) {
                            String name = fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue();
                            String value = getParameterValue(fp, child);
                            writeLine(formatParameter(name, value));
                        }
                    }
                    handleProfileNotification(parentNodeName, child, level);
                }
            }
        }
    }

    private void handleAlternativeProtocolFragments(Node xmlNode, int level, int prefixLevel, String parentPrefix, String childPrefix)
            throws Exception {
        String method = "handleAlternativeProtocolFragments";
        LOGGER.debug(method);

        if (level == 0) {
            String xpathP = String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter", xmlNode.getNodeName());
            XPathExpression exP = _xpathSchema.compile(xpathP);
            NodeList fpList = (NodeList) exP.evaluate(_rootSchema, XPathConstants.NODESET);
            if (fpList != null) {
                for (int i = 0; i < fpList.getLength(); i++) {
                    Node fpP = fpList.item(i);
                    if (fpP.getAttributes() == null) {
                        continue;
                    }
                    if (i > 0 && fpP.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE) == null) {
                        continue;
                    }
                    String nameP = null;
                    if (fpP.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_ALTERNATIVE_NAME) != null) {
                        nameP = fpP.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_ALTERNATIVE_NAME).getNodeValue();
                    } else if (fpP.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null) {
                        nameP = fpP.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue();
                    }
                    if (nameP != null) {
                        if (level <= prefixLevel) {
                            prefixLevel = 0;
                            childPrefix = "";
                        }
                        if (!childPrefix.isEmpty() && !nameP.toLowerCase().startsWith(childPrefix)) {
                            nameP = childPrefix + nameP;
                        }

                        if (!parentPrefix.isEmpty() && !"jump_".equals(parentPrefix) && !nameP.toLowerCase().startsWith(parentPrefix)) {
                            nameP = parentPrefix + nameP;
                        }
                        String valueP = getParameterValue(fpP, xmlNode);
                        writeLine(formatParameter(nameP, valueP));
                    }
                }
            }
        }
        if (xmlNode.hasChildNodes()) {
            level++;
            NodeList childs = xmlNode.getChildNodes();
            for (int i = 0; i < childs.getLength(); i++) {
                Node child = childs.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    boolean hasRef = false;
                    if (child.getAttributes().getNamedItem("ref") != null) {
                        hasRef = true;
                    }
                    if (!hasRef) {
                        String xpath = String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter", child.getNodeName());
                        XPathExpression ex = _xpathSchema.compile(xpath);
                        NodeList fpList = (NodeList) ex.evaluate(_rootSchema, XPathConstants.NODESET);
                        if (fpList != null) {
                            for (int j = 0; j < fpList.getLength(); j++) {
                                Node fp = fpList.item(j);
                                if (fp.getAttributes() == null) {
                                    continue;
                                }
                                if (j > 0 && fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE) == null) {
                                    continue;
                                }
                                String name = null;
                                if (fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_ALTERNATIVE_NAME) != null) {
                                    name = fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_ALTERNATIVE_NAME).getNodeValue();
                                } else if (fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null) {
                                    name = fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue();
                                }
                                if (name != null) {
                                    if (level <= prefixLevel) {
                                        prefixLevel = 0;
                                        childPrefix = "";
                                    }
                                    if (!childPrefix.isEmpty() && !name.toLowerCase().startsWith(childPrefix)) {
                                        name = childPrefix + name;
                                    }
                                    if (!parentPrefix.isEmpty() && !"jump_".equals(parentPrefix) && !name.toLowerCase().startsWith(parentPrefix)) {
                                        name = parentPrefix + name;
                                    }
                                    String value = getParameterValue(fp, child);
                                    writeLine(formatParameter(name, value));
                                }
                            }
                        }
                    }
                    if (child.getNodeName().toLowerCase().startsWith("proxy")) {
                        childPrefix = "proxy_";
                        prefixLevel = level;
                    }
                    handleAlternativeProtocolFragments(child, level, prefixLevel, parentPrefix, childPrefix);
                }
            }
        }
    }

    private void handleProtocolFragmentsChildNodes(Node xmlNode, String sectionName, int level, int prefixLevel, String parentPrefix,
            String childPrefix) throws Exception {
        if (xmlNode.hasChildNodes()) {
            level++;
            NodeList childs = xmlNode.getChildNodes();
            for (int i = 0; i < childs.getLength(); i++) {
                Node child = childs.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    if ("ConfigurationFiles".equals(child.getNodeName())) {
                        String val = getMergedChildsEntry(child, "configuration_files");
                        if (!SOSString.isEmpty(val)) {
                            writeLine(val);
                        }
                        continue;
                    } else if ("HTTPHeaders".equals(child.getNodeName())) {
                        String val = getMergedChildsEntry(child, "http_headers");
                        if (!SOSString.isEmpty(val)) {
                            writeLine(val);
                        }
                        continue;
                    }
                    if (child.getAttributes().getNamedItem("ref") != null) {
                        if ("JumpFragmentRef".equals(child.getNodeName())) {
                            String include = getFragmentInclude(child, "jump_");
                            _jumpIncludes.put(sectionName, include);
                        } else if ("CredentialStoreFragmentRef".equals(child.getNodeName())) {
                            String include = getFragmentInclude(child, "");
                            _credentialStoreIncludes.put(sectionName, include);
                        } else {
                            String include = getFragmentInclude(child, "");
                            if (include != null) {
                                writeNewLine();
                                writeLine(include);
                                writeNewLine();
                            }
                        }
                        continue;
                    }
                    String xpath = String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter[1]", child.getNodeName());
                    XPathExpression ex = _xpathSchema.compile(xpath);
                    Node fp = (Node) ex.evaluate(_rootSchema, XPathConstants.NODE);
                    if (fp != null && fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null) {
                        if (level <= prefixLevel) {
                            prefixLevel = 0;
                            childPrefix = "";
                        }
                        String name = fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue();
                        if (!childPrefix.isEmpty() && !name.toLowerCase().startsWith(childPrefix)) {
                            name = childPrefix + name;
                        }
                        if (!parentPrefix.isEmpty() && !"jump_".equals(parentPrefix) && !name.toLowerCase().startsWith(parentPrefix)) {
                            name = parentPrefix + name;
                        }
                        String value = getParameterValue(fp, child);
                        writeLine(formatParameter(name, value));
                    }
                    if (child.getNodeName().toLowerCase().startsWith("proxy")) {
                        childPrefix = "proxy_";
                        prefixLevel = level;
                    }
                    handleProtocolFragmentsChildNodes(child, sectionName, level, prefixLevel, parentPrefix, childPrefix);
                }
            }
        }
    }

    private void handleProfileOperation(Node xmlNode, String sectionName, String operation, int level, int prefixLevel, String parentPrefix,
            String childPrefix) throws Exception {
        if (xmlNode.hasChildNodes()) {
            level++;
            NodeList childs = xmlNode.getChildNodes();
            for (int i = 0; i < childs.getLength(); i++) {
                Node child = childs.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    if (child.getNodeName().startsWith("Alternative")) {
                        continue;
                    } else if (child.getNodeName().equalsIgnoreCase("HTTPHeaders")) {
                        if (!SOSString.isEmpty(childPrefix)) {
                            writeLine(getMergedChildsEntry(child, "http_headers", childPrefix));
                        }
                        continue;
                    }
                    if (level <= prefixLevel) {
                        prefixLevel = 0;
                        childPrefix = "";
                        if (parentPrefix.isEmpty()) {
                            writeNewLine();
                        }
                    }
                    if (child.getAttributes().getNamedItem("ref") != null) {
                        String include = getFragmentInclude(child, childPrefix);
                        if (include != null) {
                            String[] arr = include.split("=");
                            if (arr.length == 2) {
                                String includeName = arr[1].trim().split(",")[0].trim();
                                if (_jumpIncludes.containsKey(includeName)) {
                                    String jumpInclude = _jumpIncludes.get(includeName);
                                    if ("copyfrominternet".equals(operation) || "copytointernet".equals(operation) || "remove".equals(operation)
                                            || "getlist".equals(operation)) {
                                        if (_profileJumpInclude == null) {
                                            String[] jumpArr = jumpInclude.split("=");
                                            String jumpName = jumpArr.length > 0 ? jumpArr[1].trim() : null;
                                            if (jumpName != null) {
                                                if (_credentialStoreIncludes.containsKey(jumpName)) {
                                                    String csInclude = _credentialStoreIncludes.get(jumpName);
                                                    String[] csArr = csInclude.split("=");
                                                    if (arr.length == 2) {
                                                        jumpInclude += "," + csArr[1].trim();
                                                    }
                                                }
                                            }

                                            writeLine(jumpInclude);
                                            writeNewLine();
                                            _profileJumpInclude = jumpInclude;
                                        } else {
                                            LOGGER.warn(String.format("Profile [%s]: include of \"%s\" skipped(\"%s\" already included)", sectionName,
                                                    jumpInclude.replaceAll(" ", ""), _profileJumpInclude.replaceAll(" ", "")));
                                            _countWarnings++;
                                        }
                                    } else {
                                        LOGGER.warn(String.format(
                                                "Profile [%s]: include of \"%s\" skipped(jump host with operation \"%s\" is not implemented)",
                                                sectionName, jumpInclude.replaceAll(" ", ""), operation));
                                        _countWarnings++;
                                    }
                                }
                                if (_credentialStoreIncludes.containsKey(includeName)) {
                                    String csInclude = _credentialStoreIncludes.get(includeName);
                                    arr = csInclude.split("=");
                                    if (arr.length == 2) {
                                        include += "," + arr[1].trim();
                                    } else {
                                        LOGGER.warn(String.format(
                                                "Profile [%s]: CredentialStore fragment cannot be included. Missing \"=\" character in \"%s\"",
                                                sectionName, csInclude));
                                        _countWarnings++;
                                    }
                                }
                            }
                            writeLine(include);
                        }
                    }
                    handleAttributes(child, parentPrefix, childPrefix);
                    String xpath = String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter[1]", child.getNodeName());
                    XPathExpression ex = _xpathSchema.compile(xpath);
                    Node fp = (Node) ex.evaluate(_rootSchema, XPathConstants.NODE);
                    if (fp != null && fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null) {
                        String name = fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue();
                        Node suppressPrefix = fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_SUPPRESS_PREFIX);
                        if (!(suppressPrefix != null && "true".equalsIgnoreCase(suppressPrefix.getNodeValue()))) {
                            if (!childPrefix.isEmpty() && !name.toLowerCase().startsWith(childPrefix)) {
                                name = childPrefix + name;
                            }
                            if (!parentPrefix.isEmpty() && !name.toLowerCase().startsWith(parentPrefix)) {
                                name = parentPrefix + name;
                            }
                        }
                        String value = getParameterValue(fp, child);
                        StringBuilder addition = null;
                        if (fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE) != null) {
                            addition = new StringBuilder();
                            Node sibling = fp.getNextSibling();
                            while (sibling != null) {
                                if (sibling.getNodeType() == Node.ELEMENT_NODE && sibling.getAttributes() != null && sibling.getAttributes()
                                        .getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null && sibling.getAttributes().getNamedItem(
                                                SCHEMA_ATTRIBUTE_VALUE) != null) {
                                    String addName = sibling.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue();
                                    if (!childPrefix.isEmpty() && !addName.toLowerCase().startsWith(childPrefix)) {
                                        addName = childPrefix + addName;
                                    }
                                    if (addition.length() > 0) {
                                        addition.append(System.getProperty("line.separator"));
                                    }
                                    addition.append(formatParameter(addName, sibling.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE)
                                            .getNodeValue()));
                                }
                                sibling = sibling.getNextSibling();
                            }
                        }
                        if (level == 1 && "operation".equals(name)) {
                            value = getProfileOperationValue(child, sectionName, value);
                            operation = value;
                        }
                        writeLine(formatParameter(name, value));
                        if (addition != null && addition.length() > 0) {
                            writeLine(addition.toString());
                        }
                    }
                    if (child.getNodeName().toLowerCase().endsWith("source")) {
                        childPrefix = "source_";
                        prefixLevel = level;
                    } else if (child.getNodeName().toLowerCase().endsWith("target")) {
                        childPrefix = "target_";
                        prefixLevel = level;
                    }
                    if (level == 1) {
                        writeNewLine();
                    }
                    handleProfileOperation(child, sectionName, operation, level, prefixLevel, parentPrefix, childPrefix);
                }
            }
        }
    }

    private void handleAttributes(Node child, String parentPrefix, String childPrefix) throws Exception {
        String xpath = String.format(
                "./xs:element[@name='%s']/xs:complexType/xs:simpleContent/xs:extension[@base='NotEmptyType']/xs:attribute/xs:annotation/xs:appinfo/FlatParameter[1]",
                child.getNodeName());
        XPathExpression ex = _xpathSchema.compile(xpath);
        NodeList fpl = (NodeList) ex.evaluate(_rootSchema, XPathConstants.NODESET);
        if (fpl != null && fpl.getLength() > 0) {
            for (int i = 0; i < fpl.getLength(); i++) {
                Node fp = fpl.item(i);
                if (fp != null && fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null) {
                    Node attributeNode = null;
                    try {
                        attributeNode = fp.getParentNode().getParentNode().getParentNode();
                    } catch (Throwable e) {
                        LOGGER.error(e.toString(), e);
                    }
                    if (attributeNode == null) {
                        continue;
                    }
                    String name = fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue();
                    Node suppressPrefix = fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_SUPPRESS_PREFIX);
                    if (!(suppressPrefix != null && "true".equalsIgnoreCase(suppressPrefix.getNodeValue()))) {
                        if (!childPrefix.isEmpty() && !name.toLowerCase().startsWith(childPrefix)) {
                            name = childPrefix + name;
                        }
                        if (!parentPrefix.isEmpty() && !name.toLowerCase().startsWith(parentPrefix)) {
                            name = parentPrefix + name;
                        }
                    }
                    String value = getAttributeValue(fp, child, attributeNode);
                    StringBuilder addition = null;
                    if (fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE) != null) {
                        addition = new StringBuilder();
                        Node sibling = fp.getNextSibling();
                        while (sibling != null) {
                            if (sibling.getNodeType() == Node.ELEMENT_NODE && sibling.getAttributes() != null && sibling.getAttributes().getNamedItem(
                                    SCHEMA_ATTRIBUTE_NAME) != null && sibling.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE) != null) {
                                String addName = sibling.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue();
                                if (!childPrefix.isEmpty() && !addName.toLowerCase().startsWith(childPrefix)) {
                                    addName = childPrefix + addName;
                                }
                                if (addition.length() > 0) {
                                    addition.append(System.getProperty("line.separator"));
                                }
                                addition.append(formatParameter(addName, sibling.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE)
                                        .getNodeValue()));
                            }
                            sibling = sibling.getNextSibling();
                        }
                    }
                    writeLine(formatParameter(name, value));
                    if (addition != null && addition.length() > 0) {
                        writeLine(addition.toString());
                    }
                }
            }
        }
    }

    private String getProfileOperationValue(Node node, String sectionName, String value) throws Exception {
        if (("copy".equals(value) || "move".equals(value)) && !_jumpIncludes.isEmpty()) {
            String xpath = String.format(".//*[contains(local-name(),'Source')]/*[string-length(@ref)!=0]");
            XPathExpression ex = _xpathXml.compile(xpath);
            Node fp = (Node) ex.evaluate(node, XPathConstants.NODE);
            if (fp != null) {
                if ("ReadableAlternativeFragmentRef".equals(fp.getNodeName()) || "WriteadableAlternativeFragmentRef".equals(fp.getNodeName())) {
                    String altName = fp.getNodeName().substring(0, fp.getNodeName().length() - 3);
                    xpath = String.format("./Fragments/AlternativeFragments/%s[@name='%s']/*[string-length(@ref)!=0][1]", altName, fp.getAttributes()
                            .getNamedItem("ref").getNodeValue());
                    ex = _xpathXml.compile(xpath);
                    Node altFp = (Node) ex.evaluate(_rootXml, XPathConstants.NODE);
                    if (altFp != null) {
                        fp = altFp;
                    }
                }
                for (Entry<String, String> entry : _jumpIncludes.entrySet()) {
                    String[] arr = entry.getKey().split("@");
                    if (arr.length > 1) {
                        String fragmentXmlName = arr[1].trim();
                        if (fragmentXmlName.equals(fp.getAttributes().getNamedItem("ref").getNodeValue())) {
                            return "copyfrominternet";
                        }
                    }
                }
            }
            xpath = String.format(".//*[contains(local-name(),'Target')]/*[string-length(@ref)!=0]");
            ex = _xpathXml.compile(xpath);
            fp = (Node) ex.evaluate(node, XPathConstants.NODE);
            if (fp != null) {
                if ("ReadableAlternativeFragmentRef".equals(fp.getNodeName()) || "WriteadableAlternativeFragmentRef".equals(fp.getNodeName())) {
                    String altName = fp.getNodeName().substring(0, fp.getNodeName().length() - 3);
                    xpath = String.format("./Fragments/AlternativeFragments/%s[@name='%s']/*[string-length(@ref)!=0][1]", altName, fp.getAttributes()
                            .getNamedItem("ref").getNodeValue());

                    ex = _xpathXml.compile(xpath);
                    Node altFp = (Node) ex.evaluate(_rootXml, XPathConstants.NODE);
                    if (altFp != null) {
                        fp = altFp;
                    }
                }
                for (Entry<String, String> entry : _jumpIncludes.entrySet()) {
                    String[] arr = entry.getKey().split("@");
                    if (arr.length > 1) {
                        String fragmentXmlName = arr[1].trim();
                        if (fragmentXmlName.equals(fp.getAttributes().getNamedItem("ref").getNodeValue())) {
                            return "copytointernet";
                        }
                    }
                }
            }
        }
        return value;
    }

    private LinkedHashMap<String, String> handleNotificationMailFragmentsChildNodes(Node xmlNode, LinkedHashMap<String, String> mailFragmentParams,
            int level) throws Exception {
        if (xmlNode.hasChildNodes()) {
            level++;
            NodeList childs = xmlNode.getChildNodes();
            for (int i = 0; i < childs.getLength(); i++) {
                Node child = childs.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    String xpath = String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter[1]", child.getNodeName());
                    XPathExpression ex = _xpathSchema.compile(xpath);
                    Node fp = (Node) ex.evaluate(_rootSchema, XPathConstants.NODE);
                    if (fp != null) {
                        if (fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_MAIL_NAME) != null) {
                            mailFragmentParams.put(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_MAIL_NAME).getNodeValue(), getParameterValue(fp,
                                    child));
                        } else if (fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null) {
                            mailFragmentParams.put(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue(), getParameterValue(fp,
                                    child));
                        }
                    }
                    mailFragmentParams = handleNotificationMailFragmentsChildNodes(child, mailFragmentParams, level);
                }
            }
        }
        return mailFragmentParams;
    }

    private String getFragmentName(Fragment fragment, Node node) {
        StringBuilder sb = new StringBuilder();
        if (fragment.equals(Fragment.protocolFragment)) {
            sb.append("protocol_fragment_");
            sb.append(node.getNodeName().toLowerCase().replace("fragment", ""));
        } else if (fragment.equals(Fragment.credentialStoreFragment)) {
            sb.append("credentialstore_fragment");
        } else if (fragment.equals(Fragment.backgroundServiceFragment)) {
            sb.append("background_service_fragment");
        }
        sb.append("@");
        sb.append(node.getAttributes().getNamedItem("name").getNodeValue());
        return sb.toString();
    }

    private String getProfileName(Node node, Node attr) {
        return attr.getNodeValue();
    }

    private String getFragmentInclude(Node node, String prefix) throws Exception {
        String include = null;
        String ref = node.getAttributes().getNamedItem("ref").getNodeValue();
        String name = node.getNodeName().substring(0, node.getNodeName().length() - 3);
        String fragments = "ProtocolFragments";
        Fragment fragment = Fragment.protocolFragment;
        if ("CredentialStoreFragmentRef".equalsIgnoreCase(node.getNodeName())) {
            fragments = "CredentialStoreFragments";
            fragment = Fragment.credentialStoreFragment;
        } else if ("BackgroundServiceFragmentRef".equalsIgnoreCase(node.getNodeName())) {
            fragments = "NotificationFragments";
            fragment = Fragment.backgroundServiceFragment;
        }
        String xpath = String.format("./Fragments/%s/%s[@name='%s']", fragments, name, ref);
        XPathExpression ex = _xpathXml.compile(xpath);
        Node fp = (Node) ex.evaluate(_rootXml, XPathConstants.NODE);
        if (fp == null) {
            include = formatParameter(prefix + "include", ref);
            LOGGER.warn(String.format("Node %s[@ref = %s]. Not found referenced Fragments/%s/%s[@name='%s'].", node.getNodeName(), ref, fragments,
                    name, ref));
            _countWarnings++;
        } else {
            include = formatParameter(prefix + "include", getFragmentName(fragment, fp));
        }
        return include;
    }

    private String getParameterValue(Node flatParameter, Node node) {
        String value = "";
        if (flatParameter.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE) == null) {
            if (node.getFirstChild() != null) {
                value = getCdata(node);
                Node oppositeValue = flatParameter.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_OPPOSITE_VALUE);
                if (oppositeValue != null && "true".equals(oppositeValue.getNodeValue())) {
                    value = "true".equalsIgnoreCase(value) ? "false" : "true";
                }
            }
        } else {
            value = flatParameter.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE).getNodeValue();
        }
        return value;
    }

    private String getCdata(Node node) {
        NodeList childs = node.getChildNodes();
        if (childs != null) {
            for (int i = 0; i < childs.getLength(); i++) {
                Node child = childs.item(i);
                if (child.getNodeType() == Node.CDATA_SECTION_NODE) {
                    return child.getTextContent();
                }
            }
            return node.getFirstChild().getNodeValue();
        }
        return "";
    }

    private String getAttributeValue(Node flatParameter, Node node, Node attributeNode) {
        String value = "";
        try {
            if (flatParameter.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE) == null) {
                Node attrName = attributeNode.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME);
                if (attrName != null) {
                    Node valueNode = node.getAttributes().getNamedItem(attrName.getNodeValue());
                    if (valueNode != null) {
                        value = valueNode.getNodeValue();
                        Node oppositeValue = flatParameter.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_OPPOSITE_VALUE);
                        if (oppositeValue != null && "true".equals(oppositeValue.getNodeValue())) {
                            value = "true".equalsIgnoreCase(value) ? "false" : "true";
                        }
                    } else {
                        Node defaultValue = attributeNode.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_DEFAULT_VALUE);
                        if (defaultValue != null) {
                            value = defaultValue.getNodeValue();
                        }
                    }
                }
            } else {
                value = flatParameter.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE).getNodeValue();
            }
        } catch (Throwable e) {
            LOGGER.error(String.format("[%s]%s", node.getNodeName(), e.toString()), e);
        }
        return value;
    }

    private String formatParameter(String name, String value) {
        return String.format("%-43s = %s", name, value);
    }

    private void writeLine(String line) throws Exception {
        _writer.write(line);
        writeNewLine();
    }

    private void writeNewLine() throws Exception {
        _writer.write(NEW_LINE);
    }

    private static void setLogger(String path) throws Exception {
        if (!SOSString.isEmpty(path)) {
            File file = new File(path);
            if (file.isFile() && file.canRead()) {
                LoggerContext context = (LoggerContext) LogManager.getContext(false);
                context.setConfigLocation(file.toURI());
            }
        }
    }

    private void log(String msg) {
        if (_mainMethodCalled) {
            LOGGER.info(msg);
        } else {
            LOGGER.debug(msg);
        }
    }

    public boolean isMainMethodCalled() {
        return _mainMethodCalled;
    }

    protected void setMainMethodCalled(boolean val) {
        _mainMethodCalled = val;
    }

    private NamespaceContext getSchemaNamespaceContext() {
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