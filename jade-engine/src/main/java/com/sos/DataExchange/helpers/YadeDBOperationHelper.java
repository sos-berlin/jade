package com.sos.DataExchange.helpers;

import static com.sos.DataExchange.SOSJadeMessageCodes.SOSJADE_I_0100;
import static com.sos.DataExchange.SOSJadeMessageCodes.SOSJADE_I_0101;
import static com.sos.DataExchange.SOSJadeMessageCodes.SOSJADE_I_0102;
import static com.sos.DataExchange.SOSJadeMessageCodes.SOSJADE_I_0103;
import static com.sos.DataExchange.SOSJadeMessageCodes.SOSJADE_T_0012;
import static com.sos.DataExchange.SOSJadeMessageCodes.SOSJADE_T_0013;

import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

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
//    private JadeEngine yadeEngine;
    private SOSDataExchangeEngine yadeEngine;
    
    public YadeDBOperationHelper(SOSDataExchangeEngine yadeEngine) {
        this.yadeEngine = yadeEngine;
    }

    public Long storeTransferInformationToDB(UUID uuid, SOSHibernateSession dbSession) {
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
                LOGGER.info("source Host = " + yadeEngine.getOptions().getSource().host.getValue());
                LOGGER.info("source port = " + yadeEngine.getOptions().getSource().getPort().value());
                LOGGER.info("source protocol = " + yadeEngine.getOptions().getSource().getProtocol().getEnum().getText());
                LOGGER.info("source account = " + yadeEngine.getOptions().getSource().user.getValue());
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
                        LOGGER.info("source protocol id = " + sourceProtocolId);
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
                LOGGER.info("target Host = " + yadeEngine.getOptions().getTarget().host.getValue());
                LOGGER.info("target port = " + yadeEngine.getOptions().getTarget().getPort().value());
                LOGGER.info("target protocol = " + yadeEngine.getOptions().getTarget().getProtocol().getEnum().getText());
                LOGGER.info("target account = " + yadeEngine.getOptions().getTarget().user.getValue());
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
                        LOGGER.info("target protocol id = " + targetProtocolId);
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
                LOGGER.info("jump Host = " + yadeEngine.getOptions().getJumpHost().getValue());
                LOGGER.info("jump port = " + yadeEngine.getOptions().getJumpPort().value());
                LOGGER.info("jump protocol = " + yadeEngine.getOptions().getJumpProtocol().getValue());
                LOGGER.info("jump account = " + yadeEngine.getOptions().getJumpUser().getValue());
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
                        LOGGER.info("jump protocol id = " + jumpProtocolId);
                    } catch (SOSHibernateException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
                dbSession.commit();
            }

            dbSession.beginTransaction();
            DBItemYadeTransfers transferFromDb = null;
            try {
                transferFromDb = dbLayer.getTransferFromDb(uuid.toString());
                if (transferFromDb != null) {
                    LOGGER.info(String.format("transfer item with id = %1$s found in DB!", transferDBItem.getId()));
                }
            } catch (SOSHibernateException e) {
                LOGGER.error(e.getMessage(), e);
            }
            Date now = Date.from(Instant.now());
            if(transferFromDb != null) {
                transferId = transferFromDb.getId();
                transferFromDb.setEnd(now);
                if (SOSJADE_T_0012.get().equals(yadeEngine.getState()) ||
                        SOSJADE_I_0100.get().equals(yadeEngine.getState()) ||
                        SOSJADE_I_0101.get().equals(yadeEngine.getState())) {
//                    SOSJADE_T_0012=Ohne Fehler.
//                    SOSJADE_I_0100 = Es wurde eine Datei übertragen
//                    SOSJADE_I_0101 = Es wurden %1$d Dateien übertragen
                    transferFromDb.setState(1);
                    transferFromDb.setErrorCode(null);
                    transferFromDb.setErrorMessage(null);
                } else if (SOSJADE_T_0013.get().equals(yadeEngine.getState()) ||
                        SOSJADE_I_0102.get().equals(yadeEngine.getState()) ||
                        SOSJADE_I_0103.get().equals(yadeEngine.getState())) {
//                    SOSJADE_T_0013=Fehlerhaft.
//                    SOSJADE_I_0104 = Es wurden leere Dateien mit der Größe 0 bytes gefunden 
//                    SOSJADE_I_0102 = %1$d Dateien wurden nicht übertragen, weil sie leer sind (Größe <= 0 bytes)
//                    SOSJADE_I_0103 = %1$d Dateien wurden wegen skip_transfer oder overwrite_files Parameter nicht übertragen
                    transferFromDb.setState(3);
                    transferFromDb.setErrorCode(null);
                    transferFromDb.setErrorMessage(JobSchedulerException.LastErrorMessage);
                }
                transferFromDb.setLog("???");
                transferFromDb.setModified(now);
                try {
                    dbLayer.getSession().update(transferFromDb);
                    LOGGER.info("transfer DB Item updated!");
                } catch (SOSHibernateException e) {
                    LOGGER.info("error occurred trying to update transfer DB Item!");
                    LOGGER.error(e.getMessage(), e);
                }
                transferDBItem = transferFromDb;
            } else {
                LOGGER.info("processing new transfer data!");
                DBItemYadeTransfers newTransfer = new DBItemYadeTransfers();
                if (sourceProtocolDBItem != null) {
                    newTransfer.setSourceProtocolId(sourceProtocolDBItem.getId());
                    LOGGER.info("source protocol id = " + sourceProtocolDBItem.getId());
                }
                if (targetProtocolDBItem != null) {
                    newTransfer.setTargetProtocolId(targetProtocolDBItem.getId());
                    LOGGER.info("target protocol id = " + targetProtocolDBItem.getId());
                }
                if (jumpProtocolDBItem != null) {
                    newTransfer.setJumpProtocolId(jumpProtocolDBItem.getId());
                    LOGGER.info("jump protocol id = " + jumpProtocolDBItem.getId());
                }
                newTransfer.setMandator(yadeEngine.getOptions().getMandator().getValue());
                LOGGER.info("mandator = " + newTransfer.getMandator());
                newTransfer.setOperation(getOperation(yadeEngine.getOptions().getOperation()));
                LOGGER.info("operation = " + newTransfer.getOperation());
                newTransfer.setStart(Date.from(Instant.now()));
                newTransfer.setEnd(null);
                newTransfer.setState(2);
                LOGGER.info("state = " + newTransfer.getState());
                newTransfer.setErrorCode(null);
                newTransfer.setErrorMessage(null);
                newTransfer.setLog(null);
                newTransfer.setJobschedulerId(currentJobschedulerId);
                LOGGER.info("jobschedulerId = " + currentJobschedulerId);
                newTransfer.setJob(currentJob);
                LOGGER.info("job = " + currentJob);
                newTransfer.setJobChain(currentJobChain);
                LOGGER.info("job chain = " + currentJobChain);
                newTransfer.setJobChainNode(currentNodeName);
                LOGGER.info("current job chain node = " + currentNodeName);
                newTransfer.setOrderId(currentOrderId);
                LOGGER.info("orderId = " + currentOrderId);
                if (yadeEngine.getFileList() != null) {
                    newTransfer.setNumOfFiles(yadeEngine.getFileList().count());
                    LOGGER.info("Num of Files = " + yadeEngine.getFileList().count());
                }
                if (yadeEngine.getOptions().getProfile() != null) {
                    newTransfer.setProfileName(yadeEngine.getOptions().getProfile().getValue());
                    LOGGER.info("profile = " + yadeEngine.getOptions().getProfile().getValue());
                }
                newTransfer.setModified(now);
                newTransfer.setUuid(uuid.toString());
                LOGGER.info("UUID = " + uuid.toString());
                dbLayer.getSession().save(newTransfer);
                transferId = newTransfer.getId();
                transferDBItem = newTransfer;
            }
            LOGGER.info("transfer id = " + transferId);
            dbSession.commit();
        } catch (SOSHibernateException e) {
            LOGGER.error("trying db rollback");
            try {
                dbSession.rollback();
            } catch (SOSHibernateException e1) {}
            LOGGER.error(e.getMessage(), e);
        }
        LOGGER.info("store transfer information finished!");
        return transferId;
    }
    
    public void storeFilesInformationToDB(Long transferId, SOSHibernateSession dbSession) {
        SOSFileList files = yadeEngine.getFileList();
        if (files != null) {
            // TODO: implementation
            for (SOSFileListEntry fileEntry : files.getList()) {
                DBItemYadeFiles file = new DBItemYadeFiles();
                file.setTransferId(transferId);
                file.setSourcePath(fileEntry.getSourceFilename());
                file.setTargetPath(fileEntry.getTargetFilename());
                file.setSize(fileEntry.getFileSize());
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
                    LOGGER.info("file saved: " + file.getSourcePath());
                } catch (SOSHibernateException e) {
                    LOGGER.error(e.getMessage(), e);
                    try {
                        dbSession.rollback();
                    } catch (SOSHibernateException e1) {
                    }
                }
            }
            LOGGER.info("store transfer files information finished!");
        }
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

    public String getCurrentNodeName() {
        return currentNodeName;
    }

    public void setCurrentNodeName(String currentNodeName) {
        this.currentNodeName = currentNodeName;
    }

    public String getCurrentJobChain() {
        return currentJobChain;
    }

    public void setCurrentJobChain(String currentJobChain) {
        this.currentJobChain = currentJobChain;
    }

    public String getCurrentJob() {
        return currentJob;
    }
    
    public void setCurrentJob(String currentJob) {
        this.currentJob = currentJob;
    }

    public String getCurrentOrderId() {
        return currentOrderId;
    }

    public void setCurrentOrderId(String currentOrderId) {
        this.currentOrderId = currentOrderId;
    }

    public String getCurrentJobschedulerId() {
        return currentJobschedulerId;
    }
    
    public void setCurrentJobschedulerId(String currentJobschedulerId) {
        this.currentJobschedulerId = currentJobschedulerId;
    }

    public DBItemYadeProtocols getSourceProtocolDBItem() {
        return sourceProtocolDBItem;
    }

    public void setSourceProtocolDBItem(DBItemYadeProtocols sourceProtocolDBItem) {
        this.sourceProtocolDBItem = sourceProtocolDBItem;
    }

    public DBItemYadeProtocols getTargetProtocolDBItem() {
        return targetProtocolDBItem;
    }

    public void setTargetProtocolDBItem(DBItemYadeProtocols targetProtocolDBItem) {
        this.targetProtocolDBItem = targetProtocolDBItem;
    }

    public DBItemYadeProtocols getJumpProtocolDBItem() {
        return jumpProtocolDBItem;
    }

    public void setJumpProtocolDBItem(DBItemYadeProtocols jumpProtocolDBItem) {
        this.jumpProtocolDBItem = jumpProtocolDBItem;
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

}