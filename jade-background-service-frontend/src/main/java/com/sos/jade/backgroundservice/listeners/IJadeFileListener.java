package com.sos.jade.backgroundservice.listeners;

import sos.ftphistory.JadeFilesFilter;

public interface IJadeFileListener {

	/**
	 * filters the JadeFilesDBItems with the given JadeFileFilter
	 * @param filter the JadeFileFilter
	 */
	void filterJadeFiles(JadeFilesFilter filter);
	
	/**
	 * gets the JadeFilesHistoryDBItems with the given JadeFileDBItem.id
	 * @param id the JadeFileDBItem id
	 */
	void getFileHistoryByIdFromLayer(Long id);

	void getException(Exception e);
}
