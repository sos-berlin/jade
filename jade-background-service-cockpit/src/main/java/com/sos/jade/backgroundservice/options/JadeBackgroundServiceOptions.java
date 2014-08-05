

package com.sos.jade.backgroundservice.options;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.sos.JSHelper.Annotations.JSOptionClass;
import com.sos.JSHelper.Exceptions.JSExceptionMandatoryOptionMissing;
import com.sos.JSHelper.Listener.JSListener;

/**
 * \class 		JadeBackgroundServiceOptions - JadeBackgroundService
 *
 * \brief
 * An Options as a container for the Options super class.
 * The Option class will hold all the things, which would be otherwise overwritten at a re-creation
 * of the super-class.
 *
 *

 *
 * see \see C:\Users\KB\AppData\Local\Temp\scheduler_editor-5890513725838352951.html for (more) details.
 *
 * \verbatim ;
 * mechanicaly created by JobDocu2OptionsClass.xslt from http://www.sos-berlin.com at 20140605131019
 * \endverbatim
 */
@JSOptionClass(name = "JadeBackgroundServiceOptions", description = "JadeBackgroundService")
public class JadeBackgroundServiceOptions extends JadeBackgroundServiceOptionsSuperClass {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8646735260210717840L;
	@SuppressWarnings("unused")
	private final String					conClassName						= "JadeBackgroundServiceOptions";
	@SuppressWarnings("unused")
	private static Logger		logger			= Logger.getLogger(JadeBackgroundServiceOptions.class);

    /**
    * constructors
    */

	public JadeBackgroundServiceOptions() {
	} // public JadeBackgroundServiceOptions

	public JadeBackgroundServiceOptions(final JSListener pobjListener) {
		this();
		this.registerMessageListener(pobjListener);
	} // public JadeBackgroundServiceOptions

		//

	public JadeBackgroundServiceOptions (final HashMap <String, String> JSSettings) throws Exception {
		super(JSSettings);
	} // public JadeBackgroundServiceOptions (HashMap JSSettings)
/**
 * \brief CheckMandatory - prüft alle Muss-Optionen auf Werte
 *
 * \details
 * @throws Exception
 *
 * @throws Exception
 * - wird ausgelöst, wenn eine mandatory-Option keinen Wert hat
 */
		@Override  // JadeBackgroundServiceOptionsSuperClass
	public void CheckMandatory() {
		try {
			super.CheckMandatory();
		}
		catch (Exception e) {
			throw new JSExceptionMandatoryOptionMissing(e.toString());
		}
	} // public void CheckMandatory ()
}

