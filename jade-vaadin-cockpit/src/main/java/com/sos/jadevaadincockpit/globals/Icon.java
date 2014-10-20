package com.sos.jadevaadincockpit.globals;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;

public enum Icon {

	PROFILE("icons/profile_16.png"),
	FRAGMENT("icons/fragment_16.png"),
	GLOBALS("icons/globals_16.png"),
	SETTINGSFILE("icons/settingsfile_16.png");
	
	private String resourceId;
	private Resource resource;
	
	private Icon(String resourceId) {
		this.resourceId = resourceId;
		this.resource = new ThemeResource(resourceId);
	}
	
	public Resource getResource() {
		return resource;
	}
	
	public String getResourceId() {
		return resourceId;
	}

}
