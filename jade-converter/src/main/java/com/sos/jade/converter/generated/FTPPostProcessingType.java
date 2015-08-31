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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für FTPPostProcessingType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="FTPPostProcessingType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}CommandAfterFile" minOccurs="0"/>
 *         &lt;element ref="{}CommandAfterOperation" minOccurs="0"/>
 *         &lt;element ref="{}CommandBeforeRename" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FTPPostProcessingType", propOrder = {
    "commandAfterFile",
    "commandAfterOperation",
    "commandBeforeRename"
})
public class FTPPostProcessingType {

    @XmlElement(name = "CommandAfterFile")
    protected String commandAfterFile;
    @XmlElement(name = "CommandAfterOperation")
    protected String commandAfterOperation;
    @XmlElement(name = "CommandBeforeRename")
    protected String commandBeforeRename;

    /**
     * Ruft den Wert der commandAfterFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommandAfterFile() {
        return commandAfterFile;
    }

    /**
     * Legt den Wert der commandAfterFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommandAfterFile(String value) {
        this.commandAfterFile = value;
    }

    /**
     * Ruft den Wert der commandAfterOperation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommandAfterOperation() {
        return commandAfterOperation;
    }

    /**
     * Legt den Wert der commandAfterOperation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommandAfterOperation(String value) {
        this.commandAfterOperation = value;
    }

    /**
     * Ruft den Wert der commandBeforeRename-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommandBeforeRename() {
        return commandBeforeRename;
    }

    /**
     * Legt den Wert der commandBeforeRename-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommandBeforeRename(String value) {
        this.commandBeforeRename = value;
    }

}
