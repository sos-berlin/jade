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
 *         &lt;element ref="{}LocalPreProcessing" minOccurs="0"/>
 *         &lt;element ref="{}LocalPostProcessing" minOccurs="0"/>
 *         &lt;element ref="{}Rename" minOccurs="0"/>
 *         &lt;element ref="{}Zip" minOccurs="0"/>
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
    "localPreProcessing",
    "localPostProcessing",
    "rename",
    "zip"
})
@XmlRootElement(name = "LocalSource")
public class LocalSource {

    @XmlElement(name = "LocalPreProcessing")
    protected LocalPreProcessingType localPreProcessing;
    @XmlElement(name = "LocalPostProcessing")
    protected LocalPostProcessingType localPostProcessing;
    @XmlElement(name = "Rename")
    protected RenameType rename;
    @XmlElement(name = "Zip", defaultValue = "false")
    protected Boolean zip;

    /**
     * Ruft den Wert der localPreProcessing-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LocalPreProcessingType }
     *     
     */
    public LocalPreProcessingType getLocalPreProcessing() {
        return localPreProcessing;
    }

    /**
     * Legt den Wert der localPreProcessing-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LocalPreProcessingType }
     *     
     */
    public void setLocalPreProcessing(LocalPreProcessingType value) {
        this.localPreProcessing = value;
    }

    /**
     * Ruft den Wert der localPostProcessing-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LocalPostProcessingType }
     *     
     */
    public LocalPostProcessingType getLocalPostProcessing() {
        return localPostProcessing;
    }

    /**
     * Legt den Wert der localPostProcessing-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LocalPostProcessingType }
     *     
     */
    public void setLocalPostProcessing(LocalPostProcessingType value) {
        this.localPostProcessing = value;
    }

    /**
     * Ruft den Wert der rename-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RenameType }
     *     
     */
    public RenameType getRename() {
        return rename;
    }

    /**
     * Legt den Wert der rename-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RenameType }
     *     
     */
    public void setRename(RenameType value) {
        this.rename = value;
    }

    /**
     * Ruft den Wert der zip-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isZip() {
        return zip;
    }

    /**
     * Legt den Wert der zip-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setZip(Boolean value) {
        this.zip = value;
    }

}
