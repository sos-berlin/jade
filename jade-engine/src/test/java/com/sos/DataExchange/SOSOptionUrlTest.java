package com.sos.DataExchange;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.sos.JSHelper.Options.SOSOptionUrl;
import com.sos.JSHelper.interfaces.ISOSDataProviderOptions;
import com.sos.VirtualFileSystem.Options.SOSConnection2Options;

public class SOSOptionUrlTest {
	
	private SOSOptionUrl objU = new SOSOptionUrl("");
	
	@Test public void testValueString() {
		
		objU.Value("ftp://kb:kb@homer.sos/home/test/test.txt");
		ISOSDataProviderOptions objSF = new SOSConnection2Options();
		objU.getOptions(objSF);
		assertEquals("Protocol = ", "ftp", objSF.getprotocol().Value());
		assertEquals("userid = ", "kb", objSF.getUser().Value());
		assertEquals("password = ", "kb", objSF.getPassword().Value());
		assertEquals("host = ", "homer.sos", objSF.getHost().Value());
//		assertEquals("filePath = ", "/home/test/test.txt", objSF.getfile_path().Value());
	}

	@Test public void testValue4File() {
		
		objU.Value("file:///./JCLs");
		ISOSDataProviderOptions objSF = new SOSConnection2Options();
		objU.getOptions(objSF);
		assertEquals("Protocol = ", "file", objSF.getprotocol().Value());
		assertEquals("userid = ", "", objSF.getUser().Value());
		assertEquals("password = ", "", objSF.getPassword().Value());
		assertEquals("host = ", "", objSF.getHost().Value());
//		assertEquals("filePath = ", "/home/test/test.txt", objSF.getfile_path().Value());
	}

	@Test public void testValue4File2() {
		
		objU.Value("file:///kb:kb@localhost:4711/./JCLs");
		ISOSDataProviderOptions objSF = new SOSConnection2Options();
		objU.getOptions(objSF);
		assertEquals("Protocol = ", "file", objSF.getprotocol().Value());
		assertEquals("userid = ", "", objSF.getUser().Value());
		assertEquals("password = ", "", objSF.getPassword().Value());
		assertEquals("host = ", "", objSF.getHost().Value());
//		assertEquals("filePath = ", "/home/test/test.txt", objSF.getfile_path().Value());
	}

	@Test public void testValue4BigUrl() {
		
		objU.Value("    http://nobody:password@example.org:8080/cgi-bin/script.php?action=submit&pageid=86392001#section_2");
		ISOSDataProviderOptions objSF = new SOSConnection2Options();
		objU.getOptions(objSF);
		assertEquals("Protocol = ", "http", objSF.getprotocol().Value());
		assertEquals("userid = ", "nobody", objSF.getUser().Value());
		assertEquals("password = ", "password", objSF.getPassword().Value());
		assertEquals("host = ", "example.org", objSF.getHost().Value());
//		assertEquals("filePath = ", "/home/test/test.txt", objSF.getfile_path().Value());
	}

	@Test public void testSOSOptionUrl() {
		objU = new SOSOptionUrl(null, "url", "descr", "", "", false);
		assertTrue("not null", objU != null);
	}

	@Test public void testGetOptions() {
//		fail("Not yet implemented");
	}
	@Test (expected=com.sos.JSHelper.Exceptions.JobSchedulerException.class)
	
	public void testMissingUrl() {
		objU = new SOSOptionUrl(null, "url", "descr", "", "", false);
		objU.getOptions(new SOSConnection2Options());
	}
}
