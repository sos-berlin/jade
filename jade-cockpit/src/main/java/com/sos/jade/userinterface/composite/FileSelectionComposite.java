package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.dialog.classes.SOSCTabFolder;
import com.sos.dialog.classes.SOSCTabItem;
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
			SOSCTabItem tbtmItemFileRegEx = tabFolderFileSection.getTabItem("tab_Selection");
			FileRegExComposite objFileRegExComposite = new FileRegExComposite(tbtmItemFileRegEx, objJadeOptions);
			tbtmItemFileRegEx.setComposite(objFileRegExComposite);
			//
			SOSCTabItem tbtmNFileRename = tabFolderFileSection.getTabItem("tab_Rename");
			tbtmNFileRename.setComposite(new FileRenameComposite(tbtmNFileRename, objJadeOptions));
			//
			SOSCTabItem tbtmFilePolling = tabFolderFileSection.getTabItem("tab_Polling");
			tbtmFilePolling.setComposite(new FilePollingComposite(tbtmFilePolling, objJadeOptions));
			//
			SOSCTabItem tbtmFileSteadyState = tabFolderFileSection.getTabItem("tab_SteadyState");
			tbtmFileSteadyState.setComposite(new FileSteadyStateComposite(tbtmFileSteadyState, objJadeOptions));
			//
			SOSCTabItem tbtmCumulateFiles = tabFolderFileSection.getTabItem("tab_CumulateFiles");
			tbtmCumulateFiles.setComposite(new FileCumulateComposite(tbtmCumulateFiles, objJadeOptions));
		}
		tabFolderFileSection.setSelection(0);
	}

	@Override protected void enableFields() {
	}
}
