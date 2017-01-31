package com.sos.DataExchange;

import java.util.UUID;

import org.apache.log4j.Logger;

import sos.util.SOSString;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.DataExchange.helpers.UpdateXmlToOptionHelper;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.VirtualFileSystem.DataElements.SOSFileList;
import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.sos.i18n.annotation.I18NResourceBundle;

@I18NResourceBundle(baseName = "SOSDataExchange", defaultLocale = "en")
public class Jade4DMZ extends JadeBaseEngine implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Jade4DMZ.class);
    private SOSFileList fileList = null;
    private String uuid = null;
    private String sourceListFilename = null;
    private String historyFilename = null;

    private enum Operation {
        copyToInternet, copyFromInternet
    }

    public void Execute() {
        String dir = normalizeDirectoryPath(getOptions().jumpDir.getValue());
        String uuid = "jade-dmz-" + getUUID();
        String subDir = dir + uuid;
        Operation operation = null;
        try {
            if (objOptions.operation.isOperationCopyToInternet() || objOptions.operation.isOperationSendUsingDMZ()) {
                operation = Operation.copyToInternet;
            } else if (objOptions.operation.isOperationCopyFromInternet() || objOptions.operation.isOperationReceiveUsingDMZ()) {
                operation = Operation.copyFromInternet;
            } else {
                throw new JobSchedulerException(Messages.getMsg("Jade4DMZ-E-001"));
            }
            UpdateXmlToOptionHelper updateHelper = new UpdateXmlToOptionHelper(getOptions());
            if (updateHelper.checkBefore()) {
                updateHelper.executeBefore();
                objOptions = updateHelper.getOptions();
            }
            transfer(operation, subDir);
        } catch (JobSchedulerException e) {
            throw e;
        } catch (Exception e) {
            throw new JobSchedulerException("Transfer failed", e);
        }
    }

    private void transfer(Operation operation, String dir) {
        LOGGER.info(String.format("operation = %s, jump dir = %s", operation, dir));
        JadeEngine jade = null;
        fileList = null;
        try {
            jade = new JadeEngine(getTransferOptions(operation, dir));
            jade.execute();
            if (operation.equals(Operation.copyFromInternet) && objOptions.removeFiles.value()) {
                jade.executeCommandOnSource(getJadeOnDMZCommand4RemoveSource());
            }
            fileList = jade.getFileList();
        } catch (Exception e) {
            throw new JobSchedulerException("Transfer failed", e);
        } finally {
            removeDirOnDMZ(jade, operation, dir);
            try {
                if (jade != null) {
                    jade.logout();
                }
            } catch (Exception e) {
                LOGGER.warn(String.format("Logout failed: %s", e.toString()));
            }
        }
    }

    private SOSConnection2OptionsAlternate createJumpOptions(String dir) {
        SOSConnection2OptionsAlternate options = new SOSConnection2OptionsAlternate();
        options.protocol.setValue("sftp");
        options.host.setValue(objOptions.jumpHost.getValue());
        options.port.setValue(objOptions.jumpPort.getValue());
        options.user.setValue(objOptions.jumpUser.getValue());
        options.password.setValue(objOptions.jumpPassword.getValue());
        options.sshAuthMethod.setValue(objOptions.jumpSshAuthMethod.getValue());
        options.sshAuthFile.setValue(objOptions.jumpSshAuthFile.getValue());
        options.strictHostKeyChecking.value(objOptions.jumpStrictHostkeyChecking.value());
        options.directory.setValue(dir);
        options.configuration_files.setValue(objOptions.jumpConfigurationFiles.getValue());
        return options;
    }

    private SOSConnection2OptionsAlternate setJumpProxy(SOSConnection2OptionsAlternate options) {
        options.proxyProtocol.setValue(objOptions.jumpProxyProtocol.getValue());
        options.proxyHost.setValue(objOptions.jumpProxyHost.getValue().trim());
        options.proxyPort.setValue(objOptions.jumpProxyPort.getValue().trim());
        options.proxyUser.setValue(objOptions.jumpProxyUser.getValue().trim());
        options.proxyPassword.setValue(objOptions.jumpProxyPassword.getValue());
        return options;
    }

    private JADEOptions createPostTransferOptions(String dir) {
        // From DMZ to Internet as PostTransferCommands
        JADEOptions options = new JADEOptions();
        options = addJADEOptionsForTarget(options);
        SOSConnection2OptionsAlternate sourceOptions = setLocalOptionsPrefixed("source_", dir);
        SOSConnection2OptionsAlternate targetOptions = objOptions.getTarget();
        options.getConnectionOptions().setSource(sourceOptions);
        options.getConnectionOptions().setTarget(targetOptions);
        return options;
    }

    private JADEOptions createPreTransferOptions(String dir) {
        // From Internet to DMZ as PreTransferCommands
        String prefix = "target_";
        JADEOptions options = new JADEOptions();
        options = addJADEOptionsForSource(options);
        SOSConnection2OptionsAlternate targetOptions = setLocalOptionsPrefixed(prefix, dir);
        targetOptions.preCommand.setValue(objOptions.jumpPreCommand.getValue());
        targetOptions.postCommand.setValue(objOptions.jumpPostCommandOnSuccess.getValue());
        targetOptions.preTransferCommands.setValue(objOptions.jumpPreTransferCommands.getValue());
        targetOptions.postTransferCommands.setValue(objOptions.jumpPostTransferCommandsOnSuccess.getValue());
        targetOptions.postTransferCommandsOnError.setValue(objOptions.jumpPostTransferCommandsOnError.getValue());
        targetOptions.postTransferCommandsFinal.setValue(objOptions.jumpPostTransferCommandsFinal.getValue());
        targetOptions.preCommand.setPrefix(prefix);
        targetOptions.postCommand.setPrefix(prefix);
        targetOptions.preTransferCommands.setPrefix(prefix);
        targetOptions.postTransferCommands.setPrefix(prefix);
        targetOptions.postTransferCommandsOnError.setPrefix(prefix);
        targetOptions.postTransferCommandsFinal.setPrefix(prefix);
        options.getConnectionOptions().setTarget(targetOptions);
        SOSConnection2OptionsAlternate sourceOptions = objOptions.getSource();
        options.getConnectionOptions().setSource(sourceOptions);
        // Remove source files at Internet as PostTransferCommands
        options.removeFiles.value(false);
        if (objOptions.removeFiles.value() || objOptions.resultSetFileName.isDirty()) {
            options.resultSetFileName.setValue(getSourceListFilename());
        }
        return options;
    }

    /** Transfer from Intranet/Internet to DMZ
     * 
     * @param operation
     * @param dir
     * @return */
    private JADEOptions getTransferOptions(Operation operation, String dir) throws Exception {
        LOGGER.debug(String.format("operation = %s, jump dir = %s", operation, dir));
        JADEOptions options = null;
        JADEOptions jumpCommandOptions;
        SOSConnection2OptionsAlternate jumpOptions = createJumpOptions(dir);
        jumpOptions = setJumpProxy(jumpOptions);
        if (operation.equals(Operation.copyToInternet)) {
            options = createTransferToDMZOptions(dir);
            jumpCommandOptions = createPostTransferOptions(dir);
            jumpOptions.preCommand.setValue(objOptions.jumpPreCommand.getValue());
            jumpOptions.postCommand.setValue(objOptions.jumpPostCommandOnSuccess.getValue());
            jumpOptions.preTransferCommands.setValue(objOptions.jumpPreTransferCommands.getValue());
            String firstCommand = SOSString.isEmpty(objOptions.jumpPostTransferCommandsOnSuccess.getValue()) ? ""
                    : (objOptions.jumpPostTransferCommandsOnSuccess.getValue().endsWith(";") ? objOptions.jumpPostTransferCommandsOnSuccess.getValue()
                            : objOptions.jumpPostTransferCommandsOnSuccess.getValue() + ";");
            jumpOptions.postTransferCommands.setValue(firstCommand + getJadeOnDMZCommand(jumpCommandOptions));
            jumpOptions.postTransferCommandsOnError.setValue(objOptions.jumpPostTransferCommandsOnError.getValue());
            jumpOptions.postTransferCommandsFinal.setValue(objOptions.jumpPostTransferCommandsFinal.getValue());
            jumpOptions = setDestinationOptionsPrefix("target_", jumpOptions);
            options.getConnectionOptions().setSource(objOptions.getSource());
            options.getConnectionOptions().setTarget(jumpOptions);
        } else {
            options = createTransferFromDMZOptions(dir);
            jumpCommandOptions = createPreTransferOptions(dir);
            jumpOptions.preTransferCommands.setValue(getJadeOnDMZCommand(jumpCommandOptions));
            jumpOptions = setDestinationOptionsPrefix("source_", jumpOptions);
            options.getConnectionOptions().setSource(jumpOptions);
            options.getConnectionOptions().setTarget(objOptions.getTarget());
        }
        options.setDmzOption("operation", operation.name());
        options.setDmzOption("history", getHistoryFilename());
        options.setDmzOption("resultfile", getSourceListFilename());
        return options;
    }

    private SOSConnection2OptionsAlternate setDestinationOptionsPrefix(String prefix, SOSConnection2OptionsAlternate options) {
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
        return options;
    }

    private SOSConnection2OptionsAlternate setLocalOptionsPrefixed(String prefix, String dir) {
        SOSConnection2OptionsAlternate options = new SOSConnection2OptionsAlternate();
        options.protocol.setValue("local");
        options.host.setValue(objOptions.jumpHost.getValue());
        options.directory.setValue(dir);
        options.protocol.setPrefix(prefix);
        options.host.setPrefix(prefix);
        options.directory.setPrefix(prefix);
        return options;
    }

    private JADEOptions createTransferFromDMZOptions(String dir) {
        JADEOptions options = new JADEOptions();
        options = addJADEOptionsOnClient(options);
        options = addJADEOptionsForTarget(options);
        options.sourceDir.setValue(dir);
        options.targetDir.setValue(objOptions.getTarget().directory.getValue());
        return options;
    }

    /** Transfer from Intranet/Internet to DMZ without changes
     * 
     * @param targetDir
     * @return */
    private JADEOptions createTransferToDMZOptions(String dir) {
        JADEOptions options = new JADEOptions();
        options = addJADEOptionsOnClient(options);
        options = addJADEOptionsForSource(options);
        options.sourceDir.setValue(objOptions.getSource().directory.getValue());
        options.targetDir.setValue(dir);
        options.removeFiles = objOptions.removeFiles;
        return options;
    }

    private JADEOptions addJADEOptionsForSource(JADEOptions options) {
        options.operation.setValue("copy");
        options.transactional.value(true);
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
        options.zeroByteTransfer = objOptions.zeroByteTransfer;
        options.checkIntegrityHash = objOptions.checkIntegrityHash;
        options.integrityHashType = objOptions.integrityHashType;
        return options;
    }

    private JADEOptions addJADEOptionsForTarget(JADEOptions options) {
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
        options.zeroByteTransfer = objOptions.zeroByteTransfer;
        options.checkIntegrityHash = objOptions.checkIntegrityHash;
        options.createIntegrityHashFile = objOptions.createIntegrityHashFile;
        options.integrityHashType = objOptions.integrityHashType;
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
        options.historyRepeat = objOptions.historyRepeat;
        options.historyRepeatInterval = objOptions.historyRepeatInterval;
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

    private String getJadeOnDMZCommand(JADEOptions options) {
        options.file.setNotDirty();
        options.user.setDefaultValue("");
        options.history.setValue(getHistoryFilename());
        options.getSource().user.setDefaultValue("");
        options.getTarget().user.setDefaultValue("");
        StringBuilder command = new StringBuilder(objOptions.jumpCommand.getValue() + " ");
        command.append("-SendTransferHistory=false ");
        command.append(options.getOptionsAsQuotedCommandLine());
        command.append(options.getSource().getOptionsAsQuotedCommandLine());
        command.append(options.getTarget().getOptionsAsQuotedCommandLine());
        return command.toString();
    }

    private String getJadeOnDMZCommand4RemoveSource() {
        JADEOptions opts = new JADEOptions();
        opts.operation.setValue("delete");
        opts.verbose = objOptions.verbose;
        opts.fileListName.setValue(getSourceListFilename());
        opts.forceFiles.value(false);
        opts.user.setDefaultValue("");
        objOptions.user.setDefaultValue("");
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

    private void removeDirOnDMZ(JadeEngine jade, Operation operation, String dir) {
        try {
            if (jade == null) {
                return;
            }
            String command = getRemoveDirCommand(dir);
            LOGGER.info(command);
            if (operation.equals(Operation.copyToInternet)) {
                jade.executeCommandOnTarget(command);
            } else {
                jade.executeCommandOnSource(command);
            }
        } catch (Exception ex) {
            LOGGER.warn(String.format("%s", ex.toString()));
        }
    }

    private String getRemoveDirCommand(String dir) {
        if (objOptions.jumpPlatform.isWindows()) {
            dir = dir.replace('/', '\\');
            return "rmdir \"" + dir + "\" /s /q;del /F /Q " + dir + "* 2>nul";
        } else {
            return "rm -f -R " + dir + "*";
        }
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

    @Override
    public void run() {
        try {
            Execute();
        } catch (JobSchedulerException e) {
            throw e;
        } catch (Exception e) {
            throw new JobSchedulerException("abort", e);
        }
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

}