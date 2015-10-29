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
 *         &lt;element ref="{}Selection"/>
 *         &lt;element ref="{}CheckSteadyState" minOccurs="0"/>
 *         &lt;element ref="{}Directives" minOccurs="0"/>
 *         &lt;element ref="{}FileAge" minOccurs="0"/>
 *         &lt;element ref="{}FileSize" minOccurs="0"/>
 *         &lt;element ref="{}Polling" minOccurs="0"/>
 *         &lt;element ref="{}ResultSet" minOccurs="0"/>
 *         &lt;element ref="{}SkipFiles" minOccurs="0"/>
 *         &lt;element ref="{}MaxFiles" minOccurs="0"/>
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
    "selection",
    "checkSteadyState",
    "directives",
    "fileAge",
    "fileSize",
    "polling",
    "resultSet",
    "skipFiles",
    "maxFiles"
})
@XmlRootElement(name = "SourceFileOptions")
public class SourceFileOptions {

    @XmlElement(name = "Selection", required = true)
    protected Selection selection;
    @XmlElement(name = "CheckSteadyState")
    protected CheckSteadyState checkSteadyState;
    @XmlElement(name = "Directives")
    protected Directives directives;
    @XmlElement(name = "FileAge")
    protected FileAge fileAge;
    @XmlElement(name = "FileSize")
    protected FileSize fileSize;
    @XmlElement(name = "Polling")
    protected Polling polling;
    @XmlElement(name = "ResultSet")
    protected ResultSet resultSet;
    @XmlElement(name = "SkipFiles")
    protected SkipFiles skipFiles;
    @XmlElement(name = "MaxFiles", defaultValue = "-1")
    protected Integer maxFiles;

    /**
     * Ruft den Wert der selection-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Selection }
     *     
     */
    public Selection getSelection() {
        return selection;
    }

    /**
     * Legt den Wert der selection-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Selection }
     *     
     */
    public void setSelection(Selection value) {
        this.selection = value;
    }

    /**
     * Ruft den Wert der checkSteadyState-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CheckSteadyState }
     *     
     */
    public CheckSteadyState getCheckSteadyState() {
        return checkSteadyState;
    }

    /**
     * Legt den Wert der checkSteadyState-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CheckSteadyState }
     *     
     */
    public void setCheckSteadyState(CheckSteadyState value) {
        this.checkSteadyState = value;
    }

    /**
     * Ruft den Wert der directives-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Directives }
     *     
     */
    public Directives getDirectives() {
        return directives;
    }

    /**
     * Legt den Wert der directives-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Directives }
     *     
     */
    public void setDirectives(Directives value) {
        this.directives = value;
    }

    /**
     * Ruft den Wert der fileAge-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FileAge }
     *     
     */
    public FileAge getFileAge() {
        return fileAge;
    }

    /**
     * Legt den Wert der fileAge-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FileAge }
     *     
     */
    public void setFileAge(FileAge value) {
        this.fileAge = value;
    }

    /**
     * Ruft den Wert der fileSize-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FileSize }
     *     
     */
    public FileSize getFileSize() {
        return fileSize;
    }

    /**
     * Legt den Wert der fileSize-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FileSize }
     *     
     */
    public void setFileSize(FileSize value) {
        this.fileSize = value;
    }

    /**
     * Ruft den Wert der polling-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Polling }
     *     
     */
    public Polling getPolling() {
        return polling;
    }

    /**
     * Legt den Wert der polling-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Polling }
     *     
     */
    public void setPolling(Polling value) {
        this.polling = value;
    }

    /**
     * Ruft den Wert der resultSet-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ResultSet }
     *     
     */
    public ResultSet getResultSet() {
        return resultSet;
    }

    /**
     * Legt den Wert der resultSet-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultSet }
     *     
     */
    public void setResultSet(ResultSet value) {
        this.resultSet = value;
    }

    /**
     * Ruft den Wert der skipFiles-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SkipFiles }
     *     
     */
    public SkipFiles getSkipFiles() {
        return skipFiles;
    }

    /**
     * Legt den Wert der skipFiles-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SkipFiles }
     *     
     */
    public void setSkipFiles(SkipFiles value) {
        this.skipFiles = value;
    }

    /**
     * Ruft den Wert der maxFiles-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxFiles() {
        return maxFiles;
    }

    /**
     * Legt den Wert der maxFiles-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxFiles(Integer value) {
        this.maxFiles = value;
    }

}
