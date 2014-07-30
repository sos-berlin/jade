package com.sos.jadevaadincockpit.view.forms;

import com.vaadin.data.Item;
import com.vaadin.ui.AbstractComponent;

public class FilterForResultSetForm extends BaseForm {
	private static final long serialVersionUID = -6941762216284572391L;
	
	public FilterForResultSetForm(String caption, Item section) {
		super(section);
		setCaption(caption);
		
		createForm();
	}

	private void createForm() {
		
		// Components
		AbstractComponent skipFirstFileComponent = componentCreator.getComponentWithCaption(jadeOptions.skip_first_files);
		layout.addComponent(skipFirstFileComponent);

		AbstractComponent skipLastFileComponent = componentCreator.getComponentWithCaption(jadeOptions.skip_last_files);
		layout.addComponent(skipLastFileComponent);
		
		AbstractComponent maxFilesComponent = componentCreator.getComponentWithCaption(jadeOptions.MaxFiles);
		layout.addComponent(maxFilesComponent);
		
	}

	


}
