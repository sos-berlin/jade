package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;

public class FtpConectionParameterComposite extends CompositeBaseClass<SOSConnection2OptionsAlternate> {
	@SuppressWarnings("unused") private final String		conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings("unused") private static final String	conSVNVersion	= "$Id$";
	@SuppressWarnings({ "unused", "hiding" }) private final Logger		logger			= Logger.getLogger(this.getClass());

	public FtpConectionParameterComposite(final CTabItem parent, final SOSConnection2OptionsAlternate objOptions) {
		this((Composite) parent.getControl(), objOptions);
	}

	public FtpConectionParameterComposite(final Composite parent, final SOSConnection2OptionsAlternate objOptions) {
		super(parent, objOptions);
		objJadeOptions = objOptions;
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}

	}

	@Override public void createComposite() {
		objCC.getControl(objJadeOptions.HostName);
		objCC.getControl(objJadeOptions.port);
		objCC.getControl(objJadeOptions.user);
		objCC.getControl(objJadeOptions.password);
		objCC.getControl(objJadeOptions.account);
		objCC.getControl(objJadeOptions.transfer_mode);
		objCC.getControl(objJadeOptions.loadClassName, 3);
		objCC.getControl(objJadeOptions.passive_mode);
		objCC.getControl(objJadeOptions.ProtocolCommandListener);
	}
}
