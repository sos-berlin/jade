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
 *         &lt;element ref="{}OnSuccess" minOccurs="0"/>
 *         &lt;element ref="{}OnError" minOccurs="0"/>
 *         &lt;element ref="{}OnEmptyFiles" minOccurs="0"/>
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
    "onSuccess",
    "onError",
    "onEmptyFiles"
})
@XmlRootElement(name = "NotificationTriggers")
public class NotificationTriggers {

    @XmlElement(name = "OnSuccess")
    protected NotificationTriggerType onSuccess;
    @XmlElement(name = "OnError")
    protected NotificationTriggerType onError;
    @XmlElement(name = "OnEmptyFiles")
    protected NotificationTriggerType onEmptyFiles;

    /**
     * Ruft den Wert der onSuccess-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link NotificationTriggerType }
     *     
     */
    public NotificationTriggerType getOnSuccess() {
        return onSuccess;
    }

    /**
     * Legt den Wert der onSuccess-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link NotificationTriggerType }
     *     
     */
    public void setOnSuccess(NotificationTriggerType value) {
        this.onSuccess = value;
    }

    /**
     * Ruft den Wert der onError-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link NotificationTriggerType }
     *     
     */
    public NotificationTriggerType getOnError() {
        return onError;
    }

    /**
     * Legt den Wert der onError-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link NotificationTriggerType }
     *     
     */
    public void setOnError(NotificationTriggerType value) {
        this.onError = value;
    }

    /**
     * Ruft den Wert der onEmptyFiles-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link NotificationTriggerType }
     *     
     */
    public NotificationTriggerType getOnEmptyFiles() {
        return onEmptyFiles;
    }

    /**
     * Legt den Wert der onEmptyFiles-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link NotificationTriggerType }
     *     
     */
    public void setOnEmptyFiles(NotificationTriggerType value) {
        this.onEmptyFiles = value;
    }

}
