package com.sos.jadevaadincockpit.view.forms;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.jadevaadincockpit.data.ProfileContainer;
import com.sos.jadevaadincockpit.util.UiComponentCreator;
import com.vaadin.data.Item;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;

/**
 * 
 * @author JS
 *
 */
public class BaseForm extends CustomComponent {
	private static final long serialVersionUID = -4582309142575456163L;
	
	protected UiComponentCreator componentCreator;
	protected Item profile;
	protected JADEOptions jadeOptions;
	protected FormLayout layout;
	
	public BaseForm(final Item profile) {
		this("", profile);
	}
	
	public BaseForm(String caption, final Item profile) {
		setCaption(caption);
		this.profile = profile;
		jadeOptions = (JADEOptions) profile.getItemProperty(ProfileContainer.PROPERTY.JADEOPTIONS).getValue();
		componentCreator = new UiComponentCreator(profile);
		layout = new FormLayout();
		setCompositionRoot(layout);
	}
	
	/**
	 * @return the section
	 */
	public Item getSection() {
		return profile;
	}
}
