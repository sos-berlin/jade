package com.sos.jade.userinterface.composite;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.dialog.Globals;
import com.sos.dialog.classes.SOSCTabFolder;
import com.sos.dialog.classes.SOSCTabItem;
import com.sos.dialog.components.TextArea;
import com.sos.dialog.layouts.Gridlayout;
import com.sos.jade.userinterface.adapters.ISOSSWTAppenderUI;
import com.sos.jade.userinterface.data.JadeTreeViewEntry;

public class LogFileComposite extends Composite implements ISOSSWTAppenderUI {
	private JADEOptions	objJadeOptions	= null;
	private TextArea	objTextArea	= null;
	private final ArrayList<StyleRange> objSRs = new ArrayList<>();

	public LogFileComposite(final SOSCTabFolder parent, final JadeTreeViewEntry objTreeViewEntry) {
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
			SOSCTabItem tbtLogView = new SOSCTabItem(tabFolder, SWT.CLOSE);
			tbtLogView.setShowClose(true);
			String strT = "** TransferLog: " + objTreeViewEntry.getName();
			tbtLogView.setData("key", strT);
			tbtLogView.setText(strT);
			tbtLogView.setData(objTreeViewEntry);
			tbtLogView.setFont(Globals.stFontRegistry.get("text"));
			{
//				SOSComposite composite = new SOSComposite(tabFolder, SWT.NONE);
				Composite composite = new Composite(tabFolder, SWT.NONE);
				objTextArea = new TextArea(composite, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL);
				objTextArea.createContextMenue();
				tbtLogView.setControl(composite);
				objTextArea.setBackground(Globals.getCompositeBackground());
				composite.setBackground(Globals.getCompositeBackground());
				Gridlayout.set4ColumnLayout(composite);
				composite.layout(true);
				composite.getParent().layout(true);
				tabFolder.setSelection(tbtLogView);
				tabFolder.setRedraw(true);
				tabFolder.layout();
				try {
					Thread.sleep(1);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@SuppressWarnings("unused") private CTabItem getTabItem(final CTabFolder pobjTabFolder, final String pstrCaption) {
		CTabItem tbtmItemOperation = new CTabItem(pobjTabFolder, SWT.NONE);
		tbtmItemOperation.setText(pstrCaption);
		tbtmItemOperation.setFont(Globals.stFontRegistry.get("tabitem-text"));
		return tbtmItemOperation;
	}

	
	@Override public ISOSSWTAppenderUI doLog(final String logOutput) {
		if (objTextArea != null) {
			if (objTextArea.isDisposed()) {
				
			}
			Globals.setStatus(logOutput);
			String strT = objTextArea.getText();
			int intLen = strT.length();
			objTextArea.setText(objTextArea.getText() + logOutput);
			StyleRange objSR = new StyleRange();
			objSR.start = intLen;
			objSR.length = logOutput.length();
			objSR.fontStyle = SWT.NORMAL;
			// TODO use a regexp to set the colors
			if (logOutput.trim().startsWith("INFO")) {
				objSR.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
			}
			else {
				if (logOutput.trim().startsWith("ERROR") || logOutput.trim().startsWith("com.sos.JSHelper.Exceptions")) {
					objSR.foreground = Display.getDefault().getSystemColor(SWT.COLOR_RED);
				}
				else {
					objSR.foreground = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
				}
			}
			objSRs.add(objSR);
			objTextArea.setStyleRanges(objSRs.toArray(new StyleRange[objSRs.size()] ));
			objTextArea.redraw();
//			objTextArea.setSelection(intLen + logOutput.length());
			objTextArea.setSelection(objTextArea.getCharCount());
//			objTextArea.layout(true, true);
			try {
				Thread.sleep(1);
			}
			catch (InterruptedException e) {
			}
		}
		return this;
	}

	@Override public ISOSSWTAppenderUI doClose() {
		if (objTextArea != null && objTextArea.isDisposed() == false) {
			objTextArea.getParent().dispose();
			objTextArea.setParent(null);
			objTextArea.dispose();
		}
		objTextArea = null;
		objJadeOptions = null;
		this.setData(null);
		return this;
	}

	@Override public void dispose() {
		doClose();
	}
	
	  @Override
	public void doUpdate() {
		  Display.getCurrent().syncExec(new Runnable() {
		      @Override
		      public void run() {
		        if (!objTextArea.isDisposed()) {
					objTextArea.setSelection(objTextArea.getCharCount());
		        	objTextArea.getParent().layout();
		        }
		      }
		    });
		  }
}
