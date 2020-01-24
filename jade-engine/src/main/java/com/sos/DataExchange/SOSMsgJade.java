package com.sos.DataExchange;

import com.sos.localization.Messages;
import com.sos.localization.SOSMsg;

public class SOSMsgJade extends SOSMsg {

    @SuppressWarnings("unused")
    private final String conClassName = this.getClass().getSimpleName();
    @SuppressWarnings("unused")
    private static final String conSVNVersion = "$Id$";

    public static final Messages SOSMsgJadeProperties = null;

    public SOSMsgJade(final String pstrMessageCode) {
        super(pstrMessageCode);

        if (Messages == null) {
            super.setMessageResource("SOSDataExchange");
            Messages = super.Messages;
        } else {
            super.Messages = Messages;
        }
    }
}
