//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.08.28 um 03:50:36 PM CEST 
//


package com.sos.jade.converter.generated;

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
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
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
    "notifications"
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
    @XmlAttribute(name = "profile_id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String profileId;

    /**
     * Ruft den Wert der operation-Eigenschaft ab.
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
     * Legt den Wert der operation-Eigenschaft fest.
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
     * Ruft den Wert der client-Eigenschaft ab.
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
     * Legt den Wert der client-Eigenschaft fest.
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
     * Ruft den Wert der jobScheduler-Eigenschaft ab.
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
     * Legt den Wert der jobScheduler-Eigenschaft fest.
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
     * Ruft den Wert der notifications-Eigenschaft ab.
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
     * Legt den Wert der notifications-Eigenschaft fest.
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
     * Ruft den Wert der profileId-Eigenschaft ab.
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
     * Legt den Wert der profileId-Eigenschaft fest.
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
