package com.sos.DataExchange;

import org.junit.Before;
import org.junit.Test;

import com.sos.DataExchange.Options.JADEOptions;

public class JadeTestsSFTP extends JadeTestBase {

    private JADEOptions options;

    public JadeTestsSFTP() {
    }

    @Before
    public void setUp() throws Exception {
        options = new JADEOptions();
        options.settings.setValue("R:/backup/sos/java/development/SOSDataExchange/examples/jade_sftp_settings.ini");
    }

    @Test
    public void testLocal2sftp() throws Exception {
        options.profile.setValue("local_2_sftp");
        this.execute(options);
    }

    @Test
    public void testLocal2sftpCheckSteady() throws Exception {
        options.profile.setValue("local_2_sftp_check_steady");
        this.execute(options);
    }

    @Test
    public void testHttpProxyLocal2sftp() throws Exception {
        options.profile.setValue("http_proxy_local_2_sftp");
        this.execute(options);
    }

    @Test
    public void testSocks5ProxyLocal2sftp() throws Exception {
        options.profile.setValue("socks5_proxy_local_2_sftp");
        this.execute(options);
    }

    private void execute(JADEOptions options) throws Exception {
        try {
            objJadeEngine = new JadeEngine(options);
            objJadeEngine.execute();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (objJadeEngine != null) {
                objJadeEngine.logout();
            }
        }
    }

}
