package com.sos.jadevaadincockpit.view.forms;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.CheckBox;

public class OperationForm extends BaseForm {
	private static final long serialVersionUID = 4628358468761073373L;
	
	// Components
	private CheckBox concurrentTransferComponents;
	private AbstractComponent maxConcurrentTransfersComponent;
	private AbstractComponent reuseConnectionComponent;
	
	public OperationForm(String caption, Item profile) {
		super(caption, profile);
		
		createForm();
	}
	
	private void createForm() {

		AbstractComponent titleComponent = componentCreator.getComponentWithCaption(jadeOptions.title);
		layout.addComponent(titleComponent);

		AbstractComponent isFragmentComponent = componentCreator.getComponentWithCaption(jadeOptions.isFragment);
		layout.addComponent(isFragmentComponent);
		
		AbstractComponent operationComponent = componentCreator.getComponentWithCaption(jadeOptions.operation);
		layout.addComponent(operationComponent);
		
		AbstractComponent transactionModeComponent = componentCreator.getComponentWithCaption(jadeOptions.TransactionMode);
		layout.addComponent(transactionModeComponent);
		
		AbstractComponent atomicPrefixComponent = componentCreator.getComponentWithCaption(jadeOptions.atomic_prefix);
		layout.addComponent(atomicPrefixComponent);

		AbstractComponent atomicSuffixComponent = componentCreator.getComponentWithCaption(jadeOptions.atomic_suffix);
		layout.addComponent(atomicSuffixComponent);

		concurrentTransferComponents = (CheckBox) componentCreator.getComponentWithCaption(jadeOptions.ConcurrentTransfer);
		layout.addComponent(concurrentTransferComponents);
		
		maxConcurrentTransfersComponent = componentCreator.getComponentWithCaption(jadeOptions.MaxConcurrentTransfers);
		layout.addComponent(maxConcurrentTransfersComponent);

		reuseConnectionComponent = componentCreator.getComponentWithCaption(jadeOptions.reuseConnection);
		layout.addComponent(reuseConnectionComponent);
		
		AbstractComponent preTransferCommandsComponent = componentCreator.getComponentWithCaption(jadeOptions.PreTransferCommands);
		layout.addComponent(preTransferCommandsComponent);
		
		AbstractComponent postTransferCommandsComponent = componentCreator.getComponentWithCaption(jadeOptions.PostTransferCommands);
		layout.addComponent(postTransferCommandsComponent);
		
		AbstractComponent includeComponent = componentCreator.getComponentWithCaption(jadeOptions.include);
		layout.addComponent(includeComponent);
		
		AbstractComponent verboseComponent = componentCreator.getComponentWithCaption(jadeOptions.verbose);
		layout.addComponent(verboseComponent);
		
		concurrentTransferComponents.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 299132474135018944L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				changeEnabledState(concurrentTransferComponents.getValue() == null ? false : concurrentTransferComponents.getValue());
			}
		});
		
		changeEnabledState(concurrentTransferComponents.getValue() == null ? false : concurrentTransferComponents.getValue());
	}
	
	private void changeEnabledState(boolean enabled) {
		maxConcurrentTransfersComponent.setEnabled(enabled);
		reuseConnectionComponent.setEnabled(enabled);
	}
}
