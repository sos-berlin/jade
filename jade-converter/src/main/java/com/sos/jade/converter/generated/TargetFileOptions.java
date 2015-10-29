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
 *         &lt;element ref="{}AppendFiles" minOccurs="0"/>
 *         &lt;element ref="{}Atomicity" minOccurs="0"/>
 *         &lt;element ref="{}CheckSize" minOccurs="0"/>
 *         &lt;element ref="{}CumulateFiles" minOccurs="0"/>
 *         &lt;element ref="{}CompressFiles" minOccurs="0"/>
 *         &lt;element ref="{}CheckIntegrityHash" minOccurs="0"/>
 *         &lt;element ref="{}KeepModificationDate" minOccurs="0"/>
 *         &lt;element ref="{}DisableMakeDirectories" minOccurs="0"/>
 *         &lt;element ref="{}DisableOverwriteFiles" minOccurs="0"/>
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
    "appendFiles",
    "atomicity",
    "checkSize",
    "cumulateFiles",
    "compressFiles",
    "checkIntegrityHash",
    "keepModificationDate",
    "disableMakeDirectories",
    "disableOverwriteFiles"
})
@XmlRootElement(name = "TargetFileOptions")
public class TargetFileOptions {

    @XmlElement(name = "AppendFiles", defaultValue = "false")
    protected Boolean appendFiles;
    @XmlElement(name = "Atomicity")
    protected AtomicityType atomicity;
    @XmlElement(name = "CheckSize", defaultValue = "false")
    protected Boolean checkSize;
    @XmlElement(name = "CumulateFiles")
    protected CumulateFiles cumulateFiles;
    @XmlElement(name = "CompressFiles")
    protected CompressFiles compressFiles;
    @XmlElement(name = "CheckIntegrityHash")
    protected CheckIntegrityHash checkIntegrityHash;
    @XmlElement(name = "KeepModificationDate", defaultValue = "false")
    protected Boolean keepModificationDate;
    @XmlElement(name = "DisableMakeDirectories", defaultValue = "false")
    protected Boolean disableMakeDirectories;
    @XmlElement(name = "DisableOverwriteFiles", defaultValue = "false")
    protected Boolean disableOverwriteFiles;

    /**
     * Ruft den Wert der appendFiles-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAppendFiles() {
        return appendFiles;
    }

    /**
     * Legt den Wert der appendFiles-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAppendFiles(Boolean value) {
        this.appendFiles = value;
    }

    /**
     * Ruft den Wert der atomicity-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AtomicityType }
     *     
     */
    public AtomicityType getAtomicity() {
        return atomicity;
    }

    /**
     * Legt den Wert der atomicity-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AtomicityType }
     *     
     */
    public void setAtomicity(AtomicityType value) {
        this.atomicity = value;
    }

    /**
     * Ruft den Wert der checkSize-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCheckSize() {
        return checkSize;
    }

    /**
     * Legt den Wert der checkSize-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCheckSize(Boolean value) {
        this.checkSize = value;
    }

    /**
     * Ruft den Wert der cumulateFiles-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CumulateFiles }
     *     
     */
    public CumulateFiles getCumulateFiles() {
        return cumulateFiles;
    }

    /**
     * Legt den Wert der cumulateFiles-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CumulateFiles }
     *     
     */
    public void setCumulateFiles(CumulateFiles value) {
        this.cumulateFiles = value;
    }

    /**
     * Ruft den Wert der compressFiles-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CompressFiles }
     *     
     */
    public CompressFiles getCompressFiles() {
        return compressFiles;
    }

    /**
     * Legt den Wert der compressFiles-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CompressFiles }
     *     
     */
    public void setCompressFiles(CompressFiles value) {
        this.compressFiles = value;
    }

    /**
     * Ruft den Wert der checkIntegrityHash-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CheckIntegrityHash }
     *     
     */
    public CheckIntegrityHash getCheckIntegrityHash() {
        return checkIntegrityHash;
    }

    /**
     * Legt den Wert der checkIntegrityHash-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CheckIntegrityHash }
     *     
     */
    public void setCheckIntegrityHash(CheckIntegrityHash value) {
        this.checkIntegrityHash = value;
    }

    /**
     * Ruft den Wert der keepModificationDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isKeepModificationDate() {
        return keepModificationDate;
    }

    /**
     * Legt den Wert der keepModificationDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setKeepModificationDate(Boolean value) {
        this.keepModificationDate = value;
    }

    /**
     * Ruft den Wert der disableMakeDirectories-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDisableMakeDirectories() {
        return disableMakeDirectories;
    }

    /**
     * Legt den Wert der disableMakeDirectories-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDisableMakeDirectories(Boolean value) {
        this.disableMakeDirectories = value;
    }

    /**
     * Ruft den Wert der disableOverwriteFiles-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDisableOverwriteFiles() {
        return disableOverwriteFiles;
    }

    /**
     * Legt den Wert der disableOverwriteFiles-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDisableOverwriteFiles(Boolean value) {
        this.disableOverwriteFiles = value;
    }

}
