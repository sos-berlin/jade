package com.sos.jadevaadincockpit.view.components;

import com.google.common.eventbus.Subscribe;
import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.i18n.JadeCockpitMsg;
import com.sos.jadevaadincockpit.view.event.LocaleChangeEvent;
import com.vaadin.ui.ComboBox;

/**
 * 
 * @author JS
 *
 */
public class JadeComboBox extends ComboBox {
	private static final long serialVersionUID = 6500320317140192996L;
	
	private JadeCockpitMsg msg;
	
	private boolean isFocused = false;
	
	public static enum PROPERTY {
		VALUE(String.class, "");
		
		private Class<?> type;
		private Object defaultValue;
		
		private PROPERTY(Class<?> type, Object defaultValue) {
			this.type = type;
			this.defaultValue = defaultValue;
		}
		
		public Class<?> getType() {
			return type;
		}
		
		public Object getDefaultValue() {
			return defaultValue;
		}
	};
	
	public JadeComboBox() {
		super();
		JadevaadincockpitUI.getCurrent().getSessionAttributes().getEventBus().register(this);
	}
	
	public JadeComboBox(String caption) {
		super(caption);
		JadevaadincockpitUI.getCurrent().getSessionAttributes().getEventBus().register(this);
	}
	
	public JadeComboBox(JadeCockpitMsg msg) {
		super();
		this.msg = msg;
		
		initializeProperties();
		
		if (msg != null) {
			setCaption(msg.label());
			setDescription(msg.tooltip());			
		}
		
		JadevaadincockpitUI.getCurrent().getSessionAttributes().getEventBus().register(this);
	}
	
	private void initializeProperties() {
		for (PROPERTY prop : PROPERTY.values()) {
			addContainerProperty(prop, prop.getType(), prop.getDefaultValue());
		}
		setItemCaptionPropertyId(PROPERTY.VALUE);
	}
	
	/**
	 * 
	 * @param event
	 */
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
	 * @return the isFocused
	 */
	public boolean isFocused() {
		return isFocused;
	}

	/**
	 * @param isFocused the isFocused to set
	 */
	public void setFocused(boolean isFocused) {
		this.isFocused = isFocused;
	}

}
