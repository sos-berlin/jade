package com.sos.jadevaadincockpit.view.forms;

import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;

public class SFtpConnectionParameterForm extends BaseForm {
	private static final long serialVersionUID = -8494541535864140005L;
	
	private SOSConnection2OptionsAlternate jadeConnectionOptions;
	
	//Components
	private CheckBox useZlibCompressionComponent;
	private TextField zlibCompressionLevelComponent;

	public SFtpConnectionParameterForm(String caption, Item profile, SOSConnection2OptionsAlternate jadeConnectionOptions) {
		super(profile);
		setCaption(caption);
		this.jadeConnectionOptions = jadeConnectionOptions;
		
		createForm();
	}

	private void createForm() {
		
		AbstractComponent hostNameComposite = componentCreator.getComponentWithCaption(jadeConnectionOptions.HostName);
		layout.addComponent(hostNameComposite);
		
		AbstractComponent portComposite = componentCreator.getComponentWithCaption(jadeConnectionOptions.port);
		layout.addComponent(portComposite);
		
		AbstractComponent userComposite = componentCreator.getComponentWithCaption(jadeConnectionOptions.user);
		layout.addComponent(userComposite);
		
		AbstractComponent authMethodComposite = componentCreator.getComponentWithCaption(jadeConnectionOptions.auth_method);
		layout.addComponent(authMethodComposite);
		
		AbstractComponent passwordComposite = componentCreator.getComponentWithCaption(jadeConnectionOptions.password);
		layout.addComponent(passwordComposite);
		
		AbstractComponent authFileComposite = componentCreator.getComponentWithCaption(jadeConnectionOptions.auth_file);
		layout.addComponent(authFileComposite);
		
		AbstractComponent passphraseComposite = componentCreator.getComponentWithCaption(jadeConnectionOptions.passphrase);
		layout.addComponent(passphraseComposite);
		
		AbstractComponent strictHostKeyCheckingComposite = componentCreator.getComponentWithCaption(jadeConnectionOptions.StrictHostKeyChecking);
		layout.addComponent(strictHostKeyCheckingComposite);
		
		useZlibCompressionComponent = (CheckBox) componentCreator.getComponentWithCaption(jadeConnectionOptions.use_zlib_compression);
		layout.addComponent(useZlibCompressionComponent);
		
		zlibCompressionLevelComponent = (TextField) componentCreator.getComponentWithCaption(jadeConnectionOptions.zlib_compression_level);
		layout.addComponent(zlibCompressionLevelComponent);
		
		AbstractComponent loadClassNameComponent = componentCreator.getComponentWithCaption(jadeConnectionOptions.loadClassName);
		layout.addComponent(loadClassNameComponent);
		
		useZlibCompressionComponent.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 3235954496161889851L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				changeEnabledState(useZlibCompressionComponent.getValue() == null ? false : useZlibCompressionComponent.getValue());
			}
		});
		
		changeEnabledState(useZlibCompressionComponent.getValue() == null ? false : useZlibCompressionComponent.getValue());
	}
	
	private void changeEnabledState(boolean enabled) {
		zlibCompressionLevelComponent.setEnabled(enabled);
	}
}
