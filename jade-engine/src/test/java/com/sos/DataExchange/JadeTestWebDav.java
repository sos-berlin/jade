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
        objTestOptions.SourceDir.Value(strTestPathName);
        objTestOptions.Source().protocol.Value(enuSourceTransferType);
        objTestOptions.Source().user.Value("test");
        objTestOptions.Source().password.Value("12345");
        objTestOptions.Source().host.Value("local");
        objTestOptions.Target().protocol.Value(enuTargetTransferType);
        objTestOptions.TargetDir.Value(REMOTE_BASE_PATH);
        objTestOptions.Target().host.Value(WEB_URI);
        objTestOptions.Target().port.value(8080);
        objTestOptions.Target().user.Value(WEB_USER);
        objTestOptions.Target().password.Value(WEB_PASS);
        objTestOptions.Target().auth_method.Value(enuAuthenticationMethods.url);
    }

    @Test
    public void testProfileHttpsGmx2LocalOneFile() throws Exception {
        objOptions = new JADEOptions();
        objOptions.settings.Value(SETTINGS_FILE);
        objOptions.profile.Value("https_gmx_2_local_one_file");
        this.execute(objOptions);
    }

    @Test
    public void testProfileHttpsProxyGmx2LocalOneFile() throws Exception {
        objOptions = new JADEOptions();
        objOptions.settings.Value(SETTINGS_FILE);
        objOptions.profile.Value("https_proxy_gmx_2_local_one_file");
        this.execute(objOptions);
    }

    @Test
    public void testProfileHttpsHomer2LocalOneFile() throws Exception {
        objOptions = new JADEOptions();
        objOptions.settings.Value(SETTINGS_FILE);
        objOptions.profile.Value("https_homer_2_local_one_file");
        this.execute(objOptions);
    }

    @Test
    public void testProfileHttpHomer2LocalOneFile() throws Exception {
        objOptions = new JADEOptions();
        objOptions.settings.Value(SETTINGS_FILE);
        objOptions.profile.Value("http_homer_2_local_one_file");
        this.execute(objOptions);
    }

    private void execute(JADEOptions options) throws Exception {
        try {
            objJadeEngine = new JadeEngine(options);
            objJadeEngine.Execute();
        } catch (Exception ex) {
            throw ex;
        } finally {
            objJadeEngine.Logout();
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
        objTestOptions.SourceDir.Value("R:/backup/sos/java/doxygen-docs/");
        objTestOptions.TargetDir.Value(TARGET_OF_DOXYGEN_DOCS);
        super.testBigCopy();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testBigCopy2() throws Exception {
        objTestOptions.SourceDir.Value("R:/backup/sos/java/doxygen-docs/com.sos.VirtualFileSystem/");
        objTestOptions.TargetDir.Value(TARGET_OF_DOXYGEN_DOCS + "com.sos.VirtualFileSystem/");
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
        objTestOptions.Target().proxy_host.Value("proxy.sos");
        objTestOptions.Target().proxy_port.Value("3128");
        super.testSend();
    }

    @Override
    @Test
    public void testSend2() throws Exception {
        objTestOptions.TargetDir.Value("/webdav/kb/");
        objTestOptions.Target().host.Value("http://homer.sos/webdav");
        objTestOptions.Target().port.value(8080);
        objTestOptions.Target().user.Value("test");
        objTestOptions.Target().password.Value("12345");
        super.testSend();
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    public void testSend3() throws Exception {
        objTestOptions.TargetDir.Value("/jade/403");
        objTestOptions.Target().host.Value("http://homer.sos/jade/");
        objTestOptions.Target().port.value(8080);
        objTestOptions.Target().user.Value("test");
        objTestOptions.Target().password.Value("12345");
        super.testSend();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendViaMonkAsProxy() throws Exception {
        objTestOptions.TargetDir.Value("/webdav2/kb/");
        objTestOptions.Target().host.Value("http://homer.sos/webdav2/");
        objTestOptions.Target().port.value(8080);
        objTestOptions.Target().user.Value("test");
        objTestOptions.Target().password.Value("12345");
        objTestOptions.Target().proxy_host.Value("proxy.sos");
        objTestOptions.Target().proxy_port.Value("3128");
        super.testSend();
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    public void testSendViaUnknownProxy() throws Exception {
        objTestOptions.TargetDir.Value("/webdav2/kb/");
        objTestOptions.Target().host.Value("http://homer.sos/webdav2");
        objTestOptions.Target().port.value(8080);
        objTestOptions.Target().user.Value("test");
        objTestOptions.Target().password.Value("12345");
        objTestOptions.Target().proxy_host.Value("proxi.sos");
        objTestOptions.Target().proxy_port.Value("3128");
        super.testSend();
    }

    @Override
    @Test
    public void testSendFileSpec() throws Exception {
        objTestOptions.recursive.value(false);
        objTestOptions.file_spec.Value("^test.*\\.txt$");
        super.testSendFileSpec2();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendRecursive() throws Exception {
        objTestOptions.TargetDir.Value("/webdav/kb");
        objTestOptions.Target().host.Value("http://homer.sos/webdav");
        objTestOptions.Target().port.value(8080);
        objTestOptions.Target().user.Value("test");
        objTestOptions.Target().password.Value("12345");
        objTestOptions.file_spec.Value("^test.txt$");
        objTestOptions.recursive.value(true);
        super.testSendFileSpec2();
    }

    @Test
    public void testSendRecursive2() throws Exception {
        objTestOptions.SourceDir.Value("R:/nobackup/junittests/testdata/JADE/recursive");
        objTestOptions.TargetDir.Value("/webdav/kb");
        objTestOptions.Target().host.Value("http://homer.sos/webdav");
        objTestOptions.Target().port.value(8080);
        objTestOptions.Target().user.Value("test");
        objTestOptions.Target().password.Value("12345");
        objTestOptions.file_spec.Value("\\.(txt|dot)$");
        objTestOptions.recursive.value(true);
        super.testSendFileSpec2();
    }

    @Override
    @Test
    public void testCopyAndRenameSourceAndTarget() throws Exception {
        objTestOptions.TargetDir.Value("/webdav/kb");
        objTestOptions.Target().host.Value("http://homer.sos/webdav/");
        objTestOptions.Target().port.value(8080);
        objTestOptions.Target().user.Value("test");
        objTestOptions.Target().password.Value("12345");
        super.testCopyAndRenameSourceAndTarget();
    }

    @Test
    public void testCopyAndRenameSourceAndTarget2() throws Exception {
        super.testCopyAndRenameSourceAndTarget();
    }
}
