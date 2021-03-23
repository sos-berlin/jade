package com.sos.yade.commons.result;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public class YadeTransferResult implements Serializable {

    private static final long serialVersionUID = -8877189880331791745L;

    private YadeTransferResultProvider source;
    private YadeTransferResultProvider target;
    private YadeTransferResultProvider jump;

    private String settings;
    private String profile;
    private String operation;

    private Instant start;
    private Instant end;

    private String errorMessage;

    private List<YadeTransferResultEntry> entries;

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

    public YadeTransferResultProvider getSource() {
        return source;
    }

    public void setSource(YadeTransferResultProvider val) {
        source = val;
    }

    public YadeTransferResultProvider getTarget() {
        return target;
    }

    public void setTarget(YadeTransferResultProvider val) {
        target = val;
    }

    public YadeTransferResultProvider getJump() {
        return jump;
    }

    public void setJump(YadeTransferResultProvider val) {
        jump = val;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String val) {
        operation = val;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String val) {
        errorMessage = val;
    }

    public List<YadeTransferResultEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<YadeTransferResultEntry> val) {
        entries = val;
    }

}
