package com.sos.jade.backgroundservice.view;

import static com.sos.jade.backgroundservice.BackgroundserviceUI.jadeBsOptions;
import static com.sos.jade.backgroundservice.BackgroundserviceUI.parentNodeName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

import sos.ftphistory.JadeFilesHistoryFilter;
import sos.ftphistory.db.JadeFilesHistoryDBItem;
import sos.ftphistory.db.JadeFilesHistoryDBLayer;

import com.sos.jade.backgroundservice.BackgroundserviceUI;
import com.sos.jade.backgroundservice.constants.JadeBSConstants;
import com.sos.jade.backgroundservice.data.JadeDetailsContainer;
import com.sos.jade.backgroundservice.listeners.IJadeFileListener;
import com.sos.jade.backgroundservice.listeners.impl.JadeFileListenerProxy;
import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.sos.jade.backgroundservice.view.components.JadeDetailTable;
import com.sos.jade.backgroundservice.view.components.JadeFileHistoryTable;
import com.sos.jade.backgroundservice.view.components.JadeMenuBar;
import com.sos.jade.backgroundservice.view.components.filter.DuplicatesFilter;
import com.sos.jade.backgroundservice.view.components.filter.JadeFilesHistoryFilterLayout;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
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
import com.vaadin.ui.HorizontalSplitPanel;
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
	private boolean first = true;
    private Image imgDe;
    private Image imgUk;
    private Image imgUs;
    private Image imgEs;
    private JadeMenuBar jmb;
    private Label lblTitle;
    private VerticalLayout vRest;
//    private HorizontalLayout hlTableMainLayout;
	private Preferences prefs = jadeBsOptions.getPreferenceStore();
	private final Logger log = Logger.getLogger(MainView.class);
	private ProgressBar progress;
	private JadeBSMessages messages;
	private Date progressStart;
	private HorizontalLayout hlResetAndProgress;
	private static final String primaryProgressBarStyle = "v-progressbar";
	private static final Long DELAY_MEDIUM = 2000L;
	private static final Long DELAY_LONG = 5000L;
	private boolean removeDuplicates = false;
	private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSS");
	private Locale currentLocale = VaadinSession.getCurrent().getLocale();
	private DuplicatesFilter duplicatesFilter = new DuplicatesFilter();
	private Float lastSplitPosition;
	private HorizontalSplitPanel splitter;

	public MainView() {
		this.messages = new JadeBSMessages("JADEBSMessages", currentLocale);
		this.fileListener = new JadeFileListenerProxy(this);
		setImmediate(true);
		initView();
	}
	
	private void initView(){
		initComponents();
    	progress.setVisible(true);
    	if(checkReuseLastFilterSettings()){
        	runFilter(createReusedFilter());
    	}else{
        	runFilter(null);
    	}
	}
	
	private void runFilter(final JadeFilesHistoryFilter filter){
    	progressStart = new Date();
    	new SleeperThreadMedium().start();
    	new SleeperThreadLong().start();
		new Thread() {
            @Override
            public void run() {
		        try {
		        	fileListener.filterJadeFilesHistory(filter);
		        } catch (final Exception e) {
		        	fileListener.getException(e);
		        }
				UI.getCurrent().access(new Runnable() {
					@Override
					public void run() {
				        tblFileHistory.populateDatasource(historyItems);
						fileListener.closeJadeFilesHistoryDbSession();
						if(checkRemoveDuplicatesSettings()){
							duplicatesFilter.setHistoryItems(historyItems);
							((IndexedContainer)tblFileHistory.getContainerDataSource()).addContainerFilter(duplicatesFilter);
						}else if(((IndexedContainer)tblFileHistory.getContainerDataSource()).hasContainerFilters()){
							duplicatesFilter.setHistoryItems(historyItems);
							((IndexedContainer)tblFileHistory.getContainerDataSource()).removeContainerFilter(duplicatesFilter);
						}
						jmb.refreshSelectedLangOnInit();
						jmb.refreshSelectedDetailsOnInit();
						jmb.getSmDuplicatesFilter().setChecked(checkRemoveDuplicatesSettings());
						jmb.getSmPreferencesReuseFilter().setChecked(checkReuseLastFilterSettings());
						refreshButtonVisibility();
						log.debug("feedback received from proxy about Hibernate SESSION close at " + sdf.format(new Date()) + "!");
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
        
        vLayout.addComponent(createTitleLayout());
        
        HorizontalLayout hlMenuBar = initHLayout(25.0f);
        hlMenuBar.addStyleName("jadeMenuBarLayout");
        vLayout.addComponent(hlMenuBar);
        jmb = new JadeMenuBar(messages);
        hlMenuBar.addComponent(jmb);
        imgDe = initLanguageIcon("images/de_marble.png");
        imgUk = initLanguageIcon("images/gb_marble.png");
        imgUs = initLanguageIcon("images/us_marble.png");
        imgEs = initLanguageIcon("images/es_marble.png");
		hlMenuBar.addComponents(imgDe, imgUs, imgUk, imgEs);
		imgDe.setVisible(false);
		imgUk.setVisible(false);
		imgUs.setVisible(false);
		imgEs.setVisible(false);
		hlMenuBar.setExpandRatio(jmb, 1);
		hlMenuBar.setComponentAlignment(imgDe, Alignment.MIDDLE_RIGHT);
		hlMenuBar.setComponentAlignment(imgUs, Alignment.MIDDLE_RIGHT);
		hlMenuBar.setComponentAlignment(imgUk, Alignment.MIDDLE_RIGHT);
		hlMenuBar.setComponentAlignment(imgEs, Alignment.MIDDLE_RIGHT);
		setLanguageIconClickHandlers();
		
        vRest = new VerticalLayout();
        vRest.setSizeFull();
        vLayout.addComponent(vRest);
        vLayout.setExpandRatio(vRest, 1);
        createResetAndProgressLayout();
        createTablesLayout();
	}
	
	private ProgressBar initProgressBar(){
		ProgressBar progressBar = new ProgressBar();
		progressBar.setImmediate(true);
		progressBar.setIndeterminate(true);
		return progressBar;
	}
	
	private HorizontalSplitPanel initHSplitPanel(){
		HorizontalSplitPanel split = new HorizontalSplitPanel();
		split.setSizeFull();
		split.setSplitPosition(100.0f);
		return split;
	}
	
	private HorizontalLayout createTitleLayout(){
        HorizontalLayout hLayout = initHLayout(75.0f);
        final Image imgTitle = new Image();
        ThemeResource titleResource = new ThemeResource("images/job_scheduler_rabbit_circle_60x60.gif");
		imgTitle.setSource(titleResource);
        hLayout.addComponent(imgTitle);
        lblTitle = new Label(messages.getValue("MainView.title", currentLocale));
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
				currentLocale = Locale.GERMANY;
				refreshLocalization(currentLocale);
				imgDe.setVisible(false);
				refreshButtonVisibility();
			}
		});
		imgUk.addClickListener(new MouseEvents.ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void click(ClickEvent event) {
				currentLocale = Locale.UK;
				refreshLocalization(currentLocale);
				imgUk.setVisible(false);
				refreshButtonVisibility();
			}
		});
		imgUs.addClickListener(new MouseEvents.ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void click(ClickEvent event) {
				currentLocale = Locale.US;
				refreshLocalization(currentLocale);
				imgUs.setVisible(false);
				refreshButtonVisibility();
			}
		});
		imgEs.addClickListener(new MouseEvents.ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void click(ClickEvent event) {
				currentLocale = new Locale("es", "ES");
				refreshLocalization(currentLocale);
				imgEs.setVisible(false);
				refreshButtonVisibility();
			}
		});
	}
	
	public void refreshLocalization(Locale locale){
		if(locale == null){
			locale = Locale.getDefault();
		}
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
	
	public void refreshButtonVisibility(){
		List<Locale> checkedLocales = jmb.getCheckedLanguages();
		for(Locale checkedLocale : checkedLocales){
			if(checkedLocale.getCountry().equals(currentLocale.getCountry())){
				setButtonNotVisibile(checkedLocale);
			}else{
				setButtonVisibile(checkedLocale);
			}
		}
		if(!checkedLocales.contains(currentLocale)){
			currentLocale = Locale.getDefault();
			refreshLocalization(currentLocale);
			setButtonNotVisibile(currentLocale);
		}
	}
	
	private void setButtonNotVisibile(Locale locale){
		if(locale.getCountry().equals(Locale.GERMANY.getCountry())){
			imgDe.setVisible(false);
		}else if(locale.getCountry().equals(Locale.UK.getCountry())){
			imgUk.setVisible(false);
		}else if(locale.getCountry().equals(Locale.US.getCountry())){
			imgUs.setVisible(false);
		}else if(locale.getCountry().equals(new Locale("es", "ES").getCountry())){
			imgEs.setVisible(false);
		}
	}
	
	private void setButtonVisibile(Locale locale){
		if(locale.getCountry().equals(Locale.GERMANY.getCountry())){
			imgDe.setVisible(true);
		}else if(locale.getCountry().equals(Locale.UK.getCountry())){
			imgUk.setVisible(true);
		}else if(locale.getCountry().equals(Locale.US.getCountry())){
			imgUs.setVisible(true);
		}else if(locale.getCountry().equals(new Locale("es", "ES").getCountry())){
			imgEs.setVisible(true);
		}
	}
	
	private void createResetAndProgressLayout(){
		hlResetAndProgress = new HorizontalLayout();
		hlResetAndProgress.setWidth("100%");
		vRest.addComponent(hlResetAndProgress);
        Button btnResetColumnWith = new Button();
        btnResetColumnWith.setPrimaryStyleName("jadeResizeButton");
        btnResetColumnWith.setSizeUndefined();
        btnResetColumnWith.setIcon(new ThemeResource("images/resize_orange_40x20.png"));
        btnResetColumnWith.setDescription("reset column width to default");
        hlResetAndProgress.addComponent(btnResetColumnWith);
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
		progress = initProgressBar();
		hlResetAndProgress.addComponent(progress);
		hlResetAndProgress.setComponentAlignment(progress, Alignment.TOP_CENTER);
		hlResetAndProgress.setExpandRatio(progress, 1);
		progress.setVisible(false);
 	}
	
	private void createTablesLayout(){
        splitter = initHSplitPanel();
        vRest.addComponent(splitter);
        vRest.setExpandRatio(splitter, 1);
        tblFileHistory = new JadeFileHistoryTable(historyItems, messages);
        splitter.addComponent(tblFileHistory);
        tblFileHistory.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void itemClick(ItemClickEvent event) {
				if (markedRow == null || !markedRow.equals(event.getItemId())){
					JadeFilesHistoryDBItem historyItem = (JadeFilesHistoryDBItem)event.getItemId();
					tblDetails.populateDatasource(historyItem);
				}
				toggleTableVisiblity(event.getItem());
				jmb.filterDetailItems();
			}
		});
        tblFileHistory.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent event) {
				progress.setPrimaryStyleName("jadeProgressBar");
				tblFileHistory.refreshRowCache();
				tblFileHistory.markAsDirty();
	        	progress.setVisible(false);
			}
		});
        tblDetails = new JadeDetailTable(null, messages);
        tblDetails.setVisible(false);
        splitter.addComponent(tblDetails);
	}
	
	private void setSplitPosition(){
		Float newLastSplitPosition = splitter.getSplitPosition();
		if(first){
			first = false;
			splitter.setSplitPosition(75);
		}else{
			splitter.setSplitPosition(lastSplitPosition);
		}
		lastSplitPosition = newLastSplitPosition;
	}
	
	public void toggleTableVisiblity(Item item){
		if(item == null || (markedRow != null && markedRow.equals(item))){
			markedRow = null;
			tblDetails.setVisible(false);
			setSplitPosition();
		}else if (markedRow != null && !markedRow.equals(item)){
			markedRow = item;
			tblDetails.setVisible(true);
			setSplitPosition();
		}else{
			markedRow = item;
			tblDetails.setVisible(true);
			setSplitPosition();
		}
	}
	
	/**
	 * Timed Thread, to update the Progress Indicator after a medium delay with a yellow background
	 * 
	 * @author SP
	 *
	 */
	public class SleeperThreadMedium extends Thread{
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSS");
		Date actual = null;
		@Override
		public void run() {
			log.debug("SleeperThreadMedium started at " + sdf.format(new Date()) + "!");
			progress.setPrimaryStyleName("jadeProgressBar");
			if(progressStart == null){
				progressStart = new Date();
			}
			while((actual = new Date()).getTime() - progressStart.getTime() < DELAY_MEDIUM){
				continue;
			}
			UI.getCurrent().access(new Runnable() {
				@Override
				public void run() {
					progress.setPrimaryStyleName("jadeProgressBarMedium");
					log.debug("SleeperThreadMedium ended after " + (actual.getTime() - progressStart.getTime()) + "ms at " + sdf.format(actual) + "!");
				}
			});
		}
	}
	
	/**
	 * Timed Thread, to update the Progress Indicator after a long delay with a red background
	 * 
	 * @author SP
	 *
	 */
	public class SleeperThreadLong extends Thread{
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSS");
		Date actual = null;
		@Override
		public void run() {
			log.debug("SleeperThreadLong started at " + sdf.format(new Date()) + "!");
			while((actual = new Date()).getTime() - progressStart.getTime() < DELAY_LONG){
				continue;
			}
			UI.getCurrent().access(new Runnable() {
				@Override
				public void run() {
					progress.setPrimaryStyleName("jadeProgressBarSlow");
					log.debug("SleeperThreadLong ended after " + (actual.getTime() - progressStart.getTime()) + "ms at " + sdf.format(actual) + "!");
				}
			});
		}
	}

	private boolean checkReuseLastFilterSettings(){
		boolean lastUsed = false;
		try {
			if(prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES).nodeExists(JadeBSConstants.PREF_NODE_PREFERENCES_GENERAL)){
				lastUsed = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR)
						.node(JadeBSConstants.PREF_NODE_PREFERENCES).node(JadeBSConstants.PREF_NODE_PREFERENCES_GENERAL)
						.getBoolean(JadeBSConstants.PREF_KEY_LAST_USED_FILTER, false);
			}
		} catch (BackingStoreException e) {
			log.warn("Unable to read from PreferenceStore, using defaults.");
			e.printStackTrace();
		}
		return lastUsed;
	}
	
	private boolean checkRemoveDuplicatesSettings(){
		boolean removeDuplicates = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR)
				.node(JadeBSConstants.PREF_NODE_PREFERENCES).node(JadeBSConstants.PREF_NODE_PREFERENCES_GENERAL)
				.getBoolean(JadeBSConstants.PREF_KEY_REMOVE_DUPLICATES, false);
		return removeDuplicates;
	}
	
	private JadeFilesHistoryFilter createReusedFilter(){
		JadeFilesHistoryFilter filter = new JadeFilesHistoryFilter();
		Long timeFrom = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
				.getLong(JadeBSConstants.FILTER_OPTION_TRANSFER_TIMESTAMP_FROM, 0L);
		if(timeFrom != 0L){
			filter.setTransferTimestampFrom(new Date(timeFrom));
		}
		Long timeTo = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
				.getLong(JadeBSConstants.FILTER_OPTION_TRANSFER_TIMESTAMP_TO, 0L); 
		if(timeTo != 0L){
			filter.setTransferTimestampTo(new Date(timeTo));
		}
		String protocol = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
				.get(JadeBSConstants.FILTER_OPTION_PROTOCOL, null);
		if(protocol != null && !"".equals(protocol)){
			filter.setProtocol(protocol);
		}
		String status = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
				.get(JadeBSConstants.FILTER_OPTION_STATUS, null); 
		if(status != null && !"".equals(status)){
			filter.setStatus(status);
		}
		String operation = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
				.get(JadeBSConstants.FILTER_OPTION_OPERATION, null);
		if(operation != null && !"".equals(operation)){
			filter.setOperation(operation);
		}
		String sourceFile = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
				.get(JadeBSConstants.FILTER_OPTION_SOURCE_FILE, null);
		if(sourceFile != null && !"".equals(sourceFile)){
			filter.setSourceFile(sourceFile);
		}
		String sourceHost = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
				.get(JadeBSConstants.FILTER_OPTION_SOURCE_HOST, null);
		if(sourceHost != null && !"".equals(sourceHost)){
			filter.setSourceHost(sourceHost);
		}
		String targetFile = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
				.get(JadeBSConstants.FILTER_OPTION_TARGET_FILE, null);
		if(targetFile != null && !"".equals(targetFile)){
			filter.setTargetFilename(targetFile);
		}
		String targetHost = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
				.get(JadeBSConstants.FILTER_OPTION_TARGET_HOST, null);
		if(targetHost != null && !"".equals(targetHost)){
			filter.setTargetHost(targetHost);
		}
		String mandator = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
				.get(JadeBSConstants.FILTER_OPTION_MANDATOR, null);
		if(mandator != null && !"".equals(mandator)){
			filter.setMandator(mandator);
		}
		return filter;
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
	
	public Date getProgressStart() {
		return progressStart;
	}

	public JadeMenuBar getJmb() {
		return jmb;
	}

	public Image getImgDe() {
		return imgDe;
	}

	public Image getImgUk() {
		return imgUk;
	}

	public Image getImgUs() {
		return imgUs;
	}

	public Image getImgEs() {
		return imgEs;
	}

	public Locale getCurrentLocale() {
		return currentLocale;
	}

}
