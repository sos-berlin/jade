//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.08.28 um 03:50:36 PM CEST 
//


package com.sos.jade.converter.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}NotificationTriggers" minOccurs="0"/>
 *         &lt;element ref="{}MailServerFragmentRef" minOccurs="0"/>
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
    "notificationTriggers",
    "mailServerFragmentRef"
})
@XmlRootElement(name = "Notifications")
public class Notifications {

    @XmlElement(name = "NotificationTriggers")
    protected NotificationTriggers notificationTriggers;
    @XmlElement(name = "MailServerFragmentRef")
    protected MailServerFragmentRef mailServerFragmentRef;

    /**
     * Ruft den Wert der notificationTriggers-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link NotificationTriggers }
     *     
     */
    public NotificationTriggers getNotificationTriggers() {
        return notificationTriggers;
    }

    /**
     * Legt den Wert der notificationTriggers-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link NotificationTriggers }
     *     
     */
    public void setNotificationTriggers(NotificationTriggers value) {
        this.notificationTriggers = value;
    }

    /**
     * Ruft den Wert der mailServerFragmentRef-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MailServerFragmentRef }
     *     
     */
    public MailServerFragmentRef getMailServerFragmentRef() {
        return mailServerFragmentRef;
    }

    /**
     * Legt den Wert der mailServerFragmentRef-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MailServerFragmentRef }
     *     
     */
    public void setMailServerFragmentRef(MailServerFragmentRef value) {
        this.mailServerFragmentRef = value;
    }

}
