package com.sos.jade.backgroundservice;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;

import com.sos.jade.backgroundservice.data.SessionAttributes;
import com.sos.jade.backgroundservice.options.JadeBackgroundServiceOptions;
import com.sos.jade.backgroundservice.view.MainView;
import com.sos.jade.backgroundservice.view.components.filter.FilterLayoutWindow;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinServletResponse;
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
	public static Cookie cookie;
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
				String remoteHost = ((VaadinServletRequest) request).getHttpServletRequest().getRemoteHost();
				setCookieUsage(request);
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
	
	private void setCookieUsage(final VaadinRequest request){
		Cookie[] cookies = ((VaadinServletRequest) request).getCookies();
	    for (Cookie cookie : cookies){
	    	if (cookie.getName().equals("JadeBS")){
	    		this.cookie = cookie;
		    	this.cookie.setPath("/");
		    	this.cookie.setMaxAge(604800); // 604800 sec => one week 
	    	}
	    }
    	VaadinServletResponse response = ((VaadinServletResponse) VaadinService.getCurrentResponse());
	    if(this.cookie == null){
	    	this.cookie = new Cookie("JadeBS", "");
	    	this.cookie.setPath("/");
	    	this.cookie.setMaxAge(604800); // 604800 sec => one week 
	    }
        response.addCookie(this.cookie);
	}

}
