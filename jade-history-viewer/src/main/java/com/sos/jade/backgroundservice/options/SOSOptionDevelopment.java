package com.sos.jade.backgroundservice.options;

import com.sos.JSHelper.Options.JSOptionsClass;
import com.sos.JSHelper.Options.SOSOptionStringValueList;

public class SOSOptionDevelopment extends SOSOptionStringValueList {

    private static final long serialVersionUID = 1L;

    public enum DevelopmentState {
        DEVEL, QA, RELEASED
    }

    private DevelopmentState develState = null;

    public SOSOptionDevelopment(JSOptionsClass pPobjParent, String pPstrKey, String pPstrDescription, String pPstrValue, String pPstrDefaultValue,
            boolean pPflgIsMandatory) {
        super(pPobjParent, pPstrKey, pPstrDescription, pPstrValue, pPstrDefaultValue, pPflgIsMandatory);
        if (pPstrValue != null && pPstrValue.contains("env:")) {
            setDevelState(pPstrDefaultValue);
        } else {
            setDevelState(pPstrValue);
        }
    }

    public boolean isDevelopment() {
        return develState == DevelopmentState.DEVEL;
    }

    public boolean isQa() {
        return develState == DevelopmentState.QA;
    }

    public boolean isReleased() {
        return develState == DevelopmentState.RELEASED;
    }

    public void setDevelState(String state) {
        if ("DEVEL".equalsIgnoreCase(state)) {
            this.develState = DevelopmentState.DEVEL;
        } else if ("QA".equalsIgnoreCase(state)) {
            this.develState = DevelopmentState.QA;
        } else if ("RELEASED".equalsIgnoreCase(state)) {
            this.develState = DevelopmentState.RELEASED;
        }
    }

    @Override
    public void Value(final String pstrValue) {
        super.Value(pstrValue);
    }

    @Override
    public String[] getValueList() {

        if (strValueList == null) {
            strValueList = new String[] {};
            createValueList("DEVEL;QA;RELEASED");
        }

        return strValueList;
    }

}
