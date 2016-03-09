package com.sos.jade.job;

import com.sos.JSHelper.Basics.JSToolBox;
import com.sos.JSHelper.Listener.JSListenerClass;

import org.apache.log4j.Logger;
import org.junit.*;

/** \class JadeDeleteHistoryJUnitTest - JUnit-Test for
 * "Delete entries in Jade history table"
 *
 * \brief MainClass to launch JadeDeleteHistory as an executable command-line
 * program
 *
 * 
 *
 * see \see C:\Dokumente und Einstellungen\Uwe Risse\Lokale
 * Einstellungen\Temp\scheduler_editor-7740628146985625965.html for (more)
 * details.
 *
 * \verbatim ; mechanicaly created by C:\Dokumente und Einstellungen\Uwe
 * Risse\Eigene
 * Dateien\sos-berlin.com\jobscheduler.1.3.9\scheduler_139\config\JOETemplates
 * \java\xsl\JSJobDoc2JSJUnitClass.xsl from http://www.sos-berlin.com at
 * 20111221162313 \endverbatim */
public class JadeDeleteHistoryJUnitTest extends JSToolBox {

    @SuppressWarnings("unused")//$NON-NLS-1$
    private final static String conClassName = "JadeDeleteHistoryJUnitTest"; //$NON-NLS-1$
    @SuppressWarnings("unused")//$NON-NLS-1$
    private static Logger logger = Logger.getLogger(JadeDeleteHistoryJUnitTest.class);
    @SuppressWarnings("unused")//$NON-NLS-1$
    protected JadeDeleteHistoryOptions objOptions = null;
    private JadeDeleteHistory objE = null;

    public JadeDeleteHistoryJUnitTest() {
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
        objE = new JadeDeleteHistory();
        objE.registerMessageListener(this);
        objOptions = objE.Options();
        objOptions.registerMessageListener(this);

        JSListenerClass.bolLogDebugInformation = true;
        JSListenerClass.intMaxDebugLevel = 9;

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testExecute() throws Exception {

        objE.Execute();

        //		assertEquals ("auth_file", objOptions.auth_file.Value(),"test"); //$NON-NLS-1$
        //		assertEquals ("user", objOptions.user.Value(),"test"); //$NON-NLS-1$

    }
}  // class JadeDeleteHistoryJUnitTest