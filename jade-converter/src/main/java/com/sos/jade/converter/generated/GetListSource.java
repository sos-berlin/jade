//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.10.09 um 03:42:30 PM CEST 
//


package com.sos.jade.converter.generated;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element ref="{}GetListSourceFragmentRef"/>
 *         &lt;element ref="{}AlternativeGetListSourceFragmentRef" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}SourceFileOptions"/>
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
    "getListSourceFragmentRef",
    "alternativeGetListSourceFragmentRef",
    "sourceFileOptions"
})
@XmlRootElement(name = "GetListSource")
public class GetListSource {

    @XmlElement(name = "GetListSourceFragmentRef", required = true)
    protected ListableFragmentRefType getListSourceFragmentRef;
    @XmlElement(name = "AlternativeGetListSourceFragmentRef")
    protected List<AlternativeGetListSourceFragmentRef> alternativeGetListSourceFragmentRef;
    @XmlElement(name = "SourceFileOptions", required = true)
    protected SourceFileOptions sourceFileOptions;

    /**
     * Ruft den Wert der getListSourceFragmentRef-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ListableFragmentRefType }
     *     
     */
    public ListableFragmentRefType getGetListSourceFragmentRef() {
        return getListSourceFragmentRef;
    }

    /**
     * Legt den Wert der getListSourceFragmentRef-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ListableFragmentRefType }
     *     
     */
    public void setGetListSourceFragmentRef(ListableFragmentRefType value) {
        this.getListSourceFragmentRef = value;
    }

    /**
     * Gets the value of the alternativeGetListSourceFragmentRef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the alternativeGetListSourceFragmentRef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlternativeGetListSourceFragmentRef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AlternativeGetListSourceFragmentRef }
     * 
     * 
     */
    public List<AlternativeGetListSourceFragmentRef> getAlternativeGetListSourceFragmentRef() {
        if (alternativeGetListSourceFragmentRef == null) {
            alternativeGetListSourceFragmentRef = new ArrayList<AlternativeGetListSourceFragmentRef>();
        }
        return this.alternativeGetListSourceFragmentRef;
    }

    /**
     * Ruft den Wert der sourceFileOptions-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SourceFileOptions }
     *     
     */
    public SourceFileOptions getSourceFileOptions() {
        return sourceFileOptions;
    }

    /**
     * Legt den Wert der sourceFileOptions-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SourceFileOptions }
     *     
     */
    public void setSourceFileOptions(SourceFileOptions value) {
        this.sourceFileOptions = value;
    }

}
