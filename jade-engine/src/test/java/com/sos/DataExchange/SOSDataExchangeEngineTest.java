package com.sos.DataExchange;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sos.xml.SOSXMLXPath;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.DataExchange.helpers.UpdateXmlToOptionHelper;
import com.sos.JSHelper.Basics.JSToolBox;
import com.sos.JSHelper.Options.SOSOptionAuthenticationMethod;
import com.sos.JSHelper.Options.SOSOptionAuthenticationMethod.enuAuthenticationMethods;
import com.sos.JSHelper.Options.SOSOptionJSTransferMethod.enuJSTransferModes;
import com.sos.JSHelper.Options.SOSOptionJadeOperation;
import com.sos.JSHelper.Options.SOSOptionJadeOperation.enuJadeOperations;
import com.sos.JSHelper.Options.SOSOptionPortNumber;
import com.sos.JSHelper.Options.SOSOptionRegExp;
import com.sos.JSHelper.Options.SOSOptionTransferType;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;
import com.sos.JSHelper.io.Files.JSFile;
import com.sos.VirtualFileSystem.DataElements.SOSFileListEntry;
import com.sos.VirtualFileSystem.Factory.VFSFactory;
import com.sos.VirtualFileSystem.Interfaces.ISOSVFSHandler;
import com.sos.VirtualFileSystem.Options.SOSConnection2Options;
import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;

public class SOSDataExchangeEngineTest extends JSToolBox {

    protected String dynamicClassNameSource = null;
    protected String strSettingsFile = "src/test/resources/examples/jade_settings.ini";
    private static final String HOST_NAME_WILMA_SOS = "wilma.sos";
    private static final String HOST_NAME_8OF9_SOS = "8of9.sos";
    private static final String CLASS_NAME = "SOSDataExchangeEngineTest";
    private static final Logger LOGGER = LoggerFactory.getLogger(SOSDataExchangeEngineTest.class);
    private static final String TEST_PATH_NAME = "R:/backup/sos/java/junittests/testdata/JADE/";
    private static final String KB_HOME = "/home/kb/";
    private JADEOptions objOptions = null;
    private ISOSVFSHandler objVFS = null;
    private String strTestFileName = "text.txt";
    private String constrSettingsTestFile = TEST_PATH_NAME + "/jade-test.ini";
    private boolean flgUseFilePath = false;

    @Before
    public void setUp() throws Exception {
        String strLog4JFileName = "./log4j.properties";
        String strT = new File(strLog4JFileName).getAbsolutePath();
        LOGGER.info("log4j properties filename = " + strT);
        objOptions = new JADEOptions();
        objOptions.applicationName.setValue("JADE");
        objOptions.applicationDocuUrl.setValue("http://www.sos-berlin.com/doc/en/jade/JADE Parameter Reference.pdf");
        dynamicClassNameSource = "com.sos.VirtualFileSystem.SFTP.SOSVfsSFtpJCraft";
        objVFS = VFSFactory.getHandler(objOptions.protocol.getValue());
    }

    private void createTestFile() {
        createTestFile(strTestFileName);
    }

    private void createTestFile(final String pstrFileName) {
        JSFile objFile = new JSFile(TEST_PATH_NAME, pstrFileName);
        try {
            objFile.writeLine("This is a simple Testfile. nothing else.");
            objFile.close();
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    private void CreateTestFiles(final int intNumberOfFiles) {
        for (int i = 0; i < intNumberOfFiles; i++) {
            JSFile objFile = new JSFile(TEST_PATH_NAME + "Masstest" + i + ".txt");
            try {
                for (int j = 0; j < 10; j++) {
                    objFile.writeLine("This is a simple Testfile, created for the masstest. nothing else.");
                }
                objFile.close();
            } catch (IOException e) {
                LOGGER.error("", e);
            }
        }
    }

    class WriteFiles4Polling implements Runnable {

        @Override
        public void run() {
            JSFile objFile = null;
            for (int i = 0; i < 15; i++) {
                LOGGER.debug("" + i);
                objFile = new JSFile(TEST_PATH_NAME + "/test-" + i + ".poll");
                try {
                    Thread.sleep(5000);
                    objFile.write(i + ": This is a test");
                    objFile.writeLine(i + ": This is a test");
                    objFile.writeLine(i + ": This is a test");
                    objFile.writeLine(i + ": This is a test");
                } catch (IOException e) {
                    LOGGER.error("", e);
                } catch (InterruptedException e) {
                    LOGGER.error("", e);
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
                try {
                    objFile.close();
                    objFile = null;
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
            LOGGER.debug("finished");
        }
    }

    private void sendWithPolling(final boolean flgForceFiles, final boolean flgCreateFiles) throws Exception {
        final String conMethodName = CLASS_NAME + "::sendWithPolling";
        objOptions = new JADEOptions();
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.protocol.setValue(enuTransferTypes.sftp);
        assertEquals("sftp", "sftp", objOptions.protocol.getValue());
        objOptions.port.value(SOSOptionPortNumber.conPort4SSH);
        objOptions.user.setValue("test");
        objOptions.password.setValue("12345");
        objOptions.authMethod.setValue(enuAuthenticationMethods.password);
        if (flgUseFilePath) {
            objOptions.filePath.setValue("R:/backup/sos/java/junittests/testdata/SOSDataExchange/test-0.poll");
        } else {
            objOptions.fileNamePatternRegExp.setValue("^.*\\.poll$");
            objOptions.pollMinfiles.value(1);
        }
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.operation.setValue("send");
        objOptions.logFilename.setValue(objOptions.getTempDir() + "test.log");
        objOptions.profile.setValue(conMethodName);
        objOptions.pollInterval.setValue("0:30");
        objOptions.pollingDuration.setValue("05:00");
        objOptions.errorOnNoDataFound.value(flgForceFiles);
        objOptions.removeFiles.value(true);
        LOGGER.info(objOptions.dirtyString());
        if (flgCreateFiles) {
            Thread thread = new Thread(new WriteFiles4Polling());
            thread.start();
        }
        startTransfer();
    }

    @Test
    public void testUrlFile() {
        String strReplaceWhat = "^([^\\.]{8})\\.([0-9]{5})(\\.000)$";
        objOptions.operation.setValue(enuJadeOperations.rename);
        objOptions.getSource().url.setValue("file:///R:/backup/projects/anubex-dws/JCLs");
        objOptions.ReplaceWhat.setValue(strReplaceWhat);
        objOptions.ReplaceWith.setValue("\\1.jcl;;;");
        objOptions.fileNameRegExp.setValue(strReplaceWhat);
        objOptions.maxFiles.value(10);
        objOptions.verbose.value(-1);
        startTransfer();
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    public void testWrongUrl() {
        String strReplaceWhat = "^([^\\.]{8})\\.([0-9]{5})(\\.000)$";
        objOptions.operation.setValue(enuJadeOperations.rename);
        objOptions.getSource().url.setValue("filse:///R:/backup/projects/anubex-dws/JCLs");
        objOptions.ReplaceWhat.setValue(strReplaceWhat);
        objOptions.ReplaceWith.setValue("\\1.jcl;;;");
        objOptions.fileNameRegExp.setValue(strReplaceWhat);
        objOptions.maxFiles.value(10);
        objOptions.verbosityLevel.value(-1);
        objOptions.verbose.value(-1);
        startTransfer();
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    @Ignore("Test set to Ignore for later examination")
    public void testWrongUrl2() {
        String strReplaceWhat = "^([^\\.]{8})\\.([0-9]{5})(\\.000)$";
        objOptions.operation.setValue(enuJadeOperations.rename);
        objOptions.getSource().protocol.setValue("filse");
        objOptions.getSource().directory.setValue("R:/backup/projects/anubex-dws/JCLs");
        objOptions.ReplaceWhat.setValue(strReplaceWhat);
        objOptions.ReplaceWith.setValue("\\1.jcl;;;");
        objOptions.fileNameRegExp.setValue(strReplaceWhat);
        objOptions.maxFiles.value(10);
        objOptions.verbosityLevel.value(-1);
        objOptions.verbose.value(-1);
        startTransfer();
    }

    @Test
    public void testUrlFile2() {
        String strReplaceWhat = "^([^\\.]{8})\\.([0-9]{5})(\\.000)$";
        objOptions.operation.setValue(enuJadeOperations.rename);
        objOptions.getSource().protocol.setValue("file");
        objOptions.sourceDir.setValue("R:/backup/projects/anubex-dws/JCLs");
        objOptions.ReplaceWhat.setValue(strReplaceWhat);
        objOptions.ReplaceWith.setValue("\\1.jcl;;;");
        objOptions.fileNameRegExp.setValue(strReplaceWhat);
        objOptions.maxFiles.value(10);
        objOptions.verbosityLevel.value(-1);
        objOptions.verbose.value(-1);
        startTransfer();
    }

    private void startTransfer() {
        JadeEngine objJadeEngine;
        try {
            objJadeEngine = new JadeEngine(objOptions);
            objJadeEngine.execute();
            objJadeEngine.logout();

        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    public void testEmptyCommandLineParameter() throws Exception {
        try {
            objOptions.allowEmptyParameterList.setFalse();
            objOptions.commandLineArgs(new String[] {});
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    public void testEmptyCommandLineParameter2() {
        try {
            objOptions.allowEmptyParameterList.setTrue();
            objOptions.commandLineArgs(new String[] {});
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    @Test
    public void testSendWithPolling() throws Exception {
        sendWithPolling(true, true);
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    @Ignore("Test set to Ignore for later examination")
    public void testSendWithPolling0Files() throws Exception {
        sendWithPolling(true, false);
    }

    @Test
    public void testSendWithPollingAndForce() throws Exception {
        sendWithPolling(false, false);
    }

    @Test
    public void testSendWithPollingUsingFilePath() throws Exception {
        flgUseFilePath = true;
        sendWithPolling(true, true);
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    @Ignore("Test set to Ignore for later examination")
    public void testSendWithPolling0FilesUsingFilePath() throws Exception {
        flgUseFilePath = true;
        sendWithPolling(true, false);
    }

    private void logMethodName(final String pstrName) {
        //
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendServer2Server() throws Exception {
        createTestFile();
        SOSConnection2Options objConn = objOptions.getConnectionOptions();
        objConn.getSource().hostName.setValue(HOST_NAME_WILMA_SOS);
        objConn.getSource().port.value(21);
        objConn.getSource().protocol.setValue(SOSOptionTransferType.enuTransferTypes.ftp);
        objConn.getSource().user.setValue("kb");
        objConn.getSource().password.setValue("kb");
        objConn.getTarget().hostName.setValue(HOST_NAME_8OF9_SOS);
        objConn.getTarget().port.value(21);
        objConn.getTarget().protocol.setValue(SOSOptionTransferType.enuTransferTypes.ftp);
        objConn.getTarget().user.setValue("kb");
        objConn.getTarget().password.setValue("kb");
        objOptions.filePath.setValue(strTestFileName);
        objOptions.sourceDir.setValue("/home/kb");
        objOptions.targetDir.setValue("/kb");
        objOptions.operation.setValue("copy");
        objOptions.checkMandatory();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.targetDir.getValue() + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendServer2ServerWithJCraft() throws Exception {
        createTestFile();
        SOSConnection2Options objConn = objOptions.getConnectionOptions();
        objConn.getSource().hostName.setValue(HOST_NAME_WILMA_SOS);
        objConn.getSource().port.value(SOSOptionPortNumber.getStandardSFTPPort());
        objConn.getSource().protocol.setValue(SOSOptionTransferType.enuTransferTypes.sftp);
        objConn.getSource().user.setValue("kb");
        objConn.getSource().password.setValue("kb");
        objConn.getSource().sshAuthMethod.setValue("password");
        objConn.getSource().loadClassName.setValue("com.sos.VirtualFileSystem.SFTP.SOSVfsSFtpJCraft");
        objConn.getTarget().hostName.setValue(HOST_NAME_WILMA_SOS);
        objConn.getTarget().port.value(SOSOptionPortNumber.getStandardSFTPPort());
        objConn.getTarget().protocol.setValue(SOSOptionTransferType.enuTransferTypes.sftp);
        objConn.getTarget().user.setValue("sos");
        objConn.getTarget().password.setValue("sos");
        objConn.getTarget().sshAuthMethod.setValue("password");
        objConn.getTarget().loadClassName.setValue("com.sos.VirtualFileSystem.SFTP.SOSVfsSFtpJCraft");
        objOptions.filePath.setValue(strTestFileName);
        objOptions.sourceDir.setValue("/home/kb");
        objOptions.targetDir.setValue("/home/sos");
        objOptions.operation.setValue("copy");
        objOptions.checkMandatory();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.targetDir.getValue() + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendServer2ServerMultiple() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendServer2Server";
        createTestFile();
        logMethodName(conMethodName);
        SOSConnection2Options objConn = objOptions.getConnectionOptions();
        objConn.getSource().hostName.setValue(HOST_NAME_WILMA_SOS);
        objConn.getSource().port.value(21);
        objConn.getSource().protocol.setValue(SOSOptionTransferType.enuTransferTypes.ftp);
        objConn.getSource().user.setValue("kb");
        objConn.getSource().password.setValue("kb");
        objConn.getTarget().hostName.setValue(HOST_NAME_8OF9_SOS);
        objConn.getTarget().port.value(21);
        objConn.getTarget().protocol.setValue(SOSOptionTransferType.enuTransferTypes.ftp);
        objConn.getTarget().user.setValue("kb");
        objConn.getTarget().password.setValue("kb");
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.sourceDir.setValue("/home/kb");
        objOptions.targetDir.setValue("/kb");
        objOptions.operation.setValue("copy");
        objOptions.checkMandatory();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.targetDir.getValue() + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendFtp2SFtp() throws Exception {
        createTestFile();
        SOSConnection2Options objConn = objOptions.getConnectionOptions();
        SOSConnection2OptionsAlternate objS = objConn.getSource();
        objS.hostName.setValue(HOST_NAME_8OF9_SOS);
        objS.port.value(SOSOptionPortNumber.getStandardFTPPort());
        objS.protocol.setValue("ftp");
        objS.user.setValue("sos");
        objS.password.setValue("sos");
        objOptions.localDir.setValue("/");
        SOSConnection2OptionsAlternate objT = objConn.getTarget();
        objT.hostName.setValue(HOST_NAME_WILMA_SOS);
        objT.port.value(SOSOptionPortNumber.getStandardSFTPPort());
        objT.sshAuthMethod.isPassword(true);
        objT.protocol.setValue("sftp");
        objT.user.setValue("test");
        objT.password.setValue("12345");
        String strTestDir = "/home/test/";
        objOptions.remoteDir.setValue(strTestDir);
        objOptions.targetDir.setValue(strTestDir);
        strTestFileName = "wilma.sh";
        objOptions.filePath.setValue(strTestFileName);
        objOptions.operation.setValue("copy");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(strTestDir + strTestFileName).fileExists();
        assertTrue("File must exist " + strTestFileName, flgResult);
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSend() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSend";
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.protocol.setValue(enuTransferTypes.sftp);
        assertEquals("sftp", "sftp", objOptions.protocol.getValue());
        objOptions.port.value(SOSOptionPortNumber.conPort4SSH);
        objOptions.user.setValue("test");
        objOptions.password.setValue("12345");
        objOptions.authMethod.setValue(enuAuthenticationMethods.password);
        objOptions.filePath.setValue(strTestFileName);
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.operation.setValue("send");
        objOptions.logFilename.setValue("c:/temp/test.log");
        objOptions.profile.setValue(conMethodName);
        setOptions4BackgroundService();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/" + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendWithPrePostCommands() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendWithPrePostCommands";
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.protocol.setValue(enuTransferTypes.sftp);
        assertEquals("sftp", "sftp", objOptions.protocol.getValue());
        objOptions.port.value(SOSOptionPortNumber.conPort4SSH);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.authMethod.setValue(enuAuthenticationMethods.password);
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.operation.setValue("send");
        objOptions.logFilename.setValue("c:/temp/test.log");
        objOptions.profile.setValue(conMethodName);
        objOptions.preFtpCommands.setValue("rm -f t.1");
        objOptions.getTarget().postCommand.setValue("echo 'File: $TargetFileName' >> t.1;cat $TargetFileName >> t.1;rm -f $TargetFileName");
        objOptions.getTarget().preCommand.setValue("touch $TargetFileName");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/" + strTestFileName).fileExists();
        assertFalse("File must not exist", flgResult);
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendWithPrePostCommands2() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendWithPrePostCommands";
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.setValue("local");
        objOptions.protocol.setValue(enuTransferTypes.local);
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.remoteDir.setValue("c:/temp/a");
        objOptions.operation.setValue("send");
        objOptions.logFilename.setValue("c:/temp/test.log");
        objOptions.profile.setValue(conMethodName);
        objOptions.preFtpCommands.setValue("del %{remote_dir}/t.1");
        objOptions.getTarget().postCommand.setValue("echo 'File: $TargetFileName' >> c:\\temp\\a\\t.1 & type $TargetFileName >>"
                + " c:\\temp\\a\\t.1 & del $TargetFileName");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/" + strTestFileName).fileExists();
        assertFalse("File must not exist", flgResult);
        objJadeEngine.logout();
        LOGGER.debug(objOptions.getOptionsAsCommandLine());
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendRegExpAsFileName() throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        strTestFileName = "test.txt";
        objOptions.fileSpec.setValue(strTestFileName);
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.operation.setValue("send");
        objOptions.verbose.value(9);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/" + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendUsingReplacement() throws Exception {
        sendUsingReplacement("^t", "a");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendUsingReplacement2() throws Exception {
        sendUsingReplacement(".*", "renamed_[filename:]");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendUsingReplacement3() throws Exception {
        String strSaveTestfileName = strTestFileName;
        strTestFileName = "a" + strTestFileName;
        String strRenamedTestfileName = "renamed_" + strTestFileName;
        JSFile objFile = new JSFile(TEST_PATH_NAME, strRenamedTestfileName);
        if (objFile.exists()) {
            objFile.delete();
        }
        objFile = new JSFile(TEST_PATH_NAME, strTestFileName);
        if (objFile.exists()) {
            objFile.delete();
        }
        setFTPPrefixParams(".*", "renamed_[filename:]");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.remoteDir.getValue() + "/" + strRenamedTestfileName).fileExists();
        boolean flgResult2 = objJadeEngine.getSourceClient().getFileHandle(objOptions.localDir.getValue() + "/" + strTestFileName).fileExists();
        boolean flgResult3 = objJadeEngine.getSourceClient().getFileHandle(objOptions.localDir.getValue() + "/" + strRenamedTestfileName).fileExists();
        assertTrue("Files must exist", flgResult && flgResult2 && !flgResult3);
        objJadeEngine.logout();
        strTestFileName = strSaveTestfileName;
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendUsingEmptyReplacement() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendUsingEmptyReplacement";
        LOGGER.info("********************************************** " + conMethodName + "******************");
        sendUsingReplacement("^t", "");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testReceiveUsingEmptyReplacement() throws Exception {
        final String conMethodName = CLASS_NAME + "::testReceiveUsingEmptyReplacement";
        LOGGER.info("********************************************** " + conMethodName + "******************");
        Map<String, String> objHsh = new HashMap<String, String>();
        objHsh.put("ftp_host", HOST_NAME_WILMA_SOS);
        objHsh.put("ftp_port", "21");
        objHsh.put("ftp_user", "test");
        objHsh.put("ftp_password", "12345");
        objHsh.put("ftp_transfer_mode", "binary");
        objHsh.put("ftp_passive_mode", "0");
        objHsh.put("ftp_local_dir", "Y:/scheduler.test/testsuite_files/files/ftp_in/sosdex");
        objHsh.put("ftp_file_spec", "^renamed_");
        objHsh.put("ftp_remote_dir", "/home/test/temp/test/sosdex");
        objHsh.put("operation", "receive");
        objHsh.put("replacing", "^renamed_");
        objOptions = new JADEOptions();
        objOptions.setAllOptions(objOptions.deletePrefix((HashMap<String, String>) objHsh, "ftp_"));
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        assertEquals("replacing", "^renamed_", objOptions.replacing.getValue());
        assertEquals("replacement", "", objOptions.replacement.getValue());
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testRenameOnSourceOnly4SFTP() throws Exception {
        final String conMethodName = CLASS_NAME + "::testRenameOnSourceOnly4SFTP";
        LOGGER.info("********************************************** " + conMethodName + "******************");
        Map<String, String> objHsh = new HashMap<String, String>();
        objHsh.put("source_host", HOST_NAME_WILMA_SOS);
        objHsh.put("source_port", "22");
        objHsh.put("source_user", "test");
        objHsh.put("source_password", "12345");
        objHsh.put("source_protocol", "sftp");
        objHsh.put("source_ssh_auth_method", "password");
        objHsh.put("ftp_transfer_mode", "binary");
        objHsh.put("ftp_passive_mode", "false");
        objHsh.put("source_dir", "/home/test/temp/test/sosdex");
        objHsh.put("file_spec", "^.*\\.txt$");
        objHsh.put("operation", "rename");
        objHsh.put("replacing", ".*");
        objHsh.put("replacement", "oh/[filename:]");
        objOptions = new JADEOptions();
        objOptions.setAllOptions(objOptions.deletePrefix((HashMap<String, String>) objHsh, "ftp_"));
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        assertEquals("replacing", ".*", objOptions.replacing.getValue());
        assertEquals("replacement", "oh/[filename:]", objOptions.replacement.getValue());
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testRenameOnSourceOnly4FTP() throws Exception {
        final String conMethodName = CLASS_NAME + "::testRenameOnSourceOnly4FTP";
        LOGGER.info("********************************************** " + conMethodName + "******************");
        Map<String, String> objHsh = new HashMap<String, String>();
        objHsh.put("source_host", HOST_NAME_WILMA_SOS);
        objHsh.put("source_port", "21");
        objHsh.put("source_user", "test");
        objHsh.put("source_password", "12345");
        objHsh.put("source_protocol", "ftp");
        objHsh.put("source_ssh_auth_method", "password");
        objHsh.put("ftp_transfer_mode", "binary");
        objHsh.put("ftp_passive_mode", "false");
        objHsh.put("source_dir", "/home/test/temp/test/sosdex");
        objHsh.put("file_spec", "^.*\\.txt$");
        objHsh.put("operation", "rename");
        objHsh.put("replacing", ".*");
        objHsh.put("replacement", "oh/[filename:]");
        objOptions = new JADEOptions();
        objOptions.setAllOptions(objOptions.deletePrefix((HashMap<String, String>) objHsh, "ftp_"));
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        assertEquals("replacing", ".*", objOptions.replacing.getValue());
        assertEquals("replacement", "oh/[filename:]", objOptions.replacement.getValue());
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    private void setParams(final String replacing, final String replacement) throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.remoteDir.setValue(KB_HOME);
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.filePath.setValue(strTestFileName);
        objOptions.operation.setValue("send");
        objOptions.replacement.setValue(replacement);
        objOptions.replacing.setValue(replacing);
        objOptions.verbose.value(9);
    }

    private void setFTPPrefixParams(final String replacing, final String replacement) throws Exception {
        createTestFile();
        Map<String, String> objHsh = new HashMap<String, String>();
        objHsh.put("ftp_host", HOST_NAME_WILMA_SOS);
        objHsh.put("ftp_protocol", "ftp");
        objHsh.put("ftp_port", "21");
        objHsh.put("ftp_user", "test");
        objHsh.put("ftp_password", "12345");
        objHsh.put("ftp_transfer_mode", "binary");
        objHsh.put("ftp_passive_mode", "0");
        objHsh.put("ftp_local_dir", TEST_PATH_NAME);
        objHsh.put("ftp_file_spec", "^" + strTestFileName + "$");
        objHsh.put("ftp_remote_dir", "/home/test/temp/test");
        objHsh.put("operation", "send");
        objHsh.put("replacing", replacing);
        objHsh.put("replacement", replacement);
        objHsh.put("verbose", "9");
        objOptions = new JADEOptions();
        objOptions.setAllOptions(objOptions.deletePrefix((HashMap<String, String>) objHsh, "ftp_"));
    }

    public void sendUsingReplacement(final String replacing, final String replacement) throws Exception {
        setParams(replacing, replacement);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        SOSOptionRegExp objRE = new SOSOptionRegExp(null, "test", "TestOption", replacing, "", false);
        String expectedRemoteFile = KB_HOME + objRE.doReplace(strTestFileName, replacement);
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(expectedRemoteFile).fileExists();
        objJadeEngine.logout();
        assertTrue(String.format("File '%1$s' does not exist", expectedRemoteFile), flgResult);
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendUsingRelativeLocalDir() throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.remoteDir.setValue("./relative");
        objOptions.filePath.setValue(strTestFileName);
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.operation.setValue("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/relative/" + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendUsingFilePathAndLocalDir() throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.remoteDir.setValue("./relative");
        objOptions.filePath.setValue(TEST_PATH_NAME + strTestFileName);
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.operation.setValue("send");
        LOGGER.info(objOptions.dirtyString());
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/relative/" + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendUsingFilePathAndLocalDir2() throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.remoteDir.setValue("./relative");
        objOptions.filePath.setValue(TEST_PATH_NAME + strTestFileName);
        objOptions.localDir.setValue(TEST_PATH_NAME + "Test/");
        objOptions.operation.setValue("send");
        LOGGER.info(objOptions.dirtyString());
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/relative/" + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendUsingFilePathAndLocalDir3() throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.remoteDir.setValue("./relative");
        objOptions.filePath.setValue(strTestFileName);
        objOptions.localDir.setValue(TEST_PATH_NAME);
        runFilePathTest();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendUsingFilePathAndLocalDir4() throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.remoteDir.setValue("./relative");
        objOptions.filePath.setValue(TEST_PATH_NAME + strTestFileName);
        runFilePathTest();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendUsingFilePathAndLocalDir5() throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.remoteDir.setValue("./relative");
        objOptions.filePath.setValue("SOSDataExchange/" + strTestFileName);
        objOptions.localDir.setValue("R:/backup/sos/java/junittests/testdata/");
        runFilePathTest();
    }

    private void runFilePathTest() throws Exception {
        objOptions.operation.setValue("send");
        LOGGER.info(objOptions.dirtyString());
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/relative/" + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSend5() throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue(strTestFileName);
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.remoteDir.setValue(KB_HOME);
        objOptions.operation.setValue("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(KB_HOME + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendComand() throws Exception {
        objOptions = new JADEOptions();
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue(strTestFileName);
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.remoteDir.setValue(KB_HOME);
        objOptions.operation.setValue("send");
        setOptions4BackgroundService();
        objOptions.sendTransferHistory.value(false);
        objOptions.getTarget().postCommand.setValue("SITE CHMOD 777 $TargetFileName");
        objOptions.getSource().preCommand.setValue("dir $SourceFileName");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    private void setOptions4BackgroundService() {
        objOptions.schedulerHost.setValue(HOST_NAME_8OF9_SOS);
        objOptions.schedulerPort.setValue("4210");
        objOptions.schedulerTransferMethod.setValue(enuJSTransferModes.tcp.description);
        objOptions.sendTransferHistory.value(true);
    }

    public void testFerberSFtp() throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.setValue("85.214.92.170");
        objOptions.port.setValue("22");
        objOptions.protocol.setValue(enuTransferTypes.sftp);
        objOptions.alternativeHost.setValue("85.214.92.170");
        objOptions.alternativePort.setValue("22");
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue(TEST_PATH_NAME + strTestFileName);
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.operation.setValue("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendToAlternateHost() throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.setValue("xwilma.sos");
        objOptions.alternativeHost.setValue(HOST_NAME_WILMA_SOS);
        objOptions.alternativePort.setValue("21");
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue(TEST_PATH_NAME + strTestFileName);
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.operation.setValue("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/" + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendToAlternateUser() throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.setValue("wilma.sos");
        objOptions.alternativeUser.setValue("test");
        objOptions.getConnectionOptions().getTarget().getAlternatives().user.setValue("test");
        objOptions.getConnectionOptions().getTarget().getAlternatives().password.setValue("12345");
        objOptions.alternativePort.setValue("21");
        objOptions.getConnectionOptions().getAlternatives().user.getValue();
        objOptions.getConnectionOptions().getSource().getAlternatives().user.getValue();
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kbkbkb");
        objOptions.filePath.setValue(TEST_PATH_NAME + strTestFileName);
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.operation.setValue("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/" + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSend2() throws Exception {
        createTestFile();
        objOptions.host.setValue(HOST_NAME_8OF9_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue(TEST_PATH_NAME + strTestFileName);
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.remoteDir.setValue("/kb/");
        objOptions.operation.setValue("send");
        objOptions.passiveMode.value(true);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/kb/" + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSend2file_spec() throws Exception {
        String strSaveTestfileName = strTestFileName;
        strTestFileName = "3519078034.pdf";
        createTestFile();
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.protocol.setValue(enuTransferTypes.sftp);
        objOptions.sshAuthMethod.setValue(enuAuthenticationMethods.password);
        objOptions.fileSpec.setValue("^[0-9]{10}\\.pdf$");
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.remoteDir.setValue("/home/kb/");
        objOptions.transactional.setTrue();
        objOptions.atomicSuffix.setValue(".tmp");
        objOptions.postCommand.setValue("chmod 777 $TargetTransferFileName");
        objOptions.operation.setValue("send");
        objOptions.passiveMode.value(true);
        objOptions.logFilename.setValue("c:/temp/test.log");
        setMailOptions();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/" + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
        strTestFileName = strSaveTestfileName;
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendWithRename() throws Exception {
        createTestFile();
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("test");
        objOptions.password.setValue("12345");
        objOptions.protocol.setValue(enuTransferTypes.sftp);
        objOptions.sshAuthMethod.setValue(enuAuthenticationMethods.password);
        objOptions.fileSpec.setValue("^" + strTestFileName + "$");
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.remoteDir.setValue("/home/test/temp/test/");
        objOptions.replacement.setValue(".*");
        objOptions.replacing.setValue("renamed_[filename:]");
        objOptions.operation.setValue("send");
        objOptions.logFilename.setValue("c:/temp/test.log");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.remoteDir.getValue() + "/renamed_" + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
    }

    private void setMailOptions() {
        objOptions.getMailOptions().to.setValue("jade_test@sos-berlin.com");
        objOptions.getMailOptions().SMTPHost.setValue("smtp.sos");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testReceive() throws Exception {
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue(strTestFileName);
        objOptions.remoteDir.setValue("/home/kb/");
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.operation.setValue("receive");
        objOptions.transferMode.setValue("ascii");
        setOptions4BackgroundService();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testReceiveWithSymlinkInRemoteDir() throws Exception {
        JSFile objFile = new JSFile(TEST_PATH_NAME, strTestFileName);
        if (objFile.exists()) {
            objFile.delete();
        }
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("test");
        objOptions.password.setValue("12345");
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.remoteDir.setValue("/tmp/test/symlink2home.test.temp/test");
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.operation.setValue("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.localDir.getValue() + "/" + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testReceiveSFTP() throws Exception {
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.protocol.setValue(enuTransferTypes.sftp.getText());
        objOptions.port.setValue("22");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue("sosdex.txt");
        objOptions.remoteDir.setValue("/home/sos/tmp");
        objOptions.sshAuthMethod.setValue(enuAuthenticationMethods.password);
        objOptions.bufferSize.value(1024);
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.operation.setValue("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testReceiveWithUmlaut() throws Exception {
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue("Büttner.dat");
        objOptions.remoteDir.setValue("/home/kb/");
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.operation.setValue("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testReceiveWithUmlautFromLocalhost() throws Exception {
        objOptions.host.setValue("localhost");
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.fileSpec.setValue(".*ttner\\..*");
        objOptions.remoteDir.setValue("/");
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.operation.setValue("receive");
        objOptions.preFtpCommands.setValue("OPTS UTF8 ON");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testCopy() throws Exception {
        new File(TEST_PATH_NAME + strTestFileName).delete();
        SOSConnection2OptionsAlternate objS = objOptions.getConnectionOptions().getSource();
        objS.protocolCommandListener.setTrue();
        objOptions.verbose.value(9);
        objS.host.setValue(HOST_NAME_WILMA_SOS);
        objS.protocol.setValue("ftp");
        objS.user.setValue("kb");
        objS.password.setValue("kb");
        objOptions.filePath.setValue(strTestFileName);
        objOptions.sourceDir.setValue("/home/kb/");
        SOSConnection2OptionsAlternate objT = objOptions.getConnectionOptions().getTarget();
        objT.protocol.setValue("local");
        objOptions.targetDir.setValue(TEST_PATH_NAME);
        objOptions.remoteDir.setValue(TEST_PATH_NAME);
        objOptions.forceFiles.setFalse();
        objOptions.operation.setValue(SOSOptionJadeOperation.enuJadeOperations.copy);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        LOGGER.info(objOptions.dirtyString());
        objJadeEngine.execute();
        objJadeEngine.logout();
        assertTrue(new File(TEST_PATH_NAME + strTestFileName).exists());
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testReceive2() throws Exception {
        objOptions.host.setValue(HOST_NAME_8OF9_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue(strTestFileName);
        objOptions.remoteDir.setValue("/kb/");
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.operation.setValue("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendMultipleFiles() throws Exception {
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue("");
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.operation.setValue("send");
        objOptions.verbose.value(9);
        setOptions4BackgroundService();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testReceiveMultipleFiles() throws Exception {
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue("");
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.remoteDir.setValue("/home/kb/");
        objOptions.appendFiles.value(false);
        objOptions.operation.setValue("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        long intNoOfFilesTransferred = objJadeEngine.getFileList().getSuccessfulTransfers();
        for (SOSFileListEntry objListItem : objJadeEngine.getFileList().getList()) {
            String strF = makeFullPathName(objOptions.targetDir.getValue(), objListItem.getTargetFileName());
        }
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testResultSet() throws Exception {
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue("");
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.remoteDir.setValue("/home/kb/");
        objOptions.appendFiles.value(false);
        objOptions.operation.setValue("receive");
        objOptions.createResultSet.value(true);
        String strResultSetFileName = objOptions.getTempDir() + "/ResultSetFile.dat";
        objOptions.resultSetFileName.setValue(strResultSetFileName);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendAndDeleteMultipleFiles() throws Exception {
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue("");
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.operation.setValue("send");
        objOptions.deleteFilesAfterTransfer.value(true);
        objOptions.logFilename.setValue("c:/temp/test.log");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        for (SOSFileListEntry objListItem : objJadeEngine.getFileList().getList()) {
            String strF = objListItem.getSourceFileName();
            boolean flgResult = objListItem.geSourceFileTransfer().getFileHandle(strF).fileExists();
            assertFalse("File " + strF + " exist, but should not", flgResult);
        }
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testReceiveMultipleFiles2() throws Exception {
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue("");
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.remoteDir.setValue("/home/kb/");
        objOptions.appendFiles.value(false);
        objOptions.operation.setValue("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        for (SOSFileListEntry objListItem : objJadeEngine.getFileList().getList()) {
            String strF = makeFullPathName(objOptions.targetDir.getValue(), objListItem.getTargetFileName());
        }
        objJadeEngine.logout();
    }

    public void renameLocalFiles(final String source_dir, final String file_spec) throws Exception {
        SOSConnection2Options objConn = objOptions.getConnectionOptions();
        SOSConnection2OptionsAlternate objS = objConn.getSource();
        SOSConnection2OptionsAlternate objT = objConn.getTarget();
        objS.hostName.setValue(HOST_NAME_WILMA_SOS);
        objS.port.value(SOSOptionPortNumber.getStandardSFTPPort());
        objS.protocol.setValue("sftp");
        objS.user.setValue("test");
        objS.sshAuthMethod.isPassword(true);
        objS.password.setValue("12345");
        objT.hostName.setValue(HOST_NAME_WILMA_SOS);
        objT.port.value(SOSOptionPortNumber.getStandardSFTPPort());
        objT.protocol.setValue("sftp");
        objT.user.setValue("test");
        objT.sshAuthMethod.isPassword(true);
        objT.password.setValue("12345");
        objOptions.sourceDir.setValue(source_dir);
        objOptions.targetDir.setValue(source_dir);
        objOptions.filePath.setValue("");
        objOptions.operation.setValue("rename");
        objOptions.fileSpec.setValue(file_spec);
        objOptions.replacing.setValue(".*");
        objOptions.replacement.setValue("moved/[filename:]");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    public void renameFiles(final String source_dir, final String file_spec) throws Exception {
        SOSConnection2Options objConn = objOptions.getConnectionOptions();
        SOSConnection2OptionsAlternate objS = objConn.getSource();
        SOSConnection2OptionsAlternate objT = objConn.getTarget();
        objS.hostName.setValue(HOST_NAME_WILMA_SOS);
        objS.port.value(SOSOptionPortNumber.getStandardSFTPPort());
        objS.protocol.setValue("sftp");
        objS.user.setValue("test");
        objS.sshAuthMethod.isPassword(true);
        objS.password.setValue("12345");
        objT.hostName.setValue(HOST_NAME_WILMA_SOS);
        objT.port.value(SOSOptionPortNumber.getStandardSFTPPort());
        objT.protocol.setValue("sftp");
        objT.user.setValue("test");
        objT.sshAuthMethod.isPassword(true);
        objT.password.setValue("12345");
        objOptions.sourceDir.setValue(source_dir);
        objOptions.targetDir.setValue(source_dir);
        objOptions.filePath.setValue("");
        objOptions.operation.setValue("rename");
        objOptions.fileSpec.setValue(file_spec);
        objOptions.replacing.setValue(".*");
        objOptions.replacement.setValue("moved/[filename:]");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testRenameFiles() throws Exception {
        String strTestDir = "/home/test/temp/test/sosdex";
        renameFiles(strTestDir, "^\\d\\.txt$");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testRenameFiles2() throws Exception {
        String strTestDir = "/home/test/temp/test/sosdex";
        renameFiles(strTestDir, "^scheduler\\.dll$");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getSourceClient().getFileHandle(strTestDir + "/moved/scheduler.dll").fileExists();
        assertTrue("File must exist " + strTestDir + "/moved/scheduler.dll", flgResult);
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendMultipleFilesLocal2Local() throws Exception {
        CreateTestFiles(10);
        objOptions.getSource().protocol.setValue(enuTransferTypes.local);
        objOptions.getTarget().protocol.setValue(enuTransferTypes.local);
        objOptions.sourceDir.setValue(TEST_PATH_NAME);
        objOptions.targetDir.setValue(TEST_PATH_NAME + "/SOSMDX/");
        objOptions.filePath.setValue("");
        objOptions.fileNamePatternRegExp.setValue("^.*\\.txt$");
        objOptions.operation.setValue("copy");
        objOptions.getTarget().postCommand.setValue("echo $TargetFileName");
        objOptions.removeFiles.value(true);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendMultipleFilesThreaded() throws Exception {
        objOptions.maxConcurrentTransfers.value(10);
        objOptions.concurrentTransfer.value(true);
        testSendMultipleFilesLocal2Local();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testBigCopyThreaded() throws Exception {
        objOptions.maxConcurrentTransfers.value(30);
        objOptions.concurrentTransfer.value(true);
        testBigCopy();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testBigCopy() throws Exception {
        objOptions.getSource().protocol.setValue(enuTransferTypes.local);
        objOptions.getTarget().protocol.setValue(enuTransferTypes.local);
        objOptions.recursive.setTrue();
        objOptions.fileSpec.setValue("^.*$");
        objOptions.maxFiles.value(15);
        objOptions.sourceDir.setValue("R:/backup/sos/java/doxygen-docs");
        objOptions.targetDir.setValue("R:/backup/www.sos-berlin.com/doc/doxygen-docs");
        objOptions.operation.setValue(enuJadeOperations.copy);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    @Test
    public void testDeleteZipFile() throws Exception {
        final String conMethodName = CLASS_NAME + "::testDeleteZipFile";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFile, "-profile=zip_local_files" };
        objOptions.commandLineArgs(strCmdLineParameters);
        File fleFile = new File(objOptions.remoteDir.getValue());
        fleFile.delete();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testParameterPriority() throws Exception {
        final String conMethodName = CLASS_NAME + "::testParameterPriority";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFile, "-profile=zip_local_files_2", "-operation=receive" };
        objOptions.commandLineArgs(strCmdLineParameters);
        String strOperation = objOptions.operation.getValue();
        assertEquals("Operation not overwritten", "receive", strOperation);
        assertEquals("source protocol", "local", objOptions.getConnectionOptions().getSource().protocol.getValue());
        assertEquals("source dir", "J:\\E\\java\\junittests\\testdata\\SOSDataExchange/", objOptions.sourceDir.getValue());
        assertEquals("Operation not overwritten", "receive", strOperation);
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testParameterPriority2() throws Exception {
        final String conMethodName = CLASS_NAME + "::testParameterPriority";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFile, "-profile=zip_local_files", "-operation=getFileList" };
        objOptions.commandLineArgs(strCmdLineParameters);
        String strOperation = objOptions.operation.getValue();
        assertEquals("Precedence test failed", "getFileList", strOperation);
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testZipOperation() throws Exception {
        final String conMethodName = CLASS_NAME + "::testZipOperation";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFile, "-profile=zip_local_files" };
        objOptions.commandLineArgs(strCmdLineParameters);
        objOptions.sendTransferHistory.value(false);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testZipExtraction() throws Exception {
        final String conMethodName = CLASS_NAME + "::testZipExtraction";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFile, "-profile=zip_extract_2_local_files" };
        objOptions.commandLineArgs(strCmdLineParameters);
        objOptions.sendTransferHistory.value(false);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendMultipleZIPedFilesLocal2Local() throws Exception {
        objOptions.protocol.setValue("local");
        objOptions.filePath.setValue("");
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.remoteDir.setValue(TEST_PATH_NAME + "/SOSMDX/");
        objOptions.operation.setValue("send");
        objOptions.compressFiles.value(true);
        objOptions.compressedFileExtension.setValue(".zip");
        objOptions.concurrentTransfer.value(true);
        objOptions.maxConcurrentTransfers.value(5);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendMultipleFilesLocal2LocalAtomic() throws Exception {
        objOptions.protocol.setValue("local");
        objOptions.filePath.setValue("");
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.remoteDir.setValue(TEST_PATH_NAME + "/SOSMDX/");
        objOptions.operation.setValue("send");
        objOptions.atomicSuffix.setValue("~");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendMultipleFilesAtomicAndTransactional() throws Exception {
        objOptions.protocol.setValue("local");
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.remoteDir.setValue(TEST_PATH_NAME + "/SOSMDX/");
        objOptions.operation.setValue("send");
        objOptions.atomicSuffix.setValue(".xfer");
        objOptions.transactional.value(true);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testRenameMultipleFilesLocal() throws Exception {
        objOptions.protocol.setValue("local");
        objOptions.getConnectionOptions().getSource().protocol.setValue("local");
        objOptions.getConnectionOptions().getTarget().protocol.setValue("local");
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(TEST_PATH_NAME + "/SOSMDX/");
        objOptions.remoteDir.setValue(TEST_PATH_NAME + "/SOSMDX/");
        objOptions.operation.setValue("rename");
        objOptions.replacing.setValue("(.*)(.txt)");
        objOptions.replacement.setValue("\\1_[date:yyyyMMddHHmm];\\2");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testDeleteMultipleFilesLocal() throws Exception {
        objOptions.protocol.setValue("local");
        objOptions.getConnectionOptions().getSource().protocol.setValue("local");
        objOptions.getConnectionOptions().getTarget().protocol.setValue("local");
        objOptions.filePath.setValue("");
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(TEST_PATH_NAME + "/SOSMDX/");
        objOptions.remoteDir.setValue(TEST_PATH_NAME + "/SOSMDX/");
        objOptions.operation.setValue("delete");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendHugeNumberOfFiles() throws Exception {
        CreateTestFiles(50);
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue("");
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(TEST_PATH_NAME);
        objOptions.operation.setValue("send");
        objOptions.passiveMode.setTrue();
        objOptions.concurrentTransfer.value(true);
        objOptions.maxConcurrentTransfers.value(4);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testIniFile1() throws Exception {
        final String conMethodName = CLASS_NAME + "::CreateIniFile";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.setValue(constrSettingsTestFile);
        objOptions.profile.setValue("globals");
        objOptions.readSettingsFile();
        assertEquals("User ID", "kb", objOptions.user.getValue());
        assertEquals("password", "kb", objOptions.password.getValue());
    }

    @Test
    public void testCopy_Local2SFTP_recursive() throws Exception {
        final String conMethodName = CLASS_NAME + "::CreateIniFile";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("Copy_Local2SFTP_recursive");
        objOptions.readSettingsFile();
        assertEquals("User ID", "test", objOptions.getTarget().user.getValue());
        assertEquals("password", "12345", objOptions.getTarget().password.getValue());
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testJadeConfig() throws Exception {
        final String conMethodName = CLASS_NAME + "::testJadeConfig";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        objOptions.settings.setValue("examples/ConfigurationExample.jadeconf");
        objOptions.profile.setValue("copylocal2local1");
        objOptions.readSettingsFile();
        LOGGER.debug(objOptions.dirtyString());
        assertEquals("operation ", "copy", objOptions.operation.getValue());
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    @Ignore("Test set to Ignore for later examination")
    public void testIniFile2() throws Exception {
        final String conMethodName = CLASS_NAME + "::testIniFile2";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.setValue(constrSettingsTestFile);
        objOptions.profile.setValue("include-TestTest");
        objOptions.readSettingsFile();
        assertEquals("User ID", "kb", objOptions.user.getValue());
        assertEquals("password", "kb", objOptions.password.getValue());
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testIniFile3() throws Exception {
        final String conMethodName = CLASS_NAME + "::testIniFile2";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.setValue(constrSettingsTestFile);
        objOptions.profile.setValue("include-Test");
        objOptions.readSettingsFile();
        objOptions.localDir.setValue(".");
        assertEquals("User ID", "kb", objOptions.user.getValue());
        assertEquals("password", "kb", objOptions.password.getValue());
        assertEquals("Hostname", "localhost", objOptions.host.getValue());
        assertEquals("port", 88, objOptions.port.value());
        assertEquals("protocol", "scp", objOptions.protocol.getValue());
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    @Ignore("Test set to Ignore for later examination")
    public void testIniFile4() throws Exception {
        final String conMethodName = CLASS_NAME + "::testIniFile2";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.setValue(constrSettingsTestFile);
        objOptions.profile.setValue("include-TestWithNonexistenceInclude");
        objOptions.readSettingsFile();
        assertEquals("User ID", "kb", objOptions.user.getValue());
        assertEquals("password", "kb", objOptions.password.getValue());
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testIniFile5() throws Exception {
        final String conMethodName = CLASS_NAME + "::testIniFile5";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.setValue(constrSettingsTestFile);
        objOptions.profile.setValue("substitute-Test");
        objOptions.readSettingsFile();
        LOGGER.info(objOptions.dirtyString());
        String strComputerName = System.getenv("computername");
        assertEquals("User ID", System.getenv("username"), objOptions.user.getValue());
        assertEquals("Hostname", strComputerName, objOptions.host.getValue());
        assertEquals("Hostnameon Target ", strComputerName + "-abc", objOptions.getConnectionOptions().getTarget().hostName.getValue());
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testIniFileWithSourceAndTarget() throws Exception {
        final String conMethodName = CLASS_NAME + "::testIniFile5";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("ftp_server_2_server");
        objOptions.readSettingsFile();
        SOSConnection2Options objConn = objOptions.getConnectionOptions();
        assertEquals("Source.Host", HOST_NAME_WILMA_SOS, objConn.getSource().host.getValue());
        assertEquals("Target.Host", HOST_NAME_8OF9_SOS, objConn.getTarget().host.getValue());
        assertEquals("file_path", "test.txt", objOptions.filePath.getValue());
        objOptions.checkMandatory();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void BRANDUP_MOND_CRM_POC() throws Exception {
        final String conMethodName = CLASS_NAME + "::BRANDUP_MOND_CRM_POC";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("BRANDUP_MOND_CRM_POC");
        objOptions.readSettingsFile();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
    }

    @Test
    public void testAliasFromIniFile() {
        JSFile objIni = new JSFile(constrSettingsTestFile);
        try {
            objIni.writeLine("[testAlias]");
            objIni.writeLine("auth_method=password");
            objIni.writeLine("verbose=9");
            objIni.close();
            objOptions = new JADEOptions();
            objOptions.settings.setValue(constrSettingsTestFile);
            objOptions.profile.setValue("testAlias");
            objOptions.readSettingsFile();
            assertEquals("Alias: auth_method", "password", objOptions.authMethod.getValue());
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    private void CreateIniFile() throws Exception {
        JSFile objIni = new JSFile(constrSettingsTestFile);
        objIni.writeLine("[globals]");
        objIni.writeLine("globaluser=kb");
        objIni.writeLine("globalpassword=kb");
        objIni.writeLine("[include1]");
        objIni.writeLine("host=localhost");
        objIni.writeLine("[include2]");
        objIni.writeLine("port=88");
        objIni.writeLine("[include3]");
        objIni.writeLine("protocol=scp");
        objIni.writeLine("[include1_and_2]");
        objIni.writeLine("include=include1,include2");
        objIni.writeLine("[include-Test]");
        objIni.writeLine("include=include1_and_2,include3");
        objIni.writeLine("[include-TestWithNonexistenceInclude]");
        objIni.writeLine("include=include1,includeabcd2,include3");
        objIni.writeLine("[substitute-Test]");
        objIni.writeLine("user=${USERNAME}");
        objIni.writeLine("host=${COMPUTERNAME}");
        objIni.writeLine("cannotsubstitutet=${waltraut}");
        objIni.writeLine("title=${globaluser} and ${globalpassword}");
        objIni.writeLine("target_host=${host}-abc");
        objIni.writeLine("alternate_target_host=${host}-abc");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testGenericIniFile1() throws Exception {
        executeGenericIniFile("", "cumulate_test");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testGenericIniFile2() throws Exception {
        CreateTestFiles(15);
        executeGenericIniFile("", "cumulate_using_cumulative_file");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testsftp_cumulate_using_cumulative_file() throws Exception {
        CreateTestFiles(15);
        executeGenericIniFile("", "sftp_cumulate_using_cumulative_file");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testCopyFilesWithMD5() throws Exception {
        CreateTestFiles(15);
        executeGenericIniFile("", "copy_files_with_md5");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testCopyFilesCheckMD5() throws Exception {
        CreateTestFiles(15);
        executeGenericIniFile("", "copy_files_check_md5");
    }

    @Test
    @Ignore("Test set to Ignore for later examination, fails in Jenkins build")
    public void testGenericIniFile3() throws Exception {
        CreateTestFiles(15);
        executeGenericIniFile("", "ftp_receive_2_wilma");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testBackgroundService() throws Exception {
        executeGenericIniFile("", "ftp_background");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testWithoutLoadClassName() throws Exception {
        executeGenericIniFile("", "ftp_without_loadClassName");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void receive_zbf_relaxed() throws Exception {
        executeGenericIniFile("", "receive_zbf_relaxed");
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    @Ignore("Test set to Ignore for later examination")
    public void receive_zbf_strict() throws Exception {
        executeGenericIniFile("", "receive_zbf_strict");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void receive_zbf_no() throws Exception {
        executeGenericIniFile("", "receive_zbf_no");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void receive_zbf_no_noFiles() throws Exception {
        executeGenericIniFile("", "receive_zbf_no_noFiles");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void ReceiveUsingKeePass() throws Exception {
        executeGenericIniFile("", "ReceiveUsingKeePass");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void SendUsingKeePass() throws Exception {
        executeGenericIniFile("", "SendUsingKeePass");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void P2PCopyUsingKeePass() throws Exception {
        executeGenericIniFile("", "P2PCopyUsingKeePass");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void ReceiveUsingKeePassExpired() throws Exception {
        executeGenericIniFile("", "ReceiveUsingKeePassExpired");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void SendUsingKeePassExpired() throws Exception {
        executeGenericIniFile("", "SendUsingKeePassExpired");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void P2PCopyUsingKeePassExpired() throws Exception {
        executeGenericIniFile("", "P2PCopyUsingKeePassExpired");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void ReceiveUsingSSHKeyKeePass() throws Exception {
        executeGenericIniFile("", "ReceiveUsingSSHKeyKeePass");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void ReceiveUsingSFTPURLKeePass() throws Exception {
        executeGenericIniFile("", "ReceiveUsingSFTPURLKeePass");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void ReceiveUsingKeePassSecuredWithPpk() throws Exception {
        executeGenericIniFile("", "ReceiveUsingKeePassSecuredWithPpk");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void receive_zbf_yes() throws Exception {
        executeGenericIniFile("", "receive_zbf_yes");
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    public void sftp_receive_local_wrong_host() throws Exception {
        executeGenericIniFile("", "sftp_receive_local_wrong_host");
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    @Ignore("Test set to Ignore for later examination")
    public void receive_zbf_no_onlyzbf() throws Exception {
        executeGenericIniFile("", "receive_zbf_no_onlyzbf");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void send_zbf_yes() throws Exception {
        executeGenericIniFile("", "send_zbf_yes");
    }

    private void executeGenericIniFile(final String pstrIniFileName, final String pstrProfileName) throws Exception {
        LOGGER.info(System.getProperty("user.dir"));
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue(pstrProfileName);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
    }

    @Test
    public void testAliasSettings() throws Exception {
        Map<String, String> objHsh = new HashMap<String, String>();
        String strPassword = SOSOptionAuthenticationMethod.enuAuthenticationMethods.password.getText();
        objHsh.put("ssh_auth_method", strPassword);
        objOptions = new JADEOptions();
        objOptions.setAllOptions((HashMap<String, String>) objHsh);
        assertEquals("Default: ssh_auth_method", SOSOptionAuthenticationMethod.enuAuthenticationMethods.publicKey.getText().toLowerCase(),
                objOptions.authMethod.getDefaultValue());
        assertEquals("Alias: ssh_auth_method", strPassword, objOptions.authMethod.getValue());
        objHsh.put("source_ssh_auth_method", strPassword);
        objOptions = new JADEOptions();
        objOptions.setAllOptions((HashMap<String, String>) objHsh);
        assertEquals("Alias: source_ssh_auth_method", strPassword, objOptions.getSource().authMethod.getValue());
        objHsh.put("target_ssh_auth_method", strPassword);
        objOptions = new JADEOptions();
        objOptions.setAllOptions((HashMap<String, String>) objHsh);
        assertEquals("Alias: target_ssh_auth_method", strPassword, objOptions.getTarget().authMethod.getValue());
        objHsh.put("source_auth_method", strPassword);
        objOptions = new JADEOptions();
        objOptions.setAllOptions((HashMap<String, String>) objHsh);
        assertEquals("Alias: source_auth_method", strPassword, objOptions.getSource().authMethod.getValue());
    }

    @Test
    public void testAliasSettings2() throws Exception {
        Map<String, String> objHsh = new HashMap<String, String>();
        String strPassword = SOSOptionAuthenticationMethod.enuAuthenticationMethods.password.getText();
        objHsh.put("auth_method", strPassword);
        objOptions = new JADEOptions();
        objOptions.setAllOptions((HashMap<String, String>) objHsh);
        assertEquals("Alias: auth_method", strPassword, objOptions.authMethod.getValue());
        objHsh.put("source_auth_method", strPassword);
        objOptions = new JADEOptions();
        objOptions.setAllOptions((HashMap<String, String>) objHsh);
        assertEquals("Alias: source_auth_method", strPassword, objOptions.getSource().authMethod.getValue());
        objHsh.put("target_auth_method", strPassword);
        objOptions = new JADEOptions();
        objOptions.setAllOptions((HashMap<String, String>) objHsh);
        assertEquals("Alias: target_auth_method", strPassword, objOptions.getTarget().authMethod.getValue());
    }

    @Test
    public void testHashMapSettings() throws Exception {
        Map<String, String> objHsh = new HashMap<String, String>();
        objHsh.put("operation", "copy");
        objHsh.put("source_host", HOST_NAME_WILMA_SOS);
        objHsh.put("alternative_source_user", "sos");
        objHsh.put("alternative_source_password", "sos");
        objHsh.put("source_user", "sos");
        objHsh.put("source_port", "22");
        objHsh.put("source_protocol", "sftp");
        objHsh.put("source_password", "sos");
        objHsh.put("source_dir", "/home/sos/setup.scheduler/releases");
        objHsh.put("source_ssh_auth_method", "password");
        objHsh.put("target_host", "tux.sos");
        objHsh.put("target_protocol", "sftp");
        objHsh.put("target_port", "22");
        objHsh.put("target_password", "sos");
        objHsh.put("target_user", "sos");
        objHsh.put("alternative_target_user", "abcdef");
        objHsh.put("target_dir", "/srv/www/htdocs/test");
        objHsh.put("target_ssh_auth_method", "password");
        objHsh.put("overwrite_files", "true");
        objHsh.put("check_size", "true");
        objHsh.put("file_spec", "^scheduler_(win32|linux)_joe\\.[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]{4}\\.(tar\\.gz|zip)$");
        objHsh.put("recursive", "false");
        objHsh.put("verbose", "9");
        objHsh.put("buffer_size", "32000");
        objHsh.put("SendTransferHistory", "false");
        objHsh.put("log_filename", "c:/temp/test.log");
        objOptions = new JADEOptions();
        objOptions.setAllOptions(objOptions.deletePrefix((HashMap<String, String>) objHsh, "ftp_"));
        assertEquals("", HOST_NAME_WILMA_SOS, objOptions.getConnectionOptions().getSource().host.getValue());
        assertEquals("", "tux.sos", objOptions.getConnectionOptions().getTarget().host.getValue());
        assertEquals("", "/srv/www/htdocs/test/", objOptions.targetDir.getValue());
        assertEquals("log filename not set", "c:/temp/test.log", objOptions.logFilename.getValue());
        assertEquals("log filename not set", "c:/temp/test.log", objOptions.getOptionByName("log_filename"));
        String strReplTest = "Hallo, welt %{log_filename} und \nverbose = %{verbose} ersetzt. Date %{date} wird nicht ersetzt";
        String strR = objOptions.replaceVars(strReplTest);
        LOGGER.info(strR);
        strReplTest = "Hallo, welt %{log_filename} und" + "\n" + "verbose = %{verbose} ersetzt. Date %{date} wird nicht ersetzt";
        strR = objOptions.replaceVars(strReplTest);
        LOGGER.info(strR);
        assertEquals("log filename not set", "sos", objOptions.getConnectionOptions().getSource().getAlternatives().user.getValue());
        assertEquals("log filename not set", "abcdef", objOptions.getConnectionOptions().getTarget().getAlternatives().user.getValue());
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    public void testSendWithHashMapSettings() throws Exception {
        Map<String, String> objHsh = new HashMap<String, String>();
        objHsh.put("operation", "send");
        objHsh.put("local_dir", "c:/temp/");
        objHsh.put("host", "tux.sos");
        objHsh.put("protocol", "sftp");
        objHsh.put("port", "22");
        objHsh.put("password", "sos");
        objHsh.put("user", "sos");
        objHsh.put("remote_dir", "/srv/www/htdocs/test");
        objHsh.put("ssh_auth_method", SOSOptionAuthenticationMethod.enuAuthenticationMethods.password.text);
        objHsh.put("overwrite_files", "true");
        objHsh.put("check_size", "true");
        objHsh.put("file_spec", "^scheduler_(win32|linux)_joe\\.[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]{4}\\.(tar\\.gz|zip)$");
        objHsh.put("recursive", "false");
        objHsh.put("verbose", "9");
        objHsh.put("SendTransferHistory", "false");
        objOptions = new JADEOptions();
        objOptions.setAllOptions(objOptions.deletePrefix((HashMap<String, String>) objHsh, "ftp_"));
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objOptions.checkMandatory();
        assertEquals("", "localhost", objOptions.getConnectionOptions().getSource().host.getValue());
        assertEquals("", "tux.sos", objOptions.getConnectionOptions().getTarget().host.getValue());
        assertEquals("", "sftp", objOptions.getConnectionOptions().getTarget().protocol.getValue());
        objJadeEngine.execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendWithHashMapSettings2() throws Exception {
        Map<String, String> objHsh = new HashMap<String, String>();
        objHsh.put("operation", "send");
        objHsh.put("local_dir", "c:/temp/");
        objHsh.put("host", "tux.sos");
        objHsh.put("port", "22");
        objHsh.put("password", "sos");
        objHsh.put("user", "sos");
        objHsh.put("remote_dir", "/srv/www/htdocs/test");
        objHsh.put("ssh_auth_method", SOSOptionAuthenticationMethod.enuAuthenticationMethods.password.text);
        objHsh.put("overwrite_files", "true");
        objHsh.put("check_size", "true");
        objHsh.put("file_spec", "^scheduler_(win32|linux)_joe\\.[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]{4}\\.(tar\\.gz|zip)$");
        objHsh.put("recursive", "false");
        objHsh.put("verbose", "9");
        objHsh.put("SendTransferHistory", "false");
        objOptions = new JADEOptions();
        objOptions.protocol.setValue(enuTransferTypes.ftp);
        objOptions.setAllOptions(objOptions.deletePrefix((HashMap<String, String>) objHsh, "ftp_"));
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objOptions.checkMandatory();
        assertEquals("", "localhost", objOptions.getConnectionOptions().getSource().host.getValue());
        assertEquals("", "tux.sos", objOptions.getConnectionOptions().getTarget().host.getValue());
        assertEquals("", "ftp", objOptions.getConnectionOptions().getTarget().protocol.getValue());
    }

    @Test
    public void testHashMapSettings3() throws Exception {
        Map<String, String> objHsh = new HashMap<String, String>();
        objHsh.put("operation", "copy");
        objHsh.put("source_host", HOST_NAME_WILMA_SOS);
        objHsh.put("source_user", "sos");
        objHsh.put("source_port", "22");
        objHsh.put("source_protocol", "sftp");
        objHsh.put("source_password", "sos");
        objHsh.put("source_dir", "/home/sos/setup.scheduler/releases");
        objHsh.put("source_ssh_auth_method", "password");
        objHsh.put("target_host", "tux.sos");
        objHsh.put("target_protocol", "sftp");
        objHsh.put("target_port", "22");
        objHsh.put("target_password", "sos");
        objHsh.put("target_user", "sos");
        objHsh.put("target_dir", "/srv/www/htdocs/test");
        objHsh.put("target_ssh_auth_method", "password");
        objHsh.put("overwrite_files", "true");
        objHsh.put("check_size", "true");
        objHsh.put("file_spec", "^scheduler_(win32|linux)_joe\\.[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]{4}\\.(tar\\.gz|zip)$");
        objHsh.put("recursive", "false");
        objHsh.put("verbose", "9");
        objHsh.put("buffer_size", "32000");
        objHsh.put("SendTransferHistory", "false");
        String strCmd = "SITE chmod 777 $SourceFileName";
        objHsh.put("source_pre_command", strCmd);
        objHsh.put("target_pre_command", strCmd);
        objOptions = new JADEOptions();
        objOptions.setAllOptions(objOptions.deletePrefix((HashMap<String, String>) objHsh, "ftp_"));
        assertEquals("", HOST_NAME_WILMA_SOS, objOptions.getConnectionOptions().getSource().host.getValue());
        assertEquals("", "tux.sos", objOptions.getConnectionOptions().getTarget().host.getValue());
        assertEquals("", "/srv/www/htdocs/test/", objOptions.targetDir.getValue());
        assertEquals("source", strCmd, objOptions.getSource().preCommand.getValue());
        assertEquals("target", strCmd, objOptions.getTarget().preCommand.getValue());
        String strT2 = strCmd.replace("$SourceFileName", "testfile");
        assertEquals("target", "SITE chmod 777 testfile", strT2);
    }

    @Test
    public void testControlMChar() {
        Pattern SECTION_PATTERN = Pattern.compile("^\\s*\\[([^\\]]*)\\].*$");
        String strT = "[profilename]\r";
        strT = strT.replaceAll("\\r", "");
        Matcher matcher = SECTION_PATTERN.matcher(strT);
        if (matcher.matches()) {
            String sectionName = matcher.group(1);
            LOGGER.info(sectionName);
        } else {
            LOGGER.info("no match in range");
        }
        if (strT.contains("\r")) {
            LOGGER.info("enthalten.");
        } else {
            LOGGER.info("nicht enthalten.");
        }
        LOGGER.info(strT);
    }
    
    @Test
    public void testXPath() throws Exception {
        String xml = "<Selection>"
                   + "    <FilePathSelection>"
                   + "        <FilePath><![CDATA[hallo.txt]]></FilePath>"
                   + "        <Directory><![CDATA[/tmp]]></Directory>"
                   + "    </FilePathSelection>"
                   + "</Selection>";
        SOSXMLXPath xPath = new SOSXMLXPath(new StringBuffer(xml));
        LOGGER.info("Document: " + xPath.getDocument());
        LOGGER.info("Element: " + xPath.getElement());
        LOGGER.info("Node //FilePath: " + xPath.selectSingleNodeValue("//FilePath"));
        LOGGER.info("Node //Directory: " + xPath.selectSingleNodeValue("//Directory"));
        LOGGER.info("Node /Selection/FilePathSelection/FilePath: " + xPath.selectSingleNodeValue("/Selection/FilePathSelection/FilePath"));
        LOGGER.info("Node /Selection/FilePathSelection/Directory: " + xPath.selectSingleNodeValue("/Selection/FilePathSelection/Directory"));
        String irgendwat = xPath.selectSingleNodeValue("/Selection/FilePathSelection/Irgendwat");
        LOGGER.info("Node /Selection/FilePathSelection/Irgendwat sollte null sein: " + irgendwat);
        assertNull(irgendwat);
    }
    
    @Test
    public void testPreProcessing() throws Exception{
        JADEOptions initTestOptions = new JADEOptions();
        initTestOptions.settings.setValue(strSettingsFile);
        initTestOptions.getSource().protocol.setValue("sourcePROTOCOLtoOverwrite");
        initTestOptions.getSource().host.setValue("sourceHOSTtoOverwrite");
        initTestOptions.getSource().user.setValue("sourceUSERtoOverwirte");
        initTestOptions.getSource().password.setValue("sourcePASSWDtoOverwrite");
        initTestOptions.getSource().authFile.setValue("sourceAUTH_FILEtoOverwrite");
        initTestOptions.getSource().authMethod.setValue("sourceAUTH_METHODtoOverwrite");
        initTestOptions.getSource().directory.setValue("sourceDIRECTORYtoOverwrite");
        initTestOptions.getTarget().protocol.setValue("targetPROTOCOLtoOverwrite");
        initTestOptions.getTarget().host.setValue("targetHOSTtoOverwrite");
        initTestOptions.getTarget().user.setValue("targetUSERtoOverwirte");
        initTestOptions.getTarget().password.setValue("targetPASSWDtoOverwrite");
        initTestOptions.getTarget().authFile.setValue("targetAUTH_FILEtoOverwrite");
        initTestOptions.getTarget().authMethod.setValue("targetAUTH_METHODtoOverwrite");
        initTestOptions.getTarget().directory.setValue("targetDIRECTORYtoOverwrite");
        initTestOptions.filePath.setValue("FILE_PATHtoOverwrite");
        initTestOptions.fileSpec.setValue("FILE_SPECtoOverwrite");
        String xmlSelectionSnippet = "<Selection><FilePathSelection><FilePath><![CDATA[hallo.txt]]></FilePath><Directory><![CDATA[/tmp]]>"
                + "</Directory></FilePathSelection></Selection>";
        String xmlAllProtocolsSomeProfiles = "<?xml version='1.0' encoding='utf-8'?><Configurations><Fragments><ProtocolFragments>"
                + "<FTPFragment name='ftp'><BasicConnection><Hostname><![CDATA[homer.sos]]></Hostname></BasicConnection><BasicAuthentication>"
                + "<Account><![CDATA[test]]></Account><Password><![CDATA[test]]></Password></BasicAuthentication><JumpFragmentRef ref='jump'/>"
                + "</FTPFragment><FTPSFragment name='ftps'><BasicConnection><Hostname><![CDATA[homer.sos]]></Hostname></BasicConnection>"
                + "<BasicAuthentication><Account><![CDATA[test]]></Account><Password><![CDATA[test]]></Password></BasicAuthentication>"
                + "</FTPSFragment><HTTPFragment name='http'><URLConnection><URL>http://sp.sos:4111</URL></URLConnection></HTTPFragment>"
                + "<HTTPSFragment name='https'><URLConnection><URL>https://sp.sos:4111</URL></URLConnection></HTTPSFragment>"
                + "<JumpFragment name='jump'><BasicConnection><Hostname><![CDATA[homer.sos]]></Hostname></BasicConnection><SSHAuthentication>"
                + "<Account><![CDATA[test]]></Account><AuthenticationMethodPassword><Password><![CDATA[test]]></Password>"
                + "</AuthenticationMethodPassword></SSHAuthentication><JumpCommand><![CDATA[YADE.cmd]]></JumpCommand></JumpFragment>"
                + "<SFTPFragment name='sftp'><BasicConnection><Hostname><![CDATA[homer.sos]]></Hostname></BasicConnection><SSHAuthentication>"
                + "<Account><![CDATA[test]]></Account><AuthenticationMethodPassword><Password><![CDATA[test]]></Password>"
                + "</AuthenticationMethodPassword></SSHAuthentication></SFTPFragment><SMBFragment name='smb'><Hostname><![CDATA[homer.sos]]>"
                + "</Hostname><SMBAuthentication><Account><![CDATA[test]]></Account><Password><![CDATA[test]]></Password></SMBAuthentication>"
                + "</SMBFragment><WebDAVFragment name='webDAV'><URLConnection><URL>webDAV://sp.sos:4111</URL></URLConnection><BasicAuthentication>"
                + "<Account><![CDATA[test]]></Account><Password><![CDATA[test]]></Password></BasicAuthentication></WebDAVFragment>"
                + "</ProtocolFragments></Fragments><Profiles><Profile profile_id='example_filePath'><Operation><Copy><CopySource>"
                + "<CopySourceFragmentRef><LocalSource /></CopySourceFragmentRef><SourceFileOptions><Selection><FilePathSelection><FilePath>"
                + "<![CDATA[hallo.txt]]></FilePath><Directory><![CDATA[/tmp]]></Directory></FilePathSelection></Selection></SourceFileOptions>"
                + "</CopySource><CopyTarget><CopyTargetFragmentRef><FTPFragmentRef ref='ftp' /></CopyTargetFragmentRef><Directory>"
                + "<![CDATA[path_to_target_Directory]]></Directory></CopyTarget></Copy></Operation></Profile><Profile profile_id='example_fileList'>"
                + "<Operation><Copy><CopySource><CopySourceFragmentRef><LocalSource /></CopySourceFragmentRef><SourceFileOptions><Selection>"
                + "<FileListSelection><FileList><![CDATA[FileWithList.txt]]></FileList><Directory><![CDATA[path_to_file]]></Directory>"
                + "</FileListSelection></Selection></SourceFileOptions></CopySource><CopyTarget><CopyTargetFragmentRef><FTPFragmentRef ref='ftp'/>"
                + "</CopyTargetFragmentRef><Directory><![CDATA[path_to_target_Directory]]></Directory></CopyTarget></Copy></Operation></Profile>"
                + "<Profile profile_id='example_fileSpec'><Operation><Copy><CopySource><CopySourceFragmentRef><LocalSource/></CopySourceFragmentRef>"
                + "<SourceFileOptions><Selection><FileSpecSelection><FileSpec><![CDATA[.*]]></FileSpec><Directory><![CDATA[path_to_directory]]>"
                + "</Directory><Recursive>false</Recursive></FileSpecSelection></Selection></SourceFileOptions></CopySource><CopyTarget>"
                + "<CopyTargetFragmentRef><FTPFragmentRef ref='ftp' /></CopyTargetFragmentRef><Directory><![CDATA[path_to_target_Directory]]>"
                + "</Directory></CopyTarget></Copy></Operation></Profile></Profiles></Configurations>";
        String copyXml = "<?xml version='1.0' encoding='utf-8'?><Configurations><Fragments><ProtocolFragments><FTPFragment name='ftp'>"
                + "<BasicConnection><Hostname><![CDATA[homer.sos]]></Hostname></BasicConnection><BasicAuthentication><Account><![CDATA[test]]>"
                + "</Account><Password><![CDATA[test]]></Password></BasicAuthentication></FTPFragment><SFTPFragment name='sftp'>"
                + "<BasicConnection><Hostname><![CDATA[homer.sos]]></Hostname></BasicConnection><SSHAuthentication><Account><![CDATA[test]]>"
                + "</Account><AuthenticationMethodPassword><Password><![CDATA[test]]></Password></AuthenticationMethodPassword>"
                + "</SSHAuthentication></SFTPFragment></ProtocolFragments></Fragments><Profiles><Profile profile_id='example_filePath'>"
                + "<Operation><Copy><CopySource><CopySourceFragmentRef><SFTPFragmentRef ref='sftp' /></CopySourceFragmentRef><SourceFileOptions>"
                + "<Selection><FilePathSelection><FilePath><![CDATA[hallo.txt]]></FilePath><Directory><![CDATA[/tmp]]></Directory>"
                + "</FilePathSelection></Selection></SourceFileOptions></CopySource><CopyTarget><CopyTargetFragmentRef>"
                + "<FTPFragmentRef ref='ftp' /></CopyTargetFragmentRef><Directory><![CDATA[path_to_target_Directory]]></Directory>"
                + "</CopyTarget></Copy></Operation></Profile></Profiles></Configurations>";
        String copyWithJumpXml = "<?xml version='1.0' encoding='utf-8'?><Configurations><Fragments><ProtocolFragments><FTPFragment name='ftp'>"
                + "<BasicConnection><Hostname><![CDATA[homer.sos]]></Hostname></BasicConnection><BasicAuthentication><Account><![CDATA[test]]>"
                + "</Account><Password><![CDATA[test]]></Password></BasicAuthentication></FTPFragment><JumpFragment name='THEjumpFragment'>"
                + "<BasicConnection><Hostname><![CDATA[JumpHost.sos]]></Hostname></BasicConnection><SSHAuthentication><Account>"
                + "<![CDATA[JumpUserAccount]]></Account><AuthenticationMethodPassword><Password><![CDATA[JumpUserPasswd]]></Password>"
                + "</AuthenticationMethodPassword></SSHAuthentication><JumpCommand><![CDATA[/THIS/IS/THE/JUMP/COMMAND.cmd]]></JumpCommand>"
                + "</JumpFragment><SFTPFragment name='sftp'><BasicConnection><Hostname><![CDATA[homer.sos]]></Hostname></BasicConnection>"
                + "<SSHAuthentication><Account><![CDATA[test]]></Account><AuthenticationMethodPassword><Password><![CDATA[test]]></Password>"
                + "</AuthenticationMethodPassword></SSHAuthentication><JumpFragmentRef ref='THEjumpFragment' /></SFTPFragment></ProtocolFragments>"
                + "</Fragments><Profiles><Profile profile_id='example_filePath'><Operation><Copy><CopySource><CopySourceFragmentRef>"
                + "<SFTPFragmentRef ref='sftp' /></CopySourceFragmentRef><SourceFileOptions><Selection><FilePathSelection><FilePath>"
                + "<![CDATA[hallo.txt]]></FilePath><Directory><![CDATA[/tmp]]></Directory></FilePathSelection></Selection></SourceFileOptions>"
                + "</CopySource><CopyTarget><CopyTargetFragmentRef><FTPFragmentRef ref='ftp' /></CopyTargetFragmentRef><Directory>"
                + "<![CDATA[path_to_target_Directory]]></Directory></CopyTarget></Copy></Operation></Profile></Profiles></Configurations>";
        String moveXml = "<?xml version='1.0' encoding='utf-8'?><Configurations><Fragments><ProtocolFragments><FTPFragment name='ftp'>"
                + "<BasicConnection><Hostname><![CDATA[homer.sos]]></Hostname></BasicConnection><BasicAuthentication><Account><![CDATA[test]]>"
                + "</Account><Password><![CDATA[test]]></Password></BasicAuthentication></FTPFragment><SFTPFragment name='sftp'>"
                + "<BasicConnection><Hostname><![CDATA[homer.sos]]></Hostname></BasicConnection><SSHAuthentication><Account><![CDATA[test]]>"
                + "</Account><AuthenticationMethodPassword><Password><![CDATA[test]]></Password></AuthenticationMethodPassword>"
                + "</SSHAuthentication></SFTPFragment></ProtocolFragments></Fragments><Profiles><Profile profile_id='example_filePath'>"
                + "<Operation><Move><MoveSource><MoveSourceFragmentRef><SFTPFragmentRef ref='sftp' /></MoveSourceFragmentRef><SourceFileOptions>"
                + "<Selection><FilePathSelection><FilePath><![CDATA[/tmp/fileToMove.txt]]></FilePath><Directory><![CDATA[/home/test/data]]>"
                + "</Directory></FilePathSelection></Selection></SourceFileOptions></MoveSource><MoveTarget><MoveTargetFragmentRef>"
                + "<FTPFragmentRef ref='ftp' /></MoveTargetFragmentRef><Directory><![CDATA[/notHome/notTest/notData]]></Directory></MoveTarget>"
                + "</Move></Operation></Profile></Profiles></Configurations>";
        String removeXml = "<?xml version='1.0' encoding='utf-8'?><Configurations><Fragments><ProtocolFragments><FTPFragment name='ftp'>"
                + "<BasicConnection><Hostname><![CDATA[homer.sos]]></Hostname></BasicConnection><BasicAuthentication><Account><![CDATA[test]]>"
                + "</Account><Password><![CDATA[test]]></Password></BasicAuthentication></FTPFragment><SFTPFragment name='sftp'>"
                + "<BasicConnection><Hostname><![CDATA[homer.sos]]></Hostname></BasicConnection><SSHAuthentication><Account><![CDATA[test]]>"
                + "</Account><AuthenticationMethodPassword><Password><![CDATA[test]]></Password></AuthenticationMethodPassword>"
                + "</SSHAuthentication></SFTPFragment></ProtocolFragments></Fragments><Profiles><Profile profile_id='example_filePath'>"
                + "<Operation><Remove><RemoveSource><RemoveSourceFragmentRef><SFTPFragmentRef ref='sftp' /></RemoveSourceFragmentRef>"
                + "<SourceFileOptions><Selection><FilePathSelection><FilePath><![CDATA[/tmp/FileToRemove.txt]]></FilePath><Directory>"
                + "<![CDATA[/home/test/data]]></Directory></FilePathSelection></Selection></SourceFileOptions></RemoveSource></Remove>"
                + "</Operation></Profile></Profiles></Configurations>";
        String getListXml = "<?xml version='1.0' encoding='utf-8'?><Configurations><Fragments><ProtocolFragments><FTPFragment name='ftp'>"
                + "<BasicConnection><Hostname><![CDATA[homer.sos]]></Hostname></BasicConnection><BasicAuthentication><Account><![CDATA[test]]>"
                + "</Account><Password><![CDATA[test]]></Password></BasicAuthentication></FTPFragment><SFTPFragment name='sftp'>"
                + "<BasicConnection><Hostname><![CDATA[homer.sos]]></Hostname></BasicConnection><SSHAuthentication><Account><![CDATA[test]]>"
                + "</Account><AuthenticationMethodPassword><Password><![CDATA[test]]></Password></AuthenticationMethodPassword>"
                + "</SSHAuthentication></SFTPFragment></ProtocolFragments></Fragments><Profiles><Profile profile_id='example_filePath'>"
                + "<Operation><GetList><GetListSource><GetListSourceFragmentRef><SFTPFragmentRef ref='sftp' /></GetListSourceFragmentRef>"
                + "<SourceFileOptions><Selection><FilePathSelection><FilePath><![CDATA[/tmp/GetListFile.txt]]></FilePath></FilePathSelection>"
                + "</Selection></SourceFileOptions></GetListSource></GetList></Operation></Profile></Profiles></Configurations>";
        UpdateXmlToOptionHelper updateHelper = new UpdateXmlToOptionHelper(initTestOptions);
        LOGGER.info("***********************************************SELECTION VALUES*******************************************************************");
        updateHelper.getOptions().xmlUpdate.setValue(xmlSelectionSnippet);
        updateHelper.executeBefore();
        LOGGER.info("***********************************************BIG CONFIG VALUES******************************************************************");
        updateHelper.setOptions(initTestOptions);
        updateHelper.getOptions().xmlUpdate.setValue(xmlAllProtocolsSomeProfiles);
        updateHelper.executeBefore();
        LOGGER.info("**************************************************COPY VALUES*********************************************************************");
        updateHelper.setOptions(initTestOptions);
        updateHelper.getOptions().xmlUpdate.setValue(copyXml);
        updateHelper.executeBefore();
        LOGGER.info("*********************************************COPY WITH JUMP VALUES****************************************************************");
        updateHelper.setOptions(initTestOptions);
        updateHelper.getOptions().xmlUpdate.setValue(copyWithJumpXml);
        updateHelper.executeBefore();
        LOGGER.info("**************************************************MOVE VALUES*********************************************************************");
        updateHelper.setOptions(initTestOptions);
        updateHelper.getOptions().xmlUpdate.setValue(moveXml);
        updateHelper.executeBefore();
        LOGGER.info("*************************************************REMOVE VALUES********************************************************************");
        updateHelper.setOptions(initTestOptions);
        updateHelper.getOptions().xmlUpdate.setValue(removeXml);
        updateHelper.executeBefore();
        LOGGER.info("************************************************GETLIST VALUES********************************************************************");
        updateHelper.setOptions(initTestOptions);
        updateHelper.getOptions().xmlUpdate.setValue(getListXml);
        updateHelper.executeBefore();
    }

}