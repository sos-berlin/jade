package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.dialog.classes.SOSCheckBox;

public class PollEngineComposite extends CompositeBaseClass<JADEOptions> {
	@SuppressWarnings("unused")
	private final String		conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings("unused")
	private static final String	conSVNVersion	= "$Id$";
	@SuppressWarnings("unused")
	private final Logger		logger			= Logger.getLogger(this.getClass());

	public PollEngineComposite(final CTabItem parent, final JADEOptions objOptions) {
		this((Composite) parent.getControl(), objOptions);
	}

	public PollEngineComposite(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		objJadeOptions = objOptions;
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}
	}
	SOSCheckBox	btnPollingServer	= null;

	@Override public void createComposite() {
		objCC.getSeparator();
		btnPollingServer = objCC.getCheckBox(objJadeOptions.PollingServer);
		objCC.getLabel(2);
		btnPollingServer.addChild(objCC.getControl(objJadeOptions.pollingEndAt));
		btnPollingServer.addChild(objCC.getControl(objJadeOptions.pollingServerDuration));
		objCC.getSeparator();
		initValues();
	}

	protected void initValues() {
		btnPollingServer.setEnabledDisabled();
	}
}
