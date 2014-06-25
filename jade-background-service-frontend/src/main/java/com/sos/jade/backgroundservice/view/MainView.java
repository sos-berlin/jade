package com.sos.jade.backgroundservice.view;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import sos.ftphistory.db.JadeFilesDBItem;
import sos.ftphistory.db.JadeFilesHistoryDBItem;
import sos.ftphistory.db.JadeFilesHistoryDBLayer;

import com.sos.JSHelper.concurrent.SOSThreadPoolExecutor;
import com.sos.jade.backgroundservice.BackgroundserviceUI;
import com.sos.jade.backgroundservice.data.JadeDetailsContainer;
import com.sos.jade.backgroundservice.listeners.IJadeFileListener;
import com.sos.jade.backgroundservice.listeners.impl.JadeFileListenerProxy;
import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.sos.jade.backgroundservice.view.components.DetailLayout;
import com.sos.jade.backgroundservice.view.components.JadeDetailTable;
import com.sos.jade.backgroundservice.view.components.JadeMenuBar;
import com.sos.jade.backgroundservice.view.components.JadeMixedTable;
import com.sos.jade.backgroundservice.view.components.filter.FilterLayoutWindow;
import com.sos.jade.backgroundservice.view.components.filter.JadeFilesHistoryFilterLayout;
import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.MouseEvents;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

public class MainView extends CustomComponent{
	
	private static final long serialVersionUID = 6368275374953898482L;
	private JadeFilesHistoryDBLayer jadeFilesHistoryDBLayer;
	private List<JadeFilesHistoryDBItem> historyItems;
	private List<JadeFilesDBItem> fileItems;
	private VerticalSplitPanel vSplitPanel;
	private JadeMixedTable tblMixed;
	private JadeDetailTable tblDetails;
	private Item markedRow;
	private IJadeFileListener fileListener;
	private DetailLayout details;
	private Float lastSplitPosition;
	private boolean first = true;
    private Image imgDe;
    private Image imgGb;
    private Image imgUs;
    private Image imgEs;
    private JadeMenuBar jmb;
    private Label lblTitle;

	private JadeBSMessages messages;

	public MainView() {
		this.messages = new JadeBSMessages("JADEBSMessages", VaadinSession.getCurrent().getLocale());
		setImmediate(true);
		this.fileListener = new JadeFileListenerProxy(this);
		initJadeFilesHistoryDbLayer();
		initComponents();
		
        tblMixed.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void itemClick(ItemClickEvent event) {
				if (markedRow == null || !markedRow.equals(event.getItemId())){
					JadeFilesHistoryDBItem historyItem = (JadeFilesHistoryDBItem)event.getItemId();
//					details.setLabelValues(historyItem);
					tblDetails.populateDatasource(historyItem);
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

        lblTitle = new Label(messages.getValue("MainView.title", VaadinSession.getCurrent().getLocale()));
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
        
        jmb = initMenuBar();
        hlMenuBar.addComponent(jmb);
        
        imgDe = initLanguageIcon("images/de_marble.png");
        imgGb = initLanguageIcon("images/gb_marble.png");
        imgUs = initLanguageIcon("images/us_marble.png");
        imgEs = initLanguageIcon("images/es.png");

		hlMenuBar.addComponent(imgDe);
		hlMenuBar.addComponent(imgUs);
		hlMenuBar.addComponent(imgGb);
		hlMenuBar.addComponent(imgEs);

		hlMenuBar.setExpandRatio(jmb, 1);
		hlMenuBar.setComponentAlignment(imgDe, Alignment.MIDDLE_RIGHT);
		hlMenuBar.setComponentAlignment(imgUs, Alignment.MIDDLE_RIGHT);
		hlMenuBar.setComponentAlignment(imgGb, Alignment.MIDDLE_RIGHT);
		hlMenuBar.setComponentAlignment(imgEs, Alignment.MIDDLE_RIGHT);
		
		setLanguageIconClickHandlers();
		
		vSplitPanel = initVerticalSplitPanel();
        vRest.addComponent(vSplitPanel);
        vRest.setComponentAlignment(vSplitPanel, Alignment.TOP_LEFT);
        
        initTables();
        details = new DetailLayout(messages);
        details.setVisible(false);
        vSplitPanel.addComponent(details);
	}
	
	private Image initLanguageIcon(String resource){
		Image img = new Image();
		ThemeResource tr = new ThemeResource(resource);
		img.setSource(tr);
		img.setWidth(20.0f, Unit.PIXELS);
		img.setHeight(20.0f, Unit.PIXELS);
		img.addStyleName("jadeImage");
		return img;
	}
	
	private void setLanguageIconClickHandlers(){
		imgDe.addClickListener(new MouseEvents.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void click(ClickEvent event) {
				refreshLocalization(Locale.GERMANY);
			}
		});
		imgGb.addClickListener(new MouseEvents.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void click(ClickEvent event) {
				refreshLocalization(Locale.UK);
			}
		});
		imgUs.addClickListener(new MouseEvents.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void click(ClickEvent event) {
				refreshLocalization(Locale.US);
			}
		});
		imgEs.addClickListener(new MouseEvents.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void click(ClickEvent event) {
				refreshLocalization(new Locale("es", "ES"));
			}
		});
	}
	
	private void refreshLocalization(Locale locale){
		VaadinSession.getCurrent().setLocale(locale);
		MainView.this.messages.setLocale(locale);
		MainView.this.lblTitle.setValue(messages.getValue("MainView.title", locale));
		MainView.this.jmb.refreshCaptions(locale);
		MainView.this.details.refreshCaptions(locale);
		MainView.this.tblMixed.refreshColumnHeaders(locale);
		MainView.this.tblDetails.refreshColumnHeaders(locale);
		((JadeDetailsContainer)MainView.this.tblDetails.getContainerDataSource()).updateLocale(locale);
		((JadeFilesHistoryFilterLayout)((BackgroundserviceUI)MainView.this.getParent()).
				getModalWindow().getContent()).refreshCaptions(locale);
		MainView.this.markAsDirtyRecursive();
	}
	
	private void initTables(){
		HorizontalLayout hlTableMainLayout = new HorizontalLayout();
		VerticalLayout vlTableLayout = new VerticalLayout();
		vlTableLayout.setSizeFull();
		hlTableMainLayout.addComponent(vlTableLayout);
        vSplitPanel.addComponent(hlTableMainLayout);
        Button btnResetColumnWith = new Button("<->");
        btnResetColumnWith.addStyleName("jadeSizeRestButton");
        btnResetColumnWith.setHeight(20.0f, Unit.PIXELS);
        btnResetColumnWith.setDescription("resize column width");
        vlTableLayout.addComponent(btnResetColumnWith);
        tblMixed = new JadeMixedTable(historyItems, messages);
        tblMixed.setVisible(true);
        vlTableLayout.addComponent(tblMixed);
        vlTableLayout.setComponentAlignment(btnResetColumnWith, Alignment.BOTTOM_RIGHT);
        vlTableLayout.setExpandRatio(tblMixed, 1);
        btnResetColumnWith.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				tblMixed.resetColumnWidths();
			}
		});
        tblDetails = new JadeDetailTable(null, messages);
        tblDetails.setVisible(false);
        hlTableMainLayout.addComponent(tblDetails);
        hlTableMainLayout.setExpandRatio(vlTableLayout, 1);
	}
	
	private VerticalSplitPanel initVerticalSplitPanel(){
		VerticalSplitPanel vsp = new VerticalSplitPanel();
		vsp.setSizeFull();
		vsp.setSplitPosition(100);
		return vsp;
	}
	
	private JadeMenuBar initMenuBar(){
		JadeMenuBar jmb = new JadeMenuBar(messages);
 		return jmb;
	}
	
	private void toggleTableVisiblity(Item item){
		if(markedRow != null && markedRow.equals(item)){
			markedRow = null;
			details.setVisible(false);
			tblDetails.setVisible(false);
			setSplitPosition();
		}else if (markedRow != null && !markedRow.equals(item)){
			markedRow = item;
			details.setVisible(true);
			tblDetails.setVisible(true);
		}else{
			markedRow = item;
			details.setVisible(true);
			tblDetails.setVisible(true);
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
					        tblMixed.refreshRowCache();
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

	public JadeBSMessages getMessages() {
		return messages;
	}

}
