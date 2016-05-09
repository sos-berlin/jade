package com.sos.jade;

import java.io.File;
import java.util.List;

import org.eclipse.swt.widgets.Table;

import com.sos.hibernate.interfaces.ISOSHibernateDataProvider;
import com.sos.jade.db.JadeTransferDBItem;
import com.sos.jade.db.JadeTransferDBLayer;

/** @author Uwe Risse */
public class JadeHistoryDataProvider implements ISOSHibernateDataProvider {

    private List<JadeTransferDBItem> listOfTransferItems;
    private JadeTransferDBLayer jadeTransferDBLayer;

    public JadeHistoryDataProvider(File configurationFile) {
        this.jadeTransferDBLayer = new JadeTransferDBLayer(configurationFile);
    }

    public JadeHistoryFilter getFilter() {
        return jadeTransferDBLayer.getFilter();
    }

    public void resetFilter() {
        jadeTransferDBLayer.setFilter(new JadeHistoryFilter());
    }

    public void getData(int limit) {
        this.listOfTransferItems = jadeTransferDBLayer.getTransferList(limit);
    }

    @Override
    public void fillTable(Table table) {
        // TO DO Auto-generated method stub
    }

    @Override
    public void commit() {
        // TO DO Auto-generated method stub
    }

}