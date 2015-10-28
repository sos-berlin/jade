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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ReadableFragmentRefType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
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
    "webDAVFragmentRef"
})
@XmlSeeAlso({
    AlternativeCopySourceFragmentRef.class
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

    /**
     * Ruft den Wert der ftpFragmentRef-Eigenschaft ab.
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
     * Legt den Wert der ftpFragmentRef-Eigenschaft fest.
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
     * Ruft den Wert der ftpsFragmentRef-Eigenschaft ab.
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
     * Legt den Wert der ftpsFragmentRef-Eigenschaft fest.
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
     * Ruft den Wert der httpFragmentRef-Eigenschaft ab.
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
     * Legt den Wert der httpFragmentRef-Eigenschaft fest.
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
     * Ruft den Wert der httpsFragmentRef-Eigenschaft ab.
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
     * Legt den Wert der httpsFragmentRef-Eigenschaft fest.
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
     * Ruft den Wert der localSource-Eigenschaft ab.
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
     * Legt den Wert der localSource-Eigenschaft fest.
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
     * Ruft den Wert der sftpFragmentRef-Eigenschaft ab.
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
     * Legt den Wert der sftpFragmentRef-Eigenschaft fest.
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
     * Ruft den Wert der smbFragmentRef-Eigenschaft ab.
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
     * Legt den Wert der smbFragmentRef-Eigenschaft fest.
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
     * Ruft den Wert der webDAVFragmentRef-Eigenschaft ab.
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
     * Legt den Wert der webDAVFragmentRef-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link WebDAVFragmentRef }
     *     
     */
    public void setWebDAVFragmentRef(WebDAVFragmentRef value) {
        this.webDAVFragmentRef = value;
    }

}
