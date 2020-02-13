package com.sos.jade.TransferHistoryImport;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

import com.sos.VirtualFileSystem.Interfaces.IJadeTransferDetailHistoryData;

public class SOSJadeDetailImportData implements IJadeTransferDetailHistoryData {

    @SuppressWarnings("unused")
    private final String conClassName = "SOSJadeDetailImportData";
    private HashMap data = null;

    public SOSJadeDetailImportData() {
        //
    }

    public void setData(HashMap h) {
        this.data = h;
    }

    @Override
    public Integer getTransferDetailsId() {
        return Integer.parseInt(this.data.get("transfer_detail_id").toString());
    }

    @Override
    public Integer getTransferId() {
        return Integer.parseInt(this.data.get("transfer_id").toString());
    }

    @Override
    public Long getFileSize() {
        return new Long(3);
    }

    @Override
    public Integer getCommandType() {
        return Integer.parseInt(this.data.get("command_type").toString());
    }

    @Override
    public String getCommand() {
        return this.data.get("command").toString();
    }

    @Override
    public String getPid() {
        return this.data.get("pid").toString();
    }

    @Override
    public String getLastErrorMessage() {
        return this.data.get("lastErrorMessage").toString();
    }

    @Override
    public String getTargetFilename() {
        return this.data.get("targetFileName").toString();
    }

    @Override
    public String getSourceFilename() {
        return this.data.get("sourceFileName").toString();
    }

    @Override
    public String getMd5() {
        return this.data.get("md5").toString();
    }

    @Override
    public Integer getStatus() {
        return Integer.parseInt(this.data.get("status").toString());
    }

    @Override
    public Instant getStartTime() {
        return Instant.now();
    }

    @Override
    public java.time.Instant getEndTime() {
        return Instant.now();
    }

    @Override
    public String getModifiedBy() {
        return this.data.get("modifiedBy").toString();
    }

    @Override
    public String getCreatedBy() {
        return this.data.get("createdBy").toString();
    }

    @Override
    public Date getCreated() {
        return new Date();
    }

    @Override
    public Date getModified() {
        return new Date();
    }

    @Override
    public String getStatusText() {
        return this.data.get("statusText").toString();
    }

    @Override
    public String getSizeValue() {
        return this.data.get("sizeValue").toString();
    }
}
