package sos.jadehistory.job;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import sos.connection.SOSDB2Connection;
import sos.connection.SOSPgSQLConnection;
import sos.jadehistory.sql.Insert_cmd;
import sos.jadehistory.sql.Update_cmd;
import sos.scheduler.job.JobSchedulerJobAdapter;
import sos.spooler.Variable_set;
import sos.util.SOSClassUtil;
import sos.util.SOSDate;

import com.sos.JSHelper.Exceptions.JSNotImplementedException;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.JSHelper.io.Files.JSCsvFile;

public class JADEHistoryJob extends JobSchedulerJobAdapter {

    private static final int POSITION_REPEAT_COUNT = 3;
    private static final int POSITION_REPEAT_INTERVAL = 1;
    private static final int LENGTH10 = 10;
    private static final int LENGTH128 = 128;
    private static final int LENGTH255 = 255;
    private static final int LENGTH30 = 30;
    private static final int LENGTH40 = 40;
    private static final int LENGTH50 = 50;
    private static final String POSITION_REPEAT_COUNT_KEY = "position_repeat_count";
    private static final String POSITION_REPEAT_INTERVAL_KEY = "position_repeat_interval";
    private static final String OPERATION_SEND = "send";
    private static final String OPERATION_RECEIVE = "receive";
    private static final String PARAM_FILE_NAME = "scheduler_file_path";
    private static final String NULL_VALUE = "n/a";
    private static final String CREATED_BY = "sos";
    private static final String AND = "' and ";
    private static final String MODIFIED = "MODIFIED";
    private static final String MODIFIED_BY = "MODIFIED_BY";
    private static final String FILE_PREFIX = "file_prefix";
    private static final String MAPPING_FILE_SIZE = "mapping_file_size";
    private static final String MAPPING_GUID = "mapping_guid";
    private static final String MAPPING_JUMP_HOST = "mapping_jump_host";
    private static final String MAPPING_JUMP_HOST_IP = "mapping_jump_host_ip";
    private static final String MAPPING_JUMP_PORT = "mapping_jump_port";
    private static final String MAPPING_JUMP_PROTOCOL = "mapping_jump_protocol";
    private static final String MAPPING_JUMP_USER = "mapping_jump_user";
    private static final String MAPPING_LAST_ERROR_MESSAGE = "mapping_last_error_message";
    private static final String MAPPING_LOG_FILENAME = "mapping_log_filename";
    private static final String MAPPING_MANDATOR = "mapping_mandator";
    private static final String MAPPING_MD5 = "mapping_md5";
    private static final String MAPPING_OPERATION = "mapping_operation";
    private static final String MAPPING_PID = "mapping_pid";
    private static final String MAPPING_PPID = "mapping_ppid";
    private static final String MAPPING_PORT = "mapping_port";
    private static final String MAPPING_PROTOCOL = "mapping_protocol";
    private static final String MAPPING_SOURCE_DIR = "mapping_source_dir";
    private static final String MAPPING_SOURCE_FILENAME = "mapping_source_filename";
    private static final String MAPPING_SOURCE_HOST = "mapping_source_host";
    private static final String MAPPING_SOURCE_HOST_IP = "mapping_source_host_ip";
    private static final String MAPPING_SOURCE_USER = "mapping_source_user";
    private static final String MAPPING_STATUS = "mapping_status";
    private static final String MAPPING_TARGET_DIR = "mapping_target_dir";
    private static final String MAPPING_TARGET_FILENAME = "mapping_target_filename";
    private static final String MAPPING_TARGET_HOST = "mapping_target_host";
    private static final String MAPPING_TARGET_HOST_IP = "mapping_target_host_ip";
    private static final String MAPPING_TARGET_USER = "mapping_target_user";
    private static final String MAPPING_TRANSFER_END = "mapping_transfer_end";
    private static final String MAPPING_TRANSFER_START = "mapping_transfer_start";
    private static final String RECORD = "record ";
    private String filePath = "";
    private String filePrefix = "-in tab -csv -field-names";
    private LinkedHashMap<String, String> mappings = null;
    private LinkedHashMap<String, String> recordExcludedParameterNames = null;
    private LinkedHashMap<String, String> recordExtraParameterNames = null;
    private int recordSkippedCount = 0;
    private int recordSkippedErrorCount = 0;
    private int recordFoundCount = 0;
    private boolean exit = false;
    private String lastStatus = "";

    @Override
    public boolean spooler_process() {
        boolean rc = true;
        long recordCount = 0;
        Variable_set parameters = null;
        try {
            init();
            parameters = spooler.create_variable_set();
            if (spooler_task.params() != null) {
                parameters.merge(spooler_task.params());
            }
            if (spooler_job.order_queue() != null) {
                parameters.merge(spooler_task.order().params());
            }
            setConnection(JADEHistory.getConnection(spooler, getConnection(), parameters, getLogger()));
            recordCount = this.doImport(parameters);
            getLogger().info(
                    "records: imported = " + recordCount + " ( found = " + recordFoundCount + " skipped = " + recordSkippedCount
                            + " skipped [error] = " + recordSkippedErrorCount + " )");
            return spooler_job.order_queue() != null ? rc : false;
        } catch (Exception e) {
            spooler_log.error("error occurred " + e.getMessage());
            return false;
        }
    }

    public void init() throws Exception {
        initRecordMappings();
        initRecordExcludedParameterNames();
        recordExtraParameterNames = null;
        recordSkippedCount = 0;
        recordSkippedErrorCount = 0;
        recordFoundCount = 0;
        exit = false;
    }

    private void initRecordMappings() {
        mappings = new LinkedHashMap<String, String>();
        mappings.put(MAPPING_OPERATION, "operation");
        mappings.put(MAPPING_MANDATOR, "mandator");
        mappings.put(MAPPING_SOURCE_HOST, "localhost");
        mappings.put(MAPPING_SOURCE_HOST_IP, "localhost_ip");
        mappings.put(MAPPING_SOURCE_USER, "local_user");
        mappings.put(MAPPING_SOURCE_DIR, "local_dir");
        mappings.put(MAPPING_SOURCE_FILENAME, "local_filename");
        mappings.put(MAPPING_MD5, "md5");
        mappings.put(MAPPING_FILE_SIZE, "file_size");
        mappings.put(MAPPING_GUID, "guid");
        mappings.put(MAPPING_TRANSFER_START, "transfer_start");
        mappings.put(MAPPING_TRANSFER_END, "transfer_end");
        mappings.put(MAPPING_PID, "pid");
        mappings.put(MAPPING_PPID, "ppid");
        mappings.put(MAPPING_TARGET_HOST, "remote_host");
        mappings.put(MAPPING_TARGET_HOST_IP, "remote_host_ip");
        mappings.put(MAPPING_TARGET_USER, "remote_user");
        mappings.put(MAPPING_TARGET_DIR, "remote_dir");
        mappings.put(MAPPING_TARGET_FILENAME, "remote_filename");
        mappings.put(MAPPING_PROTOCOL, "protocol");
        mappings.put(MAPPING_PORT, "port");
        mappings.put(MAPPING_STATUS, "status");
        mappings.put(MAPPING_LAST_ERROR_MESSAGE, "last_error_message");
        mappings.put(MAPPING_LOG_FILENAME, "log_filename");
        mappings.put(MAPPING_JUMP_HOST, "jump_host");
        mappings.put(MAPPING_JUMP_HOST_IP, "jump_host_ip");
        mappings.put(MAPPING_JUMP_USER, "jump_user");
        mappings.put(MAPPING_JUMP_PROTOCOL, "jump_protocol");
        mappings.put(MAPPING_JUMP_PORT, "jump_port");
    }

    private void initRecordExcludedParameterNames() {
        recordExcludedParameterNames = new LinkedHashMap<String, String>();
        recordExcludedParameterNames.put("db_driver", "1");
        recordExcludedParameterNames.put("db_password", "1");
        recordExcludedParameterNames.put("db_url", "1");
        recordExcludedParameterNames.put("db_user", "1");
        recordExcludedParameterNames.put("db_class", "1");
        recordExcludedParameterNames.put("scheduler_order_configuration_loaded", "1");
        recordExcludedParameterNames.put("configuration_file", "1");
        recordExcludedParameterNames.put(FILE_PREFIX, "1");
    }

    public long doImport(final Variable_set parameters) throws Exception {
        long recordCount = 0;
        boolean isImportFile = false;
        try {
            isImportFile = parameters.value(PARAM_FILE_NAME) != null && parameters.value(PARAM_FILE_NAME).length() > 0;
            if (isImportFile) {
                getLogger().debug1("parameter [" + PARAM_FILE_NAME + "] found. make import file");
                recordCount = importFile(parameters);
            } else {
                getLogger().debug1("parameter [" + PARAM_FILE_NAME + "] not found. make import order");
                recordFoundCount++;
                recordCount = importOrder(parameters);
            }
        } catch (Exception e) {
            throw new JobSchedulerException(SOSClassUtil.getMethodName() + " : " + e.getMessage(), e);
        }
        return recordCount;
    }

    public long importOrder(final Variable_set parameters) throws Exception {
        long recordCount = 0;
        LinkedHashMap<String, String> recordParameters = new LinkedHashMap<String, String>();
        LinkedHashMap<String, String> recordExtraParameters = new LinkedHashMap<String, String>();
        LinkedHashMap<String, String> allParameters = new LinkedHashMap<String, String>();
        String[] paramsNames = parameters.names().split(";");
        for (int i = 0; i < paramsNames.length; i++) {
            if (mappings.containsValue(paramsNames[i])) {
                recordParameters.put(paramsNames[i], parameters.value(paramsNames[i]));
            } else {
                if (recordExcludedParameterNames != null && !recordExcludedParameterNames.containsKey(paramsNames[i].toLowerCase())) {
                    recordExtraParameters.put(paramsNames[i].toUpperCase(), parameters.value(paramsNames[i]));
                }
            }
            allParameters.put(paramsNames[i], parameters.value(paramsNames[i]));
        }
        if (recordParameters != null && mappings != null && recordParameters.size() == mappings.size()) {
            recordExtraParameters = checkRecordeCustomFields(recordExtraParameters);
            try {
                this.getLogger().info("importing from order");
                if (importLine(recordParameters, recordExtraParameters, true)) {
                    getConnection().commit();
                    recordCount++;
                } else {
                    getConnection().rollback();
                    recordSkippedCount++;
                }
            } catch (Exception e) {
                recordSkippedErrorCount++;
                try {
                    getConnection().rollback();
                } catch (Exception ex) {
                    // nothing to do?
                }
                String message = "error occurred importing order : " + e.getMessage();
                getLogger().error(message);
                throw new JobSchedulerException(message);
            }
        } else {
            Iterator<Entry<String, String>> it = mappings.entrySet().iterator();
            String params = "";
            while (it.hasNext()) {
                Entry<String, String> entry = it.next();
                String mappingsVal = entry.getValue();
                if (!allParameters.containsKey(mappingsVal)) {
                    params += params.length() > 0 ? "," + mappingsVal : mappingsVal;
                }
            }
            throw new JobSchedulerException(SOSClassUtil.getMethodName() + " : missing parameters for import order = " + params);
        }
        return recordCount;
    }

    public long importFile(final Variable_set parameters) throws Exception {
        long recordCount = 0;
        String filePrefixLocal = filePrefix;
        JSCsvFile hwFile = null;
        LinkedHashMap<String, String> hshRecordFields = null;
        LinkedHashMap<String, String> hshRecordExtraFields = null;
        String localFilename = null;
        long position = 0;
        long fileSize = 0;
        boolean foundPosition = false;
        int positionRepeatCountLocal = POSITION_REPEAT_COUNT;
        int positionRepeatIntervalLocal = POSITION_REPEAT_INTERVAL;
        long importFileSize = 0;
        StringBuffer sql = null;
        try {
            if (parameters.value(FILE_PREFIX) != null && parameters.value(FILE_PREFIX).length() > 0) {
                filePrefixLocal = parameters.value(FILE_PREFIX);
            }
            if (filePrefixLocal.toLowerCase().indexOf("-class=") != -1 || filePrefixLocal.toLowerCase().indexOf("-conn-str=") > -1) {
                throw new JSNotImplementedException("Database queries not supported anymore");
            } else {
                String fileName = parameters.value(PARAM_FILE_NAME);
                if (fileName == null || fileName.length() == 0) {
                    throw new JobSchedulerException("missing parameter \"" + PARAM_FILE_NAME + "\" for importFile");
                }
                hwFile = new JSCsvFile(fileName);
                hwFile.CheckColumnCount(false);
                if (!hwFile.exists()) {
                    throw new JobSchedulerException("file does not exist: " + hwFile.getAbsolutePath());
                }
                if (!hwFile.canRead()) {
                    throw new JobSchedulerException("cannot access file: " + hwFile.getAbsolutePath());
                }
                this.getLogger().info("importing entries from file [" + hwFile.getAbsolutePath() + "]");
                this.getLogger().debug("opening file source: " + filePrefixLocal + hwFile.getAbsolutePath());
                localFilename = hwFile.getName().toLowerCase();
                importFileSize = hwFile.length();
                this.getLogger().info(
                        "getting file position for  local filename = " + localFilename + " (current import file size = " + importFileSize + ")");
                sql =
                        new StringBuffer("select \"POSITION\",\"FILE_SIZE\" from " + JADEHistory.TABLE_FILES_POSITIONS + " ").append("where \"LOCAL_FILENAME\" = '"
                                + JADEHistory.getNormalizedField(getConnection(), localFilename, LENGTH255) + "'");
                try {
                    if (parameters.value(POSITION_REPEAT_COUNT_KEY) != null && parameters.value(POSITION_REPEAT_COUNT_KEY).length() > 0) {
                        positionRepeatCountLocal = Integer.parseInt(parameters.value(POSITION_REPEAT_COUNT_KEY));
                        if (positionRepeatCountLocal <= 0) {
                            positionRepeatCountLocal = POSITION_REPEAT_COUNT;
                        }
                    }
                } catch (Exception e) {
                    positionRepeatCountLocal = POSITION_REPEAT_COUNT;
                }
                try {
                    if (parameters.value(POSITION_REPEAT_INTERVAL_KEY) != null && parameters.value(POSITION_REPEAT_INTERVAL_KEY).length() > 0) {
                        positionRepeatIntervalLocal = Integer.parseInt(parameters.value(POSITION_REPEAT_INTERVAL_KEY));
                        if (positionRepeatIntervalLocal <= 0) {
                            positionRepeatIntervalLocal = POSITION_REPEAT_INTERVAL;
                        }
                    }
                } catch (Exception e) {
                    positionRepeatIntervalLocal = POSITION_REPEAT_INTERVAL;
                }
                for (int p = 0; p < positionRepeatCountLocal; p++) {
                    HashMap<?, ?> recordPos = this.getConnection().getSingle(sql.toString());
                    if (recordPos != null && !recordPos.isEmpty()) {
                        fileSize = Long.parseLong(recordPos.get("file_size").toString());
                        if (importFileSize < fileSize) {
                            this.getLogger().debug1(
                                    "last found file position in database: " + recordPos.get("position").toString()
                                            + " (position will be not used : current import file size(" + importFileSize + ") < db file size("
                                            + fileSize + ") )");
                        } else {
                            position = Long.parseLong(recordPos.get("position").toString());
                            this.getLogger().debug1("last found file position in database: " + position + " (position will be used)");
                        }
                        foundPosition = true;
                    } else {
                        this.getLogger().debug1(
                                "not found file position for \"" + localFilename + "\" in database : try in " + positionRepeatIntervalLocal
                                        + "s again");
                    }
                    if (foundPosition) {
                        break;
                    } else {
                        Thread.sleep(positionRepeatIntervalLocal * 1000);
                    }
                }
                if (!foundPosition) {
                    this.getLogger().debug1("not found file position for \"" + localFilename + "\" in database : position will be skipped");
                }
            }
            String[] strValues = null;
            hwFile.loadHeaders();
            String[] strHeader = hwFile.Headers();
            for (String header : strHeader) {
                getLogger().debug1("Header-Field:" + header);
            }
            while ((strValues = hwFile.readCSVLine()) != null) {
                recordFoundCount++;
                if (position >= recordFoundCount) {
                    recordSkippedCount++;
                    continue;
                }
                hshRecordFields = new LinkedHashMap<String, String>();
                hshRecordExtraFields = new LinkedHashMap<String, String>();
                int j = 0;
                for (String val : strValues) {
                    String strFieldName = strHeader[j++];
                    if (val == null) {
                        val = "";
                    }
                    if (mappings.containsValue(strFieldName)) {
                        hshRecordFields.put(strFieldName, val);
                    } else {
                        hshRecordExtraFields.put(strFieldName.toUpperCase(), val);
                    }
                }
                hshRecordExtraFields = checkRecordeCustomFields(hshRecordExtraFields);
                try {
                    if (importLine(hshRecordFields, hshRecordExtraFields, false)) {
                        getConnection().commit();
                        getLogger().debug1(RECORD + recordFoundCount + " imported");
                        recordCount++;
                    } else {
                        getConnection().rollback();
                        recordSkippedCount++;
                        getLogger().debug1(RECORD + recordFoundCount + " skipped");
                    }
                } catch (Exception e) {
                    recordSkippedErrorCount++;
                    getLogger().error(
                            "error occurred importing file line " + (recordFoundCount + 1) + " (record " + recordFoundCount + ") : " + e.getMessage());
                    try {
                        getConnection().rollback();
                    } catch (Exception ex) {
                        // nothing to do?
                    }
                    if (exit) {
                        break;
                    }
                }
            }
            hwFile.close();
            if (foundPosition && position < recordFoundCount) {
                try {
                    sql =
                            new StringBuffer("update " + JADEHistory.TABLE_FILES_POSITIONS + " ").append(
                                    "set \"FILE_SIZE\" = " + importFileSize + ", ").append("    \"POSITION\" = " + recordFoundCount + " ").append(
                                    "where \"LOCAL_FILENAME\" = '" + JADEHistory.getNormalizedField(getConnection(), localFilename, LENGTH255) + "'");
                    getConnection().execute(sql.toString());
                    getConnection().commit();
                } catch (Exception ee) {
                    getConnection().rollback();
                }
            }
        } catch (Exception e) {
            throw new JobSchedulerException(SOSClassUtil.getMethodName() + " : " + e.getMessage(), e);
        } finally {
            if (hwFile != null) {
                try {
                    hwFile.close();
                } catch (Exception ex) {
                    // ignore this error
                }
            }
        }
        return recordCount;
    }

    public LinkedHashMap<String, String> checkRecordeCustomFields(final LinkedHashMap<String, String> recordExtraParameters) throws Exception {
        LinkedHashMap<String, String> paramsExtra = new LinkedHashMap<String, String>();
        if (recordExtraParameterNames == null) {
            recordExtraParameterNames = new LinkedHashMap<String, String>();
        }
        try {
            if (recordExtraParameters != null && !recordExtraParameters.isEmpty()) {
                Iterator<Entry<String, String>> it = recordExtraParameters.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<String, String> entry = it.next();
                    String field = entry.getKey().toUpperCase();
                    String val = entry.getValue();
                    try {
                        String checkedField = recordExtraParameterNames.get(field);
                        if (checkedField != null) {
                            if ("1".equals(checkedField)) {
                                paramsExtra.put(field, val);
                            }
                        } else {
                            getConnection().getSingleValue("select \"" + field + "\" from " + JADEHistory.TABLE_FILES_HISTORY + " where 1=2");
                            paramsExtra.put(field, val);
                            recordExtraParameterNames.put(field, "1");
                        }
                    } catch (Exception e) {
                        recordExtraParameterNames.put(field, "0");
                        if (getConnection() instanceof SOSPgSQLConnection) {
                            getConnection().rollback();
                        }
                    }
                }
            }
        } catch (Exception e) {
            paramsExtra = null;
        }
        return paramsExtra;
    }

    private void doLineDebug(final boolean isOrder, final String msg) throws Exception {
        if (isOrder) {
            getLogger().debug2(msg);
        } else {
            getLogger().debug9(msg);
        }
    }

    private boolean importLine(final LinkedHashMap<String, String> hstRecordFields, final LinkedHashMap<String, String> phshRecordCustomFields,
            final boolean isOrder) throws Exception {
        StringBuffer sql = new StringBuffer();
        try {
            String operation = getRecordValue(hstRecordFields, MAPPING_OPERATION);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": operation = " + operation);
            String status = getRecordValue(hstRecordFields, MAPPING_STATUS);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": status = " + status);
            lastStatus = status.toLowerCase();
            String mandator = getRecordValue(hstRecordFields, MAPPING_MANDATOR);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": mandator = " + mandator);
            String sourceHost = getRecordValue(hstRecordFields, MAPPING_SOURCE_HOST, operation);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": source_host = " + sourceHost);
            String sourceHostIp = getRecordValue(hstRecordFields, MAPPING_SOURCE_HOST_IP, operation);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": source_host_ip = " + sourceHostIp);
            String sourceUser = getRecordValue(hstRecordFields, MAPPING_SOURCE_USER, operation);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": source_user = " + sourceUser);
            String sourceDir = getRecordValue(hstRecordFields, MAPPING_SOURCE_DIR, operation);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": source_dir = " + sourceDir);
            String sourceFilename = getRecordValue(hstRecordFields, MAPPING_SOURCE_FILENAME, operation);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": source_filename = " + sourceFilename);
            String md5 = getRecordValue(hstRecordFields, MAPPING_MD5);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": md5 = " + md5);
            String fileSize = getRecordValue(hstRecordFields, MAPPING_FILE_SIZE);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": file_size = " + fileSize);
            String guid = getRecordValue(hstRecordFields, MAPPING_GUID);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": guid = " + guid);
            String transferStart = getRecordValue(hstRecordFields, MAPPING_TRANSFER_START);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": transfer_start = " + transferStart);
            String transferEnd = getRecordValue(hstRecordFields, MAPPING_TRANSFER_END);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": transfer_end = " + transferEnd);
            String pid = getRecordValue(hstRecordFields, MAPPING_PID);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": pid = " + pid);
            String ppid = getRecordValue(hstRecordFields, MAPPING_PPID);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": ppid = " + ppid);
            String targetHost = getRecordValue(hstRecordFields, MAPPING_TARGET_HOST, operation);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": target_host = " + targetHost);
            String targetHostIp = getRecordValue(hstRecordFields, MAPPING_TARGET_HOST_IP, operation);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": target_host_ip = " + targetHostIp);
            String targetUser = getRecordValue(hstRecordFields, MAPPING_TARGET_USER, operation);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": target_user = " + targetUser);
            String targetDir = getRecordValue(hstRecordFields, MAPPING_TARGET_DIR, operation);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": target_dir = " + targetDir);
            String targetFilename = getRecordValue(hstRecordFields, MAPPING_TARGET_FILENAME, operation);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": target_filename = " + targetFilename);
            String protocol = getRecordValue(hstRecordFields, MAPPING_PROTOCOL);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": protocol = " + protocol);
            String port = getRecordValue(hstRecordFields, MAPPING_PORT);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": port = " + port);
            String lastErrorMessage = getRecordValue(hstRecordFields, MAPPING_LAST_ERROR_MESSAGE);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": last_error_message = " + lastErrorMessage);
            String logFilename = getRecordValue(hstRecordFields, MAPPING_LOG_FILENAME);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": log_filename = " + logFilename);
            String jumpHost = getRecordValue(hstRecordFields, MAPPING_JUMP_HOST);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": jump_host = " + jumpHost);
            String jumpHostIp = getRecordValue(hstRecordFields, MAPPING_JUMP_HOST_IP);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": jump_host_ip = " + jumpHostIp);
            String jumpUser = getRecordValue(hstRecordFields, MAPPING_JUMP_USER);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": jump_user = " + jumpUser);
            String jumpProtocol = getRecordValue(hstRecordFields, MAPPING_JUMP_PROTOCOL);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": jump_protocol = " + jumpProtocol);
            String jumpPort = getRecordValue(hstRecordFields, MAPPING_JUMP_PORT);
            doLineDebug(isOrder, RECORD + recordFoundCount + ": jump_port = " + jumpPort);
            if (phshRecordCustomFields != null && !phshRecordCustomFields.isEmpty()) {
                for (String key : phshRecordCustomFields.keySet()) {
                    String entry = phshRecordCustomFields.get(key);
                    doLineDebug(isOrder, RECORD + recordFoundCount + ": " + key.toLowerCase() + " = " + entry);
                }
            }
            sql.append("select \"ID\" ").append("from " + JADEHistory.TABLE_FILES + " ").append("where \"MANDATOR\" = '" + mandator + AND).append(
                    "       \"SOURCE_HOST\" = '" + sourceHost + AND).append("       \"SOURCE_HOST_IP\" = '" + sourceHostIp + AND).append(
                    "       \"SOURCE_DIR\" = '" + sourceDir + AND).append("       \"SOURCE_FILENAME\" = '" + sourceFilename + AND).append(
                    "       \"SOURCE_USER\" = '" + sourceUser + AND).append("       \"MD5\" = '" + md5 + "'");
            String filesId = getConnection().getSingleValue(sql.toString());
            if (filesId == null || filesId.length() == 0 || "0".equals(filesId)) {
                Insert_cmd insert1 = new Insert_cmd(getConnection(), getLogger(), JADEHistory.TABLE_FILES);
                insert1.withQuote = true;
                insert1.set("MANDATOR", mandator);
                insert1.set("SOURCE_HOST", sourceHost);
                insert1.set("SOURCE_HOST_IP", sourceHostIp);
                insert1.set("SOURCE_DIR", sourceDir);
                insert1.set("SOURCE_FILENAME", sourceFilename);
                insert1.set("SOURCE_USER", sourceUser);
                insert1.set("MD5", md5);
                insert1.set_num("FILE_SIZE", fileSize);
                insert1.set_direct("CREATED", "%now");
                insert1.set("CREATED_BY", CREATED_BY);
                insert1.set_direct(MODIFIED, "%now");
                insert1.set(MODIFIED_BY, CREATED_BY);
                getConnection().execute(insert1.make_cmd());
                if (getConnection() instanceof SOSDB2Connection) {
                    filesId = getConnection().getSingleValue("values identity_val_local()");
                } else {
                    filesId = getConnection().getLastSequenceValue(JADEHistory.SEQ_TABLE_FILES);
                }
                if (filesId == null || filesId.length() == 0 || "0".equals(filesId)) {
                    throw new JobSchedulerException("not found lastSequenceValue: SEQ [" + JADEHistory.SEQ_TABLE_FILES + "] for table "
                            + JADEHistory.TABLE_FILES);
                }
            }
            Insert_cmd insert2 = new Insert_cmd(getConnection(), getLogger(), JADEHistory.TABLE_FILES_HISTORY);
            insert2.withQuote = true;
            insert2.set("GUID", guid);
            insert2.set("JADE_ID", filesId);
            insert2.set("OPERATION", operation);
            insert2.set("TRANSFER_START", transferStart);
            insert2.set("TRANSFER_END", transferEnd);
            insert2.set_num("PID", pid);
            insert2.set_num("PPID", ppid);
            insert2.set("TARGET_HOST", targetHost);
            insert2.set("TARGET_HOST_IP", targetHost);
            insert2.set("TARGET_USER", targetUser);
            insert2.set("TARGET_DIR", targetDir);
            insert2.set("TARGET_FILENAME", targetFilename);
            insert2.set("PROTOCOL", protocol);
            insert2.set_num("PORT", port);
            insert2.set("STATUS", status);
            insert2.setNull("LAST_ERROR_MESSAGE", lastErrorMessage);
            insert2.setNull("LOG_FILENAME", logFilename);
            insert2.setNull("JUMP_HOST", jumpHost);
            insert2.setNull("JUMP_HOST_IP", jumpHostIp);
            insert2.setNull("JUMP_USER", jumpUser);
            insert2.setNull("JUMP_PROTOCOL", jumpProtocol);
            insert2.set_numNull("JUMP_PORT", jumpPort);
            if (phshRecordCustomFields != null && !phshRecordCustomFields.isEmpty()) {
                Iterator<Entry<String, String>> it = phshRecordCustomFields.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<String, String> entry = it.next();
                    String val = entry.getValue();
                    if (val == null || val.length() == 0) {
                        val = "NULL";
                    } else {
                        val = JADEHistory.getNormalizedField(getConnection(), val, LENGTH255);
                    }
                    insert2.set(entry.getKey(), val);
                }
            }
            String g = getConnection().getSingleValue("select \"GUID\" from " + JADEHistory.TABLE_FILES_HISTORY + " where \"GUID\" = '" + guid + "'");
            if (g == null || g.length() == 0 || isOrder) {
                insert2.set_direct(MODIFIED, "%now");
                insert2.set(MODIFIED_BY, CREATED_BY);
                insert2.set_direct("CREATED", "%now");
                insert2.set("CREATED_BY", CREATED_BY);
                getConnection().execute(insert2.make_cmd());
                return true;
            } else {
                if (isOrder) {
                    Update_cmd update1 = new Update_cmd(getConnection(), getLogger(), JADEHistory.TABLE_FILES_HISTORY);
                    update1.withQuote = true;
                    update1.set_where("GUID='" + guid + "'");
                    update1.copyFieldsFrom(insert2);
                    update1.set_direct(MODIFIED, "%now");
                    update1.set(MODIFIED_BY, CREATED_BY);
                    getConnection().execute(update1.make_cmd());
                }
            }
        } catch (Exception e) {
            throw new JobSchedulerException(SOSClassUtil.getMethodName() + " : " + e.getMessage());
        }
        return false;
    }

    protected String getRecordValue(final HashMap<String, String> parameters, final String mappingName) throws Exception {
        return getRecordValue(parameters, mappingName, null);
    }

    private String getRecordValue(final HashMap<String, String> record, final String mappingName, final String operation) throws Exception {
        int len = -1;
        String attrName = mappings.get(mappingName);
        if (attrName == null) {
            throw new JobSchedulerException("no found mapping name \"" + mappingName + "\"");
        }
        String attrVal = record.get(attrName);
        if (attrVal == null) {
            exit = true;
            throw new JobSchedulerException("no found attr name \"" + attrName + "\"");
        }
        attrVal = attrVal.trim();
        if (MAPPING_OPERATION.equals(mappingName)) {
            len = LENGTH30;
            attrVal = attrVal.toLowerCase();
        } else if (MAPPING_MANDATOR.equals(mappingName)) {
            len = LENGTH30;
            attrVal = attrVal.toLowerCase();
        } else if (MAPPING_SOURCE_HOST.equals(mappingName)) {
            len = LENGTH128;
            if (operation != null && OPERATION_RECEIVE.equals(operation)) {
                attrVal = getRecordValue(record, MAPPING_TARGET_HOST, OPERATION_SEND);
            }
        } else if (MAPPING_SOURCE_HOST_IP.equals(mappingName)) {
            len = LENGTH30;
            if (operation != null && OPERATION_RECEIVE.equals(operation)) {
                attrVal = getRecordValue(record, MAPPING_TARGET_HOST_IP, OPERATION_SEND);
            }
        } else if (MAPPING_SOURCE_USER.equals(mappingName)) {
            len = LENGTH128;
            if (operation != null && OPERATION_RECEIVE.equals(operation)) {
                attrVal = getRecordValue(record, MAPPING_TARGET_USER, OPERATION_SEND);
            }
        } else if (MAPPING_SOURCE_DIR.equals(mappingName)) {
            len = LENGTH255;
            if (operation != null && OPERATION_RECEIVE.equals(operation)) {
                attrVal = getRecordValue(record, MAPPING_TARGET_DIR, OPERATION_SEND);
            }
        } else if (MAPPING_SOURCE_FILENAME.equals(mappingName)) {
            len = LENGTH255;
            if (operation != null && OPERATION_RECEIVE.equals(operation)) {
                attrVal = getRecordValue(record, MAPPING_TARGET_FILENAME, OPERATION_SEND);
            }
        } else if (MAPPING_GUID.equals(mappingName)) {
            len = LENGTH40;
        } else if (MAPPING_TRANSFER_START.equals(mappingName)) {
            if (attrVal.length() > 0) {
                try {
                    SOSDate.getDateTimeAsString(attrVal, "yyyy-MM-dd HH:mm:ss");
                } catch (Exception e) {
                    throw new JobSchedulerException("illegal value for parameter [" + attrName + "] found [yyyy-MM-dd HH:mm:ss]: " + attrVal);
                }
            }
        } else if (MAPPING_TRANSFER_END.equals(mappingName)) {
            if (attrVal.length() > 0) {
                try {
                    SOSDate.getDateTimeAsString(attrVal, "yyyy-MM-dd HH:mm:ss");
                } catch (Exception e) {
                    throw new JobSchedulerException("illegal value for parameter [" + attrName + "] found [yyyy-MM-dd HH:mm:ss]: " + attrVal);
                }
            }
        } else if (MAPPING_FILE_SIZE.equals(mappingName) || MAPPING_PID.equals(mappingName) || MAPPING_PPID.equals(mappingName)
                || MAPPING_PORT.equals(mappingName) || MAPPING_JUMP_PORT.equals(mappingName)) {
            if (attrVal.length() == 0) {
                attrVal = "0";
            } else {
                try {
                    Integer.parseInt(attrVal);
                } catch (Exception e) {
                    throw new JobSchedulerException("illegal non-numeric value for parameter [" + attrName + "]: " + attrVal);
                }
            }
        } else if (MAPPING_TARGET_HOST.equals(mappingName)) {
            len = LENGTH128;
            if (operation != null && OPERATION_RECEIVE.equals(operation)) {
                attrVal = getRecordValue(record, MAPPING_SOURCE_HOST, OPERATION_SEND);
            }
        } else if (MAPPING_TARGET_HOST_IP.equals(mappingName)) {
            len = LENGTH30;
            if (operation != null && OPERATION_RECEIVE.equals(operation)) {
                attrVal = getRecordValue(record, MAPPING_SOURCE_HOST_IP, OPERATION_SEND);
            }
        } else if (MAPPING_TARGET_USER.equals(mappingName)) {
            len = LENGTH128;
            if (operation != null && OPERATION_RECEIVE.equals(operation)) {
                attrVal = getRecordValue(record, MAPPING_SOURCE_USER, OPERATION_SEND);
            }
        } else if (MAPPING_TARGET_DIR.equals(mappingName)) {
            len = LENGTH255;
            if (operation != null && OPERATION_RECEIVE.equals(operation)) {
                attrVal = getRecordValue(record, MAPPING_SOURCE_DIR, OPERATION_SEND);
            }
        } else if (MAPPING_TARGET_FILENAME.equals(mappingName)) {
            len = LENGTH255;
            if (operation != null && OPERATION_RECEIVE.equals(operation)) {
                attrVal = getRecordValue(record, MAPPING_SOURCE_FILENAME, OPERATION_SEND);
            }
        } else if (MAPPING_PROTOCOL.equals(mappingName)) {
            len = LENGTH10;
        } else if (MAPPING_MD5.equals(mappingName)) {
            len = LENGTH50;
        } else if (MAPPING_STATUS.equals(mappingName)) {
            len = LENGTH30;
        } else if (MAPPING_LAST_ERROR_MESSAGE.equals(mappingName)) {
            len = LENGTH255;
            attrVal = getNormalizedMessage(attrVal, len);
        } else if (MAPPING_LOG_FILENAME.equals(mappingName)) {
            len = LENGTH255;
        } else if (MAPPING_JUMP_HOST.equals(mappingName)) {
            len = LENGTH128;
        } else if (MAPPING_JUMP_HOST_IP.equals(mappingName)) {
            len = LENGTH30;
        } else if (MAPPING_JUMP_USER.equals(mappingName)) {
            len = LENGTH128;
        } else if (MAPPING_JUMP_PROTOCOL.equals(mappingName)) {
            len = LENGTH10;
        }
        if (attrVal.length() == 0) {
            attrVal = NULL_VALUE;
        }
        return len > 0 ? JADEHistory.getNormalizedField(getConnection(), attrVal, len) : attrVal;
    }

    private String getNormalizedMessage(String val, int len) {
        val = val.replaceAll("&quot;", "\"").replaceAll("&apos;", "'").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&");
        if (val.startsWith("\"") && val.endsWith("\"")) {
            val = val.replaceFirst("^\"", "").replaceFirst("\"$", "").replaceAll("\"\"", "\"");
        }
        int beginCut = val.length() - len;
        if (beginCut > 0) {
            val = val.substring(beginCut);
        }
        return val;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(final String path) {
        this.filePath = path;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    public void setFilePrefix(final String prefix) {
        this.filePrefix = prefix;
    }

    public LinkedHashMap<String, String> getMappings() {
        return mappings;
    }

    public void setMappings(final LinkedHashMap<String, String> mappings) {
        this.mappings = mappings;
    }
}
