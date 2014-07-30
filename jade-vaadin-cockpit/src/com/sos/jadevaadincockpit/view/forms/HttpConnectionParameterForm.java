package com.sos.jadevaadincockpit.view.forms;

import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.vaadin.data.Item;

public class HttpConnectionParameterForm extends BaseForm {
	private static final long serialVersionUID = 3764751896413062474L;

	private SOSConnection2OptionsAlternate jadeConnectionOptions;
	
	public HttpConnectionParameterForm(String caption, Item section, SOSConnection2OptionsAlternate jadeConnectionOptions) {
		super(section);
		setCaption(caption);
		this.jadeConnectionOptions = jadeConnectionOptions;
		
		createForm();
	}

	private void createForm() {
		// TODO
	}

}
