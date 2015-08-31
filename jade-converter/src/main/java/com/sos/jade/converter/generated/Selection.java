//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.08.28 um 03:50:36 PM CEST 
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
 *       &lt;choice>
 *         &lt;element ref="{}FilePathSelection"/>
 *         &lt;element ref="{}FileSpecSelection"/>
 *         &lt;element ref="{}FileListSelection"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "filePathSelection",
    "fileSpecSelection",
    "fileListSelection"
})
@XmlRootElement(name = "Selection")
public class Selection {

    @XmlElement(name = "FilePathSelection")
    protected FilePathSelection filePathSelection;
    @XmlElement(name = "FileSpecSelection")
    protected FileSpecSelection fileSpecSelection;
    @XmlElement(name = "FileListSelection")
    protected FileListSelection fileListSelection;

    /**
     * Ruft den Wert der filePathSelection-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FilePathSelection }
     *     
     */
    public FilePathSelection getFilePathSelection() {
        return filePathSelection;
    }

    /**
     * Legt den Wert der filePathSelection-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FilePathSelection }
     *     
     */
    public void setFilePathSelection(FilePathSelection value) {
        this.filePathSelection = value;
    }

    /**
     * Ruft den Wert der fileSpecSelection-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FileSpecSelection }
     *     
     */
    public FileSpecSelection getFileSpecSelection() {
        return fileSpecSelection;
    }

    /**
     * Legt den Wert der fileSpecSelection-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FileSpecSelection }
     *     
     */
    public void setFileSpecSelection(FileSpecSelection value) {
        this.fileSpecSelection = value;
    }

    /**
     * Ruft den Wert der fileListSelection-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FileListSelection }
     *     
     */
    public FileListSelection getFileListSelection() {
        return fileListSelection;
    }

    /**
     * Legt den Wert der fileListSelection-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FileListSelection }
     *     
     */
    public void setFileListSelection(FileListSelection value) {
        this.fileListSelection = value;
    }

}
