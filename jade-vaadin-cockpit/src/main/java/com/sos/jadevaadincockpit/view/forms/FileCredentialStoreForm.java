package com.sos.jadevaadincockpit.view.forms;

import com.sos.CredentialStore.Options.SOSCredentialStoreOptions;
import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.CheckBox;

public class FileCredentialStoreForm extends BaseForm {
	private static final long serialVersionUID = 2797168140865579675L;

	private SOSCredentialStoreOptions credentialStoreOptions;
	
	// Components
	private CheckBox exportAttachmentParamsComponent;
	private AbstractComponent exportAttachmentToFileNameComponent;
	private AbstractComponent deleteExportedFileOnExitComponent;
	
	public FileCredentialStoreForm(String caption, Item section, SOSConnection2OptionsAlternate jadeConnectionOptions) {
		super(section);
		setCaption(caption);
		this.credentialStoreOptions = jadeConnectionOptions.getCredentialStore();
		
		createForm();
	}

	private void createForm() {

		//Components
		AbstractComponent fileNameComponent = componentCreator.getComponentWithCaption(credentialStoreOptions.CredentialStore_FileName);
		layout.addComponent(fileNameComponent);
		
		AbstractComponent storeTypeComponent = componentCreator.getComponentWithCaption(credentialStoreOptions.CredentialStore_StoreType);
		layout.addComponent(storeTypeComponent);
		
		AbstractComponent authenticationMethodComponent = componentCreator.getComponentWithCaption(credentialStoreOptions.CredentialStore_AuthenticationMethod);
		layout.addComponent(authenticationMethodComponent);
		
		AbstractComponent keyFileNameComponent = componentCreator.getComponentWithCaption(credentialStoreOptions.CredentialStore_KeyFileName);
		layout.addComponent(keyFileNameComponent);
		
		AbstractComponent passwordComponent = componentCreator.getComponentWithCaption(credentialStoreOptions.CredentialStore_password);
		layout.addComponent(passwordComponent);
		
		AbstractComponent keyPathComponent = componentCreator.getComponentWithCaption(credentialStoreOptions.CredentialStore_KeyPath);
		layout.addComponent(keyPathComponent);
		
		AbstractComponent processNotesParamsComponent = componentCreator.getComponentWithCaption(credentialStoreOptions.CredentialStore_ProcessNotesParams);
		layout.addComponent(processNotesParamsComponent);
		
		exportAttachmentParamsComponent = (CheckBox) componentCreator.getComponentWithCaption(credentialStoreOptions.CredentialStore_ExportAttachment);
		layout.addComponent(exportAttachmentParamsComponent);
		
		exportAttachmentToFileNameComponent = componentCreator.getComponentWithCaption(credentialStoreOptions.CredentialStore_ExportAttachment2FileName);
		layout.addComponent(exportAttachmentToFileNameComponent);
		
		deleteExportedFileOnExitComponent = componentCreator.getComponentWithCaption(credentialStoreOptions.CredentialStore_DeleteExportedFileOnExit);
		layout.addComponent(deleteExportedFileOnExitComponent);
		
		exportAttachmentParamsComponent.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = -946639177495347939L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				changeEnabledState(exportAttachmentParamsComponent.getValue() == null ? false : exportAttachmentParamsComponent.getValue());
			}
		});
		
		changeEnabledState(exportAttachmentParamsComponent.getValue() == null ? false : exportAttachmentParamsComponent.getValue());
	}
	
	private void changeEnabledState(Boolean enabled) {
		exportAttachmentToFileNameComponent.setEnabled(enabled);
		deleteExportedFileOnExitComponent.setEnabled(enabled);
	}

}
