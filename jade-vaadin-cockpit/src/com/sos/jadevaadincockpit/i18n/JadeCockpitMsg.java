package com.sos.jadevaadincockpit.i18n;

import com.sos.jadevaadincockpit.globals.Globals;
import com.sos.localization.SOSMsg;

/**
 * 
 * @author JS
 *
 */
public class JadeCockpitMsg extends SOSMsg {

	public JadeCockpitMsg(String pstrMessageCode) {
		super(pstrMessageCode);

		if (Messages == null) {
			Messages = Globals.getMessages();
		}
	}
	
	
}
