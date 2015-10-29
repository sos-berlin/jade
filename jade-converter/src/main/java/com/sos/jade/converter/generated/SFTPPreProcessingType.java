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
 * <p>Java-Klasse für SFTPPreProcessingType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="SFTPPreProcessingType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}CommandBeforeFile" minOccurs="0"/>
 *         &lt;element ref="{}CommandBeforeOperation" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SFTPPreProcessingType", propOrder = {
    "commandBeforeFile",
    "commandBeforeOperation"
})
public class SFTPPreProcessingType {

    @XmlElement(name = "CommandBeforeFile")
    protected String commandBeforeFile;
    @XmlElement(name = "CommandBeforeOperation")
    protected String commandBeforeOperation;

    /**
     * Ruft den Wert der commandBeforeFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommandBeforeFile() {
        return commandBeforeFile;
    }

    /**
     * Legt den Wert der commandBeforeFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommandBeforeFile(String value) {
        this.commandBeforeFile = value;
    }

    /**
     * Ruft den Wert der commandBeforeOperation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommandBeforeOperation() {
        return commandBeforeOperation;
    }

    /**
     * Legt den Wert der commandBeforeOperation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommandBeforeOperation(String value) {
        this.commandBeforeOperation = value;
    }

}
