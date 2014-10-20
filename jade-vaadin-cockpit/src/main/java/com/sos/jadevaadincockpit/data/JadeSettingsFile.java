package com.sos.jadevaadincockpit.data;

import java.io.File;
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
import com.sos.jadevaadincockpit.view.FormsTabSheet;
import com.sos.jadevaadincockpit.view.ProfileTabSheet;
import com.sos.jadevaadincockpit.view.ProfileTree;
import com.sos.jadevaadincockpit.view.event.FileSavedCallback;
import com.vaadin.data.Item;
import com.vaadin.ui.Component;

/**
 * 
 * @author JS
 *
 */
public class JadeSettingsFile extends ProfileContainer {
	private static final long serialVersionUID = -3274869174865461105L;
	
	private static final Logger logger = Logger.getLogger(JadeSettingsFile.class.getName());
	
	private ProfileContainer profileContainer;
	
	public JadeSettingsFile() {
		logger.setLevel(Level.ALL);
		
		profileContainer = this;
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
				
//				JADEOptions options = ((JADEOptions) profileContainer.getItem(profileItemId).getItemProperty(ProfileContainer.PROPERTY.JADEOPTIONS).getValue());
				
				/**
				 * Fill a map with all the profile's options read from the settings file
				 * and set it as a property for the profile; needed to make sure that options
				 * which are not displayed in the UI forms will be written when saving.
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
			/*
			// set tree's selection on the first profile of the new settings file
			Iterator<?> it = profileTree.getChildren(rootId).iterator();
			if (it.hasNext()) {
				profileTree.setValue(it.next());
			}
			*/
			
			// set tree's selection on the new settings file to open the overview
			profileTree.setValue(rootId);
			
			JadevaadincockpitUI.getCurrent().getMainView().getJadeMenuBar().setSaveItemsEnabled(true);
		}
	}
	
	/**
	 * Close a settings file. 
	 * @param itemId the id of the settings file item
	 */
	public void closeSettingsFile(Object itemId) {
		
		ProfileTabSheet profileTabSheet = JadevaadincockpitUI.getCurrent().getMainView().getProfileTabSheet();
		
		/**
		 * close all tabs belonging to the selected settings file TODO maybe move to ProfileTabSheet
		 */
		// iterate over the profiles belonging to this settings file
		Iterator<?> childIterator = profileContainer.getChildren(itemId).iterator();
		while (childIterator.hasNext()) {
			Object childId = childIterator.next();
			Item childItem = profileContainer.getItem(childId);
			// iterate over the tabs and close the one belonging to the current profile
			Iterator<Component> componentIterator = profileTabSheet.iterator();	
			while (componentIterator.hasNext()) {
				FormsTabSheet component = (FormsTabSheet) componentIterator.next(); // tab content
				// if the tab belongs to the profile item -> close the tab
				if (component.getProfileItem().equals(childItem)) {
					profileTabSheet.removeComponent(component);
					break;
				}
			}
		}
		
		// remove the item and all its children from the container.
		profileContainer.removeItemRecursively(itemId);
		
		// disable save-functions if no more settings files are opened
		JadevaadincockpitUI.getCurrent().getMainView().getJadeMenuBar().setSaveItemsEnabled(!profileContainer.getItemIds().isEmpty());
	}
	
	/**
	 * Save a settings file. The itemId may either identify a settings file itself or a profile belonging to the settings file to save.
	 * @param itemId The ID of the file itself or a profile belonging to the settings file.
	 */
	public void saveSettingsFile(Object itemId) { // TODO Fehlerfälle?
		
		String filename = "";
		JSIniFile newJadeSettingsFile = null;
		
		// ID of the settings file
		Object rootId;
		// item is a profile
		if (!profileContainer.hasChildren(itemId)) {
			rootId = profileContainer.getParent(itemId);
		// item is a settings file
		} else {
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
				
				// options from includes are protected and will not be written to the including profile
				if (!option.isProtected()) {
					// only dirty options (with changed values) will be written
					if (option.isDirty()) {
						// ignore null and empty values
						if (optionValue != null && !optionValue.trim().isEmpty()) {
							profile.addEntry(optionName, optionValue);
						}
					}
				}
				if (optionsFromSettingsFile.containsKey(optionName)) {
					optionsFromSettingsFile.remove(optionName);
				}
			}
			
			Iterator<String> optionsFromSettingsFileIterator = optionsFromSettingsFile.keySet().iterator();
			
			while (optionsFromSettingsFileIterator.hasNext()) {
				String optionName = optionsFromSettingsFileIterator.next();
				String optionValue = optionsFromSettingsFile.get(optionName);
				
				profile.addEntry(optionName, optionValue);
			}
			// --------------------------------
		}
		
		logger.log(Level.FINE, String.format("Saving settings file '%1$s'", filename));
		newJadeSettingsFile.saveAs(ApplicationAttributes.getBasePath() + "/WEB-INF/uploads/" + filename);
	}
	
	/**
	 * Save a settings file. The itemId may either identify a settings file itself or a profile belonging to the settings file to save.
	 * @param itemId The ID of the file itself or a profile belonging to the settings file.
	 * @param callback
	 */
	public void saveSettingsFile(Object itemId, FileSavedCallback callback) {
		
		saveSettingsFile(itemId); // TODO Fehlerfälle?
		
		callback.onSuccess();
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
					
					JadevaadincockpitUI.getCurrent().getMainView().getProfileTree().setValue(profileId);
					returnValue = true;
				}
			}
		}
		
		return returnValue;
	}
	
	/**
	 * Deletes a profile from the container.
	 * @param target id of the item to delete
	 */
	public void deleteProfile(Object target) {
		
		Item targetItem = profileContainer.getItem(target);
		ProfileTabSheet profileTabSheet = JadevaadincockpitUI.getCurrent().getMainView().getProfileTabSheet();
		Component targetTab = profileTabSheet.getTab(targetItem);

		profileTabSheet.removeComponent(targetTab);
		
		// delete profile from container
		profileContainer.removeItemRecursively(target);
		
	}
	
	/**
	 * Rename a settings file.
	 * @param itemId The id of the settings file
	 * @param newName The new name
	 */
	@SuppressWarnings("unchecked")
	public void renameSettingsFile(Object itemId, String newName) {
		// get the item from the container representing the settings file
		Item settingsFileItem = profileContainer.getItem(itemId);
		// rename the item
		settingsFileItem.getItemProperty(ProfileContainer.PROPERTY.NAME).setValue(newName);
		// get the new filepath
		String newPath = new File(ApplicationAttributes.getBasePath() + "/WEB-INF/uploads/" + newName).getAbsolutePath();
		// set the filepath property to the new filepath
		settingsFileItem.getItemProperty(ProfileContainer.PROPERTY.FILEPATH).setValue(newPath);
		// change jadeoptions.settings for all profiles
		Iterator<?> profileIterator = profileContainer.getChildren(itemId).iterator();
		while(profileIterator.hasNext()) {
			Item profile = profileContainer.getItem(profileIterator.next());
			JADEOptions jadeoptions = (JADEOptions) profile.getItemProperty(ProfileContainer.PROPERTY.JADEOPTIONS).getValue();
			jadeoptions.settings.Value(newPath);
		}
	}
	
	/**
	 * 
	 * @param itemId The id of the profile
	 * @param newName The new name
	 */
	@SuppressWarnings("unchecked")
	public void renameProfile(Object itemId, String newName) {
		// get the item from the container representing the profile
		Item profileItem = profileContainer.getItem(itemId);
		// rename the item
		profileItem.getItemProperty(ProfileContainer.PROPERTY.NAME).setValue(newName);
		// change jadeoptions.profile
		JADEOptions jadeoptions = (JADEOptions) profileItem.getItemProperty(ProfileContainer.PROPERTY.JADEOPTIONS).getValue();
		jadeoptions.profile.Value(newName);
		
	}
	
	
	/**
	 * Get the container holding all loaded settings files.
	 * @return The ProfileContainer containing the settings files.
	 */
	public ProfileContainer getProfileContainer() {
		return profileContainer;
	}
}
