//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.02 at 02:31:20 PM MESZ 
//


package com.sos.jade.generated;

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
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}URLConnection"/>
 *         &lt;element ref="{}BasicAuthentication" minOccurs="0"/>
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
    "basicAuthentication",
    "jumpFragmentRef",
    "proxyForHTTP"
})
@XmlRootElement(name = "HTTPFragment")
public class HTTPFragment {

    @XmlElement(name = "URLConnection", required = true)
    protected URLConnectionType urlConnection;
    @XmlElement(name = "BasicAuthentication")
    protected BasicAuthenticationType basicAuthentication;
    @XmlElement(name = "JumpFragmentRef")
    protected JumpFragmentRef jumpFragmentRef;
    @XmlElement(name = "ProxyForHTTP")
    protected ProxyForHTTP proxyForHTTP;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String name;

    /**
     * Gets the value of the urlConnection property.
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
     * Sets the value of the urlConnection property.
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
     * Gets the value of the basicAuthentication property.
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
     * Sets the value of the basicAuthentication property.
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
     * Gets the value of the jumpFragmentRef property.
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
     * Sets the value of the jumpFragmentRef property.
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
     * Gets the value of the proxyForHTTP property.
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
     * Sets the value of the proxyForHTTP property.
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
     * Gets the value of the name property.
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
     * Sets the value of the name property.
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
