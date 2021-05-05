package com.sos.DataExchange;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sos.vfs.common.options.SOSBaseOptions;

public class JadeEngineTest {

    private static final String SETTINGS_FILE = "R:/backup/sos/java/development/SOSDataExchange/examples/jade_sftp_settings.xml";
    private static final String PROFILE = "local_2_sftp";
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
    public void test1() throws Exception {
        options.profile.setValue(PROFILE);
        this.execute(options);
    }

    private void execute(SOSBaseOptions options) throws Exception {
        SOSDataExchangeEngine engine = new SOSDataExchangeEngine(options);
        engine.execute();
    }

}
