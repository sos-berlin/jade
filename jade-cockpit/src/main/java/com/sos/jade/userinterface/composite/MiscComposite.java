package com.sos.jade.userinterface.composite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.dialog.classes.SOSCheckBox;
import com.sos.dialog.components.CompositeBaseClass;

public class MiscComposite extends CompositeBaseClass<JADEOptions> {
	@SuppressWarnings("unused")
	private final String		conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings("unused")
	private static final String	conSVNVersion	= "$Id: OperationComposite.java 21704 2013-12-29 22:35:13Z kb $";

	//	@SuppressWarnings("unused") private final Logger		logger			= Logger.getLogger(this.getClass());

	public MiscComposite(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		objJadeOptions = objOptions;
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}
	}

	private SOSCheckBox	btnConcurrentTransfer	= null;

	@Override
	public void createComposite() {

		objCC.getControl(objJadeOptions.title, 3);

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

		initValues();

	}

	private void initValues() {
		btnConcurrentTransfer.setEnabledDisabled();
	}

}
