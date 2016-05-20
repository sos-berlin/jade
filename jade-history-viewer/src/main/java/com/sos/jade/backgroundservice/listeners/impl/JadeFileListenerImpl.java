package com.sos.jade.backgroundservice.listeners.impl;

import static com.sos.jade.backgroundservice.JADEHistoryViewerUI.JADE_BS_OPTIONS;
import static com.sos.jade.backgroundservice.JADEHistoryViewerUI.hibernateConfigFile;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sos.jadehistory.JadeFilesHistoryFilter;
import sos.jadehistory.db.JadeFilesDBLayer;
import sos.jadehistory.db.JadeFilesHistoryDBItem;
import sos.jadehistory.db.JadeFilesHistoryDBLayer;

import com.sos.jade.backgroundservice.listeners.IJadeFileListener;
import com.sos.jade.backgroundservice.view.MainView;

public class JadeFileListenerImpl implements IJadeFileListener, Serializable {

    private static final long serialVersionUID = 1L;
    private JadeFilesDBLayer jadeFilesDBLayer;
    private JadeFilesHistoryDBLayer jadeFilesHistoryDBLayer;
    public MainView ui;
    private static final Logger LOGGER = LoggerFactory.getLogger(JadeFileListenerImpl.class);

    public JadeFileListenerImpl(MainView ui) {
        this.ui = ui;
        if (hibernateConfigFile != null && hibernateConfigFile.length() != 0) {
            JADE_BS_OPTIONS.hibernateConfigurationFileName.setValue(hibernateConfigFile);
        }
        JADE_BS_OPTIONS.hibernateConfigurationFileName.checkMandatory();
        this.jadeFilesDBLayer = new JadeFilesDBLayer(new File(hibernateConfigFile));
        this.jadeFilesHistoryDBLayer = new JadeFilesHistoryDBLayer(new File(hibernateConfigFile));
    }

    @Override
    public void logException(Exception e) {
        LOGGER.error("", e);
    }

    @Override
    public void getFileHistoryByIdFromLayer(Long id) {
        initJadeFilesDbSession();
        getFileHistoryFromDb(id);
        closeJadeFilesDbSession();
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
        } catch (ParseException e) {
            logException(e);
        }
    }

    private void getFilesHistoryFromDb() {
        try {
            List<JadeFilesHistoryDBItem> received = jadeFilesHistoryDBLayer.getHistoryFilesOrderedByTransferEnd();
            ui.setHistoryItems(received);
        } catch (ParseException e) {
            logException(e);
        }
    }

    private void initJadeFilesDbSession() {
        jadeFilesDBLayer.initSession();
    }

    private void closeJadeFilesDbSession() {
        jadeFilesDBLayer.closeSession();
    }

    private void initJadeFilesHistoryDbSession() {
        try {
            jadeFilesHistoryDBLayer.initSession();
        } catch (Exception e) {
            try {
                LOGGER.error("Exception occurred while initializing Session for the first time", e);
                jadeFilesHistoryDBLayer.initSession();
            } catch (Exception e1) {
                LOGGER.error("Exception occurred while initializing Session for the second time", e1);
            }
        }
    }

    @Override
    public void closeJadeFilesHistoryDbSession() {
        jadeFilesHistoryDBLayer.closeSession();
    }

}
