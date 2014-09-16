package com.sos.jade.userinterface;

import static com.sos.dialog.Globals.MsgHandler;

import java.net.URL;

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
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchActionConstants;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.dialog.Globals;
import com.sos.dialog.classes.SOSCTabFolder;
import com.sos.dialog.classes.SOSCTabItem;
import com.sos.dialog.classes.SOSComposite;
import com.sos.dialog.classes.WindowsSaver;
import com.sos.dialog.interfaces.ISOSTabItem;
import com.sos.dialog.swtdesigner.SWTResourceManager;
import com.sos.jade.userinterface.adapters.SOSDataExchangeAdapter;
import com.sos.jade.userinterface.composite.CompositeBaseClass;
import com.sos.jade.userinterface.composite.LogFileComposite;
import com.sos.jade.userinterface.composite.MainComposite;
import com.sos.jade.userinterface.composite.SourceBrowserComposite;
import com.sos.jade.userinterface.data.JadeTreeViewEntry;
import com.sos.jade.userinterface.data.SectionsHandler;
import com.sos.jade.userinterface.data.Session;
import com.sos.jade.userinterface.globals.JADEMsg;

public class JADEUserInterfaceMain extends ApplicationWindow {
	private final Logger		logger				= Logger.getLogger(JADEUserInterfaceMain.class);
	public final String			conSVNVersion		= "$Id$";
	@SuppressWarnings("unused")
	private final JADEOptions	objJadeOptions		= new JADEOptions();
	private WindowsSaver		objPersistenceStore;
	private SOSCTabFolder		tabFolder			= null;
	private JadeTreeViewEntry	objSelectedSection	= null;
	private TreeViewer			treeViewer			= null;

	/**
	 * Create the application window.
	 */
	@SuppressWarnings("deprecation")
	public JADEUserInterfaceMain() {
		super(null);
		Display display = Display.getCurrent();
		BasicConfigurator.configure();
		RootLogger.getRoot().setLevel(Level.DEBUG);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addStatusLine();
	}

	/**
	 * Create contents of the application window.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createContents(final Composite parent) {
		Globals.stFontRegistry.put("button-text", new FontData[] { new FontData("Arial", 9, SWT.BOLD) });
		Globals.stFontRegistry.put("code", new FontData[] { new FontData("Courier New", 10, SWT.NORMAL) });
		Globals.stFontRegistry.put("text", new FontData[] { new FontData("Arial", 10, SWT.NORMAL) });
		Globals.stFontRegistry.put("tabitem-text", new FontData[] { new FontData("", 9, SWT.NORMAL) });

		Display display = parent.getDisplay();
		assert display != null;
		objPersistenceStore = new WindowsSaver(this.getClass(), this.getShell(), 940, 600);

		Globals.stColorRegistry.put("IncludedOption", display.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW).getRGB());
		Globals.stColorRegistry.put("MandatoryFieldColor", display.getSystemColor(SWT.COLOR_BLUE).getRGB());
		Globals.stColorRegistry.put("Color4FieldHasFocus", display.getSystemColor(SWT.COLOR_GREEN).getRGB());

		// Colorschema

		Globals.stColorRegistry.put("FieldBackGround", new RGB(220, 249, 0)); // var.
		Globals.stColorRegistry.put("Color4FieldHasFocus", new RGB(124, 231, 0)); // var. 1 =
		Globals.stColorRegistry.put("Color4FieldInError", new RGB(255, 225, 0)); // var.

		// Globals.stColorRegistry.put("CompositeBackGround", new
		// RGB(236,252,113)); // var. 5 = #ECFC71 = rgb(236,252,113)
		// var. 5 = #FFFFB0 = rgb(255,255,176)
		Globals.stColorRegistry.put("CompositeBackGround", new RGB(255, 255, 176)); //

		Globals.setApplicationWindow(this);
		Globals.MsgHandler = new JADEMsg("init");
		parent.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(final DisposeEvent arg0) {
				logger.debug("disposed");
				objPersistenceStore.saveWindowPosAndSize();
			}
		});
		parent.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent e) {
				logger.debug("control resized");
				objPersistenceStore.saveWindowPosAndSize();
			}
		});
		setStatus(" ");
		Composite container = new SOSComposite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		{
			// new ToolBar(container, SWT.FLAT | SWT.RIGHT);
		}
		{
			final SashForm sashForm = new SashForm(container, SWT.BORDER | SWT.SMOOTH);
			sashForm.addControlListener(new ControlAdapter() {
				@Override
				public void controlResized(final ControlEvent e) {
					logger.debug("sashForm resized");
					// int[] weights = sashForm.getWeights();
					// Store your weights here
					// System.out.println("Weights: " + weights[0] + " " +
					// weights[1]);
				}
			});
			sashForm.setSashWidth(5);
			GridData gd_sashForm = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
			gd_sashForm.heightHint = 180;
			gd_sashForm.widthHint = 422;
			sashForm.setLayoutData(gd_sashForm);
			{
				treeViewer = new TreeViewer(sashForm, SWT.BORDER + SWT.MULTI);
				Tree tree = treeViewer.getTree();
				tree.setSortDirection(SWT.DOWN);
				tree.setBackground(Globals.getCompositeBackground());

				// Create the popup menu
				final MenuManager menuPopUpMenueMgr = new MenuManager("#PopupMenu");
				Menu menu = menuPopUpMenueMgr.createContextMenu(treeViewer.getControl());
				// getSite().registerContextMenu(menuMgr, treeViewer);
				menuPopUpMenueMgr.addMenuListener(new IMenuListener() {
					@Override
					public void menuAboutToShow(final IMenuManager manager) {
						if (treeViewer.getSelection().isEmpty()) {
							return;
						}
						if (treeViewer.getSelection() instanceof ISelection) {
							JadeTreeViewEntry objSelectedSection1 = null;
							if ((objSelectedSection1 = getTreeViewEntry()) != null) {
								String strCaption = objSelectedSection1.getTitle();
								if (objSelectedSection1.isExecutable()) {
									actExecute.setText(MsgHandler.newMsg("treenode_menu_Execute").params(strCaption));
									menuPopUpMenueMgr.add(actExecute);
								}
								actionBrowseSource.setText(MsgHandler.newMsg("treenode_menu_BrowseSource").params(strCaption));
								menuPopUpMenueMgr.add(actionBrowseSource);

								menuPopUpMenueMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

								actCopy.setText(MsgHandler.newMsg("treenode_menu_Copy").params(strCaption));
								actCopy.setImageDescriptor(getImage("Copy.gif"));
								menuPopUpMenueMgr.add(actCopy);

								actionSave.setText(MsgHandler.newMsg("treenode_menu_Save").params(strCaption));
								menuPopUpMenueMgr.add(actionSave);

								actSaveAs.setText(MsgHandler.newMsg("treenode_menu_SaveAs").params(strCaption));
								menuPopUpMenueMgr.add(actSaveAs);

								actDelete.setText(MsgHandler.newMsg("treenode_menu_Delete").params(strCaption));
								actDelete.setImageDescriptor(getImage("Delete.gif"));
								menuPopUpMenueMgr.add(actDelete);

								actRename.setText(MsgHandler.newMsg("treenode_menu_Rename").params(strCaption));
								actRename.setImageDescriptor(getImage("Rename.gif"));
								menuPopUpMenueMgr.add(actRename);

								actPaste.setText(MsgHandler.newMsg("treenode_menu_Paste").params(strCaption));
								actPaste.setImageDescriptor(getImage("DocumentIn.gif"));
								menuPopUpMenueMgr.add(actPaste);

								menuPopUpMenueMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
								actNew.setText(MsgHandler.newMsg("treenode_menu_New").params(strCaption));
								menuPopUpMenueMgr.add(actNew);
								actNew.setImageDescriptor(getImage("New.gif"));
								if (objSelectedSection1.isSourceGen()) {
									menuPopUpMenueMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

									MenuManager subMenu = new MenuManager(MsgHandler.newMsg("treenode_menu_Create").label(), null);
									actCreateJobChain.setText(MsgHandler.newMsg("treenode_menu_JobChain").params(strCaption));
									actCreateJobChain.setImageDescriptor(getImage("Chain.gif"));
									subMenu.add(actCreateJobChain);

									actCreateScript.setText(MsgHandler.newMsg("treenode_menu_Script").params(strCaption));
									actCreateScript.setImageDescriptor(getImage("Document.gif"));
									subMenu.add(actCreateScript);

									menuPopUpMenueMgr.add(subMenu);
								}
							}
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
				});
				treeViewer.setExpandPreCheckFilters(false);
				treeViewer.setLabelProvider(new TreeLabelProvider());
				treeViewer.setContentProvider(new TreeContentProvider());
				treeViewer.setSorter(new Sorter());
				treeViewer.setInput(initializeSession());
				treeViewer.expandAll();
				tabFolder = new SOSCTabFolder(sashForm, SWT.BORDER);
				sashForm.setWeights(new int[] { 225, 683 });
				sashForm.setVisible(true);
			}
		}
		createMenu();
		objPersistenceStore.restoreWindow();

		return container;
	}

	private ImageDescriptor getImage(final String pstrFileName) {
		URL url = Thread.currentThread().getContextClassLoader().getResource(pstrFileName);
		if (url != null) {
			return ImageDescriptor.createFromImage(SWTResourceManager.getImage(url.getPath()));
		}
		else {
			return ImageDescriptor.createFromImage(SWTResourceManager.getImage(pstrFileName));
		}
	}

	private Session	session;

	private SectionsHandler initializeSession() {
		SectionsHandler root = null;
		if (session == null) {
			session = new Session();
			root = session.loadJadeConfigurationFile();
		}
		else {
			root = session.getSectionsHandler();
		}
		return root;
	}

	SOSCTabItem	tbtmProfileTab	= null;

	private void createTabFolder(final JadeTreeViewEntry objTreeViewEntry) {
		try {
			if (CompositeBaseClass.gflgCreateControlsImmediate == true) {
				//				if (tbtmProfileTab == null) {
				tbtmProfileTab = tabFolder.getTabItem(objTreeViewEntry.getName());
				tbtmProfileTab.setText(objTreeViewEntry.getTitle());
				tbtmProfileTab.setData(objTreeViewEntry);

				ControlHelper.objValueChangedListener = objTreeViewEntry;
//				Composite composite = new SOSComposite(tabFolder, SWT.H_SCROLL + SWT.V_SCROLL );
//				tbtmProfileTab.setControl(composite);
//				// tbtmProfileTab.setData("composite", composite);
//				Gridlayout.set4ColumnLayout(composite);

				ISOSTabItem objComposite = new MainComposite(tbtmProfileTab, objTreeViewEntry);
				tbtmProfileTab.setImage(SWTResourceManager.getImage("Profil.gif"));
				tbtmProfileTab.setComposite(objComposite);
				objComposite.createTabItemComposite();
				tabFolder.setSelection(tbtmProfileTab);
				tabFolder.setRedraw(true);
			}
			else {
				if (tabFolder.setFocus(objTreeViewEntry) == null) {
					SOSCTabItem tbtmProfileTab = tabFolder.getTabItem(objTreeViewEntry.getTitle());
					tbtmProfileTab.setText(objTreeViewEntry.getTitle());
					tbtmProfileTab.setData(objTreeViewEntry);
					ISOSTabItem objComposite = new MainComposite(tbtmProfileTab, objTreeViewEntry);
					tbtmProfileTab.setComposite(objComposite);
					objComposite.createTabItemComposite();
					tabFolder.setSelection(tbtmProfileTab);
					tabFolder.setRedraw(true);
				}
			}
			tabFolder.layout();
		}
		catch (Exception e) {
			e.printStackTrace();
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

	/**
	 * Create the status line manager.
	 * 
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		statusLineManager.setCancelEnabled(true);
		//		statusLineManager.getControl().setBackground(Globals.getCompositeBackground());
		return statusLineManager;
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(final String args[]) {
		try {
			JADEUserInterfaceMain window = new JADEUserInterfaceMain();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		}
		catch (Exception e) {
			e.printStackTrace();
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
		newShell.setText("JADEUserInterface");
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
	private static Menu			mFile		= null;
	private final Menu			submenu		= null;
	private final Menu			submenu1	= null;
	private final Composite		groupmain	= null;
	/**  */
	private final static String	EMPTY		= "";

	private String getMenuText(final String pstrI18NKey, final String pstrAccelerator) {

		String strRet = Globals.MsgHandler.newMsg(pstrI18NKey).get();
		int intLen = pstrAccelerator.trim().length();
		if (intLen > 0) {
			if (intLen == 1) {
				strRet += "\tCtrl+" + pstrAccelerator;
			}
			else {
				strRet += "\t" + pstrAccelerator;
			}
		}
		return strRet;
	} // private String getMenuText

	private MenuItem getMenuItem(Menu objMenu, String pstrI18NKey, int intStyle) {
		MenuItem objMenuItem = new MenuItem(objMenu, intStyle);
		objMenuItem.setEnabled(true);
		objMenuItem.setText(getMenuText(pstrI18NKey, ""));
		return objMenuItem;
	}

	Action			actionExit			= new Action("&Exit@Ctrl+X", getImage("exit.gif")) {
											@Override
											public void run() {
												objPersistenceStore.saveWindowPosAndSize();
												Shell sShell = Display.getCurrent().getActiveShell();
												sShell.close();
//												sShell.dispose();
											}
										};

	Action			actionNew			= new Action("&New@Ctrl+N", getImage("new.gif")) {
											@Override
											public void run() {
											}
										};

	Action			actionOpen			= new Action("&Open@Ctrl+O", getImage("open.gif")) {
											@Override
											public void run() {
											}
										};

	final Action	actExecute			= new Action("", getImage("ExecuteProject.gif")) {
											@Override
											public void run() {
												try {
													if ((objSelectedSection = getTreeViewEntry()) != null) {
														setStatus("Execute ... " + objSelectedSection.getName());
														LogFileComposite objLogFileComposite = new LogFileComposite(tabFolder, objSelectedSection);
														objLogFileComposite.setData(objSelectedSection);
														SOSDataExchangeAdapter objDEx = new SOSDataExchangeAdapter();
														objDEx.Execute(objSelectedSection, objLogFileComposite);
													}
												}
												catch (Exception e) {
													e.printStackTrace();
												}
											}
										};

	final Action	actionBrowseSource	= new Action("", getImage("Document.gif")) {
											@Override
											public void run() {
												try {
													if ((objSelectedSection = getTreeViewEntry()) != null) {
														setStatus("Browse Source ... " + objSelectedSection.getName());
														SourceBrowserComposite objSourceBrowserComposite = new SourceBrowserComposite(tabFolder,
																objSelectedSection);
														objSourceBrowserComposite.setData(objSelectedSection);
													}
												}
												catch (Exception e) {
													e.printStackTrace();
												}
											}
										};

	final Action	actCopy				= new Action("Copy") {
											@Override
											public void run() {
												// showMessage("Copy");
											}
										};
	final Action	actDelete			= new Action("Delete") {
											@Override
											public void run() {
												// showMessage("Delete");
											}
										};
	final Action	actRename			= new Action("Rename") {
											@Override
											public void run() {
												// showMessage("Rename");
											}
										};
	final Action	actPaste			= new Action("Paste") {
											@Override
											public void run() {
												// showMessage("Paste");
											}
										};
	final Action	actNew				= new Action("New") {
											@Override
											public void run() {
												// showMessage("New");
											}
										};
	final Action	actCreateScript		= new Action("Script") {
											@Override
											public void run() {
												// showMessage("Script");
											}
										};
	final Action	actCreateJobChain	= new Action("JobChain") {
											@Override
											public void run() {
												// showMessage("JobChain");
											}
										};

	final Action	actionSave			= new Action("&Save\tCtrl+S", getImage("save.gif")) {
											@Override
											public void run() {
												try {
													if ((objSelectedSection = getTreeViewEntry()) != null) {
														setStatus("Save Source ... " + objSelectedSection.getName());
														objSelectedSection.getSession().saveConfigurationFile();
													}
												}
												catch (Exception e) {
													e.printStackTrace();
												}
											}
										};
	final Action	actSaveAs			= new Action("SaveAs", getImage("SaveAs.gif")) {
											@Override
											public void run() {
												try {
													if ((objSelectedSection = getTreeViewEntry()) != null) {
														setStatus("Save Source ... " + objSelectedSection.getName());
														objSelectedSection.getSession().saveConfigurationFile();
													}
												}
												catch (Exception e) {
													e.printStackTrace();
												}
											}
										};

	private Shell	sShell;

	private JadeTreeViewEntry getTreeViewEntry() {
		JadeTreeViewEntry lobjSelectedSection = null;
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		Object item = selection.getFirstElement();
		if (item instanceof JadeTreeViewEntry) {
			lobjSelectedSection = (JadeTreeViewEntry) item;
		}
		return lobjSelectedSection;
	}

	private void createMenu() {
		final Shell sShell = this.getShell();

		MenuManager barMenuManager = new MenuManager();

		MenuManager fileMenuManager = new MenuManager("&File");

		barMenuManager.add(fileMenuManager);

		fileMenuManager.add(actionNew);
		fileMenuManager.add(actionOpen);
		fileMenuManager.add(actionSave);
		fileMenuManager.add(new Separator());

		fileMenuManager.add(actionExit);

		MenuManager optionsMenuManager = new MenuManager("&Options");
		barMenuManager.add(optionsMenuManager);
		optionsMenuManager.add(actionSave);
		optionsMenuManager.add(new Separator());

		MenuManager helpMenuManager = new MenuManager("&Help");
		barMenuManager.add(helpMenuManager);
		helpMenuManager.add(actionSave);
		helpMenuManager.add(new Separator());

		barMenuManager.updateAll(true);
		sShell.setMenuBar(barMenuManager.createMenuBar((Decorations) sShell));

	}

}
