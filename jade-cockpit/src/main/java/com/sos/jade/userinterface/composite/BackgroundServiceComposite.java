package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.dialog.classes.SOSCTabItem;

public class BackgroundServiceComposite extends CompositeBaseClass<JADEOptions> {
	@SuppressWarnings({ "unused", "hiding" }) private final Logger	logger				= Logger.getLogger(BackgroundServiceComposite.class);
	public final String									conSVNVersion		= "$Id$";
	private Text										tbxBSHostName		= null;
	private Text										tbxBSPortNumber		= null;
	private CCombo										cboBSTransferMethod	= null;
	private Button										btnAppendFiles		= null;

	public BackgroundServiceComposite(final SOSCTabItem parent, final JADEOptions objOptions) {
		this((Composite) parent.getControl(), objOptions);
	}

	public BackgroundServiceComposite(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		if (CompositeBaseClass.gflgCreateControlsImmediate == true) {
			createComposite();
		}		
		
	}

	@Override public void createComposite() { 
		{
			objCC.getInvisibleSeparator();

			btnAppendFiles = (Button) objCC.getControl(objJadeOptions.SendTransferHistory);
			btnAppendFiles.addSelectionListener(EnableFieldsListener);
			btnAppendFiles.setAlignment(SWT.BOTTOM);
			objCC.getLabel(2);
			tbxBSHostName = (Text) objCC.getControl(objJadeOptions.BackgroundServiceHost, 2);
			objCC.getLabel(1);
			tbxBSPortNumber = (Text) objCC.getControl(objJadeOptions.BackgroundServicePort);
			objCC.getLabel(2);
			cboBSTransferMethod = (CCombo) objCC.getControl(objJadeOptions.Scheduler_Transfer_Method, 2);
			objCC.getLabel(1);
		}
		enableFields();
	}

	@Override protected void enableFields() {
		if (btnAppendFiles.getSelection()) {
			tbxBSHostName.setEnabled(true);
			tbxBSPortNumber.setEnabled(true);
			cboBSTransferMethod.setEnabled(true);
		}
		else {
			tbxBSHostName.setEnabled(false);
			tbxBSPortNumber.setEnabled(false);
			cboBSTransferMethod.setEnabled(false);
		}
	}
}
