package com.sos.jadevaadincockpit.view;

import java.io.File;
import java.util.Locale;

import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.globals.ApplicationAttributes;
import com.sos.jadevaadincockpit.i18n.I18NComponent;
import com.sos.jadevaadincockpit.viewmodel.ProfileContainer;
import com.vaadin.data.Item;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;

/**
 * 
 * @author JS
 *
 */
public class LogTabSheet extends TabSheet implements I18NComponent {
	private static final long serialVersionUID = -2393436049375232423L;
	
	public LogTabSheet() {
		
		setCloseHandler(new CloseHandler() {
			private static final long serialVersionUID = 2682636091439645661L;

			@Override
			public void onTabClose(TabSheet tabsheet, Component tabContent) {
				Tab tab = tabsheet.getTab(tabContent);
				JadevaadincockpitUI.getCurrent().getMainView().getJadeMenuBar().addLogMenu(tabContent, tab.getCaption());
				tabsheet.removeTab(tab);
				
				if (tabsheet.getComponentCount() == 0) {
					minimize();
				}
			}
		});
	}

	public LogPanel createLogTab(Item profile) {
		String profileName = (String) profile.getItemProperty(ProfileContainer.PROPERTY.NAME).getValue();
		
		if (getComponentCount() == 0) {
			restore();
		}
		
		LogPanel logPanel = new LogPanel(profile);
		addComponent(logPanel);
		getTab(logPanel).setClosable(true);
		getTab(logPanel).setCaption(profileName);
		
		setSelectedTab(logPanel);
		
		ThemeResource resource = new ThemeResource("icons/16-circle-green.png");
		logPanel.setIconResource(resource);
		getTab(logPanel).setIcon(resource);
		
		return logPanel;
	}

	/**
	 * Adds the execution start time to the logPanel's caption.
	 * @param logPanel
	 * @param time
	 */
	public void setLogTime(LogPanel logPanel, String time) {
		logPanel.setStartTime(time);
		String oldCaption = getTab(logPanel).getCaption();
		getTab(logPanel).setCaption(oldCaption + " - " + time);
	}
	
	/**
	 * Minimizes/hides the LogTabSheet.
	 */
	public void minimize() {
		JadevaadincockpitUI.getCurrent().getMainView().getVSplitPanel().setSplitPosition(100f, Unit.PERCENTAGE);
	}
	
	/**
	 * Restores the LogTabSheet to default size.
	 */
	public void restore() {
		JadevaadincockpitUI.getCurrent().getMainView().getVSplitPanel().setSplitPosition(70f, Unit.PERCENTAGE);
	}
	
	/**
	 * Maximizes the LogTabSheet to full size.
	 */
	public void maximize() {
		JadevaadincockpitUI.getCurrent().getMainView().getVSplitPanel().setSplitPosition(0f, Unit.PERCENTAGE);
	}

	public void refreshLocale(Locale newLocale) {
		// TODO Auto-generated method stub
	}
	
}
