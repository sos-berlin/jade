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
 *         &lt;element ref="{}SOCKS4Proxy"/>
 *         &lt;element ref="{}SOCKS5Proxy"/>
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
    "socks4Proxy",
    "socks5Proxy"
})
@XmlRootElement(name = "ProxyForFTPS")
public class ProxyForFTPS {

    @XmlElement(name = "SOCKS4Proxy")
    protected UnauthenticatedProxyType socks4Proxy;
    @XmlElement(name = "SOCKS5Proxy")
    protected AuthenticatedProxyType socks5Proxy;

    /**
     * Ruft den Wert der socks4Proxy-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link UnauthenticatedProxyType }
     *     
     */
    public UnauthenticatedProxyType getSOCKS4Proxy() {
        return socks4Proxy;
    }

    /**
     * Legt den Wert der socks4Proxy-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link UnauthenticatedProxyType }
     *     
     */
    public void setSOCKS4Proxy(UnauthenticatedProxyType value) {
        this.socks4Proxy = value;
    }

    /**
     * Ruft den Wert der socks5Proxy-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AuthenticatedProxyType }
     *     
     */
    public AuthenticatedProxyType getSOCKS5Proxy() {
        return socks5Proxy;
    }

    /**
     * Legt den Wert der socks5Proxy-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthenticatedProxyType }
     *     
     */
    public void setSOCKS5Proxy(AuthenticatedProxyType value) {
        this.socks5Proxy = value;
    }

}
