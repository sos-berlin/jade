package com.sos.jade.backgroundservice.view.components;

import com.sos.JSHelper.Basics.VersionInfo;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class AboutLayout extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	
	public AboutLayout() {
 		this.setHeight(100.0f, Unit.PIXELS);
		this.setWidth("100%");
		this.setSpacing(true);
		setPrimaryStyleName("jadeAboutWindow");
		init();
	}
	
	private void init(){
		Label release = new Label("Version: " + VersionInfo.VERSION_STRING);
		addComponent(release);
		Label allRights = new Label("all rights reserved.");
		addComponent(allRights);
	}
}
