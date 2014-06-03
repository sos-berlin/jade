package com.sos.jade.backgroundservice.view.components;

import com.sos.jade.backgroundservice.BackgroundserviceUI;
import com.sos.jade.backgroundservice.view.MainView;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;

public class JadeMenuBar extends MenuBar {
	private static final long serialVersionUID = 1L;
	private MenuItem mFile;
	private MenuItem mFilter;
	private MenuItem mPreferences;
	private MenuItem mHelp;
	private MenuItem smActivateFilter;

	public JadeMenuBar() {
		setAutoOpen(true);
		setHeight(25.0f, Unit.PIXELS);
		mFile = addItem("File", fileCommand);
		
		mFilter = addItem("Filter", filterCommand);
		smActivateFilter = mFilter.addItem("filter...", activateFilterCommand);
		mPreferences = addItem("Preferences", preferencesCommand);
		
		mHelp = addItem("Help", helpCommand);
		
	}

	private MenuBar.Command fileCommand = new MenuBar.Command() {
		private static final long serialVersionUID = 1L;

		public void menuSelected(MenuItem selectedItem) {
	        //TODO
	    }  
	};

	private MenuBar.Command filterCommand = new MenuBar.Command() {
		private static final long serialVersionUID = 1L;

		public void menuSelected(MenuItem selectedItem) {
	        //TODO
	    }  
	};

	private MenuBar.Command activateFilterCommand = new MenuBar.Command() {
		private static final long serialVersionUID = 1L;

		public void menuSelected(MenuItem selectedItem) {
			((MainView)UI.getCurrent().getContent()).setHistoryTableNotVisible();
			UI.getCurrent().addWindow(((BackgroundserviceUI)UI.getCurrent()).getModalWindow());
	    }  
	};

	private MenuBar.Command preferencesCommand = new MenuBar.Command() {
		private static final long serialVersionUID = 1L;

		public void menuSelected(MenuItem selectedItem) {
	        //TODO
	    }  
	};

	private MenuBar.Command helpCommand = new MenuBar.Command() {
		private static final long serialVersionUID = 1L;

		public void menuSelected(MenuItem selectedItem) {
	        //TODO
	    }  
	};

}
