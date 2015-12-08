package com.sos.jade.backgroundservice.view.components;

import static com.sos.jade.backgroundservice.JADEHistoryViewerUI.JADE_BS_OPTIONS;
import static com.sos.jade.backgroundservice.JADEHistoryViewerUI.parentNodeName;

import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sos.ftphistory.db.JadeFilesHistoryDBItem;

import com.sos.jade.backgroundservice.constants.JadeBSConstants;
import com.sos.jade.backgroundservice.data.JadeDetailsContainer;
import com.sos.jade.backgroundservice.enums.JadeHistoryFileColumns;
import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.vaadin.ui.Table;

public class JadeDetailTable extends Table {

    private static final long serialVersionUID = 1L;
    private static final int PAGE_LENGTH = 20;
    private static final Logger LOGGER = LoggerFactory.getLogger(JadeDetailTable.class);
    private static final int DEFAULT_COLUMN_KEY_WIDTH = 200;
    private static final int DEFAULT_COLUMN_VALUE_WIDTH = 220;
    private static final String KEY = "key";
    private static final String VALUE = "value";
    private JadeFilesHistoryDBItem detailItem;
    private JadeBSMessages messages;
    private JadeDetailsContainer container;
    private Preferences prefs = JADE_BS_OPTIONS.getPreferenceStore();

    private static final Object[] VISIBLE_COLUMNS = new String[] { KEY, VALUE };

    public JadeDetailTable(JadeFilesHistoryDBItem detailItem, JadeBSMessages messages) {
        this.detailItem = detailItem;
        this.messages = messages;
        init();
    }

    private void init() {
        if (this.detailItem != null) {
            this.container = new JadeDetailsContainer(this.detailItem, this.messages);
            this.setContainerDataSource(this.container);
        }
        setSizeFull();
        setSelectable(true);
        setImmediate(true);
        setEditable(false);
        setColumnReorderingAllowed(false);
        setColumnCollapsingAllowed(false);
        setSortEnabled(false);
        setMultiSelect(false);
        setPageLength(PAGE_LENGTH);
        addStyleName("jadeBsDetailTable");
        setColumnAlignment(JadeHistoryFileColumns.STATUS.getName(), Align.CENTER);
        enableContentRefreshing(true);
        if (container != null) {
            this.setVisibleColumns(VISIBLE_COLUMNS);
        }
        setColumnHeaders();
        try {
            if (prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_DETAIL_TABLE).node(JadeBSConstants.PREF_NODE_WIDTHS).keys().length > 0) {
                setPreferencesColumnsWidth();
            } else {
                setDefaultColumnsWidth();
            }
        } catch (BackingStoreException e) {
            LOGGER.error("error reading from PreferenceStore, setting default widths instead! {}", e);
            setDefaultColumnsWidth();
            saveDefaultColumnsWidthToPreferences();
        }
        initConfigurationChangeListeners();
    }

    /** sets alternative values for column headers */
    private void setColumnHeaders() {
        setColumnHeader(KEY, messages.getValue("DetailLayout.keyHeader"));
        setColumnHeader(VALUE, messages.getValue("DetailLayout.valueHeader"));
    }

    private void setDefaultColumnsWidth() {
        setColumnWidth(KEY, DEFAULT_COLUMN_KEY_WIDTH);
        setColumnWidth(VALUE, DEFAULT_COLUMN_VALUE_WIDTH);
    }

    /** refresh
     * 
     * @param locale */
    public void refreshColumnHeaders(Locale locale) {
        setColumnHeader(KEY, messages.getValue("DetailLayout.keyHeader", locale));
        setColumnHeader(VALUE, messages.getValue("DetailLayout.valueHeader", locale));
    }

    /** replaces the present container with a new created container related to
     * the given historyItems
     * 
     * @param historyItems List of JadeFilesHistoryDBItems */
    public void populateDatasource(JadeFilesHistoryDBItem detailItem) {
        this.detailItem = detailItem;
        this.container = new JadeDetailsContainer(this.detailItem, this.messages);
        this.setContainerDataSource(this.container);
        this.setVisibleColumns(VISIBLE_COLUMNS);
    }

    private void initConfigurationChangeListeners() {
        this.addColumnResizeListener(new Table.ColumnResizeListener() {
            private static final long serialVersionUID = 1L;
            @Override
            public void columnResize(ColumnResizeEvent event) {
                for (Object col : VISIBLE_COLUMNS) {
                    prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_DETAIL_TABLE)
                        .node(JadeBSConstants.PREF_NODE_WIDTHS).putInt(col.toString(), getColumnWidth(col));
                    LOGGER.debug("actual width of " + col.toString() + " = " + String.valueOf(getColumnWidth(col)));
                }
                try {
                    prefs.flush();
                } catch (BackingStoreException e) {
                    LOGGER.error("error while flushing Preferences for Column widths: {}", e);
                }
            }
        });
    }

    public void resetColumnWidths() {
        for (Object column : this.getVisibleColumns()) {
            // width = -1 means the column takes all the accessible space
            setColumnWidth(column.toString(), -1);
            prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_DETAIL_TABLE).node(JadeBSConstants.PREF_NODE_WIDTHS).putInt(column.toString(), -1);
        }
        this.refreshRowCache();
        this.markAsDirty();
    }

    private void setPreferencesColumnsWidth() {
        for (Object column : VISIBLE_COLUMNS) {
            int width = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_DETAIL_TABLE).node(JadeBSConstants.PREF_NODE_WIDTHS).getInt(column.toString(), 0);
            if (width != 0) {
                setColumnWidth(column.toString(), width);
                LOGGER.debug("setting width of column " + column.toString() + " to " + String.valueOf(width));
            }
        }
    }

    private void saveDefaultColumnsWidthToPreferences() {
        prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_DETAIL_TABLE).node(JadeBSConstants.PREF_NODE_WIDTHS).putInt(KEY, DEFAULT_COLUMN_KEY_WIDTH);
        prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_DETAIL_TABLE).node(JadeBSConstants.PREF_NODE_WIDTHS).putInt(VALUE, DEFAULT_COLUMN_VALUE_WIDTH);
    }

}
