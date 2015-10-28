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
 *         &lt;element ref="{}MailFragment" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}BackgroundServiceFragment" maxOccurs="unbounded" minOccurs="0"/>
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
    "mailFragment",
    "backgroundServiceFragment"
})
@XmlRootElement(name = "NotificationFragments")
public class NotificationFragments {

    @XmlElement(name = "MailFragment")
    protected List<MailFragment> mailFragment;
    @XmlElement(name = "BackgroundServiceFragment")
    protected List<BackgroundServiceFragment> backgroundServiceFragment;

    /**
     * Gets the value of the mailFragment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mailFragment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMailFragment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MailFragment }
     * 
     * 
     */
    public List<MailFragment> getMailFragment() {
        if (mailFragment == null) {
            mailFragment = new ArrayList<MailFragment>();
        }
        return this.mailFragment;
    }

    /**
     * Gets the value of the backgroundServiceFragment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the backgroundServiceFragment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBackgroundServiceFragment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BackgroundServiceFragment }
     * 
     * 
     */
    public List<BackgroundServiceFragment> getBackgroundServiceFragment() {
        if (backgroundServiceFragment == null) {
            backgroundServiceFragment = new ArrayList<BackgroundServiceFragment>();
        }
        return this.backgroundServiceFragment;
    }

}
