package com.sos.jade.db;

import java.text.SimpleDateFormat;
import java.util.List;

import org.hibernate.Query;

/** @author Uwe Risse */
public class JadeTransferDetailDBLayer extends JadeTransferDBLayer {

    public JadeTransferDetailDBLayer(final String configurationFile)   {
        super(configurationFile);
        this.setConfigurationFileName(configurationFile);
    }

    public List<JadeTransferDetailDBItem> getTransferDetailsFromTo() throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm");
        sosHibernateSession.connect();
        sosHibernateSession.beginTransaction();
        Query query = sosHibernateSession.createQuery("  from JadeTransferDetailDBItem where  created >= :createdFrom and created <= :createdTo");
        query.setTimestamp("createdFrom", createdFrom);
        query.setTimestamp("createdTo", createdTo);
        List<JadeTransferDetailDBItem> resultset = query.list();
        return resultset;
    }

    public List<JadeTransferDetailDBItem> getTransferListDetail(final int limit) throws Exception {
        sosHibernateSession.connect();
        sosHibernateSession.beginTransaction();
        Query query = sosHibernateSession.createQuery("from JadeTransferDetailDBItem " + getWhere());
        if (whereStartTime != null && !whereStartTime.equals("")) {
            query.setDate("startTime", whereStartTime);
        }
        if (whereEndTime != null && !whereEndTime.equals("")) {
            query.setDate("endTime", whereEndTime);
        }
        if (limit > 0){
            query.setMaxResults(limit);
        }
        List<JadeTransferDetailDBItem> transferDetailsList = query.list();
        return transferDetailsList;
    }

    @Override
    public int deleteFromTo() throws Exception {
        int row = deleteFromTo("JadeTransferDetailDBItem");
        return row;
    }

}
