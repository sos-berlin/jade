

package com.sos.jade.backgroundservice.options;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.sos.JSHelper.Annotations.JSOptionClass;
import com.sos.JSHelper.Exceptions.JSExceptionMandatoryOptionMissing;
import com.sos.JSHelper.Listener.JSListener;

/**
 * \class 		JadeBSFrontEndOptions - JadeBSFrontEnd
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
@JSOptionClass(name = "JadeBSFrontEndOptions", description = "JadeBSFrontEnd")
public class JadeBSFrontEndOptions extends JadeBSFrontEndOptionsSuperClass {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8646735260210717840L;
	@SuppressWarnings("unused")
	private final String					conClassName						= "JadeBSFrontEndOptions";
	@SuppressWarnings("unused")
	private static Logger		logger			= Logger.getLogger(JadeBSFrontEndOptions.class);

    /**
    * constructors
    */

	public JadeBSFrontEndOptions() {
	} // public JadeBSFrontEndOptions

	public JadeBSFrontEndOptions(final JSListener pobjListener) {
		this();
		this.registerMessageListener(pobjListener);
	} // public JadeBSFrontEndOptions

		//

	public JadeBSFrontEndOptions (final HashMap <String, String> JSSettings) throws Exception {
		super(JSSettings);
	} // public JadeBSFrontEndOptions (HashMap JSSettings)
/**
 * \brief CheckMandatory - pr�ft alle Muss-Optionen auf Werte
 *
 * \details
 * @throws Exception
 *
 * @throws Exception
 * - wird ausgel�st, wenn eine mandatory-Option keinen Wert hat
 */
		@Override  // JadeBSFrontEndOptionsSuperClass
	public void CheckMandatory() {
		try {
			super.CheckMandatory();
		}
		catch (Exception e) {
			throw new JSExceptionMandatoryOptionMissing(e.toString());
		}
	} // public void CheckMandatory ()
}

