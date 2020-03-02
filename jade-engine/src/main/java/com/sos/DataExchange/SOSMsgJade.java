package com.sos.DataExchange;

import com.sos.localization.SOSMsg;

public class SOSMsgJade extends SOSMsg {

    public SOSMsgJade(final String messageCode) {
        super(messageCode);

        if (getMessages() == null) {
            super.setMessageResource("SOSDataExchange");
        }
    }
}
