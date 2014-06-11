package com.sos.jade.backgroundservice.view.components;

import java.util.ArrayList;
import java.util.List;

import sos.ftphistory.db.JadeFilesHistoryDBItem;

import com.sos.jade.backgroundservice.data.JadeFilesHistoryContainer;
import com.sos.jade.backgroundservice.enums.JadeFileColumns;
import com.sos.jade.backgroundservice.enums.JadeHistoryFileColumns;
import com.vaadin.data.Item;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class JadeMixedTable extends Table{

	private static final long serialVersionUID = 2134585331362934124L;
	private List<JadeFilesHistoryDBItem> historyItems;
	private JadeFilesHistoryContainer container;
	private static final int PAGE_LENGTH = 10;
	
	private static final Object[] visibleColumns = new String[] {
		JadeHistoryFileColumns.STATUS.getName(), JadeFileColumns.MANDATOR.getName(),
		JadeHistoryFileColumns.TRANSFER_TIMESTAMP.getName(), JadeHistoryFileColumns.OPERATION.getName(),
		JadeHistoryFileColumns.PROTOCOL.getName(), JadeHistoryFileColumns.TARGET_FILENAME.getName(), 
		JadeFileColumns.FILE_SIZE.getName(), JadeFileColumns.SOURCE_HOST.getName(), 
		JadeHistoryFileColumns.TARGET_HOST.getName()
	};
	
//	private static final Object[] visibleColumns = new String[] {
//		JadeHistoryFileColumns.CREATED.getName(), JadeHistoryFileColumns.CREATED_BY.getName(),
//		JadeHistoryFileColumns.TRANSFER_TIMESTAMP.getName(), JadeHistoryFileColumns.LAST_ERROR_MESSAGE.getName(), 
//		JadeHistoryFileColumns.LOG_FILENAME.getName(), JadeHistoryFileColumns.MODIFIED.getName(), 
//		JadeHistoryFileColumns.MODIFIED_BY.getName(), JadeHistoryFileColumns.OPERATION.getName(), 
//		JadeHistoryFileColumns.PROTOCOL.getName(), JadeHistoryFileColumns.PORT.getName(), 
//		JadeHistoryFileColumns.STATUS.getName(), JadeHistoryFileColumns.TARGET_DIR.getName(), 
//		JadeHistoryFileColumns.TARGET_FILENAME.getName(), JadeHistoryFileColumns.TARGET_HOST.getName(), 
//		JadeHistoryFileColumns.TARGET_HOST_IP.getName(), JadeHistoryFileColumns.TARGET_USER.getName(), 
//		JadeHistoryFileColumns.JUMP_PROTOCOL.getName(), JadeHistoryFileColumns.JUMP_PORT.getName(), 
//		JadeHistoryFileColumns.JUMP_HOST.getName(), JadeHistoryFileColumns.JUMP_HOST_IP.getName(),
//		JadeHistoryFileColumns.JUMP_USER.getName()};

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
		setColumnAlignment(JadeHistoryFileColumns.STATUS.getName(), Align.CENTER);
		setCellStyleGenerator(new StatusCellStyleGenerator());
		enableContentRefreshing(true);
		if (container != null){
			this.setVisibleColumns(visibleColumns);
		}
	}
	
	public void populateDatasource(List<JadeFilesHistoryDBItem> historyItems){
		this.historyItems = historyItems;
		this.setContainerDataSource(this.container = new JadeFilesHistoryContainer(this.historyItems));
		this.setVisibleColumns(visibleColumns);
	}

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


