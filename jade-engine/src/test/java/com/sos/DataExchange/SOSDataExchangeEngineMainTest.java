package com.sos.DataExchange;

import org.junit.Ignore;
import org.junit.Test;

public class SOSDataExchangeEngineMainTest {

    private static final String SETTINGS_FILE = "/tmp/settings.xml";
    private static final String PROFILE = "profile";

    @Ignore
    @Test
    public void testYadeWithProvider() throws Exception {
        String[] args = new String[3];
        args[0] = "-settings=\"" + SETTINGS_FILE + "\"";
        args[1] = "-profile=" + PROFILE;
        args[2] = "-smb_provider=jcifs";
        SOSDataExchangeEngineMain.main(args);
    }

    @Ignore
    @Test
    public void testYade() throws Exception {
        String[] args = new String[2];
        args[0] = "-settings=\"" + SETTINGS_FILE + "\"";
        args[1] = "-profile=" + PROFILE;
        SOSDataExchangeEngineMain.main(args);
    }

    @Ignore
    @Test
    public void testYadeDMZ() throws Exception {
        String[] args = new String[2];
        args[0] = "-settings=\"" + SETTINGS_FILE + "\"";
        args[1] = "-profile=" + PROFILE;
        SOSDataExchangeEngine4DMZMain.main(args);
    }

    @Ignore
    @Test
    public void testYadeCLI() throws Exception {
        String[] args = new String[14];
        args[0] = "-SendTransferHistory=false";
        args[1] = "-file_spec=\".*\\.txt\"";
        args[2] = "-operation=\"copy\"";
        args[3] = "-transactional=\"true\"";

        args[4] = "-source_protocol=\"sftp\"";
        args[5] = "-source_host=\"<sftp server>\"";
        args[6] = "-source_port=\"22\"";
        args[7] = "-source_user=\"<sftp user>\"";
        args[8] = "-source_password=\"<sftp user password>\"";
        args[9] = "-source_ssh_auth_method=\"password\"";
        args[10] = "-source_dir=\"/tmp/source\"";

        args[11] = "-target_protocol=\"local\"";
        args[12] = "-target_host=\"localhost\"";
        args[13] = "-target_dir=\"C:/tmp/target\"";

        SOSDataExchangeEngineMain.main(args);
    }

}
