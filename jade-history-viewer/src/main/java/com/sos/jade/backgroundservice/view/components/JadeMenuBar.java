package com.sos.jade.backgroundservice.view.components;

import static com.sos.jade.backgroundservice.JADEHistoryViewerUI.jadeBsOptions;
import static com.sos.jade.backgroundservice.JADEHistoryViewerUI.parentNodeName;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.jade.backgroundservice.JADEHistoryViewerUI;
import com.sos.jade.backgroundservice.constants.JadeBSConstants;
import com.sos.jade.backgroundservice.enums.JadeFileColumns;
import com.sos.jade.backgroundservice.enums.JadeHistoryFileColumns;
import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.sos.jade.backgroundservice.view.MainView;
import com.sos.jade.backgroundservice.view.components.filter.DetailFilter;
import com.sos.jade.backgroundservice.view.components.filter.DuplicatesFilter;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;

/**
 * A MenuBar with the Jade related MenuItems; extends Vaadins {@link com.vaadin.ui.MenuBar MenuBar}
 * 
 * @author SP
 *
 */
public class JadeMenuBar extends MenuBar {
	private static final long serialVersionUID = 1L;
	private static final String MESSAGE_RESOURCE_BASE = "JadeMenuBar.";
	private static final String MESSAGE_RESOURCE_FILE = "file.";
	private static final String MESSAGE_RESOURCE_FILE_MENU = "fileMenu.";
	private static final String MESSAGE_RESOURCE_HISTORY = "fileHistory.";
	private static final String MESSAGE_RESOURCE_HELP = "help.";
	private Preferences prefs = jadeBsOptions.getPreferenceStore();
	private Logger log = LoggerFactory.getLogger(JadeMenuBar.class);
	private MenuItem mFile;
	private MenuItem mFilter;
	private MenuItem mPreferences;
	private MenuItem mHelp;
	private MenuItem smActivateFilter;
	private MenuItem smDuplicatesFilter;
	private MenuItem smPreferencesReuseFilter;
	private MenuItem smAutoRefresh;
	private MenuItem smLoadFilter;
	private MenuItem smSaveFilter;
	private MenuItem ssmLoadHistoryFilter;
	private MenuItem ssmSaveHistoryFilter;
	private MenuItem ssmLoadDetailsFilter;
	private MenuItem ssmSaveDetailsFilter;
	private MenuItem smPreferencesLanguages;
	private MenuItem smPreferencesDetails;
	private MenuItem ssmGermanCheck;
	private MenuItem ssmEnglishUKCheck;
	private MenuItem ssmEnglishUSCheck;
	private MenuItem ssmSpanishCheck;
	private MenuItem ssmActivateAllChecks;
	private MenuItem ssmDeactivateAllChecks;
	private MenuItem smAbout;
	private MenuItem smLinks;
	private MenuItem ssmLinksParameterReference;
	private MenuItem ssmLinksManual;
	private MenuItem ssmLinksFaq;
	private MenuItem ssmLinksApiReference;
	private MenuItem ssmLinksClientDocu;
	private MenuItem smFileLogout;
	private JadeBSMessages messages;
	private DuplicatesFilter duplicatesFilter = new DuplicatesFilter();
	private DetailFilter lastDetailFilter;
	private DetailFilter detailFilter;
	private Locale actualLocale;
	private Locale lastLocale;
	private List<String> globalDetailKeys;
	private MenuBar.Command detailCommand;
	private MainView mainView;


	public JadeMenuBar(JadeBSMessages messages) {
		this.messages = messages;
		mainView = getMainViewFromCurrentUI();
		setAutoOpen(true);
		addStyleName("jadeMenuBar");
		actualLocale = VaadinSession.getCurrent().getLocale();
		globalDetailKeys = setGlobalDetailKeys();
		detailCommand = new MenuBar.Command() {
			private static final long serialVersionUID = 1L;
			public void menuSelected(MenuItem selectedItem) {
	    		filterDetailItems();
		    }  
		};
		createTopLevelMenuItems();
//		createFileMenuItems();
		createFilterMenuItems();
		createPreferencesLanguageMenuItems();
		createPreferencesVisibleDetails();
		createHelpMenuItems();
	}
	
	private void createTopLevelMenuItems(){
//		this.mFile = addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "file", actualLocale), null);
		this.mFilter = addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "filter", actualLocale), null);
		this.mPreferences = addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "preferences", actualLocale), null);
		this.mHelp = addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "help", actualLocale), null);
	}
	
	private void createFileMenuItems(){
		this.smLoadFilter = mFile.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_FILE_MENU + "loadFilter", actualLocale), null);
		this.ssmLoadDetailsFilter = smLoadFilter.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_FILE_MENU + "loadDetailsFilter", actualLocale), new Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
				// TODO Auto-generated method stub
				
			}
		});
		this.ssmLoadHistoryFilter = smLoadFilter.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_FILE_MENU + "loadHistoryFilter", actualLocale), new Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
				// TODO Auto-generated method stub
				
			}
		});
		this.smSaveFilter = mFile.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_FILE_MENU + "saveFilter", actualLocale), null);
		this.ssmSaveDetailsFilter = smSaveFilter.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_FILE_MENU + "saveDetailsFilter", actualLocale), new Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
				// TODO Auto-generated method stub
				
			}
		});
		this.ssmSaveHistoryFilter = smSaveFilter.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_FILE_MENU + "saveHistoryFilter", actualLocale), new Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
				// TODO Auto-generated method stub
				
			}
		});
		mFile.addSeparator();
		this.smFileLogout = mFile.addItem("Logout", new Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
    			// "Logout" the user
    			getSession().setAttribute("user", null);
    			// Refresh this view, should redirect to login view
    			((JADEHistoryViewerUI)getUI()).getNavigator().navigateTo(LoginView.NAME);
			}
		});
	}
	private void createFilterMenuItems(){
		this.smActivateFilter = mFilter.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "doFilter", actualLocale), new MenuBar.Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
				UI.getCurrent().addWindow(((JADEHistoryViewerUI)UI.getCurrent()).getModalWindow());
		    }  
		});
		mFilter.addSeparator();
		createRemoveDuplicatesMenuItem();
		createReuseFilterMenuItem();
		createAutoRefreshMenuItem();
	}
	
	private void createAutoRefreshMenuItem(){
		this.smAutoRefresh = mFilter.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "autorefresh", actualLocale), new Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
				prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_PREFERENCES_GENERAL)
				.putBoolean(JadeBSConstants.PREF_KEY_AUTO_REFRESH, selectedItem.isChecked());
				if(mainView == null){
					mainView = getMainViewFromCurrentUI();
				}
				mainView.setAutoRefresh(selectedItem.isChecked());
			}
		});
		smAutoRefresh.setCheckable(true);
	}

	private void createRemoveDuplicatesMenuItem(){
		this.smDuplicatesFilter = mFilter.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "removeDuplicates", actualLocale), new MenuBar.Command() {
			private static final long serialVersionUID = 1L;
			public void menuSelected(MenuItem selectedItem) {
				if(mainView == null){
					mainView = getMainViewFromCurrentUI();
				}
				if(mainView.getTblDetails().isVisible()){
					mainView.getTblDetails().setVisible(false);
					mainView.getTblFileHistory().select(mainView.getTblFileHistory().getNullSelectionItemId());
					mainView.setSplitPosition(100.0f);
					mainView.setMarkedRow(null);
					mainView.toggleTableVisiblity(null);
				}
				duplicatesFilter.setHistoryItems(mainView.getHistoryItems());
				if(selectedItem.isChecked()){
					((IndexedContainer)mainView.getTblFileHistory().getContainerDataSource()).addContainerFilter(duplicatesFilter);
				}else{
					((IndexedContainer)mainView.getTblFileHistory().getContainerDataSource()).removeContainerFilter(duplicatesFilter);
				}
				prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_PREFERENCES_GENERAL)
				.putBoolean(JadeBSConstants.PREF_KEY_REMOVE_DUPLICATES, selectedItem.isChecked());
		    }  
		});
		this.smDuplicatesFilter.setCheckable(true);
		this.smDuplicatesFilter.setEnabled(true);
	}
	
	private void createReuseFilterMenuItem(){
		smPreferencesReuseFilter = this.mFilter.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "reuseFilter", actualLocale), new MenuBar.Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
				prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_PREFERENCES_GENERAL)
				.putBoolean(JadeBSConstants.PREF_KEY_LAST_USED_FILTER, selectedItem.isChecked());
			}
		});
		smPreferencesReuseFilter.setCheckable(true);
	}
	
	private void createPreferencesLanguageMenuItems(){
		smPreferencesLanguages = mPreferences.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "lang", actualLocale), null);
		ssmGermanCheck = smPreferencesLanguages.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "checkGerman", actualLocale), new MenuBar.Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
				prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES).
		        node(JadeBSConstants.PREF_NODE_AVAILABLE_LANGS).
		        putBoolean(JadeBSConstants.PREF_KEY_DE, selectedItem.isChecked());
				if(mainView == null){
					mainView = getMainViewFromCurrentUI();
				}
				mainView.getLblDE().setVisible(selectedItem.isChecked());
				mainView.refreshButtonVisibility();
			}
		});
		ssmGermanCheck.setCheckable(true);
		ssmEnglishUKCheck = smPreferencesLanguages.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "checkUK", actualLocale), new MenuBar.Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
		        prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES).
		        node(JadeBSConstants.PREF_NODE_AVAILABLE_LANGS).
		        putBoolean(JadeBSConstants.PREF_KEY_UK, selectedItem.isChecked());
				if(mainView == null){
					mainView = getMainViewFromCurrentUI();
				}
		        mainView.getLblUK().setVisible(selectedItem.isChecked());
		        mainView.refreshButtonVisibility();
			}
		});
		ssmEnglishUKCheck.setCheckable(true);
		ssmEnglishUSCheck = smPreferencesLanguages.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "checkUS", actualLocale), new MenuBar.Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
		        prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES).
		        node(JadeBSConstants.PREF_NODE_AVAILABLE_LANGS).
		        putBoolean(JadeBSConstants.PREF_KEY_US, selectedItem.isChecked());
				if(mainView == null){
					mainView = getMainViewFromCurrentUI();
				}
		        mainView.getLblUS().setVisible(selectedItem.isChecked());
		        mainView.refreshButtonVisibility();
			}
		});
		ssmEnglishUSCheck.setCheckable(true);
		ssmSpanishCheck = smPreferencesLanguages.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "checkSpanish", actualLocale), new MenuBar.Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
		        prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES).
		        node(JadeBSConstants.PREF_NODE_AVAILABLE_LANGS).
		        putBoolean(JadeBSConstants.PREF_KEY_ES, selectedItem.isChecked());
				if(mainView == null){
					mainView = getMainViewFromCurrentUI();
				}
		        mainView.getLblES().setVisible(selectedItem.isChecked());
		        mainView.refreshButtonVisibility();
			}
		});
		ssmSpanishCheck.setCheckable(true);
		smPreferencesLanguages.addSeparator();
		ssmActivateAllChecks = smPreferencesLanguages.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "checkAll", actualLocale), new MenuBar.Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
				ssmGermanCheck.setChecked(true);
				ssmEnglishUKCheck.setChecked(true);
				ssmEnglishUSCheck.setChecked(true);
				ssmSpanishCheck.setChecked(true);
		        prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES).
		        node(JadeBSConstants.PREF_NODE_AVAILABLE_LANGS).
		        putBoolean(JadeBSConstants.PREF_KEY_DE, true);
		        prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES).
		        node(JadeBSConstants.PREF_NODE_AVAILABLE_LANGS).
		        putBoolean(JadeBSConstants.PREF_KEY_UK, true);
		        prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES).
		        node(JadeBSConstants.PREF_NODE_AVAILABLE_LANGS).
		        putBoolean(JadeBSConstants.PREF_KEY_US, true);
		        prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES).
		        node(JadeBSConstants.PREF_NODE_AVAILABLE_LANGS).
		        putBoolean(JadeBSConstants.PREF_KEY_ES, true);
				if(mainView == null){
					mainView = getMainViewFromCurrentUI();
				}
		        mainView.refreshButtonVisibility();
			}
		});
		ssmDeactivateAllChecks = smPreferencesLanguages.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "checkNone", actualLocale), new MenuBar.Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
				if(mainView == null){
					mainView = getMainViewFromCurrentUI();
				}
				mainView.getLblDE().setVisible(false);
				mainView.getLblUK().setVisible(false);
				mainView.getLblUS().setVisible(false);
				mainView.getLblES().setVisible(false);
				ssmGermanCheck.setChecked(false);
				ssmEnglishUKCheck.setChecked(false);
				ssmEnglishUSCheck.setChecked(false);
				ssmSpanishCheck.setChecked(false);
		        prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES)
		        .node(JadeBSConstants.PREF_NODE_AVAILABLE_LANGS).putBoolean(JadeBSConstants.PREF_KEY_DE, false);
		        prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES)
		        .node(JadeBSConstants.PREF_NODE_AVAILABLE_LANGS).putBoolean(JadeBSConstants.PREF_KEY_UK, false);
		        prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES)
		        .node(JadeBSConstants.PREF_NODE_AVAILABLE_LANGS).putBoolean(JadeBSConstants.PREF_KEY_US, false);
		        prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES)
		        .node(JadeBSConstants.PREF_NODE_AVAILABLE_LANGS).putBoolean(JadeBSConstants.PREF_KEY_ES, false);
		        uncheckAllLangs();
		        Locale defaultLocale = Locale.getDefault();
		        if(defaultLocale.getCountry().equals(new Locale("de", "DE").getCountry())){
		        	ssmGermanCheck.setChecked(true);
		        }else if(defaultLocale.getCountry().equals(new Locale("en", "UK").getCountry())){
		        	ssmEnglishUKCheck.setChecked(true);
		        }else if(defaultLocale.getCountry().equals(new Locale("en", "US").getCountry())){
		        	ssmEnglishUSCheck.setChecked(true);
		        }else if(defaultLocale.getCountry().equals(new Locale("es", "ES").getCountry())){
		        	ssmSpanishCheck.setChecked(true);
		        }
		        VaadinSession.getCurrent().setLocale(defaultLocale);
		        mainView.refreshLocalization(defaultLocale);
			}
		});
	}
	
	private void createHelpMenuItems(){
		smAbout = mHelp.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_HELP + "about", actualLocale), new Command() {
			private static final long serialVersionUID = 1L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				getUI().addWindow(((JADEHistoryViewerUI)getUI()).getAboutWindow());
			}
		});
		mHelp.addSeparator();
		smLinks = mHelp.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_HELP + "links", actualLocale), null);
		ssmLinksFaq = smLinks.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_HELP + "faq", actualLocale), new Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
				getUI().getPage().open(new ExternalResource("http://www.sos-berlin.com/mediawiki/index.php/JADE_/_SOSFTP_FAQ").getURL(), "_blank");
			}
		});
		ssmLinksManual = smLinks.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_HELP + "manual", actualLocale), new Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
				getUI().getPage().open(new ExternalResource("http://www.sos-berlin.com/doc/en/jade/JADE%20Users%20Manual.pdf").getURL(), "_blank");
			}
		});
		ssmLinksClientDocu = smLinks.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_HELP + "clientDocu", actualLocale), new Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
				getUI().getPage().open(new ExternalResource("http://www.sos-berlin.com/modules/cjaycontent/doc/jade/jade.xml").getURL(), "_blank");
			}
		});
		ssmLinksParameterReference = smLinks.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_HELP + "params", actualLocale), new Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
				getUI().getPage().open(new ExternalResource("http://www.sos-berlin.com/doc/en/jade/JADE%20Parameter%20Reference.pdf").getURL(), "_blank");
			}
		});
		ssmLinksApiReference = smLinks.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_HELP + "api", actualLocale), new Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
				getUI().getPage().open(new ExternalResource("http://www.sos-berlin.com/doc/en/jade/JADE%20API%20Reference.pdf").getURL(), "_blank");
			}
		});
	}
	
	private void uncheckAllLangs(){
    	ssmGermanCheck.setChecked(false);
    	ssmEnglishUKCheck.setChecked(false);
    	ssmEnglishUSCheck.setChecked(false);
    	ssmSpanishCheck.setChecked(false);
	}
	
	public void refreshAutoRefreshOnInit(){
		try {
			if(prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).nodeExists(JadeBSConstants.PREF_NODE_PREFERENCES_GENERAL)){
				boolean refresh = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_PREFERENCES_GENERAL)
						.getBoolean(JadeBSConstants.PREF_KEY_AUTO_REFRESH, false);
				smAutoRefresh.setChecked(refresh);
				if(mainView != null){
					mainView.setAutoRefresh(refresh);
				}
			}
		} catch (BackingStoreException e) {
			log.warn("Unable to read from PreferenceStore, using defaults.");
			smAutoRefresh.setChecked(false);
			if(mainView != null){
				mainView.setAutoRefresh(false);
			}
		}
	}
	
	public void refreshSelectedLangOnInit(){
		try {
			if(prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES)
					.nodeExists(JadeBSConstants.PREF_NODE_AVAILABLE_LANGS)){
				boolean german = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES)
						.node(JadeBSConstants.PREF_NODE_AVAILABLE_LANGS).getBoolean(JadeBSConstants.PREF_KEY_DE, true);
				boolean englishUK = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES)
						.node(JadeBSConstants.PREF_NODE_AVAILABLE_LANGS).getBoolean(JadeBSConstants.PREF_KEY_UK, true);
				boolean englishUS = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES)
						.node(JadeBSConstants.PREF_NODE_AVAILABLE_LANGS).getBoolean(JadeBSConstants.PREF_KEY_US, true);
				boolean spanish = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES)
						.node(JadeBSConstants.PREF_NODE_AVAILABLE_LANGS).getBoolean(JadeBSConstants.PREF_KEY_ES, true);
				ssmGermanCheck.setChecked(german);
				ssmEnglishUKCheck.setChecked(englishUK);
				ssmEnglishUSCheck.setChecked(englishUS);
				ssmSpanishCheck.setChecked(spanish);
				if(mainView == null){
					mainView = getMainViewFromCurrentUI();
				}
				mainView.getLblDE().setVisible(german);
				mainView.getLblUK().setVisible(englishUK);
				mainView.getLblUS().setVisible(englishUS);
				mainView.getLblES().setVisible(spanish);
			}else{
				ssmGermanCheck.setChecked(true);
				ssmEnglishUKCheck.setChecked(true);
				ssmEnglishUSCheck.setChecked(true);
				ssmSpanishCheck.setChecked(true);
			}
		} catch (BackingStoreException e) {
			log.warn("Unable to read from PreferenceStore, using defaults.");
			ssmGermanCheck.setChecked(true);
			ssmEnglishUKCheck.setChecked(true);
			ssmEnglishUSCheck.setChecked(true);
			ssmSpanishCheck.setChecked(true);
		}
	}
	
	public void refreshSelectedDetailsOnInit(){
		try {
			if (prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES)
					.nodeExists(JadeBSConstants.PREF_NODE_VISIBLE_DETAILS)) {
				for (MenuItem item : smPreferencesDetails.getChildren()) {
					for (String key : globalDetailKeys) {
						if (item.getText().equals(
								messages.getValue(key, actualLocale))) {
							String target = null;
							if (key.contains(MESSAGE_RESOURCE_HISTORY)) {
								target = MESSAGE_RESOURCE_BASE
										+ MESSAGE_RESOURCE_HISTORY;
							} else if (key.contains(MESSAGE_RESOURCE_FILE)) {
								target = MESSAGE_RESOURCE_BASE
										+ MESSAGE_RESOURCE_FILE;
							} else {
								target = MESSAGE_RESOURCE_BASE;
							}
							item.setChecked(prefs.node(parentNodeName)
									.node(JadeBSConstants.PRIMARY_NODE_MENU_BAR)
									.node(JadeBSConstants.PREF_NODE_PREFERENCES)
									.node(JadeBSConstants.PREF_NODE_VISIBLE_DETAILS)
									.getBoolean(key.replace(target, ""), false));
							break;
						}
					}
				}
			}
		} catch (BackingStoreException e) {
			log.warn("Unable to read from PreferenceStore, using defaults.");
			for (MenuItem item : smPreferencesDetails.getChildren()) {
				item.setChecked(true);
			}
		}
	}
	
	public List<Locale> getCheckedLanguages(){
		List<Locale> checkedLocales = new ArrayList<Locale>();
		if(ssmGermanCheck.isChecked()){
			checkedLocales.add(Locale.GERMANY);
		}
		if(ssmEnglishUKCheck.isChecked()){
			checkedLocales.add(Locale.UK);
		}
		if(ssmEnglishUSCheck.isChecked()){
			checkedLocales.add(Locale.US);
		}
		if(ssmSpanishCheck.isChecked()){
			checkedLocales.add(new Locale("es", "ES"));
		}
		return checkedLocales;
	}

	public void refreshCaptions(Locale locale){
		lastLocale = actualLocale;
		actualLocale = locale;
//		mFile.setText(messages.getValue(MESSAGE_RESOURCE_BASE + "file", actualLocale));
		mFilter.setText(messages.getValue(MESSAGE_RESOURCE_BASE + "filter", actualLocale));
		smActivateFilter.setText(messages.getValue(MESSAGE_RESOURCE_BASE + "doFilter", actualLocale));
		smDuplicatesFilter.setText(messages.getValue(MESSAGE_RESOURCE_BASE + "removeDuplicates", actualLocale));
		mPreferences.setText(messages.getValue(MESSAGE_RESOURCE_BASE + "preferences", actualLocale));
		mHelp.setText(messages.getValue(MESSAGE_RESOURCE_BASE + "help", actualLocale));
		smPreferencesLanguages.setText(messages.getValue(MESSAGE_RESOURCE_BASE + "lang", actualLocale));
		smPreferencesReuseFilter.setText(messages.getValue(MESSAGE_RESOURCE_BASE + "reuseFilter", actualLocale));
		smPreferencesDetails.setText(messages.getValue(MESSAGE_RESOURCE_BASE + "visibleDetails", actualLocale));
		ssmGermanCheck.setText(messages.getValue(MESSAGE_RESOURCE_BASE + "checkGerman", actualLocale));
		ssmEnglishUKCheck.setText(messages.getValue(MESSAGE_RESOURCE_BASE + "checkUK", actualLocale));
		ssmEnglishUSCheck.setText(messages.getValue(MESSAGE_RESOURCE_BASE + "checkUS", actualLocale));
		ssmSpanishCheck.setText(messages.getValue(MESSAGE_RESOURCE_BASE + "checkSpanish", actualLocale));
		ssmActivateAllChecks.setText(messages.getValue(MESSAGE_RESOURCE_BASE + "checkAll", actualLocale));
		ssmDeactivateAllChecks.setText(messages.getValue(MESSAGE_RESOURCE_BASE + "checkNone", actualLocale));
		smAbout.setText(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_HELP + "about", actualLocale));
		smLinks.setText(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_HELP + "links", actualLocale));
		ssmLinksParameterReference.setText(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_HELP + "params", actualLocale));
		ssmLinksManual.setText(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_HELP + "manual", actualLocale));
		ssmLinksFaq.setText(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_HELP + "faq", actualLocale));
		ssmLinksClientDocu.setText(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_HELP + "clientDocu", actualLocale));
		ssmLinksApiReference.setText(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_HELP + "api", actualLocale));
		smAutoRefresh.setText(messages.getValue(MESSAGE_RESOURCE_BASE + "autorefresh", actualLocale));
//		smLoadFilter.setText(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_FILE_MENU + "loadFilter", actualLocale));
//		ssmLoadDetailsFilter.setText(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_FILE_MENU + "loadDetailsFilter", actualLocale));
//		ssmLoadHistoryFilter.setText(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_FILE_MENU + "loadHistoryFilter", actualLocale));
//		smSaveFilter.setText(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_FILE_MENU + "saveFilter", actualLocale));
//		ssmSaveDetailsFilter.setText(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_FILE_MENU + "saveDetailsFilter", actualLocale));
//		ssmSaveHistoryFilter.setText(messages.getValue(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_FILE_MENU + "saveHistoryFilter", actualLocale));
		for(MenuItem item : smPreferencesDetails.getChildren()){
			for(String key : globalDetailKeys){
				if(item.getText().equals(messages.getValue(key, lastLocale))){
					item.setText(messages.getValue(key, actualLocale));
					break;
				}
			}
		}
		JadeMenuBar.this.markAsDirty();
	}

	private void createPreferencesVisibleDetails(){
		mPreferences.addSeparator();
		smPreferencesDetails = mPreferences.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "visibleDetails", actualLocale), null);
		for(String messageKey : globalDetailKeys){
			smPreferencesDetails.addItem(messages.getValue(messageKey, actualLocale), detailCommand);
		}
		for(MenuItem mi : smPreferencesDetails.getChildren()){
			mi.setCheckable(true);
			mi.setChecked(true);
		}
	}
	
	public void filterDetailItems(){
		if(mainView == null){
			mainView = getMainViewFromCurrentUI();
		}
		if(lastDetailFilter != null){
			((IndexedContainer)mainView.getTblDetails().getContainerDataSource()).removeContainerFilter(lastDetailFilter);
		}
		List<String> itemMessageKeysToFilter = new ArrayList<String>();
		for (MenuItem item : smPreferencesDetails.getChildren()){
			if(item.isChecked()){
				for(String key : globalDetailKeys){
					if(item.getText().equals(messages.getValue(key, actualLocale))){
						itemMessageKeysToFilter.add(key);
						break;
					}
				}
			}
		}
		for(String detail : globalDetailKeys){
			String target = null;
			if(detail.contains(MESSAGE_RESOURCE_HISTORY)){
				target = MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_HISTORY;
			}else if(detail.contains(MESSAGE_RESOURCE_FILE)){
				target = MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_FILE;
			}else{
				target = MESSAGE_RESOURCE_BASE;
			}
			if(itemMessageKeysToFilter.contains(detail)){
		        prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES)
		        .node(JadeBSConstants.PREF_NODE_VISIBLE_DETAILS).putBoolean(detail.replace(target, ""), true);
			}else{
		        prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES)
		        .node(JadeBSConstants.PREF_NODE_VISIBLE_DETAILS).putBoolean(detail.replace(target, ""), false);
			}
		}
		detailFilter = new DetailFilter(itemMessageKeysToFilter);
		if(mainView == null){
			mainView = getMainViewFromCurrentUI();
		}
		((IndexedContainer)mainView.getTblDetails().getContainerDataSource()).addContainerFilter(detailFilter);
		lastDetailFilter = detailFilter;
		mainView.getTblDetails().markAsDirty();
	}
	
	private List<String> setGlobalDetailKeys() {
		List<String> keys = new ArrayList<String>();
		keys.add(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.STATUS.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.GUID.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.LAST_ERROR_MESSAGE.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeFileColumns.SOURCE_HOST.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeFileColumns.SOURCE_HOST_IP.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeFileColumns.SOURCE_USER.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeFileColumns.SOURCE_DIR.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeFileColumns.SOURCE_FILENAME.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeFileColumns.MD5.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeFileColumns.FILE_SIZE.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.TARGET_HOST.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.TARGET_HOST_IP.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.TARGET_USER.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.TARGET_DIR.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.TARGET_FILENAME.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.PROTOCOL.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.PORT.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.OPERATION.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.TRANSFER_START.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.PID.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.PPID.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.LOG_FILENAME.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeFileColumns.MANDATOR.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.JUMP_HOST.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.JUMP_HOST_IP.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.JUMP_USER.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.JUMP_PROTOCOL.getName());
		keys.add(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.JUMP_PORT.getName());
		keys.add(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_FILE + JadeFileColumns.CREATED.getName());
		keys.add(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_FILE + JadeFileColumns.CREATED_BY.getName());
		keys.add(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_FILE + JadeFileColumns.JADE_FILE_MODIFIED.getName());
		keys.add(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_FILE + JadeFileColumns.JADE_FILE_MODIFIED_BY.getName());
		keys.add(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_HISTORY + JadeHistoryFileColumns.CREATED.getName());
		keys.add(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_HISTORY + JadeHistoryFileColumns.CREATED_BY.getName());
		keys.add(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_HISTORY + JadeHistoryFileColumns.MODIFIED.getName());
		keys.add(MESSAGE_RESOURCE_BASE + MESSAGE_RESOURCE_HISTORY + JadeHistoryFileColumns.MODIFIED_BY.getName());
		return keys;
	}
	
	public MenuItem getSmDuplicatesFilter() {
		return smDuplicatesFilter;
	}

	public MenuItem getSmPreferencesReuseFilter() {
		return smPreferencesReuseFilter;
	}
	
	public MenuItem getSmPreferencesDetails() {
		return smPreferencesDetails;
	}
	
	private MainView getMainViewFromCurrentUI(){
		return ((JADEHistoryViewerUI)UI.getCurrent()).getMainView();
	}
	
}
