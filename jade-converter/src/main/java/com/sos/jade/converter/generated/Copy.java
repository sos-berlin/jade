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
 *         &lt;element ref="{}CopySource"/>
 *         &lt;element ref="{}CopyTarget"/>
 *         &lt;element ref="{}TransferOptions" minOccurs="0"/>
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
    "copySource",
    "copyTarget",
    "transferOptions"
})
@XmlRootElement(name = "Copy")
public class Copy {

    @XmlElement(name = "CopySource", required = true)
    protected CopySource copySource;
    @XmlElement(name = "CopyTarget", required = true)
    protected CopyTarget copyTarget;
    @XmlElement(name = "TransferOptions")
    protected TransferOptions transferOptions;

    /**
     * Ruft den Wert der copySource-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CopySource }
     *     
     */
    public CopySource getCopySource() {
        return copySource;
    }

    /**
     * Legt den Wert der copySource-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CopySource }
     *     
     */
    public void setCopySource(CopySource value) {
        this.copySource = value;
    }

    /**
     * Ruft den Wert der copyTarget-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CopyTarget }
     *     
     */
    public CopyTarget getCopyTarget() {
        return copyTarget;
    }

    /**
     * Legt den Wert der copyTarget-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CopyTarget }
     *     
     */
    public void setCopyTarget(CopyTarget value) {
        this.copyTarget = value;
    }

    /**
     * Ruft den Wert der transferOptions-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransferOptions }
     *     
     */
    public TransferOptions getTransferOptions() {
        return transferOptions;
    }

    /**
     * Legt den Wert der transferOptions-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransferOptions }
     *     
     */
    public void setTransferOptions(TransferOptions value) {
        this.transferOptions = value;
    }

}
