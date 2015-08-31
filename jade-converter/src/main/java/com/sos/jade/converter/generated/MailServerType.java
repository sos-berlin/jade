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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für MailServerType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="MailServerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}MailHost"/>
 *         &lt;element ref="{}QueueDirectory" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MailServerType", propOrder = {
    "mailHost",
    "queueDirectory"
})
@XmlSeeAlso({
    MailServerFragment.class
})
public class MailServerType {

    @XmlElement(name = "MailHost", required = true)
    protected MailHost mailHost;
    @XmlElement(name = "QueueDirectory")
    protected String queueDirectory;

    /**
     * Ruft den Wert der mailHost-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MailHost }
     *     
     */
    public MailHost getMailHost() {
        return mailHost;
    }

    /**
     * Legt den Wert der mailHost-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MailHost }
     *     
     */
    public void setMailHost(MailHost value) {
        this.mailHost = value;
    }

    /**
     * Ruft den Wert der queueDirectory-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueueDirectory() {
        return queueDirectory;
    }

    /**
     * Legt den Wert der queueDirectory-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueueDirectory(String value) {
        this.queueDirectory = value;
    }

}
