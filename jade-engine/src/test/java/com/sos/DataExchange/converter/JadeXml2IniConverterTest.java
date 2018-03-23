package com.sos.DataExchange.converter;

import org.junit.Test;

import com.sos.DataExchange.Options.JADEOptions;

public class JadeXml2IniConverterTest {

    private static final String SCHEMA_PATH = "src/main/resources/" + JADEOptions.SCHEMA_RESSOURCE_NAME;

    @Test
    public final void testConvert() throws Exception {
        String inputXml = "src/test/resources/examples/yade_converter_test_settings.xml";
        String outputIni = "src/test/resources/examples/yade_converter_test_settings.ini";

        JadeXml2IniConverter converter = new JadeXml2IniConverter();
        converter.process(SCHEMA_PATH, inputXml, outputIni);
    }
}