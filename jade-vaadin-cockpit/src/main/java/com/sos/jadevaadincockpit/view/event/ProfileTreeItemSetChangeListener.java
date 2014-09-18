package com.sos.jadevaadincockpit.view.event;

import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.view.ProfileTree;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;

/**
 * 
 * @author JS
 *
 */
public class ProfileTreeItemSetChangeListener implements ItemSetChangeListener {
	private static final long serialVersionUID = -1483387954662383951L;

	@Override
	public void containerItemSetChange(ItemSetChangeEvent event) {
		ProfileTree profileTree = JadevaadincockpitUI.getCurrent().getMainView().getProfileTree();
		
		JadevaadincockpitUI.getCurrent().getMainView().getJadeMenuBar().setNewProfileItemEnabled(!profileTree.getItemIds().isEmpty());
	}

}
