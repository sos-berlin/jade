package sos.jadehistory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.sos.hibernate.classes.DbItem;
import com.sos.hibernate.classes.SOSHibernateIntervalFilter;
import com.sos.hibernate.interfaces.ISOSHibernateFilter;

public class JadeFilesFilter extends SOSHibernateIntervalFilter implements ISOSHibernateFilter {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_MDINIGHT_DATE_FORMAT = "yyyy-MM-dd 00:00:00";
    private static final String DEFAULT_SECOND_BEFORE_MDINIGHT_DATE_FORMAT = "yyyy-MM-dd 23:59:59";
    private static final Logger LOGGER = Logger.getLogger(JadeFilesFilter.class);
    private String dateFormat1 = DEFAULT_DATE_FORMAT;
    private String sourceHost;
    private String sourceHostIp;
    private String sourceUser;
    private String sourceDir;
    private String sourceFilename;
    private Date createdFrom;
    private Date createdTo;
    private String createdFromIso;
    private String createdToIso;
    private Date modifiedFrom;
    private Date modifiedTo;
    private String createdBy;
    private String modifiedBy;
    private String mandator;
    private Integer fileSize;
    private Date modificationDateFrom;
    private Date modificationDateTo;

    public JadeFilesFilter() {
        // nothing to do
    }

    public String getCreatedFromIso() {
        return createdFromIso;
    }

    public String getCreatedToIso() {
        return createdToIso;
    }

    @Override
    public String getDateFormat() {
        return dateFormat1;
    }

    @Override
    public void setDateFormat(final String dateFormat) {
        dateFormat1 = dateFormat;
    }

    public String getSourceHost() {
        return sourceHost;
    }

    public void setSourceHost(final String sourceHost) {
        this.sourceHost = sourceHost;
    }

    public String getSourceHostIp() {
        return sourceHostIp;
    }

    public void setSourceHostIp(final String sourceHostIp) {
        this.sourceHostIp = sourceHostIp;
    }

    public String getSourceUser() {
        return sourceUser;
    }

    public void setSourceUser(final String sourceUser) {
        this.sourceUser = sourceUser;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(final String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public String getSourceFilename() {
        return sourceFilename;
    }

    public void setSourceFilename(final String sourceFilename) {
        this.sourceFilename = sourceFilename;
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
            formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            createdFromIso = formatter.format(from);
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
            formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            createdToIso = formatter.format(to);
        }
    }

    @Override
    public String getTitle() {
        String s = "";
        if (createdFrom != null) {
            s += String.format("From: %s ", date2Iso(createdFrom));
        }
        if (createdTo != null) {
            s += String.format("To: %s ", date2Iso(createdTo));
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
        createdFromIso = s;
    }

    @Override
    public void setIntervalToDateIso(final String s) {
        createdToIso = s;
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

    public Date getModificationDateFrom() {
        return modificationDateFrom;
    }

    public void setModificationDateFrom(final String modificationDateFrom) throws ParseException {
        if ("".equals(modificationDateFrom)) {
            this.modificationDateFrom = null;
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat1);
            Date d = formatter.parse(modificationDateFrom);
            setModificationDateFrom(d);
        }
    }

    public Date getModificationDateTo() {
        return modificationDateTo;
    }

    public void setModificationDateTo(final String modificationDateTo) throws ParseException {
        if ("".equals(modificationDateTo)) {
            this.modificationDateTo = null;
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat1);
            Date d = formatter.parse(modificationDateTo);
            setModificationDateTo(d);
        }
    }

    public void setModificationDateFrom(final Date from) {
        if (from != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_MDINIGHT_DATE_FORMAT);
            String d = formatter.format(from);
            try {
                formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
                modificationDateFrom = formatter.parse(d);
            } catch (ParseException e) {
                LOGGER.error("", e);
            }
        }
    }

    public void setModificationDateTo(final Date to) {
        if (to != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_SECOND_BEFORE_MDINIGHT_DATE_FORMAT);
            String d = formatter.format(to);
            try {
                formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
                modificationDateTo = formatter.parse(d);
            } catch (ParseException e) {
                LOGGER.error("", e);
            }
        }
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getMandator() {
        return mandator;
    }

    public void setMandator(String mandator) {
        this.mandator = mandator;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

}
