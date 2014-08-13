package com.sos.jadevaadincockpit.view.event;

import java.io.Serializable;
import java.util.EventListener;


public interface LocaleChangeListener extends Serializable, EventListener {

	void onLocaleChange(LocaleChangeEvent event);
	
}
