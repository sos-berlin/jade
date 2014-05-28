package com.sos.jade.backgroundservice.view.components;

import java.util.ArrayList;
import java.util.List;

import sos.ftphistory.db.JadeFilesDBItem;

import com.sos.jade.backgroundservice.data.JadeFilesBeanContainer;
import com.sos.jade.backgroundservice.enums.JadeFileColumns;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;

public class JadeFilesTable extends Table{

	private static final long serialVersionUID = 2134585331362934124L;
	private List<JadeFilesDBItem> fileItems;
	private BeanItemContainer<JadeFilesDBItem> container;
	private static final Object[] visibleColumns = new String[] {
		JadeFileColumns.MANDATOR.getName(), JadeFileColumns.CREATED.getName(), 
		JadeFileColumns.CREATED_BY.getName(), JadeFileColumns.JADE_FILE_MODIFIED.getName(), 
		JadeFileColumns.JADE_FILE_MODIFIED_BY.getName(), JadeFileColumns.SOURCE_DIR.getName(),
		JadeFileColumns.SOURCE_FILENAME.getName(), JadeFileColumns.SOURCE_HOST.getName(), 
		JadeFileColumns.SOURCE_HOST_IP.getName(), JadeFileColumns.SOURCE_USER.getName()};
	
	public JadeFilesTable(List<JadeFilesDBItem> fileItems){
		if(fileItems == null){
			fileItems = new ArrayList<JadeFilesDBItem>();
		}
		this.fileItems = fileItems;
		init();
	}
	
	private void init(){
		if(fileItems != null && fileItems.size() > 0){
			this.setContainerDataSource(this.container = (new JadeFilesBeanContainer()).createJadeFilesContainer(fileItems));
		}
		setSizeUndefined();
		setSelectable(true);
		setImmediate(true);
		setEditable(false);
		setColumnReorderingAllowed(true);
		if (container != null){
			this.setVisibleColumns(visibleColumns);
		}
	}
	
	public void populateDatasource(List<JadeFilesDBItem> fileItems){
		this.fileItems = fileItems;
		this.setContainerDataSource(this.container = (new JadeFilesBeanContainer()).createJadeFilesContainer(this.fileItems));
		this.setVisibleColumns(visibleColumns);
	}
}
