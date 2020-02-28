package com.sos.DataExchange;

import com.sos.localization.SOSMsg;

public class SOSMsgJade extends SOSMsg {

    public SOSMsgJade(final String messageCode) {
        super(messageCode);

        if (Messages == null) {
            super.setMessageResource("SOSDataExchange");
            Messages = super.Messages;
        } else {
            super.Messages = Messages;
        }
    }
}
