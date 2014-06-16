package com.sos.jade.backgroundservice.view.components;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sos.ftphistory.db.JadeFilesHistoryDBItem;

import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class DetailLayout extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	private Label lblGuid;
	private	Label lblSosftpId;
	private Label lblTargetHostIp; 
	private Label lblTargetUser;
	private Label lblTargetDir; 
	private Label lblPort;
	private Label lblStatus; 
	private Label lblLastErrorMessage;
	private Label lblLogFilename; 
	private Label lblJumpHost;
	private Label lblJumpHostIp;
	private Label lblJumpUser;
	private Label lblJumpProtocol;
	private Label lblJumpPort;
	private Label lblId;
	private Label lblSourceHostIp;
	private Label lblSourceUser;
	private Label lblSourceDir; 
	private Label lblSourceFilename;
	private Label lblMd5;
	// not needed except for detailed detail view
	private Label lblFileSize;
	private Label lblFileCreated; 
	private Label lblFileCreatedBy;
	private Label lblFileModified; 
	private Label lblFileModifiedBy;
	private Label lblOperation; 
	private Label lblTransferTimestamp;
	private Label lblPid;
	private Label lblPpid;
	private Label lblTargetHost;
	private Label lblTargetFilename;
	private Label lblProtocol; 
	private Label lblFileHistoryCreated; 
	private Label lblFileHistoryCreatedBy;
	private Label lblFileHistoryModified; 
	private Label lblFileHistoryModifiedBy; 
	private Label lblMandator;
	private Label lblSourceHost;
	private SimpleDateFormat sdfOut = new SimpleDateFormat("dd.MM.YYYY hh:mm:ss");
	private List<HorizontalLayout> detailLayouts = new ArrayList<HorizontalLayout>();
	private JadeBSMessages messages = new JadeBSMessages("JADEBSMessages", Locale.getDefault());
	
	public DetailLayout() {
		this.setHeight(200.0f, Unit.PIXELS);
		this.setWidth("100%");
		this.setMargin(true);
		this.setSpacing(true);
		this.addStyleName("jadeDetailLayout");
        initLabels();
        initLayouts();
	}
	
	private Label initLabel(String id, String styleName){
		return initLabel(id, null, styleName);
	}
	
	private Label initLabel(String id, String value, String styleName){
		Label lbl = new Label();
		lbl.setId(id);
		lbl.addStyleName(styleName);
		lbl.setSizeUndefined();
		if(value != null){
			lbl.setValue(value);
		}
		return lbl;
	}
	
	private HorizontalLayout initHlayout(Component... components){
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSizeFull();
		this.addComponent(hl);
		this.setExpandRatio(hl, 1);
		hl.addComponents(components);
		return hl;
	}
	
	private HorizontalLayout initHLabelLayout(Label caption, Label value){
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSizeFull();
		hl.setSpacing(true);
        hl.addStyleName("jadeDetailLayoutNoBorder");
 		hl.addComponents(caption, value);
    	hl.setComponentAlignment(caption, Alignment.MIDDLE_LEFT);
    	hl.setComponentAlignment(value, Alignment.MIDDLE_LEFT);
		hl.setExpandRatio(value, 1);
		return hl;
	}
	
	private Label initDummyValueLabel(){
		Label lbl = new Label();
		lbl.addStyleName("jadeDetailLabel");
		return lbl;
	}
	
	private void initLayouts(){
		Label lblGuidCaption = initLabel("guidCaption",
				messages.getValue("DetailLayout.guid"), "jadeDetailLabelCaption"); 
		Label lblMd5Caption = initLabel("md5Caption", 
				messages.getValue("DetailLayout.md5"), "jadeDetailLabelCaption"); 
		Label lblPortCaption = initLabel("portCaption", 
				messages.getValue("DetailLayout.port"), "jadeDetailLabelCaption"); 
		Label lblLastErrorMessageCaption = initLabel("lastErrorMessageCaption", 
				messages.getValue("DetailLayout.lastErrorMessage"), "jadeDetailLabelCaption"); 
        this.detailLayouts.add(
    		initHlayout(
				initHLabelLayout(lblGuidCaption, lblGuid), 
				initHLabelLayout(lblMd5Caption, lblMd5), 
				initHLabelLayout(lblPortCaption, lblPort), 
				initHLabelLayout(lblLastErrorMessageCaption, lblLastErrorMessage)));
		Label lblJumpHostCaption = initLabel("jumpHostCaption", 
				messages.getValue("DetailLayout.jumpHost"), "jadeDetailLabelCaption"); 
		Label lblJumpPortCaption = initLabel("jumpPortCaption", 
				messages.getValue("DetailLayout.jumpPort"), "jadeDetailLabelCaption"); 
		Label lblJumpProtocolCaption = initLabel("jumpProtocolCaption", 
				messages.getValue("DetailLayout.jumpProtocol"), "jadeDetailLabelCaption"); 
		Label lblJumpUserCaption = initLabel("jumpUserCaption", 
				messages.getValue("DetailLayout.jumpUser"), "jadeDetailLabelCaption"); 
        this.detailLayouts.add(
    		initHlayout(
				initHLabelLayout(lblJumpHostCaption, lblJumpHost), 
				initHLabelLayout(lblJumpPortCaption, lblJumpPort), 
				initHLabelLayout(lblJumpProtocolCaption, lblJumpProtocol), 
				initHLabelLayout(lblJumpUserCaption, lblJumpUser)));
		Label lblLogFilenameCaption = initLabel("logFilenameCaption", 
				"Log Filename:", "jadeDetailLabelCaption"); 
		Label lblSourceDirCaption = initLabel("sourceDirCaption", 
				messages.getValue("DetailLayout.sourceDirectory"), "jadeDetailLabelCaption"); 
		Label lblSourceFilenameCaption = initLabel("sourceFilenameCaption", 
				messages.getValue("DetailLayout.sourceFilename"), "jadeDetailLabelCaption"); 
		Label lblSourceUserCaption = initLabel("sourceUserCaption", 
				messages.getValue("DetailLayout.sourceUser"), "jadeDetailLabelCaption"); 
        this.detailLayouts.add(
    		initHlayout(
				initHLabelLayout(lblSourceDirCaption, lblSourceDir), 
				initHLabelLayout(lblSourceFilenameCaption, lblSourceFilename), 
				initHLabelLayout(lblSourceUserCaption, lblSourceUser),
				initHLabelLayout(lblLogFilenameCaption, lblLogFilename)));
		Label lblTargetDirCaption = initLabel("targetDirCaption", 
				messages.getValue("DetailLayout.targetDirectory"), "jadeDetailLabelCaption"); 
		Label lblTargetUserCaption = initLabel("targetUserCaption", 
				messages.getValue("DetailLayout.targetUser"), "jadeDetailLabelCaption"); 
		Label lblDummy1Caption = initLabel("dummy1Caption", "", "jadeDetailLabelCaption"); 
		Label lblDummy2Caption = initLabel("dummy2Caption", "", "jadeDetailLabelCaption"); 
        this.detailLayouts.add(
    		initHlayout(
				initHLabelLayout(lblTargetDirCaption, lblTargetDir), 
				initHLabelLayout(lblTargetUserCaption, lblTargetUser), 
				initHLabelLayout(lblDummy1Caption, initDummyValueLabel()), 
				initHLabelLayout(lblDummy2Caption, initDummyValueLabel())));
	}
	
	private void initDetailedLayouts(){
		Label lblGuidCaption = initLabel("guidCaption", "GUID:", "jadeDetailLabelCaption"); 
		Label lblPidCaption = initLabel("pidCaption", "PID:", "jadeDetailLabelCaption"); 
		Label lblPpidCaption = initLabel("pPidCaption", "PPID:", "jadeDetailLabelCaption"); 
		Label lblOperationCaption = initLabel("operationCaption", "Operation:", "jadeDetailLabelCaption"); 
        this.detailLayouts.add(
    		initHlayout(
				initHLabelLayout(lblGuidCaption, lblGuid), 
        		initHLabelLayout(lblPidCaption, lblPid), 
        		initHLabelLayout(lblPpidCaption, lblPpid), 
        		initHLabelLayout(lblOperationCaption, lblOperation)));
		Label lblFileHistoryCreatedCaption = initLabel("fileHistoryCreatedCaption", "File History Created:", "jadeDetailLabelCaption"); 
		Label lblFileHistoryCreatedByCaption = initLabel("fileHistoryCreatedByCaption", "File History Created By:", "jadeDetailLabelCaption"); 
		Label lblFileHistoryModifiedCaption = initLabel("fileHistoryModifiedCaption", "File History Modified:", "jadeDetailLabelCaption"); 
		Label lblFileHistoryModifiedByCaption = initLabel("fileHistoryModifiedByCaption", "File History Modified By:", "jadeDetailLabelCaption"); 
        this.detailLayouts.add(
    		initHlayout(
        		initHLabelLayout(lblFileHistoryCreatedCaption, lblFileHistoryCreated), 
        		initHLabelLayout(lblFileHistoryCreatedByCaption, lblFileHistoryCreatedBy), 
        		initHLabelLayout(lblFileHistoryModifiedCaption, lblFileHistoryModified), 
        		initHLabelLayout(lblFileHistoryModifiedByCaption, lblFileHistoryModifiedBy)));
		Label lblProtocolCaption = initLabel("protocolCaption", "Protocol:", "jadeDetailLabelCaption"); 
		Label lblPortCaption = initLabel("portCaption", "Port:", "jadeDetailLabelCaption"); 
		Label lblLogFilenameCaption = initLabel("logFilenameCaption", "Log Filename:", "jadeDetailLabelCaption"); 
		Label lblLastErrorMessageCaption = initLabel("lastErrorMessageCaption", "Last Error Message:", "jadeDetailLabelCaption"); 
        this.detailLayouts.add(
    		initHlayout(
    				initHLabelLayout(lblProtocolCaption, lblProtocol), 
					initHLabelLayout(lblPortCaption, lblPort), 
					initHLabelLayout(lblLogFilenameCaption, lblLogFilename), 
					initHLabelLayout(lblLastErrorMessageCaption, lblLastErrorMessage)));
		Label lblSourceDirCaption = initLabel("sourceDirCaption", "Source Directory:", "jadeDetailLabelCaption"); 
		Label lblSourceFilenameCaption = initLabel("sourceFilenameCaption", "Source Filename:", "jadeDetailLabelCaption"); 
		Label lblSourceHostCaption = initLabel("sourceHostCaption", "Source Host:", "jadeDetailLabelCaption"); 
		Label lblSourceUserCaption = initLabel("sourceUserCaption", "Source User:", "jadeDetailLabelCaption"); 
        this.detailLayouts.add(
    		initHlayout(
				initHLabelLayout(lblSourceDirCaption, lblSourceDir), 
				initHLabelLayout(lblSourceFilenameCaption, lblSourceFilename), 
				initHLabelLayout(lblSourceHostCaption, lblSourceHost), 
				initHLabelLayout(lblSourceUserCaption, lblSourceUser)));
		Label lblTargetDirCaption = initLabel("targetDirCaption", "Target Directory:", "jadeDetailLabelCaption"); 
		Label lblTargetFilenameCaption = initLabel("targetFilenameCaption", "Target Filename:", "jadeDetailLabelCaption"); 
		Label lblTargetHostCaption = initLabel("targetHostCaption", "Target Host:", "jadeDetailLabelCaption"); 
		Label lblTargetUserCaption = initLabel("targetUserCaption", "Target User:", "jadeDetailLabelCaption"); 
        this.detailLayouts.add(
    		initHlayout(
				initHLabelLayout(lblTargetDirCaption, lblTargetDir), 
				initHLabelLayout(lblTargetFilenameCaption, lblTargetFilename), 
				initHLabelLayout(lblTargetHostCaption, lblTargetHost), 
				initHLabelLayout(lblTargetUserCaption, lblTargetUser)));
		Label lblJumpHostCaption = initLabel("jumpHostCaption", "Jump Host:", "jadeDetailLabelCaption"); 
		Label lblJumpPortCaption = initLabel("jumpPortCaption", "Jump Port:", "jadeDetailLabelCaption"); 
		Label lblJumpProtocolCaption = initLabel("jumpProtocolCaption", "Jump Protocol:", "jadeDetailLabelCaption"); 
		Label lblJumpUserCaption = initLabel("jumpUserCaption", "Jump User:", "jadeDetailLabelCaption"); 
        this.detailLayouts.add(
    		initHlayout(
				initHLabelLayout(lblJumpHostCaption, lblJumpHost), 
				initHLabelLayout(lblJumpPortCaption, lblJumpPort), 
				initHLabelLayout(lblJumpProtocolCaption, lblJumpProtocol), 
				initHLabelLayout(lblJumpUserCaption, lblJumpUser)));
		Label lblTransferTimestampCaption = initLabel("transferTimestampCaption", "Transfer Timestamp:", "jadeDetailLabelCaption"); 
		Label lblMd5Caption = initLabel("md5Caption", "MD5:", "jadeDetailLabelCaption"); 
		Label lblFileSizeCaption = initLabel("fileSizeCaption", "File Size:", "jadeDetailLabelCaption"); 
		Label lblMandatorCaption = initLabel("mandatorCaption", "Mandator:", "jadeDetailLabelCaption"); 
        this.detailLayouts.add(
    		initHlayout(
				initHLabelLayout(lblTransferTimestampCaption, lblTransferTimestamp), 
				initHLabelLayout(lblMd5Caption, lblMd5), 
        		initHLabelLayout(lblFileSizeCaption, lblFileSize), 
        		initHLabelLayout(lblMandatorCaption, lblMandator)));
		Label lblFileCreatedCaption = initLabel("fileCreatedCaption", "File Created:", "jadeDetailLabelCaption"); 
		Label lblFileCreatedByCaption = initLabel("fileCreatedByCaption", "File Created By:", "jadeDetailLabelCaption"); 
		Label lblFileModifiedCaption = initLabel("fileModifiedCaption", "File Modified:", "jadeDetailLabelCaption"); 
		Label lblFileModifiedByCaption = initLabel("fileModifiedByCaption", "File Modified By:", "jadeDetailLabelCaption"); 
        this.detailLayouts.add(
    		initHlayout(
				initHLabelLayout(lblFileCreatedCaption, lblFileCreated), 
				initHLabelLayout(lblFileCreatedByCaption, lblFileCreatedBy), 
        		initHLabelLayout(lblFileModifiedCaption, lblFileModified), 
        		initHLabelLayout(lblFileModifiedByCaption, lblFileModifiedBy)));
        //unused: lblSourceHostIp; maybe needed if source host name is not available
        //unused: lblTargetHostIp; maybe needed if target host name is not available
        //unused: lblJumpHostIp; maybe needed if jump host name is not available
        //unused: lblSosftpId; referenced id from DB for JadeFilesDBItem from JadeFilesHistoryDBItem
        //unused: lblId; corresponding id from DB for JadeFilesDBItem
	}
	
	
	private void initLabels(){
		// JadeFilesHistoryDBItem properties
		this.lblGuid = initLabel("GUID", "jadeDetailLabel");
		this.lblSosftpId = initLabel("SosFtpId", "jadeDetailLabel");
		this.lblOperation = initLabel("Operation", "jadeDetailLabel"); 
		this.lblTransferTimestamp = initLabel("Timestamp", "jadeDetailLabel");
		this.lblPid = initLabel("PID", "jadeDetailLabel");
		this.lblPpid = initLabel("PPID", "jadeDetailLabel");
		this.lblTargetHost = initLabel("TargetHost", "jadeDetailLabel");
		this.lblTargetHostIp = initLabel("TargetHostIp", "jadeDetailLabel"); 
		this.lblTargetUser = initLabel("TargetUser", "jadeDetailLabel");
		this.lblTargetDir = initLabel("TargetDir", "jadeDetailLabel"); 
		this.lblTargetFilename = initLabel("TargetFilename", "jadeDetailLabel");
		this.lblProtocol = initLabel("Protocol", "jadeDetailLabel"); 
		this.lblPort = initLabel("Port", "jadeDetailLabel");
		this.lblStatus = initLabel("Status", "jadeDetailLabel"); 
		this.lblLastErrorMessage = initLabel("LastErrorMessage", "jadeDetailLabel");
		this.lblLogFilename = initLabel("LogFilename", "jadeDetailLabel"); 
		this.lblJumpHost = initLabel("JumpHost", "jadeDetailLabel");
		this.lblJumpHostIp = initLabel("JumpHostIp", "jadeDetailLabel");
		this.lblJumpUser = initLabel("JumpUser", "jadeDetailLabel");
		this.lblJumpProtocol = initLabel("JumpProtocol", "jadeDetailLabel");
		this.lblJumpPort = initLabel("JumpPort", "jadeDetailLabel");
		this.lblFileHistoryCreated = initLabel("FileHistoryCreated", "jadeDetailLabel"); 
		this.lblFileHistoryCreatedBy = initLabel("FileHistoryCreatedBy", "jadeDetailLabel");
		this.lblFileHistoryModified = initLabel("FileHistoryModified", "jadeDetailLabel"); 
		this.lblFileHistoryModifiedBy = initLabel("FileHistoryModifiedBy", "jadeDetailLabel"); 
		// JadeFilesDBItem properties
		this.lblId = initLabel("ID", "jadeDetailLabel");
		this.lblMandator = initLabel("Mandator", "jadeDetailLabel");
		this.lblSourceHost = initLabel("SourceHost", "jadeDetailLabel");
		this.lblSourceHostIp = initLabel("SourceHostIp", "jadeDetailLabel");
		this.lblSourceUser = initLabel("SourceUser", "jadeDetailLabel");
		this.lblSourceDir = initLabel("SourceDir", "jadeDetailLabel"); 
		this.lblSourceFilename = initLabel("SourceFilename", "jadeDetailLabel");
		this.lblMd5 = initLabel("MD5", "jadeDetailLabel");
		this.lblFileSize = initLabel("FileSize", "jadeDetailLabel");
		this.lblFileCreated = initLabel("FileCreated", "jadeDetailLabel"); 
		this.lblFileCreatedBy = initLabel("FileCreatedBy", "jadeDetailLabel");
		this.lblFileModified = initLabel("FileModified", "jadeDetailLabel"); 
		this.lblFileModifiedBy = initLabel("FileModifiedBy", "jadeDetailLabel");
	}

	public void setLabelValues(JadeFilesHistoryDBItem historyItem){
		// JadeFilesHistoryDBItem properties
		this.lblGuid.setValue(historyItem.getGuid());
		this.lblSosftpId.setValue(historyItem.getSosftpId().toString());
		this.lblOperation.setValue(historyItem.getOperation()); 
		this.lblTransferTimestamp.setValue(sdfOut.format(historyItem.getTransferTimestamp()));
		this.lblPid.setValue(historyItem.getPid().toString());
		this.lblPpid.setValue(historyItem.getPPid().toString());
		this.lblTargetHost.setValue(historyItem.getTargetHost());
		this.lblTargetHostIp.setValue(historyItem.getTargetHostIp()); 
		this.lblTargetUser.setValue(historyItem.getTargetUser());
		this.lblTargetDir.setValue(historyItem.getTargetDir()); 
		this.lblTargetFilename.setValue(historyItem.getTargetFilename());
		this.lblProtocol.setValue(historyItem.getProtocol()); 
		this.lblPort.setValue(historyItem.getPort().toString());
		this.lblStatus.setValue(historyItem.getStatus()); 
		if (historyItem.getLastErrorMessage() != null && !"NULL".equals(historyItem.getLastErrorMessage()))
			this.lblLastErrorMessage.setValue(historyItem.getLastErrorMessage());
		else
			this.lblLastErrorMessage.setValue(messages.getValue("DetailLayout.none"));
		this.lblLogFilename.setValue(historyItem.getLogFilename()); 
		if (historyItem.getJumpHost() != null && !"NULL".equals(historyItem.getJumpHost()))
			this.lblJumpHost.setValue(historyItem.getJumpHost());
		else
			this.lblJumpHost.setValue(messages.getValue("DetailLayout.notSet"));
		this.lblJumpHostIp.setValue(historyItem.getJumpHostIp());
		if (historyItem.getJumpUser() != null && !"NULL".equals(historyItem.getJumpUser()))
			this.lblJumpUser.setValue(historyItem.getJumpUser());
		else
			this.lblJumpUser.setValue(messages.getValue("DetailLayout.notSet"));
		this.lblJumpProtocol.setValue(historyItem.getJumpProtocol());
		this.lblJumpPort.setValue(historyItem.getJumpPort().toString());
		this.lblFileHistoryCreated.setValue(sdfOut.format(historyItem.getCreated())); 
		this.lblFileHistoryCreatedBy.setValue(historyItem.getCreatedBy());
		this.lblFileHistoryModified.setValue(sdfOut.format(historyItem.getModified())); 
		this.lblFileHistoryModifiedBy.setValue(historyItem.getModifiedBy()); 
		// JadeFilesDBItem properties
		this.lblId.setValue(historyItem.getJadeFilesDBItem().getId().toString());
		this.lblMandator.setValue(historyItem.getJadeFilesDBItem().getMandator());
		this.lblSourceHost.setValue(historyItem.getJadeFilesDBItem().getSourceHost());
		this.lblSourceHostIp.setValue(historyItem.getJadeFilesDBItem().getSourceHostIp());
		this.lblSourceUser.setValue(historyItem.getJadeFilesDBItem().getSourceUser());
		this.lblSourceDir.setValue(historyItem.getJadeFilesDBItem().getSourceDir()); 
		this.lblSourceFilename.setValue(historyItem.getJadeFilesDBItem().getSourceFilename());
		this.lblMd5.setValue(historyItem.getJadeFilesDBItem().getMd5());
		this.lblFileSize.setValue(historyItem.getJadeFilesDBItem().getFileSize().toString());
		this.lblFileCreated.setValue(sdfOut.format(historyItem.getJadeFilesDBItem().getCreated())); 
		this.lblFileCreatedBy.setValue(historyItem.getJadeFilesDBItem().getCreatedBy());
		this.lblFileModified.setValue(sdfOut.format(historyItem.getJadeFilesDBItem().getModified())); 
		this.lblFileModifiedBy.setValue(historyItem.getJadeFilesDBItem().getModifiedBy());
	}
	
	public void resetLabelValues(){
		for (HorizontalLayout hl : this.detailLayouts) {
			for (int i = 0; i < hl.getComponentCount(); i++) {
				if(!((Label) hl.getComponent(i)).getId().contains("caption")){
					((Label) hl.getComponent(i)).setValue(null);
				}
			}
		}
	}
}
