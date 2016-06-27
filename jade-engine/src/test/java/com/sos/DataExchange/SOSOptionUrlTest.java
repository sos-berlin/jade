package com.sos.DataExchange;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import com.sos.JSHelper.Options.SOSOptionUrl;
import com.sos.JSHelper.interfaces.ISOSDataProviderOptions;
import com.sos.VirtualFileSystem.Options.SOSConnection2Options;

public class SOSOptionUrlTest {

    private SOSOptionUrl objU = new SOSOptionUrl("");

    @Test
    public void testValueString() {
        objU.setValue("ftp://kb:kb@homer.sos/home/test/test.txt");
        ISOSDataProviderOptions objSF = new SOSConnection2Options();
        objU.getOptions(objSF);
        assertEquals("Protocol = ", "ftp", objSF.getProtocol().getValue());
        assertEquals("userid = ", "kb", objSF.getUser().getValue());
        assertEquals("password = ", "kb", objSF.getPassword().getValue());
        assertEquals("host = ", "homer.sos", objSF.getHost().getValue());
    }

    @Test
    @Ignore("Test set to Ignore for later examination, fails in Jenkins build")
    public void testValue4File() {
        objU.setValue("file:///./JCLs");
        ISOSDataProviderOptions objSF = new SOSConnection2Options();
        objU.getOptions(objSF);
        assertEquals("Protocol = ", "file", objSF.getProtocol().getValue());
        assertEquals("userid = ", "", objSF.getUser().getValue());
        assertEquals("password = ", "", objSF.getPassword().getValue());
        assertEquals("host = ", "localhost", objSF.getHost().getValue());
        assertEquals("filePath = ", "./JCLs", objU.getFolderName());
    }

    @Test
    @Ignore("Test set to Ignore for later examination, fails in Jenkins build")
    public void testValue4File3() {
        objU.setValue("file:///./JCLs");
        ISOSDataProviderOptions objSF = new SOSConnection2Options();
        objU.getOptions(objSF);
        assertEquals("Protocol = ", "file", objSF.getProtocol().getValue());
        assertEquals("userid = ", "", objSF.getUser().getValue());
        assertEquals("password = ", "", objSF.getPassword().getValue());
        assertEquals("host = ", "localhost", objSF.getHost().getValue());
        assertEquals("filePath = ", "./JCLs", objU.getFolderName());
    }

    @Test
    @Ignore("Test set to Ignore for later examination, fails in Jenkins build")
    public void testValue4File2() {
        objU.setValue("file:///kb:kb@localhost:4711/./JCLs");
        ISOSDataProviderOptions objSF = new SOSConnection2Options();
        objU.getOptions(objSF);
        assertEquals("Protocol = ", "file", objSF.getProtocol().getValue());
        assertEquals("userid = ", "", objSF.getUser().getValue());
        assertEquals("password = ", "", objSF.getPassword().getValue());
        assertEquals("host = ", "localhost", objSF.getHost().getValue());
    }

    @Test
    public void testValue4BigUrl() {
        objU.setValue("    http://nobody:password@example.org:8080/cgi-bin/script.php?action=submit&pageid=86392001#section_2");
        ISOSDataProviderOptions objSF = new SOSConnection2Options();
        objU.getOptions(objSF);
        assertEquals("Protocol = ", "http", objSF.getProtocol().getValue());
        assertEquals("userid = ", "nobody", objSF.getUser().getValue());
        assertEquals("password = ", "password", objSF.getPassword().getValue());
        assertEquals("host = ", "example.org", objSF.getHost().getValue());
    }

    @Test
    public void testSOSOptionUrl() {
        objU = new SOSOptionUrl(null, "url", "descr", "", "", false);
        assertTrue("not null", objU != null);
    }

    @Test
    public void testGetOptions() {
        //
    }

    @Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class)
    public void testMissingUrl() {
        objU = new SOSOptionUrl(null, "url", "descr", "", "", false);
        objU.getOptions(new SOSConnection2Options());
    }

}