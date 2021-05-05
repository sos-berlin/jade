package com.sos.DataExchange.history;

import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.JSHelper.Options.SOSOptionJadeOperation;
import com.sos.JSHelper.Options.SOSOptionTransferType.TransferTypes;
import com.sos.JSHelper.interfaces.IJobSchedulerEventHandler;
import com.sos.hibernate.classes.SOSHibernateSession;
import com.sos.hibernate.exceptions.SOSHibernateException;
import com.sos.jade.db.DBItemYadeFiles;
import com.sos.jade.db.DBItemYadeProtocols;
import com.sos.jade.db.DBItemYadeTransfers;
import com.sos.jade.db.YadeDBLayer;
import com.sos.vfs.common.SOSFileList;
import com.sos.vfs.common.SOSFileListEntry;
import com.sos.vfs.common.options.SOSBaseOptions;
import com.sos.vfs.common.options.SOSProviderOptions;

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
    private SOSBaseOptions options;
    private Long parentTransferId = null;
    private Long taskId = null;
    private Boolean hasErrors = null;
    private IJobSchedulerEventHandler eventHandler = null;

    public YadeDBOperationHelper(SOSBaseOptions opt, IJobSchedulerEventHandler eventHandler) {
        this.options = opt;
        this.eventHandler = eventHandler;
        addAdditionalJobInfosFromOptions();
    }

    private URL getURL(SOSProviderOptions options) {
        URL url = options.url.getUrl();
        if (url == null) {
            try {
                url = new URL(options.host.getValue());
            } catch (Throwable e) {
                LOGGER.error(e.toString(), e);
            }
        }
        return url;
    }

    public Long storeYadeTransferInformationToDB(SOSFileList fileList, SOSHibernateSession dbSession, Long parentTransferId) throws Exception {
        this.parentTransferId = parentTransferId;
        Long transferId = null;
        YadeDBLayer dbLayer = null;
        if (dbSession != null) {
            dbLayer = new YadeDBLayer(dbSession);
            SOSProviderOptions sourceOptions = options.getSource();
            SOSProviderOptions targetOptions = options.getTarget();
            if (sourceProtocolDBItem == null && options.sourceDir.isDirty()) {
                TransferTypes transferType = sourceOptions.protocol.getEnum();
                sourceProtocolDBItem = new DBItemYadeProtocols();
                sourceProtocolDBItem.setHostname(sourceOptions.host.getValue());
                sourceProtocolDBItem.setPort(sourceOptions.port.value());
                sourceProtocolDBItem.setProtocol(transferType.numeric());

                if (transferType.equals(TransferTypes.local)) {
                    sourceProtocolDBItem.setPort(0);
                } else if (transferType.equals(TransferTypes.webdav)) {
                    URL url = getURL(sourceOptions);
                    if (url != null && url.getProtocol().toLowerCase().equals(TransferTypes.webdavs.name()) || url.getProtocol().toLowerCase().equals(
                            TransferTypes.https.name())) {
                        sourceProtocolDBItem.setProtocol(TransferTypes.webdavs.numeric());
                    }
                } else if (transferType.equals(TransferTypes.http)) {
                    URL url = getURL(sourceOptions);
                    if (url != null && url.getProtocol().toLowerCase().equals(TransferTypes.https.name())) {
                        sourceProtocolDBItem.setProtocol(TransferTypes.https.numeric());
                    }
                }
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace(String.format("[source][transferType %s=%s]sourceProtocol=%s", transferType.name(), transferType.numeric(),
                            sourceProtocolDBItem.getProtocol()));
                }

                if (sourceOptions.user.isDirty() && sourceOptions.user.getValue() != null && sourceOptions.user.isNotEmpty()) {
                    sourceProtocolDBItem.setAccount(sourceOptions.user.getValue());
                } else {
                    sourceProtocolDBItem.setAccount(".");
                }
                DBItemYadeProtocols sourceProtocolFromDb = null;
                sourceProtocolFromDb = dbLayer.getProtocolFromDb(sourceProtocolDBItem.getHostname(), sourceProtocolDBItem.getPort(),
                        sourceProtocolDBItem.getProtocol(), sourceProtocolDBItem.getAccount());
                if (sourceProtocolFromDb != null) {
                    sourceProtocolDBItem = sourceProtocolFromDb;
                } else {
                    dbSession.save(sourceProtocolDBItem);
                }
                if (targetProtocolDBItem == null && options.targetDir.isDirty()) {
                    transferType = targetOptions.protocol.getEnum();
                    targetProtocolDBItem = new DBItemYadeProtocols();
                    targetProtocolDBItem.setHostname(targetOptions.host.getValue());
                    targetProtocolDBItem.setPort(targetOptions.port.value());
                    targetProtocolDBItem.setProtocol(transferType.numeric());

                    if (transferType.equals(TransferTypes.local)) {
                        targetProtocolDBItem.setPort(0);
                    } else if (transferType.equals(TransferTypes.webdav)) {
                        URL url = getURL(targetOptions);
                        if (url != null && url.getProtocol().toLowerCase().equals(TransferTypes.webdavs.name()) || url.getProtocol().toLowerCase()
                                .equals(TransferTypes.https.name())) {
                            targetProtocolDBItem.setProtocol(TransferTypes.webdavs.numeric());
                        }
                    } else if (transferType.equals(TransferTypes.http)) {
                        URL url = getURL(targetOptions);
                        if (url != null && url.getProtocol().toLowerCase().equals(TransferTypes.https.name())) {
                            targetProtocolDBItem.setProtocol(TransferTypes.https.numeric());
                        }
                    }
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace(String.format("[target][transferType %s=%s]targetProtocol=%s", transferType.name(), transferType.numeric(),
                                targetProtocolDBItem.getProtocol()));
                    }
                    if (targetOptions.user.isDirty() && targetOptions.user.getValue() != null && targetOptions.user.isNotEmpty()) {
                        targetProtocolDBItem.setAccount(targetOptions.user.getValue());
                    } else {
                        targetProtocolDBItem.setAccount(".");
                    }
                    DBItemYadeProtocols targetProtocolFromDb = null;
                    targetProtocolFromDb = dbLayer.getProtocolFromDb(targetProtocolDBItem.getHostname(), targetProtocolDBItem.getPort(),
                            targetProtocolDBItem.getProtocol(), targetProtocolDBItem.getAccount());
                    if (targetProtocolFromDb != null) {
                        targetProtocolDBItem = targetProtocolFromDb;
                    } else {
                        dbSession.save(targetProtocolDBItem);
                    }
                }

                if (jumpProtocolDBItem == null && options.jumpHost.isDirty()) {
                    transferType = TransferTypes.valueOf(options.jumpProtocol.getValue().toLowerCase());
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace(String.format("[jump]%s=%s", transferType.name(), transferType.numeric()));
                    }

                    jumpProtocolDBItem = new DBItemYadeProtocols();
                    jumpProtocolDBItem.setHostname(options.jumpHost.getValue());
                    jumpProtocolDBItem.setPort(options.jumpPort.value());
                    jumpProtocolDBItem.setProtocol(transferType.numeric());
                    if (options.jumpUser.isDirty() && options.jumpUser.getValue() != null && options.jumpUser.isNotEmpty()) {
                        jumpProtocolDBItem.setAccount(options.jumpUser.getValue());
                    } else {
                        jumpProtocolDBItem.setAccount(".");
                    }
                    DBItemYadeProtocols jumpProtocolFromDb = null;
                    jumpProtocolFromDb = dbLayer.getProtocolFromDb(jumpProtocolDBItem.getHostname(), jumpProtocolDBItem.getPort(), jumpProtocolDBItem
                            .getProtocol(), jumpProtocolDBItem.getAccount());
                    if (jumpProtocolFromDb != null) {
                        jumpProtocolDBItem = jumpProtocolFromDb;
                    } else {
                        dbSession.save(jumpProtocolDBItem);
                    }
                }

                DBItemYadeTransfers transferFromDb = null;
                if (transferDBItem != null && transferDBItem.getId() != null) {
                    transferFromDb = dbLayer.getTransferFromDb(transferDBItem.getId());
                }
                if (transferFromDb != null) {
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
                    if (fileList != null) {
                        transferFromDb.setNumOfFiles(fileList.count());
                    } else {
                        transferFromDb.setNumOfFiles(0L);
                    }
                    transferFromDb.setTaskId(taskId);
                    if (parentTransferId != null) {
                        transferFromDb.setParentTransferId(parentTransferId);
                    }
                    transferFromDb.setModified(getUTCDateFromInstant(Instant.now()));
                    dbSession.update(transferFromDb);
                    transferDBItem = transferFromDb;
                } else {
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
                    newTransfer.setMandator(options.mandator.getValue());
                    newTransfer.setOperation(getOperation(options.operation));
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
                    if (fileList != null) {
                        newTransfer.setNumOfFiles(fileList.count());
                    } else {
                        newTransfer.setNumOfFiles(0L);
                    }
                    if (options.profile != null) {
                        newTransfer.setProfileName(options.profile.getValue());
                    }
                    if (parentTransferId != null) {
                        newTransfer.setParentTransferId(parentTransferId);
                    }
                    newTransfer.setModified(getUTCDateFromInstant(Instant.now()));
                    dbSession.save(newTransfer);
                    transferId = newTransfer.getId();
                    transferDBItem = newTransfer;
                }
            }
        }
        return transferId;
    }

    public void storeInitialFilesInformationToDB(Long transferId, SOSHibernateSession dbSession, SOSFileList files) throws Exception {
        Long fileSizeSum = 0L;
        if (dbSession != null) {
            if (files != null && files.getList() != null && !files.getList().isEmpty()) {
                for (SOSFileListEntry fileEntry : files.getList()) {
                    YadeDBLayer dbLayer = new YadeDBLayer(dbSession);
                    DBItemYadeFiles fileFromDb = null;
                    fileFromDb = dbLayer.getTransferFileFromDbByConstraint(transferId, fileEntry.getSourceFilename());
                    if (fileFromDb != null) {
                        fileFromDb.setTargetPath(fileEntry.getTargetFileNameAndPath());
                        fileFromDb.setSize(fileEntry.getFileSize());
                        fileSizeSum += fileEntry.getFileSize();
                        fileFromDb.setState(fileEntry.getStatus());
                        String lastErrorMessage = fileEntry.getLastErrorMessage();
                        if (lastErrorMessage != null && !lastErrorMessage.isEmpty()) {
                            fileFromDb.setErrorCode("ERRORCODE");
                            fileFromDb.setErrorMessage(lastErrorMessage);
                        } else {
                            fileFromDb.setErrorCode(null);
                            fileFromDb.setErrorMessage(null);
                        }
                        fileFromDb.setIntegrityHash(fileEntry.getMd5());
                        fileFromDb.setModificationDate(getUTCDateFromTimeStamp(fileEntry.getSourceFileModificationDateTime()));
                        fileFromDb.setModified(getUTCDateFromInstant(Instant.now()));
                        dbSession.update(fileFromDb);
                    } else {
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
                        file.setModificationDate(getUTCDateFromTimeStamp(fileEntry.getSourceFileModificationDateTime()));
                        file.setModified(getUTCDateFromInstant(Instant.now()));
                        dbSession.save(file);
                    }
                }
            }
        }
        files.setSumFileSizes(fileSizeSum);
    }

    public void updateFileInformationToDB(SOSHibernateSession dbSession, SOSFileListEntry fileEntry) throws Exception {
        updateFileInformationToDB(dbSession, fileEntry, false, null);
    }

    public void updateFileInformationToDB(SOSHibernateSession dbSession, SOSFileListEntry fileEntry, boolean finalUpdate, String targetPath)
            throws Exception {

        if (transferDBItem == null) {
            return;
        }

        YadeDBLayer dbLayer = null;
        if (dbSession != null) {
            dbLayer = new YadeDBLayer(dbSession);
            if (fileEntry != null) {
                DBItemYadeFiles fileFromDb = null;
                fileFromDb = dbLayer.getTransferFileFromDbByConstraint(transferDBItem.getId(), fileEntry.getSourceFilename());
                if (fileFromDb != null) {
                    if (finalUpdate && (fileEntry.getStatus() == 0 || fileEntry.getStatus() == 7)) {
                        fileFromDb.setState(8);
                    } else if (finalUpdate) {
                        fileFromDb.setState(fileEntry.getStatus() + 1);
                    }
                    if (finalUpdate && fileFromDb.getState() != 7 && fileFromDb.getState() != 8 && fileFromDb.getState() != 14) {
                        if (targetPath != null) {
                            fileFromDb.setTargetPath(targetPath + fileEntry.getTargetFileName());
                        } else {
                            fileFromDb.setTargetPath(fileEntry.getTargetFileNameAndPath());
                        }
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
                    fileFromDb.setModificationDate(getUTCDateFromTimeStamp(fileEntry.getSourceFileModificationDateTime()));
                    fileFromDb.setModified(getUTCDateFromInstant(Instant.now()));
                    dbSession.update(fileFromDb);
                    Map<String, String> values = new HashMap<String, String>();
                    values.put("fileId", fileFromDb.getId().toString());
                    eventHandler.sendEvent("YADEFileStateChanged", values);
                    if (finalUpdate && parentTransferId != null) {
                        DBItemYadeFiles intervenedFileFromDb = null;
                        intervenedFileFromDb = dbLayer.getTransferFileFromDbByConstraint(parentTransferId, fileEntry.getSourceFilename());
                        if (intervenedFileFromDb != null) {
                            intervenedFileFromDb.setInterventionTransferId(transferDBItem.getId());
                            dbSession.update(intervenedFileFromDb);
                            values = new HashMap<String, String>();
                            values.put("transferId", parentTransferId.toString());
                            values.put("fileId", intervenedFileFromDb.getId().toString());
                            eventHandler.sendEvent("YADEFileStateChanged", values);
                        } else {
                            LOGGER.debug(String.format("File entry with transfer id=%1$d and path=%2$s not updated with intervention id=%3$d "
                                    + "due to item not found in DB!", parentTransferId, fileEntry.getSourceFilename(), transferDBItem.getId()));
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
                    file.setModificationDate(getUTCDateFromTimeStamp(fileEntry.getSourceFileModificationDateTime()));
                    String lastErrorMessage = fileEntry.getLastErrorMessage();
                    if (lastErrorMessage != null && !lastErrorMessage.isEmpty()) {
                        file.setErrorCode("ERRORCODE");
                        file.setErrorMessage(lastErrorMessage);
                    } else {
                        file.setErrorCode(null);
                        file.setErrorMessage(null);
                    }
                    file.setIntegrityHash(fileEntry.getMd5());
                    file.setModificationDate(getUTCDateFromTimeStamp(fileEntry.getSourceFileModificationDateTime()));
                    file.setModified(getUTCDateFromInstant(Instant.now()));
                    dbSession.save(file);
                    Map<String, String> values = new HashMap<String, String>();
                    values.put("fileId", file.getId().toString());
                    eventHandler.sendEvent("YADEFileStateChanged", values);
                }
            }
        }
    }

    public Long storeInitialTransferInformations(SOSFileList fileList, SOSHibernateSession dbSession) throws Exception {
        return storeInitialTransferInformations(fileList, dbSession, null);
    }

    public Long storeInitialTransferInformations(SOSFileList fileList, SOSHibernateSession dbSession, Long parentTransferId) throws Exception {
        this.parentTransferId = parentTransferId;
        Long transferId = null;
        if (dbSession != null) {
            transferId = storeYadeTransferInformationToDB(fileList, dbSession, parentTransferId);
        }
        return transferId;
    }

    public void addAdditionalJobInfosFromOptions() {
        if (options.getJobSchedulerId() != null) {
            currentJobschedulerId = options.getJobSchedulerId();
        }
        if (options.getJobChain() != null) {
            currentJobChain = options.getJobChain();
        }
        if (options.getJob() != null) {
            currentJob = options.getJob();
        }
        if (options.getJobChainNodeName() != null) {
            currentNodeName = options.getJobChainNodeName();
        }
        if (options.getOrderId() != null) {
            currentOrderId = options.getOrderId();
        }
        if (options.getParentTransferId() != null) {
            parentTransferId = options.getParentTransferId();
        }
        if (options.getTaskId() != null) {
            String taskIdFromOptions = options.getTaskId();
            taskId = Long.parseLong(taskIdFromOptions);
        }

    }

    public void updateFailedTransfer(SOSHibernateSession dbSession, String errorMessage) throws Exception {
        if (transferDBItem != null) {
            transferDBItem.setState(3);
            transferDBItem.setErrorMessage(errorMessage);
            transferDBItem.setEnd(getUTCDateFromInstant(Instant.now()));
            transferDBItem.setModified(getUTCDateFromInstant(Instant.now()));
            if (dbSession != null) {
                dbSession.update(transferDBItem);
            }
            eventHandler.sendEvent("YADETransferUpdated", null);
        }
    }

    public void updateSuccessfulTransfer(SOSHibernateSession dbSession) throws Exception {
        if (transferDBItem != null) {
            transferDBItem.setState(1);
            transferDBItem.setEnd(getUTCDateFromInstant(Instant.now()));
            transferDBItem.setModified(getUTCDateFromInstant(Instant.now()));
            if (dbSession != null) {
                dbSession.update(transferDBItem);
            }
            eventHandler.sendEvent("YADETransferUpdated", null);
        }
    }

    public void updateTransfersNumOfFiles(SOSHibernateSession dbSession, Long numOfFiles) throws Exception {
        if (transferDBItem != null) {
            transferDBItem.setNumOfFiles(numOfFiles);
            transferDBItem.setModified(getUTCDateFromInstant(Instant.now()));
            if (dbSession != null) {
                dbSession.update(transferDBItem);
            }
            eventHandler.sendEvent("YADETransferUpdated", null);
        }
    }

    public DBItemYadeTransfers getTransfer(Long id, SOSHibernateSession dbSession) {
        YadeDBLayer dbLayer = null;
        DBItemYadeTransfers transfer = null;
        if (dbSession != null) {
            dbLayer = new YadeDBLayer(dbSession);
            try {
                transfer = dbLayer.getTransferFromDb(id);
            } catch (SOSHibernateException e) {
                LOGGER.error(e.getMessage(), e);
                if (dbSession != null) {
                    try {
                        dbSession.rollback();
                    } catch (SOSHibernateException e1) {
                    }
                }
            }
        }
        return transfer;
    }

    private Integer getOperation(SOSOptionJadeOperation jadeOperation) {
        switch (jadeOperation.value()) {
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

    @SuppressWarnings("unused")
    private Boolean getHasErrorsFromFileState(Integer fileEntryState) {
        switch (fileEntryState) {
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

    public void setParentTransferId(Long parentTransferId) {
        this.parentTransferId = parentTransferId;
    }

    private Date getUTCDateFromInstant(Instant inDate) {
        return getUTCDateFromTimeStamp(inDate.toEpochMilli());
    }

    private Date getUTCDateFromTimeStamp(long timestamp) {
        if (timestamp < 0L) {
            return null;
        }
        return new Date(timestamp - TimeZone.getDefault().getOffset(timestamp));
    }

}