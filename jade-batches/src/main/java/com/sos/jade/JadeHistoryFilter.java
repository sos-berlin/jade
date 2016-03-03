package com.sos.jade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.sos.hibernate.classes.DbItem;

/** @author Uwe Risse */
public class JadeHistoryFilter implements com.sos.hibernate.interfaces.ISOSHibernateFilter {

    private static final Logger LOGGER = Logger.getLogger(JadeHistoryFilter.class);
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private Date plannedFrom;
    private Date plannedTo;
    private String status = "";

    public JadeHistoryFilter() {
        //
    }

    public Date getPlannedFrom() {
        return plannedFrom;
    }

    public void setPlannedFrom(Date plannedFrom) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String d = formatter.format(plannedFrom);
        try {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.plannedFrom = formatter.parse(d);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void setPlannedFrom(String plannedFrom) throws ParseException {
        if ("".equals(plannedFrom)) {
            this.plannedFrom = null;
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            Date d = formatter.parse(plannedFrom);
            setPlannedFrom(d);
        }
    }

    public void setPlannedFrom(String plannedFrom, String dateFormat) throws ParseException {
        this.dateFormat = dateFormat;
        setPlannedFrom(plannedFrom);
    }

    public void setPlannedTo(String plannedTo, String dateFormat) throws ParseException {
        this.dateFormat = dateFormat;
        setPlannedTo(plannedTo);
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Date getPlannedTo() {
        return plannedTo;
    }

    public void setPlannedTo(Date plannedTo) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        String d = formatter.format(plannedTo);
        try {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.plannedTo = formatter.parse(d);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void setPlannedTo(String plannedTo) throws ParseException {
        if ("".equals(plannedTo)) {
            this.plannedTo = null;
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            Date d = formatter.parse(plannedTo);
            setPlannedTo(d);
        }
    }

    public boolean isFiltered(DbItem dbitem) {
        return false;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getTitle() {
        // TODO Auto-generated method stub
        return null;
    }

}
