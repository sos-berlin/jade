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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
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

    protected static final String conHostNameWILMA_SOS = "wilma.sos";
    protected static final String conHostNameHOMER_SOS = "homer.sos";
    protected static final String conHostName8OF9_SOS = "8of9.sos";
    protected static final String conUserIdTest = "test";
    protected static final String conPasswordTest = "12345";
    protected static final String conTargetOfDOXYGEN_DOCS = "R:/backup/www.sos-berlin.com/doc/doxygen-docs/";
    protected static final String conSourceOfDOXYGEN_DOCS = "R:/backup/sos/java/doxygen-docs/";
    protected JadeEngine objJadeEngine = null;
    protected final String conClassName = "JadeTestBase";
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
    String constrSettingsTestFile = strTestPathName + "/SOSDEx-test.ini";
    protected String strSettingsFile = "R:/backup/sos/java/development/SOSDataExchange/examples/jade_settings.ini";
    // @SuppressWarnings("unused")
    // protected String strAPrefix = "~~";
    protected String dynamicClassNameSource = null;
    protected String gstrFilePath = "";
    private boolean flgUseFilePath = false;
    private static final Logger LOGGER = Logger.getLogger(JadeTestBase.class);

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
                String strT = objTestOptions.SourceDir.Value();
                new File(strT).mkdirs();
                objFile = new JSFile(objTestOptions.SourceDir.Value() + "/test-" + i + ".poll");
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
            boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.TargetDir.Value() + strName).FileExists();
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
            objO.Source().HostName.Value("localhost");
            objO.Source().protocol.Value("local");
            objO.Source().user.Value(conUserIdTest);
            objO.Source().password.Value(conPasswordTest);
            objO.SourceDir.Value(strTestPathName);
            objO.TargetDir.Value(objTestOptions.SourceDir.Value());
            objO.Target().user.Value(objTestOptions.Source().user.Value());
            objO.Target().password.Value(objTestOptions.Source().password.Value());
            objO.Target().protocol.Value(objTestOptions.Source().protocol.Value());
            objO.Target().port.Value(objTestOptions.Source().port.Value());
            objO.Target().HostName.Value(objTestOptions.Source().HostName.Value());
            objO.Target().auth_method.Value(objTestOptions.Source().auth_method.Value());
            objO.operation.Value(SOSOptionJadeOperation.enuJadeOperations.copy);
            objO.file_path.Value(strFilePath);
            objO.overwrite_files.setTrue();
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
        String fileListName = objOptions.FileListName.Value();
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
        objS.ssh_auth_method.isPassword(true);
        objS.password.Value("12345");
        objOptions.SourceDir.Value(source_dir);
        objOptions.file_path.Value("");
        objOptions.operation.Value("rename");
        objOptions.file_spec.Value(file_spec);
        objOptions.replacing.Value(".*");
        objOptions.replacement.Value(replacement);
        startTransfer(objOptions);
    }

    public void renameLocalFiles(final String source_dir, final String file_spec) throws Exception {
        SOSConnection2Options objConn = objOptions.getConnectionOptions();
        SOSConnection2OptionsAlternate objS = objConn.Source();
        SOSConnection2OptionsAlternate objT = objConn.Target();
        objS.HostName.Value(conHostNameWILMA_SOS);
        objS.port.value(SOSOptionPortNumber.getStandardSFTPPort());
        objS.protocol.Value("sftp");
        objS.user.Value("test");
        objS.ssh_auth_method.isPassword(true);
        objS.password.Value("12345");
        objT.HostName.Value(conHostNameWILMA_SOS);
        objT.port.value(SOSOptionPortNumber.getStandardSFTPPort());
        objT.protocol.Value("sftp");
        objT.user.Value("test");
        objT.ssh_auth_method.isPassword(true);
        objT.password.Value("12345");
        objOptions.SourceDir.Value(source_dir);
        objOptions.TargetDir.Value(source_dir);
        objOptions.file_path.Value("");
        objOptions.operation.Value("rename");
        objOptions.file_spec.Value(file_spec);
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
        final String conMethodName = conClassName + "::sendWithPolling";
        setSourceAndTarget();
        if (flgUseFilePath) {
            objOptions.file_path.Value(strTestPathName + "/test-0.poll");
        } else {
            objOptions.FileNamePatternRegExp.Value("^.*\\.poll$");
            objOptions.poll_minfiles.value(1);
        }
        objOptions.operation.Value("copy");
        objOptions.log_filename.Value(objOptions.TempDir() + "test.log");
        objOptions.profile.Value(conMethodName);
        objOptions.CreateSecurityHash.value(false);
        objOptions.poll_interval.Value("0:30");
        objOptions.PollingDuration.Value("05:00");
        objOptions.ErrorOnNoDataFound.value(flgForceFiles);
        objOptions.remove_files.value(true);
        LOGGER.info(objOptions.dirtyString());
        if (flgCreateFiles == true) {
            Thread thread = new Thread(new WriteFiles4Polling()); 
            thread.start();
        }
        startTransfer(objOptions);
    }

    public void sendWithPollingAndSteadyState() throws Exception {
        setSourceAndTarget();
        objOptions.CheckSteadyStateOfFiles.value(true);
        objOptions.CheckSteadyStateInterval.value(1);
        objOptions.CheckSteadyCount.value(999);
        objOptions.FileNamePatternRegExp.Value("^test-steady\\.poll$");
        objOptions.poll_minfiles.value(1);
        objOptions.operation.Value("copy");
        objOptions.log_filename.Value(objOptions.TempDir() + "test.log");
        objOptions.poll_interval.value(1); //
        objOptions.PollingDuration.Value("05:00");
        LOGGER.info(objOptions.dirtyString());
        Thread thread = new Thread(new WriteFile4Polling());
        thread.start();
        startTransfer(objOptions);
    }

    public void sendWithPollingAndSteadyStateError() throws Exception {
        setSourceAndTarget();
        objOptions.CheckSteadyStateOfFiles.value(true);
        objOptions.CheckSteadyStateInterval.value(1);
        objOptions.CheckSteadyCount.value(3);
        objOptions.FileNamePatternRegExp.Value("^test-unsteady\\.poll$");
        objOptions.poll_minfiles.value(1);
        objOptions.operation.Value("copy");
        objOptions.log_filename.Value(objOptions.TempDir() + "test.log");
        objOptions.poll_interval.value(1); //
        objOptions.PollingDuration.Value("05:00"); // for 5 minutes
        LOGGER.info(objOptions.dirtyString());
        Thread thread = new Thread(new WriteFileSlowly4PollingAndSteadyState());
        thread.start();
        startTransfer(objOptions);
    }

    protected void setFTPPrefixParams(final String replacing, final String replacement) throws Exception {
        Map<String, String> objHsh = new HashMap<String, String>();
        objHsh.put("ftp_host", conHostNameWILMA_SOS);
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
        objOptions.setAllOptions(objOptions.DeletePrefix((HashMap)objHsh, "ftp_"));
    }

    private void setMailOptions() {
        String strS = "JADE-Transfer: %{status},  Erfolgreiche Übertragungen = %{successful_transfers}, Fehlgeschlagene Übertragungen = %{failed_transfers}, letzter aufgetretener Fehler = %{last_error} ";
        objOptions.getMailOptions().to.Value("kb" + "@sos-berlin.com");
        objOptions.getMailOptions().SMTPHost.Value("smtp.sos");
        objOptions.getMailOptions().subject.Value(strS);
    }

    protected void setOptions4BackgroundService() {
        objOptions.scheduler_host.Value(conHostName8OF9_SOS);
        objOptions.scheduler_port.Value("4210");
        objOptions.Scheduler_Transfer_Method.Value(enuJSTransferModes.tcp.description);
        objOptions.SendTransferHistory.value(true);
    }

    protected void setParams(final String replacing, final String replacement) throws Exception {
        CreateTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value(conHostNameWILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.remote_dir.Value(strKBHome);
        objOptions.local_dir.Value(strTestPathName);
        objOptions.file_path.Value(strTestFileName);
        objOptions.operation.Value("send");
        objOptions.replacement.Value(replacement);
        objOptions.replacing.Value(replacing);
        objOptions.verbose.value(9);
    }

    private void setSourceAndTarget() {
        objOptions.CheckSecurityHash.Set(objTestOptions.CheckSecurityHash);
        objOptions.CreateSecurityHash.Set(objTestOptions.CreateSecurityHash);
        objOptions.CreateSecurityHashFile.Set(objTestOptions.CreateSecurityHashFile);
        objOptions.SecurityHashType.Set(objTestOptions.SecurityHashType);
        objOptions.pollingServerDuration.Set(objTestOptions.pollingServerDuration);
        objOptions.poll_minfiles.Set(objTestOptions.poll_minfiles);
        objOptions.VerbosityLevel.Set(objTestOptions.VerbosityLevel);
        objOptions.PollingServer.Set(objTestOptions.PollingServer);
        objOptions.Source().HostName.Value(objTestOptions.Source().HostName.Value());
        objOptions.Source().protocol.Value(objTestOptions.Source().protocol.Value());
        objOptions.Source().port.Value(objTestOptions.Source().port.Value());
        objOptions.Source().user.Value(objTestOptions.Source().user.Value());
        objOptions.Source().password.Value(objTestOptions.Source().password.Value());
        objOptions.Source().auth_method.Value(objTestOptions.Source().auth_method.Value());
        objOptions.Source().proxy_host.Value(objTestOptions.Source().proxy_host.Value());
        objOptions.Source().proxy_port.Value(objTestOptions.Source().proxy_port.Value());
        objOptions.Source().domain.Value(objTestOptions.Source().domain.Value());
        objOptions.Source().transfer_mode.Value(objTestOptions.Source().transfer_mode.Value());
        objOptions.passive_mode.value(objTestOptions.passive_mode.value());
        objOptions.Source().passive_mode.value(true);
        objOptions.SourceDir.Value(objTestOptions.SourceDir.Value());
        objOptions.TargetDir.Value(objTestOptions.TargetDir.Value());
        objOptions.Target().user.Value(objTestOptions.Target().user.Value());
        objOptions.Target().password.Value(objTestOptions.Target().password.Value());
        objOptions.Target().auth_method.Value(objTestOptions.Target().auth_method.Value());
        objOptions.Target().protocol.Value(objTestOptions.Target().protocol.Value());
        objOptions.Target().port.Value(objTestOptions.Target().port.Value());
        objOptions.Target().HostName.Value(objTestOptions.Target().HostName.Value());
        objOptions.Target().passive_mode.value(true);
        objOptions.Target().proxy_host.Value(objTestOptions.Target().proxy_host.Value());
        objOptions.Target().proxy_port.Value(objTestOptions.Target().proxy_port.Value());
        objOptions.Target().domain.Value(objTestOptions.Target().domain.Value());
        objOptions.recursive.value(objTestOptions.recursive.value());
        objOptions.remove_files.value(objTestOptions.remove_files.value());
        objOptions.force_files.Set(objTestOptions.force_files);
        objOptions.overwrite_files.value(objTestOptions.overwrite_files.value());
        objOptions.file_spec.Set(objTestOptions.file_spec);
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
        objOptions.CheckServerFeatures.setTrue();
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
        objOptions.file_spec.Value("^.*$");
        objOptions.file_path.setNotDirty();
        objOptions.SourceDir.Value(objTestOptions.SourceDir.Value());
        objOptions.TargetDir.Value(objTestOptions.TargetDir.Value());
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.CreateSecurityHash.value(false);
        objOptions.KeepModificationDate.setTrue();
        objOptions.HistoryFileName.Value("c:/temp/JADE-history.dat");
        startTransfer(objOptions);
    }

    public void testBigCopyThreaded() throws Exception {
        objOptions.MaxConcurrentTransfers.value(30);
        objOptions.ConcurrentTransfer.value(true);
        testBigCopy();
    }

    public void testCopy() throws Exception {
        final String conMethodName = conClassName + "::testCopy";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        SOSConnection2OptionsAlternate objS = objOptions.getConnectionOptions().Source();
        objS.host.Value(conHostNameWILMA_SOS);
        objS.protocol.Value("ftp");
        objS.user.Value("kb");
        objS.password.Value("kb");
        objOptions.file_path.Value(strTestFileName);
        objOptions.SourceDir.Value("/home/kb/");
        SOSConnection2OptionsAlternate objT = objOptions.getConnectionOptions().Target();
        objT.protocol.Value("local");
        objOptions.TargetDir.Value(strTestPathName);
        objOptions.remote_dir.Value(strTestPathName);
        objOptions.operation.Value(SOSOptionJadeOperation.enuJadeOperations.copy);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testCopyAndCreateVariableFolder() throws Exception {
        setSourceAndTarget();
        CreateTestFiles(10);
        objOptions.file_path.Value("");
        objOptions.FileNamePatternRegExp.Value("^.*\\.txt$");
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.TargetDir.Value(objTestOptions.TargetDir.Value() + "/SAVE[date:yyyyMMddHHmm]");
        objOptions.makeDirs.value(true);
        objOptions.CreateSecurityHash.value(false);
        objOptions.remove_files.value(false);
        objOptions.CreateResultSet.value(true);
        objOptions.ResultSetFileName.Value(strTestPathName + "/Resultset.dat");
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
        objOptions.file_path.Value("");
        objOptions.FileNamePatternRegExp.Value("^.*0000\\d\\.txt$");
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.Source().replacing.Value("(.*)(.txt)");
        objOptions.Source().replacement.Value("SAVE/\\1_[date:yyyyMMddHHmm];\\2");
        objOptions.CreateSecurityHash.value(false);
        objOptions.remove_files.value(false);
        objOptions.CreateResultSet.value(true);
        objOptions.ResultSetFileName.Value(strTestPathName + "/Resultset.dat");
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
        objOptions.file_path.Value("nofile");
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.force_files.value(false);
        startTransfer(objOptions);
    }

    public void testCopyAndRenameSourceAndTarget() throws Exception {
        setSourceAndTarget();
        CreateTestFiles(10);
        objOptions.file_path.Value("");
        objOptions.FileNamePatternRegExp.Value("^Masstest0000\\d\\.txt$");
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.Source().replacing.Value("(.*)(.txt)");
        objOptions.Source().replacement.Value("\\1_[date:yyyyMMddHHmm];\\2");
        objOptions.Target().replacing.Value("(.*)(.txt)");
        objOptions.Target().replacement.Value("\\1_[date:yyyyMMdd];\\2");
        objOptions.replacing.Value("(.*)(.txt)");
        objOptions.replacement.Value("\\1_[date:yyyyMMdd];\\2");
        objOptions.CreateSecurityHash.value(false);
        objOptions.remove_files.value(false);
        objOptions.CreateResultSet.value(true);
        objOptions.ResultSetFileName.Value(strTestPathName + "/Resultset.dat");
        objOptions.history.Value(strTestPathName + "/history.csv");
        startTransfer(objOptions);
    }

    public void testCopyMultipleFiles() throws Exception {
        setSourceAndTarget();
        objOptions.file_path.Value("");
        objOptions.FileNamePatternRegExp.Value("^.*\\.txt$");
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.atomic_suffix.Value(".tmp");
        objOptions.CreateSecurityHash.setFalse();
        startTransfer(objOptions);
    }

    public void testCopyMultipleFilesThreaded() throws Exception {
        objOptions.MaxConcurrentTransfers.value(10);
        objOptions.ConcurrentTransfer.value(true);
        testCopyMultipleFiles();
    }

    public void testCopyMultipleResultList() throws Exception {
        String strFileListName = objOptions.TempDir() + "/FileList.lst";
        objOptions.ResultSetFileName.Value(strFileListName);
        objOptions.CreateResultSet.value(true);
        testCopyMultipleFiles();
        JSFile objF = new JSFile(strFileListName);
        LOGGER.info(objF.File2String());
    }

    public void testCopyWithFileList() throws Exception {
        String strFileListName = objOptions.TempDir() + "/FileList.lst";
        objOptions.FileListName.Value(strFileListName);
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
        objOptions.file_spec.Value("^(subdir|text\\.txt)$");
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.makeDirs.setTrue();
        objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.TargetDir.Value() + "/subdir").isDirectory();
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
        objOptions.file_path.Value("");
        objOptions.force_files.setFalse();
        objOptions.FileNamePatternRegExp.Value("^.*\\.txt$");
        objOptions.operation.Value(enuJadeOperations.delete);
        startTransfer(objOptions);
    }

    public void testDeleteMultipleFilesLocal() throws Exception {
        objOptions.protocol.Value("local");
        objOptions.getConnectionOptions().Source().protocol.Value("local");
        objOptions.getConnectionOptions().Target().protocol.Value("local");
        objOptions.file_path.Value("");
        objOptions.file_spec.Value("^.*\\.txt$");
        objOptions.local_dir.Value(strTestPathName + "/SOSMDX/");
        objOptions.remote_dir.Value(strTestPathName + "/SOSMDX/");
        objOptions.operation.Value("delete");
        objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testDeleteZipFile() throws Exception {
        final String conMethodName = conClassName + "::testDeleteZipFile";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFileName, "-profile=zip_local_files" };
        objOptions.CommandLineArgs(strCmdLineParameters);
        File fleFile = new File(objOptions.remote_dir.Value());
        fleFile.delete();
    }

    public void testExecuteGetFileList() throws Exception {
        setSourceAndTarget();
        CreateTestFiles(10);
        objOptions.file_path.Value("");
        objOptions.FileNamePatternRegExp.Value("^.*\\.txt$");
        objOptions.operation.Value(enuJadeOperations.getlist);
        objOptions.CreateSecurityHash.value(false);
        objOptions.CreateResultSet.value(true);
        startTransfer(objOptions);
    }

    public void testFerberSFtp() throws Exception {
        CreateTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value("85.214.92.170");
        objOptions.port.Value("22");
        objOptions.protocol.Value(enuTransferTypes.sftp);
        objOptions.alternative_host.Value("85.214.92.170");
        objOptions.alternative_port.Value("22");
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.file_path.Value(strTestPathName + strTestFileName);
        objOptions.local_dir.Value(strTestPathName);
        objOptions.operation.Value("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
    }

    public void testGetFileList() throws Exception {
        String strFileListName = objOptions.TempDir() + "/FileList.lst";
        objOptions.ResultSetFileName.Value(strFileListName);
        objOptions.CreateResultSet.value(true);
        testExecuteGetFileList();
        JSFile objF = new JSFile(strFileListName);
        LOGGER.info(objF.File2String());
    }

    public void testHashMapSettings() throws Exception {
        Map<String, String> objHsh = new HashMap<String, String>();
        objHsh.put("operation", "copy");
        objHsh.put("source_host", conHostNameWILMA_SOS);
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
        objOptions.setAllOptions(objOptions.DeletePrefix((HashMap)objHsh, "ftp_"));
        assertEquals("", conHostNameWILMA_SOS, objOptions.getConnectionOptions().Source().host.Value());
        assertEquals("", "tux.sos", objOptions.getConnectionOptions().Target().host.Value());
        assertEquals("", "/srv/www/htdocs/test/", objOptions.TargetDir.Value());
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
        objHsh.put("source_host", conHostNameWILMA_SOS);
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
        assertEquals("", conHostNameWILMA_SOS, objOptions.getConnectionOptions().Source().host.Value());
        assertEquals("", "tux.sos", objOptions.getConnectionOptions().Target().host.Value());
        assertEquals("", "/srv/www/htdocs/test/", objOptions.TargetDir.Value());
        assertEquals("source", strCmd, objOptions.Source().Pre_Command.Value());
        assertEquals("target", strCmd, objOptions.Target().Pre_Command.Value());
        String strT2 = strCmd.replace("$SourceFileName", "testfile");
        assertEquals("target", "SITE chmod 777 testfile", strT2);
    }

    public void testIniFile1() throws Exception {
        final String conMethodName = conClassName + "::CreateIniFile";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.Value(constrSettingsTestFile);
        objOptions.profile.Value("globals");
        assertEquals("User ID", "kb", objOptions.user.Value());
        assertEquals("password", "kb", objOptions.password.Value());
    }

    public void testIniFile2() throws Exception {
        final String conMethodName = conClassName + "::testIniFile2";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.Value(constrSettingsTestFile);
        objOptions.profile.Value("include-TestTest");
        assertEquals("User ID", "kb", objOptions.user.Value());
        assertEquals("password", "kb", objOptions.password.Value());
    }

    public void testIniFile3() throws Exception {
        final String conMethodName = conClassName + "::testIniFile2";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.Value(constrSettingsTestFile);
        objOptions.profile.Value("include-Test");
        objOptions.local_dir.Value(".");
        assertEquals("User ID", "kb", objOptions.user.Value());
        assertEquals("password", "kb", objOptions.password.Value());
        assertEquals("Hostname", "hostFromInclude1", objOptions.host.Value());
        assertEquals("port", 88, objOptions.port.value());
        assertEquals("protocol", "scp", objOptions.protocol.Value());
        objOptions.CheckMandatory();
    }

    public void testIniFile4() throws Exception {
        final String conMethodName = conClassName + "::testIniFile2";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.Value(constrSettingsTestFile);
        objOptions.profile.Value("include-TestWithNonexistenceInclude");
        Assert.assertEquals("User ID", "kb", objOptions.user.Value());
        Assert.assertEquals("password", "kb", objOptions.password.Value());
    }

    public void testKeePass1() throws Exception {
        final String conMethodName = conClassName + "::testIniFile2";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        objOptions.settings.Value(strSettingsFile);
        objOptions.profile.Value("ReceiveUsingKeePass");
        testUseProfileWOCreatingTestFiles();
        assertEquals("User ID", "test", objOptions.Source().user.Value());
        assertEquals("password", "12345", objOptions.Source().password.Value());
    }

    public void testIniFile5() throws Exception {
        final String conMethodName = conClassName + "::testIniFile5";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.Value(constrSettingsTestFile);
        objOptions.profile.Value("substitute-Test");
        String strComputerName = System.getenv("computername");
        Assert.assertEquals("User ID", System.getenv("username"), objOptions.user.Value());
        Assert.assertEquals("Hostname", strComputerName, objOptions.host.Value());
        Assert.assertEquals("Hostnameon Target ", strComputerName + "-abc", objOptions.getConnectionOptions().Target().HostName.Value());
    }

    public void testIniFileWithSourceAndTarget() throws Exception {
        final String conMethodName = conClassName + "::testIniFile5";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        CreateIniFile();
        objOptions.settings.Value(strSettingsFile);
        objOptions.profile.Value("ftp_server_2_server");
        SOSConnection2Options objConn = objOptions.getConnectionOptions();
        String strComputerName = System.getenv("computername");
        Assert.assertEquals("Source.Host", conHostNameWILMA_SOS, objConn.Source().host.Value());
        Assert.assertEquals("Target.Host", conHostName8OF9_SOS, objConn.Target().host.Value());
        Assert.assertEquals("file_path", "test.txt", objOptions.file_path.Value());
        objOptions.CheckMandatory();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
    }

    public void testParameterPriority() throws Exception {
        final String conMethodName = conClassName + "::testParameterPriority";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFileName, "-profile=zip_local_files_2", "-operation=receive" };
        objOptions.CommandLineArgs(strCmdLineParameters);
        String strOperation = objOptions.operation.Value();
        assertEquals("Operation not overwritten", "receive", strOperation);
        assertEquals("source protocol", "local", objOptions.getConnectionOptions().Source().protocol.Value());
        assertEquals("source dir", strTestPathName, objOptions.SourceDir.Value());
        assertEquals("Operation not overwritten", "receive", strOperation);
    }

    public void testParameterPriority2() throws Exception {
        final String conMethodName = conClassName + "::testParameterPriority";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFileName, "-profile=zip_local_files", "-operation=getFileList" };
        objOptions.CommandLineArgs(strCmdLineParameters);
        String strOperation = objOptions.operation.Value();
        assertEquals("Precedence test failed", "getFileList", strOperation);
    }

    public void testReceive() throws Exception {
        final String conMethodName = conClassName + "::testReceive";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        objOptions.host.Value(conHostNameWILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.file_path.Value(strTestFileName);
        objOptions.remote_dir.Value("/home/kb/");
        objOptions.local_dir.Value(strTestPathName);
        objOptions.operation.Value("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testReceive2() throws Exception {
        final String conMethodName = conClassName + "::testReceive";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        objOptions.host.Value(conHostName8OF9_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.file_path.Value(strTestFileName);
        objOptions.remote_dir.Value("/kb/");
        objOptions.local_dir.Value(strTestPathName);
        objOptions.operation.Value("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testReceiveFileWithRelativeSourceDir() throws Exception {
        setSourceAndTarget();
        objOptions.file_spec.Value("\\.txt$");
        objOptions.transactional.setTrue();
        objOptions.atomic_suffix.Value("~");
        objOptions.operation.Value("receive");
        objOptions.force_files.value(false);
        objOptions.passive_mode.value(true);
        setMailOptions();
        LOGGER.info(objOptions.dirtyString());
        objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        checkFilesOnTarget(objJadeEngine.getFileList());
        objJadeEngine.Logout();
    }

    public void testReceiveMultipleFiles() throws Exception {
        objOptions.host.Value(conHostNameWILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.file_path.Value("");
        objOptions.file_spec.Value("^.*\\.txt$");
        objOptions.local_dir.Value(strTestPathName);
        objOptions.remote_dir.Value("/home/kb/");
        objOptions.append_files.value(false);
        objOptions.operation.Value("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.getFileList().SuccessfulTransfers();
        for (SOSFileListEntry objListItem : objJadeEngine.getFileList().List()) {
            String strF = MakeFullPathName(objOptions.TargetDir.Value(), objListItem.TargetFileName());
            boolean flgResult = objListItem.getDataTargetClient().getFileHandle(strF).FileExists();
            Assert.assertTrue("File " + strF + " exist, but should not", flgResult);
        }
        objJadeEngine.Logout();
    }

    public void testReceiveMultipleFiles2() throws Exception {
        objOptions.host.Value(conHostNameWILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.file_path.Value("");
        objOptions.file_spec.Value("^.*\\.txt$");
        objOptions.local_dir.Value(strTestPathName);
        objOptions.remote_dir.Value("/home/kb/");
        objOptions.append_files.value(false);
        objOptions.operation.Value("receive");
        objOptions.CheckServerFeatures.setTrue();
        objOptions.ControlEncoding.Value("UTF-8");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        long intNoOfFilesTransferred = objJadeEngine.getFileList().SuccessfulTransfers();
        for (SOSFileListEntry objListItem : objJadeEngine.getFileList().List()) {
            String strF = MakeFullPathName(objOptions.TargetDir.Value(), objListItem.TargetFileName());
            boolean flgResult = objListItem.getDataTargetClient().getFileHandle(strF).FileExists();
            Assert.assertTrue("File " + strF + " exist, but should not", flgResult);
        }
        objJadeEngine.Logout();
    }

    public void testReceiveSFTP() throws Exception {
        final String conMethodName = conClassName + "::testReceiveSFTP";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        objOptions.host.Value(conHostNameWILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.protocol.Value(enuTransferTypes.sftp.Text());
        objOptions.port.Value("22");
        objOptions.password.Value("kb");
        objOptions.file_path.Value("sosdex.txt");
        objOptions.remote_dir.Value("/home/sos/tmp");
        objOptions.ssh_auth_method.Value(enuAuthenticationMethods.password);
        objOptions.BufferSize.value(1024);
        objOptions.local_dir.Value(strTestPathName);
        objOptions.operation.Value("receive");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testReceiveUsingEmptyReplacement() throws Exception {
        final String conMethodName = conClassName + "::testReceiveUsingEmptyReplacement";
        LOGGER.info("********************************************** " + conMethodName + "******************");
        HashMap<String, String> objHsh = new HashMap<String, String>();
        objHsh.put("ftp_host", conHostNameWILMA_SOS);
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
        objOptions.file_spec.Value("^.*\\.txt$");
        objOptions.SourceDir.Value("/tmp/test/symlink2home.test.temp/test");
        objOptions.TargetDir.Value(strTestPathName);
        objOptions.operation.Value(enuJadeOperations.copy);
        LOGGER.debug(objOptions.DirtyString());
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.TargetDir.Value() + "/" + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    public void testReceiveWithUmlaut() throws Exception {
        final String conMethodName = conClassName + "::testReceive";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        objOptions.host.Value(conHostNameWILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.file_path.Value("Büttner.dat");
        objOptions.remote_dir.Value("/home/kb/");
        objOptions.local_dir.Value(strTestPathName);
        objOptions.operation.Value("receive");
        objOptions.ControlEncoding.Value("UTF-8");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testReceiveWithUmlautFromLocalhost() throws Exception {
        final String conMethodName = conClassName + "::testReceive";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        objOptions.host.Value("localhost");
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.file_spec.Value(".*ttner\\..*");
        objOptions.remote_dir.Value("/");
        objOptions.local_dir.Value(strTestPathName);
        objOptions.operation.Value("receive");
        objOptions.PreFtpCommands.Value("OPTS UTF8 ON");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testRenameFiles() throws Exception {
        final String conMethodName = conClassName + "::testRenameFiles";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        String strTestDir = objTestOptions.SourceDir.Value();
        renameFiles(strTestDir, "^.*\\d\\.txt$", "moved/[filename:]");
    }

    public void testRenameFiles2FolderWhichNotExist() throws Exception {
        final String conMethodName = conClassName + "::testRenameFiles";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        String strTestDir = objTestOptions.SourceDir.Value();
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
        objOptions.file_spec.Value("^.*\\.txt$");
        objOptions.local_dir.Value(strTestPathName + "/SOSMDX/");
        objOptions.remote_dir.Value(strTestPathName + "/SOSMDX/");
        objOptions.operation.Value("rename");
        objOptions.replacing.Value("(.*)(.txt)");
        objOptions.replacement.Value("\\1_[date:yyyyMMddHHmm];\\2");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testRenameOnSourceOnly4FTP() throws Exception {
        final String conMethodName = conClassName + "::testRenameOnSourceOnly4FTP";
        LOGGER.info("********************************************** " + conMethodName + "******************");
        HashMap<String, String> objHsh = new HashMap<String, String>();
        objHsh.put("source_host", conHostNameWILMA_SOS);
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
        final String conMethodName = conClassName + "::testRenameOnSourceOnly4SFTP";
        LOGGER.info("********************************************** " + conMethodName + "******************");
        HashMap<String, String> objHsh = new HashMap<String, String>();
        objHsh.put("source_host", conHostNameWILMA_SOS);
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
        objOptions.host.Value(conHostNameWILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.file_path.Value("");
        objOptions.file_spec.Value("^.*\\.txt$");
        objOptions.local_dir.Value(strTestPathName);
        objOptions.remote_dir.Value("/home/kb/");
        objOptions.append_files.value(false);
        objOptions.operation.Value("receive");
        objOptions.CreateResultSet.value(true);
        String strResultSetFileName = objOptions.TempDir() + "/ResultSetFile.dat";
        objOptions.ResultSetFileName.Value(strResultSetFileName);
        startTransfer(objOptions);
    }

    public void testSend() throws Exception {
        final String conMethodName = conClassName + "::testSend";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        setSourceAndTarget();
        CreateTestFile();
        objOptions.file_path.Value(strTestFileName);
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.log_filename.Value(objOptions.TempDir() + "test.log");
        objOptions.profile.Value(conMethodName);
        objOptions.VerbosityLevel.value(2);
        objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        checkFilesOnTarget(objJadeEngine.getFileList());
        objJadeEngine.Logout();
    }

    public void testSend2() throws Exception {
        CreateTestFile();
        objOptions.host.Value(conHostName8OF9_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.file_path.Value(strTestPathName + strTestFileName);
        objOptions.local_dir.Value(strTestPathName);
        objOptions.remote_dir.Value("/kb/");
        objOptions.operation.Value("send");
        objOptions.passive_mode.value(true);
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
        objOptions.file_spec.Value("^[0-9]{10}\\.pdf$");
        objOptions.transactional.setTrue();
        objOptions.atomic_suffix.Value(".tmp");
        objOptions.operation.Value("copy");
        objOptions.passive_mode.value(true);
        LOGGER.info(objOptions.dirtyString());
        objOptions.getConnectionOptions().Target().ProtocolCommandListener.setTrue();
        startTransfer(objOptions);
        checkFilesOnTarget(objJadeEngine.getFileList());
        objJadeEngine.Logout();
        strTestFileName = strSaveTestfileName;
    }

    public void testSend5() throws Exception {
        final String conMethodName = conClassName + "::testSend";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value(conHostNameWILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.file_path.Value(strTestFileName);
        objOptions.local_dir.Value(strTestPathName);
        objOptions.remote_dir.Value(strKBHome);
        objOptions.operation.Value("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(strKBHome + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    public void testSend6() throws Exception {
        final String conMethodName = conClassName + "::testSend";
        boolean flgResult = true;
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value(conHostNameWILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.remote_dir.Value(strKBHome);
        objOptions.file_path.Value(strTestFileName);
        objOptions.local_dir.Value(strTestPathName);
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
        objOptions.file_path.Value("");
        objOptions.file_spec.Value("^.*\\.txt$");
        objOptions.local_dir.Value(strTestPathName);
        objOptions.operation.Value(enuJadeOperations.send);
        objOptions.DeleteFilesAfterTransfer.setTrue();
        objOptions.log_filename.Value(objOptions.TempDir() + "/test.log");
        objOptions.auth_method.Value(enuAuthenticationMethods.password);
        startTransfer(objOptions);
        for (SOSFileListEntry objListItem : objJadeEngine.getFileList().List()) {
            String strF = objListItem.SourceFileName();
            boolean flgResult = objListItem.getDataSourceClient().getFileHandle(strF).FileExists();
            Assert.assertFalse("File " + strF + " exist, but should not", flgResult);
        }
        objJadeEngine.Logout();
    }

    public void testSendComand() throws Exception {
        final String conMethodName = conClassName + "::testSendComand";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFile();
        objOptions.host.Value(conHostNameWILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.file_path.Value(strTestFileName);
        objOptions.local_dir.Value(strTestPathName);
        objOptions.remote_dir.Value(strKBHome);
        objOptions.operation.Value(enuJadeOperations.send);
        setOptions4BackgroundService();
        objOptions.SendTransferHistory.value(false);
        objOptions.Target().Post_Command.Value("SITE CHMOD 777 $TargetFileName");
        objOptions.Source().Pre_Command.Value("dir $SourceFileName");
        startTransfer(objOptions);
    }

    public void testSendCommandAfterReplacing() throws Exception {
        final String conMethodName = conClassName + "::testSendComand2";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFile();
        objOptions.host.Value(conHostNameWILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.file_path.Value(strTestFileName);
        objOptions.local_dir.Value(strTestPathName);
        objOptions.remote_dir.Value(strKBHome);
        objOptions.operation.Value(enuJadeOperations.send);
        objOptions.Target().setloadClassName("com.sos.VirtualFileSystem.SFTP.SOSVfsSFtpJCraft");
        objOptions.Target().protocol.Value(enuTargetTransferType);
        objOptions.Target().port.value(SOSOptionPortNumber.conPort4SFTP);
        objOptions.ssh_auth_method.Value(enuAuthenticationMethods.password);
        objOptions.Target().replacing.Value(".*");
        objOptions.Target().replacement.Value("[filename:uppercase]_[date:yyyMMddHHmmss]");
        objOptions.Source().PreTransferCommands.Value("echo PreTransferCommands on Source; echo ${source_dir}");
        objOptions.Source().PostTransferCommands.Value("echo PostTransferCommands on Source; echo ${source_dir}");
        objOptions.Source().Pre_Command.Value("echo SourcePreCommand: $SourceTransferFileName + $SourceFileName");
        objOptions.Source().Post_Command.Value("echo SourcePostCommand: $SourceTransferFileName + $SourceFileName");
        objOptions.Source().TFN_Post_Command.Value("echo SourceTFNPostCommand $SourceTransferFileName + $SourceFileName");
        objOptions.Target().PreTransferCommands.Value("echo PreTransferCommands on Target; pwd");
        objOptions.Target().PostTransferCommands.Value("echo PostTransferCommands on Target; pwd");
        objOptions.Target().Pre_Command.Value("echo TargetPreCommand $TargetTransferFileName + $TargetFileName");
        objOptions.Target().Post_Command.Value("echo TargetPostCommand $TargetTransferFileName + $TargetFileName; rm $TargetFileName");
        objOptions.Target().TFN_Post_Command.Value("echo TargetTFNPostCommand $TargetTransferFileName + $TargetFileName");
        objOptions.Target().ProtocolCommandListener.value(true);
        startTransfer(objOptions);
    }

    public void testSendFileNameWithUmlaut() throws Exception {
        final String conMethodName = conClassName + "::testSend";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        String strFileName = "Büttner.dat";
        CreateTestFile(strFileName);
        objOptions = new JADEOptions();
        objOptions.protocol.Value(enuTransferTypes.ftps);
        objOptions.host.Value("localhost");
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.file_path.Value(strFileName);
        objOptions.local_dir.Value(strTestPathName);
        objOptions.operation.Value("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.getTargetClient().getHandler().ExecuteCommand("OPTS UTF8 ON");
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(strFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    public void testSendFileSpec() throws Exception {
        final String conMethodName = conClassName + "::testSend";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        setSourceAndTarget();
        CreateTestFile();
        objOptions.file_spec.Value("[0-9]{4}_(UR_RS|GZ_RS|LSTG|PZEP1)\\.txt");
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.profile.Value(conMethodName);
        objOptions.replacing.Value("([0-9]{4}_)(UR_RS|GZ_RS|LSTG|PZEP1)(\\.txt)");
        objOptions.replacement.Value("luebbenau.\1;\2;\3;");
        objOptions.force_files.value(false);
        objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        checkFilesOnTarget(objJadeEngine.getFileList());
        objJadeEngine.Logout();
    }

    public void testSendFileSpec2() throws Exception {
        final String conMethodName = conClassName + "::testSendFileSpec2";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        setSourceAndTarget();
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.force_files.value(false);
        objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testSendFtp2SFtp() throws Exception {
        final String conMethodName = conClassName + "::testSendFtp2SFtp";
        CreateTestFile();
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        SOSConnection2Options objConn = objOptions.getConnectionOptions();
        SOSConnection2OptionsAlternate objS = objConn.Source();
        objS.HostName.Value(conHostName8OF9_SOS);
        objS.port.value(SOSOptionPortNumber.getStandardFTPPort());
        objS.protocol.Value("ftp");
        objS.user.Value("sos");
        objS.password.Value("sos");
        objOptions.local_dir.Value("/");
        SOSConnection2OptionsAlternate objT = objConn.Target();
        objT.HostName.Value(conHostNameWILMA_SOS);
        objT.port.value(SOSOptionPortNumber.getStandardSFTPPort());
        objT.ssh_auth_method.isPassword(true);
        objT.protocol.Value("sftp");
        objT.user.Value("test");
        objT.password.Value("12345");
        String strTestDir = "/home/test/";
        objOptions.remote_dir.Value(strTestDir);
        objOptions.TargetDir.Value(strTestDir);
        strTestFileName = "wilma.sh";
        objOptions.file_path.Value(strTestFileName);
        objOptions.operation.Value("copy");
        objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        checkFilesOnTarget(objJadeEngine.getFileList());
        objJadeEngine.Logout();
    }

    public void testSendHugeNumberOfFiles() throws Exception {
        final String conMethodName = conClassName + "::testSendHugeNumberOfFiles";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFiles(500);
        objOptions.host.Value(conHostNameWILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.file_path.Value("");
        objOptions.file_spec.Value("^.*\\.ttxt$");
        objOptions.local_dir.Value(strTestPathName);
        objOptions.operation.Value("send");
        objOptions.ConcurrentTransfer.value(true);
        objOptions.MaxConcurrentTransfers.value(10);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testSendMultipleFiles() throws Exception {
        final String conMethodName = conClassName + "::testSendMultipleFiles";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        objOptions.host.Value(conHostNameWILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.file_path.Value("");
        objOptions.file_spec.Value("^.*\\.txt$");
        objOptions.local_dir.Value(strTestPathName);
        objOptions.operation.Value("send");
        setOptions4BackgroundService();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testSendMultipleFilesLocal2LocalAtomic() throws Exception {
        objOptions.protocol.Value("local");
        objOptions.file_path.Value("");
        objOptions.file_spec.Value("^.*\\.txt$");
        objOptions.local_dir.Value(strTestPathName);
        objOptions.remote_dir.Value(strTestPathName + "/SOSMDX/");
        objOptions.operation.Value("send");
        objOptions.atomic_suffix.Value("~");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testSendMultipleZIPedFilesLocal2Local() throws Exception {
        objOptions.protocol.Value("local");
        objOptions.file_path.Value("");
        objOptions.file_spec.Value("^.*\\.txt$");
        objOptions.local_dir.Value(strTestPathName);
        objOptions.remote_dir.Value(strTestPathName + "/SOSMDX/");
        objOptions.operation.Value("send");
        objOptions.compress_files.value(true);
        objOptions.compressed_file_extension.Value(".zip");
        objOptions.ConcurrentTransfer.value(true);
        objOptions.MaxConcurrentTransfers.value(5);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    public void testSendRegExpAsFileName() throws Exception {
        final String conMethodName = conClassName + "::testSendRegExpAsFileName";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        strTestFileName = "test.txt";
        CreateTestFile(strTestFileName);
        setSourceAndTarget();
        objOptions.file_spec.Value(strTestFileName);
        objOptions.local_dir.Value(strTestPathName);
        objOptions.operation.Value("send");
        objOptions.verbose.value(9);
        startTransfer(objOptions);
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.TargetDir.Value() + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    public void testSendServer2Server() throws Exception {
        final String conMethodName = conClassName + "::testSendServer2Server";
        CreateTestFile();
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        SOSConnection2Options objConn = objOptions.getConnectionOptions();
        objConn.Source().HostName.Value(conHostNameWILMA_SOS);
        objConn.Source().port.value(21);
        objConn.Source().protocol.Value(SOSOptionTransferType.enuTransferTypes.ftp);
        objConn.Source().user.Value("kb");
        objConn.Source().password.Value("kb");
        objConn.Target().HostName.Value(conHostName8OF9_SOS);
        objConn.Target().port.value(21);
        objConn.Target().protocol.Value(SOSOptionTransferType.enuTransferTypes.ftp);
        objConn.Target().user.Value("kb");
        objConn.Target().password.Value("kb");
        objOptions.file_path.Value(strTestFileName);
        objOptions.SourceDir.Value("/home/kb");
        objOptions.TargetDir.Value("/kb");
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.CheckMandatory();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        checkFilesOnTarget(objJadeEngine.getFileList());
        objJadeEngine.Logout();
    }

    public void testSendServer2ServerMultiple() throws Exception {
        final String conMethodName = conClassName + "::testSendServer2Server";
        CreateTestFile();
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        SOSConnection2Options objConn = objOptions.getConnectionOptions();
        objConn.Source().HostName.Value(conHostNameWILMA_SOS);
        objConn.Source().port.value(21);
        objConn.Source().protocol.Value(SOSOptionTransferType.enuTransferTypes.ftp);
        objConn.Source().user.Value("kb");
        objConn.Source().password.Value("kb");
        objConn.Target().HostName.Value(conHostName8OF9_SOS);
        objConn.Target().port.value(21);
        objConn.Target().protocol.Value(SOSOptionTransferType.enuTransferTypes.ftp);
        objConn.Target().user.Value("kb");
        objConn.Target().password.Value("kb");
        objOptions.file_spec.Value("^.*\\.txt$");
        objOptions.SourceDir.Value("/home/kb");
        objOptions.TargetDir.Value("/kb");
        objOptions.operation.Value("copy");
        objOptions.CheckMandatory();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle(objOptions.TargetDir.Value() + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    public void testSendToAlternateHost() throws Exception {
        final String conMethodName = conClassName + "::testSendToAlternateHost";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value("xwilma.sos");
        objOptions.alternative_host.Value(conHostNameWILMA_SOS);
        objOptions.alternative_port.Value("21");
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.file_path.Value(strTestPathName + strTestFileName);
        objOptions.local_dir.Value(strTestPathName);
        objOptions.operation.Value("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/" + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    public void testSendUsingEmptyReplacement() throws Exception {
        final String conMethodName = conClassName + "::testSendUsingEmptyReplacement";
        LOGGER.info("********************************************** " + conMethodName + "******************");
        sendUsingReplacement("^t", "");
    }

    public void testSendUsingRelativeLocalDir() throws Exception {
        final String conMethodName = conClassName + "::testSendUsingRelativeLocalDir";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value(conHostNameWILMA_SOS);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.remote_dir.Value("./relative");
        objOptions.file_path.Value(strTestFileName);
        objOptions.local_dir.Value(strTestPathName);
        objOptions.operation.Value("send");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/relative/" + strTestFileName).FileExists();
        assertTrue("File must exist", flgResult);
        objJadeEngine.Logout();
    }

    public void testSendUsingReplacement() throws Exception {
        final String conMethodName = conClassName + "::testSendUsingReplacement";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        sendUsingReplacement("^t", "a");
    }

    public void testSendUsingReplacement2() throws Exception {
        final String conMethodName = conClassName + "::testSendUsingReplacement2";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        sendUsingReplacement(".*", "renamed_[filename:]");
    }

    public void testSendUsingReplacement3() throws Exception {
        final String conMethodName = conClassName + "::testSendUsingReplacement3";
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
        objOptions.CheckMandatory();
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
        objOptions.CheckMandatory();
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
        final String conMethodName = conClassName + "::testSendWithPrePostCommands";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFile();
        objOptions = new JADEOptions();
        objOptions.host.Value(conHostNameWILMA_SOS);
        objOptions.protocol.Value(enuTransferTypes.sftp);
        assertEquals("sftp", "sftp", objOptions.protocol.Value());
        objOptions.port.value(SOSOptionPortNumber.conPort4SSH);
        objOptions.user.Value("kb");
        objOptions.password.Value("kb");
        objOptions.auth_method.Value(enuAuthenticationMethods.password);
        objOptions.file_spec.Value("^.*\\.txt$");
        objOptions.local_dir.Value(strTestPathName);
        objOptions.operation.Value("send");
        objOptions.log_filename.Value("c:/temp/test.log");
        objOptions.profile.Value(conMethodName);
        objOptions.PreFtpCommands.Value("rm -f t.1");
        objOptions.Target().Post_Command.Value("echo 'File: $TargetFileName' >> t.1;cat $TargetFileName >> t.1;rm -f $TargetFileName");
        objOptions.Target().Pre_Command.Value("touch $TargetFileName");
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
        boolean flgResult = objJadeEngine.getTargetClient().getFileHandle("/home/kb/" + strTestFileName).FileExists();
        assertFalse("File must not exist", flgResult);
        objJadeEngine.Logout();
    }

    public void testSendWrongFileSpec() throws Exception {
        final String conMethodName = conClassName + "::testSendWrongFileSpec";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        setSourceAndTarget();
        CreateTestFile();
        objOptions.file_spec.Value("[0-9]{4}_(UR_RS|GZ_RS|LSTG|PZEP1)\\.a1b2cw");
        objOptions.operation.Value(enuJadeOperations.copy);
        objOptions.profile.Value(conMethodName);
        objOptions.replacing.Value("([0-9]{4}_)(UR_RS|GZ_RS|LSTG|PZEP1)(\\.txt)");
        objOptions.replacement.Value("luebbenau.\1;\2;\3;");
        startTransfer(objOptions);
    }

    public void testTransferUsingFilePath() throws Exception {
        setSourceAndTarget();
        objOptions.file_path.Value(gstrFilePath);
        objOptions.transactional.setTrue();
        objOptions.atomic_suffix.Value(".tmp");
        objOptions.operation.Value("copy");
        objOptions.passive_mode.value(true);
        objOptions.verbose.value(2);
        LOGGER.info(objOptions.dirtyString());
        if (objJadeEngine == null) {
            objJadeEngine = new JadeEngine(objOptions);
        }
        objJadeEngine.Execute();
        checkFilesOnTarget(objJadeEngine.getFileList());
    }

    public void testUseProfile() throws Exception {
        final String conMethodName = conClassName + "::testUseProfile";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        CreateTestFiles(10);
        testUseProfile2();
    }

    public void testUseProfile2() throws Exception {
        final String conMethodName = conClassName + "::testUseProfile2";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        LOGGER.info(objOptions.dirtyString());
        startTransfer(objOptions);
    }

    public void testUseProfileWithoutCreatingTestFiles() throws Exception {
        final String conMethodName = conClassName + "::testUseProfileWithoutCreatingTestFiles";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        startTransfer(objOptions);
    }

    public void testUseProfileWOCreatingTestFiles() throws Exception {
        final String conMethodName = conClassName + "::testUseProfile";
        LOGGER.info("******************************************\n***** " + conMethodName + "\n******************");
        startTransfer(objOptions);
    }

    public void testZipExtraction() throws Exception {
        final String conMethodName = conClassName + "::testZipExtraction";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFileName, "-profile=zip_extract_2_local_files" };
        objOptions.CommandLineArgs(strCmdLineParameters);
        objOptions.SendTransferHistory.value(false);
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
    }

    public void testZipOperation() throws Exception {
        final String conMethodName = conClassName + "::testZipOperation";
        LOGGER.info("*********************************************** " + conMethodName + "******************");
        String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFileName, "-profile=zip_local_files" };
        objOptions.CommandLineArgs(strCmdLineParameters);
        objOptions.SendTransferHistory.value(false);
        boolean flgOK = new JSFile(objOptions.TargetDir.Value()).delete();
        JadeEngine objJadeEngine = new JadeEngine(objOptions);
        objJadeEngine.Execute();
    }
}
