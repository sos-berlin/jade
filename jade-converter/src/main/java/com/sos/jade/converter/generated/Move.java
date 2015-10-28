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
 *         &lt;element ref="{}MoveSource"/>
 *         &lt;element ref="{}MoveTarget"/>
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
    "moveSource",
    "moveTarget",
    "transferOptions"
})
@XmlRootElement(name = "Move")
public class Move {

    @XmlElement(name = "MoveSource", required = true)
    protected MoveSource moveSource;
    @XmlElement(name = "MoveTarget", required = true)
    protected MoveTarget moveTarget;
    @XmlElement(name = "TransferOptions")
    protected TransferOptions transferOptions;

    /**
     * Ruft den Wert der moveSource-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MoveSource }
     *     
     */
    public MoveSource getMoveSource() {
        return moveSource;
    }

    /**
     * Legt den Wert der moveSource-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MoveSource }
     *     
     */
    public void setMoveSource(MoveSource value) {
        this.moveSource = value;
    }

    /**
     * Ruft den Wert der moveTarget-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MoveTarget }
     *     
     */
    public MoveTarget getMoveTarget() {
        return moveTarget;
    }

    /**
     * Legt den Wert der moveTarget-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MoveTarget }
     *     
     */
    public void setMoveTarget(MoveTarget value) {
        this.moveTarget = value;
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
