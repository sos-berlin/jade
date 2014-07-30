package com.sos.jadevaadincockpit;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;

import org.slf4j.bridge.SLF4JBridgeHandler;

import com.sos.jadevaadincockpit.globals.Globals;
import com.sos.jadevaadincockpit.util.JadeDataProvider;
import com.sos.jadevaadincockpit.view.JadeMainUi;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

@Theme("jadevaadincockpit")
/**
 * 
 * @author JS
 *
 */
public class JadevaadincockpitUI extends UI {
	private static final long serialVersionUID = -7272363253057365783L;
	
	private JadeMainUi mainUi;
	
//	private static final Logger logger = Logger.getLogger(JadevaadincockpitUI.class.getName());
	private static final Logger logger = Logger.getLogger("rootLogger");
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = JadevaadincockpitUI.class)
	public static class Servlet extends VaadinServlet {
		private static final long serialVersionUID = 3426748071296101789L;
	}

	@Override
	protected void init(VaadinRequest request) {
		
		// initialize the slf4j logging bridge
		SLF4JBridgeHandler.install();
		logger.log(Level.FINEST, "SLF4J logging bridge installed."); // TODO
		
/* maybe use this later for session attributes
		try {
			VaadinSession.getCurrent().getLockInstance().lock();
			if (VaadinSession.getCurrent().getAttribute(SessionAttributes.SESSION_ID.name()) == null) {
				VaadinSession.getCurrent().setAttribute(SessionAttributes.SESSION_ID.name(), SessionAttributes.SESSION_ID);
			}
		} finally {
			VaadinSession.getCurrent().getLockInstance().unlock();
		}
*/
		// set the applications locale
		getSession().setLocale(new Locale("en", "US"));
		
		// set the message resources
		Globals.setMessages("com.sos.jadevaadincockpit.i18n.JadeCockpitMessages", getSession().getLocale(), this.getClass().getClassLoader());
		
		// create the applications mainUi
		mainUi = new JadeMainUi();
		setContent(mainUi);
		mainUi.setSizeFull();
		
		Page.getCurrent().setTitle("JADE Cockpit");
	}
	
	/**
	 * Gets the currently used UI.
	 * @return the current UI instance if available
	 */
	public static JadevaadincockpitUI getCurrent() {
		return (JadevaadincockpitUI) UI.getCurrent();
	}
	
	/**
	 * Gets the applications mainUi class.
	 * @return the mainUi class
	 */
	public JadeMainUi getJadeMainUi() {
		return mainUi;
	}
}