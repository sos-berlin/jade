package com.sos.jade.userinterface.composite;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.dialog.Globals;
import com.sos.dialog.classes.SOSCTabFolder;
import com.sos.dialog.classes.SOSCTabItem;
import com.sos.dialog.classes.SOSComposite;
import com.sos.dialog.components.TextArea;
import com.sos.dialog.layouts.Gridlayout;
import com.sos.jade.userinterface.data.JadeTreeViewEntry;

public class SourceBrowserComposite extends Composite  {
	private JADEOptions	objJadeOptions	= null;
	private TextArea	objTextArea	= null;
	private final ArrayList<StyleRange> objSRs = new ArrayList<>();

	public SourceBrowserComposite(final SOSCTabFolder parent, final JadeTreeViewEntry objTreeViewEntry) {
		super(parent, SWT.None);
		objJadeOptions = objTreeViewEntry.getOptions();
		try {
			objJadeOptions.adjustDefaults();
		}
		catch (Exception e) {
		}
		SOSCTabFolder tabFolder = parent;
		GridLayout gridLayout = new GridLayout(4, true);
		setLayout(gridLayout);
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		@SuppressWarnings("unused") Display display = Display.getCurrent();
		{
			// TODO check open tab items. avoid duplicates
			SOSCTabItem tbtSourceBrowserView = new SOSCTabItem(tabFolder, SWT.CLOSE);
			tbtSourceBrowserView.setShowClose(true);
			String strT = "** Source: " + objTreeViewEntry.getName();
//			tbtSourceBrowserView.setData("key", strT);
			tbtSourceBrowserView.setText(strT);
			tbtSourceBrowserView.setData(objTreeViewEntry);
			tbtSourceBrowserView.setFont(Globals.stFontRegistry.get("text"));
			{
				SOSComposite composite = new SOSComposite(tabFolder, SWT.NONE);
				objTextArea = new TextArea(composite, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL);
				objTextArea.createContextMenue();
				tbtSourceBrowserView.setControl(composite);
				objTextArea.setBackground(Globals.getCompositeBackground());
				composite.setBackground(Globals.getCompositeBackground());
				Gridlayout.set4ColumnLayout(composite);
				composite.layout(true);
				composite.getParent().layout(true);
				tabFolder.setSelection(tbtSourceBrowserView);
				tabFolder.setRedraw(true);
				tabFolder.layout();
				try {
					Thread.sleep(1);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				String strT1 = "[" + objJadeOptions.profile.Value() + "]\n";
				 strT1 = objJadeOptions.DirtyString();
				objTextArea.setText(strT1);
				
			}
		}
	}

	@Override protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

@Override public void dispose() {
//		doClose();
	}
	
}
