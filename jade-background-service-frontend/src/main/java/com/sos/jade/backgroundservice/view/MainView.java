package com.sos.jade.backgroundservice.view;

import static com.sos.jade.backgroundservice.BackgroundserviceUI.jadeBsOptions;

import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

import sos.ftphistory.db.JadeFilesHistoryDBItem;
import sos.ftphistory.db.JadeFilesHistoryDBLayer;

import com.sos.jade.backgroundservice.BackgroundserviceUI;
import com.sos.jade.backgroundservice.data.JadeDetailsContainer;
import com.sos.jade.backgroundservice.listeners.IJadeFileListener;
import com.sos.jade.backgroundservice.listeners.impl.JadeFileListenerProxy;
import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.sos.jade.backgroundservice.view.components.JadeDetailTable;
import com.sos.jade.backgroundservice.view.components.JadeFileHistoryTable;
import com.sos.jade.backgroundservice.view.components.JadeMenuBar;
import com.sos.jade.backgroundservice.view.components.filter.JadeFilesHistoryFilterLayout;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class MainView extends CustomComponent{
	
	private static final long serialVersionUID = 6368275374953898482L;
	private JadeFilesHistoryDBLayer jadeFilesHistoryDBLayer;
	private List<JadeFilesHistoryDBItem> historyItems;
	private JadeFileHistoryTable tblFileHistory;
	private JadeDetailTable tblDetails;
	private Item markedRow;
	private IJadeFileListener fileListener;
//	private boolean first = true;
    private Image imgDe;
    private Image imgGb;
    private Image imgUs;
    private Image imgEs;
    private JadeMenuBar jmb;
    private Label lblTitle;
    private VerticalLayout vRest;
    private HorizontalLayout hlTableMainLayout;
	private Preferences prefs = jadeBsOptions.getPreferenceStore();
	private static final String CLASS_NODE_NAME = "main-view";
	private final Logger log = Logger.getLogger(MainView.class);
	private ProgressBar progress;
	private JadeBSMessages messages;

	public MainView() {
		this.messages = new JadeBSMessages("JADEBSMessages", VaadinSession.getCurrent().getLocale());
		this.fileListener = new JadeFileListenerProxy(this);
		setImmediate(true);
		initView();
	}
	
	private void initView(){
		initComponents();
		new Thread() {
            @Override
            public void run() {
		        try {
		        	progress.setVisible(true);
		        	fileListener.filterJadeFilesHistory(null);
		        } catch (final Exception e) {
		        	fileListener.getException(e);
		        }
				UI.getCurrent().access(new Runnable() {
					@Override
					public void run() {
				        tblFileHistory.populateDatasource(historyItems);
						fileListener.closeJadeFilesHistoryDbSession();
						log.debug("feedback from Hibernate SESSION closing received in MainView");
					}
				});
            };
        }.start();
	}
	
	private void initComponents(){
        final VerticalLayout vLayout = new VerticalLayout();
        vLayout.setMargin(true);
        vLayout.setSpacing(true);
        vLayout.setSizeFull();
        vLayout.addStyleName("jadeMainVLayout");
        setCompositionRoot(vLayout);
        
        vLayout.addComponent(initTitleLayout());
        
        HorizontalLayout hlMenuBar = initHLayout(25.0f);
        hlMenuBar.addStyleName("jadeMenuBarLayout");
        vLayout.addComponent(hlMenuBar);
        jmb = new JadeMenuBar(messages);
        hlMenuBar.addComponent(jmb);
        imgDe = initLanguageIcon("images/de_marble.png");
        imgGb = initLanguageIcon("images/gb_marble.png");
        imgUs = initLanguageIcon("images/us_marble.png");
        imgEs = initLanguageIcon("images/es_marble.png");
		hlMenuBar.addComponents(imgDe, imgUs, imgGb, imgEs);
		hlMenuBar.setExpandRatio(jmb, 1);
		hlMenuBar.setComponentAlignment(imgDe, Alignment.MIDDLE_RIGHT);
		hlMenuBar.setComponentAlignment(imgUs, Alignment.MIDDLE_RIGHT);
		hlMenuBar.setComponentAlignment(imgGb, Alignment.MIDDLE_RIGHT);
		hlMenuBar.setComponentAlignment(imgEs, Alignment.MIDDLE_RIGHT);
		setLanguageIconClickHandlers();
		
        vRest = new VerticalLayout();
        vRest.setSizeFull();
        vLayout.addComponent(vRest);
        vLayout.setExpandRatio(vRest, 1);
        initTables();
	}
	
	private ProgressBar initProgressBar(){
		ProgressBar progressBar = new ProgressBar();
		progressBar.setImmediate(true);
		progressBar.setIndeterminate(true);
		return progressBar;
	}
	
	private HorizontalLayout initTitleLayout(){
        HorizontalLayout hLayout = initHLayout(75.0f);
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
        return hLayout;
	}
	
	private HorizontalLayout initHLayout(Float height){
		HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth("100%");
        hl.setHeight(height, Unit.PIXELS);
		return hl;
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
		MainView.this.tblFileHistory.refreshColumnHeaders(locale);
		MainView.this.tblDetails.refreshColumnHeaders(locale);
		if(MainView.this.tblDetails.getContainerDataSource().getItemIds() != null && 
				!MainView.this.tblDetails.getContainerDataSource().getItemIds().isEmpty())
			((JadeDetailsContainer)MainView.this.tblDetails.getContainerDataSource()).updateLocale(locale);
		((JadeFilesHistoryFilterLayout)((BackgroundserviceUI)MainView.this.getParent()).
				getModalWindow().getContent()).refreshCaptions(locale);
		MainView.this.markAsDirtyRecursive();
	}
	
	private void initTables(){
		hlTableMainLayout = new HorizontalLayout();
		hlTableMainLayout.setSizeFull();
		HorizontalLayout hlResetAndProgress = new HorizontalLayout();
		hlResetAndProgress.setSizeUndefined();
		vRest.addComponent(hlResetAndProgress);
        Button btnResetColumnWith = new Button();
        btnResetColumnWith.addStyleName("jadeResizeButton");
        btnResetColumnWith.setSizeUndefined();
        btnResetColumnWith.setIcon(new ThemeResource("images/resize_orange_40x20.png"));
        btnResetColumnWith.setDescription("reset column width to default");
        hlResetAndProgress.addComponent(btnResetColumnWith);
        vRest.addComponent(hlTableMainLayout);
        vRest.setExpandRatio(hlTableMainLayout, 1);
        tblFileHistory = new JadeFileHistoryTable(historyItems, messages);
        hlTableMainLayout.addComponent(tblFileHistory);
        tblFileHistory.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void itemClick(ItemClickEvent event) {
				if (markedRow == null || !markedRow.equals(event.getItemId())){
					JadeFilesHistoryDBItem historyItem = (JadeFilesHistoryDBItem)event.getItemId();
					tblDetails.populateDatasource(historyItem);
				}
				toggleTableVisiblity(event.getItem());
			}
		});
        tblFileHistory.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent event) {
				tblFileHistory.refreshRowCache();
				tblFileHistory.markAsDirty();
	        	progress.setVisible(false);
			}
		});
        btnResetColumnWith.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(Button.ClickEvent event) {
				tblFileHistory.resetColumnWidths();
				if(tblDetails.isVisible()){
					tblDetails.resetColumnWidths();
				}
			}
		});
        tblDetails = new JadeDetailTable(null, messages);
        tblDetails.setVisible(false);
        hlTableMainLayout.addComponent(tblDetails);
        hlTableMainLayout.setExpandRatio(tblFileHistory, 1);
		progress = initProgressBar();
		hlResetAndProgress.addComponent(progress);
		hlResetAndProgress.setComponentAlignment(progress, Alignment.MIDDLE_CENTER);
		progress.setVisible(false);
	}
	
	private void toggleTableVisiblity(Item item){
		if(markedRow != null && markedRow.equals(item)){
			markedRow = null;
			tblDetails.setVisible(false);
			hlTableMainLayout.setExpandRatio(tblFileHistory, 1);
		}else if (markedRow != null && !markedRow.equals(item)){
			markedRow = item;
			tblDetails.setVisible(true);
			hlTableMainLayout.setExpandRatio(tblFileHistory, 2);
			hlTableMainLayout.setExpandRatio(tblDetails, 1);
		}else{
			markedRow = item;
			tblDetails.setVisible(true);
			hlTableMainLayout.setExpandRatio(tblFileHistory, 2);
			hlTableMainLayout.setExpandRatio(tblDetails, 1);
		}
	}
	
	public void setMarkedRow(Item markedRow) {
		this.markedRow = markedRow;
	}
	
	public void setDetailViewVisible(boolean visible){
		tblDetails.setVisible(visible);
	}

	public List<JadeFilesHistoryDBItem> getHistoryItems() {
		return historyItems;
	}

	public void setHistoryItems(List<JadeFilesHistoryDBItem> historyItems) {
		this.historyItems = historyItems;
	}

	public JadeFileHistoryTable getTblFileHistory() {
		return tblFileHistory;
	}

	public JadeDetailTable getTblDetails() {
		return tblDetails;
	}

	public JadeBSMessages getMessages() {
		return messages;
	}

	public ProgressBar getProgress() {
		return progress;
	}

}
