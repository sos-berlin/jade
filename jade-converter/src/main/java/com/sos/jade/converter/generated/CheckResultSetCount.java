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
 *         &lt;element ref="{}ExpectedResultSetCount"/>
 *         &lt;element ref="{}RaiseErrorIfResultSetIs"/>
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
    "expectedResultSetCount",
    "raiseErrorIfResultSetIs"
})
@XmlRootElement(name = "CheckResultSetCount")
public class CheckResultSetCount {

    @XmlElement(name = "ExpectedResultSetCount")
    protected int expectedResultSetCount;
    @XmlElement(name = "RaiseErrorIfResultSetIs", required = true)
    protected String raiseErrorIfResultSetIs;

    /**
     * Ruft den Wert der expectedResultSetCount-Eigenschaft ab.
     * 
     */
    public int getExpectedResultSetCount() {
        return expectedResultSetCount;
    }

    /**
     * Legt den Wert der expectedResultSetCount-Eigenschaft fest.
     * 
     */
    public void setExpectedResultSetCount(int value) {
        this.expectedResultSetCount = value;
    }

    /**
     * Ruft den Wert der raiseErrorIfResultSetIs-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRaiseErrorIfResultSetIs() {
        return raiseErrorIfResultSetIs;
    }

    /**
     * Legt den Wert der raiseErrorIfResultSetIs-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRaiseErrorIfResultSetIs(String value) {
        this.raiseErrorIfResultSetIs = value;
    }

}
