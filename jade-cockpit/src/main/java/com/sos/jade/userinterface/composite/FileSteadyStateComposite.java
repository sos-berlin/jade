package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.dialog.classes.SOSCTabItem;

public class FileSteadyStateComposite extends CompositeBaseClass<JADEOptions> {
	@SuppressWarnings("unused")
	private final String	conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings({ "unused", "hiding" })
	private final Logger	logger			= Logger.getLogger(this.getClass());
	public final String		conSVNVersion	= "$Id: BackgroundServiceComposite.java 21704 2013-12-29 22:35:13Z kb $";

	public FileSteadyStateComposite(final SOSCTabItem parent, final JADEOptions objOptions) {
		this((Composite) parent.getControl(), objOptions);
	}

	public FileSteadyStateComposite(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}
	}

	@Override public void createComposite() {
		{
			objCC.getControl(objJadeOptions.CheckSteadyStateOfFiles);
			objCC.getLabel(2);
			objCC.getControl(objJadeOptions.CheckSteadyStateInterval);
			objCC.getLabel(2);
			objCC.getSeparator();
			objCC.getControl(objJadeOptions.SteadyStateErrorState);
		}
		enableFields();
	}

	@Override protected void enableFields() {
	}
}
