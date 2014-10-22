/**
 *
 */
package com.sos.jade.userinterface.adapters;
import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.swt.widgets.Display;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.interfaces.IJadeEngine;
import com.sos.jade.userinterface.composite.LogFileComposite;
import com.sos.jade.userinterface.data.JadeTreeViewEntry;

/**
 * @author KB
 *
 */
public class JADEEngineAdapter extends Thread implements Runnable {
	@SuppressWarnings("unused")
	private final String		conClassName		= this.getClass().getSimpleName();
	private final Logger		logger				= Logger.getLogger(this.getClass());
	@SuppressWarnings("unused")
	private static final String	conSVNVersion		= "$Id$";
	private JADEOptions			objOptions			= null;

	private ISOSSWTAppenderUI	objLogFileComposite	= null;
	private JadeTreeViewEntry	objTreeViewEntry	= null;

	//	@SuppressWarnings("unused")
	//	private final ISOSVFSHandler	objVFS			= null;

	//	@SuppressWarnings("unused")
	//	private final ISOSVfsFileTransfer	ftpClient		= null;

	public JADEEngineAdapter(final JadeTreeViewEntry pobjTreeViewEntry, final ISOSSWTAppenderUI pobjLogFileComposite) {

		objTreeViewEntry = pobjTreeViewEntry;
		objLogFileComposite = pobjLogFileComposite;

	}

	/**
	 * @param pstrResourceBundleName
	 */
	public JADEEngineAdapter(final String pstrResourceBundleName) {
		super(pstrResourceBundleName);
	}

	public void setLogFileComposite(final LogFileComposite pobjLogFileComposite) {
		objLogFileComposite = pobjLogFileComposite;
	}
	private String	strVerbose	= "0";

	public void setVerbose(final String pstrVerbose) {
		strVerbose = pstrVerbose;
	}

	@Override
	public void run() {
		start();
	}

	private void removeAppender(Logger pogjLogger) {
		for (Enumeration appenders = pogjLogger.getAllAppenders(); appenders.hasMoreElements();) {
			Appender appender = (Appender) appenders.nextElement();
			if (appender instanceof SOSSWTAppender) {
				pogjLogger.removeAppender(appender);
			}
		}
	}

	private static final SOSSWTAppender	objSWTAppender	= new SOSSWTAppender();

	@Override
	public void start() {
		// TODO Option info_log4jPatternLayout, debug_ , error_ , trace_ 
		PatternLayout layout = new PatternLayout("[%-p] %d{ABSOLUTE} - %m%n");
		Logger objRootLog = Logger.getRootLogger();

		// delete previous defined appenders, because of the singleton nature of the log4j
		removeAppender(objRootLog);

		objSWTAppender.setLayout(layout);
		objSWTAppender.setAppenderUI(objLogFileComposite);

		// ave to be defined, otherwise ERRORs are not reported (I don't know why)
		//		logger.setLevel(Level.INFO);
		//		logger.addAppender(objSWTAppender);

		objOptions = null;
		if (objOptions == null) {
			objOptions = new JADEOptions();
			String strSettingsFileName = objTreeViewEntry.getSession().getJadeProperties().getAbsolutePath();
			String strProfileName = objTreeViewEntry.getName();
			String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFileName, "-profile=" + strProfileName };
			try {
				objOptions.CommandLineArgs(strCmdLineParameters);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			// TODO class name as an Option
			Class<?> objA = classLoader.loadClass("com.sos.DataExchange.JadeEngine");
			IJadeEngine objJADEEngine = (IJadeEngine) objA.newInstance();
			Logger objJadeLogger = objJADEEngine.getLogger();
			removeAppender(objJadeLogger);
			objJADEEngine.setLogAppender(objSWTAppender);
			objJADEEngine.setJadeOptions(objOptions);
			Display.getDefault().asyncExec(objJADEEngine);
			//			System.out.println("objJADEEngine.getState() = " + objJADEEngine.getState());
		}
		catch (Exception e) {
			logger.info("abort", e);
		}
		finally {
			//			objRootLog.removeAppender(objSWTAppender);
			//			objSWTAppender = null;
		}
	}
}
