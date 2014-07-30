package com.sos.jadevaadincockpit.view.components;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;

public class ComponentGroup extends CustomComponent {
	private static final long serialVersionUID = -8522269715280352564L;
	
	private Panel panel;
	private FormLayout layout;
	
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
	
	public void addComponent(Component c) {
		layout.addComponent(c);
		c.setWidth("100%");
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
