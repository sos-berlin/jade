package com.sos.jade.backgroundservice.options;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sos.JSHelper.Basics.JSToolBox;
import com.sos.JSHelper.Listener.JSListenerClass;
import com.sos.JSHelper.Logging.Log4JHelper;

/**
 * \class 		JadeBSFrontEndOptionsJUnitTest - JadeBSFrontEnd
 *
 * \brief 
 *
 *

 *
 * see \see C:\Users\KB\AppData\Local\Temp\scheduler_editor-5890513725838352951.html for (more) details.
 * 
 * \verbatim ;
 * mechanicaly created by C:\ProgramData\sos-berlin.com\jobscheduler\latestscheduler_4446\config\JOETemplates\java\xsl\JSJobDoc2JSJUnitOptionSuperClass.xsl from http://www.sos-berlin.com at 20140605131019 
 * \endverbatim
 *
 * \section TestData Eine Hilfe zum Erzeugen einer HashMap mit Testdaten
 *
 * Die folgenden Methode kann verwendet werden, um f�r einen Test eine HashMap
 * mit sinnvollen Werten f�r die einzelnen Optionen zu erzeugen.
 *
 * \verbatim
 private HashMap <String, String> SetJobSchedulerSSHJobOptions (HashMap <String, String> pobjHM) {
	pobjHM.put ("		JadeBSFrontEndOptionsJUnitTest.auth_file", "test");  // This parameter specifies the path and name of a user's pr
		return pobjHM;
  }  //  private void SetJobSchedulerSSHJobOptions (HashMap <String, String> pobjHM)
 * \endverbatim
 */
public class JadeBackgroundServiceOptionsJUnitTest extends JSToolBox {
	private final String			conClassName	= "JadeBSFrontEndOptionsJUnitTest";						//$NON-NLS-1$
	@SuppressWarnings("unused")
	private static Logger			logger			= Logger.getLogger(JadeBackgroundServiceOptionsJUnitTest.class);
	@SuppressWarnings("unused")
	private static Log4JHelper		objLogger		= null;
	protected JadeBackgroundServiceOptions	objOptions		= null;

	public JadeBackgroundServiceOptionsJUnitTest() {
		//
	}

	@BeforeClass public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass public static void tearDownAfterClass() throws Exception {
	}

	@Before public void setUp() throws Exception {
		System.setProperty("JADE_BS_HIBERNATE_CONFIG", "/WEB-INF/classes/hibernate.cfg.xml");
		objLogger = new Log4JHelper("./log4j.properties"); //$NON-NLS-1$
		objOptions = new JadeBackgroundServiceOptions();
		JSListenerClass.bolLogDebugInformation = true;
		JSListenerClass.intMaxDebugLevel = 9;
	}

	@After public void tearDown() throws Exception {
	}

	/**
	 * \brief testClick_for_Details : 
	 * 
	 * \details
	 * 
	 *
	 */
	@Test public void testClick_for_Details() { // SOSOptionString
		objOptions.click_for_Details.Value("++false++");
		assertEquals("", objOptions.click_for_Details.Value(), "++false++");
		objOptions.click_for_Details.setTrue();
		assertTrue("Text", objOptions.click_for_Details.value());
	}

	@Test public void testHibernate_Configuration_File_Name2() { // SOSOptionString
		assertEquals("", objOptions.hibernate_Configuration_File_Name.Value(), "/WEB-INF/classes/hibernate.cfg.xml");
	}

	@Test public void testHibernate_Configuration_File_NameAlias() { // SOSOptionString
		assertEquals("", objOptions.hibernateConf.Value(), "/WEB-INF/classes/hibernate.cfg.xml");
	}

	/**
	 * \brief testHibernate_Configuration_File_Name : 
	 * 
	 * \details
	 * 
	 *
	 */
	@Test public void testHibernate_Configuration_File_Name() { // SOSOptionString
		objOptions.hibernate_Configuration_File_Name.Value("++hibernate.cfg.xml++");
		assertEquals("", objOptions.hibernate_Configuration_File_Name.Value(), "++hibernate.cfg.xml++");
	}
} // public class JadeBSFrontEndOptionsJUnitTest