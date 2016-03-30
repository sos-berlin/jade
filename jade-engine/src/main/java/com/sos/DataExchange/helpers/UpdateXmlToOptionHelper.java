package com.sos.DataExchange.helpers;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sos.xml.SOSXMLXPath;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.io.Files.JSFile;

public class UpdateXmlToOptionHelper {

    private static final Logger LOGGER = Logger.getLogger(UpdateXmlToOptionHelper.class);
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
        JSFile file = new JSFile(options.xmlUpdate.Value());
        String xml = file.File2String();
        LOGGER.debug(xml);
        extractOptionsFromXmlSnippet(xml);
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
     * To simply update the given configuration for the source file_path and directory options, only the Selection Element has to be transmitted.<br/>
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
                if ("send".equalsIgnoreCase(options.operation.Value())) {
                    operation = "Copy";
                    LOGGER.debug("Operation Type set to COPY as default");
                } else {
                    operation = options.operation.Value();
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
                            LOGGER.info("Overwriting OPERATION <" + options.operation.Value() + "> with: " + "copytointernet");
                            options.operation.Value("copytointernet");
                            isJump = true;
                        }
                        Map<String, String> sourceCredentials = getCredentialsFromFragmentNode(node);
                        for (String key : sourceCredentials.keySet()) {
                            switch (key) {
                            case "host":
                                LOGGER.info("Overwriting Source HOST <" + options.Source().host.Value() + "> with: " + sourceCredentials.get(key));
                                options.Source().host.Value(sourceCredentials.get(key));
                                break;
                            case "user":
                                LOGGER.info("Overwriting Source USER <" + options.Source().UserName.Value() + "> with: " + sourceCredentials.get(key));
                                options.Source().UserName.Value(sourceCredentials.get(key));
                                break;
                            case "password":
                                LOGGER.info("Overwriting Source PWD <*****> with: new PWD *****");
                                options.Source().password.Value(sourceCredentials.get(key));
                                break;
                            case "protocol":
                                LOGGER.info("Overwriting Source PROTOCOL <" + options.Source().protocol.Value() + "> with: " + sourceCredentials.get(key));
                                options.Source().protocol.Value(sourceCredentials.get(key));
                                break;
                            case "authMethodName":
                                LOGGER.info("Overwriting Source AUTH_METHOD <" + options.Source().auth_method.Value() + "> with: " + sourceCredentials.get(key));
                                options.Source().auth_method.Value(sourceCredentials.get(key));
                                break;
                            case "publickey":
                                LOGGER.info("Overwriting Source AUTH_FILE <" + options.Source().auth_file.Value() + "> with: " + sourceCredentials.get(key));
                                options.Source().auth_file.Value(sourceCredentials.get(key));
                                break;
                            case "connectionUrl":
                                LOGGER.info("Overwriting Source CONNECTION_URL <" + options.Source().url.Value() + "> with: " + sourceCredentials.get(key));
                                options.Source().url.Value(sourceCredentials.get(key));
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
                            options.operation.Value("copyfrominternet");
                            isJump = true;
                        }
                        Map<String, String> targetCredentials = getCredentialsFromFragmentNode(node);
                        for (String key : targetCredentials.keySet()) {
                            switch (key) {
                            case "host":
                                LOGGER.info("Overwriting Target HOST <" + options.Target().host.Value() + "> with: " + targetCredentials.get(key));
                                options.Target().host.Value(targetCredentials.get(key));
                                break;
                            case "user":
                                LOGGER.info("Overwriting Target USER <" + options.Target().UserName.Value() + "> with: " + targetCredentials.get(key));
                                options.Target().UserName.Value(targetCredentials.get(key));
                                break;
                            case "password":
                                LOGGER.info("Overwriting Target PWD <*****> with: new PWD *****");
                                options.Target().password.Value(targetCredentials.get(key));
                                break;
                            case "protocol":
                                LOGGER.info("Overwriting Target PROTOCOL <" + options.Target().protocol.Value() + "> with: " + targetCredentials.get(key));
                                options.Target().protocol.Value(targetCredentials.get(key));
                                break;
                            case "authMethodName":
                                LOGGER.info("Overwriting Target AUTH_METHOD <" + options.Target().auth_method.Value() + "> with: " + targetCredentials.get(key));
                                options.Target().auth_method.Value(targetCredentials.get(key));
                                break;
                            case "publickey":
                                LOGGER.info("Overwriting Target AUTH_FILE <" + options.Target().auth_file.Value() + "> with: " + targetCredentials.get(key));
                                options.Target().auth_file.Value(targetCredentials.get(key));
                                break;
                            case "connectionUrl":
                                LOGGER.info("Overwriting Target CONNECTION_URL <" + options.Target().url.Value() + "> with: " + targetCredentials.get(key));
                                options.Target().url.Value(targetCredentials.get(key));
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
                            LOGGER.info("Overwriting jump HOST <" + options.jump_host.Value() + "> with: " + jumpCredentials.get(key));
                            options.jump_host.Value(jumpCredentials.get(key));
                            break;
                        case "user":
                            LOGGER.info("Overwriting jump USER <" + options.jump_user.Value() + "> with: " + jumpCredentials.get(key));
                            options.jump_user.Value(jumpCredentials.get(key));
                            break;
                        case "password":
                            LOGGER.info("Overwriting jump PWD <*****> with: new PWD *****");
                            options.jump_password.Value(jumpCredentials.get(key));
                            break;
                        case "authMethodName":
                            LOGGER.info("Overwriting jump AUTH_METHOD <" + options.jump_ssh_auth_method.Value() + "> with: " + jumpCredentials.get(key));
                            options.jump_ssh_auth_method.Value(jumpCredentials.get(key));
                            break;
                        case "publickey":
                            LOGGER.info("Overwriting jump AUTH_FILE <" + options.jump_ssh_auth_file.Value() + "> with: " + jumpCredentials.get(key));
                            options.jump_ssh_auth_file.Value(jumpCredentials.get(key));
                            break;
                        case "JumpCommand":
                            LOGGER.info("Overwriting jump COMMAND <" + options.jump_command.Value() + "> with: " + jumpCredentials.get(key));
                            options.jump_command.Value(jumpCredentials.get(key));
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
            targetDir = xPath.selectSingleNodeValue("//CopyTarget/Directory");
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        if (filePath != null && !filePath.isEmpty()) {
            LOGGER.info("Overwriting Source FILE_PATH <" + options.file_path.Value() + "> with: " + filePath);
            options.file_path.Value(filePath);
            LOGGER.info("Overwriting Source DIRECTORY <" + options.Source().Directory.Value() + "> with: " + filePathDir);
            options.Source().Directory.Value(filePathDir);
        } else if (fileSpec != null && !fileSpec.isEmpty()) {
            LOGGER.info("Overwriting Source FILE_SPEC <" + options.file_spec.Value() + "> with: " + fileSpec);
            options.file_spec.Value(fileSpec);
            LOGGER.info("Overwriting Source DIRECTORY <" + options.Source().Directory.Value() + "> with: " + fileSpecDir);
            options.Source().Directory.Value(fileSpecDir);
        } else if (fileList != null && !fileList.isEmpty()) {
            LOGGER.info("Overwriting Source FILE_LIST <" + options.FileListName.Value() + "> with: " + fileList);
            options.FileListName.Value(fileList);
            LOGGER.info("Overwriting Source DIRECTORY <" + options.Source().Directory.Value() + "> with: " + fileListDir);
            options.Source().Directory.Value(fileListDir);
        } else if (targetDir != null && !targetDir.isEmpty()) {
            LOGGER.info("Overwriting Target DIRECTORY <" + options.Target().Directory.Value() + "> with: " + targetDir);
            options.Target().Directory.Value(targetDir);
        }
    }

    public JADEOptions getOptions() {
        return options;
    }

    public void setOptions(JADEOptions options) {
        this.options = options;
    }

}
