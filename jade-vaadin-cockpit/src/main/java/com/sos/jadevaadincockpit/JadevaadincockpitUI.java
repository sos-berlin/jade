package com.sos.jadevaadincockpit;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;

import org.slf4j.bridge.SLF4JBridgeHandler;

import com.sos.jadevaadincockpit.globals.ApplicationAttributes;
import com.sos.jadevaadincockpit.globals.JadeSettingsFile;
import com.sos.jadevaadincockpit.globals.SessionAttributes;
import com.sos.jadevaadincockpit.view.JadeMainUi;
import com.sos.jadevaadincockpit.view.event.LocaleChangeEvent;
import com.sos.jadevaadincockpit.view.event.LocaleChangeListener;
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
	private ApplicationAttributes applicationAttributes;
	
	private static final Logger logger = Logger.getLogger(JadevaadincockpitUI.class.getName());
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = JadevaadincockpitUI.class)
	public static class Servlet extends VaadinServlet {
		private static final long serialVersionUID = 3426748071296101789L;
	}

	@Override
	protected void init(VaadinRequest request) {

		initializeLogging();
		
		initializeLocale();
		
		initializeApplicationAttributes();
		
		// set some session specific attributes
		SessionAttributes.setJadeSettingsFile(new JadeSettingsFile());
		
		// create the applications main UI
		mainUi = new JadeMainUi();			
		setContent(mainUi);
		mainUi.setSizeFull();
		
		Page.getCurrent().setTitle("JADE Cockpit");
	}
	
	/**
	 * 
	 */
	private void initializeLogging() {
		// set "basePath" system property to make it available in the log4j.properties file
		System.setProperty("basePath", ApplicationAttributes.getBasePath());
		
		// initialize the slf4j logging bridge
		SLF4JBridgeHandler.install();
		logger.setLevel(Level.ALL);
		logger.log(Level.FINEST, "SLF4J logging bridge installed."); // TODO
	}
	
	/**
	 * Initializes the locale with the current value of the default locale for this session.
	 * This affects only this current UI.
	 */
	private void initializeLocale() {
		// Get the default locale for this session
		Locale locale = getSession().getLocale();
		// Call to affect this current UI
		setLocale(locale);
		
		logger.log(Level.FINEST, String.format("Locale set to %1$s, %2$s", locale.getLanguage(), locale.getCountry()));
	}
	
	/**
	 * JadeSettingsFile
	 */
	private void initializeApplicationAttributes() {
		applicationAttributes = new ApplicationAttributes();
		
		applicationAttributes.setMessages("com.sos.jadevaadincockpit.i18n.JadeCockpitMessages", getSession().getLocale(), getClass().getClassLoader());
	}
	
	/**
	 * Gets the currently used UI.
	 * @return the current UI instance if available
	 */
	public static JadevaadincockpitUI getCurrent() {
		return (JadevaadincockpitUI) UI.getCurrent();
	}
	
	/** TODO This is going to be replaced by listeners and the event bus
	 * Changes the applications locale and refreshes all components
	 * @param newLocale
	 */
	@Deprecated
	public void changeLocale(Locale newLocale) {
		Locale oldLocale = getSession().getLocale();
		
		setLocale(newLocale);
		getSession().setLocale(newLocale);
		logger.log(Level.FINEST, String.format("Locale set to %1$s, %2$s", newLocale.getLanguage(), newLocale.getCountry()));
		applicationAttributes.setMessages("com.sos.jadevaadincockpit.i18n.JadeCockpitMessages", getSession().getLocale(), getClass().getClassLoader());
		
//		fireLocaleChangeEvent(new LocaleChangeEvent(oldLocale, newLocale));
		
		ApplicationAttributes.getEventBus().post(new LocaleChangeEvent(oldLocale, newLocale));
		
		mainUi.refreshLocale(newLocale);
	}
	
	/**
	 * Gets the applications mainUi class.
	 * @return the mainUi class
	 */
	public JadeMainUi getJadeMainUi() {
		return mainUi;
	}
}