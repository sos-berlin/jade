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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für HistoryType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="HistoryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}HistoryFile" minOccurs="0"/>
 *         &lt;element ref="{}DisableHistoryFileAppendMode" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HistoryType", propOrder = {
    "historyFile",
    "disableHistoryFileAppendMode"
})
public class HistoryType {

    @XmlElement(name = "HistoryFile")
    protected String historyFile;
    @XmlElement(name = "DisableHistoryFileAppendMode", defaultValue = "false")
    protected Boolean disableHistoryFileAppendMode;

    /**
     * Ruft den Wert der historyFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHistoryFile() {
        return historyFile;
    }

    /**
     * Legt den Wert der historyFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHistoryFile(String value) {
        this.historyFile = value;
    }

    /**
     * Ruft den Wert der disableHistoryFileAppendMode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDisableHistoryFileAppendMode() {
        return disableHistoryFileAppendMode;
    }

    /**
     * Legt den Wert der disableHistoryFileAppendMode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDisableHistoryFileAppendMode(Boolean value) {
        this.disableHistoryFileAppendMode = value;
    }

}
