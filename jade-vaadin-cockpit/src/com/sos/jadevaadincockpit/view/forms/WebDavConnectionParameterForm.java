package com.sos.jadevaadincockpit.view.forms;

import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.vaadin.data.Item;

public class WebDavConnectionParameterForm extends BaseForm {
	private static final long serialVersionUID = 4820120641337221191L;

	private SOSConnection2OptionsAlternate jadeConnectionOptions;
	
	public WebDavConnectionParameterForm(String caption, Item profile, SOSConnection2OptionsAlternate jadeConnectionOptions) {
		super(profile);
		setCaption(caption);
		this.jadeConnectionOptions = jadeConnectionOptions;
		
		createForm();
	}
	
	private void createForm() {
		// TODO
	}
}
