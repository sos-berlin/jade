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
 *         &lt;element ref="{}Logging" minOccurs="0"/>
 *         &lt;element ref="{}Notifications" minOccurs="0"/>
 *         &lt;element ref="{}Assertions" minOccurs="0"/>
 *         &lt;element ref="{}Documentation" minOccurs="0"/>
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
    "logging",
    "notifications",
    "assertions",
    "documentation"
})
@XmlRootElement(name = "General")
public class General {

    @XmlElement(name = "History")
    protected HistoryType history;
    @XmlElement(name = "Logging")
    protected LoggingType logging;
    @XmlElement(name = "Notifications")
    protected NotificationType notifications;
    @XmlElement(name = "Assertions")
    protected AssertionType assertions;
    @XmlElement(name = "Documentation")
    protected DocumentationType documentation;

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
     * Ruft den Wert der logging-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LoggingType }
     *     
     */
    public LoggingType getLogging() {
        return logging;
    }

    /**
     * Legt den Wert der logging-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LoggingType }
     *     
     */
    public void setLogging(LoggingType value) {
        this.logging = value;
    }

    /**
     * Ruft den Wert der notifications-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link NotificationType }
     *     
     */
    public NotificationType getNotifications() {
        return notifications;
    }

    /**
     * Legt den Wert der notifications-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link NotificationType }
     *     
     */
    public void setNotifications(NotificationType value) {
        this.notifications = value;
    }

    /**
     * Ruft den Wert der assertions-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AssertionType }
     *     
     */
    public AssertionType getAssertions() {
        return assertions;
    }

    /**
     * Legt den Wert der assertions-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AssertionType }
     *     
     */
    public void setAssertions(AssertionType value) {
        this.assertions = value;
    }

    /**
     * Ruft den Wert der documentation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DocumentationType }
     *     
     */
    public DocumentationType getDocumentation() {
        return documentation;
    }

    /**
     * Legt den Wert der documentation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentationType }
     *     
     */
    public void setDocumentation(DocumentationType value) {
        this.documentation = value;
    }

}
