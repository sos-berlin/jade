package com.sos.jade.backgroundservice.listeners.impl;

import static com.sos.jade.backgroundservice.BackgroundserviceUI.objOptions;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

import sos.ftphistory.JadeFilesFilter;
import sos.ftphistory.db.JadeFilesDBItem;
import sos.ftphistory.db.JadeFilesDBLayer;
import sos.ftphistory.db.JadeFilesHistoryDBItem;

import com.sos.jade.backgroundservice.listeners.IJadeFileListener;
import com.sos.jade.backgroundservice.view.MainView;
import com.vaadin.server.VaadinService;

public class JadeFileListenerImpl implements IJadeFileListener, Serializable{
	private static final long serialVersionUID = 1L;
	private final JadeFilesDBLayer jadeFilesDBLayer;
	private final File configFile;
	private final MainView ui;
	
	public JadeFileListenerImpl(final MainView ui){
		this.ui = ui;
		String absolutePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		String configurationFilename = absolutePath + objOptions.Hibernate_Configuration_File_Name.Value();
		configFile = new File(configurationFilename);
		jadeFilesDBLayer = new JadeFilesDBLayer(configFile);
	}

	@Override
	public void getException(final Exception e) {
		e.printStackTrace();
	}

	@Override
	public void filterJadeFiles(final JadeFilesFilter filter) {
		if(filter != null){
			jadeFilesDBLayer.setFilter(filter);
		}
		getFilteredFiles();
	}

	@Override
	public void getFileHistoryByIdFromLayer(final Long id) {
		initDbSession();
		getFileHistoryFromDb(id);
		closeDbSession();
	}
	
	private void getFilteredFiles(){
		initDbSession();
		getFilesFromDb();
		closeDbSession();
	}
	
	private void getFileHistoryFromDb(final Long id){
		try {
			List<JadeFilesHistoryDBItem> received = jadeFilesDBLayer.getFilesHistoryById(id);
			ui.setHistoryItems(received);
		} catch (ParseException e) {
			getException(e);
		}
	}
	
	private void getFilesFromDb(){
        try {
        	List<JadeFilesDBItem> received = jadeFilesDBLayer.getFiles();
 			ui.setFileItems(received);
		} catch (ParseException e) {
			getException(e);
		}
	}

	private void initDbSession(){
        jadeFilesDBLayer.initSession();
	}

	private void closeDbSession(){
        jadeFilesDBLayer.closeSession();
	}
}
