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
 *         &lt;element ref="{}DisableErrorOnNoFilesFound" minOccurs="0"/>
 *         &lt;element ref="{}TransferZeroByteFiles" minOccurs="0"/>
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
    "disableErrorOnNoFilesFound",
    "transferZeroByteFiles"
})
@XmlRootElement(name = "Directives")
public class Directives {

    @XmlElement(name = "DisableErrorOnNoFilesFound", defaultValue = "false")
    protected Boolean disableErrorOnNoFilesFound;
    @XmlElement(name = "TransferZeroByteFiles")
    protected String transferZeroByteFiles;

    /**
     * Ruft den Wert der disableErrorOnNoFilesFound-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDisableErrorOnNoFilesFound() {
        return disableErrorOnNoFilesFound;
    }

    /**
     * Legt den Wert der disableErrorOnNoFilesFound-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDisableErrorOnNoFilesFound(Boolean value) {
        this.disableErrorOnNoFilesFound = value;
    }

    /**
     * Ruft den Wert der transferZeroByteFiles-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransferZeroByteFiles() {
        return transferZeroByteFiles;
    }

    /**
     * Legt den Wert der transferZeroByteFiles-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransferZeroByteFiles(String value) {
        this.transferZeroByteFiles = value;
    }

}
