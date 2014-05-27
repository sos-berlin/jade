package com.sos.jade.backgroundservice.view;

import java.util.ArrayList;
import java.util.List;

import sos.ftphistory.db.JadeFilesHistoryDBItem;

import com.sos.jade.backgroundservice.data.JadeHistoryBeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;

public class JadeFilesHistoryTable extends Table{

	private static final long serialVersionUID = 2134585331362934124L;
	private List<JadeFilesHistoryDBItem> historyItems;
	private BeanItemContainer<JadeFilesHistoryDBItem> container;

	public JadeFilesHistoryTable(){
		this.historyItems = new ArrayList<JadeFilesHistoryDBItem>();
		init();
	}
	
	public JadeFilesHistoryTable(List<JadeFilesHistoryDBItem> historyItems){
		this.historyItems = historyItems;
		init();
	}
	
	private void init(){
		if (historyItems != null && historyItems.size() > 0){
			this.setContainerDataSource(this.container = (new JadeHistoryBeanContainer()).createJadeFilesHistoryContainer(historyItems));
		}
		setSizeUndefined();
		setSelectable(true);
		setImmediate(true);
		setEditable(false);
		setColumnReorderingAllowed(true);
//		for (JadeHistoryFileColumns col : JadeHistoryFileColumns.values()){
//			setColumnHeader(col.name(), col.getName());
//		}
	}
	
	public void populateDatasource(List<JadeFilesHistoryDBItem> historyItems){
		this.historyItems = historyItems;
		this.setContainerDataSource(this.container = (new JadeHistoryBeanContainer()).createJadeFilesHistoryContainer(this.historyItems));
	}
}
