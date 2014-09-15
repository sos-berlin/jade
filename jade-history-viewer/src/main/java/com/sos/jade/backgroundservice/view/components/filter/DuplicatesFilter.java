package com.sos.jade.backgroundservice.view.components.filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sos.ftphistory.db.JadeFilesHistoryDBItem;

import com.sos.jade.backgroundservice.enums.JadeHistoryFileColumns;
import com.vaadin.data.Container;
import com.vaadin.data.Item;

/**
 * This is a Vaadin filter, to remove duplicates from the history table, which is used when all date is already received. 
 * No DB roundtrip needed. Because of that, it is faster then calling hibernate with a filter.
 * 
 * @author SP
 *
 */
public class DuplicatesFilter implements Container.Filter{
	private static final long serialVersionUID = 1L;
	private List<JadeFilesHistoryDBItem> historyItems;
	
	public DuplicatesFilter() {
	}

	public DuplicatesFilter(List<JadeFilesHistoryDBItem> historyItems) {
		this.historyItems = historyItems;
	}

	@Override
	public boolean passesFilter(Object itemId, Item item)
			throws UnsupportedOperationException {
		JadeFilesHistoryDBItem historyItem = (JadeFilesHistoryDBItem)itemId;
		return getHighestTimestampItem(historyItems, historyItem.getSosftpId()).equals(historyItem);
	}

	@Override
	public boolean appliesToProperty(Object propertyId) {
        return propertyId != null && propertyId.equals(JadeHistoryFileColumns.TRANSFER_TIMESTAMP.getName());
	}


    private JadeFilesHistoryDBItem getHighestTimestampItem(List<JadeFilesHistoryDBItem> historyItems, long id){
    	JadeFilesHistoryDBItem highestTimestampItem = null;
    	List<JadeFilesHistoryDBItem> idGroup = new ArrayList<JadeFilesHistoryDBItem>();
    	for(JadeFilesHistoryDBItem item : historyItems){
    		if(item.getSosftpId() == id){
    			idGroup.add(item);
    		}
    	}
    	Date timestamp = null;
    	for (JadeFilesHistoryDBItem historyItem : idGroup){
    		if(timestamp == null){
    			timestamp = historyItem.getTransferTimestamp();
    			highestTimestampItem = historyItem;
    		}else{
    			if(historyItem.getTransferTimestamp().after(timestamp)){
        			timestamp = historyItem.getTransferTimestamp();
        			highestTimestampItem = historyItem;
    			}
    		}
    	}
    	return highestTimestampItem;
    }

	public void setHistoryItems(List<JadeFilesHistoryDBItem> historyItems) {
		this.historyItems = historyItems;
	}
    
}
