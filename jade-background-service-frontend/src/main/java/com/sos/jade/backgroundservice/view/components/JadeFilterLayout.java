package com.sos.jade.backgroundservice.view.components;

import java.io.Serializable;
import java.util.Date;

import sos.ftphistory.JadeFilesFilter;
import sos.ftphistory.db.JadeFilesDBLayer;

import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class JadeFilterLayout extends VerticalLayout implements Serializable{
	private static final long serialVersionUID = 1L;
	private HorizontalLayout hlMain;
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
	private static final long oneDay = 24 * 60 * 60 * 1000;
	private JadeFilesDBLayer jadeFilesDBLayer;
	
	public JadeFilterLayout(JadeFilesDBLayer jadeFilesDBLayer){
		super();
		this.jadeFilesDBLayer = jadeFilesDBLayer;
		this.setSizeFull();
		initJadeFilterComponents();
	}
	
	private void initJadeFilterComponents(){
		hlMain = new HorizontalLayout();
		hlMain.setSizeFull();
		addComponent(hlMain);

		VerticalLayout vlFilterLeft = initVLayout();
		hlMain.addComponent(vlFilterLeft);
		VerticalLayout vlFilterMiddle = initVLayout();
		hlMain.addComponent(vlFilterMiddle);
		VerticalLayout vlFilterRight = initVLayout();
		hlMain.addComponent(vlFilterRight);

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
		Label lblDummy3 = initDummyLabel();
		Label lblDummy4 = initDummyLabel();
		Button btnCommit = new Button("OK");
		
		vlFilterLeft.addComponents(dfCreatedFrom, dfModifiedFrom, tfMandator, tfSourceHost, btnCommit);
		vlFilterMiddle.addComponents(dfCreatedTo, dfModifiedTo, tfSourceDir, tfSourceUser, lblDummy);
		vlFilterRight.addComponents(tfCreatedBy, tfModifiedBy, tfSourceFile, lblDummy2, lblDummy3);
		hlMain.setExpandRatio(vlFilterLeft, 1);
		hlMain.setExpandRatio(vlFilterMiddle, 1);
		hlMain.setExpandRatio(vlFilterRight, 1);
		
		btnCommit.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				JadeFilesFilter filter = new JadeFilesFilter();
				checkTextFieldValues();
				filter.setCreatedFrom(createdFrom);
				filter.setCreatedTo(createdTo);
				filter.setCreatedBy(createdBy);
				filter.setModifiedTo(modifiedTo);
				filter.setModifiedFrom(modifiedFrom);
				filter.setModifiedBy(modifiedBy);
				filter.setMandator(mandator);
				filter.setSourceDir(sourceDir);
				filter.setSourceFilename(sourceFile);
				filter.setSourceHost(sourceHost);
				filter.setSourceUser(sourceUser);
				jadeFilesDBLayer.setFilter(filter);
				// TODO Callback impl
			}
		});
//		addComponent(btnCommit);
	}
	
	private VerticalLayout initVLayout(){
		VerticalLayout vl = new VerticalLayout();
		vl.setWidth(200.0f, Unit.PIXELS);
		return vl;
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
		tf.setInputPrompt(caption);
		tf.setValue("");
		tf.setSizeFull();
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
}

