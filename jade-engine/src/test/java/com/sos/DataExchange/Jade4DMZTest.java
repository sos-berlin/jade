package com.sos.DataExchange;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.DataExchange.Options.JADEOptions;

/** @author KB */
public class Jade4DMZTest {

    protected JADEOptions objO = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(Jade4DMZTest.class);
    private static final String PATH_OF_TEST_INIS = "R:/backup/sos/java/development/SOSDataExchange/examples/";

    @Test
    @Ignore("Test set to Ignore for later examination")
    public final void testExecute() {
        Jade4DMZ objJ = new Jade4DMZ();
        objO = objJ.getOptions();
        objO.settings.setValue(PATH_OF_TEST_INIS + "jade_jumpHost_re.ini");
        objO.profile.setValue("jump_test_send");
        objO.readSettingsFile();
        objJ.Execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public final void testcopyfrominternet() {
        Jade4DMZ objJ = new Jade4DMZ();
        objO = objJ.getOptions();
        objO.settings.setValue(PATH_OF_TEST_INIS + "jade_jumpHost.ini");
        objO.profile.setValue("jump_test_copy_from_internet");
        objO.readSettingsFile();
        objJ.Execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public final void testcopytointernet() {
        Jade4DMZ objJ = new Jade4DMZ();
        objO = objJ.getOptions();
        objO.settings.setValue(PATH_OF_TEST_INIS + "jade_jumpHost.ini");
        objO.profile.setValue("jump_test_copy_to_internet");
        objO.readSettingsFile();
        objJ.Execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public final void testJumpTestCopyToInternetHttpProxy() {
        Jade4DMZ objJ = new Jade4DMZ();
        objO = objJ.getOptions();
        objO.settings.setValue(PATH_OF_TEST_INIS + "jade_jumpHost.ini");
        objO.profile.setValue("jump_test_copy_to_internet_http_proxy");
        objO.readSettingsFile();
        objJ.Execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public final void testJumpTestCopyToInternetSocks5Proxy() {
        Jade4DMZ objJ = new Jade4DMZ();
        objO = objJ.getOptions();
        objO.settings.setValue(PATH_OF_TEST_INIS + "jade_jumpHost.ini");
        objO.profile.setValue("jump_test_copy_to_internet_socks5_proxy");
        objO.readSettingsFile();
        objJ.Execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public final void testsendusingdmz() {
        Jade4DMZ objJ = new Jade4DMZ();
        objO = objJ.getOptions();
        objO.settings.setValue(PATH_OF_TEST_INIS + "jade_jumpHost.ini");
        objO.profile.setValue("jump_test_sendusingdmz");
        objO.readSettingsFile();
        objJ.Execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public final void testsendusingdmzOH() {
        Jade4DMZ objJ = new Jade4DMZ();
        objO = objJ.getOptions();
        objO.settings.setValue(PATH_OF_TEST_INIS + "jade_jumpHost.ini");
        objO.profile.setValue("jumphost_test_receive");
        objO.readSettingsFile();
        objJ.Execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public final void testreceiveusingdmz() {
        Jade4DMZ objJ = new Jade4DMZ();
        objO = objJ.getOptions();
        objO.settings.setValue(PATH_OF_TEST_INIS + "jade_jumpHost.ini");
        objO.profile.setValue("jump_test_receiveusingdmz");
        objO.readSettingsFile();
        objJ.Execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public final void testExecute3() throws Exception {
        Jade4DMZ objJ = new Jade4DMZ();
        objO = objJ.getOptions();
        objO.settings.setValue(PATH_OF_TEST_INIS + "jade_jumpHost.ini");
        objO.profile.setValue("jump_test_send");
        objO.readSettingsFile();
        String strB = objO.getOptionsAsCommandLine();
        objO.settings.setValue(null);
        objO.profile.setValue(null);
        String strC = objO.getOptionsAsCommandLine();
        LOGGER.info(strB);
        LOGGER.info(strC);
        JADEOptions objNewO = new JADEOptions();
        objNewO.commandLineArgs(strC.split(" "));
        LOGGER.info("\nobjNewO : " + objNewO.getOptionsAsCommandLine());
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public final void testExecuteDeepCopy() throws Exception {
        Jade4DMZ objJ = new Jade4DMZ();
        objO = objJ.getOptions();
        objO.settings.setValue(PATH_OF_TEST_INIS + "jade_jumpHost.ini");
        objO.profile.setValue("jump_test_send");
        objO.readSettingsFile();
        JADEOptions objNewO = (JADEOptions) objO.deepCopy(objO);
        objO.settings.setValue("");
        objO.profile.setValue("");
        String strC = objNewO.getOptionsAsCommandLine();
        LOGGER.info(strC);
    }

}