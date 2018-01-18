package com.sos.DataExchange.converter;

import org.junit.Test;

public class JadeXml2IniConverterTest {

    private static final String SCHEMA_PATH = "src/main/resources/YADE_configuration_v1.0.xsd";

    @Test
    public final void testConvert() throws Exception {
        JadeXml2IniConverter converter = new JadeXml2IniConverter();

        String inputXml = "src/test/resources/examples/yade_converter_test_settings.xml";
        String outputIni = "src/test/resources/examples/yade_converter_test_settings.ini";
        converter.process(SCHEMA_PATH, inputXml, outputIni);
    }
}