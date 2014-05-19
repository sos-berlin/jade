package com.sos.jade.userinterface.composite;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

import com.sos.DataExchange.Options.JADEOptions;


public class MiscComposite extends CompositeBaseClass<JADEOptions> {
	@SuppressWarnings("unused") private final String		conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings("unused") private static final String	conSVNVersion	= "$Id: OperationComposite.java 21704 2013-12-29 22:35:13Z kb $";
//	@SuppressWarnings("unused") private final Logger		logger			= Logger.getLogger(this.getClass());

	public MiscComposite(final CTabItem parent, final JADEOptions objOptions) {
		this((Composite) parent.getControl(), objOptions);
	}

	public MiscComposite(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		objJadeOptions = objOptions;
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}		
	}

	@Override public void createComposite() {
//		objCC.getControl(objJadeOptions.title, 3);
//		objCC.getControl(objJadeOptions.operation, 3);
//		Button btnCheckButton = (Button) objCC.getControl(objJadeOptions.TransactionMode);
//		btnCheckButton.addSelectionListener(new SelectionAdapter() {
//			@Override public void widgetSelected(final SelectionEvent e) {
//			}
//		});
//		objCC.getLabel(2);
//		objCC.getControl(objJadeOptions.atomic_prefix);
//		objCC.getControl(objJadeOptions.atomic_suffix);
//		objCC.getControl(objJadeOptions.MaxConcurrentTransfers);
//		objCC.getControl(objJadeOptions.ConcurrentTransfer);
//		objCC.getControl(objJadeOptions.reuseConnection);
//		objCC.getSeparator();
//		objCC.getControl(objJadeOptions.PreTransferCommands, 3);
//		objCC.getControl(objJadeOptions.PostTransferCommands, 3);
//		objCC.getControl(objJadeOptions.include, 3);
	}
}
