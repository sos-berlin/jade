package com.sos.jadevaadincockpit.view.forms;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.CheckBox;

public class BackgroundServiceForm extends BaseForm {
	private static final long serialVersionUID = 9033935348492670766L;
	
	// Components
	private CheckBox sendTransferHistoryComponent;
	private AbstractComponent backgroundServiceHostComponent;
	private AbstractComponent backgroundServicePortComponent;
	private AbstractComponent schedulerTransferMethodComponent;
	
	public BackgroundServiceForm(String caption, Item profile) {
		super(profile);
		setCaption(caption);
		
		
		setCompositionRoot(layout);
		createForm();
	}

	private void createForm() {
		sendTransferHistoryComponent = (CheckBox) componentCreator.getComponentWithCaption(jadeOptions.SendTransferHistory);
		layout.addComponent(sendTransferHistoryComponent);
		backgroundServiceHostComponent = componentCreator.getComponentWithCaption(jadeOptions.BackgroundServiceHost);
		layout.addComponent(backgroundServiceHostComponent);
		backgroundServicePortComponent = componentCreator.getComponentWithCaption(jadeOptions.BackgroundServicePort);
		layout.addComponent(backgroundServicePortComponent);
		schedulerTransferMethodComponent = componentCreator.getComponentWithCaption(jadeOptions.Scheduler_Transfer_Method);
		layout.addComponent(schedulerTransferMethodComponent);
		
		sendTransferHistoryComponent.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 2620707786861560881L;
			@Override
			public void valueChange(ValueChangeEvent event) {
				changeEnabledState(sendTransferHistoryComponent.getValue() == null ? false : sendTransferHistoryComponent.getValue());
			}
		});
		sendTransferHistoryComponent.setImmediate(true);
		
		changeEnabledState(sendTransferHistoryComponent.getValue() == null ? false : sendTransferHistoryComponent.getValue());
		
	}
	
	private void changeEnabledState(Boolean enabled) {
		backgroundServiceHostComponent.setEnabled(enabled);
		backgroundServicePortComponent.setEnabled(enabled);
		schedulerTransferMethodComponent.setEnabled(enabled);
	}

}
