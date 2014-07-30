package com.sos.jadevaadincockpit;

import java.util.UUID;

public enum SessionAttributes {
	SESSION_ID(UUID.class, UUID.randomUUID());
	
	private Class<?> type;
	private Object defaultValue;
	private Object value;
	
	private SessionAttributes(Class<?> type, Object defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;
		this.value = defaultValue;
	}
	
	public Class<?> getType() {
		return type;
	}
	
	public Object getDefaultValue() {
		return defaultValue;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object newValue) {
		value = newValue;
	}
}
