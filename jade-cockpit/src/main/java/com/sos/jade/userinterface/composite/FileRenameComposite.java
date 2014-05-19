package com.sos.jade.userinterface.composite;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.dialog.classes.SOSCTabItem;
import com.sos.jade.userinterface.ControlCreator;

public class FileRenameComposite extends CompositeBaseClass<JADEOptions> {
	@SuppressWarnings("unused")
	private final String	conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings({ "unused", "hiding" })
	private final Logger	logger			= Logger.getLogger(this.getClass());
	public final String		conSVNVersion	= "$Id: BackgroundServiceComposite.java 21704 2013-12-29 22:35:13Z kb $";

	public FileRenameComposite(final SOSCTabItem parent, final JADEOptions objOptions) {
		this((Composite) parent.getControl(), objOptions);
	}

	public FileRenameComposite(final Composite parent, final JADEOptions objOptions) {
		super(parent, objOptions);
		if (gflgCreateControlsImmediate == true) {
			createComposite();
		}
	}

	@Override public void createComposite() {
		objCC.getInvisibleSeparator();
		{
			Group group_1 = objCC.getGroup("rename_general");
			@SuppressWarnings("hiding") ControlCreator objCC = new ControlCreator(group_1);
			objCC.getControl(objJadeOptions.ReplaceWhat, 3);
			objCC.getControl(objJadeOptions.ReplaceWith, 3);
		}
		{
			Group group_source = objCC.getGroup("rename_source");
			@SuppressWarnings("hiding") ControlCreator objCC = new ControlCreator(group_source);
			objCC.getControl(objJadeOptions.Source().ReplaceWhat, 3);
			objCC.getControl(objJadeOptions.Source().ReplaceWith, 3);
		}
		{
			Group group_target = objCC.getGroup("rename_target");
			@SuppressWarnings("hiding") ControlCreator objCC = new ControlCreator(group_target);
			objCC.getControl(objJadeOptions.Target().ReplaceWhat, 3);
			objCC.getControl(objJadeOptions.Target().ReplaceWith, 3);
		}
		enableFields();
	}

	@Override protected void enableFields() {
	}
}
