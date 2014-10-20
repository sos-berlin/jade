package com.sos.jadevaadincockpit.view;

import java.util.Iterator;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Options.IValueChangedListener;
import com.sos.JSHelper.Options.SOSOptionElement;
import com.sos.JSHelper.Options.SOSValidationError;
import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.adapters.JadeVaadinAdapter;
import com.sos.jadevaadincockpit.data.ProfileContainer;
import com.sos.jadevaadincockpit.view.components.ComponentGroup;
import com.sos.jadevaadincockpit.view.components.ConfirmationDialog;
import com.sos.jadevaadincockpit.view.components.TextInputDialog;
import com.vaadin.data.Item;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class SettingsFileOverview extends FormLayout {
	private static final long serialVersionUID = -1210934414611151459L;

	public SettingsFileOverview() {
		
	}
	
	public void setSettingsFileItem(Item settingsFileItem) {
		
		removeAllComponents();
		
		ProfileTree profileTree = JadevaadincockpitUI.getCurrent().getMainView().getProfileTree();
		
		Object settingsFileItemId = profileTree.getValue();
		
		Iterator<?> iterator = profileTree.getChildren(settingsFileItemId).iterator();
		while (iterator.hasNext()) {
			addComponent(new ProfileOverview(profileTree.getItem(iterator.next())));
		}
		VerticalLayout spacer = new VerticalLayout();
		addComponent(spacer);
		setExpandRatio(spacer, 1);
	}
	
	public void removeGroup(Component comp) {
		removeComponent(comp);
	}
	
	
	class ProfileOverview extends CustomComponent {
		
		public ProfileOverview(final Item profileItem) {
			
			final Component comp = this;
			
			final ProfileTree profileTree = JadevaadincockpitUI.getCurrent().getMainView().getProfileTree();
			final Object profileItemId = profileItem.getItemProperty(ProfileContainer.PROPERTY.ID).getValue();
			JADEOptions options = (JADEOptions) profileItem.getItemProperty(ProfileContainer.PROPERTY.JADEOPTIONS).getValue();
			
			HorizontalLayout buttonLayout = new HorizontalLayout();
			Button executeButton = null;
			Button renameButton = null;
			Label spacerLabel = new Label();
			
			Button editButton = new Button("edit");
			editButton.setIcon(new ThemeResource("icons/edit_16.png"));
			Button deleteButton = new Button("delete");
			deleteButton.setIcon(new ThemeResource("icons/delete_16.png"));
			
			if (!options.isFragment.value() && !options.profile.Value().equalsIgnoreCase("globals")) {
				executeButton = new Button("execute");
				executeButton.setIcon(new ThemeResource("icons/exec_16.png"));
				executeButton.addClickListener(new ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						// TODO profile has to be saved before execution as checkMandatory() will read from the file (not from the options-Object)
						JadevaadincockpitUI.getCurrent().getApplicationAttributes().getJadeSettingsFile().saveSettingsFile(profileItemId);
						
						JadeVaadinAdapter jadeAdapter = new JadeVaadinAdapter();
						jadeAdapter.execute(profileItem);
						
					}
				});
			}
			
			if (!options.profile.Value().equalsIgnoreCase("globals")) {
				renameButton = new Button("rename");
				renameButton.setIcon(new ThemeResource("icons/rename_16.png"));
				renameButton.addClickListener(new ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						TextInputDialog dialog = new TextInputDialog("Rename Profile", "Please enter a new name.", new TextInputDialog.Callback() {
							
							@Override
							public void onDialogResult(boolean isOk, String input) {
								
								if (isOk) {
									JadevaadincockpitUI.getCurrent().getApplicationAttributes().getJadeSettingsFile().renameProfile(profileItemId, input);
								}
							}
						});
						dialog.setEmptyInputAllowed(false);
						dialog.setEmptyInputMessage("Please enter a name.");
						
						UI.getCurrent().addWindow(dialog);
						
					}
				});
			}
			
			if (executeButton != null) {
				buttonLayout.addComponent(executeButton);				
			}
			buttonLayout.addComponent(editButton);
			if (renameButton != null) {
				buttonLayout.addComponent(renameButton);
			}
			buttonLayout.addComponents(deleteButton, spacerLabel);
			
			buttonLayout.setExpandRatio(spacerLabel, 1);
			buttonLayout.setSpacing(true);
			
			final ComponentGroup group = new ComponentGroup(options.profile.Value());
			
			options.profile.addValueChangedListener(new IValueChangedListener() {
				
				@Override
				public void ValueHasChanged(SOSOptionElement pobjOptionElement) {
					group.setCaption(pobjOptionElement.Value());
					
				}
				
				@Override
				public void ValidationError(SOSValidationError pobjVE) {
					// TODO Auto-generated method stub
					
				}
			});
			
			Label descLabel = new Label(options.title.Value());
			
			// TODO panel soll beim Löschen des zugehörigen Items entfernt werden
//			profileTree.addItemSetChangeListener(new ItemSetChangeListener() {
//				
//				@Override
//				public void containerItemSetChange(ItemSetChangeEvent event) {
//					if (!event.getContainer().containsId(profileItemId)) {
//						removeGroup(comp);
//					}
//					
//				}
//			});
			
			group.addComponents(buttonLayout, descLabel);
			
			setCompositionRoot(group);
			
			editButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					profileTree.setValue(profileItemId);
				}
			});
			
			deleteButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					
					ConfirmationDialog dialog = new ConfirmationDialog("Delete Profile", "Do you really want to delete the selected profile from the settings file?", new ConfirmationDialog.Callback() {
					
						@Override
						public void onDialogResult(boolean isOk) {
							if (isOk) {
								// delete profile
								JadevaadincockpitUI.getCurrent().getApplicationAttributes().getJadeSettingsFile().deleteProfile(profileItemId);
							} else {
								// Do nothing
							}
						}
					});
					
					UI.getCurrent().addWindow(dialog);
				}
			});
		}
	}
}
