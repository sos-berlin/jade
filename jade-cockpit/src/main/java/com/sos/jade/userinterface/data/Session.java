package com.sos.jade.userinterface.data;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Options.SOSOptionElement;
import com.sos.JSHelper.Options.SOSOptionString;
import com.sos.JSHelper.io.Files.JSIniFile;
import com.sos.JSHelper.io.Files.SOSProfileSection;

public class Session {
	private final Logger	logger						= Logger.getLogger(Session.class);
	public final String		conSVNVersion				= "$Id$";
	private SectionsHandler	objSectionsHandler;
	private JSIniFile				objJadeConfigurationFile	= null;
	private String			name;
	private boolean			flgIsDirty					= false;

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
	}

	public SectionsHandler getSectionsHandler() {
		return objSectionsHandler;
	}

	public SectionsHandler loadJadeConfigurationFile() {
		return loadJadeConfigurationFile("src/test/resources/jade_settings.ini");
	}

	public SectionsHandler loadJadeConfigurationFile(final String pstrFileName) {
		if (objJadeConfigurationFile == null) {
			name = pstrFileName;
			objJadeConfigurationFile = new JSIniFile(pstrFileName);
			objSectionsHandler = new SectionsHandler(null, this.getName());
			for (SOSProfileSection objSection : objJadeConfigurationFile.getSections()) {
				JadeTreeViewEntry objJ = new JadeTreeViewEntry(objJadeConfigurationFile, objSection);
				objSectionsHandler.addEntry(objJ);
				objJ.setSession(this);
			}
		}
		return objSectionsHandler;
	}

	public JadeTreeViewEntry newTreeViewEntry(final JSIniFile pobjJadeConfigurationFile, final JADEOptions pobjJO) {
		JadeTreeViewEntry objTVW = new JadeTreeViewEntry(pobjJadeConfigurationFile, pobjJO, pobjJadeConfigurationFile.addSection(pobjJO.profile.Value()));
		objTVW.setSession(this);
		objSectionsHandler.addEntry(objTVW);
		return objTVW;
	}

	public JadeTreeViewEntry newTreeViewEntry (final JADEOptions pobjOptions) {
		JadeTreeViewEntry objTVW = new JadeTreeViewEntry(objJadeConfigurationFile, objJadeConfigurationFile.addSection(pobjOptions.profile.Value()));
		objTVW.setOptions(pobjOptions);
		return objTVW;
	}

	public JadeTreeViewEntry newTreeViewEntry (final SOSOptionString pobjProfile) {
		JadeTreeViewEntry objTVW = new JadeTreeViewEntry(objJadeConfigurationFile, objJadeConfigurationFile.addSection(pobjProfile.Value()));
		return objTVW;
	}
	
	public void saveConfigurationFile() {
		objJadeConfigurationFile.CreateBackup();
		try {
			objJadeConfigurationFile.close();
			SOSOptionElement.flgShowPasswords = true;
			for (JadeTreeViewEntry objEntry : objSectionsHandler.getEntries()) {
				JADEOptions objJadeOptions = objEntry.getOptions();
				// avoid writing to file
				objJadeOptions.settings.setHideOption(true);
				objJadeOptions.profile.setHideOption(true);
				objJadeOptions.settings.setProtected(true);
				objJadeOptions.profile.setProtected(true);
				String strT1 = "\n[" + objJadeOptions.profile.Value() + "]\n";
				strT1 += objJadeOptions.DirtyString();
				objJadeConfigurationFile.WriteLine(strT1);
			}
		}
		catch (IOException e) {
		}
		finally {
			SOSOptionElement.flgShowPasswords = true;			
		}
	}

	public String getName() {
		return name;
	}

	public JSIniFile getJadeProperties() {
		return objJadeConfigurationFile;
	}

	public void dispose() {
	}

	public String checkDuplicateProfileName(SOSOptionString profile) {
		String strMessage = null;
		String strName2Check = profile.Value();
		for (JadeTreeViewEntry objEntry : objSectionsHandler.getEntries()) {
			if (objEntry.getName().equalsIgnoreCase(strName2Check) == true) {
				strMessage = String.format("profile: Name '%1$s' is already defined. Use another name", strName2Check);
				break;
			}
		}
		return strMessage;
	}

	/**
	 * @return the objJadeConfigurationFile
	 */
	public JSIniFile getObjJadeConfigurationFile() {
		return objJadeConfigurationFile;
	}

	/**
	 * @param objJadeConfigurationFile the objJadeConfigurationFile to set
	 */
	public void setObjJadeConfigurationFile(JSIniFile objJadeConfigurationFile) {
		this.objJadeConfigurationFile = objJadeConfigurationFile;
	}

}