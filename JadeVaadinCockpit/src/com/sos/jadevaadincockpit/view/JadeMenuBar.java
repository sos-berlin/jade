package com.sos.jadevaadincockpit.view;

import java.io.Serializable;
import java.util.Locale;

import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.globals.Globals;
import com.sos.jadevaadincockpit.i18n.JadeCockpitMsg;
import com.sos.jadevaadincockpit.view.FileSystemBrowser.BrowserType;
import com.sos.jadevaadincockpit.viewmodel.ProfileContainer;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.UI;

/**
 * 
 * @author JS
 *
 */
public class JadeMenuBar extends MenuBar implements Serializable {
	private static final long serialVersionUID = 6566031330304886129L;
	
	private MenuItem logsMenu = null;
	private MenuItem saveMenu = null;
	private MenuItem saveAsMenu = null;
	
	public JadeMenuBar() {
		init();
	}
	
	@SuppressWarnings("unused")
	private void init() {
		
		this.setWidth(100, Unit.PERCENTAGE);
		
		MenuBar.Command newCommand = new MenuBar.Command() { // TODO new what? settings file?
			private static final long serialVersionUID = -3670981539441287472L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				// TODO stub
				Notification.show(new JadeCockpitMsg("JADE_MSG_I_0001").label()); // Sorry, not available yet.
			}
		};
		
		MenuBar.Command uploadCommand = new MenuBar.Command() {
			private static final long serialVersionUID = 6874221986358894772L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				
				FileUploadDialog uploadDialog = new FileUploadDialog(new JadeCockpitMsg("JADE_L_FileUploadDialogCaption").label()); // Upload File
				UI.getCurrent().addWindow(uploadDialog);
			}
		};
		
		MenuBar.Command downloadCommand = new MenuBar.Command() {
			private static final long serialVersionUID = -8965318353651939511L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				final FileSystemBrowser browser = new FileSystemBrowser(BrowserType.download);
				UI.getCurrent().addWindow(browser);
			}
		};
		
		MenuBar.Command openCommand = new MenuBar.Command() {
			private static final long serialVersionUID = 6326691289442188975L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				
				final FileSystemBrowser browser = new FileSystemBrowser(BrowserType.open);
				UI.getCurrent().addWindow(browser);
			}
		};

		MenuBar.Command saveCommand = new MenuBar.Command() {
			private static final long serialVersionUID = -4169791139048269341L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				ProfileTree profileTree = JadevaadincockpitUI.getCurrent().getJadeMainUi().getProfileTree();
				Globals.getJadeDataProvider().saveSettingsFile(profileTree.getValue());
			}
		};

		MenuBar.Command saveAsCommand = new MenuBar.Command() {
			private static final long serialVersionUID = -5008477679888004593L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				// TODO stub
				Notification.show(new JadeCockpitMsg("JADE_MSG_I_0001").label());
			}
		}; // saveAsCommand
		
		MenuBar.Command language_DE_Command = new MenuBar.Command() {
			private static final long serialVersionUID = -5008477679888004593L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				// TODO repaint
				Locale deLocale = new Locale("de", "DE");
//				Globals.getMessages().setLocale(deLocale); TODO
				VaadinSession.getCurrent().setLocale(deLocale);
			}
		}; // language_DE_Command
		
		MenuBar.Command language_EN_Command = new MenuBar.Command() {
			private static final long serialVersionUID = -5008477679888004593L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				// TODO repaint
				Locale enLocale = new Locale("en", "US");
//				Globals.getMessages().setLocale(enLocale); TODO
				VaadinSession.getCurrent().setLocale(enLocale);
			}
		}; // language_EN_Command

		
		MenuItem fileMenu = this.addItem(new JadeCockpitMsg("JADE_L_FileMenu").label(), null, null); // File
		MenuItem newMenu = fileMenu.addItem(new JadeCockpitMsg("JADE_L_NewMenu").label(), null, newCommand); // New
		fileMenu.addSeparator();
		MenuItem uploadMenu = fileMenu.addItem(new JadeCockpitMsg("JADE_L_UploadMenu").label(), null, uploadCommand); // Upload
		MenuItem downloadMenu = fileMenu.addItem(new JadeCockpitMsg("JADE_L_DownloadMenu").label(), null, downloadCommand); // Download
		fileMenu.addSeparator();
		MenuItem openMenu = fileMenu.addItem(new JadeCockpitMsg("JADE_L_OpenMenu").label(), null, openCommand); // Open
		saveMenu = fileMenu.addItem(new JadeCockpitMsg("JADE_L_SaveMenu").label(), null, saveCommand); // Save
		saveAsMenu = fileMenu.addItem(new JadeCockpitMsg("JADE_L_SaveAsMenu").label(), null, saveAsCommand); // Save As

		setSaveItemsEnabled(false);
		
		MenuBar.Command documentationCommand = new MenuBar.Command() {
			private static final long serialVersionUID = 1717662152556571556L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				// TODO stub
				Notification.show(new JadeCockpitMsg("JADE_MSG_I_0001").label());
			}
		}; // documentationCommand
		
		MenuBar.Command aboutCommand = new MenuBar.Command() {
			private static final long serialVersionUID = 8755866863863178041L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				AboutWindow.show();
			}
		}; // aboutCommand
		
		MenuItem settingsMenu = this.addItem(new JadeCockpitMsg("JADE_L_SettingsMenu").label(), null);
		MenuItem languageMenu = settingsMenu.addItem(new JadeCockpitMsg("JADE_L_LanguageMenu").label(), null);
		MenuItem language_EN_Menu = languageMenu.addItem(new JadeCockpitMsg("JADE_L_Language_EN_Menu").label(), language_EN_Command);
		MenuItem language_DE_Menu = languageMenu.addItem(new JadeCockpitMsg("JADE_L_Language_DE_Menu").label(), language_DE_Command);
		
		MenuItem toolsMenu = this.addItem(new JadeCockpitMsg("JADE_L_ToolsMenu").label(), null); // Tools
		logsMenu = toolsMenu.addItem(new JadeCockpitMsg("JADE_L_LogsMenu").label(), null); // Logs
		logsMenu.setEnabled(logsMenu.hasChildren());
		
		MenuItem helpMenu = this.addItem(new JadeCockpitMsg("JADE_L_HelpMenu").label(), null, null); // Help
		MenuItem documentationMenu = helpMenu.addItem(new JadeCockpitMsg("JADE_L_DocumentationMenu").label(), null, documentationCommand); // Documentation
		MenuItem aboutMenu = helpMenu.addItem(new JadeCockpitMsg("JADE_L_AboutMenu").label(), null, aboutCommand); // About
	}
	
	public MenuItem addLogMenu(final Component component, final String caption) {
		
		final MenuItem logMenu = logsMenu.addItem(caption, null);
		logsMenu.setEnabled(logsMenu.hasChildren());
		
		MenuBar.Command logCommand = new MenuBar.Command() {
			private static final long serialVersionUID = 8755866863863178041L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				LogTabSheet logTabSheet = JadevaadincockpitUI.getCurrent().getJadeMainUi().getLogTabSheet();
				if (logTabSheet.getComponentCount() == 0) {
					logTabSheet.restore();
				}
				Tab tab = logTabSheet.addTab(component, caption);
				tab.setClosable(true);
				tab.setIcon(((LogPanel) component).getIconResource());
				logTabSheet.setSelectedTab(component);
				logsMenu.removeChild(logMenu);
				logsMenu.setEnabled(logsMenu.hasChildren());
			}
		}; // logCommand
		
		logMenu.setCommand(logCommand);
		
		return logMenu;
	}
	
	/**
	 * Enable or disable the Save-/Save As-Items.
	 * @param enabled
	 */
	public void setSaveItemsEnabled(boolean enabled) {
		saveMenu.setEnabled(enabled);
		saveAsMenu.setEnabled(enabled);
	}
	
}

