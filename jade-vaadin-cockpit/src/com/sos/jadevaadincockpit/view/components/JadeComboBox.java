package com.sos.jadevaadincockpit.view.components;

import com.vaadin.ui.ComboBox;

/**
 * 
 * @author JS
 *
 */
public class JadeComboBox extends ComboBox {
	private static final long serialVersionUID = 6500320317140192996L;
	
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
		for (PROPERTY prop : PROPERTY.values()) {
			addContainerProperty(prop, prop.getType(), prop.getDefaultValue());
		}
		setItemCaptionPropertyId(PROPERTY.VALUE);
	}

}
