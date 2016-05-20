package com.sos.DataExchange;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sos.JSHelper.Options.SOSOptionPortNumber;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;

public class JadeTestFtpAsSource extends JadeTestBase {

    protected static final String WEB_URI = "https://mediacenter.gmx.net";
    protected static final String WEB_USER = "sos.apl@gmx.de";
    protected static final String WEB_PASS = "sosapl10629";
    protected static final String REMOTE_BASE_PATH = "/home/kb/";

    public JadeTestFtpAsSource() {
        enuSourceTransferType = enuTransferTypes.ftp;
        enuTargetTransferType = enuTransferTypes.local;
    }

    @Override
    @Before
    public void setUp() throws Exception {
        enuSourceTransferType = enuTransferTypes.ftp;
        enuTargetTransferType = enuTransferTypes.local;
        super.setUp();
        objTestOptions.passiveMode.setTrue();
        objTestOptions.targetDir.setValue(strTestPathName);
        objTestOptions.getTarget().protocol.setValue(enuTargetTransferType);
        objTestOptions.getTarget().user.setValue(USER_ID_TEST);
        objTestOptions.getTarget().password.setValue(PASSWORD_TEST);
        objTestOptions.getTarget().host.setValue("local");
        objTestOptions.getTarget().protocol.setValue(enuTargetTransferType);
        objTestOptions.getTarget().port.value(SOSOptionPortNumber.conPort4FTP);
        objOptions.getTarget().port.value(SOSOptionPortNumber.conPort4FTP);
        objTestOptions.getSource().protocol.setValue(enuSourceTransferType);
        objTestOptions.sourceDir.setValue("/home/test/jadetest/SOSDEX/");
        objTestOptions.getSource().host.setValue(HOST_NAME_WILMA_SOS);
        objTestOptions.getSource().port.value(SOSOptionPortNumber.conPort4FTP);
        objTestOptions.getSource().user.setValue(USER_ID_TEST);
        objTestOptions.getSource().password.setValue(PASSWORD_TEST);
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testReceiveMultipleFiles2() throws Exception {
        super.testReceiveMultipleFiles2();
    }

    @Override
    public void testBigCopy() throws Exception {
        super.testBigCopy();
    }

    public void testBigCopy2() throws Exception {
        super.testBigCopy();
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testBigCopyThreaded() throws Exception {
        this.testBigCopy();
    }

    @Override
    @Test
    public void testCopyAndRenameSource() throws Exception {
        super.testCopyAndRenameSource();
    }

    @Override
    @Test
    public void testCopyMultipleFiles() throws Exception {
        objTestOptions.transactional.value(true);
        super.testCopyMultipleFiles();
    }

    @Override
    @Test
    public void testCopyMultipleFilesThreaded() throws Exception {
        super.testCopyMultipleFilesThreaded();
    }

    @Override
    @Test
    public void testCopyMultipleResultList() throws Exception {
        super.testCopyMultipleResultList();
    }

    @Test
    public void testCopyRecursiveWithFolderInSourceDir() throws Exception {
        objTestOptions.recursive.setTrue();
        objTestOptions.makeDirs.setTrue();
        objTestOptions.sourceDir.setValue("/home/test/jadetest/SOSDEX");
        super.testCopyWithFolderInSourceDir();
        objTestOptions.recursive.setFalse();
    }

    @Override
    @Test
    public void testCopyWithFileList() throws Exception {
        super.testCopyWithFileList();
    }

    @Override
    @Test
    public void testCopyWithFolderInSourceDir() throws Exception {
        objTestOptions.sourceDir.setValue("/home/test/jadetest/SOSDEX");
        super.testCopyWithFolderInSourceDir();
    }

    @Override
    @Test
    public void testDeleteFiles() throws Exception {
        objTestOptions.fileNameRegExp.push();
        objTestOptions.fileNameRegExp.setValue("^Masstest.*\\.txt$");
        super.testDeleteFiles();
        objTestOptions.fileNameRegExp.pop();
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    @Ignore("Test set to Ignore for later examination")
    public void testDeleteFilesWithError() throws Exception {
        objTestOptions.errorOnNoDataFound.push();
        objTestOptions.errorOnNoDataFound.setTrue();
        objTestOptions.fileNameRegExp.setValue("^Masstest.\\.txt$");
        super.testDeleteFiles();
        objTestOptions.errorOnNoDataFound.pop();
    }

    @Test
    public void testDeleteFilesWithForce() throws Exception {
        objTestOptions.errorOnNoDataFound.push();
        objTestOptions.errorOnNoDataFound.setFalse();
        objTestOptions.fileNameRegExp.setValue("^Masstest.\\.txt$");
        super.testDeleteFiles();
        objTestOptions.errorOnNoDataFound.pop();
    }

    @Override
    @Test
    public void testExecuteGetFileList() throws Exception {
        super.testExecuteGetFileList();
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testReceiveFileWithRelativeSourceDir() throws Exception {
        super.testReceiveFileWithRelativeSourceDir();
    }

    @Override
    @Test
    public void testReceiveWithSymlinkInRemoteDir() throws Exception {
        super.testReceiveWithSymlinkInRemoteDir();
    }

    @Override
    @Test
    public void testRenameFiles() throws Exception {
        super.testRenameFiles();
    }

    @Override
    @Test
    public void testRenameFiles2FolderWhichNotExist() throws Exception {
        super.testRenameFiles2FolderWhichNotExist();
    }

    @Override
    @Test
    public void testSend() throws Exception {
        super.testSend();
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendAndDeleteMultipleFiles() throws Exception {
        super.testSendAndDeleteMultipleFiles();
    }

    @Override
    @Test
    public void testSendFileSpec() throws Exception {
        super.testSendFileSpec();
    }

    @Override
    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    @Ignore("Test set to Ignore for later examination")
    public void testSendWithPolling0Files() throws Exception {
        super.testSendWithPolling0Files();
    }

    @Test
    public void testTransferUsingAbsolutFilePath() throws Exception {
        /** Error and endles loop: main DEBUG 01:47:51,405 DEBUG
         * (SOSVfsFtpBaseClass.java:636) ::LogReply 550 Failed to open file. */
        gstrFilePath = "/home/test/tmp/myfile_20120801.csv";
        objTestOptions.sourceDir.setValue("");
        super.testTransferUsingFilePath();
    }

    @Test
    public void testTransferUsingAbsolutFilePath2() throws Exception {
        /** Error and endles loop: main DEBUG 01:47:51,405 DEBUG
         * (SOSVfsFtpBaseClass.java:636) ::LogReply 550 Failed to open file. */
        gstrFilePath = "/home/test/tmp/myfile_20120801.csv;/home/test/tmp/test.kb";
        objTestOptions.sourceDir.setValue("");
        super.testTransferUsingFilePath();
    }

    @Override
    @Test
    public void testTransferUsingFilePath() throws Exception {
        /** Error and endles loop: main DEBUG 01:47:51,405 DEBUG
         * (SOSVfsFtpBaseClass.java:636) ::LogReply 550 Failed to open file. */
        gstrFilePath = "myfile_20120801.csv";
        objTestOptions.sourceDir.setValue("/home/test/tmp/");
        super.testTransferUsingFilePath();
    }

    @Test
    public void testTransferUsingFilePath2() throws Exception {
        /** Error and endles loop: main DEBUG 01:47:51,405 DEBUG
         * (SOSVfsFtpBaseClass.java:636) ::LogReply 550 Failed to open file. */
        gstrFilePath = "myfile_20120801.csv;test.kb";
        objTestOptions.sourceDir.setValue("/home/test/tmp/");
        super.testTransferUsingFilePath();
    }

    @Test
    public void testTransferUsingRelativeFilePath() throws Exception {
        gstrFilePath = "./tmp/myfile_20120801.csv";
        super.testTransferUsingFilePath();
    }

    @Test
    public void testTransferUsingRelativeFilePath2() throws Exception {
        gstrFilePath = "tmp/myfile_20120801.csv";
        super.testTransferUsingFilePath();
    }

    @Test
    public void testUseProfileFilespec2() throws Exception {
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("filespec2");
        super.testUseProfileWithoutCreatingTestFiles();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendLocal2ftpFileSpec5() throws Exception {
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("send_local2ftp_file_spec_5");
        super.testUseProfileWithoutCreatingTestFiles();
    }

    @Test
    public void testUseProfileWithAsciiMode() throws Exception {
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("copyWithAsciiMode");
        super.testUseProfileWithoutCreatingTestFiles();
    }

    @Test
    public void testUseProfileWithoutOperationGetList() throws Exception {
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("getList_example_ftp");
        super.testUseProfileWithoutCreatingTestFiles();
    }

    @Test
    public void jadeHomer2Local() throws Exception {
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("jadeHomer2Local");
        objOptions.verbose.value(2);
        super.testUseProfileWithoutCreatingTestFiles();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void jadeAlternativeHomer2Local() throws Exception {
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("jadeAlternativeHomer2Local");
        super.testUseProfileWithoutCreatingTestFiles();
    }

}
