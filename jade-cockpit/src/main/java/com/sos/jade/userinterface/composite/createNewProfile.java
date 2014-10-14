package com.sos.jade.userinterface.composite;
import org.eclipse.swt.widgets.Composite;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Options.SOSOptionJadeOperation.enuJadeOperations;
import com.sos.dialog.components.CompositeBaseClass;
import com.sos.dialog.components.WaitCursor;
import com.sos.dialog.message.ErrorLog;

public class createNewProfile extends CompositeBaseClass<JADEOptions> {
	@SuppressWarnings("unused")
	private final String		conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings("unused")
	private static final String	conSVNVersion	= "$Id: OperationComposite.java 21704 2013-12-29 22:35:13Z kb $";

	public createNewProfile(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		objJadeOptions = objOptions;
//		if (gflgCreateControlsImmediate == true) {
			createComposite();
//		}
	}

	@Override
	public void createComposite() {
		try (WaitCursor objC = new WaitCursor()) {
			this.setVisible(false);
			objCC.getControl(objJadeOptions.title, 3);
			objCC.getControl(objJadeOptions.profile, 3);
			if (objJadeOptions.isFragment.isFalse()) {
				objJadeOptions.operation.Value(enuJadeOperations.copy);
				objCC.getControl(objJadeOptions.operation, 3);
				objCC.getControl(objJadeOptions.Source().protocol, 3);
				objCC.getControl(objJadeOptions.Target().protocol, 3);
			}
			initValues();
		}
		catch (Exception e) {
			new ErrorLog("problem", e);
		}
		finally {
			this.setVisible(true);
		}
	}

	private void initValues() {
	}
}
