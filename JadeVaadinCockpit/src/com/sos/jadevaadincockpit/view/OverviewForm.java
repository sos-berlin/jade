package com.sos.jadevaadincockpit.view;

import java.util.HashMap;

import com.sos.JSHelper.Options.SOSOptionElement;
import com.sos.jadevaadincockpit.view.forms.BaseForm;
import com.sos.jadevaadincockpit.viewmodel.ProfileContainer;
import com.vaadin.data.Item;
import com.vaadin.ui.AbstractComponent;

public class OverviewForm extends BaseForm {
	private static final long serialVersionUID = 1150202986237485137L;

	public OverviewForm(String caption, Item profile) {
		super(profile);
		setCaption(caption);
		
		createForm();
		
	}
	
	@SuppressWarnings("unchecked")
	private void createForm() {
		
		HashMap<String, SOSOptionElement> options = (HashMap<String, SOSOptionElement>) profile.getItemProperty(ProfileContainer.PROPERTY.OPTIONS).getValue();

		for (String s : options.keySet()) {
			SOSOptionElement optionElement = options.get(s);
			AbstractComponent comp = componentCreator.getComponentWithCaption(optionElement);
			layout.addComponent(comp);
		}
	}

}
