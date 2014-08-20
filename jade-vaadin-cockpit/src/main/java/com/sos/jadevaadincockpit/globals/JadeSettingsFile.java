package com.sos.jadevaadincockpit.globals;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Options.SOSOptionElement;
import com.sos.JSHelper.io.Files.JSIniFile;
import com.sos.JSHelper.io.Files.SOSProfileEntry;
import com.sos.JSHelper.io.Files.SOSProfileSection;
import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.view.ProfileTabSheet;
import com.sos.jadevaadincockpit.view.ProfileTree;
import com.sos.jadevaadincockpit.viewmodel.ProfileContainer;
import com.vaadin.data.Item;

/**
 * 
 * @author JS
 *
 */
public class JadeSettingsFile implements Serializable {
	private static final long serialVersionUID = -3274869174865461105L;
	private static final Logger logger = Logger.getLogger(JadeSettingsFile.class.getName());
	
	private ProfileContainer profileContainer;
	
	public JadeSettingsFile() {
		logger.setLevel(Level.ALL);
		
		profileContainer = new ProfileContainer();
	}
	
	/**
	 * 
	 * @param filepath
	 */
	public void loadSettingsFile(String filepath) {
		
		
		JSIniFile jadeSettingsFile = new JSIniFile(filepath);
		
		if (jadeSettingsFile.exists()) {
			
			// create root node (settings file)
			Object rootId = profileContainer.addRootItem(jadeSettingsFile);
			
			// create leaves (profiles)
			Map<String, SOSProfileSection> sections = jadeSettingsFile.Sections();
			for (SOSProfileSection section : sections.values()) {
				Object profileItemId = profileContainer.addProfileItem(section.Name(), rootId);
				((JADEOptions) profileContainer.getItem(profileItemId).getItemProperty(ProfileContainer.PROPERTY.JADEOPTIONS).getValue()).ReadSettingsFile();
				
				/**
				 * fill a map with all the profile's options read from the settings file
				 * and set it as a property for the profile; needed to make sure that options
				 * which are not present in the UI forms will be written when saving.
				 */
				HashMap<String, String> optionsFromSettingsFile = new HashMap<String, String>();
				for (SOSProfileEntry entry : section.Entries().values()) {
					optionsFromSettingsFile.put(entry.Name(), entry.Value());
				}
				profileContainer.getItem(profileItemId).getItemProperty(ProfileContainer.PROPERTY.OPTIONSFROMSETTINGSFILE).setValue(optionsFromSettingsFile);
				
				// -------------------- TODO nur zum debuggen
				for (SOSProfileEntry entry : section.Entries().values()) {
					if (!ApplicationAttributes.allOptionsFromSettingsFile.containsKey(entry.Name())) {
						ApplicationAttributes.allOptionsFromSettingsFile.put(entry.Name(), entry.Value());
					}
				}
				// --------------------
				
			}
			
			// set the tree item icons
			profileContainer.initializeIconProperties(rootId);
			
			// sort items
			profileContainer.sortByName();
			
			ProfileTree profileTree = JadevaadincockpitUI.getCurrent().getMainView().getProfileTree();
			// expand added items
			profileTree.expandItemsRecursively(rootId);
			// set tree's selection on first section
			Iterator<?> it = profileTree.getChildren(rootId).iterator();
			if (it.hasNext()) {
				profileTree.setValue(it.next());
			}
			
			JadevaadincockpitUI.getCurrent().getMainView().getJadeMenuBar().setSaveItemsEnabled(true);
		}
	}
	
	/**
	 * 
	 * @param itemId
	 */
	public void closeSettingsFile(Object itemId) {
		
		// clear the container
		profileContainer.removeItemRecursively(itemId);
		
		// remove all tabs
		ProfileTabSheet profileTabSheet = JadevaadincockpitUI.getCurrent().getMainView().getProfileTabSheet();
		profileTabSheet.removeAllComponents(); // TODO do not close ALL tabs
		
		// disable save-functions
		JadevaadincockpitUI.getCurrent().getMainView().getJadeMenuBar().setSaveItemsEnabled(false);
	}
	
	/**
	 * 
	 * @param itemId
	 */
	public void saveSettingsFile(Object itemId) {
		
		String filename = "";
		JSIniFile newJadeSettingsFile = null;
		
		Object rootId;
		
		if (!profileContainer.hasChildren(itemId)) { // item is a profile
			rootId = profileContainer.getParent(itemId);
		} else { // item is a settings file
			rootId = itemId;
		}
		
		Collection<?> profileItemIds = profileContainer.getChildren(rootId);
		
		Item rootItem = profileContainer.getItem(rootId);
		
		filename = (String) rootItem.getItemProperty(ProfileContainer.PROPERTY.NAME).getValue();
		newJadeSettingsFile = new JSIniFile(filename);
		
		for (Object profileItemId : profileItemIds) {
			Item profileItem = profileContainer.getItem(profileItemId);
			String profileName = (String) profileItem.getItemProperty(ProfileContainer.PROPERTY.NAME).getValue();
			SOSProfileSection profile = newJadeSettingsFile.addSection(profileName);
			
			// TODO evtl in die JADEOptions
			
			HashMap<String, SOSOptionElement> options = (HashMap<String, SOSOptionElement>) profileItem.getItemProperty(ProfileContainer.PROPERTY.OPTIONS).getValue();
			Iterator<String> optionsIterator = options.keySet().iterator();
			
			HashMap<String, String> optionsFromSettingsFile = (HashMap<String, String>) profileItem.getItemProperty(ProfileContainer.PROPERTY.OPTIONSFROMSETTINGSFILE).getValue();
			
			while (optionsIterator.hasNext()) {

				String optionName = optionsIterator.next();
				SOSOptionElement option = options.get(optionName);
				String optionValue = option.Value();
				if (option.isDirty() && optionValue != null
						&& !optionValue.trim().isEmpty()) {
					profile.addEntry(optionName, optionValue);
				}
				if (optionsFromSettingsFile.containsKey(optionName)) {
					optionsFromSettingsFile.remove(optionName);
				}
			}
			
			Iterator<String> optionsFromSettingsFileIterator = optionsFromSettingsFile.keySet().iterator();
			
			while (optionsFromSettingsFileIterator.hasNext()) {
				String optionName = optionsFromSettingsFileIterator.next();
				String optionValue = optionsFromSettingsFile.get(optionName);
				System.out.println(profileName + " --- " + optionName + " = " + optionValue);
				
				profile.addEntry(optionName, optionValue);
			}
			// --------------------------------
		}
		
		logger.log(Level.FINE, String.format("Saving settings file '%1$s'", filename));
		newJadeSettingsFile.saveAs(ApplicationAttributes.getBasePath() + "/WEB-INF/uploads/" + filename);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public boolean addProfile(String name) {
		// TODO check validity, add profile
		boolean returnValue = false;
		if (name != null) {
			if (!name.trim().isEmpty()) {
				ProfileTree profileTree = JadevaadincockpitUI.getCurrent().getMainView().getProfileTree();
				boolean isNew = true;
				for (Object id : profileContainer.getItemIds()) {
					if (((String) profileContainer.getItem(id).getItemProperty(ProfileContainer.PROPERTY.NAME).getValue()).equalsIgnoreCase(name)) {
						isNew = false;
					}
				}
				if (isNew) {
					Object parentId;
					if (profileContainer.hasChildren(profileTree.getValue())) { // current selection is file
						parentId = profileTree.getValue();
					} else { // current selection is profile
						parentId = profileContainer.getParent(profileTree.getValue());
					}
					
					UUID profileId = UUID.randomUUID();
					Item item = profileContainer.addItem(profileId);
					profileContainer.setParent(profileId, parentId);
					profileContainer.setChildrenAllowed(profileId, false);
					
					item.getItemProperty(ProfileContainer.PROPERTY.ID).setValue(profileId);
					item.getItemProperty(ProfileContainer.PROPERTY.NAME).setValue(name);
					item.getItemProperty(ProfileContainer.PROPERTY.OLDNAME).setValue(name);
					item.getItemProperty(ProfileContainer.PROPERTY.NODETYPE).setValue(ProfileContainer.NODETYPE.PROFILE);
					JSIniFile jadeConfigurationFile = (JSIniFile) profileContainer.getItem(parentId).getItemProperty(ProfileContainer.PROPERTY.JADESETTINGSFILE).getValue();
					jadeConfigurationFile.SectionName(name);
					item.getItemProperty(ProfileContainer.PROPERTY.JADESETTINGSFILE).setValue(jadeConfigurationFile);
					item.getItemProperty(ProfileContainer.PROPERTY.OPTIONS).setValue(new HashMap<String, SOSOptionElement>());
					
					JADEOptions options = new JADEOptions();
					options.gflgSubsituteVariables = false;
					
					HashMap<String, String> map = new HashMap<String, String>();
					options.setAllOptions(map);
					
					options.settings.Value(jadeConfigurationFile.getAbsolutePath());
					options.profile.Value(name);
					
					item.getItemProperty(ProfileContainer.PROPERTY.JADEOPTIONS).setValue(options);
					item.getItemProperty(ProfileContainer.PROPERTY.OPTIONSFROMSETTINGSFILE).setValue(map);
					
					returnValue = true;
				}
			}
		}
		return returnValue;
	}
	
	/**
	 * 
	 * @return
	 */
	public ProfileContainer getProfileContainer() {
		return profileContainer;
	}
}
