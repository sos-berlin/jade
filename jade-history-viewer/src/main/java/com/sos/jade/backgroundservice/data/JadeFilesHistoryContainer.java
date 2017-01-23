package com.sos.jade.backgroundservice.data;

import java.util.List;

import sos.jadehistory.db.JadeFilesHistoryDBItem;

import com.sos.jade.backgroundservice.enums.JadeFileColumns;
import com.sos.jade.backgroundservice.enums.JadeHistoryFileColumns;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Label;

public class JadeFilesHistoryContainer extends IndexedContainer {

    private List<JadeFilesHistoryDBItem> historyItems;
    private static final long serialVersionUID = 1L;
    private static final float DEFAULT_DIMENSION = 16.0f;

    public JadeFilesHistoryContainer(List<JadeFilesHistoryDBItem> historyItems) {
        this.historyItems = historyItems;
        addContainerProperties();
        addItems(historyItems);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void addItems(List<JadeFilesHistoryDBItem> historyItemList) {
        if (historyItemList != null) {
            for (JadeFilesHistoryDBItem historyItem : historyItemList) {
                try {
                    historyItem.getJadeFilesDBItem().getMandator();
                    addItem(historyItem);
                    Item item = getItem(historyItem);
                    Property status = item.getItemProperty(JadeHistoryFileColumns.STATUS.getName());
                    if ("transferred".equals(historyItem.getStatus()) || "success".equals(historyItem.getStatus())
                            || "compressed".equals(historyItem.getStatus()) || "renamed".equals(historyItem.getStatus())) {
                        status.setValue(new StatusSuccessLabel());
                    } else if ("transfer_aborted".equals(historyItem.getStatus()) || "transfer_has_errors".equals(historyItem.getStatus())) {
                        status.setValue(new StatusErrorLabel());
                    } else if ("transferring".equals(historyItem.getStatus()) || "notOverwritten".equals(historyItem.getStatus())
                            || "transfer_skipped".equals(historyItem.getStatus()) || "transferInProgress".equals(historyItem.getStatus())
                            || "waiting4transfer".equals(historyItem.getStatus()) || "transferUndefined".equals(historyItem.getStatus())
                            || "IgnoredDueToZerobyteConstraint".equals(historyItem.getStatus()) || "setBack".equals(historyItem.getStatus())
                            || "polling".equals(historyItem.getStatus()) || "deleted".equals(historyItem.getStatus())) {
                        status.setValue(new StatusTransferLabel());
                    }
                    Property mandator = item.getItemProperty(JadeFileColumns.MANDATOR.getName());
                    mandator.setValue(historyItem.getJadeFilesDBItem().getMandator());
                    Property transferStart = item.getItemProperty(JadeHistoryFileColumns.TRANSFER_START.getName());
                    transferStart.setValue(historyItem.getTransferStart());
                    Property transferEnd = item.getItemProperty(JadeHistoryFileColumns.TRANSFER_END.getName());
                    transferEnd.setValue(historyItem.getTransferEnd());
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
    }

    private void addContainerProperties() {
        addContainerProperty(JadeHistoryFileColumns.STATUS.getName(), Label.class, new Label());
        addContainerProperty(JadeFileColumns.MANDATOR.getName(), JadeFileColumns.MANDATOR.getType(), JadeFileColumns.MANDATOR.getDefaultValue());
        addContainerProperty(JadeHistoryFileColumns.TRANSFER_START.getName(), JadeHistoryFileColumns.TRANSFER_START.getType(), JadeHistoryFileColumns.TRANSFER_START.getDefaultValue());
        addContainerProperty(JadeHistoryFileColumns.TRANSFER_END.getName(), JadeHistoryFileColumns.TRANSFER_END.getType(), JadeHistoryFileColumns.TRANSFER_END.getDefaultValue());
        addContainerProperty(JadeHistoryFileColumns.OPERATION.getName(), JadeHistoryFileColumns.OPERATION.getType(), JadeHistoryFileColumns.OPERATION.getDefaultValue());
        addContainerProperty(JadeHistoryFileColumns.PROTOCOL.getName(), JadeHistoryFileColumns.PROTOCOL.getType(), JadeHistoryFileColumns.PROTOCOL.getDefaultValue());
        addContainerProperty(JadeHistoryFileColumns.TARGET_FILENAME.getName(), JadeHistoryFileColumns.TARGET_FILENAME.getType(), JadeHistoryFileColumns.TARGET_FILENAME.getDefaultValue());
        addContainerProperty(JadeFileColumns.FILE_SIZE.getName(), JadeFileColumns.FILE_SIZE.getType(), JadeFileColumns.FILE_SIZE.getDefaultValue());
        addContainerProperty(JadeFileColumns.SOURCE_HOST.getName(), JadeFileColumns.SOURCE_HOST.getType(), JadeFileColumns.SOURCE_HOST.getDefaultValue());
        addContainerProperty(JadeHistoryFileColumns.TARGET_HOST.getName(), JadeHistoryFileColumns.TARGET_HOST.getType(), JadeHistoryFileColumns.TARGET_HOST.getDefaultValue());
        addContainerProperty(JadeHistoryFileColumns.TRANSFER_END.getName(), JadeHistoryFileColumns.TRANSFER_END.getType(), JadeHistoryFileColumns.TRANSFER_END.getDefaultValue());
    }

    private class StatusSuccessLabel extends Label {

        private static final long serialVersionUID = 1L;

        public StatusSuccessLabel() {
            setWidth(DEFAULT_DIMENSION, Unit.PIXELS);
            setHeight(DEFAULT_DIMENSION, Unit.PIXELS);
            setStyleName("jadeStatusSuccessLabel");
        }
    }

    private class StatusErrorLabel extends Label {

        private static final long serialVersionUID = 1L;

        public StatusErrorLabel() {
            setWidth(DEFAULT_DIMENSION, Unit.PIXELS);
            setHeight(DEFAULT_DIMENSION, Unit.PIXELS);
            setStyleName("jadeStatusErrorLabel");
        }
    }

    private class StatusTransferLabel extends Label {

        private static final long serialVersionUID = 1L;

        public StatusTransferLabel() {
            setWidth(DEFAULT_DIMENSION, Unit.PIXELS);
            setHeight(DEFAULT_DIMENSION, Unit.PIXELS);
            setStyleName("jadeStatusTransferLabel");
        }
    }

    @SuppressWarnings("unchecked")
    public void updateItem(Object itemId) {
        if (this.containsId(itemId)) {
            Item newItem = (Item) itemId;
            Item oldItem = this.getItem(itemId);
            if (oldItem != null) {
                for (Object property : getContainerPropertyIds()) {
                    oldItem.getItemProperty(property).setValue(newItem.getItemProperty(property).getValue());
                }
            }
        }
    }

}
