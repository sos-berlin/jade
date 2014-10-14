package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.dialog.classes.SOSCheckBox;
import com.sos.dialog.components.CompositeBaseClass;

public class JITLComposite extends CompositeBaseClass<JADEOptions> {
	@SuppressWarnings("unused")
	private final String		conClassName		= this.getClass().getSimpleName();
	@SuppressWarnings("unused")
	private static final String	conSVNVersion		= "$Id$";
	@SuppressWarnings({ "unused"})
	private final Logger		logger				= Logger.getLogger(this.getClass());

	private SOSCheckBox			btnCreateAnOrder	= null;

	public JITLComposite(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		objJadeOptions = objOptions;
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}
	}

	@Override
	public void createComposite() {
//		objCC.getLabel();
		objCC.getSeparator("order_creation_mode");
		btnCreateAnOrder = (SOSCheckBox) objCC.getControl(objJadeOptions.create_order, 3);
		btnCreateAnOrder.addSelectionListener(EnableFieldsListener);

		btnCreateAnOrder.addChild(objCC.getControl(objJadeOptions.create_orders_for_all_files));
		btnCreateAnOrder.addChild(objCC.getControl(objJadeOptions.MergeOrderParameter));
		btnCreateAnOrder.addChild(objCC.getControl(objJadeOptions.order_jobchain_name));
		btnCreateAnOrder.addChild(objCC.getControl(objJadeOptions.next_state));
		objCC.getLabel();
		
		objCC.getSeparator("on_no_data_found");
		objCC.getControl(objJadeOptions.raise_error_if_result_set_is, 3);
		objCC.getControl(objJadeOptions.on_empty_result_set, 3);
		
		objCC.getLabel();
		objCC.getSeparator("poll_for_files");
		objCC.getControl(objJadeOptions.PollErrorState, 3);
		
		objCC.getLabel();
		objCC.getSeparator("check_steady_state");
		objCC.getControl(objJadeOptions.SteadyStateErrorState, 3);

		enableFields();
	}

	@Override
	protected void enableFields() {
		btnCreateAnOrder.setEnabledDisabled();
	}

}
