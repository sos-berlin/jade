package com.sos.jade.TransferHistoryImport;

import java.io.File;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.JSHelper.Basics.JSToolBox;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.VirtualFileSystem.Interfaces.IJadeTransferDetailHistoryData;
import com.sos.VirtualFileSystem.Interfaces.IJadeTransferHistoryData;
import com.sos.VirtualFileSystem.Interfaces.ISOSTransferHistory;
import com.sos.VirtualFileSystem.Options.SOSFTPOptions;
import com.sos.jade.db.JadeTransferDBItem;
import com.sos.jade.db.JadeTransferDBLayer;
import com.sos.jade.db.JadeTransferDetailDBItem;

/** @author KB, Uwe Risse */
public class SOSJadeImport extends JSToolBox implements ISOSTransferHistory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SOSJadeImport.class);
    private JadeTransferDBLayer jadeTransferDBLayer;
    private IJadeTransferHistoryData jadeTransferExportData = null;
    private IJadeTransferDetailHistoryData jadeTransferDetailImportData = null;
    private JadeTransferDBItem transferItem;
    private final File configurationFile;

    public SOSJadeImport(final File configurationFile_) {
        configurationFile = configurationFile_;
    }

    private void copyFields(final IJadeTransferDetailHistoryData jadeTransferDetailImportData, final JadeTransferDetailDBItem transferDetailItem) {
        transferDetailItem.setCommand(jadeTransferDetailImportData.getCommand());
        transferDetailItem.setSourceFilename(jadeTransferDetailImportData.getSourceFilename());
        transferDetailItem.setTargetFilename(jadeTransferDetailImportData.getTargetFilename());
        transferDetailItem.setMd5(jadeTransferDetailImportData.getMd5());
        transferDetailItem.setPid(jadeTransferDetailImportData.getPid());
        transferDetailItem.setStatus(jadeTransferDetailImportData.getStatus());
        transferDetailItem.setCommandType(jadeTransferDetailImportData.getCommandType());
        transferDetailItem.setCommand(jadeTransferDetailImportData.getCommand());
        transferDetailItem.setLastErrorMessage(jadeTransferDetailImportData.getLastErrorMessage());
        transferDetailItem.setFileSize(jadeTransferDetailImportData.getFileSize());
        Date startTime = jadeTransferDetailImportData.getStartTime() == null ? null : Date.from(jadeTransferDetailImportData.getStartTime()); 
        transferDetailItem.setStartTime(startTime);
        Date endTime = jadeTransferDetailImportData.getEndTime() == null ? null : Date.from(jadeTransferDetailImportData.getEndTime()); 
        transferDetailItem.setEndTime(endTime);
        transferDetailItem.setCreated(new Date());
        transferDetailItem.setCreatedBy(jadeTransferDetailImportData.getCreatedBy());
        transferDetailItem.setModified(new Date());
        transferDetailItem.setModifiedBy(jadeTransferDetailImportData.getModifiedBy());

    }

    private void copyFields(final IJadeTransferHistoryData jadeTransferImportData, final JadeTransferDBItem transfertem) {
        transferItem.setCommand(jadeTransferImportData.getCommand());
        transferItem.setMandator(jadeTransferImportData.getMandator());
        transferItem.setSourceHost(jadeTransferImportData.getSourceHost());
        transferItem.setSourceHostIp(jadeTransferImportData.getSourceHostIp());
        transferItem.setSourceUser(jadeTransferImportData.getSourceUser());
        transferItem.setSourceDir(jadeTransferImportData.getSourceDir());
        transferItem.setTargetHost(jadeTransferImportData.getTargetHost());
        transferItem.setTargetHostIp(jadeTransferImportData.getTargetHostIp());
        transferItem.setTargetUser(jadeTransferImportData.getTargetUser());
        transferItem.setTargetDir(jadeTransferImportData.getTargetDir());
        transferItem.setProtocolType(jadeTransferImportData.getProtocolType());
        transferItem.setPort(jadeTransferImportData.getPort());
        transferItem.setStatus(jadeTransferImportData.getStatus());
        transferItem.setLastErrorMessage(jadeTransferImportData.getLastErrorMessage());
        transferItem.setFilesCount(jadeTransferImportData.getFilesCount());
        transferItem.setProfileName(jadeTransferImportData.getProfileName());
        transferItem.setProfile(jadeTransferImportData.getProfile());
        transferItem.setLog(jadeTransferImportData.getLog());
        transferItem.setCommandType(jadeTransferImportData.getCommandType());
        transferItem.setStartTime(jadeTransferImportData.getStartTime());
        transferItem.setEndTime(jadeTransferImportData.getEndTime());
        transferItem.setFileSize(jadeTransferImportData.getFileSize());
        transferItem.setCreated(new Date());
        transferItem.setCreatedBy(jadeTransferImportData.getCreatedBy());
        transferItem.setModified(new Date());
        transferItem.setModifiedBy(jadeTransferImportData.getModifiedBy());

    }

    @Override
    public void doTransferDetail() {
        final String conMethodName = conClassName + "::doTransferDetail";
        jadeTransferDBLayer = new JadeTransferDBLayer(configurationFile.getAbsolutePath());
        JadeTransferDetailDBItem transferDetailItem = new JadeTransferDetailDBItem();

        if (transferItem == null) {
            throw new JobSchedulerException(String.format("%1$s transfer Item is not set. Cannot import.", conMethodName));
        } else {
            copyFields(jadeTransferDetailImportData, transferDetailItem);
            transferItem.addTransferDetail(transferDetailItem);
            try {
                jadeTransferDBLayer.save(transferItem);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void doTransferSummary() {
        try {
            jadeTransferDBLayer = new JadeTransferDBLayer(configurationFile.getAbsolutePath());
            transferItem = new JadeTransferDBItem();
            copyFields(jadeTransferExportData, transferItem);
            try {
                jadeTransferDBLayer.save(transferItem);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new JobSchedulerException(e);
        }
    }

    @Override
    public void close() {
        if (jadeTransferDBLayer != null) {
        }
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public void setData(final SOSFTPOptions pobjOptions) {
        return;
    }

    @Override
    public void setJadeTransferData(final IJadeTransferHistoryData jadeTransferHistoryImportData) {
        jadeTransferExportData = jadeTransferHistoryImportData;

    }

    @Override
    public void setJadeTransferDetailData(final IJadeTransferDetailHistoryData jadeTransferDetailHistoryImportData) {
        jadeTransferDetailImportData = jadeTransferDetailHistoryImportData;
    }

}