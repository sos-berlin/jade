package com.sos.jade.backgroundservice.view.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sos.ftphistory.db.JadeFilesHistoryDBItem;

import com.sos.jade.backgroundservice.data.JadeFilesHistoryContainer;
import com.sos.jade.backgroundservice.data.JadeHistoryBeanContainer;
import com.sos.jade.backgroundservice.enums.JadeFileColumns;
import com.sos.jade.backgroundservice.enums.JadeHistoryFileColumns;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.ThemeResource;
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
		if (container != null){
			this.setVisibleColumns(visibleColumns);
		}
	}
	
	public void populateDatasource(List<JadeFilesHistoryDBItem> historyItems){
		this.historyItems = historyItems;
		this.setContainerDataSource(this.container = new JadeFilesHistoryContainer(historyItems));
		this.setVisibleColumns(visibleColumns);
		Converter converter = new Converter<String, ThemeResource>() {

			private static final long serialVersionUID = 1L;

			@Override
			public ThemeResource convertToModel(String value,
					Class<? extends ThemeResource> targetType, Locale locale)
					throws com.vaadin.data.util.converter.Converter.ConversionException {
				if("success".equals(value)){
					return new ThemeResource("../images/status_green.png");
				}else if("error".equals(value)){
					return new ThemeResource("../images/status_red.png");
				}
				return null;
			}

			@Override
			public String convertToPresentation(ThemeResource value,
					Class<? extends String> targetType, Locale locale)
					throws com.vaadin.data.util.converter.Converter.ConversionException {
				if(value != null && value.getResourceId().equals("../images/status_green.png")){
					return "success";
				}else if (value != null && value.getResourceId().equals("../images/status_red.png")){
					return "error";
				}
				return null;
			}

			@Override
			public Class<ThemeResource> getModelType() {
				return ThemeResource.class;
			}

			@Override
			public Class<String> getPresentationType() {
				return String.class;
			}
		};
		this.setConverter(converter);
	}
}
