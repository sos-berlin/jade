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
                    objFile.writeLine(str);
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
                    objFile.writeLine(str);
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
                String strT = objTestOptions.sourceDir.getValue();
                new File(strT).mkdirs();
                objFile = new JSFile(objTestOptions.sourceDir.getValue() + "/test-" + i + ".poll");
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

    private void checkFilesOnTarget(final SOSFileList objFileList) throws Exception {
        for (SOSFileListEntry objEntry : objFileList.getList()) {
            String strName = objEntry.getTargetFileName();
            boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.targetDir.getValue() + strName).fileExists();
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
        objIni.writeLine("[globals]");
        objIni.writeLine("user=kb");
        objIni.writeLine("password=kb");
        objIni.writeLine("[include1]");
        objIni.writeLine("host=hostFromInclude1");
        objIni.writeLine("[include2]");
        objIni.writeLine("port=88");
        objIni.writeLine("[include3]");
        objIni.writeLine("protocol=scp");
        objIni.writeLine("[include-Test]");
        objIni.writeLine("include=include1,include2,include3");
        objIni.writeLine("[include-TestWithNonexistenceInclude]");
        objIni.writeLine("include=include1,includeabcd2,include3");
        objIni.writeLine("[substitute-Test]");
        objIni.writeLine("user=${USERNAME}");
        objIni.writeLine("host=${COMPUTERNAME}");
        objIni.writeLine("cannotsubstitutet=${waltraut}");
        objIni.writeLine("target_host=${host}-abc");
        objIni.writeLine("alternate_target_host=${host}-abc");
    }

    private void createRemoteTestFiles(final String strFilePath) {
        if (objOptions.getSource().protocol.getValue().equalsIgnoreCase(enuTransferTypes.local.getText()) == false) {
            JADEOptions objO = new JADEOptions();
            objO.getSource().protocol.setValue(enuTransferTypes.local);
            objO.getTarget().protocol.setValue(enuTargetTransferType);
            objO.getSource().hostName.setValue("localhost");
            objO.getSource().protocol.setValue("local");
            objO.getSource().user.setValue(USER_ID_TEST);
            objO.getSource().password.setValue(PASSWORD_TEST);
            objO.sourceDir.setValue(strTestPathName);
            objO.targetDir.setValue(objTestOptions.sourceDir.getValue());
            objO.getTarget().user.setValue(objTestOptions.getSource().user.getValue());
            objO.getTarget().password.setValue(objTestOptions.getSource().password.getValue());
            objO.getTarget().protocol.setValue(objTestOptions.getSource().protocol.getValue());
            objO.getTarget().port.setValue(objTestOptions.getSource().port.getValue());
            objO.getTarget().hostName.setValue(objTestOptions.getSource().hostName.getValue());
            objO.getTarget().authMethod.setValue(objTestOptions.getSource().authMethod.getValue());
            objO.operation.setValue(SOSOptionJadeOperation.enuJadeOperations.copy);
            objO.filePath.setValue(strFilePath);
            objO.overwriteFiles.setTrue();
            try {
                objJadeEngine = new JadeEngine(objO);
                objJadeEngine.execute();
            } catch (Exception e) {
                assertTrue(false);
            }
            objJadeEngine.logout();
        }
    }

    protected void CreateTestFile() {
        CreateTestFile(strTestFileName);
    }

    protected void CreateTestFile(final String pstrFileName) {
        JSFile objFile = new JSFile(strTestPathName, pstrFileName);
        try {
            objFile.writeLine("This is a simple Testfile. nothing else.");
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
        String fileListName = objOptions.fileListName.getValue();
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
                objFile.writeLine(strContent);
                objFile.close();
                if (isNotNull(objFileList)) {
                    objFileList.writeLine(strFileName);
                }
                if (objOptions.getSource().protocol.getValue().equalsIgnoreCase(enuTransferTypes.local.getText()) == false) {
                    strFilePath += objFile.getName() + ";";
                }
            } catch (IOException e) {
                LOGGER.error("", e);
                throw new JobSchedulerException(e.getMessage(), e);
            }
        }
        if (isNotNull(objFileList)) {
            try {
                objFileList.close();
            } catch (IOException e) {
                LOGGER.error("", e);
                throw new JobSchedulerException(e.getMessage(), e);
            }
        }
        createRemoteTestFiles(strFilePath);
    }

    protected void logMethodName(final String pstrName) {
        //
    }

    public void renameFiles(final String source_dir, final String file_spec, final String replacement) throws Exception {
        SOSConnection2Options objConn = objOptions.getConnectionOptions();
        SOSConnection2OptionsAlternate objS = objConn.getSource();
        SOSConnection2OptionsAlternate objT = objConn.getTarget();
        setSourceAndTarget();
        CreateTestFiles(10);
        objS.user.setValue("test");
        objS.sshAuthMethod.isPassword(true);
        objS.password.setValue("12345");
        objOptions.sourceDir.setValue(source_dir);
        objOptions.filePath.setValue("");
        objOptions.operation.setValue("rename");
        objOptions.fileSpec.setValue(file_spec);
        objOptions.replacing.setValue(".*");
        objOptions.replacement.setValue(replacement);
        startTransfer(objOptions);
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
        startTransfer(objOptions);
    }

    public void sendUsingReplacement(final String replacing, final String replacement) throws Exception {
        setParams(replacing, replacement);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        SOSOptionRegExp objRE = new SOSOptionRegExp(null, "test", "TestOption", replacing, "", false);
        String expectedRemoteFile = strKBHome + objRE.doReplace(strTestFileName, replacement);
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(expectedRemoteFile).fileExists();
        objJadeEngine.logout();
        assertTrue(String.format("File '%1$s' does not exist", expectedRemoteFile), flgResult);
    }

    private void sendWithPolling(final boolean flgForceFiles, final boolean flgCreateFiles) throws Exception {
        final String conMethodName = CLASS_NAME + "::sendWithPolling";
        setSourceAndTarget();
        if (flgUseFilePath) {
            objOptions.filePath.setValue(strTestPathName + "/test-0.poll");
        } else {
            objOptions.fileNamePatternRegExp.setValue("^.*\\.poll$");
            objOptions.pollMinfiles.value(1);
        }
        objOptions.operation.setValue("copy");
        objOptions.logFilename.setValue(objOptions.getTempDir() + "test.log");
        objOptions.profile.setValue(conMethodName);
        objOptions.pollInterval.setValue("0:30");
        objOptions.pollingDuration.setValue("05:00");
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
        objOptions.fileNamePatternRegExp.setValue("^test-steady\\.poll$");
        objOptions.pollMinfiles.value(1);
        objOptions.operation.setValue("copy");
        objOptions.logFilename.setValue(objOptions.getTempDir() + "test.log");
        objOptions.pollInterval.value(1); //
        objOptions.pollingDuration.setValue("05:00");
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
        objOptions.fileNamePatternRegExp.setValue("^test-unsteady\\.poll$");
        objOptions.pollMinfiles.value(1);
        objOptions.operation.setValue("copy");
        objOptions.logFilename.setValue(objOptions.getTempDir() + "test.log");
        objOptions.pollInterval.value(1); //
        objOptions.pollingDuration.setValue("05:00"); // for 5 minutes
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
        objOptions.setAllOptions(objOptions.deletePrefix((HashMap) objHsh, "ftp_"));
    }

    private void setMailOptions() {
        String strS = "JADE-Transfer: %{status},  Erfolgreiche Übertragungen = %{successful_transfers}, Fehlgeschlagene Übertragungen = %{failed_transfers}, letzter aufgetretener Fehler = %{last_error} ";
        objOptions.getMailOptions().to.setValue("kb" + "@sos-berlin.com");
        objOptions.getMailOptions().SMTPHost.setValue("smtp.sos");
        objOptions.getMailOptions().subject.setValue(strS);
    }

    protected void setOptions4BackgroundService() {
        objOptions.schedulerHost.setValue(HOST_NAME_8OF9_SOS);
        objOptions.schedulerPort.setValue("4210");
        objOptions.schedulerTransferMethod.setValue(enuJSTransferModes.tcp.description);
        objOptions.sendTransferHistory.value(true);
    }

    protected void setParams(final String replacing, final String replacement) throws Exception {
        CreateTestFile();
        objOptions = new JADEOptions();
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.remoteDir.setValue(strKBHome);
        objOptions.localDir.setValue(strTestPathName);
        objOptions.filePath.setValue(strTestFileName);
        objOptions.operation.setValue("send");
        objOptions.replacement.setValue(replacement);
        objOptions.replacing.setValue(replacing);
        objOptions.verbose.value(9);
    }

    private void setSourceAndTarget() {
        objOptions.checkSecurityHash.set(objTestOptions.checkSecurityHash);
        objOptions.createSecurityHashFile.set(objTestOptions.createSecurityHashFile);
        objOptions.securityHashType.set(objTestOptions.securityHashType);
        objOptions.pollingServerDuration.set(objTestOptions.pollingServerDuration);
        objOptions.pollMinfiles.set(objTestOptions.pollMinfiles);
        objOptions.verbosityLevel.set(objTestOptions.verbosityLevel);
        objOptions.pollingServer.set(objTestOptions.pollingServer);
        objOptions.getSource().hostName.setValue(objTestOptions.getSource().hostName.getValue());
        objOptions.getSource().protocol.setValue(objTestOptions.getSource().protocol.getValue());
        objOptions.getSource().port.setValue(objTestOptions.getSource().port.getValue());
        objOptions.getSource().user.setValue(objTestOptions.getSource().user.getValue());
        objOptions.getSource().password.setValue(objTestOptions.getSource().password.getValue());
        objOptions.getSource().authMethod.setValue(objTestOptions.getSource().authMethod.getValue());
        objOptions.getSource().proxyHost.setValue(objTestOptions.getSource().proxyHost.getValue());
        objOptions.getSource().proxyPort.setValue(objTestOptions.getSource().proxyPort.getValue());
        objOptions.getSource().domain.setValue(objTestOptions.getSource().domain.getValue());
        objOptions.getSource().transferMode.setValue(objTestOptions.getSource().transferMode.getValue());
        objOptions.passiveMode.value(objTestOptions.passiveMode.value());
        objOptions.getSource().passiveMode.value(true);
        objOptions.sourceDir.setValue(objTestOptions.sourceDir.getValue());
        objOptions.targetDir.setValue(objTestOptions.targetDir.getValue());
        objOptions.getTarget().user.setValue(objTestOptions.getTarget().user.getValue());
        objOptions.getTarget().password.setValue(objTestOptions.getTarget().password.getValue());
        objOptions.getTarget().authMethod.setValue(objTestOptions.getTarget().authMethod.getValue());
        objOptions.getTarget().protocol.setValue(objTestOptions.getTarget().protocol.getValue());
        objOptions.getTarget().port.setValue(objTestOptions.getTarget().port.getValue());
        objOptions.getTarget().hostName.setValue(objTestOptions.getTarget().hostName.getValue());
        objOptions.getTarget().passiveMode.value(true);
        objOptions.getTarget().proxyHost.setValue(objTestOptions.getTarget().proxyHost.getValue());
        objOptions.getTarget().proxyPort.setValue(objTestOptions.getTarget().proxyPort.getValue());
        objOptions.getTarget().domain.setValue(objTestOptions.getTarget().domain.getValue());
        objOptions.recursive.value(objTestOptions.recursive.value());
        objOptions.removeFiles.value(objTestOptions.removeFiles.value());
        objOptions.forceFiles.set(objTestOptions.forceFiles);
        objOptions.overwriteFiles.value(objTestOptions.overwriteFiles.value());
        objOptions.fileSpec.set(objTestOptions.fileSpec);
        objOptions.transactional.set(objTestOptions.transactional);
        objOptions.transactional = objTestOptions.transactional;
        objOptions.pollingWait4SourceFolder.set(objTestOptions.pollingWait4SourceFolder);
        if (objTestOptions.getSource().loadClassName.isDirty()) {
            objOptions.getSource().loadClassName.setValue(objTestOptions.getSource().loadClassName.getValue());
        }
        if (objTestOptions.getTarget().loadClassName.isDirty()) {
            objOptions.getTarget().loadClassName.setValue(objTestOptions.getTarget().loadClassName.getValue());
        }
        LOGGER.debug(objOptions.dirtyString());
        LOGGER.debug("Options for Source\n" + objOptions.getSource().dirtyString());
        LOGGER.debug("Options for Target\n" + objOptions.getTarget().dirtyString());
    }

    @Before
    public void setUp() throws Exception {
        String strLog4JFileName = "./src/test/resources/log4j.properties";
        String strT = new File(strLog4JFileName).getAbsolutePath();
        String strI = "./src/test/resources/examples/jade_settings.ini";
        strSettingsFile = new File(strI).getAbsolutePath();
        LOGGER.info("log4j properties filename = " + strT);
        objOptions = new JADEOptions();
        objOptions.getSource().protocol.setValue(enuSourceTransferType);
        objOptions.getTarget().protocol.setValue(enuTargetTransferType);
        objTestOptions = new JADEOptions();
        objTestOptions.getSource().protocol.setValue(enuSourceTransferType);
        objTestOptions.getTarget().protocol.setValue(enuTargetTransferType);
        objVFS = VFSFactory.getHandler(objOptions.protocol.getValue());
        ftpClient = (ISOSVfsFileTransfer) objVFS;
        objOptions.logFilename.setValue(objOptions.getTempDir() + "/test.log");
    }

    private void authenticate() throws Exception {
        LOGGER.info(objOptions.getTarget().dirtyString());
        objVFS.connect(objOptions.getTarget());
        objVFS.authenticate(objOptions.getTarget());

    }

    private void startTransfer(final JADEOptions pobjOptions) throws Exception {
        if (objJadeEngine == null) {
            objJadeEngine = new JadeEngine(pobjOptions);
        }
        LOGGER.info(objOptions.dirtyString());
        try {
            objJadeEngine.execute();
        } catch (JADEException e) {
            LOGGER.error("Exit code must me not equal to zero = " + e.getExitCode().ExitCode);
            LOGGER.error("", e);
        }
        objOptions.options2ClipBoard();
    }

    public void testBigCopy() throws Exception {
        setSourceAndTarget();
        objOptions.recursive.value(true);
        objOptions.fileSpec.setValue("^.*$");
        objOptions.filePath.setNotDirty();
        objOptions.sourceDir.setValue(objTestOptions.sourceDir.getValue());
        objOptions.targetDir.setValue(objTestOptions.targetDir.getValue());
        objOptions.operation.setValue(enuJadeOperations.copy);
        objOptions.keepModificationDate.setTrue();
        objOptions.historyFileName.setValue("c:/temp/JADE-history.dat");
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
        SOSConnection2OptionsAlternate objS = objOptions.getConnectionOptions().getSource();
        objS.host.setValue(HOST_NAME_WILMA_SOS);
        objS.protocol.setValue("ftp");
        objS.user.setValue("kb");
        objS.password.setValue("kb");
        objOptions.filePath.setValue(strTestFileName);
        objOptions.sourceDir.setValue("/home/kb/");
        SOSConnection2OptionsAlternate objT = objOptions.getConnectionOptions().getTarget();
        objT.protocol.setValue("local");
        objOptions.targetDir.setValue(strTestPathName);
        objOptions.remoteDir.setValue(strTestPathName);
        objOptions.operation.setValue(SOSOptionJadeOperation.enuJadeOperations.copy);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    public void testCopyAndCreateVariableFolder() throws Exception {
        setSourceAndTarget();
        CreateTestFiles(10);
        objOptions.filePath.setValue("");
        objOptions.fileNamePatternRegExp.setValue("^.*\\.txt$");
        objOptions.operation.setValue(enuJadeOperations.copy);
        objOptions.targetDir.setValue(objTestOptions.targetDir.getValue() + "/SAVE[date:yyyyMMddHHmm]");
        objOptions.makeDirs.value(true);
        objOptions.removeFiles.value(false);
        objOptions.createResultSet.value(true);
        objOptions.resultSetFileName.setValue(strTestPathName + "/Resultset.dat");
        objOptions.history.setValue(strTestPathName + "/history.csv");
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
        objOptions.filePath.setValue("");
        objOptions.fileNamePatternRegExp.setValue("^.*0000\\d\\.txt$");
        objOptions.operation.setValue(enuJadeOperations.copy);
        objOptions.getSource().replacing.setValue("(.*)(.txt)");
        objOptions.getSource().replacement.setValue("SAVE/\\1_[date:yyyyMMddHHmm];\\2");
        objOptions.removeFiles.value(false);
        objOptions.createResultSet.value(true);
        objOptions.resultSetFileName.setValue(strTestPathName + "/Resultset.dat");
        objOptions.history.setValue(strTestPathName + "/history.csv");
        startTransfer(objOptions);
    }

    /** This Test copies a file that does not exist. ForceFiles=false. No error
     * should occur
     * 
     *
     * @throws Exception */
    public void testCopyForceFiles() throws Exception {
        setSourceAndTarget();
        objOptions.filePath.setValue("nofile");
        objOptions.operation.setValue(enuJadeOperations.copy);
        objOptions.forceFiles.value(false);
        startTransfer(objOptions);
    }

    public void testCopyAndRenameSourceAndTarget() throws Exception {
        setSourceAndTarget();
        CreateTestFiles(10);
        objOptions.filePath.setValue("");
        objOptions.fileNamePatternRegExp.setValue("^Masstest0000\\d\\.txt$");
        objOptions.operation.setValue(enuJadeOperations.copy);
        objOptions.getSource().replacing.setValue("(.*)(.txt)");
        objOptions.getSource().replacement.setValue("\\1_[date:yyyyMMddHHmm];\\2");
        objOptions.getTarget().replacing.setValue("(.*)(.txt)");
        objOptions.getTarget().replacement.setValue("\\1_[date:yyyyMMdd];\\2");
        objOptions.replacing.setValue("(.*)(.txt)");
        objOptions.replacement.setValue("\\1_[date:yyyyMMdd];\\2");
        objOptions.removeFiles.value(false);
        objOptions.createResultSet.value(true);
        objOptions.resultSetFileName.setValue(strTestPathName + "/Resultset.dat");
        objOptions.history.setValue(strTestPathName + "/history.csv");
        startTransfer(objOptions);
    }

    public void testCopyMultipleFiles() throws Exception {
        setSourceAndTarget();
        objOptions.filePath.setValue("");
        objOptions.fileNamePatternRegExp.setValue("^.*\\.txt$");
        objOptions.operation.setValue(enuJadeOperations.copy);
        objOptions.atomicSuffix.setValue(".tmp");
        startTransfer(objOptions);
    }

    public void testCopyMultipleFilesThreaded() throws Exception {
        objOptions.maxConcurrentTransfers.value(10);
        objOptions.concurrentTransfer.value(true);
        testCopyMultipleFiles();
    }

    public void testCopyMultipleResultList() throws Exception {
        String strFileListName = objOptions.getTempDir() + "/FileList.lst";
        objOptions.resultSetFileName.setValue(strFileListName);
        objOptions.createResultSet.value(true);
        testCopyMultipleFiles();
        JSFile objF = new JSFile(strFileListName);
        LOGGER.info(objF.file2String());
    }

    public void testCopyWithFileList() throws Exception {
        String strFileListName = objOptions.getTempDir() + "/FileList.lst";
        objOptions.fileListName.setValue(strFileListName);
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
        objOptions.fileSpec.setValue("^(subdir|text\\.txt)$");
        objOptions.operation.setValue(enuJadeOperations.copy);
        objOptions.makeDirs.setTrue();
        objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.targetDir.getValue() + "/subdir").isDirectory();
        assertTrue("Folder '/subdir' must exist", flgResult);
        objJadeEngine.logout();
    }

    public void testDeleteFiles() throws Exception {
        setSourceAndTarget();
        CreateTestFiles(10);
        objOptions.operation.setValue("delete");
        startTransfer(objOptions);
        LOGGER.debug("Number of objects = " + objJadeEngine.getFileList().count());
        objJadeEngine.logout();
    }

    public void testDeleteFiles2() throws Exception {
        setSourceAndTarget();
        objOptions.filePath.setValue("");
        objOptions.forceFiles.setFalse();
        objOptions.fileNamePatternRegExp.setValue("^.*\\.txt$");
        objOptions.operation.setValue(enuJadeOperations.delete);
        startTransfer(objOptions);
    }

    public void testDeleteMultipleFilesLocal() throws Exception {
        objOptions.protocol.setValue("local");
        objOptions.getConnectionOptions().getSource().protocol.setValue("local");
        objOptions.getConnectionOptions().getTarget().protocol.setValue("local");
        objOptions.filePath.setValue("");
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(strTestPathName + "/SOSMDX/");
        objOptions.remoteDir.setValue(strTestPathName + "/SOSMDX/");
        objOptions.operation.setValue("delete");
        objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    public void testDeleteZipFile() throws Exception {
        final String conMethodName = CLASS_NAME + "::testDeleteZipFile";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFileName, "-profile=zip_local_files" };
        objOptions.commandLineArgs(strCmdLineParameters);
        File fleFile = new File(objOptions.remoteDir.getValue());
        fleFile.delete();
    }

    public void testExecuteGetFileList() throws Exception {
        setSourceAndTarget();
        CreateTestFiles(10);
        objOptions.filePath.setValue("");
        objOptions.fileNamePatternRegExp.setValue("^.*\\.txt$");
        objOptions.operation.setValue(enuJadeOperations.getlist);
        objOptions.createResultSet.value(true);
        startTransfer(objOptions);
    }

    public void testFerberSFtp() throws Exception {
        CreateTestFile();
        objOptions = new JADEOptions();
        objOptions.host.setValue("85.214.92.170");
        objOptions.port.setValue("22");
        objOptions.protocol.setValue(enuTransferTypes.sftp);
        objOptions.alternativeHost.setValue("85.214.92.170");
        objOptions.alternativePort.setValue("22");
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue(strTestPathName + strTestFileName);
        objOptions.localDir.setValue(strTestPathName);
        objOptions.operation.setValue("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
    }

    public void testGetFileList() throws Exception {
        String strFileListName = objOptions.getTempDir() + "/FileList.lst";
        objOptions.resultSetFileName.setValue(strFileListName);
        objOptions.createResultSet.value(true);
        testExecuteGetFileList();
        JSFile objF = new JSFile(strFileListName);
        LOGGER.info(objF.file2String());
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
        objOptions.setAllOptions(objOptions.deletePrefix((HashMap) objHsh, "ftp_"));
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
        objOptions.setAllOptions(objOptions.deletePrefix(objHsh, "ftp_"));
        assertEquals("", HOST_NAME_WILMA_SOS, objOptions.getConnectionOptions().getSource().host.getValue());
        assertEquals("", "tux.sos", objOptions.getConnectionOptions().getTarget().host.getValue());
        assertEquals("", "/srv/www/htdocs/test/", objOptions.targetDir.getValue());
        assertEquals("source", strCmd, objOptions.getSource().preCommand.getValue());
        assertEquals("target", strCmd, objOptions.getTarget().preCommand.getValue());
        String strT2 = strCmd.replace("$SourceFileName", "testfile");
        assertEquals("target", "SITE chmod 777 testfile", strT2);
    }

    public void testIniFile1() throws Exception {
        final String conMethodName = CLASS_NAME + "::CreateIniFile";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.setValue(constrSettingsTestFile);
        objOptions.profile.setValue("globals");
        assertEquals("User ID", "kb", objOptions.user.getValue());
        assertEquals("password", "kb", objOptions.password.getValue());
    }

    public void testIniFile2() throws Exception {
        final String conMethodName = CLASS_NAME + "::testIniFile2";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.setValue(constrSettingsTestFile);
        objOptions.profile.setValue("include-TestTest");
        assertEquals("User ID", "kb", objOptions.user.getValue());
        assertEquals("password", "kb", objOptions.password.getValue());
    }

    public void testIniFile3() throws Exception {
        final String conMethodName = CLASS_NAME + "::testIniFile2";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.setValue(constrSettingsTestFile);
        objOptions.profile.setValue("include-Test");
        objOptions.localDir.setValue(".");
        assertEquals("User ID", "kb", objOptions.user.getValue());
        assertEquals("password", "kb", objOptions.password.getValue());
        assertEquals("Hostname", "hostFromInclude1", objOptions.host.getValue());
        assertEquals("port", 88, objOptions.port.value());
        assertEquals("protocol", "scp", objOptions.protocol.getValue());
        objOptions.checkMandatory();
    }

    public void testIniFile4() throws Exception {
        final String conMethodName = CLASS_NAME + "::testIniFile2";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.setValue(constrSettingsTestFile);
        objOptions.profile.setValue("include-TestWithNonexistenceInclude");
        Assert.assertEquals("User ID", "kb", objOptions.user.getValue());
        Assert.assertEquals("password", "kb", objOptions.password.getValue());
    }

    public void testKeePass1() throws Exception {
        final String conMethodName = CLASS_NAME + "::testIniFile2";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("ReceiveUsingKeePass");
        testUseProfileWOCreatingTestFiles();
        assertEquals("User ID", "test", objOptions.getSource().user.getValue());
        assertEquals("password", "12345", objOptions.getSource().password.getValue());
    }

    public void testIniFile5() throws Exception {
        final String conMethodName = CLASS_NAME + "::testIniFile5";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.setValue(constrSettingsTestFile);
        objOptions.profile.setValue("substitute-Test");
        String strComputerName = System.getenv("computername");
        Assert.assertEquals("User ID", System.getenv("username"), objOptions.user.getValue());
        Assert.assertEquals("Hostname", strComputerName, objOptions.host.getValue());
        Assert.assertEquals("Hostnameon Target ", strComputerName + "-abc", objOptions.getConnectionOptions().getTarget().hostName.getValue());
    }

    public void testIniFileWithSourceAndTarget() throws Exception {
        final String conMethodName = CLASS_NAME + "::testIniFile5";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("ftp_server_2_server");
        SOSConnection2Options objConn = objOptions.getConnectionOptions();
        String strComputerName = System.getenv("computername");
        Assert.assertEquals("Source.Host", HOST_NAME_WILMA_SOS, objConn.getSource().host.getValue());
        Assert.assertEquals("Target.Host", HOST_NAME_8OF9_SOS, objConn.getTarget().host.getValue());
        Assert.assertEquals("file_path", "test.txt", objOptions.filePath.getValue());
        objOptions.checkMandatory();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
    }

    public void testParameterPriority() throws Exception {
        final String conMethodName = CLASS_NAME + "::testParameterPriority";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFileName, "-profile=zip_local_files_2", "-operation=receive" };
        objOptions.commandLineArgs(strCmdLineParameters);
        String strOperation = objOptions.operation.getValue();
        assertEquals("Operation not overwritten", "receive", strOperation);
        assertEquals("source protocol", "local", objOptions.getConnectionOptions().getSource().protocol.getValue());
        assertEquals("source dir", strTestPathName, objOptions.sourceDir.getValue());
        assertEquals("Operation not overwritten", "receive", strOperation);
    }

    public void testParameterPriority2() throws Exception {
        final String conMethodName = CLASS_NAME + "::testParameterPriority";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFileName, "-profile=zip_local_files", "-operation=getFileList" };
        objOptions.commandLineArgs(strCmdLineParameters);
        String strOperation = objOptions.operation.getValue();
        assertEquals("Precedence test failed", "getFileList", strOperation);
    }

    public void testReceive() throws Exception {
        final String conMethodName = CLASS_NAME + "::testReceive";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue(strTestFileName);
        objOptions.remoteDir.setValue("/home/kb/");
        objOptions.localDir.setValue(strTestPathName);
        objOptions.operation.setValue("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    public void testReceive2() throws Exception {
        final String conMethodName = CLASS_NAME + "::testReceive";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        objOptions.host.setValue(HOST_NAME_8OF9_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue(strTestFileName);
        objOptions.remoteDir.setValue("/kb/");
        objOptions.localDir.setValue(strTestPathName);
        objOptions.operation.setValue("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    public void testReceiveFileWithRelativeSourceDir() throws Exception {
        setSourceAndTarget();
        objOptions.fileSpec.setValue("\\.txt$");
        objOptions.transactional.setTrue();
        objOptions.atomicSuffix.setValue("~");
        objOptions.operation.setValue("receive");
        objOptions.forceFiles.value(false);
        objOptions.passiveMode.value(true);
        setMailOptions();
        LOGGER.info(objOptions.dirtyString());
        objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        checkFilesOnTarget(objJadeEngine.getFileList());
        objJadeEngine.logout();
    }

    public void testReceiveMultipleFiles() throws Exception {
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue("");
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(strTestPathName);
        objOptions.remoteDir.setValue("/home/kb/");
        objOptions.appendFiles.value(false);
        objOptions.operation.setValue("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.getFileList().getSuccessfulTransfers();
        for (SOSFileListEntry objListItem : objJadeEngine.getFileList().getList()) {
            String strF = makeFullPathName(objOptions.targetDir.getValue(), objListItem.getTargetFileName());
            boolean flgResult = objListItem.getDataTargetClient().getFileHandle(strF).fileExists();
            Assert.assertTrue("File " + strF + " exist, but should not", flgResult);
        }
        objJadeEngine.logout();
    }

    public void testReceiveMultipleFiles2() throws Exception {
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue("");
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(strTestPathName);
        objOptions.remoteDir.setValue("/home/kb/");
        objOptions.appendFiles.value(false);
        objOptions.operation.setValue("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        long intNoOfFilesTransferred = objJadeEngine.getFileList().getSuccessfulTransfers();
        for (SOSFileListEntry objListItem : objJadeEngine.getFileList().getList()) {
            String strF = makeFullPathName(objOptions.targetDir.getValue(), objListItem.getTargetFileName());
            boolean flgResult = objListItem.getDataTargetClient().getFileHandle(strF).fileExists();
            Assert.assertTrue("File " + strF + " exist, but should not", flgResult);
        }
        objJadeEngine.logout();
    }

    public void testReceiveSFTP() throws Exception {
        final String conMethodName = CLASS_NAME + "::testReceiveSFTP";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.protocol.setValue(enuTransferTypes.sftp.getText());
        objOptions.port.setValue("22");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue("sosdex.txt");
        objOptions.remoteDir.setValue("/home/sos/tmp");
        objOptions.sshAuthMethod.setValue(enuAuthenticationMethods.password);
        objOptions.bufferSize.value(1024);
        objOptions.localDir.setValue(strTestPathName);
        objOptions.operation.setValue("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
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
        objOptions.setAllOptions(objOptions.deletePrefix(objHsh, "ftp_"));
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        assertEquals("replacing", "^renamed_", objOptions.replacing.getValue());
        assertEquals("replacement", "", objOptions.replacement.getValue());
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    public void testReceiveWithSymlinkInRemoteDir() throws Exception {
        JSFile objFile = new JSFile(strTestPathName, strTestFileName);
        if (objFile.exists()) {
            objFile.delete();
        }
        setSourceAndTarget();
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.sourceDir.setValue("/tmp/test/symlink2home.test.temp/test");
        objOptions.targetDir.setValue(strTestPathName);
        objOptions.operation.setValue(enuJadeOperations.copy);
        LOGGER.debug(objOptions.dirtyString());
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.targetDir.getValue() + "/" + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
    }

    public void testReceiveWithUmlaut() throws Exception {
        final String conMethodName = CLASS_NAME + "::testReceive";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue("Büttner.dat");
        objOptions.remoteDir.setValue("/home/kb/");
        objOptions.localDir.setValue(strTestPathName);
        objOptions.operation.setValue("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    public void testReceiveWithUmlautFromLocalhost() throws Exception {
        final String conMethodName = CLASS_NAME + "::testReceive";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        objOptions.host.setValue("localhost");
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.fileSpec.setValue(".*ttner\\..*");
        objOptions.remoteDir.setValue("/");
        objOptions.localDir.setValue(strTestPathName);
        objOptions.operation.setValue("receive");
        objOptions.preFtpCommands.setValue("OPTS UTF8 ON");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    public void testRenameFiles() throws Exception {
        final String conMethodName = CLASS_NAME + "::testRenameFiles";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        String strTestDir = objTestOptions.sourceDir.getValue();
        renameFiles(strTestDir, "^.*\\d\\.txt$", "moved/[filename:]");
    }

    public void testRenameFiles2FolderWhichNotExist() throws Exception {
        final String conMethodName = CLASS_NAME + "::testRenameFiles";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        String strTestDir = objTestOptions.sourceDir.getValue();
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
        objOptions.protocol.setValue("local");
        objOptions.getConnectionOptions().getSource().protocol.setValue("local");
        objOptions.getConnectionOptions().getTarget().protocol.setValue("local");
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(strTestPathName + "/SOSMDX/");
        objOptions.remoteDir.setValue(strTestPathName + "/SOSMDX/");
        objOptions.operation.setValue("rename");
        objOptions.replacing.setValue("(.*)(.txt)");
        objOptions.replacement.setValue("\\1_[date:yyyyMMddHHmm];\\2");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
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
        objOptions.setAllOptions(objOptions.deletePrefix(objHsh, "ftp_"));
        objJadeEngine = new JadeEngine(objOptions);
        assertEquals("replacing", ".*", objOptions.replacing.getValue());
        assertEquals("replacement", "oh/[filename:]", objOptions.replacement.getValue());
        objJadeEngine.execute();
        objJadeEngine.logout();
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
        objOptions.setAllOptions(objOptions.deletePrefix(objHsh, "ftp_"));
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        assertEquals("replacing", ".*", objOptions.replacing.getValue());
        assertEquals("replacement", "oh/[filename:]", objOptions.replacement.getValue());
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    public void testResultSet() throws Exception {
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue("");
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(strTestPathName);
        objOptions.remoteDir.setValue("/home/kb/");
        objOptions.appendFiles.value(false);
        objOptions.operation.setValue("receive");
        objOptions.createResultSet.value(true);
        String strResultSetFileName = objOptions.getTempDir() + "/ResultSetFile.dat";
        objOptions.resultSetFileName.setValue(strResultSetFileName);
        startTransfer(objOptions);
    }

    public void testSend() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSend";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        setSourceAndTarget();
        CreateTestFile();
        objOptions.filePath.setValue(strTestFileName);
        objOptions.operation.setValue(enuJadeOperations.copy);
        objOptions.logFilename.setValue(objOptions.getTempDir() + "test.log");
        objOptions.profile.setValue(conMethodName);
        objOptions.verbosityLevel.value(2);
        objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        checkFilesOnTarget(objJadeEngine.getFileList());
        objJadeEngine.logout();
    }

    public void testSend2() throws Exception {
        CreateTestFile();
        objOptions.host.setValue(HOST_NAME_8OF9_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue(strTestPathName + strTestFileName);
        objOptions.localDir.setValue(strTestPathName);
        objOptions.remoteDir.setValue("/kb/");
        objOptions.operation.setValue("send");
        objOptions.passiveMode.value(true);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/kb/" + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
    }

    public void testSend2file_spec() throws Exception {
        String strSaveTestfileName = strTestFileName;
        strTestFileName = "3519078034.pdf";
        CreateTestFile();
        setSourceAndTarget();
        objOptions.fileSpec.setValue("^[0-9]{10}\\.pdf$");
        objOptions.transactional.setTrue();
        objOptions.atomicSuffix.setValue(".tmp");
        objOptions.operation.setValue("copy");
        objOptions.passiveMode.value(true);
        LOGGER.info(objOptions.dirtyString());
        objOptions.getConnectionOptions().getTarget().protocolCommandListener.setTrue();
        startTransfer(objOptions);
        checkFilesOnTarget(objJadeEngine.getFileList());
        objJadeEngine.logout();
        strTestFileName = strSaveTestfileName;
    }

    public void testSend5() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSend";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFile();
        objOptions = new JADEOptions();
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue(strTestFileName);
        objOptions.localDir.setValue(strTestPathName);
        objOptions.remoteDir.setValue(strKBHome);
        objOptions.operation.setValue("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(strKBHome + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
    }

    public void testSend6() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSend";
        boolean flgResult = true;
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFile();
        objOptions = new JADEOptions();
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.remoteDir.setValue(strKBHome);
        objOptions.filePath.setValue(strTestFileName);
        objOptions.localDir.setValue(strTestPathName);
        objOptions.operation.setValue("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        flgResult = objJadeEngine.getTargetClient().getFileHandle(strKBHome + strTestFileName).delete();
        objJadeEngine.execute();
        flgResult = objJadeEngine.getTargetClient().getFileHandle(strKBHome + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
    }

    public void testSendAndDeleteMultipleFiles() throws Exception {
        setSourceAndTarget();
        CreateTestFiles(10);
        objOptions.user.setValue("test");
        objOptions.password.setValue("12345");
        objOptions.filePath.setValue("");
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(strTestPathName);
        objOptions.operation.setValue(enuJadeOperations.send);
        objOptions.deleteFilesAfterTransfer.setTrue();
        objOptions.logFilename.setValue(objOptions.getTempDir() + "/test.log");
        objOptions.authMethod.setValue(enuAuthenticationMethods.password);
        startTransfer(objOptions);
        for (SOSFileListEntry objListItem : objJadeEngine.getFileList().getList()) {
            String strF = objListItem.getSourceFileName();
            boolean flgResult = objListItem.getDataSourceClient().getFileHandle(strF).fileExists();
            Assert.assertFalse("File " + strF + " exist, but should not", flgResult);
        }
        objJadeEngine.logout();
    }

    public void testSendComand() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendComand";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFile();
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue(strTestFileName);
        objOptions.localDir.setValue(strTestPathName);
        objOptions.remoteDir.setValue(strKBHome);
        objOptions.operation.setValue(enuJadeOperations.send);
        setOptions4BackgroundService();
        objOptions.sendTransferHistory.value(false);
        objOptions.getTarget().postCommand.setValue("SITE CHMOD 777 $TargetFileName");
        objOptions.getSource().preCommand.setValue("dir $SourceFileName");
        startTransfer(objOptions);
    }

    public void testSendCommandAfterReplacing() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendComand2";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFile();
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue(strTestFileName);
        objOptions.localDir.setValue(strTestPathName);
        objOptions.remoteDir.setValue(strKBHome);
        objOptions.operation.setValue(enuJadeOperations.send);
        objOptions.getTarget().setLoadClassName("com.sos.VirtualFileSystem.SFTP.SOSVfsSFtpJCraft");
        objOptions.getTarget().protocol.setValue(enuTargetTransferType);
        objOptions.getTarget().port.value(SOSOptionPortNumber.conPort4SFTP);
        objOptions.sshAuthMethod.setValue(enuAuthenticationMethods.password);
        objOptions.getTarget().replacing.setValue(".*");
        objOptions.getTarget().replacement.setValue("[filename:uppercase]_[date:yyyMMddHHmmss]");
        objOptions.getSource().preTransferCommands.setValue("echo PreTransferCommands on Source; echo ${source_dir}");
        objOptions.getSource().postTransferCommands.setValue("echo PostTransferCommands on Source; echo ${source_dir}");
        objOptions.getSource().preCommand.setValue("echo SourcePreCommand: $SourceTransferFileName + $SourceFileName");
        objOptions.getSource().postCommand.setValue("echo SourcePostCommand: $SourceTransferFileName + $SourceFileName");
        objOptions.getSource().tfnPostCommand.setValue("echo SourceTFNPostCommand $SourceTransferFileName + $SourceFileName");
        objOptions.getTarget().preTransferCommands.setValue("echo PreTransferCommands on Target; pwd");
        objOptions.getTarget().postTransferCommands.setValue("echo PostTransferCommands on Target; pwd");
        objOptions.getTarget().preCommand.setValue("echo TargetPreCommand $TargetTransferFileName + $TargetFileName");
        objOptions.getTarget().postCommand.setValue("echo TargetPostCommand $TargetTransferFileName + $TargetFileName; rm $TargetFileName");
        objOptions.getTarget().tfnPostCommand.setValue("echo TargetTFNPostCommand $TargetTransferFileName + $TargetFileName");
        objOptions.getTarget().protocolCommandListener.value(true);
        startTransfer(objOptions);
    }

    public void testSendFileNameWithUmlaut() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSend";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        String strFileName = "Büttner.dat";
        CreateTestFile(strFileName);
        objOptions = new JADEOptions();
        objOptions.protocol.setValue(enuTransferTypes.ftps);
        objOptions.host.setValue("localhost");
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue(strFileName);
        objOptions.localDir.setValue(strTestPathName);
        objOptions.operation.setValue("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.getTargetClient().getHandler().executeCommand("OPTS UTF8 ON");
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(strFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
    }

    public void testSendFileSpec() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSend";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        setSourceAndTarget();
        CreateTestFile();
        objOptions.fileSpec.setValue("[0-9]{4}_(UR_RS|GZ_RS|LSTG|PZEP1)\\.txt");
        objOptions.operation.setValue(enuJadeOperations.copy);
        objOptions.profile.setValue(conMethodName);
        objOptions.replacing.setValue("([0-9]{4}_)(UR_RS|GZ_RS|LSTG|PZEP1)(\\.txt)");
        objOptions.replacement.setValue("luebbenau.\1;\2;\3;");
        objOptions.forceFiles.value(false);
        objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        checkFilesOnTarget(objJadeEngine.getFileList());
        objJadeEngine.logout();
    }

    public void testSendFileSpec2() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendFileSpec2";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        setSourceAndTarget();
        objOptions.operation.setValue(enuJadeOperations.copy);
        objOptions.forceFiles.value(false);
        objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    public void testSendFtp2SFtp() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendFtp2SFtp";
        CreateTestFile();
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
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
        objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        checkFilesOnTarget(objJadeEngine.getFileList());
        objJadeEngine.logout();
    }

    public void testSendHugeNumberOfFiles() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendHugeNumberOfFiles";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFiles(500);
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue("");
        objOptions.fileSpec.setValue("^.*\\.ttxt$");
        objOptions.localDir.setValue(strTestPathName);
        objOptions.operation.setValue("send");
        objOptions.concurrentTransfer.value(true);
        objOptions.maxConcurrentTransfers.value(10);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    public void testSendMultipleFiles() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendMultipleFiles";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue("");
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(strTestPathName);
        objOptions.operation.setValue("send");
        setOptions4BackgroundService();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    public void testSendMultipleFilesLocal2LocalAtomic() throws Exception {
        objOptions.protocol.setValue("local");
        objOptions.filePath.setValue("");
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(strTestPathName);
        objOptions.remoteDir.setValue(strTestPathName + "/SOSMDX/");
        objOptions.operation.setValue("send");
        objOptions.atomicSuffix.setValue("~");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    public void testSendMultipleZIPedFilesLocal2Local() throws Exception {
        objOptions.protocol.setValue("local");
        objOptions.filePath.setValue("");
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(strTestPathName);
        objOptions.remoteDir.setValue(strTestPathName + "/SOSMDX/");
        objOptions.operation.setValue("send");
        objOptions.compressFiles.value(true);
        objOptions.compressedFileExtension.setValue(".zip");
        objOptions.concurrentTransfer.value(true);
        objOptions.maxConcurrentTransfers.value(5);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    public void testSendRegExpAsFileName() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendRegExpAsFileName";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        strTestFileName = "test.txt";
        CreateTestFile(strTestFileName);
        setSourceAndTarget();
        objOptions.fileSpec.setValue(strTestFileName);
        objOptions.localDir.setValue(strTestPathName);
        objOptions.operation.setValue("send");
        objOptions.verbose.value(9);
        startTransfer(objOptions);
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.targetDir.getValue() + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
    }

    public void testSendServer2Server() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendServer2Server";
        CreateTestFile();
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
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
        objOptions.operation.setValue(enuJadeOperations.copy);
        objOptions.checkMandatory();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        checkFilesOnTarget(objJadeEngine.getFileList());
        objJadeEngine.logout();
    }

    public void testSendServer2ServerMultiple() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendServer2Server";
        CreateTestFile();
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
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

    public void testSendToAlternateHost() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendToAlternateHost";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFile();
        objOptions = new JADEOptions();
        objOptions.host.setValue("xwilma.sos");
        objOptions.alternativeHost.setValue(HOST_NAME_WILMA_SOS);
        objOptions.alternativePort.setValue("21");
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.filePath.setValue(strTestPathName + strTestFileName);
        objOptions.localDir.setValue(strTestPathName);
        objOptions.operation.setValue("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/" + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
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
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.remoteDir.setValue("./relative");
        objOptions.filePath.setValue(strTestFileName);
        objOptions.localDir.setValue(strTestPathName);
        objOptions.operation.setValue("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/relative/" + strTestFileName).fileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.logout();
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
        objJadeEngine.execute();
        objJadeEngine.logout();
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
        objOptions.setAllOptions(objOptions.deletePrefix(objHsh, "ftp_"));
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objOptions.checkMandatory();
        assertEquals("", "localhost", objOptions.getConnectionOptions().getSource().host.getValue());
        assertEquals("", "tux.sos", objOptions.getConnectionOptions().getTarget().host.getValue());
        assertEquals("", "sftp", objOptions.getConnectionOptions().getTarget().protocol.getValue());
        objJadeEngine.execute();
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
        objOptions.protocol.setValue(enuTransferTypes.ftp);
        objOptions.setAllOptions(objOptions.deletePrefix(objHsh, "ftp_"));
        objOptions.checkMandatory();
        assertEquals("", "localhost", objOptions.getConnectionOptions().getSource().host.getValue());
        assertEquals("", "tux.sos", objOptions.getConnectionOptions().getTarget().host.getValue());
        assertEquals("", "ftp", objOptions.getConnectionOptions().getTarget().protocol.getValue());
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
        objOptions.host.setValue(HOST_NAME_WILMA_SOS);
        objOptions.protocol.setValue(enuTransferTypes.sftp);
        assertEquals("sftp", "sftp", objOptions.protocol.getValue());
        objOptions.port.value(SOSOptionPortNumber.conPort4SSH);
        objOptions.user.setValue("kb");
        objOptions.password.setValue("kb");
        objOptions.authMethod.setValue(enuAuthenticationMethods.password);
        objOptions.fileSpec.setValue("^.*\\.txt$");
        objOptions.localDir.setValue(strTestPathName);
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

    public void testSendWrongFileSpec() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSendWrongFileSpec";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        setSourceAndTarget();
        CreateTestFile();
        objOptions.fileSpec.setValue("[0-9]{4}_(UR_RS|GZ_RS|LSTG|PZEP1)\\.a1b2cw");
        objOptions.operation.setValue(enuJadeOperations.copy);
        objOptions.profile.setValue(conMethodName);
        objOptions.replacing.setValue("([0-9]{4}_)(UR_RS|GZ_RS|LSTG|PZEP1)(\\.txt)");
        objOptions.replacement.setValue("luebbenau.\1;\2;\3;");
        startTransfer(objOptions);
    }

    public void testTransferUsingFilePath() throws Exception {
        setSourceAndTarget();
        objOptions.filePath.setValue(gstrFilePath);
        objOptions.transactional.setTrue();
        objOptions.atomicSuffix.setValue(".tmp");
        objOptions.operation.setValue("copy");
        objOptions.passiveMode.value(true);
        objOptions.verbose.value(2);
        LOGGER.info(objOptions.dirtyString());
        if (objJadeEngine == null) {
            objJadeEngine = new JadeEngine(objOptions);
        }
        objJadeEngine.execute();
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
        objJadeEngine.execute();
    }

    public void testZipOperation() throws Exception {
        final String conMethodName = CLASS_NAME + "::testZipOperation";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFileName, "-profile=zip_local_files" };
        objOptions.commandLineArgs(strCmdLineParameters);
        objOptions.sendTransferHistory.value(false);
        boolean flgOK = new JSFile(objOptions.targetDir.getValue()).delete();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.execute();
    }
}
