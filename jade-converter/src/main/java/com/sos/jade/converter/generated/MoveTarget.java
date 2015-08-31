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
 *         &lt;element ref="{}MoveTargetFragmentRef"/>
 *         &lt;element ref="{}Directory"/>
 *         &lt;element ref="{}TargetFileOptions" minOccurs="0"/>
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
    "moveTargetFragmentRef",
    "directory",
    "targetFileOptions"
})
@XmlRootElement(name = "MoveTarget")
public class MoveTarget {

    @XmlElement(name = "MoveTargetFragmentRef", required = true)
    protected WriteableFragmentRefType moveTargetFragmentRef;
    @XmlElement(name = "Directory", required = true)
    protected String directory;
    @XmlElement(name = "TargetFileOptions")
    protected TargetFileOptions targetFileOptions;

    /**
     * Ruft den Wert der moveTargetFragmentRef-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link WriteableFragmentRefType }
     *     
     */
    public WriteableFragmentRefType getMoveTargetFragmentRef() {
        return moveTargetFragmentRef;
    }

    /**
     * Legt den Wert der moveTargetFragmentRef-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link WriteableFragmentRefType }
     *     
     */
    public void setMoveTargetFragmentRef(WriteableFragmentRefType value) {
        this.moveTargetFragmentRef = value;
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
     * Ruft den Wert der targetFileOptions-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TargetFileOptions }
     *     
     */
    public TargetFileOptions getTargetFileOptions() {
        return targetFileOptions;
    }

    /**
     * Legt den Wert der targetFileOptions-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TargetFileOptions }
     *     
     */
    public void setTargetFileOptions(TargetFileOptions value) {
        this.targetFileOptions = value;
    }

}
