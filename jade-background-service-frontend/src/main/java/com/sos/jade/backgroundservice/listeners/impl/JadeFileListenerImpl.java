package com.sos.jade.backgroundservice.listeners.impl;

import static com.sos.jade.backgroundservice.BackgroundserviceUI.objOptions;

import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

import sos.ftphistory.JadeFilesFilter;
import sos.ftphistory.JadeFilesHistoryFilter;
import sos.ftphistory.db.JadeFilesDBItem;
import sos.ftphistory.db.JadeFilesDBLayer;
import sos.ftphistory.db.JadeFilesHistoryDBItem;
import sos.ftphistory.db.JadeFilesHistoryDBLayer;

import com.sos.jade.backgroundservice.listeners.IJadeFileListener;
import com.sos.jade.backgroundservice.view.MainView;
import com.vaadin.server.VaadinService;

public class JadeFileListenerImpl implements IJadeFileListener, Serializable{
	private static final long serialVersionUID = 1L;
	private JadeFilesDBLayer jadeFilesDBLayer;
	private JadeFilesHistoryDBLayer jadeFilesHistoryDBLayer;
	public MainView ui;
	
	public JadeFileListenerImpl(MainView ui){
		this.ui = ui;
		String absolutePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		objOptions.hibernateConfigurationFileName.Value(absolutePath + objOptions.hibernateConfigurationFileName.Value());
		objOptions.hibernateConfigurationFileName.CheckMandatory();
		this.jadeFilesDBLayer = new JadeFilesDBLayer(objOptions.hibernateConfigurationFileName.JSFile());
		this.jadeFilesHistoryDBLayer = new JadeFilesHistoryDBLayer(objOptions.hibernateConfigurationFileName.JSFile());
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
		initJadeFilesDbSession();
		getFileHistoryFromDb(id);
		closeJadeFilesDbSession();
	}
	
	@Override
	public void filterJadeFilesHistory(JadeFilesHistoryFilter filter) {
		if(filter != null){
			this.jadeFilesHistoryDBLayer.setFilter(filter);
		}
		getFilteredFilesHistory();
	}
	
	private void getFilteredFiles(){
		initJadeFilesDbSession();
		getFilesFromDb();
		closeJadeFilesDbSession();
	}
	
	private void getFilteredFilesHistory(){
		initJadeFilesHistoryDbSession();
		getFilesHistoryFromDb();
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

	private void getFilesHistoryFromDb(){
        try {
        	List<JadeFilesHistoryDBItem> received = jadeFilesHistoryDBLayer.getHistoryFiles();
 			ui.setHistoryItems(received);
		} catch (ParseException e) {
			getException(e);
		}
	}

	private void initJadeFilesDbSession(){
        jadeFilesDBLayer.initSession();
	}

	private void closeJadeFilesDbSession(){
        jadeFilesDBLayer.closeSession();
	}

	private void initJadeFilesHistoryDbSession(){
        jadeFilesHistoryDBLayer.initSession();
	}

	@Override
	public void closeJadeFilesHistoryDbSession(){
        jadeFilesHistoryDBLayer.closeSession();
	}

}
