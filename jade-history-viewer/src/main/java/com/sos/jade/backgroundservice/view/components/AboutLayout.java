package com.sos.jade.backgroundservice.view.components;

import com.sos.JSHelper.Basics.VersionInfo;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class AboutLayout extends VerticalLayout {

    private static final long serialVersionUID = 1L;
    private static final float DEFAULT_HEIGHT = 100.0f;

    public AboutLayout() {
        this.setHeight(DEFAULT_HEIGHT, Unit.PIXELS);
        this.setWidth("100%");
        this.setSpacing(true);
        setPrimaryStyleName("jadeAboutWindow");
        init();
    }

    private void init() {
        Label release = new Label("Version: " + VersionInfo.VERSION_STRING);
        addComponent(release);
        Label allRights = new Label("all rights reserved.");
        addComponent(allRights);
    }
}
