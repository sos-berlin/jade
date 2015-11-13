//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.11.11 um 11:55:19 AM CET 
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
 *         &lt;element ref="{}HashAlgorithm" minOccurs="0"/>
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
    "hashAlgorithm"
})
@XmlRootElement(name = "CheckIntegrityHash")
public class CheckIntegrityHash {

    @XmlElement(name = "HashAlgorithm", defaultValue = "md5")
    protected String hashAlgorithm;

    /**
     * 
     *                      
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;div xmlns="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema"&gt;
     *                         &lt;strong&gt;References&lt;/strong&gt;
     *                         &lt;p&gt;&lt;ul&gt;&lt;li&gt;Schema: &lt;a href="https://www.sos-berlin.com/schema/yade/YADE_configuration_v1.0/YADE_configuration_v1_0_xsd_Element_CheckIntegrityHash.html" target="_blank"&gt;CheckIntegrityHash&lt;/a&gt;
     *                               &lt;/li&gt;&lt;li&gt;Parameter: &lt;a href="https://kb.sos-berlin.com/display/PKB/YADE+Parameter+Reference+-+"&gt;CheckIntegrityHash&lt;/a&gt;
     *                               &lt;/li&gt;
     *                            &lt;/ul&gt;
     *                         &lt;/p&gt;
     *                         &lt;strong&gt;Notes&lt;/strong&gt;
     *                         &lt;p&gt;
     * 			This parameter makes use of a checksum file that is available with the source of a file transfer.
     * 			The name of the checksum file is assumed to be the same as the source file with an additional extension &lt;em&gt;.md5&lt;/em&gt;.
     * 			During file transfer the contents of the checksum file is compared with the checksum that is calculated from
     * 			the transfer of the file.
     * 		&lt;/p&gt;
     *                         &lt;p&gt;
     * 			When used with a jump host then integrity checking applies to source and jump host, 
     * 			not to the target of the transfer.
     * 		&lt;/p&gt;
     *                         &lt;p&gt;
     * 			With this element being used a checksum file is expected on the source system and 
     * 			the integrity hash for the target file is calculated and compared with the 
     * 			respective integrity hash of the source file.
     * 			If the hashes are not equal then the file transfer will be rolled back.
     * 			If the checksum file on the source system is missing then this will be reported as an information
     * 			but will not affect the transfer of files.
     * 		&lt;/p&gt;
     *                      &lt;/div&gt;
     * </pre>
     * 
     *                   
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    /**
     * Legt den Wert der hashAlgorithm-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHashAlgorithm(String value) {
        this.hashAlgorithm = value;
    }

}
