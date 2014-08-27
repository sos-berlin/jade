package com.sos.jadevaadincockpit.util;

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
import com.sos.jadevaadincockpit.data.ProfileContainer;
import com.sos.jadevaadincockpit.globals.ApplicationAttributes;
import com.sos.jadevaadincockpit.view.ProfileTabSheet;
import com.sos.jadevaadincockpit.view.ProfileTree;
import com.vaadin.data.Item;

/**
 * 
 * @author JS
 *
 */
@Deprecated
public class JadeDataProvider implements Serializable {
	private static final long serialVersionUID = -8882251496758382683L;

	private static final Logger logger = Logger.getLogger(JadeDataProvider.class.getName());
	
	public JadeDataProvider() {
		logger.setLevel(Level.ALL);
	}
	
	
	public void loadSettingsFile(final String filePath) {
		
		ProfileTree profileTree = JadevaadincockpitUI.getCurrent().getMainView().getProfileTree();
		
		ProfileContainer profileContainer = profileTree.getProfileContainer();
		
		JSIniFile jadeSettingsFile = new JSIniFile(filePath);
		
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
	 * Removes the settings file identified by the itemId from the tree view and closes all corresponding tabs.
	 * @param itemId the settings file id to close
	 */
	public void closeSettingsFile(Object itemId) {
		
		// clear the container
		ProfileContainer container = (ProfileContainer) JadevaadincockpitUI.getCurrent().getMainView().getProfileTree().getContainerDataSource();
		container.removeItemRecursively(itemId);
		
		// remove all tabs
		ProfileTabSheet profileTabSheet = JadevaadincockpitUI.getCurrent().getMainView().getProfileTabSheet();
		profileTabSheet.removeAllComponents(); // TODO do not close ALL tabs
		
		// disable save-functions
		JadevaadincockpitUI.getCurrent().getMainView().getJadeMenuBar().setSaveItemsEnabled(false);
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
////			 * f�r jedes Formularfeld aus ((HashMap<String, AbstractComponent>) treeItem.getItemProperty(ProfileTreeContainer.PROPERTY.COMPONENTS).getValue()) muss ein Eintrag im Container angelegt werden
////			 * alle Eintr�ge, die in der ConfigDatei nicht ausgef�llt sind, sollten in der Tabelle ausgeblendet oder anderweitig markiert werden
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
////							 * wurde kein Formularfeld f�r den entsprechenden
////							 * Parameter erstellt, oder der Parametername aus
////							 * der ConfigDatei stimmt nicht mit dem ShortKey des
////							 * entsprechenden SOSOptionElements �berein.
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
		
		ProfileContainer profileContainer = JadevaadincockpitUI.getCurrent().getMainView().getProfileTree().getProfileContainer();
		
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
	 * @param name The new profile's name
	 * @return True if the profile was added, false otherwise
	 */
	public boolean addProfile(String name) {
		// TODO check validity, add profile
		boolean returnValue = false;
		if (name != null) {
			if (!name.trim().isEmpty()) {
				ProfileTree profileTree = JadevaadincockpitUI.getCurrent().getMainView().getProfileTree();
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
}
