package com.sos.jadevaadincockpit.view;

import com.sos.jadevaadincockpit.i18n.JadeCockpitMsg;
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
public class MainView extends CustomComponent {
	private static final long serialVersionUID = 727271516807040138L;
	
	// ui-components
	private ProfileTree profileTree;
//	private ParameterTable entryTable;
	private JadeMenuBar jadeMenuBar;
	private ProfileTabSheet profileTabSheet;
	private LogTabSheet logTabSheet;
	private DropArea dropArea;
	private SettingsFileOverview settingsFileOverview;
	
	// layouts
	private VerticalLayout vLayout;
	private VerticalSplitPanel vSplitPanel;
	private HorizontalSplitPanel hSplitPanel1;
	private VerticalLayout vLayout1;
	private HorizontalSplitPanel hSplitPanel2;
	
	public MainView() {
		
		profileTree = new ProfileTree();
		profileTabSheet = new ProfileTabSheet();
		logTabSheet = new LogTabSheet();
		dropArea = new DropArea(new JadeCockpitMsg("jade_l_MainUi_DropArea").label()); // Drag files here
		dropArea.setSizeFull();
		dropArea.setDragAndDropUploadEnabled(false);
		settingsFileOverview = new SettingsFileOverview();
		
		
		vLayout = new VerticalLayout();
		vSplitPanel = new VerticalSplitPanel();
		hSplitPanel1 = new HorizontalSplitPanel();
		vLayout1 = new VerticalLayout();
		hSplitPanel2 = new HorizontalSplitPanel();
		
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
		if (profileTree.getContainerDataSource().size() > 0) {
			vLayout1.addComponent(profileTree);
		} else {
			vLayout1.addComponent(dropArea);			
		}
		
		hSplitPanel1.setFirstComponent(wrapper);
		wrapper.setSizeFull();
		
		hSplitPanel2.setSplitPosition(70, Unit.PERCENTAGE);
		hSplitPanel1.setSecondComponent(profileTabSheet);
		profileTabSheet.setSizeFull();
		
		vSplitPanel.setSecondComponent(logTabSheet);
		logTabSheet.setSizeFull();
		
		// TODO for test purpose
//		Button debugButton = new Button("debug");
//		vLayout1.addComponent(debugButton);
//		debugButton.setEnabled(true);
//		debugButton.addClickListener(new ClickListener() {
//			private static final long serialVersionUID = 863220379882101635L;
//
//			@Override
//			public void buttonClick(ClickEvent event) {
//				Object itemId = Globals.container.addItem();
//				Globals.container.getItem(itemId).getItemProperty(ProfileContainer.PROPERTY.NAME).setValue("NEW ITEM");
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
	
	/**
	 * Shows the ProfileTabSheet and removes the SettingsFileOverview.
	 */
	public void showProfileTabSheet() {
		hSplitPanel1.removeComponent(settingsFileOverview);
		hSplitPanel1.addComponent(profileTabSheet);
	}
	
	/**
	 * Shows the SettingsFileOverview and removes the ProfileTabSheet.
	 */
	public void showSettingsFileOverview() {
		hSplitPanel1.removeComponent(profileTabSheet);
		hSplitPanel1.addComponent(settingsFileOverview);
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
	
	public SettingsFileOverview getSettingsFileOverview() {
		if (settingsFileOverview == null) {
			settingsFileOverview = new SettingsFileOverview();
		}
		return settingsFileOverview;
	}
	
	public VerticalSplitPanel getVSplitPanel() {
		return vSplitPanel;
	}
	
	public HorizontalSplitPanel getHSplitPanel1() {
		return hSplitPanel1;
	}

}
