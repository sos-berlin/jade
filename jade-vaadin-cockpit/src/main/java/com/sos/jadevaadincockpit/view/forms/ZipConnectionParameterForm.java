package com.sos.jadevaadincockpit.view.forms;

import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.vaadin.data.Item;

public class ZipConnectionParameterForm extends BaseForm {
	private static final long serialVersionUID = 7541252466295449907L;
	
	private SOSConnection2OptionsAlternate jadeConnectionOptions;
	
	public ZipConnectionParameterForm(String caption, Item profile, SOSConnection2OptionsAlternate jadeConnectionOptions) {
		super(profile);
		setCaption(caption);
		this.jadeConnectionOptions = jadeConnectionOptions;
		
		createForm();
	}

	private void createForm() {
		// TODO
	}
}
