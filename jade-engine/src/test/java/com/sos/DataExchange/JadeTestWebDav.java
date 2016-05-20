package com.sos.DataExchange;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Options.SOSOptionAuthenticationMethod.enuAuthenticationMethods;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;

public class JadeTestWebDav extends JadeTestBase {

    protected static final String WEB_URI = "http://homer.sos/webdav";
    protected static final String WEB_USER = "test";
    protected static final String WEB_PASS = "12345";
    protected static final String REMOTE_BASE_PATH = "/home/test/";

    private static final String SETTINGS_FILE = "src/test/resources/examples/jade_webdav_settings.ini";

    public JadeTestWebDav() {
        enuSourceTransferType = enuTransferTypes.local;
        enuTargetTransferType = enuTransferTypes.webdav;
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        objTestOptions.sourceDir.setValue(strTestPathName);
        objTestOptions.getSource().protocol.setValue(enuSourceTransferType);
        objTestOptions.getSource().user.setValue("test");
        objTestOptions.getSource().password.setValue("12345");
        objTestOptions.getSource().host.setValue("local");
        objTestOptions.getTarget().protocol.setValue(enuTargetTransferType);
        objTestOptions.targetDir.setValue(REMOTE_BASE_PATH);
        objTestOptions.getTarget().host.setValue(WEB_URI);
        objTestOptions.getTarget().port.value(8080);
        objTestOptions.getTarget().user.setValue(WEB_USER);
        objTestOptions.getTarget().password.setValue(WEB_PASS);
        objTestOptions.getTarget().authMethod.setValue(enuAuthenticationMethods.url);
    }

    @Test
    public void testProfileHttpsGmx2LocalOneFile() throws Exception {
        objOptions = new JADEOptions();
        objOptions.settings.setValue(SETTINGS_FILE);
        objOptions.profile.setValue("https_gmx_2_local_one_file");
        this.execute(objOptions);
    }

    @Test
    public void testProfileHttpsProxyGmx2LocalOneFile() throws Exception {
        objOptions = new JADEOptions();
        objOptions.settings.setValue(SETTINGS_FILE);
        objOptions.profile.setValue("https_proxy_gmx_2_local_one_file");
        this.execute(objOptions);
    }

    @Test
    public void testProfileHttpsHomer2LocalOneFile() throws Exception {
        objOptions = new JADEOptions();
        objOptions.settings.setValue(SETTINGS_FILE);
        objOptions.profile.setValue("https_homer_2_local_one_file");
        this.execute(objOptions);
    }

    @Test
    public void testProfileHttpHomer2LocalOneFile() throws Exception {
        objOptions = new JADEOptions();
        objOptions.settings.setValue(SETTINGS_FILE);
        objOptions.profile.setValue("http_homer_2_local_one_file");
        this.execute(objOptions);
    }

    private void execute(JADEOptions options) throws Exception {
        try {
            objJadeEngine = new JadeEngine(options);
            objJadeEngine.execute();
        } catch (Exception ex) {
            throw ex;
        } finally {
            objJadeEngine.logout();
        }
    }

    @Override
    @Test
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
    public void testCopyWithFileList() throws Exception {
        super.testCopyWithFileList();
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
    public void testRenameFiles() throws Exception {
        super.testRenameFiles();
    }

    @Override
    @Test
    public void testSend() throws Exception {
        super.testSend();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendViaProxy() throws Exception {
        objTestOptions.getTarget().proxyHost.setValue("proxy.sos");
        objTestOptions.getTarget().proxyPort.setValue("3128");
        super.testSend();
    }

    @Override
    @Test
    public void testSend2() throws Exception {
        objTestOptions.targetDir.setValue("/webdav/kb/");
        objTestOptions.getTarget().host.setValue("http://homer.sos/webdav");
        objTestOptions.getTarget().port.value(8080);
        objTestOptions.getTarget().user.setValue("test");
        objTestOptions.getTarget().password.setValue("12345");
        super.testSend();
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    public void testSend3() throws Exception {
        objTestOptions.targetDir.setValue("/jade/403");
        objTestOptions.getTarget().host.setValue("http://homer.sos/jade/");
        objTestOptions.getTarget().port.value(8080);
        objTestOptions.getTarget().user.setValue("test");
        objTestOptions.getTarget().password.setValue("12345");
        super.testSend();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendViaMonkAsProxy() throws Exception {
        objTestOptions.targetDir.setValue("/webdav2/kb/");
        objTestOptions.getTarget().host.setValue("http://homer.sos/webdav2/");
        objTestOptions.getTarget().port.value(8080);
        objTestOptions.getTarget().user.setValue("test");
        objTestOptions.getTarget().password.setValue("12345");
        objTestOptions.getTarget().proxyHost.setValue("proxy.sos");
        objTestOptions.getTarget().proxyPort.setValue("3128");
        super.testSend();
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    public void testSendViaUnknownProxy() throws Exception {
        objTestOptions.targetDir.setValue("/webdav2/kb/");
        objTestOptions.getTarget().host.setValue("http://homer.sos/webdav2");
        objTestOptions.getTarget().port.value(8080);
        objTestOptions.getTarget().user.setValue("test");
        objTestOptions.getTarget().password.setValue("12345");
        objTestOptions.getTarget().proxyHost.setValue("proxi.sos");
        objTestOptions.getTarget().proxyPort.setValue("3128");
        super.testSend();
    }

    @Override
    @Test
    public void testSendFileSpec() throws Exception {
        objTestOptions.recursive.value(false);
        objTestOptions.fileSpec.setValue("^test.*\\.txt$");
        super.testSendFileSpec2();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendRecursive() throws Exception {
        objTestOptions.targetDir.setValue("/webdav/kb");
        objTestOptions.getTarget().host.setValue("http://homer.sos/webdav");
        objTestOptions.getTarget().port.value(8080);
        objTestOptions.getTarget().user.setValue("test");
        objTestOptions.getTarget().password.setValue("12345");
        objTestOptions.fileSpec.setValue("^test.txt$");
        objTestOptions.recursive.value(true);
        super.testSendFileSpec2();
    }

    @Test
    public void testSendRecursive2() throws Exception {
        objTestOptions.sourceDir.setValue("R:/nobackup/junittests/testdata/JADE/recursive");
        objTestOptions.targetDir.setValue("/webdav/kb");
        objTestOptions.getTarget().host.setValue("http://homer.sos/webdav");
        objTestOptions.getTarget().port.value(8080);
        objTestOptions.getTarget().user.setValue("test");
        objTestOptions.getTarget().password.setValue("12345");
        objTestOptions.fileSpec.setValue("\\.(txt|dot)$");
        objTestOptions.recursive.value(true);
        super.testSendFileSpec2();
    }

    @Override
    @Test
    public void testCopyAndRenameSourceAndTarget() throws Exception {
        objTestOptions.targetDir.setValue("/webdav/kb");
        objTestOptions.getTarget().host.setValue("http://homer.sos/webdav/");
        objTestOptions.getTarget().port.value(8080);
        objTestOptions.getTarget().user.setValue("test");
        objTestOptions.getTarget().password.setValue("12345");
        super.testCopyAndRenameSourceAndTarget();
    }

    @Test
    public void testCopyAndRenameSourceAndTarget2() throws Exception {
        super.testCopyAndRenameSourceAndTarget();
    }
}
