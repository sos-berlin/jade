package com.sos.DataExchange;

import static com.sos.DataExchange.SOSJadeMessageCodes.SOSJADE_D_0200;
import static com.sos.DataExchange.SOSJadeMessageCodes.SOSJADE_E_0098;
import static com.sos.DataExchange.SOSJadeMessageCodes.SOSJADE_E_0099;
import static com.sos.DataExchange.SOSJadeMessageCodes.SOSJADE_E_0100;
import static com.sos.DataExchange.SOSJadeMessageCodes.SOSJADE_E_0101;
import static com.sos.DataExchange.SOSJadeMessageCodes.SOSJADE_I_0100;
import static com.sos.DataExchange.SOSJadeMessageCodes.SOSJADE_I_0101;
import static com.sos.DataExchange.SOSJadeMessageCodes.SOSJADE_I_0102;
import static com.sos.DataExchange.SOSJadeMessageCodes.SOSJADE_I_0103;
import static com.sos.DataExchange.SOSJadeMessageCodes.SOSJADE_I_0115;
import static com.sos.DataExchange.SOSJadeMessageCodes.SOSJADE_T_0012;
import static com.sos.DataExchange.SOSJadeMessageCodes.SOSJADE_T_0013;
import static com.sos.DataExchange.SOSJadeMessageCodes.TRANSACTION_ABORTED;
import static com.sos.DataExchange.SOSJadeMessageCodes.TRANSFER_ABORTED;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.DataExchange.helpers.UpdateXmlToOptionHelper;
import com.sos.DataExchange.history.YadeHistory;
import com.sos.JSHelper.Basics.VersionInfo;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.JSHelper.Options.JSOptionsClass;
import com.sos.JSHelper.Options.SOSOptionBoolean;
import com.sos.JSHelper.Options.SOSOptionFolderName;
import com.sos.JSHelper.Options.SOSOptionRegExp;
import com.sos.JSHelper.Options.SOSOptionTime;
import com.sos.JSHelper.Options.SOSOptionTransferType;
import com.sos.JSHelper.concurrent.SOSThreadPoolExecutor;
import com.sos.JSHelper.interfaces.IJadeEngine;
import com.sos.JSHelper.interfaces.IJobSchedulerEventHandler;
import com.sos.JSHelper.io.Files.JSFile;
import com.sos.VirtualFileSystem.DataElements.SOSFileList;
import com.sos.VirtualFileSystem.DataElements.SOSFileListEntry;
import com.sos.VirtualFileSystem.DataElements.SOSFileListEntry.TransferStatus;
import com.sos.VirtualFileSystem.DataElements.SOSTransferStateCounts;
import com.sos.VirtualFileSystem.DataElements.SOSVfsConnectionFactory;
import com.sos.VirtualFileSystem.HTTP.SOSVfsHTTP;
import com.sos.VirtualFileSystem.Interfaces.ISOSVfsFileTransfer;
import com.sos.VirtualFileSystem.Interfaces.ISOSVirtualFile;
import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.sos.VirtualFileSystem.common.SOSFileEntry;
import com.sos.exception.SOSYadeSourceConnectionException;
import com.sos.exception.SOSYadeTargetConnectionException;

import sos.net.SOSMail;
import sos.net.mail.options.SOSSmtpMailOptions;
import sos.net.mail.options.SOSSmtpMailOptions.enuMailClasses;
import sos.util.SOSString;

public class SOSDataExchangeEngine extends JadeBaseEngine implements IJadeEngine {

    protected static final String JADE_LOGGER_NAME = "JadeReportLog";
    private static final Logger JADE_REPORT_LOGGER = LoggerFactory.getLogger(JADE_LOGGER_NAME);

    private static final Logger LOGGER = LoggerFactory.getLogger(SOSDataExchangeEngine.class);
    private static final boolean isDebugEnabled = LOGGER.isDebugEnabled();

    private enum PollingMethod {
        PollTimeout, PollingServerDuration, PollForever
    }

    private static final int POLLING_MAX_RERUNS_ON_CONNECTION_ERROR = 1_000;
    private static final int POLLING_WAIT_INTERVAL_ON_CONNECTION_ERROR = 10;// seconds
    private static final int POLLING_WAIT_INTERVAL_ON_TRANSFER_ERROR = 30;// seconds
    private static final String KEYWORD_LAST_ERROR = "last_error";
    private static final String KEYWORD_STATE = "state";
    private static final String KEYWORD_SUCCESSFUL_TRANSFERS = "successful_transfers";
    private static final String KEYWORD_FAILED_TRANSFERS = "failed_transfers";
    private static final String KEYWORD_SKIPPED_TRANSFERS = "skipped_transfers";
    private static final String KEYWORD_STATUS = "status";

    private SOSVfsConnectionFactory factory = null;
    private IJobSchedulerEventHandler historyHandler = null;
    private IJadeEngineClientHandler engineClientHandler = null;// for DMZ

    private ISOSVfsFileTransfer sourceClient = null;
    private ISOSVfsFileTransfer targetClient = null;
    private SOSFileList sourceFileList = null;

    private long countPollingServerFiles = 0;

    private Instant startTime;
    private Instant endTime;

    public SOSDataExchangeEngine() throws Exception {
        getOptions();
    }

    public SOSDataExchangeEngine(final HashMap<String, String> settings) throws Exception {
        getOptions();
        objOptions.setAllOptions(settings);
    }

    public SOSDataExchangeEngine(final Properties properties) throws Exception {
        getOptions();
    }

    public SOSDataExchangeEngine(final JADEOptions jadeOptions) throws Exception {
        super(jadeOptions);
        objOptions = jadeOptions;
        if (objOptions.settings.isDirty()) {
            objOptions.readSettingsFile();
        }
    }

    public boolean checkSourceFilesSteady() {
        boolean steady = true;
        if (objOptions.checkSteadyStateOfFiles.isTrue() && sourceFileList != null && sourceFileList.size() > 0) {
            String msg = "[start]checkSteadyStateOfFiles";
            LOGGER.info(msg);
            objJSJobUtilities.setStateText(msg);

            long interval = objOptions.checkSteadyStateInterval.getTimeAsSeconds();
            for (int i = 0; i < objOptions.checkSteadyCount.value(); i++) {
                steady = true;

                String position = String.format("%sof%s", i + 1, objOptions.checkSteadyCount.value());
                LOGGER.info(String.format("[%s][wait]%ss...", position, interval));

                doSleep(interval);
                for (SOSFileListEntry entry : sourceFileList.getList()) {
                    if (!checkSourceFileSteady(entry, position)) {
                        steady = false;
                    }
                }
                if (steady) {
                    LOGGER.info(String.format("[%s][all files seem steady]extra waiting %ss for late comers.", position, interval));
                    doSleep(interval);
                    for (SOSFileListEntry entry : sourceFileList.getList()) {
                        entry.setSourceFileSteady(false);
                        if (!checkSourceFileSteady(entry, position)) {
                            steady = false;
                        }
                    }
                }
                if (steady) {
                    LOGGER.info(String.format("[%s][break]all files are steady.", position));
                    break;
                }
            }
            if (!steady) {
                msg = "not all files are steady";
                LOGGER.error(msg);
                for (SOSFileListEntry entry : sourceFileList.getList()) {
                    if (!entry.isSourceFileSteady()) {
                        LOGGER.info(String.format("[%s]file is not steady", entry.getSourceFileName()));
                    }
                }
                if (objOptions.steadyStateErrorState.isDirty()) {
                    objJSJobUtilities.setNextNodeState(objOptions.steadyStateErrorState.getValue());
                } else {
                    throw new JobSchedulerException(msg);
                }
            }
        }
        return steady;
    }

    private boolean checkSourceFileSteady(SOSFileListEntry entry, String position) {
        boolean steady = true;
        if (!entry.isSourceFileSteady()) {
            if (entry.getSourceFileLastCheckedFileSize() < 0) {
                entry.setSourceFileLastCheckedFileSize(entry.getFileSize());
                if (entry.getEntry() != null) {
                    entry.getEntry().setFilesize(entry.getSourceFileLastCheckedFileSize());
                }
            }
            entry.setSourceFileSteadyProperties(sourceClient.getFileHandle(entry.getSourceFileName()));
            if (entry.getSourceFileLastCheckedFileSize().equals(entry.getFileSize())) {
                entry.setSourceFileSteady(true);
                if (isDebugEnabled) {
                    LOGGER.debug(String.format("[%s][%s][%s bytes]not changed", position, entry.getSourceFileName(), entry
                            .getSourceFileLastCheckedFileSize()));
                }
            } else {
                steady = false;
                LOGGER.info(String.format("[%s][%s][%s -> %s bytes]changed", position, entry.getSourceFileName(), entry
                        .getSourceFileLastCheckedFileSize(), entry.getFileSize()));
            }
            entry.setSourceFileLastCheckedFileSize(entry.getFileSize());
            if (entry.getEntry() != null) {
                entry.getEntry().setFilesize(entry.getSourceFileLastCheckedFileSize());
            }
        }
        return steady;
    }

    private void doLogout(ISOSVfsFileTransfer client) throws Exception {
        if (client != null) {
            client.logout();
            client.disconnect();
            client.close();
            client = null;
        }
    }

    protected void showSummary() {
        printState();
        showResult();
    }

    private String getPoolingServerDurationValue() {
        String val = getOptions().pollingServerDuration.getValue();
        boolean isNumeric = val.chars().allMatch(Character::isDigit);
        return isNumeric ? val + "s" : val + " (" + getOptions().pollingServerDuration.getTimeAsSeconds() + "s)";
    }

    private String[] doPollingForFiles(long pollingServerStartTime, PollingMethod pollingMethod) {
        String[] fileList = null;
        if (objOptions.isFilePollingEnabled()) {
            long pollInterval = objOptions.pollInterval.getTimeAsSeconds();
            String sourceDir = objOptions.sourceDir.getValue();

            ISOSVirtualFile sourceFile = null;
            long pollTimeout = getPollTimeout();
            long currentFilesCount = sourceFileList.size();
            boolean isSourceDirFounded = false;
            long filesCount = 0;
            long currentPollingTime = 0;

            PollingLoop: while (true) {
                if (currentPollingTime == 0) {
                    if (isDebugEnabled) {
                        LOGGER.debug(String.format("[start]%s minutes...", getPollTimeoutText()));
                    }
                }
                if (currentPollingTime > pollTimeout) {
                    String msg = String.format("[end]%s minutes", getPollTimeoutText());
                    if (isDebugEnabled) {
                        LOGGER.debug(msg);
                    }
                    objJSJobUtilities.setStateText(msg);
                    break PollingLoop;
                }
                if (!isSourceDirFounded) {
                    sourceFile = sourceClient.getFileHandle(sourceDir);
                    if (objOptions.pollingWait4SourceFolder.isFalse()) {
                        if (sourceFile.notExists()) {
                            throw new JobSchedulerException(String.format(
                                    "[WaitForSourceFolder=false][%s]source directory not found. Polling terminated.", sourceDir));
                        }
                        isSourceDirFounded = true;
                    } else {
                        try {
                            if (sourceFile.notExists()) {
                                LOGGER.info(String.format("[%s]directory not found. Wait for the directory due to polling mode...", sourceDir));
                            } else {
                                isSourceDirFounded = true;
                            }
                        } catch (Exception e) {
                            if (isDebugEnabled) {
                                LOGGER.debug(String.format("[%s][directory not found. Wait for the directory due to polling mode...]%s", sourceDir, e
                                        .toString()), e);
                            }
                            isSourceDirFounded = false;
                        }
                    }
                    if (isDebugEnabled) {
                        LOGGER.debug(String.format("isSourceDirFounded=%s", isSourceDirFounded));
                    }
                }
                if (isSourceDirFounded) {
                    try {
                        if (objOptions.oneOrMoreSingleFilesSpecified()) {
                            currentFilesCount = 0;
                            objOptions.pollMinfiles.value(sourceFileList.count());
                            for (SOSFileListEntry entry : sourceFileList.getList()) {
                                if (entry.isSourceFileExists()) {
                                    currentFilesCount++;
                                    entry.setParent(sourceFileList);
                                }
                            }
                        } else {
                            String integrityHashFileExtention = objOptions.checkIntegrityHash.isTrue() ? "." + objOptions.integrityHashType.getValue()
                                    : null;
                            selectFilesOnSource(true, sourceFile, objOptions.sourceDir, objOptions.fileSpec, objOptions.recursive,
                                    integrityHashFileExtention);
                            currentFilesCount = sourceFileList.count();
                        }
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }

                    if (isDebugEnabled) {
                        LOGGER.debug(String.format("[pollMinfiles=%s][currentFilesCount=%s]", objOptions.pollMinfiles.value(), currentFilesCount));
                    }

                    if ((objOptions.pollMinfiles.value() == 0 && currentFilesCount > 0) || (objOptions.pollMinfiles.value() > 0
                            && currentFilesCount >= objOptions.pollMinfiles.value())) {

                        if (isDebugEnabled) {
                            LOGGER.debug("break");
                        }
                        break PollingLoop;
                    }
                }

                String msg = String.format("[wait]%s seconds...", pollInterval);
                LOGGER.debug(msg);
                objJSJobUtilities.setStateText(msg);

                doSleep(pollInterval);
                currentPollingTime += pollInterval;

                if (filesCount >= currentFilesCount && filesCount != 0) {
                    if (objOptions.waitingForLateComers.isTrue()) {
                        objOptions.waitingForLateComers.setFalse();
                    } else {
                        break PollingLoop;
                    }
                }
                tryReconnectByPolling(pollingServerStartTime, currentPollingTime, pollingMethod);
            }
        }
        return fileList;
    }

    private void tryReconnectByPolling(long pollingServerStartTime, long currentPollingTime, PollingMethod pollingMethod) {
        tryReconnectClientByPolling(pollingServerStartTime, currentPollingTime, pollingMethod, "source", sourceClient);
        if (objOptions.isNeedTargetClient()) {
            tryReconnectClientByPolling(pollingServerStartTime, currentPollingTime, pollingMethod, "target", targetClient);
        }
    }

    private void tryReconnectClientByPolling(long pollingServerStartTime, long currentPollingTime, PollingMethod pollingMethod, String range,
            ISOSVfsFileTransfer client) {
        if (!client.isConnected()) {
            LOGGER.warn(String.format("[%s]is not connected. try to reconnect...", range));
            try {
                doLogout(client);
            } catch (Throwable e) {
                LOGGER.error(String.format("[%s][doLogout]%s", range, e.toString()), e);
            }

            boolean run = true;
            int count = 0;
            while (run) {
                count++;
                try {
                    client.reconnect(range.equalsIgnoreCase("source") ? objOptions.getSource() : objOptions.getTarget());
                    LOGGER.info(String.format("[%s]reconnected. continue polling after error ...", range));
                    run = false;
                } catch (Throwable e) {
                    if (pollingMethod.equals(PollingMethod.PollForever)) {
                        if (count >= POLLING_MAX_RERUNS_ON_CONNECTION_ERROR) {
                            throw new JobSchedulerException(e);
                        }
                    } else {
                        long currentTime = System.currentTimeMillis() / 1_000;
                        long pollingTime = pollingMethod.equals(PollingMethod.PollingServerDuration) ? pollingServerStartTime : currentPollingTime;
                        long duration = currentTime - pollingTime;
                        if (duration >= getPollTimeout()) {
                            throw new JobSchedulerException(e);
                        }
                    }

                    String error = String.format("[%s][reconnect]exception occured, wait 10s and try again (%s of %s)- %s", range, count,
                            POLLING_MAX_RERUNS_ON_CONNECTION_ERROR, e.toString());
                    if (count % 100 == 1) {
                        JobSchedulerException.LastErrorMessage = error;
                        doProcessMail(enuMailClasses.MailOnError);
                        JobSchedulerException.LastErrorMessage = "";
                    }

                    LOGGER.error(error, e);
                    doSleep(POLLING_WAIT_INTERVAL_ON_CONNECTION_ERROR);
                }
            }
        }
    }

    private void doProcessMail(final enuMailClasses mailClasses) {
        SOSSmtpMailOptions mailOptions = objOptions.getMailOptions();
        SOSSmtpMailOptions mailOptionsWithMailClasses = mailOptions.getOptions(mailClasses);
        if (mailOptionsWithMailClasses == null || !mailOptionsWithMailClasses.FileNotificationTo.isDirty()) {
            mailOptionsWithMailClasses = mailOptions;
        }
        processSendMail(mailClasses, mailOptionsWithMailClasses);
    }

    private void doSleep(final long time) {
        try {
            Thread.sleep(time * 1_000);
        } catch (InterruptedException e) {
            // nothing to do
        }
    }

    @Override
    public void execute() throws Exception {
        setLogger();
        objOptions.getTextProperties().put("version", VersionInfo.VERSION_STRING);
        objOptions.logFilename.setLogger(JADE_REPORT_LOGGER);
        YadeHistory history = null;
        if (historyHandler != null) {
            history = (YadeHistory) historyHandler;
        }
        try {
            startTime = Instant.now();
            JobSchedulerException.LastErrorMessage = "";
            try {
                getOptions().checkMandatory();
                if (history != null) {
                    history.beforeTransfer(objOptions, null);
                }
            } finally {
                showBanner();
            }
            UpdateXmlToOptionHelper updateHelper = new UpdateXmlToOptionHelper(objOptions);
            if (updateHelper.checkBefore()) {
                updateHelper.executeBefore();
                objOptions = updateHelper.getOptions();
            }

            transfer();
            transferAfterCheck();

            if (history != null) {
                history.afterTransfer();
            }
            if (!JobSchedulerException.LastErrorMessage.isEmpty()) {
                throw new JobSchedulerException(JobSchedulerException.LastErrorMessage);
            }
            endTime = Instant.now();
        } catch (SOSYadeSourceConnectionException | SOSYadeTargetConnectionException e) {
            if (history != null) {
                history.onException(e);
            }
            throw new JobSchedulerException(e.getCause());
        } catch (JobSchedulerException e) {
            if (history != null) {
                history.onException(e);
            }
            throw e;
        } catch (Exception e) {
            if (history != null) {
                history.onException(e);
            }
            throw new JobSchedulerException(e.toString(), e);
        } finally {
            if (engineClientHandler != null) {
                engineClientHandler.onEnd(this);
            }

            try {
                logout();
            } catch (Exception ex) {
                LOGGER.warn(String.format("exception on logout: %s", ex.toString()), ex);
            }

            if (endTime == null) {
                endTime = Instant.now();
            }
            if (history != null) {
                history.sendYadeEventOnEnd();
            }

            printState();
            showResult();
            sendNotifications();

        }
    }

    protected void showResult() {
        String msg = "";
        if (objOptions.bannerFooter.isDirty()) {
            msg = objOptions.bannerFooter.getJSFile().getContent();
        } else {
            msg = SOSJadeMessageCodes.SOSJADE_T_0011.get();
        }
        setTextProperties();
        msg = objOptions.replaceVars(msg);
        JADE_REPORT_LOGGER.info(msg);
        LOGGER.info(msg);
    }

    private String showBannerHeaderSource(SOSConnection2OptionsAlternate options, boolean isSource) {
        StringBuilder sb = new StringBuilder();
        String pattern4String = "  | %-22s= %s%n";
        String pattern4Bool = "  | %-22s= %b%n";
        String pattern4IntegrityHash = "  | %-22s= %b (%s)%n";
        String pattern4Rename = "  | %-22s= %s -> %s%n";
        sb.append(String.format(pattern4String, "Protocol", options.protocol.getValue()));
        sb.append(String.format(pattern4String, "Host", options.host.getValue()));
        try {
            sb.append(String.format(pattern4String, "IP", InetAddress.getByName(options.host.getValue()).getHostAddress()));
        } catch (UnknownHostException e) {
            sb.append(String.format(pattern4String, "IP", "could not be resolved!"));
        }
        if (!options.protocol.isLocal()) {
            sb.append(String.format(pattern4String, "User", options.user.getValue()));
            if (options.protocol.getEnum() != SOSOptionTransferType.enuTransferTypes.sftp && options.protocol
                    .getEnum() != SOSOptionTransferType.enuTransferTypes.ftp && options.protocol
                            .getEnum() != SOSOptionTransferType.enuTransferTypes.zip) {
                sb.append(String.format(pattern4String, "AuthMethod", options.sshAuthMethod.getValue()));
            }
            if (options.protocol.getEnum() == SOSOptionTransferType.enuTransferTypes.sftp) {
                if (options.required_authentications.isNotEmpty()) {
                    sb.append(String.format(pattern4String, "RequiredAuths", options.required_authentications.getValue()));
                    sb.append(String.format(pattern4String, "Password", "***"));
                    sb.append(String.format(pattern4String, "AuthFile", "***"));
                    if (options.passphrase.isNotEmpty()) {
                        sb.append(String.format(pattern4String, "Passphrase", "***"));
                    }
                } else {
                    String am = "";
                    if (options.password.isNotEmpty() && options.authFile.isNotEmpty()) {
                        am = "password,publickey";
                    } else {
                        am = options.authMethod.getValue();
                    }
                    String pa = options.preferred_authentications.getValue();
                    if (SOSString.isEmpty(pa)) {
                        pa = am;
                    }
                    sb.append(String.format(pattern4String, am.indexOf(",") == -1 ? "AuthMethod" : "AuthMethods", am));
                    if (pa.indexOf(",") != -1) {
                        sb.append(String.format(pattern4String, "PreferredAuths", pa));
                    }
                    if (options.password.isNotEmpty()) {
                        sb.append(String.format(pattern4String, "Password", "***"));
                    }
                    if (options.authFile.isNotEmpty()) {
                        sb.append(String.format(pattern4String, "AuthFile", "***"));
                    }
                    if (options.passphrase.isNotEmpty()) {
                        sb.append(String.format(pattern4String, "Passphrase", "***"));
                    }
                }

            } else {
                sb.append(String.format(pattern4String, "Password", "***"));
            }

            if (options.protocol.getEnum() == SOSOptionTransferType.enuTransferTypes.ftp) {
                sb.append(String.format(pattern4Bool, "Passive", options.passiveMode.value()));
                sb.append(String.format(pattern4String, "TransferMode", options.transferMode.getValue()));
            }
        }
        if (isSource) {
            if (options.directory.isDirty()) {
                sb.append(String.format(pattern4String, "Directory", options.directory.getValue()));
            }
            if (getOptions().filePath.isNotEmpty()) {
                sb.append(String.format(pattern4String, "FilePath", getOptions().filePath.getValue()));
            }
            if (getOptions().fileListName.isDirty()) {
                sb.append(String.format(pattern4String, "FileList", getOptions().fileListName.getValue()));
            }
            if (getOptions().fileSpec.isDirty()) {
                sb.append(String.format(pattern4String, "FileSpec", getOptions().fileSpec.getValue()));
            }
            sb.append(String.format(pattern4Bool, "ErrorWhenNoFilesFound", getOptions().forceFiles.value()));
            sb.append(String.format(pattern4Bool, "Recursive", getOptions().recursive.value()));
            if (getOptions().skipTransfer.isFalse()) {
                sb.append(String.format(pattern4Bool, "Remove", getOptions().removeFiles.value()));
                if (getOptions().pollInterval.isDirty() || getOptions().pollTimeout.isDirty() || getOptions().pollMinfiles.isDirty()) {
                    sb.append(String.format(pattern4String, "PollingInterval", getOptions().pollInterval.getValue() + "s"));
                    sb.append(String.format(pattern4String, "PollingTimeout", getOptions().pollTimeout.getValue() + "m"));
                    sb.append(String.format(pattern4String, "PollingMinFiles", getOptions().pollMinfiles.getValue()));
                    if (getOptions().pollingWait4SourceFolder.isDirty()) {
                        sb.append(String.format(pattern4String, "PollingWaitForSourceFolder", getOptions().pollingWait4SourceFolder.getValue()));
                    }
                    if (getOptions().pollingServer.isDirty()) {
                        sb.append(String.format(pattern4String, "PollingServer", getOptions().pollingServer.getValue()));
                    }
                    if (getOptions().pollingServerDuration.isDirty()) {
                        sb.append(String.format(pattern4String, "PollingServerDuration", getPoolingServerDurationValue()));
                    }
                    if (getOptions().pollingServerPollForever.isDirty()) {
                        sb.append(String.format(pattern4String, "PollForever", getOptions().pollingServerPollForever.getValue()));
                    }
                }
                if (getOptions().checkSteadyStateOfFiles.isTrue()) {
                    sb.append(String.format(pattern4String, "CheckSteadyInterval", getOptions().checkSteadyStateInterval.getValue()));
                    sb.append(String.format(pattern4String, "CheckSteadyCount", getOptions().checkSteadyCount.getValue()));
                }
            }
            if (getOptions().checkIntegrityHash.isDirty()) {
                sb.append(String.format(pattern4IntegrityHash, "CheckIntegrity", getOptions().checkIntegrityHash.value(),
                        getOptions().integrityHashType.getValue()));
            }
        } else {
            sb.append(String.format(pattern4String, "Directory", options.directory.getValue()));
            sb.append(String.format(pattern4Bool, "OverwriteFiles", getOptions().overwriteFiles.value()));
            if (getOptions().appendFiles.isTrue()) {
                sb.append(String.format(pattern4Bool, "AppendFiles", getOptions().appendFiles.value()));
            }
            if (getOptions().compressFiles.isTrue()) {
                sb.append(String.format(pattern4Bool, "CompressFiles", getOptions().compressFiles.value()));
            }
            if (getOptions().cumulateFiles.isTrue()) {
                sb.append(String.format(pattern4Bool, "CumulateFiles", getOptions().cumulateFiles.value()));
                sb.append(String.format(pattern4Bool, "CumulateFileName", getOptions().cumulativeFileName.getValue()));
            }
            if (getOptions().atomicPrefix.isDirty()) {
                sb.append(String.format(pattern4String, "AtomicPrefix", getOptions().atomicPrefix.getValue()));
            }
            if (getOptions().atomicSuffix.isDirty()) {
                sb.append(String.format(pattern4String, "AtomicSuffix", getOptions().atomicSuffix.getValue()));
            }
            if (getOptions().createIntegrityHashFile.isDirty()) {
                sb.append(String.format(pattern4IntegrityHash, "CreateIntegrityFile", getOptions().createIntegrityHashFile.value(),
                        getOptions().integrityHashType.getValue()));
            }
        }
        if (options.replacement.isDirty() && options.replacing.isNotEmpty()) {
            sb.append(String.format(pattern4Rename, "Rename", options.replacing.getValue(), options.replacement.getValue()));
        }
        return sb.toString();
    }

    private void showBanner() {
        StringBuilder sb = new StringBuilder();
        if (objOptions.bannerHeader.isDirty()) {
            // this parameter should deprecated
            sb = new StringBuilder(objOptions.replaceVars(objOptions.bannerHeader.getJSFile().getContent()));
        } else {
            String timestamp = "";
            try {
                timestamp = SOSOptionTime.getCurrentDateAsString(getOptions().dateFormatMask.getValue()) + " " + SOSOptionTime.getCurrentTimeAsString(
                        getOptions().timeFormatMask.getValue());
            } catch (Exception e) {
                timestamp = getOptions().getDate() + " " + getOptions().getTime();
            }
            String pattern4String = "  %-24s= %s%n";
            String pattern4Bool = "  %-24s= %b%n";
            String pattern4SourceTarget = "%n  +------------%s------------%n";
            sb.append(String.format("%n%072d%n", 0).replace('0', '*'));
            sb.append(String.format("*%70s*%n", " "));
            sb.append(String.format("*%25s%-45s*%n", "YADE", " - Managed File Transfer"));
            sb.append(String.format("*%44s%-26s*%n", "-----www.sos-berlin.com", "-----"));
            sb.append(String.format("*%70s*%n", " "));
            sb.append(String.format("%072d%n", 0).replace('0', '*'));
            sb.append(String.format(pattern4String, "Version", VersionInfo.VERSION_STRING));
            sb.append(String.format(pattern4String, "Date", timestamp));
            sb.append(String.format(pattern4String, "SettingsFile", getOptions().getOriginalSettingsFile() == null ? "" : getOptions()
                    .getOriginalSettingsFile()));
            sb.append(String.format(pattern4String, "Profile", getOptions().profile.getValue()));
            sb.append(String.format(pattern4String, "Operation", getOptions().operation.getValue()));
            sb.append(String.format(pattern4Bool, "Transactional", getOptions().transactional.value()));
            if (getOptions().skipTransfer.isDirty()) {
                sb.append(String.format(pattern4Bool, "SkipTransfer", getOptions().skipTransfer.value()));
            }
            if (getOptions().history.isDirty()) {
                sb.append(String.format(pattern4String, "History", getOptions().history.getValue()));
            }
            if (getOptions().logFilename.isDirty()) {
                sb.append(String.format(pattern4String, "LogFile", getOptions().logFilename.getValue()));
            }
            sb.append(String.format(pattern4SourceTarget, "Source"));
            sb.append(showBannerHeaderSource(getOptions().getSource(), true));
            if (objOptions.isNeedTargetClient()) {
                sb.append(String.format(pattern4SourceTarget, "Target"));
                sb.append(showBannerHeaderSource(getOptions().getTarget(), false));
            }
            sb.append("\n");
        }
        JADE_REPORT_LOGGER.info(sb.toString());
        LOGGER.info(sb.toString());
    }

    public SOSFileList getFileList() {
        return sourceFileList;
    }

    private long getPollTimeout() {
        return objOptions.pollTimeout.isDirty() ? objOptions.pollTimeout.value() * 60 : objOptions.pollingDuration.getTimeAsSeconds();
    }

    private String getPollTimeoutText() {
        return objOptions.pollTimeout.isDirty() ? objOptions.pollTimeout.getValue() : objOptions.pollingDuration.getValue();
    }

    private List<SOSFileEntry> getSingleFileNames() {
        List<SOSFileEntry> entries = new ArrayList<>();
        String localDir = objOptions.sourceDir.getValueWithFileSeparator();

        if (objOptions.filePath.isNotEmpty()) {
            String filePath = objOptions.filePath.getValue();
            if (isDebugEnabled) {
                LOGGER.debug(String.format("single file(s) specified : '%1$s'", filePath));
            }
            try {
                String[] arr = filePath.split(";");
                for (String filename : arr) {
                    filename = filename.trim();
                    if (!filename.isEmpty()) {
                        if (!localDir.trim().isEmpty() && !isAPathName(filename)) {
                            filename = localDir + filename;
                        }
                        SOSFileEntry entry = sourceClient.getFileEntry(filename);
                        if (entry == null) {
                            LOGGER.info(String.format("[%s]not found", filename));
                        } else {
                            if (isDebugEnabled) {
                                LOGGER.debug(String.format("[%s]found", filename));
                            }
                            entries.add(entry);
                        }
                    }
                }
            } catch (Exception e) {
                throw new JobSchedulerException(String.format("error while reading file_path='%1$s': %2$s", objOptions.filePath.getValue(), e
                        .toString()), e);
            }
        }
        if (objOptions.fileListName.isNotEmpty()) {
            String fileListName = objOptions.fileListName.getValue();
            JSFile file = new JSFile(fileListName);
            if (file.exists()) {
                try {
                    StringBuffer line = null;
                    String filename = "";
                    while ((line = file.getLine()) != null) {
                        filename = line.toString().trim();
                        // see filePath above
                        if (!filename.isEmpty()) {
                            if (!localDir.trim().isEmpty() && !isAPathName(filename)) {
                                filename = localDir + filename;
                            }
                            SOSFileEntry entry = sourceClient.getFileEntry(filename);
                            if (entry == null) {
                                LOGGER.info(String.format("[%s]not found", filename));
                            } else {
                                if (isDebugEnabled) {
                                    LOGGER.debug(String.format("[%s]found", filename));
                                }
                                entries.add(entry);
                            }
                        }
                    }
                } catch (JobSchedulerException e1) {
                    throw e1;
                } catch (Exception e1) {
                    throw new JobSchedulerException(String.format("error while reading '%1$s': %2$s", objOptions.fileListName.getValue(), e1
                            .toString()), e1);
                } finally {
                    try {
                        file.close();
                    } catch (IOException e) {
                        // nothing to do
                    }
                }
            } else {
                throw new JobSchedulerException(String.format("%1$s doesn't exist.", objOptions.fileListName.getValue()));
            }
        }
        return entries;
    }

    @Override
    public String getState() {
        return (String) objOptions.getTextProperties().get(KEYWORD_STATE);
    }

    protected boolean isAPathName(final String path) {
        boolean ok = false;
        String aPath = path.replaceAll("\\\\", "/");
        if (!(aPath.startsWith("./") || aPath.startsWith("../"))) {
            // drive:/, protocol:/, /, ~/, $ (Unix Env), %...% (Windows Env)
            if (aPath.matches("^[a-zA-Z]+:/.*") || aPath.startsWith("/") || aPath.startsWith("~/") || aPath.startsWith("$") || aPath.matches(
                    "^%[a-zA-Z_0-9.-]+%.*")) {
                ok = true;
            } else {
                // nothing to do
            }
        }
        return ok;
    }

    @Override
    public void logout() {
        try {
            doLogout(targetClient);
            doLogout(sourceClient);
        } catch (Exception e) {
            // nothing to do
        }
        if (getOptions().getDeleteSettingsFileOnExit() && objOptions.settings.getValue() != null && !objOptions.settings.getValue().toLowerCase()
                .endsWith(".xml")) {
            try {
                String msg = "deleted";
                if (!Files.deleteIfExists(Paths.get(objOptions.settings.getValue()))) {
                    msg = "cant'be deleted";
                }
                if (isDebugEnabled) {
                    LOGGER.debug(String.format("[%s]settings file %s", objOptions.settings.getValue(), msg));
                }
            } catch (IOException e) {
                if (isDebugEnabled) {
                    LOGGER.debug(String.format("[%s]settings file can't be deleted, exception %s", objOptions.settings.getValue(), e.toString()), e);
                }
            }
        }
    }

    private void makeDirs() {
        if (objOptions.skipTransfer.isFalse()) {
            makeDirs(objOptions.targetDir.getValue());
        }
    }

    private boolean makeDirs(final String path) {
        boolean cd = true;
        try {
            if (objOptions.makeDirs.value()) {
                if (SOSString.isEmpty(path)) {
                    throw new Exception("path is empty");
                }
                if (targetClient.isDirectory(path)) {
                    cd = true;
                } else {
                    targetClient.mkdir(path);
                    cd = targetClient.isDirectory(path);
                }
            } else {
                if (path != null && !path.isEmpty()) {
                    cd = targetClient.isDirectory(path);
                }
            }
            if (isDebugEnabled) {
                LOGGER.debug(String.format("[target][%s]cd=%s", path, cd));
            }
        } catch (Exception e) {
            throw new JobSchedulerException(String.format("[target][makeDirs][%s][failed]%s", path, e.toString()), e);
        }
        return cd;
    }

    private long oneOrMoreSingleFilesSpecified(final String sourceDir) {
        long founded = 0;
        sourceFileList.create(getSingleFileNames(), -1);
        long currentFounded = sourceFileList.size();
        if (objOptions.isFilePollingEnabled()) {
            long currentPollingTime = 0;
            long pollInterval = objOptions.pollInterval.getTimeAsSeconds();
            long pollTimeout = getPollTimeout();
            while (true) {
                founded = 0;
                if (currentPollingTime > pollTimeout) {
                    LOGGER.info(String.format("polling: time '%1$s' is over. polling terminated", getPollTimeoutText()));
                    break;
                }
                for (SOSFileListEntry entry : sourceFileList.getList()) {
                    if (entry.isSourceFileExists()) {
                        founded++;
                        entry.setParent(sourceFileList);
                    }
                }
                if (founded == currentFounded) {
                    break;
                }
                if (objOptions.pollMinfiles.value() > 0 && founded >= objOptions.pollMinfiles.value()) {
                    break;
                }
                String msg = String.format("file-polling: going to sleep for %1$d seconds. '%2$d' files found, waiting for '%3$d' files",
                        pollInterval, founded, currentFounded);
                LOGGER.info(msg);
                objJSJobUtilities.setStateText(msg);

                doSleep(pollInterval);
                currentPollingTime += pollInterval;
            }
        } else {
            founded = currentFounded;
        }
        String msg = String.format("%1$d files found.", sourceFileList.size());
        LOGGER.info(msg);
        objJSJobUtilities.setStateText(msg);
        return founded;
    }

    @Override
    public JADEOptions getOptions() {
        if (objOptions == null) {
            objOptions = new JADEOptions();
        }
        return objOptions;
    }

    @Override
    public void setJadeOptions(final JSOptionsClass options) {
        objOptions = (JADEOptions) options;
    }

    private void transferAfterCheck() {
        String state = "";
        if (objOptions.operation.isOperationGetList() || objOptions.operation.isOperationRemove()) {
        } else {
            if (objOptions.fileListName.isNotEmpty()) {
                state = SOSJADE_E_0098.params(objOptions.fileListName.getValue());
            } else if (objOptions.filePath.isNotEmpty()) {
                state = SOSJADE_E_0099.params(objOptions.filePath.getValue());
            } else {
                state = SOSJADE_E_0100.params(objOptions.fileSpec.getValue());
            }
        }
        if (sourceFileList == null || sourceFileList.isEmpty()) {
            if (objOptions.forceFiles.isTrue()) {
                objOptions.getTextProperties().put(KEYWORD_STATE, state);
                throw new JobSchedulerException(state);
            }
        }
    }

    private void printState() {

        SOSTransferStateCounts counter = getTransferCounter();
        StringBuilder state = null;
        if (counter.getSuccess() == 1) {
            state = new StringBuilder(SOSJADE_I_0100.get());
        } else {
            state = new StringBuilder(SOSJADE_I_0101.params(counter.getSuccess()));
        }
        if (counter.getSkipped() > 0) {
            state.append(" ").append(SOSJADE_I_0103.params(counter.getSkipped()));
        }
        if (counter.getAbortedZeroBytes() > 0 || counter.getSkippedZeroBytes() > 0) {
            state.append(" ").append(SOSJADE_I_0102.params(counter.getAbortedZeroBytes() + counter.getSkippedZeroBytes()));
        }
        state.append(" ").append(SOSFileListEntry.getDateTimeInfos(startTime, endTime));
        LOGGER.info(state.toString());
        JADE_REPORT_LOGGER.info(state.toString());
        objOptions.getTextProperties().put(KEYWORD_STATE, state);
        // if (counter.getFailed() > 0 || !JobSchedulerException.LastErrorMessage.isEmpty()) {
        // return false;
        // }
        // return true;
    }

    private void processSendMail(final enuMailClasses mailClasses, final SOSSmtpMailOptions mailOptions) {
        if (mailOptions != null && mailOptions.FileNotificationTo.isDirty()) {
            String mailSubject = mailOptions.subject.getValue();
            String mailBody = mailOptions.body.getValue();
            String mailAttachments = mailOptions.attachment.getValue();
            try {
                StringBuilder attachment = new StringBuilder(mailOptions.attachment.getValue());
                if (mailOptions.attachment.isDirty()) {
                    attachment.append(";");
                }
                if (objOptions.logFilename.isDirty()) {
                    String logFileName = objOptions.logFilename.getHtmlLogFileName();
                    if (logFileName.length() > 0) {
                        attachment.append(logFileName);
                    }
                    logFileName = objOptions.logFilename.getValue();
                    if (logFileName.length() > 0) {
                        if (attachment.length() > 0) {
                            attachment.append(";");
                        }
                        attachment.append(logFileName);
                    }
                    if (attachment.length() > 0) {
                        mailOptions.attachment.setValue(attachment.toString());
                    }
                }
                if (!mailOptions.subject.isDirty()) {
                    mailOptions.subject.setValue("JADE: ");
                }
                mailOptions.subject.setValue(objOptions.replaceVars(mailOptions.subject.getValue()));

                StringBuilder body = new StringBuilder(objOptions.replaceVars(mailOptions.body.getValue()));
                if (!JobSchedulerException.LastErrorMessage.isEmpty()) {
                    body.append("\n[error]").append(JobSchedulerException.LastErrorMessage).append("\n");
                }

                if (sourceFileList != null) {
                    body.append("\n").append("List of files:").append("\n");

                    boolean isOnEmptyFiles = mailClasses.equals(enuMailClasses.MailOnEmptyFiles);
                    boolean add = true;
                    for (SOSFileListEntry entry : sourceFileList.getList()) {
                        Long fileSize = entry.getFileSize();
                        String fileName = entry.getSourceFilename();
                        TransferStatus transferStatus = entry.getTransferStatus();
                        if (isOnEmptyFiles) {
                            add = false;
                            if (fileSize == 0) {
                                add = true;
                                if (transferStatus.equals(TransferStatus.ignoredDueToZerobyteConstraint)) {
                                    transferStatus = TransferStatus.transfer_skipped;
                                }
                            }
                        } else {
                            add = true;
                        }
                        if (add) {
                            body.append(fileName.replaceAll("\\\\", "/")).append("[").append(transferStatus.name()).append("]").append("[").append(
                                    fileSize).append(" bytes]").append("\n");
                        }
                    }
                }
                mailOptions.body.setValue(body.toString());

                if (!mailOptions.from.isDirty()) {
                    mailOptions.from.setValue("JADE@sos-berlin.com");
                }
                SOSMail mail = new SOSMail(mailOptions.host.getValue());
                LOGGER.debug(mailOptions.dirtyString());
                mail.sendMail(mailOptions);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            } finally {
                mailOptions.subject.setValue(mailSubject);
                mailOptions.body.setValue(mailBody);
                mailOptions.attachment.setValue(mailAttachments);
            }
        }
    }

    private void sendFiles(final SOSFileList fileList) {
        int maxParallelTransfers = 0;
        if (objOptions.concurrentTransfer.isTrue()) {
            // TODO resolve problem with apache ftp client in multithreading
            // mode
        }
        if (maxParallelTransfers <= 0 || objOptions.cumulateFiles.isTrue()) {
            int size = fileList.getList().size();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("files to transfer %s", size));
            }
            for (SOSFileListEntry entry : fileList.getList()) {
                entry.run();
            }
        } else {
            SOSThreadPoolExecutor executor = new SOSThreadPoolExecutor(maxParallelTransfers);
            for (SOSFileListEntry entry : fileList.getList()) {
                executor.runTask(entry);
            }
            try {
                executor.shutDown();
                executor.objThreadPool.awaitTermination(1, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    private void sendNotifications() {
        if (isDebugEnabled) {
            LOGGER.debug(String.format("[mailOnError=%s][mailOnSuccess=%s][mailOnEmptyFiles=%s]", objOptions.mailOnError.value(),
                    objOptions.mailOnSuccess.value(), objOptions.mailOnEmptyFiles.value()));
            if (sourceFileList == null) {
                LOGGER.debug("sourceFileList is NULL");
            } else {
                LOGGER.debug(String.format("[failedTransfers=%s][successfulTransfers=%s][successZeroByteFiles=%s][skippedZeroByteFiles=%s]",
                        sourceFileList.getFailedTransfers(), sourceFileList.getSuccessfulTransfers(), sourceFileList.getCounterSuccessZeroByteFiles(),
                        sourceFileList.getCounterSkippedZeroByteFiles()));
            }
        }

        if (sourceFileList == null) {
            if (objOptions.mailOnError.isTrue() && !JobSchedulerException.LastErrorMessage.isEmpty()) {
                doProcessMail(enuMailClasses.MailOnError);
            }
        } else {
            if (objOptions.mailOnError.isTrue() && (sourceFileList.getFailedTransfers() > 0 || !JobSchedulerException.LastErrorMessage.isEmpty())) {
                doProcessMail(enuMailClasses.MailOnError);
            } else if (objOptions.mailOnSuccess.isTrue() && sourceFileList.getSuccessfulTransfers() > 0) {
                doProcessMail(enuMailClasses.MailOnSuccess);
            }
            if (objOptions.mailOnEmptyFiles.isTrue() && (sourceFileList.getCounterSuccessZeroByteFiles() > 0 || sourceFileList
                    .getCounterAbortedZeroByteFiles() > 0 || sourceFileList.getCounterSkippedZeroByteFiles() > 0)) {
                doProcessMail(enuMailClasses.MailOnEmptyFiles);
            }
        }
    }

    private void setSystemProperties() {
        try {
            if (!SOSString.isEmpty(getOptions().system_property_files.getValue())) {
                String files = getOptions().system_property_files.getValue();
                if (!SOSString.isEmpty(files)) {
                    LOGGER.info(String.format("set system properties from files: %s", files));
                    setSystemProperties(files);
                }
            }
        } catch (Exception ex) {
            LOGGER.warn("error on setSystemProperties: " + ex.toString());
        }
    }

    private void setSystemProperties(String files) {
        String[] arr = files.trim().split(";");
        for (String file : arr) {
            file = file.trim();
            BufferedReader in = null;
            FileReader fr = null;
            try {
                LOGGER.debug(String.format("read property file: %s", file));
                // extra not use prop.load(input) - load() not read the
                // properties in the original order
                fr = new FileReader(file);
                in = new BufferedReader(fr);
                String line;
                while ((line = in.readLine()) != null) {
                    line = line.trim();
                    if (!SOSString.isEmpty(line)) {
                        Properties p = new Properties();
                        StringReader s = null;
                        try {
                            s = new StringReader(line);
                            p.load(s);
                        } catch (Exception e) {
                            LOGGER.warn(String.format("can't load property from line [%s]: %s", line, e.toString()));
                        } finally {
                            if (s != null) {
                                try {
                                    s.close();
                                } catch (Exception ex) {
                                }
                            }
                        }

                        Optional<Entry<Object, Object>> o = p.entrySet().stream().findFirst();
                        if (o.isPresent()) {
                            String key = (String) o.get().getKey();
                            String value = (String) o.get().getValue();
                            LOGGER.debug(String.format("set system property: %s = %s", key, value));
                            System.setProperty(key, value);
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.warn(String.format("error on read property file [%s]: %s", file, e.toString()));
            } finally {
                if (fr != null) {
                    try {
                        fr.close();
                    } catch (Exception ex) {
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception ex) {
                    }
                }
            }
        }
    }

    private void setTextProperties() {
        SOSTransferStateCounts counter = getTransferCounter();
        objOptions.getTextProperties().put(KEYWORD_SUCCESSFUL_TRANSFERS, String.valueOf(counter.getSuccess()));
        objOptions.getTextProperties().put(KEYWORD_FAILED_TRANSFERS, String.valueOf(counter.getFailed()));
        objOptions.getTextProperties().put(KEYWORD_SKIPPED_TRANSFERS, String.valueOf(counter.getSkipped() + counter.getSkippedZeroBytes()));
        if (JobSchedulerException.LastErrorMessage.length() <= 0) {
            objOptions.getTextProperties().put(KEYWORD_STATUS, SOSJADE_T_0012.get());
        } else {
            objOptions.getTextProperties().put(KEYWORD_STATUS, SOSJADE_T_0013.get());
        }
        if (sourceFileList != null && SOSString.isEmpty(JobSchedulerException.LastErrorMessage) && !SOSString.isEmpty(sourceFileList
                .getLastErrorMessage())) {
            objOptions.getTextProperties().put(KEYWORD_LAST_ERROR, sourceFileList.getLastErrorMessage());
        } else {
            objOptions.getTextProperties().put(KEYWORD_LAST_ERROR, JobSchedulerException.LastErrorMessage);
        }
    }

    private SOSTransferStateCounts getTransferCounter() {
        SOSTransferStateCounts counter = new SOSTransferStateCounts();
        if (sourceFileList != null) {
            counter = sourceFileList.countTransfers();
        }
        return counter;
    }

    public ISOSVfsFileTransfer getTargetClient() {
        return targetClient;
    }

    public ISOSVfsFileTransfer getSourceClient() {
        return sourceClient;
    }

    public void setEngineClientHandler(IJadeEngineClientHandler handler) {
        engineClientHandler = handler;
    }

    /** Send files by .... from source to a target
     *
     * @return boolean
     * @throws Exception */
    public void transfer() throws Exception {
        Exception exception = null;
        boolean isFilePollingEnabled = objOptions.isFilePollingEnabled();
        YadeHistory history = null;
        if (historyHandler != null) {
            history = (YadeHistory) historyHandler;
        }
        try {
            getOptions().checkMandatory();
            if (isDebugEnabled) {
                LOGGER.debug(getOptions().dirtyString());
                LOGGER.debug("[source]" + getOptions().getSource().dirtyString());
                LOGGER.debug("[target]" + getOptions().getTarget().dirtyString());
            }
            if (history != null) {
                if (objOptions.pollingServer.isTrue() || objOptions.pollingServerPollForever.isTrue() || objOptions.pollingServerDuration
                        .getTimeAsSeconds() > 0) {
                    throw new Exception("PollingServer setting should be \"false\" if YADE is executed by JobScheduler.");
                }
            }

            setSystemProperties();
            setTextProperties();
            sourceFileList = new SOSFileList(objOptions, historyHandler);
            factory = new SOSVfsConnectionFactory(objOptions);
            factory.createConnectionPool();
            if (objOptions.lazyConnectionMode.isFalse() && objOptions.isNeedTargetClient()) {
                targetClient = (ISOSVfsFileTransfer) factory.getTargetPool().getUnused();
                sourceFileList.setTargetClient(targetClient);
                makeDirs();
            }
            try {
                sourceClient = (ISOSVfsFileTransfer) factory.getSourcePool().getUnused();
                sourceFileList.setSourceClient(sourceClient);
                String sourceDir = objOptions.sourceDir.getValue();
                String targetDir = objOptions.targetDir.getValue();

                if (engineClientHandler != null) {
                    engineClientHandler.onBeforeOperation(this);
                }

                executePreTransferCommands();

                if (history != null) {
                    history.setFileRestriction(objOptions);
                }

                PollingMethod pollingMethod = null;
                long pollingServerStartTime = 0;
                countPollingServerFiles = 0;
                if (isFilePollingEnabled) {
                    if (objOptions.pollingServer.isTrue()) {
                        if (objOptions.pollingServerDuration.isDirty() && objOptions.pollingServerPollForever.isFalse()) {
                            pollingMethod = PollingMethod.PollingServerDuration;
                        } else {
                            pollingMethod = PollingMethod.PollForever;
                        }
                    } else {
                        pollingMethod = PollingMethod.PollTimeout;
                    }
                    pollingServerStartTime = System.currentTimeMillis() / 1_000;
                    LOGGER.info(String.format("[%s]start polling ...", pollingMethod.name()));
                }
                PollingServerLoop: while (true) {
                    if (isFilePollingEnabled) {
                        doPollingForFiles(pollingServerStartTime, pollingMethod);
                        if (sourceFileList.isEmpty() && objOptions.pollingServer.isFalse() && objOptions.pollErrorState.isDirty()) {
                            String pollErrorState = objOptions.pollErrorState.getValue();
                            LOGGER.info("set order-state to " + pollErrorState);
                            setNextNodeState(pollErrorState);
                            break PollingServerLoop;
                        }
                    } else {
                        if (objOptions.oneOrMoreSingleFilesSpecified()) {
                            oneOrMoreSingleFilesSpecified(sourceDir);
                        } else {
                            ISOSVirtualFile fileHandle = sourceClient.getFileHandle(sourceDir);
                            if (isDebugEnabled) {
                                String msg = "";
                                if (objOptions.isNeedTargetClient()) {
                                    msg = "[source directory/file=" + sourceDir + "][target directory=" + targetDir + "][file regexp="
                                            + objOptions.fileSpec.getValue() + "]";
                                } else {
                                    msg = SOSJADE_D_0200.params(sourceDir, objOptions.fileSpec.getValue());
                                }
                                LOGGER.debug(msg);
                            }
                            String integrityHashFileExtention = objOptions.checkIntegrityHash.isTrue() ? "." + objOptions.integrityHashType.getValue()
                                    : null;
                            selectFilesOnSource(isFilePollingEnabled, fileHandle, objOptions.sourceDir, objOptions.fileSpec, objOptions.recursive,
                                    integrityHashFileExtention);
                        }
                    }
                    if (!checkSourceFilesSteady()) {
                        break PollingServerLoop;
                    }

                    boolean executeOperation = true;
                    try {
                        sourceFileList.handleZeroByteFiles();
                    } catch (Throwable e) {
                        if (isFilePollingEnabled) {
                            LOGGER.error(String.format("[%s]%s", pollingMethod.name(), e.toString()));
                            executeOperation = false;
                        } else {
                            throw e;
                        }
                    }

                    try {
                        if (executeOperation) {
                            if (objOptions.operation.isOperationGetList()) {
                                String msg = SOSJADE_I_0115.get();
                                LOGGER.info(msg);
                                JADE_REPORT_LOGGER.info(msg);
                                objOptions.removeFiles.setFalse();
                                objOptions.forceFiles.setFalse();
                                sourceFileList.logFileList();
                                sourceFileList.createResultSetFile();
                            } else {
                                sourceFileList.createResultSetFile();
                                if (!sourceFileList.isEmpty() && objOptions.skipTransfer.isFalse()) {
                                    if (objOptions.lazyConnectionMode.isTrue()) {
                                        targetClient = (ISOSVfsFileTransfer) factory.getTargetPool().getUnused();
                                        sourceFileList.setTargetClient(targetClient);
                                        makeDirs();
                                    }
                                    if (history != null) {
                                        history.beforeFileTransfer(sourceFileList);
                                    }
                                    sendFiles(sourceFileList);
                                    if (history != null) {
                                        history.afterFileTransfer(sourceFileList);
                                    }
                                    sourceFileList.renameTargetAndSourceFiles();
                                    executePostTransferCommands();
                                    sourceFileList.deleteSourceFiles();
                                }
                            }
                        }

                        if (objOptions.pollingServer.isFalse() || objOptions.skipTransfer.isTrue()) {
                            if (objOptions.isNeedTargetClient()) {
                                targetClient.close();
                            }
                            sourceClient.close();
                            break PollingServerLoop;
                        } else {
                            if (isFilePollingEnabled) {
                                if (pollingMethod.equals(PollingMethod.PollingServerDuration)) {
                                    if (isPollingServerDurationElapsed(pollingMethod, pollingServerStartTime)) {
                                        break PollingServerLoop;
                                    }
                                }

                                showSummary();
                                sendNotifications();

                                startNextPollingCycle(pollingMethod, !executeOperation);
                            } else {
                                if (!objOptions.pollTimeout.isDirty()) {
                                    LOGGER.info("Polling settings ignored due PollTimeout=" + objOptions.pollTimeout.getValue() + "m");
                                }

                                if (objOptions.isNeedTargetClient() && targetClient != null) {
                                    targetClient.close();
                                }
                                if (sourceClient != null) {
                                    sourceClient.close();
                                }
                                break PollingServerLoop;
                            }
                        }
                    } catch (JobSchedulerException e) {
                        String msg = null;
                        if (objOptions.transactional.value()) {
                            msg = TRANSACTION_ABORTED.get(e);
                        } else {
                            msg = TRANSFER_ABORTED.get(e);
                        }
                        LOGGER.error(msg);
                        JADE_REPORT_LOGGER.error(msg);
                        sourceFileList.rollback();

                        if (history != null) {
                            history.afterFileTransfer(sourceFileList);
                        }
                        if (isFilePollingEnabled) {
                            LOGGER.error(e.toString(), e);
                            tryReconnectByPolling(pollingServerStartTime, 0, pollingMethod);
                        } else {
                            throw e;
                        }
                    }
                }

                if (pollingMethod != null) {
                    LOGGER.info(String.format("[%s]end polling", pollingMethod.name()));
                }
            } catch (Exception e) {
                if (history != null) {
                    history.onFileTransferException(sourceFileList);
                }
                throw e;
            } finally {
                setTextProperties();
            }
        } catch (Exception e) {
            setTextProperties();
            objOptions.getTextProperties().put(KEYWORD_STATUS, SOSJADE_T_0013.get());
            JADE_REPORT_LOGGER.info(SOSJADE_E_0101.params(e.toString()), e);
            try {
                executePostTransferCommandsOnError(e);
            } catch (Exception ex) {
                LOGGER.error(ex.toString());
            }
            exception = e;
            throw e;
        } finally {
            try {
                executePostTransferCommandsFinal(exception);
            } catch (Exception e) {
                LOGGER.error(e.toString());
            }
        }
    }

    private boolean isPollingServerDurationElapsed(PollingMethod pollingMethod, long pollingServerStartTime) {
        long currentTime = System.currentTimeMillis() / 1_000;
        long duration = currentTime - pollingServerStartTime;
        if (duration >= objOptions.pollingServerDuration.getTimeAsSeconds()) {
            LOGGER.debug(String.format("[%s][PollingServerDuration=%s][duration=%ss]time elapsed. terminate polling server", pollingMethod.name(),
                    getPoolingServerDurationValue(), duration));
            return true;
        }
        return false;
    }

    private void startNextPollingCycle(PollingMethod pollingMethod, boolean hasError) {

        JobSchedulerException.LastErrorMessage = "";
        countPollingServerFiles += sourceFileList.size();

        LOGGER.info(String.format("[%s][total=%s][current=%s]start next polling cycle ...", pollingMethod.name(), countPollingServerFiles,
                sourceFileList.size()));

        sourceFileList.clearFileList();
        sourceFileList.resetTransferCountersCounted();
        sourceFileList.resetNoOfZeroByteSizeFiles();

        if (hasError) {
            if (isDebugEnabled) {
                LOGGER.info(String.format("[wait]%ss", POLLING_WAIT_INTERVAL_ON_TRANSFER_ERROR));
            }
            doSleep(POLLING_WAIT_INTERVAL_ON_TRANSFER_ERROR);
        }
    }

    private void executePreTransferCommands() throws Exception {
        SOSConnection2OptionsAlternate target = objOptions.getTarget();
        if (target.alternateOptionsUsed.isTrue()) {
            target = target.getAlternatives();
            executeTransferCommands("alternative_target_pre_transfer_commands", targetClient, target.preTransferCommands.getValue(),
                    target.commandDelimiter.getValue());
        } else {
            executeTransferCommands("pre_transfer_commands", targetClient, objOptions.preTransferCommands.getValue(), target.commandDelimiter
                    .getValue());
            executeTransferCommands("target_pre_transfer_commands", targetClient, target.preTransferCommands.getValue(), target.commandDelimiter
                    .getValue());
        }
        SOSConnection2OptionsAlternate source = objOptions.getSource();
        String caller = "source_pre_transfer_commands";
        if (source.alternateOptionsUsed.isTrue()) {
            source = source.getAlternatives();
            caller = "alternative_" + caller;
        }
        executeTransferCommands(caller, sourceClient, source.preTransferCommands.getValue(), source.commandDelimiter.getValue());
        if (objOptions.isNeedTargetClient()) {
            targetClient.reconnect(target);
        }
    }

    private void executePostTransferCommands() throws Exception {
        SOSConnection2OptionsAlternate target = objOptions.getTarget();
        if (target.alternateOptionsUsed.isTrue()) {
            target = target.getAlternatives();
            executeTransferCommands("alternative_target_post_transfer_commands", targetClient, target.postTransferCommands.getValue(),
                    target.commandDelimiter.getValue());
        } else {
            executeTransferCommands("post_transfer_commands", targetClient, objOptions.postTransferCommands.getValue(), target.commandDelimiter
                    .getValue());
            executeTransferCommands("target_post_transfer_commands", targetClient, target.postTransferCommands.getValue(), target.commandDelimiter
                    .getValue());
        }
        SOSConnection2OptionsAlternate source = objOptions.getSource();
        String caller = "source_post_transfer_commands";
        if (source.alternateOptionsUsed.isTrue()) {
            source = source.getAlternatives();
            caller = "alternative_" + caller;
        }
        if (!SOSString.isEmpty(source.postTransferCommands.getValue())) {
            if (sourceClient == null) {
                throw new Exception("sourceClient is NULL");
            }
            sourceClient.reconnect(source);
            executeTransferCommands(caller, sourceClient, source.postTransferCommands.getValue(), source.commandDelimiter.getValue());
        }
    }

    private void executePostTransferCommandsOnError(Exception e) throws Exception {
        StringBuilder exception = new StringBuilder();
        String caller = "";
        if (!(e instanceof SOSYadeTargetConnectionException)) {
            SOSConnection2OptionsAlternate target = objOptions.getTarget();
            caller = "target_post_transfer_commands_on_error";
            if (target.alternateOptionsUsed.isTrue()) {
                target = target.getAlternatives();
                caller = "alternative_" + caller;
            }
            try {
                executeTransferCommands(caller, targetClient, target.postTransferCommandsOnError.getValue(), target.commandDelimiter.getValue());
            } catch (Exception ex) {
                exception.append(String.format("[%s]:%s", caller, ex.toString()));
            }
        }
        if (!(e instanceof SOSYadeSourceConnectionException)) {
            SOSConnection2OptionsAlternate source = objOptions.getSource();
            caller = "source_post_transfer_commands_on_error";
            if (source.alternateOptionsUsed.isTrue()) {
                source = source.getAlternatives();
                caller = "alternative_" + caller;
            }
            if (!SOSString.isEmpty(source.postTransferCommandsOnError.getValue())) {
                try {
                    // with JADE4DMZ it could be that the
                    // target.PostTransferCommands
                    // needs more time than the source connection is still
                    // established
                    if (sourceClient == null) {
                        throw new Exception("sourceClient is NULL");
                    }
                    sourceClient.reconnect(source);
                    executeTransferCommands(caller, sourceClient, source.postTransferCommandsOnError.getValue(), source.commandDelimiter.getValue());
                } catch (Exception ex) {
                    if (exception.length() > 0) {
                        exception.append(", ");
                    }
                    exception.append(String.format("[%s]:%s", caller, ex.toString()));
                }
            }
        }
        if (exception.length() > 0) {
            throw new Exception(exception.toString());
        }
    }

    private void executePostTransferCommandsFinal(Exception e) throws Exception {
        StringBuilder exception = new StringBuilder();
        String caller = "";
        if (e == null || !(e instanceof SOSYadeTargetConnectionException)) {
            SOSConnection2OptionsAlternate target = objOptions.getTarget();
            caller = "target_post_transfer_commands_final";
            if (target.alternateOptionsUsed.isTrue()) {
                target = target.getAlternatives();
                caller = "alternative_" + caller;
            }
            try {
                executeTransferCommands(caller, targetClient, target.postTransferCommandsFinal.getValue(), target.commandDelimiter.getValue());
            } catch (Exception ex) {
                exception.append(String.format("[%s]:%s", caller, ex.toString()));
            }
        }

        if (e == null || !(e instanceof SOSYadeSourceConnectionException)) {
            SOSConnection2OptionsAlternate source = objOptions.getSource();
            caller = "source_post_transfer_commands_final";
            if (source.alternateOptionsUsed.isTrue()) {
                source = source.getAlternatives();
                caller = "alternative_" + caller;
            }
            if (!SOSString.isEmpty(source.postTransferCommandsFinal.getValue())) {
                try {
                    // with JADE4DMZ it could be that the
                    // target.PostTransferCommands
                    // needs more time than the source connection is still
                    // established
                    if (sourceClient == null) {
                        throw new Exception("objDataSourceClient is NULL");
                    }
                    sourceClient.reconnect(source);
                    executeTransferCommands(caller, sourceClient, source.postTransferCommandsFinal.getValue(), source.commandDelimiter.getValue());
                } catch (Exception ex) {
                    if (exception.length() > 0) {
                        exception.append(", ");
                    }
                    exception.append(String.format("[%s]:%s", caller, ex.toString()));
                }
            }
        }
        if (exception.length() > 0) {
            throw new Exception(exception.toString());
        }
    }

    protected void executeTransferCommands(String commandOptionName, final ISOSVfsFileTransfer fileTransfer, final String commands,
            final String delimiter) throws Exception {
        if (commands != null && commands.trim().length() > 0) {
            if (SOSString.isEmpty(delimiter)) {
                LOGGER.info(String.format("[%s]%s", commandOptionName, commands.trim()));
                fileTransfer.getHandler().executeCommand(commands);
            } else {
                String[] values = commands.split(delimiter);
                if (values.length > 1) {
                    LOGGER.debug(String.format("[%s]commands=%s", commandOptionName, commands.trim()));
                }
                for (String command : values) {
                    if (!SOSString.isEmpty(command.trim())) {
                        LOGGER.info(String.format("[%s]%s", commandOptionName, command.trim()));
                        fileTransfer.getHandler().executeCommand(command);
                    }
                }
            }
        }
    }

    private boolean selectFilesOnSource(boolean isFilePollingEnabled, final ISOSVirtualFile sourceFile, final SOSOptionFolderName sourceDir,
            final SOSOptionRegExp regExp, final SOSOptionBoolean recursive, final String integrityHashFileExtention) throws Exception {
        if (sourceClient instanceof SOSVfsHTTP) {
            throw new JobSchedulerException("a file spec selection is not supported with http(s) protocol");
        }
        if (sourceFile.isDirectory()) {
            LOGGER.trace("[source]is directory");

            int maxFiles = -1;
            if (objOptions.maxFiles.isDirty()) {
                objOptions.maxFiles.value();
            }
            sourceFileList.create(sourceClient.getFilelist(sourceDir.getValue(), regExp.getValue(), 0, recursive.value(), false,
                    integrityHashFileExtention), maxFiles);

            String msg = String.format("[source][%s][recursive=%s][%s]%s files found", sourceDir.getValue(), recursive.value(), regExp.getValue(),
                    sourceFileList.size());
            if (isFilePollingEnabled) {
                LOGGER.debug(msg);
            } else {
                LOGGER.info(msg);
            }
            objJSJobUtilities.setStateText(msg);
            return true;
        } else {
            LOGGER.info(String.format("[source][%s]directory not found", sourceDir.getValue()));
            return false;
        }
    }

    public void setJobSchedulerEventHandler(IJobSchedulerEventHandler val) {
        historyHandler = val;
    }
}
