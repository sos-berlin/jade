package com.sos.jade.job;

import com.sos.JSHelper.Basics.JSToolBox;
import org.apache.log4j.Logger;


/**
 * \class 		SOSJadeHistoryMain - Main-Class for "Import from order or file to JadeHistoryTable"
 *
 * \brief MainClass to launch SOSJadeHistory as an executable command-line program
 *
 * This Class SOSJadeHistoryMain is the worker-class.
 *

 *
 * see \see C:\Dokumente und Einstellungen\Uwe Risse\Lokale Einstellungen\Temp\scheduler_editor-200438664770970222.html for (more) details.
 *
 * \verbatim ;
 * mechanicaly created by C:\Dokumente und Einstellungen\Uwe Risse\Eigene Dateien\sos-berlin.com\jobscheduler.1.3.9\scheduler_139\config\JOETemplates\java\xsl\JSJobDoc2JSMainClass.xsl from http://www.sos-berlin.com at 20120117120500 
 * \endverbatim
 */
public class SOSJadeHistoryMain extends JSToolBox {
	private final static String					conClassName						= "SOSJadeHistoryMain"; //$NON-NLS-1$
	private static Logger		logger			= Logger.getLogger(SOSJadeHistoryMain.class);
	@SuppressWarnings("unused")	

	protected SOSJadeHistoryOptions	objOptions			= null;

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

		logger = Logger.getRootLogger();
		logger.info("SOSJadeHistory - Main"); //$NON-NLS-1$

		try {
			SOSJadeHistory objM = new SOSJadeHistory();
			SOSJadeHistoryOptions objO = objM.getOptions();
			
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

}  // class SOSJadeHistoryMain