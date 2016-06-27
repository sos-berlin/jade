package com.sos.DataExchange;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sos.JSHelper.DataElements.JSDataElementDate;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;
import com.sos.JSHelper.io.Files.JSFile;

public class JadeTestLocal extends JadeTestBase {

    private static final Logger LOGGER = Logger.getLogger(JadeTestLocal.class);

    @Override
    @Test
    public void testSendRegExpAsFileName() throws Exception {
        super.testSendRegExpAsFileName();
    }

    @Override
    @Test
    public void testTransferUsingFilePath() throws Exception {
        CreateTestFile("test.txt");
        gstrFilePath = "test.txt";
        super.testTransferUsingFilePath();
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    public void testTransferUsingWrongFilePath() throws Exception {
        gstrFilePath = "test-test-test.txt";
        super.testTransferUsingFilePath();
    }

    @Override
    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    public void testSendWrongFileSpec() throws Exception {
        super.testSendWrongFileSpec();
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        enuSourceTransferType = enuTransferTypes.local;
        enuTargetTransferType = enuTransferTypes.local;
        objTestOptions.sourceDir.setValue(strTestPathName);
        objTestOptions.targetDir.setValue(strTestPathName + "/SOSMDX/");
        objTestOptions.getSource().protocol.setValue(enuSourceTransferType);
        objTestOptions.getTarget().protocol.setValue(enuTargetTransferType);
    }

    @Test
    public void testUseSubstitution() throws Exception {
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("substitute_example");
        objOptions.readSettingsFile();
        LOGGER.info(objOptions.dirtyString());
    }

    @Override
    @Test
    public void testUseProfile() throws Exception {
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("getList_example");
        super.testUseProfile();
    }

    @Test
    public void testgetList_variable_filespec_example() throws Exception {
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("getList_variable_filespec_example");
        CreateTestFile(String.format("TestFile_%1$s.123", JSDataElementDate.getCurrentTimeAsString("yyyyMMdd")));
        super.testUseProfile2();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testFtpReceive2Wilma() throws Exception {
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("ftp_receive_2_wilma");
        super.testUseProfile();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void CopyAndRenameSourceAndTarget() throws Exception {
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("CopyAndRenameSourceAndTarget_Local2Local");
        super.testUseProfile();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void CopyAndCreateVariableFolder() throws Exception {
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("CopyAndCreateVariableFolder_Local2Local");
        super.testUseProfile();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void CopyAndMoveSource() throws Exception {
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("CopyAndMoveSource_Local2Local");
        super.testUseProfile();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void CopyAndMoveSource2NewFolder() throws Exception {
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("CopyAndMoveSource2NewFolder_Local2Local");
        super.testUseProfile();
        JSFile objFile = new JSFile(strTestPathName, "UNKNOWNFOLDER/Masstest00000.txt");
        assertTrue("File " + objFile.getAbsolutePath() + " must exist", objFile.exists());
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void CopyAndRenameSource() throws Exception {
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("CopyAndRenameSource_Local2Local");
        super.testUseProfile();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void CopyUsingUNCNames() throws Exception {
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("Copy_Local2Local_UNC");
        super.testUseProfileWOCreatingTestFiles();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void CopyUsingUNCNamesWithNetUse() throws Exception {
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("Copy_Local2Local_UNC_withNetUse");
        super.testUseProfileWOCreatingTestFiles();
    }

    @Test
    public void Copy_Local2Local_recursive() throws Exception {
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("Copy_Local2Local_recursive");
        super.testUseProfileWOCreatingTestFiles();
    }

    @Override
    @Test
    public void testExecuteGetFileList() throws Exception {
        super.testExecuteGetFileList();
    }

    @Override
    @Test
    public void testCopyAndCreateVariableFolder() throws Exception {
        super.testCopyAndCreateVariableFolder();
    }

    @Override
    @Test
    public void testCopyAndRenameSourceAndTarget() throws Exception {
        super.testCopyAndRenameSourceAndTarget();
    }

    @Override
    @Test
    public void testCopyAndRenameSource() throws Exception {
        super.testCopyAndRenameSource();
    }

    @Override
    @Test
    public void testCopyMultipleFiles() throws Exception {
        super.testCopyMultipleFiles();
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testCopyMultipleFilesThreaded() throws Exception {
        super.testCopyMultipleFilesThreaded();
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination, test runs endlessly")
    public void testBigCopyThreaded() throws Exception {
        this.testBigCopy();
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    @Ignore("Test set to Ignore for later examination")
    public void testSendWithPollingWithoutWait4SourceDir() throws Exception {
        objTestOptions.sourceDir.setValue(strTestPathName + "/badname/");
        super.testSendWithPolling();
    }

    @Override
    @Test
    public void testSendWithPolling() throws Exception {
        objTestOptions.sourceDir.setValue(strTestPathName + "/badname/");
        objTestOptions.pollingWait4SourceFolder.setTrue();
        super.testSendWithPolling();
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testCopyWithFileList() throws Exception {
        super.testCopyWithFileList();
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testBigCopy() throws Exception {
        objTestOptions.sourceDir.setValue(SOURCE_OF_DOXYGEN_DOCS);
        objTestOptions.targetDir.setValue(TARGET_OF_DOXYGEN_DOCS);
        super.testBigCopy();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testBigCopy2() throws Exception {
        objTestOptions.sourceDir.setValue(SOURCE_OF_DOXYGEN_DOCS + "SOSVirtualFileSystem/");
        objTestOptions.targetDir.setValue(TARGET_OF_DOXYGEN_DOCS + "SOSVirtualFileSystem/");
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
    public void testSendAndCreateMd5Hash() throws Exception {
        objTestOptions.createSecurityHashFile.setTrue();
        super.testSend();
    }

    @Test
    public void testSendAndCreatesha256Hash() throws Exception {
        objTestOptions.createSecurityHashFile.setTrue();
        objTestOptions.securityHashType.setValue("SHA-256");
        super.testSend();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendWithMd5Check() throws Exception {
        objTestOptions.checkSecurityHash.setTrue();
        super.testSend();
    }

    @Override
    @Test
    public void testDeleteFiles2() throws Exception {
        super.testDeleteFiles2();
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testCopyWithFolderInSourceDir() throws Exception {
        super.testCopyWithFolderInSourceDir();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void CopyAndCheckSteadyState_Local2Local() throws Exception {
        objOptions.settings.setValue(strSettingsFile);
        objOptions.profile.setValue("CopyAndCheckSteadyState_Local2Local");
        super.testUseProfile();
    }

}
