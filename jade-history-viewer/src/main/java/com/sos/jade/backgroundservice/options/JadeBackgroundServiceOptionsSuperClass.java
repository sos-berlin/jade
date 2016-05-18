package com.sos.jade.backgroundservice.options;

import java.util.HashMap;

import com.sos.JSHelper.Annotations.JSOptionClass;
import com.sos.JSHelper.Annotations.JSOptionDefinition;
import com.sos.JSHelper.Exceptions.JSExceptionMandatoryOptionMissing;
import com.sos.JSHelper.Listener.JSListener;
import com.sos.JSHelper.Options.JSOptionsClass;
import com.sos.JSHelper.Options.SOSOptionInFileName;
import com.sos.JSHelper.Options.SOSOptionString;

@JSOptionClass(name = "JadeBackgroundServiceOptionsSuperClass", description = "JadeBackgroundServiceOptionsSuperClass")
public class JadeBackgroundServiceOptionsSuperClass extends JSOptionsClass {

    private static final long serialVersionUID = 338878527661019237L;
    private final String conClassName = "JadeBackgroundServiceOptionsSuperClass";

    @JSOptionDefinition(name = "hibernateConfigurationFileName", description = "", key = "hibernateConfigurationFileName", type = "SOSOptionString", mandatory = true)
    public SOSOptionInFileName hibernateConfigurationFileName = new SOSOptionInFileName(this, conClassName + ".hibernateConfigurationFileName", "", "env:hibernateConfigFile", "/WEB-INF/classes/hibernate.cfg.xml", true);

    public SOSOptionInFileName hibernateConf = (SOSOptionInFileName) hibernateConfigurationFileName.SetAlias("hibernateConf", "H");

    public SOSOptionInFileName getHibernateConfigurationFileName() {
        return hibernateConfigurationFileName;
    }

    public void setHibernateConfigurationFileName(final SOSOptionInFileName pHibernateConfigurationFileName) {
        hibernateConfigurationFileName = pHibernateConfigurationFileName;
    }

    @JSOptionDefinition(name = "security_server", description = "", key = "security_server", type = "SOSOptionString", mandatory = false)
    public SOSOptionString securityServer = new SOSOptionString(this, conClassName + ".SecurityServer", "Security Server for security rest service", "", "", false);

    public SOSOptionString getSecurityServer() {
        return this.securityServer;
    }

    public void setSecurityServer(final SOSOptionString p_securityServer) {
        this.securityServer = p_securityServer;
    }

    @JSOptionDefinition(name = "devel", description = "sets the state of Development; possible states are DEVEL, QA, RELEASED", key = "devel", type = "SOSOptionDevelopment", mandatory = false)
    public SOSOptionDevelopment devel = new SOSOptionDevelopment(this, conClassName + ".devel", "sets the state of Development; possible states are DEVEL, QA, RELEASED", "env:develMode", "DEVEL", false);

    public SOSOptionDevelopment getDevel() {
        return devel;
    }

    public JadeBackgroundServiceOptionsSuperClass setDevel(final SOSOptionDevelopment pstrValue) {
        devel = pstrValue;
        return this;
    }

    @JSOptionDefinition(name = "webserverType", description = "the type of webserver which runs the application", key = "webserverType", type = "SOSOptionWebserverType", mandatory = false)
    public SOSOptionWebserverType webserverType = new SOSOptionWebserverType(this, conClassName + ".webserverType", "the type of webserver which runs the application, possible values are TOMCAT, JETTY", "env:serverType", "TOMCAT", false);

    public SOSOptionWebserverType getWebserverType() {
        return webserverType;
    }

    public JadeBackgroundServiceOptionsSuperClass setWebserverType(final SOSOptionWebserverType pstrValue) {
        webserverType = pstrValue;
        return this;
    }

    public JadeBackgroundServiceOptionsSuperClass() {
        objParentClass = this.getClass();
    }

    public JadeBackgroundServiceOptionsSuperClass(final JSListener pobjListener) {
        this();
        this.registerMessageListener(pobjListener);
    }

    public JadeBackgroundServiceOptionsSuperClass(final HashMap<String, String> JSSettings) throws Exception {
        this();
        this.setAllOptions(JSSettings);
    }

    @Override
    public void setAllOptions(final HashMap<String, String> pobjJSSettings) {
        objSettings = pobjJSSettings;
        super.setAllOptions(pobjJSSettings);
    }

    @Override
    public void checkMandatory() throws JSExceptionMandatoryOptionMissing, Exception {
        try {
            super.checkMandatory();
        } catch (Exception e) {
            throw new JSExceptionMandatoryOptionMissing(e.toString());
        }
    }

    @Override
    public void commandLineArgs(final String[] pstrArgs) {
        super.commandLineArgs(pstrArgs);
        this.setAllOptions(super.objSettings);
    }
}