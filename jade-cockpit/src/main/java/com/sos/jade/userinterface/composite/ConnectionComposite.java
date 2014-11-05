package com.sos.jade.userinterface.composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.sos.dialog.classes.SOSCTabFolder;
import com.sos.dialog.components.CompositeBaseClass;
import com.sos.dialog.interfaces.ISOSTabItem;

public class ConnectionComposite extends CompositeBaseClass<SOSConnection2OptionsAlternate> {
	//	@SuppressWarnings({ "unused" })
	//	private final Logger			logger					= Logger.getLogger(ConnectionComposite.class);
	public final String						conSVNVersion			= "$Id$";
	private SOSConnection2OptionsAlternate	objConnectionOptions	= null;

	public ConnectionComposite(final Composite parent, final SOSConnection2OptionsAlternate pobjOptions) {
		super(parent, pobjOptions);
		objConnectionOptions = pobjOptions;
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}
	}

	@Override
	public void createComposite() {
		SOSCTabFolder tabFC = new SOSCTabFolder(this, SWT.None);
		tabFC.ItemsHasClose = false;
		{
			SOSConnection2OptionsAlternate objO = objConnectionOptions;
			ISOSTabItem objCDC = createTab(tabFC, new ConnectionDataComposite<SOSConnection2OptionsAlternate>(tabFC, objO), "tab_Server");
			//			SOSCTabItem tbtmServer = tabFC.getTabItem("tab_Server");
			//			ConnectionDataComposite<SOSConnection2OptionsAlternate> objCDC = new ConnectionDataComposite<>(tabFC, objO);
			//			tbtmServer.setControl(objCDC);

			createTab(tabFC, new FileCredentialStoreComposite(tabFC, objO.getCredentialStore()), "tab_CredentialStore");

			//			SOSCTabItem tbtmItem = tabFC.getTabItem("tab_CredentialStore");
			//			FileCredentialStoreComposite objFCSC = new FileCredentialStoreComposite(tabFC, objO.getCredentialStore());
			//			tbtmItem.setControl(objFCSC);

			//			if (intWhatType > 2) { // TODO it depends on the type of the data provider wether a proxy or jump is possible

			createTab(tabFC, new ConnectionDataComposite<SOSConnection2OptionsAlternate>(tabFC, objO.getProxyOptions()), "tab_Proxy");
			createTab(tabFC, new ConnectionDataComposite<SOSConnection2OptionsAlternate>(tabFC, objO.getJumpServerOptions()), "tab_Jump");

			objCDC.createTabItemComposite();
		}
		// TODO global variable für zuletzt gezeigten Tab. auf den dann positionieren.
		tabFC.setSelection(0);
	}

}
