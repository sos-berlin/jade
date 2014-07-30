package com.sos.jadevaadincockpit.view.forms;

import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.vaadin.data.Item;

public class ImapConnectionParameterForm extends BaseForm {
	private static final long serialVersionUID = 2833403570220811593L;

	private SOSConnection2OptionsAlternate jadeConnectionOptions;
	
	public ImapConnectionParameterForm(String caption, Item section, SOSConnection2OptionsAlternate jadeConnectionOptions) {
		super(section);
		setCaption(caption);
		this.jadeConnectionOptions = jadeConnectionOptions;
		
		createForm();
	}

	private void createForm() {
		// TODO
	}
}
