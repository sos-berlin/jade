package com.sos.jade.backgroundservice.view.components.filter;

import com.vaadin.ui.Window;

public class FilterLayoutWindow extends Window {

    private static final long serialVersionUID = 1L;
    private static final float DEFAULT_HEIGHT = 330.0f;
    private static final float DEFAULT_WIDTH = 350.0f;

    public FilterLayoutWindow() {
        setModal(true);
        center();
        setHeight(DEFAULT_HEIGHT, Unit.PIXELS);
        setWidth(DEFAULT_WIDTH, Unit.PIXELS);
        setContent(new JadeFilesHistoryFilterLayout());
    }

}
