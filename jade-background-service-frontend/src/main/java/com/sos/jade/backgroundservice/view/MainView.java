package com.sos.jade.backgroundservice.view;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import sos.ftphistory.db.JadeFilesDBItem;
import sos.ftphistory.db.JadeFilesHistoryDBItem;
import sos.ftphistory.db.JadeFilesHistoryDBLayer;

import com.sos.JSHelper.concurrent.SOSThreadPoolExecutor;
import com.sos.jade.backgroundservice.listeners.IJadeFileListener;
import com.sos.jade.backgroundservice.listeners.impl.JadeFileListenerProxy;
import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.sos.jade.backgroundservice.view.components.DetailLayout;
import com.sos.jade.backgroundservice.view.components.JadeFilesHistoryTable;
import com.sos.jade.backgroundservice.view.components.JadeFilesTable;
import com.sos.jade.backgroundservice.view.components.JadeMenuBar;
import com.sos.jade.backgroundservice.view.components.JadeMixedTable;
import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

public class MainView extends CustomComponent{
	
	private static final long serialVersionUID = 6368275374953898482L;
//	private String absolutePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	private JadeFilesHistoryDBLayer jadeFilesHistoryDBLayer;
	private List<JadeFilesHistoryDBItem> historyItems;
	private List<JadeFilesDBItem> fileItems;
	private VerticalSplitPanel vSplitPanel;
	private JadeMixedTable tblMixed;
	private Item markedRow;
	private IJadeFileListener fileListener;
	private DetailLayout details;
	private Float lastSplitPosition;
	private boolean first = true;
	private JadeBSMessages messages = new JadeBSMessages("JADEBSMessages", Locale.getDefault());

	public MainView() {
		this.fileListener = new JadeFileListenerProxy(this);
		initJadeFilesHistoryDbLayer();
		initComponents();
		
        tblMixed.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void itemClick(ItemClickEvent event) {
				if (markedRow == null || !markedRow.equals(event.getItemId())){
					JadeFilesHistoryDBItem historyItem = (JadeFilesHistoryDBItem)event.getItemId();
					details.setLabelValues(historyItem);
				}
				toggleTableVisiblity(event.getItem());
			}
		});
	}
	
	private void initComponents(){
        final VerticalLayout vLayout = new VerticalLayout();
        vLayout.setMargin(true);
        vLayout.setSpacing(true);
        vLayout.setSizeFull();
        vLayout.addStyleName("jadeMainVLayout");
        setCompositionRoot(vLayout);

        final HorizontalLayout hLayout = new HorizontalLayout();
        hLayout.setWidth("100%");
        hLayout.setHeight(75.0f, Unit.PIXELS);
        vLayout.addComponent(hLayout);

        HorizontalLayout hlMenuBar = new HorizontalLayout();
        hlMenuBar.setHeight(25.0f, Unit.PIXELS);
        hlMenuBar.setWidth("100%");
        hlMenuBar.addStyleName("jadeMenuBarLayout");
        vLayout.addComponent(hlMenuBar);

        final VerticalLayout vRest = new VerticalLayout();
        vRest.setSizeFull();
        vLayout.addComponent(vRest);

        vLayout.setExpandRatio(vRest, 1);

        final Image imgTitle = new Image();
        ThemeResource titleResource = new ThemeResource("images/job_scheduler_rabbit_circle_60x60.gif");
		imgTitle.setSource(titleResource);
        hLayout.addComponent(imgTitle);

//        final Label lblTitle = new Label("JADE background service");
        final Label lblTitle = new Label(messages.getValue("MainView.title"));
        lblTitle.setStyleName("jadeTitleLabel");
        hLayout.addComponent(lblTitle);
        
        final Image imgLogo = new Image();
        ThemeResource trLogo = new ThemeResource("images/sos_logo_84x60_transparent.png");
        imgLogo.setSource(trLogo);

        hLayout.addComponent(imgLogo);
        hLayout.setComponentAlignment(imgLogo, Alignment.MIDDLE_RIGHT);
        hLayout.setExpandRatio(imgTitle, 1);
        hLayout.setExpandRatio(lblTitle, 8);
        hLayout.setExpandRatio(imgLogo, 1);
        
        JadeMenuBar jmb = initMenuBar();
        hlMenuBar.addComponent(jmb);
        
        vSplitPanel = initVerticalSplitPanel();
        vRest.addComponent(vSplitPanel);
        vRest.setComponentAlignment(vSplitPanel, Alignment.TOP_LEFT);
        
        initTables();
        details = new DetailLayout();
        details.setVisible(false);
        vSplitPanel.addComponent(details);
	}
	
	private void initTables(){
        tblMixed = new JadeMixedTable(historyItems);
        vSplitPanel.addComponent(tblMixed);
        tblMixed.setVisible(true);
	}
	
	private VerticalSplitPanel initVerticalSplitPanel(){
		VerticalSplitPanel vsp = new VerticalSplitPanel();
		vsp.setSizeFull();
		vsp.setSplitPosition(100);
		return vsp;
	}
	
	private JadeMenuBar initMenuBar(){
		JadeMenuBar jmb = new JadeMenuBar();
		return jmb;
	}
	
	private void toggleTableVisiblity(Item item){
		if(markedRow != null && markedRow.equals(item)){
			markedRow = null;
			details.setVisible(false);
			setSplitPosition();
		}else if (markedRow != null && !markedRow.equals(item)){
			markedRow = item;
			details.setVisible(true);
		}else{
			markedRow = item;
			details.setVisible(true);
			setSplitPosition();
		}
	}
	
	private void setSplitPosition(){
		Float newLastSplitPosition = vSplitPanel.getSplitPosition();
		if(first){
			first = false;
			vSplitPanel.setSplitPosition(65);
		}else{
			vSplitPanel.setSplitPosition(lastSplitPosition);
		}
		lastSplitPosition = newLastSplitPosition;
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
							fileListener.closeJadeFilesHistoryDbSession();
					        tblMixed.populateDatasource(historyItems);
					//        tblMixed.refreshRowCache();
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
	
	public void setDetailViewVisible(boolean visible){
		details.setVisible(visible);
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

	public JadeMixedTable getTblMixed() {
		return tblMixed;
	}

}
