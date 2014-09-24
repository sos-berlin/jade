package com.sos.jade.userinterface.data;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Options.IValueChangedListener;
import com.sos.JSHelper.Options.SOSValidationError;
import com.sos.JSHelper.interfaces.IDirty;
import com.sos.JSHelper.io.Files.JSIniFile;
import com.sos.JSHelper.io.Files.SOSProfileSection;
import com.sos.dialog.interfaces.ISOSControlProperties;

public class JadeTreeViewEntry implements ISOSControlProperties, IValueChangedListener, IDirty {
	@SuppressWarnings("unused")
	private final Logger		logger			= Logger.getLogger(JadeTreeViewEntry.class);
	public final String			conSVNVersion	= "$Id$";
	private SOSProfileSection	objSection		= null;
	private Session				objSession		= null;
	private JSIniFile			objIniFile		= null;
	private enuTreeItemType		enuType			= enuTreeItemType.IsRoot;
	public enum enuTreeItemType {
		IsRoot, fragments_root, profiles_root, profiles, profile, fragment, globals, fragments_used, transfer_history, servers_used, source_servers, target_servers;
	}

	private JADEOptions	objOptions	= null;

	public enuTreeItemType getType() {
		return enuType;
	}

	//	private final SectionsHandler	group;
	public JadeTreeViewEntry(final enuTreeItemType iType) {
		enuType = iType;
	}

	public JadeTreeViewEntry(final JSIniFile pobjIniFile, final SOSProfileSection pobjSection) {
		objSection = pobjSection;
		objIniFile = pobjIniFile;
		enuType = enuTreeItemType.profiles;
		// Layzy ??
		logger.debug(String.format("initialize section '%1$s'", objSection.Name()));
		getOptions();
	}

	public void setSession(final Session pobjSession) {
		objSession = pobjSession;
	}

	public Session getSession() {
		return objSession;
	}

	public SOSProfileSection getSection() {
		return objSection;
	}

	@Override
	public String getName() {
		String strR = "";
		switch (enuType) {
			case IsRoot:
				strR = "JADE config file";
				//				strR = objIniFile.getName();
				break;
			case fragments_root:
				strR = "Fragments";
				break;
			case profiles_root:
				strR = "Transfer Profiles";
				break;
			case profiles:
			default:
				strR = objSection.Name();
				break;
		}
		return strR;
	}

	public JSIniFile getParent() {
		return objIniFile;
	}

	public void setOptions(final JADEOptions pobjOptions) {
		objOptions = pobjOptions;
	}

	private boolean	flgIsFragment	= false;
	private boolean	flgIsProfile	= false;

	public boolean isFragment() {
		return flgIsFragment;
	}

	public boolean isProfile() {
		return flgIsProfile;
	}

	public JADEOptions getOptions() {
		if (objOptions == null) {
			objOptions = new JADEOptions();
			objOptions.gflgSubsituteVariables = false; //
			if (objIniFile != null) {
				objOptions.settings.Value(objIniFile.getAbsolutePath());
				objOptions.profile.Value(getName());
				objOptions.settings.setProtected(true);
				objOptions.profile.setProtected(true);
				objOptions.ReadSettingsFile();
				flgIsFragment = objOptions.isFragment.isTrue();
				logger.trace("is Fragment = " + flgIsFragment);
				flgIsProfile = objOptions.isFragment.isFalse();
			}
		}
		return objOptions;
	}

	@Override
	public String getTitle() {

		String strT = this.getName();
		//		String strT2 = getOptions().title.Value();
		//		if (strT2.length() > 0) {
		//			strT = strT + " - " + strT2;
		//		}

		return strT;
	}

	@Override
	public void selectChild() {
		if (objTreeItem != null) {
			objTreeItem.getParent().setSelection(objTreeItem);
		}
	}

	TreeItem	objTreeItem	= null;

	public void setTreeItem(final TreeItem pobjTreeItem) {
		objTreeItem = pobjTreeItem;
	}

	public boolean isExecutable() {
		boolean flgIsExec = flgIsProfile == true;
		return flgIsExec;
	}

	public boolean isSourceGen() {
		boolean flgIsExec = flgIsProfile == true;
		return flgIsExec;
	}

	@Override
	public void ValueHasChanged(String pstrNewValue) {
		if (objTreeItem != null && objTreeItem.isDisposed() == false) {
			String strT = objTreeItem.getText();
			if (strT.startsWith("*") == false) {
				strT = "*> " + strT;
				objTreeItem.setText(strT);
			}
		}
		if (objIniFile != null) {
			objIniFile.setDirty();
		}
	}

	@Override
	public void ValidationError(SOSValidationError pobjVE) {

	}

	@Override
	// IDirty
	public boolean isDirty() {
		return objIniFile.isDirty();
	}

	@Override
	// IDirty
	public void setDirty(boolean pflgIsDirty) {
		objIniFile.setDirty();
	}

	@Override
	// IDirty
	public void setDirty() {
		objIniFile.setDirty();
	}

	@Override
	// IDirty
	public int doSave(final boolean pflgAskForSave) {
		int buttonID = 0;
		if (pflgAskForSave) {
			Display display = Display.getCurrent();
			Shell shell = display.getActiveShell();

			MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);

			messageBox.setText("JADE Warning");
			messageBox.setMessage("Save the changes before exiting?");
			buttonID = messageBox.open();
			switch (buttonID) {
				case SWT.YES:
					objSession.saveConfigurationFile();
				case SWT.NO:
					// exits here ...
					break;
				case SWT.CANCEL:
					// does nothing ...
			}
		}
		return buttonID;
	}
}
