package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

import com.sos.CredentialStore.Options.SOSCredentialStoreOptions;
import com.sos.dialog.classes.SOSCheckBox;
import com.sos.dialog.components.CompositeBaseClass;

public class FileCredentialStoreComposite extends CompositeBaseClass<SOSCredentialStoreOptions> {
	@SuppressWarnings("unused")
	private final String				conClassName				= this.getClass().getSimpleName();
	@SuppressWarnings({ "unused", "hiding" })
	private final Logger				logger						= Logger.getLogger(this.getClass());
	public final String					conSVNVersion				= "$Id: BackgroundServiceComposite.java 21704 2013-12-29 22:35:13Z kb $";
	private SOSCredentialStoreOptions	objJadeConnectionOptions	= null;

	//	public FileCredentialStoreComposite(final SOSCTabItem parent, final JSOptionsClass objOptions) {
	//		this((Composite) parent.getControl(), objOptions);
	//	}
	//
	public FileCredentialStoreComposite(final Composite parent, final SOSCredentialStoreOptions objOptions) {
		super(parent, null);
		objJadeConnectionOptions = objOptions;
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}
	}

	SOSCheckBox	btnExportAttachment	= null;
	SOSCheckBox	btnUseCS			= null;

	@Override
	public void createComposite() {
		{
			SOSCredentialStoreOptions objCSO = objJadeConnectionOptions;
			btnUseCS = (SOSCheckBox) objCC.getControl(objCSO.use_credential_Store);
			objCC.getLabel(2);
			btnUseCS.addChild(objCC.getControl(objCSO.CredentialStore_FileName, 3));
			btnUseCS.addChild(objCC.getControl(objCSO.CredentialStore_StoreType, 3));
			btnUseCS.addChild(objCC.getControl(objCSO.CredentialStore_AuthenticationMethod, 3));
			btnUseCS.addChild(objCC.getControl(objCSO.CredentialStore_KeyFileName, 3));
			btnUseCS.addChild(objCC.getControl(objCSO.CredentialStore_password, 3));
			objCC.getSeparator();
			btnUseCS.addChild(objCC.getControl(objCSO.CredentialStore_KeyPath, 3));
			btnUseCS.addChild(objCC.getControl(objCSO.CredentialStore_ProcessNotesParams));
			objCC.getLabel(2);
			btnExportAttachment = objCC.getCheckBox(objCSO.CredentialStore_ExportAttachment);
			btnUseCS.addChild(btnExportAttachment);
			objCC.getLabel(2);
			btnExportAttachment.addChild(objCC.getControl(objCSO.CredentialStore_ExportAttachment2FileName));
			objCC.getLabel(2);
			btnExportAttachment.addChild(objCC.getControl(objCSO.CredentialStore_DeleteExportedFileOnExit));
		}
		enableFields();
	}

	@Override
	protected void enableFields() {
		btnUseCS.setEnabledDisabled();
		btnExportAttachment.setEnabledDisabled();
	}

}
