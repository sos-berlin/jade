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
 *         &lt;element ref="{}BufferSize" minOccurs="0"/>
 *         &lt;element ref="{}ConcurrentTransfer" minOccurs="0"/>
 *         &lt;element ref="{}Transactional" minOccurs="0"/>
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
    "bufferSize",
    "concurrentTransfer",
    "transactional"
})
@XmlRootElement(name = "TransferOptions")
public class TransferOptions {

    @XmlElement(name = "BufferSize", defaultValue = "4096")
    protected Integer bufferSize;
    @XmlElement(name = "ConcurrentTransfer")
    protected ConcurrencyType concurrentTransfer;
    @XmlElement(name = "Transactional", defaultValue = "false")
    protected Boolean transactional;

    /**
     * Ruft den Wert der bufferSize-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBufferSize() {
        return bufferSize;
    }

    /**
     * Legt den Wert der bufferSize-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBufferSize(Integer value) {
        this.bufferSize = value;
    }

    /**
     * Ruft den Wert der concurrentTransfer-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ConcurrencyType }
     *     
     */
    public ConcurrencyType getConcurrentTransfer() {
        return concurrentTransfer;
    }

    /**
     * Legt den Wert der concurrentTransfer-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ConcurrencyType }
     *     
     */
    public void setConcurrentTransfer(ConcurrencyType value) {
        this.concurrentTransfer = value;
    }

    /**
     * Ruft den Wert der transactional-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTransactional() {
        return transactional;
    }

    /**
     * Legt den Wert der transactional-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTransactional(Boolean value) {
        this.transactional = value;
    }

}
