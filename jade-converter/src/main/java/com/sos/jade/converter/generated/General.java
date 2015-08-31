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
 *         &lt;element ref="{}History" minOccurs="0"/>
 *         &lt;element ref="{}BackgroundService" minOccurs="0"/>
 *         &lt;element ref="{}Logging" minOccurs="0"/>
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
    "history",
    "backgroundService",
    "logging",
    "mailServerFragmentRef"
})
@XmlRootElement(name = "General")
public class General {

    @XmlElement(name = "History")
    protected HistoryType history;
    @XmlElement(name = "BackgroundService")
    protected BackgroundServiceType backgroundService;
    @XmlElement(name = "Logging")
    protected Logging logging;
    @XmlElement(name = "MailServerFragmentRef")
    protected MailServerFragmentRef mailServerFragmentRef;

    /**
     * Ruft den Wert der history-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link HistoryType }
     *     
     */
    public HistoryType getHistory() {
        return history;
    }

    /**
     * Legt den Wert der history-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link HistoryType }
     *     
     */
    public void setHistory(HistoryType value) {
        this.history = value;
    }

    /**
     * Ruft den Wert der backgroundService-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BackgroundServiceType }
     *     
     */
    public BackgroundServiceType getBackgroundService() {
        return backgroundService;
    }

    /**
     * Legt den Wert der backgroundService-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BackgroundServiceType }
     *     
     */
    public void setBackgroundService(BackgroundServiceType value) {
        this.backgroundService = value;
    }

    /**
     * Ruft den Wert der logging-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Logging }
     *     
     */
    public Logging getLogging() {
        return logging;
    }

    /**
     * Legt den Wert der logging-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Logging }
     *     
     */
    public void setLogging(Logging value) {
        this.logging = value;
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
