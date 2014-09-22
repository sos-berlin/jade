package com.sos.jade.userinterface.composite;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.dialog.classes.SOSCheckBox;

public class OperationComposite extends CompositeBaseClass<JADEOptions> {
	private final String		conClassName			= this.getClass().getSimpleName();
	@SuppressWarnings("unused")
	private static final String	conSVNVersion			= "$Id$";
	private SOSCheckBox			btnConcurrentTransfer	= null;

	public OperationComposite(final CTabFolder pobjTabFolder, final JADEOptions objOptions) {
		super(pobjTabFolder, objOptions);
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}
		pobjTabFolder.pack();
	}

	public OperationComposite(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		objJadeOptions = objOptions;
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}
	}

	@Override
	public void createComposite() {
		logger.debug("createComposite " + conClassName);

//		objCC.getSeparator();
		objCC.getControl(objJadeOptions.title, 3);
		objCC.getControl(objJadeOptions.operation, 3);
		
		
		@SuppressWarnings("unused")
		SOSCheckBox btnCheckButton = (SOSCheckBox) objCC.getControl(objJadeOptions.TransactionMode);
		objCC.getLabel(2);
		objCC.getControl(objJadeOptions.atomic_prefix);
		objCC.getControl(objJadeOptions.atomic_suffix);
		btnConcurrentTransfer = (SOSCheckBox) objCC.getControl(objJadeOptions.ConcurrentTransfer);
		Text txtMaxConcurrentTransfers = (Text) objCC.getControl(objJadeOptions.MaxConcurrentTransfers);
		SOSCheckBox btnReuseConnection = (SOSCheckBox) objCC.getControl(objJadeOptions.reuseConnection);
		btnConcurrentTransfer.addChild(txtMaxConcurrentTransfers);
		btnConcurrentTransfer.addChild(btnReuseConnection);
		objCC.getSeparator();
		objCC.getControl(objJadeOptions.PreTransferCommands, 3);
		objCC.getControl(objJadeOptions.PostTransferCommands, 3);
		objCC.getControl(objJadeOptions.include, 3);
		objCC.getSeparator();
		initValues();
	}

	private void initValues() {
		btnConcurrentTransfer.setEnabledDisabled();
	}
}
