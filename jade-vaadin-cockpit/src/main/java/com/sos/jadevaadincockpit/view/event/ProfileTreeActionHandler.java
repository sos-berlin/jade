package com.sos.jadevaadincockpit.view.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.sos.JSHelper.Options.SOSOptionElement;
import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.adapters.JadeVaadinAdapter;
import com.sos.jadevaadincockpit.data.ProfileContainer;
import com.sos.jadevaadincockpit.globals.ApplicationAttributes;
import com.sos.jadevaadincockpit.i18n.JadeCockpitMsg;
import com.sos.jadevaadincockpit.view.NewProfileWindow;
import com.sos.jadevaadincockpit.view.ProfileTree;
import com.sos.jadevaadincockpit.view.components.ConfirmationDialog;
import com.sos.jadevaadincockpit.view.components.TextInputDialog;
import com.vaadin.data.Item;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

/**
 * 
 * @author JS
 *
 */
public class ProfileTreeActionHandler implements Handler {
	private static final long serialVersionUID = -1010544471098644643L;

	private static final Action SAVE_FILE = new Action(new JadeCockpitMsg("jade_l_saveFileAction").label());
	private static final Action CLOSE_FILE = new Action(new JadeCockpitMsg("jade_l_closeFileAction").label());
	private static final Action ADD_PROFILE = new Action(new JadeCockpitMsg("jade_l_addProfileAction").label());
	private static final Action RENAME_FILE = new Action(new JadeCockpitMsg("jade_l_renameFileAction").label());
	private static final Action UPDATE_INCLUDES = new Action(new JadeCockpitMsg("jade_l_updateIncludes").label());
	
    private static final Action DELETE_PROFILE = new Action(new JadeCockpitMsg("jade_l_deleteProfileAction").label());
    private static final Action RENAME_PROFILE = new Action(new JadeCockpitMsg("jade_l_renameProfileAction").label());
    private static final Action EXECUTE_PROFILE = new Action(new JadeCockpitMsg("jade_l_executeProfileAction").label());
    
    private static final Action DEBUG_PRINT_OPTIONS = new Action("DEBUG: print dirty options"); // dbg
    private static final Action DEBUG_PRINT_MISSING_OPTIONS = new Action("DEBUG: print missig options"); // dbg
    
    private static final Action[] settingsFileActions = new Action[] { SAVE_FILE, CLOSE_FILE, ADD_PROFILE, RENAME_FILE };
    private static final Action[] profileActions = new Action[] { EXECUTE_PROFILE, RENAME_PROFILE, DELETE_PROFILE };
    
    private static final Action[] debugActions = new Action[] { DEBUG_PRINT_OPTIONS, DEBUG_PRINT_MISSING_OPTIONS }; // dbg

	@Override
	public Action[] getActions(Object target, Object sender) {
		
		List<Action> actionsList = new ArrayList<Action>();
		
		if (target != null) {
			
			ProfileTree profileTree = JadevaadincockpitUI.getCurrent().getMainView().getProfileTree();
			
			ProfileContainer profileContainer = profileTree.getProfileContainer();
			
			if (profileContainer.isRoot(target)) { // item which received the right click is a settings file
				
				for (Action a : settingsFileActions) {
					
					actionsList.add(a);
				}
			} else { // item which received the right click is a profile
				
				for (Action a : profileActions) {
					
					actionsList.add(a);
				}
			}
		}
		
		/* dbg
		for (Action a : debugActions) {
			
			actionsList.add(a);
		}
		*/
		
		Action[] actions = actionsList.toArray(new Action[actionsList.size()]);
		
		return actions;
	}

	@Override
	public void handleAction(Action action, Object sender, final Object target) {
		
		ProfileTree profileTree = JadevaadincockpitUI.getCurrent().getMainView().getProfileTree();
		final ProfileContainer profileContainer = profileTree.getProfileContainer();
		
		if (profileContainer.isRoot(target)) { // item which received the right click is a settings file; check settingsFileActions
			
			if (action == SAVE_FILE) {
				
				JadevaadincockpitUI.getCurrent().getApplicationAttributes().getJadeSettingsFile().saveSettingsFile(target, new FileSavedCallback() {
					private static final long serialVersionUID = -1589885885474868477L;

					@Override
					public void onSuccess() {
						Notification.show("File saved", Notification.Type.TRAY_NOTIFICATION);
					}
					
					@Override
					public void onFailure() {
						// TODO Auto-generated method stub
						
					}
				});
				
			} else if (action == CLOSE_FILE) {
				
				JadevaadincockpitUI.getCurrent().getApplicationAttributes().getJadeSettingsFile().closeSettingsFile(target);
				
			} else if (action == ADD_PROFILE) {
				
				TextInputDialog dialog = new TextInputDialog("Add Profile", "Please enter a name.", new TextInputDialog.Callback() {
					
					@Override
					public void onDialogResult(boolean isOk, String input) {
						
						if (isOk) {
							// get the id of the currently selected settings file (which will be the parent of the newly added profile)
							Object parentId = JadevaadincockpitUI.getCurrent().getMainView().getProfileTree().getSelectedSettingsFileItemId();
							// add the profile to the settings file
							JadevaadincockpitUI.getCurrent().getApplicationAttributes().getJadeSettingsFile().addProfile(input, parentId);
						}
						
					}
				});
				dialog.setEmptyInputAllowed(false);
				dialog.setEmptyInputMessage("Please enter a name.");
				
				UI.getCurrent().addWindow(dialog);

			} else if (action == RENAME_FILE) {
				
				TextInputDialog dialog = new TextInputDialog("Rename File", "Please enter a new name.", new TextInputDialog.Callback() {
					
					@Override
					public void onDialogResult(boolean isOk, String input) {
						
						if (isOk) {
							if (!input.endsWith(".ini")) { // TODO
								input = input.concat(".ini");
							}
							JadevaadincockpitUI.getCurrent().getApplicationAttributes().getJadeSettingsFile().renameSettingsFile(target, input);
						}
					}
				});
				dialog.setEmptyInputAllowed(false);
				dialog.setEmptyInputMessage("Please enter a name.");
				
				UI.getCurrent().addWindow(dialog);
				
//			} else if (action == UPDATE_INCLUDES) { // TODO
//				
//				ConfirmationDialog dialog = new ConfirmationDialog("Update Includes", "The settings file has to be saved before updating includes. Do you want to save now?", new Callback() {
//					
//					@Override
//					public void onDialogResult(boolean isOk) {
//						if (isOk) {
//							// save file before updating includes
//							JadevaadincockpitUI.getCurrent().getApplicationAttributes().getJadeSettingsFile().saveSettingsFile(target);
//							
//							// iterate over profiles
//							Iterator<?> childIterator = profileContainer.getChildren(target).iterator();
//							while (childIterator.hasNext()) {
//								Object id = childIterator.next();
//								Item child = profileContainer.getItem(id);
//								// re-read the (previously saved) file to resolve includes
//								((JADEOptions) child.getItemProperty(ProfileContainer.PROPERTY.JADEOPTIONS).getValue()).ReadSettingsFile();
//								// this is necessary to handle new protected field. would be easier if setProtected() in SOSOptionElement fired an event
//								for (BaseForm form : JadevaadincockpitUI.getCurrent().getMainView().getProfileTabSheet().getTab(child).getForms()) {
//									UiComponentCreator componentCreator = form.getUiComponentCreator();
//									for (String key : componentCreator.getFields().keySet()) {
//										AbstractField<?> field = componentCreator.getFields().get(key);
//										SOSOptionElement optionElement = (SOSOptionElement) field.getData();
//										if (optionElement.isProtected()) {
//											componentCreator.handleProtectedField(field, optionElement.isProtected());
//										}
//										
//									}
//									
//								}
//							}
//						} else {
//							// Do nothing
//						}
//					}
//				});
//				
//				UI.getCurrent().addWindow(dialog);
			}
			
		} else { // item which received the right click is a profile; check profileActions
			
			if (action == EXECUTE_PROFILE) {
				
				// TODO profile has to be saved before execution as checkMandatory() will read from the file (not from the options-Object)
				JadevaadincockpitUI.getCurrent().getApplicationAttributes().getJadeSettingsFile().saveSettingsFile(target);
				
				JadeVaadinAdapter jadeAdapter = new JadeVaadinAdapter();
				jadeAdapter.execute(profileTree.getItem(target));

			} else if (action == RENAME_PROFILE) {
				
				TextInputDialog dialog = new TextInputDialog("Rename Profile", "Please enter a new name.", new TextInputDialog.Callback() {
					
					@Override
					public void onDialogResult(boolean isOk, String input) {
						
						if (isOk) {
							JadevaadincockpitUI.getCurrent().getApplicationAttributes().getJadeSettingsFile().renameProfile(target, input);
						}
					}
				});
				dialog.setEmptyInputAllowed(false);
				dialog.setEmptyInputMessage("Please enter a name.");
				
				UI.getCurrent().addWindow(dialog);
				
			} else if (action == DELETE_PROFILE) {
				
				ConfirmationDialog dialog = new ConfirmationDialog("Delete Profile", "Do you really want to delete the selected profile from the settings file?", new ConfirmationDialog.Callback() {
				
					@Override
					public void onDialogResult(boolean isOk) {
						if (isOk) {
							// delete profile
							JadevaadincockpitUI.getCurrent().getApplicationAttributes().getJadeSettingsFile().deleteProfile(target);
						} else {
							// Do nothing
						}
					}
				});
				
				UI.getCurrent().addWindow(dialog);
			}
		}
		
		if (action == DEBUG_PRINT_OPTIONS) { // dbg
			
			System.out.println("DIRTY OPTIONS");
			Item targetItem = profileTree.getItem(target);
			@SuppressWarnings("unchecked")
			HashMap<String, SOSOptionElement> options = (HashMap<String, SOSOptionElement>) targetItem.getItemProperty(ProfileContainer.PROPERTY.OPTIONS).getValue();
			if (options != null) {
				Iterator<String> optionsIterator = options.keySet().iterator();
				while (optionsIterator.hasNext()) {
					String optionName = optionsIterator.next();
					SOSOptionElement option = options.get(optionName);
					String optionValue = option.Value();
					if (option.isDirty()) {
						System.out.println(optionName + " = " + optionValue);					
					}
				}
			} else {
				System.out.println("options is null");
			}
			
		} else if (action == DEBUG_PRINT_MISSING_OPTIONS) {
			for (String s : ApplicationAttributes.missingOptions.keySet()) {
				System.out.println("MISSING OPTION:          " + s + " = " + ApplicationAttributes.missingOptions.get(s));
			}
		}
	}
}
