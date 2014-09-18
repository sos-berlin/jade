package com.sos.jadevaadincockpit.view;

import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.data.ProfileContainer;
import com.sos.jadevaadincockpit.view.event.ProfileTreeActionHandler;
import com.sos.jadevaadincockpit.view.event.ProfileTreeItemSetChangeListener;
import com.sos.jadevaadincockpit.view.event.ProfileTreeValueChangeListener;
import com.vaadin.ui.Tree;

/**
 * 
 * @author JS
 * 
 */
public class ProfileTree extends Tree {
	private static final long serialVersionUID = 8381787778049298221L;

	public ProfileTree() {
		init();
	}

	private void init() {
		
		setItemCaptionPropertyId(ProfileContainer.PROPERTY.NAME);
		setContainerDataSource(JadevaadincockpitUI.getCurrent().getApplicationAttributes().getJadeSettingsFile().getProfileContainer());
		setItemIconPropertyId(ProfileContainer.PROPERTY.ICON);
		setImmediate(true);
		setNullSelectionAllowed(false);
		
		addValueChangeListener(new ProfileTreeValueChangeListener());
		addItemSetChangeListener(new ProfileTreeItemSetChangeListener());
		
		// move to separate class
		addItemSetChangeListener(new ItemSetChangeListener() {
			private static final long serialVersionUID = 8981890881364063205L;

			@Override
			public void containerItemSetChange(ItemSetChangeEvent event) {
				if (event.getContainer().getItemIds().size() == 0) {
					JadevaadincockpitUI.getCurrent().getMainView().hideProfileTree(true);
				} else {
					JadevaadincockpitUI.getCurrent().getMainView().hideProfileTree(false);
				}
				
			}
		});
		
		addActionHandler(new ProfileTreeActionHandler());
	}
	
	public ProfileContainer getProfileContainer() {
		return (ProfileContainer) getContainerDataSource();
	}
	
	/**
	 * Get the ID of the currently selected settings file item. If a profile is selected, get the ID of its parent item (which in turn is a settings file item).
	 * @return The ID of the currently selected settings file item.
	 */
	public Object getSelectedSettingsFileItemId() {
		
		Object selectedSettingsFileItemId;
		
		Object currentSelectionId = getValue();
		
		if(areChildrenAllowed(currentSelectionId)) {
			selectedSettingsFileItemId = currentSelectionId;
		} else {
			selectedSettingsFileItemId = getParent(currentSelectionId);
		}

		return selectedSettingsFileItemId;
	}

}