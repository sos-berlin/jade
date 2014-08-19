package com.sos.jadevaadincockpit.i18n;

import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.localization.SOSMsg;

/**
 * 
 * @author JS
 *
 */
public class JadeCockpitMsg extends SOSMsg {

	public JadeCockpitMsg(String pstrMessageCode) {
		super(pstrMessageCode);
		
		/*
		 * TODO
		 * objMissingCodesPropertiesFile aus Superklasse sollte hier gesetzt werden. (protected oder setter)
		 */
		
		if (Messages == null) {
			Messages = JadevaadincockpitUI.getCurrent().getApplicationAttributes().getMessages();
		}
	}
	
	public String getMessageCode() {
		return strMessageCode;
	}
	
	
}
