package com.sos.DataExchange.history;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.vfs.common.SOSCommonProvider;
import com.sos.vfs.common.SOSFileList;
import com.sos.vfs.common.SOSFileListEntry;
import com.sos.vfs.common.SOSFileListEntry.TransferStatus;
import com.sos.vfs.common.options.SOSBaseOptions;
import com.sos.vfs.common.options.SOSProviderOptions;
import com.sos.yade.commons.Yade;
import com.sos.yade.commons.Yade.TransferEntryState;
import com.sos.yade.commons.Yade.TransferProtocol;
import com.sos.yade.commons.result.YadeTransferResult;
import com.sos.yade.commons.result.YadeTransferResultEntry;
import com.sos.yade.commons.result.YadeTransferResultProtocol;
import com.sos.yade.commons.result.YadeTransferResultSerializer;

import sos.spooler.Task;
import sos.util.SOSString;

public class YadeTransferResultHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(YadeTransferResultHelper.class);
    private final YadeTransferResult result;

    public static boolean useSetHistoryField(HashMap<String, String> schedulerParams) {
        return schedulerParams != null && !SOSString.isEmpty(schedulerParams.get("return_values"));
    }

    public static void process2historyField(Task spoolerTask, SOSBaseOptions options, Instant startTime, Instant endTime, Throwable exception,
            SOSFileList entries) {
        process2historyField(spoolerTask, options, startTime, endTime, exception, entries, null, null, null);
    }

    public static void process2historyField(Task spoolerTask, SOSBaseOptions options, Instant startTime, Instant endTime, Throwable exception,
            SOSFileList entries, String sourceDir, String targetDir, String jumpDir) {

        try {
            YadeTransferResultHelper helper = new YadeTransferResultHelper(options, startTime, endTime, exception);
            helper.setEntries(entries, sourceDir, targetDir, jumpDir);
            helper.serialize2historyField(spoolerTask);
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
        }
    }

    public static void process2file(SOSBaseOptions options, Instant startTime, Instant endTime, Throwable exception, SOSFileList entries) {
        process2file(options, startTime, endTime, exception, entries, null, null, null);
    }

    public static void process2file(SOSBaseOptions options, Instant startTime, Instant endTime, Throwable exception, SOSFileList entries,
            String sourceDir, String targetDir, String jumpDir) {
        if (!SOSString.isEmpty(options.return_values.getValue())) {
            String file = options.return_values.getValue();
            LOGGER.debug("[return-values]process " + file);

            try {
                YadeTransferResultHelper helper = new YadeTransferResultHelper(options, startTime, endTime, exception);
                helper.setEntries(entries, sourceDir, targetDir, jumpDir);
                helper.serialize2File(file);
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace(String.format("[%s]%s", file, new String(Files.readAllBytes(Paths.get(file)), "UTF-8")));
                }
            } catch (Exception e) {
                LOGGER.error(e.toString(), e);
            }
        }
    }

    public YadeTransferResultHelper(SOSBaseOptions options, Instant startTime, Instant endTime, Throwable exception) throws Exception {
        result = new YadeTransferResult();
        result.setSource(getProtocol(options.getSource()));
        result.setTarget(getProtocol(options.getTarget()));
        result.setJump(getJumpProtocol(options));

        result.setMandator(SOSString.isEmpty(options.mandator.getValue()) ? null : options.mandator.getValue());
        result.setSettings(SOSString.isEmpty(options.settings.getValue()) ? null : options.settings.getValue());
        result.setProfile(SOSString.isEmpty(options.profile.getValue()) ? null : options.profile.getValue());
        result.setOperation(options.operation.getValue());

        result.setStart(startTime);
        result.setEnd(endTime);

        if (exception != null) {
            result.setErrorMessage(exception.getMessage());
        }
        if (!JobSchedulerException.LastErrorMessage.isEmpty()) {
            result.setErrorMessage(JobSchedulerException.LastErrorMessage);
        }
    }

    public void setEntries(SOSFileList list, String sourceDir, String targetDir, String jumpDir) {
        if (list == null || list.getList() == null || list.getList().size() == 0) {
            return;
        }
        List<YadeTransferResultEntry> entries = new ArrayList<>();
        for (SOSFileListEntry le : list.getList()) {
            YadeTransferResultEntry entry = new YadeTransferResultEntry();
            entry.setSource(sourceDir == null ? SOSCommonProvider.normalizePath(le.getSourceFilename()) : getSourcePath(sourceDir, jumpDir, le
                    .getSourceFilename()));
            entry.setTarget(targetDir == null ? getTargetPath(le.getTargetFileNameAndPath()) : SOSCommonProvider.normalizePath(targetDir, le
                    .getTargetFilename()));
            entry.setSize(le.getFileSize());
            entry.setModificationDate(le.getSourceFileModificationDateTime());
            entry.setIntegrityHash(le.getMd5());
            entry.setErrorMessage(SOSString.isEmpty(le.getLastErrorMessage()) ? null : le.getLastErrorMessage());
            entry.setState(getEntryStatus(le.getStatusText()));

            entries.add(entry);
        }
        result.setEntries(entries);
    }

    private String getSourcePath(String sourceDir, String jumpDir, String sourceFileName) {
        if (SOSString.isEmpty(jumpDir) || sourceFileName.length() <= jumpDir.length()) {
            return SOSCommonProvider.normalizePath(sourceFileName);
        }
        String relative = SOSCommonProvider.normalizePath(sourceFileName).substring(jumpDir.length());
        return SOSCommonProvider.normalizePath(sourceDir, relative);
    }

    private String getTargetPath(String filePath) {
        return SOSString.isEmpty(filePath) ? null : filePath;
    }

    private String getEntryStatus(String status) {
        try {
            TransferStatus ts = TransferStatus.valueOf(status);
            switch (ts) {
            case transferUndefined:
                return TransferEntryState.UNKNOWN.value();
            case waiting4transfer:
                return TransferEntryState.WAITING.value();
            case transferring:
                return TransferEntryState.TRANSFERRING.value();
            case transferInProgress:
                return TransferEntryState.IN_PROGRESS.value();
            case transferred:
                return TransferEntryState.TRANSFERRED.value();
            case transfer_skipped:
                return TransferEntryState.SKIPPED.value();
            case transfer_has_errors:
                return TransferEntryState.FAILED.value();
            case transfer_aborted:
                return TransferEntryState.ABORTED.value();
            case compressed:
                return TransferEntryState.COMPRESSED.value();
            case notOverwritten:
                return TransferEntryState.NOT_OVERWRITTEN.value();
            case deleted:
                return TransferEntryState.DELETED.value();
            case renamed:
                return TransferEntryState.RENAMED.value();
            case ignoredDueToZerobyteConstraint:
                return TransferEntryState.IGNORED_DUE_TO_ZEROBYTE_CONSTRAINT.value();
            case setBack:
                return TransferEntryState.ROLLED_BACK.value();
            case polling:
                return TransferEntryState.POLLING.value();
            case moved:
                return TransferEntryState.MOVED.value();
            }
        } catch (Throwable e) {
        }
        return TransferEntryState.UNKNOWN.value();
    }

    private YadeTransferResultProtocol getProtocol(SOSProviderOptions options) {
        YadeTransferResultProtocol p = new YadeTransferResultProtocol();
        p.setHost(options.host.getValue());
        setPortAndProtocol(p, options);
        p.setAccount(options.user.isDirty() && !SOSString.isEmpty(options.user.getValue()) ? options.user.getValue() : Yade.DEFAULT_ACCOUNT);
        return p;
    }

    private YadeTransferResultProtocol getJumpProtocol(SOSBaseOptions options) {
        YadeTransferResultProtocol p = null;
        if (options.jumpHost.isDirty()) {
            p = new YadeTransferResultProtocol();
            p.setHost(options.jumpHost.getValue());
            p.setPort(options.jumpPort.value());
            p.setProtocol(options.jumpProtocol.getValue());
            p.setAccount(options.jumpUser.isDirty() && !SOSString.isEmpty(options.jumpUser.getValue()) ? options.jumpUser.getValue()
                    : Yade.DEFAULT_ACCOUNT);
        }
        return p;
    }

    private void setPortAndProtocol(YadeTransferResultProtocol p, SOSProviderOptions options) {
        p.setPort(options.port.value());
        p.setProtocol(options.protocol.getValue());

        TransferProtocol tp = TransferProtocol.fromValue(p.getProtocol());
        if (TransferProtocol.UNKNOWN.equals(tp)) {
            tp = TransferProtocol.LOCAL;
            p.setProtocol(tp.value());
        }
        URL url;
        switch (tp) {
        case LOCAL:
            p.setPort(0);
            break;

        case WEBDAV:
            url = getURL(options.url.getUrl(), p.getHost());
            if (url != null && url.getProtocol().equalsIgnoreCase(TransferProtocol.WEBDAVS.value()) || url.getProtocol().toLowerCase().equals(
                    TransferProtocol.HTTPS.value())) {
                p.setProtocol(TransferProtocol.WEBDAVS.value());
            }
            break;
        case HTTP:
            url = getURL(options.url.getUrl(), p.getHost());
            if (url != null && url.getProtocol().toLowerCase().equals(TransferProtocol.HTTPS.value())) {
                p.setProtocol(TransferProtocol.HTTPS.value());
            }
            break;
        default:
            break;
        }

    }

    private URL getURL(URL url, String host) {
        if (url == null) {
            try {
                url = new URL(host);
            } catch (Throwable e) {
                LOGGER.error(e.toString(), e);
            }
        }
        return url;
    }

    public void serialize2File(String file) throws Exception {
        YadeTransferResultSerializer<YadeTransferResult> serializer = new YadeTransferResultSerializer<YadeTransferResult>();

        Files.write(Paths.get(file), new StringBuilder(Yade.JOB_ARGUMENT_NAME_RETURN_VALUES).append("=").append(serializer.serialize(result))
                .toString().getBytes());

    }

    public void serialize2historyField(Task spoolerTask) throws Exception {
        YadeTransferResultSerializer<YadeTransferResult> serializer = new YadeTransferResultSerializer<YadeTransferResult>();
        spoolerTask.set_history_field("TRANSFER_HISTORY", serializer.serialize(result));
    }
}
