package com.sos.jade.backgroundservice.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sos.ftphistory.db.JadeFilesHistoryDBItem;

import com.sos.jade.backgroundservice.enums.Columns;
import com.vaadin.data.util.IndexedContainer;

public class JadeHistoryContainer extends IndexedContainer{
	private static final long serialVersionUID = 7685755428975400129L;
	private Map<String, Object> itemIds = new HashMap<String, Object>();

	public JadeHistoryContainer(List<JadeFilesHistoryDBItem> items){
		for(Columns col: Columns.values()){
			this.addContainerProperty(col.getName(), col.getType(), col.getDefaultValue());
		}
		for (JadeFilesHistoryDBItem item : items){
			addItem(item);
		}
	}
	
	
}
