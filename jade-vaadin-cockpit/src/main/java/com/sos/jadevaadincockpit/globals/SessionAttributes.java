package com.sos.jadevaadincockpit.globals;

import com.google.common.eventbus.EventBus;
import com.sos.jadevaadincockpit.view.event.EventHelper;
import com.vaadin.server.VaadinSession;

/**
 * 
 * @author JS
 *
 */
public class SessionAttributes {

	public enum Attributes {
		EVENTBUS; // USER

		private Attributes() {
		}

		public Object getValue() {
			Object returnValue;
			try {
				VaadinSession.getCurrent().getLockInstance().lock();
				returnValue = VaadinSession.getCurrent().getAttribute(this.name());
			} finally {
				VaadinSession.getCurrent().getLockInstance().unlock();
			}
			return returnValue;
		}

		public void setValue(Object newValue) {
			try {
				VaadinSession.getCurrent().getLockInstance().lock();
				VaadinSession.getCurrent().setAttribute(this.name(), newValue);
			} finally {
				VaadinSession.getCurrent().getLockInstance().unlock();
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public EventBus getEventBus() {
		return (EventBus) Attributes.EVENTBUS.getValue();
	}
	
	/**
	 * 
	 * @param newValue
	 */
	public void setEventBus(EventBus newValue) {
		Attributes.EVENTBUS.setValue(newValue);
	}
}
