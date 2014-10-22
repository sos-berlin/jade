package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.sos.JSHelper.Options.SOSOptionTransferType;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;
import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.sos.dialog.classes.SOSCTabFolder;
import com.sos.dialog.classes.SOSCTabItem;
import com.sos.dialog.classes.SOSCheckBox;
import com.sos.dialog.components.CompositeBaseClass;
import com.sos.dialog.interfaces.ISOSTabItem;

public class ConnectionDataComposite<T> extends CompositeBaseClass<T> {
	@SuppressWarnings({ "unused", "hiding" })
	private final Logger					logger						= Logger.getLogger(ConnectionDataComposite.class);
	public final String						conSVNVersion				= "$Id$";
	private SOSConnection2OptionsAlternate	objJadeConnectionOptions	= null;
	private SOSCTabFolder					tabFolderProtocols			= null;
	private final SOSCTabItem				tbtmFtp						= null;
	private final SOSCTabItem				tbtmSFtp					= null;
	private final SOSCTabItem				tbtmFtpS					= null;
	private final SOSCTabItem				tbtmLocal					= null;
	private final SOSCTabItem				tbtmWebDav					= null;
	private final SOSCTabItem				tbtmHttp					= null;
	private final SOSCTabItem				tbtmHttps					= null;
	private final SOSCTabItem				tbtmSmtp					= null;
	private final SOSCTabItem				tbtmIMap					= null;
	private final SOSCTabItem				tbtmZip						= null;

	//	public ConnectionDataComposite(final SOSCTabItem parent, final T objOptions) {
	//		super(parent.getParent(), objOptions);
	//	}
	//
	public ConnectionDataComposite(final Composite parent, final T objOptions) {
		super(parent, null);
		objJadeConnectionOptions = (SOSConnection2OptionsAlternate) objOptions;
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}
	}

	SOSCheckBox	btnUseCS	= null;
	CCombo		cboProtocol	= null;

	@Override
	public void createComposite() {
		int i = this.getChildren().length;
		// TODO protocol kann für jede Verbindung (alternate, proxy, jump) unterschiedlich sein
		cboProtocol = (CCombo) objCC.getControl(objJadeConnectionOptions.protocol);

		cboProtocol.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent arg0) {
				selectTabItem(cboProtocol.getText());
			}
		});

		objCC.getSeparator();
		//
		tabFolderProtocols = new SOSCTabFolder(this, SWT.NONE);
		tabFolderProtocols.ItemsHasClose = false;
		//		tabFolderProtocols.flgRejectTabItemSelection = true;
		tabFolderProtocols.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));

		createTab(tabFolderProtocols, new FtpConectionParameterComposite(tabFolderProtocols, objJadeConnectionOptions), "tab_" + enuTransferTypes.ftp);
		createTab(tabFolderProtocols, new SFtpConectionParameterComposite(tabFolderProtocols, objJadeConnectionOptions), "tab_" + enuTransferTypes.sftp);
		createTab(tabFolderProtocols, new FtpSConectionParameterComposite(tabFolderProtocols, objJadeConnectionOptions), "tab_" + enuTransferTypes.ftps);
		createTab(tabFolderProtocols, new FileSystemConectionParameterComposite(tabFolderProtocols, objJadeConnectionOptions), "tab_" + enuTransferTypes.local);
		createTab(tabFolderProtocols, new WebdavConectionParameterComposite(tabFolderProtocols, objJadeConnectionOptions), "tab_" + enuTransferTypes.webdav);
		createTab(tabFolderProtocols, new HttpConectionParameterComposite(tabFolderProtocols, objJadeConnectionOptions), "tab_" + enuTransferTypes.http);
		createTab(tabFolderProtocols, new HttpsConectionParameterComposite(tabFolderProtocols, objJadeConnectionOptions), "tab_" + enuTransferTypes.https);
		// target only
		if (objJadeConnectionOptions.isSource == false) {
			createTab(tabFolderProtocols, new SmtpConectionParameterComposite(tabFolderProtocols, objJadeConnectionOptions), "tab_" + enuTransferTypes.smtp);
		}
		else {
			// source only
			createTab(tabFolderProtocols, new IMapConectionParameterComposite(tabFolderProtocols, objJadeConnectionOptions), "tab_" + enuTransferTypes.imap);
		}
		createTab(tabFolderProtocols, new ZipConectionParameterComposite(tabFolderProtocols, objJadeConnectionOptions), "tab_" + enuTransferTypes.zip);
		//
		btnUseCS = (SOSCheckBox) objCC.getControl(objJadeConnectionOptions.getCredentialStore().use_credential_Store);
		objCC.getLabel(2);

		objCC.getControl(objJadeConnectionOptions.Pre_Command, 3);
		objCC.getControl(objJadeConnectionOptions.Post_Command, 3);
		objCC.getControl(objJadeConnectionOptions.FolderName, 3);
		objCC.getControl(objJadeConnectionOptions.makeDirs, 3);
		objCC.getControl(objJadeConnectionOptions.ReplaceWhat, 3);
		objCC.getControl(objJadeConnectionOptions.ReplaceWith, 3);

		initValues();
		doResize();
		//
		selectTabItem(objJadeConnectionOptions.protocol.Value());
	}

	private void selectTabItem(final String pstrWhatProcotol) {
		String strP = "tab_" + pstrWhatProcotol;
		for (CTabItem objTabItem : tabFolderProtocols.getItems()) {
			String strT = (String) objTabItem.getData("key");
			if (strT != null && strT.equalsIgnoreCase(strP)) {
				tabFolderProtocols.setSelection(objTabItem);
				tabFolderProtocols.flgRejectTabItemSelection = false;
				ISOSTabItem objCurrTab = (ISOSTabItem) objTabItem.getData("composite");
				if (objCurrTab != null) {
					objCurrTab.createTabItemComposite();
				}
				tabFolderProtocols.getParent().layout(true, true);
				break;
			}
		}

	}

	// TODO must be a method of the Options-Class
	@SuppressWarnings("unused")
	private void setMandatory(final SOSOptionTransferType pobjTF) {
		switch (pobjTF.getEnum()) {
			case ftp:
				objJadeConnectionOptions.HostName.isMandatory(true);
				objJadeConnectionOptions.port.isMandatory(true);
				objJadeConnectionOptions.user.isMandatory(true);
				break;
			case sftp:
				objJadeConnectionOptions.HostName.isMandatory(true);
				objJadeConnectionOptions.port.isMandatory(true);
				objJadeConnectionOptions.user.isMandatory(true);
				objJadeConnectionOptions.auth_method.isMandatory(true);
				break;
			case ftps:
				objJadeConnectionOptions.HostName.isMandatory(true);
				objJadeConnectionOptions.port.isMandatory(true);
				objJadeConnectionOptions.user.isMandatory(true);
				break;
			case local:
				break;
			case http:
				objJadeConnectionOptions.HostName.isMandatory(true);
				objJadeConnectionOptions.port.isMandatory(true);
				objJadeConnectionOptions.user.isMandatory(true);
				break;
			case https:
				objJadeConnectionOptions.HostName.isMandatory(true);
				objJadeConnectionOptions.port.isMandatory(true);
				objJadeConnectionOptions.user.isMandatory(true);
				break;
			case smtp:
				objJadeConnectionOptions.HostName.isMandatory(true);
				objJadeConnectionOptions.port.isMandatory(true);
				objJadeConnectionOptions.user.isMandatory(true);
				break;
			case imap:
				objJadeConnectionOptions.HostName.isMandatory(true);
				objJadeConnectionOptions.port.isMandatory(true);
				objJadeConnectionOptions.user.isMandatory(true);
				break;
			case webdav:
				objJadeConnectionOptions.HostName.isMandatory(true);
				objJadeConnectionOptions.port.isMandatory(true);
				objJadeConnectionOptions.user.isMandatory(true);
				break;
			default:
				break;
		}
	}

	//	private SOSCTabItem getTabItem(final enuTransferTypes penuT) {
	//		SOSCTabItem objTabItem = tabFolderProtocols.getTabItem("tab_" + penuT.Text());
	//		return objTabItem;
	//	}
	//	
	protected void initValues() {
		btnUseCS.setEnabledDisabled();
	}
}
