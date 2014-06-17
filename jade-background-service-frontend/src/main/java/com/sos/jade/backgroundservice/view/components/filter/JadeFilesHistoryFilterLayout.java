package com.sos.jade.backgroundservice.view.components.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import sos.ftphistory.JadeFilesHistoryFilter;

import com.sos.JSHelper.concurrent.SOSThreadPoolExecutor;
import com.sos.jade.backgroundservice.listeners.IJadeFileListener;
import com.sos.jade.backgroundservice.listeners.impl.JadeFileListenerProxy;
import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.sos.jade.backgroundservice.view.MainView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
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
	private VerticalLayout vlMain;
	private Date timestampFrom;
	private Date timestampTo;
	private DateField dfTimestampFrom;
	private DateField dfTimestampTo;
	private String mandator;
	private String protocol;
	private String sourceFile;
	private String sourceHost;
	private String targetFile;
	private String targetHost;
	private TextField tfMandator;
	private TextField tfSourceFile;
	private TextField tfSourceHost;
	private TextField tfTargetFile;
	private TextField tfTargetHost;
	private TextField tfProtocol;
	private NativeSelect nsStatus;
	private NativeSelect nsOperation;
	private Button btnCommit;
	private Button btnDiscard;

	private JadeBSMessages messages;

	@SuppressWarnings("unused")
	private static final long oneDay = 24 * 60 * 60 * 1000;
	private MainView ui;
	
	public JadeFilesHistoryFilterLayout(MainView ui){
		super();
		this.ui = ui;
		this.messages = ui.getMessages();
		this.setSizeFull();
		this.setMargin(true);
		initJadeFilesHistoryFilterComponents();
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
	 * helper method to create an empty {@link com.vaadin.ui.Label Label}
	 * 
	 * @return the created {@link com.vaadin.ui.Label Label}
	 */
	private Label initDummyLabel(){
		Label lbl = new Label();
		lbl.setSizeFull();
		return lbl;
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

	private void checkTextFieldValues(){
		if("".equals(mandator)){
			mandator = null;
		}
		if("".equals(protocol)){
			protocol = null;
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
	 * JadeFilesHistoryFilter and populates the {@link com.sos.jade.backgroundservice.view.components.JadeMixedTable JadeMixedTable} with the filtered data
	 * 
	 * @param listener the {@link IJadeFileListener} which holds the methods to access the JadeFilesHistoryDBLayer
	 * @param historyFilter the JadeFilesHistoryFilter to filter JadeFilesHistoryDBItems and the related JadeFilesDBItem with
	 */
	private void filterData(final IJadeFileListener listener, final JadeFilesHistoryFilter historyFilter) {
		
		SOSThreadPoolExecutor objTPE = new SOSThreadPoolExecutor(1);
			objTPE.runTask(
					new Thread() {
		            
					@Override
		            public void run() {
		                try {
		                	listener.filterJadeFilesHistory(historyFilter);
		                } catch (final Exception e) {
		                    listener.getException(e);
		                }
						UI.getCurrent().access(new Runnable() {
							@Override
							public void run() {
						        ui.getTblMixed().populateDatasource(ui.getHistoryItems());
						        ui.getTblMixed().markAsDirty();
								listener.closeJadeFilesHistoryDbSession();
						        ui.getTblMixed().setVisible(true);
							}
						});
		            };
	        });
		try {
			objTPE.shutDown();
			objTPE.objThreadPool.awaitTermination(1, TimeUnit.DAYS);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
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
		tfMandator = initTextField(messages.getValue("FilterLayout.mandator"), mandator);
		tfProtocol = initTextField(messages.getValue("FilterLayout.protocol"), protocol);
		tfSourceFile = initTextField(messages.getValue("FilterLayout.sourceFile"), sourceFile);
		tfSourceHost = initTextField(messages.getValue("FilterLayout.sourceHost"), sourceHost);
		tfTargetFile = initTextField(messages.getValue("FilterLayout.targetFile"), targetFile);
		tfTargetHost = initTextField(messages.getValue("FilterLayout.targetHost"), targetHost);
		List<String> statusList = new ArrayList<String>();
		statusList.add(STATUS_SUCCESS);
		statusList.add(STATUS_ERROR);
		nsStatus = new NativeSelect(messages.getValue("FilterLayout.status"), statusList);
		List<String> operationList = new ArrayList<String>();
		operationList.add(OPERATION_COPY);
		operationList.add(OPERATION_SEND);
		operationList.add(OPERATION_RECEIVE);
		nsOperation = new NativeSelect(messages.getValue("FilterLayout.operation"), operationList);
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
		hlForth.addComponents(tfProtocol, tfTargetHost);
		hlForth.setExpandRatio(tfProtocol, 1);
		hlForth.setExpandRatio(tfTargetHost, 1);
		hlFifth.addComponents(dfTimestampFrom, dfTimestampTo);
		hlFifth.setExpandRatio(dfTimestampFrom, 1);
		hlFifth.setExpandRatio(dfTimestampTo, 1);
		hlButtons.addComponents(btnDiscard, btnCommit);
		hlButtons.setComponentAlignment(btnDiscard, Alignment.MIDDLE_LEFT);
		hlButtons.setComponentAlignment(btnCommit, Alignment.MIDDLE_LEFT);
		
		btnCommit.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				ui.setMarkedRow(null);
				ui.setDetailViewVisible(true);
				JadeFilesHistoryFilter filter = new JadeFilesHistoryFilter();
				checkTextFieldValues();
				filter.setTransferTimestampFrom(dfTimestampFrom.getValue());
				filter.setTransferTimestampTo(dfTimestampTo.getValue());
				filter.setProtocol(tfProtocol.getValue());
				if(nsStatus.getValue() != null && !"".equals(nsStatus.getValue()))
					filter.setStatus(nsStatus.getValue().toString());
				if(nsOperation.getValue() != null && !"".equals(nsOperation.getValue()))
					filter.setOperation(nsOperation.getValue().toString());
				filter.setSourceFile(tfSourceFile.getValue());
				filter.setSourceHost(tfSourceHost.getValue());
				filter.setTargetFilename(tfTargetFile.getValue());
				filter.setTargetHost(tfTargetHost.getValue());
				filter.setMandator(tfMandator.getValue());
				filterData(new JadeFileListenerProxy(ui), filter);
				((FilterLayoutWindow)JadeFilesHistoryFilterLayout.this.getParent()).close();
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
	
	public void refreshCaptions(Locale locale){
		dfTimestampFrom.setCaption(messages.getValue("FilterLayout.from", locale));
		dfTimestampTo.setCaption(messages.getValue("FilterLayout.to", locale));
		tfMandator.setCaption(messages.getValue("FilterLayout.mandator", locale));
		tfProtocol.setCaption(messages.getValue("FilterLayout.protocol", locale));
		tfSourceFile.setCaption(messages.getValue("FilterLayout.sourceFile", locale));
		tfSourceHost.setCaption(messages.getValue("FilterLayout.sourceHost", locale));
		tfTargetFile.setCaption(messages.getValue("FilterLayout.targetFile", locale));
		tfTargetHost.setCaption(messages.getValue("FilterLayout.targetHost", locale));
		nsStatus.setCaption(messages.getValue("FilterLayout.status", locale));
		nsOperation.setCaption(messages.getValue("FilterLayout.operation", locale));
		btnCommit.setCaption(messages.getValue("FilterLayout.ok", locale));
		btnDiscard.setCaption(messages.getValue("FilterLayout.discard", locale));
	}
	
}

