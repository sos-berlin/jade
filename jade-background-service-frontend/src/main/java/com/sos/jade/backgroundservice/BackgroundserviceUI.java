package com.sos.jade.backgroundservice;

import java.io.File;

import javax.servlet.annotation.WebServlet;

import com.sos.jade.backgroundservice.view.MainUI;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@SuppressWarnings("serial")
public class BackgroundserviceUI extends UI
{
	private String absolutePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = BackgroundserviceUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
		try {
			VaadinSession.getCurrent().getLockInstance().lock();
			if (VaadinSession.getCurrent().getAttribute(SessionAttributes.SESSION_ID.name()) == null) {
				VaadinSession.getCurrent().setAttribute(SessionAttributes.SESSION_ID.name(), SessionAttributes.SESSION_ID);
			}
		} finally {
			VaadinSession.getCurrent().getLockInstance().unlock();
		}

    	MainUI ui = new MainUI();
        setContent(ui);
		ui.setSizeFull();
		
		Page.getCurrent().setTitle("JADE Background Service");
    }

}
