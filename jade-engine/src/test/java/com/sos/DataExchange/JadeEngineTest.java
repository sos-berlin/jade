package com.sos.DataExchange;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sos.vfs.common.options.SOSBaseOptions;
import com.sos.vfs.common.options.SOSProviderOptions;
import com.sos.vfs.sftp.SOSSFTP;
import com.sos.vfs.sftp.SOSSFTP.SSHProvider;
import com.sos.vfs.webdav.SOSWebDAV.WebDAVProvider;

public class JadeEngineTest {

    private static final String SETTINGS_FILE = "D://Temp/settings.xml";
    private static final String PROFILE = "copy";
    private SOSBaseOptions options;

    public JadeEngineTest() {
    }

    @Before
    public void setUp() throws Exception {
        options = new SOSBaseOptions();
        options.settings.setValue(SETTINGS_FILE);
    }

    @Ignore
    @Test
    public void test() throws Exception {
        // options.ssh_provider.setValue(SSHProvider.SSHJ.name());
        options.profile.setValue(PROFILE);
        this.execute(options);
    }

    @Ignore
    @Test
    public void testWebDavJackrabbit() throws Exception {
        options.webdav_provider.setValue(WebDAVProvider.JACKRABBIT.name());
        options.profile.setValue(PROFILE);
        this.execute(options);
    }

    @Ignore
    @Test
    public void testWebDavWebdavclient4j() throws Exception {
        options.webdav_provider.setValue(WebDAVProvider.WEBDAVCLIENT4J.name());
        options.profile.setValue(PROFILE);
        this.execute(options);
    }

    @Ignore
    @Test
    public void testSSHProvider() throws Exception {
        options.ssh_provider.setValue(SSHProvider.SSHJ.name());
        options.profile.setValue(PROFILE);

        SOSDataExchangeEngine engine = new SOSDataExchangeEngine(options);
        SOSProviderOptions pOptions = engine.getOptions().getSource();
        // pOptions.useZlibCompression.setValue("true");

        SOSSFTP p = new SOSSFTP(options.ssh_provider);
        try {
            p.connect(pOptions);

            String source = "D:/Temp/my.exe";
            String target = "/home/sos/my.exe";

            p.putFile(source, target);
            p.get(target, source + ".get.exe");

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            p.disconnect();
        }
    }

    @Ignore
    @Test
    public void testDMZ() throws Exception {
        String[] args = new String[3];
        args[0] = "-settings=\"" + SETTINGS_FILE + "\"";
        args[1] = "-profile=" + PROFILE;
        args[2] = "-ssh_provider=" + SSHProvider.SSHJ.name();
        SOSDataExchangeEngine4DMZMain.main(args);
    }

    private void execute(SOSBaseOptions options) throws Exception {
        SOSDataExchangeEngine engine = new SOSDataExchangeEngine(options);
        engine.execute();
    }

}
