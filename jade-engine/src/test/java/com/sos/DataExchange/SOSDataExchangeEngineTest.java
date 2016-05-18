package com.sos.DataExchange;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sos.DataExchange.Options.JADEOptions;
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
    private static final Logger LOGGER = Logger.getLogger(SOSDataExchangeEngineTest.class);
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
        objOptions.ApplicationName.Value("JADE");
        objOptions.ApplicationDocuUrl.Value("http://www.sos-berlin.com/doc/en/jade/JADE Parameter Reference.pdf");
        dynamicClassNameSource = "com.sos.VirtualFileSystem.SFTP.SOSVfsSFtpJCraft";
        objVFS = VFSFactory.getHandler(objOptions.protocol.Value());
    }

    private void createTestFile() {
        createTestFile(strTestFileName);
    }

    private void createTestFile(final String pstrFileName) {
        JSFile objFile = new JSFile(TEST_PATH_NAME, pstrFileName);
        try {
            objFile.WriteLine("This is a simple Testfile. nothing else.");
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
                    objFile.WriteLine("This is a simple Testfile, created for the masstest. nothing else.");
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
                LOGGER.debug(i);
                objFile = new JSFile(TEST_PATH_NAME + "/test-" + i + ".poll");
                try {
                    Thread.sleep(5000);
                    objFile.Write(i + ": This is a test");
                    objFile.WriteLine(i + ": This is a test");
                    objFile.WriteLine(i + ": This is a test");
                    objFile.WriteLine(i + ": This is a test");
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
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.protocol.Value(enuTransferTypes.sftp);
        assertEquals("sftp", "sftp", objOptions.protocol.Value());
        objOptions.port.value(SOSOptionPortNumber.conPort4SSH);
        objOptions.user.Value("test");
        objOptions.password.Value("12345");
        objOptions.authMethod.Value(enuAuthenticationMethods.password);
        if (flgUseFilePath) {
            objOptions.filePath.Value("R:/backup/sos/java/junittests/testdata/SOSDataExchange/test-0.poll");
        } else {
            objOptions.fileNamePatternRegExp.Value("^.*\\.poll$");
            objOptions.pollMinfiles.value(1);
        }
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.operation.Value("send");
        objOptions.log_filename.Value(objOptions.TempDir() + "test.log");
        objOptions.profile.Value(conMethodName);
        objOptions.pollInterval.Value("0:30");
        objOptions.pollingDuration.Value("05:00");
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
        objOptions.operation.Value(enuJadeOperations.rename);
        objOptions.Source().url.Value("file:///R:/backup/projects/anubex-dws/JCLs");
        objOptions.ReplaceWhat.Value(strReplaceWhat);
        objOptions.ReplaceWith.Value("\\1.jcl;;;");
        objOptions.fileNameRegExp.Value(strReplaceWhat);
        objOptions.maxFiles.value(10);
        objOptions.verbose.value(-1);
        startTransfer();
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    public void testWrongUrl() {
        String strReplaceWhat = "^([^\\.]{8})\\.([0-9]{5})(\\.000)$";
        objOptions.operation.Value(enuJadeOperations.rename);
        objOptions.Source().url.Value("filse:///R:/backup/projects/anubex-dws/JCLs");
        objOptions.ReplaceWhat.Value(strReplaceWhat);
        objOptions.ReplaceWith.Value("\\1.jcl;;;");
        objOptions.fileNameRegExp.Value(strReplaceWhat);
        objOptions.maxFiles.value(10);
        objOptions.verbosityLevel.value(-1);
        objOptions.verbose.value(-1);
        startTransfer();
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    @Ignore("Test set to Ignore for later examination")
    public void testWrongUrl2() {
        String strReplaceWhat = "^([^\\.]{8})\\.([0-9]{5})(\\.000)$";
        objOptions.operation.Value(enuJadeOperations.rename);
        objOptions.Source().protocol.Value("filse");
        objOptions.Source().directory.Value("R:/backup/projects/anubex-dws/JCLs");
        objOptions.ReplaceWhat.Value(strReplaceWhat);
        objOptions.ReplaceWith.Value("\\1.jcl;;;");
        objOptions.fileNameRegExp.Value(strReplaceWhat);
        objOptions.maxFiles.value(10);
        objOptions.verbosityLevel.value(-1);
        objOptions.verbose.value(-1);
        startTransfer();
    }

    @Test
    public void testUrlFile2() {
        String strReplaceWhat = "^([^\\.]{8})\\.([0-9]{5})(\\.000)$";
        objOptions.operation.Value(enuJadeOperations.rename);
        objOptions.Source().protocol.Value("file");
        objOptions.sourceDir.Value("R:/backup/projects/anubex-dws/JCLs");
        objOptions.ReplaceWhat.Value(strReplaceWhat);
        objOptions.ReplaceWith.Value("\\1.jcl;;;");
        objOptions.fileNameRegExp.Value(strReplaceWhat);
        objOptions.maxFiles.value(10);
        objOptions.verbosityLevel.value(-1);
        objOptions.verbose.value(-1);
        startTransfer();
    }

    private void startTransfer() {
        JadeEngine objJadeEngine;
        try {
            objJadeEngine = new JadeEngine(objOptions);
            objJadeEngine.Execute();
            objJadeEngine.Logout();

        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    public void testEmptyCommandLineParameter() throws Exception {
        try {
            objOptions.AllowEmptyParameterList.setFalse();
            objOptions.commandLineArgs(new String[] {});
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    public void testEmptyCommandLineParameter2() {
        try {
            objOptions.AllowEmptyParameterList.setTrue();
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
        objConn.Source().hostName.Value(HOST_NAME_WILMA_SOS);
        objConn.Source().port.value(21);
        objConn.Source().protocol.Value(SOSOptionTransferType.enuTransferTypes.ftp);
        objConn.Source().user.Value("kb");
        objConn.Source().password.Value("kb");
        objConn.Target().hostName.Value(HOST_NAME_8OF9_SOS);
        objConn.Target().port.value(21);
        objConn.Target().protocol.Value(SOSOptionTransferType.enuTransferTypes.ftp);
        objConn.Target().user.Value("kb");
        objConn.Target().password.Value("kb");
        objOptions.filePath.Value(strTestFileName);
        objOptions.sourceDir.Value("/home/kb");
        objOptions.targetDir.Value("/kb");
        objOptions.operation.Value("copy");
        objOptions.checkMandatory();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.targetDir.Value() + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendServer2ServerWithJCraft() throws Exception {
        createTestFile();
        SOSConnection2Options objConn = objOptions.getConnectionOptions();
        objConn.Source().hostName.Value(HOST_NAME_WILMA_SOS);
        objConn.Source().port.value(SOSOptionPortNumber.getStandardSFTPPort());
        objConn.Source().protocol.Value(SOSOptionTransferType.enuTransferTypes.sftp);
        objConn.Source().user.Value("kb");
        objConn.Source().password.Value("kb");
        objConn.Source().sshAuthMethod.Value("password");
        objConn.Source().loadClassName.Value("com.sos.VirtualFileSystem.SFTP.SOSVfsSFtpJCraft");
        objConn.Target().hostName.Value(HOST_NAME_WILMA_SOS);
        objConn.Target().port.value(SOSOptionPortNumber.getStandardSFTPPort());
        objConn.Target().protocol.Value(SOSOptionTransferType.enuTransferTypes.sftp);
        objConn.Target().user.Value("sos");
        objConn.Target().password.Value("sos");
        objConn.Target().sshAuthMethod.Value("password");
        objConn.Target().loadClassName.Value("com.sos.VirtualFileSystem.SFTP.SOSVfsSFtpJCraft");
        objOptions.filePath.Value(strTestFileName);
        objOptions.sourceDir.Value("/home/kb");
        objOptions.targetDir.Value("/home/sos");
        objOptions.operation.Value("copy");
        objOptions.checkMandatory();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.targetDir.Value() + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendServer2ServerMultiple() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendServer2Server";
        createTestFile();
        logMethodName(conMethodName);
        SOSConnection2Options objConn = objOptions.getConnectionOptions();
        objConn.Source().hostName.Value(HOST_NAME_WILMA_SOS);
        objConn.Source().port.value(21);
        objConn.Source().protocol.Value(SOSOptionTransferType.enuTransferTypes.ftp);
        objConn.Source().user.Value("kb");
        objConn.Source().password.Value("kb");
        objConn.Target().hostName.Value(HOST_NAME_8OF9_SOS);
        objConn.Target().port.value(21);
        objConn.Target().protocol.Value(SOSOptionTransferType.enuTransferTypes.ftp);
        objConn.Target().user.Value("kb");
        objConn.Target().password.Value("kb");
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.sourceDir.Value("/home/kb");
        objOptions.targetDir.Value("/kb");
        objOptions.operation.Value("copy");
        objOptions.checkMandatory();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.targetDir.Value() + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendFtp2SFtp() throws Exception {
        createTestFile();
        SOSConnection2Options objConn = objOptions.getConnectionOptions();
        SOSConnection2OptionsAlternate objS = objConn.Source();
        objS.hostName.Value(HOST_NAME_8OF9_SOS);
        objS.port.value(SOSOptionPortNumber.getStandardFTPPort());
        objS.protocol.Value("ftp");
        objS.user.Value("sos");
        objS.password.Value("sos");
        objOptions.localDir.Value("/");
        SOSConnection2OptionsAlternate objT = objConn.Target();
        objT.hostName.Value(HOST_NAME_WILMA_SOS);
        objT.port.value(SOSOptionPortNumber.getStandardSFTPPort());
        objT.sshAuthMethod.isPassword(true);
        objT.protocol.Value("sftp");
        objT.user.Value("test");
        objT.password.Value("12345");
        String strTestDir = "/home/test/";
        objOptions.remoteDir.Value(strTestDir);
        objOptions.targetDir.Value(strTestDir);
        strTestFileName = "wilma.sh";
        objOptions.filePath.Value(strTestFileName);
        objOptions.operation.Value("copy");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(strTestDir + strTestFileName).FileExists();
        assertTrue("File must exist " + strTestFileName, flgResult);
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSend() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSend";
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.protocol.Value(enuTransferTypes.sftp);
        assertEquals("sftp", "sftp", objOptions.protocol.Value());
        objOptions.port.value(SOSOptionPortNumber.conPort4SSH);
        objOptions.user.Value("test");
        objOptions.password.Value("12345");
        objOptions.authMethod.Value(enuAuthenticationMethods.password);
        objOptions.filePath.Value(strTestFileName);
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.operation.Value("send");
        objOptions.log_filename.Value("c:/temp/test.log");
        objOptions.profile.Value(conMethodName);
        setOptions4BackgroundService();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/" + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendWithPrePostCommands() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendWithPrePostCommands";
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.protocol.Value(enuTransferTypes.sftp);
        assertEquals("sftp", "sftp", objOptions.protocol.Value());
        objOptions.port.value(SOSOptionPortNumber.conPort4SSH);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.authMethod.Value(enuAuthenticationMethods.password);
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.operation.Value("send");
        objOptions.log_filename.Value("c:/temp/test.log");
        objOptions.profile.Value(conMethodName);
        objOptions.preFtpCommands.Value("rm -f t.1");
        objOptions.Target().postCommand.Value("echo 'File: $TargetFileName' >> t.1;cat $TargetFileName >> t.1;rm -f $TargetFileName");
        objOptions.Target().preCommand.Value("touch $TargetFileName");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/" + strTestFileName).FileExists();
        assertFalse("File must not exist", flgResult);
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendWithPrePostCommands2() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendWithPrePostCommands";
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value("local");
        objOptions.protocol.Value(enuTransferTypes.local);
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.remoteDir.Value("c:/temp/a");
        objOptions.operation.Value("send");
        objOptions.log_filename.Value("c:/temp/test.log");
        objOptions.profile.Value(conMethodName);
        objOptions.preFtpCommands.Value("del %{remote_dir}/t.1");
        objOptions.Target().postCommand.Value("echo 'File: $TargetFileName' >> c:\\temp\\a\\t.1 & type $TargetFileName >>"
                + " c:\\temp\\a\\t.1 & del $TargetFileName");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/" + strTestFileName).FileExists();
        assertFalse("File must not exist", flgResult);
        objJadeEngine.Logout();
        LOGGER.debug(objOptions.getOptionsAsCommandLine());
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendRegExpAsFileName() throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        strTestFileName = "test.txt";
        objOptions.fileSpec.Value(strTestFileName);
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.operation.Value("send");
        objOptions.verbose.value(9);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/" + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
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
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.remoteDir.Value() + "/" + strRenamedTestfileName).FileExists();
        boolean flgResult2 = objJadeEngine.getSourceClient().getFileHandle(objOptions.localDir.Value() + "/" + strTestFileName).FileExists();
        boolean flgResult3 = objJadeEngine.getSourceClient().getFileHandle(objOptions.localDir.Value() + "/" + strRenamedTestfileName).FileExists();
        assertTrue("Files must exist", flgResult && flgResult2 && !flgResult3);
        objJadeEngine.Logout();
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
        objOptions.setAllOptions(objOptions.DeletePrefix((HashMap<String, String>) objHsh, "ftp_"));
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        assertEquals("replacing", "^renamed_", objOptions.replacing.Value());
        assertEquals("replacement", "", objOptions.replacement.Value());
        objJadeEngine.Execute();
        objJadeEngine.Logout();
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
        objOptions.setAllOptions(objOptions.DeletePrefix((HashMap<String, String>) objHsh, "ftp_"));
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        assertEquals("replacing", ".*", objOptions.replacing.Value());
        assertEquals("replacement", "oh/[filename:]", objOptions.replacement.Value());
        objJadeEngine.Execute();
        objJadeEngine.Logout();
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
        objOptions.setAllOptions(objOptions.DeletePrefix((HashMap<String, String>) objHsh, "ftp_"));
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        assertEquals("replacing", ".*", objOptions.replacing.Value());
        assertEquals("replacement", "oh/[filename:]", objOptions.replacement.Value());
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    private void setParams(final String replacing, final String replacement) throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.remoteDir.Value(KB_HOME);
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.filePath.Value(strTestFileName);
        objOptions.operation.Value("send");
        objOptions.replacement.Value(replacement);
        objOptions.replacing.Value(replacing);
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
        objOptions.setAllOptions(objOptions.DeletePrefix((HashMap<String, String>) objHsh, "ftp_"));
    }

    public void sendUsingReplacement(final String replacing, final String replacement) throws Exception {
        setParams(replacing, replacement);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        SOSOptionRegExp objRE = new SOSOptionRegExp(null, "test", "TestOption", replacing, "", false);
        String expectedRemoteFile = KB_HOME + objRE.doReplace(strTestFileName, replacement);
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(expectedRemoteFile).FileExists();
        objJadeEngine.Logout();
        assertTrue(String.format("File '%1$s' does not exist", expectedRemoteFile), flgResult);
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendUsingRelativeLocalDir() throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.remoteDir.Value("./relative");
        objOptions.filePath.Value(strTestFileName);
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.operation.Value("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/relative/" + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendUsingFilePathAndLocalDir() throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.remoteDir.Value("./relative");
        objOptions.filePath.Value(TEST_PATH_NAME + strTestFileName);
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.operation.Value("send");
        LOGGER.info(objOptions.dirtyString());
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/relative/" + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendUsingFilePathAndLocalDir2() throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.remoteDir.Value("./relative");
        objOptions.filePath.Value(TEST_PATH_NAME + strTestFileName);
        objOptions.localDir.Value(TEST_PATH_NAME + "Test/");
        objOptions.operation.Value("send");
        LOGGER.info(objOptions.dirtyString());
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/relative/" + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendUsingFilePathAndLocalDir3() throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.remoteDir.Value("./relative");
        objOptions.filePath.Value(strTestFileName);
        objOptions.localDir.Value(TEST_PATH_NAME);
        runFilePathTest();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendUsingFilePathAndLocalDir4() throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.remoteDir.Value("./relative");
        objOptions.filePath.Value(TEST_PATH_NAME + strTestFileName);
        runFilePathTest();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendUsingFilePathAndLocalDir5() throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.remoteDir.Value("./relative");
        objOptions.filePath.Value("SOSDataExchange/" + strTestFileName);
        objOptions.localDir.Value("R:/backup/sos/java/junittests/testdata/");
        runFilePathTest();
    }

    private void runFilePathTest() throws Exception {
        objOptions.operation.Value("send");
        LOGGER.info(objOptions.dirtyString());
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/relative/" + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSend5() throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value(strTestFileName);
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.remoteDir.Value(KB_HOME);
        objOptions.operation.Value("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(KB_HOME + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendComand() throws Exception {
        objOptions = new JADEOptions();
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value(strTestFileName);
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.remoteDir.Value(KB_HOME);
        objOptions.operation.Value("send");
        setOptions4BackgroundService();
        objOptions.sendTransferHistory.value(false);
        objOptions.Target().postCommand.Value("SITE CHMOD 777 $TargetFileName");
        objOptions.Source().preCommand.Value("dir $SourceFileName");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    private void setOptions4BackgroundService() {
        objOptions.schedulerHost.Value(HOST_NAME_8OF9_SOS);
        objOptions.schedulerPort.Value("4210");
        objOptions.schedulerTransferMethod.Value(enuJSTransferModes.tcp.description);
        objOptions.sendTransferHistory.value(true);
    }

    public void testFerberSFtp() throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value("85.214.92.170");
        objOptions.port.Value("22");
        objOptions.protocol.Value(enuTransferTypes.sftp);
        objOptions.alternativeHost.Value("85.214.92.170");
        objOptions.alternativePort.Value("22");
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value(TEST_PATH_NAME + strTestFileName);
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.operation.Value("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendToAlternateHost() throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value("xwilma.sos");
        objOptions.alternativeHost.Value(HOST_NAME_WILMA_SOS);
        objOptions.alternativePort.Value("21");
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value(TEST_PATH_NAME + strTestFileName);
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.operation.Value("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/" + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendToAlternateUser() throws Exception {
        createTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value("wilma.sos");
        objOptions.alternativeUser.Value("test");
        objOptions.getConnectionOptions().Target().Alternatives().user.Value("test");
        objOptions.getConnectionOptions().Target().Alternatives().password.Value("12345");
        objOptions.alternativePort.Value("21");
        objOptions.getConnectionOptions().Alternatives().user.Value();
        objOptions.getConnectionOptions().Source().Alternatives().user.Value();
        objOptions.user.Value("kb");
        objOptions.password.Value("kbkbkb");
        objOptions.filePath.Value(TEST_PATH_NAME + strTestFileName);
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.operation.Value("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/" + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSend2() throws Exception {
        createTestFile();
        objOptions.host.Value(HOST_NAME_8OF9_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value(TEST_PATH_NAME + strTestFileName);
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.remoteDir.Value("/kb/");
        objOptions.operation.Value("send");
        objOptions.passiveMode.value(true);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/kb/" + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSend2file_spec() throws Exception {
        String strSaveTestfileName = strTestFileName;
        strTestFileName = "3519078034.pdf";
        createTestFile();
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.protocol.Value(enuTransferTypes.sftp);
        objOptions.sshAuthMethod.Value(enuAuthenticationMethods.password);
        objOptions.fileSpec.Value("^[0-9]{10}\\.pdf$");
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.remoteDir.Value("/home/kb/");
        objOptions.transactional.setTrue();
        objOptions.atomicSuffix.Value(".tmp");
        objOptions.postCommand.Value("chmod 777 $TargetTransferFileName");
        objOptions.operation.Value("send");
        objOptions.passiveMode.value(true);
        objOptions.log_filename.Value("c:/temp/test.log");
        setMailOptions();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/" + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
        strTestFileName = strSaveTestfileName;
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendWithRename() throws Exception {
        createTestFile();
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("test");
        objOptions.password.Value("12345");
        objOptions.protocol.Value(enuTransferTypes.sftp);
        objOptions.sshAuthMethod.Value(enuAuthenticationMethods.password);
        objOptions.fileSpec.Value("^" + strTestFileName + "$");
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.remoteDir.Value("/home/test/temp/test/");
        objOptions.replacement.Value(".*");
        objOptions.replacing.Value("renamed_[filename:]");
        objOptions.operation.Value("send");
        objOptions.log_filename.Value("c:/temp/test.log");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.remoteDir.Value() + "/renamed_" + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    private void setMailOptions() {
        objOptions.getMailOptions().to.Value("jade_test@sos-berlin.com");
        objOptions.getMailOptions().SMTPHost.Value("smtp.sos");
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testReceive() throws Exception {
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value(strTestFileName);
        objOptions.remoteDir.Value("/home/kb/");
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.operation.Value("receive");
        objOptions.transferMode.Value("ascii");
        setOptions4BackgroundService();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testReceiveWithSymlinkInRemoteDir() throws Exception {
        JSFile objFile = new JSFile(TEST_PATH_NAME, strTestFileName);
        if (objFile.exists()) {
            objFile.delete();
        }
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("test");
        objOptions.password.Value("12345");
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.remoteDir.Value("/tmp/test/symlink2home.test.temp/test");
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.operation.Value("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.localDir.Value() + "/" + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testReceiveSFTP() throws Exception {
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.protocol.Value(enuTransferTypes.sftp.Text());
        objOptions.port.Value("22");
        objOptions.password.Value("kb");
        objOptions.filePath.Value("sosdex.txt");
        objOptions.remoteDir.Value("/home/sos/tmp");
        objOptions.sshAuthMethod.Value(enuAuthenticationMethods.password);
        objOptions.bufferSize.value(1024);
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.operation.Value("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testReceiveWithUmlaut() throws Exception {
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value("Büttner.dat");
        objOptions.remoteDir.Value("/home/kb/");
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.operation.Value("receive");
        objOptions.controlEncoding.Value("UTF-8");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testReceiveWithUmlautFromLocalhost() throws Exception {
        objOptions.host.Value("localhost");
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.fileSpec.Value(".*ttner\\..*");
        objOptions.remoteDir.Value("/");
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.operation.Value("receive");
        objOptions.preFtpCommands.Value("OPTS UTF8 ON");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testCopy() throws Exception {
        new File(TEST_PATH_NAME + strTestFileName).delete();
        LOGGER.setLevel(Level.DEBUG);
        SOSConnection2OptionsAlternate objS = objOptions.getConnectionOptions().Source();
        objS.protocolCommandListener.setTrue();
        objOptions.verbose.value(9);
        objS.host.Value(HOST_NAME_WILMA_SOS);
        objS.protocol.Value("ftp");
        objS.user.Value("kb");
        objS.password.Value("kb");
        objOptions.filePath.Value(strTestFileName);
        objOptions.sourceDir.Value("/home/kb/");
        SOSConnection2OptionsAlternate objT = objOptions.getConnectionOptions().Target();
        objT.protocol.Value("local");
        objOptions.targetDir.Value(TEST_PATH_NAME);
        objOptions.remoteDir.Value(TEST_PATH_NAME);
        objOptions.forceFiles.setFalse();
        objOptions.operation.Value(SOSOptionJadeOperation.enuJadeOperations.copy);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        LOGGER.info(objOptions.DirtyString());
        objJadeEngine.Execute();
        objJadeEngine.Logout();
        assertTrue(new File(TEST_PATH_NAME + strTestFileName).exists());
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testReceive2() throws Exception {
        objOptions.host.Value(HOST_NAME_8OF9_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value(strTestFileName);
        objOptions.remoteDir.Value("/kb/");
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.operation.Value("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendMultipleFiles() throws Exception {
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value("");
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.operation.Value("send");
        objOptions.verbose.value(9);
        setOptions4BackgroundService();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testReceiveMultipleFiles() throws Exception {
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value("");
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.remoteDir.Value("/home/kb/");
        objOptions.appendFiles.value(false);
        objOptions.operation.Value("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        long intNoOfFilesTransferred = objJadeEngine.getFileList().SuccessfulTransfers();
        for (SOSFileListEntry objListItem : objJadeEngine.getFileList().List()) {
            String strF = makeFullPathName(objOptions.targetDir.Value(), objListItem.TargetFileName());
            boolean flgResult = objListItem.getDataTargetClient().getFileHandle(strF).FileExists();
            assertTrue("File " + strF + " exist, but should not", flgResult);
        }
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testResultSet() throws Exception {
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value("");
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.remoteDir.Value("/home/kb/");
        objOptions.appendFiles.value(false);
        objOptions.operation.Value("receive");
        objOptions.createResultSet.value(true);
        String strResultSetFileName = objOptions.TempDir() + "/ResultSetFile.dat";
        objOptions.resultSetFileName.Value(strResultSetFileName);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendAndDeleteMultipleFiles() throws Exception {
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value("");
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.operation.Value("send");
        objOptions.deleteFilesAfterTransfer.value(true);
        objOptions.log_filename.Value("c:/temp/test.log");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        for (SOSFileListEntry objListItem : objJadeEngine.getFileList().List()) {
            String strF = objListItem.SourceFileName();
            boolean flgResult = objListItem.getDataSourceClient().getFileHandle(strF).FileExists();
            assertFalse("File " + strF + " exist, but should not", flgResult);
        }
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testReceiveMultipleFiles2() throws Exception {
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value("");
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.remoteDir.Value("/home/kb/");
        objOptions.appendFiles.value(false);
        objOptions.operation.Value("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        for (SOSFileListEntry objListItem : objJadeEngine.getFileList().List()) {
            String strF = makeFullPathName(objOptions.targetDir.Value(), objListItem.TargetFileName());
            boolean flgResult = objListItem.getDataTargetClient().getFileHandle(strF).FileExists();
            assertTrue("File " + strF + " exist, but should not", flgResult);
        }
        objJadeEngine.Logout();
    }

    public void renameLocalFiles(final String source_dir, final String file_spec) throws Exception {
        SOSConnection2Options objConn = objOptions.getConnectionOptions();
        SOSConnection2OptionsAlternate objS = objConn.Source();
        SOSConnection2OptionsAlternate objT = objConn.Target();
        objS.hostName.Value(HOST_NAME_WILMA_SOS);
        objS.port.value(SOSOptionPortNumber.getStandardSFTPPort());
        objS.protocol.Value("sftp");
        objS.user.Value("test");
        objS.sshAuthMethod.isPassword(true);
        objS.password.Value("12345");
        objT.hostName.Value(HOST_NAME_WILMA_SOS);
        objT.port.value(SOSOptionPortNumber.getStandardSFTPPort());
        objT.protocol.Value("sftp");
        objT.user.Value("test");
        objT.sshAuthMethod.isPassword(true);
        objT.password.Value("12345");
        objOptions.sourceDir.Value(source_dir);
        objOptions.targetDir.Value(source_dir);
        objOptions.filePath.Value("");
        objOptions.operation.Value("rename");
        objOptions.fileSpec.Value(file_spec);
        objOptions.replacing.Value(".*");
        objOptions.replacement.Value("moved/[filename:]");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void renameFiles(final String source_dir, final String file_spec) throws Exception {
        SOSConnection2Options objConn = objOptions.getConnectionOptions();
        SOSConnection2OptionsAlternate objS = objConn.Source();
        SOSConnection2OptionsAlternate objT = objConn.Target();
        objS.hostName.Value(HOST_NAME_WILMA_SOS);
        objS.port.value(SOSOptionPortNumber.getStandardSFTPPort());
        objS.protocol.Value("sftp");
        objS.user.Value("test");
        objS.sshAuthMethod.isPassword(true);
        objS.password.Value("12345");
        objT.hostName.Value(HOST_NAME_WILMA_SOS);
        objT.port.value(SOSOptionPortNumber.getStandardSFTPPort());
        objT.protocol.Value("sftp");
        objT.user.Value("test");
        objT.sshAuthMethod.isPassword(true);
        objT.password.Value("12345");
        objOptions.sourceDir.Value(source_dir);
        objOptions.targetDir.Value(source_dir);
        objOptions.filePath.Value("");
        objOptions.operation.Value("rename");
        objOptions.fileSpec.Value(file_spec);
        objOptions.replacing.Value(".*");
        objOptions.replacement.Value("moved/[filename:]");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testRenameFiles() throws Exception {
        String strTestDir = "/home/test/temp/test/sosdex";
        renameFiles(strTestDir, "^\\d\\.txt$");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testRenameFiles2() throws Exception {
        String strTestDir = "/home/test/temp/test/sosdex";
        renameFiles(strTestDir, "^scheduler\\.dll$");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getSourceClient().getFileHandle(strTestDir + "/moved/scheduler.dll").FileExists();
        assertTrue("File must exist " + strTestDir + "/moved/scheduler.dll", flgResult);
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendMultipleFilesLocal2Local() throws Exception {
        CreateTestFiles(10);
        objOptions.Source().protocol.Value(enuTransferTypes.local);
        objOptions.Target().protocol.Value(enuTransferTypes.local);
        objOptions.sourceDir.Value(TEST_PATH_NAME);
        objOptions.targetDir.Value(TEST_PATH_NAME + "/SOSMDX/");
        objOptions.filePath.Value("");
        objOptions.fileNamePatternRegExp.Value("^.*\\.txt$");
        objOptions.operation.Value("copy");
        objOptions.Target().postCommand.Value("echo $TargetFileName");
        objOptions.removeFiles.value(true);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
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
        objOptions.Source().protocol.Value(enuTransferTypes.local);
        objOptions.Target().protocol.Value(enuTransferTypes.local);
        objOptions.recursive.setTrue();
        objOptions.fileSpec.Value("^.*$");
        objOptions.maxFiles.value(15);
        objOptions.sourceDir.Value("R:/backup/sos/java/doxygen-docs");
        objOptions.targetDir.Value("R:/backup/www.sos-berlin.com/doc/doxygen-docs");
        objOptions.operation.Value(enuJadeOperations.copy);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    @Test
    public void testDeleteZipFile() throws Exception {
        final String conMethodName = CLASS_NAME + "::testDeleteZipFile";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFile, "-profile=zip_local_files" };
        objOptions.commandLineArgs(strCmdLineParameters);
        File fleFile = new File(objOptions.remoteDir.Value());
        fleFile.delete();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testParameterPriority() throws Exception {
        final String conMethodName = CLASS_NAME + "::testParameterPriority";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFile, "-profile=zip_local_files_2", "-operation=receive" };
        objOptions.commandLineArgs(strCmdLineParameters);
        String strOperation = objOptions.operation.Value();
        assertEquals("Operation not overwritten", "receive", strOperation);
        assertEquals("source protocol", "local", objOptions.getConnectionOptions().Source().protocol.Value());
        assertEquals("source dir", "J:\\E\\java\\junittests\\testdata\\SOSDataExchange/", objOptions.sourceDir.Value());
        assertEquals("Operation not overwritten", "receive", strOperation);
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testParameterPriority2() throws Exception {
        final String conMethodName = CLASS_NAME + "::testParameterPriority";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFile, "-profile=zip_local_files", "-operation=getFileList" };
        objOptions.commandLineArgs(strCmdLineParameters);
        String strOperation = objOptions.operation.Value();
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
        objJadeEngine.Execute();
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
        objJadeEngine.Execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendMultipleZIPedFilesLocal2Local() throws Exception {
        objOptions.protocol.Value("local");
        objOptions.filePath.Value("");
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.remoteDir.Value(TEST_PATH_NAME + "/SOSMDX/");
        objOptions.operation.Value("send");
        objOptions.compressFiles.value(true);
        objOptions.compressedFileExtension.Value(".zip");
        objOptions.concurrentTransfer.value(true);
        objOptions.maxConcurrentTransfers.value(5);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendMultipleFilesLocal2LocalAtomic() throws Exception {
        objOptions.protocol.Value("local");
        objOptions.filePath.Value("");
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.remoteDir.Value(TEST_PATH_NAME + "/SOSMDX/");
        objOptions.operation.Value("send");
        objOptions.atomicSuffix.Value("~");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendMultipleFilesAtomicAndTransactional() throws Exception {
        objOptions.protocol.Value("local");
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.remoteDir.Value(TEST_PATH_NAME + "/SOSMDX/");
        objOptions.operation.Value("send");
        objOptions.atomicSuffix.Value(".xfer");
        objOptions.transactional.value(true);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testRenameMultipleFilesLocal() throws Exception {
        objOptions.protocol.Value("local");
        objOptions.getConnectionOptions().Source().protocol.Value("local");
        objOptions.getConnectionOptions().Target().protocol.Value("local");
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(TEST_PATH_NAME + "/SOSMDX/");
        objOptions.remoteDir.Value(TEST_PATH_NAME + "/SOSMDX/");
        objOptions.operation.Value("rename");
        objOptions.replacing.Value("(.*)(.txt)");
        objOptions.replacement.Value("\\1_[date:yyyyMMddHHmm];\\2");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testDeleteMultipleFilesLocal() throws Exception {
        objOptions.protocol.Value("local");
        objOptions.getConnectionOptions().Source().protocol.Value("local");
        objOptions.getConnectionOptions().Target().protocol.Value("local");
        objOptions.filePath.Value("");
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(TEST_PATH_NAME + "/SOSMDX/");
        objOptions.remoteDir.Value(TEST_PATH_NAME + "/SOSMDX/");
        objOptions.operation.Value("delete");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendHugeNumberOfFiles() throws Exception {
        CreateTestFiles(50);
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value("");
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(TEST_PATH_NAME);
        objOptions.operation.Value("send");
        objOptions.passiveMode.setTrue();
        objOptions.concurrentTransfer.value(true);
        objOptions.maxConcurrentTransfers.value(4);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testIniFile1() throws Exception {
        final String conMethodName = CLASS_NAME + "::CreateIniFile";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.Value(constrSettingsTestFile);
        objOptions.profile.Value("globals");
        objOptions.ReadSettingsFile();
        assertEquals("User ID", "kb", objOptions.user.Value());
        assertEquals("password", "kb", objOptions.password.Value());
    }

    @Test
    public void testCopy_Local2SFTP_recursive() throws Exception {
        final String conMethodName = CLASS_NAME + "::CreateIniFile";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        objOptions.settings.Value(strSettingsFile);
        objOptions.profile.Value("Copy_Local2SFTP_recursive");
        objOptions.ReadSettingsFile();
        assertEquals("User ID", "test", objOptions.Target().user.Value());
        assertEquals("password", "12345", objOptions.Target().password.Value());
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testJadeConfig() throws Exception {
        final String conMethodName = CLASS_NAME + "::testJadeConfig";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        objOptions.settings.Value("examples/ConfigurationExample.jadeconf");
        objOptions.profile.Value("copylocal2local1");
        objOptions.ReadSettingsFile();
        LOGGER.debug(objOptions.DirtyString());
        assertEquals("operation ", "copy", objOptions.operation.Value());
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    @Ignore("Test set to Ignore for later examination")
    public void testIniFile2() throws Exception {
        final String conMethodName = CLASS_NAME + "::testIniFile2";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.Value(constrSettingsTestFile);
        objOptions.profile.Value("include-TestTest");
        objOptions.ReadSettingsFile();
        assertEquals("User ID", "kb", objOptions.user.Value());
        assertEquals("password", "kb", objOptions.password.Value());
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testIniFile3() throws Exception {
        final String conMethodName = CLASS_NAME + "::testIniFile2";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.Value(constrSettingsTestFile);
        objOptions.profile.Value("include-Test");
        objOptions.ReadSettingsFile();
        objOptions.localDir.Value(".");
        assertEquals("User ID", "kb", objOptions.user.Value());
        assertEquals("password", "kb", objOptions.password.Value());
        assertEquals("Hostname", "localhost", objOptions.host.Value());
        assertEquals("port", 88, objOptions.port.value());
        assertEquals("protocol", "scp", objOptions.protocol.Value());
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    @Ignore("Test set to Ignore for later examination")
    public void testIniFile4() throws Exception {
        final String conMethodName = CLASS_NAME + "::testIniFile2";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.Value(constrSettingsTestFile);
        objOptions.profile.Value("include-TestWithNonexistenceInclude");
        objOptions.ReadSettingsFile();
        assertEquals("User ID", "kb", objOptions.user.Value());
        assertEquals("password", "kb", objOptions.password.Value());
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testIniFile5() throws Exception {
        final String conMethodName = CLASS_NAME + "::testIniFile5";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.Value(constrSettingsTestFile);
        objOptions.profile.Value("substitute-Test");
        objOptions.ReadSettingsFile();
        LOGGER.info(objOptions.DirtyString());
        String strComputerName = System.getenv("computername");
        assertEquals("User ID", System.getenv("username"), objOptions.user.Value());
        assertEquals("Hostname", strComputerName, objOptions.host.Value());
        assertEquals("Hostnameon Target ", strComputerName + "-abc", objOptions.getConnectionOptions().Target().hostName.Value());
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testIniFileWithSourceAndTarget() throws Exception {
        final String conMethodName = CLASS_NAME + "::testIniFile5";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.Value(strSettingsFile);
        objOptions.profile.Value("ftp_server_2_server");
        objOptions.ReadSettingsFile();
        SOSConnection2Options objConn = objOptions.getConnectionOptions();
        assertEquals("Source.Host", HOST_NAME_WILMA_SOS, objConn.Source().host.Value());
        assertEquals("Target.Host", HOST_NAME_8OF9_SOS, objConn.Target().host.Value());
        assertEquals("file_path", "test.txt", objOptions.filePath.Value());
        objOptions.checkMandatory();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void BRANDUP_MOND_CRM_POC() throws Exception {
        final String conMethodName = CLASS_NAME + "::BRANDUP_MOND_CRM_POC";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        objOptions.settings.Value(strSettingsFile);
        objOptions.profile.Value("BRANDUP_MOND_CRM_POC");
        objOptions.ReadSettingsFile();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
    }

    @Test
    public void testAliasFromIniFile() {
        JSFile objIni = new JSFile(constrSettingsTestFile);
        try {
            objIni.WriteLine("[testAlias]");
            objIni.WriteLine("auth_method=password");
            objIni.WriteLine("verbose=9");
            objIni.close();
            objOptions = new JADEOptions();
            objOptions.settings.Value(constrSettingsTestFile);
            objOptions.profile.Value("testAlias");
            objOptions.ReadSettingsFile();
            assertEquals("Alias: auth_method", "password", objOptions.authMethod.Value());
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    private void CreateIniFile() throws Exception {
        JSFile objIni = new JSFile(constrSettingsTestFile);
        objIni.WriteLine("[globals]");
        objIni.WriteLine("globaluser=kb");
        objIni.WriteLine("globalpassword=kb");
        objIni.WriteLine("[include1]");
        objIni.WriteLine("host=localhost");
        objIni.WriteLine("[include2]");
        objIni.WriteLine("port=88");
        objIni.WriteLine("[include3]");
        objIni.WriteLine("protocol=scp");
        objIni.WriteLine("[include1_and_2]");
        objIni.WriteLine("include=include1,include2");
        objIni.WriteLine("[include-Test]");
        objIni.WriteLine("include=include1_and_2,include3");
        objIni.WriteLine("[include-TestWithNonexistenceInclude]");
        objIni.WriteLine("include=include1,includeabcd2,include3");
        objIni.WriteLine("[substitute-Test]");
        objIni.WriteLine("user=${USERNAME}");
        objIni.WriteLine("host=${COMPUTERNAME}");
        objIni.WriteLine("cannotsubstitutet=${waltraut}");
        objIni.WriteLine("title=${globaluser} and ${globalpassword}");
        objIni.WriteLine("target_host=${host}-abc");
        objIni.WriteLine("alternate_target_host=${host}-abc");
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
        objOptions.settings.Value(strSettingsFile);
        objOptions.profile.Value(pstrProfileName);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
    }

    @Test
    public void testAliasSettings() throws Exception {
        Map<String, String> objHsh = new HashMap<String, String>();
        String strPassword = SOSOptionAuthenticationMethod.enuAuthenticationMethods.password.Text();
        objHsh.put("ssh_auth_method", strPassword);
        objOptions = new JADEOptions();
        objOptions.setAllOptions((HashMap<String, String>) objHsh);
        assertEquals("Default: ssh_auth_method", SOSOptionAuthenticationMethod.enuAuthenticationMethods.publicKey.Text().toLowerCase(),
                objOptions.authMethod.DefaultValue());
        assertEquals("Alias: ssh_auth_method", strPassword, objOptions.authMethod.Value());
        objHsh.put("source_ssh_auth_method", strPassword);
        objOptions = new JADEOptions();
        objOptions.setAllOptions((HashMap<String, String>) objHsh);
        assertEquals("Alias: source_ssh_auth_method", strPassword, objOptions.Source().authMethod.Value());
        objHsh.put("target_ssh_auth_method", strPassword);
        objOptions = new JADEOptions();
        objOptions.setAllOptions((HashMap<String, String>) objHsh);
        assertEquals("Alias: target_ssh_auth_method", strPassword, objOptions.Target().authMethod.Value());
        objHsh.put("source_auth_method", strPassword);
        objOptions = new JADEOptions();
        objOptions.setAllOptions((HashMap<String, String>) objHsh);
        assertEquals("Alias: source_auth_method", strPassword, objOptions.Source().authMethod.Value());
    }

    @Test
    public void testAliasSettings2() throws Exception {
        Map<String, String> objHsh = new HashMap<String, String>();
        String strPassword = SOSOptionAuthenticationMethod.enuAuthenticationMethods.password.Text();
        objHsh.put("auth_method", strPassword);
        objOptions = new JADEOptions();
        objOptions.setAllOptions((HashMap<String, String>) objHsh);
        assertEquals("Alias: auth_method", strPassword, objOptions.authMethod.Value());
        objHsh.put("source_auth_method", strPassword);
        objOptions = new JADEOptions();
        objOptions.setAllOptions((HashMap<String, String>) objHsh);
        assertEquals("Alias: source_auth_method", strPassword, objOptions.Source().authMethod.Value());
        objHsh.put("target_auth_method", strPassword);
        objOptions = new JADEOptions();
        objOptions.setAllOptions((HashMap<String, String>) objHsh);
        assertEquals("Alias: target_auth_method", strPassword, objOptions.Target().authMethod.Value());
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
        objOptions.setAllOptions(objOptions.DeletePrefix((HashMap<String, String>) objHsh, "ftp_"));
        assertEquals("", HOST_NAME_WILMA_SOS, objOptions.getConnectionOptions().Source().host.Value());
        assertEquals("", "tux.sos", objOptions.getConnectionOptions().Target().host.Value());
        assertEquals("", "/srv/www/htdocs/test/", objOptions.targetDir.Value());
        assertEquals("log filename not set", "c:/temp/test.log", objOptions.log_filename.Value());
        assertEquals("log filename not set", "c:/temp/test.log", objOptions.OptionByName("log_filename"));
        String strReplTest = "Hallo, welt %{log_filename} und \nverbose = %{verbose} ersetzt. Date %{date} wird nicht ersetzt";
        String strR = objOptions.replaceVars(strReplTest);
        LOGGER.info(strR);
        strReplTest = "Hallo, welt %{log_filename} und" + "\n" + "verbose = %{verbose} ersetzt. Date %{date} wird nicht ersetzt";
        strR = objOptions.replaceVars(strReplTest);
        LOGGER.info(strR);
        assertEquals("log filename not set", "sos", objOptions.getConnectionOptions().Source().Alternatives().user.Value());
        assertEquals("log filename not set", "abcdef", objOptions.getConnectionOptions().Target().Alternatives().user.Value());
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
        objOptions.setAllOptions(objOptions.DeletePrefix((HashMap<String, String>) objHsh, "ftp_"));
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objOptions.checkMandatory();
        assertEquals("", "localhost", objOptions.getConnectionOptions().Source().host.Value());
        assertEquals("", "tux.sos", objOptions.getConnectionOptions().Target().host.Value());
        assertEquals("", "sftp", objOptions.getConnectionOptions().Target().protocol.Value());
        objJadeEngine.Execute();
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
        objOptions.protocol.Value(enuTransferTypes.ftp);
        objOptions.setAllOptions(objOptions.DeletePrefix((HashMap<String, String>) objHsh, "ftp_"));
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objOptions.checkMandatory();
        assertEquals("", "localhost", objOptions.getConnectionOptions().Source().host.Value());
        assertEquals("", "tux.sos", objOptions.getConnectionOptions().Target().host.Value());
        assertEquals("", "ftp", objOptions.getConnectionOptions().Target().protocol.Value());
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
        objOptions.setAllOptions(objOptions.DeletePrefix((HashMap<String, String>) objHsh, "ftp_"));
        assertEquals("", HOST_NAME_WILMA_SOS, objOptions.getConnectionOptions().Source().host.Value());
        assertEquals("", "tux.sos", objOptions.getConnectionOptions().Target().host.Value());
        assertEquals("", "/srv/www/htdocs/test/", objOptions.targetDir.Value());
        assertEquals("source", strCmd, objOptions.Source().preCommand.Value());
        assertEquals("target", strCmd, objOptions.Target().preCommand.Value());
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

}