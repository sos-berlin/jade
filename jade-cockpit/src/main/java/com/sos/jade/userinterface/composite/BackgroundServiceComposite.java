package com.sos.jade.userinterface.composite;
import org.eclipse.swt.widgets.Composite;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.dialog.classes.SOSCheckBox;
import com.sos.dialog.components.CompositeBaseClass;

public class BackgroundServiceComposite extends CompositeBaseClass<JADEOptions> {
	public final String	conSVNVersion			= "$Id$";
	//	private final Text		tbxBSHostName			= null;
	//	private final Text		tbxBSPortNumber			= null;
	//	private final CCombo	cboBSTransferMethod		= null;
	private SOSCheckBox	btnSendTransferHistory	= null;

	public BackgroundServiceComposite(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		if (CompositeBaseClass.gflgCreateControlsImmediate == true) {
			createComposite();
		}
	}

	@Override
	public void createComposite() {
		{
			btnSendTransferHistory = (SOSCheckBox) objCC.getControl(objJadeOptions.SendTransferHistory);
			btnSendTransferHistory.addSelectionListener(EnableFieldsListener);
			objCC.getLabel(2);
			btnSendTransferHistory.addChild(objCC.getControl(objJadeOptions.BackgroundServiceHost, 2));
			objCC.getLabel(1);
			btnSendTransferHistory.addChild(objCC.getControl(objJadeOptions.BackgroundServicePort));
			objCC.getLabel(2);
			btnSendTransferHistory.addChild(objCC.getControl(objJadeOptions.BackgroundServiceTransferMethod, 2));
			objCC.getLabel(1);
		}
		enableFields();
	}

	@Override
	protected void enableFields() {
		btnSendTransferHistory.setEnabledDisabled();
	}
}
