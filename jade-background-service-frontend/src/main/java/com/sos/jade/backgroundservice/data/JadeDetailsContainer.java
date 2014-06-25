package com.sos.jade.backgroundservice.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.bcel.generic.NEW;

import sos.ftphistory.db.JadeFilesHistoryDBItem;

import com.sos.jade.backgroundservice.enums.JadeFileColumns;
import com.sos.jade.backgroundservice.enums.JadeHistoryFileColumns;
import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.StringToDateConverter;

public class JadeDetailsContainer extends IndexedContainer {
	private static final long serialVersionUID = 1L;

	private JadeBSMessages messages;
	private JadeFilesHistoryDBItem historyItem;
	private Locale locale;
	private StringToDateConverter dateConverter = new StringToDateConverter(); 

	public JadeDetailsContainer(JadeFilesHistoryDBItem historyItem, JadeBSMessages messages) {
		this.historyItem = historyItem;
		this.messages = messages;

		addContainerProperties();
		addItems(convertHistoryItemToDetailsList(historyItem));
	}

	private List<JadeHistoryDetailItem> convertHistoryItemToDetailsList(JadeFilesHistoryDBItem historyItem){
		List<JadeHistoryDetailItem> detailItems = new ArrayList<JadeHistoryDetailItem>();
		// HistoryItem
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.GUID.getName(),
				historyItem.getGuid(), 
				"DetailLayout.guid"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.OPERATION.getName(), 
				historyItem.getOperation(), 
				"DetailLayout.operation"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.TRANSFER_TIMESTAMP.getName(), 
				historyItem.getTransferTimestamp(), 
				"DetailLayout.transferTimestamp"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.PID.getName(), 
				historyItem.getPid(), 
				"DetailLayout.pid"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.PPID.getName(), 
				historyItem.getPPid(), 
				"DetailLayout.ppid"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.TARGET_HOST.getName(), 
				historyItem.getTargetHost(), 
				"DetailLayout.targetHost"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.TARGET_HOST_IP.getName(), 
				historyItem.getTargetHostIp(), 
				"DetailLayout.targetHostIp"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.TARGET_USER.getName(), 
				historyItem.getTargetUser(), 
				"DetailLayout.targetUser"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.TARGET_DIR.getName(), 
				historyItem.getTargetDir(), 
				"DetailLayout.targetDirectory"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.TARGET_FILENAME.getName(), 
				historyItem.getTargetFilename(), 
				"DetailLayout.targetFilename"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.PROTOCOL.getName(), 
				historyItem.getProtocol(), 
				"DetailLayout.protocol"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.PORT.getName(), 
				historyItem.getPort(), 
				"DetailLayout.port"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.STATUS.getName(), 
				historyItem.getStatus(), 
				"DetailLayout.status"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.LAST_ERROR_MESSAGE.getName(), 
				historyItem.getLastErrorMessage(), 
				"DetailLayout.lastErrorMessage"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.LOG_FILENAME.getName(), 
				historyItem.getLogFilename(), 
				"DetailLayout.logFilename"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.JUMP_HOST.getName(), 
				historyItem.getJumpHost(), 
				"DetailLayout.jumpHost"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.JUMP_HOST_IP.getName(), 
				historyItem.getJumpHostIp(), 
				"DetailLayout.jumpHostIp"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.JUMP_USER.getName(), 
				historyItem.getJumpUser(), 
				"DetailLayout.jumpUser"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.JUMP_PROTOCOL.getName(), 
				historyItem.getJumpProtocol(), 
				"DetailLayout.jumpProtocol"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.JUMP_PORT.getName(), 
				historyItem.getJumpPort(), 
				"DetailLayout.jumpPort"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.CREATED.getName(), 
				historyItem.getCreated(), 
				"DetailLayout.fileHistoryCreated"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.CREATED_BY.getName(), 
				historyItem.getCreatedBy(), 
				"DetailLayout.fileHistoryCreatedBy"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.MODIFIED.getName(), 
				historyItem.getModified(), 
				"DetailLayout.fileHistoryModified"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.MODIFIED_BY.getName(), 
				historyItem.getModifiedBy(), 
				"DetailLayout.fileHistoryModifiedBy"));
		// FileItem
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeFileColumns.MANDATOR.getName(), 
				historyItem.getJadeFilesDBItem().getMandator(), 
				"DetailLayout.mandator"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeFileColumns.SOURCE_HOST.getName(), 
				historyItem.getJadeFilesDBItem().getSourceHost(), 
				"DetailLayout.sourceHost"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeFileColumns.SOURCE_HOST_IP.getName(), 
				historyItem.getJadeFilesDBItem().getSourceHostIp(), 
				"DetailLayout.sourceHostIp"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeFileColumns.SOURCE_USER.getName(), 
				historyItem.getJadeFilesDBItem().getSourceUser(), 
				"DetailLayout.sourceUser"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeFileColumns.SOURCE_DIR.getName(), 
				historyItem.getJadeFilesDBItem().getSourceDir(), 
				"DetailLayout.sourceDirectory"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeFileColumns.SOURCE_FILENAME.getName(), 
				historyItem.getJadeFilesDBItem().getSourceFilename(), 
				"DetailLayout.sourceFilename"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeFileColumns.MD5.getName(), 
				historyItem.getJadeFilesDBItem().getMd5(), 
				"DetailLayout.md5"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeFileColumns.FILE_SIZE.getName(), 
				historyItem.getJadeFilesDBItem().getFileSize(), 
				"DetailLayout.fileSize"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeFileColumns.CREATED.getName(), 
				historyItem.getJadeFilesDBItem().getCreated(), 
				"DetailLayout.fileCreated"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeFileColumns.CREATED_BY.getName(), 
				historyItem.getJadeFilesDBItem().getCreatedBy(), 
				"DetailLayout.fileCreatedBy"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeFileColumns.JADE_FILE_MODIFIED.getName(), 
				historyItem.getJadeFilesDBItem().getModified(), 
				"DetailLayout.fileModified"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeFileColumns.JADE_FILE_MODIFIED_BY.getName(), 
				historyItem.getJadeFilesDBItem().getModifiedBy(), 
				"DetailLayout.fileModifiedBy"));
		return detailItems;
	}
	
	private void addItems(List<JadeHistoryDetailItem> detailItems){
		for(JadeHistoryDetailItem detailItem : detailItems){
			addItem(detailItem);
			Item item = getItem(detailItem);
			if (item != null) {
				Property key = item.getItemProperty("key");
				key.setValue(detailItem.getDisplayName());
				Property value = item.getItemProperty("value");
				value.setValue(detailItem.getValue());
				if(detailItem.getValue() instanceof Date){
					value.setValue(dateConverter.convertToPresentation((Date)detailItem.getValue(), String.class, locale));
				}
				Property messageKey = item.getItemProperty("messageKey");
				messageKey.setValue(detailItem.getMessageKey());
			}
		}
	}
	
	private void addContainerProperties(){
		this.addContainerProperty("key", String.class, "displayName");
		this.addContainerProperty("value", Object.class, null);
		this.addContainerProperty("displayName", String.class, null);
		this.addContainerProperty("messageKey", String.class, null);
	}
	
	@SuppressWarnings("unchecked")
	public void updateLocale(Locale locale){
		// update key values and values of type Date
		for(Object itemId : this.getItemIds()){
			Item item = getItem(itemId); 
			String messageKey = ((JadeHistoryDetailItem)itemId).getMessageKey();
			item.getItemProperty("key").setValue(messages.getValue(messageKey, locale));
			if(((messageKey.toLowerCase().contains("created") || messageKey.toLowerCase().contains("modified")) 
					&& !messageKey.contains("By")) || messageKey.toLowerCase().contains("timestamp")){
				item.getItemProperty("value").setValue(dateConverter.convertToPresentation(
						(Date)((JadeHistoryDetailItem)itemId).getValue(), String.class, locale));
			}
		}
		
	}
	
	public JadeBSMessages getMessages() {
		return messages;
	}

	public void setMessages(JadeBSMessages messages) {
		this.messages = messages;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
}
