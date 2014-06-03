package com.sos.jade.backgroundservice.listeners.impl;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

import com.sos.jade.backgroundservice.listeners.IJadeFileListener;
import com.sos.jade.backgroundservice.view.MainView;
import com.vaadin.server.VaadinService;

import sos.ftphistory.JadeFilesFilter;
import sos.ftphistory.db.JadeFilesDBItem;
import sos.ftphistory.db.JadeFilesDBLayer;
import sos.ftphistory.db.JadeFilesHistoryDBItem;

public class JadeFileListenerImpl implements IJadeFileListener, Serializable{
	private static final long serialVersionUID = 1L;
	private String absolutePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	private String configurationFilename = absolutePath + "/WEB-INF/classes/hibernate.cfg.xml";
	private JadeFilesDBLayer jadeFilesDBLayer;
	private File configFile;
	private MainView ui;
	
	public JadeFileListenerImpl(MainView ui){
		this.ui = ui;
		this.configFile = new File(configurationFilename);
		this.jadeFilesDBLayer = new JadeFilesDBLayer(configFile);
	}

	@Override
	public void getException(Exception e) {
		e.printStackTrace();
	}

	@Override
	public void filterJadeFiles(JadeFilesFilter filter) {
		if(filter != null){
			this.jadeFilesDBLayer.setFilter(filter);
		}
		getFilteredFiles();
	}

	@Override
	public void getFileHistoryByIdFromLayer(Long id) {
		initDbSession();
		getFileHistoryFromDb(id);
		closeDbSession();
	}
	
	private void getFilteredFiles(){
		initDbSession();
		getFilesFromDb();
		closeDbSession();
	}
	
	private void getFileHistoryFromDb(Long id){
		try {
			List<JadeFilesHistoryDBItem> received = this.jadeFilesDBLayer.getFilesHistoryById(id);
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
