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
 *         &lt;element ref="{}RemoveSource"/>
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
    "removeSource"
})
@XmlRootElement(name = "Remove")
public class Remove {

    @XmlElement(name = "RemoveSource", required = true)
    protected RemoveSource removeSource;

    /**
     * Ruft den Wert der removeSource-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RemoveSource }
     *     
     */
    public RemoveSource getRemoveSource() {
        return removeSource;
    }

    /**
     * Legt den Wert der removeSource-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RemoveSource }
     *     
     */
    public void setRemoveSource(RemoveSource value) {
        this.removeSource = value;
    }

}
