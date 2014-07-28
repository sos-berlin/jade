package com.sos.jade.backgroundservice.view.components;

import com.vaadin.server.Page;
import com.vaadin.ui.Window;

public class AboutWindow extends Window {
	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 400;
	private static final float WIDTH_FL = 400.0f;
	public AboutWindow() {
		setModal(false);
		setDraggable(false);
		setResizable(false);
		setHeight(160.0f, Unit.PIXELS);
		setWidth(WIDTH_FL, Unit.PIXELS);
		setPositionX(Page.getCurrent().getBrowserWindowWidth() - WIDTH);
		setPositionY(0);
		setContent(new AboutLayout());
	}

}
