package com.sos.jade.backgroundservice.view.components.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import sos.ftphistory.JadeFilesFilter;

import com.sos.JSHelper.concurrent.SOSThreadPoolExecutor;
import com.sos.jade.backgroundservice.listeners.IJadeFileListener;
import com.sos.jade.backgroundservice.listeners.impl.JadeFileListenerProxy;
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

public class JadeFilesFilterLayout extends VerticalLayout implements Serializable{
	private static final long serialVersionUID = 1L;
	private VerticalLayout vlMain;
	private Date createdFrom;
	private Date createdTo;
	private Date modifiedFrom;
	private Date modifiedTo;
	private DateField dfCreatedFrom;
	private DateField dfCreatedTo;
	private DateField dfModifiedFrom;
	private DateField dfModifiedTo;
	private String createdBy;
	private String modifiedBy;
	private String mandator;
	private String sourceDir;
	private String sourceFile;
	private String sourceHost;
	private String sourceUser;
	private TextField tfCreatedBy;
	private TextField tfModifiedBy;
	private TextField tfMandator;
	private TextField tfSourceDir;
	private TextField tfSourceFile;
	private TextField tfSourceHost;
	private TextField tfSourceUser;
	private NativeSelect nsStatus;
	private NativeSelect nsOperation;

	@SuppressWarnings("unused")
	private static final long oneDay = 24 * 60 * 60 * 1000;
	private MainView ui;
	
	public JadeFilesFilterLayout(MainView ui){
		super();
		this.ui = ui;
		this.setSizeFull();
		this.setMargin(true);
		initJadeFilterComponents();
	}
	
	private void initJadeFilterComponents(){
		vlMain = new VerticalLayout();
		vlMain.setHeight(200.0f, Unit.PIXELS);
		addComponent(vlMain);

		HorizontalLayout hlFirst = initHLayout();
		vlMain.addComponent(hlFirst);
		HorizontalLayout hlSecond = initHLayout();
		vlMain.addComponent(hlSecond);
		HorizontalLayout hlThird = initHLayout();
		vlMain.addComponent(hlThird);
		HorizontalLayout hlForth = initHLayout();
		vlMain.addComponent(hlForth);
		
		dfCreatedFrom = initDateField("createdFrom", createdFrom);
		dfCreatedTo = initDateField("createdTo", createdTo);
		dfModifiedFrom = initDateField("modifiedFrom", modifiedFrom);
		dfModifiedTo = initDateField("modifiedTo", modifiedTo);
		tfCreatedBy = initTextField("createdBy", createdBy);
		tfCreatedBy.setDescription("The User who created this job.");
		tfModifiedBy = initTextField("modifiedBy", modifiedBy);
		tfModifiedBy.setDescription("The User who modified this job last.");
		tfMandator = initTextField("mandator", mandator);
		tfSourceDir = initTextField("sourceDir", sourceDir);
		tfSourceFile = initTextField("sourceFile", sourceFile);
		tfSourceHost = initTextField("sourceHost", sourceHost);
		tfSourceUser = initTextField("sourceUser", sourceUser);
		Label lblDummy = initDummyLabel();
		Label lblDummy2 = initDummyLabel();
//		Label lblDummy3 = initDummyLabel();

		List<String> statusList = new ArrayList<String>();
		statusList.add("success");
		statusList.add("error");
		nsStatus = new NativeSelect("Status", statusList);
		List<String> processList = new ArrayList<String>();
		processList.add("send");
		processList.add("receive");
		nsOperation = new NativeSelect("Process", processList);
		Button btnCommit = new Button("OK");

		hlFirst.addComponents(tfMandator, dfCreatedFrom, dfCreatedTo, tfCreatedBy);
		hlFirst.setExpandRatio(tfMandator, 1);
		hlFirst.setExpandRatio(dfCreatedFrom, 1);
		hlFirst.setExpandRatio(dfCreatedTo, 1);
		hlFirst.setExpandRatio(tfCreatedBy, 1);
		hlSecond.addComponents(nsStatus, dfModifiedFrom, dfModifiedTo, tfModifiedBy);
		hlSecond.setExpandRatio(nsStatus, 1);
		hlSecond.setExpandRatio(dfModifiedFrom, 1);
		hlSecond.setExpandRatio(dfModifiedTo, 1);
		hlSecond.setExpandRatio(tfModifiedBy, 1);
		hlThird.addComponents(tfSourceDir, tfSourceFile, tfSourceHost, tfSourceUser);
		hlThird.setExpandRatio(tfSourceDir, 1);
		hlThird.setExpandRatio(tfSourceFile, 1);
		hlThird.setExpandRatio(tfSourceHost, 1);
		hlThird.setExpandRatio(tfSourceUser, 1);
		hlForth.addComponents(nsOperation, lblDummy, lblDummy2, btnCommit);
		hlForth.setExpandRatio(nsOperation, 1);
		hlForth.setExpandRatio(lblDummy, 1);
		hlForth.setExpandRatio(lblDummy2, 1);
		hlForth.setExpandRatio(btnCommit, 1);
		hlForth.setComponentAlignment(btnCommit, Alignment.MIDDLE_LEFT);
		
		btnCommit.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				ui.setMarkedRow(null);
				ui.setHistoryTableNotVisible();
				JadeFilesFilter filter = new JadeFilesFilter();
				checkTextFieldValues();
				filter.setCreatedFrom(dfCreatedFrom.getValue());
				filter.setCreatedTo(dfCreatedTo.getValue());
				filter.setCreatedBy(tfCreatedBy.getValue());
				filter.setModifiedTo(dfModifiedTo.getValue());
				filter.setModifiedFrom(dfModifiedFrom.getValue());
				filter.setModifiedBy(tfModifiedBy.getValue());
				filter.setMandator(tfMandator.getValue());
				filter.setSourceDir(tfSourceDir.getValue());
				filter.setSourceFilename(tfSourceFile.getValue());
				filter.setSourceHost(tfSourceHost.getValue());
				filter.setSourceUser(tfSourceUser.getValue());
				// TODO fileSize impl
				getFilteredData(new JadeFileListenerProxy(ui), filter);
				((FilterLayoutWindow)JadeFilesFilterLayout.this.getParent()).close();
			}
		});
	}
	
//	private VerticalLayout initVLayout(){
//		VerticalLayout vl = new VerticalLayout();
//		vl.setWidth(150.0f, Unit.PIXELS);
//		return vl;
//	}
	
	private HorizontalLayout initHLayout(){
		HorizontalLayout hl = new HorizontalLayout();
		hl.setHeight(50.0f, Unit.PIXELS);
		hl.setWidth(600.0f, Unit.PIXELS);
		return hl;
	}
	
	private Label initDummyLabel(){
		Label lbl = new Label();
		lbl.setSizeFull();
		return lbl;
	}
	
	private DateField initDateField(String caption, Date date){
		DateField df = new DateField(caption, date);
		df.setSizeFull();
		return df;
	}
	
	private TextField initTextField(String caption, String text){
		TextField tf = new TextField(caption, text);
		tf.setHeight(23.0f, Unit.PIXELS);
		tf.setWidth("100%");
		tf.setInputPrompt(caption);
		tf.setValue("");
		return tf;
	}

	private void checkTextFieldValues(){
		if("".equals(createdBy)){
			createdBy = null;
		}
		if("".equals(modifiedBy)){
			modifiedBy = null;
		}
		if("".equals(mandator)){
			mandator = null;
		}
		if("".equals(sourceDir)){
			sourceDir = null;
		}
		if("".equals(sourceFile)){
			sourceFile = null;
		}
		if("".equals(sourceHost)){
			sourceHost = null;
		}
		if("".equals(sourceUser)){
			sourceUser = null;
		}
	}


	private void getFilteredData(final IJadeFileListener listener, final JadeFilesFilter filesFilter) {
		
		SOSThreadPoolExecutor objTPE = new SOSThreadPoolExecutor(1);
			objTPE.runTask(        new Thread() {
	            @Override
	            public void run() {
	                try {
	                	listener.filterJadeFiles(filesFilter);
	                } catch (final Exception e) {
	                    listener.getException(e);
	                }
					UI.getCurrent().access(new Runnable() {
						@Override
						public void run() {
					        ui.getTblFiles().populateDatasource(ui.getFileItems());
					        ui.getTblFiles().markAsDirty();
					        ui.getTblFiles().setVisible(true);
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
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                	listener.filterJadeFiles(filesFilter);
//                } catch (final Exception e) {
//                    listener.getException(e);
//                }
//				UI.getCurrent().access(new Runnable() {
//					@Override
//					public void run() {
//				        ui.getTblFiles().populateDatasource(ui.getFileItems());
//				        ui.getTblFiles().markAsDirty();
//				        ui.getTblFiles().setVisible(true);
//					}
//				});
//            };
//        }.start();
    }
	
}

