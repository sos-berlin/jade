package com.sos.DataExchange;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sos.JSHelper.Options.SOSOptionAuthenticationMethod.enuAuthenticationMethods;
import com.sos.JSHelper.Options.SOSOptionPortNumber;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;

public class JadeTestsFtp extends JadeTestBase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        enuSourceTransferType = enuTransferTypes.local;
        enuTargetTransferType = enuTransferTypes.sftp;
        objTestOptions.sourceDir.setValue(strTestPathName);
        objTestOptions.targetDir.setValue("/home/test/jadetest" + "/SOSDEX/");
        objTestOptions.getSource().protocol.setValue(enuSourceTransferType);
        objTestOptions.getTarget().protocol.setValue(enuTargetTransferType);
        objTestOptions.getTarget().host.setValue(HOST_NAME_WILMA_SOS);
        objTestOptions.getTarget().port.value(SOSOptionPortNumber.conPort4SFTP);
        objTestOptions.getTarget().user.setValue("test");
        objTestOptions.getTarget().password.setValue("12345");
        objTestOptions.getSource().user.setValue("test");
        objTestOptions.getSource().password.setValue("12345");
        objTestOptions.user.setValue("test");
        objTestOptions.password.setValue("12345");
        objTestOptions.getTarget().authMethod.setValue(enuAuthenticationMethods.password);
        objTestOptions.getSource().loadClassName.setValue("com.sos.VirtualFileSystem.SFTP.SOSVfsSFtp");
        objTestOptions.getTarget().loadClassName.setValue("com.sos.VirtualFileSystem.SFTP.SOSVfsSFtp");
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testReceiveWithSymlinkInRemoteDir() throws Exception {
        super.testReceiveWithSymlinkInRemoteDir();
    }

    @Override
    @Test
    public void testSend2file_spec() throws Exception {
        super.testSend2file_spec();
    }

    @Override
    @Test
    public void testDeleteFiles2() throws Exception {
        super.testDeleteFiles2();
    }

    @Override
    @Test
    public void testCopyMultipleFiles() throws Exception {
        super.testCopyMultipleFiles();
    }

    @Override
    @Test
    public void testCopyMultipleFilesThreaded() throws Exception {
        CreateTestFiles(150);
        super.testCopyMultipleFilesThreaded();
    }

    @Override
    public void testBigCopyThreaded() throws Exception {
        this.testBigCopy();
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testCopyWithFileList() throws Exception {
        super.testCopyWithFileList();
    }

    @Override
    public void testBigCopy() throws Exception {
        objTestOptions.sourceDir.setValue("R:/backup/sos/java/doxygen-docs/");
        super.testBigCopy();
    }

    @Test
    public void testBigCopy2() throws Exception {
        objTestOptions.sourceDir.setValue("R:/backup/sos/java/doxygen-docs/com.sos.VirtualFileSystem/");
        super.testBigCopy();
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
    public void testSendWithJCraft() throws Exception {
        objTestOptions.getTarget().setLoadClassName("com.sos.VirtualFileSystem.SFTP.SOSVfsSFtpJCraft");
        super.testSend();
    }

    @Override
    @Test
    public void testSendFileSpec() throws Exception {
        super.testSendFileSpec();
    }

    @Override
    @Test
    public void testCopyAndCreateVariableFolder() throws Exception {
        super.testCopyAndCreateVariableFolder();
    }

    @Test
    public void testCopyLocal2SFTPrecursive() throws Exception {
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("Copy_Local2SFTP_recursive");
        super.testUseProfileWOCreatingTestFiles();
    }

    @Test
    public void testSosFtp186WithTransactional() throws Exception {
        objOptions.transactional.value(true);
        super.testSendCommandAfterReplacing();
    }

    @Test
    public void testSosFtp186WithAtomicSuffix() throws Exception {
        objOptions.atomicSuffix.setValue("~");
        super.testSendCommandAfterReplacing();
    }

    @Test
    public void testSosFtp186WithOutAtomicSuffix() throws Exception {
        super.testSendCommandAfterReplacing();
    }

    @Test
    public void sftpSendWithCommands() throws Exception {
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("sftpSendWithCommands");
        super.testUseProfileWOCreatingTestFiles();
    }

    @Test
    public void testSendWithMail() throws Exception {
        objOptions.mailOnSuccess.value(true);
        objOptions.getMailOptions().FileNotificationTo.setValue("oh@sos-berlin.com");
        objOptions.getMailOptions().SMTPHost.setValue("smtp.sos");
        objOptions.resultSetFileName.setValue(objOptions.getTempDir() + "resultList.txt");
        objOptions.createResultSet.value(true);
        super.testSend();
    }

    @Test
    public void testLocal2sftpWithNotEnoughSpaceOnTarget() throws Exception {
        objTestOptions.targetDir.setValue("/media/ramdisk");
        objTestOptions.sourceDir.setValue("R:/nobackup/junittests/testdata/JADE/testLocal2sftpWithNotEnoughSpaceOnTarget");
        objOptions.forceFiles.value(false);
        super.testCopyMultipleFiles();
    }

}
