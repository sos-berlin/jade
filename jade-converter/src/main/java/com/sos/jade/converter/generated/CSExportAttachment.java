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
 *         &lt;element ref="{}CSExportedFile"/>
 *         &lt;element ref="{}CSKeepExportedFileOnExit" minOccurs="0"/>
 *         &lt;element ref="{}CSOverwriteExportedFile" minOccurs="0"/>
 *         &lt;element ref="{}CSPermissionsForExportedFile" minOccurs="0"/>
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
    "csExportedFile",
    "csKeepExportedFileOnExit",
    "csOverwriteExportedFile",
    "csPermissionsForExportedFile"
})
@XmlRootElement(name = "CSExportAttachment")
public class CSExportAttachment {

    @XmlElement(name = "CSExportedFile", required = true)
    protected String csExportedFile;
    @XmlElement(name = "CSKeepExportedFileOnExit", defaultValue = "false")
    protected Boolean csKeepExportedFileOnExit;
    @XmlElement(name = "CSOverwriteExportedFile", defaultValue = "false")
    protected Boolean csOverwriteExportedFile;
    @XmlElement(name = "CSPermissionsForExportedFile", defaultValue = "600")
    protected String csPermissionsForExportedFile;

    /**
     * Ruft den Wert der csExportedFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSExportedFile() {
        return csExportedFile;
    }

    /**
     * Legt den Wert der csExportedFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSExportedFile(String value) {
        this.csExportedFile = value;
    }

    /**
     * Ruft den Wert der csKeepExportedFileOnExit-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCSKeepExportedFileOnExit() {
        return csKeepExportedFileOnExit;
    }

    /**
     * Legt den Wert der csKeepExportedFileOnExit-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCSKeepExportedFileOnExit(Boolean value) {
        this.csKeepExportedFileOnExit = value;
    }

    /**
     * Ruft den Wert der csOverwriteExportedFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCSOverwriteExportedFile() {
        return csOverwriteExportedFile;
    }

    /**
     * Legt den Wert der csOverwriteExportedFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCSOverwriteExportedFile(Boolean value) {
        this.csOverwriteExportedFile = value;
    }

    /**
     * Ruft den Wert der csPermissionsForExportedFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSPermissionsForExportedFile() {
        return csPermissionsForExportedFile;
    }

    /**
     * Legt den Wert der csPermissionsForExportedFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSPermissionsForExportedFile(String value) {
        this.csPermissionsForExportedFile = value;
    }

}
