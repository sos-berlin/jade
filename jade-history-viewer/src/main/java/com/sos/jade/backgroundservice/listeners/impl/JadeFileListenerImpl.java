package com.sos.jade.backgroundservice.listeners.impl;

import static com.sos.jade.backgroundservice.JADEHistoryViewerUI.hibernateConfigFile;
import static com.sos.jade.backgroundservice.JADEHistoryViewerUI.jadeBsOptions;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sos.jadehistory.JadeFilesHistoryFilter;
import sos.jadehistory.db.JadeFilesDBLayer;
import sos.jadehistory.db.JadeFilesHistoryDBItem;
import sos.jadehistory.db.JadeFilesHistoryDBLayer;

import com.sos.jade.backgroundservice.listeners.IJadeFileListener;
import com.sos.jade.backgroundservice.view.MainView;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.UI;

public class JadeFileListenerImpl implements IJadeFileListener, Serializable{
	private static final long serialVersionUID = 1L;
	private JadeFilesDBLayer jadeFilesDBLayer;
	private JadeFilesHistoryDBLayer jadeFilesHistoryDBLayer;
	public MainView ui;
	private Logger log = LoggerFactory.getLogger(JadeFileListenerImpl.class);
	private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSS");
	
	public JadeFileListenerImpl(MainView ui){
		this.ui = ui;
		String absolutePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		if(hibernateConfigFile != null && hibernateConfigFile.length() != 0){
			jadeBsOptions.hibernateConfigurationFileName.Value(hibernateConfigFile);
		}
 		jadeBsOptions.hibernateConfigurationFileName.CheckMandatory();
		this.jadeFilesDBLayer = new JadeFilesDBLayer(new File(hibernateConfigFile));
		this.jadeFilesHistoryDBLayer = new JadeFilesHistoryDBLayer(new File(hibernateConfigFile));
	}

	@Override
	public void getException(Exception e) {
		e.printStackTrace();
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
	
	private void getFilesHistoryFromDb(){
        try {
        	List<JadeFilesHistoryDBItem> received = jadeFilesHistoryDBLayer.getHistoryFilesOrderedByTransferEnd();
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
        try {
			jadeFilesHistoryDBLayer.initSession();
		} catch (Exception e) {
			try {
				log.error("Exception occurred while initializing Session for the first time" + e);
				// retry
				jadeFilesHistoryDBLayer.initSession();
			} catch (Exception e1) {
				log.error("Exception occurred while initializing Session for the second time" + e1);
			}
		}
	}

	@Override
	public void closeJadeFilesHistoryDbSession(){
		jadeFilesHistoryDBLayer.closeSession();
	}

}
