package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.dialog.classes.SOSCheckBox;

public class FileCumulateComposite extends CompositeBaseClass<JADEOptions> {
	@SuppressWarnings("unused")
	private final String	conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings({ "unused", "hiding" })
	private final Logger	logger			= Logger.getLogger(this.getClass());
	public final String		conSVNVersion	= "$Id: BackgroundServiceComposite.java 21704 2013-12-29 22:35:13Z kb $";

	public FileCumulateComposite(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}
	}

	private SOSCheckBox btnCumulateFiles = null;
	/**
	 * 
	 */
	@Override public void createComposite() {
		objCC.getInvisibleSeparator();

		btnCumulateFiles =  objCC.getCheckBox(objJadeOptions.CumulateFiles);
		objCC.getLabel(2);
		btnCumulateFiles.addChild(objCC.getControl(objJadeOptions.CumulativeFileName, 3));
		btnCumulateFiles.addChild(objCC.getControl(objJadeOptions.CumulativeFileSeparator));
		objCC.getLabel(2);
		btnCumulateFiles.addChild(objCC.getControl(objJadeOptions.CumulativeFileDelete));
		enableFields();
		initValues();
	}

	/**
	 * 
	 */
	@Override protected void enableFields() {
	}
	
	private void initValues() {
		btnCumulateFiles.setEnabledDisabled();
	}
	
}
