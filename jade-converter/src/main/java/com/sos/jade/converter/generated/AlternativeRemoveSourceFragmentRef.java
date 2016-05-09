package com.sos.jade.converter.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"directory"})
@XmlRootElement(name = "AlternativeRemoveSourceFragmentRef")
public class AlternativeRemoveSourceFragmentRef extends WriteableFragmentRefType {

    @XmlElement(name = "Directory")
    protected String directory;

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String value) {
        this.directory = value;
    }

}