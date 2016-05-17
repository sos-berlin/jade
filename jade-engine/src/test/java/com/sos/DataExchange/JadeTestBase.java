package com.sos.DataExchange;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Basics.JSToolBox;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
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
import com.sos.VirtualFileSystem.DataElements.SOSFileList;
import com.sos.VirtualFileSystem.DataElements.SOSFileListEntry;
import com.sos.VirtualFileSystem.Factory.VFSFactory;
import com.sos.VirtualFileSystem.Interfaces.ISOSVFSHandler;
import com.sos.VirtualFileSystem.Interfaces.ISOSVfsFileTransfer;
import com.sos.VirtualFileSystem.Options.SOSConnection2Options;
import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.sos.VirtualFileSystem.exceptions.JADEException;

// oh 18.04.14 No runnable methods [SP]
@Ignore("Test set to Ignore for later examination")
public abstract class JadeTestBase extends JSToolBox {

    protected static final String HOST_NAME_WILMA_SOS = "wilma.sos";
    protected static final String HOST_NAME_HOMER_SOS = "homer.sos";
    protected static final String HOST_NAME_8OF9_SOS = "8of9.sos";
    protected static final String USER_ID_TEST = "test";
    protected static final String PASSWORD_TEST = "12345";
    protected static final String TARGET_OF_DOXYGEN_DOCS = "R:/backup/www.sos-berlin.com/doc/doxygen-docs/";
    protected static final String SOURCE_OF_DOXYGEN_DOCS = "R:/backup/sos/java/doxygen-docs/";
    protected JadeEngine objJadeEngine = null;
    protected static final String CLASS_NAME = "JadeTestBase";
    protected JADEOptions objOptions = null;
    protected JADEOptions objTestOptions = null;
    protected String strSettingsFileName = "./scripts/sosdex_settings.ini";
    protected ISOSVFSHandler objVFS = null;
    protected ISOSVfsFileTransfer ftpClient = null;
    protected String strTestFileName = "text.txt";
    protected String strTestPathName = "R:/nobackup/junittests/testdata/JADE/";
    protected String strKBHome = "/home/kb/";
    protected enuTransferTypes enuSourceTransferType = enuTransferTypes.local;
    protected enuTransferTypes enuTargetTransferType = enuTransferTypes.local;
    protected String strSettingsFile = "R:/backup/sos/java/development/SOSDataExchange/examples/jade_settings.ini";
    protected String dynamicClassNameSource = null;
    protected String gstrFilePath = "";
    private static final Logger LOGGER = Logger.getLogger(JadeTestBase.class);
    private String constrSettingsTestFile = strTestPathName + "/SOSDEx-test.ini";
    private boolean flgUseFilePath = false;

    class WriteFile4Polling implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                LOGGER.error("", e);
            }
            JSFile objFile = null;
            objFile = new JSFile(strTestPathName + "/test-steady.poll");
            for (int i = 0; i < 1024 * 50; i++) {
                try {
                    String str = "";
                    for (int j = 0; j < 24; j++) {
                        str = str + "a";
                    }
                    objFile.WriteLine(str);
                } catch (IOException e) {
                    LOGGER.error("", e);
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
            try {
                objFile.close();
                objFile = null;
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }

    class WriteFileSlowly4PollingAndSteadyState implements Runnable {

        @Override
        public void run() {
            JSFile objFile = null;
            objFile = new JSFile(strTestPathName + "/test-unsteady.poll");
            if (objFile.exists()) {
                objFile.delete();
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                LOGGER.error("", e);
            }
            for (int i = 0; i < 1024 * 50; i++) {
                try {
                    Thread.sleep(250);
                    String str = "";
                    for (int j = 0; j < 24; j++) {
                        str = str + "a";
                    }
                    objFile.WriteLine(str);
                } catch (IOException e) {
                    LOGGER.error("", e);
                } catch (InterruptedException e) {
                    LOGGER.error("", e);
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
            try {
                objFile.close();
                objFile = null;
            } catch (Exception e) {
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
                String strT = objTestOptions.sourceDir.Value();
                new File(strT).mkdirs();
                objFile = new JSFile(objTestOptions.sourceDir.Value() + "/test-" + i + ".poll");
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

    private void checkFilesOnTarget(final SOSFileList objFileList) throws Exception {
        for (SOSFileListEntry objEntry : objFileList.List()) {
            String strName = objEntry.TargetFileName();
            boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.targetDir.Value() + strName).FileExists();
            assertTrue(String.format("File '%1$s' must exist", strName), flgResult);
        }
    }

    protected void CreateBigTestFile(final String pstrFileName, final int fileSize) {
        JSFile objFile = new JSFile(strTestPathName, pstrFileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(objFile);
            out.write(new byte[fileSize]);
        } catch (IOException e) {
            LOGGER.error("", e);
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                    createRemoteTestFiles(pstrFileName);
                } catch (Exception x) {
                }
            }
        }
    }

    protected void CreateIniFile() throws Exception {
        JSFile objIni = new JSFile(constrSettingsTestFile);
        objIni.WriteLine("[globals]");
        objIni.WriteLine("user=kb");
        objIni.WriteLine("password=kb");
        objIni.WriteLine("[include1]");
        objIni.WriteLine("host=hostFromInclude1");
        objIni.WriteLine("[include2]");
        objIni.WriteLine("port=88");
        objIni.WriteLine("[include3]");
        objIni.WriteLine("protocol=scp");
        objIni.WriteLine("[include-Test]");
        objIni.WriteLine("include=include1,include2,include3");
        objIni.WriteLine("[include-TestWithNonexistenceInclude]");
        objIni.WriteLine("include=include1,includeabcd2,include3");
        objIni.WriteLine("[substitute-Test]");
        objIni.WriteLine("user=${USERNAME}");
        objIni.WriteLine("host=${COMPUTERNAME}");
        objIni.WriteLine("cannotsubstitutet=${waltraut}");
        objIni.WriteLine("target_host=${host}-abc");
        objIni.WriteLine("alternate_target_host=${host}-abc");
    }

    private void createRemoteTestFiles(final String strFilePath) {
        if (objOptions.Source().protocol.Value().equalsIgnoreCase(enuTransferTypes.local.Text()) == false) {
            JADEOptions objO = new JADEOptions();
            objO.Source().protocol.Value(enuTransferTypes.local);
            objO.Target().protocol.Value(enuTargetTransferType);
            objO.Source().hostName.Value("localhost");
            objO.Source().protocol.Value("local");
            objO.Source().user.Value(USER_ID_TEST);
            objO.Source().password.Value(PASSWORD_TEST);
            objO.sourceDir.Value(strTestPathName);
            objO.targetDir.Value(objTestOptions.sourceDir.Value());
            objO.Target().user.Value(objTestOptions.Source().user.Value());
            objO.Target().password.Value(objTestOptions.Source().password.Value());
            objO.Target().protocol.Value(objTestOptions.Source().protocol.Value());
            objO.Target().port.Value(objTestOptions.Source().port.Value());
            objO.Target().hostName.Value(objTestOptions.Source().hostName.Value());
            objO.Target().authMethod.Value(objTestOptions.Source().authMethod.Value());
            objO.operation.Value(SOSOptionJadeOperation.enuJadeOperations.copy);
            objO.filePath.Value(strFilePath);
            objO.overwriteFiles.setTrue();
            try {
                objJadeEngine = new JadeEngine(objO);
                objJadeEngine.Execute();
            } catch (Exception e) {
                assertTrue(false);
            }
            objJadeEngine.Logout();
        }
    }

    protected void CreateTestFile() {
        CreateTestFile(strTestFileName);
    }

    protected void CreateTestFile(final String pstrFileName) {
        JSFile objFile = new JSFile(strTestPathName, pstrFileName);
        try {
            objFile.WriteLine("This is a simple Testfile. nothing else.");
            objFile.close();
            createRemoteTestFiles(pstrFileName);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    protected void CreateTestFiles(final int intNumberOfFiles) {
        String strContent = "";
        for (int j = 0; j < 10; j++) {
            strContent += "This is a simple Testfile, created for the masstest. nothing else." + "\n";
        }
        String fileListName = objOptions.fileListName.Value();
        JSFile objFileList = null;
        if (isNotEmpty(fileListName)) {
            objFileList = new JSFile(fileListName);
        }
        String strFilePath = "";
        for (int i = 0; i < intNumberOfFiles; i++) {
            String strIndex = String.format("%05d", i);
            String strFileName = String.format("%1$sMasstest%2$s.txt", strTestPathName, strIndex);
            JSFile objFile = new JSFile(strFileName);
            try {
                objFile.WriteLine(strContent);
                objFile.close();
                if (isNotNull(objFileList)) {
                    objFileList.WriteLine(strFileName);
                }
                if (objOptions.Source().protocol.Value().equalsIgnoreCase(enuTransferTypes.local.Text()) == false) {
                    strFilePath += objFile.getName() + ";";
                }
            } catch (IOException e) {
                LOGGER.error("", e);
                throw new JobSchedulerException(e.getLocalizedMessage(), e);
            }
        }
        if (isNotNull(objFileList)) {
            try {
                objFileList.close();
            } catch (IOException e) {
                LOGGER.error("", e);
                throw new JobSchedulerException(e.getLocalizedMessage(), e);
            }
        }
        createRemoteTestFiles(strFilePath);
    }

    protected void logMethodName(final String pstrName) {
        //
    }

    public void renameFiles(final String source_dir, final String file_spec, final String replacement) throws Exception {
        SOSConnection2Options objConn = objOptions.getConnectionOptions();
        SOSConnection2OptionsAlternate objS = objConn.Source();
        SOSConnection2OptionsAlternate objT = objConn.Target();
        setSourceAndTarget();
        CreateTestFiles(10);
        objS.user.Value("test");
        objS.sshAuthMethod.isPassword(true);
        objS.password.Value("12345");
        objOptions.sourceDir.Value(source_dir);
        objOptions.filePath.Value("");
        objOptions.operation.Value("rename");
        objOptions.fileSpec.Value(file_spec);
        objOptions.replacing.Value(".*");
        objOptions.replacement.Value(replacement);
        startTransfer(objOptions);
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
        startTransfer(objOptions);
    }

    public void sendUsingReplacement(final String replacing, final String replacement) throws Exception {
        setParams(replacing, replacement);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        SOSOptionRegExp objRE = new SOSOptionRegExp(null, "test", "TestOption", replacing, "", false);
        String expectedRemoteFile = strKBHome + objRE.doReplace(strTestFileName, replacement);
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(expectedRemoteFile).FileExists();
        objJadeEngine.Logout();
        assertTrue(String.format("File '%1$s' does not exist", expectedRemoteFile), flgResult);
    }

    private void sendWithPolling(final boolean flgForceFiles, final boolean flgCreateFiles) throws Exception {
        final String conMethodName = CLASS_NAME + "::sendWithPolling";
        setSourceAndTarget();
        if (flgUseFilePath) {
            objOptions.filePath.Value(strTestPathName + "/test-0.poll");
        } else {
            objOptions.fileNamePatternRegExp.Value("^.*\\.poll$");
            objOptions.pollMinfiles.value(1);
        }
        objOptions.operation.Value("copy");
        objOptions.log_filename.Value(objOptions.TempDir() + "test.log");
        objOptions.profile.Value(conMethodName);
        objOptions.pollInterval.Value("0:30");
        objOptions.pollingDuration.Value("05:00");
        objOptions.errorOnNoDataFound.value(flgForceFiles);
        objOptions.removeFiles.value(true);
        LOGGER.info(objOptions.dirtyString());
        if (flgCreateFiles == true) {
            Thread thread = new Thread(new WriteFiles4Polling());
            thread.start();
        }
        startTransfer(objOptions);
    }

    public void sendWithPollingAndSteadyState() throws Exception {
        setSourceAndTarget();
        objOptions.checkSteadyStateOfFiles.value(true);
        objOptions.checkSteadyStateInterval.value(1);
        objOptions.checkSteadyCount.value(999);
        objOptions.fileNamePatternRegExp.Value("^test-steady\\.poll$");
        objOptions.pollMinfiles.value(1);
        objOptions.operation.Value("copy");
        objOptions.log_filename.Value(objOptions.TempDir() + "test.log");
        objOptions.pollInterval.value(1); //
        objOptions.pollingDuration.Value("05:00");
        LOGGER.info(objOptions.dirtyString());
        Thread thread = new Thread(new WriteFile4Polling());
        thread.start();
        startTransfer(objOptions);
    }

    public void sendWithPollingAndSteadyStateError() throws Exception {
        setSourceAndTarget();
        objOptions.checkSteadyStateOfFiles.value(true);
        objOptions.checkSteadyStateInterval.value(1);
        objOptions.checkSteadyCount.value(3);
        objOptions.fileNamePatternRegExp.Value("^test-unsteady\\.poll$");
        objOptions.pollMinfiles.value(1);
        objOptions.operation.Value("copy");
        objOptions.log_filename.Value(objOptions.TempDir() + "test.log");
        objOptions.pollInterval.value(1); //
        objOptions.pollingDuration.Value("05:00"); // for 5 minutes
        LOGGER.info(objOptions.dirtyString());
        Thread thread = new Thread(new WriteFileSlowly4PollingAndSteadyState());
        thread.start();
        startTransfer(objOptions);
    }

    protected void setFTPPrefixParams(final String replacing, final String replacement) throws Exception {
        Map<String, String> objHsh = new HashMap<String, String>();
        objHsh.put("ftp_host", HOST_NAME_WILMA_SOS);
        objHsh.put("ftp_port", "21");
        objHsh.put("ftp_user", "test");
        objHsh.put("ftp_password", "12345");
        objHsh.put("ftp_transfer_mode", "binary");
        objHsh.put("ftp_passive_mode", "0");
        objHsh.put("ftp_local_dir", "//8of9/C/scheduler.test/testsuite_files/files/ftp_out/");
        objHsh.put("ftp_file_spec", ".*");
        objHsh.put("ftp_remote_dir", "/home/test/temp/test/sosdex");
        objHsh.put("operation", "send");
        objHsh.put("replacing", replacing);
        objHsh.put("replacement", replacement);
        objHsh.put("verbose", "9");
        objOptions = new JADEOptions();
        objOptions.setAllOptions(objOptions.DeletePrefix((HashMap) objHsh, "ftp_"));
    }

    private void setMailOptions() {
        String strS = "JADE-Transfer: %{status},  Erfolgreiche Übertragungen = %{successful_transfers}, Fehlgeschlagene Übertragungen = %{failed_transfers}, letzter aufgetretener Fehler = %{last_error} ";
        objOptions.getMailOptions().to.Value("kb" + "@sos-berlin.com");
        objOptions.getMailOptions().SMTPHost.Value("smtp.sos");
        objOptions.getMailOptions().subject.Value(strS);
    }

    protected void setOptions4BackgroundService() {
        objOptions.schedulerHost.Value(HOST_NAME_8OF9_SOS);
        objOptions.schedulerPort.Value("4210");
        objOptions.schedulerTransferMethod.Value(enuJSTransferModes.tcp.description);
        objOptions.sendTransferHistory.value(true);
    }

    protected void setParams(final String replacing, final String replacement) throws Exception {
        CreateTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.remoteDir.Value(strKBHome);
        objOptions.localDir.Value(strTestPathName);
        objOptions.filePath.Value(strTestFileName);
        objOptions.operation.Value("send");
        objOptions.replacement.Value(replacement);
        objOptions.replacing.Value(replacing);
        objOptions.verbose.value(9);
    }

    private void setSourceAndTarget() {
        objOptions.checkSecurityHash.Set(objTestOptions.checkSecurityHash);
        objOptions.createSecurityHashFile.Set(objTestOptions.createSecurityHashFile);
        objOptions.securityHashType.Set(objTestOptions.securityHashType);
        objOptions.pollingServerDuration.Set(objTestOptions.pollingServerDuration);
        objOptions.pollMinfiles.Set(objTestOptions.pollMinfiles);
        objOptions.verbosityLevel.Set(objTestOptions.verbosityLevel);
        objOptions.pollingServer.Set(objTestOptions.pollingServer);
        objOptions.Source().hostName.Value(objTestOptions.Source().hostName.Value());
        objOptions.Source().protocol.Value(objTestOptions.Source().protocol.Value());
        objOptions.Source().port.Value(objTestOptions.Source().port.Value());
        objOptions.Source().user.Value(objTestOptions.Source().user.Value());
        objOptions.Source().password.Value(objTestOptions.Source().password.Value());
        objOptions.Source().authMethod.Value(objTestOptions.Source().authMethod.Value());
        objOptions.Source().proxyHost.Value(objTestOptions.Source().proxyHost.Value());
        objOptions.Source().proxyPort.Value(objTestOptions.Source().proxyPort.Value());
        objOptions.Source().domain.Value(objTestOptions.Source().domain.Value());
        objOptions.Source().transferMode.Value(objTestOptions.Source().transferMode.Value());
        objOptions.passiveMode.value(objTestOptions.passiveMode.value());
        objOptions.Source().passiveMode.value(true);
        objOptions.sourceDir.Value(objTestOptions.sourceDir.Value());
        objOptions.targetDir.Value(objTestOptions.targetDir.Value());
        objOptions.Target().user.Value(objTestOptions.Target().user.Value());
        objOptions.Target().password.Value(objTestOptions.Target().password.Value());
        objOptions.Target().authMethod.Value(objTestOptions.Target().authMethod.Value());
        objOptions.Target().protocol.Value(objTestOptions.Target().protocol.Value());
        objOptions.Target().port.Value(objTestOptions.Target().port.Value());
        objOptions.Target().hostName.Value(objTestOptions.Target().hostName.Value());
        objOptions.Target().passiveMode.value(true);
        objOptions.Target().proxyHost.Value(objTestOptions.Target().proxyHost.Value());
        objOptions.Target().proxyPort.Value(objTestOptions.Target().proxyPort.Value());
        objOptions.Target().domain.Value(objTestOptions.Target().domain.Value());
        objOptions.recursive.value(objTestOptions.recursive.value());
        objOptions.removeFiles.value(objTestOptions.removeFiles.value());
        objOptions.forceFiles.Set(objTestOptions.forceFiles);
        objOptions.overwriteFiles.value(objTestOptions.overwriteFiles.value());
        objOptions.fileSpec.Set(objTestOptions.fileSpec);
        objOptions.transactional.Set(objTestOptions.transactional);
        objOptions.transactional = objTestOptions.transactional;
        objOptions.pollingWait4SourceFolder.Set(objTestOptions.pollingWait4SourceFolder);
        if (objTestOptions.Source().loadClassName.isDirty()) {
            objOptions.Source().loadClassName.Value(objTestOptions.Source().loadClassName.Value());
        }
        if (objTestOptions.Target().loadClassName.isDirty()) {
            objOptions.Target().loadClassName.Value(objTestOptions.Target().loadClassName.Value());
        }
        LOGGER.debug(objOptions.dirtyString());
        LOGGER.debug("Options for Source\n" + objOptions.Source().dirtyString());
        LOGGER.debug("Options for Target\n" + objOptions.Target().dirtyString());
    }

    @Before
    public void setUp() throws Exception {
        String strLog4JFileName = "./src/test/resources/log4j.properties";
        String strT = new File(strLog4JFileName).getAbsolutePath();
        String strI = "./src/test/resources/examples/jade_settings.ini";
        strSettingsFile = new File(strI).getAbsolutePath();
        LOGGER.info("log4j properties filename = " + strT);
        objOptions = new JADEOptions();
        objOptions.Source().protocol.Value(enuSourceTransferType);
        objOptions.Target().protocol.Value(enuTargetTransferType);
        objTestOptions = new JADEOptions();
        objTestOptions.Source().protocol.Value(enuSourceTransferType);
        objTestOptions.Target().protocol.Value(enuTargetTransferType);
        objVFS = VFSFactory.getHandler(objOptions.protocol.Value());
        ftpClient = (ISOSVfsFileTransfer) objVFS;
        objOptions.log_filename.Value(objOptions.TempDir() + "/test.log");
        objOptions.checkServerFeatures.setTrue();
    }

    private void authenticate() throws Exception {
        LOGGER.info(objOptions.Target().dirtyString());
        objVFS.Connect(objOptions.Target());
        objVFS.Authenticate(objOptions.Target());

    }

    private void startTransfer(final JADEOptions pobjOptions) throws Exception {
        if (objJadeEngine == null) {
            objJadeEngine = new JadeEngine(pobjOptions);
        }
        LOGGER.info(objOptions.dirtyString());
        try {
            objJadeEngine.Execute();
        } catch (JADEException e) {
            LOGGER.error("Exit code must me not equal to zero = " + e.getExitCode().ExitCode);
            LOGGER.error("", e);
        }
        objOptions.Options2ClipBoard();
    }

    public void testBigCopy() throws Exception {
        setSourceAndTarget();
        objOptions.recursive.value(true);
        objOptions.fileSpec.Value("^.*$");
        objOptions.filePath.setNotDirty();
        objOptions.sourceDir.Value(objTestOptions.sourceDir.Value());
        objOptions.targetDir.Value(objTestOptions.targetDir.Value());
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.keepModificationDate.setTrue();
        objOptions.historyFileName.Value("c:/temp/JADE-history.dat");
        startTransfer(objOptions);
    }

    public void testBigCopyThreaded() throws Exception {
        objOptions.maxConcurrentTransfers.value(30);
        objOptions.concurrentTransfer.value(true);
        testBigCopy();
    }

    public void testCopy() throws Exception {
        final String conMethodName = CLASS_NAME + "::testCopy";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        SOSConnection2OptionsAlternate objS = objOptions.getConnectionOptions().Source();
        objS.host.Value(HOST_NAME_WILMA_SOS);
        objS.protocol.Value("ftp");
        objS.user.Value("kb");
        objS.password.Value("kb");
        objOptions.filePath.Value(strTestFileName);
        objOptions.sourceDir.Value("/home/kb/");
        SOSConnection2OptionsAlternate objT = objOptions.getConnectionOptions().Target();
        objT.protocol.Value("local");
        objOptions.targetDir.Value(strTestPathName);
        objOptions.remoteDir.Value(strTestPathName);
        objOptions.operation.Value(SOSOptionJadeOperation.enuJadeOperations.copy);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testCopyAndCreateVariableFolder() throws Exception {
        setSourceAndTarget();
        CreateTestFiles(10);
        objOptions.filePath.Value("");
        objOptions.fileNamePatternRegExp.Value("^.*\\.txt$");
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.targetDir.Value(objTestOptions.targetDir.Value() + "/SAVE[date:yyyyMMddHHmm]");
        objOptions.makeDirs.value(true);
        objOptions.removeFiles.value(false);
        objOptions.createResultSet.value(true);
        objOptions.resultSetFileName.Value(strTestPathName + "/Resultset.dat");
        objOptions.history.Value(strTestPathName + "/history.csv");
        startTransfer(objOptions);
    }

    /** This Test creates 10 Testfiles and copy these files to a folder on the
     * target. The original source files will be renamed. As a result of the
     * renaming the files will be stored in a subfolder named "SAVE" and the
     * filename will be extended by a DateTime stamp.
     *
     * @throws Exception */
    // @Test
    public void testCopyAndRenameSource() throws Exception {
        setSourceAndTarget();
        CreateTestFiles(10);
        objOptions.filePath.Value("");
        objOptions.fileNamePatternRegExp.Value("^.*0000\\d\\.txt$");
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.Source().replacing.Value("(.*)(.txt)");
        objOptions.Source().replacement.Value("SAVE/\\1_[date:yyyyMMddHHmm];\\2");
        objOptions.removeFiles.value(false);
        objOptions.createResultSet.value(true);
        objOptions.resultSetFileName.Value(strTestPathName + "/Resultset.dat");
        objOptions.history.Value(strTestPathName + "/history.csv");
        startTransfer(objOptions);
    }

    /** This Test copies a file that does not exist. ForceFiles=false. No error
     * should occur
     * 
     *
     * @throws Exception */
    public void testCopyForceFiles() throws Exception {
        setSourceAndTarget();
        objOptions.filePath.Value("nofile");
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.forceFiles.value(false);
        startTransfer(objOptions);
    }

    public void testCopyAndRenameSourceAndTarget() throws Exception {
        setSourceAndTarget();
        CreateTestFiles(10);
        objOptions.filePath.Value("");
        objOptions.fileNamePatternRegExp.Value("^Masstest0000\\d\\.txt$");
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.Source().replacing.Value("(.*)(.txt)");
        objOptions.Source().replacement.Value("\\1_[date:yyyyMMddHHmm];\\2");
        objOptions.Target().replacing.Value("(.*)(.txt)");
        objOptions.Target().replacement.Value("\\1_[date:yyyyMMdd];\\2");
        objOptions.replacing.Value("(.*)(.txt)");
        objOptions.replacement.Value("\\1_[date:yyyyMMdd];\\2");
        objOptions.removeFiles.value(false);
        objOptions.createResultSet.value(true);
        objOptions.resultSetFileName.Value(strTestPathName + "/Resultset.dat");
        objOptions.history.Value(strTestPathName + "/history.csv");
        startTransfer(objOptions);
    }

    public void testCopyMultipleFiles() throws Exception {
        setSourceAndTarget();
        objOptions.filePath.Value("");
        objOptions.fileNamePatternRegExp.Value("^.*\\.txt$");
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.atomicSuffix.Value(".tmp");
        startTransfer(objOptions);
    }

    public void testCopyMultipleFilesThreaded() throws Exception {
        objOptions.maxConcurrentTransfers.value(10);
        objOptions.concurrentTransfer.value(true);
        testCopyMultipleFiles();
    }

    public void testCopyMultipleResultList() throws Exception {
        String strFileListName = objOptions.TempDir() + "/FileList.lst";
        objOptions.resultSetFileName.Value(strFileListName);
        objOptions.createResultSet.value(true);
        testCopyMultipleFiles();
        JSFile objF = new JSFile(strFileListName);
        LOGGER.info(objF.File2String());
    }

    public void testCopyWithFileList() throws Exception {
        String strFileListName = objOptions.TempDir() + "/FileList.lst";
        objOptions.fileListName.Value(strFileListName);
        testCopyMultipleFiles();
    }

    /** SourceDir contains a subfolder which is matched by the file_spec.
     *
     * @throws Exception */
    public void testCopyWithFolderInSourceDir() throws Exception {
        setSourceAndTarget();
        JSFile objFile = new JSFile(strTestPathName, "subdir");
        if (objFile.exists()) {
            objFile.delete();
        }
        objOptions.fileSpec.Value("^(subdir|text\\.txt)$");
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.makeDirs.setTrue();
        objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.targetDir.Value() + "/subdir").isDirectory();
        assertTrue("Folder '/subdir' must exist", flgResult);
        objJadeEngine.Logout();
    }

    public void testDeleteFiles() throws Exception {
        setSourceAndTarget();
        CreateTestFiles(10);
        objOptions.operation.Value("delete");
        startTransfer(objOptions);
        LOGGER.debug("Number of objects = " + objJadeEngine.getFileList().count());
        objJadeEngine.Logout();
    }

    public void testDeleteFiles2() throws Exception {
        setSourceAndTarget();
        objOptions.filePath.Value("");
        objOptions.forceFiles.setFalse();
        objOptions.fileNamePatternRegExp.Value("^.*\\.txt$");
        objOptions.operation.Value(enuJadeOperations.delete);
        startTransfer(objOptions);
    }

    public void testDeleteMultipleFilesLocal() throws Exception {
        objOptions.protocol.Value("local");
        objOptions.getConnectionOptions().Source().protocol.Value("local");
        objOptions.getConnectionOptions().Target().protocol.Value("local");
        objOptions.filePath.Value("");
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(strTestPathName + "/SOSMDX/");
        objOptions.remoteDir.Value(strTestPathName + "/SOSMDX/");
        objOptions.operation.Value("delete");
        objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testDeleteZipFile() throws Exception {
        final String conMethodName = CLASS_NAME + "::testDeleteZipFile";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFileName, "-profile=zip_local_files" };
        objOptions.commandLineArgs(strCmdLineParameters);
        File fleFile = new File(objOptions.remoteDir.Value());
        fleFile.delete();
    }

    public void testExecuteGetFileList() throws Exception {
        setSourceAndTarget();
        CreateTestFiles(10);
        objOptions.filePath.Value("");
        objOptions.fileNamePatternRegExp.Value("^.*\\.txt$");
        objOptions.operation.Value(enuJadeOperations.getlist);
        objOptions.createResultSet.value(true);
        startTransfer(objOptions);
    }

    public void testFerberSFtp() throws Exception {
        CreateTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value("85.214.92.170");
        objOptions.port.Value("22");
        objOptions.protocol.Value(enuTransferTypes.sftp);
        objOptions.alternativeHost.Value("85.214.92.170");
        objOptions.alternativePort.Value("22");
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value(strTestPathName + strTestFileName);
        objOptions.localDir.Value(strTestPathName);
        objOptions.operation.Value("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
    }

    public void testGetFileList() throws Exception {
        String strFileListName = objOptions.TempDir() + "/FileList.lst";
        objOptions.resultSetFileName.Value(strFileListName);
        objOptions.createResultSet.value(true);
        testExecuteGetFileList();
        JSFile objF = new JSFile(strFileListName);
        LOGGER.info(objF.File2String());
    }

    public void testHashMapSettings() throws Exception {
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
        objHsh.put("log_filename", "c:/temp/test.log");
        objOptions = new JADEOptions();
        objOptions.setAllOptions(objOptions.DeletePrefix((HashMap) objHsh, "ftp_"));
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
    }

    public void testHashMapSettings3() throws Exception {
        HashMap<String, String> objHsh = new HashMap<String, String>();
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
        objOptions.setAllOptions(objOptions.DeletePrefix(objHsh, "ftp_"));
        assertEquals("", HOST_NAME_WILMA_SOS, objOptions.getConnectionOptions().Source().host.Value());
        assertEquals("", "tux.sos", objOptions.getConnectionOptions().Target().host.Value());
        assertEquals("", "/srv/www/htdocs/test/", objOptions.targetDir.Value());
        assertEquals("source", strCmd, objOptions.Source().preCommand.Value());
        assertEquals("target", strCmd, objOptions.Target().preCommand.Value());
        String strT2 = strCmd.replace("$SourceFileName", "testfile");
        assertEquals("target", "SITE chmod 777 testfile", strT2);
    }

    public void testIniFile1() throws Exception {
        final String conMethodName = CLASS_NAME + "::CreateIniFile";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.Value(constrSettingsTestFile);
        objOptions.profile.Value("globals");
        assertEquals("User ID", "kb", objOptions.user.Value());
        assertEquals("password", "kb", objOptions.password.Value());
    }

    public void testIniFile2() throws Exception {
        final String conMethodName = CLASS_NAME + "::testIniFile2";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.Value(constrSettingsTestFile);
        objOptions.profile.Value("include-TestTest");
        assertEquals("User ID", "kb", objOptions.user.Value());
        assertEquals("password", "kb", objOptions.password.Value());
    }

    public void testIniFile3() throws Exception {
        final String conMethodName = CLASS_NAME + "::testIniFile2";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.Value(constrSettingsTestFile);
        objOptions.profile.Value("include-Test");
        objOptions.localDir.Value(".");
        assertEquals("User ID", "kb", objOptions.user.Value());
        assertEquals("password", "kb", objOptions.password.Value());
        assertEquals("Hostname", "hostFromInclude1", objOptions.host.Value());
        assertEquals("port", 88, objOptions.port.value());
        assertEquals("protocol", "scp", objOptions.protocol.Value());
        objOptions.checkMandatory();
    }

    public void testIniFile4() throws Exception {
        final String conMethodName = CLASS_NAME + "::testIniFile2";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.Value(constrSettingsTestFile);
        objOptions.profile.Value("include-TestWithNonexistenceInclude");
        Assert.assertEquals("User ID", "kb", objOptions.user.Value());
        Assert.assertEquals("password", "kb", objOptions.password.Value());
    }

    public void testKeePass1() throws Exception {
        final String conMethodName = CLASS_NAME + "::testIniFile2";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        objOptions.settings.Value(strSettingsFile);
        objOptions.profile.Value("ReceiveUsingKeePass");
        testUseProfileWOCreatingTestFiles();
        assertEquals("User ID", "test", objOptions.Source().user.Value());
        assertEquals("password", "12345", objOptions.Source().password.Value());
    }

    public void testIniFile5() throws Exception {
        final String conMethodName = CLASS_NAME + "::testIniFile5";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.Value(constrSettingsTestFile);
        objOptions.profile.Value("substitute-Test");
        String strComputerName = System.getenv("computername");
        Assert.assertEquals("User ID", System.getenv("username"), objOptions.user.Value());
        Assert.assertEquals("Hostname", strComputerName, objOptions.host.Value());
        Assert.assertEquals("Hostnameon Target ", strComputerName + "-abc", objOptions.getConnectionOptions().Target().hostName.Value());
    }

    public void testIniFileWithSourceAndTarget() throws Exception {
        final String conMethodName = CLASS_NAME + "::testIniFile5";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.Value(strSettingsFile);
        objOptions.profile.Value("ftp_server_2_server");
        SOSConnection2Options objConn = objOptions.getConnectionOptions();
        String strComputerName = System.getenv("computername");
        Assert.assertEquals("Source.Host", HOST_NAME_WILMA_SOS, objConn.Source().host.Value());
        Assert.assertEquals("Target.Host", HOST_NAME_8OF9_SOS, objConn.Target().host.Value());
        Assert.assertEquals("file_path", "test.txt", objOptions.filePath.Value());
        objOptions.checkMandatory();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
    }

    public void testParameterPriority() throws Exception {
        final String conMethodName = CLASS_NAME + "::testParameterPriority";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFileName, "-profile=zip_local_files_2", "-operation=receive" };
        objOptions.commandLineArgs(strCmdLineParameters);
        String strOperation = objOptions.operation.Value();
        assertEquals("Operation not overwritten", "receive", strOperation);
        assertEquals("source protocol", "local", objOptions.getConnectionOptions().Source().protocol.Value());
        assertEquals("source dir", strTestPathName, objOptions.sourceDir.Value());
        assertEquals("Operation not overwritten", "receive", strOperation);
    }

    public void testParameterPriority2() throws Exception {
        final String conMethodName = CLASS_NAME + "::testParameterPriority";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFileName, "-profile=zip_local_files", "-operation=getFileList" };
        objOptions.commandLineArgs(strCmdLineParameters);
        String strOperation = objOptions.operation.Value();
        assertEquals("Precedence test failed", "getFileList", strOperation);
    }

    public void testReceive() throws Exception {
        final String conMethodName = CLASS_NAME + "::testReceive";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value(strTestFileName);
        objOptions.remoteDir.Value("/home/kb/");
        objOptions.localDir.Value(strTestPathName);
        objOptions.operation.Value("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testReceive2() throws Exception {
        final String conMethodName = CLASS_NAME + "::testReceive";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        objOptions.host.Value(HOST_NAME_8OF9_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value(strTestFileName);
        objOptions.remoteDir.Value("/kb/");
        objOptions.localDir.Value(strTestPathName);
        objOptions.operation.Value("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testReceiveFileWithRelativeSourceDir() throws Exception {
        setSourceAndTarget();
        objOptions.fileSpec.Value("\\.txt$");
        objOptions.transactional.setTrue();
        objOptions.atomicSuffix.Value("~");
        objOptions.operation.Value("receive");
        objOptions.forceFiles.value(false);
        objOptions.passiveMode.value(true);
        setMailOptions();
        LOGGER.info(objOptions.dirtyString());
        objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        checkFilesOnTarget(objJadeEngine.getFileList());
        objJadeEngine.Logout();
    }

    public void testReceiveMultipleFiles() throws Exception {
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value("");
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(strTestPathName);
        objOptions.remoteDir.Value("/home/kb/");
        objOptions.appendFiles.value(false);
        objOptions.operation.Value("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.getFileList().SuccessfulTransfers();
        for (SOSFileListEntry objListItem : objJadeEngine.getFileList().List()) {
            String strF = makeFullPathName(objOptions.targetDir.Value(), objListItem.TargetFileName());
            boolean flgResult = objListItem.getDataTargetClient().getFileHandle(strF).FileExists();
            Assert.assertTrue("File " + strF + " exist, but should not", flgResult);
        }
        objJadeEngine.Logout();
    }

    public void testReceiveMultipleFiles2() throws Exception {
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value("");
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(strTestPathName);
        objOptions.remoteDir.Value("/home/kb/");
        objOptions.appendFiles.value(false);
        objOptions.operation.Value("receive");
        objOptions.checkServerFeatures.setTrue();
        objOptions.controlEncoding.Value("UTF-8");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        long intNoOfFilesTransferred = objJadeEngine.getFileList().SuccessfulTransfers();
        for (SOSFileListEntry objListItem : objJadeEngine.getFileList().List()) {
            String strF = makeFullPathName(objOptions.targetDir.Value(), objListItem.TargetFileName());
            boolean flgResult = objListItem.getDataTargetClient().getFileHandle(strF).FileExists();
            Assert.assertTrue("File " + strF + " exist, but should not", flgResult);
        }
        objJadeEngine.Logout();
    }

    public void testReceiveSFTP() throws Exception {
        final String conMethodName = CLASS_NAME + "::testReceiveSFTP";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.protocol.Value(enuTransferTypes.sftp.Text());
        objOptions.port.Value("22");
        objOptions.password.Value("kb");
        objOptions.filePath.Value("sosdex.txt");
        objOptions.remoteDir.Value("/home/sos/tmp");
        objOptions.sshAuthMethod.Value(enuAuthenticationMethods.password);
        objOptions.bufferSize.value(1024);
        objOptions.localDir.Value(strTestPathName);
        objOptions.operation.Value("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testReceiveUsingEmptyReplacement() throws Exception {
        final String conMethodName = CLASS_NAME + "::testReceiveUsingEmptyReplacement";
        LOGGER.info("********************************************** " + conMethodName + "******************");
        HashMap<String, String> objHsh = new HashMap<String, String>();
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
        objOptions.setAllOptions(objOptions.DeletePrefix(objHsh, "ftp_"));
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        assertEquals("replacing", "^renamed_", objOptions.replacing.Value());
        assertEquals("replacement", "", objOptions.replacement.Value());
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testReceiveWithSymlinkInRemoteDir() throws Exception {
        JSFile objFile = new JSFile(strTestPathName, strTestFileName);
        if (objFile.exists()) {
            objFile.delete();
        }
        setSourceAndTarget();
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.sourceDir.Value("/tmp/test/symlink2home.test.temp/test");
        objOptions.targetDir.Value(strTestPathName);
        objOptions.operation.Value(enuJadeOperations.copy);
        LOGGER.debug(objOptions.DirtyString());
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.targetDir.Value() + "/" + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    public void testReceiveWithUmlaut() throws Exception {
        final String conMethodName = CLASS_NAME + "::testReceive";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value("Büttner.dat");
        objOptions.remoteDir.Value("/home/kb/");
        objOptions.localDir.Value(strTestPathName);
        objOptions.operation.Value("receive");
        objOptions.controlEncoding.Value("UTF-8");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testReceiveWithUmlautFromLocalhost() throws Exception {
        final String conMethodName = CLASS_NAME + "::testReceive";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        objOptions.host.Value("localhost");
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.fileSpec.Value(".*ttner\\..*");
        objOptions.remoteDir.Value("/");
        objOptions.localDir.Value(strTestPathName);
        objOptions.operation.Value("receive");
        objOptions.preFtpCommands.Value("OPTS UTF8 ON");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testRenameFiles() throws Exception {
        final String conMethodName = CLASS_NAME + "::testRenameFiles";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        String strTestDir = objTestOptions.sourceDir.Value();
        renameFiles(strTestDir, "^.*\\d\\.txt$", "moved/[filename:]");
    }

    public void testRenameFiles2FolderWhichNotExist() throws Exception {
        final String conMethodName = CLASS_NAME + "::testRenameFiles";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        String strTestDir = objTestOptions.sourceDir.Value();
        boolean flgErrorOccurs = true;
        try {
            renameFiles(strTestDir, "^.*\\d\\.txt$", "folderDoesNotExist/[filename:]");
            flgErrorOccurs = false;
        } catch (Exception e) {
            flgErrorOccurs = true;
        }
        Assert.assertFalse("Exception expected", flgErrorOccurs);
    }

    public void testRenameMultipleFilesLocal() throws Exception {
        objOptions.protocol.Value("local");
        objOptions.getConnectionOptions().Source().protocol.Value("local");
        objOptions.getConnectionOptions().Target().protocol.Value("local");
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(strTestPathName + "/SOSMDX/");
        objOptions.remoteDir.Value(strTestPathName + "/SOSMDX/");
        objOptions.operation.Value("rename");
        objOptions.replacing.Value("(.*)(.txt)");
        objOptions.replacement.Value("\\1_[date:yyyyMMddHHmm];\\2");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testRenameOnSourceOnly4FTP() throws Exception {
        final String conMethodName = CLASS_NAME + "::testRenameOnSourceOnly4FTP";
        LOGGER.info("********************************************** " + conMethodName + "******************");
        HashMap<String, String> objHsh = new HashMap<String, String>();
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
        objOptions.setAllOptions(objOptions.DeletePrefix(objHsh, "ftp_"));
        objJadeEngine = new JadeEngine(objOptions);
        assertEquals("replacing", ".*", objOptions.replacing.Value());
        assertEquals("replacement", "oh/[filename:]", objOptions.replacement.Value());
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testRenameOnSourceOnly4SFTP() throws Exception {
        final String conMethodName = CLASS_NAME + "::testRenameOnSourceOnly4SFTP";
        LOGGER.info("********************************************** " + conMethodName + "******************");
        HashMap<String, String> objHsh = new HashMap<String, String>();
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
        objOptions.setAllOptions(objOptions.DeletePrefix(objHsh, "ftp_"));
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        assertEquals("replacing", ".*", objOptions.replacing.Value());
        assertEquals("replacement", "oh/[filename:]", objOptions.replacement.Value());
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testResultSet() throws Exception {
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value("");
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(strTestPathName);
        objOptions.remoteDir.Value("/home/kb/");
        objOptions.appendFiles.value(false);
        objOptions.operation.Value("receive");
        objOptions.createResultSet.value(true);
        String strResultSetFileName = objOptions.TempDir() + "/ResultSetFile.dat";
        objOptions.resultSetFileName.Value(strResultSetFileName);
        startTransfer(objOptions);
    }

    public void testSend() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSend";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        setSourceAndTarget();
        CreateTestFile();
        objOptions.filePath.Value(strTestFileName);
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.log_filename.Value(objOptions.TempDir() + "test.log");
        objOptions.profile.Value(conMethodName);
        objOptions.verbosityLevel.value(2);
        objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        checkFilesOnTarget(objJadeEngine.getFileList());
        objJadeEngine.Logout();
    }

    public void testSend2() throws Exception {
        CreateTestFile();
        objOptions.host.Value(HOST_NAME_8OF9_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value(strTestPathName + strTestFileName);
        objOptions.localDir.Value(strTestPathName);
        objOptions.remoteDir.Value("/kb/");
        objOptions.operation.Value("send");
        objOptions.passiveMode.value(true);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/kb/" + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    public void testSend2file_spec() throws Exception {
        String strSaveTestfileName = strTestFileName;
        strTestFileName = "3519078034.pdf";
        CreateTestFile();
        setSourceAndTarget();
        objOptions.fileSpec.Value("^[0-9]{10}\\.pdf$");
        objOptions.transactional.setTrue();
        objOptions.atomicSuffix.Value(".tmp");
        objOptions.operation.Value("copy");
        objOptions.passiveMode.value(true);
        LOGGER.info(objOptions.dirtyString());
        objOptions.getConnectionOptions().Target().protocolCommandListener.setTrue();
        startTransfer(objOptions);
        checkFilesOnTarget(objJadeEngine.getFileList());
        objJadeEngine.Logout();
        strTestFileName = strSaveTestfileName;
    }

    public void testSend5() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSend";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value(strTestFileName);
        objOptions.localDir.Value(strTestPathName);
        objOptions.remoteDir.Value(strKBHome);
        objOptions.operation.Value("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(strKBHome + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    public void testSend6() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSend";
        boolean flgResult = true;
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.remoteDir.Value(strKBHome);
        objOptions.filePath.Value(strTestFileName);
        objOptions.localDir.Value(strTestPathName);
        objOptions.operation.Value("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        flgResult = objJadeEngine.getTargetClient().getFileHandle(strKBHome + strTestFileName).delete();
        objJadeEngine.Execute();
        flgResult = objJadeEngine.getTargetClient().getFileHandle(strKBHome + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    public void testSendAndDeleteMultipleFiles() throws Exception {
        setSourceAndTarget();
        CreateTestFiles(10);
        objOptions.user.Value("test");
        objOptions.password.Value("12345");
        objOptions.filePath.Value("");
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(strTestPathName);
        objOptions.operation.Value(enuJadeOperations.send);
        objOptions.deleteFilesAfterTransfer.setTrue();
        objOptions.log_filename.Value(objOptions.TempDir() + "/test.log");
        objOptions.authMethod.Value(enuAuthenticationMethods.password);
        startTransfer(objOptions);
        for (SOSFileListEntry objListItem : objJadeEngine.getFileList().List()) {
            String strF = objListItem.SourceFileName();
            boolean flgResult = objListItem.getDataSourceClient().getFileHandle(strF).FileExists();
            Assert.assertFalse("File " + strF + " exist, but should not", flgResult);
        }
        objJadeEngine.Logout();
    }

    public void testSendComand() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendComand";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFile();
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value(strTestFileName);
        objOptions.localDir.Value(strTestPathName);
        objOptions.remoteDir.Value(strKBHome);
        objOptions.operation.Value(enuJadeOperations.send);
        setOptions4BackgroundService();
        objOptions.sendTransferHistory.value(false);
        objOptions.Target().postCommand.Value("SITE CHMOD 777 $TargetFileName");
        objOptions.Source().preCommand.Value("dir $SourceFileName");
        startTransfer(objOptions);
    }

    public void testSendCommandAfterReplacing() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendComand2";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFile();
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value(strTestFileName);
        objOptions.localDir.Value(strTestPathName);
        objOptions.remoteDir.Value(strKBHome);
        objOptions.operation.Value(enuJadeOperations.send);
        objOptions.Target().setLoadClassName("com.sos.VirtualFileSystem.SFTP.SOSVfsSFtpJCraft");
        objOptions.Target().protocol.Value(enuTargetTransferType);
        objOptions.Target().port.value(SOSOptionPortNumber.conPort4SFTP);
        objOptions.sshAuthMethod.Value(enuAuthenticationMethods.password);
        objOptions.Target().replacing.Value(".*");
        objOptions.Target().replacement.Value("[filename:uppercase]_[date:yyyMMddHHmmss]");
        objOptions.Source().PreTransferCommands.Value("echo PreTransferCommands on Source; echo ${source_dir}");
        objOptions.Source().PostTransferCommands.Value("echo PostTransferCommands on Source; echo ${source_dir}");
        objOptions.Source().preCommand.Value("echo SourcePreCommand: $SourceTransferFileName + $SourceFileName");
        objOptions.Source().postCommand.Value("echo SourcePostCommand: $SourceTransferFileName + $SourceFileName");
        objOptions.Source().tfnPostCommand.Value("echo SourceTFNPostCommand $SourceTransferFileName + $SourceFileName");
        objOptions.Target().PreTransferCommands.Value("echo PreTransferCommands on Target; pwd");
        objOptions.Target().PostTransferCommands.Value("echo PostTransferCommands on Target; pwd");
        objOptions.Target().preCommand.Value("echo TargetPreCommand $TargetTransferFileName + $TargetFileName");
        objOptions.Target().postCommand.Value("echo TargetPostCommand $TargetTransferFileName + $TargetFileName; rm $TargetFileName");
        objOptions.Target().tfnPostCommand.Value("echo TargetTFNPostCommand $TargetTransferFileName + $TargetFileName");
        objOptions.Target().protocolCommandListener.value(true);
        startTransfer(objOptions);
    }

    public void testSendFileNameWithUmlaut() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSend";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        String strFileName = "Büttner.dat";
        CreateTestFile(strFileName);
        objOptions = new JADEOptions();
        objOptions.protocol.Value(enuTransferTypes.ftps);
        objOptions.host.Value("localhost");
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value(strFileName);
        objOptions.localDir.Value(strTestPathName);
        objOptions.operation.Value("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.getTargetClient().getHandler().ExecuteCommand("OPTS UTF8 ON");
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(strFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    public void testSendFileSpec() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSend";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        setSourceAndTarget();
        CreateTestFile();
        objOptions.fileSpec.Value("[0-9]{4}_(UR_RS|GZ_RS|LSTG|PZEP1)\\.txt");
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.profile.Value(conMethodName);
        objOptions.replacing.Value("([0-9]{4}_)(UR_RS|GZ_RS|LSTG|PZEP1)(\\.txt)");
        objOptions.replacement.Value("luebbenau.\1;\2;\3;");
        objOptions.forceFiles.value(false);
        objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        checkFilesOnTarget(objJadeEngine.getFileList());
        objJadeEngine.Logout();
    }

    public void testSendFileSpec2() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendFileSpec2";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        setSourceAndTarget();
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.forceFiles.value(false);
        objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testSendFtp2SFtp() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendFtp2SFtp";
        CreateTestFile();
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
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
        objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        checkFilesOnTarget(objJadeEngine.getFileList());
        objJadeEngine.Logout();
    }

    public void testSendHugeNumberOfFiles() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendHugeNumberOfFiles";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFiles(500);
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value("");
        objOptions.fileSpec.Value("^.*\\.ttxt$");
        objOptions.localDir.Value(strTestPathName);
        objOptions.operation.Value("send");
        objOptions.concurrentTransfer.value(true);
        objOptions.maxConcurrentTransfers.value(10);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testSendMultipleFiles() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendMultipleFiles";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value("");
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(strTestPathName);
        objOptions.operation.Value("send");
        setOptions4BackgroundService();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testSendMultipleFilesLocal2LocalAtomic() throws Exception {
        objOptions.protocol.Value("local");
        objOptions.filePath.Value("");
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(strTestPathName);
        objOptions.remoteDir.Value(strTestPathName + "/SOSMDX/");
        objOptions.operation.Value("send");
        objOptions.atomicSuffix.Value("~");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testSendMultipleZIPedFilesLocal2Local() throws Exception {
        objOptions.protocol.Value("local");
        objOptions.filePath.Value("");
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(strTestPathName);
        objOptions.remoteDir.Value(strTestPathName + "/SOSMDX/");
        objOptions.operation.Value("send");
        objOptions.compressFiles.value(true);
        objOptions.compressedFileExtension.Value(".zip");
        objOptions.concurrentTransfer.value(true);
        objOptions.maxConcurrentTransfers.value(5);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testSendRegExpAsFileName() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendRegExpAsFileName";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        strTestFileName = "test.txt";
        CreateTestFile(strTestFileName);
        setSourceAndTarget();
        objOptions.fileSpec.Value(strTestFileName);
        objOptions.localDir.Value(strTestPathName);
        objOptions.operation.Value("send");
        objOptions.verbose.value(9);
        startTransfer(objOptions);
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.targetDir.Value() + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    public void testSendServer2Server() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendServer2Server";
        CreateTestFile();
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
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
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.checkMandatory();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        checkFilesOnTarget(objJadeEngine.getFileList());
        objJadeEngine.Logout();
    }

    public void testSendServer2ServerMultiple() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendServer2Server";
        CreateTestFile();
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
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

    public void testSendToAlternateHost() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendToAlternateHost";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value("xwilma.sos");
        objOptions.alternativeHost.Value(HOST_NAME_WILMA_SOS);
        objOptions.alternativePort.Value("21");
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.filePath.Value(strTestPathName + strTestFileName);
        objOptions.localDir.Value(strTestPathName);
        objOptions.operation.Value("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/" + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    public void testSendUsingEmptyReplacement() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendUsingEmptyReplacement";
        LOGGER.info("********************************************** " + conMethodName + "******************");
        sendUsingReplacement("^t", "");
    }

    public void testSendUsingRelativeLocalDir() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendUsingRelativeLocalDir";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.remoteDir.Value("./relative");
        objOptions.filePath.Value(strTestFileName);
        objOptions.localDir.Value(strTestPathName);
        objOptions.operation.Value("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/relative/" + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    public void testSendUsingReplacement() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendUsingReplacement";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        sendUsingReplacement("^t", "a");
    }

    public void testSendUsingReplacement2() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendUsingReplacement2";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        sendUsingReplacement(".*", "renamed_[filename:]");
    }

    public void testSendUsingReplacement3() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendUsingReplacement3";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        setFTPPrefixParams(".*", "renamed_[filename:]");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testSendWithHashMapSettings() throws Exception {
        HashMap<String, String> objHsh = new HashMap<String, String>();
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
        objOptions.setAllOptions(objOptions.DeletePrefix(objHsh, "ftp_"));
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objOptions.checkMandatory();
        assertEquals("", "localhost", objOptions.getConnectionOptions().Source().host.Value());
        assertEquals("", "tux.sos", objOptions.getConnectionOptions().Target().host.Value());
        assertEquals("", "sftp", objOptions.getConnectionOptions().Target().protocol.Value());
        objJadeEngine.Execute();
    }

    public void testSendWithHashMapSettings2() throws Exception {
        HashMap<String, String> objHsh = new HashMap<String, String>();
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
        objOptions.setAllOptions(objOptions.DeletePrefix(objHsh, "ftp_"));
        objOptions.checkMandatory();
        assertEquals("", "localhost", objOptions.getConnectionOptions().Source().host.Value());
        assertEquals("", "tux.sos", objOptions.getConnectionOptions().Target().host.Value());
        assertEquals("", "ftp", objOptions.getConnectionOptions().Target().protocol.Value());
    }

    public void testSendWithPolling() throws Exception {
        sendWithPolling(true, true);
    }

    public void testSendWithPolling0Files() throws Exception {
        sendWithPolling(true, false);
    }

    public void testSendWithPolling0FilesUsingFilePath() throws Exception {
        flgUseFilePath = true;
        sendWithPolling(true, false);
    }

    public void testSendWithPollingAndForce() throws Exception {
        sendWithPolling(false, false);
    }

    public void testSendWithPollingAndSteadyState() throws Exception {
        sendWithPollingAndSteadyState();
    }

    public void testSendWithPollingUsingFilePath() throws Exception {
        flgUseFilePath = true;
        sendWithPolling(true, true);
    }

    public void testSendWithPrePostCommands() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendWithPrePostCommands";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value(HOST_NAME_WILMA_SOS);
        objOptions.protocol.Value(enuTransferTypes.sftp);
        assertEquals("sftp", "sftp", objOptions.protocol.Value());
        objOptions.port.value(SOSOptionPortNumber.conPort4SSH);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.authMethod.Value(enuAuthenticationMethods.password);
        objOptions.fileSpec.Value("^.*\\.txt$");
        objOptions.localDir.Value(strTestPathName);
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

    public void testSendWrongFileSpec() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendWrongFileSpec";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        setSourceAndTarget();
        CreateTestFile();
        objOptions.fileSpec.Value("[0-9]{4}_(UR_RS|GZ_RS|LSTG|PZEP1)\\.a1b2cw");
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.profile.Value(conMethodName);
        objOptions.replacing.Value("([0-9]{4}_)(UR_RS|GZ_RS|LSTG|PZEP1)(\\.txt)");
        objOptions.replacement.Value("luebbenau.\1;\2;\3;");
        startTransfer(objOptions);
    }

    public void testTransferUsingFilePath() throws Exception {
        setSourceAndTarget();
        objOptions.filePath.Value(gstrFilePath);
        objOptions.transactional.setTrue();
        objOptions.atomicSuffix.Value(".tmp");
        objOptions.operation.Value("copy");
        objOptions.passiveMode.value(true);
        objOptions.verbose.value(2);
        LOGGER.info(objOptions.dirtyString());
        if (objJadeEngine == null) {
            objJadeEngine = new JadeEngine(objOptions);
        }
        objJadeEngine.Execute();
        checkFilesOnTarget(objJadeEngine.getFileList());
    }

    public void testUseProfile() throws Exception {
        final String conMethodName = CLASS_NAME + "::testUseProfile";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFiles(10);
        testUseProfile2();
    }

    public void testUseProfile2() throws Exception {
        final String conMethodName = CLASS_NAME + "::testUseProfile2";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        LOGGER.info(objOptions.dirtyString());
        startTransfer(objOptions);
    }

    public void testUseProfileWithoutCreatingTestFiles() throws Exception {
        final String conMethodName = CLASS_NAME + "::testUseProfileWithoutCreatingTestFiles";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        startTransfer(objOptions);
    }

    public void testUseProfileWOCreatingTestFiles() throws Exception {
        final String conMethodName = CLASS_NAME + "::testUseProfile";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        startTransfer(objOptions);
    }

    public void testZipExtraction() throws Exception {
        final String conMethodName = CLASS_NAME + "::testZipExtraction";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFileName, "-profile=zip_extract_2_local_files" };
        objOptions.commandLineArgs(strCmdLineParameters);
        objOptions.sendTransferHistory.value(false);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
    }

    public void testZipOperation() throws Exception {
        final String conMethodName = CLASS_NAME + "::testZipOperation";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFileName, "-profile=zip_local_files" };
        objOptions.commandLineArgs(strCmdLineParameters);
        objOptions.sendTransferHistory.value(false);
        boolean flgOK = new JSFile(objOptions.targetDir.Value()).delete();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
    }
}
