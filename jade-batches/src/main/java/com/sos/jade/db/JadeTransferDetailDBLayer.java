package com.sos.jade.db;

import java.text.SimpleDateFormat;
import java.util.List;

import org.hibernate.Query;

/** @author Uwe Risse */
public class JadeTransferDetailDBLayer extends JadeTransferDBLayer {

    public JadeTransferDetailDBLayer(final String configurationFile) {
        super(configurationFile);
        this.setConfigurationFileName(configurationFile);
        this.initConnection(this.getConfigurationFileName());
    }

    public List<JadeTransferDetailDBItem> getTransferDetailsFromTo() throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm");
        connection.connect();
        connection.beginTransaction();
        Query query = connection.createQuery("  from JadeTransferDetailDBItem where  created >= :createdFrom and created <= :createdTo");
        query.setTimestamp("createdFrom", createdFrom);
        query.setTimestamp("createdTo", createdTo);
        List<JadeTransferDetailDBItem> resultset = query.list();
        return resultset;
    }

    public List<JadeTransferDetailDBItem> getTransferListDetail(final int limit) throws Exception {
        connection.connect();
        connection.beginTransaction();
        Query query = connection.createQuery("from JadeTransferDetailDBItem " + getWhere());
        if (whereStartTime != null && !whereStartTime.equals("")) {
            query.setDate("startTime", whereStartTime);
        }
        if (whereEndTime != null && !whereEndTime.equals("")) {
            query.setDate("endTime", whereEndTime);
        }
        query.setMaxResults(limit);
        List<JadeTransferDetailDBItem> transferDetailsList = query.list();
        return transferDetailsList;
    }

    @Override
    public int deleteFromTo() throws Exception {
        int row = deleteFromTo("JadeTransferDetailDBItem");
        return row;
    }

}
