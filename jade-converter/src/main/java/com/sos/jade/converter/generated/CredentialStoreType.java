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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für CredentialStoreType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CredentialStoreType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}CSFile"/>
 *         &lt;element ref="{}CSAuthentication"/>
 *         &lt;element ref="{}CSEntryPath"/>
 *         &lt;element ref="{}CSExportAttachment" minOccurs="0"/>
 *         &lt;element ref="{}CSStoreType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CredentialStoreType", propOrder = {
    "csFile",
    "csAuthentication",
    "csEntryPath",
    "csExportAttachment",
    "csStoreType"
})
@XmlSeeAlso({
    CredentialStoreFragment.class
})
public class CredentialStoreType {

    @XmlElement(name = "CSFile", required = true)
    protected String csFile;
    @XmlElement(name = "CSAuthentication", required = true)
    protected CSAuthentication csAuthentication;
    @XmlElement(name = "CSEntryPath", required = true)
    protected String csEntryPath;
    @XmlElement(name = "CSExportAttachment")
    protected CSExportAttachment csExportAttachment;
    @XmlElement(name = "CSStoreType", defaultValue = "KeePassX")
    protected String csStoreType;

    /**
     * Ruft den Wert der csFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSFile() {
        return csFile;
    }

    /**
     * Legt den Wert der csFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSFile(String value) {
        this.csFile = value;
    }

    /**
     * Ruft den Wert der csAuthentication-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CSAuthentication }
     *     
     */
    public CSAuthentication getCSAuthentication() {
        return csAuthentication;
    }

    /**
     * Legt den Wert der csAuthentication-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CSAuthentication }
     *     
     */
    public void setCSAuthentication(CSAuthentication value) {
        this.csAuthentication = value;
    }

    /**
     * Ruft den Wert der csEntryPath-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSEntryPath() {
        return csEntryPath;
    }

    /**
     * Legt den Wert der csEntryPath-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSEntryPath(String value) {
        this.csEntryPath = value;
    }

    /**
     * Ruft den Wert der csExportAttachment-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CSExportAttachment }
     *     
     */
    public CSExportAttachment getCSExportAttachment() {
        return csExportAttachment;
    }

    /**
     * Legt den Wert der csExportAttachment-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CSExportAttachment }
     *     
     */
    public void setCSExportAttachment(CSExportAttachment value) {
        this.csExportAttachment = value;
    }

    /**
     * Ruft den Wert der csStoreType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSStoreType() {
        return csStoreType;
    }

    /**
     * Legt den Wert der csStoreType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSStoreType(String value) {
        this.csStoreType = value;
    }

}
