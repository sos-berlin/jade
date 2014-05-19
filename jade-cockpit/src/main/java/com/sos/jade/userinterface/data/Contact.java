package com.sos.jade.userinterface.data;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.PlatformObject;

public abstract class Contact extends PlatformObject {

	@SuppressWarnings("unused")
	private final Logger			logger				= Logger.getLogger(Contact.class);
	public final String		conSVNVersion		= "$Id$";


	public abstract String getName();

	public abstract SectionsHandler getParent();
}
