//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.11.11 um 11:55:19 AM CET 
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
 *         &lt;element ref="{}ResultSetFile" minOccurs="0"/>
 *         &lt;element ref="{}CheckResultSetCount" minOccurs="0"/>
 *         &lt;element ref="{}EmptyResultSetState" minOccurs="0"/>
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
    "resultSetFile",
    "checkResultSetCount",
    "emptyResultSetState"
})
@XmlRootElement(name = "ResultSet")
public class ResultSet {

    @XmlElement(name = "ResultSetFile")
    protected String resultSetFile;
    @XmlElement(name = "CheckResultSetCount")
    protected CheckResultSetCount checkResultSetCount;
    @XmlElement(name = "EmptyResultSetState")
    protected String emptyResultSetState;

    /**
     * Ruft den Wert der resultSetFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultSetFile() {
        return resultSetFile;
    }

    /**
     * Legt den Wert der resultSetFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultSetFile(String value) {
        this.resultSetFile = value;
    }

    /**
     * Ruft den Wert der checkResultSetCount-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CheckResultSetCount }
     *     
     */
    public CheckResultSetCount getCheckResultSetCount() {
        return checkResultSetCount;
    }

    /**
     * Legt den Wert der checkResultSetCount-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CheckResultSetCount }
     *     
     */
    public void setCheckResultSetCount(CheckResultSetCount value) {
        this.checkResultSetCount = value;
    }

    /**
     * Ruft den Wert der emptyResultSetState-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmptyResultSetState() {
        return emptyResultSetState;
    }

    /**
     * Legt den Wert der emptyResultSetState-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmptyResultSetState(String value) {
        this.emptyResultSetState = value;
    }

}
