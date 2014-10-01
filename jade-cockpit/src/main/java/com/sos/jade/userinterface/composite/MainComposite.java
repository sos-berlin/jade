package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.dialog.classes.SOSCTabFolder;
import com.sos.dialog.components.CompositeBaseClass;
import com.sos.jade.userinterface.data.JadeTreeViewEntry;

public class MainComposite extends CompositeBaseClass<JADEOptions> {
	@SuppressWarnings("unused")
	private final String		conClassName		= this.getClass().getSimpleName();
	@SuppressWarnings("unused")
	private static final String	conSVNVersion		= "$Id$";
	@SuppressWarnings("unused")
	private final Logger		logger1				= Logger.getLogger(this.getClass());
	private JadeTreeViewEntry	objTreeViewEntry	= null;

	private SOSCTabFolder pobjThisTabFolder  = null;
	
	public MainComposite(final SOSCTabFolder pobjTabFolder, final JadeTreeViewEntry pobjTreeViewEntry) {
		super(pobjTabFolder);
		objTreeViewEntry = pobjTreeViewEntry;
		objJadeOptions = objTreeViewEntry.getOptions();
		pobjThisTabFolder = pobjTabFolder;
		try {
			objJadeOptions.adjustDefaults();
		}
		catch (Exception e) {
			throw new JobSchedulerException(e);
		}
	}

//	public MainComposite(final CTabItem pobjTabItem, final JadeTreeViewEntry pobjTreeViewEntry) {
//		super((Composite) pobjTabItem.getControl());
//		objTreeViewEntry = pobjTreeViewEntry;
//		objJadeOptions = objTreeViewEntry.getOptions();
//	}
//
	@Override
	public void createComposite() {
		{
			SOSCTabFolder objMainTabFolder = new SOSCTabFolder(this, SWT.NONE);
			objMainTabFolder.ItemsHasClose = false;

			OperationComposite objOpC = new OperationComposite(objMainTabFolder, objJadeOptions);
			createTab(objMainTabFolder, objOpC , "tab_Operation");
			createTab(objMainTabFolder, new ConnectionComposite(objMainTabFolder, objJadeOptions, 1), "tab_Source");
			//			objConnectionComposite.createTabItemComposite();

			// Primary, Alternate
			//						CTabItem tbtmNewItem_2 = tabFolder_2.getTabItem("Alt Source");
			//						{
			//							Composite composite = new Composite(tabFolder_2, SWT.NONE);
			//							tbtmNewItem_2.setControl(composite);
			//							Gridlayout.set4ColumnLayout(composite);
			//							new ConnectionComposite(composite, objJadeOptions, 2);
			//		
			createTab(objMainTabFolder, new FileSelectionComposite(objMainTabFolder, objJadeOptions), "tab_Objects");

			if (objJadeOptions.NeedTargetClient() == true) {
				createTab(objMainTabFolder, new ConnectionComposite(objMainTabFolder, objJadeOptions, 3), "tab_Target");
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

			createTab(objMainTabFolder, new MiscComposite(objMainTabFolder, objJadeOptions), "tab_Misc");
			createTab(objMainTabFolder, new LoggingComposite(objMainTabFolder, objJadeOptions), "tab_Logging");
			createTab(objMainTabFolder, new NotificationComposite(objMainTabFolder, objJadeOptions), "tab_Notification");
			createTab(objMainTabFolder, new BackgroundServiceComposite(objMainTabFolder, objJadeOptions), "tab_BService");
			createTab(objMainTabFolder, new JITLComposite(objMainTabFolder, objJadeOptions), "tab_JITL");
			createTab(objMainTabFolder, new PollEngineComposite(objMainTabFolder, objJadeOptions), "tab_PollEngine");
			// TODO letzte Position global merken,. auch in die Preferenzen schreiben.
			objOpC.createTabItemComposite();
			objMainTabFolder.setSelection(0);
		}
	}
}