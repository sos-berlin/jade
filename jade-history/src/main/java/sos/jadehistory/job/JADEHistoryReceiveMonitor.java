package sos.jadehistory.job;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import sos.connection.SOSConnection;
import sos.scheduler.job.JobSchedulerJob;
import sos.spooler.Monitor_impl;
import sos.spooler.Variable_set;
import sos.util.SOSClassUtil;
import sos.util.SOSSchedulerLogger;
import sos.util.SOSString;

public class JADEHistoryReceiveMonitor extends Monitor_impl {

    private static final int LENGTH128 = 128;
    private static final int LENGTH255 = 255;

    @Override
    public boolean spooler_process_before() {
        try {
            spooler_task.order().params().set_var("replacing", "[.]csv");
            spooler_task.order().params().set_var("replacement", "{sos[date:yyyyMMddHHmmssSSS]sos}.csv");
            return true;
        } catch (Exception e) {
            spooler_log.warn("error occurred in spooler_process_before(222): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean spooler_process_after(final boolean arg0) throws Exception {
        if (!arg0) {
            return arg0;
        }
        SOSConnection conn = null;
        SOSSchedulerLogger log = null;
        Variable_set parameters = null;
        String host = null;
        String remoteDir = null;
        try {
            parameters = spooler.create_variable_set();
            if (spooler_task.params() != null) {
                parameters.merge(spooler_task.params());
            }
            if (spooler_job.order_queue() != null) {
                parameters.merge(spooler_task.order().params());
            }
            JADEHistory.debugParams(parameters, spooler_log);
            if (parameters != null && parameters.count() > 0) {
                if ("0".equals(parameters.value("ftp_result_files"))) {
                    spooler_log.debug9("no files were received");
                } else {
                    host = parameters.value("source_host");
                    remoteDir = parameters.value("source_dir");
                    if (host != null && host.length() > 0 && remoteDir != null && remoteDir.length() > 0) {
                        try {
                            String[] files = parameters.value("ftp_result_filepaths").split(";");
                            log = new SOSSchedulerLogger(spooler_log);
                            conn = JADEHistory.getConnection(getHibernateConfigurationReporting(), parameters, log);
                            conn.connect();
                            for (String file : files) {
                                fillPosition(conn, host, remoteDir, file);
                            }
                        } catch (Exception e) {
                            spooler_log.info("ERROR : cannot found position : " + e.getMessage());
                        } finally {
                            try {
                                if (conn != null) {
                                    conn.disconnect();
                                }
                            } catch (Exception e) {
                                // nothing to do
                            }
                        }
                    } else {
                        spooler_log.debug3("missing host or ftp_remote_dir for file positions : host = " + host + " ftp_remote_dir = " + remoteDir);
                    }
                }
            }
        } catch (Exception e) {
            // nothing to do
        }
        return super.spooler_process_after(arg0);
    }

    private void fillPosition(final SOSConnection conn, String host, String remoteDir, String file) throws Exception {
        StringBuffer sql = new StringBuffer();
        StringBuilder s = new StringBuilder();
        String remoteFilename = "";
        String localFilename = "";
        try {
            host = JADEHistory.getNormalizedValue(host);
            remoteDir = JADEHistory.getNormalizedValue(remoteDir);
            file = JADEHistory.getNormalizedValue(file);
            String[] fp = file.split("/");
            localFilename = fp[fp.length - 1];
            remoteFilename = localFilename.toLowerCase().replaceAll("(\\{sos)(.)*(sos\\})(\\.csv)$", "\\.csv");
            sql =
                    new StringBuffer("select \"LOCAL_FILENAME\" from " + JADEHistory.TABLE_FILES_POSITIONS + " ").append(
                            "where \"HOST\" = '" + JADEHistory.getNormalizedField(conn, host, LENGTH128) + "' and ").append(
                            "   \"REMOTE_DIR\" = '" + JADEHistory.getNormalizedField(conn, remoteDir, LENGTH255) + "' and ").append(
                            "   \"REMOTE_FILENAME\" = '" + JADEHistory.getNormalizedField(conn, remoteFilename, LENGTH255) + "'");
            String lastLocalFileName = conn.getSingleValue(sql.toString());
            if (lastLocalFileName == null || lastLocalFileName.length() == 0) {
                sql =
                        new StringBuffer("insert into " + JADEHistory.TABLE_FILES_POSITIONS
                                + "(\"HOST\",\"REMOTE_DIR\",\"REMOTE_FILENAME\",\"LOCAL_FILENAME\",\"FILE_SIZE\",\"POSITION\") ").append("values('"
                                + JADEHistory.getNormalizedField(conn, host, LENGTH128) + "','"
                                + JADEHistory.getNormalizedField(conn, remoteDir, LENGTH255) + "','"
                                + JADEHistory.getNormalizedField(conn, remoteFilename, LENGTH255) + "','"
                                + JADEHistory.getNormalizedField(conn, localFilename, LENGTH255) + "',0,0)");
            } else {
                sql =
                        new StringBuffer("update " + JADEHistory.TABLE_FILES_POSITIONS + " ").append(
                                "set \"LOCAL_FILENAME\" = '" + JADEHistory.getNormalizedField(conn, localFilename, LENGTH255) + "' ").append(
                                "where \"LOCAL_FILENAME\" = '" + JADEHistory.getNormalizedField(conn, lastLocalFileName, LENGTH255) + "'");
            }
            conn.execute(sql.toString());
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (Exception ee) {
                // nothing to do
            }
            throw new Exception(SOSClassUtil.getMethodName() + " : " + e.getMessage());
        }
    }

    private Path getHibernateConfigurationScheduler(){
        Variable_set vs = spooler.variables();
        if(vs != null){
            String var = vs.value(JobSchedulerJob.SCHEDULER_PARAM_HIBERNATE_SCHEDULER);
            if(!SOSString.isEmpty(var)){
                return Paths.get(var);
            }
        }
        return Paths.get(spooler.configuration_directory()).getParent().resolve(JobSchedulerJob.HIBERNATE_DEFAULT_FILE_NAME_SCHEDULER);
    }
    
    private Path getHibernateConfigurationReporting(){
        Variable_set vs = spooler.variables();
        if(vs != null){
            String var = vs.value(JobSchedulerJob.SCHEDULER_PARAM_HIBERNATE_REPORTING);
            if(!SOSString.isEmpty(var)){
                return Paths.get(var);
            }
        }
        Path configFile = Paths.get(spooler.configuration_directory()).getParent().resolve(JobSchedulerJob.HIBERNATE_DEFAULT_FILE_NAME_REPORTING);
        if(Files.exists(configFile)){
            return configFile;
        }
        return getHibernateConfigurationScheduler();
    }
}
