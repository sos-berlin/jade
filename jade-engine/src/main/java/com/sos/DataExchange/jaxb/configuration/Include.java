//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB)
// Reference Implementation, v2.2.4
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source
// schema.
// Generated on: 2012.08.21 at 08:04:29 PM CEST
//

package com.sos.DataExchange.jaxb.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/** <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="href" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="parse" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre> */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "include", namespace = "http://www.w3.org/2001/XInclude")
public class Include {

    @XmlAttribute(name = "href")
    protected String href;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "parse")
    protected String parse;

    /** Gets the value of the href property.
     * 
     * @return possible object is {@link String } */
    public String getHref() {
        return href;
    }

    /** Sets the value of the href property.
     * 
     * @param value allowed object is {@link String } */
    public void setHref(final String value) {
        href = value;
    }

    /** Gets the value of the type property.
     * 
     * @return possible object is {@link String } */
    public String getType() {
        return type;
    }

    /** Sets the value of the type property.
     * 
     * @param value allowed object is {@link String } */
    public void setType(final String value) {
        type = value;
    }

    /** Gets the value of the parse property.
     * 
     * @return possible object is {@link String } */
    public String getParse() {
        return parse;
    }

    /** Sets the value of the parse property.
     * 
     * @param value allowed object is {@link String } */
    public void setParse(final String value) {
        parse = value;
    }

}
