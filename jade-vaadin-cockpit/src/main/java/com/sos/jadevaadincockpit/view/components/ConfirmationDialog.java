package com.sos.jadevaadincockpit.view.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseListener;

/**
 * 
 * @author JS
 *
 */
public class ConfirmationDialog extends Window implements ClickListener, CloseListener {
	private static final long serialVersionUID = -8446446811658321342L;
	
	private FormLayout layout = new FormLayout();
	private HorizontalLayout buttonLayout = new HorizontalLayout();
	
	private Callback callback;
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
		buttonLayout.setWidth("100%");
		buttonLayout.addComponents(okButton, cancelButton);
		layout.addComponent(buttonLayout);
		
		layout.setMargin(true);
		layout.setSpacing(true);
		
		center();
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		close();
		callback.onDialogResult(event.getButton() == okButton);
	}

	@Override
	public void windowClose(CloseEvent e) {
		// dialog closed, fire callback
		callback.onDialogResult(false);
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
		 * @param isOk True if OK button was selected, false otherwise.
		 */
		public void onDialogResult(boolean isOk);
	}
}
