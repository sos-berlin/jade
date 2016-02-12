package com.sos.DataExchange;

import org.junit.Before;
import org.junit.Test;

import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;

public class JadeTestCifs extends JadeTestBase {

    public JadeTestCifs() {
        enuSourceTransferType = enuTransferTypes.local;
        enuTargetTransferType = enuTransferTypes.smb;
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        objTestOptions.Source().protocol.Value(enuSourceTransferType);
        objTestOptions.SourceDir.Value(strTestPathName);
        objTestOptions.Target().protocol.Value(enuTargetTransferType);
        objTestOptions.Target().host.Value("wilma.sos");
        objTestOptions.Target().user.Value("test");
        objTestOptions.Target().password.Value("12345");
        objTestOptions.Target().domain.Value("sos");
        objTestOptions.TargetDir.Value("test/jadetest" + "/SOSDEX/");
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
    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    public void testSend2() throws Exception {
        objTestOptions.TargetDir.Value("test/tmp/403");
        super.testSend();
    }

    @Override
    @Test
    public void testSendFileSpec() throws Exception {
        objTestOptions.recursive.value(false);
        objTestOptions.file_spec.Value("\\.txt$");
        objTestOptions.SourceDir.Value(strTestPathName + "recursive");
        super.testSendFileSpec2();
    }

    @Test
    public void testSendRecursive() throws Exception {
        objTestOptions.file_spec.Value("\\.txt$");
        objTestOptions.recursive.value(true);
        objTestOptions.SourceDir.Value(strTestPathName + "recursive");
        super.testSendFileSpec2();
    }

    @Override
    @Test
    public void testCopyAndRenameSourceAndTarget() throws Exception {
        super.testCopyAndRenameSourceAndTarget();
    }

}
