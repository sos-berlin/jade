package com.sos.jadevaadincockpit.view.forms;

import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.vaadin.data.Item;
import com.vaadin.ui.AbstractComponent;

public class FileSystemConnectionParameterForm extends BaseForm {
	private static final long serialVersionUID = 3590357207431451425L;
	
	private SOSConnection2OptionsAlternate jadeConnectionOptions;
	
	public FileSystemConnectionParameterForm(String caption, Item section, SOSConnection2OptionsAlternate jadeConnectionOptions) {
		super(section);
		setCaption(caption);
		this.jadeConnectionOptions = jadeConnectionOptions;
		
		createForm();
	}
	
	private void createForm() {

		AbstractComponent preCommandComponent = componentCreator.getComponentWithCaption(jadeConnectionOptions.Pre_Command);
		layout.addComponent(preCommandComponent);
		
		AbstractComponent postCommandComponent = componentCreator.getComponentWithCaption(jadeConnectionOptions.Post_Command);
		layout.addComponent(postCommandComponent);
	}
	
}
