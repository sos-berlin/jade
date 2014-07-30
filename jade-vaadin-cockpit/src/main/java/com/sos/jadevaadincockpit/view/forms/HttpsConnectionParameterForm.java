package com.sos.jadevaadincockpit.view.forms;

import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.vaadin.data.Item;

public class HttpsConnectionParameterForm extends BaseForm {
	private static final long serialVersionUID = -7797745409300681043L;
	
	private SOSConnection2OptionsAlternate jadeConnectionOptions;
	
	public HttpsConnectionParameterForm(String caption, Item section, SOSConnection2OptionsAlternate jadeConnectionOptions) {
		super(section);
		setCaption(caption);
		this.jadeConnectionOptions = jadeConnectionOptions;
		
		createForm();
	}

	private void createForm() {
		// TODO
	}

}
