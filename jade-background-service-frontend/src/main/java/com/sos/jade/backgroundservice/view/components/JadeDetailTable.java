package com.sos.jade.backgroundservice.view.components;

import static com.sos.jade.backgroundservice.BackgroundserviceUI.jadeBsOptions;
import static com.sos.jade.backgroundservice.BackgroundserviceUI.parentNodeName;

import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

import sos.ftphistory.db.JadeFilesHistoryDBItem;

import com.sos.jade.backgroundservice.data.JadeDetailsContainer;
import com.sos.jade.backgroundservice.enums.JadeHistoryFileColumns;
import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.vaadin.ui.Table;

public class JadeDetailTable extends Table {
	private static final long serialVersionUID = 1L;
	private static final String COLUMN_ORDER = "colOrder";
	private static final String CLASS_NODE_NAME = "detail-table";
	private static final String PREF_NODE_NAME_ORDER = "table_column_order";
	private static final String PREF_NODE_NAME_WIDTHS = "table_column_widths";
	private static final String PREF_NODE_NAME_COLLAPSE = "table_column_collapse";
	private static final String PREF_KEY_NAME_ORDER = "column_order";
	private static final String DELIMITER = "|";
	private static final String ENTRY_DELIMITER = ";";
	private static final String DELIMITER_REGEX = "[|]";
	private static final String EQUAL_CHAR = "=";
	private static final int PAGE_LENGTH = 20;
	private JadeFilesHistoryDBItem detailItem;
	private JadeBSMessages messages;
	private JadeDetailsContainer container;
	private Preferences prefs = jadeBsOptions.getPreferenceStore();
	private final Logger log = Logger.getLogger(JadeDetailTable.class);
	
	
	private static final Object[] visibleColumns = new String[] {"key", "value"};
	
	public JadeDetailTable(JadeFilesHistoryDBItem detailItem, JadeBSMessages messages) {
		this.detailItem = detailItem;
		this.messages = messages;
		init();
	}

	/**
	 * initializes the table component with its dimension and visible columns
	 */
	private void init(){
		if (this.detailItem != null){
			this.setContainerDataSource(this.container = new JadeDetailsContainer(this.detailItem, this.messages));
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
		if (container != null){
			this.setVisibleColumns(visibleColumns);
		}
		setColumnHeaders();
		setPreferencesColumnsWidth();
		initConfigurationChangeListeners();
	}
	
	/**
	 * sets alternative values for column headers
	 */
	private void setColumnHeaders(){
		setColumnHeader("key", messages.getValue("DetailLayout.keyHeader"));
		setColumnHeader("value", messages.getValue("DetailLayout.valueHeader"));
	}
	
	/**
	 * refresh 
	 * @param locale
	 */
	public void refreshColumnHeaders(Locale locale){
		setColumnHeader("key", messages.getValue("DetailLayout.keyHeader", locale));
		setColumnHeader("value", messages.getValue("DetailLayout.valueHeader", locale));
	}
	
	/**
	 * replaces the present container with a new created container related to the given historyItems
	 * 
	 * @param historyItems List of JadeFilesHistoryDBItems
	 */
	public void populateDatasource(JadeFilesHistoryDBItem detailItem){
		this.detailItem = detailItem;
		this.setContainerDataSource(this.container = new JadeDetailsContainer(this.detailItem, this.messages));
		this.setVisibleColumns(visibleColumns);
	}
	
	private void initConfigurationChangeListeners(){
		this.addColumnResizeListener(new Table.ColumnResizeListener() {
			
			@Override
			public void columnResize(ColumnResizeEvent event) {
				int oldWidth = event.getPreviousWidth();
		        // Get the new width of the resized column
		        int width = event.getCurrentWidth();
		        
		        // Get the property ID of the resized column
		        String column = (String) event.getPropertyId();
		        // save all column widths, to make sure that unresized columns are displayed.
		        // correctly after reload. Because of default size = -1 all columns use the 
		        // available space equally. If some columns are resized and others not, the 
		        // unresized columns would go on using all the available space, which can be more
		        // than before changing width of the others.
		        for(Object col : visibleColumns){
			        prefs.node(parentNodeName).node(CLASS_NODE_NAME).node(PREF_NODE_NAME_WIDTHS).putInt(col.toString(), getColumnWidth(col));
		        	log.debug("actual width of " + col.toString() + " = " + String.valueOf(getColumnWidth(col)));
		        }
				try {
					prefs.flush();
				} catch (BackingStoreException e) {
					e.printStackTrace();
					log.error("error while flushing Preferences for Column widths: ", e);
				}
			}
		});
	}
	
	public void resetColumnWidths(){
		for(Object key : this.getVisibleColumns()){
			// width = -1 means the column takes all the accessible space 
			setColumnWidth(key.toString(), -1);
	        prefs.node(parentNodeName).node(CLASS_NODE_NAME).node(PREF_NODE_NAME_WIDTHS).putInt(key.toString(), -1);
		}
		this.refreshRowCache();
		this.markAsDirty();
	}
	
	private void setPreferencesColumnsWidth(){
		for(Object key : visibleColumns){
			int width = prefs.node(parentNodeName).node(CLASS_NODE_NAME).node(PREF_NODE_NAME_WIDTHS).getInt(key.toString(), 0);
			if (width != 0){
				setColumnWidth(key.toString(), width);
				log.debug("setting width of column " + key.toString() + " to " + String.valueOf(width));
			}
		}
	}
	
}
