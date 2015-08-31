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
 *         &lt;element ref="{}Hostname"/>
 *         &lt;element ref="{}SMBAuthentication"/>
 *         &lt;element ref="{}CredentialStoreFragmentRef" minOccurs="0"/>
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
    "hostname",
    "smbAuthentication",
    "credentialStoreFragmentRef"
})
@XmlRootElement(name = "SMBFragment")
public class SMBFragment {

    @XmlElement(name = "Hostname", required = true)
    protected String hostname;
    @XmlElement(name = "SMBAuthentication", required = true)
    protected SMBAuthentication smbAuthentication;
    @XmlElement(name = "CredentialStoreFragmentRef")
    protected CredentialStoreFragmentRef credentialStoreFragmentRef;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String name;

    /**
     * Ruft den Wert der hostname-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Legt den Wert der hostname-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHostname(String value) {
        this.hostname = value;
    }

    /**
     * Ruft den Wert der smbAuthentication-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SMBAuthentication }
     *     
     */
    public SMBAuthentication getSMBAuthentication() {
        return smbAuthentication;
    }

    /**
     * Legt den Wert der smbAuthentication-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SMBAuthentication }
     *     
     */
    public void setSMBAuthentication(SMBAuthentication value) {
        this.smbAuthentication = value;
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
