package com.sos.jadevaadincockpit.view.forms;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.CheckBox;

public class FileCumulateForm extends BaseForm {
	private static final long serialVersionUID = 2505118696155608176L;
	
	// Components
	private CheckBox cumulateFilesComponent;
	private AbstractComponent cumulativeFileNameComponent;
	private AbstractComponent cumulativeFileSeparatorComponent;
	private AbstractComponent cumulativeFileDeleteComponent;

	public FileCumulateForm(String caption, Item section) {
		super(section);
		setCaption(caption);
		
		createForm();
	}

	private void createForm() {
		cumulateFilesComponent = (CheckBox) componentCreator.getComponentWithCaption(jadeOptions.CumulateFiles);
		layout.addComponent(cumulateFilesComponent);
		
		cumulativeFileNameComponent = componentCreator.getComponentWithCaption(jadeOptions.CumulativeFileName);
		layout.addComponent(cumulativeFileNameComponent);
		
		cumulativeFileSeparatorComponent = componentCreator.getComponentWithCaption(jadeOptions.CumulativeFileSeparator);
		layout.addComponent(cumulativeFileSeparatorComponent);
		
		cumulativeFileDeleteComponent = componentCreator.getComponentWithCaption(jadeOptions.CumulativeFileDelete);
		layout.addComponent(cumulativeFileDeleteComponent);
		
		cumulateFilesComponent.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = -1696930165836003001L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				changeEnabledState(cumulateFilesComponent.getValue() == null ? false : cumulateFilesComponent.getValue());
			}
		});
		
		changeEnabledState(cumulateFilesComponent.getValue() == null ? false : cumulateFilesComponent.getValue());
	}

	protected void changeEnabledState(Boolean enabled) {
		cumulativeFileNameComponent.setEnabled(enabled);
		cumulativeFileSeparatorComponent.setEnabled(enabled);
		cumulativeFileDeleteComponent.setEnabled(enabled);
	}

}
