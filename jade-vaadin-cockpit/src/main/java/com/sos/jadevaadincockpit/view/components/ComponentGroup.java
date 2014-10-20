package com.sos.jadevaadincockpit.view.components;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;

/**
 * 
 * @author JS
 *
 */
public class ComponentGroup extends CustomComponent {
	private static final long serialVersionUID = -8522269715280352564L;
	
	private Panel panel;
	private FormLayout layout;
	
	/**
	 * Creates a group of vertically aligned components and adds a caption to the group.
	 * @param caption
	 */
	public ComponentGroup(String caption) {
		
		panel = new Panel();
		layout = new FormLayout();
		setCaption(caption);
		setWidth("80%");
		panel.setSizeFull();
		panel.setContent(layout);
		layout.setSizeFull();
		layout.setMargin(true);
		setCompositionRoot(panel);
	}
	
	/**
	 * 
	 * @param c
	 */
	public void addComponent(Component c) {
		layout.addComponent(c);
		c.setWidth("100%");
	}
	
	/**
	 * Adds the components in the given order to this component container.
	 * @param c The components to add.
	 */
	public void addComponents(Component... c) {
		for (Component comp : c) {
			layout.addComponent(comp);
			comp.setWidth("100%");
		}
	}
	
//	public void setWidth(String width) {
//		panel.setWidth(width);
//	}
//	
//	public void setWidth(float width, Unit unit) {
//		panel.setWidth(width, unit);
//	}
	
	public void setCaption(String caption) {
		panel.setCaption(caption);
	}
	
	public String getCaption() {
		return panel.getCaption();
	}
}
