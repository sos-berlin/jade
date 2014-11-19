package com.sos.jadevaadincockpit.view.forms;

import com.sos.jadevaadincockpit.view.components.ComponentGroup;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.VerticalLayout;

public class OperationForm extends BaseForm {
	private static final long serialVersionUID = 4628358468761073373L;
	
	// Components
	private CheckBox concurrentTransferComponents;
	private AbstractComponent maxConcurrentTransfersComponent;
	private AbstractComponent reuseConnectionComponent;
	private AbstractComponent preTransferCommandsComponent;
	private AbstractComponent postTransferCommandsComponent;
	private AbstractComponent includeComponent;
	
	public OperationForm(String caption, Item profile) {
		super(caption, profile);
		
		createForm();
	}
	
	private void createForm() {
		
		layout = new VerticalLayout();
		layout.setMargin(true);
		setCompositionRoot(layout);
		
		FormLayout layout1 = new FormLayout();
		layout1.setMargin(true);

		AbstractComponent titleComponent = componentCreator.getComponentWithCaption(jadeOptions.title);
		layout1.addComponent(titleComponent);

//		AbstractComponent isFragmentComponent = componentCreator.getComponentWithCaption(jadeOptions.isFragment);
//		layout.addComponent(isFragmentComponent);
		
		AbstractComponent operationComponent = componentCreator.getComponentWithCaption(jadeOptions.operation);
		layout1.addComponent(operationComponent);
		
		layout.addComponent(layout1);
		
		ComponentGroup dataSourceGroup = new ComponentGroup("Source");
		
		AbstractComponent sourceDirComponent = componentCreator.getComponentWithCaption(jadeOptions.Source().FolderName);
		dataSourceGroup.addComponent(sourceDirComponent);
		
		AbstractComponent fileSpecComponent = componentCreator.getComponentWithCaption(jadeOptions.file_spec);
		dataSourceGroup.addComponent(fileSpecComponent);
		
		AbstractComponent recurseSubFoldersComponent = componentCreator.getComponentWithCaption(jadeOptions.RecurseSubFolders);
		dataSourceGroup.addComponent(recurseSubFoldersComponent);
		
		layout.addComponent(dataSourceGroup);
		
		ComponentGroup dataTargetGroup = new ComponentGroup("Target");
		
		AbstractComponent targetProtocolComponent = componentCreator.getComponentWithCaption(jadeOptions.Target().protocol);
		dataTargetGroup.addComponent(targetProtocolComponent);
		
		AbstractComponent targetFolderComponent = componentCreator.getComponentWithCaption(jadeOptions.Target().FolderName);
		dataTargetGroup.addComponent(targetFolderComponent);
		
		AbstractComponent overwriteFilesComponent = componentCreator.getComponentWithCaption(jadeOptions.overwrite_files);
		dataTargetGroup.addComponent(overwriteFilesComponent);
		
		AbstractComponent targetMakeDirsComponent = componentCreator.getComponentWithCaption(jadeOptions.Target().makeDirs);
		dataTargetGroup.addComponent(targetMakeDirsComponent);
		
		AbstractComponent targetReplaceWhatComponent = componentCreator.getComponentWithCaption(jadeOptions.Target().ReplaceWhat);
		dataTargetGroup.addComponent(targetReplaceWhatComponent);
		
		AbstractComponent targetReplaceWithComponent = componentCreator.getComponentWithCaption(jadeOptions.Target().ReplaceWith);
		dataTargetGroup.addComponent(targetReplaceWithComponent);
		
		layout.addComponent(dataTargetGroup);
		
		
		
		
//		AbstractComponent transactionModeComponent = componentCreator.getComponentWithCaption(jadeOptions.TransactionMode);
//		layout.addComponent(transactionModeComponent);
		
//		AbstractComponent atomicPrefixComponent = componentCreator.getComponentWithCaption(jadeOptions.atomic_prefix);
//		layout.addComponent(atomicPrefixComponent);

//		AbstractComponent atomicSuffixComponent = componentCreator.getComponentWithCaption(jadeOptions.atomic_suffix);
//		layout.addComponent(atomicSuffixComponent);

//		concurrentTransferComponents = (CheckBox) componentCreator.getComponentWithCaption(jadeOptions.ConcurrentTransfer);
//		layout.addComponent(concurrentTransferComponents);
		
//		maxConcurrentTransfersComponent = componentCreator.getComponentWithCaption(jadeOptions.MaxConcurrentTransfers);
//		layout.addComponent(maxConcurrentTransfersComponent);

//		reuseConnectionComponent = componentCreator.getComponentWithCaption(jadeOptions.reuseConnection);
//		layout.addComponent(reuseConnectionComponent);
		
		FormLayout layout2 = new FormLayout();
		layout2.setMargin(true);
		
		preTransferCommandsComponent = componentCreator.getComponentWithCaption(jadeOptions.PreTransferCommands);
		layout2.addComponent(preTransferCommandsComponent);
		
		postTransferCommandsComponent = componentCreator.getComponentWithCaption(jadeOptions.PostTransferCommands);
		layout2.addComponent(postTransferCommandsComponent);
		
		includeComponent = componentCreator.getComponentWithCaption(jadeOptions.include);
		layout2.addComponent(includeComponent);
		
		layout.addComponent(layout2);
		
//		AbstractComponent verboseComponent = componentCreator.getComponentWithCaption(jadeOptions.verbose);
//		layout.addComponent(verboseComponent);
		
//		concurrentTransferComponents.addValueChangeListener(new ValueChangeListener() {
//			private static final long serialVersionUID = 299132474135018944L;
//
//			@Override
//			public void valueChange(ValueChangeEvent event) {
//				changeEnabledState(concurrentTransferComponents.getValue() == null ? false : concurrentTransferComponents.getValue());
//			}
//		});
		
//		changeEnabledState(concurrentTransferComponents.getValue() == null ? false : concurrentTransferComponents.getValue());
	}
	
//	private void changeEnabledState(boolean enabled) {
//		maxConcurrentTransfersComponent.setEnabled(enabled);
//		reuseConnectionComponent.setEnabled(enabled);
//	}
}
