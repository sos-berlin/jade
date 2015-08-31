//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.08.28 um 03:50:36 PM CEST 
//


package com.sos.jade.converter.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für AltWriteableFragmentRefType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="AltWriteableFragmentRefType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element ref="{}AltFTPFragmentRef"/>
 *         &lt;element ref="{}AltFTPSFragmentRef"/>
 *         &lt;element ref="{}AltSFTPFragmentRef"/>
 *         &lt;element ref="{}AltSMBFragmentRef"/>
 *         &lt;element ref="{}AltWebDAVFragmentRef"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AltWriteableFragmentRefType", propOrder = {
    "altFTPFragmentRefOrAltFTPSFragmentRefOrAltSFTPFragmentRef"
})
@XmlSeeAlso({
    WriteableAlternativeFragment.class
})
public class AltWriteableFragmentRefType {

    @XmlElements({
        @XmlElement(name = "AltFTPFragmentRef", type = AltFTPFragmentRef.class),
        @XmlElement(name = "AltFTPSFragmentRef", type = AltFTPSFragmentRef.class),
        @XmlElement(name = "AltSFTPFragmentRef", type = AltSFTPFragmentRef.class),
        @XmlElement(name = "AltSMBFragmentRef", type = AltSMBFragmentRef.class),
        @XmlElement(name = "AltWebDAVFragmentRef", type = AltWebDAVFragmentRef.class)
    })
    protected List<Object> altFTPFragmentRefOrAltFTPSFragmentRefOrAltSFTPFragmentRef;

    /**
     * Gets the value of the altFTPFragmentRefOrAltFTPSFragmentRefOrAltSFTPFragmentRef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the altFTPFragmentRefOrAltFTPSFragmentRefOrAltSFTPFragmentRef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAltFTPFragmentRefOrAltFTPSFragmentRefOrAltSFTPFragmentRef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AltFTPFragmentRef }
     * {@link AltFTPSFragmentRef }
     * {@link AltSFTPFragmentRef }
     * {@link AltSMBFragmentRef }
     * {@link AltWebDAVFragmentRef }
     * 
     * 
     */
    public List<Object> getAltFTPFragmentRefOrAltFTPSFragmentRefOrAltSFTPFragmentRef() {
        if (altFTPFragmentRefOrAltFTPSFragmentRefOrAltSFTPFragmentRef == null) {
            altFTPFragmentRefOrAltFTPSFragmentRefOrAltSFTPFragmentRef = new ArrayList<Object>();
        }
        return this.altFTPFragmentRefOrAltFTPSFragmentRefOrAltSFTPFragmentRef;
    }

}
