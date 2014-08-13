package com.sos.jadevaadincockpit.view.components;

import java.util.Locale;

import com.sos.jadevaadincockpit.i18n.I18NComponent;
import com.sos.jadevaadincockpit.i18n.JadeCockpitMsg;
import com.sos.jadevaadincockpit.util.FileUploadDragAndDropWrapper;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author JS
 *
 */
public class DropArea extends CustomComponent implements I18NComponent {
	private static final long serialVersionUID = -4958415043511144354L;
	
	private String message = "";
	
	private FileUploadDragAndDropWrapper dropPanelWrapper = null;
	
	private DropHandler dropHandler = null;
	
	public DropArea() {
		this("");
	}
	
	public DropArea(String message) {
		this.message = message;
		init();
	}
	
	private void init() {
		
		Label infoLabel = new Label(message);
		infoLabel.setSizeUndefined();
		
		VerticalLayout dropPanel = new VerticalLayout(infoLabel);
		dropPanel.setComponentAlignment(infoLabel, Alignment.MIDDLE_CENTER);
		
		dropPanel.setStyleName("drop-area");
		dropPanel.setMargin(true);
		dropPanel.setSizeFull();
		
		dropPanelWrapper = new FileUploadDragAndDropWrapper(dropPanel);
		dropPanelWrapper.setSizeFull();
		setCompositionRoot(dropPanelWrapper);
		dropHandler = dropPanelWrapper.getDropHandler();
	}
	
	/**
	 * Enables/disables the Drag and Drop upload for this component.
	 * @param enabled Drag and Drop upload enabled if true, disabled otherwise
	 */
	public void setDragAndDropUploadEnabled(boolean enabled) {
		if (enabled == true) {
			dropPanelWrapper.setDropHandler(dropHandler);
		} else {
			dropPanelWrapper.setDropHandler(null);
		}
	}
	
	/**
	 * 
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
		// TODO repaint etc
	}
	
	/**
	 * 
	 * @return
	 */
	public String getMessage() {
		return this.message;
	}

	public void refreshLocale(Locale newLocale) {
		// TODO Auto-generated method stub
		
	}
}
