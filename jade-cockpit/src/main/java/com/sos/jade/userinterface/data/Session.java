package com.sos.jade.userinterface.data;
import org.apache.log4j.Logger;

import com.sos.JSHelper.io.Files.JSIniFile;
import com.sos.JSHelper.io.Files.SOSProfileSection;

public class Session {
	private final Logger	logger				= Logger.getLogger(Session.class);
	public final String		conSVNVersion		= "$Id$";
	private SectionsHandler	objSectionsHandler;
	JSIniFile				objJadeConfigurationFile	= null;
	private String			name;
	private boolean			flgIsDirty			= false;

	public Session() {
//		PatternLayout layout = new PatternLayout("%5p [%t] (%F:%L) - %m%n");
//		Logger objRootLog = Logger.getRootLogger();
//		Appender consoleAppender2 = new ConsoleAppender(layout);
//		objRootLog.addAppender(consoleAppender2);
//		// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
		//		objRootLog.setLevel(Level.DEBUG);
		//		objRootLog.debug("Log4J configured programmatically");
	}

	public boolean isDirty() {
		return flgIsDirty;
	}

	public void isDirty(final boolean pflgIsDirty) {
		flgIsDirty = pflgIsDirty;
	}

	public void setSessionDescription(final String name, final String server) {
		this.name = name;
		//		this.server = server;
	}
//	public String	strFileName	= "";
	//			String strFileName = "C:/Users/KB/indigo-rcp/com.sos.rcp.jade/testdata/sosdex_settings.ini";
	// R:/backup/sos/java/development/JADEUserInterface/TestData/jade_settings.ini
	//			server = "localhost";

	public SectionsHandler getSectionsHandler () {
		return objSectionsHandler;
	}
	public SectionsHandler loadJadeConfigurationFile() {
		return loadJadeConfigurationFile("R:/backup/sos/java/development/SOSDataExchange/examples/jade_settings.ini");
	}
	public SectionsHandler loadJadeConfigurationFile(final String pstrFileName) {
		if (objJadeConfigurationFile == null) {
			name = pstrFileName;
			objJadeConfigurationFile = new JSIniFile(pstrFileName);
			logger.debug("FileName = " + objJadeConfigurationFile.getAbsolutePath());
			objSectionsHandler = new SectionsHandler(null, this.getName());
			logger.debug("number of sections = " + objJadeConfigurationFile.Sections().size());
			for (SOSProfileSection objSection : objJadeConfigurationFile.Sections().values()) {
				logger.debug(String.format("initialize section '%1$s'", objSection.Name()));
				JadeTreeViewEntry objJ = new JadeTreeViewEntry(objJadeConfigurationFile, objSection);
				objSectionsHandler.addEntry(objJ);
				objJ.setSession(this);
			}
		}
//		if (objSectionsHandler == null) {
//			objSectionsHandler = new SectionsHandler(null, name);
//		}
		return objSectionsHandler;
	}

	public String getName() {
		return name;
	}

	public JSIniFile getJadeProperties() {
		return objJadeConfigurationFile;
	}

	public void dispose() {
	}
	// Display display = new Display();
	// Color red = display.getSystemColor(SWT.COLOR_RED);
	// Color blue = display.getSystemColor(SWT.COLOR_BLUE);
	// Color white = display.getSystemColor(SWT.COLOR_WHITE);
	// Color gray = display.getSystemColor(SWT.COLOR_GRAY);
}