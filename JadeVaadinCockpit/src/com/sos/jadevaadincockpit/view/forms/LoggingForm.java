package com.sos.jadevaadincockpit.view.forms;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.CheckBox;

public class LoggingForm extends BaseForm {
	private static final long serialVersionUID = -78711118553671965L;

	// Components
	private CheckBox debugComponent;
	private AbstractComponent debugLevelComponent;
	
	public LoggingForm(String caption, Item profile) {
		super(profile);
		setCaption(caption);
		
		createForm();
	}

	private void createForm() {
		
		AbstractComponent logFilenameComponent = componentCreator.getComponentWithCaption(jadeOptions.log_filename);
		layout.addComponent(logFilenameComponent);
		
		AbstractComponent log4jPropertyFilenameComponent = componentCreator.getComponentWithCaption(jadeOptions.log4jPropertyFileName);
		layout.addComponent(log4jPropertyFilenameComponent);
		
		debugComponent = (CheckBox) componentCreator.getComponentWithCaption(jadeOptions.Debug);
		layout.addComponent(debugComponent);
		
		debugLevelComponent = componentCreator.getComponentWithCaption(jadeOptions.DebugLevel);
		layout.addComponent(debugLevelComponent);
		
		AbstractComponent bannerHeaderComponent = componentCreator.getComponentWithCaption(jadeOptions.banner_header);
		layout.addComponent(bannerHeaderComponent);
		
		AbstractComponent bannerFooterComponent = componentCreator.getComponentWithCaption(jadeOptions.banner_footer);
		layout.addComponent(bannerFooterComponent);
		
		AbstractComponent historyFilenameComponent = componentCreator.getComponentWithCaption(jadeOptions.HistoryFileName);
		layout.addComponent(historyFilenameComponent);
		
		debugComponent.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 3560687287549225838L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				changeEnabledState(debugComponent.getValue() == null ? false : debugComponent.getValue());
			}
		});
		
		changeEnabledState(debugComponent.getValue() == null ? false : debugComponent.getValue());
	}

	private void changeEnabledState(Boolean enabled) {
		debugLevelComponent.setEnabled(enabled);
	}
}
