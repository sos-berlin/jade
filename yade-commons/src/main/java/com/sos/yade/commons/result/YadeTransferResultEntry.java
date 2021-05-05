package com.sos.yade.commons.result;

import java.io.Serializable;

public class YadeTransferResultEntry implements Serializable {

    private static final long serialVersionUID = -8877189880331791745L;

    private String source;
    private String target;
    private long size;
    private long modificationDate;

    private String state;
    private String errorMessage;
    private String integrityHash;

    public String getSource() {
        return source;
    }

    public void setSource(String val) {
        source = val;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String val) {
        target = val;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long val) {
        size = val;
    }

    public long getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(long val) {
        modificationDate = val;
    }

    public String getState() {
        return state;
    }

    public void setState(String val) {
        state = val;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String val) {
        errorMessage = val;
    }

    public String getIntegrityHash() {
        return integrityHash;
    }

    public void setIntegrityHash(String val) {
        integrityHash = val;
    }

}
