package com.sos.jade.backgroundservice.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sos.jadehistory.db.JadeFilesHistoryDBItem;

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
    private static final String MESSAGE_RESOURCE_BASE = "JadeMenuBar.";
    private static final String MESSAGE_RESOURCE_FILE = "file.";
    private static final String MESSAGE_RESOURCE_HISTORY = "fileHistory.";
    private static final String DETAIL_LAYOUT_NOT_SET = "DetailLayout.notSet";
    private static final String RECEIVED_VALUE = "Received value of {} is null, setting default value";
    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String DISPLAY_NAME = "displayName";
    private static final String MESSAGE_KEY = "messageKey";
    private static final Logger LOGGER = LoggerFactory.getLogger(JadeDetailsContainer.class);

    public JadeDetailsContainer(JadeFilesHistoryDBItem historyItem, JadeBSMessages messages) {
        this.historyItem = historyItem;
        this.messages = messages;

        addContainerProperties();
        addItems(convertHistoryItemToDetailsList(historyItem));
    }

    private List<JadeHistoryDetailItem> convertHistoryItemToDetailsList(JadeFilesHistoryDBItem historyItem) {
        List<JadeHistoryDetailItem> detailItems = new ArrayList<JadeHistoryDetailItem>();
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.STATUS.getName(), historyItem.getStatus(), MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.STATUS.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.GUID.getName(), historyItem.getGuid(), MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.GUID.getName()));
        if (historyItem.getLastErrorMessage() != null && !"NULL".equals(historyItem.getLastErrorMessage())) {
            detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.LAST_ERROR_MESSAGE.getName(), historyItem.getLastErrorMessage(), MESSAGE_RESOURCE_BASE
                    + JadeHistoryFileColumns.LAST_ERROR_MESSAGE.getName()));
        } else {
            detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.LAST_ERROR_MESSAGE.getName(), messages.getValue("DetailLayout.none"), MESSAGE_RESOURCE_BASE
                    + JadeHistoryFileColumns.LAST_ERROR_MESSAGE.getName()));
            LOGGER.debug(RECEIVED_VALUE, JadeHistoryFileColumns.LAST_ERROR_MESSAGE.getName());
        }
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeFileColumns.SOURCE_HOST.getName(), historyItem.getJadeFilesDBItem().getSourceHost(), MESSAGE_RESOURCE_BASE
                + JadeFileColumns.SOURCE_HOST.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeFileColumns.SOURCE_HOST_IP.getName(), historyItem.getJadeFilesDBItem().getSourceHostIp(), MESSAGE_RESOURCE_BASE
                + JadeFileColumns.SOURCE_HOST_IP.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeFileColumns.SOURCE_USER.getName(), historyItem.getJadeFilesDBItem().getSourceUser(), MESSAGE_RESOURCE_BASE
                + JadeFileColumns.SOURCE_USER.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeFileColumns.SOURCE_DIR.getName(), historyItem.getJadeFilesDBItem().getSourceDir(), MESSAGE_RESOURCE_BASE
                + JadeFileColumns.SOURCE_DIR.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeFileColumns.SOURCE_FILENAME.getName(), historyItem.getJadeFilesDBItem().getSourceFilename(), MESSAGE_RESOURCE_BASE
                + JadeFileColumns.SOURCE_FILENAME.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeFileColumns.MD5.getName(), historyItem.getJadeFilesDBItem().getMd5(), MESSAGE_RESOURCE_BASE
                + JadeFileColumns.MD5.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeFileColumns.FILE_SIZE.getName(), historyItem.getJadeFilesDBItem().getFileSize(), MESSAGE_RESOURCE_BASE
                + JadeFileColumns.FILE_SIZE.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.TARGET_HOST.getName(), historyItem.getTargetHost(), MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.TARGET_HOST.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.TARGET_HOST_IP.getName(), historyItem.getTargetHostIp(), MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.TARGET_HOST_IP.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.TARGET_USER.getName(), historyItem.getTargetUser(), MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.TARGET_USER.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.TARGET_DIR.getName(), historyItem.getTargetDir(), MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.TARGET_DIR.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.TARGET_FILENAME.getName(), historyItem.getTargetFilename(), MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.TARGET_FILENAME.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.PROTOCOL.getName(), historyItem.getProtocol(), MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.PROTOCOL.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.PORT.getName(), historyItem.getPort(), MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.PORT.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.OPERATION.getName(), historyItem.getOperation(), MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.OPERATION.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.TRANSFER_START.getName(), historyItem.getTransferStart(), MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.TRANSFER_START.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.TRANSFER_END.getName(), historyItem.getTransferEnd(), MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.TRANSFER_END.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.PID.getName(), historyItem.getPid(), MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.PID.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.PPID.getName(), historyItem.getPPid(), MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.PPID.getName()));
        if (historyItem.getLogFilename() != null && !"NULL".equals(historyItem.getLogFilename())) {
            detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.LOG_FILENAME.getName(), historyItem.getLogFilename(), MESSAGE_RESOURCE_BASE
                    + JadeHistoryFileColumns.LOG_FILENAME.getName()));
        } else {
            detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.LOG_FILENAME.getName(), messages.getValue(DETAIL_LAYOUT_NOT_SET), MESSAGE_RESOURCE_BASE
                    + JadeHistoryFileColumns.LOG_FILENAME.getName()));
            LOGGER.debug(RECEIVED_VALUE, JadeHistoryFileColumns.LOG_FILENAME.getName());
        }
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeFileColumns.MANDATOR.getName(), historyItem.getJadeFilesDBItem().getMandator(), MESSAGE_RESOURCE_BASE
                + JadeFileColumns.MANDATOR.getName()));
        if (historyItem.getJumpHost() != null && !"NULL".equals(historyItem.getJumpHost())) {
            detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.JUMP_HOST.getName(), historyItem.getJumpHost(), MESSAGE_RESOURCE_BASE
                    + JadeHistoryFileColumns.JUMP_HOST.getName()));
        } else {
            detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.JUMP_HOST.getName(), messages.getValue(DETAIL_LAYOUT_NOT_SET), MESSAGE_RESOURCE_BASE
                    + JadeHistoryFileColumns.JUMP_HOST.getName()));
            LOGGER.debug(RECEIVED_VALUE, JadeHistoryFileColumns.JUMP_HOST.getName());
        }
        if (historyItem.getJumpHostIp() != null && !"NULL".equals(historyItem.getJumpHostIp())) {
            detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.JUMP_HOST_IP.getName(), historyItem.getJumpHostIp(), MESSAGE_RESOURCE_BASE
                    + JadeHistoryFileColumns.JUMP_HOST_IP.getName()));
        } else {
            detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.JUMP_HOST_IP.getName(), messages.getValue(DETAIL_LAYOUT_NOT_SET), MESSAGE_RESOURCE_BASE
                    + JadeHistoryFileColumns.JUMP_HOST_IP.getName()));
            LOGGER.debug(RECEIVED_VALUE, JadeHistoryFileColumns.JUMP_HOST_IP.getName());
        }
        if (historyItem.getJumpUser() != null && !"NULL".equals(historyItem.getJumpUser())) {
            detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.JUMP_USER.getName(), historyItem.getJumpUser(), MESSAGE_RESOURCE_BASE
                    + JadeHistoryFileColumns.JUMP_USER.getName()));
        } else {
            detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.JUMP_USER.getName(), messages.getValue(DETAIL_LAYOUT_NOT_SET), MESSAGE_RESOURCE_BASE
                    + JadeHistoryFileColumns.JUMP_USER.getName()));
            LOGGER.debug(RECEIVED_VALUE, JadeHistoryFileColumns.JUMP_USER.getName());
        }
        if (historyItem.getJumpProtocol() != null && !"NULL".equals(historyItem.getJumpProtocol())) {
            detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.JUMP_PROTOCOL.getName(), historyItem.getJumpProtocol(), MESSAGE_RESOURCE_BASE
                    + JadeHistoryFileColumns.JUMP_PROTOCOL.getName()));
        } else {
            detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.JUMP_PROTOCOL.getName(), messages.getValue(DETAIL_LAYOUT_NOT_SET), MESSAGE_RESOURCE_BASE
                    + JadeHistoryFileColumns.JUMP_PROTOCOL.getName()));
            LOGGER.debug(RECEIVED_VALUE, JadeHistoryFileColumns.JUMP_PROTOCOL.getName());
        }
        if (historyItem.getJumpPort() != null && !"NULL".equals(historyItem.getJumpPort())) {
            detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.JUMP_PORT.getName(), historyItem.getJumpPort(), MESSAGE_RESOURCE_BASE
                    + JadeHistoryFileColumns.JUMP_PORT.getName()));
        } else {
            detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.JUMP_PORT.getName(), messages.getValue(DETAIL_LAYOUT_NOT_SET), MESSAGE_RESOURCE_BASE
                    + JadeHistoryFileColumns.JUMP_PORT.getName()));
            LOGGER.debug(RECEIVED_VALUE, JadeHistoryFileColumns.JUMP_PORT.getName());
        }
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.CREATED.getName(), historyItem.getCreated(), MESSAGE_RESOURCE_BASE
                + MESSAGE_RESOURCE_HISTORY + JadeHistoryFileColumns.CREATED.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.CREATED_BY.getName(), historyItem.getCreatedBy(), MESSAGE_RESOURCE_BASE
                + MESSAGE_RESOURCE_HISTORY + JadeHistoryFileColumns.CREATED_BY.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.MODIFIED.getName(), historyItem.getModified(), MESSAGE_RESOURCE_BASE
                + MESSAGE_RESOURCE_HISTORY + JadeHistoryFileColumns.MODIFIED.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeHistoryFileColumns.MODIFIED_BY.getName(), historyItem.getModifiedBy(), MESSAGE_RESOURCE_BASE
                + MESSAGE_RESOURCE_HISTORY + JadeHistoryFileColumns.MODIFIED_BY.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeFileColumns.CREATED.getName(), historyItem.getJadeFilesDBItem().getCreated(), MESSAGE_RESOURCE_BASE
                + MESSAGE_RESOURCE_FILE + JadeFileColumns.CREATED.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeFileColumns.CREATED_BY.getName(), historyItem.getJadeFilesDBItem().getCreatedBy(), MESSAGE_RESOURCE_BASE
                + MESSAGE_RESOURCE_FILE + JadeFileColumns.CREATED_BY.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeFileColumns.JADE_FILE_MODIFIED.getName(), historyItem.getJadeFilesDBItem().getModified(), MESSAGE_RESOURCE_BASE
                + MESSAGE_RESOURCE_FILE + JadeFileColumns.JADE_FILE_MODIFIED.getName()));
        detailItems.add(new JadeHistoryDetailItem(this.messages, JadeFileColumns.JADE_FILE_MODIFIED_BY.getName(), historyItem.getJadeFilesDBItem().getModifiedBy(), MESSAGE_RESOURCE_BASE
                + MESSAGE_RESOURCE_FILE + JadeFileColumns.JADE_FILE_MODIFIED_BY.getName()));
        return detailItems;
    }

    @SuppressWarnings("unchecked")
    private void addItems(List<JadeHistoryDetailItem> detailItems) {
        for (JadeHistoryDetailItem detailItem : detailItems) {
            addItem(detailItem);
            Item item = getItem(detailItem);
            if (item != null) {
                Property<String> key = item.getItemProperty(KEY);
                key.setValue(detailItem.getDisplayName());
                Property<Object> value = item.getItemProperty(VALUE);
                value.setValue(detailItem.getValue());
                if (detailItem.getValue() instanceof Date) {
                    value.setValue(dateConverter.convertToPresentation((Date) detailItem.getValue(), String.class, messages.getBundle().getLocale()));
                }
                Property<String> messageKey = item.getItemProperty(MESSAGE_KEY);
                messageKey.setValue(detailItem.getMessageKey());
            }
        }
    }

    private void addContainerProperties() {
        this.addContainerProperty(KEY, String.class, DISPLAY_NAME);
        this.addContainerProperty(VALUE, Object.class, null);
        this.addContainerProperty(DISPLAY_NAME, String.class, null);
        this.addContainerProperty(MESSAGE_KEY, String.class, null);
    }

    @SuppressWarnings("unchecked")
    public void updateLocale(Locale locale) {
        for (Object itemId : this.getItemIds()) {
            Item item = getItem(itemId);
            String messageKey = ((JadeHistoryDetailItem) itemId).getMessageKey();
            item.getItemProperty(KEY).setValue(messages.getValue(messageKey, locale) + ":");
            if (((messageKey.toLowerCase().contains("created") || messageKey.toLowerCase().contains("modified")) && !messageKey.contains("By"))
                    || messageKey.toLowerCase().contains("transferStart") || messageKey.toLowerCase().contains("transferEnd")) {
                item.getItemProperty(VALUE).setValue(dateConverter.convertToPresentation((Date) ((JadeHistoryDetailItem) itemId).getValue(), String.class, locale));
            } else if ((MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.LAST_ERROR_MESSAGE.getName()).equalsIgnoreCase(messageKey)
                    && "null".equalsIgnoreCase(this.historyItem.getLastErrorMessage())) {
                item.getItemProperty(VALUE).setValue(messages.getValue("DetailLayout.none", locale));
                LOGGER.debug(RECEIVED_VALUE, JadeHistoryFileColumns.LAST_ERROR_MESSAGE.getName());
            } else if (((MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.JUMP_HOST.getName()).equalsIgnoreCase(messageKey) && "null".equalsIgnoreCase(this.historyItem.getJumpHost()))
                    || ((MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.JUMP_HOST_IP.getName()).equalsIgnoreCase(messageKey) && "null".equalsIgnoreCase(this.historyItem.getJumpHostIp()))
                    || ((MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.JUMP_USER.getName()).equalsIgnoreCase(messageKey) && "null".equalsIgnoreCase(this.historyItem.getJumpUser()))
                    || ((MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.LOG_FILENAME.getName()).equalsIgnoreCase(messageKey) && "null".equalsIgnoreCase(this.historyItem.getLogFilename()))
                    || ((MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.JUMP_PROTOCOL.getName()).equalsIgnoreCase(messageKey) && "null".equalsIgnoreCase(this.historyItem.getJumpProtocol()))
                    || ((MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.JUMP_PORT.getName()).equalsIgnoreCase(messageKey) && this.historyItem.getJumpPort() != null)) {
                item.getItemProperty(VALUE).setValue(messages.getValue(DETAIL_LAYOUT_NOT_SET, locale));
                LOGGER.debug(RECEIVED_VALUE, messageKey.replace(MESSAGE_RESOURCE_BASE, ""));
            }
        }

    }

}
