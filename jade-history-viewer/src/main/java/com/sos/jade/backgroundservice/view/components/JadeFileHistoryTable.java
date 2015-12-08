package com.sos.jade.backgroundservice.view.components;

import static com.sos.jade.backgroundservice.JADEHistoryViewerUI.JADE_BS_OPTIONS;
import static com.sos.jade.backgroundservice.JADEHistoryViewerUI.parentNodeName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sos.jadehistory.db.JadeFilesHistoryDBItem;

import com.sos.jade.backgroundservice.constants.JadeBSConstants;
import com.sos.jade.backgroundservice.data.JadeFilesHistoryContainer;
import com.sos.jade.backgroundservice.enums.JadeFileColumns;
import com.sos.jade.backgroundservice.enums.JadeHistoryFileColumns;
import com.sos.jade.backgroundservice.enums.TransferStatusValues;
import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class JadeFileHistoryTable extends Table {

    private static final long serialVersionUID = 2134585331362934124L;
    private static final int DEFAULT_COLUMN_KEY_WIDTH = 200;
    private static final int DEFAULT_COLUMN_VALUE_WIDTH = 220;
    private static final int PAGE_LENGTH = 20;
    private static final Logger LOGGER = LoggerFactory.getLogger(JadeFileHistoryTable.class);
    private static final String MESSAGE_RESOURCE_BASE = "JadeMenuBar.";
    private static final String COLUMN_COLLAPSE_KEY = "columnCollapse";
    private List<JadeFilesHistoryDBItem> historyItems;
    private JadeFilesHistoryContainer container;
    private JadeBSMessages messages;
    private Preferences prefs = JADE_BS_OPTIONS.getPreferenceStore();

    private static final Object[] VISIBLE_COLUMNS = new String[] { JadeHistoryFileColumns.STATUS.getName(), JadeFileColumns.MANDATOR.getName(),
            JadeHistoryFileColumns.TRANSFER_START.getName(), JadeHistoryFileColumns.TRANSFER_END.getName(),
            JadeHistoryFileColumns.OPERATION.getName(), JadeHistoryFileColumns.PROTOCOL.getName(), JadeHistoryFileColumns.TARGET_FILENAME.getName(),
            JadeFileColumns.FILE_SIZE.getName(), JadeFileColumns.SOURCE_HOST.getName(), JadeHistoryFileColumns.TARGET_HOST.getName() };

    public JadeFileHistoryTable(List<JadeFilesHistoryDBItem> historyItems, JadeBSMessages messages) {
        if (historyItems == null) {
            historyItems = new ArrayList<JadeFilesHistoryDBItem>();
        }
        this.historyItems = historyItems;
        this.messages = messages;
        init();
    }

    /** initializes the table component with its dimension and visible columns */
    private void init() {
        if (historyItems != null && !historyItems.isEmpty()) {
            this.setContainerDataSource(this.container = new JadeFilesHistoryContainer(historyItems));
        }
        setSizeFull();
        setSelectable(true);
        setImmediate(true);
        setEditable(false);
        setColumnReorderingAllowed(true);
        setColumnCollapsingAllowed(true);
        setSortEnabled(true);
        setMultiSelect(false);
        setPageLength(PAGE_LENGTH);
        addStyleName("jadeBsTable");
        setColumnAlignment(JadeHistoryFileColumns.STATUS.getName(), Align.CENTER);
        setCellStyleGenerator(new StatusCellStyleGenerator());
        enableContentRefreshing(true);
        setColumnHeaders();
        initConfigurationChangeListeners();
        setSavedPreferences();
    }

    private void setSavedPreferences() {
        setPreferencesColumnsWidth();
        getColumnsCollapsed();
        setPreferencesColumnOrder();
    }

    private void setPreferencesColumnOrder() {
        String strVc = null;
        strVc = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_HISTORY_TABLE).node(JadeBSConstants.PREF_NODE_ORDER).get(JadeBSConstants.PREF_KEY_ORDER, null);
        if (strVc != null && ((historyItems != null && !historyItems.isEmpty()) || this.container != null)) {
            if (strVc.contains("transferTimestamp")) {
                strVc.replace("transferTimestamp", "transferEnd");
            }
            this.setVisibleColumns((Object[]) strVc.split(JadeBSConstants.DELIMITER_REGEX));
            this.refreshRowCache();
            this.markAsDirty();
            LOGGER.debug("VisibleColumnsOrder after setting from Preferences: {}", createOrderedColumnsString(this.getVisibleColumns()));
        } else if (container != null) {
            this.setVisibleColumns(VISIBLE_COLUMNS);
        }
    }

    private void setPreferencesColumnsWidth() {
        for (Object key : VISIBLE_COLUMNS) {
            int width = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_HISTORY_TABLE).node(JadeBSConstants.PREF_NODE_WIDTHS).getInt(key.toString(), 0);
            if (width != 0) {
                setColumnWidth(key.toString(), width);
            }
        }
    }

    /** sets alternative values for column headers */
    private void setColumnHeaders() {
        setColumnHeader(JadeHistoryFileColumns.STATUS.getName(), messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.STATUS.getName()));
        setColumnHeader(JadeFileColumns.MANDATOR.getName(), messages.getValue(MESSAGE_RESOURCE_BASE + JadeFileColumns.MANDATOR.getName()));
        setColumnHeader(JadeHistoryFileColumns.TRANSFER_START.getName(), messages.getValue(MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.TRANSFER_START.getName()));
        setColumnHeader(JadeHistoryFileColumns.TRANSFER_END.getName(), messages.getValue(MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.TRANSFER_END.getName()));
        setColumnHeader(JadeHistoryFileColumns.OPERATION.getName(), messages.getValue(MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.OPERATION.getName()));
        setColumnHeader(JadeHistoryFileColumns.PROTOCOL.getName(), messages.getValue(MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.PROTOCOL.getName()));
        setColumnHeader(JadeHistoryFileColumns.TARGET_FILENAME.getName(), messages.getValue(MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.TARGET_FILENAME.getName()));
        setColumnHeader(JadeFileColumns.FILE_SIZE.getName(), messages.getValue(MESSAGE_RESOURCE_BASE + JadeFileColumns.FILE_SIZE.getName()));
        setColumnHeader(JadeFileColumns.SOURCE_HOST.getName(), messages.getValue(MESSAGE_RESOURCE_BASE + JadeFileColumns.SOURCE_HOST.getName()));
        setColumnHeader(JadeHistoryFileColumns.TARGET_HOST.getName(), messages.getValue(MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.TARGET_HOST.getName()));
    }

    /** refresh
     * 
     * @param locale */
    public void refreshColumnHeaders(Locale locale) {
        setColumnHeader(JadeHistoryFileColumns.STATUS.getName(), messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.STATUS.getName(), locale));
        setColumnHeader(JadeFileColumns.MANDATOR.getName(), messages.getValue(MESSAGE_RESOURCE_BASE + JadeFileColumns.MANDATOR.getName(), locale));
        setColumnHeader(JadeHistoryFileColumns.TRANSFER_START.getName(), messages.getValue(MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.TRANSFER_START.getName(), locale));
        setColumnHeader(JadeHistoryFileColumns.TRANSFER_END.getName(), messages.getValue(MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.TRANSFER_END.getName(), locale));
        setColumnHeader(JadeHistoryFileColumns.OPERATION.getName(), messages.getValue(MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.OPERATION.getName(), locale));
        setColumnHeader(JadeHistoryFileColumns.PROTOCOL.getName(), messages.getValue(MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.PROTOCOL.getName(), locale));
        setColumnHeader(JadeHistoryFileColumns.TARGET_FILENAME.getName(), messages.getValue(MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.TARGET_FILENAME.getName(), locale));
        setColumnHeader(JadeFileColumns.FILE_SIZE.getName(), messages.getValue(MESSAGE_RESOURCE_BASE + JadeFileColumns.FILE_SIZE.getName(), locale));
        setColumnHeader(JadeFileColumns.SOURCE_HOST.getName(), messages.getValue(MESSAGE_RESOURCE_BASE + JadeFileColumns.SOURCE_HOST.getName(), locale));
        setColumnHeader(JadeHistoryFileColumns.TARGET_HOST.getName(), messages.getValue(MESSAGE_RESOURCE_BASE
                + JadeHistoryFileColumns.TARGET_HOST.getName(), locale));
    }

    /** replaces the present container with a new created container related to
     * the given historyItems
     * 
     * @param historyItems List of JadeFilesHistoryDBItems */
    public void populateDatasource(List<JadeFilesHistoryDBItem> historyItems) {
        this.historyItems = historyItems;
        this.setContainerDataSource(this.container = new JadeFilesHistoryContainer(this.historyItems));
        setSavedPreferences();
        this.fireItemSetChange();
        this.fireValueChange(true);
    }

    private class StatusCellStyleGenerator implements CellStyleGenerator {

        private static final long serialVersionUID = 1L;

        @Override
        public String getStyle(Table source, Object itemId, Object propertyId) {
            if (itemId != null && propertyId != null && JadeHistoryFileColumns.STATUS.getName().equals(propertyId)) {
                if ("success".equals(((JadeFilesHistoryDBItem) itemId).getStatus())) {
                    ((Label) source.getItem(itemId).getItemProperty(propertyId).getValue()).setStyleName("jadeStatusSuccessLabel");
                    return "jadeStatusSuccessLabel";
                } else if ("error".equals(((JadeFilesHistoryDBItem) itemId).getStatus())) {
                    ((Label) source.getItem(itemId).getItemProperty(propertyId).getValue()).setStyleName("jadeStatusErrorLabel");
                    return "jadeStatusErrorLabel";
                } else if ("transferring".equals(((JadeFilesHistoryDBItem) itemId).getStatus())
                        || "notOverwritten".equals(((JadeFilesHistoryDBItem) itemId).getStatus())) {
                    ((Label) source.getItem(itemId).getItemProperty(propertyId).getValue()).setStyleName("jadeStatusTransferLabel");
                    return "jadeStatusTransferLabel";
                }
            }
            return null;
        }
    }

    private void initConfigurationChangeListeners() {
        this.addColumnReorderListener(new Table.ColumnReorderListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void columnReorder(ColumnReorderEvent event) {
                Object[] reorderedVisibleColumns = JadeFileHistoryTable.this.getVisibleColumns();
                setOrderedColumnsPreferencesNode(reorderedVisibleColumns);
            }
        });
        this.addColumnResizeListener(new Table.ColumnResizeListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void columnResize(ColumnResizeEvent event) {
                for (Object col : VISIBLE_COLUMNS) {
                    prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_HISTORY_TABLE).node(JadeBSConstants.PREF_NODE_WIDTHS).putInt(col.toString(), getColumnWidth(col));
                    LOGGER.debug("actual width of {} = {}", col.toString(), String.valueOf(getColumnWidth(col)));
                }
                try {
                    prefs.flush();
                } catch (BackingStoreException e) {
                    LOGGER.error("error while flushing Preferences for Column widths: {}", e);
                }
            }
        });
        this.addColumnCollapseListener(new ColumnCollapseListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void columnCollapse(ColumnCollapseEvent event) {
                Object column = event.getPropertyId();
                setColumnCollapsed(column, event.getCollapsed());
                prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_HISTORY_TABLE).node(JadeBSConstants.PREF_NODE_COLLAPSE).putBoolean(column.toString(), event.getCollapsed());

            }
        });
    }

    private void getColumnsCollapsed() {
        if (container != null) {
            for (Object column : container.getContainerPropertyIds()) {
                setColumnCollapsed(column, prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_HISTORY_TABLE).node(JadeBSConstants.PREF_NODE_COLLAPSE).getBoolean(column.toString(), false));
            }
        }
    }

    private String createOrderedColumnsString(Object[] orderedCols) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object obj : orderedCols) {
            if (!first) {
                sb.append(JadeBSConstants.DELIMITER);
            }
            sb.append(obj.toString());
            first = false;
        }
        return sb.toString();
    }

    private void setOrderedColumnsPreferencesNode(Object[] orderedCols) {
        prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_HISTORY_TABLE).node(JadeBSConstants.PREF_NODE_ORDER).put(JadeBSConstants.PREF_KEY_ORDER, createOrderedColumnsString(orderedCols));
        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
            LOGGER.error("error while flushing Preferences for Column Order: {}", e);
        }
    }

    public void resetColumnWidths() {
        for (Object key : this.getVisibleColumns()) {
            setColumnWidth(key.toString(), -1);
            prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_HISTORY_TABLE).node(JadeBSConstants.PREF_NODE_WIDTHS).putInt(key.toString(), -1);
        }
        this.refreshRowCache();
        this.markAsDirty();
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);
        handleColumnCollapseEvent(variables);
    }

    private void handleColumnCollapseEvent(Map<String, Object> variables) {
        if (variables.containsKey("collapsedcolumns")) {
            String[] collapsedCollumns = (String[]) variables.get("collapsedcolumns");
            Object[] actualVisibleColumns = getVisibleColumns();
            for (int j = 0; j < actualVisibleColumns.length; j++) {
                boolean isCollapsed = ArrayUtils.contains(collapsedCollumns, variables.get(actualVisibleColumns[j]));
                if (isColumnCollapsed(actualVisibleColumns[j]) && !isCollapsed) {
                    fireEvent(new ColumnCollapseEvent(this, actualVisibleColumns[j], isColumnCollapsed(actualVisibleColumns[j])));
                } else if (!isColumnCollapsed(actualVisibleColumns[j]) && !isCollapsed) {
                    fireEvent(new ColumnCollapseEvent(this, actualVisibleColumns[j], isCollapsed));
                }
            }
        }
    }

    public void addColumnCollapseListener(ColumnCollapseListener listener) {
        addListener(COLUMN_COLLAPSE_KEY, ColumnCollapseEvent.class, listener, ColumnCollapseEvent.COLUMN_COLLAPSE_METHOD);
    }

    public void removeColumnCollapseListener(ColumnCollapseListener listener) {
        removeListener(COLUMN_COLLAPSE_KEY, ColumnCollapseEvent.class, listener);
    }

    public static class ColumnCollapseEvent extends Component.Event {

        private static final long serialVersionUID = 1L;
        public static final java.lang.reflect.Method COLUMN_COLLAPSE_METHOD;
        static {
            try {
                COLUMN_COLLAPSE_METHOD = ColumnCollapseListener.class.getDeclaredMethod(COLUMN_COLLAPSE_KEY, new Class[] { ColumnCollapseEvent.class });
            } catch (final java.lang.NoSuchMethodException e) {
                throw new java.lang.RuntimeException(e);
            }
        }
        private final boolean collapsed;
        private final Object columnPropertyId;

        public ColumnCollapseEvent(Component source, Object propertyId, boolean collapsed) {
            super(source);
            this.collapsed = collapsed;
            columnPropertyId = propertyId;
        }

        public Object getPropertyId() {
            return columnPropertyId;
        }

        public boolean getCollapsed() {
            return collapsed;
        }
    }

    public interface ColumnCollapseListener extends Serializable {

        public void columnCollapse(ColumnCollapseEvent event);
    }

}
