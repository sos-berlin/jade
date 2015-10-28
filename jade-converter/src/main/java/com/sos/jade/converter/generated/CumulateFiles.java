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
 *         &lt;element ref="{}CumulativeFileSeparator" minOccurs="0"/>
 *         &lt;element ref="{}CumulativeFilename" minOccurs="0"/>
 *         &lt;element ref="{}CumulativeFileDelete" minOccurs="0"/>
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
    "cumulativeFileSeparator",
    "cumulativeFilename",
    "cumulativeFileDelete"
})
@XmlRootElement(name = "CumulateFiles")
public class CumulateFiles {

    @XmlElement(name = "CumulativeFileSeparator")
    protected String cumulativeFileSeparator;
    @XmlElement(name = "CumulativeFilename")
    protected String cumulativeFilename;
    @XmlElement(name = "CumulativeFileDelete", defaultValue = "false")
    protected Boolean cumulativeFileDelete;

    /**
     * Ruft den Wert der cumulativeFileSeparator-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCumulativeFileSeparator() {
        return cumulativeFileSeparator;
    }

    /**
     * Legt den Wert der cumulativeFileSeparator-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCumulativeFileSeparator(String value) {
        this.cumulativeFileSeparator = value;
    }

    /**
     * Ruft den Wert der cumulativeFilename-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCumulativeFilename() {
        return cumulativeFilename;
    }

    /**
     * Legt den Wert der cumulativeFilename-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCumulativeFilename(String value) {
        this.cumulativeFilename = value;
    }

    /**
     * Ruft den Wert der cumulativeFileDelete-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCumulativeFileDelete() {
        return cumulativeFileDelete;
    }

    /**
     * Legt den Wert der cumulativeFileDelete-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCumulativeFileDelete(Boolean value) {
        this.cumulativeFileDelete = value;
    }

}
