package com.sos.jadevaadincockpit.view.forms;

import com.sos.jadevaadincockpit.view.components.ComponentGroup;
import com.vaadin.data.Item;
import com.vaadin.ui.AbstractComponent;

public class FileRenameForm extends BaseForm {
	private static final long serialVersionUID = -223045504779498452L;
	
	public FileRenameForm(String caption, Item section) {
		super(section);
		setCaption(caption);
		
		createForm();
	}

	private void createForm() {
		
		ComponentGroup generalGroup = new ComponentGroup("General");
		
		AbstractComponent replaceWhatComponent = componentCreator.getComponentWithCaption(jadeOptions.ReplaceWhat);
		generalGroup.addComponent(replaceWhatComponent);
		
		AbstractComponent replaceWithComponent = componentCreator.getComponentWithCaption(jadeOptions.ReplaceWith);
		generalGroup.addComponent(replaceWithComponent);
		
		layout.addComponent(generalGroup);
		
		// ---
		
		ComponentGroup sourceGroup = new ComponentGroup("Source");
		
		AbstractComponent sourceReplaceWhatComponent = componentCreator.getComponentWithCaption(jadeOptions.Source().ReplaceWhat);
		sourceGroup.addComponent(sourceReplaceWhatComponent);
		
		AbstractComponent sourceReplaceWithComponent = componentCreator.getComponentWithCaption(jadeOptions.Source().ReplaceWith);
		sourceGroup.addComponent(sourceReplaceWithComponent);
		
		layout.addComponent(sourceGroup);
		
		// ---
		
		ComponentGroup targetGroup = new ComponentGroup("Target");
		
		AbstractComponent targetReplaceWhatComponent = componentCreator.getComponentWithCaption(jadeOptions.Target().ReplaceWhat);
		targetGroup.addComponent(targetReplaceWhatComponent);
		
		AbstractComponent targetReplaceWithComponent = componentCreator.getComponentWithCaption(jadeOptions.Target().ReplaceWith);
		targetGroup.addComponent(targetReplaceWithComponent);
		
		layout.addComponent(targetGroup);
		
	}

}
