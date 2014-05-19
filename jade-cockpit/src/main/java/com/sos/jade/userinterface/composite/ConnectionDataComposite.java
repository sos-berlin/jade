package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.sos.JSHelper.Options.JSOptionsClass;
import com.sos.JSHelper.Options.SOSOptionTransferType;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;
import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.sos.dialog.classes.SOSCTabFolder;
import com.sos.dialog.classes.SOSCTabItem;
import com.sos.dialog.classes.SOSCheckBox;

public class ConnectionDataComposite extends CompositeBaseClass<SOSConnection2OptionsAlternate> {
	@SuppressWarnings({ "unused", "hiding" }) private final Logger	logger						= Logger.getLogger(ConnectionDataComposite.class);
	public final String									conSVNVersion				= "$Id$";
	private SOSConnection2OptionsAlternate				objJadeConnectionOptions	= null;
	private SOSCTabFolder								tabFolderProtocols			= null;
	private SOSCTabItem									tbtmFtp						= null;
	private SOSCTabItem									tbtmSFtp					= null;
	private SOSCTabItem									tbtmFtpS					= null;
	private SOSCTabItem									tbtmLocal					= null;
	private SOSCTabItem									tbtmWebDav					= null;
	private SOSCTabItem									tbtmHttp					= null;
	private SOSCTabItem									tbtmHttps					= null;
	private SOSCTabItem									tbtmSmtp					= null;
	private SOSCTabItem									tbtmIMap					= null;
	private SOSCTabItem									tbtmZip						= null;

	public ConnectionDataComposite(final CTabItem parent, final JSOptionsClass objOptions) {
		this((Composite) parent.getControl(), objOptions);
	}

	public ConnectionDataComposite(final Composite parent, final JSOptionsClass objOptions) {
		super(parent, null);
		objJadeConnectionOptions = (SOSConnection2OptionsAlternate) objOptions;
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}

	}

	SOSCheckBox btnUseCS = null;
	CCombo cboProtocol = null;
	
	@Override public void createComposite() {
		int i = this.getChildren().length;
//		this.setLayout(Gridlayout.get4ColumnLayout());
//		Gridlayout.set4ColumnLayout(this);
		// TODO protocol kann für jede Verbindung (alternate, proxy, jump) unterschiedlich sein
		cboProtocol = (CCombo) objCC.getControl(objJadeConnectionOptions.protocol);
		
		cboProtocol.addModifyListener(new ModifyListener() {
			@Override public void modifyText(final ModifyEvent arg0) {
				selectTabItem(cboProtocol.getText());
			}
		});

		objCC.getSeparator();
		//
		tabFolderProtocols = new SOSCTabFolder(objParent, SWT.NONE);
		tabFolderProtocols.ItemsHasClose = false;
//		tabFolderProtocols.flgRejectTabItemSelection = true;
		tabFolderProtocols.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		tbtmFtp = getTabItem(enuTransferTypes.ftp);
		tbtmFtp.setComposite(new FtpConectionParameterComposite(tbtmFtp, objJadeConnectionOptions));
		//
		tbtmSFtp = getTabItem(enuTransferTypes.sftp);
		tbtmSFtp.setComposite(new SFtpConectionParameterComposite(tbtmSFtp, objJadeConnectionOptions));
		tbtmFtpS = getTabItem(enuTransferTypes.ftps);
		tbtmFtpS.setComposite(new FtpSConectionParameterComposite(tbtmFtpS, objJadeConnectionOptions));
		tbtmLocal = getTabItem(enuTransferTypes.local);
		tbtmLocal.setComposite(new FileSystemConectionParameterComposite(tbtmLocal, objJadeConnectionOptions));
		tbtmWebDav = getTabItem(enuTransferTypes.webdav);
		tbtmWebDav.setComposite(new WebdavConectionParameterComposite(tbtmWebDav, objJadeConnectionOptions));
		tbtmHttp = getTabItem(enuTransferTypes.http);
		tbtmHttp.setComposite(new HttpConectionParameterComposite(tbtmHttp, objJadeConnectionOptions));
		tbtmHttps = getTabItem(enuTransferTypes.https);
		tbtmHttps.setComposite(new HttpsConectionParameterComposite(tbtmHttps, objJadeConnectionOptions));
		// target only
		if (objJadeConnectionOptions.isSource == false) {
			tbtmSmtp = getTabItem(enuTransferTypes.smtp);
			tbtmSmtp.setComposite(new SmtpConectionParameterComposite(tbtmSmtp, objJadeConnectionOptions));
		}
		else {
			// source only
			tbtmIMap = getTabItem(enuTransferTypes.imap);
			tbtmIMap.setComposite(new IMapConectionParameterComposite(tbtmIMap, objJadeConnectionOptions));
		}
		tbtmZip = getTabItem(enuTransferTypes.zip);
		tbtmZip.setComposite(new ZipConectionParameterComposite(tbtmZip, objJadeConnectionOptions));
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
		//
		selectTabItem(objJadeConnectionOptions.protocol.Value());
	}

	private void selectTabItem (final String pstrWhatProcotol) {
		String strP = "tab_" + pstrWhatProcotol;
		for (CTabItem objTabItem : tabFolderProtocols.getItems()) {
			String strT = (String) objTabItem.getData("key");
			if (strT != null && strT.equalsIgnoreCase(strP)) {
//				tabFolderProtocols.flgRejectTabItemSelection = false;
				tabFolderProtocols.setSelection(objTabItem);
//				tabFolderProtocols.flgRejectTabItemSelection = true;
//				ISOSTabItem objCurrTab = (ISOSTabItem) objTabItem.getData("composite");
//				if (objCurrTab != null) {
//					objCurrTab.createTabItemComposite();
//					objParent.layout();
//					tabFolderProtocols.layout();
//				}
				break;
			}
		}

	}
	// TODO must be a method of the Options-Class
	@SuppressWarnings("unused") private void setMandatory(final SOSOptionTransferType pobjTF) {
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

	private SOSCTabItem getTabItem(final enuTransferTypes penuT) {
		SOSCTabItem objTabItem = tabFolderProtocols.getTabItem("tab_" + penuT.Text());
		return objTabItem;
	}
	
	protected void initValues() {
		btnUseCS.setEnabledDisabled();
	}	
}
