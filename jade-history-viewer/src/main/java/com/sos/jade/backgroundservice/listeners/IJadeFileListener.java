package com.sos.jade.backgroundservice.listeners;

import sos.ftphistory.JadeFilesHistoryFilter;

public interface IJadeFileListener {

    void getFileHistoryByIdFromLayer(Long id);

    void filterJadeFilesHistory(JadeFilesHistoryFilter filter);

    void logException(Exception e);

    void closeJadeFilesHistoryDbSession();
}
