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
 *         &lt;element ref="{}Selection"/>
 *         &lt;element ref="{}CheckSteadyState" minOccurs="0"/>
 *         &lt;element ref="{}Directives" minOccurs="0"/>
 *         &lt;element ref="{}Polling" minOccurs="0"/>
 *         &lt;element ref="{}ResultSet" minOccurs="0"/>
 *         &lt;element ref="{}SkipFiles" minOccurs="0"/>
 *         &lt;element ref="{}MaxFiles" minOccurs="0"/>
 *         &lt;element ref="{}CheckIntegrityHash" minOccurs="0"/>
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
    "selection",
    "checkSteadyState",
    "directives",
    "polling",
    "resultSet",
    "skipFiles",
    "maxFiles",
    "checkIntegrityHash"
})
@XmlRootElement(name = "SourceFileOptions")
public class SourceFileOptions {

    @XmlElement(name = "Selection", required = true)
    protected Selection selection;
    @XmlElement(name = "CheckSteadyState")
    protected CheckSteadyState checkSteadyState;
    @XmlElement(name = "Directives")
    protected Directives directives;
    @XmlElement(name = "Polling")
    protected Polling polling;
    @XmlElement(name = "ResultSet")
    protected ResultSet resultSet;
    @XmlElement(name = "SkipFiles")
    protected SkipFiles skipFiles;
    @XmlElement(name = "MaxFiles", defaultValue = "-1")
    protected Integer maxFiles;
    @XmlElement(name = "CheckIntegrityHash")
    protected CheckIntegrityHash checkIntegrityHash;

    /**
     * 
     *                      
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;div xmlns="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema"&gt;
     *                         &lt;strong&gt;References&lt;/strong&gt;
     *                         &lt;p&gt;&lt;ul&gt;&lt;li&gt;Schema: &lt;a href="https://www.sos-berlin.com/schema/yade/YADE_configuration_v1.0/YADE_configuration_v1_0_xsd_Element_SourceFileOptions.html" target="_blank"&gt;SourceFileOptions&lt;/a&gt;
     *                               &lt;/li&gt;&lt;li&gt;Parameter: &lt;a href="https://kb.sos-berlin.com/display/PKB/YADE+Parameter+Reference+-+Selection"&gt;SourceFileOptions&lt;/a&gt;
     *                               &lt;/li&gt;
     *                            &lt;/ul&gt;
     *                         &lt;/p&gt;
     *                         &lt;strong&gt;Notes&lt;/strong&gt;
     *                         &lt;p&gt;
     * 			These options apply to the handling of files on a source server. They specify e.g. the 
     * 			&lt;a href="https://kb.sos-berlin.com/display/PKB/YADE+Parameter+Reference+-+Selection" shape="rect"&gt;Selection&lt;/a&gt; of files for
     * 			&lt;a href="https://kb.sos-berlin.com/display/PKB/YADE+Parameter+Reference+-+Copy" shape="rect"&gt;Copy&lt;/a&gt; and
     * 			&lt;a href="https://kb.sos-berlin.com/display/PKB/YADE+Parameter+Reference+-+Move" shape="rect"&gt;Move&lt;/a&gt; operations.
     * 		&lt;/p&gt;
     *                      &lt;/div&gt;
     * </pre>
     * 
     *                   
     * 
     * @return
     *     possible object is
     *     {@link Selection }
     *     
     */
    public Selection getSelection() {
        return selection;
    }

    /**
     * Legt den Wert der selection-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Selection }
     *     
     */
    public void setSelection(Selection value) {
        this.selection = value;
    }

    /**
     * 
     *                      
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;div xmlns="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema"&gt;
     *                         &lt;strong&gt;References&lt;/strong&gt;
     *                         &lt;p&gt;&lt;ul&gt;&lt;li&gt;Schema: &lt;a href="https://www.sos-berlin.com/schema/yade/YADE_configuration_v1.0/YADE_configuration_v1_0_xsd_Element_SourceFileOptions.html" target="_blank"&gt;SourceFileOptions&lt;/a&gt;
     *                               &lt;/li&gt;&lt;li&gt;Parameter: &lt;a href="https://kb.sos-berlin.com/display/PKB/YADE+Parameter+Reference+-+CheckSteadyState"&gt;SourceFileOptions&lt;/a&gt;
     *                               &lt;/li&gt;
     *                            &lt;/ul&gt;
     *                         &lt;/p&gt;
     *                         &lt;strong&gt;Notes&lt;/strong&gt;
     *                         &lt;p&gt;
     * 			These options apply to the handling of files on a source server. They specify e.g. the 
     * 			&lt;a href="https://kb.sos-berlin.com/display/PKB/YADE+Parameter+Reference+-+Selection" shape="rect"&gt;Selection&lt;/a&gt; of files for
     * 			&lt;a href="https://kb.sos-berlin.com/display/PKB/YADE+Parameter+Reference+-+Copy" shape="rect"&gt;Copy&lt;/a&gt; and
     * 			&lt;a href="https://kb.sos-berlin.com/display/PKB/YADE+Parameter+Reference+-+Move" shape="rect"&gt;Move&lt;/a&gt; operations.
     * 		&lt;/p&gt;
     *                      &lt;/div&gt;
     * </pre>
     * 
     *                   
     * 
     * @return
     *     possible object is
     *     {@link CheckSteadyState }
     *     
     */
    public CheckSteadyState getCheckSteadyState() {
        return checkSteadyState;
    }

    /**
     * Legt den Wert der checkSteadyState-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CheckSteadyState }
     *     
     */
    public void setCheckSteadyState(CheckSteadyState value) {
        this.checkSteadyState = value;
    }

    /**
     * 
     *                      
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;div xmlns="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema"&gt;
     *                         &lt;strong&gt;References&lt;/strong&gt;
     *                         &lt;p&gt;&lt;ul&gt;&lt;li&gt;Schema: &lt;a href="https://www.sos-berlin.com/schema/yade/YADE_configuration_v1.0/YADE_configuration_v1_0_xsd_Element_SourceFileOptions.html" target="_blank"&gt;SourceFileOptions&lt;/a&gt;
     *                               &lt;/li&gt;&lt;li&gt;Parameter: &lt;a href="https://kb.sos-berlin.com/display/PKB/YADE+Parameter+Reference+-+Directives"&gt;SourceFileOptions&lt;/a&gt;
     *                               &lt;/li&gt;
     *                            &lt;/ul&gt;
     *                         &lt;/p&gt;
     *                         &lt;strong&gt;Notes&lt;/strong&gt;
     *                         &lt;p&gt;
     * 			These options apply to the handling of files on a source server. They specify e.g. the 
     * 			&lt;a href="https://kb.sos-berlin.com/display/PKB/YADE+Parameter+Reference+-+Selection" shape="rect"&gt;Selection&lt;/a&gt; of files for
     * 			&lt;a href="https://kb.sos-berlin.com/display/PKB/YADE+Parameter+Reference+-+Copy" shape="rect"&gt;Copy&lt;/a&gt; and
     * 			&lt;a href="https://kb.sos-berlin.com/display/PKB/YADE+Parameter+Reference+-+Move" shape="rect"&gt;Move&lt;/a&gt; operations.
     * 		&lt;/p&gt;
     *                      &lt;/div&gt;
     * </pre>
     * 
     *                   
     * 
     * @return
     *     possible object is
     *     {@link Directives }
     *     
     */
    public Directives getDirectives() {
        return directives;
    }

    /**
     * Legt den Wert der directives-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Directives }
     *     
     */
    public void setDirectives(Directives value) {
        this.directives = value;
    }

    /**
     * 
     *                      
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;div xmlns="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema"&gt;
     *                         &lt;strong&gt;References&lt;/strong&gt;
     *                         &lt;p&gt;&lt;ul&gt;&lt;li&gt;Schema: &lt;a href="https://www.sos-berlin.com/schema/yade/YADE_configuration_v1.0/YADE_configuration_v1_0_xsd_Element_SourceFileOptions.html" target="_blank"&gt;SourceFileOptions&lt;/a&gt;
     *                               &lt;/li&gt;&lt;li&gt;Parameter: &lt;a href="https://kb.sos-berlin.com/display/PKB/YADE+Parameter+Reference+-+Polling"&gt;SourceFileOptions&lt;/a&gt;
     *                               &lt;/li&gt;
     *                            &lt;/ul&gt;
     *                         &lt;/p&gt;
     *                         &lt;strong&gt;Notes&lt;/strong&gt;
     *                         &lt;p&gt;
     * 			These options apply to the handling of files on a source server. They specify e.g. the 
     * 			&lt;a href="https://kb.sos-berlin.com/display/PKB/YADE+Parameter+Reference+-+Selection" shape="rect"&gt;Selection&lt;/a&gt; of files for
     * 			&lt;a href="https://kb.sos-berlin.com/display/PKB/YADE+Parameter+Reference+-+Copy" shape="rect"&gt;Copy&lt;/a&gt; and
     * 			&lt;a href="https://kb.sos-berlin.com/display/PKB/YADE+Parameter+Reference+-+Move" shape="rect"&gt;Move&lt;/a&gt; operations.
     * 		&lt;/p&gt;
     *                      &lt;/div&gt;
     * </pre>
     * 
     *                   
     * 
     * @return
     *     possible object is
     *     {@link Polling }
     *     
     */
    public Polling getPolling() {
        return polling;
    }

    /**
     * Legt den Wert der polling-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Polling }
     *     
     */
    public void setPolling(Polling value) {
        this.polling = value;
    }

    /**
     * Ruft den Wert der resultSet-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ResultSet }
     *     
     */
    public ResultSet getResultSet() {
        return resultSet;
    }

    /**
     * Legt den Wert der resultSet-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultSet }
     *     
     */
    public void setResultSet(ResultSet value) {
        this.resultSet = value;
    }

    /**
     * Ruft den Wert der skipFiles-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SkipFiles }
     *     
     */
    public SkipFiles getSkipFiles() {
        return skipFiles;
    }

    /**
     * Legt den Wert der skipFiles-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SkipFiles }
     *     
     */
    public void setSkipFiles(SkipFiles value) {
        this.skipFiles = value;
    }

    /**
     * Ruft den Wert der maxFiles-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxFiles() {
        return maxFiles;
    }

    /**
     * Legt den Wert der maxFiles-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxFiles(Integer value) {
        this.maxFiles = value;
    }

    /**
     * Ruft den Wert der checkIntegrityHash-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CheckIntegrityHash }
     *     
     */
    public CheckIntegrityHash getCheckIntegrityHash() {
        return checkIntegrityHash;
    }

    /**
     * Legt den Wert der checkIntegrityHash-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CheckIntegrityHash }
     *     
     */
    public void setCheckIntegrityHash(CheckIntegrityHash value) {
        this.checkIntegrityHash = value;
    }

}
