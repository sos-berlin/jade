package com.sos.jade.backgroundservice.view.components;

import java.util.Locale;

import com.sos.jade.backgroundservice.BackgroundserviceUI;
import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.sos.jade.backgroundservice.view.MainView;
import com.vaadin.server.VaadinSession;
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
	private JadeBSMessages messages;

	public JadeMenuBar(JadeBSMessages messages) {
		this.messages = messages;
		setAutoOpen(true);
		addStyleName("jadeMenuBar");
		
		mFile = addItem(messages.getValue("JadeMenuBar.file", VaadinSession.getCurrent().getLocale()), fileCommand);
		
		mFilter = addItem(messages.getValue("JadeMenuBar.filter", VaadinSession.getCurrent().getLocale()), filterCommand);
		this.smActivateFilter = mFilter.addItem(messages.getValue("JadeMenuBar.doFilter", VaadinSession.getCurrent().getLocale()), activateFilterCommand);
		mPreferences = addItem(messages.getValue("JadeMenuBar.preferences", VaadinSession.getCurrent().getLocale()), preferencesCommand);
		
		mHelp = addItem(messages.getValue("JadeMenuBar.help", VaadinSession.getCurrent().getLocale()), helpCommand);
		
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

	public void refreshCaptions(Locale locale){
		mFile.setText(messages.getValue("JadeMenuBar.file", locale));
		mFilter.setText(messages.getValue("JadeMenuBar.filter", locale));
		smActivateFilter.setText(messages.getValue("JadeMenuBar.doFilter", locale));
		mPreferences.setText(messages.getValue("JadeMenuBar.preferences", locale));
		mHelp.setText(messages.getValue("JadeMenuBar.help", locale));
		JadeMenuBar.this.markAsDirty();
	}

}
