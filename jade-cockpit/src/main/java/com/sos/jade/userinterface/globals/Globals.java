package com.sos.jade.userinterface.globals;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;

public class Globals {

	@SuppressWarnings("unused")
	private final Logger			logger				= Logger.getLogger(Globals.class);
	public final String		conSVNVersion		= "$Id$";

//	public static int gTextBoxStyle = SWT.None;
	public static int gTextBoxStyle = SWT.BORDER;
	public static int gButtonStyle = SWT.BORDER;
	public static FontRegistry stFontRegistry = new FontRegistry();
	public static ColorRegistry stColorRegistry = new ColorRegistry();

	public Globals() {
		// TODO Auto-generated constructor stub
	}

}
