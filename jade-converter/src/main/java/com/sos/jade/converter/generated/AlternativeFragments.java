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
 *         &lt;element ref="{}ReadableAlternativeFragment" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}WriteableAlternativeFragment" maxOccurs="unbounded" minOccurs="0"/>
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
    "readableAlternativeFragment",
    "writeableAlternativeFragment"
})
@XmlRootElement(name = "AlternativeFragments")
public class AlternativeFragments {

    @XmlElement(name = "ReadableAlternativeFragment")
    protected List<ReadableAlternativeFragment> readableAlternativeFragment;
    @XmlElement(name = "WriteableAlternativeFragment")
    protected List<WriteableAlternativeFragment> writeableAlternativeFragment;

    /**
     * Gets the value of the readableAlternativeFragment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the readableAlternativeFragment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReadableAlternativeFragment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReadableAlternativeFragment }
     * 
     * 
     */
    public List<ReadableAlternativeFragment> getReadableAlternativeFragment() {
        if (readableAlternativeFragment == null) {
            readableAlternativeFragment = new ArrayList<ReadableAlternativeFragment>();
        }
        return this.readableAlternativeFragment;
    }

    /**
     * Gets the value of the writeableAlternativeFragment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the writeableAlternativeFragment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWriteableAlternativeFragment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WriteableAlternativeFragment }
     * 
     * 
     */
    public List<WriteableAlternativeFragment> getWriteableAlternativeFragment() {
        if (writeableAlternativeFragment == null) {
            writeableAlternativeFragment = new ArrayList<WriteableAlternativeFragment>();
        }
        return this.writeableAlternativeFragment;
    }

}
