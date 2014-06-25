package com.sos.jade.backgroundservice.data;

import java.io.Serializable;

import com.sos.jade.backgroundservice.util.JadeBSMessages;

public class JadeHistoryDetailItem implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String key;
	private Object value;
	private String displayName;
	private String messageKey;
	private JadeBSMessages messages;

	public JadeHistoryDetailItem (JadeBSMessages messages, String key, Object value, String messageKey){
		this.messages = messages;
		this.key = key;
		this.value = value;
		this.messageKey = messageKey;
		this.displayName = messages.getValue(messageKey);
		
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
	public String getDisplayName() {
		return messages.getValue(messageKey);
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}
	
}
