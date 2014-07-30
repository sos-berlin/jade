package com.sos.jadevaadincockpit.view.forms;

import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.vaadin.data.Item;
import com.vaadin.ui.AbstractComponent;

public class FtpConnectionParameterForm extends BaseForm {
	private static final long serialVersionUID = 6210356519197373909L;

	private SOSConnection2OptionsAlternate jadeConnectionOptions;
	
	public FtpConnectionParameterForm(String caption, Item section, SOSConnection2OptionsAlternate jadeConnectionOptions) {
		super(section);
		setCaption(caption);
		this.jadeConnectionOptions = jadeConnectionOptions;
		
		createForm();
	}

	private void createForm() {
		AbstractComponent hostNameComponent = componentCreator.getComponentWithCaption(jadeConnectionOptions.host);
		layout.addComponent(hostNameComponent);

		AbstractComponent portComponent = componentCreator.getComponentWithCaption(jadeConnectionOptions.port);
		layout.addComponent(portComponent);
		
		AbstractComponent userComponent = componentCreator.getComponentWithCaption(jadeConnectionOptions.user);
		layout.addComponent(userComponent);
		
		AbstractComponent passwordComponent = componentCreator.getComponentWithCaption(jadeConnectionOptions.password);
		layout.addComponent(passwordComponent);
		
		AbstractComponent accountComponent = componentCreator.getComponentWithCaption(jadeConnectionOptions.account);
		layout.addComponent(accountComponent);
		
		AbstractComponent transferModeComponent = componentCreator.getComponentWithCaption(jadeConnectionOptions.transfer_mode);
		layout.addComponent(transferModeComponent);
		
		AbstractComponent loadClassNameComponent = componentCreator.getComponentWithCaption(jadeConnectionOptions.loadClassName);
		layout.addComponent(loadClassNameComponent);
		
		AbstractComponent passiveModeComponent = componentCreator.getComponentWithCaption(jadeConnectionOptions.passive_mode);
		layout.addComponent(passiveModeComponent);
		
		AbstractComponent protocolCommandListenerComponent = componentCreator.getComponentWithCaption(jadeConnectionOptions.ProtocolCommandListener);
		layout.addComponent(protocolCommandListenerComponent);
	}
}
