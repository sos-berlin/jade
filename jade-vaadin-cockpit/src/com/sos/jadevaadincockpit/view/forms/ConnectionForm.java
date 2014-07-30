package com.sos.jadevaadincockpit.view.forms;

import com.sos.VirtualFileSystem.Options.SOSConnection2Options;
import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.vaadin.data.Item;
import com.vaadin.ui.TabSheet;

public class ConnectionForm extends BaseForm {
	private static final long serialVersionUID = 8499290861554445200L;
	
	private ConnectionType connectionType;
	private SOSConnection2Options connection2Options;
	
	private TabSheet tabSheet = new TabSheet();
	
	public static enum ConnectionType { SOURCE, SOURCE_ALTERNATIVES, TARGET, TARGET_ALTERNATIVES };
	
	public ConnectionForm(String caption, Item profile, ConnectionType connectionType) {
		super(profile);
		setCaption(caption);
		this.connectionType = connectionType;
		this.connection2Options = jadeOptions.getConnectionOptions();
		
		setCompositionRoot(tabSheet);
		createForm();
	}
	
	private void createForm() {
		
		SOSConnection2OptionsAlternate connection2OptionsAlternate = null;
		switch (connectionType) {
			case SOURCE:
				connection2OptionsAlternate = connection2Options.Source();
				break;
			case SOURCE_ALTERNATIVES:
				connection2OptionsAlternate = connection2Options.Source().Alternatives();
				break;
			case TARGET:
				connection2OptionsAlternate = connection2Options.Target();
				break;
			case TARGET_ALTERNATIVES:
				connection2OptionsAlternate = connection2Options.Target().Alternatives();
				break;
			default:
				
				break;
		}
		
		tabSheet.addComponent(new ConnectionDataForm("Server", profile, connection2OptionsAlternate));
		tabSheet.addComponent(new FileCredentialStoreForm("CredentialStore", profile, connection2OptionsAlternate));
		
		if (connectionType.equals(ConnectionType.SOURCE)) {
			tabSheet.addComponent(new FilterForResultSetForm("Filter", profile));
		} else if (connectionType.equals(ConnectionType.TARGET) || connectionType.equals(ConnectionType.SOURCE_ALTERNATIVES)) {
			tabSheet.addComponent(new ConnectionDataForm("Proxy", profile, connection2OptionsAlternate.getProxyOptions()));
			tabSheet.addComponent(new ConnectionDataForm("Jump", profile, connection2Options.JumpServer()));
		}
		tabSheet.setSelectedTab(tabSheet.getTab(0));
		
	}
	
}
