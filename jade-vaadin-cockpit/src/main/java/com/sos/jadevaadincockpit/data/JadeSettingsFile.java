package com.sos.jadevaadincockpit.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Options.SOSOptionElement;
import com.sos.JSHelper.io.Files.JSIniFile;
import com.sos.JSHelper.io.Files.SOSProfileEntry;
import com.sos.JSHelper.io.Files.SOSProfileSection;
import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.globals.ApplicationAttributes;
import com.sos.jadevaadincockpit.view.ProfileTabSheet;
import com.sos.jadevaadincockpit.view.ProfileTree;
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
	 * Load a settings file (e.g. ini, xml) and migrate the content to a container.
	 * @param filepath The path of the settings file.
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
				
				// -------------------- dbg
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
			// set tree's selection on the first profile of the new settings file
			Iterator<?> it = profileTree.getChildren(rootId).iterator();
			if (it.hasNext()) {
				profileTree.setValue(it.next());
			}
			
			JadevaadincockpitUI.getCurrent().getMainView().getJadeMenuBar().setSaveItemsEnabled(true);
		}
	}
	
	/**
	 * Close a settings file. 
	 * @param itemId
	 */
	public void closeSettingsFile(Object itemId) {
		
		// remove the item and all its children from the container.
		profileContainer.removeItemRecursively(itemId);
		
		// remove all tabs // FIXME remove only the tabs related to the closed file
		ProfileTabSheet profileTabSheet = JadevaadincockpitUI.getCurrent().getMainView().getProfileTabSheet();
		profileTabSheet.removeAllComponents();
		
		// disable save-functions // FIXME only if there are no further files opened
		JadevaadincockpitUI.getCurrent().getMainView().getJadeMenuBar().setSaveItemsEnabled(false);
	}
	
	/**
	 * Save a settings file. The itemId may either identify a settings file itself or a profile belonging to the settings file.
	 * @param itemId The ID of the file itself or a profile belonging to the settings file.
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
			
			// TODO wenn alle options auf einem profil gelöscht wurden, sodass es beim schreiben leer ist, darf es nicht geschrieben werden
			
			while (optionsIterator.hasNext()) {

				String optionName = optionsIterator.next();
				SOSOptionElement option = options.get(optionName);
				String optionValue = option.Value();
				if (option.isDirty() && optionValue != null
						&& !optionValue.trim().isEmpty() && !option.isProtected()) {
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
	 * Add a new profile with the given <code>name</code> to the settings file identified by the <code>parentId</code>
	 * @param name The name of the new profile.
	 * @param parentId The ID of the settings file to add the new profile to.
	 * @return True if the add was successful, false otherwise.
	 */
	public boolean addProfile(String name, Object parentId) {
		
		boolean returnValue = false;
		
		if (name != null && parentId != null) {
			// check for empty String and whether the parent item really is a settings file
			if (!name.trim().isEmpty() && profileContainer.hasChildren(parentId)) {
				
				// check if a profile with the given name already exists as a child of the given parent item
				boolean isNew = true;
				for (Object id : profileContainer.getChildren(parentId)) {
					if (((String) profileContainer.getItem(id).getItemProperty(ProfileContainer.PROPERTY.NAME).getValue()).equalsIgnoreCase(name)) {
						isNew = false;
					}
				}
				
				// only proceed if no profile with the given name exists
				if (isNew) {
					
					Object profileId = profileContainer.addProfileItem(name, parentId);
					
					/* FIXME this seems weird. what is "jadeConfigurationFile.SectionName(name)" good for?
					JSIniFile jadeConfigurationFile = (JSIniFile) profileContainer.getItem(parentId).getItemProperty(ProfileContainer.PROPERTY.JADESETTINGSFILE).getValue();
					jadeConfigurationFile.SectionName(name);
					item.getItemProperty(ProfileContainer.PROPERTY.JADESETTINGSFILE).setValue(jadeConfigurationFile);
					*/
					returnValue = true;
				}
			}
		}
		
		return returnValue;
	}
	
	
	/**
	 * Get the container holding all loaded settings files.
	 * @return The ProfileContainer containing the settings files.
	 */
	public ProfileContainer getProfileContainer() {
		return profileContainer;
	}
}
