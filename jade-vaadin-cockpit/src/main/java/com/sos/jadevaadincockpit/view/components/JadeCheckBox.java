package com.sos.jadevaadincockpit.view.components;

import com.google.common.eventbus.Subscribe;
import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.i18n.JadeCockpitMsg;
import com.sos.jadevaadincockpit.view.event.LocaleChangeEvent;
import com.vaadin.ui.CheckBox;

public class JadeCheckBox extends CheckBox {
	private static final long serialVersionUID = 1L;
	private JadeCockpitMsg msg;
	
	public JadeCheckBox() {
		super();
		JadevaadincockpitUI.getCurrent().getSessionAttributes().getEventBus().register(this);
	}
	
	public JadeCheckBox(String caption) {
		super(caption);
		JadevaadincockpitUI.getCurrent().getSessionAttributes().getEventBus().register(this);
	}
	
	public JadeCheckBox(JadeCockpitMsg msg) {
		super();
		this.msg = msg;
		
		if (msg != null) {
			setCaption(msg.label());
			setDescription(msg.tooltip());			
		}
		
		JadevaadincockpitUI.getCurrent().getSessionAttributes().getEventBus().register(this);
	}
	
	@Subscribe
	public void localeChanged(LocaleChangeEvent event) {
		JadeCockpitMsg oldMsg = msg;
		JadeCockpitMsg newMsg = null;
		if (oldMsg != null) {
			newMsg = new JadeCockpitMsg(oldMsg.getMessageCode());
			msg = newMsg;
			updateLocalizedStrings();
		}
	}
	
	/**
	 * 
	 */
	protected void updateLocalizedStrings() {
		setCaption(msg.label());
		setDescription(msg.tooltip());
	}
	
	/**
	 * 
	 * @param newMsg
	 */
	public void setJadeCockpitMsg(JadeCockpitMsg newMsg) {
		msg = newMsg;
		updateLocalizedStrings();
	}
	
}
