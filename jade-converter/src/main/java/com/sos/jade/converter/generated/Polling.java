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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element ref="{}PollInterval" minOccurs="0"/>
 *         &lt;element ref="{}PollTimeout" minOccurs="0"/>
 *         &lt;element ref="{}MinFiles" minOccurs="0"/>
 *         &lt;element ref="{}WaitForSourceFolder" minOccurs="0"/>
 *         &lt;element ref="{}PollErrorState" minOccurs="0"/>
 *         &lt;element ref="{}PollingServer" minOccurs="0"/>
 *         &lt;element ref="{}PollingServerDuration" minOccurs="0"/>
 *         &lt;element ref="{}PollForever" minOccurs="0"/>
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
    "pollInterval",
    "pollTimeout",
    "minFiles",
    "waitForSourceFolder",
    "pollErrorState",
    "pollingServer",
    "pollingServerDuration",
    "pollForever"
})
@XmlRootElement(name = "Polling")
public class Polling {

    @XmlElement(name = "PollInterval", defaultValue = "60")
    protected Integer pollInterval;
    @XmlElement(name = "PollTimeout", defaultValue = "0")
    protected Integer pollTimeout;
    @XmlElement(name = "MinFiles", defaultValue = "1")
    protected Integer minFiles;
    @XmlElement(name = "WaitForSourceFolder")
    protected Boolean waitForSourceFolder;
    @XmlElement(name = "PollErrorState")
    protected String pollErrorState;
    @XmlElement(name = "PollingServer", defaultValue = "false")
    protected Boolean pollingServer;
    @XmlElement(name = "PollingServerDuration", defaultValue = "0")
    protected Integer pollingServerDuration;
    @XmlElement(name = "PollForever", defaultValue = "false")
    protected Boolean pollForever;

    /**
     * Ruft den Wert der pollInterval-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPollInterval() {
        return pollInterval;
    }

    /**
     * Legt den Wert der pollInterval-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPollInterval(Integer value) {
        this.pollInterval = value;
    }

    /**
     * Ruft den Wert der pollTimeout-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPollTimeout() {
        return pollTimeout;
    }

    /**
     * Legt den Wert der pollTimeout-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPollTimeout(Integer value) {
        this.pollTimeout = value;
    }

    /**
     * Ruft den Wert der minFiles-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMinFiles() {
        return minFiles;
    }

    /**
     * Legt den Wert der minFiles-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMinFiles(Integer value) {
        this.minFiles = value;
    }

    /**
     * Ruft den Wert der waitForSourceFolder-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isWaitForSourceFolder() {
        return waitForSourceFolder;
    }

    /**
     * Legt den Wert der waitForSourceFolder-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWaitForSourceFolder(Boolean value) {
        this.waitForSourceFolder = value;
    }

    /**
     * Ruft den Wert der pollErrorState-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPollErrorState() {
        return pollErrorState;
    }

    /**
     * Legt den Wert der pollErrorState-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPollErrorState(String value) {
        this.pollErrorState = value;
    }

    /**
     * Ruft den Wert der pollingServer-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPollingServer() {
        return pollingServer;
    }

    /**
     * Legt den Wert der pollingServer-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPollingServer(Boolean value) {
        this.pollingServer = value;
    }

    /**
     * Ruft den Wert der pollingServerDuration-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPollingServerDuration() {
        return pollingServerDuration;
    }

    /**
     * Legt den Wert der pollingServerDuration-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPollingServerDuration(Integer value) {
        this.pollingServerDuration = value;
    }

    /**
     * Ruft den Wert der pollForever-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPollForever() {
        return pollForever;
    }

    /**
     * Legt den Wert der pollForever-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPollForever(Boolean value) {
        this.pollForever = value;
    }

}
