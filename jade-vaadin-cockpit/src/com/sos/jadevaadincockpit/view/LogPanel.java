package com.sos.jadevaadincockpit.view;

import com.sos.jadevaadincockpit.util.IVaadinLogAppender;
import com.vaadin.data.Item;
import com.vaadin.server.Resource;
import com.vaadin.ui.TextArea;

/**
 * 
 * @author JS
 *
 */
public class LogPanel extends TextArea implements IVaadinLogAppender {
	private static final long serialVersionUID = 3060129196230688920L;
	
	private String log = "";
	private String startTime = "";
	private Resource iconResource = null;
	
	public LogPanel(Item profile) {

		setWordwrap(false);
		setSizeFull();
		setData(profile);
		setReadOnly(true);
	}	

	@Override
	public void doLog(String logLine) {
		setReadOnly(false);
		log += logLine;
//		log += System.lineSeparator();
		setValue(log);
		setReadOnly(true);
	}

	@Override
	public void doClose() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the iconResource
	 */
	public Resource getIconResource() {
		return iconResource;
	}

	/**
	 * @param iconResource the iconResource to set
	 */
	public void setIconResource(Resource iconResource) {
		this.iconResource = iconResource;
	}

}

