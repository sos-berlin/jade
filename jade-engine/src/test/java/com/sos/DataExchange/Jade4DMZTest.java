package com.sos.DataExchange;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import com.sos.DataExchange.Options.JADEOptions;

/** @author KB */
public class Jade4DMZTest {

    protected JADEOptions objO = null;
    private static final Logger LOGGER = Logger.getLogger(Jade4DMZTest.class);
    private static final String PATH_OF_TEST_INIS = "R:/backup/sos/java/development/SOSDataExchange/examples/";

    /** Test method for {@link com.sos.DataExchange.Jade4DMZ#Jade4DMZ()}. */
    @Test
    public final void testJade4DMZ() {
        // fail("Not yet implemented");
    }

    /** Test method for
     * {@link com.sos.DataExchange.Jade4DMZ#Jade4DMZ(java.util.Properties)}. */
    @Test
    public final void testJade4DMZProperties() {
        // fail("Not yet implemented");
    }

    /** Test method for
     * {@link com.sos.DataExchange.Jade4DMZ#Jade4DMZ(com.sos.VirtualFileSystem.Options.JADEOptions)}
     * . */
    @Test
    public final void testJade4DMZJADEOptions() {
        // fail("Not yet implemented");
    }

    /** Test method for
     * {@link com.sos.DataExchange.Jade4DMZ#Jade4DMZ(java.util.HashMap)}. */
    @Test
    public final void testJade4DMZHashMapOfStringString() {
        // fail("Not yet implemented");
    }

    /** Test method for {@link com.sos.DataExchange.Jade4DMZ#Options()}. */
    @Test
    public final void testOptions() {
        // fail("Not yet implemented");
    }

    /** Test method for {@link com.sos.DataExchange.Jade4DMZ#Execute()}. */
    @Test
    @Ignore("Test set to Ignore for later examination")
    public final void testExecute() {
        Jade4DMZ objJ = new Jade4DMZ();
        objO = objJ.Options();
        objO.settings.Value(PATH_OF_TEST_INIS + "jade_jumpHost_re.ini");
        objO.profile.Value("jump_test_send");
        objO.ReadSettingsFile();
        objJ.Execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public final void testcopyfrominternet() {
        Jade4DMZ objJ = new Jade4DMZ();
        objO = objJ.Options();
        objO.settings.Value(PATH_OF_TEST_INIS + "jade_jumpHost.ini");
        objO.profile.Value("jump_test_copy_from_internet");
        objO.ReadSettingsFile();
        objJ.Execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public final void testcopytointernet() {
        Jade4DMZ objJ = new Jade4DMZ();
        objO = objJ.Options();
        objO.settings.Value(PATH_OF_TEST_INIS + "jade_jumpHost.ini");
        objO.profile.Value("jump_test_copy_to_internet");
        objO.ReadSettingsFile();
        objJ.Execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public final void testJumpTestCopyToInternetHttpProxy() {
        Jade4DMZ objJ = new Jade4DMZ();
        objO = objJ.Options();
        objO.settings.Value(PATH_OF_TEST_INIS + "jade_jumpHost.ini");
        objO.profile.Value("jump_test_copy_to_internet_http_proxy");
        objO.ReadSettingsFile();
        objJ.Execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public final void testJumpTestCopyToInternetSocks5Proxy() {
        Jade4DMZ objJ = new Jade4DMZ();
        objO = objJ.Options();
        objO.settings.Value(PATH_OF_TEST_INIS + "jade_jumpHost.ini");
        objO.profile.Value("jump_test_copy_to_internet_socks5_proxy");
        objO.ReadSettingsFile();
        objJ.Execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public final void testsendusingdmz() {
        Jade4DMZ objJ = new Jade4DMZ();
        objO = objJ.Options();
        objO.settings.Value(PATH_OF_TEST_INIS + "jade_jumpHost.ini");
        objO.profile.Value("jump_test_sendusingdmz");
        objO.ReadSettingsFile();
        objJ.Execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public final void testsendusingdmzOH() {
        Jade4DMZ objJ = new Jade4DMZ();
        objO = objJ.Options();
        objO.settings.Value(PATH_OF_TEST_INIS + "jade_jumpHost.ini");
        objO.profile.Value("jumphost_test_receive");
        objO.ReadSettingsFile();
        objJ.Execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public final void testreceiveusingdmz() {
        Jade4DMZ objJ = new Jade4DMZ();
        objO = objJ.Options();
        objO.settings.Value(PATH_OF_TEST_INIS + "jade_jumpHost.ini");
        objO.profile.Value("jump_test_receiveusingdmz");
        objO.ReadSettingsFile();
        objJ.Execute();
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public final void testExecute3() throws Exception {
        Jade4DMZ objJ = new Jade4DMZ();
        objO = objJ.Options();
        objO.settings.Value(PATH_OF_TEST_INIS + "jade_jumpHost.ini");
        objO.profile.Value("jump_test_send");
        objO.ReadSettingsFile();
        String strB = objO.getOptionsAsCommandLine();
        objO.settings.Value(null);
        objO.profile.Value(null);
        String strC = objO.getOptionsAsCommandLine();
        LOGGER.info(strB);
        LOGGER.info(strC);
        JADEOptions objNewO = new JADEOptions();
        objNewO.CommandLineArgs(strC.split(" "));
        LOGGER.info("\nobjNewO : " + objNewO.getOptionsAsCommandLine());
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public final void testExecuteDeepCopy() throws Exception {
        Jade4DMZ objJ = new Jade4DMZ();
        objO = objJ.Options();
        objO.settings.Value(PATH_OF_TEST_INIS + "jade_jumpHost.ini");
        objO.profile.Value("jump_test_send");
        objO.ReadSettingsFile();
        JADEOptions objNewO = (JADEOptions) objO.deepCopy(objO);
        objO.settings.Value("");
        objO.profile.Value("");
        String strC = objNewO.getOptionsAsCommandLine();
        LOGGER.info(strC);
    }

}
