package com.sos.jadevaadincockpit.viewmodel;

import java.util.HashMap;

import com.sos.JSHelper.Options.SOSOptionElement;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;

/**
 * 
 * @author JS
 *
 */
public class ParameterContainer extends IndexedContainer {
	private static final long serialVersionUID = 5904015232260171282L;
	
	public static enum PROPERTY {
		NAME(String.class, ""), VALUE(String.class, ""), OLDVALUE(String.class, ""), PROFILENAME(String.class, ""), PROFILEID(Object.class, null), OPTIONELEMENT(SOSOptionElement.class, null);
		
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
	
	private HashMap<String, Object> itemIds = new HashMap<String, Object>();
	
	public ParameterContainer() {
		
		addContainerProperty(PROPERTY.NAME, PROPERTY.NAME.getType(), PROPERTY.NAME.getDefaultValue());
		addContainerProperty(PROPERTY.VALUE, PROPERTY.VALUE.getType(), PROPERTY.VALUE.getDefaultValue());
		addContainerProperty(PROPERTY.OLDVALUE, PROPERTY.OLDVALUE.getType(), PROPERTY.OLDVALUE.getDefaultValue());
		addContainerProperty(PROPERTY.PROFILENAME, PROPERTY.PROFILENAME.getType(), PROPERTY.PROFILENAME.getDefaultValue());
		addContainerProperty(PROPERTY.PROFILEID, PROPERTY.PROFILEID.getType(), PROPERTY.PROFILEID.getDefaultValue());
		addContainerProperty(PROPERTY.OPTIONELEMENT, PROPERTY.OPTIONELEMENT.getType(), PROPERTY.OPTIONELEMENT.getDefaultValue());
	}
	
	public Property<String> getNameProperty(Object itemId) {
		return getItem(itemId).getItemProperty(PROPERTY.NAME);
	}
	
	public Property<String> getValueProperty(Object itemId) {
		return getItem(itemId).getItemProperty(PROPERTY.VALUE);
	}
	
	public Property<String> getOldValueProperty(Object itemId) {
		return getItem(itemId).getItemProperty(PROPERTY.OLDVALUE);
	}
	
	public Property<String> getProfileNameProperty(Object itemId) {
		return getItem(itemId).getItemProperty(PROPERTY.PROFILENAME);
	}
	
	public Property<Object> getProfileIdProperty(Object itemId) {
		return getItem(itemId).getItemProperty(PROPERTY.PROFILEID);
	}
	
	public Property<SOSOptionElement> getOptionElementProperty(Object itemId) {
		return getItem(itemId).getItemProperty(PROPERTY.OPTIONELEMENT);
	}
	
}
