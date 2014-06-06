package com.sos.jade.backgroundservice.view;

import java.util.List;
import java.util.concurrent.TimeUnit;

import sos.ftphistory.db.JadeFilesDBItem;
import sos.ftphistory.db.JadeFilesDBLayer;
import sos.ftphistory.db.JadeFilesHistoryDBItem;
import sos.ftphistory.db.JadeFilesHistoryDBLayer;

import com.sos.JSHelper.concurrent.SOSThreadPoolExecutor;
import com.sos.jade.backgroundservice.enums.JadeFileColumns;
import com.sos.jade.backgroundservice.enums.TableType;
import com.sos.jade.backgroundservice.listeners.IJadeFileListener;
import com.sos.jade.backgroundservice.listeners.impl.JadeFileListenerProxy;
import com.sos.jade.backgroundservice.view.components.JadeFilesHistoryTable;
import com.sos.jade.backgroundservice.view.components.JadeFilesTable;
import com.sos.jade.backgroundservice.view.components.JadeMenuBar;
import com.sos.jade.backgroundservice.view.components.JadeMixedTable;
import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

public class MainView extends CustomComponent{
	
	private static final long serialVersionUID = 6368275374953898482L;
	private String absolutePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	private JadeFilesDBLayer jadeFilesDBLayer;
	private JadeFilesHistoryDBLayer jadeFilesHistoryDBLayer;
	private List<JadeFilesHistoryDBItem> historyItems;
	private List<JadeFilesDBItem> fileItems;
	private VerticalSplitPanel vSplitPanel;
	private JadeFilesHistoryTable tblHistory;
	private JadeFilesTable tblFiles;
	private JadeMixedTable tblMixed;
	private Item markedRow;
	private IJadeFileListener fileListener;

	public MainView() {
		this.fileListener = new JadeFileListenerProxy(this);
//		initJadeFileDbLayer();
		initJadeFilesHistoryDbLayer();
		initComponents();
		
        tblFiles.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void itemClick(ItemClickEvent event) {
				if (markedRow == null || !markedRow.equals(event.getItemId())){
					// then query all depending jadeHistoryFilesItems
					if(event.getItem().getItemProperty(JadeFileColumns.ID.getName()) != null){
						final Long id = (Long)event.getItem().getItemProperty(JadeFileColumns.ID.getName()).getValue();
		                try {
							fileListener.getFileHistoryByIdFromLayer(id);
		                } catch (final Exception e) {
		                	fileListener.getException(e);
		                }
				        tblHistory.populateDatasource(historyItems);
				        tblHistory.markAsDirty();

//						SOSThreadPoolExecutor objTPE = new SOSThreadPoolExecutor(1);
//						objTPE.runTask(
//							new Thread() {
//				            @Override
//				            public void run() {
//				                try {
//									fileListener.getFileHistoryByIdFromLayer(id);
//				                } catch (final Exception e) {
//				                	fileListener.getException(e);
//				                }
//								UI.getCurrent().access(new Runnable() {
//									@Override
//									public void run() {
//								        tblHistory.populateDatasource(historyItems);
//								        tblHistory.markAsDirty();
//									}
//								});
//				            };
//				        });
//					try {
//						objTPE.shutDown();
//						objTPE.objThreadPool.awaitTermination(1, TimeUnit.DAYS);
//					}
//					catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//						new Thread(){
//							public void run(){
//								fileListener.getFileHistoryByIdFromLayer(id);
//								
//								UI.getCurrent().access(new Runnable() {
//									@Override
//									public void run() {
//								        tblHistory.populateDatasource(historyItems);
//								        tblHistory.markAsDirty();
//									}
//								});
//							}
//						}.start();
					}
				}
				toggleTableVisiblity(event.getItem());
			}
		});
	}
	
	private void initComponents(){
        final VerticalLayout vLayout = new VerticalLayout();
        vLayout.setMargin(true);
        vLayout.setSizeFull();
        setCompositionRoot(vLayout);

        final VerticalLayout vTitle = new VerticalLayout();
        vTitle.setHeight(70.0f, Unit.PIXELS);
        vLayout.addComponent(vTitle);

        final VerticalLayout vRest = new VerticalLayout();
        vRest.setSizeFull();

        vLayout.addComponent(vRest);
        vLayout.setExpandRatio(vTitle, 1);
        vLayout.setExpandRatio(vRest, 10);

        final HorizontalLayout hLayout = new HorizontalLayout();
        vTitle.addComponent(hLayout);

        final Image imgTitle = new Image();
        ThemeResource titleResource = new ThemeResource("images/job_scheduler_rabbit_circle_60x60.gif");
		imgTitle.setSource(titleResource);
        hLayout.addComponent(imgTitle);

        final Label lblTitle = new Label("JADE background service");
        lblTitle.setStyleName("jadeTitleLabel");
        hLayout.addComponent(lblTitle);
        
        JadeMenuBar jmb = initMenuBar();
        vRest.addComponent(jmb);
        
        vSplitPanel = initVerticalSplitPanel();
        vRest.addComponent(vSplitPanel);
        
        vRest.setExpandRatio(jmb, 1);
		vRest.setExpandRatio(vSplitPanel, 19);

        initTables();
	}
	
	private void initTables(){
        tblFiles = (JadeFilesTable)initTable(TableType.FILE, fileItems);
        tblHistory = (JadeFilesHistoryTable)initTable(TableType.HISTORY, null);
        tblMixed = (JadeMixedTable)initTable(TableType.MIXED, historyItems);
        vSplitPanel.addComponent(tblMixed);
        tblMixed.setVisible(true);
//        vSplitPanel.addComponent(tblFiles);
//        vSplitPanel.addComponent(tblHistory);
//        tblFiles.setVisible(false);
//        tblHistory.setVisible(false);
	}
	
	@SuppressWarnings("unchecked")
	private Table initTable(TableType tableType, Object data){
		switch(tableType){
		case FILE:
			return new JadeFilesTable((List<JadeFilesDBItem>)data);
		case HISTORY:
			return new JadeFilesHistoryTable((List<JadeFilesHistoryDBItem>)data);
		case MIXED:
			return new JadeMixedTable((List<JadeFilesHistoryDBItem>)data);
		}
		return null;
		
	}
	
	private VerticalSplitPanel initVerticalSplitPanel(){
		VerticalSplitPanel vsp = new VerticalSplitPanel();
		vsp.setSizeFull();
		return vsp;
	}
	
	private JadeMenuBar initMenuBar(){
		JadeMenuBar jmb = new JadeMenuBar();
		return jmb;
	}
	
	private void toggleTableVisiblity(Item item){
		if(markedRow != null && markedRow.equals(item)){
			markedRow = null;
			tblHistory.setVisible(false);
		}else{
			markedRow = item;
			tblHistory.setVisible(true);
		}
	}
	
	private void initJadeFileDbLayer(){
		SOSThreadPoolExecutor objTPE = new SOSThreadPoolExecutor(1);
		objTPE.runTask(
			new Thread() {
	            @Override
	            public void run() {
	                try {
	                	fileListener.filterJadeFiles(null);
	                } catch (final Exception e) {
	                	fileListener.getException(e);
	                }
					UI.getCurrent().access(new Runnable() {
						@Override
						public void run() {
					        tblFiles.populateDatasource(fileItems);
					        tblFiles.markAsDirty();
					        tblFiles.setVisible(true);
						}
					});
	            };
	        });
		try {
			objTPE.shutDown();
			objTPE.objThreadPool.awaitTermination(1, TimeUnit.DAYS);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void initJadeFilesHistoryDbLayer(){
		SOSThreadPoolExecutor objTPE = new SOSThreadPoolExecutor(1);
		objTPE.runTask(
			new Thread() {
	            @Override
	            public void run() {
	                try {
	                	fileListener.filterJadeFilesHistory(null);
	                } catch (final Exception e) {
	                	fileListener.getException(e);
	                }
					UI.getCurrent().access(new Runnable() {
						@Override
						public void run() {
					        tblMixed.populateDatasource(historyItems);
					        tblMixed.markAsDirty();
					        tblMixed.setVisible(true);
						}
					});
	            };
	        });
		try {
			objTPE.shutDown();
			objTPE.objThreadPool.awaitTermination(1, TimeUnit.DAYS);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Item getMarkedRow() {
		return markedRow;
	}

	public void setMarkedRow(Item markedRow) {
		this.markedRow = markedRow;
	}
	
	public void setHistoryTableNotVisible(){
		tblHistory.setVisible(false);
	}

	public List<JadeFilesDBItem> getFileItems() {
		return fileItems;
	}

	public void setFileItems(List<JadeFilesDBItem> fileItems) {
		this.fileItems = fileItems;
	}

	public List<JadeFilesHistoryDBItem> getHistoryItems() {
		return historyItems;
	}

	public void setHistoryItems(List<JadeFilesHistoryDBItem> historyItems) {
		this.historyItems = historyItems;
	}

	public JadeFilesHistoryTable getTblHistory() {
		return tblHistory;
	}

	public JadeFilesTable getTblFiles() {
		return tblFiles;
	}

	public JadeMixedTable getTblMixed() {
		return tblMixed;
	}

}
