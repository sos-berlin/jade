package com.sos.jade.db;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.sos.hibernate.classes.DbItem;

@Entity
@Table(name = "YADE_FILES")
@SequenceGenerator(name = "YADE_FI_ID_SEQ", sequenceName = "YADE_FI_ID_SEQ", allocationSize = 1)
public class DBItemYadeFiles extends DbItem implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String YADE_FILES_SEQUENCE = "YADE_FI_ID_SEQ";
    /** Primary key */
    private Long id;

    /** Others */
    private Long transferId;
    private Long interventionTransferId;
    private String sourcePath;
    private String targetPath;
    private Long size;
    private Date modificationDate;
    private Integer state;
    private String errorCode;
    private String errorMessage;
    private String integrityHash;
    private Date modified;
    
    public DBItemYadeFiles() {
    }

    /** Primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = YADE_FILES_SEQUENCE)
    @Column(name = "`ID`", nullable = false)
    public Long getId() {
        return id;
    }
    
    /** Primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = YADE_FILES_SEQUENCE)
    @Column(name = "`ID`", nullable = false)
    public void setId(Long id) {
        this.id = id;
    }
    
    @Column(name = "`TRANSFER_ID`", nullable = false)
    public Long getTransferId() {
        return transferId;
    }
    
    @Column(name = "`TRANSFER_ID`", nullable = false)
    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }
    
    @Column(name = "`INTERVENTION_TRANSFER_ID`", nullable = true)
    public Long getInterventionTransferId() {
        return interventionTransferId;
    }
    
    @Column(name = "`INTERVENTION_TRANSFER_ID`", nullable = true)
    public void setInterventionTransferId(Long interventionTransferId) {
        this.interventionTransferId = interventionTransferId;
    }
    
    @Column(name = "`SOURCE_PATH`", nullable = false)
    public String getSourcePath() {
        return sourcePath;
    }
    
    @Column(name = "`SOURCE_PATH`", nullable = false)
    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }
    
    @Column(name = "`TARGET_PATH`", nullable = true)
    public String getTargetPath() {
        return targetPath;
    }
    
    @Column(name = "`TARGET_PATH`", nullable = true)
    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }
    
    @Column(name = "`SIZE`", nullable = true)
    public Long getSize() {
        return size;
    }
    
    @Column(name = "`SIZE`", nullable = true)
    public void setSize(Long size) {
        this.size = size;
    }
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "`MODIFICATION_DATE`", nullable = true)
    public Date getModificationDate() {
        return modificationDate;
    }
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "`MODIFICATION_DATE`", nullable = true)
    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }
    
    @Column(name = "`STATE`", nullable = false)
    public Integer getState() {
        return state;
    }
    
    @Column(name = "`STATE`", nullable = false)
    public void setState(Integer state) {
        this.state = state;
    }
    
    @Column(name = "`ERROR_CODE`", nullable = true)
    public String getErrorCode() {
        return errorCode;
    }
    
    @Column(name = "`ERROR_CODE`", nullable = true)
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    @Column(name = "`ERROR_MESSAGE`", nullable = true)
    public String getErrorMessage() {
        return errorMessage;
    }
    
    @Column(name = "`ERROR_MESSAGE`", nullable = true)
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    @Column(name = "`INTEGRITY_HASH`", nullable = true)
    public String getIntegrityHash() {
        return integrityHash;
    }
    
    @Column(name = "`INTEGRITY_HASH`", nullable = true)
    public void setIntegrityHash(String integrityHash) {
        this.integrityHash = integrityHash;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "`MODIFIED`", nullable = false)
    public Date getModified() {
        return modified;
    }
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "`MODIFIED`", nullable = false)
    public void setModified(Date modified) {
        this.modified = modified;
    }
    
    @Override
    public int hashCode() {
        // always build on unique constraint
        return new HashCodeBuilder().append(transferId).append(sourcePath).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        // always compare on unique constraint
        if (other == this) {
            return true;
        }
        if (!(other instanceof DBItemYadeFiles)) {
            return false;
        }
        DBItemYadeFiles rhs = ((DBItemYadeFiles) other);
        return new EqualsBuilder().append(transferId, rhs.transferId).append(sourcePath, rhs.sourcePath).isEquals();
    }

}