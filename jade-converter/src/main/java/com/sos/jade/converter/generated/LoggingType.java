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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für LoggingType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="LoggingType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}LogFile" minOccurs="0"/>
 *         &lt;element ref="{}Log4JPropertyFile" minOccurs="0"/>
 *         &lt;element ref="{}DebugLevel" minOccurs="0"/>
 *         &lt;element ref="{}ProtocolCommandListener" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LoggingType", propOrder = {
    "logFile",
    "log4JPropertyFile",
    "debugLevel",
    "protocolCommandListener"
})
public class LoggingType {

    @XmlElement(name = "LogFile", defaultValue = "stdout")
    protected String logFile;
    @XmlElement(name = "Log4JPropertyFile", defaultValue = "./log4j.properties")
    protected String log4JPropertyFile;
    @XmlElement(name = "DebugLevel", defaultValue = "0")
    protected Integer debugLevel;
    @XmlElement(name = "ProtocolCommandListener", defaultValue = "false")
    protected Boolean protocolCommandListener;

    /**
     * Ruft den Wert der logFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLogFile() {
        return logFile;
    }

    /**
     * Legt den Wert der logFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLogFile(String value) {
        this.logFile = value;
    }

    /**
     * Ruft den Wert der log4JPropertyFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLog4JPropertyFile() {
        return log4JPropertyFile;
    }

    /**
     * Legt den Wert der log4JPropertyFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLog4JPropertyFile(String value) {
        this.log4JPropertyFile = value;
    }

    /**
     * Ruft den Wert der debugLevel-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDebugLevel() {
        return debugLevel;
    }

    /**
     * Legt den Wert der debugLevel-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDebugLevel(Integer value) {
        this.debugLevel = value;
    }

    /**
     * Ruft den Wert der protocolCommandListener-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isProtocolCommandListener() {
        return protocolCommandListener;
    }

    /**
     * Legt den Wert der protocolCommandListener-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setProtocolCommandListener(Boolean value) {
        this.protocolCommandListener = value;
    }

}
