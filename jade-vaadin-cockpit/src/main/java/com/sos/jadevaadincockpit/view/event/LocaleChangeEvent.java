package com.sos.jadevaadincockpit.view.event;

import java.io.Serializable;
import java.util.Locale;

public class LocaleChangeEvent implements Serializable {
	private static final long serialVersionUID = -3049378482965800132L;
	
	private Locale oldLocale;
	private Locale newLocale;
	
	public LocaleChangeEvent(Locale oldLocale, Locale newLocale) {
		this.oldLocale = oldLocale;
		this.newLocale = newLocale;
	}
	
	public Locale getOldLocale() {
		return oldLocale;
	}
	
	public Locale getNewLocale() {
		return newLocale;
	}
}
