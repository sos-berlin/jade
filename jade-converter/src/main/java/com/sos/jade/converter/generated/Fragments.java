//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.08.28 um 03:50:36 PM CEST 
//


package com.sos.jade.converter.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element ref="{}ProtocolFragments"/>
 *         &lt;element ref="{}AlternativeFragments" minOccurs="0"/>
 *         &lt;element ref="{}NotificationFragments" minOccurs="0"/>
 *         &lt;element ref="{}CredentialStoreFragments" minOccurs="0"/>
 *         &lt;element ref="{}MailServerFragments" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "protocolFragments",
    "alternativeFragments",
    "notificationFragments",
    "credentialStoreFragments",
    "mailServerFragments"
})
@XmlRootElement(name = "Fragments")
public class Fragments {

    @XmlElement(name = "ProtocolFragments", required = true)
    protected ProtocolFragments protocolFragments;
    @XmlElement(name = "AlternativeFragments")
    protected AlternativeFragments alternativeFragments;
    @XmlElement(name = "NotificationFragments")
    protected NotificationFragments notificationFragments;
    @XmlElement(name = "CredentialStoreFragments")
    protected CredentialStoreFragments credentialStoreFragments;
    @XmlElement(name = "MailServerFragments")
    protected MailServerFragments mailServerFragments;

    /**
     * Ruft den Wert der protocolFragments-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ProtocolFragments }
     *     
     */
    public ProtocolFragments getProtocolFragments() {
        return protocolFragments;
    }

    /**
     * Legt den Wert der protocolFragments-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ProtocolFragments }
     *     
     */
    public void setProtocolFragments(ProtocolFragments value) {
        this.protocolFragments = value;
    }

    /**
     * Ruft den Wert der alternativeFragments-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AlternativeFragments }
     *     
     */
    public AlternativeFragments getAlternativeFragments() {
        return alternativeFragments;
    }

    /**
     * Legt den Wert der alternativeFragments-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AlternativeFragments }
     *     
     */
    public void setAlternativeFragments(AlternativeFragments value) {
        this.alternativeFragments = value;
    }

    /**
     * Ruft den Wert der notificationFragments-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link NotificationFragments }
     *     
     */
    public NotificationFragments getNotificationFragments() {
        return notificationFragments;
    }

    /**
     * Legt den Wert der notificationFragments-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link NotificationFragments }
     *     
     */
    public void setNotificationFragments(NotificationFragments value) {
        this.notificationFragments = value;
    }

    /**
     * Ruft den Wert der credentialStoreFragments-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CredentialStoreFragments }
     *     
     */
    public CredentialStoreFragments getCredentialStoreFragments() {
        return credentialStoreFragments;
    }

    /**
     * Legt den Wert der credentialStoreFragments-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CredentialStoreFragments }
     *     
     */
    public void setCredentialStoreFragments(CredentialStoreFragments value) {
        this.credentialStoreFragments = value;
    }

    /**
     * Ruft den Wert der mailServerFragments-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MailServerFragments }
     *     
     */
    public MailServerFragments getMailServerFragments() {
        return mailServerFragments;
    }

    /**
     * Legt den Wert der mailServerFragments-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MailServerFragments }
     *     
     */
    public void setMailServerFragments(MailServerFragments value) {
        this.mailServerFragments = value;
    }

}
