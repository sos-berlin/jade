package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;

public class ZipConectionParameterComposite extends CompositeBaseClass<SOSConnection2OptionsAlternate> {
	@SuppressWarnings("unused") 	private final String		conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings("unused") 	private static final String	conSVNVersion	= "$Id$";
	@SuppressWarnings("unused") 	private final Logger		logger			= Logger.getLogger(this.getClass());

	public ZipConectionParameterComposite(final CTabItem parent, final SOSConnection2OptionsAlternate objOptions) {
		this((Composite) parent.getControl(), objOptions);
	}

	public ZipConectionParameterComposite(final Composite parent, final SOSConnection2OptionsAlternate objOptions) {
		super(parent, objOptions);
		objJadeOptions = objOptions;
		if (CompositeBaseClass.gflgCreateControlsImmediate == true) {
			createComposite();
		}		
	}

	@Override
	public void createComposite() {
//		setMandatory(objJadeConnectionOptions.protocol);
		composite.layout();

	}
}
