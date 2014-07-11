package com.sos.jade.backgroundservice.view.components;

import static com.sos.jade.backgroundservice.BackgroundserviceUI.jadeBsOptions;
import static com.sos.jade.backgroundservice.BackgroundserviceUI.parentNodeName;

import java.util.Locale;
import java.util.prefs.Preferences;

import com.sos.auth.SOSJaxbSubject;
import com.sos.auth.rest.SOSWebserviceAuthenticationRecord;
import com.sos.auth.rest.client.SOSRestShiroClient;
import com.sos.auth.rest.permission.model.SOSPermissionShiro;
import com.sos.jade.backgroundservice.BackgroundserviceUI;
import com.sos.jade.backgroundservice.constants.JadeBSConstants;
import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.sos.jade.backgroundservice.view.MainView;
import com.sos.jade.backgroundservice.view.components.filter.FilterLayoutWindow;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;


public class LoginView extends CustomComponent implements View, Button.ClickListener {
	private static final long serialVersionUID = 1L;
    private static final String   COMMAND_PERMISSIONS = "/jobscheduler/rest/sosPermission/permissions?user=%s&pwd=%s";
	public static final String NAME = "login";
	private TextField txtUser;
	private PasswordField txtPasswd;
	private Button loginButton;
    private SOSJaxbSubject currentUser;
    private String user;
    private String passwd;
	private Preferences prefs = jadeBsOptions.getPreferenceStore();

	private JadeBSMessages messages = new JadeBSMessages("JADEBSMessages",Locale.getDefault());

	public LoginView() {
		setSizeFull();
		txtUser = new TextField("User:");
		txtUser.setWidth("300px");
		txtUser.setRequired(true);
		txtUser.setInputPrompt("username");
		txtUser.setInvalidAllowed(false);
		txtPasswd = new PasswordField("Password:");
		txtPasswd.setWidth("300px");
//		txtPasswd.addValidator(new PasswordValidator());
		txtPasswd.setRequired(true);
		txtPasswd.setValue("");
		txtPasswd.setNullRepresentation("");

		loginButton = new Button("Login", this);
		loginButton.setClickShortcut(KeyCode.ENTER, null);
		VerticalLayout fields = new VerticalLayout(txtUser, txtPasswd, loginButton);
		fields.setCaption("To access the application, please login with your jobscheduler credentials.");
		fields.setSpacing(true);
		fields.setMargin(new MarginInfo(true, true, true, false));
		fields.setSizeUndefined();

		VerticalLayout viewLayout = new VerticalLayout(fields);
		viewLayout.setSizeFull();
		viewLayout.setComponentAlignment(fields, Alignment.MIDDLE_CENTER);
		setCompositionRoot(viewLayout);
		
	}

	@Override
	public void buttonClick(ClickEvent event) {
		user = txtUser.getValue(); 
		passwd = this.txtPasswd.getValue();
		boolean isValid;
		try {
			isValid = doLogin();
		} catch (Exception e) {
			isValid = false;
		}
		if(isValid){ 
			//Store the current user in the session
			getSession().setAttribute("user", getUser());
			((BackgroundserviceUI)getUI()).parentNodeName = getUser();
			parentNodeName = getUser();
			prefs.node(parentNodeName);
			// Navigate to main view
			((BackgroundserviceUI)getUI()).getMainView().initView();
			((BackgroundserviceUI)getUI()).setModalWindow(new FilterLayoutWindow());
			getUI().getNavigator().navigateTo(MainView.NAME);
		} else {
			// Wrong password:
			// clear the currentUser(shiro) and the password field and refocuses it
			currentUser = null;
			this.txtPasswd.setValue(null);
			this.txtPasswd.focus();
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		txtUser.focus();
	}

    private boolean doLogin() throws Exception {
        SOSRestShiroClient sosRestShiroClient = new SOSRestShiroClient();
        SOSWebserviceAuthenticationRecord sosWebserviceAuthenticationRecord = new SOSWebserviceAuthenticationRecord();
        
        try {
        	if (getUser() != null){
        		sosWebserviceAuthenticationRecord.setUser(getUser());
        		sosWebserviceAuthenticationRecord.setPassword(getPasswd());
        		sosWebserviceAuthenticationRecord.setResource(JadeBSConstants.JS_AUTH_SERVER + COMMAND_PERMISSIONS);
        		sosWebserviceAuthenticationRecord.setSessionId("");
           
        		SOSPermissionShiro sosPermissionShiro = sosRestShiroClient.getPermissions(sosWebserviceAuthenticationRecord);
        		currentUser = new SOSJaxbSubject(sosPermissionShiro);
                if (currentUser == null) {
                	Notification.show("error authenticating user/password", Type.ERROR_MESSAGE);
                }else if (!currentUser.isAuthenticated()) {
        			Notification.show("could not authenticate user/password", Type.HUMANIZED_MESSAGE);
        		}
        	}
        }catch (Exception e) {
        	Notification.show("No security server available!", Type.ERROR_MESSAGE);
        }
        return (currentUser != null && currentUser.isAuthenticated());
    }

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
    
}
