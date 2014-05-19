package com.sos.jade.userinterface.globals;
import org.apache.log4j.Logger;

import com.sos.dialog.classes.SOSMsgControl;
import com.sos.localization.Messages;

public class JADEMsg extends SOSMsgControl {
	@SuppressWarnings("unused") 
	private final String		conClassName	= this.getClass().getSimpleName();
	@SuppressWarnings("unused")
	private static final String	conSVNVersion	= "$Id$";
	@SuppressWarnings("unused")
	private final Logger		logger			= Logger.getLogger(this.getClass());
	private static Messages		objJadeMessages	= null;

	public JADEMsg(final String pstrMessageCode) {
		super(adjustMsgCode(pstrMessageCode));
		if (objJadeMessages == null) {
			super.setMessageResource("JADEMessages");
			objJadeMessages = super.Messages;
		}
		else {
			super.Messages = objJadeMessages;
		}
	} // public

	@Override
	public JADEMsg newMsg(final String pstrMessageCode) {
		return new JADEMsg (pstrMessageCode);
	}

	private static  String adjustMsgCode (final String pstrMsgCode) {
		return "JADE_L_" + pstrMsgCode.toLowerCase();
	}
	@Override
	public void openHelp(final String helpKey) {
	} // public void openHelp
}
