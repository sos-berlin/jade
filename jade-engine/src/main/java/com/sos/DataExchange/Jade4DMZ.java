package com.sos.DataExchange;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.credentialstore.options.SOSCredentialStoreOptions;
import com.sos.DataExchange.options.JADEOptions;
import com.sos.DataExchange.helpers.UpdateXmlToOptionHelper;
import com.sos.DataExchange.history.YadeHistory;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.vfs.common.SOSFileList;
import com.sos.vfs.common.options.SOSProviderOptions;
import com.sos.i18n.annotation.I18NResourceBundle;
import com.sos.keepass.SOSKeePassDatabase;

import sos.util.SOSString;

@I18NResourceBundle(baseName = "SOSDataExchange", defaultLocale = "en")
public class Jade4DMZ extends JadeBaseEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(Jade4DMZ.class);
    private static final String INTERNALLY_COMMAND_DELIMITER = "SOSJUMPCD";
    private SOSFileList fileList = null;
    private String uuid = null;
    private String sourceListFilename = null;
    private String historyFilename = null;
    private String initialTargetDir = null;
    private YadeHistory history = null;

    protected enum Operation {
        copyToInternet, copyFromInternet, remove, getlist
    }

    public void execute() {

        setLogger();
        initialTargetDir = getOptions().targetDir.getValue();

        Operation operation = null;
        try {
            if (objOptions.operation.isOperationCopyToInternet() || objOptions.operation.isOperationSendUsingDMZ()) {
                operation = Operation.copyToInternet;
            } else if (objOptions.operation.isOperationCopyFromInternet() || objOptions.operation.isOperationReceiveUsingDMZ()) {
                operation = Operation.copyFromInternet;
            } else if (objOptions.operation.isOperationRemove()) {
                operation = Operation.remove;
            } else if (objOptions.operation.isOperationGetList()) {
                operation = Operation.getlist;
            } else {
                throw new JobSchedulerException(String.format("unsuported operation \"%s\"", objOptions.operation.getValue()));
            }

            UpdateXmlToOptionHelper updateHelper = new UpdateXmlToOptionHelper(getOptions());
            if (updateHelper.checkBefore()) {
                updateHelper.executeBefore();
                objOptions = updateHelper.getOptions();
            }
            objOptions.checkMandatory();

            if (history != null) {
                history.beforeTransfer(objOptions, null);
            }

            transfer(operation);

            if (history != null) {
                history.afterTransfer();
            }
        } catch (JobSchedulerException e) {
            if (history != null) {
                history.onException(e);
            }
            throw e;
        } catch (Exception e) {
            throw new JobSchedulerException("Transfer failed", e);
        } finally {
            if (history != null) {
                history.sendYadeEventOnEnd();
            }
        }
    }

    private void transfer(Operation operation) {
        String jumpDir = normalizeDirectoryPath(getOptions().jumpDir.getValue());
        String uuid = "jade-dmz-" + getUUID();
        String dir = jumpDir + uuid;

        LOGGER.info(String.format("operation = %s, jump dir = %s", operation, dir));
        JadeEngine jade = null;
        fileList = null;
        try {
            Jade4DMZEngineClientHandler clientHandler = new Jade4DMZEngineClientHandler(getOptions(), operation, jumpDir, uuid, dir);

            jade = new JadeEngine(getTransferOptions(operation, dir, clientHandler));
            jade.setEngineClientHandler(clientHandler);
            jade.execute();

            if (operation.equals(Operation.copyFromInternet) && objOptions.removeFiles.value()) {
                jade.executeTransferCommands("source remove files", jade.getSourceClient(), getJadeOnDMZCommand4RemoveSource(), null);
            }
            fileList = jade.getFileList();

            if (history != null) {
                history.afterDMZFileTransfer(fileList, initialTargetDir);
            }
        } catch (Exception e) {
            if (history != null) {
                history.onDMZFileTransferException(fileList, initialTargetDir);
            }
            throw new JobSchedulerException("Transfer failed", e);
        }
    }

    private SOSProviderOptions createJumpOptions(String dir) throws Exception {
        SOSProviderOptions options = new SOSProviderOptions();
        options.protocol.setValue("sftp");
        options.host.setValue(objOptions.jumpHost.getValue());
        options.port.setValue(objOptions.jumpPort.getValue());
        options.user.setValue(objOptions.jumpUser.getValue());
        options.required_authentications.setValue(objOptions.jump_required_authentications.getValue());
        options.preferred_authentications.setValue(objOptions.jump_preferred_authentications.getValue());
        options.sshAuthMethod.setValue(objOptions.jumpSshAuthMethod.getValue());
        options.password.setValue(objOptions.jumpPassword.getValue());
        options.sshAuthFile.setValue(objOptions.jumpSshAuthFile.getValue());
        options.passphrase.setValue(objOptions.jump_passphrase.getValue());
        options.strictHostKeyChecking.value(objOptions.jumpStrictHostkeyChecking.value());
        options.directory.setValue(dir);
        options.configuration_files.setValue(objOptions.jumpConfigurationFiles.getValue());
        options.proxyProtocol.setValue(objOptions.jumpProxyProtocol.getValue());
        options.proxyHost.setValue(objOptions.jumpProxyHost.getValue().trim());
        options.proxyPort.setValue(objOptions.jumpProxyPort.getValue().trim());
        options.proxyUser.setValue(objOptions.jumpProxyUser.getValue().trim());
        options.proxyPassword.setValue(objOptions.jumpProxyPassword.getValue());
        options.server_alive_interval.setValue(objOptions.jump_server_alive_interval.getValue());
        options.server_alive_count_max.setValue(objOptions.jump_server_alive_count_max.getValue());
        options.connect_timeout.setValue(objOptions.jump_connect_timeout.getValue());
        options.channel_connect_timeout.setValue(objOptions.jump_channel_connect_timeout.getValue());
        if (objOptions.jump_use_credential_store.value()) {
            SOSCredentialStoreOptions csOptions = new SOSCredentialStoreOptions();
            csOptions.useCredentialStore.setValue(objOptions.jump_use_credential_store.getValue());
            csOptions.credentialStoreFileName.setValue(objOptions.jump_CredentialStore_FileName.getValue());
            csOptions.credentialStoreAuthenticationMethod.setValue(objOptions.jump_CredentialStore_AuthenticationMethod.getValue());
            csOptions.credentialStoreKeyFileName.setValue(objOptions.jump_CredentialStore_KeyFileName.getValue());
            csOptions.credentialStorePassword.setValue(objOptions.jump_CredentialStore_Password.getValue());
            csOptions.credentialStoreKeyPath.setValue(objOptions.jump_CredentialStore_KeyPath.getValue());
            options.setCredentialStore(csOptions);

            Path jumpKpdPath = Paths.get(csOptions.credentialStoreFileName.getValue());
            Object jumpKpd = null;
            Object kpd = objOptions.getSource().keepass_database.value();
            if (kpd != null && ((SOSKeePassDatabase) kpd).getFile().equals(jumpKpdPath)) {
                LOGGER.debug("set jump settings from source KeePass");
                jumpKpd = kpd;
            }
            if (jumpKpd == null) {
                kpd = objOptions.getTarget().keepass_database.value();
                if (kpd != null && ((SOSKeePassDatabase) kpd).getFile().equals(jumpKpdPath)) {
                    LOGGER.debug("set jump settings from target KeePass");
                    jumpKpd = kpd;
                }
            }
            if (jumpKpd == null) {
                LOGGER.debug("set jump settings from jump KeePass");
            }
            options.keepass_database.value(jumpKpd);
            options.checkCredentialStoreOptions();
            options.keepass_database.value(null);
            csOptions.useCredentialStore.setValue("false");// prevent double checkCredentialStore
            objOptions.jumpHost.setValue(options.host.getValue());// override possible cs://...
        }
        return options;
    }

    private JADEOptions createPostTransferOptions(Operation operation, String dir) throws Exception {
        // From DMZ to Internet as PostTransferCommands
        JADEOptions options = new JADEOptions();
        options = addJADEOptionsForTarget(operation, options);
        SOSProviderOptions sourceOptions = setLocalOptionsPrefixed("source_", dir);
        SOSProviderOptions targetOptions = objOptions.getTarget();
        options.getTransfer().setSource(sourceOptions);
        options.getTransfer().setTarget(targetOptions);
        return options;
    }

    private JADEOptions createPreTransferOptions(Operation operation, String dir) throws Exception {
        // From Internet to DMZ as PreTransferCommands
        String prefix = "target_";
        JADEOptions options = new JADEOptions();
        options = addJADEOptionsForSource(operation, options);
        SOSProviderOptions targetOptions = setLocalOptionsPrefixed(prefix, dir);
        targetOptions.preCommand.setValue(objOptions.jumpPreCommand.getValue());
        targetOptions.postCommand.setValue(objOptions.jumpPostCommandOnSuccess.getValue());
        targetOptions.preTransferCommands.setValue(objOptions.jumpPreTransferCommands.getValue());
        targetOptions.postTransferCommands.setValue(objOptions.jumpPostTransferCommandsOnSuccess.getValue());
        targetOptions.postTransferCommandsOnError.setValue(objOptions.jumpPostTransferCommandsOnError.getValue());
        targetOptions.postTransferCommandsFinal.setValue(objOptions.jumpPostTransferCommandsFinal.getValue());
        targetOptions.commandDelimiter.setValue(objOptions.jumpCommandDelimiter.getValue());
        targetOptions.preCommand.setPrefix(prefix);
        targetOptions.postCommand.setPrefix(prefix);
        targetOptions.preTransferCommands.setPrefix(prefix);
        targetOptions.postTransferCommands.setPrefix(prefix);
        targetOptions.postTransferCommandsOnError.setPrefix(prefix);
        targetOptions.postTransferCommandsFinal.setPrefix(prefix);
        targetOptions.commandDelimiter.setPrefix(prefix);
        options.getTransfer().setTarget(targetOptions);
        SOSProviderOptions sourceOptions = objOptions.getSource();
        options.getTransfer().setSource(sourceOptions);
        // Remove source files at Internet as PostTransferCommands
        options.removeFiles.value(false);
        if (objOptions.removeFiles.value() || objOptions.resultSetFileName.isDirty()) {
            options.resultSetFileName.setValue(getSourceListFilename());
        }
        return options;
    }

    /** Transfer from Intranet/Internet to DMZ
     * 
     * @return */
    private JADEOptions getTransferOptions(Operation operation, String dir, Jade4DMZEngineClientHandler clientHandler) throws Exception {
        LOGGER.debug(String.format("operation = %s, jump dir = %s", operation, dir));
        JADEOptions options = null;
        JADEOptions jumpCommandOptions;
        SOSProviderOptions jumpOptions = createJumpOptions(dir);
        if (operation.equals(Operation.copyToInternet)) {
            options = createTransferToDMZOptions(operation, dir);
            jumpCommandOptions = createPostTransferOptions(operation, dir);

            jumpOptions.preCommand.setValue(getJumpCommand(objOptions.jumpPreCommand.getValue()));
            jumpOptions.postCommand.setValue(getJumpCommand(objOptions.jumpPostCommandOnSuccess.getValue()));
            jumpOptions.preTransferCommands.setValue(getJumpCommand(objOptions.jumpPreTransferCommands.getValue()));

            String firstCommand = "";
            String jumpPostTransferCommandsOnSuccess = getJumpCommand(objOptions.jumpPostTransferCommandsOnSuccess.getValue());
            if (!SOSString.isEmpty(jumpPostTransferCommandsOnSuccess)) {
                firstCommand = jumpPostTransferCommandsOnSuccess.endsWith(INTERNALLY_COMMAND_DELIMITER) ? jumpPostTransferCommandsOnSuccess
                        : jumpPostTransferCommandsOnSuccess + INTERNALLY_COMMAND_DELIMITER;
            }
            jumpOptions.postTransferCommands.setValue(firstCommand + getJadeOnDMZCommand(operation, jumpCommandOptions));
            jumpOptions.postTransferCommandsOnError.setValue(getJumpCommand(objOptions.jumpPostTransferCommandsOnError.getValue()));
            jumpOptions.postTransferCommandsFinal.setValue(getJumpCommand(objOptions.jumpPostTransferCommandsFinal.getValue()));
            jumpOptions.commandDelimiter.setValue(INTERNALLY_COMMAND_DELIMITER);

            jumpOptions = setProviderOptionsPrefix(jumpOptions, "target_");

            options.getTransfer().setSource(objOptions.getSource());
            options.getTransfer().setTarget(jumpOptions);
        } else {
            options = createTransferFromDMZOptions(operation, dir);
            jumpCommandOptions = createPreTransferOptions(operation, dir);
            if (clientHandler.isCopyFromInternetWithFileList()) {
                jumpCommandOptions.fileListName.setValue(clientHandler.getJumpFileListName());
            }
            jumpOptions.preTransferCommands.setValue(getJadeOnDMZCommand(operation, jumpCommandOptions));
            jumpOptions = setProviderOptionsPrefix(jumpOptions, "source_");

            jumpOptions.commandDelimiter.setValue(INTERNALLY_COMMAND_DELIMITER);

            options.getTransfer().setSource(jumpOptions);
            options.getTransfer().setTarget(objOptions.getTarget());
        }
        options.setDmzOption("operation", operation.name());
        options.setDmzOption("history", getHistoryFilename());
        options.setDmzOption("resultfile", getSourceListFilename());
        return options;
    }

    private String getJumpCommand(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll(objOptions.jumpCommandDelimiter.getValue(), INTERNALLY_COMMAND_DELIMITER);
    }

    private SOSProviderOptions setProviderOptionsPrefix(SOSProviderOptions options, String prefix) {
        options.protocol.setPrefix(prefix);
        options.host.setPrefix(prefix);
        options.port.setPrefix(prefix);
        options.user.setPrefix(prefix);
        options.password.setPrefix(prefix);
        options.sshAuthMethod.setPrefix(prefix);
        options.sshAuthFile.setPrefix(prefix);
        options.proxyProtocol.setPrefix(prefix);
        options.proxyHost.setPrefix(prefix);
        options.proxyPort.setPrefix(prefix);
        options.proxyUser.setPrefix(prefix);
        options.proxyPassword.setPrefix(prefix);
        options.preCommand.setPrefix(prefix);
        options.postCommand.setPrefix(prefix);
        options.preTransferCommands.setPrefix(prefix);
        options.postTransferCommands.setPrefix(prefix);
        options.directory.setPrefix(prefix);
        options.postTransferCommandsFinal.setPrefix(prefix);
        options.postTransferCommandsOnError.setPrefix(prefix);
        options.keepass_database.setPrefix(prefix);
        options.keepass_database_entry.setPrefix(prefix);
        options.keepass_attachment_property_name.setPrefix(prefix);
        options.passphrase.setPrefix(prefix);
        options.preferred_authentications.setPrefix(prefix);
        options.required_authentications.setPrefix(prefix);
        options.server_alive_interval.setPrefix(prefix);
        options.server_alive_count_max.setPrefix(prefix);
        options.connect_timeout.setPrefix(prefix);
        options.channel_connect_timeout.setPrefix(prefix);
        return options;
    }

    private SOSProviderOptions setLocalOptionsPrefixed(String prefix, String dir) {
        SOSProviderOptions options = new SOSProviderOptions();
        options.protocol.setValue("local");
        options.host.setValue(objOptions.jumpHost.getValue());
        options.directory.setValue(dir);
        options.protocol.setPrefix(prefix);
        options.host.setPrefix(prefix);
        options.directory.setPrefix(prefix);
        return options;
    }

    private JADEOptions createTransferFromDMZOptions(Operation operation, String dir) throws Exception {
        JADEOptions options = new JADEOptions();
        options = addJADEOptionsOnClient(options);
        options = addJADEOptionsForTarget(operation, options);
        options.sourceDir.setValue(dir);
        options.targetDir.setValue(objOptions.getTarget().directory.getValue());
        return options;
    }

    /** Transfer from Intranet/Internet to DMZ without changes
     * 
     * @param targetDir
     * @return */
    private JADEOptions createTransferToDMZOptions(Operation operation, String dir) throws Exception {
        JADEOptions options = new JADEOptions();
        options = addJADEOptionsOnClient(options);
        options = addJADEOptionsForSource(operation, options);
        options.sourceDir.setValue(objOptions.getSource().directory.getValue());
        options.targetDir.setValue(dir);
        options.removeFiles = objOptions.removeFiles;
        return options;
    }

    private JADEOptions addJADEOptionsForSource(Operation operation, JADEOptions options) {
        if (operation.equals(Operation.remove)) {
            options.operation.setValue("delete");
        } else if (operation.equals(Operation.getlist)) {
            options.operation.setValue("getlist");
        } else {
            options.operation.setValue("copy");
            options.transactional.value(true);
        }
        options.atomicPrefix = objOptions.atomicPrefix;
        options.atomicSuffix = objOptions.atomicSuffix;
        options.bufferSize = objOptions.bufferSize;
        options.checkSteadyCount = objOptions.checkSteadyCount;
        options.checkSteadyStateInterval = objOptions.checkSteadyStateInterval;
        options.checkSteadyStateOfFiles = objOptions.checkSteadyStateOfFiles;
        options.pollingWait4SourceFolder = objOptions.pollingWait4SourceFolder;
        // not supported: options.PollingServer = objOptions.PollingServer;
        options.pollingEndAt = objOptions.pollingEndAt;
        options.pollingServerPollForever = objOptions.pollingServerPollForever;
        options.pollingServerDuration = objOptions.pollingServerDuration;
        options.pollKeepConnection = objOptions.pollKeepConnection;
        options.pollInterval = objOptions.pollInterval;
        options.pollMinfiles = objOptions.pollMinfiles;
        options.pollingDuration = objOptions.pollingDuration;
        options.pollTimeout = objOptions.pollTimeout;
        options.maxFiles = objOptions.maxFiles;
        options.fileListName = objOptions.fileListName;
        options.filePath = objOptions.filePath;
        options.fileSpec = objOptions.fileSpec;
        options.forceFiles = objOptions.forceFiles;
        options.recursive = objOptions.recursive;
        options.skipTransfer = objOptions.skipTransfer;
        options.keepModificationDate = objOptions.keepModificationDate;
        // special handling: options.remove_files = objOptions.remove_files;
        options.verbose = objOptions.verbose;
        options.protocolCommandListener = objOptions.protocolCommandListener;
        options.zeroByteTransfer = objOptions.zeroByteTransfer;
        options.checkIntegrityHash = objOptions.checkIntegrityHash;
        options.integrityHashType = objOptions.integrityHashType;
        return options;
    }

    private JADEOptions addJADEOptionsForTarget(Operation operation, JADEOptions options) {
        if (operation.equals(Operation.remove)) {
            options.operation.setValue("delete");
            options.transactional.value(false);
            options.fileSpec.setValue("_not_exists_");
            options.atomicPrefix = objOptions.atomicPrefix;
            options.atomicSuffix = objOptions.atomicSuffix;
            options.bufferSize = objOptions.bufferSize;
            options.makeDirs.value(false);
            options.appendFiles.value(false);
            options.checkInterval = objOptions.checkInterval;
            options.checkRetry = objOptions.checkRetry;
            options.checkSize.value(false);
            options.forceFiles.value(false);
            options.overwriteFiles.value(true);
            options.recursive.value(false);
            options.skipTransfer.value(false);
            options.keepModificationDate = objOptions.keepModificationDate;
            options.verbose = objOptions.verbose;
            options.protocolCommandListener = objOptions.protocolCommandListener;
            options.zeroByteTransfer = objOptions.zeroByteTransfer;
            options.checkIntegrityHash.value(false);
            options.createIntegrityHashFile.value(false);
            options.integrityHashType = objOptions.integrityHashType;
        } else if (operation.equals(Operation.getlist)) {
            options.operation.setValue("getlist");
            options.transactional.value(true);
            options.fileSpec.setValue(".*");
            options.atomicPrefix = objOptions.atomicPrefix;
            options.atomicSuffix = objOptions.atomicSuffix;
            options.bufferSize = objOptions.bufferSize;
            options.makeDirs = objOptions.makeDirs;
            options.appendFiles = objOptions.appendFiles;
            options.checkInterval = objOptions.checkInterval;
            options.checkRetry = objOptions.checkRetry;
            options.checkSize = objOptions.checkSize;
            options.forceFiles = objOptions.forceFiles;
            options.overwriteFiles = objOptions.overwriteFiles;
            options.recursive = objOptions.recursive;
            options.skipTransfer = objOptions.skipTransfer;
            options.keepModificationDate = objOptions.keepModificationDate;
            options.verbose = objOptions.verbose;
            options.protocolCommandListener = objOptions.protocolCommandListener;
            options.zeroByteTransfer = objOptions.zeroByteTransfer;
            options.checkIntegrityHash = objOptions.checkIntegrityHash;
            options.createIntegrityHashFile = objOptions.createIntegrityHashFile;
            options.integrityHashType = objOptions.integrityHashType;
        } else {
            options.operation.setValue("copy");
            options.transactional.value(true);
            options.fileSpec.setValue(".*");
            options.atomicPrefix = objOptions.atomicPrefix;
            options.atomicSuffix = objOptions.atomicSuffix;
            options.bufferSize = objOptions.bufferSize;
            options.makeDirs = objOptions.makeDirs;
            options.appendFiles = objOptions.appendFiles;
            options.checkInterval = objOptions.checkInterval;
            options.checkRetry = objOptions.checkRetry;
            options.checkSize = objOptions.checkSize;
            options.forceFiles = objOptions.forceFiles;
            options.overwriteFiles = objOptions.overwriteFiles;
            options.recursive = objOptions.recursive;
            options.skipTransfer = objOptions.skipTransfer;
            options.keepModificationDate = objOptions.keepModificationDate;
            options.verbose = objOptions.verbose;
            options.protocolCommandListener = objOptions.protocolCommandListener;
            options.zeroByteTransfer = objOptions.zeroByteTransfer;
            options.checkIntegrityHash = objOptions.checkIntegrityHash;
            options.createIntegrityHashFile = objOptions.createIntegrityHashFile;
            options.integrityHashType = objOptions.integrityHashType;
        }
        return options;
    }

    private JADEOptions addJADEOptionsOnClient(JADEOptions options) {
        options.mailOnSuccess = objOptions.mailOnSuccess;
        options.mailOnError = objOptions.mailOnError;
        options.mailOnEmptyFiles = objOptions.mailOnEmptyFiles;
        options.sendTransferHistory = objOptions.sendTransferHistory;
        options.backgroundServiceHost = objOptions.backgroundServiceHost;
        options.backgroundServiceJobChainName = objOptions.backgroundServiceJobChainName;
        options.backgroundServicePort = objOptions.backgroundServicePort;
        options.schedulerHost = objOptions.schedulerHost;
        options.schedulerJobChain = objOptions.schedulerJobChain;
        options.schedulerPort = objOptions.schedulerPort;
        options.schedulerTransferMethod = objOptions.schedulerTransferMethod;
        options.history = objOptions.history;
        options.historyFileName = objOptions.historyFileName;
        options.historyFileAppendMode = objOptions.historyFileAppendMode;
        options.mandator = objOptions.mandator;
        options.compressFiles = objOptions.compressFiles;
        options.compressedFileExtension = objOptions.compressedFileExtension;
        options.cumulateFiles = objOptions.cumulateFiles;
        options.cumulativeFileName = objOptions.cumulativeFileName;
        options.cumulativeFileSeparator = objOptions.cumulativeFileSeparator;
        options.cumulativeFileDelete = objOptions.cumulativeFileDelete;
        options.expectedSizeOfResultSet = objOptions.expectedSizeOfResultSet;
        options.raiseErrorIfResultSetIs = objOptions.raiseErrorIfResultSetIs;
        options.logFilename = objOptions.logFilename;
        options.log4jPropertyFileName = objOptions.log4jPropertyFileName;
        options.resultSetFileName = objOptions.resultSetFileName;
        options.system_property_files = objOptions.system_property_files;
        return options;
    }

    private String getJadeOnDMZCommand(Operation operation, JADEOptions options) throws Exception {
        // options.history.setValue(getHistoryFilename());
        options.history.setValue("");
        options.getSource().user.setDefaultValue("");
        options.getTarget().user.setDefaultValue("");
        options.getSource().include.setValue("");
        options.getTarget().include.setValue("");
        options.getSource().keepass_attachment_property_name.setValue("");
        options.getTarget().keepass_attachment_property_name.setValue("");
        StringBuilder command = new StringBuilder(objOptions.jumpCommand.getValue() + " ");
        command.append("-SendTransferHistory=false ");
        command.append(options.getOptionsAsQuotedCommandLine());
        command.append(options.getSource().getOptionsAsQuotedCommandLine());
        if (!operation.equals(Operation.remove)) {
            command.append(options.getTarget().getOptionsAsQuotedCommandLine());
        }
        return command.toString();
    }

    private String getJadeOnDMZCommand4RemoveSource() throws Exception {
        JADEOptions opts = new JADEOptions();
        opts.operation.setValue("delete");
        opts.verbose = objOptions.verbose;
        opts.fileListName.setValue(getSourceListFilename());
        opts.forceFiles.value(false);
        objOptions.getSource().user.setDefaultValue("");
        objOptions.getSource().directory.setNotDirty();
        objOptions.getSource().postCommand.setNotDirty();
        objOptions.getSource().postTransferCommands.setNotDirty();
        objOptions.getSource().postFtpCommands.setNotDirty();
        objOptions.getSource().preCommand.setNotDirty();
        objOptions.getSource().preFtpCommands.setNotDirty();
        objOptions.getSource().preTransferCommands.setNotDirty();
        objOptions.getSource().tfnPostCommand.setNotDirty();
        StringBuilder command = new StringBuilder(objOptions.jumpCommand.getValue() + " ");
        objOptions.getSource().postTransferCommandsFinal.setNotDirty();
        command.append("-SendTransferHistory=false ");
        command.append(opts.getOptionsAsQuotedCommandLine());
        command.append(objOptions.getSource().getOptionsAsQuotedCommandLine());
        return command.toString();
    }

    public SOSFileList getFileList() {
        return fileList;
    }

    @Override
    public JADEOptions getOptions() {
        if (objOptions == null) {
            objOptions = new JADEOptions();
        }
        return objOptions;
    }

    private String normalizeDirectoryPath(String path) {
        path = path.replaceAll("\\\\", "/");
        return path.endsWith("/") ? path : path + "/";
    }

    private String getUUID() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        return uuid;
    }

    private String getSourceListFilename() {
        if (sourceListFilename == null) {
            sourceListFilename = normalizeDirectoryPath(objOptions.jumpDir.getValue()) + "jade-dmz-" + getUUID() + ".source.tmp";
        }
        return sourceListFilename;
    }

    private String getHistoryFilename() {
        if (historyFilename == null) {
            historyFilename = normalizeDirectoryPath(objOptions.jumpDir.getValue()) + "jade-dmz-" + getUUID() + ".history.csv";
        }
        return historyFilename;
    }

    public void setHistory(YadeHistory h) {
        history = h;
    }
}