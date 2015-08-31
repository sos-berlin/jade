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
 *         &lt;element ref="{}ResultSetFile" minOccurs="0"/>
 *         &lt;element ref="{}CheckResultSetSize" minOccurs="0"/>
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
    "checkResultSetSize",
    "emptyResultSetState"
})
@XmlRootElement(name = "ResultSet")
public class ResultSet {

    @XmlElement(name = "ResultSetFile")
    protected String resultSetFile;
    @XmlElement(name = "CheckResultSetSize")
    protected CheckResultSetSize checkResultSetSize;
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
     * Ruft den Wert der checkResultSetSize-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CheckResultSetSize }
     *     
     */
    public CheckResultSetSize getCheckResultSetSize() {
        return checkResultSetSize;
    }

    /**
     * Legt den Wert der checkResultSetSize-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CheckResultSetSize }
     *     
     */
    public void setCheckResultSetSize(CheckResultSetSize value) {
        this.checkResultSetSize = value;
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
