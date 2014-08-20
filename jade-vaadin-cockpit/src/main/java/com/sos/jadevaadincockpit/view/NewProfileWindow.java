package com.sos.jadevaadincockpit.view;

import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.i18n.JadeCockpitMsg;
import com.sos.jadevaadincockpit.view.components.JadeTextField;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;

/**
 * 
 * @author JS
 *
 */
public class NewProfileWindow extends Window {
	private static final long serialVersionUID = -5835180354807542093L;
	
	FormLayout vLayout;
	HorizontalLayout hLayout;
	
	private JadeTextField nameTextField;
	private Button okButton;
	private Button cancelButton;
	private Notification nameNotValidNotification = new Notification(new JadeCockpitMsg("jade_n_nameNotValid").label(), Notification.Type.ERROR_MESSAGE);
	
	public NewProfileWindow() {
		
		setCaption(new JadeCockpitMsg("jade_l_newProfileTitle").label());
		
		vLayout = new FormLayout();
		hLayout = new HorizontalLayout();
		
		nameTextField = new JadeTextField(new JadeCockpitMsg("jade_l_profile"));
		/**
		 * FIXME This seems like an awkward workaround. The ShorcutListener has to be removed
		 * when moving from the TextField, as it would react to the event fired from
		 * anywhere in the application
		 */
		nameTextField.addFocusListener(new FocusListener() {
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
				
				nameTextField.addShortcutListener(shortcutListener);
				nameTextField.addBlurListener(new BlurListener() {
					private static final long serialVersionUID = 5454343524587840811L;

					@Override
					public void blur(BlurEvent event) {
						nameTextField.removeShortcutListener(shortcutListener);
					}
				});
				
			}
		});
		nameTextField.focus();
		
		okButton = new Button(new JadeCockpitMsg("jade_b_ok").label());
		cancelButton = new Button(new JadeCockpitMsg("jade_b_cancel").label());
		
		okButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = -1601728589946232816L;

			@Override
			public void buttonClick(ClickEvent event) {

				String profileName = nameTextField.getValue();

				if (JadevaadincockpitUI.getCurrent().getApplicationAttributes().getJadeSettingsFile().addProfile(profileName)) {
					close();
				} else {
					nameNotValidNotification.show(Page.getCurrent());
				}
			}
		});
		
		cancelButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 3084812570022080915L;

			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		
		hLayout.addComponents(okButton, cancelButton);
		vLayout.addComponents(nameTextField, hLayout);
		hLayout.setWidth("100%");
		
		setContent(vLayout);
		vLayout.setWidth("100%");
		vLayout.setMargin(true);
		vLayout.setSpacing(true);
		
		setResizable(false);
		setWidth("20%");
		center();
	}
}
