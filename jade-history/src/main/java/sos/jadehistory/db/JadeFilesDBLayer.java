package sos.jadehistory.db;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import sos.jadehistory.JadeFilesFilter;

import com.sos.hibernate.classes.DbItem;
import com.sos.hibernate.layer.SOSHibernateIntervalDBLayer;

public class JadeFilesDBLayer extends SOSHibernateIntervalDBLayer implements Serializable {

    protected JadeFilesFilter filter = null;
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(JadeFilesDBLayer.class);

    public JadeFilesDBLayer(String configurationFileName) {
        super();
        this.setConfigurationFileName(configurationFileName);
        this.initConnection(this.getConfigurationFileName());
        this.resetFilter();
    }

    public JadeFilesDBItem get(Long id) {
        if (id == null) {
            return null;
        }
        try {
            connection.connect();
            connection.beginTransaction();
            return (JadeFilesDBItem) ((Session) this.connection.getCurrentSession()).get(JadeFilesDBItem.class, id);
        } catch (Exception e) {
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
            and = " and ";
        }
        if (filter.getCreatedTo() != null) {
            where += and + " created <= :createdTo ";
            and = " and ";
        }
        if (filter.getModifiedFrom() != null) {
            where += and + " modified >= :modifiedFrom";
            and = " and ";
        }
        if (filter.getModifiedTo() != null) {
            where += and + " modified <= :modifiedTo ";
            and = " and ";
        }
        if (filter.getModificationDateFrom() != null) {
            where += and + " modificationDate >= :modificationDateFrom";
            and = " and ";
        }
        if (filter.getModificationDateTo() != null) {
            where += and + " modificationDate <= :modificationDateTo ";
            and = " and ";
        }
        if (filter.getMandator() != null && !"".equals(filter.getMandator())) {
            where += and + " mandator=:mandator";
            and = " and ";
        }
        if (filter.getCreatedBy() != null && !"".equals(filter.getCreatedBy())) {
            where += and + " createdBy=:createdBy";
            and = " and ";
        }
        if (filter.getModifiedBy() != null && !"".equals(filter.getModifiedBy())) {
            where += and + " modifiedBy=:modifiedBy";
            and = " and ";
        }
        if (filter.getSourceDir() != null && !"".equals(filter.getSourceDir())) {
            where += and + " sourceDir=:sourceDir";
            and = " and ";
        }
        if (filter.getSourceFilename() != null && !"".equals(filter.getSourceFilename())) {
            where += and + " sourceFilename like :sourceFilename";
            and = " and ";
        }
        if (filter.getSourceHost() != null && !"".equals(filter.getSourceHost())) {
            where += and + " sourceHost=:sourceHost";
            and = " and ";
        }
        if (filter.getSourceHostIp() != null && !"".equals(filter.getSourceHostIp())) {
            where += and + " sourceHostIp=:sourceHostIp";
            and = " and ";
        }
        if (filter.getSourceUser() != null && !"".equals(filter.getSourceUser())) {
            where += and + " sourceUser=:sourceUser";
            and = " and ";
        }
        if (filter.getFileSize() != null) {
            where += and + " fileSize=:fileSize";
            and = " and ";
        }
        if (!where.trim().equals("")) {
            where = "where " + where;
        }
        return where;
    }

    protected String getWhereFromTo() {
        String where = "";
        String and = "";
        if (filter.getCreatedFrom() != null) {
            where += and + " created >= :createdFrom";
            and = " and ";
        }
        if (filter.getCreatedTo() != null) {
            where += and + " created <= :createdTo ";
            and = " and ";
        }
        if (!where.trim().equals("")) {
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
        String q = "delete from JadeFilesHistoryDBItem e where e.jadeFilesDBItem.id IN (select id from JadeFilesDBItem " + getWhere() + ")";
        int row = 0;
        try {
            if (connection == null) {
                initConnection(getConfigurationFileName());
            }
            connection.connect();
            connection.beginTransaction();
            Query query = connection.createQuery(q);
            setWhere(query);
            row = query.executeUpdate();
            connection.commit();
            String hql = "delete from JadeFilesDBItem " + getWhere();
            connection.connect();
            connection.beginTransaction();
            query = connection.createQuery(hql);
            setWhere(query);
            row = query.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            LOGGER.error("Error occurred while trying to delete items: ", e);
            e.printStackTrace();
        }
        return row;
    }

    public List<DbItem> getFilesFromTo(Date from, Date to) {
        filter.setCreatedFrom(from);
        filter.setCreatedTo(to);
        List<DbItem> resultset = null;
        try {
            if (connection == null) {
                initConnection(getConfigurationFileName());
            }
            connection.connect();
            connection.beginTransaction();
            Query query = connection.createQuery("  from JadeFilesDBItem " + getWhere());
            if (filter.getCreatedFrom() != null) {
                query.setTimestamp("createdFrom", filter.getCreatedFrom());
            }
            if (filter.getCreatedTo() != null) {
                query.setTimestamp("createdTo", filter.getCreatedTo());
            }
            resultset = query.list();
        } catch (Exception e) {
            LOGGER.error("Error occurred receiving Data for the given timeframe: ", e);
        }
        return resultset;
    }

    public List<JadeFilesHistoryDBItem> getFilesHistoryById(Long jadeId) throws ParseException {
        List<JadeFilesHistoryDBItem> resultset = null;
        try {
            if (connection == null) {
                initConnection(getConfigurationFileName());
            }
            connection.connect();
            connection.beginTransaction();
            Query query = connection.createQuery("  from JadeFilesHistoryDBItem where jadeId=:jadeId");
            query.setLong("jadeId", jadeId);
            resultset = query.list();
        } catch (Exception e) {
            LOGGER.error("Error occurred getting DBItem: ", e);
        }
        return resultset;

    }

    public List<JadeFilesDBItem> getFiles() throws ParseException {
        List<JadeFilesDBItem> resultset = null;
        try {
            if (connection == null) {
                initConnection(getConfigurationFileName());
            }
            connection.connect();
            connection.beginTransaction();
            Query query = connection.createQuery("  from JadeFilesDBItem " + getWhere());
            setWhere(query);
            resultset = query.list();
        } catch (Exception e) {
            LOGGER.error("Error occurred receiving DBItems: ", e);
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
        String q = "delete from JadeFilesHistoryDBItem e where e.jadeFilesDBItem.id IN (select id from JadeFilesDBItem " + getWhereFromTo() + ")";
        int row = 0;
        try {
            if (connection == null) {
                initConnection(getConfigurationFileName());
            }
            connection.connect();
            connection.beginTransaction();
            Query query = connection.createQuery(q);
            if (filter.getCreatedFrom() != null) {
                query.setTimestamp("createdFrom", filter.getCreatedFrom());
            }
            if (filter.getCreatedTo() != null) {
                query.setTimestamp("createdTo", filter.getCreatedTo());
            }
            row = query.executeUpdate();
            connection.commit();
            String hql = "delete from JadeFilesDBItem " + getWhereFromTo();
            connection.connect();
            connection.beginTransaction();
            query = connection.createQuery(hql);
            if (filter.getCreatedFrom() != null) {
                query.setTimestamp("createdFrom", filter.getCreatedFrom());
            }
            if (filter.getCreatedTo() != null) {
                query.setTimestamp("createdTo", filter.getCreatedTo());
            }
            row = query.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            LOGGER.error("Error occurred while trying to delete interval: ", e);
        }
        return row;
    }

}
