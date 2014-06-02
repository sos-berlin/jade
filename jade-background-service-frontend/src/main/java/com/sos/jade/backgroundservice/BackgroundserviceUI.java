package com.sos.jade.backgroundservice;

import javax.servlet.annotation.WebServlet;

import com.sos.jade.backgroundservice.view.MainView;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

@Theme("bs")
@Title("Jade Background Service")
@SuppressWarnings("serial")
public class BackgroundserviceUI extends UI
{
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

    	MainView ui = new MainView();
        setContent(ui);
		ui.setSizeFull();
		
    }

}
