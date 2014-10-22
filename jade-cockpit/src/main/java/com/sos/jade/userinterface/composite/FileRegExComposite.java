package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.dialog.classes.SOSCheckBox;
import com.sos.dialog.components.CompositeBaseClass;

public class FileRegExComposite extends CompositeBaseClass<JADEOptions> {
	@SuppressWarnings("unused")
	private final String	conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings({ "unused", "hiding" })
	private final Logger	logger			= Logger.getLogger(this.getClass());
	public final String		conSVNVersion	= "$Id: BackgroundServiceComposite.java 21704 2013-12-29 22:35:13Z kb $";

	public FileRegExComposite(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}
	}

	@Override
	public void createComposite() {
		{

			objCC.getSeparator("datasource");

			objCC.getControl(objJadeOptions.Source().FolderName, 3);
			objCC.getControl(objJadeOptions.file_spec, 3);
			objCC.getControl(objJadeOptions.file_path, 3);
			objCC.getControl(objJadeOptions.FileListName, 3);

			boolean flgShowRemove = objJadeOptions.operation.isOperationRename() == false && objJadeOptions.operation.isOperationDelete() == false;

			if (flgShowRemove) {
				objCC.getControl(objJadeOptions.remove_files);
				objCC.getLabel(2);
			}
			objCC.getControl(objJadeOptions.RecurseSubFolders);
			objCC.getLabel(2);
			objCC.getControl(objJadeOptions.force_files);
			objCC.getLabel(2);
			objCC.getControl(objJadeOptions.TransferZeroByteFiles);
			objCC.getLabel(2);

			if (objJadeOptions.NeedTargetClient() == true) {
				objCC.getSeparator("datatarget");
				objCC.getControl(objJadeOptions.Target().FolderName, 3);

				// TODO one-of-us: evtl. radiobutton?
				SOSCheckBox objC1 = objCC.getCheckBox(objJadeOptions.append_files);
				objC1.addSelectionListener(EnableOneOfUsOrNoneListener);
				objCC.getLabel(2);

				SOSCheckBox objC2 = objCC.getCheckBox(objJadeOptions.overwrite_files);
				objC2.addSelectionListener(EnableOneOfUsOrNoneListener);
				objCC.getLabel(2);

				SOSCheckBox objC3 = objCC.getCheckBox(objJadeOptions.CumulateFiles);
				objC3.addSelectionListener(EnableOneOfUsOrNoneListener);
				objCC.getLabel(2);

				objC1.addChild(objJadeOptions.overwrite_files, objJadeOptions.CumulateFiles);
				objC2.addChild(objJadeOptions.append_files, objJadeOptions.CumulateFiles);
				objC3.addChild(objJadeOptions.append_files, objJadeOptions.overwrite_files);

				objCC.getControl(objJadeOptions.Target().createFolders);
				objCC.getLabel(2);
			}
		}
		enableFields();
	}

	@Override
	protected void enableFields() {
	}

}
