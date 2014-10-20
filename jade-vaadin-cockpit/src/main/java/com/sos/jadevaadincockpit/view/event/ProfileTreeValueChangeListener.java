package com.sos.jadevaadincockpit.view.event;

import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.data.ProfileContainer;
import com.sos.jadevaadincockpit.view.ProfileTabSheet;
import com.sos.jadevaadincockpit.view.ProfileTree;
import com.sos.jadevaadincockpit.view.SettingsFileOverview;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

/**
 * 
 * @author JS
 *
 */
public class ProfileTreeValueChangeListener implements ValueChangeListener {
	private static final long serialVersionUID = 427297849344867710L;

	@Override
	public void valueChange(ValueChangeEvent event) {
		
		ProfileTree profileTree = JadevaadincockpitUI.getCurrent().getMainView().getProfileTree();
		Object selectedItemId = profileTree.getValue();
		Item selectedItem = profileTree.getItem(selectedItemId);
		
		/**
		 * load data to create the according tab in the
		 * tabPanel (only if the selected item is a profile, not a settings file)
		 */
		if (selectedItem != null) {
			
			// check if the tree's selected item is a profile
			if (selectedItem
					.getItemProperty(ProfileContainer.PROPERTY.NODETYPE)
					.getValue().equals(ProfileContainer.NODETYPE.PROFILE)) {
				
				// create forms for the selected profile
				ProfileTabSheet tabSheet = JadevaadincockpitUI.getCurrent().getMainView().getProfileTabSheet();
				tabSheet.setSelectedTab(tabSheet.getTab(selectedItem));
				JadevaadincockpitUI.getCurrent().getMainView().getHSplitPanel1().setSecondComponent(tabSheet);
				tabSheet.setSizeFull();

				// // load parameters from config file to container
				// ParameterContainer entryTableContainer = Constants
				// .getJadeDataProvider().loadParameters(selectedItem);

				// get the selectedItem's entries (entryTableContainer)
				// EntryTableContainer entryTableContainer =
				// (EntryTableContainer)
				// selectedItem.getItemProperty(ProfileTreeContainer.PROPERTY.ENTRIES).getValue();

				// set the container as containerDataSource for the entryTable.
				// The visible column ids have to be passed
//				Object[] columnsArray = { ParameterContainer.PROPERTY.NAME,
//						ParameterContainer.PROPERTY.VALUE };
//				List<?> columnsList = Arrays.asList(columnsArray);
				// JadevaadincockpitUI
				// .getCurrent()
				// .getJadeMainUi()
				// .getEntryTableControl()
				// .getEntryTable()
				// .setContainerDataSource(entryTableContainer,
				// columnsList);
			} else if (selectedItem
					.getItemProperty(ProfileContainer.PROPERTY.NODETYPE)
					.getValue().equals(ProfileContainer.NODETYPE.FILE)) {
				SettingsFileOverview overview = JadevaadincockpitUI.getCurrent().getMainView().getSettingsFileOverview();
				overview.setSettingsFileItem(selectedItem);
				JadevaadincockpitUI.getCurrent().getMainView().showSettingsFileOverview();
			}
		}
		
	}

}
