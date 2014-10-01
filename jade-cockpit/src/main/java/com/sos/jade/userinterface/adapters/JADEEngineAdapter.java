/**
 *
 */
package com.sos.jade.userinterface.adapters;
import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Basics.JSToolBox;
import com.sos.JSHelper.interfaces.IJadeEngine;
import com.sos.VirtualFileSystem.Interfaces.ISOSVFSHandler;
import com.sos.VirtualFileSystem.Interfaces.ISOSVfsFileTransfer;
import com.sos.jade.userinterface.composite.LogFileComposite;
import com.sos.jade.userinterface.data.JadeTreeViewEntry;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.swt.widgets.Display;

/**
 * @author KB
 *
 */
public class JADEEngineAdapter extends JSToolBox {
	@SuppressWarnings("unused")
	private final String				conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings("unused")
	private final Logger				logger			= Logger.getLogger(this.getClass());
	@SuppressWarnings("unused")
	private static final String			conSVNVersion	= "$Id$";
	@SuppressWarnings("unused")
	private JADEOptions				objOptions		= null;
	@SuppressWarnings("unused")
	private final ISOSVFSHandler		objVFS			= null;
	@SuppressWarnings("unused")
	private final ISOSVfsFileTransfer	ftpClient		= null;

	public JADEEngineAdapter() {
	}

	/**
	 * @param pstrResourceBundleName
	 */
	public JADEEngineAdapter(final String pstrResourceBundleName) {
		super(pstrResourceBundleName);
	}
	@SuppressWarnings("unused")
	private LogFileComposite	objLogFileComposite	= null;

	public void setLogFileComposite(final LogFileComposite pobjLogFileComposite) {
		objLogFileComposite = pobjLogFileComposite;
	}
	private String	strVerbose	= "0";

	public void setVerbose(final String pstrVerbose) {
		strVerbose = pstrVerbose;
	}

	public void Execute(final JadeTreeViewEntry objTreeViewEntry, final ISOSSWTAppenderUI pobjLogFileComposite) {
		PatternLayout layout = new PatternLayout("%5p [%t] (%F:%L) - %m%n");
		SOSSWTAppender objSWTAppender = new SOSSWTAppender(layout);
		objSWTAppender.setAppenderUI(pobjLogFileComposite);
		Logger objRootLog = Logger.getRootLogger();
		objRootLog.setLevel(Level.DEBUG);
		objRootLog.addAppender(objSWTAppender);
		//
		// Appender consoleAppender2 = new ConsoleAppender(layout);
		// // Appender consoleAppender = new ConsoleAppender(layout);
		//
		// objRootLog.addAppender(consoleAppender2);
		//
		// // ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
		// objRootLog.setLevel(Level.DEBUG);
		// objRootLog.debug("Log4J configured programmatically");
		//
		// String strLog4JFileName = "./log4j.properties";
		// String strT = new File(strLog4JFileName).getAbsolutePath();
		// System.out.println(strT);
		// logger.info("logfilename = " + strT);
//		objOptions = objTreeViewEntry.getOptions();
		objOptions = null;
		if (objOptions == null) {
			objOptions = new JADEOptions();
			String strSettingsFileName = objTreeViewEntry.getSession().getJadeProperties().getAbsolutePath();
			//		consoleAppender.setSession(objSection.getSession());
			String strProfileName = objTreeViewEntry.getName();
			//		consoleAppender.setConsoleName(strProfileName);
			//		int intVerbose = objSection.getSession().getJadeProperties().getPropertyInt(strProfileName, "verbose", 0);
			String[] strCmdLineParameters = new String[] { "-settings=" + strSettingsFileName, "-profile=" + strProfileName, "-verbose=" + strVerbose };
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
			objJADEEngine.setJadeOptions(objOptions);
			//			Display.getDefault().syncExec(objJADEEngine);
			Display.getDefault().asyncExec(objJADEEngine);
			//			SOSThreadPoolExecutor objTPE = new SOSThreadPoolExecutor();
			//			objTPE.runTask(objJADEEngine);
			//			
			//			try {
			//				objTPE.shutDown();
			//				objTPE.objThreadPool.awaitTermination(1, TimeUnit.DAYS);
			//			}
			//			catch (InterruptedException e) {
			//				e.printStackTrace();
			//			}
			System.out.println(objJADEEngine.getState());
		}
		catch (Exception e) {
			logger.info("abort", e);
		}
	}
}
