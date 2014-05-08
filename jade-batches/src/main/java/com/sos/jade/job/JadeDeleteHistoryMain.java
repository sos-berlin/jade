

package com.sos.jade.job;

import org.apache.log4j.Logger;
import com.sos.JSHelper.Basics.JSToolBox;
import com.sos.JSHelper.Logging.Log4JHelper;


/**
 * \class 		JadeDeleteHistoryMain - Main-Class for "Delete entries in Jade history table"
 *
 * \brief MainClass to launch JadeDeleteHistory as an executable command-line program
 *
 * This Class JadeDeleteHistoryMain is the worker-class.
 *

 *
 * see \see C:\Dokumente und Einstellungen\Uwe Risse\Lokale Einstellungen\Temp\scheduler_editor-7740628146985625965.html for (more) details.
 *
 * \verbatim ;
 * mechanicaly created by C:\Dokumente und Einstellungen\Uwe Risse\Eigene Dateien\sos-berlin.com\jobscheduler.1.3.9\scheduler_139\config\JOETemplates\java\xsl\JSJobDoc2JSMainClass.xsl from http://www.sos-berlin.com at 20111221162313 
 * \endverbatim
 */
public class JadeDeleteHistoryMain extends JSToolBox {
	private final static String					conClassName						= "JadeDeleteHistoryMain"; //$NON-NLS-1$
	private static Logger		logger			= Logger.getLogger(JadeDeleteHistoryMain.class);
	@SuppressWarnings("unused")	
	private static Log4JHelper	objLogger		= null;

	protected JadeDeleteHistoryOptions	objOptions			= null;

	/**
	 * 
	 * \brief main
	 * 
	 * \details
	 *
	 * \return void
	 *
	 * @param pstrArgs
	 * @throws Exception
	 */
	public final static void main(String[] pstrArgs) {

		final String conMethodName = conClassName + "::Main"; //$NON-NLS-1$

		objLogger = new Log4JHelper("./log4j.properties"); //$NON-NLS-1$

		logger = Logger.getRootLogger();
		logger.info("JadeDeleteHistory - Main"); //$NON-NLS-1$

		try {
			JadeDeleteHistory objM = new JadeDeleteHistory();
			JadeDeleteHistoryOptions objO = objM.Options();
			
			objO.CommandLineArgs(pstrArgs);
			objM.Execute();
		}
		
		catch (Exception e) {
			System.err.println(conMethodName + ": " + "Error occured ..." + e.getMessage()); 
			e.printStackTrace(System.err);
			int intExitCode = 99;
			logger.error(String.format("JSJ-E-105: %1$s - terminated with exit-code %2$d", conMethodName, intExitCode), e);		
			System.exit(intExitCode);
		}
		
		logger.info(String.format("JSJ-I-106: %1$s - ended without errors", conMethodName));		
	}

}  // class JadeDeleteHistoryMain