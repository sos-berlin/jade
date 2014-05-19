package com.sos.jade.userinterface.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.sos.JSHelper.Options.JSOptionsClass;
import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;

public class TemplateComposite extends Composite {

	@SuppressWarnings("unused")
	private SOSConnection2OptionsAlternate	objJadeOptions	= null;

	public TemplateComposite(final Composite parent, final JSOptionsClass objOptions) {
		super(parent, SWT.None);
		objJadeOptions = (SOSConnection2OptionsAlternate) objOptions;

		GridLayout gridLayout = new GridLayout(4, true);
		setLayout(gridLayout);
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		{
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
