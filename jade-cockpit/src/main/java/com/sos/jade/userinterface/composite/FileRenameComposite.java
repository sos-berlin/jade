package com.sos.jade.userinterface.composite;
import org.eclipse.swt.widgets.Composite;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.dialog.components.CompositeBaseClass;

public class FileRenameComposite extends CompositeBaseClass<JADEOptions> {
	@SuppressWarnings("unused")
	private final String	conClassName	= this.getClass().getSimpleName();
	//	@SuppressWarnings({ "unused" })
	//	private final Logger	logger			= Logger.getLogger(this.getClass());
	public final String		conSVNVersion	= "$Id: BackgroundServiceComposite.java 21704 2013-12-29 22:35:13Z kb $";

	public FileRenameComposite(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}
	}

	@Override
	public void createComposite() {
		objCC.getInvisibleSeparator();
		{
			boolean isOperationRename = objJadeOptions.operation.isOperationRename();
			if (isOperationRename == false) {
				objCC.getSeparator("rename_general");
				objCC.getControl(objJadeOptions.ReplaceWhat, 3);
				objCC.getControl(objJadeOptions.ReplaceWith, 3);
			}

			objCC.getSeparator("rename_on_source");
			objCC.getControl(objJadeOptions.Source().ReplaceWhat, 3);
			objCC.getControl(objJadeOptions.Source().ReplaceWith, 3);

			if (isOperationRename == false) {
				objCC.getSeparator("rename_on_target");
				objCC.getControl(objJadeOptions.Target().ReplaceWhat, 3);
				objCC.getControl(objJadeOptions.Target().ReplaceWith, 3);
			}
		}
		enableFields();
	}

	@Override
	protected void enableFields() {
	}
}
