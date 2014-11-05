package com.sos.jade.userinterface.composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.sos.dialog.classes.SOSCTabFolder;
import com.sos.dialog.components.CompositeBaseClass;

public class ServerComposite extends CompositeBaseClass<JADEOptions> {
	public final String	conSVNVersion	= "$Id: ServerComposite.java 27690 2014-10-22 10:06:09Z kb $";
	private int			intWhatType		= 0;
	private JADEOptions	objOptions		= null;

	public ServerComposite(final Composite parent, final JADEOptions pobjOptions, final int pWhatType) {
		super(parent, pobjOptions);
		intWhatType = pWhatType;
		objOptions = pobjOptions;
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}
	}

	@Override
	public void createComposite() {
		SOSCTabFolder tabFolderConnection = new SOSCTabFolder(this, SWT.None);
		tabFolderConnection.ItemsHasClose = false;
		{
			SOSConnection2OptionsAlternate objPrimaryO = null;
			switch (intWhatType) {
				case 1: // primary source
					objPrimaryO = objOptions.getConnectionOptions().Source();
					break;
				case 3: // primary target
					objPrimaryO = objOptions.getConnectionOptions().Target();
					break;
				default:
					break;
			}

			createTab(tabFolderConnection, new ConnectionComposite(tabFolderConnection, objPrimaryO), "tab_Primary_Server").createTabItemComposite();
			createTab(tabFolderConnection, new ConnectionComposite(tabFolderConnection, objPrimaryO.Alternatives()), "tab_Alternate_Server");

			//			SOSCTabItem tbtmPrimaryServer = tabFolderConnection.getTabItem("tab_Primary_Server");
			//			ConnectionComposite objCDC = new ConnectionComposite(tabFolderConnection, objPrimaryO);
			//			tbtmPrimaryServer.setControl(objCDC);
			//			SOSCTabItem tbtmAlternateServer = tabFolderConnection.getTabItem("tab_Alternate_Server");
			//			ConnectionComposite objCDCAlternate = new ConnectionComposite(tabFolderConnection, objAlternateO);
			//			tbtmAlternateServer.setControl(objCDCAlternate);
			//			objCDC.createTabItemComposite();
		}
		// TODO global variable für zuletzt gezeigten Tab. auf den dann positionieren.
		tabFolderConnection.setSelection(0);
	}

}
