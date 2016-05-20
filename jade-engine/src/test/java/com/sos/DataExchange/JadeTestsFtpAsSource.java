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
        objTestOptions.targetDir.setValue(strTestPathName);
        objTestOptions.getTarget().protocol.setValue(enuTargetTransferType);
        objTestOptions.getTarget().user.setValue(USER_ID_TEST);
        objTestOptions.getTarget().password.setValue(PASSWORD_TEST);
        objTestOptions.getTarget().host.setValue("local");
        objTestOptions.getTarget().protocol.setValue(enuTargetTransferType);
        objTestOptions.getSource().protocol.setValue(enuSourceTransferType);
        objTestOptions.getTarget().protocol.setValue(enuTargetTransferType);
        objTestOptions.sourceDir.setValue("/home/test/jadetest/SOSDEX");
        objTestOptions.getSource().host.setValue(HOST_NAME_WILMA_SOS);
        objTestOptions.getSource().port.value(SOSOptionPortNumber.conPort4SFTP);
        objTestOptions.getSource().user.setValue(USER_ID_TEST);
        objTestOptions.getSource().password.setValue(PASSWORD_TEST);
        objTestOptions.getSource().authMethod.setValue(enuAuthenticationMethods.password);
        objOptions.profile.setValue(CLASS_NAME);
    }

    @Override
    @Test
    public void testReceiveWithSymlinkInRemoteDir() throws Exception {
        super.testReceiveWithSymlinkInRemoteDir();
    }

    @Override
    @Test
    public void testUseProfileWithoutCreatingTestFiles() throws Exception {
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("getList_example_sftp");
        super.testUseProfileWithoutCreatingTestFiles();
    }

    @Override
    @Test
    public void testTransferUsingFilePath() throws Exception {
        gstrFilePath = "myfile_20120801.csv";
        objTestOptions.sourceDir.setValue("/home/test/tmp/");
        super.testTransferUsingFilePath();
    }

    @Test
    public void testTransferUsingAbsolutFilePath() throws Exception {
        gstrFilePath = "/home/test/tmp/myfile_20120801.csv";
        objTestOptions.sourceDir.setValue("");
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
        objTestOptions.sourceDir.setValue("");
        objOptions.profile.setValue("testTransferUsingRelativeFilePath");
        super.testTransferUsingFilePath();
    }

    @Test
    public void testTransferUsingRelativeFilePath2() throws Exception {
        objTestOptions.sourceDir.setValue("");
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
        objTestOptions.getTarget().user.setValue("");
        objTestOptions.getTarget().host.setValue("");
        JSCsvFile csvFile = new JSCsvFile("R:/nobackup/junittests/testdata/JADE/history_files/historyWithEmptyHost.csv");
        if (csvFile.exists()) {
            csvFile.delete();
        }
        objOptions.historyFileName.setValue(csvFile.getAbsolutePath());
        setOptions4BackgroundService();
        super.testSend();
        String[] strValues = null;
        csvFile.loadHeaders();
        String[] strHeader = csvFile.getHeaders();
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
        objTestOptions.fileNameRegExp.setValue("^Masstest.\\.txt$");
        super.testDeleteFiles();
    }

    @Test
    public void testDeleteFilesWithForce() throws Exception {
        objTestOptions.errorOnNoDataFound.push();
        objTestOptions.errorOnNoDataFound.setFalse();
        objTestOptions.fileNameRegExp.setValue("^Masstest.\\.txt$");
        super.testDeleteFiles();
        objTestOptions.errorOnNoDataFound.pop();
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    public void testDeleteFilesWithError() throws Exception {
        objTestOptions.errorOnNoDataFound.push();
        objTestOptions.errorOnNoDataFound.setTrue();
        objTestOptions.fileNameRegExp.setValue("^Masstest.\\.txt$");
        super.testDeleteFiles();
        objTestOptions.errorOnNoDataFound.pop();
    }

    @Test
    public void testSFTPReceive() throws Exception {
        JADEOptions objTestOptions = new JADEOptions();
        objTestOptions.operation.setValue(enuJadeOperations.receive);
        objTestOptions.host.setValue("homer.sos");
        objTestOptions.user.setValue("test");
        objTestOptions.password.setValue("12345");
        objTestOptions.port.value(SOSOptionPortNumber.conPort4SFTP);
        objTestOptions.protocol.setValue(enuSourceTransferType);
        objTestOptions.authMethod.setValue(enuAuthenticationMethods.password);
        objTestOptions.remoteDir.setValue("/tmp/test/jade/out");
        objTestOptions.localDir.setValue("\\\\8of9\\c\\tmp\\sftpreceive");
        objTestOptions.fileSpec.setValue(".*");
        objTestOptions.recursive.value(true);
        objTestOptions.verbose.value(9);
        JadeEngine objJadeEngine = new JadeEngine(objTestOptions);
        objJadeEngine.execute();
        objJadeEngine.logout();
    }

    @Test
    public void testSendWithMail() throws Exception {
        objOptions.mailOnSuccess.isTrue();
        objOptions.getMailOptions().FileNotificationTo.setValue("oh@sos-berlin.com");
        objOptions.getMailOptions().SMTPHost.setValue("smtp.sos");
        super.testSend();
    }

    @Test
    public void testSendWithOutAccessToOneFileInSourceFolder() throws Exception {
        objTestOptions.sourceDir.setValue("/home/test/noaccess");
        objTestOptions.fileSpec.setValue("\\.txt$");
        objTestOptions.forceFiles.value(false);
        super.testCopyMultipleFiles();
    }

    @Test
    public void testSendWithOutAccessToOneFileInSourceFolder2() throws Exception {
        objTestOptions.sourceDir.setValue("/home/test/noaccess");
        objTestOptions.fileSpec.setValue("\\.txt$");
        objTestOptions.forceFiles.value(true);
        super.testCopyMultipleFiles();
    }

    @Test
    public void testSendWithOutAccessToSourceFolder() throws Exception {
        objTestOptions.sourceDir.setValue("/root");
        objTestOptions.fileSpec.setValue("\\.txt$");
        objTestOptions.forceFiles.value(false);
        try {
            super.testCopyMultipleFiles();
        } catch (JADEException e) {
            System.out.println("Exit code must me not equal to zero = " + e.getExitCode().ExitCode);
            e.printStackTrace();
        }
    }

}