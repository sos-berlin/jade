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
 * <p>Java-Klasse für FTPSClientSecurityType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="FTPSClientSecurityType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}SecurityMode" minOccurs="0"/>
 *         &lt;element ref="{}KeyStoreType" minOccurs="0"/>
 *         &lt;element ref="{}KeyStoreFile" minOccurs="0"/>
 *         &lt;element ref="{}KeyStorePassword" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FTPSClientSecurityType", propOrder = {
    "securityMode",
    "keyStoreType",
    "keyStoreFile",
    "keyStorePassword"
})
public class FTPSClientSecurityType {

    @XmlElement(name = "SecurityMode", defaultValue = "explicit")
    protected String securityMode;
    @XmlElement(name = "KeyStoreType", defaultValue = "JKS")
    protected String keyStoreType;
    @XmlElement(name = "KeyStoreFile")
    protected String keyStoreFile;
    @XmlElement(name = "KeyStorePassword")
    protected String keyStorePassword;

    /**
     * Ruft den Wert der securityMode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecurityMode() {
        return securityMode;
    }

    /**
     * Legt den Wert der securityMode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecurityMode(String value) {
        this.securityMode = value;
    }

    /**
     * Ruft den Wert der keyStoreType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeyStoreType() {
        return keyStoreType;
    }

    /**
     * Legt den Wert der keyStoreType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeyStoreType(String value) {
        this.keyStoreType = value;
    }

    /**
     * Ruft den Wert der keyStoreFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeyStoreFile() {
        return keyStoreFile;
    }

    /**
     * Legt den Wert der keyStoreFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeyStoreFile(String value) {
        this.keyStoreFile = value;
    }

    /**
     * Ruft den Wert der keyStorePassword-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    /**
     * Legt den Wert der keyStorePassword-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeyStorePassword(String value) {
        this.keyStorePassword = value;
    }

}
