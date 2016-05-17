package com.sos.DataExchange;

import org.junit.Before;
import org.junit.Test;

import com.sos.JSHelper.Options.SOSOptionAuthenticationMethod.enuAuthenticationMethods;
import com.sos.JSHelper.Options.SOSOptionPortNumber;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;

public class JadeTestFtp extends JadeTestBase {

    public JadeTestFtp() {
        enuSourceTransferType = enuTransferTypes.local;
        enuTargetTransferType = enuTransferTypes.ftp;
    }

    @Override
    @Before
    public void setUp() throws Exception {
        enuSourceTransferType = enuTransferTypes.local;
        enuTargetTransferType = enuTransferTypes.ftp;
        super.setUp();
        objTestOptions.sourceDir.Value(strTestPathName);
        objTestOptions.targetDir.Value("/home/test/jadetest" + "/SOSDEX/");
        objTestOptions.Source().protocol.Value(enuSourceTransferType);
        objTestOptions.Target().protocol.Value(enuTargetTransferType);
        objTestOptions.Target().host.Value(HOST_NAME_WILMA_SOS);
        objTestOptions.Target().port.value(SOSOptionPortNumber.conPort4FTP);
        objTestOptions.Target().user.Value("test");
        objTestOptions.Target().password.Value("12345");
        objTestOptions.Source().user.Value("test");
        objTestOptions.Source().password.Value("12345");
        objTestOptions.Target().authMethod.Value(enuAuthenticationMethods.password);
    }

    @Override
    @Test
    public void testCopyAndRenameSource() throws Exception {
        super.testCopyAndRenameSource();
    }

    @Override
    @Test
    public void testCopyForceFiles() throws Exception {
        super.testCopyForceFiles();

    }

    @Override
    @Test
    public void testSend2file_spec() throws Exception {
        super.testSend2file_spec();
    }

    @Override
    @Test
    public void testCopyMultipleFiles() throws Exception {
        objTestOptions.Target().host.Value("homer.sos");
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
    public void testBigCopyThreaded() throws Exception {
        this.testBigCopy();
    }

    @Test
    public void testBigCopy2() throws Exception {
        objTestOptions.sourceDir.Value("R:/backup/sos/java/doxygen-docs/com.sos.VirtualFileSystem/");
        objTestOptions.targetDir.Value("/home/test/doc/doxygen-docs/com.sos.VirtualFileSystem/");
        super.testBigCopy();
    }

    @Override
    @Test
    public void testCopyWithFileList() throws Exception {
        super.testCopyWithFileList();
    }

    @Override
    @Test
    public void testCopyMultipleResultList() throws Exception {
        super.testCopyMultipleResultList();
    }

    @Override
    @Test
    public void testGetFileList() throws Exception {
        super.testGetFileList();
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

    @Override
    @Test
    public void testKeePass1() throws Exception {
        super.testKeePass1();
    }

    @Override
    @Test
    public void testSendWithPollingAndSteadyState() throws Exception {
        super.testSendWithPollingAndSteadyState();
    }

    @Test
    public void testSendWithPollingAndSteadyStateError() throws Exception {
        objOptions.steadyStateErrorState.Value("nextState");
        super.sendWithPollingAndSteadyStateError();
    }

    @Test
    public void testCopyLocal2AlternativeFTPwithHistory() throws Exception {
        objOptions.settings.Value(strSettingsFile);
        objOptions.profile.Value("Copy_Local2AlternativeFTP_withHistorie");
        super.testUseProfileWOCreatingTestFiles();
    }

    @Test
    public void testCopyLocal2FTPrecursive() throws Exception {
        objOptions.settings.Value(strSettingsFile);
        objOptions.profile.Value("Copy_Local2FTP_recursive");
        super.testUseProfileWOCreatingTestFiles();
    }

    @Test
    public void copyLocal2ftpReplacingWithCreateDirectory() throws Exception {
        objOptions.settings.Value(strSettingsFile);
        objOptions.profile.Value("copy_local2ftp_replacingWithCreateDirectory");
        super.testUseProfileWOCreatingTestFiles();
    }

    @Test
    public void testPclFtpRec() throws Exception {
        objOptions.settings.Value(strSettingsFile);
        objOptions.profile.Value("PCL_FTP_REC");
        super.testUseProfileWOCreatingTestFiles();
    }

    @Test
    public void testSosftp158() throws Exception {
        objOptions.settings.Value(strSettingsFile);
        objOptions.profile.Value("sosftp_158");
        super.testUseProfileWOCreatingTestFiles();
    }

    @Test
    public void testUrlExample1() throws Exception {
        objOptions.settings.Value(strSettingsFile);
        objOptions.profile.Value("url_example_1");
        super.testUseProfileWOCreatingTestFiles();
    }

    @Test
    public void testSendLocal2ftpTargetReplacing() throws Exception {
        objOptions.settings.Value(strSettingsFile);
        objOptions.profile.Value("send_local2ftp_target_replacing");
        super.testUseProfileWOCreatingTestFiles();
    }

    @Test
    public void testcopyLocal2ftpAscii() throws Exception {
        objOptions.settings.Value(strSettingsFile);
        objOptions.profile.Value("copy_local2ftp_ascii");
        super.testUseProfileWOCreatingTestFiles();
    }

    @Test
    public void testcopyLocal2ftp() throws Exception {
        objOptions.settings.Value(strSettingsFile);
        objOptions.profile.Value("copy_passive2ftp");
        super.testUseProfileWOCreatingTestFiles();
    }

}
