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
    private Label lblSosftpId;
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
    private Label lblGuidCaption;
    private Label lblPidCaption;
    private Label lblPpidCaption;
    private Label lblOperationCaption;
    private Label lblFileHistoryCreatedCaption;
    private Label lblFileHistoryCreatedByCaption;
    private Label lblFileHistoryModifiedCaption;
    private Label lblFileHistoryModifiedByCaption;
    private Label lblProtocolCaption;
    private Label lblPortCaption;
    private Label lblLogFilenameCaption;
    private Label lblLastErrorMessageCaption;
    private Label lblSourceDirCaption;
    private Label lblSourceFilenameCaption;
    private Label lblSourceHostCaption;
    private Label lblSourceUserCaption;
    private Label lblTargetDirCaption;
    private Label lblTargetFilenameCaption;
    private Label lblTargetHostCaption;
    private Label lblTargetUserCaption;
    private Label lblJumpHostCaption;
    private Label lblJumpPortCaption;
    private Label lblJumpProtocolCaption;
    private Label lblJumpUserCaption;
    private Label lblTransferTimestampCaption;
    private Label lblMd5Caption;
    private Label lblFileSizeCaption;
    private Label lblMandatorCaption;
    private Label lblFileCreatedCaption;
    private Label lblFileCreatedByCaption;
    private Label lblFileModifiedCaption;
    private Label lblFileModifiedByCaption;
    private SimpleDateFormat sdfOut = new SimpleDateFormat("dd.MM.YYYY hh:mm:ss");
    private List<HorizontalLayout> detailLayouts = new ArrayList<HorizontalLayout>();
    private JadeBSMessages messages;
    private JadeFilesHistoryDBItem historyItem;
    private static final String CAPTION_STYLE = "jadeDetailLabelCaption";
    private static final String VALUE_STYLE = "jadeDetailLabel";
    private static final float DEFAULT_HEIGHT = 180.0f;
    private static final String DETAIL_LAYOUT_NOT_SET = "DetailLayout.notSet";

    public DetailLayout(JadeBSMessages messages) {
        this.messages = messages;
        this.setHeight(DEFAULT_HEIGHT, Unit.PIXELS);
        this.setWidth("100%");
        this.setMargin(true);
        this.setSpacing(true);
        this.addStyleName("jadeDetailLayout");
        initLabels();
        initCaptionLabels();
        initLayouts();
    }

    private Label initValueLabel(String id) {
        return initLabel(id, null, VALUE_STYLE);
    }

    private Label initCaptionLabel(String id, String value) {
        return initLabel(id, value, CAPTION_STYLE);
    }

    private Label initLabel(String id, String value, String styleName) {
        Label lbl = new Label();
        lbl.setId(id);
        lbl.addStyleName(styleName);
        lbl.setSizeUndefined();
        if (value != null) {
            lbl.setValue(value);
        }
        return lbl;
    }

    private HorizontalLayout initHlayout(Component... components) {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeFull();
        this.addComponent(hl);
        this.setExpandRatio(hl, 1);
        hl.addComponents(components);
        return hl;
    }

    private HorizontalLayout initHLabelLayout(Label caption, Label value) {
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

    private Label initDummyValueLabel() {
        Label lbl = new Label();
        lbl.addStyleName(VALUE_STYLE);
        return lbl;
    }

    private void initLayouts() {
        this.detailLayouts.add(initHlayout(initHLabelLayout(lblGuidCaption, lblGuid), initHLabelLayout(lblMd5Caption, lblMd5), initHLabelLayout(lblPortCaption, lblPort), initHLabelLayout(lblLastErrorMessageCaption, lblLastErrorMessage)));
        this.detailLayouts.add(initHlayout(initHLabelLayout(lblJumpHostCaption, lblJumpHost), initHLabelLayout(lblJumpPortCaption, lblJumpPort), initHLabelLayout(lblJumpProtocolCaption, lblJumpProtocol), initHLabelLayout(lblJumpUserCaption, lblJumpUser)));
        this.detailLayouts.add(initHlayout(initHLabelLayout(lblSourceDirCaption, lblSourceDir), initHLabelLayout(lblSourceFilenameCaption, lblSourceFilename), initHLabelLayout(lblSourceUserCaption, lblSourceUser), initHLabelLayout(lblLogFilenameCaption, lblLogFilename)));
        this.detailLayouts.add(initHlayout(initHLabelLayout(lblTargetDirCaption, lblTargetDir), initHLabelLayout(lblTargetUserCaption, lblTargetUser), initHLabelLayout(initCaptionLabel("dummy1Caption", ""), initDummyValueLabel()), initHLabelLayout(initCaptionLabel("dummy2Caption", ""), initDummyValueLabel())));
    }

    @SuppressWarnings("unused")
    private void initDetailedLayouts() {
        this.detailLayouts.add(initHlayout(initHLabelLayout(lblGuidCaption, lblGuid), initHLabelLayout(lblPidCaption, lblPid), initHLabelLayout(lblPpidCaption, lblPpid), initHLabelLayout(lblOperationCaption, lblOperation)));
        this.detailLayouts.add(initHlayout(initHLabelLayout(lblFileHistoryCreatedCaption, lblFileHistoryCreated), initHLabelLayout(lblFileHistoryCreatedByCaption, lblFileHistoryCreatedBy), initHLabelLayout(lblFileHistoryModifiedCaption, lblFileHistoryModified), initHLabelLayout(lblFileHistoryModifiedByCaption, lblFileHistoryModifiedBy)));
        this.detailLayouts.add(initHlayout(initHLabelLayout(lblProtocolCaption, lblProtocol), initHLabelLayout(lblPortCaption, lblPort), initHLabelLayout(lblLogFilenameCaption, lblLogFilename), initHLabelLayout(lblLastErrorMessageCaption, lblLastErrorMessage)));
        this.detailLayouts.add(initHlayout(initHLabelLayout(lblSourceDirCaption, lblSourceDir), initHLabelLayout(lblSourceFilenameCaption, lblSourceFilename), initHLabelLayout(lblSourceHostCaption, lblSourceHost), initHLabelLayout(lblSourceUserCaption, lblSourceUser)));
        this.detailLayouts.add(initHlayout(initHLabelLayout(lblTargetDirCaption, lblTargetDir), initHLabelLayout(lblTargetFilenameCaption, lblTargetFilename), initHLabelLayout(lblTargetHostCaption, lblTargetHost), initHLabelLayout(lblTargetUserCaption, lblTargetUser)));
        this.detailLayouts.add(initHlayout(initHLabelLayout(lblJumpHostCaption, lblJumpHost), initHLabelLayout(lblJumpPortCaption, lblJumpPort), initHLabelLayout(lblJumpProtocolCaption, lblJumpProtocol), initHLabelLayout(lblJumpUserCaption, lblJumpUser)));
        this.detailLayouts.add(initHlayout(initHLabelLayout(lblTransferTimestampCaption, lblTransferTimestamp), initHLabelLayout(lblMd5Caption, lblMd5), initHLabelLayout(lblFileSizeCaption, lblFileSize), initHLabelLayout(lblMandatorCaption, lblMandator)));
        this.detailLayouts.add(initHlayout(initHLabelLayout(lblFileCreatedCaption, lblFileCreated), initHLabelLayout(lblFileCreatedByCaption, lblFileCreatedBy), initHLabelLayout(lblFileModifiedCaption, lblFileModified), initHLabelLayout(lblFileModifiedByCaption, lblFileModifiedBy)));
        // unused: lblSourceHostIp; maybe needed if source host name is not
        // available
        // unused: lblTargetHostIp; maybe needed if target host name is not
        // available
        // unused: lblJumpHostIp; maybe needed if jump host name is not
        // available
        // unused: lblSosftpId; referenced id from DB for JadeFilesDBItem from
        // JadeFilesHistoryDBItem
        // unused: lblId; corresponding id from DB for JadeFilesDBItem
    }

    private void initLabels() {
        // JadeFilesHistoryDBItem properties
        this.lblGuid = initValueLabel("GUID");
        this.lblSosftpId = initValueLabel("SosFtpId");
        this.lblOperation = initValueLabel("Operation");
        this.lblTransferTimestamp = initValueLabel("Timestamp");
        this.lblPid = initValueLabel("PID");
        this.lblPpid = initValueLabel("PPID");
        this.lblTargetHost = initValueLabel("TargetHost");
        this.lblTargetHostIp = initValueLabel("TargetHostIp");
        this.lblTargetUser = initValueLabel("TargetUser");
        this.lblTargetDir = initValueLabel("TargetDir");
        this.lblTargetFilename = initValueLabel("TargetFilename");
        this.lblProtocol = initValueLabel("Protocol");
        this.lblPort = initValueLabel("Port");
        this.lblStatus = initValueLabel("Status");
        this.lblLastErrorMessage = initValueLabel("LastErrorMessage");
        this.lblLogFilename = initValueLabel("LogFilename");
        this.lblJumpHost = initValueLabel("JumpHost");
        this.lblJumpHostIp = initValueLabel("JumpHostIp");
        this.lblJumpUser = initValueLabel("JumpUser");
        this.lblJumpProtocol = initValueLabel("JumpProtocol");
        this.lblJumpPort = initValueLabel("JumpPort");
        this.lblFileHistoryCreated = initValueLabel("FileHistoryCreated");
        this.lblFileHistoryCreatedBy = initValueLabel("FileHistoryCreatedBy");
        this.lblFileHistoryModified = initValueLabel("FileHistoryModified");
        this.lblFileHistoryModifiedBy = initValueLabel("FileHistoryModifiedBy");
        this.lblId = initValueLabel("ID");
        this.lblMandator = initValueLabel("Mandator");
        this.lblSourceHost = initValueLabel("SourceHost");
        this.lblSourceHostIp = initValueLabel("SourceHostIp");
        this.lblSourceUser = initValueLabel("SourceUser");
        this.lblSourceDir = initValueLabel("SourceDir");
        this.lblSourceFilename = initValueLabel("SourceFilename");
        this.lblMd5 = initValueLabel("MD5");
        this.lblFileSize = initValueLabel("FileSize");
        this.lblFileCreated = initValueLabel("FileCreated");
        this.lblFileCreatedBy = initValueLabel("FileCreatedBy");
        this.lblFileModified = initValueLabel("FileModified");
        this.lblFileModifiedBy = initValueLabel("FileModifiedBy");
    }

    public void setLabelValues(JadeFilesHistoryDBItem historyItem) {
        this.historyItem = historyItem;
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
        if (historyItem.getLastErrorMessage() != null && !"NULL".equals(historyItem.getLastErrorMessage())) {
            this.lblLastErrorMessage.setValue(historyItem.getLastErrorMessage());
        } else {
            this.lblLastErrorMessage.setValue(messages.getValue("DetailLayout.none"));
        }
        this.lblLogFilename.setValue(historyItem.getLogFilename());
        if (historyItem.getJumpHost() != null && !"NULL".equals(historyItem.getJumpHost())) {
            this.lblJumpHost.setValue(historyItem.getJumpHost());
        } else {
            this.lblJumpHost.setValue(messages.getValue(DETAIL_LAYOUT_NOT_SET));
        }
        this.lblJumpHostIp.setValue(historyItem.getJumpHostIp());
        if (historyItem.getJumpUser() != null && !"NULL".equals(historyItem.getJumpUser())) {
            this.lblJumpUser.setValue(historyItem.getJumpUser());
        } else {
            this.lblJumpUser.setValue(messages.getValue(DETAIL_LAYOUT_NOT_SET));
        }
        this.lblJumpProtocol.setValue(historyItem.getJumpProtocol());
        this.lblJumpPort.setValue(historyItem.getJumpPort().toString());
        this.lblFileHistoryCreated.setValue(sdfOut.format(historyItem.getCreated()));
        this.lblFileHistoryCreatedBy.setValue(historyItem.getCreatedBy());
        this.lblFileHistoryModified.setValue(sdfOut.format(historyItem.getModified()));
        this.lblFileHistoryModifiedBy.setValue(historyItem.getModifiedBy());
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

    private void initCaptionLabels() {
        lblGuidCaption = initCaptionLabel("guidCaption", messages.getValue("DetailLayout.guid"));
        lblPidCaption = initCaptionLabel("pidCaption", messages.getValue("DetailLayout.pid"));
        lblPpidCaption = initCaptionLabel("pPidCaption", messages.getValue("DetailLayout.ppid"));
        lblOperationCaption = initCaptionLabel("operationCaption", messages.getValue("DetailLayout.operation"));
        lblFileHistoryCreatedCaption = initCaptionLabel("fileHistoryCreatedCaption", messages.getValue("DetailLayout.fileHistoryCreated"));
        lblFileHistoryCreatedByCaption = initCaptionLabel("fileHistoryCreatedByCaption", messages.getValue("DetailLayout.fileHistoryCreatedBy"));
        lblFileHistoryModifiedCaption = initCaptionLabel("fileHistoryModifiedCaption", messages.getValue("DetailLayout.fileHistoryModified"));
        lblFileHistoryModifiedByCaption = initCaptionLabel("fileHistoryModifiedByCaption", messages.getValue("DetailLayout.fileHistoryModifiedBy"));
        lblProtocolCaption = initCaptionLabel("protocolCaption", messages.getValue("DetailLayout.protocol"));
        lblPortCaption = initCaptionLabel("portCaption", messages.getValue("DetailLayout.port"));
        lblLogFilenameCaption = initCaptionLabel("logFilenameCaption", messages.getValue("DetailLayout.logFilename"));
        lblLastErrorMessageCaption = initCaptionLabel("lastErrorMessageCaption", messages.getValue("DetailLayout.lastErrorMessage"));
        lblSourceDirCaption = initCaptionLabel("sourceDirCaption", messages.getValue("DetailLayout.sourceDirectory"));
        lblSourceFilenameCaption = initCaptionLabel("sourceFilenameCaption", messages.getValue("DetailLayout.sourceFilename"));
        lblSourceHostCaption = initCaptionLabel("sourceHostCaption", messages.getValue("DetailLayout.sourceHost"));
        lblSourceUserCaption = initCaptionLabel("sourceUserCaption", messages.getValue("DetailLayout.sourceUser"));
        lblTargetDirCaption = initCaptionLabel("targetDirCaption", messages.getValue("DetailLayout.targetDirectory"));
        lblTargetFilenameCaption = initCaptionLabel("targetFilenameCaption", messages.getValue("DetailLayout.targetFilename"));
        lblTargetHostCaption = initCaptionLabel("targetHostCaption", messages.getValue("DetailLayout.targetHost"));
        lblTargetUserCaption = initCaptionLabel("targetUserCaption", messages.getValue("DetailLayout.targetUser"));
        lblJumpHostCaption = initCaptionLabel("jumpHostCaption", messages.getValue("DetailLayout.jumpHost"));
        lblJumpPortCaption = initCaptionLabel("jumpPortCaption", messages.getValue("DetailLayout.jumpPort"));
        lblJumpProtocolCaption = initCaptionLabel("jumpProtocolCaption", messages.getValue("DetailLayout.jumpProtocol"));
        lblJumpUserCaption = initCaptionLabel("jumpUserCaption", messages.getValue("DetailLayout.jumpUser"));
        lblTransferTimestampCaption = initCaptionLabel("transferTimestampCaption", messages.getValue("DetailLayout.transferTimestamp"));
        lblMd5Caption = initCaptionLabel("md5Caption", messages.getValue("DetailLayout.md5"));
        lblFileSizeCaption = initCaptionLabel("fileSizeCaption", messages.getValue("DetailLayout.fileSize"));
        lblMandatorCaption = initCaptionLabel("mandatorCaption", messages.getValue("DetailLayout.mandator"));
        lblFileCreatedCaption = initCaptionLabel("fileCreatedCaption", messages.getValue("DetailLayout.fileCreated"));
        lblFileCreatedByCaption = initCaptionLabel("fileCreatedByCaption", messages.getValue("DetailLayout.fileCreatedBy"));
        lblFileModifiedCaption = initCaptionLabel("fileModifiedCaption", messages.getValue("DetailLayout.fileModified"));
        lblFileModifiedByCaption = initCaptionLabel("fileModifiedByCaption", messages.getValue("DetailLayout.fileModifiedBy"));
    }

    public void resetLabelValues() {
        for (HorizontalLayout hl : this.detailLayouts) {
            for (int i = 0; i < hl.getComponentCount(); i++) {
                if (!((Label) hl.getComponent(i)).getId().contains("caption")) {
                    ((Label) hl.getComponent(i)).setValue(null);
                }
            }
        }
    }

    public void refreshCaptions(Locale locale) {
        lblGuidCaption.setValue(messages.getValue("DetailLayout.guid", locale));
        lblPidCaption.setValue(messages.getValue("DetailLayout.pid", locale));
        lblPpidCaption.setValue(messages.getValue("DetailLayout.ppid", locale));
        lblOperationCaption.setValue(messages.getValue("DetailLayout.operation", locale));
        lblFileHistoryCreatedCaption.setValue(messages.getValue("DetailLayout.fileHistoryCreated", locale));
        lblFileHistoryCreatedByCaption.setValue(messages.getValue("DetailLayout.fileHistoryCreatedBy", locale));
        lblFileHistoryModifiedCaption.setValue(messages.getValue("DetailLayout.fileHistoryModified", locale));
        lblFileHistoryModifiedByCaption.setValue(messages.getValue("DetailLayout.fileHistoryModifiedBy", locale));
        lblProtocolCaption.setValue(messages.getValue("DetailLayout.protocol", locale));
        lblPortCaption.setValue(messages.getValue("DetailLayout.port", locale));
        lblLogFilenameCaption.setValue(messages.getValue("DetailLayout.logFilename", locale));
        lblLastErrorMessageCaption.setValue(messages.getValue("DetailLayout.lastErrorMessage", locale));
        lblSourceDirCaption.setValue(messages.getValue("DetailLayout.sourceDirectory", locale));
        lblSourceFilenameCaption.setValue(messages.getValue("DetailLayout.sourceFilename", locale));
        lblSourceHostCaption.setValue(messages.getValue("DetailLayout.sourceHost", locale));
        lblSourceUserCaption.setValue(messages.getValue("DetailLayout.sourceUser", locale));
        lblTargetDirCaption.setValue(messages.getValue("DetailLayout.targetDirectory", locale));
        lblTargetFilenameCaption.setValue(messages.getValue("DetailLayout.targetFilename", locale));
        lblTargetHostCaption.setValue(messages.getValue("DetailLayout.targetHost", locale));
        lblTargetUserCaption.setValue(messages.getValue("DetailLayout.targetUser", locale));
        lblJumpHostCaption.setValue(messages.getValue("DetailLayout.jumpHost", locale));
        lblJumpPortCaption.setValue(messages.getValue("DetailLayout.jumpPort", locale));
        lblJumpProtocolCaption.setValue(messages.getValue("DetailLayout.jumpProtocol", locale));
        lblJumpUserCaption.setValue(messages.getValue("DetailLayout.jumpUser", locale));
        lblTransferTimestampCaption.setValue(messages.getValue("DetailLayout.transferTimestamp", locale));
        lblMd5Caption.setValue(messages.getValue("DetailLayout.md5", locale));
        lblFileSizeCaption.setValue(messages.getValue("DetailLayout.fileSize", locale));
        lblMandatorCaption.setValue(messages.getValue("DetailLayout.mandator", locale));
        lblFileCreatedCaption.setValue(messages.getValue("DetailLayout.fileCreated", locale));
        lblFileCreatedByCaption.setValue(messages.getValue("DetailLayout.fileCreatedBy", locale));
        lblFileModifiedCaption.setValue(messages.getValue("DetailLayout.fileModified", locale));
        lblFileModifiedByCaption.setValue(messages.getValue("DetailLayout.fileModifiedBy", locale));
        if (historyItem != null && historyItem.getLastErrorMessage() != null && !"NULL".equals(historyItem.getLastErrorMessage())) {
            this.lblLastErrorMessage.setValue(historyItem.getLastErrorMessage());
        } else {
            this.lblLastErrorMessage.setValue(messages.getValue("DetailLayout.none", locale));
        }
        if (historyItem != null && historyItem.getJumpHost() != null && !"NULL".equals(historyItem.getJumpHost())) {
            this.lblJumpHost.setValue(historyItem.getJumpHost());
        } else {
            this.lblJumpHost.setValue(messages.getValue(DETAIL_LAYOUT_NOT_SET, locale));
        }
        if (historyItem != null && historyItem.getJumpUser() != null && !"NULL".equals(historyItem.getJumpUser())) {
            this.lblJumpUser.setValue(historyItem.getJumpUser());
        } else {
            this.lblJumpUser.setValue(messages.getValue(DETAIL_LAYOUT_NOT_SET, locale));
        }
    }

}
