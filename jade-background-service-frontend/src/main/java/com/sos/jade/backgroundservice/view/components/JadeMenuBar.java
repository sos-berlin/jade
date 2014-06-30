package com.sos.jade.backgroundservice.view.components;

import java.util.Locale;

import com.sos.jade.backgroundservice.BackgroundserviceUI;
import com.sos.jade.backgroundservice.util.DuplicatesFilter;
import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.sos.jade.backgroundservice.view.MainView;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.IndexedContainer;
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
	private MenuItem smDuplicatesFilter;
	private MenuItem smLoadFilter;
	private MenuItem smSaveFilter;
	private JadeBSMessages messages;
	private boolean duplicatesFilterActive = false;
	private DuplicatesFilter filter = new DuplicatesFilter();


	public JadeMenuBar(JadeBSMessages messages) {
		this.messages = messages;
		setAutoOpen(true);
		addStyleName("jadeMenuBar");
		
		this.mFile = addItem(messages.getValue("JadeMenuBar.file", VaadinSession.getCurrent().getLocale()), fileCommand);
		
		this.mFilter = addItem(messages.getValue("JadeMenuBar.filter", VaadinSession.getCurrent().getLocale()), filterCommand);
		this.smActivateFilter = mFilter.addItem(messages.getValue("JadeMenuBar.doFilter", VaadinSession.getCurrent().getLocale()), activateFilterCommand);
		this.smDuplicatesFilter = mFilter.addItem(messages.getValue("JadeMenuBar.removeDuplicates", VaadinSession.getCurrent().getLocale()), duplicatesFilterCommand);
		this.smDuplicatesFilter.setCheckable(true);
		this.smDuplicatesFilter.setEnabled(true);
		this.mPreferences = addItem(messages.getValue("JadeMenuBar.preferences", VaadinSession.getCurrent().getLocale()), preferencesCommand);
		
		this.mHelp = addItem(messages.getValue("JadeMenuBar.help", VaadinSession.getCurrent().getLocale()), helpCommand);
		
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

	private MenuBar.Command duplicatesFilterCommand = new MenuBar.Command() {
		private static final long serialVersionUID = 1L;

		public void menuSelected(MenuItem selectedItem) {
			if(((MainView)UI.getCurrent().getContent()).getTblDetails().isVisible()){
				((MainView)UI.getCurrent().getContent()).getTblDetails().setVisible(false);
				((MainView)UI.getCurrent().getContent()).setMarkedRow(null);
			}
			filter.setHistoryItems(((MainView)UI.getCurrent().getContent()).getHistoryItems());
			if(selectedItem.isChecked()){
				((IndexedContainer)((MainView)UI.getCurrent().getContent()).getTblFileHistory().getContainerDataSource()).addContainerFilter(filter);
			}else{
				((IndexedContainer)((MainView)UI.getCurrent().getContent()).getTblFileHistory().getContainerDataSource()).removeContainerFilter(filter);
//				((IndexedContainer)((MainView)UI.getCurrent().getContent()).getTblFileHistory().getContainerDataSource()).removeAllContainerFilters();
			}
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
		smDuplicatesFilter.setText(messages.getValue("JadeMenuBar.removeDuplicates", locale));
		mPreferences.setText(messages.getValue("JadeMenuBar.preferences", locale));
		mHelp.setText(messages.getValue("JadeMenuBar.help", locale));
		JadeMenuBar.this.markAsDirty();
	}

	public MenuItem getSmDublicatesFilter() {
		return smDuplicatesFilter;
	}

}
