package com.sos.jadevaadincockpit.view;

import com.sos.jadevaadincockpit.util.FileUploadDragAndDropWrapper;
import com.sos.jadevaadincockpit.view.components.DropArea;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

/**
 * 
 * @author JS
 *
 */
public class JadeMainUi extends CustomComponent {
	private static final long serialVersionUID = 727271516807040138L;
	
	// ui-components
	private ProfileTree profileTree;
//	private ParameterTable entryTable;
	private JadeMenuBar jadeMenuBar;
	private ProfileTabSheet profileTabSheet;
	private LogTabSheet logTabSheet;
	private DropArea dropArea;
	
	// layouts
	private VerticalLayout vLayout;
	private VerticalSplitPanel vSplitPanel;
	private HorizontalSplitPanel hSplitPanel1;
	private VerticalLayout vLayout1;
	private HorizontalSplitPanel hSplitPanel2;
	
	public JadeMainUi() {
		
		profileTree = new ProfileTree();
//		entryTable = entryTableControl.getEntryTable();
		profileTabSheet = new ProfileTabSheet();
		logTabSheet = new LogTabSheet();
		dropArea = new DropArea("Drag files here");
		dropArea.setSizeFull();
		dropArea.setDragAndDropUploadEnabled(false);
		
		
		vLayout = new VerticalLayout();
		vSplitPanel = new VerticalSplitPanel();
		hSplitPanel1 = new HorizontalSplitPanel();
		vLayout1 = new VerticalLayout();
		hSplitPanel2 = new HorizontalSplitPanel();
		
		/*
		 * ui layers:
		 * 
		 *  vLayout
		 *   |--menuBar
		 *   `--vSplitPanel
		 *     `--hSplitPanel1
		 *       `--vLayout1
		 *         |--tree
		 *       `--hSplitPanel2
		 *         |--tabPanel
		 *         |--table
		 */
		
		vLayout.setSizeFull();
		
		jadeMenuBar = new JadeMenuBar();
		vLayout.addComponent(jadeMenuBar);
		
		vSplitPanel.setSplitPosition(100, Unit.PERCENTAGE);
		vSplitPanel.setSizeFull();
		vLayout.addComponent(vSplitPanel);
		vLayout.setExpandRatio(vSplitPanel, 9);
		
		hSplitPanel1.setSplitPosition(20, Unit.PERCENTAGE);
		vSplitPanel.setFirstComponent(hSplitPanel1);
		
		vLayout1.setSizeFull();
		FileUploadDragAndDropWrapper wrapper = new FileUploadDragAndDropWrapper(vLayout1);
//		vLayout1.addComponent(profileTree);
		vLayout1.addComponent(dropArea);
		
//		DropArea dropArea = new DropArea("Drag files here");
//		dropArea.setSizeFull();
//		dropArea.setDragAndDropUploadEnabled(false);
//		vLayout1.addComponent(dropArea); // TODO replace with tree on file load
		
		hSplitPanel1.setFirstComponent(wrapper);
		wrapper.setSizeFull();
		
		hSplitPanel2.setSplitPosition(70, Unit.PERCENTAGE);
//		hSplitPanel1.setSecondComponent(hSplitPanel2);
		hSplitPanel1.setSecondComponent(profileTabSheet);
		profileTabSheet.setSizeFull();
		
//		hSplitPanel2.setFirstComponent(profileTabSheet);
//		profileTabSheet.setSizeFull();
		
		vSplitPanel.setSecondComponent(logTabSheet);
		logTabSheet.setSizeFull();
		
		// TODO for test purpose
//		final VaadinJadeSettingsFile sf = new VaadinJadeSettingsFile(Globals.getUploadPath() + "js_jade_settings.ini");
//		final VaadinJadeSettingsFile sf2 = new VaadinJadeSettingsFile(Globals.getUploadPath() + "einzeiler.ini");
		
//		Button debugButton = new Button("debug_open");
//		vLayout1.addComponent(debugButton);
//		debugButton.setEnabled(true);
//		debugButton.addClickListener(new ClickListener() {
//			private static final long serialVersionUID = 863220379882101635L;
//
//			@Override
//			public void buttonClick(ClickEvent event) {
//				sf.readSettingsFile();
//				sf.displaySettingsFile();
//			}
//		});
//		
//		Button debugButton1 = new Button("debug_open2");
//		vLayout1.addComponent(debugButton1);
//		debugButton1.setEnabled(true);
//		debugButton1.addClickListener(new ClickListener() {
//			private static final long serialVersionUID = 863220379882101635L;
//
//			@Override
//			public void buttonClick(ClickEvent event) {
//				sf2.readSettingsFile();
//				sf2.displaySettingsFile();
//			}
//		});
//		
//		Button debugButton2 = new Button("debug_save");
//		vLayout1.addComponent(debugButton2);
//		debugButton2.setEnabled(true);
//		debugButton2.addClickListener(new ClickListener() {
//			private static final long serialVersionUID = 863220379882101635L;
//
//			@Override
//			public void buttonClick(ClickEvent event) {
//				sf.saveSettingsFile();
//			}
//		});
		
//		hSplitPanel2.setSecondComponent(entryTable);
		
		setCompositionRoot(vLayout);
	}
	
	public void hideProfileTree(boolean hide) {
		if (hide == true) {
			vLayout1.removeComponent(profileTree);
			vLayout1.addComponent(dropArea);
		} else {
			vLayout1.removeComponent(dropArea);
			vLayout1.addComponent(profileTree);
		}
	}
	
	public JadeMenuBar getJadeMenuBar() {
		if (jadeMenuBar == null) {
			jadeMenuBar = new JadeMenuBar();
		}
		return jadeMenuBar;
	}
	
	public LogTabSheet getLogTabSheet() {
		if (logTabSheet == null) {
			logTabSheet = new LogTabSheet();
		}
		return logTabSheet;
	}

	public ProfileTree getProfileTree() {
		if (profileTree == null) {
			profileTree = new ProfileTree();
		}
		return profileTree;
	}

	public ProfileTabSheet getProfileTabSheet() {
		if (profileTabSheet == null) {
			profileTabSheet = new ProfileTabSheet();
		}
		return profileTabSheet;
	}
	
	public VerticalSplitPanel getVSplitPanel() {
		return vSplitPanel;
	}

}