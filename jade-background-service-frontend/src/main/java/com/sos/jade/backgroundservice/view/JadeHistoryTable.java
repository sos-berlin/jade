package com.sos.jade.backgroundservice.view;

import java.util.List;

import sos.ftphistory.db.JadeFilesHistoryDBItem;

import com.sos.jade.backgroundservice.data.JadeHistoryBeanContainer;
import com.sos.jade.backgroundservice.data.JadeHistoryContainer;
import com.sos.jade.backgroundservice.enums.Columns;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;

public class JadeHistoryTable extends Table{

	private static final long serialVersionUID = 2134585331362934124L;
	private List<JadeFilesHistoryDBItem> historyItems;
//	private JadeHistoryContainer container;
	private BeanItemContainer<JadeFilesHistoryDBItem> container;
	public JadeHistoryTable(List<JadeFilesHistoryDBItem> historyItems){
		this.historyItems = historyItems;
		init();
	}
	
	private void init(){
		this.setContainerDataSource(container = (new JadeHistoryBeanContainer()).createJadeFilesHistoryContainer(historyItems));
		setSizeUndefined();;
		setSelectable(true);
		setImmediate(true);
		setEditable(true);
		setColumnReorderingAllowed(true);
		for (Columns col : Columns.values()){
			setColumnHeader(col.name(), col.getName());
		}
		

	}
}
