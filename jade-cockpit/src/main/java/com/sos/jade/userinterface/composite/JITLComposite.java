package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

import com.sos.DataExchange.Options.JADEOptions;

public class JITLComposite extends CompositeBaseClass<JADEOptions> {
	@SuppressWarnings("unused") private final String		conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings("unused") private static final String	conSVNVersion	= "$Id$";
	@SuppressWarnings({ "unused", "hiding" }) private final Logger		logger			= Logger.getLogger(this.getClass());

	public JITLComposite(final CTabItem parent, final JADEOptions objOptions) {
		this((Composite) parent.getControl(), objOptions);
	}
//
	public JITLComposite(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		objJadeOptions = objOptions;
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}

	}

	@Override public void createComposite() {
		objCC.getSeparator();
		objCC.getControl(objJadeOptions.create_order);
		objCC.getControl(objJadeOptions.create_orders_for_all_files);
		objCC.getControl(objJadeOptions.MergeOrderParameter);
		objCC.getControl(objJadeOptions.next_state);
		objCC.getControl(objJadeOptions.on_empty_result_set);
		objCC.getControl(objJadeOptions.order_jobchain_name);
		objCC.getControl(objJadeOptions.raise_error_if_result_set_is);
		objCC.getControl(objJadeOptions.PollErrorState);
		objCC.getControl(objJadeOptions.SteadyStateErrorState);
		objCC.getSeparator();
	}
}
