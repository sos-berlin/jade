//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.02 at 02:31:20 PM MESZ 
//


package com.sos.jade.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}Operation"/>
 *         &lt;element ref="{}Client" minOccurs="0"/>
 *         &lt;element ref="{}JobScheduler" minOccurs="0"/>
 *         &lt;element ref="{}Notifications" minOccurs="0"/>
 *         &lt;element ref="{}CredentialStore" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="profile_id" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "operation",
    "client",
    "jobScheduler",
    "notifications",
    "credentialStore"
})
@XmlRootElement(name = "Profile")
public class Profile {

    @XmlElement(name = "Operation", required = true)
    protected Operation operation;
    @XmlElement(name = "Client")
    protected Client client;
    @XmlElement(name = "JobScheduler")
    protected JobScheduler jobScheduler;
    @XmlElement(name = "Notifications")
    protected Notifications notifications;
    @XmlElement(name = "CredentialStore")
    protected CredentialStore credentialStore;
    @XmlAttribute(name = "profile_id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String profileId;

    /**
     * Gets the value of the operation property.
     * 
     * @return
     *     possible object is
     *     {@link Operation }
     *     
     */
    public Operation getOperation() {
        return operation;
    }

    /**
     * Sets the value of the operation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Operation }
     *     
     */
    public void setOperation(Operation value) {
        this.operation = value;
    }

    /**
     * Gets the value of the client property.
     * 
     * @return
     *     possible object is
     *     {@link Client }
     *     
     */
    public Client getClient() {
        return client;
    }

    /**
     * Sets the value of the client property.
     * 
     * @param value
     *     allowed object is
     *     {@link Client }
     *     
     */
    public void setClient(Client value) {
        this.client = value;
    }

    /**
     * Gets the value of the jobScheduler property.
     * 
     * @return
     *     possible object is
     *     {@link JobScheduler }
     *     
     */
    public JobScheduler getJobScheduler() {
        return jobScheduler;
    }

    /**
     * Sets the value of the jobScheduler property.
     * 
     * @param value
     *     allowed object is
     *     {@link JobScheduler }
     *     
     */
    public void setJobScheduler(JobScheduler value) {
        this.jobScheduler = value;
    }

    /**
     * Gets the value of the notifications property.
     * 
     * @return
     *     possible object is
     *     {@link Notifications }
     *     
     */
    public Notifications getNotifications() {
        return notifications;
    }

    /**
     * Sets the value of the notifications property.
     * 
     * @param value
     *     allowed object is
     *     {@link Notifications }
     *     
     */
    public void setNotifications(Notifications value) {
        this.notifications = value;
    }

    /**
     * Gets the value of the credentialStore property.
     * 
     * @return
     *     possible object is
     *     {@link CredentialStore }
     *     
     */
    public CredentialStore getCredentialStore() {
        return credentialStore;
    }

    /**
     * Sets the value of the credentialStore property.
     * 
     * @param value
     *     allowed object is
     *     {@link CredentialStore }
     *     
     */
    public void setCredentialStore(CredentialStore value) {
        this.credentialStore = value;
    }

    /**
     * Gets the value of the profileId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfileId() {
        return profileId;
    }

    /**
     * Sets the value of the profileId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfileId(String value) {
        this.profileId = value;
    }

}
