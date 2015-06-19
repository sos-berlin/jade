package com.sos.jade.backgroundservice.data;

import java.util.List;

import sos.jadehistory.db.JadeFilesHistoryDBItem;

import com.sos.jade.backgroundservice.enums.JadeFileColumns;
import com.sos.jade.backgroundservice.enums.JadeHistoryFileColumns;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Label;

public class JadeFilesHistoryContainer extends IndexedContainer{
	private List<JadeFilesHistoryDBItem> historyItems;
	private static final long serialVersionUID = 1L;

	public JadeFilesHistoryContainer(List<JadeFilesHistoryDBItem> historyItems) {
		this.historyItems = historyItems;
		addContainerProperties();
		addItems(historyItems);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addItems(List<JadeFilesHistoryDBItem> historyItems){
		// for each JadeFilesHistoryDBItem add one item to the container with the given properties
		for (JadeFilesHistoryDBItem historyItem : historyItems){
			try {
				historyItem.getJadeFilesDBItem().getMandator();
				addItem(historyItem);
				Item item = getItem(historyItem);
				Property status = item.getItemProperty(JadeHistoryFileColumns.STATUS.getName());
				// currently use is a css-styled Label
				if("success".equals(historyItem.getStatus())){
					status.setValue(new StatusSuccessLabel());
				}else if("error".equals(historyItem.getStatus())){
					status.setValue(new StatusErrorLabel());
				}else if("transferring".equals(historyItem.getStatus())){
					status.setValue(new StatusTransferLabel());
				}
				Property mandator = item.getItemProperty(JadeFileColumns.MANDATOR.getName());
				mandator.setValue(historyItem.getJadeFilesDBItem().getMandator());
				Property transferTimestamp = item.getItemProperty(JadeHistoryFileColumns.TRANSFER_TIMESTAMP.getName());
				transferTimestamp.setValue(historyItem.getTransferTimestamp());
				Property operation = item.getItemProperty(JadeHistoryFileColumns.OPERATION.getName());
				operation.setValue(historyItem.getOperation());
				Property protocol = item.getItemProperty(JadeHistoryFileColumns.PROTOCOL.getName());
				protocol.setValue(historyItem.getProtocol());
				Property targetFilename = item.getItemProperty(JadeHistoryFileColumns.TARGET_FILENAME.getName());
				targetFilename.setValue(historyItem.getTargetFilename());
				Property fileSize = item.getItemProperty(JadeFileColumns.FILE_SIZE.getName());
				fileSize.setValue(historyItem.getJadeFilesDBItem().getFileSize());
				Property sourceHost = item.getItemProperty(JadeFileColumns.SOURCE_HOST.getName());
				sourceHost.setValue(historyItem.getJadeFilesDBItem().getSourceHost());
				Property targetHost = item.getItemProperty(JadeHistoryFileColumns.TARGET_HOST.getName());
				targetHost.setValue(historyItem.getTargetHost());
			} catch (Exception e) {
				continue;
			}
		}
	}
	
	private void addContainerProperties(){
		addContainerProperty(JadeHistoryFileColumns.STATUS.getName(), 
				Label.class,
				new Label());
		addContainerProperty(JadeFileColumns.MANDATOR.getName(), 
				JadeFileColumns.MANDATOR.getType(), 
				JadeFileColumns.MANDATOR.getDefaultValue());
		addContainerProperty(JadeHistoryFileColumns.TRANSFER_TIMESTAMP.getName(), 
				JadeHistoryFileColumns.TRANSFER_TIMESTAMP.getType(), 
				JadeHistoryFileColumns.TRANSFER_TIMESTAMP.getDefaultValue());
		addContainerProperty(JadeHistoryFileColumns.OPERATION.getName(), 
				JadeHistoryFileColumns.OPERATION.getType(), 
				JadeHistoryFileColumns.OPERATION.getDefaultValue());
		addContainerProperty(JadeHistoryFileColumns.PROTOCOL.getName(), 
				JadeHistoryFileColumns.PROTOCOL.getType(), 
				JadeHistoryFileColumns.PROTOCOL.getDefaultValue());
		addContainerProperty(JadeHistoryFileColumns.TARGET_FILENAME.getName(), 
				JadeHistoryFileColumns.TARGET_FILENAME.getType(), 
				JadeHistoryFileColumns.TARGET_FILENAME.getDefaultValue());
		addContainerProperty(JadeFileColumns.FILE_SIZE.getName(), 
				JadeFileColumns.FILE_SIZE.getType(), 
				JadeFileColumns.FILE_SIZE.getDefaultValue());
		addContainerProperty(JadeFileColumns.SOURCE_HOST.getName(), 
				JadeFileColumns.SOURCE_HOST.getType(), 
				JadeFileColumns.SOURCE_HOST.getDefaultValue());
		addContainerProperty(JadeHistoryFileColumns.TARGET_HOST.getName(), 
				JadeHistoryFileColumns.TARGET_HOST.getType(), 
				JadeHistoryFileColumns.TARGET_HOST.getDefaultValue());
	}
	
	private class StatusSuccessLabel extends Label{
		private static final long serialVersionUID = 1L;
		public StatusSuccessLabel() {
			setWidth(16.0f, Unit.PIXELS);
			setHeight(16.0f, Unit.PIXELS);
			setStyleName("jadeStatusSuccessLabel");
		}
	}
	
	private class StatusErrorLabel extends Label{
		private static final long serialVersionUID = 1L;
		public StatusErrorLabel() {
			setWidth(16.0f, Unit.PIXELS);
			setHeight(16.0f, Unit.PIXELS);
			setStyleName("jadeStatusErrorLabel");
		}
	}
	
	private class StatusTransferLabel extends Label{
		private static final long serialVersionUID = 1L;
		public StatusTransferLabel() {
			setWidth(16.0f, Unit.PIXELS);
			setHeight(16.0f, Unit.PIXELS);
			setStyleName("jadeStatusTransferLabel");
		}
	}
	
    @SuppressWarnings("unchecked")
		public void updateItem(Object itemId){
        if (this.containsId(itemId)){
            Item newItem = (Item)itemId;
            Item oldItem = this.getItem(itemId);
            if (oldItem != null){
                for (Object property: getContainerPropertyIds()){
                    oldItem.getItemProperty(property).setValue(newItem.getItemProperty(property).getValue());
                }
            }
        }
    }
    
}
