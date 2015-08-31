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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für NotificationTriggerType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="NotificationTriggerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}MailFragmentRef"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NotificationTriggerType", propOrder = {
    "mailFragmentRef"
})
public class NotificationTriggerType {

    @XmlElement(name = "MailFragmentRef", required = true)
    protected MailFragmentRef mailFragmentRef;

    /**
     * Ruft den Wert der mailFragmentRef-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MailFragmentRef }
     *     
     */
    public MailFragmentRef getMailFragmentRef() {
        return mailFragmentRef;
    }

    /**
     * Legt den Wert der mailFragmentRef-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MailFragmentRef }
     *     
     */
    public void setMailFragmentRef(MailFragmentRef value) {
        this.mailFragmentRef = value;
    }

}
