package com.sos.jadevaadincockpit;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;

import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.sos.jadevaadincockpit.data.JadeSettingsFile;
import com.sos.jadevaadincockpit.globals.ApplicationAttributes;
import com.sos.jadevaadincockpit.globals.SessionAttributes;
import com.sos.jadevaadincockpit.view.MainView;
import com.sos.jadevaadincockpit.view.event.EventHelper;
import com.sos.jadevaadincockpit.view.event.LocaleChangeEvent;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.UI;

@Theme("jadevaadincockpit")
@Push
@PreserveOnRefresh
/**
 * 
 * @author JS
 *
 */
public class JadevaadincockpitUI extends UI {
	private static final long serialVersionUID = -7272363253057365783L;
	
	private MainView mainView;
	private ApplicationAttributes applicationAttributes;
	private SessionAttributes sessionAttributes;
	
	private static final Logger logger = Logger.getLogger(JadevaadincockpitUI.class.getName());
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = JadevaadincockpitUI.class, widgetset = "com.sos.jadevaadincockpit.AppWidgetSet")
	public static class Servlet extends VaadinServlet {
		private static final long serialVersionUID = 3426748071296101789L;
	}

	@Override
	protected void init(VaadinRequest request) {
		
		initializeLogging();
		
		initializeLocale();
		
		initializeSessionAttributes();
		
		initializeApplicationAttributes();
		
		new EventHelper();
		
		// Create the applications main UI. TODO eigene Methode
		mainView = new MainView();			
		setContent(mainView);
		mainView.setSizeFull();
		
		Page.getCurrent().setTitle("JADE Cockpit");
	}
	
	/**
	 * Initialize the logging framework.
	 */
	private void initializeLogging() {
		// Set "basePath" system property to make it available in the log4j.properties file.
		System.setProperty("basePath", ApplicationAttributes.getBasePath());
		
		// Might have been installed in another UI.
		if (!SLF4JBridgeHandler.isInstalled()) {
			// Initialize the slf4j logging bridge.
			SLF4JBridgeHandler.install();			
		}
		logger.setLevel(Level.ALL);
		logger.log(Level.FINEST, "SLF4J logging bridge installed in UI '" + this.getUIId() +"'."); // TODO
	}
	
	/**
	 * Initialize the locale with the current value of the default locale for this session.
	 * This affects only this current UI.
	 */
	private void initializeLocale() {
		getSession().setLocale(new Locale("en", "gb"));
		// Get the default locale for this session.
		Locale locale = getSession().getLocale();
		// Call to affect this current UI.
		setLocale(locale);
		
		logger.log(Level.FINEST, String.format("Locale set to %1$s, %2$s", locale.getLanguage(), locale.getCountry()));
	}
	
	/**
	 * Check and set various session-scoped attributes.
	 */
	private void initializeSessionAttributes() {
		sessionAttributes = new SessionAttributes();
		// Check if the EventBus had been initialized. Create a new one if thats not the case.
		if (sessionAttributes.getEventBus() == null) {
			sessionAttributes.setEventBus(new EventBus("SessionEventBus"));
		}
		sessionAttributes.getEventBus().register(this);
	}
	
	/**
	 * Check and set various application-scoped wide attributes.
	 */
	private void initializeApplicationAttributes() {
		applicationAttributes = new ApplicationAttributes();
		// Set the message resources for this application.
		applicationAttributes.setMessages("com.sos.jadevaadincockpit.i18n.JadeCockpitMessages", getSession().getLocale(), getClass().getClassLoader());
		// Create a new settings file for this application.
		applicationAttributes.setJadeSettingsFile(new JadeSettingsFile());
	}
	
	/**
	 * Gets the currently used UI.
	 * @return the current UI instance if available
	 */
	public static JadevaadincockpitUI getCurrent() {
		return (JadevaadincockpitUI) UI.getCurrent();
	}
	
	/**
	 * Get the session specific attributes.
	 * @return the session attributes
	 */
	public SessionAttributes getSessionAttributes() {
		return sessionAttributes;
	}
	
	/**
	 * Get the UI specific attributes.
	 * @return the application attributes
	 */
	public ApplicationAttributes getApplicationAttributes() {
		return applicationAttributes;
	}
	
	/** FIXME
	 * 
	 * @param newLocale
	 */@Deprecated
	public void changeLocale(Locale newLocale) {
		Locale oldLocale = getSession().getLocale();
		
		setLocale(newLocale);
		getSession().setLocale(newLocale);
		// TODO andere UIs beachten. bisher werden nur die aktuelle und alle zukünftigen UIs aktualisiert
		
		logger.log(Level.FINEST, String.format("Locale set to %1$s, %2$s", newLocale.getLanguage(), newLocale.getCountry()));
		applicationAttributes.setMessages("com.sos.jadevaadincockpit.i18n.JadeCockpitMessages", newLocale, getClass().getClassLoader());
		
		sessionAttributes.getEventBus().post(new LocaleChangeEvent(oldLocale, newLocale));
		
//		mainView.refreshLocale(newLocale);
	}
	
	/**
	 * Get the application's main view class.
	 * @return the main view
	 */
	public MainView getMainView() {
		return mainView;
	}
}