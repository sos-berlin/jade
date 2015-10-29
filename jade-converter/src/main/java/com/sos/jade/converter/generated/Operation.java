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
 *       &lt;choice>
 *         &lt;element ref="{}Copy"/>
 *         &lt;element ref="{}Move"/>
 *         &lt;element ref="{}Remove"/>
 *         &lt;element ref="{}GetList"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "copy",
    "move",
    "remove",
    "getList"
})
@XmlRootElement(name = "Operation")
public class Operation {

    @XmlElement(name = "Copy")
    protected Copy copy;
    @XmlElement(name = "Move")
    protected Move move;
    @XmlElement(name = "Remove")
    protected Remove remove;
    @XmlElement(name = "GetList")
    protected GetList getList;

    /**
     * Ruft den Wert der copy-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Copy }
     *     
     */
    public Copy getCopy() {
        return copy;
    }

    /**
     * Legt den Wert der copy-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Copy }
     *     
     */
    public void setCopy(Copy value) {
        this.copy = value;
    }

    /**
     * Ruft den Wert der move-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Move }
     *     
     */
    public Move getMove() {
        return move;
    }

    /**
     * Legt den Wert der move-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Move }
     *     
     */
    public void setMove(Move value) {
        this.move = value;
    }

    /**
     * Ruft den Wert der remove-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Remove }
     *     
     */
    public Remove getRemove() {
        return remove;
    }

    /**
     * Legt den Wert der remove-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Remove }
     *     
     */
    public void setRemove(Remove value) {
        this.remove = value;
    }

    /**
     * Ruft den Wert der getList-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GetList }
     *     
     */
    public GetList getGetList() {
        return getList;
    }

    /**
     * Legt den Wert der getList-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GetList }
     *     
     */
    public void setGetList(GetList value) {
        this.getList = value;
    }

}
