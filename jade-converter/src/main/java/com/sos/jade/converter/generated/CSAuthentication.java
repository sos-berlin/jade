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
 *       &lt;choice>
 *         &lt;element ref="{}PasswordAuthentication"/>
 *         &lt;element ref="{}KeyFileAuthentication"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "passwordAuthentication",
    "keyFileAuthentication"
})
@XmlRootElement(name = "CSAuthentication")
public class CSAuthentication {

    @XmlElement(name = "PasswordAuthentication")
    protected PasswordAuthentication passwordAuthentication;
    @XmlElement(name = "KeyFileAuthentication")
    protected KeyFileAuthentication keyFileAuthentication;

    /**
     * Ruft den Wert der passwordAuthentication-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PasswordAuthentication }
     *     
     */
    public PasswordAuthentication getPasswordAuthentication() {
        return passwordAuthentication;
    }

    /**
     * Legt den Wert der passwordAuthentication-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PasswordAuthentication }
     *     
     */
    public void setPasswordAuthentication(PasswordAuthentication value) {
        this.passwordAuthentication = value;
    }

    /**
     * Ruft den Wert der keyFileAuthentication-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link KeyFileAuthentication }
     *     
     */
    public KeyFileAuthentication getKeyFileAuthentication() {
        return keyFileAuthentication;
    }

    /**
     * Legt den Wert der keyFileAuthentication-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link KeyFileAuthentication }
     *     
     */
    public void setKeyFileAuthentication(KeyFileAuthentication value) {
        this.keyFileAuthentication = value;
    }

}