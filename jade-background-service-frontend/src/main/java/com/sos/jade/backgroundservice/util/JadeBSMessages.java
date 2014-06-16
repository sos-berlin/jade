package com.sos.jade.backgroundservice.util;

import java.io.Serializable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class JadeBSMessages implements Serializable{

	private static final long serialVersionUID = 1L;
	private ResourceBundle bundle;
	private Locale locale = Locale.getDefault();
	private String bundleName;

	public JadeBSMessages(String bundleName, Locale locale){
		this.bundleName = bundleName;
		if (locale != null){
			this.locale = locale;
		} else {
			this.locale = Locale.getDefault();
		}
		try{
			bundle = ResourceBundle.getBundle(bundleName, locale);
		} catch (MissingResourceException mre){
			bundle = ResourceBundle.getBundle(bundleName);
		}
	}
	
	public ResourceBundle getBundle(){
		return bundle;
	}
	
	public String getValue(String key){
		return bundle.getString(key);
	}
	
	public void setLocale (Locale locale){
		this.locale = locale;
		this.bundle = ResourceBundle.getBundle(bundleName, locale);
	}
}
