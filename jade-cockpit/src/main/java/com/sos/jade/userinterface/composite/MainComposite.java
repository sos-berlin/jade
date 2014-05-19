package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.dialog.classes.SOSCTabFolder;
import com.sos.dialog.classes.SOSCTabItem;
import com.sos.dialog.interfaces.ISOSTabItem;
import com.sos.jade.userinterface.data.JadeTreeViewEntry;

public class MainComposite extends CompositeBaseClass<JADEOptions> {
	@SuppressWarnings("unused") private final String		conClassName		= this.getClass().getSimpleName();
	@SuppressWarnings("unused") private static final String	conSVNVersion		= "$Id$";
	@SuppressWarnings("unused") private final Logger		logger1				= Logger.getLogger(this.getClass());
	private JadeTreeViewEntry								objTreeViewEntry	= null;

	public MainComposite(final CTabItem parent, final JadeTreeViewEntry pobjTreeViewEntry) {
		super((Composite) parent.getControl());
		objTreeViewEntry = pobjTreeViewEntry;
		objJadeOptions = objTreeViewEntry.getOptions();
		try {
			objJadeOptions.adjustDefaults();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new JobSchedulerException(e);
		}
	}

	@Override public void createComposite() {
		{
			SOSCTabFolder objMainTabFolder = new SOSCTabFolder(objParent, SWT.NONE);
			objMainTabFolder.ItemsHasClose = false;
			
//            ToolBar toolbarA = new ToolBar(objMainTabFolder,  SWT.FLAT | SWT.HORIZONTAL);
//            objMainTabFolder.setTopRight(toolbarA);

			//
			SOSCTabItem tbtmItemOperation = objMainTabFolder.getTabItem("tab_Operation");
			ISOSTabItem objCompositeOperation = new OperationComposite(tbtmItemOperation, objJadeOptions);
			tbtmItemOperation.setComposite(objCompositeOperation);
			SOSCTabItem tbtmSourceItem = objMainTabFolder.getTabItem("tab_Source");
			ISOSTabItem objConnectionComposite = new ConnectionComposite(tbtmSourceItem, objJadeOptions, 1);
			tbtmSourceItem.setComposite(objConnectionComposite);
//			objConnectionComposite.createTabItemComposite();
			// Primary, Alternate
			//						CTabItem tbtmNewItem_2 = tabFolder_2.getTabItem("Alt Source");
			//						{
			//							Composite composite = new Composite(tabFolder_2, SWT.NONE);
			//							tbtmNewItem_2.setControl(composite);
			//							Gridlayout.set4ColumnLayout(composite);
			//							new ConnectionComposite(composite, objJadeOptions, 2);
			//						}
			SOSCTabItem tbtmItemObjects = objMainTabFolder.getTabItem("tab_Objects");
			ISOSTabItem objFileSelectionComposite = new FileSelectionComposite(tbtmItemObjects, objJadeOptions);
			tbtmItemObjects.setComposite(objFileSelectionComposite);
//			objFileSelectionComposite.createTabItemComposite();
			if (objJadeOptions.NeedTargetClient() == true) {
				SOSCTabItem tbtmTarget = objMainTabFolder.getTabItem("tab_Target");
				tbtmTarget.setComposite(new ConnectionComposite(tbtmTarget, objJadeOptions, 3));
				//						{
				//							CTabItem tbtmNewItem_3 = tabFolder_2.getTabItem("Alt Target");
				//							{
				//								Composite composite = new Composite(tabFolder_2, SWT.NONE);
				//								tbtmNewItem_3.setControl(composite);
				//								Gridlayout.set4ColumnLayout(composite);
				//								new ConnectionComposite(composite, objJadeOptions, 4);
				//							}
				//						}
			}
			SOSCTabItem tbtmMisc = objMainTabFolder.getTabItem("tab_Misc");
			tbtmMisc.setComposite(new MiscComposite(tbtmMisc, objJadeOptions));
			//
			SOSCTabItem tbtmLogging = objMainTabFolder.getTabItem("tab_Logging");
			tbtmLogging.setComposite(new LoggingComposite(tbtmLogging, objJadeOptions));
			//
			SOSCTabItem tbtmNitifications = objMainTabFolder.getTabItem("tab_Notification"); 
			tbtmNitifications.setComposite(new NotificationComposite(tbtmNitifications, objJadeOptions));
			//
			SOSCTabItem tbtmtab_BService = objMainTabFolder.getTabItem("tab_BService");
			tbtmtab_BService.setComposite(new BackgroundServiceComposite(tbtmtab_BService, objJadeOptions));
			//
			SOSCTabItem tbtmJITL = objMainTabFolder.getTabItem("tab_JITL");
			tbtmJITL.setComposite(new JITLComposite(tbtmJITL, objJadeOptions));
			//
			SOSCTabItem tbtmPollEngine = objMainTabFolder.getTabItem("tab_PollEngine");
			tbtmPollEngine.setComposite(new PollEngineComposite(tbtmPollEngine, objJadeOptions));
			// TODO letzte Position global merken,. auch in die Preferenzen schreiben.
//			objCompositeOperation.createTabItemComposite();
			objMainTabFolder.setSelection(0);
		}
	}
}