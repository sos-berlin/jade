package com.sos.jadevaadincockpit.view.forms;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.CheckBox;

public class PollEngineForm extends BaseForm {
	private static final long serialVersionUID = 7323697371900955559L;

	// Components
	private CheckBox pollingServerComponent;
	private AbstractComponent pollingEndAtComponent;
	private AbstractComponent pollingServerDurationComponent;

	public PollEngineForm(String caption, Item profile) {
		super(profile);
		setCaption(caption);
		
		createForm();
	}

	private void createForm() {
		
		pollingServerComponent = (CheckBox) componentCreator.getComponentWithCaption(jadeOptions.PollingServer);
		layout.addComponent(pollingServerComponent);
		
		pollingEndAtComponent = componentCreator.getComponentWithCaption(jadeOptions.pollingEndAt);
		layout.addComponent(pollingEndAtComponent);
		
		pollingServerDurationComponent = componentCreator.getComponentWithCaption(jadeOptions.pollingServerDuration);
		layout.addComponent(pollingServerDurationComponent);
		
		pollingServerComponent.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 5573605377110222422L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				changeEnabledState(pollingServerComponent.getValue() == null ? false : pollingServerComponent.getValue());
			}
		});
		
		changeEnabledState(pollingServerComponent.getValue() == null ? false : pollingServerComponent.getValue());
	}
	
	private void changeEnabledState(Boolean enabled) {
		pollingEndAtComponent.setEnabled(enabled);
		pollingServerDurationComponent.setEnabled(enabled);
	}

}
