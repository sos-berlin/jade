package com.sos.jade.backgroundservice.view.components;

import static com.sos.jade.backgroundservice.BackgroundserviceUI.jadeBsOptions;
import static com.sos.jade.backgroundservice.BackgroundserviceUI.parentNodeName;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.io.Serializable;
import java.lang.reflect.Method;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import sos.ftphistory.db.JadeFilesHistoryDBItem;

import com.sos.jade.backgroundservice.constants.JadeBSConstants;
import com.sos.jade.backgroundservice.data.JadeFilesHistoryContainer;
import com.sos.jade.backgroundservice.enums.JadeFileColumns;
import com.sos.jade.backgroundservice.enums.JadeHistoryFileColumns;
import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class JadeFileHistoryTable extends Table{

	private static final long serialVersionUID = 2134585331362934124L;
	private List<JadeFilesHistoryDBItem> historyItems;
	private JadeFilesHistoryContainer container;
	private static final int PAGE_LENGTH = 20;
	private JadeBSMessages messages;
//	private Map<String, Integer> defaultColumnWidths;
//	private String lastColOrder;
//	private String sessionsLastColOrder;
	private Preferences prefs = jadeBsOptions.getPreferenceStore();
	private final Logger log = Logger.getLogger(JadeFileHistoryTable.class);
	private static final String MESSAGE_RESOURCE_BASE = "JadeMenuBar.";
	
	private static final Object[] visibleColumns = new String[] {
		JadeHistoryFileColumns.STATUS.getName(), JadeFileColumns.MANDATOR.getName(),
		JadeHistoryFileColumns.TRANSFER_TIMESTAMP.getName(), JadeHistoryFileColumns.OPERATION.getName(),
		JadeHistoryFileColumns.PROTOCOL.getName(), JadeHistoryFileColumns.TARGET_FILENAME.getName(), 
		JadeFileColumns.FILE_SIZE.getName(), JadeFileColumns.SOURCE_HOST.getName(), 
		JadeHistoryFileColumns.TARGET_HOST.getName()};
	
	public JadeFileHistoryTable(List<JadeFilesHistoryDBItem> historyItems, JadeBSMessages messages){
		if(historyItems == null){
			historyItems = new ArrayList<JadeFilesHistoryDBItem>();
		}
		this.historyItems = historyItems;
		this.messages = messages;
		init();
	}
	
	/**
	 * initializes the table component with its dimension and visible columns
	 */
	private void init(){
		if (historyItems != null && historyItems.size() > 0){
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
		// cookie processing not needed anymore, better work with Preferences
//		else if(cookie != null && cookie.getValue().contains(COLUMN_ORDER)){
//			lastColOrder = cookie.getValue().substring(
//					cookie.getValue().indexOf(COLUMN_ORDER), 
//					cookie.getValue().indexOf(ENTRY_DELIMITER, cookie.getValue().indexOf(COLUMN_ORDER)) + 1);
//			String val = lastColOrder.substring(cookie.getValue().indexOf(EQUAL_CHAR) + 1);
//			val = val.substring(0, val.indexOf(ENTRY_DELIMITER));
//			log.debug("ColumnOrder before setting visible columns: " + val);
//			Object[] visibleColsFromCookie = val.split(DELIMITER_REGEX);
//			this.setVisibleColumns(visibleColsFromCookie);
//			log.debug("VisibleColumnsOrder after setting from cookie value: " + this.getVisibleColumns());
//			this.refreshRowCache();
//			this.markAsDirty();
//		}
		setColumnHeaders();
		initConfigurationChangeListeners();
		setSavedPreferences();
	}
	
	private void setSavedPreferences(){
		setPreferencesColumnsWidth();
		getColumnsCollapsed();
		setPreferencesColumnOrder();
	}
	
	private void setPreferencesColumnOrder(){
		String strVc = null;
		strVc = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_HISTORY_TABLE).node(JadeBSConstants.PREF_NODE_ORDER).get(JadeBSConstants.PREF_KEY_ORDER, null);
		if (strVc != null && ((historyItems != null && historyItems.size() > 0) || this.container != null)){
			this.setVisibleColumns((Object[])strVc.split(JadeBSConstants.DELIMITER_REGEX));
			this.refreshRowCache();
			this.markAsDirty();
			log.debug("VisibleColumnsOrder after setting from Preferences: " + createOrderedColumnsString(this.getVisibleColumns()));
		}else if (container != null){
			this.setVisibleColumns(visibleColumns);
		}
	}
	
	private void setPreferencesColumnsWidth(){
		for(Object key : visibleColumns){
			int width = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_HISTORY_TABLE).node(JadeBSConstants.PREF_NODE_WIDTHS).getInt(key.toString(), 0);
			if (width != 0){
				setColumnWidth(key.toString(), width);
				log.debug("setting width of column " + key.toString() + " to " + String.valueOf(width));
			}
		}
	}
	
	/**
	 * sets alternative values for column headers
	 */
	private void setColumnHeaders(){
		setColumnHeader(JadeHistoryFileColumns.STATUS.getName(), 
				messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.STATUS.getName()));
		setColumnHeader(JadeFileColumns.MANDATOR.getName(), 
				messages.getValue(MESSAGE_RESOURCE_BASE + JadeFileColumns.MANDATOR.getName()));
		setColumnHeader(JadeHistoryFileColumns.TRANSFER_TIMESTAMP.getName(), 
				messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.TRANSFER_TIMESTAMP.getName()));
		setColumnHeader(JadeHistoryFileColumns.OPERATION.getName(), 
				messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.OPERATION.getName()));
		setColumnHeader(JadeHistoryFileColumns.PROTOCOL.getName(), 
				messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.PROTOCOL.getName()));
		setColumnHeader(JadeHistoryFileColumns.TARGET_FILENAME.getName(), 
				messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.TARGET_FILENAME.getName()));
		setColumnHeader(JadeFileColumns.FILE_SIZE.getName(), 
				messages.getValue(MESSAGE_RESOURCE_BASE + JadeFileColumns.FILE_SIZE.getName()));
		setColumnHeader(JadeFileColumns.SOURCE_HOST.getName(), 
				messages.getValue(MESSAGE_RESOURCE_BASE + JadeFileColumns.SOURCE_HOST.getName()));
		setColumnHeader(JadeHistoryFileColumns.TARGET_HOST.getName(), 
				messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.TARGET_HOST.getName()));
	}
	
	/**
	 * refresh 
	 * @param locale
	 */
	public void refreshColumnHeaders(Locale locale){
		setColumnHeader(JadeHistoryFileColumns.STATUS.getName(), 
				messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.STATUS.getName(), locale));
		setColumnHeader(JadeFileColumns.MANDATOR.getName(), 
				messages.getValue(MESSAGE_RESOURCE_BASE + JadeFileColumns.MANDATOR.getName(), locale));
		setColumnHeader(JadeHistoryFileColumns.TRANSFER_TIMESTAMP.getName(), 
				messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.TRANSFER_TIMESTAMP.getName(), locale));
		setColumnHeader(JadeHistoryFileColumns.OPERATION.getName(), 
				messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.OPERATION.getName(), locale));
		setColumnHeader(JadeHistoryFileColumns.PROTOCOL.getName(), 
				messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.PROTOCOL.getName(), locale));
		setColumnHeader(JadeHistoryFileColumns.TARGET_FILENAME.getName(), 
				messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.TARGET_FILENAME.getName(), locale));
		setColumnHeader(JadeFileColumns.FILE_SIZE.getName(), 
				messages.getValue(MESSAGE_RESOURCE_BASE + JadeFileColumns.FILE_SIZE.getName(), locale));
		setColumnHeader(JadeFileColumns.SOURCE_HOST.getName(), 
				messages.getValue(MESSAGE_RESOURCE_BASE + JadeFileColumns.SOURCE_HOST.getName(), locale));
		setColumnHeader(JadeHistoryFileColumns.TARGET_HOST.getName(), 
				messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.TARGET_HOST.getName(), locale));
	}
	
	/**
	 * replaces the present container with a new created container related to the given historyItems
	 * 
	 * @param historyItems List of JadeFilesHistoryDBItems
	 */
	public void populateDatasource(List<JadeFilesHistoryDBItem> historyItems){
		this.historyItems = historyItems;
		this.setContainerDataSource(this.container = new JadeFilesHistoryContainer(this.historyItems));
		setSavedPreferences();
		this.fireItemSetChange();
		this.fireValueChange(true);
	}

	/**
	 * a {@link com.vaadin.ui.Table.CellStyleGenerator CellStyleGenerator} to style the representation of the status field
	 * according to the value
	 * 
	 * @author SP
	 *
	 */
	private class StatusCellStyleGenerator implements CellStyleGenerator{
		private static final long serialVersionUID = 1L;

		@Override
		public String getStyle(Table source, Object itemId, Object propertyId) {
			if (itemId != null && propertyId != null) {
				if (JadeHistoryFileColumns.STATUS.getName().equals(propertyId)) {
					if ("success".equals(((JadeFilesHistoryDBItem) itemId).getStatus())) {
						((Label)source.getItem(itemId).getItemProperty(propertyId).getValue()).setStyleName("jadeStatusSuccessLabel");
						return "jadeStatusSuccessLabel";
					} else if ("error".equals(((JadeFilesHistoryDBItem) itemId).getStatus())) {
						((Label)source.getItem(itemId).getItemProperty(propertyId).getValue()).setStyleName("jadeStatusErrorLabel");
						return "jadeStatusErrorLabel";
					}
				}
			}
			return null;
		}
	}
	
	private void initConfigurationChangeListeners(){
		this.addColumnReorderListener(new Table.ColumnReorderListener() {
 			private static final long serialVersionUID = 1L;

			@Override
			public void columnReorder(ColumnReorderEvent event) {
				Object[] reorderedVisibleColumns = JadeFileHistoryTable.this.getVisibleColumns();
				setOrderedColumnsPreferencesNode(reorderedVisibleColumns);
//				String newCookieValue = createCookieOrderValueString(reorderedVisibleColumns);
//				if(lastColOrder != null && cookie.getValue().contains(lastColOrder)){
//					cookie.setValue(cookie.getValue().replace(lastColOrder, newCookieValue));
//				}else if (sessionsLastColOrder != null && cookie.getValue().contains(sessionsLastColOrder)){
//					cookie.setValue(cookie.getValue().replace(sessionsLastColOrder, newCookieValue));
//				}else{
//					cookie.setValue(cookie.getValue() + newCookieValue);
//				}
//				sessionsLastColOrder = newCookieValue;
			}
		});

		this.addColumnResizeListener(new Table.ColumnResizeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void columnResize(ColumnResizeEvent event) {
		        // save all column widths, to make sure that unresized columns are displayed.
		        // correctly after reload. Because of default size = -1 all columns use the 
		        // available space equally. If some columns are resized and others not, the 
		        // unresized columns would go on using all the available space, which can be more
		        // than before changing width of the others.
		        for(Object col : visibleColumns){
			        prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_HISTORY_TABLE).node(JadeBSConstants.PREF_NODE_WIDTHS).putInt(col.toString(), getColumnWidth(col));
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
	
	private void getColumnsCollapsed(){
		if (container != null) {
			for (Object column : container.getContainerPropertyIds()) {
				setColumnCollapsed(
						column,
						prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_HISTORY_TABLE)
						.node(JadeBSConstants.PREF_NODE_COLLAPSE).getBoolean(column.toString(), false));
			}
		}
	}
	
//	private String createCookieOrderValueString(Object[] orderedCols){
//		StringBuilder sb = new StringBuilder();
//		sb.append(COLUMN_ORDER).append(EQUAL_CHAR);
//		sb.append(createOrderedColumnsString(orderedCols));
//		sb.append(ENTRY_DELIMITER);
//		return sb.toString();
//	}
	
	private String createOrderedColumnsString(Object[] orderedCols){
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Object obj : orderedCols){
			if (!first){
				sb.append(JadeBSConstants.DELIMITER);
			}
			sb.append(obj.toString());
			first = false;
		}
		return sb.toString();
	}
	
	private void setOrderedColumnsPreferencesNode(Object[] orderedCols){
		prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_HISTORY_TABLE).node(JadeBSConstants.PREF_NODE_ORDER).put(JadeBSConstants.PREF_KEY_ORDER, createOrderedColumnsString(orderedCols));
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
			log.error("error while flushing Preferences for Column Order: ", e);
		}
	}
	
	public void resetColumnWidths(){
		for(Object key : this.getVisibleColumns()){
			// width = -1 means the column takes all the available space 
			setColumnWidth(key.toString(), -1);
	        prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_HISTORY_TABLE).node(JadeBSConstants.PREF_NODE_WIDTHS).putInt(key.toString(), -1);
		}
		this.refreshRowCache();
		this.markAsDirty();
	}

   @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        handleColumnCollapseEvent(variables);
        super.changeVariables(source, variables);
    }

    /**
     * Handles the column collapse event sent by the client.
     *
     * @param variables
     */
    private void handleColumnCollapseEvent(Map<String, Object> variables) {
        if (variables.containsKey("collapsedcolumns")) {
            String[] collapsedCollumns  = (String[]) variables.get("collapsedcolumns");
            Object[] visibleColumns = getVisibleColumns();
            for (int j = 0; j < visibleColumns.length; j++) {
                boolean isCollapsed = ArrayUtils.contains(collapsedCollumns , variables.get(visibleColumns[j]));
                if (isColumnCollapsed(visibleColumns[j]) != isCollapsed){
                    fireEvent(new ColumnCollapseEvent(this, visibleColumns[j], isColumnCollapsed(visibleColumns[j])));
                }
            }
        }else{
        	
        }
    }
   
    public void addColumnCollapseListener(ColumnCollapseListener listener){
        addListener("columnCollapse", ColumnCollapseEvent.class, listener, ColumnCollapseEvent.COLUMN_COLLAPSE_METHOD);
    }
   
    public void removeColumnCollapseListener(ColumnCollapseListener listener){
        removeListener("columnCollapse", ColumnCollapseEvent.class, listener);
    }
	   
    public static class ColumnCollapseEvent extends Component.Event {
        public static final java.lang.reflect.Method COLUMN_COLLAPSE_METHOD;

        static {
            try {
                COLUMN_COLLAPSE_METHOD = ColumnCollapseListener.class.getDeclaredMethod("columnCollapse",
                                new Class[] { ColumnCollapseEvent.class });
            } catch (final java.lang.NoSuchMethodException e) {
                // This should never happen
                throw new java.lang.RuntimeException(e);
            }
        }

        private final boolean collapsed;
        private final Object columnPropertyId;

        /**
         * Constructor
         *
         * @param source
         *            The source of the event
         * @param propertyId
         *            The columns property id
         * @param collapsed
         *            Indicates if colum was collapsed or elapsed
         */
        public ColumnCollapseEvent(Component source, Object propertyId, boolean collapsed) {
            super(source);
            this.collapsed = collapsed;
            columnPropertyId = propertyId;
        }

        /**
         * Get the column property id of the column that was resized.
         *
         * @return The column property id
         */
        public Object getPropertyId() {
            return columnPropertyId;
        }
       
        public boolean getCollapsed(){
            return collapsed;
        }
       
    }
	   
    /**
     * Interface for listening to column collapse events.
     */
    public interface ColumnCollapseListener extends Serializable {

        /**
         * This method is triggered when the column has been collapsed or elapsed
         *
         * @param event
         *            The event which contains the column property id and new collapse state
         */
        public void columnCollapse(ColumnCollapseEvent event);
    }
}


