package com.sos.jadevaadincockpit.view.event;

import java.io.Serializable;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;
import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

public class EventHelper implements Serializable {
	private static final long serialVersionUID = -6752587063767423734L;
	Logger logger = Logger.getLogger(EventHelper.class.getName());
	
	public EventHelper() {
		JadevaadincockpitUI.getCurrent().getSessionAttributes().getEventBus().register(this);
		logger.setLevel(Level.ALL);
	}
	
	@Subscribe
	public void onLocaleChange(LocaleChangeEvent event) {
		
		JadevaadincockpitUI currentUi = JadevaadincockpitUI.getCurrent();
		VaadinSession session = currentUi.getSession();
		session.setLocale(event.getNewLocale());
		
		Iterator<UI> iterator = session.getUIs().iterator();
		while (iterator.hasNext()) {
			JadevaadincockpitUI ui = (JadevaadincockpitUI) iterator.next();
			ui.setLocale(event.getNewLocale());
			ui.getApplicationAttributes().setMessages("com.sos.jadevaadincockpit.i18n.JadeCockpitMessages", event.getNewLocale(), ui.getClass().getClassLoader());
//			ui.getMainView().refreshLocale(event.getNewLocale()); // TODO
		}
		logger.log(Level.FINEST, String.format("Locale changed to %1$s from %2$s", event.getNewLocale().getLanguage(), event.getOldLocale().getLanguage()));
	}
	
}
