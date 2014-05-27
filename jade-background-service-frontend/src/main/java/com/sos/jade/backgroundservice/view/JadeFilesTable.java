package com.sos.jade.backgroundservice.view;

import java.util.List;

import sos.ftphistory.db.JadeFilesDBItem;

import com.sos.jade.backgroundservice.data.JadeFilesBeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;

public class JadeFilesTable extends Table{

	private static final long serialVersionUID = 2134585331362934124L;
	private List<JadeFilesDBItem> fileItems;
	private BeanItemContainer<JadeFilesDBItem> container;
	public JadeFilesTable(List<JadeFilesDBItem> fileItems){
		this.fileItems = fileItems;
		init();
	}
	
	private void init(){
		this.setContainerDataSource(this.container = (new JadeFilesBeanContainer()).createJadeFilesContainer(fileItems));
		setSizeUndefined();
		setSelectable(true);
		setImmediate(true);
		setEditable(false);
		setColumnReorderingAllowed(true);
//		for (JadeFileColumns col : JadeFileColumns.values()){
//			setColumnHeader(col.name(), col.getName());
//		}
	}
	
	public void populateDatasource(List<JadeFilesDBItem> fileItems){
		this.fileItems = fileItems;
		this.setContainerDataSource(this.container = (new JadeFilesBeanContainer()).createJadeFilesContainer(this.fileItems));
	}
}
