package com.sos.jadevaadincockpit.view;

import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.view.event.ProfileTreeActionHandler;
import com.sos.jadevaadincockpit.view.event.ProfileTreeValueChangeListener;
import com.sos.jadevaadincockpit.viewmodel.ProfileContainer;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.ui.Tree;

/**
 * 
 * @author JS
 * 
 */
public class ProfileTree extends Tree {
	private static final long serialVersionUID = 8381787778049298221L;

	private ProfileContainer profileContainer;

	public ProfileTree() {
		init();
	}

	private void init() {
		
		getProfileContainer();
		setItemCaptionPropertyId(ProfileContainer.PROPERTY.NAME);
		setContainerDataSource(profileContainer);
		setItemIconPropertyId(ProfileContainer.PROPERTY.ICON);
		setImmediate(true);
		setNullSelectionAllowed(false);
		
		addValueChangeListener(new ProfileTreeValueChangeListener());
		
		// move to separate class
		addItemSetChangeListener(new ItemSetChangeListener() {
			private static final long serialVersionUID = 8981890881364063205L;

			@Override
			public void containerItemSetChange(ItemSetChangeEvent event) {
				if (event.getContainer().getItemIds().size() == 0) {
					JadevaadincockpitUI.getCurrent().getJadeMainUi().hideProfileTree(true);
				} else {
					JadevaadincockpitUI.getCurrent().getJadeMainUi().hideProfileTree(false);
				}
				
			}
		});
		
		addActionHandler(new ProfileTreeActionHandler());
	}
	
	public ProfileContainer getProfileContainer() {
		if (profileContainer == null) {
			profileContainer = new ProfileContainer();
		}
		return profileContainer;
	}
	
	public void setProfileContainer(ProfileContainer profileContainer) {
		this.profileContainer = profileContainer;
		setContainerDataSource(profileContainer);
	}
}