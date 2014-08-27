package com.sos.jadevaadincockpit.view.event;

import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.data.ProfileContainer;
import com.sos.jadevaadincockpit.view.FormsTabSheet;
import com.sos.jadevaadincockpit.view.ProfileTree;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.CloseHandler;
import com.vaadin.ui.TabSheet.Tab;

public class ProfileTabSheetCloseHandler implements CloseHandler {
	private static final long serialVersionUID = 6225966771787291565L;

	@Override
	public void onTabClose(TabSheet tabsheet, Component tabContent) {
		Tab tab = tabsheet.getTab(tabContent);
		tabsheet.removeTab(tab);
		if (tabsheet.getComponentCount() == 0) {
			
//			No tabs opened after closing tab. Set the containerDataSource null and set the tree's selection to the configFile item.
//			JadevaadincockpitUI.getCurrent().getJadeMainUi().getEntryTableControl().getEntryTable().setContainerDataSource(null);
			FormsTabSheet removedContent = (FormsTabSheet) tabContent;
			Object removedItemId = removedContent.getProfileItem().getItemProperty(ProfileContainer.PROPERTY.ID).getValue();
			
			ProfileTree profileTree = JadevaadincockpitUI.getCurrent().getMainView().getProfileTree();
			
			Object parentId = profileTree.getParent(removedItemId);
			
			profileTree.setValue(parentId);
		}
	}

}
