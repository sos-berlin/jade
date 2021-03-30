package com.sos.yade.commons.result;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public class YadeTransferResult implements Serializable {

    private static final long serialVersionUID = -8877189880331791745L;

    private YadeTransferResultProtocol source;
    private YadeTransferResultProtocol target;
    private YadeTransferResultProtocol jump;

    private List<YadeTransferResultEntry> entries;
    private Instant start;
    private Instant end;

    private String mandator;
    private String settings;
    private String profile;
    private String operation;
    private String errorMessage;

    public YadeTransferResultProtocol getSource() {
        return source;
    }

    public void setSource(YadeTransferResultProtocol val) {
        source = val;
    }

    public YadeTransferResultProtocol getTarget() {
        return target;
    }

    public void setTarget(YadeTransferResultProtocol val) {
        target = val;
    }

    public YadeTransferResultProtocol getJump() {
        return jump;
    }

    public void setJump(YadeTransferResultProtocol val) {
        jump = val;
    }

    public List<YadeTransferResultEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<YadeTransferResultEntry> val) {
        entries = val;
    }

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant val) {
        start = val;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant val) {
        end = val;
    }

    public String getMandator() {
        return mandator;
    }

    public void setMandator(String val) {
        mandator = val;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String val) {
        settings = val;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String val) {
        profile = val;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String val) {
        operation = val;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String val) {
        errorMessage = val;
    }

}
