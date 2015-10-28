//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.10.09 um 03:42:30 PM CEST 
//


package com.sos.jade.converter.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


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
 *         &lt;element ref="{}URLConnection"/>
 *         &lt;element ref="{}AcceptUntrustedCertificate" minOccurs="0"/>
 *         &lt;element ref="{}DisableCertificateHostnameVerification" minOccurs="0"/>
 *         &lt;element ref="{}BasicAuthentication" minOccurs="0"/>
 *         &lt;element ref="{}CredentialStoreFragmentRef" minOccurs="0"/>
 *         &lt;element ref="{}JumpFragmentRef" minOccurs="0"/>
 *         &lt;element ref="{}ProxyForHTTP" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "urlConnection",
    "acceptUntrustedCertificate",
    "disableCertificateHostnameVerification",
    "basicAuthentication",
    "credentialStoreFragmentRef",
    "jumpFragmentRef",
    "proxyForHTTP"
})
@XmlRootElement(name = "HTTPSFragment")
public class HTTPSFragment {

    @XmlElement(name = "URLConnection", required = true)
    protected URLConnectionType urlConnection;
    @XmlElement(name = "AcceptUntrustedCertificate", defaultValue = "false")
    protected Boolean acceptUntrustedCertificate;
    @XmlElement(name = "DisableCertificateHostnameVerification", defaultValue = "false")
    protected Boolean disableCertificateHostnameVerification;
    @XmlElement(name = "BasicAuthentication")
    protected BasicAuthenticationType basicAuthentication;
    @XmlElement(name = "CredentialStoreFragmentRef")
    protected CredentialStoreFragmentRef credentialStoreFragmentRef;
    @XmlElement(name = "JumpFragmentRef")
    protected JumpFragmentRef jumpFragmentRef;
    @XmlElement(name = "ProxyForHTTP")
    protected ProxyForHTTP proxyForHTTP;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String name;

    /**
     * Ruft den Wert der urlConnection-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link URLConnectionType }
     *     
     */
    public URLConnectionType getURLConnection() {
        return urlConnection;
    }

    /**
     * Legt den Wert der urlConnection-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link URLConnectionType }
     *     
     */
    public void setURLConnection(URLConnectionType value) {
        this.urlConnection = value;
    }

    /**
     * Ruft den Wert der acceptUntrustedCertificate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAcceptUntrustedCertificate() {
        return acceptUntrustedCertificate;
    }

    /**
     * Legt den Wert der acceptUntrustedCertificate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAcceptUntrustedCertificate(Boolean value) {
        this.acceptUntrustedCertificate = value;
    }

    /**
     * Ruft den Wert der disableCertificateHostnameVerification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDisableCertificateHostnameVerification() {
        return disableCertificateHostnameVerification;
    }

    /**
     * Legt den Wert der disableCertificateHostnameVerification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDisableCertificateHostnameVerification(Boolean value) {
        this.disableCertificateHostnameVerification = value;
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

    /**
     * Ruft den Wert der credentialStoreFragmentRef-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CredentialStoreFragmentRef }
     *     
     */
    public CredentialStoreFragmentRef getCredentialStoreFragmentRef() {
        return credentialStoreFragmentRef;
    }

    /**
     * Legt den Wert der credentialStoreFragmentRef-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CredentialStoreFragmentRef }
     *     
     */
    public void setCredentialStoreFragmentRef(CredentialStoreFragmentRef value) {
        this.credentialStoreFragmentRef = value;
    }

    /**
     * Ruft den Wert der jumpFragmentRef-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link JumpFragmentRef }
     *     
     */
    public JumpFragmentRef getJumpFragmentRef() {
        return jumpFragmentRef;
    }

    /**
     * Legt den Wert der jumpFragmentRef-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link JumpFragmentRef }
     *     
     */
    public void setJumpFragmentRef(JumpFragmentRef value) {
        this.jumpFragmentRef = value;
    }

    /**
     * Ruft den Wert der proxyForHTTP-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ProxyForHTTP }
     *     
     */
    public ProxyForHTTP getProxyForHTTP() {
        return proxyForHTTP;
    }

    /**
     * Legt den Wert der proxyForHTTP-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ProxyForHTTP }
     *     
     */
    public void setProxyForHTTP(ProxyForHTTP value) {
        this.proxyForHTTP = value;
    }

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
