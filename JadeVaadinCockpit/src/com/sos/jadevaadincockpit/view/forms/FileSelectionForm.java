package com.sos.jadevaadincockpit.view.forms;

import com.vaadin.data.Item;
import com.vaadin.ui.TabSheet;

public class FileSelectionForm extends BaseForm {
	private static final long serialVersionUID = 3943143306232024043L;
	
	private TabSheet tabSheet = new TabSheet();
	
	public FileSelectionForm(String caption, Item section) {
		super(section);
		setCaption(caption);
		
		setCompositionRoot(tabSheet);
		createForm();
	}

	private void createForm() {
		
		tabSheet.addComponent(new FileRegExForm("Selection", profile));
		tabSheet.addComponent(new FileRenameForm("Rename", profile));
		tabSheet.addComponent(new FilePollingForm("Polling", profile));
		tabSheet.addComponent(new FileCumulateForm("CumulateFiles", profile));
		
		tabSheet.setSelectedTab(tabSheet.getTab(0));
		
	}
}
