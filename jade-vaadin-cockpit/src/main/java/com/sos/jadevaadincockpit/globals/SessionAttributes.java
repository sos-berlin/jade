package com.sos.jadevaadincockpit.globals;

import com.vaadin.server.VaadinSession;

/**
 * 
 * @author JS
 *
 */
public class SessionAttributes {

	public enum Attributes {
		SESSION_ID, JADESETTINGSFILE;

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
	
	public static Object getSessionId() {
		return (Object) Attributes.SESSION_ID.getValue();
	}
	
	public static void setSessionId(Object newValue) {
		Attributes.SESSION_ID.setValue(newValue);
	}
	
	public static JadeSettingsFile getJadeSettingsFile() {
		return (JadeSettingsFile) Attributes.JADESETTINGSFILE.getValue();
	}
	
	public static void setJadeSettingsFile(JadeSettingsFile newValue) {
		Attributes.JADESETTINGSFILE.setValue(newValue);
	}
}
