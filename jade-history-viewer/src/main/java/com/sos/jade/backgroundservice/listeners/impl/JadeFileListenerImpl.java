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
import sos.jadehistory.db.JadeHistoryDBLayer;

import com.sos.hibernate.classes.SOSHibernateConnection;
import com.sos.jade.backgroundservice.listeners.IJadeFileListener;
import com.sos.jade.backgroundservice.view.MainView;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.UI;

public class JadeFileListenerImpl implements IJadeFileListener, Serializable{
	private static final long serialVersionUID = 1L;
//	private JadeFilesDBLayer jadeFilesDBLayer;
//	private JadeFilesHistoryDBLayer jadeFilesHistoryDBLayer;
	public MainView ui;
	private Logger log = LoggerFactory.getLogger(JadeFileListenerImpl.class);
	private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSS");
	private JadeHistoryDBLayer historyDBLayer;
	private SOSHibernateConnection connection;
	
	public JadeFileListenerImpl(MainView ui){
		this.ui = ui;
		String absolutePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		if(hibernateConfigFile != null && hibernateConfigFile.length() != 0){
			jadeBsOptions.hibernateConfigurationFileName.Value(hibernateConfigFile);
		}
 		jadeBsOptions.hibernateConfigurationFileName.CheckMandatory();
 		connect();
//		this.jadeFilesDBLayer = new JadeFilesDBLayer(new File(hibernateConfigFile));
//		this.jadeFilesHistoryDBLayer = new JadeFilesHistoryDBLayer(new File(hibernateConfigFile));
	}

	@Override
	public void getException(Exception e) {
		e.printStackTrace();
	}

	private void connect(){
 		try {
			this.connection = new SOSHibernateConnection(hibernateConfigFile);
			connection.connect();
	 		// TODO: DBLayer mit connection instanziieren
			this.historyDBLayer = new JadeHistoryDBLayer(connection);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void disconnect(){
		connection.disconnect();
	}
	
	@Override
	public void getFileHistoryByIdFromLayer(Long id) {
		connect();
		try {
			List<JadeFilesHistoryDBItem> received = this.historyDBLayer.getFilesHistoryById(id);
			ui.setHistoryItems(received);
		} catch (Exception e) {
			getException(e);
		}
		disconnect();
	}
	
	@Override
	public void filterJadeFilesHistory(JadeFilesHistoryFilter filter) {
		connect();
		if(filter != null){
			this.historyDBLayer.setFilesHistoryFilter(filter);
		}
        try {
        	List<JadeFilesHistoryDBItem> received = historyDBLayer.getHistoryFilesOrderedByTransferEnd();
 			ui.setHistoryItems(received);
		} catch (Exception e) {
			getException(e);
		}
		disconnect();
	}
	
}
