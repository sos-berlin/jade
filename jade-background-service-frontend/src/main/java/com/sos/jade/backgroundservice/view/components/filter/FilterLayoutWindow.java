package com.sos.jade.backgroundservice.view.components.filter;

import com.vaadin.ui.Window;

public class FilterLayoutWindow extends Window {
	private static final long serialVersionUID = 1L;

	public FilterLayoutWindow() {
		setModal(true);
		center();
		setHeight(330.0f, Unit.PIXELS);
		setWidth(350.0f, Unit.PIXELS);
		setContent(new JadeFilesHistoryFilterLayout());
	}

}
