package com.sos.DataExchange;

/**
* \class Jade4DMZTest
*
* \brief Jade4DMZTest -
*
* \details
*
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
* \version $Id$
* \see reference
*
* Created on 06.06.2012 17:30:40
 */

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.sos.DataExchange.Options.JADEOptions;

/**
 * @author KB
 *
 */
public class Jade4DMZTest {

	@SuppressWarnings("unused")
	private final String		conClassName	= "Jade4DMZTest";
	@SuppressWarnings("unused")
	private static final String	conSVNVersion	= "$Id$";
	@SuppressWarnings("unused")
	private static final Logger	logger			= Logger.getLogger(Jade4DMZTest.class);
	private final String		strPathOfTestInis = "R:/backup/sos/java/development/SOSDataExchange/examples/";
	

	public Jade4DMZTest() {
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
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
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
	@Before
	public void setUp() throws Exception {
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
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.sos.DataExchange.Jade4DMZ#Jade4DMZ()}.
	 */
	@Test
	public final void testJade4DMZ() {
		// fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.sos.DataExchange.Jade4DMZ#Jade4DMZ(java.util.Properties)}.
	 */
	@Test
	public final void testJade4DMZProperties() {
		// fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.sos.DataExchange.Jade4DMZ#Jade4DMZ(com.sos.VirtualFileSystem.Options.JADEOptions)}.
	 */
	@Test
	public final void testJade4DMZJADEOptions() {
		// fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.sos.DataExchange.Jade4DMZ#Jade4DMZ(java.util.HashMap)}.
	 */
	@Test
	public final void testJade4DMZHashMapOfStringString() {
		// fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.sos.DataExchange.Jade4DMZ#Options()}.
	 */
	@Test
	public final void testOptions() {
		// fail("Not yet implemented"); // TODO
	}
	protected JADEOptions	objO	= null;

	/**
	 * Test method for {@link com.sos.DataExchange.Jade4DMZ#Execute()}.
	 */
	@Test
	@Ignore("Test set to Ignore for later examination")
	public final void testExecute() {
		Jade4DMZ objJ = new Jade4DMZ();
		objO = objJ.Options();

		objO.settings.Value(strPathOfTestInis + "jade_jumpHost_re.ini");
		objO.profile.Value("jump_test_send");
		objO.profile.Value("jump_test_receive");
		
		//objO.settings.Value(strPathOfTestInis + "jade_jumpHost.ini");
		//objO.profile.Value("jump_test_copy_from_internet_socks5_proxy");
		//objO.profile.Value("jump_test_copy_to_internet");
		
		
		objO.ReadSettingsFile();

		objJ.Execute();
	}

	
	@Test
	@Ignore("Test set to Ignore for later examination")
	public final void testcopyfrominternet() {
		Jade4DMZ objJ = new Jade4DMZ();
		objO = objJ.Options();

		objO.settings.Value(strPathOfTestInis + "jade_jumpHost.ini");
		objO.profile.Value("jump_test_copy_from_internet");
		objO.ReadSettingsFile();

		objJ.Execute();
	}
	
	@Test
	@Ignore("Test set to Ignore for later examination")
	public final void testcopytointernet() {
		Jade4DMZ objJ = new Jade4DMZ();
		objO = objJ.Options();

		objO.settings.Value(strPathOfTestInis + "jade_jumpHost.ini");
		objO.profile.Value("jump_test_copy_to_internet");
		objO.ReadSettingsFile();

		objJ.Execute();
	}
	
	@Test
	@Ignore("Test set to Ignore for later examination")
	public final void testJumpTestCopyToInternetHttpProxy() {
		Jade4DMZ objJ = new Jade4DMZ();
		objO = objJ.Options();

		objO.settings.Value(strPathOfTestInis + "jade_jumpHost.ini");
		objO.profile.Value("jump_test_copy_to_internet_http_proxy");
		objO.ReadSettingsFile();

		objJ.Execute();
	}
	
	@Test
	@Ignore("Test set to Ignore for later examination")
	public final void testJumpTestCopyToInternetSocks5Proxy() {
		Jade4DMZ objJ = new Jade4DMZ();
		objO = objJ.Options();

		objO.settings.Value(strPathOfTestInis + "jade_jumpHost.ini");
		objO.profile.Value("jump_test_copy_to_internet_socks5_proxy");
		objO.ReadSettingsFile();

		objJ.Execute();
	}
	
	@Test
  @Ignore("Test set to Ignore for later examination")
	public final void testsendusingdmz() {
		Jade4DMZ objJ = new Jade4DMZ();
		objO = objJ.Options();

		objO.settings.Value(strPathOfTestInis + "jade_jumpHost.ini");
		objO.profile.Value("jump_test_sendusingdmz");
		objO.ReadSettingsFile();

		objJ.Execute();
	}
	
	@Test
  @Ignore("Test set to Ignore for later examination")
	public final void testsendusingdmzOH() {
		Jade4DMZ objJ = new Jade4DMZ();
		objO = objJ.Options();

		objO.settings.Value(strPathOfTestInis + "jade_jumpHost.ini");
		objO.profile.Value("jumphost_test_receive");
		objO.ReadSettingsFile();

		objJ.Execute();
	}

	@Test
	@Ignore("Test set to Ignore for later examination")
	public final void testreceiveusingdmz() {
		Jade4DMZ objJ = new Jade4DMZ();
		objO = objJ.Options();

		objO.settings.Value(strPathOfTestInis + "jade_jumpHost.ini");
		objO.profile.Value("jump_test_receiveusingdmz");
		objO.ReadSettingsFile();

		objJ.Execute();
	}

	@Test
	@Ignore("Test set to Ignore for later examination")
	public final void testExecute3() throws Exception {
		Jade4DMZ objJ = new Jade4DMZ();
		objO = objJ.Options();

		objO.settings.Value(strPathOfTestInis + "jade_jumpHost.ini");
		objO.profile.Value("jump_test_send");
		objO.ReadSettingsFile();
		String strB = objO.getOptionsAsCommandLine();

		objO.settings.Value(null);
		objO.profile.Value(null);
		String strC = objO.getOptionsAsCommandLine();

		System.out.println(strB);
		System.out.println(strC);

		JADEOptions objNewO = new JADEOptions();
		objNewO.CommandLineArgs(strC.split(" "));
		System.out.println("\nobjNewO : " + objNewO.getOptionsAsCommandLine());

	}

	@Test
  @Ignore("Test set to Ignore for later examination")
	public final void testExecuteDeepCopy() throws Exception {
		Jade4DMZ objJ = new Jade4DMZ();
		objO = objJ.Options();

		objO.settings.Value(strPathOfTestInis + "jade_jumpHost.ini");
		objO.profile.Value("jump_test_send");
		objO.ReadSettingsFile();

		JADEOptions objNewO = (JADEOptions) objO.deepCopy(objO);
		objO.settings.Value("");
		objO.profile.Value("");

		String strC = objNewO.getOptionsAsCommandLine();
		System.out.println(strC);
	}


}
