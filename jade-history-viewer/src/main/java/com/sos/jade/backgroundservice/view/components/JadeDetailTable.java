package com.sos.jade.backgroundservice.view.components;

import static com.sos.jade.backgroundservice.JADEHistoryViewerUI.jadeBsOptions;
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
	private JadeFilesHistoryDBItem detailItem;
	private JadeBSMessages messages;
	private JadeDetailsContainer container;
	private Preferences prefs = jadeBsOptions.getPreferenceStore();
	private final Logger log = LoggerFactory.getLogger(JadeDetailTable.class);
	
	
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
		try {
			if(prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_DETAIL_TABLE).node(JadeBSConstants.PREF_NODE_WIDTHS).keys().length > 0){
				setPreferencesColumnsWidth();
			}else{
				setDefaultColumnsWidth();
			}
		} catch (BackingStoreException e) {
			log.error("error reading from PreferenceStore, setting default widths instead! {}", e);
			setDefaultColumnsWidth();
			saveDefaultColumnsWidthToPreferences();
		}
		initConfigurationChangeListeners();
	}
	
	/**
	 * sets alternative values for column headers
	 */
	private void setColumnHeaders(){
		setColumnHeader("key", messages.getValue("DetailLayout.keyHeader"));
		setColumnHeader("value", messages.getValue("DetailLayout.valueHeader"));
	}

	private void setDefaultColumnsWidth(){
		setColumnWidth("key", 200);
		setColumnWidth("value", 220);
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
			        prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_DETAIL_TABLE).node(JadeBSConstants.PREF_NODE_WIDTHS).putInt(col.toString(), getColumnWidth(col));
		        	log.debug("actual width of " + col.toString() + " = " + String.valueOf(getColumnWidth(col)));
		        }
				try {
					prefs.flush();
				} catch (BackingStoreException e) {
					e.printStackTrace();
					log.error("error while flushing Preferences for Column widths: {}", e);
				}
			}
		});
	}
	
	public void resetColumnWidths(){
		for(Object column : this.getVisibleColumns()){
			// width = -1 means the column takes all the accessible space 
			setColumnWidth(column.toString(), -1);
	        prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_DETAIL_TABLE).node(JadeBSConstants.PREF_NODE_WIDTHS).putInt(column.toString(), -1);
		}
		this.refreshRowCache();
		this.markAsDirty();
	}
	
	private void setPreferencesColumnsWidth(){
		for(Object column : visibleColumns){
			int width = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_DETAIL_TABLE).node(JadeBSConstants.PREF_NODE_WIDTHS).getInt(column.toString(), 0);
			if (width != 0){
				setColumnWidth(column.toString(), width);
				log.debug("setting width of column " + column.toString() + " to " + String.valueOf(width));
			}
		}
	}
	
	private void saveDefaultColumnsWidthToPreferences(){
        prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_DETAIL_TABLE).node(JadeBSConstants.PREF_NODE_WIDTHS).putInt("key", 200);
        prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_DETAIL_TABLE).node(JadeBSConstants.PREF_NODE_WIDTHS).putInt("value", 220);
	}
	
}
