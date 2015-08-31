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
 *         &lt;element ref="{}CheckSteadyStateInterval" minOccurs="0"/>
 *         &lt;element ref="{}CheckSteadyStateCount" minOccurs="0"/>
 *         &lt;element ref="{}CheckSteadyStateErrorState" minOccurs="0"/>
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
    "checkSteadyStateInterval",
    "checkSteadyStateCount",
    "checkSteadyStateErrorState"
})
@XmlRootElement(name = "CheckSteadyState")
public class CheckSteadyState {

    @XmlElement(name = "CheckSteadyStateInterval", defaultValue = "1")
    protected Integer checkSteadyStateInterval;
    @XmlElement(name = "CheckSteadyStateCount", defaultValue = "30")
    protected Integer checkSteadyStateCount;
    @XmlElement(name = "CheckSteadyStateErrorState")
    protected String checkSteadyStateErrorState;

    /**
     * Ruft den Wert der checkSteadyStateInterval-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCheckSteadyStateInterval() {
        return checkSteadyStateInterval;
    }

    /**
     * Legt den Wert der checkSteadyStateInterval-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCheckSteadyStateInterval(Integer value) {
        this.checkSteadyStateInterval = value;
    }

    /**
     * Ruft den Wert der checkSteadyStateCount-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCheckSteadyStateCount() {
        return checkSteadyStateCount;
    }

    /**
     * Legt den Wert der checkSteadyStateCount-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCheckSteadyStateCount(Integer value) {
        this.checkSteadyStateCount = value;
    }

    /**
     * Ruft den Wert der checkSteadyStateErrorState-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCheckSteadyStateErrorState() {
        return checkSteadyStateErrorState;
    }

    /**
     * Legt den Wert der checkSteadyStateErrorState-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCheckSteadyStateErrorState(String value) {
        this.checkSteadyStateErrorState = value;
    }

}
