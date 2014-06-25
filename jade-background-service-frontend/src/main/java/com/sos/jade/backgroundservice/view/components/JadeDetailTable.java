package com.sos.jade.backgroundservice.view.components;

import java.util.Locale;

import sos.ftphistory.db.JadeFilesHistoryDBItem;

import com.sos.jade.backgroundservice.data.JadeDetailsContainer;
import com.sos.jade.backgroundservice.enums.JadeHistoryFileColumns;
import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.vaadin.ui.Table;

public class JadeDetailTable extends Table {
	private static final long serialVersionUID = 1L;
	private JadeFilesHistoryDBItem detailItem;
	private JadeBSMessages messages;
	private JadeDetailsContainer container;
	private static final int PAGE_LENGTH = 20;
	
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
		setColumnReorderingAllowed(true);
		setColumnCollapsingAllowed(true);
		setSortEnabled(true);
		setMultiSelect(false);
		setPageLength(PAGE_LENGTH);
		addStyleName("jadeBsTable");
		setColumnAlignment(JadeHistoryFileColumns.STATUS.getName(), Align.CENTER);
		enableContentRefreshing(true);
		if (container != null){
			this.setVisibleColumns(visibleColumns);
		}
		setColumnHeaders();
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
	
}
