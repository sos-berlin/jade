package com.sos.jade.job;

import java.util.HashMap;

import com.sos.JSHelper.Annotations.JSOptionClass;

import com.sos.JSHelper.Exceptions.JSExceptionMandatoryOptionMissing;
import com.sos.JSHelper.Listener.JSListener;
import org.apache.log4j.Logger;

/** \class JadeDeleteHistoryOptions - Delete entries in Jade history table
 *
 * \brief An Options as a container for the Options super class. The Option
 * class will hold all the things, which would be otherwise overwritten at a
 * re-creation of the super-class.
 *
 *
 * 
 *
 * see \see C:\Dokumente und Einstellungen\Uwe Risse\Lokale
 * Einstellungen\Temp\scheduler_editor-1482207325261082807.html for (more)
 * details.
 * 
 * \verbatim ; mechanicaly created by JobDocu2OptionsClass.xslt from
 * http://www.sos-berlin.com at 20111221170034 \endverbatim */
@JSOptionClass(name = "JadeDeleteHistoryOptions", description = "Delete entries in Jade history table")
public class JadeDeleteHistoryOptions extends JadeDeleteHistoryOptionsSuperClass {

    @SuppressWarnings("unused")//$NON-NLS-1$
    private final String conClassName = "JadeDeleteHistoryOptions";  //$NON-NLS-1$
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(JadeDeleteHistoryOptions.class);

    /** constructors */

    public JadeDeleteHistoryOptions() {
    } // public JadeDeleteHistoryOptions

    public JadeDeleteHistoryOptions(JSListener pobjListener) {
        this();
        this.registerMessageListener(pobjListener);
    } // public JadeDeleteHistoryOptions

    //

    public JadeDeleteHistoryOptions(HashMap<String, String> JSSettings) throws Exception {
        super(JSSettings);
    } // public JadeDeleteHistoryOptions (HashMap JSSettings)

    /** \brief CheckMandatory - prüft alle Muss-Optionen auf Werte
     *
     * \details
     * 
     * @throws Exception
     *
     * @throws Exception - wird ausgelöst, wenn eine mandatory-Option keinen
     *             Wert hat */
    @Override
    // JadeDeleteHistoryOptionsSuperClass
    public void CheckMandatory() {
        try {
            super.CheckMandatory();
        } catch (Exception e) {
            throw new JSExceptionMandatoryOptionMissing(e.toString());
        }
    } // public void CheckMandatory ()
}
