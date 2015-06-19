package com.sos.jade.backgroundservice.view.components.filter;

import static com.sos.jade.backgroundservice.JADEHistoryViewerUI.jadeBsOptions;
import static com.sos.jade.backgroundservice.JADEHistoryViewerUI.parentNodeName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sos.jadehistory.JadeFilesHistoryFilter;

import com.sos.jade.backgroundservice.JADEHistoryViewerUI;
import com.sos.jade.backgroundservice.constants.JadeBSConstants;
import com.sos.jade.backgroundservice.enums.JadeFileColumns;
import com.sos.jade.backgroundservice.enums.JadeHistoryFileColumns;
import com.sos.jade.backgroundservice.listeners.IJadeFileListener;
import com.sos.jade.backgroundservice.listeners.impl.JadeFileListenerProxy;
import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.sos.jade.backgroundservice.view.MainView;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * A FilterLayout with pre-configured Components, the Layout extends Vaadins {@link com.vaadin.ui.VerticalLayout VerticalLayout}
 * 
 * @author SP
 *
 */
public class JadeFilesHistoryFilterLayout extends VerticalLayout implements Serializable{
	private static final long serialVersionUID = 1L;
	private static final String STATUS_SUCCESS = "success";
	private static final String STATUS_ERROR = "error";
	private static final String OPERATION_SEND = "send";
	private static final String OPERATION_RECEIVE = "receive";
	private static final String OPERATION_COPY = "copy";
	private static final String PROTOCOL_FTP = "ftp";
	private static final String PROTOCOL_SFTP = "sftp";
  private static final String PROTOCOL_LOCAL = "local";
  private static final String PROTOCOL_HTTP = "http";
  private static final String PROTOCOL_HTTPS = "https";
  private static final String PROTOCOL_WEBDAV = "webdav";
	private static final String MESSAGE_RESOURCE_BASE = "JadeMenuBar.";
	private static final float DROPDOWN_WIDTH = 75.0f;
	@SuppressWarnings("unused")
	private static final String MESSAGE_RESOURCE_FILE = "file.";
	@SuppressWarnings("unused")
	private static final String MESSAGE_RESOURCE_HISTORY = "fileHistory.";
	private VerticalLayout vlMain;
	private Date timestampFrom;
	private Date timestampTo;
	private DateField dfTimestampFrom;
	private DateField dfTimestampTo;
	private String mandator;
	private String sourceFile;
	private String sourceHost;
	private String targetFile;
	private String targetHost;
	private TextField tfMandator;
	private TextField tfSourceFile;
	private TextField tfSourceHost;
	private TextField tfTargetFile;
	private TextField tfTargetHost;
//	private TextField tfProtocol;
	private NativeSelect nsStatus;
	private NativeSelect nsOperation;
	private NativeSelect nsProtocol;
	private Button btnCommit;
	private Button btnDiscard;
	private Preferences prefs = jadeBsOptions.getPreferenceStore();
	@SuppressWarnings("unused")
	private JadeFilesHistoryFilter lastFilter;
	private Logger log = LoggerFactory.getLogger(JadeFilesHistoryFilterLayout.class);
	
	private JadeBSMessages messages;

	@SuppressWarnings("unused")
	private static final long oneDay = 24 * 60 * 60 * 1000;
	private MainView mainView;
	
	public JadeFilesHistoryFilterLayout(){
		super();
		this.mainView = getMainViewFromCurrentUI();
		this.messages = mainView.getMessages();
		this.setSizeFull();
		this.setMargin(true);
		this.focus();
		initJadeFilesHistoryFilterComponents();
		checkLastFilterSettings();
	}
	
	/**
	 * helper method to create a pre configured {@link com.vaadin.ui.HorizontalLayout HorizontalLayout}
	 * 
	 * @return the created {@link com.vaadin.ui.HorizontalLayout HorizontalLayout}
	 */
	private HorizontalLayout initHLayout(){
		HorizontalLayout hl = new HorizontalLayout();
		hl.setHeight(50.0f, Unit.PIXELS);
		hl.setWidth(300.0f, Unit.PIXELS);
		return hl;
	}
	
	/**
	 * helper method to create a pre configured {@link com.vaadin.ui.DateField DateField}
	 * 
	 * @return the created {@link com.vaadin.ui.DateField DateField}
	 */
	private DateField initDateField(String caption, Date date){
		DateField df = new DateField(caption, date);
		df.setSizeFull();
		return df;
	}
	
	/**
	 * helper method to create a pre configured {@link com.vaadin.ui.TextField TextField}
	 * 
	 * @return the created {@link com.vaadin.ui.TextField TextField}
	 */
	private TextField initTextField(String caption, String text){
		TextField tf = new TextField(caption, text);
		tf.setHeight(23.0f, Unit.PIXELS);
		tf.setWidth("100%");
		tf.setInputPrompt(caption);
		tf.setValue("");
		return tf;
	}

	private NativeSelect initNativeSelect(String caption, List<String> valueList){
		NativeSelect ns = new NativeSelect(caption, valueList);
		ns.setWidth(DROPDOWN_WIDTH, Unit.PIXELS);
		return ns;
	}
	
	private void checkTextFieldValues(){
		if("".equals(mandator)){
			mandator = null;
		}
		if("".equals(sourceFile)){
			sourceFile = null;
		}
		if("".equals(sourceHost)){
			sourceHost = null;
		}
		if("".equals(targetFile)){
			targetFile = null;
		}
		if("".equals(targetHost)){
			targetHost = null;
		}
	}


	/**
	 * tells the {@link IJadeFileListener} to filter the JadeFilesHistoryDBItems with the given
	 * JadeFilesHistoryFilter and populates the {@link com.sos.jade.backgroundservice.view.components.JadeFileHistoryTable JadeFileHistoryTable} with the filtered data
	 * 
	 * @param listener the {@link IJadeFileListener} which holds the methods to access the JadeFilesHistoryDBLayer
	 * @param historyFilter the JadeFilesHistoryFilter to filter JadeFilesHistoryDBItems and the related JadeFilesDBItem with
	 */
	private void filterData(final IJadeFileListener listener, final JadeFilesHistoryFilter historyFilter) {
		
		new Thread() {
			@Override
	        public void run() {
	            try {
	            	mainView.getProgress().setVisible(true);
	            	listener.filterJadeFilesHistory(historyFilter);
	            } catch (final Exception e) {
	                listener.getException(e);
	            }
				UI.getCurrent().access(new Runnable() {
					@Override
					public void run() {
						if(mainView == null){
							mainView = getMainViewFromCurrentUI();
						}
				        mainView.getTblDetails().setVisible(false);
				        mainView.getTblFileHistory().populateDatasource(mainView.getHistoryItems());
				        mainView.getTblFileHistory().markAsDirty();
				        mainView.getLblEntryCount().setValue((messages.getValue("MainView.entryCount", mainView.getCurrentLocale()) + " " + mainView.getHistoryItems().size()));
						listener.closeJadeFilesHistoryDbSession();
						mainView.getProgress().setPrimaryStyleName("jadeProgressBar");
				        mainView.getProgress().setVisible(false);
					}
				});
	        }
		}.start();
    }
	
	/**
	 * initializes the {@link JadeFilesHistoryFilterLayout} with its components
	 * 
	 */
	private void initJadeFilesHistoryFilterComponents(){
		vlMain = new VerticalLayout();
		vlMain.setHeight(250.0f, Unit.PIXELS);
		addComponent(vlMain);

		HorizontalLayout hlFirst = initHLayout();
		vlMain.addComponent(hlFirst);
		HorizontalLayout hlSecond = initHLayout();
		vlMain.addComponent(hlSecond);
		HorizontalLayout hlThird = initHLayout();
		vlMain.addComponent(hlThird);
		HorizontalLayout hlForth = initHLayout();
		vlMain.addComponent(hlForth);
		HorizontalLayout hlFifth = initHLayout();
		vlMain.addComponent(hlFifth);
		HorizontalLayout hlButtons = initHLayout();
		vlMain.addComponent(hlButtons);
		
		dfTimestampFrom = initDateField(messages.getValue("FilterLayout.from"), timestampFrom);
		dfTimestampTo = initDateField(messages.getValue("FilterLayout.to"), timestampTo);
		tfMandator = initTextField(messages.getValue(MESSAGE_RESOURCE_BASE + JadeFileColumns.MANDATOR.getName()), mandator);
		tfSourceFile = initTextField(messages.getValue(MESSAGE_RESOURCE_BASE + JadeFileColumns.SOURCE_FILENAME.getName()), sourceFile);
		tfSourceHost = initTextField(messages.getValue(MESSAGE_RESOURCE_BASE + JadeFileColumns.SOURCE_HOST.getName()), sourceHost);
		tfTargetFile = initTextField(messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.TARGET_FILENAME.getName()), targetFile);
		tfTargetHost = initTextField(messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.TARGET_HOST.getName()), targetHost);
		List<String> statusList = new ArrayList<String>();
		statusList.add(STATUS_SUCCESS);
		statusList.add(STATUS_ERROR);
		nsStatus = initNativeSelect(messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.STATUS.getName()), statusList);
		List<String> operationList = new ArrayList<String>();
		operationList.add(OPERATION_COPY);
		operationList.add(OPERATION_SEND);
		operationList.add(OPERATION_RECEIVE);
		nsOperation = initNativeSelect(messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.OPERATION.getName()), operationList);
		List<String> protocolList = new ArrayList<String>();
		protocolList.add(PROTOCOL_FTP);
		protocolList.add(PROTOCOL_SFTP);
    protocolList.add(PROTOCOL_HTTP);
    protocolList.add(PROTOCOL_HTTPS);
    protocolList.add(PROTOCOL_WEBDAV);
    protocolList.add(PROTOCOL_LOCAL);
		nsProtocol = initNativeSelect(messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.PROTOCOL.getName()), protocolList);
		btnCommit = new Button(messages.getValue("FilterLayout.ok"));
		btnDiscard = new Button(messages.getValue("FilterLayout.discard"));

		hlFirst.addComponents(tfMandator, tfSourceFile);
		hlFirst.setExpandRatio(tfMandator, 1);
		hlFirst.setExpandRatio(tfSourceFile, 1);
		hlSecond.addComponents(nsStatus, tfTargetFile);
		hlSecond.setExpandRatio(nsStatus, 1);
		hlSecond.setExpandRatio(tfTargetFile, 1);
		hlThird.addComponents(nsOperation, tfSourceHost);
		hlThird.setExpandRatio(nsOperation, 1);
		hlThird.setExpandRatio(tfSourceHost, 1);
		hlForth.addComponents(nsProtocol, tfTargetHost);
		hlForth.setExpandRatio(nsProtocol, 1);
		hlForth.setExpandRatio(tfTargetHost, 1);
		hlFifth.addComponents(dfTimestampFrom, dfTimestampTo);
		hlFifth.setExpandRatio(dfTimestampFrom, 1);
		hlFifth.setExpandRatio(dfTimestampTo, 1);
		hlButtons.addComponents(btnDiscard, btnCommit);
		hlButtons.setComponentAlignment(btnDiscard, Alignment.MIDDLE_LEFT);
		hlButtons.setComponentAlignment(btnCommit, Alignment.MIDDLE_LEFT);
		btnCommit.setClickShortcut(KeyCode.ENTER, null);
		btnDiscard.setClickShortcut(KeyCode.ESCAPE, null);
		btnCommit.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				if(mainView == null){
					mainView = getMainViewFromCurrentUI();
				}
				mainView.setMarkedRow(null);
				mainView.setDetailViewVisible(false);
				mainView.toggleTableVisiblity(null);
				mainView.getProgress().setVisible(true);
				mainView.getJmb().getSmDuplicatesFilter().setChecked(false);
				checkTextFieldValues();
				saveFilterPreferences();
				((FilterLayoutWindow)JadeFilesHistoryFilterLayout.this.getParent()).close();
				filterData(new JadeFileListenerProxy(mainView), createJadeFilesHistoryFilter());
			}
		});
		btnDiscard.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				((FilterLayoutWindow)JadeFilesHistoryFilterLayout.this.getParent()).close();
			}
		});
	}
	
	private JadeFilesHistoryFilter createJadeFilesHistoryFilter(){
		JadeFilesHistoryFilter filter = new JadeFilesHistoryFilter();
		filter.setTransferTimestampFrom(dfTimestampFrom.getValue());
		filter.setTransferTimestampTo(dfTimestampTo.getValue());
		if(nsProtocol.getValue() != null && !"".equals(nsProtocol.getValue()))
			filter.setProtocol(nsProtocol.getValue().toString());
		if(nsStatus.getValue() != null && !"".equals(nsStatus.getValue()))
			filter.setStatus(nsStatus.getValue().toString());
		if(nsOperation.getValue() != null && !"".equals(nsOperation.getValue()))
			filter.setOperation(nsOperation.getValue().toString());
		filter.setSourceFile(tfSourceFile.getValue());
		filter.setSourceHost(tfSourceHost.getValue());
		filter.setTargetFilename(tfTargetFile.getValue());
		filter.setTargetHost(tfTargetHost.getValue());
		filter.setMandator(tfMandator.getValue());
		return filter;
	}
	
	private void saveFilterPreferences(){
		if(dfTimestampFrom.getValue() != null){
			prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
			.putLong(JadeBSConstants.FILTER_OPTION_TRANSFER_TIMESTAMP_FROM, dfTimestampFrom.getValue().getTime());
		}
		if(dfTimestampTo.getValue() != null){
			prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
			.putLong(JadeBSConstants.FILTER_OPTION_TRANSFER_TIMESTAMP_TO, dfTimestampTo.getValue().getTime());
		}
		if(nsProtocol.getValue() != null && !"".equals(nsProtocol.getValue())){
			prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
			.put(JadeBSConstants.FILTER_OPTION_PROTOCOL, nsProtocol.getValue().toString());
		}
		if(nsStatus.getValue() != null && !"".equals(nsStatus.getValue())){
			prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
			.put(JadeBSConstants.FILTER_OPTION_STATUS, nsStatus.getValue().toString());
		}
		if(nsOperation.getValue() != null && !"".equals(nsOperation.getValue())){
			prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
			.put(JadeBSConstants.FILTER_OPTION_OPERATION, nsOperation.getValue().toString());
		}
		if(tfSourceFile.getValue() != null && !"".equals(tfSourceFile.getValue())){
			prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
			.put(JadeBSConstants.FILTER_OPTION_SOURCE_FILE, tfSourceFile.getValue());
		}
		if(tfSourceHost.getValue() != null && !"".equals(tfSourceHost.getValue())){
			prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
			.put(JadeBSConstants.FILTER_OPTION_SOURCE_HOST, tfSourceHost.getValue());
		}
		if(tfTargetFile.getValue() != null && !"".equals(tfTargetFile.getValue())){
			prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
			.put(JadeBSConstants.FILTER_OPTION_TARGET_FILE, tfTargetFile.getValue());
		}
		if(tfTargetHost.getValue() != null && !"".equals(tfTargetHost.getValue())){
			prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
			.put(JadeBSConstants.FILTER_OPTION_TARGET_HOST, tfTargetHost.getValue());
		}
		if(tfMandator.getValue() != null && !"".equals(tfMandator.getValue())){
			prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
			.put(JadeBSConstants.FILTER_OPTION_MANDATOR, tfMandator.getValue());
		}
	}
	
	private void checkLastFilterSettings(){
		boolean lastUsed = false;
		try {
			if(prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).nodeExists(JadeBSConstants.PREF_NODE_PREFERENCES_GENERAL)){
				lastUsed = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_PREFERENCES_GENERAL)
						.getBoolean(JadeBSConstants.PREF_KEY_LAST_USED_FILTER, false);
			}
		} catch (BackingStoreException e) {
			log.warn("Unable to read from PreferenceStore, using defaults.");
			e.printStackTrace();
		}
		if (lastUsed){
			Long timeFrom = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
					.getLong(JadeBSConstants.FILTER_OPTION_TRANSFER_TIMESTAMP_FROM, 0L);
			if(timeFrom != 0L){
				dfTimestampFrom.setValue(new Date(timeFrom));
			}
			Long timeTo = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
					.getLong(JadeBSConstants.FILTER_OPTION_TRANSFER_TIMESTAMP_TO, 0L); 
			if(timeTo != 0L){
				dfTimestampTo.setValue(new Date(timeTo));
			}
			String protocol = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
					.get(JadeBSConstants.FILTER_OPTION_PROTOCOL, null);
			if(protocol != null && !"".equals(protocol)){
				nsProtocol.setValue(protocol);
			}
			String status = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
					.get(JadeBSConstants.FILTER_OPTION_STATUS, null); 
			if(status != null && !"".equals(status)){
				nsStatus.setValue(status);
			}
			String operation = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
					.get(JadeBSConstants.FILTER_OPTION_OPERATION, null);
			if(operation != null && !"".equals(operation)){
				nsOperation.setValue(operation);
			}
			String sourceFile = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
					.get(JadeBSConstants.FILTER_OPTION_SOURCE_FILE, null);
			if(sourceFile != null && !"".equals(sourceFile)){
				tfSourceFile.setValue(sourceFile);
			}
			String sourceHost = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
					.get(JadeBSConstants.FILTER_OPTION_SOURCE_HOST, null);
			if(sourceHost != null && !"".equals(sourceHost)){
				tfSourceHost.setValue(sourceHost);
			}
			String targetFile = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
					.get(JadeBSConstants.FILTER_OPTION_TARGET_FILE, null);
			if(targetFile != null && !"".equals(targetFile)){
				tfTargetFile.setValue(targetFile);
			}
			String targetHost = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
					.get(JadeBSConstants.FILTER_OPTION_TARGET_HOST, null);
			if(targetHost != null && !"".equals(targetHost)){
				tfTargetHost.setValue(targetHost);
			}
			String mandator = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER)
					.get(JadeBSConstants.FILTER_OPTION_MANDATOR, null);
			if(mandator != null && !"".equals(mandator)){
				tfMandator.setValue(mandator);
			}
			lastFilter = createJadeFilesHistoryFilter();
		}
	}
	
	public void refreshCaptions(Locale locale){
		dfTimestampFrom.setCaption(messages.getValue("FilterLayout.from", locale));
		dfTimestampTo.setCaption(messages.getValue("FilterLayout.to", locale));
		tfMandator.setCaption(messages.getValue(MESSAGE_RESOURCE_BASE + JadeFileColumns.MANDATOR.getName(), locale));
		tfMandator.setInputPrompt(messages.getValue(MESSAGE_RESOURCE_BASE + JadeFileColumns.MANDATOR.getName(), locale));
		nsProtocol.setCaption(messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.PROTOCOL.getName(), locale));
		tfSourceFile.setCaption(messages.getValue(MESSAGE_RESOURCE_BASE + JadeFileColumns.SOURCE_FILENAME.getName(), locale));
		tfSourceFile.setInputPrompt(messages.getValue(MESSAGE_RESOURCE_BASE + JadeFileColumns.SOURCE_FILENAME.getName(), locale));
		tfSourceHost.setCaption(messages.getValue(MESSAGE_RESOURCE_BASE + JadeFileColumns.SOURCE_HOST.getName(), locale));
		tfSourceHost.setInputPrompt(messages.getValue(MESSAGE_RESOURCE_BASE + JadeFileColumns.SOURCE_HOST.getName(), locale));
		tfTargetFile.setCaption(messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.TARGET_FILENAME.getName(), locale));
		tfTargetFile.setInputPrompt(messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.TARGET_FILENAME.getName(), locale));
		tfTargetHost.setCaption(messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.TARGET_HOST.getName(), locale));
		tfTargetHost.setInputPrompt(messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.TARGET_HOST.getName(), locale));
		nsStatus.setCaption(messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.STATUS.getName(), locale));
		nsOperation.setCaption(messages.getValue(MESSAGE_RESOURCE_BASE + JadeHistoryFileColumns.OPERATION.getName(), locale));
		btnCommit.setCaption(messages.getValue("FilterLayout.ok", locale));
		btnDiscard.setCaption(messages.getValue("FilterLayout.discard", locale));
	}
	
	private MainView getMainViewFromCurrentUI(){
		return ((JADEHistoryViewerUI)UI.getCurrent()).getMainView();
	}
	
}

