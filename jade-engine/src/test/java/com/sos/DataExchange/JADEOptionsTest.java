package com.sos.DataExchange;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;
import com.sos.JSHelper.io.Files.JSFile;

public class JADEOptionsTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(JADEOptionsTest.class);
    private JADEOptions objO = null;
    private String constrSettingsTestFile = "";

    public JADEOptionsTest() {
        //
    }

    @Before
    public void setUp() throws Exception {
        objO = new JADEOptions();
        objO.fileSpec.setValue(".*");
    }

    @Test
    @Ignore("Test set to Ignore for later examination, fails in Jenkins build")
    public void testLoadSettingsFile() {
        CreateSettingsFile();
        readProfile("globals");
        assertEquals("user id must be kb", "kb", objO.user.getValue());
        LOGGER.info(objO.user.getToolTip());
        readProfile("include-Test");
        assertEquals("user id must be kb", "kb", objO.user.getValue());
        assertEquals("host must be hostFromInclude1", "hostFromInclude1", objO.host.getValue());
        assertTrue("host must be protected", objO.host.isProtected());
        assertTrue("user id must be protected", objO.user.isProtected());
        LOGGER.info(objO.host.getToolTip());
        objO.host.setValue("localhost");
        assertEquals("host must be localhost", "localhost", objO.host.getValue());
        readProfile("include-Test2");
        assertEquals("user id must be testtest", "testtest", objO.user.getValue());
        assertEquals("host must be willi", "willi", objO.host.getValue());
        assertFalse("host must be protected", objO.host.isProtected());
        assertFalse("user id must be protected", objO.user.isProtected());
        LOGGER.info(objO.host.getToolTip() + "\n\n");
        readProfile("substitute-Test");
        assertEquals("title must be this_is_temp", "this_is_temp", objO.title.getValue());
        readProfile("include4");
        assertTrue("host must be protected", objO.host.isProtected());
        assertTrue("port id must be protected", objO.port.isProtected());
        assertEquals("port ", "88", objO.port.getValue());
        assertEquals("host ", "hostFromInclude5", objO.host.getValue());
        readProfile("external_includes");
        assertEquals("host ", "externalFileHost", objO.host.getValue());
        LOGGER.info(objO.host.getToolTip() + "\n\n");
        assertEquals("user id must be testtest", "test", objO.user.getValue());
        LOGGER.info(objO.user.getToolTip() + "\n\n");
        readProfile("include_as_source");
        assertEquals("host ", "hostFromInclude5", objO.getSource().host.getValue());
        LOGGER.info(objO.host.getToolTip() + "\n\n");
        assertEquals("user id must be testtest", "test", objO.getSource().user.getValue());
        LOGGER.info(objO.user.getToolTip() + "\n\n");
        readProfile("include_as_target");
        assertEquals("host ", "hostFromInclude5", objO.getTarget().host.getValue());
        LOGGER.info(objO.host.getToolTip() + "\n\n");
        assertEquals("user id must be testtest", "test", objO.getTarget().user.getValue());
        LOGGER.info(objO.user.getToolTip() + "\n\n");
    }

    private void readProfile(final String pstrProfileName) {
        objO = new JADEOptions();
        objO.settings.setValue(constrSettingsTestFile);
        objO.profile.setValue(pstrProfileName);
        objO.readSettingsFile();
    }

    private void CreateSettingsFile() {
        constrSettingsTestFile = objO.getTempDir() + "JADEOptionsTest.ini";
        JSFile objIni = new JSFile(constrSettingsTestFile);
        objIni.deleteOnExit();
        String constrSettingsTestFile2 = objO.getTempDir() + "JADEOptionsTest2.ini";
        JSFile objIni2 = new JSFile(constrSettingsTestFile2);
        objIni2.deleteOnExit();
        try {
            objIni.writeLine("[globals]").writeLine("user=kb").writeLine("password=kb").writeLine("temp=this_is_temp").writeLine("[include1]")
                .writeLine("host=hostFromInclude1").writeLine("[include2]").writeLine("port=88").writeLine("[include3]").writeLine("protocol=scp")
                .writeLine("[include5]").writeLine("protocol=scp").writeLine("host=hostFromInclude5").writeLine("user=test").writeLine("[include4]")
                .writeLine("protocol=sftp").writeLine("include=include1,include2,include5").writeLine("[include_as_source]")
                .writeLine("source_include=include4").writeLine("[include_as_target]").writeLine("target_include=include4").writeLine("[include-Test]")
                .writeLine("include=include1,include2,include3").writeLine("[include-TestWithNonexistenceInclude]")
                .writeLine("include=include1,includeabcd2,include3").writeLine("[include-Test2]").writeLine("include=include1,include2,include3")
                .writeLine("host=willi").writeLine("user=testtest").writeLine("[substitute-Test]").writeLine("user=${USERNAME}")
                .writeLine("host=${COMPUTERNAME}").writeLine("title=${temp}").writeLine("cannotsubstitutet=${waltraut}")
                .writeLine("target_host=${host}-abc").writeLine("target_host=${host}").writeLine("alternate_target_host=${host}-abc")
                .writeLine("[external_includes]").writeLine("include=file:" + constrSettingsTestFile2 + "\\globals,file:" + "./JADEOptionsTest2.ini"
                + "/include1").close();
            objIni2.writeLine("[globals]").writeLine("user=test").writeLine("password=testtest").writeLine("temp=this_is_temp")
                .writeLine("[include1]").writeLine("host=externalFileHost").close();
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    @Test
    public void testReplaceWhatNull() {
        objO.getTarget().protocol.setValue(enuTransferTypes.ftp);
        objO.ReplaceWhat.setNull();
        objO.ReplaceWith.setNull();
        objO.checkMandatory();
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    public void testReplaceWhatNull2() {
        objO.getTarget().protocol.setValue(enuTransferTypes.ftp);
        objO.ReplaceWhat.setNull();
        objO.ReplaceWith.setValue("Hello JADE");
        objO.checkMandatory();
    }

    @Test
    public void testReplaceWithNull() {
        objO.getTarget().protocol.setValue(enuTransferTypes.ftp);
        objO.ReplaceWhat.setValue("Hello World");
        objO.ReplaceWith.setNull();
        objO.checkMandatory();
    }

    @Test
    public void testReplacement() {
        objO.getTarget().protocol.setValue(enuTransferTypes.ftp);
        objO.ReplaceWhat.setValue("Hello World");
        objO.ReplaceWith.setValue("");
        objO.checkMandatory();
    }

    @Test
    public void testReplacement2() {
        objO.getTarget().protocol.setValue(enuTransferTypes.ftp);
        objO.ReplaceWhat.setValue("Hello World");
        objO.ReplaceWith.setValue("Hello JADE");
        objO.checkMandatory();
    }

    @Test
    public void testOperation() {
        objO.operation.setValue("remove");
        assertEquals("testOperation", "delete", objO.operation.getValue());
    }

    @Test
    public void testSourceDir() {
        objO.sourceDir.setValue("test-[date:yyyy-MM-dd]");
        LOGGER.info(objO.sourceDir.getValue());
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    public void testOperationIsIllegal() {
        objO.operation.setValue("xyzddd");
        LOGGER.info("" + objO.operation.value());
    }

}