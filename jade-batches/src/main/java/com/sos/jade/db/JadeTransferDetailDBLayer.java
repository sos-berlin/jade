package com.sos.jade.db;

import java.io.File;
import java.text.ParseException;
import java.util.List;

import org.hibernate.Query;

/** @author Uwe Risse */
public class JadeTransferDetailDBLayer extends JadeTransferDBLayer {

    public JadeTransferDetailDBLayer(final File configurationFile) {
        super(configurationFile);
        this.setConfigurationFile(configurationFile);
    }

    public List<JadeTransferDetailDBItem> getTransferDetailsFromTo() throws ParseException {
        beginTransaction();
        Query query = session.createQuery("  from JadeTransferDetailDBItem where  created >= :createdFrom and created <= :createdTo");
        query.setTimestamp("createdFrom", createdFrom);
        query.setTimestamp("createdTo", createdTo);
        List<JadeTransferDetailDBItem> resultset = query.list();
        commit();
        return resultset;
    }

    public List<JadeTransferDetailDBItem> getTransferListDetail(final int limit) throws Exception {
        beginTransaction();
        Query query = session.createQuery("from JadeTransferDetailDBItem " + getWhere());
        if (whereStartTime != null && !"".equals(whereStartTime)) {
            query.setDate("startTime", whereStartTime);
        }
        if (whereEndTime != null && !"".equals(whereEndTime)) {
            query.setDate("endTime", whereEndTime);
        }
        query.setMaxResults(limit);
        List<JadeTransferDetailDBItem> transferDetailsList = query.list();
        commit();
        return transferDetailsList;
    }

    @Override
    public int deleteFromTo() {
        return deleteFromTo("JadeTransferDetailDBItem");
    }

}