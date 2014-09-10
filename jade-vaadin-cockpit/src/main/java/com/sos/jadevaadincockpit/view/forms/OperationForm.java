package com.sos.jadevaadincockpit.view.forms;

import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.data.ProfileContainer;
import com.sos.jadevaadincockpit.view.components.JadeTextField;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;

public class OperationForm extends BaseForm {
	private static final long serialVersionUID = 4628358468761073373L;
	
	// Components
	private CheckBox concurrentTransferComponents;
	private AbstractComponent maxConcurrentTransfersComponent;
	private AbstractComponent reuseConnectionComponent;
	private AbstractComponent preTransferCommandsComponent;
	private AbstractComponent postTransferCommandsComponent;
	private JadeTextField includeComponent;
	private Button resolveIncludesButton;
	
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
		
		preTransferCommandsComponent = componentCreator.getComponentWithCaption(jadeOptions.PreTransferCommands);
		layout.addComponent(preTransferCommandsComponent);
		
		postTransferCommandsComponent = componentCreator.getComponentWithCaption(jadeOptions.PostTransferCommands);
		layout.addComponent(postTransferCommandsComponent);
		
		includeComponent = (JadeTextField) componentCreator.getComponentWithCaption(jadeOptions.include);
		includeComponent.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 299132474135018944L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO
			}
		});
		layout.addComponent(includeComponent);
		
		resolveIncludesButton = new Button("Resolve includes");
		resolveIncludesButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 6324115208155007518L;

			@Override
			public void buttonClick(ClickEvent event) {
				// TODO muss hier wirklich erst gespeichert werden?
				Object profileId = profile.getItemProperty(ProfileContainer.PROPERTY.ID).getValue();
				JadevaadincockpitUI.getCurrent().getApplicationAttributes().getJadeSettingsFile().saveSettingsFile(profileId);
				jadeOptions.ReadSettingsFile();
			}
		});
		
		layout.addComponent(resolveIncludesButton);
		
		
//		AbstractComponent verboseComponent = componentCreator.getComponentWithCaption(jadeOptions.verbose);
//		layout.addComponent(verboseComponent);
		
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
