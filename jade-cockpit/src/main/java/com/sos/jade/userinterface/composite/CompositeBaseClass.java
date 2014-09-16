/**
 *
 */
package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

import com.sos.JSHelper.Options.IValueChangedListener;
import com.sos.JSHelper.Options.SOSOptionElement;
import com.sos.dialog.Globals;
import com.sos.dialog.interfaces.ISOSTabItem;
import com.sos.dialog.layouts.Gridlayout;
import com.sos.jade.userinterface.ControlCreator;

/**
 * @author KB
 *
 */
public abstract class CompositeBaseClass<T> extends Composite implements ISOSTabItem {
	private final String		conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings("unused") private static final String	conSVNVersion	= "$Id$";
	protected Logger			logger			= Logger.getLogger(this.getClass());
	protected Composite										objParent		= null;
	protected T												objJadeOptions	= null;
	protected ControlCreator								objCC			= null;
	protected Composite										composite		= this;
	public static boolean gflgCreateControlsImmediate = true;

	public CompositeBaseClass(final CTabItem parent, final T objOptions) {
		this((Composite) parent.getControl(), objOptions);
	}

	@Override public boolean setParent(final Composite pobjParent) {
		super.setParent(pobjParent);
		return true;
	}

	public CompositeBaseClass(final Composite parent, final T objOptions) {
		super(parent, SWT.H_SCROLL + SWT.V_SCROLL);
		objJadeOptions = objOptions;
		getControlCreator(parent);
	}

	private void getControlCreator(final Composite parent) {
		composite = parent;
		objParent = parent;
//		Gridlayout.set4ColumnLayout(composite);
		setLayout(Gridlayout.get4ColumnLayout());
		objCC = new ControlCreator(composite);
		objCC.getInvisibleSeparator();
		setBackground(Globals.getCompositeBackground());
	}

	public CompositeBaseClass(final Composite parent) {
		super(parent, SWT.None);
		getControlCreator(parent);
	}

	@Override public void createTabItemComposite() {
//		Gridlayout.set4ColumnLayout(composite);
		objCC = new ControlCreator(composite);
		createComposite();
		logger.debug("createTabItemComposite " + conClassName);
		composite.layout(true);
		composite.getParent().layout(true);
	}
	protected SelectionAdapter	EnableFieldsListener	= new SelectionAdapter() {
															@Override public void widgetSelected(final SelectionEvent e) {
																enableFields();
															}
														};

	protected void enableFields() {
	}

	@Override public boolean validateData() {
		return true;
	}

	@Override public void dispose() {
		logger.debug("Control disposed: " + conClassName);
		for (Control objContr : composite.getChildren()) {
//			Object objO = objContr.getData();
//			if (objO != null && objO instanceof SOSOptionElement) {
//				
//			}
			Object objO = objContr.getData();
			if (objO instanceof IValueChangedListener) {
				SOSOptionElement objV = (SOSOptionElement) objO;

			}
			Listener[] objL = objContr.getListeners(SWT.ALL);
			for (Listener listener : objL) {
				objContr.removeListener(SWT.ALL, listener);
			}
//
		}
		super.dispose();
		composite.dispose();
	}

	@Override protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
