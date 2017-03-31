package com.sos.jade.backgroundservice.listeners.impl;

import static com.sos.jade.backgroundservice.JADEHistoryViewerUI.JADE_BS_OPTIONS;
import static com.sos.jade.backgroundservice.JADEHistoryViewerUI.hibernateConfigFile;
import static com.sos.jade.backgroundservice.JADEHistoryViewerUI.factory;

import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sos.jadehistory.JadeFilesHistoryFilter;
import sos.jadehistory.db.JadeFilesDBItem;
import sos.jadehistory.db.JadeFilesDBLayer;
import sos.jadehistory.db.JadeFilesHistoryDBItem;
import sos.jadehistory.db.JadeFilesHistoryDBLayer;

import com.sos.hibernate.classes.ClassList;
import com.sos.hibernate.classes.SOSHibernateFactory;
import com.sos.jade.backgroundservice.listeners.IJadeFileListener;
import com.sos.jade.backgroundservice.view.MainView;

public class JadeFileListenerImpl implements IJadeFileListener, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(JadeFileListenerImpl.class);
    private JadeFilesDBLayer jadeFilesDBLayer;
    private JadeFilesHistoryDBLayer jadeFilesHistoryDBLayer;
    public MainView ui;

    public JadeFileListenerImpl(MainView ui) {
        this.ui = ui;
        if (hibernateConfigFile != null && hibernateConfigFile.length() != 0) {
            JADE_BS_OPTIONS.hibernateConfigurationFileName.setValue(hibernateConfigFile);
        }
        JADE_BS_OPTIONS.hibernateConfigurationFileName.checkMandatory();
        this.jadeFilesDBLayer = new JadeFilesDBLayer(hibernateConfigFile);
        this.jadeFilesHistoryDBLayer = new JadeFilesHistoryDBLayer(hibernateConfigFile);
    }

    @Override
    public void logException(Exception e) {
        LOGGER.error(e.getMessage(), e);
    }

    @Override
    public void getFileHistoryByIdFromLayer(Long id) {
        initJadeFilesDbSession();
        getFileHistoryFromDb(id);
    }

    @Override
    public void filterJadeFilesHistory(JadeFilesHistoryFilter filter) {
        if (filter != null) {
            this.jadeFilesHistoryDBLayer.setFilter(filter);
        }
        getFilteredFilesHistory();
    }

    private void getFilteredFilesHistory() {
        initJadeFilesHistoryDbSession();
        getFilesHistoryFromDb();
    }

    private void getFileHistoryFromDb(Long id) {
        try {
            List<JadeFilesHistoryDBItem> received = this.jadeFilesDBLayer.getFilesHistoryById(id);
            ui.setHistoryItems(received);
        } catch (Exception e) {
            logException(e);
        }
    }

    private void getFilesHistoryFromDb() {
        try {
            List<JadeFilesHistoryDBItem> received = jadeFilesHistoryDBLayer.getHistoryFilesOrderedByTransferEnd();
            ui.setHistoryItems(received);
        } catch (Exception e) {
            logException(e);
        }
    }

    private void initJadeFilesDbSession() {
        try {
            if (factory == null) {
                factory = new SOSHibernateFactory(hibernateConfigFile);
                factory.getClassMapping().add(JadeFilesDBItem.class);
                factory.getClassMapping().add(JadeFilesHistoryDBItem.class);
                factory.build();
            }
            jadeFilesDBLayer.setFactory(factory);
            jadeFilesDBLayer.initStatefullConnection();
        } catch (Exception e) {
            LOGGER.error("Exception occurred while creating statefull Session", e);
        }
    }

    private void initJadeFilesHistoryDbSession() {
        try {
            if (factory == null) {
                factory = new SOSHibernateFactory(hibernateConfigFile);
                factory.getClassMapping().add(JadeFilesDBItem.class);
                factory.getClassMapping().add(JadeFilesHistoryDBItem.class);
                factory.build();
            }
            jadeFilesHistoryDBLayer.setFactory(factory);
            jadeFilesHistoryDBLayer.initStatefullConnection();
        } catch (Exception e) {
            try {
                LOGGER.error("Exception occurred while initializing Session for the first time", e);
            } catch (Exception e1) {
                LOGGER.error("Exception occurred while initializing Session for the second time", e1);
            }
        }
    }

}
