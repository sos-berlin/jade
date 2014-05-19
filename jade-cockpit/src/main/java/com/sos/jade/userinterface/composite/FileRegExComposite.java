package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.dialog.classes.SOSCTabItem;

public class FileRegExComposite extends CompositeBaseClass<JADEOptions> {
	@SuppressWarnings("unused")
	private final String	conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings({ "unused", "hiding" })
	private final Logger	logger			= Logger.getLogger(this.getClass());
	public final String		conSVNVersion	= "$Id: BackgroundServiceComposite.java 21704 2013-12-29 22:35:13Z kb $";

	public FileRegExComposite(final SOSCTabItem parent, final JADEOptions objOptions) {
		this((Composite) parent.getControl(), objOptions);
	}

	public FileRegExComposite(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}
	}

	@Override public void createComposite() {
		{
			objCC.getControl(objJadeOptions.Source().FolderName, 3);
			objCC.getControl(objJadeOptions.file_spec, 3);
			objCC.getControl(objJadeOptions.file_path, 3);
			objCC.getControl(objJadeOptions.FileListName, 3);
			objCC.getControl(objJadeOptions.Target().FolderName, 3);
			objCC.getControl(objJadeOptions.remove_files);
			objCC.getLabel(2);
			objCC.getControl(objJadeOptions.force_files);
			objCC.getLabel(2);
			objCC.getControl(objJadeOptions.RecurseSubFolders);
			objCC.getLabel(2);
			objCC.getControl(objJadeOptions.append_files);
			objCC.getLabel(2);
			objCC.getControl(objJadeOptions.overwrite_files);
			objCC.getLabel(2);
		}
		enableFields();
	}

	@Override protected void enableFields() {
	}
}
