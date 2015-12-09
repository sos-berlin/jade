package com.sos.jade.backgroundservice.listeners.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sos.jadehistory.JadeFilesHistoryFilter;

import com.sos.jade.backgroundservice.listeners.IJadeFileListener;
import com.sos.jade.backgroundservice.view.MainView;

public class JadeFileListenerProxy extends JadeFileListenerImpl implements IJadeFileListener {

    private final String conClassName = this.getClass().getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(JadeFileListenerProxy.class);
    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSS");

    private static final long serialVersionUID = 1L;

    public JadeFileListenerProxy(MainView ui) {
        super(ui);
        LOGGER.debug(String.format("Class %1$s initialized", conClassName));
    }

    @Override
    public void logException(Exception e) {
        super.logException(e);
    }

    @Override
    public void getFileHistoryByIdFromLayer(Long id) {
        LOGGER.debug("getFileHistoryByIdFromLayer entered at " + sdf.format(new Date()));
        super.getFileHistoryByIdFromLayer(id);
        LOGGER.debug("getFileHistoryByIdFromLayer exited at " + sdf.format(new Date()));
    }

    @Override
    public void filterJadeFilesHistory(JadeFilesHistoryFilter filter) {
        LOGGER.debug("filterJadeFilesHistory entered at " + sdf.format(new Date()));
        super.filterJadeFilesHistory(filter);
        LOGGER.debug("received items count: " + super.ui.getHistoryItems().size());
        LOGGER.debug("filterJadeFilesHistory exited at " + sdf.format(new Date()));
    }

}
