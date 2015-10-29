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
 *         &lt;element ref="{}OrderJobChainName"/>
 *         &lt;element ref="{}CreateOrderForAllFiles" minOccurs="0"/>
 *         &lt;element ref="{}NextState" minOccurs="0"/>
 *         &lt;element ref="{}MergeOrderParameters" minOccurs="0"/>
 *         &lt;element ref="{}CreateOrderOnRemoteJobScheduler" minOccurs="0"/>
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
    "orderJobChainName",
    "createOrderForAllFiles",
    "nextState",
    "mergeOrderParameters",
    "createOrderOnRemoteJobScheduler"
})
@XmlRootElement(name = "CreateOrder")
public class CreateOrder {

    @XmlElement(name = "OrderJobChainName", required = true)
    protected String orderJobChainName;
    @XmlElement(name = "CreateOrderForAllFiles", defaultValue = "false")
    protected Boolean createOrderForAllFiles;
    @XmlElement(name = "NextState")
    protected String nextState;
    @XmlElement(name = "MergeOrderParameters", defaultValue = "false")
    protected Boolean mergeOrderParameters;
    @XmlElement(name = "CreateOrderOnRemoteJobScheduler")
    protected CreateOrderOnRemoteJobScheduler createOrderOnRemoteJobScheduler;

    /**
     * Ruft den Wert der orderJobChainName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderJobChainName() {
        return orderJobChainName;
    }

    /**
     * Legt den Wert der orderJobChainName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderJobChainName(String value) {
        this.orderJobChainName = value;
    }

    /**
     * Ruft den Wert der createOrderForAllFiles-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCreateOrderForAllFiles() {
        return createOrderForAllFiles;
    }

    /**
     * Legt den Wert der createOrderForAllFiles-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCreateOrderForAllFiles(Boolean value) {
        this.createOrderForAllFiles = value;
    }

    /**
     * Ruft den Wert der nextState-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNextState() {
        return nextState;
    }

    /**
     * Legt den Wert der nextState-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNextState(String value) {
        this.nextState = value;
    }

    /**
     * Ruft den Wert der mergeOrderParameters-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMergeOrderParameters() {
        return mergeOrderParameters;
    }

    /**
     * Legt den Wert der mergeOrderParameters-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMergeOrderParameters(Boolean value) {
        this.mergeOrderParameters = value;
    }

    /**
     * Ruft den Wert der createOrderOnRemoteJobScheduler-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CreateOrderOnRemoteJobScheduler }
     *     
     */
    public CreateOrderOnRemoteJobScheduler getCreateOrderOnRemoteJobScheduler() {
        return createOrderOnRemoteJobScheduler;
    }

    /**
     * Legt den Wert der createOrderOnRemoteJobScheduler-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CreateOrderOnRemoteJobScheduler }
     *     
     */
    public void setCreateOrderOnRemoteJobScheduler(CreateOrderOnRemoteJobScheduler value) {
        this.createOrderOnRemoteJobScheduler = value;
    }

}
