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
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element ref="{}SkipFirstFiles" minOccurs="0"/>
 *           &lt;element ref="{}SkipLastFiles" minOccurs="0"/>
 *         &lt;/sequence>
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
    "skipFirstFiles",
    "skipLastFiles"
})
@XmlRootElement(name = "SkipFiles")
public class SkipFiles {

    @XmlElement(name = "SkipFirstFiles", defaultValue = "0")
    protected Integer skipFirstFiles;
    @XmlElement(name = "SkipLastFiles", defaultValue = "0")
    protected Integer skipLastFiles;

    /**
     * Ruft den Wert der skipFirstFiles-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSkipFirstFiles() {
        return skipFirstFiles;
    }

    /**
     * Legt den Wert der skipFirstFiles-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSkipFirstFiles(Integer value) {
        this.skipFirstFiles = value;
    }

    /**
     * Ruft den Wert der skipLastFiles-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSkipLastFiles() {
        return skipLastFiles;
    }

    /**
     * Legt den Wert der skipLastFiles-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSkipLastFiles(Integer value) {
        this.skipLastFiles = value;
    }

}
