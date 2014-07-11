package com.sos.jade.backgroundservice.view.components;

import com.vaadin.server.Page;
import com.vaadin.ui.Window;

public class AboutWindow extends Window {
	private static final long serialVersionUID = 1L;

	public AboutWindow() {
		setModal(false);
		setDraggable(false);
		setResizable(false);
		setHeight(160.0f, Unit.PIXELS);
		setWidth(400.0f, Unit.PIXELS);
		setPositionX(Page.getCurrent().getBrowserWindowWidth() - 400);
		setPositionY(0);
//		addStyleName("jadeAboutWindow");
		setContent(new AboutLayout());
	}

}
