//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.02 at 02:31:20 PM MESZ 
//


package com.sos.jade.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}AuthenticationFile"/>
 *         &lt;element ref="{}Passphrase" minOccurs="0"/>
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
    "authenticationFile",
    "passphrase"
})
@XmlRootElement(name = "AuthenticationMethodPublickey")
public class AuthenticationMethodPublickey {

    @XmlElement(name = "AuthenticationFile", required = true)
    protected String authenticationFile;
    @XmlElement(name = "Passphrase")
    protected String passphrase;

    /**
     * Gets the value of the authenticationFile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthenticationFile() {
        return authenticationFile;
    }

    /**
     * Sets the value of the authenticationFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthenticationFile(String value) {
        this.authenticationFile = value;
    }

    /**
     * Gets the value of the passphrase property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassphrase() {
        return passphrase;
    }

    /**
     * Sets the value of the passphrase property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassphrase(String value) {
        this.passphrase = value;
    }

}
