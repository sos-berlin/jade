package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

import com.sos.DataExchange.Options.JADEOptions;

public class SteadyStateComposite extends CompositeBaseClass<JADEOptions> {
	@SuppressWarnings("unused") private final String		conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings("unused") private static final String	conSVNVersion	= "$Id$";
	@SuppressWarnings({ "unused", "hiding" }) private final Logger		logger			= Logger.getLogger(this.getClass());

	public SteadyStateComposite(final CTabItem parent, final JADEOptions objOptions) {
		this((Composite) parent.getControl(), objOptions);
	}

	public SteadyStateComposite(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		objJadeOptions = objOptions;
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}		
	}

	@Override public void createComposite() {
		objCC.getControl(objJadeOptions.CheckSteadyStateOfFiles);
		objCC.getControl(objJadeOptions.CheckSteadyCount);
		objCC.getControl(objJadeOptions.CheckSteadyStateInterval);
		objCC.getSeparator();
	}
}
