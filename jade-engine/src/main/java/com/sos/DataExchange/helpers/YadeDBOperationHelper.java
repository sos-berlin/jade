package com.sos.DataExchange.helpers;

import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.DataExchange.Jade4DMZ;
import com.sos.DataExchange.SOSDataExchangeEngine;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.JSHelper.Options.SOSOptionJadeOperation;
import com.sos.JSHelper.Options.SOSOptionTransferType;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;
import com.sos.VirtualFileSystem.DataElements.SOSFileList;
import com.sos.VirtualFileSystem.DataElements.SOSFileListEntry;
import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
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
    private Jade4DMZ yadeDMZEngine;
    private Long parentTransferId = null;
    private Long taskId = null;
    private Boolean hasErrors = null; 
    
    public YadeDBOperationHelper(SOSDataExchangeEngine yadeEngine) {
        this.yadeEngine = yadeEngine;
        addAdditionalJobInfosFromOptions();
    }

    public YadeDBOperationHelper(Jade4DMZ yadeDMZEngine) {
        this.yadeDMZEngine = yadeDMZEngine;
        addAdditionalJobInfosFromOptions();
    }

    public Long storeYadeTransferInformationToDB(SOSHibernateSession dbSession, Long parentTransferId) {
        this.parentTransferId = parentTransferId;
        Long transferId = null;
        YadeDBLayer dbLayer = new YadeDBLayer(dbSession);
        SOSConnection2OptionsAlternate sourceOptions = yadeEngine.getOptions().getSource();
        SOSConnection2OptionsAlternate targetOptions = yadeEngine.getOptions().getTarget();
        try {
            if (sourceProtocolDBItem == null && yadeEngine.getOptions().sourceDir.isDirty()) {
                dbSession.beginTransaction();
                sourceProtocolDBItem = new DBItemYadeProtocols();
                sourceProtocolDBItem.setHostname(sourceOptions.host.getValue());
                sourceProtocolDBItem.setPort(sourceOptions.port.value());
                Integer sourceProtocol = getProtocolFromTransferType(sourceOptions.protocol.getEnum());
                if (sourceOptions.protocol.getEnum() == enuTransferTypes.local) {
                    sourceProtocolDBItem.setPort(0);
                }
                if (sourceOptions.protocol.getEnum() == enuTransferTypes.webdav) {
                    URL url = sourceOptions.url.getUrl();
                    if (url.getProtocol().toLowerCase().equals("webdavs")) {
                        sourceProtocolDBItem.setProtocol(8);
                    } else {
                        sourceProtocolDBItem.setProtocol(sourceProtocol);
                    }
                } else {
                    sourceProtocolDBItem.setProtocol(sourceProtocol);
                }
                sourceProtocolDBItem.setAccount(sourceOptions.user.getValue());
                DBItemYadeProtocols sourceProtocolFromDb = null;
                try {
                    sourceProtocolFromDb = dbLayer.getProtocolFromDb(sourceProtocolDBItem.getHostname(),
                            sourceProtocolDBItem.getPort(), sourceProtocolDBItem.getProtocol(), sourceProtocolDBItem.getAccount());
                } catch (SOSHibernateException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                if (sourceProtocolFromDb != null) {
                    sourceProtocolDBItem = sourceProtocolFromDb;
                } else {
                    try {
                        dbLayer.getSession().save(sourceProtocolDBItem);
                    } catch (SOSHibernateException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
                dbSession.commit();
            } else {
                
            }
            
            if (targetProtocolDBItem == null && yadeEngine.getOptions().targetDir.isDirty()) {
                dbSession.beginTransaction();
                targetProtocolDBItem = new DBItemYadeProtocols();
                targetProtocolDBItem.setHostname(targetOptions.host.getValue());
                targetProtocolDBItem.setPort(targetOptions.port.value());
                Integer targetProtocol = getProtocolFromTransferType(targetOptions.protocol.getEnum());
                if (targetOptions.protocol.getEnum() == enuTransferTypes.local) {
                    targetProtocolDBItem.setPort(0);
                }
                if (targetOptions.protocol.getEnum() == enuTransferTypes.webdav) {
                    URL url = targetOptions.url.getUrl();
                    if (url.getProtocol().toLowerCase().equals("webdavs")) {
                        targetProtocolDBItem.setProtocol(8);
                    } else {
                        targetProtocolDBItem.setProtocol(targetProtocol);
                    }
                } else {
                    targetProtocolDBItem.setProtocol(targetProtocol);
                }
                targetProtocolDBItem.setAccount(targetOptions.user.getValue());
                DBItemYadeProtocols targetProtocolFromDb = null;
                try {
                    targetProtocolFromDb = dbLayer.getProtocolFromDb(targetProtocolDBItem.getHostname(),
                            targetProtocolDBItem.getPort(), targetProtocolDBItem.getProtocol(), targetProtocolDBItem.getAccount());
                } catch (SOSHibernateException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                if (targetProtocolFromDb != null) {
                    targetProtocolDBItem = targetProtocolFromDb;
                } else {
                    try {
                        dbLayer.getSession().save(targetProtocolDBItem);
                    } catch (SOSHibernateException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
                dbSession.commit();
            }
            
            if (jumpProtocolDBItem == null && yadeEngine.getOptions().jumpHost.isDirty()) { 
                dbSession.beginTransaction();
                jumpProtocolDBItem = new DBItemYadeProtocols();
                jumpProtocolDBItem.setHostname(yadeEngine.getOptions().jumpHost.getValue());
                jumpProtocolDBItem.setPort(yadeEngine.getOptions().jumpPort.value());
                jumpProtocolDBItem.setProtocol(getProtocolFromString(yadeEngine.getOptions().jumpProtocol.getValue()));
                jumpProtocolDBItem.setAccount(yadeEngine.getOptions().jumpUser.getValue());
                DBItemYadeProtocols jumpProtocolFromDb = null;
                try {
                    jumpProtocolFromDb = dbLayer.getProtocolFromDb(jumpProtocolDBItem.getHostname(),
                            jumpProtocolDBItem.getPort(), jumpProtocolDBItem.getProtocol(), jumpProtocolDBItem.getAccount());
                } catch (SOSHibernateException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                if (jumpProtocolFromDb != null) {
                    jumpProtocolDBItem = jumpProtocolFromDb;
                } else {
                    try {
                        dbLayer.getSession().save(jumpProtocolDBItem);
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
            if(transferFromDb != null) {
                transferId = transferFromDb.getId();
                transferFromDb.setEnd(getUTCDateFromInstant(Instant.now()));
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
                if (yadeEngine.getFileList() != null) {
                    transferFromDb.setNumOfFiles(yadeEngine.getFileList().count());
                } else {
                    transferFromDb.setNumOfFiles(0L);
                }
                transferFromDb.setTaskId(taskId);
                if(parentTransferId != null) {
                    transferFromDb.setParentTransferId(parentTransferId);
                }
                transferFromDb.setModified(getUTCDateFromInstant(Instant.now()));
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
                newTransfer.setMandator(yadeEngine.getOptions().mandator.getValue());
                newTransfer.setOperation(getOperation(yadeEngine.getOptions().operation));
                newTransfer.setStart(getUTCDateFromInstant(Instant.now()));
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
                } else {
                    newTransfer.setNumOfFiles(0L);
                }
                if (yadeEngine.getOptions().getProfile() != null) {
                    newTransfer.setProfileName(yadeEngine.getOptions().profile.getValue());
                }
                if(parentTransferId != null) {
                    newTransfer.setParentTransferId(parentTransferId);
                }
                newTransfer.setModified(getUTCDateFromInstant(Instant.now()));
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
    
    public Long storeYadeDMZTransferInformationToDB(SOSHibernateSession dbSession, Long parentTransferId) {
        this.parentTransferId = parentTransferId;
        Long transferId = null;
        YadeDBLayer dbLayer = new YadeDBLayer(dbSession);
        SOSConnection2OptionsAlternate sourceOptions = yadeDMZEngine.getOptions().getSource();
        SOSConnection2OptionsAlternate targetOptions = yadeDMZEngine.getOptions().getTarget();
        try {
            if (sourceProtocolDBItem == null && yadeDMZEngine.getOptions().sourceDir.isDirty()) {
                dbSession.beginTransaction();
                sourceProtocolDBItem = new DBItemYadeProtocols();
                sourceProtocolDBItem.setHostname(sourceOptions.host.getValue());
                sourceProtocolDBItem.setPort(sourceOptions.port.value());
                Integer sourceProtocol = getProtocolFromTransferType(sourceOptions.protocol.getEnum());
                if (sourceOptions.protocol.getEnum() == enuTransferTypes.local) {
                    sourceProtocolDBItem.setPort(0);
                }
                if (sourceOptions.protocol.getEnum() == enuTransferTypes.webdav) {
                    URL url = sourceOptions.url.getUrl();
                    if (url.getProtocol().toLowerCase().equals("webdavs")) {
                        sourceProtocolDBItem.setProtocol(8);
                    } else {
                        sourceProtocolDBItem.setProtocol(sourceProtocol);
                    }
                } else {
                    sourceProtocolDBItem.setProtocol(sourceProtocol);
                }
                sourceProtocolDBItem.setAccount(sourceOptions.user.getValue());
                DBItemYadeProtocols sourceProtocolFromDb = null;
                try {
                    sourceProtocolFromDb = dbLayer.getProtocolFromDb(sourceProtocolDBItem.getHostname(),
                            sourceProtocolDBItem.getPort(), sourceProtocolDBItem.getProtocol(), sourceProtocolDBItem.getAccount());
                } catch (SOSHibernateException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                if (sourceProtocolFromDb != null) {
                    sourceProtocolDBItem = sourceProtocolFromDb;
                } else {
                    try {
                        dbLayer.getSession().save(sourceProtocolDBItem);
                    } catch (SOSHibernateException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
                dbSession.commit();
            }
            
            if (targetProtocolDBItem == null && yadeDMZEngine.getOptions().targetDir.isDirty()) {
                dbSession.beginTransaction();
                targetProtocolDBItem = new DBItemYadeProtocols();
                targetProtocolDBItem.setHostname(targetOptions.host.getValue());
                targetProtocolDBItem.setPort(targetOptions.port.value());
                Integer targetProtocol = getProtocolFromTransferType(targetOptions.protocol.getEnum());
                if (targetOptions.protocol.getEnum() == enuTransferTypes.local) {
                    targetProtocolDBItem.setPort(0);
                }
                if (targetOptions.protocol.getEnum() == enuTransferTypes.webdav) {
                    URL url = targetOptions.url.getUrl();
                    if (url.getProtocol().toLowerCase().equals("webdavs")) {
                        targetProtocolDBItem.setProtocol(8);
                    } else {
                        targetProtocolDBItem.setProtocol(targetProtocol);
                    }
                } else {
                    targetProtocolDBItem.setProtocol(targetProtocol);
                }
                targetProtocolDBItem.setAccount(yadeDMZEngine.getOptions().getTarget().user.getValue());
                DBItemYadeProtocols targetProtocolFromDb = null;
                try {
                    targetProtocolFromDb = dbLayer.getProtocolFromDb(targetProtocolDBItem.getHostname(),
                            targetProtocolDBItem.getPort(), targetProtocolDBItem.getProtocol(), targetProtocolDBItem.getAccount());
                } catch (SOSHibernateException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                if (targetProtocolFromDb != null) {
                    targetProtocolDBItem = targetProtocolFromDb;
                } else {
                    try {
                        dbLayer.getSession().save(targetProtocolDBItem);
                    } catch (SOSHibernateException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
                dbSession.commit();
            }
            
            if (jumpProtocolDBItem == null && yadeDMZEngine.getOptions().jumpHost.isDirty()) { 
                dbSession.beginTransaction();
                jumpProtocolDBItem = new DBItemYadeProtocols();
                jumpProtocolDBItem.setHostname(yadeDMZEngine.getOptions().jumpHost.getValue());
                jumpProtocolDBItem.setPort(yadeDMZEngine.getOptions().jumpPort.value());
                jumpProtocolDBItem.setProtocol(getProtocolFromString(yadeDMZEngine.getOptions().jumpProtocol.getValue()));
                jumpProtocolDBItem.setAccount(yadeDMZEngine.getOptions().jumpUser.getValue());
                DBItemYadeProtocols jumpProtocolFromDb = null;
                try {
                    jumpProtocolFromDb = dbLayer.getProtocolFromDb(jumpProtocolDBItem.getHostname(),
                            jumpProtocolDBItem.getPort(), jumpProtocolDBItem.getProtocol(), jumpProtocolDBItem.getAccount());
                } catch (SOSHibernateException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                if (jumpProtocolFromDb != null) {
                    jumpProtocolDBItem = jumpProtocolFromDb;
                } else {
                    try {
                        dbLayer.getSession().save(jumpProtocolDBItem);
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
            if(transferFromDb != null) {
                transferId = transferFromDb.getId();
                transferFromDb.setEnd(getUTCDateFromInstant(Instant.now()));
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
                if (yadeDMZEngine.getFileList() != null) {
                    transferFromDb.setNumOfFiles(yadeDMZEngine.getFileList().count());
                } else {
                    transferFromDb.setNumOfFiles(0L);
                }
                transferFromDb.setTaskId(taskId);
                if(parentTransferId != null) {
                    transferFromDb.setParentTransferId(parentTransferId);
                }
                transferFromDb.setModified(getUTCDateFromInstant(Instant.now()));
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
                newTransfer.setMandator(yadeDMZEngine.getOptions().mandator.getValue());
                newTransfer.setOperation(getOperation(yadeDMZEngine.getOptions().operation));
                newTransfer.setStart(getUTCDateFromInstant(Instant.now()));
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
                if (yadeDMZEngine.getFileList() != null) {
                    newTransfer.setNumOfFiles(yadeDMZEngine.getFileList().count());
                } else {
                    newTransfer.setNumOfFiles(0L);
                }
                if (yadeDMZEngine.getOptions().getProfile() != null) {
                    newTransfer.setProfileName(yadeDMZEngine.getOptions().profile.getValue());
                }
                if(parentTransferId != null) {
                    newTransfer.setParentTransferId(parentTransferId);
                }
                newTransfer.setModified(getUTCDateFromInstant(Instant.now()));
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
        if (files != null && files.getList() != null && !files.getList().isEmpty()) {
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
                file.setModified(getUTCDateFromInstant(Instant.now()));
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
                if (finalUpdate && (fileEntry.getStatus() == 0 || fileEntry.getStatus() == 7)) {
                    fileFromDb.setState(8);
                } else if (finalUpdate) {
                     fileFromDb.setState(fileEntry.getStatus() + 1);
                }
                if (finalUpdate && fileFromDb.getState() != 7 && fileFromDb.getState() != 8 && fileFromDb.getState() != 14) {
                    fileFromDb.setTargetPath(fileEntry.getTargetFileNameAndPath());
                } else {
                    fileFromDb.setTargetPath(null);
                }
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
                fileFromDb.setModified(getUTCDateFromInstant(Instant.now()));
                try {
                    dbSession.beginTransaction();
                    dbSession.update(fileFromDb);
                    dbSession.commit();
                    dbSession.refresh(fileFromDb);
                    if (finalUpdate && parentTransferId != null) {
                        DBItemYadeFiles intervenedFileFromDb = null;
                        intervenedFileFromDb = 
                                dbLayer.getTransferFileFromDbByConstraint(parentTransferId, fileEntry.getSourceFilename());
                        if (intervenedFileFromDb != null) {
                            LOGGER.debug(String.format(
                                    "File entry with transfer id = %1$d to update with intervention id = %2$d found in DB!"),
                                    parentTransferId, transferDBItem.getId());
                            intervenedFileFromDb.setInterventionTransferId(transferDBItem.getId());
                            dbSession.beginTransaction();
                            dbSession.update(intervenedFileFromDb);
                            dbSession.commit();
                        } else {
                            LOGGER.debug(String.format(
                                    "File entry with transfer id=%1$d and path=%2$s not updated with intervention id=%3$d "
                                    + "due to item not found in DB!", 
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
                if (finalUpdate && (fileEntry.getStatus() == 0 || fileEntry.getStatus() == 7)) {
                    file.setState(8);
                } else if (finalUpdate) {
                     file.setState(fileEntry.getStatus() + 1);
                }
                if (finalUpdate && file.getState() != 7 && file.getState() != 8 && file.getState() != 14) {
                    file.setTargetPath(fileEntry.getTargetFileNameAndPath());
                } else {
                    file.setTargetPath(null);
                }
                file.setSize(fileEntry.getFileSize());
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
                file.setModified(getUTCDateFromInstant(Instant.now()));
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
        this.parentTransferId = parentTransferId;
        Long transferId = null;
        if (dbSession != null) {
            if (yadeEngine != null) {
                transferId = storeYadeTransferInformationToDB(dbSession, parentTransferId);
            } else if (yadeDMZEngine != null) {
                transferId = storeYadeDMZTransferInformationToDB(dbSession, parentTransferId);
            }
            LOGGER.debug("initial transfer information stored to DB!");
        }
        return transferId;
    }

    public void addAdditionalJobInfosFromOptions() {
        if (yadeEngine != null) {
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
        } else if (yadeDMZEngine != null) {
            if (yadeDMZEngine.getOptions().getJobSchedulerId() != null) {
                currentJobschedulerId = yadeDMZEngine.getOptions().getJobSchedulerId();
            }
            if (yadeDMZEngine.getOptions().getJobChain() != null) {
                currentJobChain = yadeDMZEngine.getOptions().getJobChain();
            }
            if (yadeDMZEngine.getOptions().getJob() != null) {
                currentJob = yadeDMZEngine.getOptions().getJob();
            }
            if (yadeDMZEngine.getOptions().getJobChainNodeName() != null) {
                currentNodeName = yadeDMZEngine.getOptions().getJobChainNodeName();
            }
            if (yadeDMZEngine.getOptions().getOrderId() != null) {
                currentOrderId = yadeDMZEngine.getOptions().getOrderId();
            }
            if (yadeDMZEngine.getOptions().getParentTransferId() != null) {
                parentTransferId = yadeDMZEngine.getOptions().getParentTransferId();
            }
            if (yadeDMZEngine.getOptions().getTaskId() != null) {
                String taskIdFromOptions = yadeDMZEngine.getOptions().getTaskId();
                taskId = Long.parseLong(taskIdFromOptions);
            }
        }
    }
    
    public void updateFailedTransfer(SOSHibernateSession dbSession, String errorMessage) throws SOSHibernateException {
        if (transferDBItem != null) {
            transferDBItem.setState(3);
            transferDBItem.setErrorMessage(errorMessage);
            transferDBItem.setEnd(getUTCDateFromInstant(Instant.now()));
            transferDBItem.setModified(getUTCDateFromInstant(Instant.now()));
            dbSession.beginTransaction();
            dbSession.update(transferDBItem);
            dbSession.commit();
        }        
    }
    
    public void updateSuccessfulTransfer(SOSHibernateSession dbSession) throws SOSHibernateException {
        if (transferDBItem != null) {
            transferDBItem.setState(1);
            transferDBItem.setEnd(getUTCDateFromInstant(Instant.now()));
            transferDBItem.setModified(getUTCDateFromInstant(Instant.now()));
            dbSession.beginTransaction();
            dbSession.update(transferDBItem);
            dbSession.commit();
        }        
    }
    
    public void updateTransfersNumOfFiles(SOSHibernateSession dbSession, Long numOfFiles) throws SOSHibernateException {
        if (transferDBItem != null) {
            transferDBItem.setNumOfFiles(numOfFiles);
            transferDBItem.setModified(getUTCDateFromInstant(Instant.now()));
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
        case 15:
            return false;
        case 6:
        case 7:
        case 10:
        case 14:
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
    
    public Jade4DMZ getYadeDMZEngine() {
        return yadeDMZEngine;
    }
    
    public void setYadeDMZEngine(Jade4DMZ yadeDMZEngine) {
        this.yadeDMZEngine = yadeDMZEngine;
    }

    public void setParentTransferId(Long parentTransferId) {
        this.parentTransferId = parentTransferId;
    }
    
    private Date getUTCDateFromInstant(Instant inDate) {
        return new Date(inDate.toEpochMilli() - TimeZone.getDefault().getOffset(inDate.toEpochMilli()));
    }
}