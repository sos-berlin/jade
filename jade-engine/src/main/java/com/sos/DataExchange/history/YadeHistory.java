package com.sos.DataExchange.history;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.DataExchange.history.YadeDBOperationHelper;
import com.sos.JSHelper.interfaces.IJobSchedulerEventHandler;
import com.sos.VirtualFileSystem.DataElements.SOSFileList;
import com.sos.VirtualFileSystem.DataElements.SOSFileListEntry;
import com.sos.hibernate.classes.SOSHibernateFactory;
import com.sos.hibernate.classes.SOSHibernateSession;
import com.sos.hibernate.exceptions.SOSHibernateException;
import com.sos.jade.db.DBItemYadeFiles;
import com.sos.jade.db.DBItemYadeTransfers;
import com.sos.jade.db.YadeDBLayer;
import com.sos.jitl.reporting.db.DBLayer;

public class YadeHistory {

    private static final Logger LOGGER = Logger.getLogger(YadeHistory.class);

    private static final String SESSION_IDENTIFIER = "YadeJob";
    private SOSHibernateFactory dbFactory;
    private YadeDBOperationHelper dbHelper = null;
    private IJobSchedulerEventHandler eventHandler;
    private Long transferId;
    private Long parentTransferId;
    private boolean hasException = false;
    private boolean isIntervention = false;
    private String filePathRestriction = null;

    public YadeHistory(IJobSchedulerEventHandler handler) {
        eventHandler = handler;
    }

    public void buildFactory(Path hibernateFile) {
        try {
            dbFactory = new SOSHibernateFactory(hibernateFile);
            dbFactory.setIdentifier(SESSION_IDENTIFIER);
            dbFactory.setAutoCommit(false);
            dbFactory.addClassMapping(DBLayer.getYadeClassMapping());
            dbFactory.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            dbFactory.build();
        } catch (Throwable ex) {
            LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, ex.toString()), ex);
            hasException = true;
        }
    }

    public void closeFactory() {
        try {
            if (dbFactory != null) {
                dbFactory.close();
            }
        } catch (Throwable ex) {
            LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, ex.toString()), ex);
            hasException = true;
        }
    }

    public void beforeTransfer(JADEOptions options, SOSFileList fileList) {
        if (dbFactory == null) {
            LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, "dbFactory is null"));
            return;
        }

        try {
            dbHelper = new YadeDBOperationHelper(options, eventHandler);
            SOSHibernateSession dbSession = null;
            try {
                dbSession = dbFactory.openStatelessSession(SESSION_IDENTIFIER);
                LOGGER.debug("DB Session opened before transfer!");
                if (parentTransferId != null) {
                    dbHelper.setParentTransferId(parentTransferId);
                    DBItemYadeTransfers existingTransfer = dbHelper.getTransfer(parentTransferId, dbSession);
                    if (existingTransfer != null && existingTransfer.getJobChainNode().equals(options.getJobChainNodeName()) && existingTransfer
                            .getOrderId().equals(options.getOrderId()) && existingTransfer.getState() == 3) {
                        existingTransfer.setHasIntervention(true);
                        try {
                            dbSession.beginTransaction();
                            dbSession.update(existingTransfer);
                            dbSession.commit();
                        } catch (SOSHibernateException e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                        transferId = dbHelper.storeInitialTransferInformations(fileList, dbSession, existingTransfer.getId());
                    } else {
                        transferId = dbHelper.storeInitialTransferInformations(fileList, dbSession);
                    }
                } else {
                    transferId = dbHelper.storeInitialTransferInformations(fileList, dbSession);
                }
                sendYadeEvent("YADETransferStarted");
            } finally {
                dbSession.close();
                LOGGER.debug("DB Session closed before transfer!");
            }
        } catch (Throwable ex) {
            LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, ex.toString()), ex);
            hasException = true;
        }
    }

    public void afterTransfer() {
        if (dbFactory == null) {
            LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, "dbFactory is null"));
            return;
        }
        if (hasException) {
            return;
        }

        try {
            SOSHibernateSession dbSession = null;
            try {
                dbSession = dbFactory.openStatelessSession(SESSION_IDENTIFIER);
                dbHelper.updateSuccessfulTransfer(dbSession);
            } finally {
                dbSession.close();
            }
        } catch (Throwable ex) {
            LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, ex.toString()), ex);
            hasException = true;
        }
    }

    public void beforeFileTransfer(SOSFileList fileList) {
        if (dbFactory == null) {
            LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, "dbFactory is null"));
            return;
        }
        if (hasException) {
            return;
        }

        try {
            SOSHibernateSession dbSession = null;
            try {
                dbSession = dbFactory.openStatelessSession(SESSION_IDENTIFIER);

                dbHelper.updateTransfersNumOfFiles(dbSession, fileList.size());
                if (transferId != null) {
                    dbHelper.storeInitialFilesInformationToDB(transferId, dbSession, fileList);
                }
            } finally {
                dbSession.close();
            }
        } catch (Throwable ex) {
            LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, ex.toString()), ex);
            hasException = true;
        }
    }

    public void afterFileTransfer(SOSFileList fileList) {
        if (dbFactory == null) {
            LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, "dbFactory is null"));
            return;
        }
        if (hasException) {
            return;
        }

        try {
            SOSHibernateSession dbSession = null;
            try {
                dbSession = dbFactory.openStatelessSession(SESSION_IDENTIFIER);

                for (SOSFileListEntry entry : fileList.getList()) {
                    dbHelper.updateFileInformationToDB(dbSession, entry, true, null);
                }
                dbHelper.updateTransfersNumOfFiles(dbSession, fileList.count());
            } finally {
                dbSession.close();
            }
        } catch (Throwable ex) {
            LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, ex.toString()), ex);
            hasException = true;
        }
    }

    public void afterDMZFileTransfer(SOSFileList fileList, String targetDir) {
        if (dbFactory == null) {
            LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, "dbFactory is null"));
            return;
        }
        if (hasException) {
            return;
        }

        try {
            SOSHibernateSession dbSession = null;
            try {
                dbSession = dbFactory.openStatelessSession(SESSION_IDENTIFIER);

                dbHelper.storeInitialFilesInformationToDB(transferId, dbSession, fileList);
                dbHelper.updateTransfersNumOfFiles(dbSession, fileList.count());
                for (SOSFileListEntry entry : fileList.getList()) {
                    dbHelper.updateFileInformationToDB(dbSession, entry, true, targetDir);
                }
            } finally {
                dbSession.close();
            }
        } catch (Throwable ex) {
            LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, ex.toString()), ex);
            hasException = true;
        }
    }

    public void onException(Exception e) {
        if (dbFactory == null) {
            LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, "dbFactory is null"));
            return;
        }
        if (hasException) {
            return;
        }

        SOSHibernateSession dbSession = null;
        try {
            dbSession = dbFactory.openStatelessSession(SESSION_IDENTIFIER);
            dbHelper.updateFailedTransfer(dbSession, String.format("%1$s: %2$s", e.getClass().getSimpleName(), e.getMessage()));
        } catch (Throwable ex) {
            LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, ex.toString()), ex);
            hasException = true;
        } finally {
            dbSession.close();
        }
    }

    public void onFileTransferException(SOSFileList fileList) {
        if (dbFactory == null) {
            LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, "dbFactory is null"));
            return;
        }
        if (hasException) {
            return;
        }

        SOSHibernateSession dbSession = null;
        try {
            dbSession = dbFactory.openStatelessSession(SESSION_IDENTIFIER);

            for (SOSFileListEntry entry : fileList.getList()) {
                if (dbFactory != null) {
                    dbHelper.updateFileInformationToDB(dbSession, entry, true, null);
                }
            }

        } catch (Throwable ex) {
            LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, ex.toString()), ex);
            hasException = true;
        } finally {
            dbSession.close();
        }
    }

    public void onDMZFileTransferException(SOSFileList fileList, String targetDir) {
        if (dbFactory == null) {
            LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, "dbFactory is null"));
            return;
        }
        if (hasException) {
            return;
        }

        SOSHibernateSession dbSession = null;
        try {
            dbSession = dbFactory.openStatelessSession(SESSION_IDENTIFIER);

            dbHelper.updateTransfersNumOfFiles(dbSession, fileList.count());
            for (SOSFileListEntry entry : fileList.getList()) {
                dbHelper.updateFileInformationToDB(dbSession, entry, true, targetDir);
            }

        } catch (Throwable ex) {
            LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, ex.toString()), ex);
            hasException = true;
        } finally {
            dbSession.close();
        }
    }

    public void setFileRestriction(JADEOptions options) {
        if (dbFactory == null) {
            LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, "dbFactory is null"));
            return;
        }
        if (hasException) {
            return;
        }

        if (filePathRestriction != null) {
            LOGGER.info("*** transfer was restarted with a reduced fileList");
            LOGGER.info("*** with the filePathRestriction: " + filePathRestriction);
            if (options.fileListName.isNotEmpty()) {
                options.fileListName.setNull();
            }
            options.filePath.setValue(filePathRestriction);
        }
    }

    public void sendYadeEventOnEnd() {
        sendYadeEvent("YADETransferFinished");
    }

    public void updateFileInDB(Map<String, String> values) {
        if (dbFactory == null) {
            LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, "dbFactory is null"));
            return;
        }
        if (hasException) {
            return;
        }
        SOSHibernateSession dbSession = null;
        try {
            dbSession = dbFactory.openStatelessSession(SESSION_IDENTIFIER);
            YadeDBLayer dbLayer = new YadeDBLayer(dbSession);
            DBItemYadeFiles file = null;
            String filePath = null;
            try {
                filePath = values.get("sourcePath");
                dbSession.beginTransaction();
                file = dbLayer.getTransferFileFromDbByConstraint(transferId, filePath);
                dbSession.commit();
            } catch (SOSHibernateException e) {
                LOGGER.error(e.getMessage(), e);
            }
            if (file != null) {
                if (parentTransferId != null) {
                    DBItemYadeFiles intervenedFile = null;
                    filePath = values.get("sourcePath");
                    try {
                        dbSession.beginTransaction();
                        intervenedFile = dbLayer.getTransferFileFromDbByConstraint(parentTransferId, filePath);
                        dbSession.commit();
                    } catch (SOSHibernateException e) {
                        LOGGER.error(e.getMessage(), e);
                        try {
                            dbSession.rollback();
                        } catch (SOSHibernateException e1) {
                        }
                    }
                    if (intervenedFile != null) {
                        intervenedFile.setInterventionTransferId(transferId);
                        try {
                            dbSession.beginTransaction();
                            dbSession.update(intervenedFile);
                            dbSession.commit();
                        } catch (SOSHibernateException e) {
                            LOGGER.error(e.getMessage(), e);
                            try {
                                dbSession.rollback();
                            } catch (SOSHibernateException e1) {
                            }
                        }
                    }
                }
                for (String key : values.keySet()) {
                    // key = propertyName
                    // values.get(key) = propertyValue
                    switch (key) {
                    case "sourcePath":
                        file.setSourcePath(values.get(key));
                        break;
                    case "targetPath":
                        file.setTargetPath(values.get(key));
                        break;
                    case "state":
                        file.setState(Integer.parseInt(values.get(key)));
                        break;
                    case "errorCode":
                        file.setErrorCode(values.get(key));
                        break;
                    case "errorMessage":
                        file.setErrorMessage(values.get(key));
                        break;
                    default:
                        break;
                    }
                }
                try {
                    dbSession.beginTransaction();
                    dbSession.update(file);
                    dbSession.commit();
                    Map<String, String> eventValues = new HashMap<String, String>();
                    eventValues.put("fileId", file.getId().toString());
                    eventHandler.sendEvent("YADEFileStateChanged", eventValues);
                } catch (SOSHibernateException e) {
                    LOGGER.error(e.getMessage(), e);
                    try {
                        dbSession.rollback();
                    } catch (SOSHibernateException e1) {
                    }
                }
            }
        } catch (Throwable ex) {
            LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, ex.toString()), ex);
            hasException = true;

        } finally {
            if (dbSession != null) {
                dbSession.close();
                LOGGER.debug("DB Session closed in interface while transferring");
            }
        }

    }

    private void sendYadeEvent(String message) {
        if (dbFactory == null) {
            LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, "dbFactory is null"));
            return;
        }

        if (eventHandler != null && transferId != null) {
            try {
                Map<String, String> values = new HashMap<String, String>();
                values.put("transferId", transferId.toString());
                eventHandler.sendEvent(message, values);
            } catch (Throwable ex) {
                LOGGER.error(String.format("[%s]%s", SESSION_IDENTIFIER, ex.toString()), ex);
                hasException = true;
            }
        }
    }

    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    public Long getParentTransferId() {
        return parentTransferId;
    }

    public void setParentTransferId(Long parentTransferId) {
        this.parentTransferId = parentTransferId;
    }

    public boolean isIntervention() {
        return isIntervention;
    }

    public void setIntervention(boolean isIntervention) {
        this.isIntervention = isIntervention;
    }

    public String getFilePathRestriction() {
        return filePathRestriction;
    }

    public void setFilePathRestriction(String filePathRestriction) {
        this.filePathRestriction = filePathRestriction;
    }

}
