package com.sos.DataExchange;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sos.JSHelper.Options.SOSOptionAuthenticationMethod.enuAuthenticationMethods;
import com.sos.JSHelper.Options.SOSOptionPortNumber;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;

public class JadeTestWebDavAsSource extends JadeTestBase {

    protected final String WEB_URI = "http://homer.sos/webdav";
    protected final String WEB_USER = "test";
    protected final String WEB_PASS = "12345";
    protected final String REMOTE_BASE_PATH = "/home/kb/";

    public JadeTestWebDavAsSource() {
        enuSourceTransferType = enuTransferTypes.webdav;
        enuTargetTransferType = enuTransferTypes.local;
    }

    /** \brief setUp
     * 
     * \details
     *
     * \return void
     *
     * @throws java.lang.Exception */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        objTestOptions.targetDir.setValue(strTestPathName);

        objTestOptions.getTarget().protocol.setValue(enuTargetTransferType);
        objTestOptions.getTarget().user.setValue("test");
        objTestOptions.getTarget().password.setValue("12345");
        objTestOptions.getTarget().host.setValue("local");
        objTestOptions.getTarget().protocol.setValue(enuTargetTransferType);

        objTestOptions.getSource().protocol.setValue(enuSourceTransferType);

        objTestOptions.sourceDir.setValue(REMOTE_BASE_PATH);
        objTestOptions.getSource().host.setValue(WEB_URI);
        objTestOptions.getSource().port.value(8080);
        objTestOptions.getSource().user.setValue(WEB_USER);
        objTestOptions.getSource().password.setValue(WEB_PASS);
        objTestOptions.getSource().authMethod.setValue(enuAuthenticationMethods.url);
    }

    public void homerAsSource() throws Exception {
        objTestOptions.getSource().host.setValue("http://homer.sos:8080/webdav/");
        objTestOptions.getSource().user.setValue("test");
        objTestOptions.getSource().password.setValue("12345");
    }

    public void sourceBehindProxy() throws Exception {
        objTestOptions.getSource().proxyHost.setValue("proxy.sos");
        objTestOptions.getSource().proxyPort.value(3128);
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSend2file_spec() throws Exception {
        super.testSend2file_spec();
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
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
    @Ignore("Test set to Ignore for later examination")
    public void testBigCopyThreaded() throws Exception {
        this.testBigCopy();
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testBigCopy() throws Exception {
        objTestOptions.sourceDir.setValue("R:/backup/sos/java/doxygen-docs/");
        objTestOptions.targetDir.setValue(TARGET_OF_DOXYGEN_DOCS);
        super.testBigCopy();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testBigCopy2() throws Exception {
        objTestOptions.sourceDir.setValue("R:/backup/sos/java/doxygen-docs/com.sos.VirtualFileSystem/");
        objTestOptions.targetDir.setValue(TARGET_OF_DOXYGEN_DOCS + "com.sos.VirtualFileSystem/");
        super.testBigCopy();
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testCopyMultipleResultList() throws Exception {
        super.testCopyMultipleResultList();
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendAndDeleteMultipleFiles() throws Exception {
        super.testSendAndDeleteMultipleFiles();
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testRenameFiles() throws Exception {
        super.testRenameFiles();
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSend() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSend";
        super.testSend();
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSend2() throws Exception {
        final String conMethodName = CLASS_NAME + "::testSend2";
        // /jade liegt in /tmp/test/jade
        objTestOptions.sourceDir.setValue("/jade/out");
        homerAsSource();
        super.testSend();
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendFileSpec() throws Exception {
        // in /jade/out sind Unterverzeichnisse
        final String conMethodName = CLASS_NAME + "::testSendFileSpec";
        objTestOptions.sourceDir.setValue("/jade/out");
        homerAsSource();
        objTestOptions.recursive.value(false);
        objTestOptions.fileSpec.setValue(".*");
        super.testSendFileSpec2();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendFileSpecWithProxy() throws Exception {
        // in /jade/out sind Unterverzeichnisse
        final String conMethodName = CLASS_NAME + "::testSendFileSpecWithProxy";
        objTestOptions.sourceDir.setValue("/jade/out");
        homerAsSource();
        sourceBehindProxy();
        objTestOptions.recursive.value(false);
        objTestOptions.fileSpec.setValue(".*");
        super.testSendFileSpec2();
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendFileSpec2() throws Exception {
        // in /jade/massive sind keine Unterverzeichnisse
        final String conMethodName = CLASS_NAME + "::testSendFileSpec";
        objTestOptions.sourceDir.setValue("/jade/massive");
        homerAsSource();
        objTestOptions.recursive.value(false);
        objTestOptions.fileSpec.setValue(".*");
        super.testSendFileSpec2();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendRecursive() throws Exception {
        @SuppressWarnings("unused")
        final String conMethodName = CLASS_NAME + "::testSendRecursive";
        objTestOptions.sourceDir.setValue("/jade/out");
        homerAsSource();
        objTestOptions.recursive.value(true);
        objTestOptions.fileSpec.setValue("\\.txt$");
        super.testSendFileSpec2();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendRecursiveWithProxy() throws Exception {
        @SuppressWarnings("unused")
        final String conMethodName = CLASS_NAME + "::testSendRecursiveWithProxy";
        objTestOptions.sourceDir.setValue("/jade/out");
        homerAsSource();
        sourceBehindProxy();
        objTestOptions.recursive.value(true);
        objTestOptions.fileSpec.setValue("\\.txt$");
        super.testSendFileSpec2();
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testCopyAndRenameSourceAndTarget() throws Exception {
        objTestOptions.sourceDir.setValue("/jade/out");
        homerAsSource();
        super.testCopyAndRenameSourceAndTarget();
    }
}
