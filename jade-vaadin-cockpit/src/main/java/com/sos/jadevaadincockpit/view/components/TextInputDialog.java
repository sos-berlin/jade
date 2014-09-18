package com.sos.jadevaadincockpit.view.components;

import com.sos.jadevaadincockpit.i18n.JadeCockpitMsg;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class TextInputDialog extends Window implements ClickListener {
	private static final long serialVersionUID = -8446446811658321342L;
	
	private FormLayout layout = new FormLayout();
	private HorizontalLayout buttonLayout = new HorizontalLayout();
	
	private Callback callback;
	private Button okButton = new Button(new JadeCockpitMsg("jade_b_ok").label(), this);
	private Button cancelButton = new Button(new JadeCockpitMsg("jade_b_cancel").label(), this);
	private Label messageLabel = new Label();
	private TextField inputField = new TextField();
	
	private String emptyInputMessage = "";
	private boolean emptyInputAllowed = true;
	
	/**
	 * 
	 * @param caption
	 * @param message
	 * @param callback
	 */
	public TextInputDialog(String caption, String message, Callback callback) {
		super(caption);
		
		this.callback = callback;
		
		setModal(true);
		
		setContent(layout);
		
		if (message != null) {
			this.messageLabel.setCaption(message);
		}
		
		layout.addComponent(messageLabel);
		layout.addComponent(inputField);
		
		buttonLayout.setSpacing(true);
		buttonLayout.setWidth("100%");
		buttonLayout.addComponents(okButton, cancelButton);
		layout.addComponent(buttonLayout);
		
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setWidth("100%");
		
		/**
		 * FIXME This seems like an awkward workaround. The ShorcutListener has to be removed
		 * when moving from the TextField, as it would react to the event fired from
		 * anywhere in the application. No better solution found.
		 */
		inputField.addFocusListener(new FocusListener() {
			private static final long serialVersionUID = -4902217207569657646L;

			@Override
			public void focus(FocusEvent event) {
				
				final ShortcutListener shortcutListener = new ShortcutListener("", KeyCode.ENTER, null) {
					private static final long serialVersionUID = -5731205965313085989L;

					@Override
					public void handleAction(Object sender, Object target) {
						okButton.click();
					}
				};
				
				inputField.addShortcutListener(shortcutListener);
				inputField.addBlurListener(new BlurListener() {
					private static final long serialVersionUID = 5454343524587840811L;

					@Override
					public void blur(BlurEvent event) {
						inputField.removeShortcutListener(shortcutListener);
					}
				});
				
			}
		});
		inputField.focus();
		
		setWidth("20%");
		center();
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		// cancelled, close the dialog and fire callback
		if (event.getButton() == cancelButton) {
			close();
			callback.onDialogResult(false, inputField.getValue());
		} else {
			// ok selected, check if empty input is allowed and input is empty
			if (!emptyInputAllowed && inputField.getValue().isEmpty()) {
				// input must not be empty, show error message, do NOT close the dialog
				Notification.show(emptyInputMessage, Notification.Type.ERROR_MESSAGE);
			} else {
				// close the dialog and fire callback
				close();
				callback.onDialogResult(true, inputField.getValue());
			}
		}
	}
	
	/**
	 * Define whether an empty input is allowed. If thats not the case, a message can be defined with <code>setEmptyInputMessage</code>.
	 * Setting the value to true will reset the emptyInputMessage.
	 * @param allowed default is true.
	 */
	public void setEmptyInputAllowed(boolean allowed) {
		emptyInputAllowed = allowed;
		if (!allowed) {
			setEmptyInputMessage(""); // reset the message
		}
	}
	
	/**
	 * Check whether an empty input is allowed.
	 * @return true is it is allowed, false otherwise
	 */
	public boolean isEmptyInputAllowed() {
		return emptyInputAllowed;
	}
	
	/**
	 * Defines the message which is shown if <code>setEmptyInputAllowed()</code> is set to false and the input is empty.
	 * @param message default is "".
	 */
	public void setEmptyInputMessage(String message) {
		emptyInputMessage = message;
	}
	
	/**
	 * Get the message which is shown if <code>setEmptyInputAllowed()</code> is set to false and the input is empty.
	 * @return the message
	 */
	public String getEmptyInputMessage() {
		return emptyInputMessage;
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
	
	public interface Callback {
		
		/**
		 * 
		 * @param isOk
		 * @param input
		 */
		public void onDialogResult(boolean isOk, String input);
	}

}
