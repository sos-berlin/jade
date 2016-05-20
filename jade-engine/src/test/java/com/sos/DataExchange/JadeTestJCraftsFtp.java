/**
 *
 */
package com.sos.DataExchange;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sos.JSHelper.Options.SOSOptionAuthenticationMethod.enuAuthenticationMethods;
import com.sos.JSHelper.Options.SOSOptionPortNumber;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;

/** @author KB */
// oh 06.05.14 test crashed: Exception in thread "main"
// java.lang.OutOfMemoryError: Java heap space [SP]
@Ignore("Test set to Ignore for later examination")
public class JadeTestJCraftsFtp extends JadeTestBase {

    @Test
    public void testSendTransactional() throws Exception {
        super.testSend2file_spec();
    }

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
        objTestOptions.getTarget().authMethod.setValue(enuAuthenticationMethods.password);
        objTestOptions.getSource().loadClassName.setValue("com.sos.VirtualFileSystem.SFTP.SOSVfsSFtpJCraft");
        objTestOptions.getTarget().loadClassName.setValue("com.sos.VirtualFileSystem.SFTP.SOSVfsSFtpJCraft");
    }

    @Override
    @Test
    public void testSend2file_spec() throws Exception {
        super.testSend2file_spec();
    }

    @Override
    @Test
    public void testCopyMultipleFiles() throws Exception {
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

    @Override
    @Test
    public void testCopyWithFileList() throws Exception {
        super.testCopyWithFileList();
    }

    @Override
    @Test
    public void testBigCopy() throws Exception {
        objTestOptions.sourceDir.setValue("R:/backup/sos/java/doxygen-docs/");
        objTestOptions.targetDir.setValue(TARGET_OF_DOXYGEN_DOCS);
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

    @Override
    @Test
    public void testSendFileSpec() throws Exception {
        super.testSendFileSpec();
    }

    @Test
    public void testLocal2sftpWithNotEnoughSpaceOnTarget() throws Exception {
        objTestOptions.targetDir.setValue("/media/ramdisk");
        objTestOptions.sourceDir.setValue("R:/nobackup/junittests/testdata/JADE/testLocal2sftpWithNotEnoughSpaceOnTarget");
        objTestOptions.forceFiles.value(false);
        super.testCopyMultipleFiles();
    }

}
