package com.sos.jade.backgroundservice.view.components;

import com.sos.jade.backgroundservice.view.MainView;
import com.vaadin.ui.Window;

public class FilterLayoutWindow extends Window {
	private static final long serialVersionUID = 1L;

	public FilterLayoutWindow(MainView ui) {
		setModal(true);
		center();
		setHeight(275.0f, Unit.PIXELS);
		setWidth(635.0f, Unit.PIXELS);
		setContent(new JadeFilterLayout(ui));
	}

}
