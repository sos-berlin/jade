package com.sos.jade.userinterface.composite;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

import com.sos.DataExchange.Options.JADEOptions;

public class Filter4ResultSetComposite extends CompositeBaseClass<JADEOptions> {
	@SuppressWarnings("unused") private final String		conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings("unused") private static final String	conSVNVersion	= "$Id$";
//	@SuppressWarnings({ "unused", "hiding" }) private final Logger		logger			= Logger.getLogger(this.getClass());

	public Filter4ResultSetComposite(final CTabItem parent, final JADEOptions objOptions) {
		this((Composite) parent.getControl(), objOptions);
	}

	public Filter4ResultSetComposite(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		objJadeOptions = objOptions;
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}

	}

	@Override public void createComposite() {
		objCC.getSeparator();
		
		objCC.getControl(objJadeOptions.skip_first_files);
		objCC.getControl(objJadeOptions.skip_last_files);
		objCC.getControl(objJadeOptions.MaxFiles);
		objCC.getSeparator();
	}
}
