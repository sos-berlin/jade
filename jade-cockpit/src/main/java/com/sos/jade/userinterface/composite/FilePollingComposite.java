package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.dialog.classes.SOSCTabItem;

public class FilePollingComposite extends CompositeBaseClass<JADEOptions> {
	@SuppressWarnings("unused") private final String				conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings({ "unused", "hiding" }) private final Logger	logger			= Logger.getLogger(this.getClass());
	public final String												conSVNVersion	= "$Id: BackgroundServiceComposite.java 21704 2013-12-29 22:35:13Z kb $";

	public FilePollingComposite(final SOSCTabItem parent, final JADEOptions objOptions) {
		this((Composite) parent.getControl(), objOptions);
	}

	public FilePollingComposite(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}
	}

	@Override public void createComposite() {
		{
//			objCC.getInvisibleSeparator();

			objCC.getControl(objJadeOptions.poll_interval);
			objCC.getLabel(2);
			objCC.getControl(objJadeOptions.poll_minfiles);
			objCC.getControl(objJadeOptions.poll_timeout);
			objCC.getControl(objJadeOptions.PollKeepConnection);
			objCC.getLabel(2);
		}
		enableFields();
	}

	@Override protected void enableFields() {
	}
}
