package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.dialog.classes.SOSCTabFolder;
import com.sos.dialog.layouts.Gridlayout;

public class FileSelectionComposite extends CompositeBaseClass<JADEOptions> {
	@SuppressWarnings({ "unused", "hiding" })
	private final Logger	logger			= Logger.getLogger(FileSelectionComposite.class);
	public final String		conSVNVersion	= "$Id$";

	//	private JADEOptions	objJadeOptions	= null;
	public FileSelectionComposite(final CTabItem parent, final JADEOptions objOptions) {
		this((Composite) parent.getControl(), objOptions);
	}

	public FileSelectionComposite(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		objJadeOptions = objOptions;
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}
	}

	@Override public void createComposite() {
		Gridlayout.set4ColumnLayout(this);
		SOSCTabFolder tabFolderFileSection = new SOSCTabFolder(this, SWT.NONE);
		tabFolderFileSection.ItemsHasClose = false;
		{
			createTab(tabFolderFileSection, new FileRegExComposite(tabFolderFileSection, objJadeOptions), "tab_Selection");
			createTab(tabFolderFileSection, new FileRenameComposite(tabFolderFileSection, objJadeOptions), "tab_Rename");
			createTab(tabFolderFileSection, new FilePollingComposite(tabFolderFileSection, objJadeOptions), "tab_Polling");
			createTab(tabFolderFileSection, new FileSteadyStateComposite(tabFolderFileSection, objJadeOptions), "tab_SteadyState");
			createTab(tabFolderFileSection, new FileCumulateComposite(tabFolderFileSection, objJadeOptions), "tab_CumulateFiles");
		}
		tabFolderFileSection.setSelection(0);
	}

	@Override protected void enableFields() {
	}
}
