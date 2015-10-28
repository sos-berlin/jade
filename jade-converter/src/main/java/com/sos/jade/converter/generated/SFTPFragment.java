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
 *         &lt;element ref="{}BasicConnection"/>
 *         &lt;element ref="{}SSHAuthentication"/>
 *         &lt;element ref="{}CredentialStoreFragmentRef" minOccurs="0"/>
 *         &lt;element ref="{}JumpFragmentRef" minOccurs="0"/>
 *         &lt;element ref="{}ProxyForSFTP" minOccurs="0"/>
 *         &lt;element ref="{}StrictHostkeyChecking" minOccurs="0"/>
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
    "sshAuthentication",
    "credentialStoreFragmentRef",
    "jumpFragmentRef",
    "proxyForSFTP",
    "strictHostkeyChecking"
})
@XmlRootElement(name = "SFTPFragment")
public class SFTPFragment {

    @XmlElement(name = "BasicConnection", required = true)
    protected BasicConnectionType basicConnection;
    @XmlElement(name = "SSHAuthentication", required = true)
    protected SSHAuthenticationType sshAuthentication;
    @XmlElement(name = "CredentialStoreFragmentRef")
    protected CredentialStoreFragmentRef credentialStoreFragmentRef;
    @XmlElement(name = "JumpFragmentRef")
    protected JumpFragmentRef jumpFragmentRef;
    @XmlElement(name = "ProxyForSFTP")
    protected ProxyForSFTP proxyForSFTP;
    @XmlElement(name = "StrictHostkeyChecking", defaultValue = "false")
    protected Boolean strictHostkeyChecking;
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
     * Ruft den Wert der sshAuthentication-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SSHAuthenticationType }
     *     
     */
    public SSHAuthenticationType getSSHAuthentication() {
        return sshAuthentication;
    }

    /**
     * Legt den Wert der sshAuthentication-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SSHAuthenticationType }
     *     
     */
    public void setSSHAuthentication(SSHAuthenticationType value) {
        this.sshAuthentication = value;
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
     * Ruft den Wert der proxyForSFTP-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ProxyForSFTP }
     *     
     */
    public ProxyForSFTP getProxyForSFTP() {
        return proxyForSFTP;
    }

    /**
     * Legt den Wert der proxyForSFTP-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ProxyForSFTP }
     *     
     */
    public void setProxyForSFTP(ProxyForSFTP value) {
        this.proxyForSFTP = value;
    }

    /**
     * Ruft den Wert der strictHostkeyChecking-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isStrictHostkeyChecking() {
        return strictHostkeyChecking;
    }

    /**
     * Legt den Wert der strictHostkeyChecking-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setStrictHostkeyChecking(Boolean value) {
        this.strictHostkeyChecking = value;
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
