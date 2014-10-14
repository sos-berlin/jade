package com.sos.jade.userinterface.composite;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.sos.dialog.components.CompositeBaseClass;

public class OperationComposite extends CompositeBaseClass<JADEOptions> {
	private final String		conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings("unused")
	private static final String	conSVNVersion	= "$Id$";

	public OperationComposite(final CTabFolder pobjTabFolder, final JADEOptions objOptions) {
		super(pobjTabFolder, objOptions);
		objJadeOptions = objOptions;
		if (gflgCreateControlsImmediate == true) {
			createComposite();
			pobjTabFolder.pack();
			pobjTabFolder.layout(true);
		}
	}

	public OperationComposite(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		objJadeOptions = objOptions;
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}
	}

	CCombo	cboProtocol	= null;

	@Override
	public void createComposite() {
		logger.debug("createComposite " + conClassName);

		objCC.getControl(objJadeOptions.title, 3);
		objCC.getControl(objJadeOptions.operation, 3);

		objCC.getSeparator("datasource");

		if (objJadeOptions.operation.isOperationSend() == false) {
			cboProtocol = (CCombo) objCC.getControl(objJadeOptions.Source().protocol);

			setMandatory(objJadeOptions.Target(), false);
			setMandatory(objJadeOptions.Source(), true);

			createConnectionControls(objJadeOptions.Source());

		}
		objCC.getControl(objJadeOptions.Source().FolderName, 3);
		objCC.getControl(objJadeOptions.file_spec, 3);

		objCC.getControl(objJadeOptions.RecurseSubFolders);

		if (objJadeOptions.operation.isOperationDelete() == true) {
			objCC.getLabel();
		}
		else {
			objCC.getControl(objJadeOptions.remove_files);
		}

		if (objJadeOptions.operation.isOperationRename() == true) {
			objCC.getControl(objJadeOptions.Source().ReplaceWhat, 3);
			objCC.getControl(objJadeOptions.Source().ReplaceWith, 3);
		}

		if (objJadeOptions.NeedTargetClient() == true) {
			objCC.getSeparator("datatarget");

			if (objJadeOptions.operation.isOperationReceive() == false) {
				cboProtocol = (CCombo) objCC.getControl(objJadeOptions.Target().protocol);

				setMandatory(objJadeOptions.Target(), true);
				setMandatory(objJadeOptions.Source(), false);

				createConnectionControls(objJadeOptions.Target());
			}
			objCC.getControl(objJadeOptions.Target().FolderName, 3);
			objCC.getControl(objJadeOptions.overwrite_files);
			objCC.getControl(objJadeOptions.Target().makeDirs);
			objCC.getControl(objJadeOptions.Target().ReplaceWhat, 3);
			objCC.getControl(objJadeOptions.Target().ReplaceWith, 3);

			objCC.getSeparator();
		}
		objCC.getControl(objJadeOptions.PreTransferCommands, 3);
		objCC.getControl(objJadeOptions.PostTransferCommands, 3);
		objCC.getControl(objJadeOptions.include, 3);
		//		objCC.getSeparator();
		initValues();
		pack();
		layout(true);
	}

	private void initValues() {
	}

	private void setMandatory(SOSConnection2OptionsAlternate pobjO, final boolean pflgIsMandatory) {
		pobjO.HostName.isMandatory(pflgIsMandatory);
		pobjO.port.isMandatory(pflgIsMandatory);
		pobjO.user.isMandatory(pflgIsMandatory);
		pobjO.password.isMandatory(pflgIsMandatory);
	}

	private void createConnectionControls(SOSConnection2OptionsAlternate pobjO) {
		if (pobjO.protocol.needConnectionData() == true) {
			objCC.getControl(pobjO.port);
			objCC.getControl(pobjO.HostName);
			objCC.getControl(pobjO.user);
			objCC.getControl(pobjO.password);
		}
		objCC.getLabel(2);
	}
}
