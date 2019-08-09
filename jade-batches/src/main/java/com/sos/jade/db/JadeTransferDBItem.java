package com.sos.jade.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.hibernate.Session;
import org.hibernate.StatelessSession;

import com.sos.hibernate.classes.DbItem;

/** @author Uwe Risse */
@Entity
@Table(name = "SOSJADE_TRANSFERS")
public class JadeTransferDBItem extends DbItem {

    private Long transferId;
    private String mandator;
    private String sourceHost;
    private String sourceHostIp;
    private String sourceUser;
    private String sourceDir;
    private String targetHost;
    private String targetHostIp;
    private String targetUser;
    private String targetDir;
    private Integer protocolType;
    private Integer port;
    private Integer status;
    private String lastErrorMessage;
    private Integer filesCount;
    private String profileName;
    private String profile;
    private String log;
    private Integer commandType;
    private String command;
    private Date startTime;
    private Date endTime;
    private Long fileSize;
    private Date created;
    private String createdBy;
    private Date modified;
    private String modifiedBy;
    private List<JadeTransferDetailDBItem> jadeTransferDetailDBItems = new ArrayList<JadeTransferDetailDBItem>();
 
    @OneToMany(mappedBy = "[transferId")
    public List<JadeTransferDetailDBItem> getJadeTransferDetailDBItems() {
        return jadeTransferDetailDBItems;
    }

    public void setJadeTransferDetailDBItems(List<JadeTransferDetailDBItem> jadeTransferDetailDBItems) {
        this.jadeTransferDetailDBItems = jadeTransferDetailDBItems;
    }

    public JadeTransferDBItem() {
        //
    }
 
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "[TRANSFER_ID]", nullable = false)
    public Long getTransferId() {
        return transferId;
    }

    @Column(name = "[TRANSFER_ID]", nullable = false)
    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    @Column(name = "[MANDATOR]", nullable = false)
    public void setMandator(String mandator) {
        this.mandator = mandator;
    }

    @Column(name = "[MANDATOR]", nullable = false)
    public String getMandator() {
        return mandator;
    }

    @Column(name = "[FILES_SIZE]", nullable = false)
    public Long getFileSize() {
        return fileSize;
    }

    @Column(name = "[FILES_SIZE]", nullable = false)
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Column(name = "[SOURCE_HOST]", nullable = true)
    public String getSourceHost() {
        return sourceHost;
    }

    @Column(name = "[SOURCE_HOST]", nullable = true)
    public void setSourceHost(String sourceHost) {
        this.sourceHost = sourceHost;
    }

    @Column(name = "[SOURCE_HOST_IP]", nullable = false)
    public void setSourceHostIp(String sourceHostIp) {
        this.sourceHostIp = sourceHostIp;
    }

    @Column(name = "[SOURCE_HOST_IP]", nullable = false)
    public String getSourceHostIp() {
        return sourceHostIp;
    }

    @Column(name = "[SOURCE_USER]", nullable = false)
    public void setSourceUser(String sourceUser) {
        this.sourceUser = sourceUser;
    }

    @Column(name = "[SOURCE_USER]", nullable = false)
    public String getSourceUser() {
        return sourceUser;
    }

    @Column(name = "[SOURCE_DIR]", nullable = false)
    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    @Column(name = "[SOURCE_DIR]", nullable = false)
    public String getSourceDir() {
        return sourceDir;
    }

    @Column(name = "[TARGET_HOST]", nullable = false)
    public String getTargetHost() {
        return targetHost;
    }

    @Column(name = "[TARGET_HOST]", nullable = false)
    public void setTargetHost(String targetHost) {
        this.targetHost = targetHost;
    }

    @Column(name = "[TARGET_HOST_IP]", nullable = false)
    public void setTargetHostIp(String targetHostIp) {
        this.targetHostIp = targetHostIp;
    }

    @Column(name = "[TARGET_HOST_IP]", nullable = false)
    public String getTargetHostIp() {
        return targetHostIp;
    }

    @Column(name = "[TARGET_USER]", nullable = false)
    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }

    @Column(name = "[TARGET_USER]", nullable = false)
    public String getTargetUser() {
        return targetUser;
    }

    @Column(name = "[TARGET_DIR]", nullable = false)
    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    @Column(name = "[TARGET_DIR]", nullable = false)
    public String getTargetDir() {
        return targetDir;
    }

    @Column(name = "[PROTOCOL_TYPE]", nullable = false)
    public void setProtocolType(Integer protocolType) {
        this.protocolType = protocolType;
    }

    @Column(name = "[PROTOCOL_TYPE]", nullable = false)
    public Integer getProtocolType() {
        return protocolType;
    }

    @Column(name = "[PORT]", nullable = false)
    public void setPort(Integer port) {
        this.port = port;
    }

    @Column(name = "[PORT]", nullable = false)
    public Integer getPort() {
        return port;
    }

    @Column(name = "[FILES_COUNT]", nullable = false)
    public void setFilesCount(Integer filesCount) {
        this.filesCount = filesCount;
    }

    @Column(name = "[FILES_COUNT]", nullable = false)
    public Integer getFilesCount() {
        return filesCount;
    }

    @Column(name = "[PROFILE_NAME]", nullable = false)
    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    @Column(name = "[PROFILE_NAME]", nullable = false)
    public String getProfileName() {
        return profileName;
    }

    @Lob
    @Column(name = "[PROFILE]", nullable = false)
    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Lob
    @Column(name = "[PROFILE]", nullable = false)
    public String getProfile() {
        return profile;
    }

    @Column(name = "[COMMAND_TYPE]", nullable = false)
    public void setCommandType(Integer commandType) {
        this.commandType = commandType;
    }

    @Column(name = "[COMMAND_TYPE]", nullable = false)
    public Integer getCommandType() {
        return commandType;
    }

    @Lob
    @Column(name = "[COMMAND]", nullable = false)
    public void setCommand(String command) {
        this.command = command;
    }

    @Lob
    @Column(name = "[COMMAND]", nullable = false)
    public String getCommand() {
        return command;
    }

    @Lob
    @Column(name = "[LOG]", nullable = false)
    public void setLog(String log) {
        this.log = log;
    }

    @Lob
    @Column(name = "[LOG]", nullable = false)
    public String getLog() {
        return log;
    }

    @Column(name = "[STATUS]", nullable = false)
    public void setStatus(Integer status) {
        this.status = status;
    }

    @Column(name = "[STATUS]", nullable = false)
    public Integer getStatus() {
        return status;
    }

    @Lob
    @Column(name = "[LAST_ERROR_MESSAGE]", nullable = true)
    public void setLastErrorMessage(String lastErrorMessage) {
        this.lastErrorMessage = lastErrorMessage;
    }

    @Lob
    @Column(name = "[LAST_ERROR_MESSAGE]", nullable = true)
    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    @Column(name = "[START_TIME]", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Column(name = "[START_TIME]", nullable = false)
    public Date getStartTime() {
        return startTime;
    }

    @Column(name = "[END_TIME]", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Column(name = "[END_TIME]", nullable = false)
    public Date getEndTime() {
        return endTime;
    }

    @Transient
    public String getStatusValue() {
        return String.valueOf(status);

    }

    @Transient
    public String getStartTimeIso() {
        if (this.getStartTime() == null) {
            return "";
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            String getStartTimeIso = formatter.format(this.getStartTime());
            return getStartTimeIso;
        }
    }

    @Transient
    public String getEndTimeIso() {
        if (this.getEndTime() == null) {
            return "";
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            return formatter.format(this.getEndTime());
        }
    }

    @Column(name = "[MODIFIED_BY]", nullable = false)
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Column(name = "[MODIFIED_BY]", nullable = false)
    public String getModifiedBy() {
        return modifiedBy;
    }

    @Column(name = "[CREATED_BY]", nullable = false)
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "[CREATED_BY]", nullable = false)
    public String getCreatedBy() {
        return createdBy;
    }

    @Column(name = "[CREATED]", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreated() {
        return created;
    }

    @Column(name = "[CREATED]", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public void setCreated(Date created) {
        this.created = created;
    }

    @Column(name = "[MODIFIED]", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getModified() {
        return modified;
    }

    @Column(name = "[MODIFIED]", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    public void setModified(Date modified) {
        this.modified = modified;
    }

    @Transient
    public void addTransferDetail(JadeTransferDetailDBItem transferDetail) {
        this.getJadeTransferDetailDBItems().add(transferDetail);
        transferDetail.setJadeTransferDBItem(this);
    }
  
}