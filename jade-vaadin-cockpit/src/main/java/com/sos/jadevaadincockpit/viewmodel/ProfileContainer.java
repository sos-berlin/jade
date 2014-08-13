package com.sos.jadevaadincockpit.viewmodel;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Options.SOSOptionElement;
import com.sos.JSHelper.io.Files.JSIniFile;
import com.sos.jadevaadincockpit.globals.ApplicationAttributes;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;

/**
 * 
 * @author JS
 * 
 */
public class ProfileContainer extends HierarchicalContainer {
	private static final long serialVersionUID = 4806921940302464671L;

	public static enum PROPERTY {
		ID(Object.class, null), 
		NAME(String.class, ""), 
		OLDNAME(String.class, ""), 
		NODETYPE(ProfileContainer.NODETYPE.class, ProfileContainer.NODETYPE.PROFILE), 
		ENTRIES(Container.class, null), 
		JADEOPTIONS(JADEOptions.class, null), 
		OPTIONS(HashMap.class, null), 
		OPTIONSFROMSETTINGSFILE(HashMap.class, null), 
		JADESETTINGSFILE(JSIniFile.class, null), 
		ICON(Resource.class, new FileResource(new File(ApplicationAttributes.getBasePath() + "/WEB-INF/icons/Profile.gif")));

		private Class<?> type;
		private Object defaultValue;

		private PROPERTY(Class<?> type, Object defaultValue) {
			this.type = type;
			this.defaultValue = defaultValue;
		}

		public Class<?> getType() {
			return type;
		}

		public Object getDefaultValue() {
			return defaultValue;
		}
	};

	public static enum NODETYPE {
		FILE, PROFILE;
	};

	public ProfileContainer() {
		for (PROPERTY prop : PROPERTY.values()) {
			addContainerProperty(prop, prop.getType(), prop.getDefaultValue());
		}
	}

	/**
	 * Sets all container properties for the given item.
	 * 
	 * @param itemId
	 *            The id of the item to set the properties for
	 * @param id
	 *            ProfileTreeContainer.PROPERTY.ID
	 * @param name
	 *            ProfileTreeContainer.PROPERTY.NAME
	 * @param oldName
	 *            ProfileTreeContainer.PROPERTY.OLDNAME
	 * @param nodeType
	 *            ProfileTreeContainer.PROPERTY.NODETYPE
	 * @param entries
	 *            ProfileTreeContainer.PROPERTY.ENTRIES
	 * @param jadeOptions
	 *            ProfileTreeContainer.PROPERTY.JADEOPTIONS
	 * @param options
	 *            ProfileTreeContainer.PROPERTY.OPTIONS
	 * @param jadeSettingsFile
	 *            ProfileTreeContainer.PROPERTY.JADESETTINGSFILE
	 */
	@SuppressWarnings("unchecked")
	private void setAllProperties(Object itemId, Object id, String name, // TODO an ge�nderte Properties anpassen
			String oldName, NODETYPE nodeType, JADEOptions jadeOptions,
			HashMap<String, SOSOptionElement> options,
			JSIniFile jadeSettingsFile) {
		Item item = getItem(itemId);
		item.getItemProperty(PROPERTY.ID).setValue(id);
		item.getItemProperty(PROPERTY.NAME).setValue(name);
		item.getItemProperty(PROPERTY.OLDNAME).setValue(oldName);
		item.getItemProperty(PROPERTY.NODETYPE).setValue(nodeType);
		item.getItemProperty(PROPERTY.JADEOPTIONS).setValue(jadeOptions);
		item.getItemProperty(PROPERTY.OPTIONS).setValue(options);
		item.getItemProperty(PROPERTY.JADESETTINGSFILE).setValue(
				jadeSettingsFile);
	}

	/**
	 * Creates a new root item for the given jadeSettingsFile.
	 * 
	 * @param jadeSettingsFile
	 * @return the created root item id.
	 */
	@SuppressWarnings("unchecked")
	public Object addRootItem(JSIniFile jadeSettingsFile) {
		Object rootId = UUID.randomUUID();
		
		Item rootItem = addItem(rootId);

		setAllProperties(rootId, rootId, jadeSettingsFile.getName(),
				jadeSettingsFile.getName(), NODETYPE.FILE,
				(JADEOptions) PROPERTY.JADEOPTIONS.getDefaultValue(),
				new HashMap<String, SOSOptionElement>(), jadeSettingsFile);
		rootItem.getItemProperty(PROPERTY.ICON).setValue(null);
			
		setParent(rootId, null);
		setChildrenAllowed(rootId, true);

		return rootId;
	}

	/**
	 * Creates a new profileItem.
	 * @param profileName the name of the new profile
	 * @param parentId the id of the parent item (a settings file)
	 * @return The created profileItem id or null, if the container does not
	 *         contain a rootItem (-> no settings file has been loaded yet)
	 */
	public Object addProfileItem(String profileName, Object parentId) {
		
		Object profileId = null;
		/**
		 * If the container does not contain any items at this point, there is
		 * no settings file loaded to which the profile could be added.
		 * 
		 */
		if (getItemIds().size() > 0) {
			
			profileId = UUID.randomUUID();
			addItem(profileId);
			setParent(profileId, parentId);
			setChildrenAllowed(profileId, false);
			
			 // TODO globals profil muss aufgelöst werden
			
			JSIniFile jadeSettingsFile = (JSIniFile) getItem(parentId).getItemProperty(PROPERTY.JADESETTINGSFILE).getValue();

			JADEOptions jadeOptions = new JADEOptions();
			jadeOptions.gflgSubsituteVariables = false;
			jadeOptions.settings.Value(jadeSettingsFile.getAbsolutePath());
			jadeOptions.profile.Value(profileName);
//			jadeOptions.ReadSettingsFile(); // TODO this takes quite some time

			setAllProperties(profileId, profileId, profileName, profileName,
					NODETYPE.PROFILE, jadeOptions,
					new HashMap<String, SOSOptionElement>(), jadeSettingsFile);
		}
		return profileId;
	}
	
	/**
	 * Sorts the items by NAME-Property.
	 */
	public void sortByName() {
		sort(new Object[] {PROPERTY.NAME}, new boolean[] {true});
	}

	/**
	 * Set the ICON-Property for the specified <code>rootId</code> and its children.
	 * @param rootId
	 */
	@SuppressWarnings("unchecked")
	public void initializeIconProperties(Object rootId) {
		
		Item rootItem = getItem(rootId);
		
		if (rootItem != null) {
			
			rootItem.getItemProperty(PROPERTY.ICON).setValue(new FileResource(new File(ApplicationAttributes.getBasePath() + "/WEB-INF/icons/Document_small.gif")));
			
			Iterator<?> childrenIterator = getChildren(rootId).iterator();
			
			while (childrenIterator.hasNext()) {
				
				Object profileItemId = childrenIterator.next();
				
				HashMap<String, String> optionsFromSettingsFile = (HashMap<String, String>) getContainerProperty(profileItemId, PROPERTY.OPTIONSFROMSETTINGSFILE).getValue();
				
				if (optionsFromSettingsFile.containsKey("is_fragment")) {
					
					if (optionsFromSettingsFile.get("is_fragment").equalsIgnoreCase("true")) {
						
						getContainerProperty(profileItemId, PROPERTY.ICON).setValue(new FileResource(new File(ApplicationAttributes.getBasePath() + "/WEB-INF/icons/Fragment.gif")));
						
					}
					
				} else if (optionsFromSettingsFile.containsKey("isFragment")) {
					
					if (optionsFromSettingsFile.get("isFragment").equalsIgnoreCase("true")) {
						
						getContainerProperty(profileItemId, PROPERTY.ICON).setValue(new FileResource(new File(ApplicationAttributes.getBasePath() + "/WEB-INF/icons/Fragment.gif")));
						
					}
					
				} else {
					
					getContainerProperty(profileItemId, PROPERTY.ICON).setValue(PROPERTY.ICON.getDefaultValue());
					
				}
					
			}
		}
		
		
	}

}
