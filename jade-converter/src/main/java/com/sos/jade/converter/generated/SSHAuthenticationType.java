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
 * <p>Java-Klasse für SSHAuthenticationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="SSHAuthenticationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}Account"/>
 *         &lt;choice>
 *           &lt;element ref="{}AuthenticationMethodPassword"/>
 *           &lt;element ref="{}AuthenticationMethodPublickey"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SSHAuthenticationType", propOrder = {
    "account",
    "authenticationMethodPassword",
    "authenticationMethodPublickey"
})
public class SSHAuthenticationType {

    @XmlElement(name = "Account", required = true)
    protected String account;
    @XmlElement(name = "AuthenticationMethodPassword")
    protected AuthenticationMethodPassword authenticationMethodPassword;
    @XmlElement(name = "AuthenticationMethodPublickey")
    protected AuthenticationMethodPublickey authenticationMethodPublickey;

    /**
     * Ruft den Wert der account-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccount() {
        return account;
    }

    /**
     * Legt den Wert der account-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccount(String value) {
        this.account = value;
    }

    /**
     * Ruft den Wert der authenticationMethodPassword-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AuthenticationMethodPassword }
     *     
     */
    public AuthenticationMethodPassword getAuthenticationMethodPassword() {
        return authenticationMethodPassword;
    }

    /**
     * Legt den Wert der authenticationMethodPassword-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthenticationMethodPassword }
     *     
     */
    public void setAuthenticationMethodPassword(AuthenticationMethodPassword value) {
        this.authenticationMethodPassword = value;
    }

    /**
     * Ruft den Wert der authenticationMethodPublickey-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AuthenticationMethodPublickey }
     *     
     */
    public AuthenticationMethodPublickey getAuthenticationMethodPublickey() {
        return authenticationMethodPublickey;
    }

    /**
     * Legt den Wert der authenticationMethodPublickey-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthenticationMethodPublickey }
     *     
     */
    public void setAuthenticationMethodPublickey(AuthenticationMethodPublickey value) {
        this.authenticationMethodPublickey = value;
    }

}
