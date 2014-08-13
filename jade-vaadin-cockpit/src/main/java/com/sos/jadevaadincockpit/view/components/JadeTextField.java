package com.sos.jadevaadincockpit.view.components;

import com.google.common.eventbus.Subscribe;
import com.sos.jadevaadincockpit.globals.ApplicationAttributes;
import com.sos.jadevaadincockpit.i18n.JadeCockpitMsg;
import com.sos.jadevaadincockpit.view.event.LocaleChangeEvent;
import com.sos.jadevaadincockpit.view.event.LocaleChangeListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

/**
 * 
 * @author JS
 *
 */
public class JadeTextField extends TextField {
	private static final long serialVersionUID = -7967998186821025196L;
	
	private JadeCockpitMsg msg;
	
	public JadeTextField() {
		this(null);
	}
	
	public JadeTextField(JadeCockpitMsg msg) {
		super();
		this.msg = msg;
		
		if (msg != null) {
			setCaption(msg.label());
			setDescription(msg.tooltip());			
		}
		
//		JadevaadincockpitUI.getCurrent().addLocaleChangeListener(this);
		
		ApplicationAttributes.getEventBus().register(this);
	}
	
	@Subscribe
	public void localeChanged(LocaleChangeEvent event) {
		Notification.show("LocaleChangeEvent received! " + String.format("Changing Locale from '%1$s' to '%2$s'", event.getOldLocale(), event.getNewLocale()));
		JadeCockpitMsg oldMsg = msg;
		JadeCockpitMsg newMsg = null;
		if (oldMsg != null) {
			newMsg = new JadeCockpitMsg(oldMsg.getMessageCode());
			msg = newMsg;
			updateLocale();
		}
	}
	
	/**
	 * 
	 */
	protected void updateLocale() {
		setCaption(msg.label());
		setDescription(msg.tooltip());
	}
	
	public void setJadeCockpitMsg(JadeCockpitMsg newMsg) {
		msg = newMsg;
		updateLocale();
	}
	
}
