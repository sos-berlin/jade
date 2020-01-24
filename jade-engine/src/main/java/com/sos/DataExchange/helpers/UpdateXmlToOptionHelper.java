package com.sos.DataExchange.helpers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sos.xml.SOSXMLXPath;

import com.sos.DataExchange.Options.JADEOptions;

public class UpdateXmlToOptionHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateXmlToOptionHelper.class);
    private JADEOptions options;

    /**
     * Constructor to instantiate class with the related Options
     * 
     * @param jadeOptions
     */
    public UpdateXmlToOptionHelper(JADEOptions jadeOptions) {
        this.options = jadeOptions;
    }

    public boolean checkBefore() {
        return options.updateConfiguration.value();
    }

    public void executeBefore() {
        LOGGER.debug(options.xmlUpdate.getValue());
        extractOptionsFromXmlSnippet(options.xmlUpdate.getValue());
    }

    /**
     * Reads specific items from the given {@link Node} and returns a {@link Map} with the values
     * 
     * @param node to read the values from
     * @return Map with the selected values
     */
    private Map<String, String> getCredentialsFromFragmentNode(Node node) {
        Map<String, String> credentials = new HashMap<String, String>();
        NodeList childNodes = node.getChildNodes();
        switch (node.getNodeName()) {
        case "FTPFragment":
            credentials.put("protocol", "ftp");
        case "FTPSFragment":
            if (credentials.get("protocol") == null || credentials.get("protocol").isEmpty()) {
                credentials.put("protocol", "ftps");
            }
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node currentNode = childNodes.item(i);
                if ("BasicConnection".equalsIgnoreCase(currentNode.getNodeName())) {
                    credentials.put("host", currentNode.getFirstChild().getFirstChild().getNodeValue());
                } else if ("BasicAuthentication".equalsIgnoreCase(currentNode.getNodeName())) {
                    credentials.put("user", currentNode.getFirstChild().getFirstChild().getNodeValue());
                    credentials.put("password", currentNode.getFirstChild().getNextSibling().getFirstChild().getNodeValue());
                }
            }
            break;
        case "HTTPFragment":
            credentials.put("protocol", "http");
        case "HTTPSFragment":
            if (credentials.get("protocol") == null || credentials.get("protocol").isEmpty()) {
                credentials.put("protocol", "https");
            }
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node currentNode = childNodes.item(i);
                if ("URLConnection".equalsIgnoreCase(currentNode.getNodeName())) {
                    credentials.put("connectionUrl", currentNode.getFirstChild().getFirstChild().getNodeValue());
                }
            }
            break;
        case "JumpFragment":
            credentials.put("protocol", "sftp");
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node currentNode = childNodes.item(i);
                if ("BasicConnection".equalsIgnoreCase(currentNode.getNodeName())) {
                    credentials.put("host", currentNode.getFirstChild().getFirstChild().getNodeValue());
                } else if ("SSHAuthentication".equalsIgnoreCase(currentNode.getNodeName())) {
                    credentials.put("user", currentNode.getFirstChild().getFirstChild().getNodeValue());
                    Node authMethod = currentNode.getFirstChild().getNextSibling();
                    if (authMethod.getNodeName().contains("Password")) {
                        credentials.put("authMethodName", "password");
                        credentials.put("password", authMethod.getFirstChild().getFirstChild().getNodeValue());
                    } else {
                        credentials.put("authMethodName", "publickey");
                        credentials.put("publickey", authMethod.getFirstChild().getFirstChild().getNodeValue());
                    }
                } else if ("JumpCommand".equalsIgnoreCase(currentNode.getNodeName())) {
                    credentials.put("JumpCommand", currentNode.getFirstChild().getNodeValue());
                }
            }
            break;
        case "SFTPFragment":
            credentials.put("protocol", "sftp");
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node currentNode = childNodes.item(i);
                if ("BasicConnection".equalsIgnoreCase(currentNode.getNodeName())) {
                    credentials.put("host", currentNode.getFirstChild().getFirstChild().getNodeValue());
                } else if ("SSHAuthentication".equalsIgnoreCase(currentNode.getNodeName())) {
                    credentials.put("user", currentNode.getFirstChild().getFirstChild().getNodeValue());
                    Node authMethod = currentNode.getFirstChild().getNextSibling();
                    if (authMethod.getNodeName().contains("Password")) {
                        credentials.put("authMethodName", "password");
                        credentials.put("password", authMethod.getFirstChild().getFirstChild().getNodeValue());
                    } else {
                        credentials.put("authMethodName", "publickey");
                        credentials.put("publickey", authMethod.getFirstChild().getFirstChild().getNodeValue());
                    }
                }
            }
            break;
        case "SMBFragment":
            credentials.put("protocol", "smb");
            if ("Hostname".equalsIgnoreCase(node.getNodeName())) {
                credentials.put("host", node.getFirstChild().getNodeValue());
            } else if ("SMBAuthentication".equalsIgnoreCase(node.getNodeName())) {
                credentials.put("user", node.getFirstChild().getFirstChild().getNodeValue());
                credentials.put("password", node.getFirstChild().getNextSibling().getFirstChild().getNodeValue());
            }
            break;
        case "WebDAVFragment":
            credentials.put("protocol", "WebDAV");
            if ("URLConnection".equalsIgnoreCase(node.getNodeName())) {
                credentials.put("connectionUrl", node.getFirstChild().getFirstChild().getNodeValue());
            } else if ("BasicAuthentication".equalsIgnoreCase(node.getNodeName())) {
                credentials.put("user", node.getFirstChild().getFirstChild().getNodeValue());
                credentials.put("password", node.getFirstChild().getNextSibling().getFirstChild().getNodeValue());
            }
            break;
        default:
            break;
        }
        return credentials;
    }

    /**
     * Extracts the given values from given the XML snippet and updates the the option class accordingly.<br/>
     * The XML snippet has to consist of YADE-XML compliant Elements, but does not have to be a complete YADE-XML-Configuration.<br/>
     * <br/>
     * <b>Example:</b><br/>
     * To simply update the given configuration for the source file_path and directory options,
     *  only the Selection Element has to be transmitted.<br/>
     * The following XML snippet would be sufficient.<br/>
     * <br/>
     * <pre>
     * &lt;Selection>
     *     &lt;FileSpecSelection>
     *         &lt;FileSpec>&lt;![CDATA[test1.txt]]>&lt;/FileSpec>
     *         &lt;Directory>&lt;![CDATA[/tmp]]>&lt;/Directory>
     *     &lt;/FileSpecSelection>
     * &lt;/Selection>
     * </pre>
     * 
     * @param xml A {@link String} representation of the given YADE-XML snippet. 
     */
    private void extractOptionsFromXmlSnippet(String xml) {
        String operation = null;
        String sourceFragmentInUse = null;
        String targetFragmentInUse = null;
        String filePath = null;
        String filePathDir = null;
        String fileSpec = null;
        String fileSpecDir = null;
        String fileList = null;
        String fileListDir = null;
        String targetDir = null;
        try {
            SOSXMLXPath xPath = new SOSXMLXPath(new StringBuffer(xml));
            if (xPath.selectSingleNode("//Operation") != null) {
                operation = xPath.selectSingleNode("//Operation").getFirstChild().getNodeName();
                LOGGER.debug("Operation Type update received from XML. New Operation: " + operation);
            } else {
                LOGGER.debug("*******No Operation Element specified in XML Snippet, falling back to configured operation from options*******");
                if ("send".equalsIgnoreCase(options.operation.getValue())) {
                    operation = "Copy";
                    LOGGER.debug("Operation Type set to COPY as default");
                } else {
                    operation = options.operation.getValue();
                    LOGGER.debug("Operation not changed! Operation Type already in use is: " + operation);
                }
            }
            if (operation != null && !operation.isEmpty()) {
                Boolean isJump = false;
                sourceFragmentInUse = xPath.selectDocumentText("//" + operation + "Source/" + operation + "SourceFragmentRef");
                if (sourceFragmentInUse != null && !sourceFragmentInUse.isEmpty()) {
                    String nodeName = xPath.selectSingleNode("//" + operation + "SourceFragmentRef").getFirstChild().getNodeName();
                    if (!"LocalSource".equalsIgnoreCase(nodeName)) {
                        Node node = xPath.selectSingleNode("//" + nodeName.replaceAll("Ref", ""));
                        if (node.getFirstChild().getNextSibling().getNextSibling() != null) {
                            LOGGER.debug("Overwriting OPERATION <" + options.operation.getValue() + "> with: " + "copytointernet");
                            options.operation.setValue("copytointernet");
                            isJump = true;
                        }
                        Map<String, String> sourceCredentials = getCredentialsFromFragmentNode(node);
                        for (String key : sourceCredentials.keySet()) {
                            switch (key) {
                            case "host":
                                LOGGER.debug("Overwriting Source HOST <" + options.getSource().host.getValue() + "> with: "
                                        + sourceCredentials.get(key));
                                options.getSource().host.setValue(sourceCredentials.get(key));
                                break;
                            case "user":
                                LOGGER.debug("Overwriting Source USER <" + options.getSource().userName.getValue() + "> with: "
                                        + sourceCredentials.get(key));
                                options.getSource().userName.setValue(sourceCredentials.get(key));
                                break;
                            case "password":
                                LOGGER.debug("Overwriting Source PWD <*****> with: new PWD *****");
                                options.getSource().password.setValue(sourceCredentials.get(key));
                                break;
                            case "protocol":
                                LOGGER.debug("Overwriting Source PROTOCOL <" + options.getSource().protocol.getValue() + "> with: "
                                        + sourceCredentials.get(key));
                                options.getSource().protocol.setValue(sourceCredentials.get(key));
                                break;
                            case "authMethodName":
                                LOGGER.debug("Overwriting Source AUTH_METHOD <" + options.getSource().authMethod.getValue() + "> with: "
                                        + sourceCredentials.get(key));
                                options.getSource().authMethod.setValue(sourceCredentials.get(key));
                                break;
                            case "publickey":
                                LOGGER.debug("Overwriting Source AUTH_FILE <" + options.getSource().authFile.getValue() + "> with: "
                                        + sourceCredentials.get(key));
                                options.getSource().authFile.setValue(sourceCredentials.get(key));
                                break;
                            case "connectionUrl":
                                LOGGER.debug("Overwriting Source CONNECTION_URL <" + options.getSource().url.getValue() + "> with: "
                                        + sourceCredentials.get(key));
                                options.getSource().url.setValue(sourceCredentials.get(key));
                                break;
                            default:
                                break;
                            }
                        }
                    }
                }
                targetFragmentInUse = xPath.selectDocumentText("//" + operation + "Target/" + operation + "TargetFragmentRef");
                if (targetFragmentInUse != null && !targetFragmentInUse.isEmpty()) {
                    String nodeName = xPath.selectSingleNode("//" + operation + "TargetFragmentRef").getFirstChild().getNodeName();
                    if (!"LocalSource".equalsIgnoreCase(nodeName)) {
                        Node node = xPath.selectSingleNode("//" + nodeName.replaceAll("Ref", ""));
                        if (node.getFirstChild().getNextSibling().getNextSibling() != null) {
                            options.operation.setValue("copyfrominternet");
                            isJump = true;
                        }
                        Map<String, String> targetCredentials = getCredentialsFromFragmentNode(node);
                        for (String key : targetCredentials.keySet()) {
                            switch (key) {
                            case "host":
                                LOGGER.debug("Overwriting Target HOST <" + options.getTarget().host.getValue() + "> with: "
                                        + targetCredentials.get(key));
                                options.getTarget().host.setValue(targetCredentials.get(key));
                                break;
                            case "user":
                                LOGGER.debug("Overwriting Target USER <" + options.getTarget().userName.getValue() + "> with: "
                                        + targetCredentials.get(key));
                                options.getTarget().userName.setValue(targetCredentials.get(key));
                                break;
                            case "password":
                                LOGGER.debug("Overwriting Target PWD <*****> with: new PWD *****");
                                options.getTarget().password.setValue(targetCredentials.get(key));
                                break;
                            case "protocol":
                                LOGGER.debug("Overwriting Target PROTOCOL <" + options.getTarget().protocol.getValue() + "> with: "
                                        + targetCredentials.get(key));
                                options.getTarget().protocol.setValue(targetCredentials.get(key));
                                break;
                            case "authMethodName":
                                LOGGER.debug("Overwriting Target AUTH_METHOD <" + options.getTarget().authMethod.getValue() + "> with: "
                                        + targetCredentials.get(key));
                                options.getTarget().authMethod.setValue(targetCredentials.get(key));
                                break;
                            case "publickey":
                                LOGGER.debug("Overwriting Target AUTH_FILE <" + options.getTarget().authFile.getValue() + "> with: "
                                        + targetCredentials.get(key));
                                options.getTarget().authFile.setValue(targetCredentials.get(key));
                                break;
                            case "connectionUrl":
                                LOGGER.debug("Overwriting Target CONNECTION_URL <" + options.getTarget().url.getValue() + "> with: "
                                        + targetCredentials.get(key));
                                options.getTarget().url.setValue(targetCredentials.get(key));
                                break;
                            default:
                                break;
                            }
                        }
                    }
                }
                if (isJump) {
                    Node node = xPath.selectSingleNode("//JumpFragment");
                    Map<String, String> jumpCredentials = getCredentialsFromFragmentNode(node);
                    for (String key : jumpCredentials.keySet()) {
                        switch (key) {
                        case "host":
                            LOGGER.debug("Overwriting jump HOST <" + options.jumpHost.getValue() + "> with: " + jumpCredentials.get(key));
                            options.jumpHost.setValue(jumpCredentials.get(key));
                            break;
                        case "user":
                            LOGGER.debug("Overwriting jump USER <" + options.jumpUser.getValue() + "> with: " + jumpCredentials.get(key));
                            options.jumpUser.setValue(jumpCredentials.get(key));
                            break;
                        case "password":
                            LOGGER.debug("Overwriting jump PWD <*****> with: new PWD *****");
                            options.jumpPassword.setValue(jumpCredentials.get(key));
                            break;
                        case "authMethodName":
                            LOGGER.debug("Overwriting jump AUTH_METHOD <" + options.jumpSshAuthMethod.getValue() + "> with: "
                                    + jumpCredentials.get(key));
                            options.jumpSshAuthMethod.setValue(jumpCredentials.get(key));
                            break;
                        case "publickey":
                            LOGGER.debug("Overwriting jump AUTH_FILE <" + options.jumpSshAuthFile.getValue() + "> with: "
                                    + jumpCredentials.get(key));
                            options.jumpSshAuthFile.setValue(jumpCredentials.get(key));
                            break;
                        case "JumpCommand":
                            LOGGER.debug("Overwriting jump COMMAND <" + options.jumpCommand.getValue() + "> with: " + jumpCredentials.get(key));
                            options.jumpCommand.setValue(jumpCredentials.get(key));
                            break;
                        default:
                            break;
                        }
                    }
                }
            }
            filePath = xPath.selectSingleNodeValue("//Selection/FilePathSelection/FilePath");
            filePathDir = xPath.selectSingleNodeValue("//Selection/FilePathSelection/Directory");
            fileSpec = xPath.selectSingleNodeValue("//Selection/FileSpecSelection/FileSpec");
            fileSpecDir = xPath.selectSingleNodeValue("//Selection/FileSpecSelection/Directory");
            fileList = xPath.selectSingleNodeValue("//Selection/FileListSelection/FileList");
            fileListDir = xPath.selectSingleNodeValue("//Selection/FileListSelection/Directory");
            if("copy".equalsIgnoreCase(operation)) {
                targetDir = xPath.selectSingleNodeValue("//CopyTarget/Directory");
            } else if ("move".equalsIgnoreCase(operation)){
                targetDir = xPath.selectSingleNodeValue("//MoveTarget/Directory");
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        if (filePath != null && !filePath.isEmpty()) {
            LOGGER.debug("Overwriting Source FILE_PATH <" + options.filePath.getValue() + "> with: " + filePath);
            options.filePath.setValue(filePath);
            LOGGER.debug("Overwriting Source DIRECTORY <" + options.getSource().directory.getValue() + "> with: " + filePathDir);
            options.getSource().directory.setValue(filePathDir);
        } else if (fileSpec != null && !fileSpec.isEmpty()) {
            LOGGER.debug("Overwriting Source FILE_SPEC <" + options.fileSpec.getValue() + "> with: " + fileSpec);
            options.fileSpec.setValue(fileSpec);
            LOGGER.debug("Overwriting Source DIRECTORY <" + options.getSource().directory.getValue() + "> with: " + fileSpecDir);
            options.getSource().directory.setValue(fileSpecDir);
        } else if (fileList != null && !fileList.isEmpty()) {
            LOGGER.debug("Overwriting Source FILE_LIST <" + options.fileListName.getValue() + "> with: " + fileList);
            options.fileListName.setValue(fileList);
            LOGGER.debug("Overwriting Source DIRECTORY <" + options.getSource().directory.getValue() + "> with: " + fileListDir);
            options.getSource().directory.setValue(fileListDir);
        }
        if (targetDir != null && !targetDir.isEmpty()) {
            LOGGER.debug("Overwriting Target DIRECTORY <" + options.getTarget().directory.getValue() + "> with: " + targetDir);
            options.getTarget().directory.setValue(targetDir);
        }
    }

    public JADEOptions getOptions() {
        return options;
    }

    public void setOptions(JADEOptions options) {
        this.options = options;
    }

}
