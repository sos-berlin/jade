//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.08.28 um 03:50:36 PM CEST 
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
 *         &lt;element ref="{}BasicConnection"/>
 *         &lt;element ref="{}BasicAuthentication"/>
 *         &lt;element ref="{}FTPSClientSecurity" minOccurs="0"/>
 *         &lt;element ref="{}FTPSProtocol" minOccurs="0"/>
 *         &lt;element ref="{}CredentialStoreFragmentRef" minOccurs="0"/>
 *         &lt;element ref="{}JumpFragmentRef" minOccurs="0"/>
 *         &lt;element ref="{}ProxyForFTPS" minOccurs="0"/>
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
    "basicConnection",
    "basicAuthentication",
    "ftpsClientSecurity",
    "ftpsProtocol",
    "credentialStoreFragmentRef",
    "jumpFragmentRef",
    "proxyForFTPS"
})
@XmlRootElement(name = "FTPSFragment")
public class FTPSFragment {

    @XmlElement(name = "BasicConnection", required = true)
    protected BasicConnectionType basicConnection;
    @XmlElement(name = "BasicAuthentication", required = true)
    protected BasicAuthenticationType basicAuthentication;
    @XmlElement(name = "FTPSClientSecurity")
    protected FTPSClientSecurityType ftpsClientSecurity;
    @XmlElement(name = "FTPSProtocol", defaultValue = "SSL")
    protected String ftpsProtocol;
    @XmlElement(name = "CredentialStoreFragmentRef")
    protected CredentialStoreFragmentRef credentialStoreFragmentRef;
    @XmlElement(name = "JumpFragmentRef")
    protected JumpFragmentRef jumpFragmentRef;
    @XmlElement(name = "ProxyForFTPS")
    protected ProxyForFTPS proxyForFTPS;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String name;

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

    /**
     * Ruft den Wert der ftpsClientSecurity-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FTPSClientSecurityType }
     *     
     */
    public FTPSClientSecurityType getFTPSClientSecurity() {
        return ftpsClientSecurity;
    }

    /**
     * Legt den Wert der ftpsClientSecurity-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FTPSClientSecurityType }
     *     
     */
    public void setFTPSClientSecurity(FTPSClientSecurityType value) {
        this.ftpsClientSecurity = value;
    }

    /**
     * Ruft den Wert der ftpsProtocol-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFTPSProtocol() {
        return ftpsProtocol;
    }

    /**
     * Legt den Wert der ftpsProtocol-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFTPSProtocol(String value) {
        this.ftpsProtocol = value;
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
     * Ruft den Wert der proxyForFTPS-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ProxyForFTPS }
     *     
     */
    public ProxyForFTPS getProxyForFTPS() {
        return proxyForFTPS;
    }

    /**
     * Legt den Wert der proxyForFTPS-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ProxyForFTPS }
     *     
     */
    public void setProxyForFTPS(ProxyForFTPS value) {
        this.proxyForFTPS = value;
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
