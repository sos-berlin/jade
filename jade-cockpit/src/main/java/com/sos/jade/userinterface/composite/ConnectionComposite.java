package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.VirtualFileSystem.Options.SOSConnection2Options;
import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.sos.dialog.Globals;
import com.sos.dialog.classes.SOSCTabFolder;
import com.sos.dialog.classes.SOSCTabItem;
import com.sos.dialog.layouts.Gridlayout;

public class ConnectionComposite extends CompositeBaseClass<JADEOptions> {
	@SuppressWarnings({ "unused" })
	private final Logger			logger					= Logger.getLogger(ConnectionComposite.class);
	public final String				conSVNVersion			= "$Id$";
	private SOSConnection2Options	objConnectionOptions	= null;
	private int						intWhatType				= 0;
	private JADEOptions				objOptions				= null;

	public ConnectionComposite(final Composite parent, final JADEOptions pobjOptions, final int pWhatType) {
		super(parent, pobjOptions);
		objConnectionOptions = pobjOptions.getConnectionOptions();
		intWhatType = pWhatType;
		objOptions = pobjOptions;
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}
	}

	@Override
	public void createComposite() {
		Gridlayout.set4ColumnLayout(this);
		SOSCTabFolder tabFolderConnection = new SOSCTabFolder(this, SWT.None /* .H_SCROLL | SWT.V_SCROLL */);
		tabFolderConnection.setBackground(Globals.getCompositeBackground());
		tabFolderConnection.ItemsHasClose = false;
		{
			SOSConnection2OptionsAlternate objO = null;
			// TODO der switch muß in die connection options, Proxy fehlt hier auch noch.
			switch (intWhatType) {
				case 1:
					objO = objConnectionOptions.Source();
					break;
				case 2:
					objO = objConnectionOptions.Source().Alternatives();
					break;
				case 3:
					objO = objConnectionOptions.Target();
					break;
				case 4:
					objO = objConnectionOptions.Target().Alternatives();
					break;
				default:
					break;
			}
			
			SOSCTabItem tbtmServer = tabFolderConnection.getTabItem("tab_Server");
			ConnectionDataComposite<SOSConnection2OptionsAlternate> objCDC = new ConnectionDataComposite<>(tabFolderConnection, objO);
			tbtmServer.setControl(objCDC);

			SOSCTabItem tbtmItem = tabFolderConnection.getTabItem("tab_CredentialStore");
			FileCredentialStoreComposite objFCSC = new FileCredentialStoreComposite(tabFolderConnection, objO);
			tbtmItem.setControl(objFCSC);

			if (intWhatType <= 1) {
				SOSCTabItem tbtmFilter = tabFolderConnection.getTabItem("tab_Filter");
				tbtmFilter.setControl(new Filter4ResultSetComposite(tabFolderConnection, objOptions));
			}
			if (intWhatType > 2) {
				SOSCTabItem tbtmProxy = tabFolderConnection.getTabItem("tab_Proxy");
				tbtmProxy.setControl(new ConnectionDataComposite<SOSConnection2OptionsAlternate>(tabFolderConnection, objO));
				SOSCTabItem tbtmJump = tabFolderConnection.getTabItem("tab_Jump");
				tbtmJump.setControl(new ConnectionDataComposite<SOSConnection2OptionsAlternate>(tabFolderConnection, objConnectionOptions.JumpServer()));
			}
		}
		// TODO global variable für zuletzt gezeigten Tab. auf den dann positionieren.
		tabFolderConnection.setSelection(0);
	}

}
