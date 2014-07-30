package com.sos.jadevaadincockpit.view.forms;

import com.vaadin.data.Item;
import com.vaadin.ui.AbstractComponent;

public class JitlForm extends BaseForm {
	private static final long serialVersionUID = -4719262519342893731L;
	
	public JitlForm(String caption, Item profile) {
		super(profile);
		setCaption(caption);
		
		createForm();
	}

	private void createForm() {
		
		AbstractComponent createOrderComponent = componentCreator.getComponentWithCaption(jadeOptions.create_order);
		layout.addComponent(createOrderComponent);
		
		AbstractComponent createOrdersForAllFilesComponent = componentCreator.getComponentWithCaption(jadeOptions.create_orders_for_all_files);
		layout.addComponent(createOrdersForAllFilesComponent);
		
		AbstractComponent mergeOrderParameterComponent = componentCreator.getComponentWithCaption(jadeOptions.MergeOrderParameter);
		layout.addComponent(mergeOrderParameterComponent);
		
		AbstractComponent nextStateComponent = componentCreator.getComponentWithCaption(jadeOptions.next_state);
		layout.addComponent(nextStateComponent);
		
		AbstractComponent onEmptyResultSetComponent = componentCreator.getComponentWithCaption(jadeOptions.on_empty_result_set);
		layout.addComponent(onEmptyResultSetComponent);
		
		AbstractComponent orderJobchainNameComponent = componentCreator.getComponentWithCaption(jadeOptions.order_jobchain_name);
		layout.addComponent(orderJobchainNameComponent);
		
		AbstractComponent raiseErrorIfResultSetIsComponent = componentCreator.getComponentWithCaption(jadeOptions.raise_error_if_result_set_is);
		layout.addComponent(raiseErrorIfResultSetIsComponent);
		
		AbstractComponent pollErrorStateComponent = componentCreator.getComponentWithCaption(jadeOptions.PollErrorState);
		layout.addComponent(pollErrorStateComponent);
		
		AbstractComponent steadyStateErrorStateComponent = componentCreator.getComponentWithCaption(jadeOptions.SteadyStateErrorState);
		layout.addComponent(steadyStateErrorStateComponent);
	}
}
