package com.sos.jade.converter;

import javax.xml.bind.JAXBException;

import org.junit.Test;

public class IniToXmlConverterTest {
	
	public IniToXmlConverterTest() {
		//
	}

	@Test
	public void testMain() throws JAXBException {
		String settingsFile = "C:/Temp/jade/settings/oh_settings.ini";
		String[] options = {"-settings="+settingsFile,"-outputDir=C:/Temp/jade/test"}; 
		IniToXmlConverter.main(options);
	}

}
