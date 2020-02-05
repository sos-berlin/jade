package com.sos.DataExchange.converter;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

public class JadeXml2IniConverterTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(JadeXml2IniConverterTest.class);
    private static final String SCHEMA_PATH = "http://localhost:4446/joc/xsd/yade/YADE_configuration_v1.12.xsd";

    @Ignore
    @Test
    public final void testConvert() throws Exception {
        String xmlFile = "src/test/resources/yade.xml";

        xmlFile = "src/test/resources/yade.xml";
        String xml = new String(Files.readAllBytes(Paths.get(xmlFile)));

        StringBuilder header = new StringBuilder("#######################").append(JadeXml2IniConverter.NEW_LINE);
        header.append("# Header").append(JadeXml2IniConverter.NEW_LINE);
        header.append("#######################");

        InputSource xmlInputSource = new InputSource();
        xmlInputSource.setCharacterStream(new StringReader(xml));

        JadeXml2IniConverter converter = new JadeXml2IniConverter();
        byte[] result = converter.process(new InputSource(SCHEMA_PATH), xmlInputSource, header);

        LOGGER.info("result:" + new String(result));
    }
}