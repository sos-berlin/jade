package com.sos.DataExchange;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;
import com.sos.JSHelper.io.Files.JSFile;

public class JADEOptionsTest {

    private final String conClassName = "JADEOptionsTest";
    private final static Logger LOGGER = Logger.getLogger(JADEOptionsTest.class);
    private JADEOptions objO = null;
    private String constrSettingsTestFile = "";

    public JADEOptionsTest() {
        //
    }

    @Before
    public void setUp() throws Exception {
        objO = new JADEOptions();
        objO.file_spec.Value(".*");
    }

    @Test
    public void testCheckMandatory() {
        // fail("Not yet implemented");
    }

    @Test
    public void testJADEOptions() {
        // fail("Not yet implemented");
    }

    @Test
    public void testJADEOptionsJSListener() {
        // fail("Not yet implemented");
    }

    @Test
    public void testJADEOptionsHashMapOfStringString() {
        // fail("Not yet implemented");
    }

    @Test
    public void testReadSettingsFile() {
        // fail("Not yet implemented");
    }

    @Test
    public void testSubstituteVariablesStringPropertiesStringString() {
        // fail("Not yet implemented");
    }

    @Test
    @Ignore("Test set to Ignore for later examination, fails in Jenkins build")
    public void testLoadSettingsFile() {
        CreateSettingsFile();
        readProfile("globals");
        assertEquals("user id must be kb", "kb", objO.user.Value());
        LOGGER.info(objO.user.getToolTip());
        readProfile("include-Test");
        assertEquals("user id must be kb", "kb", objO.user.Value());
        assertEquals("host must be hostFromInclude1", "hostFromInclude1", objO.host.Value());
        assertTrue("host must be protected", objO.host.isProtected());
        assertTrue("user id must be protected", objO.user.isProtected());
        LOGGER.info(objO.host.getToolTip());
        objO.host.Value("localhost");
        assertEquals("host must be localhost", "localhost", objO.host.Value());
        readProfile("include-Test2");
        assertEquals("user id must be testtest", "testtest", objO.user.Value());
        assertEquals("host must be willi", "willi", objO.host.Value());
        assertFalse("host must be protected", objO.host.isProtected());
        assertFalse("user id must be protected", objO.user.isProtected());
        LOGGER.info(objO.host.getToolTip() + "\n\n");
        readProfile("substitute-Test");
        assertEquals("title must be this_is_temp", "this_is_temp", objO.title.Value());
        readProfile("include4");
        assertTrue("host must be protected", objO.host.isProtected());
        assertTrue("port id must be protected", objO.port.isProtected());
        assertEquals("port ", "88", objO.port.Value());
        assertEquals("host ", "hostFromInclude5", objO.host.Value());
        readProfile("external_includes");
        assertEquals("host ", "externalFileHost", objO.host.Value());
        LOGGER.info(objO.host.getToolTip() + "\n\n");
        assertEquals("user id must be testtest", "test", objO.user.Value());
        LOGGER.info(objO.user.getToolTip() + "\n\n");
        readProfile("include_as_source");
        assertEquals("host ", "hostFromInclude5", objO.Source().host.Value());
        LOGGER.info(objO.host.getToolTip() + "\n\n");
        assertEquals("user id must be testtest", "test", objO.Source().user.Value());
        LOGGER.info(objO.user.getToolTip() + "\n\n");
        readProfile("include_as_target");
        assertEquals("host ", "hostFromInclude5", objO.Target().host.Value());
        LOGGER.info(objO.host.getToolTip() + "\n\n");
        assertEquals("user id must be testtest", "test", objO.Target().user.Value());
        LOGGER.info(objO.user.getToolTip() + "\n\n");
    }

    private void readProfile(final String pstrProfileName) {
        objO = new JADEOptions();
        objO.settings.Value(constrSettingsTestFile);
        objO.profile.Value(pstrProfileName);
        objO.ReadSettingsFile();
    }

    private void CreateSettingsFile() {
        constrSettingsTestFile = objO.TempDir() + "JADEOptionsTest.ini";
        JSFile objIni = new JSFile(constrSettingsTestFile);
        objIni.deleteOnExit();
        String constrSettingsTestFile2 = objO.TempDir() + "JADEOptionsTest2.ini";
        JSFile objIni2 = new JSFile(constrSettingsTestFile2);
        objIni2.deleteOnExit();
        try {
            objIni.WriteLine("[globals]").WriteLine("user=kb").WriteLine("password=kb").WriteLine("temp=this_is_temp").WriteLine("[include1]").WriteLine("host=hostFromInclude1").WriteLine("[include2]").WriteLine("port=88").WriteLine("[include3]").WriteLine("protocol=scp").WriteLine("[include5]").WriteLine("protocol=scp").WriteLine("host=hostFromInclude5").WriteLine("user=test").WriteLine("[include4]").WriteLine("protocol=sftp").WriteLine("include=include1,include2,include5").WriteLine("[include_as_source]").WriteLine("source_include=include4").WriteLine("[include_as_target]").WriteLine("target_include=include4").WriteLine("[include-Test]").WriteLine("include=include1,include2,include3").WriteLine("[include-TestWithNonexistenceInclude]").WriteLine("include=include1,includeabcd2,include3").WriteLine("[include-Test2]").WriteLine("include=include1,include2,include3").WriteLine("host=willi").WriteLine("user=testtest").WriteLine("[substitute-Test]").WriteLine("user=${USERNAME}").WriteLine("host=${COMPUTERNAME}").WriteLine("title=${temp}").WriteLine("cannotsubstitutet=${waltraut}").WriteLine("target_host=${host}-abc").WriteLine("target_host=${host}").WriteLine("alternate_target_host=${host}-abc").WriteLine("[external_includes]").WriteLine("include=file:"
                    + constrSettingsTestFile2 + "\\globals,file:" + "./JADEOptionsTest2.ini" + "/include1").close();
            objIni2.WriteLine("[globals]") //
            .WriteLine("user=test").WriteLine("password=testtest").WriteLine("temp=this_is_temp").WriteLine("[include1]").WriteLine("host=externalFileHost").close();
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    @Test
    public void testReplaceWhatNull() {
        objO.Target().protocol.Value(enuTransferTypes.ftp);
        objO.ReplaceWhat.setNull();
        objO.ReplaceWith.setNull();
        objO.CheckMandatory();
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    public void testReplaceWhatNull2() {
        objO.Target().protocol.Value(enuTransferTypes.ftp);
        objO.ReplaceWhat.setNull();
        objO.ReplaceWith.Value("Hello JADE");
        objO.CheckMandatory();
    }

    @Test
    public void testReplaceWithNull() {
        objO.Target().protocol.Value(enuTransferTypes.ftp);
        objO.ReplaceWhat.Value("Hello World");
        objO.ReplaceWith.setNull();
        objO.CheckMandatory();
    }

    @Test
    public void testReplacement() {
        objO.Target().protocol.Value(enuTransferTypes.ftp);
        objO.ReplaceWhat.Value("Hello World");
        objO.ReplaceWith.Value("");
        objO.CheckMandatory();
    }

    @Test
    public void testReplacement2() {
        objO.Target().protocol.Value(enuTransferTypes.ftp);
        objO.ReplaceWhat.Value("Hello World");
        objO.ReplaceWith.Value("Hello JADE");
        objO.CheckMandatory();
    }

    @Test
    public void testOperation() {
        objO.operation.Value("remove");
        assertEquals("testOperation", "delete", objO.operation.Value());
    }

    @Test
    public void testSourceDir() {
        objO.SourceDir.Value("test-[date:yyyy-MM-dd]");
        LOGGER.info(objO.SourceDir.Value());
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    public void testOperationIsIllegal() {
        objO.operation.Value("xyzddd");
        LOGGER.info(objO.operation.value());
    }

    @Test
    public final void testGetlog_filename() {
        // fail("Not yet implemented");
    }

    @Test
    public final void testSetlog_filename() {
        // fail("Not yet implemented");
    }

    @Test
    public final void testGetlog4jPropertyFileName() {
        // fail("Not yet implemented");
    }

    @Test
    public final void testSetlog4jPropertyFileName() {
        // fail("Not yet implemented");
    }
}
