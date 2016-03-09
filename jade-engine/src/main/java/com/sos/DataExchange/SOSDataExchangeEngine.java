package com.sos.DataExchange;

import static com.sos.DataExchange.SOSJadeMessageCodes.EXCEPTION_RAISED;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import sos.net.SOSMail;
import sos.net.mail.options.SOSSmtpMailOptions;
import sos.net.mail.options.SOSSmtpMailOptions.enuMailClasses;
import sos.util.SOSString;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Basics.VersionInfo;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.JSHelper.Options.JSOptionsClass;
import com.sos.JSHelper.Options.SOSOptionBoolean;
import com.sos.JSHelper.Options.SOSOptionCommandString;
import com.sos.JSHelper.Options.SOSOptionFolderName;
import com.sos.JSHelper.Options.SOSOptionRegExp;
import com.sos.JSHelper.Options.SOSOptionTime;
import com.sos.JSHelper.Options.SOSOptionTransferType;
import com.sos.JSHelper.concurrent.SOSThreadPoolExecutor;
import com.sos.JSHelper.interfaces.IJadeEngine;
import com.sos.JSHelper.io.Files.JSFile;
import com.sos.VirtualFileSystem.DataElements.SOSFileList;
import com.sos.VirtualFileSystem.DataElements.SOSFileListEntry;
import com.sos.VirtualFileSystem.DataElements.SOSFileListEntry.HistoryRecordType;
import com.sos.VirtualFileSystem.DataElements.SOSTransferStateCounts;
import com.sos.VirtualFileSystem.DataElements.SOSVfsConnectionFactory;
import com.sos.VirtualFileSystem.Factory.VFSFactory;
import com.sos.VirtualFileSystem.HTTP.SOSVfsHTTP;
import com.sos.VirtualFileSystem.Interfaces.ISOSVFSHandler;
import com.sos.VirtualFileSystem.Interfaces.ISOSVfsFileTransfer;
import com.sos.VirtualFileSystem.Interfaces.ISOSVfsFileTransfer2;
import com.sos.VirtualFileSystem.Interfaces.ISOSVirtualFile;
import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.sos.VirtualFileSystem.common.SOSFileEntries;
import com.sos.scheduler.model.SchedulerObjectFactory;
import com.sos.scheduler.model.commands.JSCmdAddOrder;
import com.sos.scheduler.model.objects.Params;
import com.sos.scheduler.model.objects.Spooler;

public class SOSDataExchangeEngine extends JadeBaseEngine implements Runnable, IJadeEngine {

    private ISOSVfsFileTransfer targetClient = null;
    private ISOSVfsFileTransfer sourceClient = null;

    private final String className = SOSDataExchangeEngine.class.getSimpleName();
    private final static Logger LOGGER = Logger.getLogger(SOSDataExchangeEngine.class);
    private final String jadeLoggerName = "JadeReportLog";
    private final Logger jadeReportLogger = Logger.getLogger(jadeLoggerName);
    private ISOSVFSHandler targetHandler = null;
    private SOSFileList sourceFileList = null;
    private SOSVfsConnectionFactory factory = null;
    private SchedulerObjectFactory schedulerFactory = null;
    private long countPollingServerFiles = 0;
    private long countSentHistoryRecords = 0;

    private static final String KEYWORD_LAST_ERROR = "last_error";
    private static final String KEYWORD_STATE = "state";
    private static final String KEYWORD_SUCCESSFUL_TRANSFERS = "successful_transfers";
    private static final String KEYWORD_FAILED_TRANSFERS = "failed_transfers";
    private static final String KEYWORD_SKIPPED_TRANSFERS = "skipped_transfers";
    private static final String KEYWORD_STATUS = "status";

    public SOSDataExchangeEngine() throws Exception {
    }

    public SOSDataExchangeEngine(final HashMap<String, String> settings) throws Exception {
        this.getOptions();
        objOptions.setAllOptions(settings);
    }

    public SOSDataExchangeEngine(final Properties properties) throws Exception {
        this.getOptions();
    }

    public SOSFileEntries getSOSFileEntries() {
        return sourceClient.getSOSFileEntries();
    }

    public SOSDataExchangeEngine(final JADEOptions jadeOptions) throws Exception {
        super(jadeOptions);
        objOptions = jadeOptions;
        if (objOptions.settings.isDirty()) {
            objOptions.ReadSettingsFile();
        }
    }

    /** TODO in die DataSource verlagern? Oder in die FileList? Multithreaded
     * ausfuehren?
     * 
     * @return */
    public boolean checkSteadyStateOfFiles() {
        boolean allFilesAreSteady = true;
        if (objOptions.CheckSteadyStateOfFiles.isTrue() && sourceFileList != null) {
            long interval = objOptions.CheckSteadyStateInterval.getTimeAsSeconds();
            setInfo("checking file(s) for steady state");
            for (int i = 0; i < objOptions.CheckSteadyCount.value(); i++) {
                allFilesAreSteady = true;
                String msg = String.format("steady check (%s of %s):", (i + 1), objOptions.CheckSteadyCount.value());

                LOGGER.info(String.format("%s waiting %ss.", msg, interval));
                doSleep(interval);

                for (SOSFileListEntry entry : sourceFileList.List()) {
                    if (!checkSteadyStateOfFile(entry, msg)) {
                        allFilesAreSteady = false;
                    }
                }
                if (allFilesAreSteady) {
                    // one extra check because of
                    // https://change.sos-berlin.com/browse/JADE-372
                    // more than one file could be updated SERIAL on the source.
                    LOGGER.info(String.format("all files seem steady! Extra %s waiting %ss for late comers.", msg, interval));
                    doSleep(interval);

                    for (SOSFileListEntry entry : sourceFileList.List()) {
                        entry.setSteady(false);
                        if (!checkSteadyStateOfFile(entry, msg)) {
                            allFilesAreSteady = false;
                        }
                    }
                }
                if (allFilesAreSteady) {
                    LOGGER.info(String.format("%s break steady check. all files are steady.", msg));
                    break;
                }
            }
            if (allFilesAreSteady == false) {
                String msg = "not all files are steady";
                LOGGER.error(msg);
                for (SOSFileListEntry objFile : sourceFileList.List()) {
                    if (objFile.isSteady() == false) {
                        LOGGER.info(String.format("File '%1$s' is not steady", objFile.SourceFileName()));
                    }
                }
                if (objOptions.SteadyStateErrorState.isDirty()) {
                    objJSJobUtilities.setNextNodeState(objOptions.SteadyStateErrorState.Value());
                } else {
                    throw new JobSchedulerException(msg);
                }
            }
        }
        return allFilesAreSteady;
    }

    private boolean checkSteadyStateOfFile(SOSFileListEntry entry, String msg) {
        boolean fileIsSteady = true;
        if (entry.isSteady() == false) {
            // initialize property with the first file size
            if (entry.getLastCheckedFileSize() < 0) {
                entry.setLastCheckedFileSize(entry.getFileSize());
            }
            // current file size
            entry.setSourceFileProperties(sourceClient.getFileHandle(entry.SourceFileName()));

            if (entry.getLastCheckedFileSize().equals(entry.getFileSize())) {
                entry.setSteady(true);
                LOGGER.debug(String.format("%s Not changed. file size: %s bytes. '%s'", msg, entry.getLastCheckedFileSize(), entry.SourceFileName()));
            } else {
                fileIsSteady = false;
                LOGGER.info(String.format("%s Changed. file size: new = %s bytes, old = %s bytes. '%s'", msg, entry.getFileSize(), entry.getLastCheckedFileSize(), entry.SourceFileName()));

            }
            entry.setLastCheckedFileSize(entry.getFileSize());
        }
        return fileIsSteady;
    }

    /** @param client
     * @throws Exception */
    private void doLogout(ISOSVfsFileTransfer client) throws Exception {
        if (client != null) {
            client.logout();
            client.disconnect();
            client.close();
            client = null;
        }
    }

    /** TODO multi threaded approach TODO move to SOSFileList
     * 
     * @return */
    private String[] doPollingForFiles() {
        String[] fileList = null;
        if (objOptions.isFilePollingEnabled()) {
            long pollTimeout = getPollTimeout();
            long pollInterval = objOptions.poll_interval.getTimeAsSeconds();
            long currentPollingTime = 0;

            String sourceDir = objOptions.SourceDir.Value();
            boolean isSourceDirFounded = false;

            long filesCount = 0;
            long currentFilesCount = sourceFileList.size();
            ISOSVirtualFile sourceFile = null;

            PollingLoop: while (true) {
                if (currentPollingTime > pollTimeout) { // time exceeded ?
                    setInfo(String.format("file-polling: time '%1$s' is over. polling terminated", getPollTimeoutText()));
                    break PollingLoop;
                }
                if (isSourceDirFounded == false) {
                    sourceFile = sourceClient.getFileHandle(sourceDir);
                    if (objOptions.pollingWait4SourceFolder.isFalse()) {
                        isSourceDirFounded = true; // To keep the previous
                                                   // behaviour
                    } else {
                        try { // Test, wether the source folder is available. if
                              // not and polling is active, wait for the folder
                            if (sourceFile.notExists() == true) {
                                LOGGER.info(String.format("directory %1$s not found. Wait for the directory due to polling mode.", sourceDir));
                            } else {
                                isSourceDirFounded = true;
                            }
                        } catch (Exception e) {
                            isSourceDirFounded = false;
                        }
                    }
                }
                if (isSourceDirFounded == true) {
                    try {
                        if (objOptions.OneOrMoreSingleFilesSpecified() == true) {
                            currentFilesCount = 0;
                            objOptions.poll_minfiles.value(sourceFileList.count());
                            for (SOSFileListEntry objItem : sourceFileList.List()) {
                                if (objItem.SourceFileExists()) {
                                    currentFilesCount++;
                                    objItem.setParent(sourceFileList); // to
                                                                       // reinit
                                                                       // the
                                                                       // file-size
                                }
                            }
                        } else {
                            String integrityHashFileExtention = objOptions.CheckIntegrityHash.isTrue() ? "." + objOptions.IntegrityHashType.Value()
                                    : null;
                            selectFilesOnSource(sourceFile, objOptions.SourceDir, objOptions.file_spec, objOptions.recursive, integrityHashFileExtention);
                            currentFilesCount = sourceFileList.count();
                        }
                    } catch (Exception e) {
                        LOGGER.error(e.getLocalizedMessage());
                    }
                    if (objOptions.poll_minfiles.isNotDirty() && currentFilesCount > 0) { // amount
                        break PollingLoop; // minfiles not specified and
                                           // one/some files found
                    }
                    if (objOptions.poll_minfiles.isDirty() && currentFilesCount >= objOptions.poll_minfiles.value()) { // amount
                        break PollingLoop;
                    }
                }
                //
                setInfo(String.format("file-polling: going to sleep for %1$d seconds. regexp '%2$s'", pollInterval, objOptions.file_spec.Value()));

                doSleep(pollInterval);
                currentPollingTime += pollInterval;

                setInfo(String.format("file-polling: %1$d files found for regexp '%2$s' on directory '%3$s'.", currentFilesCount, objOptions.file_spec.Value(), sourceDir));

                if (filesCount >= currentFilesCount && filesCount != 0) { // no
                                                                          // additional
                                                                          // file
                                                                          // found
                    if (objOptions.WaitingForLateComers.isTrue()) { // just wait
                                                                    // a round
                                                                    // for
                                                                    // latecommers
                        objOptions.WaitingForLateComers.setFalse();
                    } else {
                        break PollingLoop;
                    }
                }
            } // while
        }
        return fileList;
    }

    private void doProcessMail(final enuMailClasses mailClasses) {
        SOSSmtpMailOptions mailOptions = objOptions.getMailOptions();
        SOSSmtpMailOptions mailOptionsWithMailClasses = mailOptions.getOptions(mailClasses);
        if (mailOptionsWithMailClasses == null || mailOptionsWithMailClasses.FileNotificationTo.isDirty() == false) {
            mailOptionsWithMailClasses = mailOptions;
        }
        processSendMail(mailOptionsWithMailClasses);
    }

    private void doSleep(final long time) {
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
        }
    }

    private void setLogger() {
        VFSFactory.setParentLogger(jadeLoggerName);

        int verbose = objOptions.verbose.value();
        if (verbose <= 1) {
            Logger.getRootLogger().setLevel(Level.INFO);
        } else {
            if (verbose > 8) {
                Logger.getRootLogger().setLevel(Level.TRACE);
                LOGGER.setLevel(Level.TRACE);
                LOGGER.debug("set loglevel to TRACE due to option verbose = " + verbose);
            } else {
                Logger.getRootLogger().setLevel(Level.DEBUG);
                LOGGER.debug("set loglevel to DEBUG due to option verbose = " + verbose);
            }
        }
    }

    @Override
    public boolean Execute() throws Exception {

        setLogger();

        objOptions.getTextProperties().put("version", VersionInfo.VERSION_STRING);
        objOptions.log_filename.setLogger(jadeReportLogger);

        boolean ok = false;
        try {
            JobSchedulerException.LastErrorMessage = "";
            try {
                getOptions().CheckMandatory();
            } catch (JobSchedulerException e) {
                throw e;
            } catch (Exception e) {
                throw new JobSchedulerException(e.getLocalizedMessage());
            } finally {
                showBanner();
            }

            ok = transfer();

            if (JobSchedulerException.LastErrorMessage.length() > 0) {
                throw new JobSchedulerException(JobSchedulerException.LastErrorMessage);
            }
        } catch (JobSchedulerException e) {
            throw e;
        } catch (Exception e) {
            throw new JobSchedulerException(e.getLocalizedMessage());
        } finally {
            showResult();
            sendNotifications();
        }
        return ok;
    }

    private void showResult() {
        String msg = "";
        if (objOptions.banner_footer.isDirty()) {
            msg = objOptions.banner_footer.JSFile().getContent();
        } else {
            msg = SOSJadeMessageCodes.SOSJADE_T_0011.get();
        }
        setTextProperties();
        msg = objOptions.replaceVars(msg);
        jadeReportLogger.info(msg);
        LOGGER.info(msg);
    }

    private String showBannerHeaderSource(SOSConnection2OptionsAlternate options, boolean isSource) {
        StringBuffer sb = new StringBuffer();
        String pattern4String = "  | %-22s= %s%n";
        String pattern4Bool = "  | %-22s= %b%n";
        String pattern4IntegrityHash = "  | %-22s= %b (%s)%n";
        String pattern4Rename = "  | %-22s= %s -> %s%n";
        sb.append(String.format(pattern4String, "Protocol", options.protocol.Value()));
        sb.append(String.format(pattern4String, "Host", options.host.Value()));
        if (!options.protocol.isLocal()) {
            sb.append(String.format(pattern4String, "User", options.user.Value()));
            if (options.protocol.getEnum() != SOSOptionTransferType.enuTransferTypes.ftp
                    && options.protocol.getEnum() != SOSOptionTransferType.enuTransferTypes.zip) {
                sb.append(String.format(pattern4String, "AuthMethod", options.ssh_auth_method.Value()));
            }
            if (options.protocol.getEnum() == SOSOptionTransferType.enuTransferTypes.sftp
                    && !options.ssh_auth_method.Value().equalsIgnoreCase("password")) {
                sb.append(String.format(pattern4String, "AuthFile", "***"));
            } else {
                sb.append(String.format(pattern4String, "Password", "***"));
            }
            if (options.protocol.getEnum() == SOSOptionTransferType.enuTransferTypes.ftp) {
                sb.append(String.format(pattern4Bool, "Passive", options.passive_mode.value()));
                sb.append(String.format(pattern4String, "TransferMode", options.transfer_mode.Value()));
            }
        }

        if (isSource) {
            if (options.Directory.isDirty()) {
                sb.append(String.format(pattern4String, "Directory", options.Directory.Value()));
            }
            if (getOptions().file_path.IsNotEmpty()) {
                sb.append(String.format(pattern4String, "FilePath", getOptions().file_path.Value()));
            }
            if (getOptions().FileListName.isDirty()) {
                sb.append(String.format(pattern4String, "FileList", getOptions().FileListName.Value()));
            }
            if (getOptions().file_spec.isDirty()) {
                sb.append(String.format(pattern4String, "FileSpec", getOptions().file_spec.Value()));
            }
            sb.append(String.format(pattern4Bool, "ErrorWhenNoFilesFound", getOptions().force_files.value()));
            sb.append(String.format(pattern4Bool, "Recursive", getOptions().recursive.value()));
            if (getOptions().skip_transfer.isFalse()) {
                sb.append(String.format(pattern4Bool, "Remove", getOptions().remove_files.value()));
                if (getOptions().poll_interval.isDirty() || getOptions().poll_timeout.isDirty() || getOptions().poll_minfiles.isDirty()) {
                    sb.append(String.format(pattern4String, "PollingInterval", getOptions().poll_interval.Value()));
                    sb.append(String.format(pattern4String, "PollingTimeout", getOptions().poll_timeout.Value()));
                    sb.append(String.format(pattern4String, "PollingMinFiles", getOptions().poll_minfiles.Value()));
                }
                if (getOptions().CheckSteadyStateOfFiles.isTrue()) {
                    sb.append(String.format(pattern4String, "CheckSteadyInterval", getOptions().CheckSteadyStateInterval.Value()));
                    sb.append(String.format(pattern4String, "CheckSteadyCount", getOptions().CheckSteadyCount.Value()));
                }
            }
            if (getOptions().CheckIntegrityHash.isDirty()) {
                sb.append(String.format(pattern4IntegrityHash, "CheckIntegrity", getOptions().CheckIntegrityHash.value(), getOptions().IntegrityHashType.Value()));
            }
        } else {
            sb.append(String.format(pattern4String, "Directory", options.Directory.Value()));
            sb.append(String.format(pattern4Bool, "OverwriteFiles", getOptions().overwrite_files.value()));
            if (getOptions().append_files.isTrue()) {
                sb.append(String.format(pattern4Bool, "AppendFiles", getOptions().append_files.value()));
            }
            if (getOptions().compress_files.isTrue()) {
                sb.append(String.format(pattern4Bool, "CompressFiles", getOptions().compress_files.value()));
            }
            if (getOptions().CumulateFiles.isTrue()) {
                sb.append(String.format(pattern4Bool, "CumulateFiles", getOptions().CumulateFiles.value()));
                sb.append(String.format(pattern4Bool, "CumulateFileName", getOptions().CumulativeFileName.Value()));
            }
            if (getOptions().atomic_prefix.isDirty()) {
                sb.append(String.format(pattern4String, "AtomicPrefix", getOptions().atomic_prefix.Value()));
            }
            if (getOptions().atomic_suffix.isDirty()) {
                sb.append(String.format(pattern4String, "AtomicSuffix", getOptions().atomic_suffix.Value()));
            }
            if (getOptions().CreateIntegrityHashFile.isDirty()) {
                sb.append(String.format(pattern4IntegrityHash, "CreateIntegrityFile", getOptions().CreateIntegrityHashFile.value(), getOptions().IntegrityHashType.Value()));
            }
        }
        if (options.replacement.isDirty() && options.replacing.IsNotEmpty()) {
            sb.append(String.format(pattern4Rename, "Rename", options.replacing.Value(), options.replacement.Value()));
        }
        return sb.toString();
    }

    private void showBanner() {
        StringBuffer sb = new StringBuffer();
        if (objOptions.banner_header.isDirty()) {
            // this parameter should deprecated
            sb = new StringBuffer(objOptions.replaceVars(objOptions.banner_header.JSFile().getContent()));
        } else {
            String timestamp = "";
            try {
                timestamp = SOSOptionTime.getCurrentDateAsString(getOptions().DateFormatMask.Value()) + " "
                        + SOSOptionTime.getCurrentTimeAsString(getOptions().TimeFormatMask.Value());
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
            if (getOptions().settings.IsNotEmpty()) {
                sb.append(String.format(pattern4String, "SettingsFile", getOptions().settings.Value()));
            }
            if (getOptions().profile.IsNotEmpty()) {
                sb.append(String.format(pattern4String, "Profile", getOptions().profile.Value()));
            }
            sb.append(String.format(pattern4String, "Operation", getOptions().operation.Value()));
            sb.append(String.format(pattern4Bool, "Transactional", getOptions().transactional.value()));
            if (getOptions().skip_transfer.isDirty()) {
                sb.append(String.format(pattern4Bool, "SkipTransfer", getOptions().skip_transfer.value()));
            }
            if (getOptions().history.isDirty()) {
                sb.append(String.format(pattern4String, "History", getOptions().history.Value()));
            }
            if (getOptions().log_filename.isDirty()) {
                sb.append(String.format(pattern4String, "LogFile", getOptions().log_filename.Value()));
            }
            sb.append(String.format(pattern4SourceTarget, "Source"));
            sb.append(showBannerHeaderSource(getOptions().Source(), true));
            if (objOptions.NeedTargetClient()) {
                sb.append(String.format(pattern4SourceTarget, "Target"));
                sb.append(showBannerHeaderSource(getOptions().Target(), false));
            }
            sb.append("\n");
        }
        jadeReportLogger.info(sb.toString());
        LOGGER.info(sb.toString());
    }

    private void fillFileList(final String[] fileList, final String sourceDir) {
        if (objOptions.MaxFiles.isDirty() == true && fileList.length > objOptions.MaxFiles.value()) {
            for (int i = 0; i < objOptions.MaxFiles.value(); i++) {
                sourceFileList.add(fileList[i]);
            }
        } else {
            sourceFileList.add(fileList, sourceDir);
        }
    }

    public SOSFileList getFileList() {
        return sourceFileList;
    }

    private long getPollTimeout() {
        long pollTimeout = 0;
        if (objOptions.poll_timeout.isDirty()) {
            pollTimeout = objOptions.poll_timeout.value() * 60; // convert to
                                                                // seconds,
                                                                // poll_timeout
                                                                // is defined in
                                                                // minutes
        } else {
            pollTimeout = objOptions.PollingDuration.getTimeAsSeconds();
        }
        return pollTimeout;
    }

    private String getPollTimeoutText() {
        String pollTimeout = "";
        if (objOptions.poll_timeout.isDirty()) {
            pollTimeout = objOptions.poll_timeout.Value();
        } else {
            pollTimeout = objOptions.PollingDuration.Value();
        }
        return pollTimeout;
    }

    private String[] getSingleFileNames() {
        Vector<String> fileList = new Vector<String>();
        String localDir = objOptions.SourceDir.ValueWithFileSeparator();

        if (objOptions.file_path.IsNotEmpty() == true) {
            String filePath = objOptions.file_path.Value();
            LOGGER.debug(String.format("single file(s) specified : '%1$s'", filePath));

            try {
                String[] arr = filePath.split(";");
                for (String filename : arr) {
                    filename = filename.trim();
                    if (filename.length() > 0) {
                        if (localDir.trim().length() > 0) {
                            if (isAPathName(filename) == false) {
                                filename = localDir + filename;
                            }
                        }
                        fileList.add(filename);
                    }
                }
            } catch (Exception e) {
                throw new JobSchedulerException(String.format("error while reading file_path='%1$s': %2$s", objOptions.file_path.Value(), e.toString()));
            }
        }
        if (objOptions.FileListName.IsNotEmpty() == true) {
            String fileListName = objOptions.FileListName.Value();
            JSFile file = new JSFile(fileListName);
            if (file.exists() == true) {
                try {
                    StringBuffer line = null;
                    String filename = "";
                    while ((line = file.GetLine()) != null) {
                        filename = line.toString().trim();
                        if (filename.length() > 0) {
                            if (localDir.trim().length() > 0) {
                                if (isAPathName(filename) == false) {
                                    filename = localDir + filename;
                                }
                            }
                            fileList.add(filename);
                        }
                    }
                } catch (JobSchedulerException e1) {
                    throw e1;
                } catch (Exception e1) {
                    throw new JobSchedulerException(String.format("error while reading '%1$s': %2$s", objOptions.FileListName.Value(), e1.toString()));
                } finally {
                    try {
                        file.close();
                    } catch (IOException e) {
                    }
                }
            } else {
                throw new JobSchedulerException(String.format("%1$s doesn't exist.", objOptions.FileListName.Value()));
            }
        }

        return (fileList.size() > 0) ? fileList.toArray(new String[fileList.size()]) : new String[0];
    }

    @Override
    public String getState() {
        return (String) objOptions.getTextProperties().get(KEYWORD_STATE);
    }

    protected boolean isAPathName(final String path) {
        boolean ok = false;
        String aPath = path.replaceAll("\\\\", "/");
        if (aPath.startsWith("./") || aPath.startsWith("../")) { // relative to
                                                                 // localdir

        } else {
            // drive:/, protocol:/, /, ~/, $ (Unix Env), %...% (Windows Env)
            if (aPath.matches("^[a-zA-Z]+:/.*") || aPath.startsWith("/") || aPath.startsWith("~/") || aPath.startsWith("$")
                    || aPath.matches("^%[a-zA-Z_0-9.-]+%.*")) {
                ok = true;
            } else {
                // flgOK = (lstrPathName.contains("/") == true);
            }
        }
        return ok;
    }

    @Override
    public void Logout() {
        try {
            doLogout(targetClient);
            doLogout(sourceClient);
        } catch (Exception e) {
        }
    }

    private void makeDirs() {
        if (objOptions.skip_transfer.isFalse()) {
            makeDirs(objOptions.TargetDir.Value());
        }
    }

    private boolean makeDirs(final String path) {
        boolean cd = true;
        try {
            if (objOptions.makeDirs.value()) {
                if (SOSString.isEmpty(path)) {
                    throw new Exception("objOptions.TargetDir is empty");
                }
                if (targetClient.isDirectory(path)) {
                    cd = true;
                } else {
                    targetClient.mkdir(path);
                    cd = targetClient.isDirectory(path);
                }
            } else {
                if (path != null && path.length() > 0) {
                    cd = targetClient.isDirectory(path);
                }
            }
            // TODO alternative_remote_dir, wozu und wie gehen wir damit um?
            /*
             * if (cd == false &&
             * objOptions.alternative_remote_dir.IsNotEmpty()) {// alternative
             * Parameter String alternativeRemoteDir =
             * objOptions.alternative_remote_dir.Value();
             * LOGGER.debug("..try with alternative parameter [remoteDir=" +
             * alternativeRemoteDir + "]"); cd =
             * targetClient.isDirectory(alternativeRemoteDir);
             * objOptions.TargetDir.Value(alternativeRemoteDir); }
             */
        } catch (Exception e) {
            throw new JobSchedulerException("..error in makeDirs: " + e, e);
        }
        return cd;
    }

    private long oneOrMoreSingleFilesSpecified(final String sourceDir) {
        long founded = 0;
        // if (objOptions.skip_transfer.isFalse()) {
        sourceFileList.add(getSingleFileNames(), sourceDir, true);
        long currentFounded = sourceFileList.size();
        if (objOptions.isFilePollingEnabled() == true) {
            long currentPollingTime = 0;
            long pollInterval = objOptions.poll_interval.getTimeAsSeconds();
            long pollTimeout = getPollTimeout();
            while (true) {
                founded = 0;
                if (currentPollingTime > pollTimeout) { // time exceeded ?
                    LOGGER.info(String.format("polling: time '%1$s' is over. polling terminated", getPollTimeoutText()));
                    break;
                }
                for (SOSFileListEntry entry : sourceFileList.List()) {
                    if (entry.SourceFileExists()) {
                        founded++;
                        entry.setParent(sourceFileList); // to reinit the
                                                         // file-size
                    }
                }
                if (founded == currentFounded) {
                    break;
                }
                if (objOptions.poll_minfiles.value() > 0 && founded >= objOptions.poll_minfiles.value()) {
                    break;
                }
                String msg = String.format("file-polling: going to sleep for %1$d seconds. '%2$d' files found, waiting for '%3$d' files", pollInterval, founded, currentFounded);
                setInfo(msg);
                doSleep(pollInterval);
                currentPollingTime += pollInterval;
            }
        } else {
            founded = currentFounded;
        }
        setInfo(String.format("%1$d files found.", sourceFileList.size()));
        // }
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

    private boolean printState(boolean rc) throws Exception {
        String state = "processing successful ended";

        if (objOptions.FileListName.IsNotEmpty()) {
            state = SOSJADE_E_0098.params(objOptions.FileListName.Value());
        } else if (objOptions.file_path.IsNotEmpty()) {
            state = SOSJADE_E_0099.params(objOptions.file_path.Value());
        } else {
            state = SOSJADE_E_0100.params(objOptions.file_spec.Value());
        }
        SOSTransferStateCounts counts = getTransferCounts();
        if (sourceFileList.size() == 0) {
            if (objOptions.force_files.isTrue()) {
                objOptions.getTextProperties().put(KEYWORD_STATE, state);
                throw new JobSchedulerException(state);
            }
        } else {
            long countSuccess = counts.getSuccessTransfers();
            long countSkipped = counts.getSkippedTransfers();
            long countZeroByte = counts.getZeroBytesTransfers();
            if (countSuccess == 1) {
                state = SOSJADE_I_0100.get();
            } else {
                state = SOSJADE_I_0101.params(countSuccess);
            }
            if (countZeroByte > 0) {
                state += " " + SOSJADE_I_0102.params(countZeroByte);
            }
            if (countSkipped > 0) {
                state += " " + SOSJADE_I_0103.params(countSkipped);
            }
        }

        LOGGER.info(state);
        jadeReportLogger.info(state);
        objOptions.getTextProperties().put(KEYWORD_STATE, state);
        if (counts.getFailedTranfers() > 0 || JobSchedulerException.LastErrorMessage.length() > 0) {
            return false;
        }
        return true;
    }

    private void processSendMail(final SOSSmtpMailOptions mailOptions) {
        if (mailOptions != null && mailOptions.FileNotificationTo.isDirty() == true) {
            try {
                StringBuffer attachment = new StringBuffer(mailOptions.attachment.Value());
                if (mailOptions.attachment.isDirty()) {
                    attachment.append(";");
                }
                if (objOptions.log_filename.isDirty() == true) {
                    String logFileName = objOptions.log_filename.getHtmlLogFileName();
                    if (logFileName.length() > 0) {
                        attachment.append(logFileName);
                    }
                    logFileName = objOptions.log_filename.Value();
                    if (logFileName.length() > 0) {
                        if (attachment.length() > 0) {
                            attachment.append(";");
                        }
                        attachment.append(logFileName);
                    }
                    if (attachment.length() > 0) {
                        mailOptions.attachment.Value(attachment.toString());
                    }
                }
                if (mailOptions.subject.isDirty() == false) {
                    mailOptions.subject.Value("JADE: ");
                }
                StringBuffer subject = new StringBuffer(mailOptions.subject.Value());
                mailOptions.subject.Value(objOptions.replaceVars(subject.toString()));
                // http://www.sos-berlin.com/jira/browse/SOSFTP-201

                StringBuffer body = new StringBuffer(objOptions.replaceVars(mailOptions.body.Value()));
                body.append("\n").append("List of transferred Files:").append("\n");

                for (SOSFileListEntry entry : sourceFileList.List()) {
                    body.append(entry.getSourceFilename().replaceAll("\\\\", "/")).append("\n");
                }
                mailOptions.body.Value(body.toString());

                if (mailOptions.from.isDirty() == false) {
                    mailOptions.from.Value("JADE@sos-berlin.com");
                }

                SOSMail mail = new SOSMail(mailOptions.host.Value());
                LOGGER.debug(mailOptions.dirtyString());
                mail.sendMail(mailOptions);
            } catch (Exception e) {
                LOGGER.error(e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void run() {
        try {
            this.Execute();
        } catch (Exception e) {
            throw new JobSchedulerException(EXCEPTION_RAISED.get(e), e);
        }
    }

    /** TODO prüfen, ob eine Verlagerung in SOSFileList die bessere Lösung ist.
     * Stichwort: Multithreading
     * 
     * @param fileList */
    private void sendFiles(final SOSFileList fileList) {

        int maxParallelTransfers = 0;
        if (objOptions.ConcurrentTransfer.isTrue()) {
            // TODO resolve problem with apache ftp client in multithreading
            // mode
            // maxParallelTransfers = objOptions.MaxConcurrentTransfers.value();
        }
        if (maxParallelTransfers <= 0 || objOptions.CumulateFiles.isTrue()) {
            for (SOSFileListEntry entry : fileList.List()) {
                entry.Options(objOptions);
                entry.setDataSourceClient(sourceClient);
                entry.setDataTargetClient(targetClient);
                entry.setConnectionPool4Source(factory.getSourcePool());
                entry.setConnectionPool4Target(factory.getTargetPool());
                entry.run();
            }
        } else {
            SOSThreadPoolExecutor executor = new SOSThreadPoolExecutor(maxParallelTransfers);
            for (SOSFileListEntry entry : fileList.List()) {
                entry.Options(objOptions);
                entry.setConnectionPool4Source(factory.getSourcePool());
                entry.setConnectionPool4Target(factory.getTargetPool());
                executor.runTask(entry);
            }
            try {
                executor.shutDown();
                executor.objThreadPool.awaitTermination(1, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                LOGGER.error(e.getLocalizedMessage());
            }
        }
    }

    private void sendNotifications() {
        // TODO Nagios anbinden
        // TODO Status über MQ senden
        // TODO Fehler an JIRA, Peregrine, etc. senden. Ticket aufmachen.
        SOSSmtpMailOptions mailOptions = objOptions.getMailOptions();
        if (objOptions.mail_on_success.isTrue() && sourceFileList.FailedTransfers() <= 0 || mailOptions.FileNotificationTo.isDirty() == true) {
            doProcessMail(enuMailClasses.MailOnSuccess);
        }
        if (objOptions.mail_on_error.isTrue() && (sourceFileList.FailedTransfers() > 0 || JobSchedulerException.LastErrorMessage.length() > 0)) {
            doProcessMail(enuMailClasses.MailOnError);
        }
        if (objOptions.mail_on_empty_files.isTrue() && sourceFileList.getZeroByteCount() > 0) {
            doProcessMail(enuMailClasses.MailOnEmptyFiles);
        }
    }

    private void setInfo(final String msg) {
        LOGGER.info(msg);
        objJSJobUtilities.setStateText(msg);
    }

    private void setSystemProperties() {
        try {
            if (!SOSString.isEmpty(getOptions().system_property_files.Value())) {
                String files = getOptions().system_property_files.Value();
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

    /** TODO das muß beim JobScheduler-Job in die Parameter zurück, aber nur da
     * siehe hierzu das Interface ... */

    private void setTextProperties() {
        SOSTransferStateCounts counts = getTransferCounts();
        objOptions.getTextProperties().put(KEYWORD_SUCCESSFUL_TRANSFERS, String.valueOf(counts.getSuccessTransfers()));
        objOptions.getTextProperties().put(KEYWORD_FAILED_TRANSFERS, String.valueOf(counts.getFailedTranfers()));
        objOptions.getTextProperties().put(KEYWORD_SKIPPED_TRANSFERS, String.valueOf(counts.getSkippedTransfers() + counts.getZeroBytesTransfers()));
        if (JobSchedulerException.LastErrorMessage.length() <= 0) {
            objOptions.getTextProperties().put(KEYWORD_STATUS, SOSJADE_T_0012.get());
        } else {
            objOptions.getTextProperties().put(KEYWORD_STATUS, SOSJADE_T_0013.get());
        }
        objOptions.getTextProperties().put(KEYWORD_LAST_ERROR, JobSchedulerException.LastErrorMessage);
    }

    private long getSuccessfulTransfers() {
        long count = 0;
        if (sourceFileList != null) {
            count = sourceFileList.SuccessfulTransfers();
            if (countPollingServerFiles > 0) {
                count = countPollingServerFiles;
            }
        }
        return count;
    }

    private SOSTransferStateCounts getTransferCounts() {
        SOSTransferStateCounts counts = new SOSTransferStateCounts();
        if (sourceFileList != null) {
            counts = sourceFileList.countTransfers();
            if (countPollingServerFiles > 0) {
                counts.setSuccessTransfers(countPollingServerFiles);
            }
        }
        return counts;
    }

    public ISOSVfsFileTransfer getTargetClient() {
        return targetClient;
    }

    public ISOSVfsFileTransfer getSourceClient() {
        return sourceClient;
    }

    public boolean transfer() throws Exception {
        boolean rc = false;
        try { // to connect, authenticate and execute commands
            getOptions().CheckMandatory();

            LOGGER.debug(getOptions().dirtyString());
            LOGGER.debug("Source : " + getOptions().Source().dirtyString());
            LOGGER.debug("Target : " + getOptions().Target().dirtyString());

            setSystemProperties();
            setTextProperties();

            sourceFileList = new SOSFileList(targetHandler);
            sourceFileList.Options(objOptions);
            sourceFileList.StartTransaction();
            // TODO separate Operations: (1) connect and (2) authenticate
            // if (objConFactory == null) {
            factory = new SOSVfsConnectionFactory(objOptions);
            // }
            if (objOptions.LazyConnectionMode.isFalse() && objOptions.NeedTargetClient()) {
                targetClient = (ISOSVfsFileTransfer) factory.getTargetPool().getUnused();
                sourceFileList.objDataTargetClient = targetClient;
                makeDirs();
            }
            try {
                sourceClient = (ISOSVfsFileTransfer) factory.getSourcePool().getUnused();
                sourceFileList.objDataSourceClient = sourceClient;

                String sourceDir = objOptions.SourceDir.Value();
                String targetDir = objOptions.TargetDir.Value();
                long startPollingServer = System.currentTimeMillis() / 1000;
                countPollingServerFiles = 0;

                executePreTransferCommands();

                PollingServerLoop: while (true) {
                    if (objOptions.isFilePollingEnabled() == true) {
                        doPollingForFiles();
                        if (sourceFileList.size() <= 0 && objOptions.PollingServer.isFalse()) {
                            if (objOptions.PollErrorState.isDirty()) {
                                String pollErrorState = objOptions.PollErrorState.Value();
                                LOGGER.info("set order-state to " + pollErrorState);
                                setNextNodeState(pollErrorState);
                                break PollingServerLoop;
                            }
                        }
                    } else {
                        if (objOptions.OneOrMoreSingleFilesSpecified()) {
                            oneOrMoreSingleFilesSpecified(sourceDir);
                        } else {
                            // sourceClient.changeWorkingDirectory(sourceDir);
                            ISOSVirtualFile fileHandle = sourceClient.getFileHandle(sourceDir);
                            String msg = "";
                            if (objOptions.NeedTargetClient() == true) {
                                msg = "source directory/file: " + sourceDir + ", target directory: " + targetDir + ", file regexp: "
                                        + objOptions.file_spec.Value();
                            } else {
                                msg = SOSJADE_D_0200.params(sourceDir, objOptions.file_spec.Value());
                            }
                            LOGGER.debug(msg);
                            String integrityHashFileExtention = objOptions.CheckIntegrityHash.isTrue() ? "." + objOptions.IntegrityHashType.Value()
                                    : null;
                            selectFilesOnSource(fileHandle, objOptions.SourceDir, objOptions.file_spec, objOptions.recursive, integrityHashFileExtention);
                        }
                    }
                    // TODO checkSteadyStateOfFiles in FileListEntry einbauen
                    if (checkSteadyStateOfFiles() == false) { // not all files
                                                              // are steady ...
                        break PollingServerLoop;
                    }
                    sourceFileList.handleZeroByteFiles();
                    try {
                        if (objOptions.operation.isOperationGetList()) {
                            String msg = SOSJADE_I_0115.get();
                            LOGGER.info(msg);
                            jadeReportLogger.info(msg);

                            objOptions.remove_files.setFalse();
                            objOptions.force_files.setFalse();

                            sourceFileList.logFileList();
                            sourceFileList.CreateResultSetFile();
                        } else {
                            sourceFileList.CreateResultSetFile();
                            if (sourceFileList.size() > 0 && objOptions.skip_transfer.isFalse()) {
                                if (objOptions.LazyConnectionMode.isTrue()) {
                                    targetClient = (ISOSVfsFileTransfer) factory.getTargetPool().getUnused();
                                    sourceFileList.objDataTargetClient = targetClient;
                                    makeDirs();
                                }

                                sendFiles(sourceFileList);
                                // execute postTransferCommands after
                                // renameAtomicTransferFiles
                                // (transactional=true)-Problem
                                // http://www.sos-berlin.com/jira/browse/SOSFTP-186
                                sourceFileList.renameTargetAndSourceFiles();

                                executePostTransferCommands();

                                sourceFileList.deleteSourceFiles();
                                // if (objOptions.TransactionMode.isTrue()) {
                                sourceFileList.EndTransaction();
                                sendTransferHistory();
                                // }
                            }

                        }
                        // -----
                        if (objOptions.PollingServer.isFalse() || objOptions.skip_transfer.isTrue()) {
                            if (objOptions.NeedTargetClient() == true) {
                                targetClient.close();
                            }
                            sourceClient.close();
                            break PollingServerLoop;
                        } else {
                            if (objOptions.pollingServerDuration.isDirty() && objOptions.PollingServerPollForever.isFalse()) {
                                long currentTime = System.currentTimeMillis() / 1000;
                                long duration = currentTime - startPollingServer;
                                if (duration >= objOptions.pollingServerDuration.getTimeAsSeconds()) {
                                    LOGGER.debug("PollingServerMode: time elapsed. terminate polling server");
                                    break PollingServerLoop;
                                }
                            }
                            LOGGER.debug("PollingServerMode: start next polling cycle");
                            // TODO check external end signal for Polling server
                            // TODO end-time for polling server as an option
                            // sendNotifications();
                            countPollingServerFiles += sourceFileList.size();
                            sourceFileList.clearFileList();
                        }
                    } catch (JobSchedulerException e) {
                        String msg = null;
                        if (objOptions.transactional.value()) {
                            msg = TRANSACTION_ABORTED.get(e);
                        } else {
                            msg = TRANSFER_ABORTED.get(e);
                        }
                        LOGGER.error(msg);
                        jadeReportLogger.error(msg);
                        sourceFileList.Rollback();
                        sendTransferHistory();
                        throw e;
                    }
                }
                rc = printState(rc);
                return rc;
            } catch (Exception e) {
                rc = false;
                throw e;
            } finally {
                setTextProperties();
            }
        } catch (Exception e) {
            setTextProperties();
            objOptions.getTextProperties().put(KEYWORD_STATUS, SOSJADE_T_0013.get());
            jadeReportLogger.info(SOSJADE_E_0101.params(e.getLocalizedMessage()), e);
            try {
                executePostTransferCommandsOnError();
            } catch (Exception ex) {
                LOGGER.error("executePostTransferCommandsOnError: " + ex.toString());
            }
            throw e;
        } finally {
            try {
                executePostTransferCommandsFinal();
            } catch (Exception ex) {
                LOGGER.error("executePostTransferCommandsFinal: " + ex.toString());
            }
        }
    }

    private void executePreTransferCommands() throws Exception {
        SOSConnection2OptionsAlternate target = objOptions.Target();
        if (target.AlternateOptionsUsed.isTrue()) {
            target = target.Alternatives();
            executeTransferCommands("alternative_target_pre_transfer_commands", targetClient, target.Alternatives().PreTransferCommands);
        } else {
            executeTransferCommands("pre_transfer_commands", targetClient, objOptions.PreTransferCommands);
            executeTransferCommands("target_pre_transfer_commands", targetClient, target.PreTransferCommands);
        }
        SOSConnection2OptionsAlternate source = objOptions.Source();
        if (source.AlternateOptionsUsed.isTrue()) {
            executeTransferCommands("alternative_source_pre_transfer_commands", sourceClient, source.Alternatives().PreTransferCommands);
        } else {
            executeTransferCommands("source_pre_transfer_commands", sourceClient, source.PreTransferCommands);
        }
        // with JADE4DMZ it could be that the source.PreTransferCommands
        // needs more time than the target connection is still established
        if (objOptions.NeedTargetClient()) {
            targetClient.reconnect(target);
        }
    }

    private void executePostTransferCommands() throws Exception {
        SOSConnection2OptionsAlternate target = objOptions.Target();
        if (target.AlternateOptionsUsed.isTrue()) {
            target = target.Alternatives();
            executeTransferCommands("alternative_target_post_transfer_commands", targetClient, target.PostTransferCommands);
        } else {
            executeTransferCommands("post_transfer_commands", targetClient, objOptions.PostTransferCommands);
            executeTransferCommands("target_post_transfer_commands", targetClient, target.PostTransferCommands);
        }

        SOSConnection2OptionsAlternate source = objOptions.Source();
        String caller = "source_post_transfer_commands";
        if (source.AlternateOptionsUsed.isTrue()) {
            source = source.Alternatives();
            caller = "alternative_" + caller;
        }
        // with JADE4DMZ it could be that the target.PostTransferCommands
        // needs more time than the source connection is still established
        sourceClient.reconnect(source);
        executeTransferCommands(caller, sourceClient, source.PostTransferCommands);
    }

    private void executePostTransferCommandsOnError() throws Exception {
        StringBuffer exception = new StringBuffer();

        SOSConnection2OptionsAlternate target = objOptions.Target();
        String command = target.post_transfer_commands_on_error.Value();
        if (target.AlternateOptionsUsed.isTrue()) {
            command = target.Alternatives().post_transfer_commands_on_error.Value();
        }
        if (!SOSString.isEmpty(command)) {
            try {
                executeCommandOnTarget(command);
            } catch (Exception ex) {
                exception.append("[target client");
                if (target.AlternateOptionsUsed.isTrue()) {
                    exception.append(" alternate");
                }
                exception.append("] " + ex.toString());
            }
        }

        SOSConnection2OptionsAlternate source = objOptions.Source();
        command = source.post_transfer_commands_on_error.Value();
        if (source.AlternateOptionsUsed.isTrue()) {
            command = source.Alternatives().post_transfer_commands_on_error.Value();
        }

        if (!SOSString.isEmpty(command)) {
            try {
                // with JADE4DMZ it could be that the
                // target.PostTransferCommands
                // needs more time than the source connection is still
                // established
                if (sourceClient == null) {
                    throw new Exception("objDataSourceClient is NULL");
                }
                sourceClient.reconnect(source);
                executeCommandOnSource(command);
            } catch (Exception ex) {
                if (exception.length() > 0) {
                    exception.append(", ");
                }
                exception.append("[source client");
                if (source.AlternateOptionsUsed.isTrue()) {
                    exception.append(" alternate");
                }
                exception.append("] " + ex.toString());
            }
        }
        if (exception.length() > 0) {
            throw new Exception(exception.toString());
        }
    }

    private void executePostTransferCommandsFinal() throws Exception {
        StringBuffer exception = new StringBuffer();

        SOSConnection2OptionsAlternate target = objOptions.Target();
        String command = target.post_transfer_commands_final.Value();
        if (target.AlternateOptionsUsed.isTrue()) {
            command = target.Alternatives().post_transfer_commands_final.Value();
        }
        if (!SOSString.isEmpty(command)) {
            try {
                executeCommandOnTarget(command);
            } catch (Exception ex) {
                exception.append("[target client");
                if (target.AlternateOptionsUsed.isTrue()) {
                    exception.append(" alternate");
                }
                exception.append("] " + ex.toString());
            }
        }

        SOSConnection2OptionsAlternate source = objOptions.Source();
        command = source.post_transfer_commands_final.Value();
        if (source.AlternateOptionsUsed.isTrue()) {
            command = source.Alternatives().post_transfer_commands_final.Value();
        }
        if (!SOSString.isEmpty(command)) {
            try {
                // with JADE4DMZ it could be that the
                // target.PostTransferCommands
                // needs more time than the source connection is still
                // established
                if (sourceClient == null) {
                    throw new Exception("objDataSourceClient is NULL");
                }
                sourceClient.reconnect(source);
                executeCommandOnSource(command);
            } catch (Exception ex) {
                if (exception.length() > 0) {
                    exception.append(", ");
                }
                exception.append("[source client");
                if (source.AlternateOptionsUsed.isTrue()) {
                    exception.append(" alternate");
                }
                exception.append("] " + ex.toString());
            }
        }
        if (exception.length() > 0) {
            throw new Exception(exception.toString());
        }
    }

    public void executeCommandOnTarget(String command) throws Exception {
        if (targetClient == null) {
            throw new Exception("objDataTargetClient is NULL");
        }
        targetClient.getHandler().ExecuteCommand(command);
    }

    public void executeCommandOnSource(String command) throws Exception {
        if (sourceClient == null) {
            throw new Exception("objDataSourceClient is NULL");
        }
        sourceClient.getHandler().ExecuteCommand(command);
    }

    /** TODO Command separator as global option
     * 
     * @param commandCallerMethod
     * @param fileTransfer
     * @param commands
     * @throws Exception */
    private void executeTransferCommands(String commandCallerMethod, final ISOSVfsFileTransfer fileTransfer, final SOSOptionCommandString commands)
            throws Exception {
        if (commands.IsNotEmpty()) {
            LOGGER.info(commandCallerMethod);
            for (String command : commands.split()) {
                fileTransfer.getHandler().ExecuteCommand(objOptions.replaceVars(command));
            }
        }
    }

    private long selectFilesOnSource(final ISOSVirtualFile localeFile, final SOSOptionFolderName sourceDir, final SOSOptionRegExp regExp,
            final SOSOptionBoolean recursive, final String integrityHashFileExtention) throws Exception {
        if (sourceClient instanceof SOSVfsHTTP) {
            throw new JobSchedulerException("a file spec selection is not supported with http(s) protocol");
        }
        if (localeFile.isDirectory() == true) {
            if (sourceClient instanceof ISOSVfsFileTransfer2) {
                ISOSVfsFileTransfer2 ft = (ISOSVfsFileTransfer2) sourceClient;
                ft.clearFileListEntries();
                sourceFileList = ft.getFileListEntries(sourceFileList, sourceDir.Value(), regExp.Value(), recursive.value());
            } else {
                String[] fileList = sourceClient.getFilelist(sourceDir.Value(), regExp.Value(), 0, recursive.value(), integrityHashFileExtention);
                fillFileList(fileList, sourceDir.Value());
            }
            setInfo(String.format("%1$d files found for regexp '%2$s'.", sourceFileList.size(), regExp.Value()));
        } else { // not a directory, seems to be a filename
            sourceFileList.add(sourceDir.Value());
        }
        return sourceFileList.size();
    }

    public void sendTransferHistory() {
        if (objOptions.SendTransferHistory.isTrue()) {
            String schedulerHost = objOptions.scheduler_host.Value();
            if (isEmpty(schedulerHost)) {
                LOGGER.info("No data sent to the background service due to missing host name");
                return;
            }
            if (objOptions.scheduler_port.isNotDirty()) {
                LOGGER.info("No data sent to the background service due to missing port number");
                return;
            }

            for (SOSFileListEntry entry : sourceFileList.List()) {
                if (sendTransferHistory4File(entry)) {
                    countSentHistoryRecords++;
                }
            }
            // TODO I18N
            LOGGER.info(String.format("%s transfer history records sent to background service, scheduler = %s:%s ,job chain = %s, transfer method = %s", countSentHistoryRecords, objOptions.scheduler_host.Value(), objOptions.scheduler_port.Value(), objOptions.scheduler_job_chain.Value(), objOptions.Scheduler_Transfer_Method.Value()));
        } else {
            LOGGER.info(String.format("No data sent to the background service due to parameter '%1$s' = false", objOptions.SendTransferHistory.getShortKey()));
        }
    }

    /** TODO custom-fields einbauen bei JADE ist es moeglich "custom" Felder zu
     * definieren, die in der Transfer History als Auftragsparameter
     * mitgeschickt werden. Damit man diese Felder identifizieren kann, werden
     * hier Parameter defininiert, die beim Auftrag dabei sind, aber keine
     * "custom" Felder sind
     *
     * ? alternativ Metadaten der Tabelle lesen (Spalten) und mit den
     * Auftragsparameter vergleichen
     * 
     * @param entry
     * @return */
    private boolean sendTransferHistory4File(final SOSFileListEntry entry) {
        Map<String, String> fileProperties = entry.getFileAttributes(HistoryRecordType.XML);
        if (schedulerFactory == null) {
            schedulerFactory = new SchedulerObjectFactory(objOptions.scheduler_host.Value(), objOptions.scheduler_port.value());
            schedulerFactory.initMarshaller(Spooler.class);
            schedulerFactory.Options().TransferMethod.Value(objOptions.Scheduler_Transfer_Method.Value());
            schedulerFactory.Options().PortNumber.Value(objOptions.scheduler_port.Value());
            schedulerFactory.Options().ServerName.Value(objOptions.scheduler_host.Value());
        }
        JSCmdAddOrder addOrder = schedulerFactory.createAddOrder();
        addOrder.setJobChain(objOptions.scheduler_job_chain.Value() /* "scheduler_sosftp_history" */);

        Params params = schedulerFactory.setParams(fileProperties);
        addOrder.setParams(params);
        addOrder.run();

        return true;
    }
}
