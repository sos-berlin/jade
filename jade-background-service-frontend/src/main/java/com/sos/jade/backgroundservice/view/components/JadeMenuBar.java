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
import com.sos.jade.backgroundservice.enums.JadeHistoryFileColumns;
import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.sos.jade.backgroundservice.view.MainView;
import com.sos.jade.backgroundservice.view.components.filter.DuplicatesFilter;
import com.vaadin.data.util.IndexedContainer;
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
	private Preferences prefs = jadeBsOptions.getPreferenceStore();
	private Logger log = Logger.getLogger(JadeMenuBar.class);
	
	private JadeBSMessages messages;
	private boolean duplicatesFilterActive = false;
	private DuplicatesFilter filter = new DuplicatesFilter();


	public JadeMenuBar(JadeBSMessages messages) {
		this.messages = messages;
		setAutoOpen(true);
		addStyleName("jadeMenuBar");
		createTopLevelMenuItems();
		createFilterMenuItems();
		createPreferencesLanguageMenuItems();
		createPreferencesReuseFilterMenuItem();
	}
	
	private void createTopLevelMenuItems(){
		this.mFile = addItem(messages.getValue("JadeMenuBar.file", VaadinSession.getCurrent().getLocale()), null);
		this.mFilter = addItem(messages.getValue("JadeMenuBar.filter", VaadinSession.getCurrent().getLocale()), null);
		this.mPreferences = addItem(messages.getValue("JadeMenuBar.preferences", VaadinSession.getCurrent().getLocale()), null);
		this.mHelp = addItem(messages.getValue("JadeMenuBar.help", VaadinSession.getCurrent().getLocale()), null);
	}
	
	private void createFilterMenuItems(){
		this.smActivateFilter = mFilter.addItem(messages.getValue("JadeMenuBar.doFilter", VaadinSession.getCurrent().getLocale()), new MenuBar.Command() {
			private static final long serialVersionUID = 1L;
			public void menuSelected(MenuItem selectedItem) {
				((MainView)UI.getCurrent().getContent()).setDetailViewVisible(false);
				UI.getCurrent().addWindow(((BackgroundserviceUI)UI.getCurrent()).getModalWindow());
		    }  
		});
		this.smDuplicatesFilter = mFilter.addItem(messages.getValue("JadeMenuBar.removeDuplicates", VaadinSession.getCurrent().getLocale()), new MenuBar.Command() {
			private static final long serialVersionUID = 1L;
			public void menuSelected(MenuItem selectedItem) {
				if(((MainView)UI.getCurrent().getContent()).getTblDetails().isVisible()){
					((MainView)UI.getCurrent().getContent()).getTblDetails().setVisible(false);
					((MainView)UI.getCurrent().getContent()).setMarkedRow(null);
				}
				filter.setHistoryItems(((MainView)UI.getCurrent().getContent()).getHistoryItems());
				if(selectedItem.isChecked()){
					((IndexedContainer)((MainView)UI.getCurrent().getContent()).getTblFileHistory().getContainerDataSource()).addContainerFilter(filter);
				}else{
					((IndexedContainer)((MainView)UI.getCurrent().getContent()).getTblFileHistory().getContainerDataSource()).removeContainerFilter(filter);
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
		smPreferencesReuseFilter = this.mPreferences.addItem(messages.getValue("JadeMenuBar.reuseFilter", VaadinSession.getCurrent().getLocale()), new MenuBar.Command() {
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
		smPreferencesLanguages = mPreferences.addItem(messages.getValue("JadeMenuBar.lang", VaadinSession.getCurrent().getLocale()), null);
		ssmGermanCheck = smPreferencesLanguages.addItem(messages.getValue("JadeMenuBar.checkGerman", VaadinSession.getCurrent().getLocale()), new MenuBar.Command() {
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
		ssmEnglishUKCheck = smPreferencesLanguages.addItem(messages.getValue("JadeMenuBar.checkUK", VaadinSession.getCurrent().getLocale()), new MenuBar.Command() {
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
		ssmEnglishUSCheck = smPreferencesLanguages.addItem(messages.getValue("JadeMenuBar.checkUS", VaadinSession.getCurrent().getLocale()), new MenuBar.Command() {
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
		ssmSpanishCheck = smPreferencesLanguages.addItem(messages.getValue("JadeMenuBar.checkSpanish", VaadinSession.getCurrent().getLocale()), new MenuBar.Command() {
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
		ssmActivateAllChecks = smPreferencesLanguages.addItem(messages.getValue("JadeMenuBar.checkAll", VaadinSession.getCurrent().getLocale()), new MenuBar.Command() {
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
		ssmDeactivateAllChecks = smPreferencesLanguages.addItem(messages.getValue("JadeMenuBar.checkNone", VaadinSession.getCurrent().getLocale()), new MenuBar.Command() {
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
		mFile.setText(messages.getValue("JadeMenuBar.file", locale));
		mFilter.setText(messages.getValue("JadeMenuBar.filter", locale));
		smActivateFilter.setText(messages.getValue("JadeMenuBar.doFilter", locale));
		smDuplicatesFilter.setText(messages.getValue("JadeMenuBar.removeDuplicates", locale));
		mPreferences.setText(messages.getValue("JadeMenuBar.preferences", locale));
		mHelp.setText(messages.getValue("JadeMenuBar.help", locale));
		smPreferencesLanguages.setText(messages.getValue("JadeMenuBar.lang", locale));
		smPreferencesReuseFilter.setText(messages.getValue("JadeMenuBar.reuseFilter", locale));
		smPreferencesDetails.setText(messages.getValue("JadeMenuBar.visibleDetails", locale));
		ssmGermanCheck.setText(messages.getValue("JadeMenuBar.checkGerman", locale));
		ssmEnglishUKCheck.setText(messages.getValue("JadeMenuBar.checkUK", locale));
		ssmEnglishUSCheck.setText(messages.getValue("JadeMenuBar.checkUS", locale));
		ssmSpanishCheck.setText(messages.getValue("JadeMenuBar.checkSpanish", locale));
		ssmActivateAllChecks.setText(messages.getValue("JadeMenuBar.checkAll", locale));
		ssmDeactivateAllChecks.setText(messages.getValue("JadeMenuBar.checkNone", locale));
		JadeMenuBar.this.markAsDirty();
	}

	public MenuItem getSmDuplicatesFilter() {
		return smDuplicatesFilter;
	}

	public MenuItem getSmPreferencesReuseFilter() {
		return smPreferencesReuseFilter;
	}
	
	private void createPreferencesVisibleDetails(){
		smPreferencesDetails = mPreferences.addItem(messages.getValue("JadeMenuBar.visibleDetails", VaadinSession.getCurrent().getLocale()), null);
		for(JadeHistoryFileColumns column : JadeHistoryFileColumns.values()){
			
		}
	}
}
