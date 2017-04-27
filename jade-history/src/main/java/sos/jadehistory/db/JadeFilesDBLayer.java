package sos.jadehistory.db;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;

import sos.jadehistory.JadeFilesFilter;

import com.sos.hibernate.classes.DbItem;
import com.sos.hibernate.classes.SOSHibernateFactory;
import com.sos.hibernate.classes.SOSHibernateSession;
import com.sos.hibernate.layer.SOSHibernateIntervalDBLayer;

public class JadeFilesDBLayer extends SOSHibernateIntervalDBLayer implements Serializable {

    protected JadeFilesFilter filter = null;
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(JadeFilesDBLayer.class);
    private static final String AND = " and ";
    private static final String CREATED_FROM = "createdFrom";
    private static final String CREATED_TO = "createdTo";
    private SOSHibernateFactory factory;

    public JadeFilesDBLayer(String configurationFileName) {
        super();
        this.setConfigurationFileName(configurationFileName);
        this.resetFilter();
    }

    public JadeFilesDBLayer(SOSHibernateSession session) {
        super();
        this.setConfigurationFileName(session.getFactory().getConfigFile().get().toFile().getAbsolutePath());
        this.sosHibernateSession = session;
        this.resetFilter();
        
    }
    
    public JadeFilesDBItem get(Long id) throws Exception {
        if (id == null) {
            return null;
        }
        if (sosHibernateSession == null) {
        }
        sosHibernateSession.beginTransaction();
        return (JadeFilesDBItem) this.sosHibernateSession.get(JadeFilesDBItem.class, id);
    }

    public void resetFilter() {
        filter = new JadeFilesFilter();
        this.filter = new JadeFilesFilter();
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
        if (filter.getModificationDateFrom() != null) {
            where += and + " modificationDate >= :modificationDateFrom";
            and = AND;
        }
        if (filter.getModificationDateTo() != null) {
            where += and + " modificationDate <= :modificationDateTo ";
            and = AND;
        }
        if (filter.getMandator() != null && !"".equals(filter.getMandator())) {
            where += and + " mandator=:mandator";
            and = AND;
        }
        if (filter.getCreatedBy() != null && !"".equals(filter.getCreatedBy())) {
            where += and + " createdBy=:createdBy";
            and = AND;
        }
        if (filter.getModifiedBy() != null && !"".equals(filter.getModifiedBy())) {
            where += and + " modifiedBy=:modifiedBy";
            and = AND;
        }
        if (filter.getSourceDir() != null && !"".equals(filter.getSourceDir())) {
            where += and + " sourceDir=:sourceDir";
            and = AND;
        }
        if (filter.getSourceFilename() != null && !"".equals(filter.getSourceFilename())) {
            where += and + " sourceFilename like :sourceFilename";
            and = AND;
        }
        if (filter.getSourceHost() != null && !"".equals(filter.getSourceHost())) {
            where += and + " sourceHost=:sourceHost";
            and = AND;
        }
        if (filter.getSourceHostIp() != null && !"".equals(filter.getSourceHostIp())) {
            where += and + " sourceHostIp=:sourceHostIp";
            and = AND;
        }
        if (filter.getSourceUser() != null && !"".equals(filter.getSourceUser())) {
            where += and + " sourceUser=:sourceUser";
            and = AND;
        }
        if (filter.getFileSize() != null) {
            where += and + " fileSize=:fileSize";
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
            query.setTimestamp(CREATED_FROM, filter.getCreatedFrom());
        }
        if (filter.getCreatedTo() != null && !"".equals(filter.getCreatedTo())) {
            query.setTimestamp(CREATED_TO, filter.getCreatedTo());
        }
        if (filter.getModifiedFrom() != null && !"".equals(filter.getModifiedFrom())) {
            query.setTimestamp("modifiedFrom", filter.getModifiedFrom());
        }
        if (filter.getModifiedTo() != null && !"".equals(filter.getModifiedTo())) {
            query.setTimestamp("modifiedTo", filter.getModifiedTo());
        }
        if (filter.getModificationDateFrom() != null && !"".equals(filter.getModificationDateFrom())) {
            query.setTimestamp("modificationDateFrom", filter.getModificationDateFrom());
        }
        if (filter.getModificationDateTo() != null && !"".equals(filter.getModificationDateTo())) {
            query.setTimestamp("modificationDateTo", filter.getModificationDateTo());
        }
        if (filter.getMandator() != null && !"".equals(filter.getMandator())) {
            query.setText("mandator", filter.getMandator());
        }
        if (filter.getCreatedBy() != null && !"".equals(filter.getCreatedBy())) {
            query.setText("createdBy", filter.getCreatedBy());
        }
        if (filter.getModifiedBy() != null && !"".equals(filter.getModifiedBy())) {
            query.setText("modifiedBy", filter.getModifiedBy());
        }
        if (filter.getSourceDir() != null && !"".equals(filter.getSourceDir())) {
            query.setText("sourceDir", filter.getSourceDir());
        }
        if (filter.getSourceFilename() != null && !"".equals(filter.getSourceFilename())) {
            query.setText("sourceFilename", filter.getSourceFilename());
        }
        if (filter.getSourceHost() != null && !"".equals(filter.getSourceHost())) {
            query.setText("sourceHost", filter.getSourceHost());
        }
        if (filter.getSourceHostIp() != null && !"".equals(filter.getSourceHostIp())) {
            query.setText("sourceHostIp", filter.getSourceHostIp());
        }
        if (filter.getSourceUser() != null && !"".equals(filter.getSourceUser())) {
            query.setText("sourceUser", filter.getSourceUser());
        }
        if (filter.getFileSize() != null) {
            query.setInteger("fileSize", filter.getFileSize());
        }
    }

    public int delete() throws Exception {
        String q = "delete from JadeFilesHistoryDBItem e where e.jadeFilesDBItem.id IN (select id from JadeFilesDBItem " + getWhere() + ")";
        int row = 0;
        Query query = sosHibernateSession.createQuery(q);
        setWhere(query);
        row = query.executeUpdate();
        sosHibernateSession.commit();
        String hql = "delete from JadeFilesDBItem " + getWhere();
        sosHibernateSession.beginTransaction();
        query = sosHibernateSession.createQuery(hql);
        setWhere(query);
        row = query.executeUpdate();
        sosHibernateSession.commit();
        return row;
    }

    public List<DbItem> getFilesFromTo(Date from, Date to) throws Exception {
        filter.setCreatedFrom(from);
        filter.setCreatedTo(to);
        List<DbItem> resultset = null;
        sosHibernateSession.beginTransaction();
        Query query = sosHibernateSession.createQuery("  from JadeFilesDBItem " + getWhere());
        if (filter.getCreatedFrom() != null) {
            query.setTimestamp(CREATED_FROM, filter.getCreatedFrom());
        }
        if (filter.getCreatedTo() != null) {
            query.setTimestamp(CREATED_TO, filter.getCreatedTo());
        }
        resultset = query.list();
        return resultset;
    }

    public List<JadeFilesHistoryDBItem> getFilesHistoryById(Long jadeId) throws Exception {
        List<JadeFilesHistoryDBItem> resultset = null;
        sosHibernateSession.beginTransaction();
        Query query = sosHibernateSession.createQuery("  from JadeFilesHistoryDBItem where jadeId=:jadeId");
        query.setLong("jadeId", jadeId);
        resultset = query.list();
        return resultset;
    }

    public List<JadeFilesDBItem> getFiles() throws Exception {
        List<JadeFilesDBItem> resultset = null;
        sosHibernateSession.beginTransaction();
        Query query = sosHibernateSession.createQuery("  from JadeFilesDBItem " + getWhere());
        setWhere(query);
        resultset = query.list();
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
    public JadeFilesFilter getFilter() {
        return filter;
    }

    public void setFilter(JadeFilesFilter filter) {
        this.filter = filter;
    }

    @Override
    public void onAfterDeleting(DbItem h) {
        // nothing to do
    }

    @Override
    public List<DbItem> getListOfItemsToDelete() throws Exception {
        return getFilesFromTo(filter.getCreatedFrom(), filter.getCreatedTo());
    }

    @Override
    public long deleteInterval() throws Exception {
        String q = "delete from JadeFilesHistoryDBItem e where e.jadeFilesDBItem.id IN (select id from JadeFilesDBItem " + getWhereFromTo() + ")";
        int row = 0;
        Query query = sosHibernateSession.createQuery(q);
        if (filter.getCreatedFrom() != null) {
            query.setTimestamp(CREATED_FROM, filter.getCreatedFrom());
        }
        if (filter.getCreatedTo() != null) {
            query.setTimestamp(CREATED_TO, filter.getCreatedTo());
        }
        row = query.executeUpdate();
        String hql = "delete from JadeFilesDBItem " + getWhereFromTo();
        query = sosHibernateSession.createQuery(hql);
        if (filter.getCreatedFrom() != null) {
            query.setTimestamp(CREATED_FROM, filter.getCreatedFrom());
        }
        if (filter.getCreatedTo() != null) {
            query.setTimestamp(CREATED_TO, filter.getCreatedTo());
        }
        row = query.executeUpdate();
        return row;
    }

    public SOSHibernateSession initStatefullConnection() throws Exception {
        sosHibernateSession = factory.openSession();
        return sosHibernateSession;
    }

    public SOSHibernateFactory getFactory() {
        return factory;
    }
    
    public void setFactory(SOSHibernateFactory factory) {
        this.factory = factory;
    }

}