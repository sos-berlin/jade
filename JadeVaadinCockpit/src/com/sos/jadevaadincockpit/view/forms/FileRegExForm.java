package com.sos.jadevaadincockpit.view.forms;

import com.vaadin.data.Item;
import com.vaadin.ui.AbstractComponent;

public class FileRegExForm extends BaseForm {
	private static final long serialVersionUID = -7865600618165958826L;
	
	public FileRegExForm(String caption, Item section) {
		super(section);
		setCaption(caption);
		
		createForm();
	}

	private void createForm() {
		
		// Components
		AbstractComponent sourceFolderNameComponent = componentCreator.getComponentWithCaption(jadeOptions.Source().FolderName);
		layout.addComponent(sourceFolderNameComponent);
		
		AbstractComponent fileSpecComponent = componentCreator.getComponentWithCaption(jadeOptions.file_spec);
		layout.addComponent(fileSpecComponent);
		
		AbstractComponent filePathComponent = componentCreator.getComponentWithCaption(jadeOptions.file_path);
		layout.addComponent(filePathComponent);
		
		AbstractComponent fileListNameComponent = componentCreator.getComponentWithCaption(jadeOptions.FileListName);
		layout.addComponent(fileListNameComponent);
		
		AbstractComponent targetFolderNameComponent = componentCreator.getComponentWithCaption(jadeOptions.Target().FolderName);
		layout.addComponent(targetFolderNameComponent);
		
		AbstractComponent removeFilesComponent = componentCreator.getComponentWithCaption(jadeOptions.remove_files);
		layout.addComponent(removeFilesComponent);
		
		AbstractComponent forceFilesComponent = componentCreator.getComponentWithCaption(jadeOptions.force_files);
		layout.addComponent(forceFilesComponent);
		
		AbstractComponent recurseSubFoldersComponent = componentCreator.getComponentWithCaption(jadeOptions.RecurseSubFolders);
		layout.addComponent(recurseSubFoldersComponent);
		
		AbstractComponent appendFilesComponent = componentCreator.getComponentWithCaption(jadeOptions.append_files);
		layout.addComponent(appendFilesComponent);
		
		AbstractComponent overwriteFilesComponent = componentCreator.getComponentWithCaption(jadeOptions.overwrite_files);
		layout.addComponent(overwriteFilesComponent);
		
	}

}
