package com.sos.jade.backgroundservice;

import javax.servlet.annotation.WebServlet;

import com.sos.jade.backgroundservice.data.SessionAttributes;
import com.sos.jade.backgroundservice.options.JadeBackgroundServiceOptions;
import com.sos.jade.backgroundservice.view.MainView;
import com.sos.jade.backgroundservice.view.components.filter.FilterLayoutWindow;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

@Theme("bs")
@Title("Jade Background Service")
@Push
public class BackgroundserviceUI extends UI {
	private static final long serialVersionUID = 1L;
	private MainView ui;
	private FilterLayoutWindow modalWindow;

	public final static JadeBackgroundServiceOptions objOptions = new JadeBackgroundServiceOptions();
	@WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = BackgroundserviceUI.class)
    public static class Servlet extends VaadinServlet {
		private static final long serialVersionUID = 1L;
    }

    @Override
    protected void init(final VaadinRequest request) {
		try {
			VaadinSession.getCurrent().getLockInstance().lock();
			if (VaadinSession.getCurrent().getAttribute(SessionAttributes.SESSION_ID.name()) == null) {
				VaadinSession.getCurrent().setAttribute(SessionAttributes.SESSION_ID.name(), SessionAttributes.SESSION_ID);
			}
		} finally {
			VaadinSession.getCurrent().getLockInstance().unlock();
		}
    	ui = new MainView();
        setContent(ui);
		ui.setSizeFull();
		modalWindow = new FilterLayoutWindow(ui);
    }

	public FilterLayoutWindow getModalWindow() {
		return modalWindow;
	}

}
