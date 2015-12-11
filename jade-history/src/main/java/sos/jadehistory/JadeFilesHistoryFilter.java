package sos.jadehistory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import sos.jadehistory.db.JadeFilesDBItem;

import com.sos.hibernate.classes.DbItem;
import com.sos.hibernate.classes.SOSHibernateIntervalFilter;
import com.sos.hibernate.interfaces.ISOSHibernateFilter;

public class JadeFilesHistoryFilter extends SOSHibernateIntervalFilter implements ISOSHibernateFilter {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_MDINIGHT_DATE_FORMAT = "yyyy-MM-dd 00:00:00";
    private static final String DEFAULT_SECOND_BEFORE_MDINIGHT_DATE_FORMAT = "yyyy-MM-dd 23:59:59";
    private static final Logger LOGGER = Logger.getLogger(JadeFilesFilter.class);
    private String dateFormat1 = DEFAULT_DATE_FORMAT;
    private String guid;
    private Long jadeId;
    private String operation;
    private Date transferStartFrom;
    private Date transferStartTo;
    private Date transferEndFrom;
    private Date transferEndTo;
    private Integer pid;
    private Integer ppid;
    private String targetHost;
    private String targetHostIp;
    private String targetUser;
    private String targetDir;
    private String targetFilename;
    private String protocol;
    private Integer port;
    private String status;
    private String lastErrorMessage;
    private String logFilename;
    private String jumpHost;
    private String jumpHostIp;
    private String jumpUser;
    private String jumpProtocol;
    private Integer jumpPort;
    private Date createdFrom;
    private Date createdTo;
    private String createdBy;
    private Date modifiedFrom;
    private Date modifiedTo;
    private String modifiedBy;
    private String transferStartFromIso;
    private String transferStartToIso;
    private String transferEndFromIso;
    private String transferEndToIso;
    private String mandator;
    private Integer fileSize;
    private String sourceFile;
    private String sourceHost;

    private JadeFilesDBItem jadeFilesDBItem;

    public JadeFilesHistoryFilter() {
        // nothing to do
    }

    @Override
    public String getDateFormat() {
        return dateFormat1;
    }

    @Override
    public void setDateFormat(final String dateFormat) {
        dateFormat1 = dateFormat;
    }

    public Date getCreatedFrom() {
        return createdFrom;
    }

    public void setCreatedFrom(final String createdFrom) throws ParseException {
        if ("".equals(createdFrom)) {
            this.createdFrom = null;
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat1);
            Date d = formatter.parse(createdFrom);
            setCreatedFrom(d);
        }
    }

    public void setCreatedFrom(final Date from) {
        if (from != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_MDINIGHT_DATE_FORMAT);
            String d = formatter.format(from);
            try {
                formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
                createdFrom = formatter.parse(d);
            } catch (ParseException e) {
                LOGGER.error("", e);
            }
        }
    }

    public Date getCreatedTo() {
        return createdTo;
    }

    public void setCreatedTo(final String createdTo) throws ParseException {
        if ("".equals(createdTo)) {
            this.createdTo = null;
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat1);
            Date d = formatter.parse(createdTo);
            setCreatedTo(d);
        }
    }

    public void setCreatedTo(final Date to) {
        if (to != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_SECOND_BEFORE_MDINIGHT_DATE_FORMAT);
            String d = formatter.format(to);
            try {
                formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
                createdTo = formatter.parse(d);
            } catch (ParseException e) {
                LOGGER.error("", e);
            }
        }
    }

    @Override
    public String getTitle() {
        String s = "";
        if (transferStartFrom != null) {
            s += String.format("From: %s ", date2Iso(transferStartFrom));
        }
        if (transferStartTo != null) {
            s += String.format("To: %s ", date2Iso(transferStartTo));
        }
        return String.format("%1s ", s);
    }

    @Override
    public boolean isFiltered(final DbItem h) {
        return false;
    }

    @Override
    public void setIntervalFromDate(final Date d) {
        createdFrom = d;
    }

    @Override
    public void setIntervalToDate(final Date d) {
        createdTo = d;
    }

    @Override
    public void setIntervalFromDateIso(final String s) {
        transferStartFromIso = s;
    }

    @Override
    public void setIntervalToDateIso(final String s) {
        transferStartToIso = s;
    }

    public Date getModifiedFrom() {
        return modifiedFrom;
    }

    public void setModifiedFrom(final String modifiedFrom) throws ParseException {
        if ("".equals(modifiedFrom)) {
            this.modifiedFrom = null;
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat1);
            Date d = formatter.parse(modifiedFrom);
            setModifiedFrom(d);
        }
    }

    public Date getModifiedTo() {
        return modifiedTo;
    }

    public void setModifiedTo(final String modifiedTo) throws ParseException {
        if ("".equals(modifiedTo)) {
            this.modifiedTo = null;
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat1);
            Date d = formatter.parse(modifiedTo);
            setModifiedTo(d);
        }
    }

    public void setModifiedFrom(final Date from) {
        if (from != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_MDINIGHT_DATE_FORMAT);
            String d = formatter.format(from);
            try {
                formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
                modifiedFrom = formatter.parse(d);
            } catch (ParseException e) {
                LOGGER.error("", e);
            }
        }
    }

    public void setModifiedTo(final Date to) {
        if (to != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_SECOND_BEFORE_MDINIGHT_DATE_FORMAT);
            String d = formatter.format(to);
            try {
                formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
                modifiedTo = formatter.parse(d);
            } catch (ParseException e) {
                LOGGER.error("", e);
            }
        }
    }

    public Date getTransferStartFrom() {
        return transferStartFrom;
    }

    public void setTransferStartFrom(final Date from) {
        if (from != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_MDINIGHT_DATE_FORMAT);
            String d = formatter.format(from);
            try {
                formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
                transferStartFrom = formatter.parse(d);
            } catch (ParseException e) {
                LOGGER.error("", e);
            }
            formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            transferStartFromIso = formatter.format(from);
        }
    }

    public Date getTransferStartTo() {
        return transferStartTo;
    }

    public void setTransferStartTo(final String transferStartTo) throws ParseException {
        if ("".equals(transferStartTo)) {
            this.transferStartTo = null;
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat1);
            Date d = formatter.parse(transferStartTo);
            setTransferStartTo(d);
        }
    }

    public void setTransferStartTo(final Date to) {
        if (to != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_SECOND_BEFORE_MDINIGHT_DATE_FORMAT);
            String d = formatter.format(to);
            try {
                formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
                transferStartTo = formatter.parse(d);
            } catch (ParseException e) {
                LOGGER.error("", e);
            }
            formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            transferStartFromIso = formatter.format(to);
        }
    }

    public Date getTransferEndFrom() {
        return transferEndFrom;
    }

    public void setTransferEndFrom(final Date from) {
        if (from != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_MDINIGHT_DATE_FORMAT);
            String d = formatter.format(from);
            try {
                formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
                transferEndFrom = formatter.parse(d);
            } catch (ParseException e) {
                LOGGER.error("", e);
            }
            formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            transferEndFromIso = formatter.format(from);
        }
    }

    public Date getTransferEndTo() {
        return transferEndTo;
    }

    public void setTransferEndTo(final String transferEndTo) throws ParseException {
        if ("".equals(transferEndTo)) {
            this.transferEndTo = null;
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat1);
            Date d = formatter.parse(transferEndTo);
            setTransferEndTo(d);
        }
    }

    public void setTransferEndTo(final Date to) {
        if (to != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_SECOND_BEFORE_MDINIGHT_DATE_FORMAT);
            String d = formatter.format(to);
            try {
                formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
                transferEndTo = formatter.parse(d);
            } catch (ParseException e) {
                LOGGER.error("", e);
            }
            formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            transferEndToIso = formatter.format(to);
        }
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getJadeId() {
        return jadeId;
    }

    public void setJadeId(Long jadeId) {
        this.jadeId = jadeId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getPpid() {
        return ppid;
    }

    public void setPpid(Integer ppid) {
        this.ppid = ppid;
    }

    public String getTargetHost() {
        return targetHost;
    }

    public void setTargetHost(String targetHost) {
        this.targetHost = targetHost;
    }

    public String getTargetHostIp() {
        return targetHostIp;
    }

    public void setTargetHostIp(String targetHostIp) {
        this.targetHostIp = targetHostIp;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }

    public String getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    public String getTargetFilename() {
        return targetFilename;
    }

    public void setTargetFilename(String targetFilename) {
        this.targetFilename = targetFilename;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public void setLastErrorMessage(String lastErrorMessage) {
        this.lastErrorMessage = lastErrorMessage;
    }

    public String getLogFilename() {
        return logFilename;
    }

    public void setLogFilename(String logFilename) {
        this.logFilename = logFilename;
    }

    public String getJumpHost() {
        return jumpHost;
    }

    public void setJumpHost(String jumpHost) {
        this.jumpHost = jumpHost;
    }

    public String getJumpHostIp() {
        return jumpHostIp;
    }

    public void setJumpHostIp(String jumpHostIp) {
        this.jumpHostIp = jumpHostIp;
    }

    public String getJumpUser() {
        return jumpUser;
    }

    public void setJumpUser(String jumpUser) {
        this.jumpUser = jumpUser;
    }

    public String getJumpProtocol() {
        return jumpProtocol;
    }

    public void setJumpProtocol(String jumpProtocol) {
        this.jumpProtocol = jumpProtocol;
    }

    public Integer getJumpPort() {
        return jumpPort;
    }

    public void setJumpPort(Integer jumpPort) {
        this.jumpPort = jumpPort;
    }

    public JadeFilesDBItem getJadeFilesDBItem() {
        return jadeFilesDBItem;
    }

    public void setJadeFilesDBItem(JadeFilesDBItem jadeFilesDBItem) {
        this.jadeFilesDBItem = jadeFilesDBItem;
    }

    public String getMandator() {
        return mandator;
    }

    public void setMandator(String mandator) {
        this.mandator = mandator;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getSourceHost() {
        return sourceHost;
    }

    public void setSourceHost(String sourceHost) {
        this.sourceHost = sourceHost;
    }

}
