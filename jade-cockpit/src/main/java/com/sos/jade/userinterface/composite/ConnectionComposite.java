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
	@SuppressWarnings({ "unused", "hiding" }) private final Logger	logger					= Logger.getLogger(ConnectionComposite.class);
	public final String												conSVNVersion			= "$Id$";
	private SOSConnection2Options									objConnectionOptions	= null;
	private int														intWhatType				= 0;
	private JADEOptions											objOptions				= null;

	public ConnectionComposite(final SOSCTabItem parent, final JADEOptions objOptions, final int pWhatType) {
		this((Composite) parent.getControl(), objOptions, pWhatType);
	}

	public ConnectionComposite(final Composite parent, final JADEOptions pobjOptions, final int pWhatType) {
		super(parent, pobjOptions);
		objConnectionOptions = pobjOptions.getConnectionOptions();
		intWhatType = pWhatType;
		objOptions = pobjOptions;
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}

	}

	@Override public void createComposite() {
		Gridlayout.set4ColumnLayout(this);
		SOSCTabFolder tabFolderConnection = new SOSCTabFolder(this, SWT.NONE);
		tabFolderConnection.setBackground(Globals.getCompositeBackground());
		tabFolderConnection.ItemsHasClose = false;
//		logger.debug(conSVNVersion);
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
			tbtmServer.setComposite(new ConnectionDataComposite(tbtmServer, objO));

			SOSCTabItem tbtmCredentialStore = tabFolderConnection.getTabItem("tab_CredentialStore");
			tbtmCredentialStore.setComposite(new FileCredentialStoreComposite(tbtmCredentialStore, objO));

			if (intWhatType <= 1) {
				SOSCTabItem tbtmFilter = tabFolderConnection.getTabItem("tab_Filter");
				tbtmFilter.setComposite(new Filter4ResultSetComposite(tbtmFilter, objOptions));
			}
			if (intWhatType > 2) {
				SOSCTabItem tbtmProxy = tabFolderConnection.getTabItem("tab_Proxy");
				tbtmProxy.setComposite(new ConnectionDataComposite(tbtmProxy, objO));
				SOSCTabItem tbtmJump = tabFolderConnection.getTabItem("tab_Jump");
				tbtmJump.setComposite(new ConnectionDataComposite(tbtmJump, objConnectionOptions.JumpServer()));
			}
		}
		// TODO global variable für zuletzt gezeigten Tab. auf den dann positionieren.
		tabFolderConnection.setSelection(0);
	}
}
