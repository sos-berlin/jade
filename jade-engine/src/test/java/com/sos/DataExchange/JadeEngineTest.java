package com.sos.DataExchange;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sos.DataExchange.Options.JADEOptions;

public class JadeEngineTest {

    private static final String SETTINGS_FILE = "R:/backup/sos/java/development/SOSDataExchange/examples/jade_sftp_settings.xml";
    private static final String PROFILE = "local_2_sftp";
    private JADEOptions options;

    public JadeEngineTest() {
    }

    @Before
    public void setUp() throws Exception {
        options = new JADEOptions();
        options.settings.setValue(SETTINGS_FILE);
    }

    @Ignore
    @Test
    public void test1() throws Exception {
        options.profile.setValue(PROFILE);
        this.execute(options);
    }

    @Ignore
    @Test
    public void test2() throws Exception {
        options.profile.setValue(PROFILE);
        options.readSettingsFile();
    }

    private void execute(JADEOptions options) throws Exception {
        JadeEngine engine = new JadeEngine(options);
        engine.execute();
    }

}
