package com.sos.jade.job;

import java.util.HashMap;

import com.sos.JSHelper.Annotations.JSOptionClass;
import com.sos.JSHelper.Annotations.JSOptionDefinition;
import com.sos.JSHelper.Exceptions.JSExceptionMandatoryOptionMissing;
import com.sos.JSHelper.Listener.JSListener;
import com.sos.JSHelper.Options.JSOptionsClass;
import com.sos.JSHelper.Options.SOSOptionString;

@JSOptionClass(name = "SOSJadeHistoryOptionsSuperClass", description = "SOSJadeHistoryOptionsSuperClass")
public class SOSJadeHistoryOptionsSuperClass extends JSOptionsClass {

    private static final String CLASSNAME = "SOSJadeHistoryOptionsSuperClass";

    @JSOptionDefinition(name = "configuration_file", description = "", key = "configuration_file", type = "SOSOptionString", mandatory = false)
    public SOSOptionString configuration_file = new SOSOptionString(this, CLASSNAME + ".configuration_file", "", " ", " ", false);

    public SOSOptionString getconfiguration_file() {
        return configuration_file;
    }

    public void setconfiguration_file(final SOSOptionString p_configuration_file) {
        configuration_file = p_configuration_file;
    }

    public SOSJadeHistoryOptionsSuperClass() {
        objParentClass = this.getClass();
    }

    public SOSJadeHistoryOptionsSuperClass(final JSListener pobjListener) {
        this();
        this.registerMessageListener(pobjListener);
    }

    public SOSJadeHistoryOptionsSuperClass(final HashMap<String, String> JSSettings) throws Exception {
        this();
        this.setAllOptions(JSSettings);
    }

    @Override
    public void setAllOptions(final HashMap<String, String> pobjJSSettings) {
        flgSetAllOptions = true;
        objSettings = pobjJSSettings;
        super.Settings(objSettings);
        super.setAllOptions(pobjJSSettings);
        flgSetAllOptions = false;
    }

    @Override
    public void CheckMandatory() throws JSExceptionMandatoryOptionMissing, Exception {
        try {
            super.CheckMandatory();
        } catch (Exception e) {
            throw new JSExceptionMandatoryOptionMissing(e.toString());
        }
    }

    @Override
    public void CommandLineArgs(final String[] pstrArgs) {
        super.CommandLineArgs(pstrArgs);
        this.setAllOptions(super.objSettings);
    }

}