package com.sos.jadevaadincockpit.view.forms;

import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.vaadin.data.Item;

public class FtpSConnectionParameterForm extends BaseForm {
	private static final long serialVersionUID = 5014928100844189928L;
	
	private SOSConnection2OptionsAlternate jadeConnectionOptions;
	
	public FtpSConnectionParameterForm(String caption, Item section, SOSConnection2OptionsAlternate jadeConnectionOptions) {
		super(section);
		setCaption(caption);
		this.jadeConnectionOptions = jadeConnectionOptions;
		
		createForm();
	}

	private void createForm() {
		// TODO
	}

}
