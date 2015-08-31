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
 * <p>Java-Klasse für BackgroundServiceType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="BackgroundServiceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}BackgroundServiceHost" minOccurs="0"/>
 *         &lt;element ref="{}BackgroundServicePort" minOccurs="0"/>
 *         &lt;element ref="{}BackgroundServiceProtocol" minOccurs="0"/>
 *         &lt;element ref="{}BackgroundServiceJobChainName" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BackgroundServiceType", propOrder = {
    "backgroundServiceHost",
    "backgroundServicePort",
    "backgroundServiceProtocol",
    "backgroundServiceJobChainName"
})
public class BackgroundServiceType {

    @XmlElement(name = "BackgroundServiceHost", defaultValue = "localhost")
    protected String backgroundServiceHost;
    @XmlElement(name = "BackgroundServicePort", defaultValue = "4444")
    protected Integer backgroundServicePort;
    @XmlElement(name = "BackgroundServiceProtocol", defaultValue = "udp")
    protected String backgroundServiceProtocol;
    @XmlElement(name = "BackgroundServiceJobChainName", defaultValue = "scheduler_jade_history")
    protected String backgroundServiceJobChainName;

    /**
     * Ruft den Wert der backgroundServiceHost-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBackgroundServiceHost() {
        return backgroundServiceHost;
    }

    /**
     * Legt den Wert der backgroundServiceHost-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBackgroundServiceHost(String value) {
        this.backgroundServiceHost = value;
    }

    /**
     * Ruft den Wert der backgroundServicePort-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBackgroundServicePort() {
        return backgroundServicePort;
    }

    /**
     * Legt den Wert der backgroundServicePort-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBackgroundServicePort(Integer value) {
        this.backgroundServicePort = value;
    }

    /**
     * Ruft den Wert der backgroundServiceProtocol-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBackgroundServiceProtocol() {
        return backgroundServiceProtocol;
    }

    /**
     * Legt den Wert der backgroundServiceProtocol-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBackgroundServiceProtocol(String value) {
        this.backgroundServiceProtocol = value;
    }

    /**
     * Ruft den Wert der backgroundServiceJobChainName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBackgroundServiceJobChainName() {
        return backgroundServiceJobChainName;
    }

    /**
     * Legt den Wert der backgroundServiceJobChainName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBackgroundServiceJobChainName(String value) {
        this.backgroundServiceJobChainName = value;
    }

}
