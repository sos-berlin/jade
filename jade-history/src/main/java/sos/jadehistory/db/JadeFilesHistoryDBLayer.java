package sos.jadehistory.db;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import sos.jadehistory.JadeFilesHistoryFilter;

import com.sos.hibernate.classes.DbItem;
import com.sos.hibernate.layer.SOSHibernateIntervalDBLayer;

public class JadeFilesHistoryDBLayer extends SOSHibernateIntervalDBLayer implements Serializable {

    private static final long serialVersionUID = 1L;
    protected JadeFilesHistoryFilter filter = null;
    private static final Logger LOGGER = Logger.getLogger(JadeFilesHistoryDBLayer.class);
    private static final String AND = " and ";
    private static final String JADE_ID = "jadeId";

    public JadeFilesHistoryDBLayer(String configurationFileName) {
        super();
        this.setConfigurationFileName(configurationFileName);
        this.initConnection(this.getConfigurationFileName());
        this.resetFilter();
    }

    public JadeFilesHistoryDBItem get(String guid) {
        if (guid == null || "".equals(guid)) {
            return null;
        }
        try {
            connection.connect();
            connection.beginTransaction();
            return (JadeFilesHistoryDBItem) ((Session) this.connection.getCurrentSession()).get(JadeFilesHistoryDBItem.class, guid);
        } catch (Exception e) {
            return null;
        }
    }

    public void resetFilter() {
        filter = new JadeFilesHistoryFilter();
        this.filter = new JadeFilesHistoryFilter();
        this.filter.setDateFormat("yyyy-MM-dd HH:mm:ss");
        this.filter.setOrderCriteria("startTime");
        this.filter.setSortMode("desc");
    }

    protected String getWhere() {
        String where = "";
        String and = "";
        if (filter.getCreatedFrom() != null) {
            where += and + " created >= :createdFrom";
            and = AND;
        }
        if (filter.getCreatedTo() != null) {
            where += and + " created <= :createdTo ";
            and = AND;
        }
        if (filter.getModifiedFrom() != null) {
            where += and + " modified >= :modifiedFrom";
            and = AND;
        }
        if (filter.getModifiedTo() != null) {
            where += and + " modified <= :modifiedTo ";
            and = AND;
        }
        if (filter.getTransferStartFrom() != null) {
            where += and + " transferStart >= :transferStartFrom";
            and = AND;
        }
        if (filter.getTransferStartTo() != null) {
            where += and + " transferStart <= :transferStartTo";
            and = AND;
        }
        if (filter.getTransferEndFrom() != null) {
            where += and + " transferEnd >= :transferEndFrom";
            and = AND;
        }
        if (filter.getTransferEndTo() != null) {
            where += and + " transferEnd <= :transferEndTo";
            and = AND;
        }
        if (filter.getCreatedBy() != null && !filter.getCreatedBy().equals("")) {
            where += and + " createdBy=:createdBy";
            and = AND;
        }
        if (filter.getModifiedBy() != null && !filter.getModifiedBy().equals("")) {
            where += and + " modifiedBy=:modifiedBy";
            and = AND;
        }
        if (filter.getGuid() != null && !"".equals(filter.getGuid())) {
            where += and + " guid=:guid";
            and = AND;
        }
        if (filter.getPid() != null && !"".equals(filter.getPid())) {
            where += and + " pid=:pid";
            and = AND;
        }
        if (filter.getPpid() != null && !"".equals(filter.getPpid())) {
            where += and + " ppid=:ppid";
            and = AND;
        }
        if (filter.getLastErrorMessage() != null && !"".equals(filter.getLastErrorMessage())) {
            where += and + " lastErrorMessage=:lastErrorMessage";
            and = AND;
        }
        if (filter.getStatus() != null && !"".equals(filter.getStatus())) {
            where += and + " status=:status";
            and = AND;
        }
        if (filter.getLogFilename() != null && !"".equals(filter.getLogFilename())) {
            where += and + " logFilename=:logFilename";
            and = AND;
        }
        if (filter.getJumpHost() != null && !"".equals(filter.getJumpHost())) {
            where += and + " jumpHost=:jumpHost";
            and = AND;
        }
        if (filter.getJumpHostIp() != null && !"".equals(filter.getJumpHostIp())) {
            where += and + " jumpHostIp=:jumpHostIp";
            and = AND;
        }
        if (filter.getJumpProtocol() != null && !"".equals(filter.getJumpProtocol())) {
            where += and + " jumpProtocol=:jumpProtocol";
            and = AND;
        }
        if (filter.getJumpPort() != null && !"".equals(filter.getJumpPort())) {
            where += and + " jumpPort=:jumpPort";
            and = AND;
        }
        if (filter.getJumpUser() != null && !"".equals(filter.getJumpUser())) {
            where += and + " jumpUser=:jumpUser";
            and = AND;
        }
        if (filter.getOperation() != null && !"".equals(filter.getOperation())) {
            where += and + " operation=:operation";
            and = AND;
        }
        if (filter.getPort() != null && !"".equals(filter.getPort())) {
            where += and + " port=:port";
            and = AND;
        }
        if (filter.getProtocol() != null && !"".equals(filter.getProtocol())) {
            where += and + " protocol=:protocol";
            and = AND;
        }
        if (filter.getJadeId() != null && !"".equals(filter.getJadeId())) {
            where += and + " jadeId=:jadeId";
            and = AND;
        }
        if (filter.getTargetDir() != null && !"".equals(filter.getTargetDir())) {
            where += and + " targetDir=:targetDir";
            and = AND;
        }
        if (filter.getTargetFilename() != null && !"".equals(filter.getTargetFilename())) {
            where += and + " targetFilename like :targetFilename";
            and = AND;
        }
        if (filter.getTargetHost() != null && !"".equals(filter.getTargetHost())) {
            where += and + " targetHost=:targetHost";
            and = AND;
        }
        if (filter.getTargetHostIp() != null && !"".equals(filter.getTargetHostIp())) {
            where += and + " targetHostIp=:targetHostIp";
            and = AND;
        }
        if (filter.getTargetUser() != null && !"".equals(filter.getTargetUser())) {
            where += and + " targetUser=:targetUser";
            and = AND;
        }
        if (filter.getMandator() != null && !"".equals(filter.getMandator())) {
            where += and + " history.jadeFilesDBItem.mandator=:mandator";
            and = AND;
        }
        if (filter.getFileSize() != null && !"".equals(filter.getFileSize())) {
            where += and + " history.jadeFilesDBItem.fileSize=:fileSize";
            and = AND;
        }
        if (filter.getSourceFile() != null && !"".equals(filter.getSourceFile())) {
            where += and + " history.jadeFilesDBItem.sourceFilename like :sourceFilename";
            and = AND;
        }
        if (filter.getSourceHost() != null && !"".equals(filter.getSourceHost())) {
            where += and + " history.jadeFilesDBItem.sourceHost=:sourceHost";
            and = AND;
        }
        if (!"".equals(where.trim())) {
            where = "where " + where;
        }
        return where;
    }

    protected String getWhereFromTo() {
        String where = "";
        String and = "";
        if (filter.getCreatedFrom() != null) {
            where += and + " created >= :createdFrom";
            and = AND;
        }
        if (filter.getCreatedTo() != null) {
            where += and + " created <= :createdTo ";
            and = AND;
        }
        if (!"".equals(where.trim())) {
            where = "where " + where;
        }
        return where;
    }

    private void setWhere(Query query) {
        if (filter.getCreatedFrom() != null && !"".equals(filter.getCreatedFrom())) {
            query.setTimestamp("createdFrom", filter.getCreatedFrom());
        }
        if (filter.getCreatedTo() != null && !"".equals(filter.getCreatedTo())) {
            query.setTimestamp("createdTo", filter.getCreatedTo());
        }
        if (filter.getModifiedFrom() != null && !"".equals(filter.getModifiedFrom())) {
            query.setTimestamp("modifiedFrom", filter.getModifiedFrom());
        }
        if (filter.getModifiedTo() != null && !"".equals(filter.getModifiedTo())) {
            query.setTimestamp("modifiedTo", filter.getModifiedTo());
        }
        if (filter.getCreatedBy() != null && !"".equals(filter.getCreatedBy())) {
            query.setText("createdBy", filter.getCreatedBy());
        }
        if (filter.getModifiedBy() != null && !"".equals(filter.getModifiedBy())) {
            query.setText("modifiedBy", filter.getModifiedBy());
        }
        if (filter.getGuid() != null && !"".equals(filter.getGuid())) {
            query.setText("guid", filter.getGuid());
        }
        if (filter.getJadeId() != null && !"".equals(filter.getJadeId())) {
            query.setLong(JADE_ID, filter.getJadeId());
        }
        if (filter.getOperation() != null && !"".equals(filter.getOperation())) {
            query.setText("operation", filter.getOperation());
        }
        if (filter.getTransferStartFrom() != null && !"".equals(filter.getTransferStartFrom())) {
            query.setTimestamp("transferStartFrom", filter.getTransferStartFrom());
        }
        if (filter.getTransferStartTo() != null && !"".equals(filter.getTransferStartTo())) {
            query.setTimestamp("transferStartTo", filter.getTransferStartTo());
        }
        if (filter.getTransferEndFrom() != null && !"".equals(filter.getTransferEndFrom())) {
            query.setTimestamp("transferEndFrom", filter.getTransferEndFrom());
        }
        if (filter.getTransferEndTo() != null && !"".equals(filter.getTransferEndTo())) {
            query.setTimestamp("transferEndTo", filter.getTransferEndTo());
        }
        if (filter.getPid() != null && !"".equals(filter.getPid())) {
            query.setInteger("pid", filter.getPid());
        }
        if (filter.getPpid() != null && !"".equals(filter.getPpid())) {
            query.setInteger("ppid", filter.getPpid());
        }
        if (filter.getTargetHost() != null && !"".equals(filter.getTargetHost())) {
            query.setText("targetHost", filter.getTargetHost());
        }
        if (filter.getTargetHostIp() != null && !"".equals(filter.getTargetHostIp())) {
            query.setText("targetHostIp", filter.getTargetHostIp());
        }
        if (filter.getTargetUser() != null && !"".equals(filter.getTargetUser())) {
            query.setText("targetUser", filter.getTargetUser());
        }
        if (filter.getTargetDir() != null && !"".equals(filter.getTargetDir())) {
            query.setText("targetDir", filter.getTargetDir());
        }
        if (filter.getTargetFilename() != null && !"".equals(filter.getTargetFilename())) {
            query.setText("targetFilename", filter.getTargetFilename());
        }
        if (filter.getProtocol() != null && !"".equals(filter.getProtocol())) {
            query.setText("protocol", filter.getProtocol());
        }
        if (filter.getPort() != null && !"".equals(filter.getPort())) {
            query.setInteger("port", filter.getPort());
        }
        if (filter.getStatus() != null && !"".equals(filter.getStatus())) {
            query.setText("status", filter.getStatus());
        }
        if (filter.getLastErrorMessage() != null && !"".equals(filter.getLastErrorMessage())) {
            query.setText("lastErrorMessage", filter.getLastErrorMessage());
        }
        if (filter.getLogFilename() != null && !"".equals(filter.getLogFilename())) {
            query.setText("logFilename", filter.getLogFilename());
        }
        if (filter.getJumpHost() != null && !"".equals(filter.getJumpHost())) {
            query.setText("jumpHost", filter.getJumpHost());
        }
        if (filter.getJumpHostIp() != null && !"".equals(filter.getJumpHostIp())) {
            query.setText("jumpHostIp", filter.getJumpHostIp());
        }
        if (filter.getJumpUser() != null && !"".equals(filter.getJumpUser())) {
            query.setText("jumpUser", filter.getJumpUser());
        }
        if (filter.getJumpProtocol() != null && !"".equals(filter.getJumpProtocol())) {
            query.setText("jumpProtocol", filter.getJumpProtocol());
        }
        if (filter.getJumpPort() != null && !"".equals(filter.getJumpPort())) {
            query.setInteger("jumpPort", filter.getJumpPort());
        }
        if (filter.getJadeFilesDBItem() != null && filter.getJadeFilesDBItem().getId() != null) {
            query.setLong(JADE_ID, filter.getJadeFilesDBItem().getId());
        }
        if (filter.getMandator() != null && !"".equals(filter.getMandator())) {
            query.setText("mandator", filter.getMandator());
        }
        if (filter.getFileSize() != null && !"".equals(filter.getFileSize())) {
            query.setInteger("fileSize", filter.getFileSize());
        }
        if (filter.getSourceFile() != null && !"".equals(filter.getSourceFile())) {
            query.setText("sourceFilename", filter.getSourceFile());
        }
        if (filter.getSourceHost() != null && !"".equals(filter.getSourceHost())) {
            query.setText("sourceHost", filter.getSourceHost());
        }
    }

    public List<DbItem> getFilesHistoryFromTo(Date from, Date to) {
        filter.setCreatedFrom(from);
        filter.setCreatedTo(to);
        List<DbItem> resultset = null;
        if (connection == null) {
            initConnection(getConfigurationFileName());
        }
        try {
            connection.connect();
            connection.beginTransaction();
            Query query = connection.createQuery("  from JadeFilesHistoryDBItem " + getWhere());
            if (filter.getCreatedFrom() != null) {
                query.setTimestamp("createdFrom", filter.getCreatedFrom());
            }
            if (filter.getCreatedTo() != null) {
                query.setTimestamp("createdTo", filter.getCreatedTo());
            }
            resultset = query.list();
        } catch (Exception e) {
            LOGGER.error("Error occured receiving data for the given interval: ", e);
        }
        return resultset;
    }

    public List<JadeFilesHistoryDBItem> getFilesHistoryById(Long jadeId) throws ParseException {
        List<JadeFilesHistoryDBItem> resultset = null;
        if (connection == null) {
            initConnection(getConfigurationFileName());
        }
        try {
            connection.connect();
            connection.beginTransaction();
            Query query = connection.createQuery("  from JadeFilesHistoryDBItem where jadeId=:jadeId");
            query.setLong(JADE_ID, jadeId);
            resultset = query.list();
        } catch (Exception e) {
            LOGGER.error("Error occurred receiving DBItem: ", e);
        }
        return resultset;
    }

    public JadeFilesDBItem getJadeFileItemById(Long jadeId) throws ParseException {
        List<JadeFilesDBItem> resultset = null;
        if (connection == null) {
            initConnection(getConfigurationFileName());
        }
        try {
            connection.connect();
            connection.beginTransaction();
            Query query = connection.createQuery("  from JadeFilesDBItem where id=:jadeId");
            query.setLong(JADE_ID, jadeId);
            resultset = query.list();
        } catch (Exception e) {
            LOGGER.error("Error occurred receiving DBItems: ", e);
        }
        // id is unique, therefore only one item has to be returned
        return resultset != null ? resultset.get(0) : null;
    }

    public List<JadeFilesHistoryDBItem> getHistoryFiles() throws ParseException {
        List<JadeFilesHistoryDBItem> resultset = null;
        if (connection == null) {
            initConnection(getConfigurationFileName());
        }
        try {
            connection.connect();
            connection.beginTransaction();
            Query query = connection.createQuery("  from JadeFilesHistoryDBItem history " + getWhere());
            setWhere(query);
            resultset = query.list();
        } catch (Exception e) {
            LOGGER.error("Error occurred receiving HistoryDBItems", e);
        }
        return resultset;
    }

    public List<JadeFilesHistoryDBItem> getHistoryFilesOrderedByTransferEnd() throws ParseException {
        List<JadeFilesHistoryDBItem> resultset = null;
        if (connection == null) {
            initConnection(getConfigurationFileName());
        }
        try {
            connection.connect();
            connection.beginTransaction();
            Query query = connection.createQuery("  from JadeFilesHistoryDBItem history " + getWhere() + " order by transferEnd desc");
            setWhere(query);
            resultset = query.list();
        } catch (Exception e) {
            LOGGER.error("Error occurred receiving ordered HistoryDBItems", e);
        }
        return resultset;
    }

    public void setCreatedFrom(Date createdFrom) {
        filter.setCreatedFrom(createdFrom);
    }

    public void setCreatedTo(Date createdTo) {
        filter.setCreatedTo(createdTo);
    }

    public void setDateFormat(String dateFormat) {
        filter.setDateFormat(dateFormat);
    }

    public void setCreatedFrom(String createdFrom) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(filter.getDateFormat());
        setCreatedFrom(formatter.parse(createdFrom));
    }

    public void setCreatedTo(String createdTo) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(filter.getDateFormat());
        setCreatedTo(formatter.parse(createdTo));
    }

    @Override
    public JadeFilesHistoryFilter getFilter() {
        return filter;
    }

    public void setFilter(JadeFilesHistoryFilter filter) {
        this.filter = filter;
    }

    @Override
    public void onAfterDeleting(DbItem h) {
        // nothing to do
    }

    @Override
    public List<DbItem> getListOfItemsToDelete() {
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
        return getFilesHistoryFromTo(filter.getCreatedFrom(), filter.getCreatedTo());

    }

    @Override
    public long deleteInterval() {
        return 0;
    }

}
