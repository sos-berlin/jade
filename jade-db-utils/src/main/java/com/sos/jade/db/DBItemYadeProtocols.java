package com.sos.jade.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.sos.hibernate.classes.DbItem;

@Entity
@Table(name = "YADE_PROTOCOLS")
@SequenceGenerator(name = "YADE_PR_ID_SEQ", sequenceName = "YADE_PR_ID_SEQ", allocationSize = 1)
public class DBItemYadeProtocols extends DbItem implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String YADE_PROTOCOLS_SEQUENCE = "YADE_PR_ID_SEQ";
    /** Primary key */
    private Long id;
    /** Others */
    private String hostname;
    private Integer port;
    private Integer protocol;
    private String account;
    
    public DBItemYadeProtocols() {
    }
    
    /** Primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = YADE_PROTOCOLS_SEQUENCE)
    @Column(name = "`ID`", nullable = false)
    public Long getId() {
        return id;
    }
    
    /** Primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = YADE_PROTOCOLS_SEQUENCE)
    @Column(name = "`ID`", nullable = false)
    public void setId(Long id) {
        this.id = id;
    }
    
    @Column(name = "`HOSTNAME`", nullable = false)
    public String getHostname() {
        return hostname;
    }
    
    @Column(name = "`HOSTNAME`", nullable = false)
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    
    @Column(name = "`PORT`", nullable = true)
    public Integer getPort() {
        return port;
    }
    
    @Column(name = "`PORT`", nullable = true)
    public void setPort(Integer port) {
        this.port = port;
    }
    
    @Column(name = "`PROTOCOL`", nullable = false)
    public Integer getProtocol() {
        return protocol;
    }
    
    @Column(name = "`PROTOCOL`", nullable = false)
    public void setProtocol(Integer protocol) {
        this.protocol = protocol;
    }
    
    @Column(name = "`ACCOUNT`", nullable = true)
    public String getAccount() {
        return account;
    }
    
    @Column(name = "`ACCOUNT`", nullable = true)
    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public int hashCode() {
        // always build on unique constraint
        return new HashCodeBuilder().append(hostname).append(port).append(protocol).append(account).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        // always compare on unique constraint
        if (other == this) {
            return true;
        }
        if (!(other instanceof DBItemYadeProtocols)) {
            return false;
        }
        DBItemYadeProtocols rhs = ((DBItemYadeProtocols) other);
        return new EqualsBuilder().append(hostname, rhs.hostname).append(port, rhs.port).append(protocol, rhs.protocol).
                append(account, rhs.account).isEquals();
    }

}
