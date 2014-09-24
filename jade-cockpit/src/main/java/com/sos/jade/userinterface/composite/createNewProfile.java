package com.sos.jade.userinterface.composite;
import org.eclipse.swt.widgets.Composite;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Options.SOSOptionJadeOperation.enuJadeOperations;

public class createNewProfile extends CompositeBaseClass<JADEOptions> {
	@SuppressWarnings("unused")
	private final String		conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings("unused")
	private static final String	conSVNVersion	= "$Id: OperationComposite.java 21704 2013-12-29 22:35:13Z kb $";

	public createNewProfile(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		objJadeOptions = objOptions;
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}
	}

	@Override
	public void createComposite() {

		objCC.getControl(objJadeOptions.title, 3);
		objCC.getControl(objJadeOptions.profile, 3);
		if (objJadeOptions.isFragment.isFalse()) {
			objJadeOptions.operation.Value(enuJadeOperations.copy);
			objCC.getControl(objJadeOptions.operation, 3);
		}

		initValues();
	}

	private void initValues() {
	}

}
