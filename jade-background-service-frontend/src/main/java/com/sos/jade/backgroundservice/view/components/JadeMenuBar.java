package com.sos.jade.backgroundservice.view.components;

import static com.sos.jade.backgroundservice.BackgroundserviceUI.jadeBsOptions;
import static com.sos.jade.backgroundservice.BackgroundserviceUI.parentNodeName;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

import com.sos.jade.backgroundservice.BackgroundserviceUI;
import com.sos.jade.backgroundservice.constants.JadeBSConstants;
import com.sos.jade.backgroundservice.data.JadeDetailsContainer;
import com.sos.jade.backgroundservice.data.JadeHistoryDetailItem;
import com.sos.jade.backgroundservice.enums.JadeFileColumns;
import com.sos.jade.backgroundservice.enums.JadeHistoryFileColumns;
import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.sos.jade.backgroundservice.view.MainView;
import com.sos.jade.backgroundservice.view.components.filter.DetailFilter;
import com.sos.jade.backgroundservice.view.components.filter.DuplicatesFilter;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.MenuBar.MenuItem;

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
	private static final String MESSAGE_RESOURCE_HISTORY = "fileHistory.";
	private Preferences prefs = jadeBsOptions.getPreferenceStore();
	private Logger log = Logger.getLogger(JadeMenuBar.class);
	private MenuItem mFile;
	private MenuItem mFilter;
	private MenuItem mPreferences;
	private MenuItem mHelp;
	private MenuItem smActivateFilter;
	private MenuItem smDuplicatesFilter;
	private MenuItem smLoadFilter;
	private MenuItem smSaveFilter;
	private MenuItem smPreferencesLanguages;
	private MenuItem smPreferencesReuseFilter;
	private MenuItem smPreferencesDetails;
	private MenuItem ssmGermanCheck;
	private MenuItem ssmEnglishUKCheck;
	private MenuItem ssmEnglishUSCheck;
	private MenuItem ssmSpanishCheck;
	private MenuItem ssmActivateAllChecks;
	private MenuItem ssmDeactivateAllChecks;
	private JadeBSMessages messages;
	private boolean duplicatesFilterActive = false;
	private DuplicatesFilter duplicatesFilter = new DuplicatesFilter();
	private DetailFilter lastDetailFilter;
	private DetailFilter detailFilter;
	private Locale actualLocale;
	private Locale lastLocale;
	private List<String> globalDetailKeys;
	private MenuBar.Command detailCommand;


	public JadeMenuBar(JadeBSMessages messages) {
		this.messages = messages;
		setAutoOpen(true);
		addStyleName("jadeMenuBar");
		actualLocale = VaadinSession.getCurrent().getLocale();
		globalDetailKeys = setGlobalDetailKeys();
		detailCommand = new MenuBar.Command() {
		    public void menuSelected(MenuItem selectedItem) {
	    		filterDetailItems();
		    }  
		};
		createTopLevelMenuItems();
		createFilterMenuItems();
		createPreferencesLanguageMenuItems();
		createPreferencesReuseFilterMenuItem();
		createPreferencesVisibleDetails();
	}
	
	private void createTopLevelMenuItems(){
		this.mFile = addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "file", actualLocale), null);
		this.mFilter = addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "filter", actualLocale), null);
		this.mPreferences = addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "preferences", actualLocale), null);
		this.mHelp = addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "help", actualLocale), null);
	}
	
	private void createFilterMenuItems(){
		this.smActivateFilter = mFilter.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "doFilter", actualLocale), new MenuBar.Command() {
			private static final long serialVersionUID = 1L;
			public void menuSelected(MenuItem selectedItem) {
				UI.getCurrent().addWindow(((BackgroundserviceUI)UI.getCurrent()).getModalWindow());
		    }  
		});
		this.smDuplicatesFilter = mFilter.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "removeDuplicates", actualLocale), new MenuBar.Command() {
			private static final long serialVersionUID = 1L;
			public void menuSelected(MenuItem selectedItem) {
				if(((MainView)UI.getCurrent().getContent()).getTblDetails().isVisible()){
					((MainView)UI.getCurrent().getContent()).getTblDetails().setVisible(false);
					((MainView)UI.getCurrent().getContent()).setMarkedRow(null);
				}
				duplicatesFilter.setHistoryItems(((MainView)UI.getCurrent().getContent()).getHistoryItems());
				if(selectedItem.isChecked()){
					((IndexedContainer)((MainView)UI.getCurrent().getContent()).getTblFileHistory().getContainerDataSource()).addContainerFilter(duplicatesFilter);
				}else{
					((IndexedContainer)((MainView)UI.getCurrent().getContent()).getTblFileHistory().getContainerDataSource()).removeContainerFilter(duplicatesFilter);
				}
				prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES)
				.node(JadeBSConstants.PREF_NODE_PREFERENCES_GENERAL).putBoolean(JadeBSConstants.PREF_KEY_REMOVE_DUPLICATES, selectedItem.isChecked());
		    }  
		});
		this.smDuplicatesFilter.setCheckable(true);
		this.smDuplicatesFilter.setEnabled(true);
	}

	private void createPreferencesReuseFilterMenuItem(){
		mPreferences.addSeparator();
		smPreferencesReuseFilter = this.mPreferences.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "reuseFilter", actualLocale), new MenuBar.Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
				prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_MENU_BAR).node(JadeBSConstants.PREF_NODE_PREFERENCES)
				.node(JadeBSConstants.PREF_NODE_PREFERENCES_GENERAL).putBoolean(JadeBSConstants.PREF_KEY_LAST_USED_FILTER, selectedItem.isChecked());
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
				((MainView)UI.getCurrent().getContent()).getImgDe().setVisible(selectedItem.isChecked());
				((MainView)UI.getCurrent().getContent()).refreshButtonVisibility();
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
				((MainView)UI.getCurrent().getContent()).getImgUk().setVisible(selectedItem.isChecked());
				((MainView)UI.getCurrent().getContent()).refreshButtonVisibility();
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
				((MainView)UI.getCurrent().getContent()).getImgUs().setVisible(selectedItem.isChecked());
				((MainView)UI.getCurrent().getContent()).refreshButtonVisibility();
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
				((MainView)UI.getCurrent().getContent()).getImgEs().setVisible(selectedItem.isChecked());
				((MainView)UI.getCurrent().getContent()).refreshButtonVisibility();
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
		        ((MainView)UI.getCurrent().getContent()).refreshButtonVisibility();
			}
		});
		ssmDeactivateAllChecks = smPreferencesLanguages.addItem(messages.getValue(MESSAGE_RESOURCE_BASE + "checkNone", actualLocale), new MenuBar.Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
				((MainView)UI.getCurrent().getContent()).getImgDe().setVisible(false);
				((MainView)UI.getCurrent().getContent()).getImgUk().setVisible(false);
				((MainView)UI.getCurrent().getContent()).getImgUs().setVisible(false);
				((MainView)UI.getCurrent().getContent()).getImgEs().setVisible(false);
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
		        ((MainView)UI.getCurrent().getContent()).refreshLocalization(defaultLocale);
			}
		});
	}
	
	private void uncheckAllLangs(){
    	ssmGermanCheck.setChecked(false);
    	ssmEnglishUKCheck.setChecked(false);
    	ssmEnglishUSCheck.setChecked(false);
    	ssmSpanishCheck.setChecked(false);
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
				((MainView)UI.getCurrent().getContent()).getImgDe().setVisible(german);
				ssmEnglishUKCheck.setChecked(englishUK);
				((MainView)UI.getCurrent().getContent()).getImgUk().setVisible(englishUK);
				ssmEnglishUSCheck.setChecked(englishUS);
				((MainView)UI.getCurrent().getContent()).getImgUs().setVisible(englishUS);
				ssmSpanishCheck.setChecked(spanish);
				((MainView)UI.getCurrent().getContent()).getImgEs().setVisible(spanish);
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
		mFile.setText(messages.getValue(MESSAGE_RESOURCE_BASE + "file", actualLocale));
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
		if(lastDetailFilter != null){
			((JadeDetailsContainer)((MainView)UI.getCurrent().getContent()).getTblDetails().getContainerDataSource()).removeContainerFilter(lastDetailFilter);
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
		((JadeDetailsContainer)((MainView)UI.getCurrent().getContent()).getTblDetails().getContainerDataSource()).addContainerFilter(detailFilter);
		lastDetailFilter = detailFilter;
		((MainView)UI.getCurrent().getContent()).getTblDetails().markAsDirty();
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
		keys.add(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.TRANSFER_TIMESTAMP.getName());
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
	
}
