package com.sos.jade.backgroundservice.listeners;

import sos.jadehistory.JadeFilesHistoryFilter;

public interface IJadeFileListener {

    void getFileHistoryByIdFromLayer(Long id);

    void filterJadeFilesHistory(JadeFilesHistoryFilter filter);

    void logException(Exception e);

}
