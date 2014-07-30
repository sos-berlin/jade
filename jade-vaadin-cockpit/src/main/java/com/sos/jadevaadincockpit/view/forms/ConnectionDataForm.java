package com.sos.jadevaadincockpit.view.forms;

import java.util.Iterator;

import com.sos.JSHelper.Options.JSOptionsClass;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;
import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.sos.jadevaadincockpit.view.components.JadeComboBox;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;


public class ConnectionDataForm  extends BaseForm {
	private static final long serialVersionUID = -832206288774586775L;
	
	private SOSConnection2OptionsAlternate jadeConnectionOptions;
	
	// Layout
	private VerticalLayout rootLayout = new VerticalLayout();
	private VerticalLayout tabSheetLayout = new VerticalLayout();
	
	private ComboBox protocolComponent;
	private TabSheet tabSheet;
	
	private FtpConnectionParameterForm ftpConnectionParameterForm;
	private SFtpConnectionParameterForm sFtpConnectionParameterForm;
	private FtpSConnectionParameterForm ftpSConnectionParameterForm;
	private FileSystemConnectionParameterForm fileSystemConnectionParameterForm;
	private WebDavConnectionParameterForm webDavConnectionParameterForm;
	private HttpConnectionParameterForm httpConnectionParameterForm;
	private HttpsConnectionParameterForm httpsConnectionParameterForm;
	private SmtpConnectionParameterForm smtpConnectionParameterForm;
	private ImapConnectionParameterForm imapConnectionParameterForm;
	private ZipConnectionParameterForm zipConnectionParameterForm;

	public ConnectionDataForm(String caption, Item section, JSOptionsClass jadeConnectionOptions) {
		super(section);
		setCaption(caption);
		this.jadeConnectionOptions = (SOSConnection2OptionsAlternate) jadeConnectionOptions;
		
		createForm();
		changeEnabledState();
	}

	private void createForm() {
		// Layout
		setCompositionRoot(rootLayout);
		rootLayout.addComponent(layout);
		rootLayout.addComponent(tabSheetLayout);
		
		tabSheet = new TabSheet();
		tabSheet.setHeight("45%");
		
		// Components
		protocolComponent = (ComboBox) componentCreator.getComponentWithCaption(jadeConnectionOptions.protocol);
		protocolComponent.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 957937419512823031L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				changeEnabledState();
			}
		});
		layout.addComponent(protocolComponent);
		
		ftpConnectionParameterForm = new FtpConnectionParameterForm("FTP", profile, jadeConnectionOptions);
		sFtpConnectionParameterForm = new SFtpConnectionParameterForm("SFTP", profile, jadeConnectionOptions);
		ftpSConnectionParameterForm = new FtpSConnectionParameterForm("FTPS", profile, jadeConnectionOptions);
		fileSystemConnectionParameterForm = new FileSystemConnectionParameterForm("Local", profile, jadeConnectionOptions);
		webDavConnectionParameterForm = new WebDavConnectionParameterForm("WebDav", profile, jadeConnectionOptions);
		httpConnectionParameterForm = new HttpConnectionParameterForm("Http", profile, jadeConnectionOptions);
		httpsConnectionParameterForm = new HttpsConnectionParameterForm("Https", profile, jadeConnectionOptions);
		
		tabSheet.addComponent(ftpConnectionParameterForm);
		tabSheet.addComponent(sFtpConnectionParameterForm);
		tabSheet.addComponent(ftpSConnectionParameterForm);
		tabSheet.addComponent(fileSystemConnectionParameterForm);
		tabSheet.addComponent(webDavConnectionParameterForm);
		tabSheet.addComponent(httpConnectionParameterForm);
		tabSheet.addComponent(httpsConnectionParameterForm);
		
		if (!jadeConnectionOptions.isSource) { // Target only
			smtpConnectionParameterForm = new SmtpConnectionParameterForm("SMTP", profile, jadeConnectionOptions);
			tabSheet.addComponent(smtpConnectionParameterForm);
		} else { // Source only
			imapConnectionParameterForm = new ImapConnectionParameterForm("IMAP", profile, jadeConnectionOptions);
			tabSheet.addComponent(imapConnectionParameterForm);
		}
		zipConnectionParameterForm = new ZipConnectionParameterForm("Zip", profile, jadeConnectionOptions);
		tabSheet.addComponent(zipConnectionParameterForm);
		AbstractComponent useCredentialStoreComponent = componentCreator.getComponentWithCaption(jadeConnectionOptions.getCredentialStore().use_credential_Store);
		layout.addComponent(useCredentialStoreComponent);
		AbstractComponent preCommandComponent = componentCreator.getComponentWithCaption(jadeConnectionOptions.Pre_Command);
		layout.addComponent(preCommandComponent);
		AbstractComponent postCommandComponent = componentCreator.getComponentWithCaption(jadeConnectionOptions.Post_Command);
		layout.addComponent(postCommandComponent);
		AbstractComponent folderNameComponent = componentCreator.getComponentWithCaption(jadeConnectionOptions.FolderName);
		layout.addComponent(folderNameComponent);
		AbstractComponent makeDirsComponent = componentCreator.getComponentWithCaption(jadeConnectionOptions.makeDirs);
		layout.addComponent(makeDirsComponent);
		AbstractComponent replaceWhatComponent = componentCreator.getComponentWithCaption(jadeConnectionOptions.ReplaceWhat);
		layout.addComponent(replaceWhatComponent);
		AbstractComponent replaceWithComponent = componentCreator.getComponentWithCaption(jadeConnectionOptions.ReplaceWith);
		layout.addComponent(replaceWithComponent);
		
		tabSheetLayout.addComponent(tabSheet);
		
	}
	
	private AbstractComponent getComponentByTransferType() {
		AbstractComponent retVal = null;
		Item item = protocolComponent.getItem(protocolComponent.getValue());

		if (item != null) {
			Property<?> valueProp = item
					.getItemProperty(JadeComboBox.PROPERTY.VALUE);

			if (valueProp != null) {
				String selection = (String) valueProp.getValue();

				if (selection.equals(enuTransferTypes.file.Text())) {
					retVal = fileSystemConnectionParameterForm;
				} else if (selection.equals(enuTransferTypes.ftp.Text())) {
					retVal = ftpConnectionParameterForm;
				} else if (selection.equals(enuTransferTypes.ftps.Text())) {
					retVal = ftpSConnectionParameterForm;
				} else if (selection.equals(enuTransferTypes.http.Text())) {
					retVal = httpConnectionParameterForm;
				} else if (selection.equals(enuTransferTypes.https.Text())) {
					retVal = httpsConnectionParameterForm;
				} else if (selection.equals(enuTransferTypes.imap.Text())) {
					retVal = imapConnectionParameterForm;
				} else if (selection.equals(enuTransferTypes.local.Text())) { // same as file
					retVal = fileSystemConnectionParameterForm;
					// } else if (selection.equals(enuTransferTypes.mq.Text()))
					// {
					// retVal = null;
				} else if (selection.equals(enuTransferTypes.sftp.Text())) {
					retVal = sFtpConnectionParameterForm;
					// } else if (selection.equals(enuTransferTypes.smb.Text()))
					// {
					// retVal = null;
				} else if (selection.equals(enuTransferTypes.smtp.Text())) {
					retVal = smtpConnectionParameterForm;
					// } else if
					// (selection.equals(enuTransferTypes.ssh2.Text())) {
					// retVal = null;
					// } else if (selection.equals(enuTransferTypes.svn.Text()))
					// {
					// retVal = null;
				} else if (selection.equals(enuTransferTypes.webdav.Text())) {
					retVal = webDavConnectionParameterForm;
				} else if (selection.equals(enuTransferTypes.zip.Text())) {
					retVal = zipConnectionParameterForm;
				}
			}
		}
		return retVal;
	}
		
	private void changeEnabledState() {
		AbstractComponent comp = getComponentByTransferType();
		
		Iterator<Component> i = tabSheet.iterator();

		while (i.hasNext()) {
			Component o = i.next();
			if (o.equals(comp)) {
				tabSheet.getTab(o).setEnabled(true);
				tabSheet.setSelectedTab(o);
			} else {
				tabSheet.getTab(o).setEnabled(false);
			}
		}
	}
}
