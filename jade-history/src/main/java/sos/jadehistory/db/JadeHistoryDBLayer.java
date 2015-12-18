package sos.jadehistory.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;

import sos.jadehistory.JadeFilesFilter;
import sos.jadehistory.JadeFilesHistoryFilter;

import com.sos.hibernate.classes.DbItem;
import com.sos.hibernate.classes.SOSHibernateConnection;

public class JadeHistoryDBLayer {

    private static final Logger LOGGER = Logger.getLogger(JadeHistoryDBLayer.class);
    private static final String AND = " and ";
    private static final String CREATED_FROM = "createdFrom";
    private static final String CREATED_TO = "createdTo";
    private static final String CREATED_LE_TO = " created <= :createdTo ";
    private static final String CREATED_ME_FROM = " created >= :createdFrom";
    private static final String JADE_ID = "jadeId";
    private static final String WHERE = "where ";
    private SOSHibernateConnection connection;
    protected JadeFilesFilter filesFilter;
    protected JadeFilesHistoryFilter historyFilesFilter;

    public JadeHistoryDBLayer(SOSHibernateConnection connection) {
        this.connection = connection;
        this.resetFilter();
    }

    public JadeFilesDBItem get(Long id) {
        if (id == null) {
            return null;
        }
        try {
            return (JadeFilesDBItem) ((Session) connection.getCurrentSession()).get(JadeFilesDBItem.class, id);
        } catch (Exception e) {
            LOGGER.error("", e);
            return null;
        }
    }

    public void resetFilter() {
        this.filesFilter = new JadeFilesFilter();
        this.filesFilter.setDateFormat("yyyy-MM-dd HH:mm:ss");
        this.filesFilter.setOrderCriteria("startTime");
        this.filesFilter.setSortMode("desc");

        this.historyFilesFilter = new JadeFilesHistoryFilter();
        this.historyFilesFilter.setDateFormat("yyyy-MM-dd HH:mm:ss");
        this.historyFilesFilter.setOrderCriteria("startTime");
        this.historyFilesFilter.setSortMode("desc");
    }

    protected String getFilesWhere() {
        String where = "";
        String and = "";
        if (filesFilter.getCreatedFrom() != null) {
            where += and + CREATED_ME_FROM;
            and = AND;
        }
        if (filesFilter.getCreatedTo() != null) {
            where += and + CREATED_LE_TO;
            and = AND;
        }
        if (filesFilter.getModifiedFrom() != null) {
            where += and + " modified >= :modifiedFrom";
            and = AND;
        }
        if (filesFilter.getModifiedTo() != null) {
            where += and + " modified <= :modifiedTo ";
            and = AND;
        }
        if (filesFilter.getModificationDateFrom() != null) {
            where += and + " modificationDate >= :modificationDateFrom";
            and = AND;
        }
        if (filesFilter.getModificationDateTo() != null) {
            where += and + " modificationDate <= :modificationDateTo ";
            and = AND;
        }
        if (filesFilter.getMandator() != null && !"".equals(filesFilter.getMandator())) {
            where += and + " mandator=:mandator";
            and = AND;
        }
        if (filesFilter.getCreatedBy() != null && !"".equals(filesFilter.getCreatedBy())) {
            where += and + " createdBy=:createdBy";
            and = AND;
        }
        if (filesFilter.getModifiedBy() != null && !"".equals(filesFilter.getModifiedBy())) {
            where += and + " modifiedBy=:modifiedBy";
            and = AND;
        }
        if (filesFilter.getSourceDir() != null && !"".equals(filesFilter.getSourceDir())) {
            where += and + " sourceDir=:sourceDir";
            and = AND;
        }
        if (filesFilter.getSourceFilename() != null && !"".equals(filesFilter.getSourceFilename())) {
            where += and + " sourceFilename like :sourceFilename";
            and = AND;
        }
        if (filesFilter.getSourceHost() != null && !"".equals(filesFilter.getSourceHost())) {
            where += and + " sourceHost=:sourceHost";
            and = AND;
        }
        if (filesFilter.getSourceHostIp() != null && !"".equals(filesFilter.getSourceHostIp())) {
            where += and + " sourceHostIp=:sourceHostIp";
            and = AND;
        }
        if (filesFilter.getSourceUser() != null && !"".equals(filesFilter.getSourceUser())) {
            where += and + " sourceUser=:sourceUser";
            and = AND;
        }
        if (filesFilter.getFileSize() != null) {
            where += and + " fileSize=:fileSize";
            and = AND;
        }
        if (!"".equals(where.trim())) {
            where = WHERE + where;
        }
        return where;
    }

    protected String getFilesWhereFromTo() {
        String where = "";
        String and = "";
        if (filesFilter.getCreatedFrom() != null) {
            where += and + CREATED_ME_FROM;
            and = AND;
        }
        if (filesFilter.getCreatedTo() != null) {
            where += and + CREATED_LE_TO;
            and = AND;
        }
        if (!"".equals(where.trim())) {
            where = WHERE + where;
        }
        return where;
    }

    private void setFilesWhere(Query query) {
        if (filesFilter.getCreatedFrom() != null && !"".equals(filesFilter.getCreatedFrom())) {
            query.setTimestamp(CREATED_FROM, filesFilter.getCreatedFrom());
        }
        if (filesFilter.getCreatedTo() != null && !"".equals(filesFilter.getCreatedTo())) {
            query.setTimestamp(CREATED_TO, filesFilter.getCreatedTo());
        }
        if (filesFilter.getModifiedFrom() != null && !"".equals(filesFilter.getModifiedFrom())) {
            query.setTimestamp("modifiedFrom", filesFilter.getModifiedFrom());
        }
        if (filesFilter.getModifiedTo() != null && !"".equals(filesFilter.getModifiedTo())) {
            query.setTimestamp("modifiedTo", filesFilter.getModifiedTo());
        }
        if (filesFilter.getModificationDateFrom() != null && !"".equals(filesFilter.getModificationDateFrom())) {
            query.setTimestamp("modificationDateFrom", filesFilter.getModificationDateFrom());
        }
        if (filesFilter.getModificationDateTo() != null && !"".equals(filesFilter.getModificationDateTo())) {
            query.setTimestamp("modificationDateTo", filesFilter.getModificationDateTo());
        }
        if (filesFilter.getMandator() != null && !"".equals(filesFilter.getMandator())) {
            query.setText("mandator", filesFilter.getMandator());
        }
        if (filesFilter.getCreatedBy() != null && !"".equals(filesFilter.getCreatedBy())) {
            query.setText("createdBy", filesFilter.getCreatedBy());
        }
        if (filesFilter.getModifiedBy() != null && !"".equals(filesFilter.getModifiedBy())) {
            query.setText("modifiedBy", filesFilter.getModifiedBy());
        }
        if (filesFilter.getSourceDir() != null && !"".equals(filesFilter.getSourceDir())) {
            query.setText("sourceDir", filesFilter.getSourceDir());
        }
        if (filesFilter.getSourceFilename() != null && !"".equals(filesFilter.getSourceFilename())) {
            query.setText("sourceFilename", filesFilter.getSourceFilename());
        }
        if (filesFilter.getSourceHost() != null && !"".equals(filesFilter.getSourceHost())) {
            query.setText("sourceHost", filesFilter.getSourceHost());
        }
        if (filesFilter.getSourceHostIp() != null && !"".equals(filesFilter.getSourceHostIp())) {
            query.setText("sourceHostIp", filesFilter.getSourceHostIp());
        }
        if (filesFilter.getSourceUser() != null && !"".equals(filesFilter.getSourceUser())) {
            query.setText("sourceUser", filesFilter.getSourceUser());
        }
        if (filesFilter.getFileSize() != null) {
            query.setInteger("fileSize", filesFilter.getFileSize());
        }
    }

    public int delete() throws Exception {
        connection.beginTransaction();
        String q = "delete from JadeFilesHistoryDBItem e where e.jadeFilesDBItem.id IN (select id from JadeFilesDBItem " + getFilesWhere() + ")";
        Query query = connection.createQuery(q);
        setFilesWhere(query);
        int row = query.executeUpdate();
        String hql = "delete from JadeFilesDBItem " + getFilesWhere();
        query = connection.createQuery(hql);
        setFilesWhere(query);
        row = query.executeUpdate();
        return row;
    }

    public List<DbItem> getFilesFromTo(Date from, Date to) throws Exception {
        filesFilter.setCreatedFrom(from);
        filesFilter.setCreatedTo(to);
        connection.beginTransaction();
        Query query = connection.createQuery("  from JadeFilesDBItem " + getFilesWhere());
        if (filesFilter.getCreatedFrom() != null) {
            query.setTimestamp(CREATED_FROM, filesFilter.getCreatedFrom());
        }
        if (filesFilter.getCreatedTo() != null) {
            query.setTimestamp(CREATED_TO, filesFilter.getCreatedTo());
        }
        return query.list();
    }

    public List<JadeFilesHistoryDBItem> getFilesHistoryById(Long jadeId) throws Exception {
        connection.beginTransaction();
        Query query = connection.createQuery("  from JadeFilesHistoryDBItem where jadeId=:jadeId");
        query.setLong(JADE_ID, jadeId);
        List<JadeFilesHistoryDBItem> resultset = query.list();
        connection.commit();
        return resultset;
    }

    public List<JadeFilesDBItem> getFiles() throws Exception {
        connection.beginTransaction();
        Query query = connection.createQuery("  from JadeFilesDBItem " + getFilesWhere());
        setFilesWhere(query);
        List<JadeFilesDBItem> resultset = query.list();
        connection.commit();
        return resultset;
    }

    public void setCreatedFrom(Date createdFrom) {
        filesFilter.setCreatedFrom(createdFrom);
    }

    public void setCreatedTo(Date createdTo) {
        filesFilter.setCreatedTo(createdTo);
    }

    public void setDateFormat(String dateFormat) {
        filesFilter.setDateFormat(dateFormat);
    }

    public void setCreatedFrom(String createdFrom) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(filesFilter.getDateFormat());
        setCreatedFrom(formatter.parse(createdFrom));
    }

    public void setCreatedTo(String createdTo) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(filesFilter.getDateFormat());
        setCreatedTo(formatter.parse(createdTo));
    }

    public JadeFilesFilter getFilesFilter() {
        return filesFilter;
    }

    public void setFilesFilter(JadeFilesFilter filter) {
        this.filesFilter = filter;
    }

    public JadeFilesHistoryFilter getFilesHistoryFilter() {
        return historyFilesFilter;
    }

    public void setFilesHistoryFilter(JadeFilesHistoryFilter filter) {
        this.historyFilesFilter = filter;
    }

    public List<DbItem> getListOfItemsToDelete() throws Exception {
        return getFilesFromTo(filesFilter.getCreatedFrom(), filesFilter.getCreatedTo());
    }

    public long deleteInterval() throws Exception {
        connection.beginTransaction();
        String q = "delete from JadeFilesHistoryDBItem e where e.jadeFilesDBItem.id IN (select id from JadeFilesDBItem " + getFilesWhereFromTo() + ")";
        Query query = connection.createQuery(q);
        if (filesFilter.getCreatedFrom() != null) {
            query.setTimestamp(CREATED_FROM, filesFilter.getCreatedFrom());
        }
        if (filesFilter.getCreatedTo() != null) {
            query.setTimestamp(CREATED_TO, filesFilter.getCreatedTo());
        }
        int row = query.executeUpdate();
        String hql = "delete from JadeFilesDBItem " + getFilesWhereFromTo();
        query = connection.createQuery(hql);
        if (filesFilter.getCreatedFrom() != null) {
            query.setTimestamp(CREATED_FROM, filesFilter.getCreatedFrom());
        }
        if (filesFilter.getCreatedTo() != null) {
            query.setTimestamp(CREATED_TO, filesFilter.getCreatedTo());
        }
        row = query.executeUpdate();
        return row;
    }

    public JadeFilesHistoryDBItem get(String guid) throws Exception {
        if (guid == null || "".equals(guid)) {
            return null;
        }
        try {
            return (JadeFilesHistoryDBItem) ((Session) connection.getCurrentSession()).get(JadeFilesHistoryDBItem.class, guid);
        } catch (ObjectNotFoundException e) {
            return null;
        }
    }

    protected String getHistoryWhere() {
        String where = "";
        String and = "";
        if (historyFilesFilter.getCreatedFrom() != null) {
            where += and + CREATED_ME_FROM;
            and = AND;
        }
        if (historyFilesFilter.getCreatedTo() != null) {
            where += and + CREATED_LE_TO;
            and = AND;
        }
        if (historyFilesFilter.getModifiedFrom() != null) {
            where += and + " modified >= :modifiedFrom";
            and = AND;
        }
        if (historyFilesFilter.getModifiedTo() != null) {
            where += and + " modified <= :modifiedTo ";
            and = AND;
        }
        if (historyFilesFilter.getTransferStartFrom() != null) {
            where += and + " transferStart >= :transferStartFrom";
            and = AND;
        }
        if (historyFilesFilter.getTransferStartTo() != null) {
            where += and + " transferStart <= :transferStartTo";
            and = AND;
        }
        if (historyFilesFilter.getTransferEndFrom() != null) {
            where += and + " transferEnd >= :transferEndFrom";
            and = AND;
        }
        if (historyFilesFilter.getTransferEndTo() != null) {
            where += and + " transferEnd <= :transferEndTo";
            and = AND;
        }
        if (historyFilesFilter.getCreatedBy() != null && !"".equals(historyFilesFilter.getCreatedBy())) {
            where += and + " createdBy=:createdBy";
            and = AND;
        }
        if (historyFilesFilter.getModifiedBy() != null && !"".equals(historyFilesFilter.getModifiedBy())) {
            where += and + " modifiedBy=:modifiedBy";
            and = AND;
        }
        if (historyFilesFilter.getGuid() != null && !"".equals(historyFilesFilter.getGuid())) {
            where += and + " guid=:guid";
            and = AND;
        }
        if (historyFilesFilter.getPid() != null && !"".equals(historyFilesFilter.getPid())) {
            where += and + " pid=:pid";
            and = AND;
        }
        if (historyFilesFilter.getPpid() != null && !"".equals(historyFilesFilter.getPpid())) {
            where += and + " ppid=:ppid";
            and = AND;
        }
        if (historyFilesFilter.getLastErrorMessage() != null && !"".equals(historyFilesFilter.getLastErrorMessage())) {
            where += and + " lastErrorMessage=:lastErrorMessage";
            and = AND;
        }
        if (historyFilesFilter.getStatus() != null && !"".equals(historyFilesFilter.getStatus())) {
            where += and + " status=:status";
            and = AND;
        }
        if (historyFilesFilter.getLogFilename() != null && !"".equals(historyFilesFilter.getLogFilename())) {
            where += and + " logFilename=:logFilename";
            and = AND;
        }
        if (historyFilesFilter.getJumpHost() != null && !"".equals(historyFilesFilter.getJumpHost())) {
            where += and + " jumpHost=:jumpHost";
            and = AND;
        }
        if (historyFilesFilter.getJumpHostIp() != null && !"".equals(historyFilesFilter.getJumpHostIp())) {
            where += and + " jumpHostIp=:jumpHostIp";
            and = AND;
        }
        if (historyFilesFilter.getJumpProtocol() != null && !"".equals(historyFilesFilter.getJumpProtocol())) {
            where += and + " jumpProtocol=:jumpProtocol";
            and = AND;
        }
        if (historyFilesFilter.getJumpPort() != null && !"".equals(historyFilesFilter.getJumpPort())) {
            where += and + " jumpPort=:jumpPort";
            and = AND;
        }
        if (historyFilesFilter.getJumpUser() != null && !"".equals(historyFilesFilter.getJumpUser())) {
            where += and + " jumpUser=:jumpUser";
            and = AND;
        }
        if (historyFilesFilter.getOperation() != null && !"".equals(historyFilesFilter.getOperation())) {
            where += and + " operation=:operation";
            and = AND;
        }
        if (historyFilesFilter.getPort() != null && !"".equals(historyFilesFilter.getPort())) {
            where += and + " port=:port";
            and = AND;
        }
        if (historyFilesFilter.getProtocol() != null && !"".equals(historyFilesFilter.getProtocol())) {
            where += and + " protocol=:protocol";
            and = AND;
        }
        if (historyFilesFilter.getJadeId() != null && !"".equals(historyFilesFilter.getJadeId())) {
            where += and + " jadeId=:jadeId";
            and = AND;
        }
        if (historyFilesFilter.getTargetDir() != null && !"".equals(historyFilesFilter.getTargetDir())) {
            where += and + " targetDir=:targetDir";
            and = AND;
        }
        if (historyFilesFilter.getTargetFilename() != null && !"".equals(historyFilesFilter.getTargetFilename())) {
            where += and + " targetFilename like :targetFilename";
            and = AND;
        }
        if (historyFilesFilter.getTargetHost() != null && !"".equals(historyFilesFilter.getTargetHost())) {
            where += and + " targetHost=:targetHost";
            and = AND;
        }
        if (historyFilesFilter.getTargetHostIp() != null && !"".equals(historyFilesFilter.getTargetHostIp())) {
            where += and + " targetHostIp=:targetHostIp";
            and = AND;
        }
        if (historyFilesFilter.getTargetUser() != null && !"".equals(historyFilesFilter.getTargetUser())) {
            where += and + " targetUser=:targetUser";
            and = AND;
        }
        if (historyFilesFilter.getMandator() != null && !"".equals(historyFilesFilter.getMandator())) {
            where += and + " history.jadeFilesDBItem.mandator=:mandator";
            and = AND;
        }
        if (historyFilesFilter.getFileSize() != null && !"".equals(historyFilesFilter.getFileSize())) {
            where += and + " history.jadeFilesDBItem.fileSize=:fileSize";
            and = AND;
        }
        if (historyFilesFilter.getSourceFile() != null && !"".equals(historyFilesFilter.getSourceFile())) {
            where += and + " history.jadeFilesDBItem.sourceFilename like :sourceFilename";
            and = AND;
        }
        if (historyFilesFilter.getSourceHost() != null && !"".equals(historyFilesFilter.getSourceHost())) {
            where += and + " history.jadeFilesDBItem.sourceHost=:sourceHost";
            and = AND;
        }
        if (!"".equals(where.trim())) {
            where = WHERE + where;
        }
        return where;
    }

    protected String getHistoryWhereFromTo() {
        String where = "";
        String and = "";
        if (historyFilesFilter.getCreatedFrom() != null) {
            where += and + CREATED_ME_FROM;
            and = AND;
        }
        if (historyFilesFilter.getCreatedTo() != null) {
            where += and + CREATED_LE_TO;
            and = AND;
        }
        if (!"".equals(where.trim())) {
            where = WHERE + where;
        }
        return where;
    }

    private void setHistoryWhere(Query query) {
        if (historyFilesFilter.getCreatedFrom() != null && !"".equals(historyFilesFilter.getCreatedFrom())) {
            query.setTimestamp(CREATED_FROM, historyFilesFilter.getCreatedFrom());
        }
        if (historyFilesFilter.getCreatedTo() != null && !"".equals(historyFilesFilter.getCreatedTo())) {
            query.setTimestamp(CREATED_TO, historyFilesFilter.getCreatedTo());
        }
        if (historyFilesFilter.getModifiedFrom() != null && !"".equals(historyFilesFilter.getModifiedFrom())) {
            query.setTimestamp("modifiedFrom", historyFilesFilter.getModifiedFrom());
        }
        if (historyFilesFilter.getModifiedTo() != null && !"".equals(historyFilesFilter.getModifiedTo())) {
            query.setTimestamp("modifiedTo", historyFilesFilter.getModifiedTo());
        }
        if (historyFilesFilter.getCreatedBy() != null && !"".equals(historyFilesFilter.getCreatedBy())) {
            query.setText("createdBy", historyFilesFilter.getCreatedBy());
        }
        if (historyFilesFilter.getModifiedBy() != null && !"".equals(historyFilesFilter.getModifiedBy())) {
            query.setText("modifiedBy", historyFilesFilter.getModifiedBy());
        }
        if (historyFilesFilter.getGuid() != null && !"".equals(historyFilesFilter.getGuid())) {
            query.setText("guid", historyFilesFilter.getGuid());
        }
        if (historyFilesFilter.getJadeId() != null && !"".equals(historyFilesFilter.getJadeId())) {
            query.setLong(JADE_ID, historyFilesFilter.getJadeId());
        }
        if (historyFilesFilter.getOperation() != null && !"".equals(historyFilesFilter.getOperation())) {
            query.setText("operation", historyFilesFilter.getOperation());
        }
        if (historyFilesFilter.getTransferStartFrom() != null && !"".equals(historyFilesFilter.getTransferStartFrom())) {
            query.setTimestamp("transferStartFrom", historyFilesFilter.getTransferStartFrom());
        }
        if (historyFilesFilter.getTransferStartTo() != null && !"".equals(historyFilesFilter.getTransferStartTo())) {
            query.setTimestamp("transferStartTo", historyFilesFilter.getTransferStartTo());
        }
        if (historyFilesFilter.getTransferEndFrom() != null && !"".equals(historyFilesFilter.getTransferEndFrom())) {
            query.setTimestamp("transferEndFrom", historyFilesFilter.getTransferEndFrom());
        }
        if (historyFilesFilter.getTransferEndTo() != null && !"".equals(historyFilesFilter.getTransferEndTo())) {
            query.setTimestamp("transferEndTo", historyFilesFilter.getTransferEndTo());
        }
        if (historyFilesFilter.getPid() != null && !"".equals(historyFilesFilter.getPid())) {
            query.setInteger("pid", historyFilesFilter.getPid());
        }
        if (historyFilesFilter.getPpid() != null && !"".equals(historyFilesFilter.getPpid())) {
            query.setInteger("ppid", historyFilesFilter.getPpid());
        }
        if (historyFilesFilter.getTargetHost() != null && !"".equals(historyFilesFilter.getTargetHost())) {
            query.setText("targetHost", historyFilesFilter.getTargetHost());
        }
        if (historyFilesFilter.getTargetHostIp() != null && !"".equals(historyFilesFilter.getTargetHostIp())) {
            query.setText("targetHostIp", historyFilesFilter.getTargetHostIp());
        }
        if (historyFilesFilter.getTargetUser() != null && !"".equals(historyFilesFilter.getTargetUser())) {
            query.setText("targetUser", historyFilesFilter.getTargetUser());
        }
        if (historyFilesFilter.getTargetDir() != null && !"".equals(historyFilesFilter.getTargetDir())) {
            query.setText("targetDir", historyFilesFilter.getTargetDir());
        }
        if (historyFilesFilter.getTargetFilename() != null && !"".equals(historyFilesFilter.getTargetFilename())) {
            query.setText("targetFilename", historyFilesFilter.getTargetFilename());
        }
        if (historyFilesFilter.getProtocol() != null && !"".equals(historyFilesFilter.getProtocol())) {
            query.setText("protocol", historyFilesFilter.getProtocol());
        }
        if (historyFilesFilter.getPort() != null && !"".equals(historyFilesFilter.getPort())) {
            query.setInteger("port", historyFilesFilter.getPort());
        }
        if (historyFilesFilter.getStatus() != null && !"".equals(historyFilesFilter.getStatus())) {
            query.setText("status", historyFilesFilter.getStatus());
        }
        if (historyFilesFilter.getLastErrorMessage() != null && !"".equals(historyFilesFilter.getLastErrorMessage())) {
            query.setText("lastErrorMessage", historyFilesFilter.getLastErrorMessage());
        }
        if (historyFilesFilter.getLogFilename() != null && !"".equals(historyFilesFilter.getLogFilename())) {
            query.setText("logFilename", historyFilesFilter.getLogFilename());
        }
        if (historyFilesFilter.getJumpHost() != null && !"".equals(historyFilesFilter.getJumpHost())) {
            query.setText("jumpHost", historyFilesFilter.getJumpHost());
        }
        if (historyFilesFilter.getJumpHostIp() != null && !"".equals(historyFilesFilter.getJumpHostIp())) {
            query.setText("jumpHostIp", historyFilesFilter.getJumpHostIp());
        }
        if (historyFilesFilter.getJumpUser() != null && !"".equals(historyFilesFilter.getJumpUser())) {
            query.setText("jumpUser", historyFilesFilter.getJumpUser());
        }
        if (historyFilesFilter.getJumpProtocol() != null && !"".equals(historyFilesFilter.getJumpProtocol())) {
            query.setText("jumpProtocol", historyFilesFilter.getJumpProtocol());
        }
        if (historyFilesFilter.getJumpPort() != null && !"".equals(historyFilesFilter.getJumpPort())) {
            query.setInteger("jumpPort", historyFilesFilter.getJumpPort());
        }
        if (historyFilesFilter.getJadeFilesDBItem() != null && historyFilesFilter.getJadeFilesDBItem().getId() != null) {
            query.setLong(JADE_ID, historyFilesFilter.getJadeFilesDBItem().getId());
        }
        if (historyFilesFilter.getMandator() != null && !"".equals(historyFilesFilter.getMandator())) {
            query.setText("mandator", historyFilesFilter.getMandator());
        }
        if (historyFilesFilter.getFileSize() != null && !"".equals(historyFilesFilter.getFileSize())) {
            query.setInteger("fileSize", historyFilesFilter.getFileSize());
        }
        if (historyFilesFilter.getSourceFile() != null && !"".equals(historyFilesFilter.getSourceFile())) {
            query.setText("sourceFilename", historyFilesFilter.getSourceFile());
        }
        if (historyFilesFilter.getSourceHost() != null && !"".equals(historyFilesFilter.getSourceHost())) {
            query.setText("sourceHost", historyFilesFilter.getSourceHost());
        }
    }

    public List<DbItem> getFilesHistoryFromTo(Date from, Date to) throws Exception {
        historyFilesFilter.setCreatedFrom(from);
        historyFilesFilter.setCreatedTo(to);
        connection.beginTransaction();
        Query query = connection.createQuery("  from JadeFilesHistoryDBItem " + getHistoryWhere());
        if (historyFilesFilter.getCreatedFrom() != null) {
            query.setTimestamp(CREATED_FROM, historyFilesFilter.getCreatedFrom());
        }
        if (historyFilesFilter.getCreatedTo() != null) {
            query.setTimestamp(CREATED_TO, historyFilesFilter.getCreatedTo());
        }
        return query.list();
    }

    public JadeFilesDBItem getJadeFileItemById(Long jadeId) throws Exception {
        connection.beginTransaction();
        Query query = connection.createQuery("  from JadeFilesDBItem where id=:jadeId");
        query.setLong(JADE_ID, jadeId);
        List<JadeFilesDBItem> resultset = query.list();
        connection.commit();
        // id is unique, therefore only one item has to be returned
        return resultset.get(0);
    }

    public List<JadeFilesHistoryDBItem> getHistoryFiles() throws Exception {
        connection.beginTransaction();
        Query query = connection.createQuery("  from JadeFilesHistoryDBItem history " + getHistoryWhere());
        setHistoryWhere(query);
        List<JadeFilesHistoryDBItem> resultset = query.list();
        connection.commit();
        return resultset;
    }

    public List<JadeFilesHistoryDBItem> getHistoryFilesOrderedByTransferEnd() throws Exception {
        connection.beginTransaction();
        Query query = connection.createQuery("  from JadeFilesHistoryDBItem history " + getHistoryWhere() + " order by transferEnd desc");
        setHistoryWhere(query);
        List<JadeFilesHistoryDBItem> resultset = query.list();
        connection.commit();
        return resultset;
    }

    public void setHistoryCreatedFrom(Date createdFrom) {
        historyFilesFilter.setCreatedFrom(createdFrom);
    }

    public void setHistoryCreatedTo(Date createdTo) {
        historyFilesFilter.setCreatedTo(createdTo);
    }

    public void setHistoryDateFormat(String dateFormat) {
        historyFilesFilter.setDateFormat(dateFormat);
    }

    public void setHistoryCreatedFrom(String createdFrom) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(historyFilesFilter.getDateFormat());
        setHistoryCreatedFrom(formatter.parse(createdFrom));
    }

    public void setHistoryCreatedTo(String createdTo) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(historyFilesFilter.getDateFormat());
        setHistoryCreatedTo(formatter.parse(createdTo));
    }

    public JadeFilesHistoryFilter getHistoryFilter() {
        return historyFilesFilter;
    }

    public void setHistoryFilter(JadeFilesHistoryFilter historyFilesFilter) {
        this.historyFilesFilter = historyFilesFilter;
    }

    public List<DbItem> getListOfHistoryItemsToDelete() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
        return getFilesHistoryFromTo(historyFilesFilter.getCreatedFrom(), historyFilesFilter.getCreatedTo());
    }

}
