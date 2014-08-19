package com.sos.jadevaadincockpit.view;

import java.io.Serializable;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sos.jadevaadincockpit.JadevaadincockpitUI;
import com.sos.jadevaadincockpit.globals.ApplicationAttributes;
import com.sos.jadevaadincockpit.globals.JadeSettingsFile;
import com.sos.jadevaadincockpit.globals.SessionAttributes;
import com.sos.jadevaadincockpit.i18n.I18NComponent;
import com.sos.jadevaadincockpit.i18n.JadeCockpitMsg;
import com.sos.jadevaadincockpit.view.FileSystemBrowser.BrowserType;
import com.sos.jadevaadincockpit.view.event.LocaleChangeEvent;
import com.sos.jadevaadincockpit.viewmodel.ProfileContainer;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.UI;

/**
 * 
 * @author JS
 *
 */
public class JadeMenuBar extends MenuBar implements Serializable, I18NComponent {
	private static final long serialVersionUID = 6566031330304886129L;
	
	private Logger logger = Logger.getLogger(JadeMenuBar.class.getName());
	
	private MenuBar.Command newCommand;
	private MenuBar.Command uploadCommand;
	private MenuBar.Command downloadCommand;
	private MenuBar.Command openCommand;
	private MenuBar.Command saveCommand;
	private MenuBar.Command saveAsCommand;
	private MenuBar.Command language_DE_Command;
	private MenuBar.Command language_EN_Command;
	private MenuBar.Command documentationCommand;
	private MenuBar.Command aboutCommand;
	
	private MenuItem fileMenu;
	private MenuItem newMenu;
	private MenuItem uploadMenu;
	private MenuItem downloadMenu;
	private MenuItem openMenu;
	private MenuItem saveMenu;
	private MenuItem saveAsMenu;
	private MenuItem settingsMenu;
	private MenuItem languageMenu;
	private MenuItem language_EN_Menu;
	private MenuItem language_DE_Menu;
	private MenuItem toolsMenu;
	private MenuItem logsMenu;
	private MenuItem helpMenu;
	private MenuItem documentationMenu;
	private MenuItem aboutMenu;
	
	public JadeMenuBar() {
		init();
	}
	
	@SuppressWarnings("unused")
	private void init() {
		
		logger.setLevel(Level.ALL);
		
		this.setWidth(100, Unit.PERCENTAGE);
		
		getCommands();
		
		fileMenu = this.addItem(new JadeCockpitMsg("JADE_L_FileMenu").label(), null, null); // File
		newMenu = fileMenu.addItem(new JadeCockpitMsg("JADE_L_NewMenu").label(), null, newCommand); // New
		fileMenu.addSeparator();
		uploadMenu = fileMenu.addItem(new JadeCockpitMsg("JADE_L_UploadMenu").label(), null, uploadCommand); // Upload
		downloadMenu = fileMenu.addItem(new JadeCockpitMsg("JADE_L_DownloadMenu").label(), null, downloadCommand); // Download
		fileMenu.addSeparator();
		openMenu = fileMenu.addItem(new JadeCockpitMsg("JADE_L_OpenMenu").label(), null, openCommand); // Open
		saveMenu = fileMenu.addItem(new JadeCockpitMsg("JADE_L_SaveMenu").label(), null, saveCommand); // Save
		saveAsMenu = fileMenu.addItem(new JadeCockpitMsg("JADE_L_SaveAsMenu").label(), null, saveAsCommand); // Save As

		setSaveItemsEnabled(false);
		
		settingsMenu = this.addItem(new JadeCockpitMsg("JADE_L_SettingsMenu").label(), null);
		languageMenu = settingsMenu.addItem(new JadeCockpitMsg("JADE_L_LanguageMenu").label(), null);
		language_EN_Menu = languageMenu.addItem(new JadeCockpitMsg("JADE_L_Language_EN_Menu").label(), language_EN_Command);
		language_DE_Menu = languageMenu.addItem(new JadeCockpitMsg("JADE_L_Language_DE_Menu").label(), language_DE_Command);
		
		toolsMenu = this.addItem(new JadeCockpitMsg("JADE_L_ToolsMenu").label(), null); // Tools
		logsMenu = toolsMenu.addItem(new JadeCockpitMsg("JADE_L_LogsMenu").label(), null); // Logs
		logsMenu.setEnabled(logsMenu.hasChildren());
		
		helpMenu = this.addItem(new JadeCockpitMsg("JADE_L_HelpMenu").label(), null, null); // Help
		documentationMenu = helpMenu.addItem(new JadeCockpitMsg("JADE_L_DocumentationMenu").label(), null, documentationCommand); // Documentation
		aboutMenu = helpMenu.addItem(new JadeCockpitMsg("JADE_L_AboutMenu").label(), null, aboutCommand); // About
		
		setImmediate(true);
	}
	
	private void getCommands() {
		
		newCommand = getNewCommand();
		
		uploadCommand = getUploadCommand();
		
		downloadCommand = getDownloadCommand();
		
		openCommand = getOpenCommand();

		saveCommand = getSaveCommand();

		saveAsCommand = getSaveAsCommand();
		
		language_DE_Command = getLanguage_DE_Command();
		
		language_EN_Command = getLanguage_EN_Command();
		
		documentationCommand = getDocumentationCommand();
		
		aboutCommand = getAboutCommand();
	}

	private Command getAboutCommand() {
		return new MenuBar.Command() {
			private static final long serialVersionUID = 8755866863863178041L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				AboutWindow.show();
			}
		};
	}

	private Command getDocumentationCommand() {
		return new MenuBar.Command() {
			private static final long serialVersionUID = 1717662152556571556L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				// TODO stub
				Notification.show(new JadeCockpitMsg("JADE_MSG_I_0001").label());
			}
		};
	}

	private Command getLanguage_EN_Command() {
		return new MenuBar.Command() {
			private static final long serialVersionUID = -5008477679888004593L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				Locale enLocale = new Locale("en", "US");
				Locale oldLocale = getSession().getLocale();
				JadevaadincockpitUI.getCurrent().getSessionAttributes().getEventBus().post(new LocaleChangeEvent(oldLocale, enLocale));
			}
		};
	}

	private Command getLanguage_DE_Command() {
		return new MenuBar.Command() {
			private static final long serialVersionUID = -5008477679888004593L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				// TODO repaint
				Locale deLocale = new Locale("de", "DE");
				Locale oldLocale = getSession().getLocale();
				JadevaadincockpitUI.getCurrent().getSessionAttributes().getEventBus().post(new LocaleChangeEvent(oldLocale, deLocale));
			}
		};
	}

	private Command getSaveAsCommand() {
		return new MenuBar.Command() {
			private static final long serialVersionUID = -5008477679888004593L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				// TODO stub
				Notification.show(new JadeCockpitMsg("JADE_MSG_I_0001").label());
			}
		};
	}

	private Command getSaveCommand() {
		return new MenuBar.Command() {
			private static final long serialVersionUID = -4169791139048269341L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				ProfileTree profileTree = JadevaadincockpitUI.getCurrent().getMainView().getProfileTree();
				JadevaadincockpitUI.getCurrent().getApplicationAttributes().getJadeSettingsFile().saveSettingsFile(profileTree.getValue());
			}
		};
	}

	private Command getOpenCommand() {
		return new MenuBar.Command() {
			private static final long serialVersionUID = 6326691289442188975L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				
				final FileSystemBrowser browser = new FileSystemBrowser(BrowserType.open);
				UI.getCurrent().addWindow(browser);
			}
		};
	}

	private Command getDownloadCommand() {
		return new MenuBar.Command() {
			private static final long serialVersionUID = -8965318353651939511L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				final FileSystemBrowser browser = new FileSystemBrowser(BrowserType.download);
				UI.getCurrent().addWindow(browser);
			}
		};
	}

	private Command getNewCommand() {
		return new MenuBar.Command() { // TODO new what? settings file?
			private static final long serialVersionUID = -3670981539441287472L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				// TODO stub
				Notification.show(new JadeCockpitMsg("JADE_MSG_I_0001").label()); // Sorry, not available yet.
				JadevaadincockpitUI.getCurrent().getSessionAttributes().getEventBus().post(new ClickEvent(JadevaadincockpitUI.getCurrent()));
			}
		};
	}
	
	private Command getUploadCommand() {
		return new MenuBar.Command() {
			private static final long serialVersionUID = 6874221986358894772L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				
				FileUploadDialog uploadDialog = new FileUploadDialog(new JadeCockpitMsg("JADE_L_FileUploadDialogCaption").label()); // Upload File
				UI.getCurrent().addWindow(uploadDialog);
			}
		};
	}

	public MenuItem addLogMenu(final Component component, final String caption) {
		
		final MenuItem logMenu = logsMenu.addItem(caption, null);
		logsMenu.setEnabled(logsMenu.hasChildren());
		
		MenuBar.Command logCommand = new MenuBar.Command() {
			private static final long serialVersionUID = 8755866863863178041L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				LogTabSheet logTabSheet = JadevaadincockpitUI.getCurrent().getMainView().getLogTabSheet();
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

	@Override
	public void refreshLocale(Locale newLocale) {

		getCommands();

		saveMenu.setText(new JadeCockpitMsg("JADE_L_SaveMenu").label());
		saveAsMenu.setText(new JadeCockpitMsg("JADE_L_SaveAsMenu").label());
		fileMenu.setText(new JadeCockpitMsg("JADE_L_FileMenu").label());
		newMenu.setText(new JadeCockpitMsg("JADE_L_NewMenu").label());
		uploadMenu.setText(new JadeCockpitMsg("JADE_L_UploadMenu").label());
		downloadMenu.setText(new JadeCockpitMsg("JADE_L_DownloadMenu").label());
		openMenu.setText(new JadeCockpitMsg("JADE_L_OpenMenu").label());
		settingsMenu.setText(new JadeCockpitMsg("JADE_L_SettingsMenu").label());
		languageMenu.setText(new JadeCockpitMsg("JADE_L_LanguageMenu").label());
		language_EN_Menu.setText(new JadeCockpitMsg("JADE_L_Language_EN_Menu").label());
		language_DE_Menu.setText(new JadeCockpitMsg("JADE_L_Language_DE_Menu").label());
		toolsMenu.setText(new JadeCockpitMsg("JADE_L_ToolsMenu").label());
		logsMenu.setText(new JadeCockpitMsg("JADE_L_LogsMenu").label());
		helpMenu.setText(new JadeCockpitMsg("JADE_L_HelpMenu").label());
		documentationMenu.setText(new JadeCockpitMsg("JADE_L_DocumentationMenu").label());
		aboutMenu.setText(new JadeCockpitMsg("JADE_L_AboutMenu").label());
	}
	
	/**
	 * TODO test
	 */
	public void doSomething(String text) {
		getItems().get(0).setText(text);
		JadeMenuBar.this.markAsDirty();
	}

}

