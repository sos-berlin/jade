package com.sos.jade.backgroundservice.options;
import static org.junit.Assert.assertEquals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sos.JSHelper.Basics.JSToolBox;
import com.sos.JSHelper.Listener.JSListenerClass;

/**
 * \class 		JadeBackgroundServiceOptionsJUnitTest - JadeBackgroundService
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
 * Die folgenden Methode kann verwendet werden, um für einen Test eine HashMap
 * mit sinnvollen Werten für die einzelnen Optionen zu erzeugen.
 *
 * \verbatim
 private HashMap <String, String> SetJobSchedulerSSHJobOptions (HashMap <String, String> pobjHM) {
	pobjHM.put ("		JadeBSFrontEndOptionsJUnitTest.auth_file", "test");  // This parameter specifies the path and name of a user's pr
		return pobjHM;
  }  //  private void SetJobSchedulerSSHJobOptions (HashMap <String, String> pobjHM)
 * \endverbatim
 */
public class JadeBackgroundServiceOptionsJUnitTest extends JSToolBox {
	@SuppressWarnings("unused")
	private final String			conClassName	= "JadeBackgroundServiceOptionsJUnitTest";						//$NON-NLS-1$
	@SuppressWarnings("unused")
//	private static Logger			logger			= LoggerFactory.getLogger(JadeBackgroundServiceOptionsJUnitTest.class);
	protected JadeBackgroundServiceOptions	objOptions		= null;

	public JadeBackgroundServiceOptionsJUnitTest() {
		//
	}

	@BeforeClass 
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass 
	public static void tearDownAfterClass() throws Exception {
	}

	@Before 
	public void setUp() throws Exception {
		System.setProperty("JADE_BS_HIBERNATE_CONFIG", "/WEB-INF/classes/hibernate.cfg.xml");
		objOptions = new JadeBackgroundServiceOptions();
		JSListenerClass.bolLogDebugInformation = true;
		JSListenerClass.intMaxDebugLevel = 9;
	}

	@After 
	public void tearDown() throws Exception {
	}

	@Test 
	public void testHibernateConfigurationFileName2() { // SOSOptionString
		assertEquals("", objOptions.hibernateConfigurationFileName.Value(), "/WEB-INF/classes/hibernate.cfg.xml");
	}

	@Test 
	public void testHibernateConfigurationFileNameAlias() { // SOSOptionString
		assertEquals("", objOptions.hibernateConf.Value(), "/WEB-INF/classes/hibernate.cfg.xml");
	}

	/**
	 * \brief testHibernateConfigurationFileName : 
	 * 
	 * \details
	 * 
	 *
	 */
	@Test 
	public void testHibernateConfigurationFileName() { // SOSOptionString
		objOptions.hibernateConfigurationFileName.Value("++hibernate.cfg.xml++");
		assertEquals("", objOptions.hibernateConfigurationFileName.Value(), "++hibernate.cfg.xml++");
	}
} // public class JadeBackgroundServiceOptionsJUnitTest