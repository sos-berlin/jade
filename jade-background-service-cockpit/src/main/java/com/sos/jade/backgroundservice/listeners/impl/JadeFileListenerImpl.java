package com.sos.jade.backgroundservice.listeners.impl;

import static com.sos.jade.backgroundservice.BackgroundserviceUI.jadeBsOptions;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sos.ftphistory.JadeFilesHistoryFilter;
import sos.ftphistory.db.JadeFilesDBLayer;
import sos.ftphistory.db.JadeFilesHistoryDBItem;
import sos.ftphistory.db.JadeFilesHistoryDBLayer;

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
		if(jadeBsOptions.getDevel().isDevelopment()){
			// webserver runs in development environment
			if(jadeBsOptions.getWebserverType().isJetty()){
				//run on mvn jetty:run with envirmonment variables to the config file
				jadeBsOptions.hibernateConfigurationFileName.Value(jadeBsOptions.hibernateConfigurationFileName.Value());
			}else if(jadeBsOptions.getWebserverType().isTomcat()){
				//run on tomcat with the config file from the webapp folder
				jadeBsOptions.hibernateConfigurationFileName.Value(absolutePath + jadeBsOptions.hibernateConfigurationFileName.Value());
			}
		}
 		jadeBsOptions.hibernateConfigurationFileName.CheckMandatory();
		this.jadeFilesDBLayer = new JadeFilesDBLayer(jadeBsOptions.hibernateConfigurationFileName.JSFile());
		this.jadeFilesHistoryDBLayer = new JadeFilesHistoryDBLayer(jadeBsOptions.hibernateConfigurationFileName.JSFile());
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
        	List<JadeFilesHistoryDBItem> received = jadeFilesHistoryDBLayer.getHistoryFilesOrderedByTimestamp();
 			ui.setHistoryItems(received);
		} catch (ParseException e) {
			getException(e);
		}
	}

	private void initJadeFilesDbSession(){
        jadeFilesDBLayer.initSession();
	}

	private void closeJadeFilesDbSession(){
		// let some time pass before closing the actual hibernate session
		new Thread(){
			@Override
			public void run() {
				try {
					Thread.sleep(10000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				UI.getCurrent().access(new Runnable() {
					@Override
					public void run() {
				        jadeFilesDBLayer.closeSession();
					}
				});
			};
		}.start();
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
		// let some time pass before closing the actual hibernate session
		new Thread(){
			@Override
			public void run() {
				try {
					Thread.sleep(10000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				UI.getCurrent().access(new Runnable() {
					@Override
					public void run() {
				        jadeFilesHistoryDBLayer.closeSession();
						log.debug("Hibernate SESSION finally closed at " + sdf.format(new Date()) + "!");
					}
				});
			};
		}.start();
	}

}
