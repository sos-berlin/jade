package com.sos.DataExchange.helpers;

import java.net.URL;
import java.time.Instant;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.DataExchange.SOSDataExchangeEngine;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.JSHelper.Options.SOSOptionJadeOperation;
import com.sos.JSHelper.Options.SOSOptionTransferType;
import com.sos.VirtualFileSystem.DataElements.SOSFileList;
import com.sos.VirtualFileSystem.DataElements.SOSFileListEntry;
import com.sos.hibernate.classes.SOSHibernateSession;
import com.sos.hibernate.exceptions.SOSHibernateException;
import com.sos.jade.db.DBItemYadeFiles;
import com.sos.jade.db.DBItemYadeProtocols;
import com.sos.jade.db.DBItemYadeTransfers;
import com.sos.jade.db.YadeDBLayer;


public class YadeDBOperationHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(YadeDBOperationHelper.class);
    private String currentNodeName;
    private String currentJobChain;
    private String currentJob;
    private String currentOrderId;
    private String currentJobschedulerId;
    private DBItemYadeProtocols sourceProtocolDBItem;
    private DBItemYadeProtocols targetProtocolDBItem;
    private DBItemYadeProtocols jumpProtocolDBItem;
    private DBItemYadeTransfers transferDBItem;
    private SOSDataExchangeEngine yadeEngine;
    private Long parentTransferId = null;
    private Long taskId = null;
    private Boolean hasErrors = null; 
    
    public YadeDBOperationHelper(SOSDataExchangeEngine yadeEngine) {
        this.yadeEngine = yadeEngine;
        addAdditionalJobInfosFromOptions();
    }

    public Long storeTransferInformationToDB(SOSHibernateSession dbSession, Long parentTransferId) {
        Long transferId = null;
        YadeDBLayer dbLayer = new YadeDBLayer(dbSession);
        try {
            if (sourceProtocolDBItem == null && yadeEngine.getOptions().sourceDir.isDirty()) {
                dbSession.beginTransaction();
                sourceProtocolDBItem = new DBItemYadeProtocols();
                sourceProtocolDBItem.setHostname(yadeEngine.getOptions().getSource().host.getValue());
                sourceProtocolDBItem.setPort(yadeEngine.getOptions().getSource().getPort().value());
                Integer sourceProtocol = getProtocolFromTransferType(yadeEngine.getOptions().getSource().getProtocol().getEnum());
                if (sourceProtocol == 7) {
                    URL url = yadeEngine.getOptions().getSource().getUrl().getUrl();
                    if (url.getProtocol().toLowerCase().equals("webdavs")) {
                        sourceProtocolDBItem.setProtocol(8);
                    } else {
                        sourceProtocolDBItem.setProtocol(sourceProtocol);
                    }
                } else {
                    sourceProtocolDBItem.setProtocol(sourceProtocol);
                }
                sourceProtocolDBItem.setAccount(yadeEngine.getOptions().getSource().user.getValue());
//                LOGGER.debug("source Host = " + yadeEngine.getOptions().getSource().host.getValue());
//                LOGGER.debug("source port = " + yadeEngine.getOptions().getSource().getPort().value());
//                LOGGER.debug("source protocol = " + yadeEngine.getOptions().getSource().getProtocol().getEnum().getText());
//                LOGGER.debug("source account = " + yadeEngine.getOptions().getSource().user.getValue());
                DBItemYadeProtocols sourceProtocolFromDb = null;
                try {
                    sourceProtocolFromDb = dbLayer.getProtocolFromDb(sourceProtocolDBItem.getHostname(),
                            sourceProtocolDBItem.getPort(), sourceProtocolDBItem.getProtocol());
                } catch (SOSHibernateException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                Long sourceProtocolId = null;
                if (sourceProtocolFromDb != null) {
                    sourceProtocolId = sourceProtocolFromDb.getId();
                    sourceProtocolDBItem = sourceProtocolFromDb;
                } else {
                    try {
                        dbLayer.getSession().save(sourceProtocolDBItem);
                        sourceProtocolId = sourceProtocolDBItem.getId();
                    } catch (SOSHibernateException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
                dbSession.commit();
            }
            
            if (targetProtocolDBItem == null && yadeEngine.getOptions().targetDir.isDirty()) {
                dbSession.beginTransaction();
                targetProtocolDBItem = new DBItemYadeProtocols();
                targetProtocolDBItem.setHostname(yadeEngine.getOptions().getTarget().host.getValue());
                targetProtocolDBItem.setPort(yadeEngine.getOptions().getTarget().getPort().value());
                Integer targetProtocol = getProtocolFromTransferType(yadeEngine.getOptions().getTarget().getProtocol().getEnum());
                if (targetProtocol == 7) {
                    URL url = yadeEngine.getOptions().getTarget().getUrl().getUrl();
                    if (url.getProtocol().toLowerCase().equals("https")) {
                        targetProtocolDBItem.setProtocol(8);
                    } else {
                        targetProtocolDBItem.setProtocol(targetProtocol);
                    }
                } else {
                    targetProtocolDBItem.setProtocol(targetProtocol);
                }
                targetProtocolDBItem.setAccount(yadeEngine.getOptions().getTarget().user.getValue());
//                LOGGER.debug("target Host = " + yadeEngine.getOptions().getTarget().host.getValue());
//                LOGGER.debug("target port = " + yadeEngine.getOptions().getTarget().getPort().value());
//                LOGGER.debug("target protocol = " + yadeEngine.getOptions().getTarget().getProtocol().getEnum().getText());
//                LOGGER.debug("target account = " + yadeEngine.getOptions().getTarget().user.getValue());
                DBItemYadeProtocols targetProtocolFromDb = null;
                try {
                    targetProtocolFromDb = dbLayer.getProtocolFromDb(targetProtocolDBItem.getHostname(),
                            targetProtocolDBItem.getPort(), targetProtocolDBItem.getProtocol());
                } catch (SOSHibernateException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                Long targetProtocolId = null;
                if (targetProtocolFromDb != null) {
                    targetProtocolId = targetProtocolFromDb.getId();
                    targetProtocolDBItem = targetProtocolFromDb;
                } else {
                    try {
                        dbLayer.getSession().save(targetProtocolDBItem);
                        targetProtocolId = targetProtocolDBItem.getId();
                    } catch (SOSHibernateException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
                dbSession.commit();
            }
            
            if (jumpProtocolDBItem == null && yadeEngine.getOptions().getJumpHost().isDirty()) { 
                dbSession.beginTransaction();
                jumpProtocolDBItem = new DBItemYadeProtocols();
                jumpProtocolDBItem.setHostname(yadeEngine.getOptions().getJumpHost().getValue());
                jumpProtocolDBItem.setPort(yadeEngine.getOptions().getJumpPort().value());
                jumpProtocolDBItem.setProtocol(getProtocolFromString(yadeEngine.getOptions().getJumpProtocol().getValue()));
                jumpProtocolDBItem.setAccount(yadeEngine.getOptions().getJumpUser().getValue());
//                LOGGER.debug("jump Host = " + yadeEngine.getOptions().getJumpHost().getValue());
//                LOGGER.debug("jump port = " + yadeEngine.getOptions().getJumpPort().value());
//                LOGGER.debug("jump protocol = " + yadeEngine.getOptions().getJumpProtocol().getValue());
//                LOGGER.debug("jump account = " + yadeEngine.getOptions().getJumpUser().getValue());
                DBItemYadeProtocols jumpProtocolFromDb = null;
                try {
                    jumpProtocolFromDb = dbLayer.getProtocolFromDb(jumpProtocolDBItem.getHostname(),
                            jumpProtocolDBItem.getPort(), jumpProtocolDBItem.getProtocol());
                } catch (SOSHibernateException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                Long jumpProtocolId = null;
                if (jumpProtocolFromDb != null) {
                    jumpProtocolId = jumpProtocolFromDb.getId();
                    jumpProtocolDBItem = jumpProtocolFromDb;
                } else {
                    try {
                        dbLayer.getSession().save(jumpProtocolDBItem);
                        jumpProtocolId = jumpProtocolDBItem.getId();
                    } catch (SOSHibernateException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
                dbSession.commit();
            }

            dbSession.beginTransaction();
            DBItemYadeTransfers transferFromDb = null;
            try {
                if (transferDBItem != null && transferDBItem.getId() != null) {
                    transferFromDb = dbLayer.getTransferFromDb(transferDBItem.getId());
                }
                if (transferFromDb != null) {
                    LOGGER.debug(String.format("transfer item with id = %1$s found in DB!", transferDBItem.getId()));
                }
            } catch (SOSHibernateException e) {
                LOGGER.error(e.getMessage(), e);
            }
            Date now = Date.from(Instant.now());
            if(transferFromDb != null) {
                transferId = transferFromDb.getId();
                transferFromDb.setEnd(now);
                if (hasErrors == null) {
                    transferFromDb.setState(2);
                    transferFromDb.setErrorCode(null);
                    transferFromDb.setErrorMessage(null);
                } else if (!hasErrors) {
                    transferFromDb.setState(1);
                    transferFromDb.setErrorCode(null);
                    transferFromDb.setErrorMessage(null);
                } else {
                    transferFromDb.setState(3);
                    transferFromDb.setErrorCode(null);
                    transferFromDb.setErrorMessage(JobSchedulerException.LastErrorMessage);
                }
                transferFromDb.setTaskId(taskId);
                if(parentTransferId != null) {
                    transferFromDb.setParentTransferId(parentTransferId);
                }
                transferFromDb.setModified(now);
                try {
                    dbLayer.getSession().update(transferFromDb);
                    LOGGER.debug("transfer DB Item updated!");
                } catch (SOSHibernateException e) {
                    LOGGER.error("error occurred trying to update transfer DB Item!");
                    LOGGER.error(e.getMessage(), e);
                }
                transferDBItem = transferFromDb;
            } else {
                LOGGER.debug("processing new transfer data!");
                DBItemYadeTransfers newTransfer = new DBItemYadeTransfers();
                if (sourceProtocolDBItem != null) {
                    newTransfer.setSourceProtocolId(sourceProtocolDBItem.getId());
                }
                if (targetProtocolDBItem != null) {
                    newTransfer.setTargetProtocolId(targetProtocolDBItem.getId());
                }
                if (jumpProtocolDBItem != null) {
                    newTransfer.setJumpProtocolId(jumpProtocolDBItem.getId());
                }
                newTransfer.setMandator(yadeEngine.getOptions().getMandator().getValue());
                newTransfer.setOperation(getOperation(yadeEngine.getOptions().getOperation()));
                newTransfer.setStart(Date.from(Instant.now()));
                newTransfer.setEnd(null);
                newTransfer.setState(2);
                newTransfer.setErrorCode(null);
                newTransfer.setErrorMessage(null);
                newTransfer.setJobschedulerId(currentJobschedulerId);
                newTransfer.setJob(currentJob);
                newTransfer.setJobChain(currentJobChain);
                newTransfer.setJobChainNode(currentNodeName);
                newTransfer.setOrderId(currentOrderId);
                newTransfer.setTaskId(taskId);
                if (yadeEngine.getFileList() != null) {
                    newTransfer.setNumOfFiles(yadeEngine.getFileList().count());
                }
                if (yadeEngine.getOptions().getProfile() != null) {
                    newTransfer.setProfileName(yadeEngine.getOptions().getProfile().getValue());
                }
                if(parentTransferId != null) {
                    newTransfer.setParentTransferId(parentTransferId);
                }
                newTransfer.setModified(now);
                dbLayer.getSession().save(newTransfer);
                transferId = newTransfer.getId();
                transferDBItem = newTransfer;
            }
            dbSession.commit();
            LOGGER.debug("store transfer information finished!");
        } catch (SOSHibernateException e) {
            try {
                dbSession.rollback();
            } catch (SOSHibernateException e1) {}
            LOGGER.error(e.getMessage(), e);
        }
        return transferId;
    }
    
    public void storeInitialFilesInformationToDB(Long transferId, SOSHibernateSession dbSession, SOSFileList files) {
        Long fileSizeSum = 0L;
        if (files != null) {
            for (SOSFileListEntry fileEntry : files.getList()) {
                DBItemYadeFiles file = new DBItemYadeFiles();
                file.setTransferId(transferId);
                file.setSourcePath(fileEntry.getSourceFilename());
                file.setTargetPath(fileEntry.getTargetFileNameAndPath());
                file.setSize(fileEntry.getFileSize());
                fileSizeSum += fileEntry.getFileSize();
                file.setState(fileEntry.getStatus());
                String lastErrorMessage = fileEntry.getLastErrorMessage();
                if (lastErrorMessage != null && !lastErrorMessage.isEmpty()) {
                    file.setErrorCode("ERRORCODE");
                    file.setErrorMessage(lastErrorMessage);
                } else {
                    file.setErrorCode(null);
                    file.setErrorMessage(null);
                }
                file.setIntegrityHash(fileEntry.getMd5());
                file.setModificationDate(fileEntry.getModified());
                file.setModified(Date.from(Instant.now()));
                try {
                    dbSession.beginTransaction();
                    dbSession.save(file);
                    dbSession.commit();
                    LOGGER.debug("YADE_FILE stored in DB: " + file.getSourcePath());
                } catch (SOSHibernateException e) {
                    LOGGER.error(e.getMessage(), e);
                    try {
                        dbSession.rollback();
                    } catch (SOSHibernateException e1) {
                    }
                }
            }
            LOGGER.debug("store transfer files information finished!");
        }
        files.setSumOfFileSizes(fileSizeSum);
    }
    public void updateFileInformationToDB(SOSHibernateSession dbSession, SOSFileListEntry fileEntry) {
        updateFileInformationToDB(dbSession, fileEntry, false);
    }
    
    public void updateFileInformationToDB(SOSHibernateSession dbSession, SOSFileListEntry fileEntry, boolean finalUpdate) {
        YadeDBLayer dbLayer = new YadeDBLayer(dbSession);
        if (fileEntry != null) {
            DBItemYadeFiles fileFromDb = null;
            try {
                fileFromDb = dbLayer.getTransferFileFromDbByConstraint(transferDBItem.getId(), fileEntry.getSourceFilename());
            } catch (SOSHibernateException e) {
                LOGGER.error(e.getMessage(), e);
            }
            if (fileFromDb != null) {
                if (finalUpdate && fileEntry.getStatus() == 4) {
                    fileFromDb.setState(5);
                    hasErrors = false;
                } else if (finalUpdate && fileEntry.getStatus() == 6) {
                    fileFromDb.setState(7);
                } else if (finalUpdate && (fileEntry.getStatus() == 0 || fileEntry.getStatus() == 7)) {
                    fileFromDb.setState(8);
                } else {
                    hasErrors = getHasErrorsFromFileState(fileEntry.getStatus());
                    fileFromDb.setState(fileEntry.getStatus());
                }
                fileFromDb.setTargetPath(fileEntry.getTargetFileNameAndPath());
                String lastErrorMessage = fileEntry.getLastErrorMessage();
                if (lastErrorMessage != null && !lastErrorMessage.isEmpty()) {
                    fileFromDb.setErrorCode("ERRORCODE");
                    fileFromDb.setErrorMessage(lastErrorMessage);
                } else {
                    fileFromDb.setErrorCode(null);
                    fileFromDb.setErrorMessage(null);
                }
                fileFromDb.setIntegrityHash(fileEntry.getMd5());
                fileFromDb.setModificationDate(fileEntry.getModificationDate());
                fileFromDb.setModified(Date.from(Instant.now()));
                try {
                    dbSession.beginTransaction();
                    dbSession.update(fileFromDb);
                    dbSession.commit();
                    dbSession.refresh(fileFromDb);
                    if (finalUpdate && parentTransferId != null) {
                        DBItemYadeFiles intervenedFileFromDb = null;
                        intervenedFileFromDb = dbLayer.getTransferFileFromDbByConstraint(parentTransferId, fileEntry.getSourceFilename());
                        if (intervenedFileFromDb != null) {
                            intervenedFileFromDb.setInterventionTransferId(transferDBItem.getId());
                            dbSession.beginTransaction();
                            dbSession.update(intervenedFileFromDb);
                            dbSession.commit();
                        } else {
                            LOGGER.debug(String.format(
                                    "File entry with transfer id=%1$d and path=%2$s not updated with intervention id=%3$d due to item not found in DB!", 
                                    parentTransferId, fileEntry.getSourceFilename(), transferDBItem.getId()));
                        }
                    }
                } catch (SOSHibernateException e) {
                    LOGGER.error(e.getMessage(), e);
                    try {
                        dbSession.rollback();
                    } catch (SOSHibernateException e1) {
                    }
                }
            } else {
                DBItemYadeFiles file = new DBItemYadeFiles();
                file.setTransferId(transferDBItem.getId());
                file.setSourcePath(fileEntry.getSourceFilename());
                file.setTargetPath(fileEntry.getTargetFileNameAndPath());
                file.setSize(fileEntry.getFileSize());
                file.setState(fileEntry.getStatus());
                file.setModificationDate(fileEntry.getModified());
                String lastErrorMessage = fileEntry.getLastErrorMessage();
                if (lastErrorMessage != null && !lastErrorMessage.isEmpty()) {
                    file.setErrorCode("ERRORCODE");
                    file.setErrorMessage(lastErrorMessage);
                } else {
                    file.setErrorCode(null);
                    file.setErrorMessage(null);
                }
                file.setIntegrityHash(fileEntry.getMd5());
                file.setModificationDate(fileEntry.getModified());
                file.setModified(Date.from(Instant.now()));
                try {
                    dbSession.beginTransaction();
                    dbSession.save(file);
                    dbSession.commit();
                    LOGGER.debug("YADE_FILE stored in DB: " + file.getSourcePath());
                } catch (SOSHibernateException e) {
                    LOGGER.error(e.getMessage(), e);
                    try {
                        dbSession.rollback();
                    } catch (SOSHibernateException e1) {
                    }
                }
            }
            LOGGER.debug("store transfer files information finished!");
        }
    }

    public Long storeInitialTransferInformations(SOSHibernateSession dbSession) {
        return storeInitialTransferInformations(dbSession, null);
    }

    public Long storeInitialTransferInformations(SOSHibernateSession dbSession, Long parentTransferId) {
        Long transferId = null;
        if (dbSession != null) {
            transferId = storeTransferInformationToDB(dbSession, parentTransferId);
            LOGGER.debug("initial transfer information stored to DB!");
        }
        return transferId;
    }

    public void addAdditionalJobInfosFromOptions() {
        if (yadeEngine.getOptions().getJobSchedulerId() != null) {
            currentJobschedulerId = yadeEngine.getOptions().getJobSchedulerId();
        }
        if (yadeEngine.getOptions().getJobChain() != null) {
            currentJobChain = yadeEngine.getOptions().getJobChain();
        }
        if (yadeEngine.getOptions().getJob() != null) {
            currentJob = yadeEngine.getOptions().getJob();
        }
        if (yadeEngine.getOptions().getJobChainNodeName() != null) {
            currentNodeName = yadeEngine.getOptions().getJobChainNodeName();
        }
        if (yadeEngine.getOptions().getOrderId() != null) {
            currentOrderId = yadeEngine.getOptions().getOrderId();
        }
        if (yadeEngine.getOptions().getParentTransferId() != null) {
            parentTransferId = yadeEngine.getOptions().getParentTransferId();
        }
        if (yadeEngine.getOptions().getTaskId() != null) {
            String taskIdFromOptions = yadeEngine.getOptions().getTaskId();
            taskId = Long.parseLong(taskIdFromOptions);
        }
    }
    
    public void updateFailedTransfer(SOSHibernateSession dbSession, String errorMessage) throws SOSHibernateException {
        if (transferDBItem != null) {
            transferDBItem.setState(3);
            transferDBItem.setErrorMessage(errorMessage);
            transferDBItem.setEnd(Date.from(Instant.now()));
            transferDBItem.setModified(Date.from(Instant.now()));
            dbSession.beginTransaction();
            dbSession.update(transferDBItem);
            dbSession.commit();
        }        
    }
    
    public void updateSuccessfulTransfer(SOSHibernateSession dbSession) throws SOSHibernateException {
        if (transferDBItem != null) {
            transferDBItem.setState(1);
            transferDBItem.setEnd(Date.from(Instant.now()));
            transferDBItem.setModified(Date.from(Instant.now()));
            dbSession.beginTransaction();
            dbSession.update(transferDBItem);
            dbSession.commit();
        }        
    }
    
    public void updateTransfersNumOfFiles(SOSHibernateSession dbSession, Long numOfFiles) throws SOSHibernateException {
        if (transferDBItem != null) {
            transferDBItem.setNumOfFiles(numOfFiles);
            transferDBItem.setModified(Date.from(Instant.now()));
            dbSession.beginTransaction();
            dbSession.update(transferDBItem);
            dbSession.commit();
        }
    }
    
    public DBItemYadeTransfers getTransfer(Long id, SOSHibernateSession dbSession) throws SOSHibernateException {
        YadeDBLayer dbLayer = new YadeDBLayer(dbSession);
        return dbLayer.getTransferFromDb(id);
    }
    
    private Integer getOperation(SOSOptionJadeOperation jadeOperation) {
        switch(jadeOperation.value()) {
        case copy:
            return 1;
        case move:
            return 2;
        case getlist:
            return 3;
        case rename:
            return 4;
        case copytointernet:
        case sendusingdmz:
            return 5;
        case copyfrominternet:
        case receiveusingdmz:
            return 6;
        default:
            return 0;    
        }
    }
    
    private Integer getProtocolFromTransferType(SOSOptionTransferType.enuTransferTypes transferType) {
        switch(transferType) {
        case local:
            return 1;
        case ftp:
            return 2;
        case ftps:
            return 3;
        case sftp:
            return 4;
        case http:
            return 5;
        case https:
            return 6;
        case webdav:
            return 7;
        case smb:
            return 9;
        default:
            return null;
        }
    }
    
    private Integer getProtocolFromString (String protocolName) {
        switch(protocolName.toLowerCase()) {
        case "local":
            return 1;
        case "ftp":
            return 2;
        case "ftps":
            return 3;
        case "sftp":
            return 4;
        case "http":
            return 5;
        case "https":
            return 6;
        case "webdav":
            return 7;
        case "webdavs":
            return 8;
        case "smb":
            return 9;
        default:
            return null;
        }
    }

    private Boolean getHasErrorsFromFileState(Integer fileEntryState) {
        switch(fileEntryState) {
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 8:
        case 9:
        case 11:
        case 12:
        case 13:
        case 14:
        case 15:
            return false;
        case 6:
        case 7:
        case 10:
            return true;
        }
        return null;
    }

    public DBItemYadeTransfers getTransferDBItem() {
        return transferDBItem;
    }

    public void setTransferDBItem(DBItemYadeTransfers transferDBItem) {
        this.transferDBItem = transferDBItem;
    }

    public SOSDataExchangeEngine getYadeEngine() {
        return yadeEngine;
    }

    public void setYadeEngine(SOSDataExchangeEngine yadeEngine) {
        this.yadeEngine = yadeEngine;
    }

    public void setParentTransferId(Long parentTransferId) {
        this.parentTransferId = parentTransferId;
    }

}