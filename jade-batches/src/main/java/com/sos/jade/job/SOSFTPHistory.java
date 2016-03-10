package com.sos.jade.job;

import sos.connection.SOSConnection;
import sos.connection.SOSMySQLConnection;
import sos.connection.SOSPgSQLConnection;
import sos.scheduler.job.JobSchedulerJob;
import sos.settings.SOSProfileSettings;
import sos.spooler.Log;
import sos.spooler.Spooler;
import sos.spooler.Variable_set;
import sos.util.SOSLogger;

public class SOSFTPHistory {

    public static String TABLE_FILES = "SOSFTP_FILES";
    public static String TABLE_FILES_HISTORY = "SOSFTP_FILES_HISTORY";
    public static String TABLE_FILES_POSITIONS = "SOSFTP_FILES_POSITIONS";
    public static String SEQ_TABLE_FILES = "SOSFTP_FILES_ID_SEQ";
    private static boolean _doDebug = false;

    public static SOSConnection getConnection(Spooler spooler, SOSConnection conn, Variable_set parameters, SOSLogger log) throws Exception {
        try {
            if (parameters.value("db_class") != null && !parameters.value("db_class").isEmpty()) {
                if (conn != null) {
                    try {
                        conn.rollback();
                        conn.disconnect();
                    } catch (Exception ex) {
                        // gracefully ignore this error
                    }
                }
                log.debug3("connecting to database using order params ...");
                conn = SOSConnection.createInstance(parameters.value("db_class"), parameters.value("db_driver"), parameters.value("db_url"), parameters.value("db_user"), parameters.value("db_password"), log);
                conn.connect();
                log.debug3("connected to database using order params");
            } else {
                if (conn == null) {
                    log.debug3("connecting to database using Job Scheduler connection ...");
                    conn = JobSchedulerJob.getSchedulerConnection(new SOSProfileSettings(spooler.ini_path()), log);
                    conn.connect();
                    log.debug3("connected to database using Job Scheduler connection");
                } else {
                    log.debug3("using existing connection");
                }
            }
        } catch (Exception e) {
            throw new Exception("connect to database failed: " + e.getMessage(), e);
        }
        return conn;
    }

    public static String getNormalizedValue(String val) {
        if (val != null && !val.isEmpty()) {
            val = val.toLowerCase().replaceAll("\\\\", "/");
        }
        return val;
    }

    public static String getNormalizedField(SOSConnection conn, String value, int length) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        value = (value.length() > length) ? value.substring(0, length) : value;
        if (conn instanceof SOSPgSQLConnection || conn instanceof SOSMySQLConnection) {
            value = value.replaceAll("\\\\", "\\\\\\\\");
        }
        return value.replaceAll("'", "''");
    }

    public static void debugParams(Variable_set params, Log log) throws Exception {
        if (_doDebug && params != null && params.count() > 0) {
            String[] names = params.names().split(";");
            for (int i = 0; i < names.length; i++) {
                log.info("debugParams : " + names[i] + " = " + params.value(names[i]));
            }
        }
    }

}
