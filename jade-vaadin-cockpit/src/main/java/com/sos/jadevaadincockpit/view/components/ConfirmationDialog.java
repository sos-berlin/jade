package com.sos.jadevaadincockpit.view.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

/**
 * 
 * @author JS
 *
 */
public class ConfirmationDialog extends Window implements ClickListener {
	private static final long serialVersionUID = -8446446811658321342L;
	
	private FormLayout layout = new FormLayout();
	private HorizontalLayout buttonLayout = new HorizontalLayout();
	
	Callback callback;
	private Button okButton = new Button("Ok", this);
	private Button cancelButton = new Button("Cancel", this);
	private Label messageLabel = new Label();
	
	/**
	 * 
	 * @param caption
	 * @param message
	 * @param callback
	 */
	public ConfirmationDialog(String caption, String message, Callback callback) {
		super(caption);
		
		this.callback = callback;
		
		setModal(true);
		
		setContent(layout);
		
		if (message != null) {
			this.messageLabel.setCaption(message);
		}
		
		layout.addComponent(messageLabel);
		
		buttonLayout.setSpacing(true);
		buttonLayout.addComponents(okButton, cancelButton);
		layout.addComponent(buttonLayout);
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		close();
		callback.onDialogResult(event.getButton() == okButton);
	}
	
	/**
	 * Set the caption of the OK button.
	 * @param caption
	 */
	public void setOkCaption(String caption) {
		okButton.setCaption(caption);
	}
	
	/**
	 * 
	 * @return The caption of the OK button.
	 */
	public String getOkCaption() {
		return okButton.getCaption();
	}
	
	/**
	 * Set the caption of the Cancel button.
	 * @param caption
	 */
	public void setCancelCaption(String caption) {
		cancelButton.setCaption(caption);
	}
	
	/**
	 * 
	 * @return The caption of the Cancel button.
	 */
	public String getCancelCaption() {
		return cancelButton.getCaption();
	}
	
	/**
	 * Set the dialog's message.
	 * @param message
	 */
	public void setMessage(String message) {
		messageLabel.setCaption(message);
	}
	
	/**
	 * 
	 * @return The dialog's message.
	 */
	public String getMessage() {
		return messageLabel.getCaption();
	}
	
	/**
	 * 
	 */
	public interface Callback {
		/**
		 * 
		 * @param isOk
		 */
		public void onDialogResult(boolean isOk);
	}

}
