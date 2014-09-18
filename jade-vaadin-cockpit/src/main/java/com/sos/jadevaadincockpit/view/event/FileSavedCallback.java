package com.sos.jadevaadincockpit.view.event;

import java.io.Serializable;

/**
 * 
 * @author JS
 *
 */
public interface FileSavedCallback extends Serializable {
	
	/**
	 * 
	 */
	public void onSuccess();
	
	/**
	 * 
	 */
	public void onFailure();
}
