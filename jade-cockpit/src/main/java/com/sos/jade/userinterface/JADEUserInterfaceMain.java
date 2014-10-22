package com.sos.jade.userinterface;

import static com.sos.dialog.Globals.MsgHandler;
import static com.sos.dialog.swtdesigner.SWTResourceManager.getImageFromResource;

import java.util.concurrent.Future;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.RootLogger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchActionConstants;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Options.SOSOptionOutFileName;
import com.sos.JSHelper.concurrent.SOSThreadPoolExecutor;
import com.sos.JSHelper.io.Files.JSIniFile;
import com.sos.dialog.Globals;
import com.sos.dialog.classes.DialogAdapter;
import com.sos.dialog.classes.SOSCTabFolder;
import com.sos.dialog.classes.SOSCTabItem;
import com.sos.dialog.classes.SOSComposite;
import com.sos.dialog.classes.SOSSashForm;
import com.sos.dialog.classes.WindowsSaver;
import com.sos.dialog.components.CompositeBaseClass;
import com.sos.dialog.components.ControlHelper;
import com.sos.dialog.components.SOSCursor;
import com.sos.dialog.components.SOSPreferenceStore;
import com.sos.dialog.components.SplashScreen;
import com.sos.dialog.components.WaitCursor;
import com.sos.dialog.interfaces.IDialogActionHandler;
import com.sos.dialog.menu.MenueActionExecuteBase;
import com.sos.dialog.menu.MenueActionExit;
import com.sos.dialog.menu.MenueActionNewBase;
import com.sos.dialog.menu.MenueActionOpenBase;
import com.sos.dialog.menu.MenueActionSave;
import com.sos.dialog.menu.SOSMenueEvent;
import com.sos.dialog.message.ErrorLog;
import com.sos.dialog.swtdesigner.SWTResourceManager;
import com.sos.jade.userinterface.adapters.JADEEngineAdapter;
import com.sos.jade.userinterface.composite.LogFileComposite;
import com.sos.jade.userinterface.composite.MainComposite;
import com.sos.jade.userinterface.composite.SourceBrowserComposite;
import com.sos.jade.userinterface.composite.createNewProfile;
import com.sos.jade.userinterface.data.JadeTreeViewEntry;
import com.sos.jade.userinterface.data.SectionsHandler;
import com.sos.jade.userinterface.data.Session;
import com.sos.jade.userinterface.globals.JADEMsg;

public class JADEUserInterfaceMain extends ApplicationWindow {
	public static final String	conAPP_NAME			= "JADECockpit";
	private final Logger		logger				= Logger.getLogger(JADEUserInterfaceMain.class);
	public final String			conSVNVersion		= "$Id$";
	@SuppressWarnings("unused")
	private final JADEOptions	objJadeOptions		= new JADEOptions();
	private WindowsSaver		objPersistenceStore;
	private SOSCTabFolder		tabFolder			= null;
	private JadeTreeViewEntry	objSelectedTreeItem	= null;
	private TreeViewer			treeViewer			= null;

	SplashScreen				splash				= null;

	/**
	 * Create the application window.
	 */
	@SuppressWarnings("deprecation")
	public JADEUserInterfaceMain() {
		super(null);
		//		Display display = Display.getCurrent();
		splash = SplashScreen.newInstance(2, "Jade-Splash-1.png");
		splash.show();

		BasicConfigurator.configure();
		RootLogger.getRoot().setLevel(Level.DEBUG);
		Globals.gstrApplication = ErrorLog.gstrApplication = conAPP_NAME;
		SOSPreferenceStore.gstrApplication = ErrorLog.gstrApplication;
		CompositeBaseClass.gflgCreateControlsImmediate = false;
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addStatusLine();
	}

	@Override
	protected Control createContents(final Composite parent) {
		try (@SuppressWarnings("resource")
		SOSCursor objCur = new SOSCursor().showWait()) {
			Control objC = createContents2(parent);
			splash.step();
			return objC;
		}
		catch (Exception e) {
			splash = null;
		}
		return null;
	}
	//	private final String	strCurrentConfigFile	= "src/test/resources/jade_settings.ini";
	private final String	strCurrentConfigFile	= "src/test/resources/new.ini";

	/**
	 * Create contents of the application window.
	 * 
	 * @param parent
	 */
	protected Control createContents2(final Composite parent) {

		Display display = parent.getDisplay();
		assert display != null;
		objPersistenceStore = new WindowsSaver(this.getClass(), this.getShell(), 940, 600);

		Globals.setApplicationWindow(this);
		Globals.MsgHandler = new JADEMsg("init");

		statusLineManager.getControl().setBackground(Globals.getCompositeBackground());

		setStatus(" ");
		Composite container = new SOSComposite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		{
			final SOSSashForm sashForm = new SOSSashForm(container, SWT.BORDER | SWT.SMOOTH, conAPP_NAME + "Sash");
			parent.addControlListener(new ControlAdapter() {
				@Override
				public void controlResized(final ControlEvent e) {
					logger.debug("control resized");
					//					objPersistenceStore.saveWindowPosAndSize();
				}
			});
			{
				treeViewer = new TreeViewer(sashForm, SWT.BORDER + SWT.MULTI);
				Tree tree = treeViewer.getTree();
				tree.setSortDirection(SWT.DOWN);
				tree.setBackground(Globals.getCompositeBackground());

				// Create the popup menu
				final MenuManager menuPopUpMenueMgr = new MenuManager("#PopupMenu");
				menuPopUpMenueMgr.setRemoveAllWhenShown(true); // see http://wiki.eclipse.org/FAQ_How_do_I_make_menus_with_dynamic_contents%3F

				Menu menu = menuPopUpMenueMgr.createContextMenu(treeViewer.getControl());
				// getSite().registerContextMenu(menuMgr, treeViewer);
				menuPopUpMenueMgr.addMenuListener(new IMenuListener() {
					@Override
					public void menuAboutToShow(final IMenuManager manager) {
						try (SOSCursor objC = new SOSCursor().showWait()) {
							if (treeViewer.getSelection().isEmpty()) {
								return;
							}
							if (treeViewer.getSelection() instanceof ISelection) {
								JadeTreeViewEntry objSelectedSection1 = null;
								if ((objSelectedSection1 = getTreeViewEntry()) != null) {
									String strCaption = objSelectedSection1.getTitle();
									if (objSelectedSection1.isExecutable()) {
										menuPopUpMenueMgr.add(new actionExecuteProfile());
									}
									actionBrowseSource.setText(MsgHandler.newMsg("treenode_menu_BrowseSource").params(strCaption));
									menuPopUpMenueMgr.add(actionBrowseSource);

									menuPopUpMenueMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

									actCopy.setText(MsgHandler.newMsg("treenode_menu_Copy").params(strCaption));
									actCopy.setImageDescriptor(getImageDescr("Copy.gif"));
									menuPopUpMenueMgr.add(actCopy);

									//									menuPopUpMenueMgr.add(new actionSave(strCaption));
									menuPopUpMenueMgr.add(actSave.addParam(strCaption));

									actSaveAs.setText(MsgHandler.newMsg("treenode_menu_SaveAs").params(strCaption));
									menuPopUpMenueMgr.add(actSaveAs);

									actDelete.setText(MsgHandler.newMsg("treenode_menu_Delete").params(strCaption));
									actDelete.setImageDescriptor(getImageDescr("Delete.gif"));
									menuPopUpMenueMgr.add(actDelete);

									actRename.setText(MsgHandler.newMsg("treenode_menu_Rename").params(strCaption));
									actRename.setImageDescriptor(getImageDescr("Rename.gif"));
									menuPopUpMenueMgr.add(actRename);

									actPaste.setText(MsgHandler.newMsg("treenode_menu_Paste").params(strCaption));
									actPaste.setImageDescriptor(getImageDescr("DocumentIn.gif"));
									menuPopUpMenueMgr.add(actPaste);

									menuPopUpMenueMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
									menuPopUpMenueMgr.add(new actionNewProfile(strCaption));

									if (objSelectedSection1.isSourceGen()) {
										menuPopUpMenueMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

										MenuManager subMenu = new MenuManager(MsgHandler.newMsg("treenode_menu_Create").params(strCaption),
												getImageDescr("jobscheduler_rabbit.png"), "");
										actCreateJobChain.setText(MsgHandler.newMsg("treenode_menu_JobChain").params(strCaption));
										actCreateJobChain.setImageDescriptor(getImageDescr("Chain.gif"));
										subMenu.add(actCreateJobChain);

										actCreateScript.setText(MsgHandler.newMsg("treenode_menu_Script").params(strCaption));
										actCreateScript.setImageDescriptor(getImageDescr("Document.gif"));
										subMenu.add(actCreateScript);

										menuPopUpMenueMgr.add(subMenu);
									}
								}
							}
						}
						catch (Exception e) {
							new ErrorLog("problem with menu", e);
						}

					}
				});
				menuPopUpMenueMgr.setRemoveAllWhenShown(true);
				treeViewer.getControl().setMenu(menu);
				treeViewer.addTreeListener(new ITreeViewerListener() {
					@Override
					public void treeCollapsed(final TreeExpansionEvent event) {
					}

					@Override
					public void treeExpanded(final TreeExpansionEvent event) {
					}
				});
				treeViewer.addDoubleClickListener(new IDoubleClickListener() {
					@Override
					public void doubleClick(final DoubleClickEvent objEvent) {
						try (SOSCursor objCur = new SOSCursor().showWait()) {
							IStructuredSelection objSelection = (IStructuredSelection) objEvent.getSelection();
							TreeViewer objTv = (TreeViewer) objEvent.getSource();
							Tree tree1 = objTv.getTree();
							int i = 0;
							for (Object objSel : objSelection.toList()) {
								if (objSel instanceof JadeTreeViewEntry) {
									JadeTreeViewEntry objJadeTreeViewEntry = (JadeTreeViewEntry) objSel;
									if (objJadeTreeViewEntry.isFragment() || objJadeTreeViewEntry.isProfile()) {
										String strM = "Section ausgewählt: " + objJadeTreeViewEntry.getName();
										setStatus(strM);
										objJadeTreeViewEntry.setTreeItem(tree1.getSelection()[i++]);
										createTabFolder(objJadeTreeViewEntry);
									}
								}
							}

						}
						catch (Exception e) {
							new ErrorLog("problem", e);
						}
					}
				});
				treeViewer.setExpandPreCheckFilters(false);
				treeViewer.setLabelProvider(new TreeLabelProvider());
				treeViewer.setContentProvider(new TreeContentProvider());
				treeViewer.setSorter(new Sorter());
				treeViewer.setInput(initializeSession(strCurrentConfigFile));
				getShell().setText(Globals.gstrApplication + ": " + strCurrentConfigFile);
				treeViewer.expandAll();

				tabFolder = new SOSCTabFolder(sashForm, SWT.BORDER);
				tabFolder.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent event) {
						logger.debug("CTabFolder Item selected");
						CTabItem objSelectedItem = tabFolder.getSelection();
						JadeTreeViewEntry objTVE = (JadeTreeViewEntry) objSelectedItem.getData();
						if (objTVE != null) {
							TreeItem objTI = objTVE.getTreeItem();
							if (objTI != null) {
								treeViewer.getTree().setSelection(objTI);
								treeViewer.setSelection(treeViewer.getSelection(), true);
							}
						}
					}
				});
				sashForm.setWeights(new int[] { 225, 683 });
				sashForm.setVisible(true);
			}
			objPersistenceStore.restoreWindow();
			objPersistenceStore.loadSash(sashForm);
			createMenu();
		}

		return container;
	}

	private ImageDescriptor getImageDescr(final String pstrFileName) {
		return ImageDescriptor.createFromImage(getImage(pstrFileName));
	}

	private Image getImage(final String pstrFileName) {
		return SWTResourceManager.getImageFromResource(pstrFileName);
	}

	private Session	session;

	private SectionsHandler initializeSession(final String pstrJADEConfigurationFileName) {
		SectionsHandler root = null;
		if (session == null) {
			session = new Session(pstrJADEConfigurationFileName);
			root = session.loadJadeConfigurationFile(splash);
		}
		else {
			root = session.getSectionsHandler();
		}
		return root;
	}

	SOSCTabItem	tbtmProfileTab	= null;

	private void createTabFolder(final JadeTreeViewEntry objTreeViewEntry) {
		try (WaitCursor objWC = new WaitCursor()) {
			Globals.globalShell().setRedraw(false);
			tbtmProfileTab = tabFolder.getTabItem(objTreeViewEntry.getName());
			tbtmProfileTab.setText(objTreeViewEntry.getTitle());
			tbtmProfileTab.setData(objTreeViewEntry);

			ControlHelper.objValueChangedListener = objTreeViewEntry;

			MainComposite objMainComposite = new MainComposite(tabFolder, objTreeViewEntry);
			tbtmProfileTab.setImage(getImageFromResource("org/freedesktop/tango/16x16/status/network-idle.png"));
			tbtmProfileTab.setComposite(objMainComposite);
			objMainComposite.createTabItemComposite();
			tbtmProfileTab.setControl(objMainComposite);
			tabFolder.setSelection(tbtmProfileTab);
			//			else {
			//				if (tabFolder.setFocus(objTreeViewEntry) == null) {
			//					SOSCTabItem tbtmProfileTab = tabFolder.getTabItem(objTreeViewEntry.getTitle());
			//					tbtmProfileTab.setText(objTreeViewEntry.getTitle());
			//					tbtmProfileTab.setData(objTreeViewEntry);
			//					ISOSTabItem objComposite = new MainComposite(tabFolder, objTreeViewEntry);
			//					tbtmProfileTab.setComposite(objComposite);
			//					objComposite.createTabItemComposite();
			//					tabFolder.setSelection(tbtmProfileTab);
			//					tabFolder.setRedraw(true);
			//				}
			//			}
		}
		catch (Exception e) {
			new ErrorLog("problem", e);
		}
		finally {
			Globals.globalShell().layout(true, true);
			Globals.globalShell().setRedraw(true);
		}
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		createStatusLineManager();
	}

	/**
	 * Create the toolbar manager.
	 * 
	 * @return the toolbar manager
	 */
	@Override
	protected ToolBarManager createToolBarManager(final int style) {
		return null;
	}

	StatusLineManager	statusLineManager	= null;

	/**
	 * Create the status line manager.
	 * 
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		statusLineManager = new StatusLineManager();
		statusLineManager.setCancelEnabled(true);
		return statusLineManager;
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(final String args[]) {
		try {
			JADEUserInterfaceMain objJADECockPit = new JADEUserInterfaceMain();
			objJADECockPit.setBlockOnOpen(true);
			objJADECockPit.open();
			Display.getCurrent().dispose();
		}
		catch (Exception e) {
			new ErrorLog("problem", e);
		}
	}

	/**
	 * Configure the shell.
	 * 
	 * @param newShell
	 */
	@Override
	protected void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(conAPP_NAME);
		newShell.setBackground(Globals.getCompositeBackground());
		newShell.setImage(SWTResourceManager.getImageFromResource("Jade-Splash-1.png"));
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		objPersistenceStore = new WindowsSaver(this.getClass(), this.getShell(), 940, 600);
		return objPersistenceStore.getWindowSize();
		// return new Point(947, 502);
	}

	private static class Sorter extends ViewerSorter {
		@Override
		public int compare(final Viewer viewer, final Object e1, final Object e2) {
			JadeTreeViewEntry item1 = (JadeTreeViewEntry) e1;
			JadeTreeViewEntry item2 = (JadeTreeViewEntry) e2;
			return item1.getName().toLowerCase().compareTo(item2.getName().toLowerCase());
		}
	}

	//	private Menu				menuBar					= null;
	//	private static Menu			mFile		= null;
	//	private final Menu			submenu		= null;
	//	private final Menu			submenu1	= null;
	//	private final Composite		groupmain	= null;
	/**  */
	private final static String	EMPTY	= "";

	//	private String getMenuText(final String pstrI18NKey, final String pstrAccelerator) {
	//
	//		String strRet = Globals.MsgHandler.newMsg(pstrI18NKey).get();
	//		int intLen = pstrAccelerator.trim().length();
	//		if (intLen > 0) {
	//			if (intLen == 1) {
	//				strRet += "\tCtrl+" + pstrAccelerator;
	//			}
	//			else {
	//				strRet += "\t" + pstrAccelerator;
	//			}
	//		}
	//		return strRet;
	//	} // private String getMenuText
	//
	//	private MenuItem getMenuItem(Menu objMenu, String pstrI18NKey, int intStyle) {
	//		MenuItem objMenuItem = new MenuItem(objMenu, intStyle);
	//		objMenuItem.setEnabled(true);
	//		objMenuItem.setText(getMenuText(pstrI18NKey, ""));
	//		return objMenuItem;
	//	}
	//
	private class actionSave extends MenueActionSave {

		public actionSave() {
			super(null);
		}

		public actionSave(final String pstrMenueTextParameter) {
			super(pstrMenueTextParameter);
		}

		@Override
		public void executeSave() {
			try (WaitCursor objWC = new WaitCursor()) {
				if ((objSelectedTreeItem = getTreeViewEntry()) != null) {
					setStatus("Save File ... " + objSelectedTreeItem.getName());
					objSelectedTreeItem.getSession().saveConfigurationFile();
					setStatus("File " + objSelectedTreeItem.getName() + " saved");

					// reset the dirty flag(s)
				}
			}
			catch (Exception e) {
				new ErrorLog("Problem", e);
			}
		}
	}

	private class actionNewProfile extends MenueActionNewBase implements IDialogActionHandler {

		private JADEOptions	objJO	= null;

		public actionNewProfile(final String pstrMenueTextI18NKey) {
			super(pstrMenueTextI18NKey);
		}

		@Override
		public void run() {
			try (SOSCursor objC = new SOSCursor().showWait()) {
				if ((objSelectedTreeItem = getTreeViewEntry()) != null) {
					DialogAdapter objDA = new DialogAdapter(Display.getCurrent().getActiveShell(), "newJadeProfile");
					// set Action
					objJO = new JADEOptions();
					switch (objSelectedTreeItem.getType()) {
						case IsRoot:
							break;

						case profile:
						case profiles:
						case profiles_root:
							objJO.isFragment.setFalse();
							objDA.open(this, new createNewProfile(objDA.createContents(), objJO));
							break;

						case fragments_root:
						case fragment:
							objJO.isFragment.setTrue();
							objDA.open(this, new createNewProfile(objDA.createContents(), objJO));
							break;

						default:
							break;
					}
				}
			}
			catch (Exception e) {
				new ErrorLog("problem", e);
			}
			finally {
			}
		}

		@Override
		public void doOK(final SOSMenueEvent objE) {

			try (SOSCursor objC = new SOSCursor().showWait()) {
				switch (objE.operation) {
					case show:
						objE.showMenueItem = true;
						break;

					case execute:
						try {
							objE.addMessage(objJO.title.getErrorIfMandatory(true));
							objE.addMessage(objJO.profile.getErrorIfMandatory(true));
							objE.addMessage(objJO.operation.getErrorIfMandatory(true));

							// check if the profile-name is not duplicated

							if (objJO.profile.isDirty()) {
								objE.addMessage(session.checkDuplicateProfileName(objJO.profile));
							}

							// create treeview item
							JSIniFile objIni = session.getObjJadeConfigurationFile();
							JadeTreeViewEntry objTVE = session.newTreeViewEntry(objIni, objJO);
							treeViewer.refresh();
							if (objTVE != null) {
								TreeItem objTI = objTVE.getTreeItem();
								if (objTI != null) {
									treeViewer.setSelection(new StructuredSelection(objTI));
									objTI.setExpanded(true);
								}
							}
							// go to the maintenance window for this profile
							objSelectedTreeItem = objTVE;
							createTabFolder(objTVE);
							objTVE.ValueHasChanged();
						}
						catch (Exception e) {
							objE.addMessage(e.getLocalizedMessage());
							new ErrorLog("problem", e);
						}
						finally {
							objE.doIt = objE.hasMessage() == false;
						}

						break;

					default:
						objE.doIt = false;
						objE.showMenueItem = false;
						break;
				}
			}
			catch (Exception e) {
				// TODO: handle exception
			}
		}

		@Override
		public void doCancel(final SOSMenueEvent objE) {
			switch (objE.operation) {
				case show:
					objE.showMenueItem = true;
					break;

				case execute:
					objE.doIt = true;
					Globals.setStatus("canceled");
					break;

				default:
					objE.doIt = false;
					objE.showMenueItem = false;
					break;
			}
		}
	}

	private class actionNewConfigFile extends MenueActionNewBase {

		public actionNewConfigFile(final String pstrMenueTextI18NKey) {
			super(pstrMenueTextI18NKey);
		}

		@Override
		public void run() {
			try (WaitCursor objWC = new WaitCursor()) {
				SOSFileDialog objFD = new SOSFileDialog("Dialog_L_new_config_File");
				String strSelectedFileName = null;
				if ((strSelectedFileName = objFD.getSelectedFileName()) == null) {
					//		    	cancel
				}
				else {
					if (strSelectedFileName.endsWith(".ini") == false) {
						strSelectedFileName += ".ini";
					}
					SOSOptionOutFileName objOF = new SOSOptionOutFileName(strSelectedFileName);
					if (objOF.JSFile().exists() == false) {
						objOF.JSFile().WriteLine("[globals]") //
								.WriteLine("TEMPDIR=" + System.getProperty("temp.dir"))
								.WriteLine("isFragment=true")
								.WriteLine("; created by JADECockpit")
								.close();
					}
					else {
						// nothing to do, use the file as it is
					}
					ReInitTreeViewer(strSelectedFileName);
				}

			}
			catch (Exception e) {
				new ErrorLog("problem", e);
			}
			finally {
			}
		}
	}

	private void ReInitTreeViewer(final String strSelectedFileName) {
		tabFolder.closeAllTabItems();
		session = null;
		splash = null;
		treeViewer.getTree().setRedraw(false);
		setStatus(String.format("load config file '%1$s'", strSelectedFileName));
		treeViewer.setInput(initializeSession(strSelectedFileName));
		getShell().setText(Globals.gstrApplication + ": " + strSelectedFileName);
		treeViewer.refresh();
		treeViewer.expandAll();
		treeViewer.getTree().setRedraw(true);
		setStatus(String.format("config file '%1$s' loaded", strSelectedFileName));
	}

	private String getIconName(final String pstrIcon) {
		return "org/freedesktop/tango/22x22/" + pstrIcon + ".png";
	}

	private class SOSFileDialog extends FileDialog {

		private String	strSelectedFileName	= "";

		public SOSFileDialog(final String pstrPreferenceStoreKey) {
			super(Display.getCurrent().getActiveShell());

			setText(Globals.gstrApplication + ":" + MsgHandler.newMsg(pstrPreferenceStoreKey));
			setFilterExtensions(new String[] { "*.ini", "*.jadeconf" });
			String strLastDirectoryName = objPersistenceStore.getProperty(pstrPreferenceStoreKey, System.getProperty("user.dir", "c:/temp"));
			setFilterPath(strLastDirectoryName);
			//			objFD.setFilterNames(new String[] { "Textfiles(*.txt)" });
			strSelectedFileName = open();
			if (strSelectedFileName == null) {
				//		    	cancel
			}
			else {
				logger.debug(strSelectedFileName);
				objPersistenceStore.saveProperty(pstrPreferenceStoreKey, strSelectedFileName);
			}
		}

		public String getSelectedFileName() {
			return strSelectedFileName;
		}

		@Override
		protected void checkSubclass() {
			// Disable the check that prevents subclassing of SWT components
		}

	}
	private class actionOpenConfigFile extends MenueActionOpenBase implements IDialogActionHandler {

		public actionOpenConfigFile() {
			super(EMPTY);
		}

		@Override
		protected void executeOpen() {

			SOSFileDialog objFD = new SOSFileDialog("Dialog_L_OpenFileDialog");
			String strSelectedFileName = null;
			if ((strSelectedFileName = objFD.getSelectedFileName()) == null) {
				//		    	cancel
			}
			else {
				try (WaitCursor objWC = new WaitCursor()) {
					ReInitTreeViewer(strSelectedFileName);
				}
				catch (Exception e) {
					new ErrorLog("problem", e);
				}
				finally {
				}
			}
		}

		@Override
		public void doCancel(final SOSMenueEvent pobjMenueEvent) {

		}
	}

	SOSThreadPoolExecutor	objTPE	= new SOSThreadPoolExecutor();

	private class actionExecuteProfile extends MenueActionExecuteBase implements IDialogActionHandler {

		public actionExecuteProfile() {
			super(EMPTY);
		}

		@Override
		protected void execute() {
			try (WaitCursor objWC = new WaitCursor()) {
				if ((objSelectedTreeItem = getTreeViewEntry()) != null) {
					setStatus("Execute ... " + objSelectedTreeItem.getName());
					LogFileComposite objLogFileComposite = new LogFileComposite(tabFolder, objSelectedTreeItem);
					objLogFileComposite.setData(objSelectedTreeItem);
					JADEEngineAdapter objJADEEngine = new JADEEngineAdapter(objSelectedTreeItem, objLogFileComposite);

					Future objF = objTPE.runTask(objJADEEngine);
					//					objF.get();
					try {
						//						objTPE.shutDown();
						//						objTPE.objThreadPool.awaitTermination(1, TimeUnit.DAYS);
						logger.info("transfer complete");
					}
					catch (Exception e) {
						e.printStackTrace();
					}

					//					objJADEEngine.start();
					//					objLogFileComposite.setData(null);
				}
			}
			catch (Exception e) {
				new ErrorLog("Problem", e);
			}
		}

		@Override
		public void doCancel(final SOSMenueEvent pobjMenueEvent) {

		}
	}

	//	Action			actionOpen			= new actionOpen();

	//	final Action	actExecute			= new Action("", getImageDescr("ExecuteProject.gif")) {
	//											@Override
	//											public void run() {
	//												try (WaitCursor objWC = new WaitCursor()) {
	//													if ((objSelectedTreeItem = getTreeViewEntry()) != null) {
	//														setStatus("Execute ... " + objSelectedTreeItem.getName());
	//														LogFileComposite objLogFileComposite = new LogFileComposite(tabFolder, objSelectedTreeItem);
	//														objLogFileComposite.setData(objSelectedTreeItem);
	//														JADEEngineAdapter objJADEEngine = new JADEEngineAdapter();
	//														objJADEEngine.Execute(objSelectedTreeItem, objLogFileComposite);
	//													}
	//												}
	//												catch (Exception e) {
	//													new ErrorLog("Problem", e);
	//												}
	//											}
	//										};
	//
	final Action		actionBrowseSource	= new Action("", getImageDescr("Document.gif")) {
												@Override
												public void run() {
													try (SOSCursor objC = new SOSCursor().showWait()) {
														if ((objSelectedTreeItem = getTreeViewEntry()) != null) {
															setStatus("Browse Source ... " + objSelectedTreeItem.getName());
															SourceBrowserComposite objSourceBrowserComposite = new SourceBrowserComposite(tabFolder,
																	objSelectedTreeItem);
															objSourceBrowserComposite.setData(objSelectedTreeItem);
														}
													}
													catch (Exception e) {
														new ErrorLog("Problem", e);
													}
												}
											};

	final Action		actCopy				= new Action("Copy", getImageDescr(getIconName("actions/edit-copy"))) {
												@Override
												public void run() {
													try (SOSCursor objC = new SOSCursor().showWait()) {

													}
													catch (Exception e) {
														new ErrorLog("problem", e);
													}
													finally {
													}
													// showMessage("Copy");
												}
											};
	final Action		actDelete			= new Action("Delete", getImageDescr(getIconName("actions/edit-delete"))) {
												@Override
												public void run() {
													try (SOSCursor objC = new SOSCursor().showWait()) {

													}
													catch (Exception e) {
														new ErrorLog("problem", e);
													}
													finally {
													}
													// showMessage("Delete");
												}
											};
	final Action		actRename			= new Action("Rename", getImageDescr("edit-rename.png")) {
												@Override
												public void run() {
													try (SOSCursor objC = new SOSCursor().showWait()) {

													}
													catch (Exception e) {
														new ErrorLog("problem", e);
													}
													finally {
													}
													// showMessage("Rename");
												}
											};
	final Action		actPaste			= new Action("Paste") {
												@Override
												public void run() {
													try (SOSCursor objC = new SOSCursor().showWait()) {

													}
													catch (Exception e) {
														new ErrorLog("problem", e);
													}
													finally {
													}
													// showMessage("Paste");
												}
											};
	final Action		actCreateScript		= new Action("Script") {
												@Override
												public void run() {
													try (SOSCursor objC = new SOSCursor().showWait()) {

													}
													catch (Exception e) {
														new ErrorLog("problem", e);
													}
													finally {
													}
													// showMessage("Script");
												}
											};
	final Action		actCreateJobChain	= new Action("JobChain") {
												@Override
												public void run() {
													try (SOSCursor objC = new SOSCursor().showWait()) {

													}
													catch (Exception e) {
														new ErrorLog("problem", e);
													}
													finally {
													}
													// showMessage("JobChain");
												}
											};

	//	Action			actionExit			= new MenueActionExit();

	final actionSave	actSave				= new actionSave();
	final Action		actSaveAs			= new Action("SaveAs", getImageDescr("SaveAs.gif")) {
												@Override
												public void run() {
													try (SOSCursor objC = new SOSCursor().showWait()) {
														if ((objSelectedTreeItem = getTreeViewEntry()) != null) {
															setStatus("Save Source ... " + objSelectedTreeItem.getName());
															objSelectedTreeItem.getSession().saveConfigurationFile();
														}
													}
													catch (Exception e) {
														e.printStackTrace();
													}
												}
											};

	//	private Shell	sShell;

	private JadeTreeViewEntry getTreeViewEntry() {
		JadeTreeViewEntry lobjSelectedSection = null;
		try (WaitCursor objWC = new WaitCursor()) {
			IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
			Object item = selection.getFirstElement();
			if (item instanceof JadeTreeViewEntry) {
				lobjSelectedSection = (JadeTreeViewEntry) item;
			}

		}
		catch (Exception e) {
			new ErrorLog("problem", e);
		}
		finally {
		}
		return lobjSelectedSection;
	}

	private void createMenu() {
		final Shell sShell = this.getShell();

		try (SOSCursor objC = new SOSCursor().showWait()) {
			MenuManager barMenuManager = new MenuManager();

			MenuManager fileMenuManager = new MenuManager("&File");
			barMenuManager.add(fileMenuManager);

			fileMenuManager.add(new actionNewConfigFile(null));
			fileMenuManager.add(new actionOpenConfigFile());
			fileMenuManager.add(actSave);
			fileMenuManager.add(new Separator());

			fileMenuManager.add(new MenueActionExit());

			MenuManager optionsMenuManager = new MenuManager("&Options");
			barMenuManager.add(optionsMenuManager);
			optionsMenuManager.add(actSave);
			optionsMenuManager.add(new Separator());

			MenuManager helpMenuManager = new MenuManager("&Help");
			barMenuManager.add(helpMenuManager);
			helpMenuManager.add(actSave);
			helpMenuManager.add(new Separator());

			barMenuManager.updateAll(true);
			sShell.setMenuBar(barMenuManager.createMenuBar((Decorations) sShell));

		}
		catch (Exception e) {
			new ErrorLog("problem", e);
		}
		finally {
		}
	}
}
