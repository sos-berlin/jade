package com.sos.jade.backgroundservice.view.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sos.ftphistory.db.JadeFilesHistoryDBItem;

import com.sos.jade.backgroundservice.data.JadeFilesHistoryContainer;
import com.sos.jade.backgroundservice.enums.JadeFileColumns;
import com.sos.jade.backgroundservice.enums.JadeHistoryFileColumns;
import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class JadeMixedTable extends Table{

	private static final long serialVersionUID = 2134585331362934124L;
	private List<JadeFilesHistoryDBItem> historyItems;
	private JadeFilesHistoryContainer container;
	private static final int PAGE_LENGTH = 10;
	private JadeBSMessages messages = new JadeBSMessages("JADEBSMessages", Locale.getDefault());
	
	private static final Object[] visibleColumns = new String[] {
		JadeHistoryFileColumns.STATUS.getName(), JadeFileColumns.MANDATOR.getName(),
		JadeHistoryFileColumns.TRANSFER_TIMESTAMP.getName(), JadeHistoryFileColumns.OPERATION.getName(),
		JadeHistoryFileColumns.PROTOCOL.getName(), JadeHistoryFileColumns.TARGET_FILENAME.getName(), 
		JadeFileColumns.FILE_SIZE.getName(), JadeFileColumns.SOURCE_HOST.getName(), 
		JadeHistoryFileColumns.TARGET_HOST.getName()};
	
	public JadeMixedTable(List<JadeFilesHistoryDBItem> historyItems){
		if(historyItems == null){
			historyItems = new ArrayList<JadeFilesHistoryDBItem>();
		}
		this.historyItems = historyItems;
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
		setPageLength(PAGE_LENGTH);
		addStyleName("jadeBsTable");
		setColumnAlignment(JadeHistoryFileColumns.STATUS.getName(), Align.CENTER);
		setCellStyleGenerator(new StatusCellStyleGenerator());
		enableContentRefreshing(true);
		if (container != null){
			this.setVisibleColumns(visibleColumns);
		}
		setColumnHeaders();
	}
	
	private void setColumnHeaders(){
		setColumnHeader(JadeHistoryFileColumns.STATUS.getName(), messages.getValue("MixedTable.status"));
		setColumnHeader(JadeFileColumns.MANDATOR.getName(), messages.getValue("MixedTable.mandator"));
		setColumnHeader(JadeHistoryFileColumns.TRANSFER_TIMESTAMP.getName(), messages.getValue("MixedTable.transferTimestamp"));
		setColumnHeader(JadeHistoryFileColumns.OPERATION.getName(), messages.getValue("MixedTable.operation"));
		setColumnHeader(JadeHistoryFileColumns.PROTOCOL.getName(), messages.getValue("MixedTable.protocol"));
		setColumnHeader(JadeHistoryFileColumns.TARGET_FILENAME.getName(), messages.getValue("MixedTable.targetFilename"));
		setColumnHeader(JadeFileColumns.FILE_SIZE.getName(), messages.getValue("MixedTable.fileSize"));
		setColumnHeader(JadeFileColumns.SOURCE_HOST.getName(), messages.getValue("MixedTable.sourceHost"));
		setColumnHeader(JadeHistoryFileColumns.TARGET_HOST.getName(), messages.getValue("MixedTable.targetHost"));
	}
	
	/**
	 * replaces the present container with a new created container related to the given historyItems
	 * 
	 * @param historyItems List of JadeFilesHistoryDBItems
	 */
	public void populateDatasource(List<JadeFilesHistoryDBItem> historyItems){
		this.historyItems = historyItems;
		this.setContainerDataSource(this.container = new JadeFilesHistoryContainer(this.historyItems));
		this.setVisibleColumns(visibleColumns);
	}

	/**
	 * a {@link com.vaadin.ui.Table.CellStyleGenrator CellStyleGenerator} to style the representation of the status field
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
    
}


