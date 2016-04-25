package com.sos.DataExchange;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Options.SOSOptionAuthenticationMethod.enuAuthenticationMethods;
import com.sos.JSHelper.Options.SOSOptionJadeOperation.enuJadeOperations;
import com.sos.JSHelper.Options.SOSOptionPortNumber;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;
import com.sos.JSHelper.io.Files.JSCsvFile;
import com.sos.VirtualFileSystem.exceptions.JADEException;

public class JadeTestsFtpAsSource extends JadeTestBase {

    public JadeTestsFtpAsSource() {
        enuSourceTransferType = enuTransferTypes.sftp;
        enuTargetTransferType = enuTransferTypes.local;
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        enuSourceTransferType = enuTransferTypes.sftp;
        enuTargetTransferType = enuTransferTypes.local;
        objTestOptions.TargetDir.Value(strTestPathName);
        objTestOptions.Target().protocol.Value(enuTargetTransferType);
        objTestOptions.Target().user.Value(USER_ID_TEST);
        objTestOptions.Target().password.Value(PASSWORD_TEST);
        objTestOptions.Target().host.Value("local");
        objTestOptions.Target().protocol.Value(enuTargetTransferType);
        objTestOptions.Source().protocol.Value(enuSourceTransferType);
        objTestOptions.Target().protocol.Value(enuTargetTransferType);
        objTestOptions.SourceDir.Value("/home/test/jadetest/SOSDEX");
        objTestOptions.Source().host.Value(HOST_NAME_WILMA_SOS);
        objTestOptions.Source().port.value(SOSOptionPortNumber.conPort4SFTP);
        objTestOptions.Source().user.Value(USER_ID_TEST);
        objTestOptions.Source().password.Value(PASSWORD_TEST);
        objTestOptions.Source().auth_method.Value(enuAuthenticationMethods.password);
        objOptions.profile.Value(CLASS_NAME);
    }

    @Override
    @Test
    public void testReceiveWithSymlinkInRemoteDir() throws Exception {
        super.testReceiveWithSymlinkInRemoteDir();
    }

    @Override
    @Test
    public void testUseProfileWithoutCreatingTestFiles() throws Exception {
        objOptions.settings.Value(strSettingsFile);
        objOptions.profile.Value("getList_example_sftp");
        super.testUseProfileWithoutCreatingTestFiles();
    }

    @Override
    @Test
    public void testTransferUsingFilePath() throws Exception {
        gstrFilePath = "myfile_20120801.csv";
        objTestOptions.SourceDir.Value("/home/test/tmp/");
        super.testTransferUsingFilePath();
    }

    @Test
    public void testTransferUsingAbsolutFilePath() throws Exception {
        gstrFilePath = "/home/test/tmp/myfile_20120801.csv";
        objTestOptions.SourceDir.Value("");
        super.testTransferUsingFilePath();
    }

    @Test
    public void testTransferUsingAbsolutFilePath2() throws Exception {
        gstrFilePath = "/home/test/tmp/myfile_20120801.csv";
        super.testTransferUsingFilePath();
    }

    @Test
    public void testTransferUsingRelativeFilePath() throws Exception {
        gstrFilePath = "./tmp/myfile_20120801.csv";
        objTestOptions.SourceDir.Value("");
        objOptions.profile.Value("testTransferUsingRelativeFilePath");
        super.testTransferUsingFilePath();
    }

    @Test
    public void testTransferUsingRelativeFilePath2() throws Exception {
        objTestOptions.SourceDir.Value("");
        gstrFilePath = "tmp/myfile_20120801.csv";
        super.testTransferUsingFilePath();
    }

    @Override
    @Test
    public void testCopyMultipleFiles() throws Exception {
        super.testCopyMultipleFiles();
    }

    @Override
    @Test
    public void testDeleteFiles2() throws Exception {
        super.testDeleteFiles2();
    }

    @Override
    @Test
    public void testCopyMultipleFilesThreaded() throws Exception {
        super.testCopyMultipleFilesThreaded();
    }

    @Override
    @Test
    public void testBigCopyThreaded() throws Exception {
        this.testBigCopy();
    }

    @Override
    @Test
    public void testCopyWithFileList() throws Exception {
        super.testCopyWithFileList();
    }

    @Override
    @Test
    public void testBigCopy() throws Exception {
        //
    }

    @Test
    public void testBigCopy2() throws Exception {
        //
    }

    @Override
    @Test
    public void testCopyMultipleResultList() throws Exception {
        super.testCopyMultipleResultList();
    }

    @Override
    @Test
    public void testSendAndDeleteMultipleFiles() throws Exception {
        super.testSendAndDeleteMultipleFiles();
    }

    @Override
    @Test
    public void testRenameFiles() throws Exception {
        super.testRenameFiles();
    }

    @Override
    @Test
    public void testSend() throws Exception {
        super.testSend();
    }

    @Test
    public void testSendWithHistoryAndBackgroundServiceAndEmptyHostUser() throws Exception {
        objTestOptions.Target().user.Value("");
        objTestOptions.Target().host.Value("");
        JSCsvFile csvFile = new JSCsvFile("R:/nobackup/junittests/testdata/JADE/history_files/historyWithEmptyHost.csv");
        if (csvFile.exists()) {
            csvFile.delete();
        }
        objOptions.HistoryFileName.Value(csvFile.getAbsolutePath());
        setOptions4BackgroundService();
        super.testSend();
        String[] strValues = null;
        csvFile.loadHeaders();
        String[] strHeader = csvFile.Headers();
        String remote_host = "";
        String remote_host_ip = "";
        String remote_user = "";
        strValues = csvFile.readCSVLine();
        for (int j = 0; j < strValues.length; j++) {
            if ("remote_host".equals(strHeader[j])) {
                remote_host = strValues[j];
            }
            if ("remote_host_ip".equals(strHeader[j])) {
                remote_host_ip = strValues[j];
            }
            if ("remote_user".equals(strHeader[j])) {
                remote_user = strValues[j];
            }
        }
        csvFile.close();
        assertTrue("remote_host is empty", remote_host.length() > 0);
        assertTrue("remote_user is empty", remote_user.length() > 0);
        assertTrue("remote_host_ip is empty", remote_host_ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}"));
    }

    @Override
    @Test
    public void testSendFileSpec() throws Exception {
        super.testSendFileSpec();
    }

    @Override
    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    public void testSendWithPolling0Files() throws Exception {
        super.testSendWithPolling0Files();
    }

    @Override
    @Test
    public void testCopyWithFolderInSourceDir() throws Exception {
        super.testCopyWithFolderInSourceDir();
    }

    @Test
    public void testCopyRecursiveWithFolderInSourceDir() throws Exception {
        objTestOptions.recursive.value(true);
        super.testCopyWithFolderInSourceDir();
        objTestOptions.recursive.setFalse();
    }

    @Override
    @Test
    public void testDeleteFiles() throws Exception {
        objTestOptions.FileNameRegExp.Value("^Masstest.\\.txt$");
        super.testDeleteFiles();
    }

    @Test
    public void testDeleteFilesWithForce() throws Exception {
        objTestOptions.ErrorOnNoDataFound.push();
        objTestOptions.ErrorOnNoDataFound.setFalse();
        objTestOptions.FileNameRegExp.Value("^Masstest.\\.txt$");
        super.testDeleteFiles();
        objTestOptions.ErrorOnNoDataFound.pop();
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    public void testDeleteFilesWithError() throws Exception {
        objTestOptions.ErrorOnNoDataFound.push();
        objTestOptions.ErrorOnNoDataFound.setTrue();
        objTestOptions.FileNameRegExp.Value("^Masstest.\\.txt$");
        super.testDeleteFiles();
        objTestOptions.ErrorOnNoDataFound.pop();
    }

    @Test
    public void testSFTPReceive() throws Exception {
        JADEOptions objTestOptions = new JADEOptions();
        objTestOptions.operation.Value(enuJadeOperations.receive);
        objTestOptions.host.Value("homer.sos");
        objTestOptions.user.Value("test");
        objTestOptions.password.Value("12345");
        objTestOptions.port.value(SOSOptionPortNumber.conPort4SFTP);
        objTestOptions.protocol.Value(enuSourceTransferType);
        objTestOptions.auth_method.Value(enuAuthenticationMethods.password);
        objTestOptions.remote_dir.Value("/tmp/test/jade/out");
        objTestOptions.local_dir.Value("\\\\8of9\\c\\tmp\\sftpreceive");
        objTestOptions.file_spec.Value(".*");
        objTestOptions.recursive.value(true);
        objTestOptions.verbose.value(9);
        JadeEngine objJadeEngine = new JadeEngine(objTestOptions);
        objJadeEngine.Execute();
        objJadeEngine.Logout();
    }

    @Test
    public void testSendWithMail() throws Exception {
        objOptions.mail_on_success.isTrue();
        objOptions.getMailOptions().FileNotificationTo.Value("oh@sos-berlin.com");
        objOptions.getMailOptions().SMTPHost.Value("smtp.sos");
        super.testSend();
    }

    @Test
    public void testSendWithOutAccessToOneFileInSourceFolder() throws Exception {
        objTestOptions.SourceDir.Value("/home/test/noaccess");
        objTestOptions.file_spec.Value("\\.txt$");
        objTestOptions.force_files.value(false);
        super.testCopyMultipleFiles();
    }

    @Test
    public void testSendWithOutAccessToOneFileInSourceFolder2() throws Exception {
        objTestOptions.SourceDir.Value("/home/test/noaccess");
        objTestOptions.file_spec.Value("\\.txt$");
        objTestOptions.force_files.value(true);
        super.testCopyMultipleFiles();
    }

    @Test
    public void testSendWithOutAccessToSourceFolder() throws Exception {
        objTestOptions.SourceDir.Value("/root");
        objTestOptions.file_spec.Value("\\.txt$");
        objTestOptions.force_files.value(false);
        try {
            super.testCopyMultipleFiles();
        } catch (JADEException e) {
            System.out.println("Exit code must me not equal to zero = " + e.getExitCode().ExitCode);
            e.printStackTrace();
        }
    }

}