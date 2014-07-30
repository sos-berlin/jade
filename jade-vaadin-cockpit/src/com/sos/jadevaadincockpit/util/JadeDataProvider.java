package com.sos.jadevaadincockpit.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Options.SOSOptionElement;
import com.sos.JSHelper.io.Files.JSIniFile;
import com.sos.JSHelper.io.Files.SOSProfileEntry;
import com.sos.JSHelper.io.Files.SOSProfileSection;
import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.globals.Globals;
import com.sos.jadevaadincockpit.view.ProfileTabSheet;
import com.sos.jadevaadincockpit.view.ProfileTree;
import com.sos.jadevaadincockpit.viewmodel.ProfileContainer;
import com.vaadin.data.Item;
import com.vaadin.ui.Notification;

/**
 * 
 * @author JS
 *
 */
public class JadeDataProvider implements Serializable {
	private static final long serialVersionUID = -8882251496758382683L;

	
	public void loadSettingsFile(final String filePath) {
		
		ProfileTree profileTree = JadevaadincockpitUI.getCurrent().getJadeMainUi().getProfileTree();
		
		ProfileContainer profileContainer = profileTree.getProfileContainer();
		
		JSIniFile jadeSettingsFile = new JSIniFile(filePath);
		
		if (jadeSettingsFile.exists()) {
			
			// create root node (settings file)
			Object rootId = profileContainer.addRootItem(jadeSettingsFile);
			
			// create leaves (profiles)
			Map<String, SOSProfileSection> sections = jadeSettingsFile.Sections();
			for (SOSProfileSection section : sections.values()) {
				Object profileItemId = profileContainer.addProfileItem(section.Name(), rootId);
				
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
					if (!Globals.allOptionsFromSettingsFile.containsKey(entry.Name())) {
						Globals.allOptionsFromSettingsFile.put(entry.Name(), entry.Value());
					}
				}
				// --------------------
				
			}
			
			// set the tree item icons
			profileContainer.initializeIconProperties(rootId);
			
			// sort items
			profileContainer.sortByName();
			
			// expand added items
			profileTree.expandItemsRecursively(rootId);
			// set tree's selection on first section
			Iterator<?> it = profileTree.getChildren(rootId).iterator();
			if (it.hasNext()) {
				profileTree.setValue(it.next());
			}
			
			JadevaadincockpitUI.getCurrent().getJadeMainUi().getJadeMenuBar().setSaveItemsEnabled(true);
		}
	}
	
	/**
	 * Removes the settings file identified by the itemId from the tree view and closes all corresponding tabs.
	 * @param itemId the settings file id to close
	 */
	public void closeSettingsFile(Object itemId) {
		
		// clear the container
		ProfileContainer container = (ProfileContainer) JadevaadincockpitUI.getCurrent().getJadeMainUi().getProfileTree().getContainerDataSource();
		container.removeItemRecursively(itemId);
		
		// remove all tabs
		ProfileTabSheet profileTabSheet = JadevaadincockpitUI.getCurrent().getJadeMainUi().getProfileTabSheet(); // TODO do not close ALL tabs
		profileTabSheet.removeAllComponents();
		
		// disable save-functions
		JadevaadincockpitUI.getCurrent().getJadeMainUi().getJadeMenuBar().setSaveItemsEnabled(false);
	}
	
//	public ParameterContainer loadParameters(Item profileItem) {
//		
////		ParameterContainer parameterContainer = (ParameterContainer) profileItem.getItemProperty(ProfileContainer.PROPERTY.ENTRIES).getValue();
//		
//		JADEOptions jadeOptions = (JADEOptions) profileItem.getItemProperty(ProfileContainer.PROPERTY.JADEOPTIONS).getValue();
//		
//		// get the corresponding configFile (JSIniFile)
//		JSIniFile jadeConfigurationFile = (JSIniFile) profileItem.getItemProperty(ProfileContainer.PROPERTY.JADESETTINGSFILE).getValue();
//		
//		String profileName = (String) profileItem.getItemProperty(ProfileContainer.PROPERTY.NAME).getValue();
//		Object profileId= profileItem.getItemProperty(ProfileContainer.PROPERTY.ID).getValue();
//		
////		if (parameterContainer == null) {
////			
////			parameterContainer = new ParameterContainer();
////			/** 
////			 * für jedes Formularfeld aus ((HashMap<String, AbstractComponent>) treeItem.getItemProperty(ProfileTreeContainer.PROPERTY.COMPONENTS).getValue()) muss ein Eintrag im Container angelegt werden
////			 * alle Einträge, die in der ConfigDatei nicht ausgefüllt sind, sollten in der Tabelle ausgeblendet oder anderweitig markiert werden
////			 */
////			
////			HashMap<String, SOSOptionElement> options = (HashMap<String, SOSOptionElement>) profileItem.getItemProperty(ProfileContainer.PROPERTY.OPTIONS).getValue();
////			Iterator<String> optionsIterator = options.keySet().iterator();
////			while(optionsIterator.hasNext()) {
////				String optionName = optionsIterator.next();
////				// add a new item with the parameter name as id
////				Item item = parameterContainer.addItem(optionName);
////				item.getItemProperty(ParameterContainer.PROPERTY.NAME).setValue(optionName); // the id and name at the same time (should be unique)
////				item.getItemProperty(ParameterContainer.PROPERTY.PROFILENAME).setValue(profileName);
////				item.getItemProperty(ParameterContainer.PROPERTY.PROFILEID).setValue(profileId);
////				item.getItemProperty(
////						ParameterContainer.PROPERTY.OPTIONELEMENT)
////						.setValue(options.get(optionName));
////			}
////			
////			// read the section from the settings file which matches the given treeItem
////			SOSProfileSection profile = jadeConfigurationFile.getSection(profileName.toLowerCase());
////			
////			if (profile != null) {
////				if (profile.Entries() != null) {
////					for (SOSProfileEntry entry : profile.Entries().values()) {
////						// check if the container contains an item with the name
////						// from the settingsFile
////						Item item = parameterContainer.getItem(entry.Name());
////						if (item == null) {
////							item = parameterContainer.getItem(entry.Name()
////									.toLowerCase());
////						}
////						// the container contains a corresponding item
////						if (item != null) {
////							// set the item's properties
////							item.getItemProperty(
////									ParameterContainer.PROPERTY.VALUE)
////									.setValue(entry.Value());
////							
//////							System.out.println("Initializing option '" + item.getItemProperty(ParameterContainer.PROPERTY.NAME).getValue() + "' with value '" + item.getItemProperty(
//////									ParameterContainer.PROPERTY.VALUE).getValue() + "|||" + entry.Value() + "'");
////							
////							item.getItemProperty(
////									ParameterContainer.PROPERTY.OLDVALUE)
////									.setValue(entry.Value());
////						} else {
////							/**
////							 * TODO item konnte nicht gefunden werden. Entweder
////							 * wurde kein Formularfeld für den entsprechenden
////							 * Parameter erstellt, oder der Parametername aus
////							 * der ConfigDatei stimmt nicht mit dem ShortKey des
////							 * entsprechenden SOSOptionElements überein.
////							 * 
////							 * Diese items sollten separat behandelt werden.
////							 * (Wie?)
////							 */
////						}
////					}
////				}
////			}
////
////			// set the newly created container
////			profileItem.getItemProperty(ProfileContainer.PROPERTY.ENTRIES).setValue(parameterContainer);
////			
//			// fill the JADEOptions-Object with the loaded params
//			String[] cmdLineParameters = new String[] { "-settings=" + jadeConfigurationFile.getAbsolutePath(), "-profile=" + profileItem.getItemProperty(ProfileContainer.PROPERTY.NAME).getValue(), "-verbose=0" };
//			try {
//				jadeOptions.CommandLineArgs(cmdLineParameters);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
////		}
////		return parameterContainer;
//	}

	/**
	 * Saves the settings file specified by the given <code>itemId</code>.
	 * @param rootId the id of the item thats represents the settings file to save.
	 */
	public void saveSettingsFile(Object itemId) {
		
		ProfileContainer profileContainer = JadevaadincockpitUI.getCurrent().getJadeMainUi().getProfileTree().getProfileContainer();
		
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
		
		newJadeSettingsFile.saveAs(Globals.getBasePath() + "/WEB-INF/uploads/" + filename);
	}
	
	/**
	 * use saveSettingsFile
	 * @param profileContainer
	 * @param showNotification
	 */@Deprecated 
	public void saveData(final ProfileContainer profileContainer, boolean showNotification) { // TODO die Notification muss hier raus
//		
//		// TODO bisher gab es nur eine settings file, jetzt mehrere. save funktion daran anpassen
//		
//		
//		/**
//		 * rootItem holen, JSIniFile mit Namen des rootItem erstellen
//		 * für jedes profileItem im Tree:
//		 * Section mit Namen des profileItem erstellen
//		 * 
//		 * wenn das Profile nicht angeklickt wurde, wurden die options bis hierher auch noch nicht gesetzt -> aus der alten Datei lesen und unverändert schreiben
//		 */
//		String filename = "";
//		JSIniFile newJadeSettingsFile = null;
//		
//		Item rootItem = profileContainer.getRootItem();
//		Object rootId = profileContainer.getRootId();
//		Collection<?> profileItemIds = profileContainer.getChildren(rootId);
//		
//		filename = (String) rootItem.getItemProperty(ProfileContainer.PROPERTY.NAME).getValue();
//		newJadeSettingsFile = new JSIniFile(filename);
//		
//		
//		for (Object profileItemId : profileItemIds) {
//			Item profileItem = profileContainer.getItem(profileItemId);
//			String profileName = (String) profileItem.getItemProperty(ProfileContainer.PROPERTY.NAME).getValue();
//			SOSProfileSection profile = newJadeSettingsFile.addSection(profileName);
//			
//			// -------------------------------- // TODO nochmal prüfen. evtl in der ersten schleife gleich die options aus optionsFromSettingsFile entfernen?
//			HashMap<String, SOSOptionElement> options = (HashMap<String, SOSOptionElement>) profileItem.getItemProperty(ProfileContainer.PROPERTY.OPTIONS).getValue();
//			Iterator<String> optionsIterator = options.keySet().iterator();
//			
//			HashMap<String, String> optionsFromSettingsFile = (HashMap<String, String>) profileItem.getItemProperty(ProfileContainer.PROPERTY.OPTIONSFROMSETTINGSFILE).getValue();
//			
//			while (optionsIterator.hasNext()) {
//
//				String optionName = optionsIterator.next();
//				SOSOptionElement option = options.get(optionName);
//				String optionValue = option.Value();
//				if (option.isDirty() && optionValue != null
//						&& !optionValue.trim().isEmpty()) {
//					profile.addEntry(optionName, optionValue);
//				}
//				if (optionsFromSettingsFile.containsKey(optionName)) {
//					optionsFromSettingsFile.remove(optionName);
//				}
//			}
//			
//			Iterator<String> optionsFromSettingsFileIterator = optionsFromSettingsFile.keySet().iterator();
//			
//			while (optionsFromSettingsFileIterator.hasNext()) {
//				String optionName = optionsFromSettingsFileIterator.next();
//				String optionValue = optionsFromSettingsFile.get(optionName);
//				System.out.println(profileName + " --- " + optionName + " = " + optionValue);
//				
//				profile.addEntry(optionName, optionValue);
//			}
//			// --------------------------------
//		}
//			
//			
//			
//			
//			
//			
//			// profiles
////			if (profileContainer.getItem(id).getItemProperty(ProfileContainer.PROPERTY.NODETYPE).getValue().equals(ProfileContainer.NODETYPE.PROFILE)) {
//			
////			Item profileItem = profileContainer.getItem(profileItemId);
////			SOSProfileSection profile = newJadeConfigurationFile.addSection((String) profileItem.getItemProperty(ProfileContainer.PROPERTY.NAME).getValue());
////			
////			ParameterContainer parameterContainer = (ParameterContainer) profileItem.getItemProperty(ProfileContainer.PROPERTY.ENTRIES).getValue();
////			if (parameterContainer == null) {
////				parameterContainer = loadParameters(profileItem);
////			}
////			if (parameterContainer != null) {
////				for (Object parameterItemId : parameterContainer.getItemIds()) {
////					Item parameterItem = parameterContainer.getItem(parameterItemId);
////					SOSOptionElement option = (SOSOptionElement) parameterItem.getItemProperty(ParameterContainer.PROPERTY.OPTIONELEMENT).getValue();
////					String optionName = (String) parameterItem.getItemProperty(ParameterContainer.PROPERTY.NAME).getValue();
////					String optionValue = (String) parameterItem.getItemProperty(ParameterContainer.PROPERTY.VALUE).getValue();
////					if (option instanceof SOSOptionBoolean) {
////						SOSOptionBoolean optionBool = (SOSOptionBoolean) option;
////						if (optionValue == null || optionValue.isEmpty()) {
////							optionValue = "false";
////						} else {
////							optionValue = optionBool.Value();
////							profile.addEntry(optionName, optionValue);
////						}
////					} else if (option.isDirty() && optionValue != null) {
////						profile.addEntry(optionName, optionValue);
////					}
////				}
////			}
//			
//			
//				// ---------------------------------				
////				JADEOptions jadeOptions = (JADEOptions) profileItem.getItemProperty(ProfileContainer.PROPERTY.JADEOPTIONS).getValue();
////				
////				
////				String dirtyString = jadeOptions.DirtyString();
////				System.out.println(dirtyString);
////				HashMap<String, SOSOptionElement> options = (HashMap<String, SOSOptionElement>) profile.getItemProperty(ProfileTreeContainer.PROPERTY.OPTIONS).getValue();
//				
////				String[] dirtyStringArray = dirtyString.split("\n");
////				HashMap<String, String> allDirtyOptions = new HashMap<String, String>();
////				for (String s : dirtyStringArray) {
////					if (!s.isEmpty()) {
////						String [] dirtyOption = s.split("=");
////						allDirtyOptions.put(dirtyOption[0].trim(), dirtyOption[1].trim());
////					}
////				}
//				
//				
//				
//				
//				
//				
//				/*
//				 * TODO sind die JadeOptions, die als Property im Profile (im Tree) vorhanden sind, mit den jeweils aktuellen Parametern (die aus den Formularen) gefüllt?
//				   --------> Ja! Außerdem enthält jadeOptions.DirtyString() tatsächlich nur die "dirty values". Diese können geschrieben werden. Booleans müssen trotzdem 
//				 gesondert beachtet werden, da bei der CheckBox nicht zwischen null und false unterschieden werden kann (eine nicht angeklickte CheckBox kann beides bedeuten)
//				 */
//				
//				// ---------------------------------
//				/*
//				ParameterContainer parameterContainer = (ParameterContainer) profile.getItemProperty(ProfileTreeContainer.PROPERTY.ENTRIES).getValue();
//				if (parameterContainer == null) {
//					parameterContainer = loadParameters(profile);
//				}
//				if (parameterContainer != null) {
//					for (Object entryId : parameterContainer.getItemIds()) {
//						section.addEntry((String) parameterContainer.getItem(entryId).getItemProperty(ParameterContainer.PROPERTY.NAME).getValue(), (String) parameterContainer.getItem(entryId).getItemProperty(ParameterContainer.PROPERTY.VALUE).getValue());
//					}
//				}
//				*/
////			}
////		}
//		
//		newJadeSettingsFile.saveAs(Globals.getBasePath() + "/WEB-INF/uploads/" + filename);
//		
//		if (showNotification) {
//			Notification.show("File has been saved!", Notification.Type.TRAY_NOTIFICATION);
//		}
	}

	/**
	 * 
	 * @param name The new profile's name
	 * @return True if the profile was added, false otherwise
	 */
	public boolean addProfile(String name) {

		return addProfile(name, false);
	}
	
	/**
	 * 
	 * @param name The new profile's name
	 * @param isFragment Specify whether the new profile is a fragment
	 * @return True if the profile was added, false otherwise
	 */
	public boolean addProfile(String name, boolean isFragment) { // TODO
		// TODO check validity, add profile
		boolean returnValue = false;
		if (name != null) {
			if (!name.trim().isEmpty()) {
				ProfileTree profileTree = JadevaadincockpitUI.getCurrent().getJadeMainUi().getProfileTree();
				ProfileContainer container = profileTree.getProfileContainer();
				boolean isNew = true;
				for (Object id : container.getItemIds()) {
					if (((String) profileTree.getItem(id).getItemProperty(ProfileContainer.PROPERTY.NAME).getValue()).equalsIgnoreCase(name)) {
						isNew = false;
					}
				}
				if (isNew) {
					Object parentId;
					if (profileTree.hasChildren(profileTree.getValue())) { // is file
						parentId = profileTree.getValue();
					} else { // is profile
						parentId = profileTree.getParent(profileTree.getValue());
					}
					
					UUID profileId = UUID.randomUUID();
					Item item = container.addItem(profileId);
					container.setParent(profileId, parentId);
					container.setChildrenAllowed(profileId, false);
					
					item.getItemProperty(ProfileContainer.PROPERTY.ID).setValue(profileId);
					item.getItemProperty(ProfileContainer.PROPERTY.NAME).setValue(name);
					item.getItemProperty(ProfileContainer.PROPERTY.OLDNAME).setValue(name);
					item.getItemProperty(ProfileContainer.PROPERTY.NODETYPE).setValue(ProfileContainer.NODETYPE.PROFILE);
					JSIniFile jadeConfigurationFile = (JSIniFile) profileTree.getItem(parentId).getItemProperty(ProfileContainer.PROPERTY.JADESETTINGSFILE).getValue();
					jadeConfigurationFile.SectionName(name);
					item.getItemProperty(ProfileContainer.PROPERTY.JADESETTINGSFILE).setValue(jadeConfigurationFile);
					item.getItemProperty(ProfileContainer.PROPERTY.OPTIONS).setValue(new HashMap<String, SOSOptionElement>());
					
					JADEOptions options = new JADEOptions();
					options.gflgSubsituteVariables = false;
					options.settings.Value(jadeConfigurationFile.getAbsolutePath());
					options.profile.Value(name);
					item.getItemProperty(ProfileContainer.PROPERTY.JADEOPTIONS).setValue(options);
					
					// Eine Option muss gesetzt werden, damit options.readSettingsFile() nicht fehlschlägt (funktioniert nicht bei leeren Profiles)
					options.isFragment.value(isFragment);
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(options.isFragment.getShortKey(), options.isFragment.Value());
					item.getItemProperty(ProfileContainer.PROPERTY.OPTIONSFROMSETTINGSFILE).setValue(map);
					
					// the new profile has to be written to the settings file so that options.readSettingsFile() can be called
					saveSettingsFile(parentId);
					
					// call options.readSettingsFile() to fill the options-object
					options.ReadSettingsFile();
					
					returnValue = true;
				}
			}
		}
		return returnValue;
	}
	
}
