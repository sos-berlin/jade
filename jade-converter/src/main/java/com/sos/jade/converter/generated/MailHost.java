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
 *         &lt;element ref="{}BasicConnection"/>
 *         &lt;element ref="{}BasicAuthentication" minOccurs="0"/>
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
    "basicConnection",
    "basicAuthentication"
})
@XmlRootElement(name = "MailHost")
public class MailHost {

    @XmlElement(name = "BasicConnection", required = true)
    protected BasicConnectionType basicConnection;
    @XmlElement(name = "BasicAuthentication")
    protected BasicAuthenticationType basicAuthentication;

    /**
     * Ruft den Wert der basicConnection-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BasicConnectionType }
     *     
     */
    public BasicConnectionType getBasicConnection() {
        return basicConnection;
    }

    /**
     * Legt den Wert der basicConnection-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BasicConnectionType }
     *     
     */
    public void setBasicConnection(BasicConnectionType value) {
        this.basicConnection = value;
    }

    /**
     * Ruft den Wert der basicAuthentication-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BasicAuthenticationType }
     *     
     */
    public BasicAuthenticationType getBasicAuthentication() {
        return basicAuthentication;
    }

    /**
     * Legt den Wert der basicAuthentication-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BasicAuthenticationType }
     *     
     */
    public void setBasicAuthentication(BasicAuthenticationType value) {
        this.basicAuthentication = value;
    }

}
