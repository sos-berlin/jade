package sos.jadehistory.db;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import sos.jadehistory.JadeFilesFilter;

import com.sos.hibernate.classes.DbItem;
import com.sos.hibernate.layer.SOSHibernateIntervalDBLayer;

public class JadeFilesDBLayer extends SOSHibernateIntervalDBLayer implements Serializable {

    protected JadeFilesFilter filter = null;
    private static final long serialVersionUID = 1L;
    private static final String AND = " and ";
    private static final String CREATED_FROM = "createdFrom";
    private static final String CREATED_TO = "createdTo";

    public JadeFilesDBLayer(File configurationFile) {
        super();
        this.setConfigurationFile(configurationFile);
        this.resetFilter();
    }

    public JadeFilesDBItem get(Long id) {
        if (id == null) {
            return null;
        }
        try {
            return (JadeFilesDBItem) this.getSession().get(JadeFilesDBItem.class, id);
        } catch (ObjectNotFoundException e) {
            return null;
        }
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

    public int delete() {
        if (session == null) {
            beginTransaction();
        }
        String q = "delete from JadeFilesHistoryDBItem e where e.jadeFilesDBItem.id IN (select id from JadeFilesDBItem " + getWhere() + ")";
        Query query = session.createQuery(q);
        setWhere(query);
        int row = query.executeUpdate();
        String hql = "delete from JadeFilesDBItem " + getWhere();
        query = session.createQuery(hql);
        setWhere(query);
        row = query.executeUpdate();
        return row;
    }

    public List<DbItem> getFilesFromTo(Date from, Date to) {
        Session session = getSession();
        filter.setCreatedFrom(from);
        filter.setCreatedTo(to);
        session.beginTransaction();
        Query query = session.createQuery("  from JadeFilesDBItem " + getWhere());
        if (filter.getCreatedFrom() != null) {
            query.setTimestamp(CREATED_FROM, filter.getCreatedFrom());
        }
        if (filter.getCreatedTo() != null) {
            query.setTimestamp(CREATED_TO, filter.getCreatedTo());
        }
        return query.list();
    }

    public List<JadeFilesHistoryDBItem> getFilesHistoryById(Long jadeId) throws ParseException {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        Query query = session.createQuery("  from JadeFilesHistoryDBItem where jadeId=:jadeId");
        query.setLong("jadeId", jadeId);
        List<JadeFilesHistoryDBItem> resultset = query.list();
        transaction.commit();
        return resultset;
    }

    public List<JadeFilesDBItem> getFiles() throws ParseException {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        Query query = session.createQuery("  from JadeFilesDBItem " + getWhere());
        setWhere(query);
        List<JadeFilesDBItem> resultset = query.list();
        transaction.commit();
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
    public List<DbItem> getListOfItemsToDelete() {
        return getFilesFromTo(filter.getCreatedFrom(), filter.getCreatedTo());

    }

    @Override
    public long deleteInterval() {
        if (session == null) {
            beginTransaction();
        }
        String q = "delete from JadeFilesHistoryDBItem e where e.jadeFilesDBItem.id IN (select id from JadeFilesDBItem " + getWhereFromTo() + ")";
        Query query = session.createQuery(q);
        if (filter.getCreatedFrom() != null) {
            query.setTimestamp("createdFrom", filter.getCreatedFrom());
        }
        if (filter.getCreatedTo() != null) {
            query.setTimestamp("createdTo", filter.getCreatedTo());
        }
        int row = query.executeUpdate();
        String hql = "delete from JadeFilesDBItem " + getWhereFromTo();
        query = session.createQuery(hql);
        if (filter.getCreatedFrom() != null) {
            query.setTimestamp(CREATED_FROM, filter.getCreatedFrom());
        }
        if (filter.getCreatedTo() != null) {
            query.setTimestamp(CREATED_TO, filter.getCreatedTo());
        }
        row = query.executeUpdate();
        return row;
    }

}
