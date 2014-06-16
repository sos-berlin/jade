package com.sos.jade.backgroundservice.view.components;

import java.util.Locale;

import com.sos.jade.backgroundservice.BackgroundserviceUI;
import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.sos.jade.backgroundservice.view.MainView;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;

/**
 * A MenuBar with the Jade related MenuItems; extends Vaadins {@link com.vaadin.ui.MenuBar MenuBar}
 * 
 * @author SP
 *
 */
public class JadeMenuBar extends MenuBar {
	private static final long serialVersionUID = 1L;
	private MenuItem mFile;
	private MenuItem mFilter;
	private MenuItem mPreferences;
	private MenuItem mHelp;
	private MenuItem smActivateFilter;
	private MenuItem smLoadFilter;
	private MenuItem smSaveFilter;
	private JadeBSMessages messages = new JadeBSMessages("JADEBSMessages", Locale.getDefault());

	public JadeMenuBar() {
		setAutoOpen(true);
		addStyleName("jadeMenuBar");
		
//		setHeight(25.0f, Unit.PIXELS);
//		mFile = addItem("File", fileCommand);
//		
//		mFilter = addItem("Filter", filterCommand);
//		this.smActivateFilter = mFilter.addItem("filter...", activateFilterCommand);
//		mPreferences = addItem("Preferences", preferencesCommand);
//		
//		mHelp = addItem("Help", helpCommand);
		
		mFile = addItem(messages.getValue("JadeMenuBar.file"), fileCommand);
		
		mFilter = addItem(messages.getValue("JadeMenuBar.filter"), filterCommand);
		this.smActivateFilter = mFilter.addItem(messages.getValue("JadeMenuBar.doFilter"), activateFilterCommand);
		mPreferences = addItem(messages.getValue("JadeMenuBar.preferences"), preferencesCommand);
		
		mHelp = addItem(messages.getValue("JadeMenuBar.help"), helpCommand);
		
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
			((MainView)UI.getCurrent().getContent()).setDetailViewVisible(false);
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
