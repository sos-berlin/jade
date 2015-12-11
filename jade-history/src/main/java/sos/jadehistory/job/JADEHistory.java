package sos.jadehistory.job;

import sos.connection.SOSConnection;
import sos.connection.SOSMySQLConnection;
import sos.connection.SOSPgSQLConnection;
import sos.scheduler.job.JobSchedulerJob;
import sos.settings.SOSProfileSettings;
import sos.spooler.Log;
import sos.spooler.Spooler;
import sos.spooler.Variable_set;
import sos.util.SOSLogger;

public class JADEHistory {

    public static final String TABLE_FILES = "JADE_FILES";
    public static final String TABLE_FILES_HISTORY = "JADE_FILES_HISTORY";
    public static final String TABLE_FILES_POSITIONS = "JADE_FILES_POSITIONS";
    public static final String SEQ_TABLE_FILES = "JADE_FILES_ID_SEQ";
    private static final String DB_CLASS = "db_class";
    private static boolean _doDebug = false;

    public static SOSConnection getConnection(final Spooler spooler, SOSConnection conn, final Variable_set parameters, final SOSLogger log) throws Exception {
        try {
            if (parameters.value(DB_CLASS) != null && parameters.value(DB_CLASS).length() > 0) {
                if (conn != null) {
                    try {
                        conn.rollback();
                        conn.disconnect();
                    } catch (Exception ex) {
                        // nothing to do?
                    }
                }
                log.debug3("connecting to database using order params ...");
                conn = SOSConnection.createInstance(parameters.value(DB_CLASS), parameters.value("db_driver"), parameters.value("db_url"), 
                        parameters.value("db_user"), parameters.value("db_password"), log);
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
            throw new Exception("connect to database failed: " + e.getMessage());
        }
        return conn;
    }

    public static String getNormalizedValue(String val) {
        if (val != null && val.length() > 0) {
            val = val.toLowerCase().replaceAll("\\\\", "/");
        }
        return val;
    }

    public static String getNormalizedField(final SOSConnection conn, String value, final int length) {
        if (value == null || value.length() == 0) {
            return "";
        }
        value = value.length() > length ? value.substring(0, length) : value;
        if (conn instanceof SOSPgSQLConnection || conn instanceof SOSMySQLConnection) {
            value = value.replaceAll("\\\\", "\\\\\\\\");
        }
        return value.replaceAll("'", "''");
    }

    public static void debugParams(final Variable_set params, final Log log) throws Exception {
        if (_doDebug && params != null && params.count() > 0) {
            String[] names = params.names().split(";");
            for (String name : names) {
                log.info("debugParams : " + name + " = " + params.value(name));
            }
        }
    }

}
