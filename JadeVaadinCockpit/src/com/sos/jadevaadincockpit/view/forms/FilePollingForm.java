package com.sos.jadevaadincockpit.view.forms;

import com.vaadin.data.Item;
import com.vaadin.ui.AbstractComponent;

public class FilePollingForm extends BaseForm {
	private static final long serialVersionUID = 2549978129645010277L;
	
	public FilePollingForm(String caption, Item section) {
		super(section);
		setCaption(caption);
		
		createForm();
	}

	private void createForm() {
		
		AbstractComponent pollIntervalComponent = componentCreator.getComponentWithCaption(jadeOptions.poll_interval);
		layout.addComponent(pollIntervalComponent);
		AbstractComponent pollMinfilesComponent = componentCreator.getComponentWithCaption(jadeOptions.poll_minfiles);
		layout.addComponent(pollMinfilesComponent);
		AbstractComponent pollTimeoutComponent = componentCreator.getComponentWithCaption(jadeOptions.poll_timeout);
		layout.addComponent(pollTimeoutComponent);
		AbstractComponent pollKeepConnectionComponent = componentCreator.getComponentWithCaption(jadeOptions.PollKeepConnection);
		layout.addComponent(pollKeepConnectionComponent);
		
	}

}
