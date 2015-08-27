//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.08.25 at 03:34:05 PM MESZ 
//


package com.sos.jade.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReadableFragmentRefType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReadableFragmentRefType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{}FTPFragmentRef"/>
 *         &lt;element ref="{}FTPSFragmentRef"/>
 *         &lt;element ref="{}HTTPFragmentRef"/>
 *         &lt;element ref="{}HTTPSFragmentRef"/>
 *         &lt;element ref="{}LocalSource"/>
 *         &lt;element ref="{}SFTPFragmentRef"/>
 *         &lt;element ref="{}SMBFragmentRef"/>
 *         &lt;element ref="{}WebDAVFragmentRef"/>
 *         &lt;element ref="{}ReadableAlternativeFragmentRef"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReadableFragmentRefType", propOrder = {
    "ftpFragmentRef",
    "ftpsFragmentRef",
    "httpFragmentRef",
    "httpsFragmentRef",
    "localSource",
    "sftpFragmentRef",
    "smbFragmentRef",
    "webDAVFragmentRef",
    "readableAlternativeFragmentRef"
})
public class ReadableFragmentRefType {

    @XmlElement(name = "FTPFragmentRef")
    protected FTPFragmentRef ftpFragmentRef;
    @XmlElement(name = "FTPSFragmentRef")
    protected FTPSFragmentRef ftpsFragmentRef;
    @XmlElement(name = "HTTPFragmentRef")
    protected HTTPFragmentRef httpFragmentRef;
    @XmlElement(name = "HTTPSFragmentRef")
    protected HTTPSFragmentRef httpsFragmentRef;
    @XmlElement(name = "LocalSource")
    protected LocalSource localSource;
    @XmlElement(name = "SFTPFragmentRef")
    protected SFTPFragmentRef sftpFragmentRef;
    @XmlElement(name = "SMBFragmentRef")
    protected SMBFragmentRef smbFragmentRef;
    @XmlElement(name = "WebDAVFragmentRef")
    protected WebDAVFragmentRef webDAVFragmentRef;
    @XmlElement(name = "ReadableAlternativeFragmentRef")
    protected ReadableAlternativeFragmentRef readableAlternativeFragmentRef;

    /**
     * Gets the value of the ftpFragmentRef property.
     * 
     * @return
     *     possible object is
     *     {@link FTPFragmentRef }
     *     
     */
    public FTPFragmentRef getFTPFragmentRef() {
        return ftpFragmentRef;
    }

    /**
     * Sets the value of the ftpFragmentRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link FTPFragmentRef }
     *     
     */
    public void setFTPFragmentRef(FTPFragmentRef value) {
        this.ftpFragmentRef = value;
    }

    /**
     * Gets the value of the ftpsFragmentRef property.
     * 
     * @return
     *     possible object is
     *     {@link FTPSFragmentRef }
     *     
     */
    public FTPSFragmentRef getFTPSFragmentRef() {
        return ftpsFragmentRef;
    }

    /**
     * Sets the value of the ftpsFragmentRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link FTPSFragmentRef }
     *     
     */
    public void setFTPSFragmentRef(FTPSFragmentRef value) {
        this.ftpsFragmentRef = value;
    }

    /**
     * Gets the value of the httpFragmentRef property.
     * 
     * @return
     *     possible object is
     *     {@link HTTPFragmentRef }
     *     
     */
    public HTTPFragmentRef getHTTPFragmentRef() {
        return httpFragmentRef;
    }

    /**
     * Sets the value of the httpFragmentRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link HTTPFragmentRef }
     *     
     */
    public void setHTTPFragmentRef(HTTPFragmentRef value) {
        this.httpFragmentRef = value;
    }

    /**
     * Gets the value of the httpsFragmentRef property.
     * 
     * @return
     *     possible object is
     *     {@link HTTPSFragmentRef }
     *     
     */
    public HTTPSFragmentRef getHTTPSFragmentRef() {
        return httpsFragmentRef;
    }

    /**
     * Sets the value of the httpsFragmentRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link HTTPSFragmentRef }
     *     
     */
    public void setHTTPSFragmentRef(HTTPSFragmentRef value) {
        this.httpsFragmentRef = value;
    }

    /**
     * Gets the value of the localSource property.
     * 
     * @return
     *     possible object is
     *     {@link LocalSource }
     *     
     */
    public LocalSource getLocalSource() {
        return localSource;
    }

    /**
     * Sets the value of the localSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocalSource }
     *     
     */
    public void setLocalSource(LocalSource value) {
        this.localSource = value;
    }

    /**
     * Gets the value of the sftpFragmentRef property.
     * 
     * @return
     *     possible object is
     *     {@link SFTPFragmentRef }
     *     
     */
    public SFTPFragmentRef getSFTPFragmentRef() {
        return sftpFragmentRef;
    }

    /**
     * Sets the value of the sftpFragmentRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link SFTPFragmentRef }
     *     
     */
    public void setSFTPFragmentRef(SFTPFragmentRef value) {
        this.sftpFragmentRef = value;
    }

    /**
     * Gets the value of the smbFragmentRef property.
     * 
     * @return
     *     possible object is
     *     {@link SMBFragmentRef }
     *     
     */
    public SMBFragmentRef getSMBFragmentRef() {
        return smbFragmentRef;
    }

    /**
     * Sets the value of the smbFragmentRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link SMBFragmentRef }
     *     
     */
    public void setSMBFragmentRef(SMBFragmentRef value) {
        this.smbFragmentRef = value;
    }

    /**
     * Gets the value of the webDAVFragmentRef property.
     * 
     * @return
     *     possible object is
     *     {@link WebDAVFragmentRef }
     *     
     */
    public WebDAVFragmentRef getWebDAVFragmentRef() {
        return webDAVFragmentRef;
    }

    /**
     * Sets the value of the webDAVFragmentRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebDAVFragmentRef }
     *     
     */
    public void setWebDAVFragmentRef(WebDAVFragmentRef value) {
        this.webDAVFragmentRef = value;
    }

    /**
     * Gets the value of the readableAlternativeFragmentRef property.
     * 
     * @return
     *     possible object is
     *     {@link ReadableAlternativeFragmentRef }
     *     
     */
    public ReadableAlternativeFragmentRef getReadableAlternativeFragmentRef() {
        return readableAlternativeFragmentRef;
    }

    /**
     * Sets the value of the readableAlternativeFragmentRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReadableAlternativeFragmentRef }
     *     
     */
    public void setReadableAlternativeFragmentRef(ReadableAlternativeFragmentRef value) {
        this.readableAlternativeFragmentRef = value;
    }

}