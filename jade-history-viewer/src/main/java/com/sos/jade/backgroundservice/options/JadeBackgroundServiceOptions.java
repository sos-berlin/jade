package com.sos.jade.backgroundservice.options;

import java.util.HashMap;

import com.sos.JSHelper.Annotations.JSOptionClass;
import com.sos.JSHelper.Exceptions.JSExceptionMandatoryOptionMissing;
import com.sos.JSHelper.Listener.JSListener;

@JSOptionClass(name = "JadeBackgroundServiceOptions", description = "JadeBackgroundService")
public class JadeBackgroundServiceOptions extends JadeBackgroundServiceOptionsSuperClass {

    private static final long serialVersionUID = 8646735260210717840L;

    public JadeBackgroundServiceOptions() {
        // Nothing to do
    }

    public JadeBackgroundServiceOptions(final JSListener pobjListener) {
        this();
        this.registerMessageListener(pobjListener);
    }

    public JadeBackgroundServiceOptions(final HashMap<String, String> jsSettings) throws Exception {
        super(jsSettings);
    }

    @Override
    public void CheckMandatory() {
        try {
            super.CheckMandatory();
        } catch (Exception e) {
            throw new JSExceptionMandatoryOptionMissing(e.toString());
        }
    }

}