package com.sos.jadevaadincockpit.view;

import com.sos.jadevaadincockpit.data.ProfileContainer;
import com.sos.jadevaadincockpit.view.event.ProfileTreeActionHandler;
import com.sos.jadevaadincockpit.view.event.ProfileTreeValueChangeListener;
import com.vaadin.data.Item;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Tree;

/**
 * 
 * @author JS
 *
 */
public class ProfilesView extends CustomComponent {
	private static final long serialVersionUID = -7442189969020018078L;
	
	private Tree tree;
	
	public ProfilesView() {
		
		tree = new Tree();
		setCompositionRoot(tree);
		
		tree.setItemCaptionPropertyId(ProfileContainer.PROPERTY.NAME);
		tree.setContainerDataSource(new ProfileContainer());
		tree.setItemIconPropertyId(ProfileContainer.PROPERTY.ICON);
		tree.setImmediate(true);
		setImmediate(true);
		tree.setNullSelectionAllowed(false);
		tree.addValueChangeListener(new ProfileTreeValueChangeListener());
		tree.addActionHandler(new ProfileTreeActionHandler());
		
		tree.setDropHandler(new DropHandler() {
			private static final long serialVersionUID = -4551508733241662261L;

			@Override
			public AcceptCriterion getAcceptCriterion() {
				return AcceptAll.get();
			}
			
			@Override
			public void drop(DragAndDropEvent event) {
				// Wrapper for the object that is dragged
				Transferable transferable = event.getTransferable();
				
				Notification.show("Transferable: " + transferable.toString());
				
//				// Make sure the drag source is the same tree
//		        if (t.getSourceComponent() != tree)
//		            return;
				
//				TreeTargetDetails target = (TreeTargetDetails) event.getTargetDetails();

//		        // Get ids of the dragged item and the target item
//		        Object sourceItemId = t.getData("itemId");
//		        Object targetItemId = target.getItemIdOver();

//		        // On which side of the target the item was dropped 
//		        VerticalDropLocation location = target.getDropLocation();
		        
//		        HierarchicalContainer container = (HierarchicalContainer)
//		        tree.getContainerDataSource();
//
//		        // Drop right on an item -> make it a child
//		        if (location == VerticalDropLocation.MIDDLE)
//		            tree.setParent(sourceItemId, targetItemId);
//
//		        // Drop at the top of a subtree -> make it previous
//		        else if (location == VerticalDropLocation.TOP) {
//		            Object parentId = container.getParent(targetItemId);
//		            container.setParent(sourceItemId, parentId);
//		            container.moveAfterSibling(sourceItemId, targetItemId);
//		            container.moveAfterSibling(targetItemId, sourceItemId);
//		        }
//		        
//		        // Drop below another item -> make it next 
//		        else if (location == VerticalDropLocation.BOTTOM) {
//		            Object parentId = container.getParent(targetItemId);
//		            container.setParent(sourceItemId, parentId);
//		            container.moveAfterSibling(sourceItemId, targetItemId);
//		        }
				
			}
		});
	}
	
	/**
	 * Get the ID of the currently selected settings file item. If a profile is selected, get the ID of its parent item (which in turn is a settings file item).
	 * @return The ID of the currently selected settings file item.
	 */
	public Object getSelectedSettingsFileItemId() {
		
		Object selectedSettingsFileItemId;
		
		Object currentSelectionId = getSelectedItemId();
		
		if(tree.areChildrenAllowed(currentSelectionId)) {
			selectedSettingsFileItemId = currentSelectionId;
		} else {
			selectedSettingsFileItemId = tree.getParent(currentSelectionId);
		}

		return selectedSettingsFileItemId;
	}
	
	/**
	 * 
	 * @return
	 */
	public ProfileContainer getProfileContainer() {
		return (ProfileContainer) tree.getContainerDataSource();
	}
	
	/**
	 * TODO
	 */
	public void addProfile() {
		
	}
	
	/**
	 * Gets the selected item id or in multiselect mode a set of selected ids.
	 * @return the selected item id.
	 */
	public Object getSelectedItemId() {
		return tree.getValue();
	}
	
	/**
	 * Checks if the item specified with <code>itemId</code> represents a settings file.
	 * @param itemId ID of the item to be tested.
	 * @return <code>true</code> if the specified item represents a settings file, <code>false</code> otherwise (is a profile).
	 */
	public boolean isSettingsFile(Object itemId) {
		return tree.hasChildren(itemId);
	}
	
	/**
	 * Checks if the item specified with <code>itemId</code> represents a profile.
	 * @param itemId ID of the item to be tested.
	 * @return <code>true</code> if the specified item represents a profile, <code>false</code> otherwise (is a settings file).
	 */
	public boolean isProfile(Object itemId) {
		return tree.hasChildren(itemId);
	}
	
	/**
	 * Gets the item from the container with given id. If the container does not contain the requested item, null is returned.
	 * @param itemId the item id.
	 * @return the item from the container.
	 */
	public Item getItem(Object itemId) {
		return tree.getItem(itemId);
	}
	
	/**
	 * For development purpose. Probably should not be available later.
	 * @return
	 * @deprecated
	 */
	public Tree getTree() {
		return tree;
	}
}
