//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.10.09 um 03:42:30 PM CEST 
//


package com.sos.jade.converter.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für NotificationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="NotificationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}MailServerFragmentRef" minOccurs="0"/>
 *         &lt;element ref="{}BackgroundServiceFragmentRef" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NotificationType", propOrder = {
    "mailServerFragmentRef",
    "backgroundServiceFragmentRef"
})
public class NotificationType {

    @XmlElement(name = "MailServerFragmentRef")
    protected MailServerFragmentRef mailServerFragmentRef;
    @XmlElement(name = "BackgroundServiceFragmentRef")
    protected BackgroundServiceFragmentRef backgroundServiceFragmentRef;

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

    /**
     * Ruft den Wert der backgroundServiceFragmentRef-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BackgroundServiceFragmentRef }
     *     
     */
    public BackgroundServiceFragmentRef getBackgroundServiceFragmentRef() {
        return backgroundServiceFragmentRef;
    }

    /**
     * Legt den Wert der backgroundServiceFragmentRef-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BackgroundServiceFragmentRef }
     *     
     */
    public void setBackgroundServiceFragmentRef(BackgroundServiceFragmentRef value) {
        this.backgroundServiceFragmentRef = value;
    }

}
