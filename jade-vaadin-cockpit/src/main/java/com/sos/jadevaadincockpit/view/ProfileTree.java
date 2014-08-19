package com.sos.jadevaadincockpit.view;

import java.util.Locale;

import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.globals.ApplicationAttributes;
import com.sos.jadevaadincockpit.globals.JadeSettingsFile;
import com.sos.jadevaadincockpit.i18n.I18NComponent;
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
public class ProfileTree extends Tree implements I18NComponent {
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

	@Override
	public void refreshLocale(Locale newLocale) {
		// TODO Auto-generated method stub
		
	}
	
//	public void setProfileContainer(ProfileContainer profileContainer) {
//		this.profileContainer = profileContainer;
//		setContainerDataSource(profileContainer);
//	}
}