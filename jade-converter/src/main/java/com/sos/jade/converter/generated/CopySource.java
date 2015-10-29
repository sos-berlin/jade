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
 *         &lt;element ref="{}CopySourceFragmentRef"/>
 *         &lt;element ref="{}AlternativeCopySourceFragmentRef" maxOccurs="unbounded" minOccurs="0"/>
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
    "copySourceFragmentRef",
    "alternativeCopySourceFragmentRef",
    "sourceFileOptions"
})
@XmlRootElement(name = "CopySource")
public class CopySource {

    @XmlElement(name = "CopySourceFragmentRef", required = true)
    protected ReadableFragmentRefType copySourceFragmentRef;
    @XmlElement(name = "AlternativeCopySourceFragmentRef")
    protected List<AlternativeCopySourceFragmentRef> alternativeCopySourceFragmentRef;
    @XmlElement(name = "SourceFileOptions", required = true)
    protected SourceFileOptions sourceFileOptions;

    /**
     * Ruft den Wert der copySourceFragmentRef-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ReadableFragmentRefType }
     *     
     */
    public ReadableFragmentRefType getCopySourceFragmentRef() {
        return copySourceFragmentRef;
    }

    /**
     * Legt den Wert der copySourceFragmentRef-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ReadableFragmentRefType }
     *     
     */
    public void setCopySourceFragmentRef(ReadableFragmentRefType value) {
        this.copySourceFragmentRef = value;
    }

    /**
     * Gets the value of the alternativeCopySourceFragmentRef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the alternativeCopySourceFragmentRef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlternativeCopySourceFragmentRef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AlternativeCopySourceFragmentRef }
     * 
     * 
     */
    public List<AlternativeCopySourceFragmentRef> getAlternativeCopySourceFragmentRef() {
        if (alternativeCopySourceFragmentRef == null) {
            alternativeCopySourceFragmentRef = new ArrayList<AlternativeCopySourceFragmentRef>();
        }
        return this.alternativeCopySourceFragmentRef;
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
