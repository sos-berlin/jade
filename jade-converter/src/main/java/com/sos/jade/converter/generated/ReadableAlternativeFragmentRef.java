//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.08.28 um 03:50:36 PM CEST 
//


package com.sos.jade.converter.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


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
 *         &lt;element ref="{}FTPPreProcessing" minOccurs="0"/>
 *         &lt;element ref="{}FTPPostProcessing" minOccurs="0"/>
 *         &lt;element ref="{}SFTPPreProcessing" minOccurs="0"/>
 *         &lt;element ref="{}SFTPPostProcessing" minOccurs="0"/>
 *         &lt;element ref="{}Rename" minOccurs="0"/>
 *         &lt;element ref="{}Directory" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "ftpPreProcessing",
    "ftpPostProcessing",
    "sftpPreProcessing",
    "sftpPostProcessing",
    "rename",
    "directory"
})
@XmlRootElement(name = "ReadableAlternativeFragmentRef")
public class ReadableAlternativeFragmentRef {

    @XmlElement(name = "FTPPreProcessing")
    protected FTPPreProcessingType ftpPreProcessing;
    @XmlElement(name = "FTPPostProcessing")
    protected FTPPostProcessingType ftpPostProcessing;
    @XmlElement(name = "SFTPPreProcessing")
    protected SFTPPreProcessingType sftpPreProcessing;
    @XmlElement(name = "SFTPPostProcessing")
    protected SFTPPostProcessingType sftpPostProcessing;
    @XmlElement(name = "Rename")
    protected RenameType rename;
    @XmlElement(name = "Directory")
    protected String directory;
    @XmlAttribute(name = "ref", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String ref;

    /**
     * Ruft den Wert der ftpPreProcessing-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FTPPreProcessingType }
     *     
     */
    public FTPPreProcessingType getFTPPreProcessing() {
        return ftpPreProcessing;
    }

    /**
     * Legt den Wert der ftpPreProcessing-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FTPPreProcessingType }
     *     
     */
    public void setFTPPreProcessing(FTPPreProcessingType value) {
        this.ftpPreProcessing = value;
    }

    /**
     * Ruft den Wert der ftpPostProcessing-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FTPPostProcessingType }
     *     
     */
    public FTPPostProcessingType getFTPPostProcessing() {
        return ftpPostProcessing;
    }

    /**
     * Legt den Wert der ftpPostProcessing-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FTPPostProcessingType }
     *     
     */
    public void setFTPPostProcessing(FTPPostProcessingType value) {
        this.ftpPostProcessing = value;
    }

    /**
     * Ruft den Wert der sftpPreProcessing-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SFTPPreProcessingType }
     *     
     */
    public SFTPPreProcessingType getSFTPPreProcessing() {
        return sftpPreProcessing;
    }

    /**
     * Legt den Wert der sftpPreProcessing-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SFTPPreProcessingType }
     *     
     */
    public void setSFTPPreProcessing(SFTPPreProcessingType value) {
        this.sftpPreProcessing = value;
    }

    /**
     * Ruft den Wert der sftpPostProcessing-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SFTPPostProcessingType }
     *     
     */
    public SFTPPostProcessingType getSFTPPostProcessing() {
        return sftpPostProcessing;
    }

    /**
     * Legt den Wert der sftpPostProcessing-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SFTPPostProcessingType }
     *     
     */
    public void setSFTPPostProcessing(SFTPPostProcessingType value) {
        this.sftpPostProcessing = value;
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
     * Ruft den Wert der directory-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * Legt den Wert der directory-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDirectory(String value) {
        this.directory = value;
    }

    /**
     * Ruft den Wert der ref-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRef() {
        return ref;
    }

    /**
     * Legt den Wert der ref-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRef(String value) {
        this.ref = value;
    }

}
