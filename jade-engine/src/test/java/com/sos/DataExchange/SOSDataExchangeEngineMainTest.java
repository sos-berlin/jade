package com.sos.DataExchange;

import org.junit.Ignore;
import org.junit.Test;

public class SOSDataExchangeEngineMainTest {

    private static final String SETTINGS_FILE = "R:/backup/sos/java/development/SOSDataExchange/examples/jade_sftp_settings.xml";
    private static final String PROFILE = "local_2_sftp";

    @Ignore
    @Test
    public void test1() throws Exception {
        String[] args = new String[2];
        args[0] = "-settings=\"" + SETTINGS_FILE + "\"";
        args[1] = "-profile=" + PROFILE;
        SOSDataExchangeEngineMain.main(args);
    }

}
