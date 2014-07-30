package com.sos.jadevaadincockpit.view.forms;

import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.vaadin.data.Item;

public class SmtpConnectionParameterForm extends BaseForm {
	private static final long serialVersionUID = -1979471559811029989L;

	private SOSConnection2OptionsAlternate jadeConnectionOptions;
	
	public SmtpConnectionParameterForm(String caption, Item profile, SOSConnection2OptionsAlternate jadeConnectionOptions) {
		super(profile);
		setCaption(caption);
		this.jadeConnectionOptions = jadeConnectionOptions;
		
		createForm();
	}

	private void createForm() {
		// TODO Auto-generated method stub
	}

}
