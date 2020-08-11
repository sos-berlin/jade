package com.sos.jade.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Query;

import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.hibernate.layer.SOSHibernateDBLayer;
import com.sos.jade.JadeHistoryFilter;

/** @author Uwe Risse */
public class JadeTransferDBLayer extends SOSHibernateDBLayer {

    protected Date createdFrom;
    protected Date createdTo;
    protected String whereProfileName = null;
    protected String whereStatus = null;
    protected Date whereStartTime = null;
    protected Date whereEndTime = null;
    protected String whereStartTimeIso;
    protected String whereEndTimeIso;
    protected String dateFormat;
    private JadeHistoryFilter filter;
    private int age;

    public JadeTransferDBLayer(String configurationFile) {
        super();
        try {
            this.createStatelessConnection(configurationFile);
        } catch (Exception e) {
            throw new JobSchedulerException(e);
        }
        this.setConfigurationFileName(configurationFile);
        this.dateFormat = "dd.MM.yyyy hh:mm";
    }

    public JadeHistoryFilter getFilter() {
        return filter;
    }

    public void setFilter(JadeHistoryFilter filter_) {
        filter = filter_;
    }

    protected String getWhereFromTo() {
        return " created >= :createdFrom and created <= :createdTo";
    }

    public void setAge(int age) throws ParseException {
        Date now = new Date();
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(now);
        calendar.add(GregorianCalendar.DAY_OF_MONTH, age * (-1));
        now = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String froms = formatter.format(now);
        froms = froms + "T00:00:00";
        formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        this.setCreatedTo(new Date());
        this.setCreatedFrom(formatter.parse(froms));
        this.age = age;
    }

    public List<JadeTransferDBItem> getTransfersFromTo() throws Exception {
        List<JadeTransferDBItem> resultset = null;
        sosHibernateSession.connect();
        sosHibernateSession.beginTransaction();
        Query query = sosHibernateSession.createQuery("  from JadeTransferDBItem where " + getWhereFromTo());
        query.setTimestamp("createdFrom", createdFrom);
        query.setTimestamp("createdTo", createdTo);
        resultset = query.list();
        return resultset;
    }

    protected String getWhere() {
        String where = "";
        String and = "";
        if (whereProfileName != null && !"".equals(whereProfileName)) {
            where += and + " profileName='" + whereProfileName + "'";
            and = " and ";
        }
        if (whereStatus != null && !"".equals(whereStatus)) {
            where += and + " status=" + whereStatus;
            and = " and ";
        }
        if (whereStartTime != null && !"".equals(whereStartTime)) {
            where += and + " startTime>= :startTime";
            and = " and ";
        }
        if (whereEndTime != null && !"".equals(whereEndTime)) {
            where += and + " endTime <= :endTime ";
            and = " and ";
        }
        if (!"".equals(where.trim())) {
            where = "where " + where;
        }
        return where;

    }

    public List<JadeTransferDBItem> getTransferList(int limit) throws Exception {
        List<JadeTransferDBItem> transferList = null;
        sosHibernateSession.connect();
        sosHibernateSession.beginTransaction();
        Query query = sosHibernateSession.createQuery("from JadeTransferDBItem " + getWhere());
        if (whereStartTime != null && !"".equals(whereStartTime)) {
            query.setDate("startTime", whereStartTime);
        }
        if (whereEndTime != null && !"".equals(whereEndTime)) {
            query.setDate("endTime", whereEndTime);
        }
        if (limit > 0){
            query.setMaxResults(limit);
        }
        transferList = query.list();
        return transferList;
    }

    public int deleteFromTo(String tableName) throws Exception {
        String hql = "delete from " + tableName + " where " + getWhereFromTo();
        int row = 0;
        sosHibernateSession.beginTransaction();
        Query query = sosHibernateSession.createQuery(hql);
        if (createdFrom != null && !"".equals(createdFrom)) {
            query.setTimestamp("createdFrom", createdFrom);
        }
        if (createdTo != null && !"".equals(createdTo)) {
            query.setTimestamp("createdTo", createdTo);
        }
        row = query.executeUpdate();
        sosHibernateSession.commit();
        return row;
    }

    public int deleteFromTo() throws Exception {
        int row = deleteFromTo("JadeTransferDBItem");
        return row;
    }

    public void setWhereProfileName(String whereProfileName) {
        this.whereProfileName = whereProfileName;
    }

    public void setWhereStatus(String whereStatus) {
        this.whereStatus = whereStatus;
    }

    public void setWhereStartTime(Date whereStartTime) {
        this.whereStartTime = whereStartTime;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        whereStartTimeIso = formatter.format(whereStartTime);
    }

    public void setWhereEndTime(Date whereEndTime) {
        this.whereEndTime = whereEndTime;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        whereEndTimeIso = formatter.format(whereEndTime);
    }

    public void setWhereStartTime(String whereStartTime) throws ParseException {
        if ("".equals(whereStartTime)) {
            this.whereStartTime = null;
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm");
            Date d = formatter.parse(whereStartTime);
            setWhereStartTime(d);
        }
    }

    public void setWhereEndTime(String whereEndTime) throws ParseException {
        if ("".equals(whereEndTime)) {
            this.whereEndTime = null;
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm");
            Date d = formatter.parse(whereEndTime);
            setWhereEndTime(d);
        }
    }

    public void setCreatedFrom(Date createdFrom) {
        this.createdFrom = createdFrom;
    }

    public void setCreatedTo(Date createdTo) {
        this.createdTo = createdTo;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setCreatedFrom(String createdFrom) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        this.createdFrom = formatter.parse(createdFrom);
    }

    public void setCreatedTo(String createdTo) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        this.createdTo = formatter.parse(createdTo);
    }

    public void save(JadeTransferDBItem transferItem) throws Exception {

        sosHibernateSession.beginTransaction();
        sosHibernateSession.save(transferItem);
        sosHibernateSession.commit();
    }
}
