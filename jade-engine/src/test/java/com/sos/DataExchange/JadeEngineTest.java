package com.sos.DataExchange;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sos.vfs.common.options.SOSBaseOptions;
import com.sos.vfs.sftp.SOSSFTP.SSHProvider;

public class JadeEngineTest {

    private static final String SETTINGS_FILE = "R:/backup/sos/java/development/SOSDataExchange/examples/jade_sftp_settings.xml";
    private static final String PROFILE = "copy_sftp_localhost_2_local";
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
        options.ssh_provider.setValue(SSHProvider.SSHJ.name());
        options.profile.setValue(PROFILE);
        this.execute(options);
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
