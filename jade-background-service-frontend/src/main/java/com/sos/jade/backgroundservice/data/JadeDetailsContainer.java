package com.sos.jade.backgroundservice.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sos.ftphistory.db.JadeFilesHistoryDBItem;

import com.sos.jade.backgroundservice.enums.JadeFileColumns;
import com.sos.jade.backgroundservice.enums.JadeHistoryFileColumns;
import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.converter.StringToDateConverter;

public class JadeDetailsContainer extends IndexedContainer {
	private static final long serialVersionUID = 1L;

	private JadeBSMessages messages;
	private JadeFilesHistoryDBItem historyItem;
	private StringToDateConverter dateConverter = new StringToDateConverter(); 

	public JadeDetailsContainer(JadeFilesHistoryDBItem historyItem, JadeBSMessages messages) {
		this.historyItem = historyItem;
		this.messages = messages;

		addContainerProperties();
		addItems(convertHistoryItemToDetailsList(historyItem));
	}

	private List<JadeHistoryDetailItem> convertHistoryItemToDetailsList(JadeFilesHistoryDBItem historyItem){
		List<JadeHistoryDetailItem> detailItems = new ArrayList<JadeHistoryDetailItem>();
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.STATUS.getName(), 
				historyItem.getStatus(), 
				"DetailLayout.status"));
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeHistoryFileColumns.GUID.getName(),
				historyItem.getGuid(), 
				"DetailLayout.guid"));
		if (historyItem.getLastErrorMessage() != null && !"NULL".equals(historyItem.getLastErrorMessage())){
			detailItems.add(new JadeHistoryDetailItem(this.messages,
					JadeHistoryFileColumns.LAST_ERROR_MESSAGE.getName(), 
					historyItem.getLastErrorMessage(), 
					"DetailLayout.lastErrorMessage"));
		}else{
			detailItems.add(new JadeHistoryDetailItem(this.messages,
					JadeHistoryFileColumns.LAST_ERROR_MESSAGE.getName(), 
					messages.getValue("DetailLayout.none"), 
					"DetailLayout.lastErrorMessage"));
		}
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
		if(historyItem.getLogFilename() != null && !"NULL".equals(historyItem.getLogFilename())){
			detailItems.add(new JadeHistoryDetailItem(this.messages,
					JadeHistoryFileColumns.LOG_FILENAME.getName(), 
					historyItem.getLogFilename(), 
					"DetailLayout.logFilename"));
		}else{
			detailItems.add(new JadeHistoryDetailItem(this.messages,
					JadeHistoryFileColumns.LOG_FILENAME.getName(), 
					messages.getValue("DetailLayout.notSet"), 
					"DetailLayout.logFilename"));
		}
		detailItems.add(new JadeHistoryDetailItem(this.messages,
				JadeFileColumns.MANDATOR.getName(), 
				historyItem.getJadeFilesDBItem().getMandator(), 
				"DetailLayout.mandator"));
		if (historyItem.getJumpHost() != null && !"NULL".equals(historyItem.getJumpHost())){
			detailItems.add(new JadeHistoryDetailItem(this.messages,
					JadeHistoryFileColumns.JUMP_HOST.getName(), 
					historyItem.getJumpHost(), 
					"DetailLayout.jumpHost"));
		}else{
			detailItems.add(new JadeHistoryDetailItem(this.messages,
					JadeHistoryFileColumns.JUMP_HOST.getName(), 
					messages.getValue("DetailLayout.notSet"), 
					"DetailLayout.jumpHost"));
		}
		if (historyItem.getJumpHostIp() != null && !"NULL".equals(historyItem.getJumpHostIp())){
			detailItems.add(new JadeHistoryDetailItem(this.messages,
					JadeHistoryFileColumns.JUMP_HOST_IP.getName(), 
					historyItem.getJumpHostIp(), 
					"DetailLayout.jumpHostIp"));
		}else{
			detailItems.add(new JadeHistoryDetailItem(this.messages,
					JadeHistoryFileColumns.JUMP_HOST_IP.getName(), 
					messages.getValue("DetailLayout.notSet"), 
					"DetailLayout.jumpHostIp"));
		}
		if (historyItem.getJumpUser() != null && !"NULL".equals(historyItem.getJumpUser())){
			detailItems.add(new JadeHistoryDetailItem(this.messages,
					JadeHistoryFileColumns.JUMP_USER.getName(), 
					historyItem.getJumpUser(), 
					"DetailLayout.jumpUser"));
		}else{
			detailItems.add(new JadeHistoryDetailItem(this.messages,
					JadeHistoryFileColumns.JUMP_USER.getName(), 
					messages.getValue("DetailLayout.notSet"), 
					"DetailLayout.jumpUser"));
		}
		if(historyItem.getJumpProtocol() != null && !"NULL".equals(historyItem.getJumpProtocol())){
			detailItems.add(new JadeHistoryDetailItem(this.messages,
					JadeHistoryFileColumns.JUMP_PROTOCOL.getName(), 
					historyItem.getJumpProtocol(), 
					"DetailLayout.jumpProtocol"));
		}else{
			detailItems.add(new JadeHistoryDetailItem(this.messages,
					JadeHistoryFileColumns.JUMP_PROTOCOL.getName(), 
					messages.getValue("DetailLayout.notSet"), 
					"DetailLayout.jumpProtocol"));
		}
		if(historyItem.getJumpPort() != null && !"NULL".equals(historyItem.getJumpPort())){
			detailItems.add(new JadeHistoryDetailItem(this.messages,
					JadeHistoryFileColumns.JUMP_PORT.getName(), 
					historyItem.getJumpPort(), 
					"DetailLayout.jumpPort"));
		}else{
			detailItems.add(new JadeHistoryDetailItem(this.messages,
					JadeHistoryFileColumns.JUMP_PORT.getName(), 
					messages.getValue("DetailLayout.notSet"), 
					"DetailLayout.jumpPort"));
		}
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
	
	@SuppressWarnings("unchecked")
	private void addItems(List<JadeHistoryDetailItem> detailItems){
		for(JadeHistoryDetailItem detailItem : detailItems){
			addItem(detailItem);
			Item item = getItem(detailItem);
			if (item != null) {
				Property<String> key = item.getItemProperty("key");
				key.setValue(detailItem.getDisplayName());
				Property<Object> value = item.getItemProperty("value");
				value.setValue(detailItem.getValue());
				if(detailItem.getValue() instanceof Date){
					value.setValue(dateConverter.convertToPresentation((Date)detailItem.getValue(), String.class, messages.getBundle().getLocale()));
				}
				Property<String> messageKey = item.getItemProperty("messageKey");
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
		// update key values, empty values and values of type Date
		for(Object itemId : this.getItemIds()){
			Item item = getItem(itemId); 
			String messageKey = ((JadeHistoryDetailItem)itemId).getMessageKey();
			item.getItemProperty("key").setValue(messages.getValue(messageKey, locale));
			if(((messageKey.toLowerCase().contains("created") || messageKey.toLowerCase().contains("modified")) 
					&& !messageKey.contains("By")) || messageKey.toLowerCase().contains("timestamp")){
				item.getItemProperty("value").setValue(dateConverter.convertToPresentation(
						(Date)((JadeHistoryDetailItem)itemId).getValue(), String.class, locale));
			}else if("DetailLayout.lastErrorMessage".equalsIgnoreCase(messageKey) && "null".equalsIgnoreCase(this.historyItem.getLastErrorMessage())){
				item.getItemProperty("value").setValue(messages.getValue("DetailLayout.none", locale));
			}else if(("DetailLayout.jumpHost".equalsIgnoreCase(messageKey) && "null".equalsIgnoreCase(this.historyItem.getJumpHost())) ||
					("DetailLayout.jumpHostIp".equalsIgnoreCase(messageKey) && "null".equalsIgnoreCase(this.historyItem.getJumpHostIp())) ||
					("DetailLayout.jumpUser".equalsIgnoreCase(messageKey) && "null".equalsIgnoreCase(this.historyItem.getJumpUser())) ||
					("DetailLayout.logFilename".equalsIgnoreCase(messageKey) && "null".equalsIgnoreCase(this.historyItem.getLogFilename())) ||
					("DetailLayout.jumpProtocol".equalsIgnoreCase(messageKey) && "null".equalsIgnoreCase(this.historyItem.getJumpProtocol())) ||
					("DetailLayout.jumpPort".equalsIgnoreCase(messageKey) && this.historyItem.getJumpPort() != null)){
				item.getItemProperty("value").setValue(messages.getValue("DetailLayout.notSet", locale));
			}
		}
		
	}
	
}
