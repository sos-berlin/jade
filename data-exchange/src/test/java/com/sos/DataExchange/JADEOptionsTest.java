package com.sos.DataExchange;
import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Options.SOSOptionJadeOperation.enuJadeOperations;

/**
* \class JADEOptionsTest
*
* \brief JADEOptionsTest -
*
* \details
*
* \section JADEOptionsTest.java_intro_sec Introduction
*
* \section JADEOptionsTest.java_samples Some Samples
*
* \code
*   .... code goes here ...
* \endcode
*
* <p style="text-align:center">
* <br />---------------------------------------------------------------------------
* <br /> APL/Software GmbH - Berlin
* <br />##### generated by ClaviusXPress (http://www.sos-berlin.com) #########
* <br />---------------------------------------------------------------------------
* </p>
* \author KB
* @version $Id$17.09.2010
* \see reference
*
* Created on 17.09.2010 09:01:43
 */
/**
 * @author KB
 *
 */
public class JADEOptionsTest {
	@SuppressWarnings("unused")
	private static final String	conSVNVersion	= "$Id$";
	@SuppressWarnings("unused")
	private final String		conClassName	= "JADEOptionsTest";
	@SuppressWarnings("unused")
	private final static Logger	logger			= Logger.getLogger(JADEOptionsTest.class);
	private JADEOptions			objO			= null;

	public JADEOptionsTest() {
		//
	}

	/**
	 * \brief setUpBeforeClass
	 *
	 * \details
	 *
	 * \return void
	 *
	 * @throws java.lang.Exception
	 */
	@BeforeClass public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * \brief tearDownAfterClass
	 *
	 * \details
	 *
	 * \return void
	 *
	 * @throws java.lang.Exception
	 */
	@AfterClass public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * \brief setUp
	 *
	 * \details
	 *
	 * \return void
	 *
	 * @throws java.lang.Exception
	 */
	@Before public void setUp() throws Exception {
		objO = new JADEOptions();
		objO.file_spec.Value(".*");
	}

	/**
	 * \brief tearDown
	 *
	 * \details
	 *
	 * \return void
	 *
	 * @throws java.lang.Exception
	 */
	@After public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.sos.VirtualFileSystem.Options.JADEOptions#CheckMandatory()}.
	 */
	@Test public void testCheckMandatory() {
		//		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.sos.VirtualFileSystem.Options.JADEOptions#JADEOptions()}.
	 */
	@Test public void testJADEOptions() {
		//		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.sos.VirtualFileSystem.Options.JADEOptions#JADEOptions(com.sos.JSHelper.Listener.JSListener)}.
	 */
	@Test public void testJADEOptionsJSListener() {
		//		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.sos.VirtualFileSystem.Options.JADEOptions#JADEOptions(java.util.HashMap)}.
	 */
	@Test public void testJADEOptionsHashMapOfStringString() {
		//		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.sos.VirtualFileSystem.Options.JADEOptions#ReadSettingsFile()}.
	 */
	@Test public void testReadSettingsFile() {
		//		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.sos.VirtualFileSystem.Options.JADEOptions#SubstituteVariables(java.lang.String, java.util.Properties, java.lang.String, java.lang.String)}.
	 */
	@Test public void testSubstituteVariablesStringPropertiesStringString() {
		//		fail("Not yet implemented");
	}

	@Test public void testLoadSettingsFile() {
		CreateSettingsFile();
		//		fail("Not yet implemented");
	}

	private void CreateSettingsFile() {
	}

	@Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class) 
	public void testReplaceing() {
		objO.operation.Value(enuJadeOperations.send);
		objO.ReplaceWhat.Value("Hello World");
		objO.ReplaceWith.setNull();
		objO.CheckMandatory();
	}

	@Test public void testOperation() {
		objO.operation.Value("remove");
		assertEquals("testOperation", "delete", objO.operation.Value());
	}

	@Test public void testSourceDir() {
		objO.SourceDir.Value("test-[date:yyyy-MM-dd]");
		System.out.println(objO.SourceDir.Value());
	}

	@Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class) 
	public void testOperationIsIllegal() {
		objO.operation.Value("xyzddd");
		System.out.println(objO.operation.value());
	}

	@Test(expected = com.sos.JSHelper.Exceptions.JobSchedulerException.class) 
	public void testReplaceingNull() {
		objO.operation.Value(enuJadeOperations.send);
		objO.ReplaceWhat.Value("Hello World");
		objO.CheckMandatory();
	}

	@Test public void testReplaceing2() {
		objO.file_spec.Value(".*");
		objO.operation.Value(enuJadeOperations.send);
		objO.ReplaceWhat.Value("Hello World");
		objO.ReplaceWith.Value("");
		System.out.println(objO.DirtyString());
		objO.CheckMandatory();
	}

	@Test public final void testGetlog_filename() {
		// fail("Not yet implemented"); // TODO
	}

	@Test public final void testSetlog_filename() {
		// fail("Not yet implemented"); // TODO
	}

	@Test public final void testGetlog4jPropertyFileName() {
		// fail("Not yet implemented"); // TODO
	}

	@Test public final void testSetlog4jPropertyFileName() {
		// fail("Not yet implemented"); // TODO
	}
}
