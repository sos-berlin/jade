

package com.sos.jade.job;

import java.util.HashMap;

import com.sos.JSHelper.Annotations.JSOptionClass;

import com.sos.JSHelper.Exceptions.JSExceptionMandatoryOptionMissing;
import com.sos.JSHelper.Listener.JSListener; 
import org.apache.log4j.Logger;

/**
 * \class 		SOSJadeHistoryOptions - Import from order or file to JadeHistoryTable
 *
 * \brief 
 * An Options as a container for the Options super class. 
 * The Option class will hold all the things, which would be otherwise overwritten at a re-creation
 * of the super-class.
 *
 *

 *
 * see \see C:\Dokumente und Einstellungen\Uwe Risse\Lokale Einstellungen\Temp\scheduler_editor-8747611472807146950.html for (more) details.
 * 
 * \verbatim ;
 * mechanicaly created by JobDocu2OptionsClass.xslt from http://www.sos-berlin.com at 20120117145957 
 * \endverbatim
 */
@JSOptionClass(name = "SOSJadeHistoryOptions", description = "Import from order or file to JadeHistoryTable")
public class SOSJadeHistoryOptions extends SOSJadeHistoryOptionsSuperClass {
	@SuppressWarnings("unused")  //$NON-NLS-1$
	private final String					conClassName						= "SOSJadeHistoryOptions";  //$NON-NLS-1$
	@SuppressWarnings("unused")
	private static Logger		logger			= Logger.getLogger(SOSJadeHistoryOptions.class);

    /**
    * constructors
    */
    
	public SOSJadeHistoryOptions() {
	} // public SOSJadeHistoryOptions

	public SOSJadeHistoryOptions(JSListener pobjListener) {
		this();
		this.registerMessageListener(pobjListener);
	} // public SOSJadeHistoryOptions

		//

	public SOSJadeHistoryOptions (HashMap <String, String> JSSettings) throws Exception {
		super(JSSettings);
	} // public SOSJadeHistoryOptions (HashMap JSSettings)
/**
 * \brief CheckMandatory - prüft alle Muss-Optionen auf Werte
 *
 * \details
 * @throws Exception
 *
 * @throws Exception
 * - wird ausgelöst, wenn eine mandatory-Option keinen Wert hat
 */
		@Override  // SOSJadeHistoryOptionsSuperClass
	public void CheckMandatory() {
		try {
			super.CheckMandatory();
		}
		catch (Exception e) {
			throw new JSExceptionMandatoryOptionMissing(e.toString());
		}
	} // public void CheckMandatory ()
}

