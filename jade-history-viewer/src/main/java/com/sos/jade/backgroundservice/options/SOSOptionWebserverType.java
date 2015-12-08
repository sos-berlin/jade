package com.sos.jade.backgroundservice.options;

import com.sos.JSHelper.Options.JSOptionsClass;
import com.sos.JSHelper.Options.SOSOptionStringValueList;

public class SOSOptionWebserverType extends SOSOptionStringValueList {

    private static final long serialVersionUID = 1L;

    public enum WebserverType {
        JETTY, TOMCAT
    }

    private WebserverType webServerType = null;

    public SOSOptionWebserverType(JSOptionsClass pPobjParent, String pPstrKey, String pPstrDescription, String pPstrValue, String pPstrDefaultValue,
            boolean pPflgIsMandatory) {
        super(pPobjParent, pPstrKey, pPstrDescription, pPstrValue, pPstrDefaultValue, pPflgIsMandatory);
        if (pPstrValue != null && pPstrValue.contains("env:")) {
            setWebserverType(pPstrDefaultValue);
        } else {
            setWebserverType(pPstrValue);
        }
    }

    public boolean isJetty() {
        return webServerType == WebserverType.JETTY;
    }

    public boolean isTomcat() {
        return webServerType == WebserverType.TOMCAT;
    }

    private void setWebserverType(String type) {
        if ("JETTY".equalsIgnoreCase(type)) {
            this.webServerType = WebserverType.JETTY;
        } else if ("TOMCAT".equalsIgnoreCase(type)) {
            this.webServerType = WebserverType.TOMCAT;
        }
    }

    @Override
    public String[] getValueList() {
        if (strValueList == null) {
            strValueList = new String[] {};
            createValueList("JETTY;TOMCAT");
        }
        return strValueList;
    }

}
