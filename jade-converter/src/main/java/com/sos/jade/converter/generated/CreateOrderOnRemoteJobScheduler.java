//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.10.09 um 03:42:30 PM CEST 
//


package com.sos.jade.converter.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}JobSchedulerHostname"/>
 *         &lt;element ref="{}JobSchedulerPort" minOccurs="0"/>
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
    "jobSchedulerHostname",
    "jobSchedulerPort"
})
@XmlRootElement(name = "CreateOrderOnRemoteJobScheduler")
public class CreateOrderOnRemoteJobScheduler {

    @XmlElement(name = "JobSchedulerHostname", required = true)
    protected String jobSchedulerHostname;
    @XmlElement(name = "JobSchedulerPort", defaultValue = "4444")
    protected Integer jobSchedulerPort;

    /**
     * Ruft den Wert der jobSchedulerHostname-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJobSchedulerHostname() {
        return jobSchedulerHostname;
    }

    /**
     * Legt den Wert der jobSchedulerHostname-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJobSchedulerHostname(String value) {
        this.jobSchedulerHostname = value;
    }

    /**
     * Ruft den Wert der jobSchedulerPort-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getJobSchedulerPort() {
        return jobSchedulerPort;
    }

    /**
     * Legt den Wert der jobSchedulerPort-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setJobSchedulerPort(Integer value) {
        this.jobSchedulerPort = value;
    }

}
