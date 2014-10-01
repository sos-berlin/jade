package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.dialog.classes.SOSCTabItem;
import com.sos.dialog.classes.SOSCheckBox;
import com.sos.dialog.components.CompositeBaseClass;

public class LoggingComposite extends CompositeBaseClass<JADEOptions> {
	@SuppressWarnings("unused") private final Logger	logger					= Logger.getLogger(LoggingComposite.class);
	public final String									conSVNVersion			= "$Id$";
	private SOSCheckBox									btnConcurrentTransfer	= null;

	public LoggingComposite(final SOSCTabItem parent, final JADEOptions objOptions) {
		this((Composite) parent.getControl(), objOptions);
	}

	public LoggingComposite(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}
	}

	@Override public void createComposite() {
		{
			objCC.getControl(objJadeOptions.log_filename, 3);
			objCC.getControl(objJadeOptions.log4jPropertyFileName, 3);
			btnConcurrentTransfer = (SOSCheckBox) objCC.getControl(objJadeOptions.Debug);
			Text txtMaxConcurrentTransfers = (Text) objCC.getControl(objJadeOptions.DebugLevel);
			btnConcurrentTransfer.addChild(txtMaxConcurrentTransfers);
			objCC.getSeparator();
			objCC.getControl(objJadeOptions.banner_header, 3);
			objCC.getControl(objJadeOptions.banner_footer, 3);
			objCC.getSeparator();
			objCC.getControl(objJadeOptions.HistoryFileName, 3);
			initValues();
		}
	}

	private void initValues() {
		btnConcurrentTransfer.setEnabledDisabled();
	}
}
