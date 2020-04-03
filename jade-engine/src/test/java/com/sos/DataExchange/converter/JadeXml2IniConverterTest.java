package com.sos.DataExchange.converter;

import org.junit.Ignore;
import org.junit.Test;

import com.sos.DataExchange.JadeBaseEngine;

public class JadeXml2IniConverterTest {

    private static final String SCHEMA_PATH = "src/main/resources/" + JadeBaseEngine.SCHEMA_RESSOURCE_NAME;

    @Ignore
    @Test
    public final void testConvert() throws Exception {
        String inputXml = "src/test/resources/examples/yade_converter_test_settings.xml";
        String outputIni = "src/test/resources/examples/yade_converter_test_settings.ini";

        JadeXml2IniConverter converter = new JadeXml2IniConverter();
        converter.process(SCHEMA_PATH, inputXml, outputIni);
    }

    @Ignore
    @Test
    public final void testConvertMain() throws Exception {
        String inputXml = "src/test/resources/examples/yade_converter_test_settings.xml";
        String outputIni = "src/test/resources/examples/yade_converter_test_settings.ini";
        String log4j = "src/test/resources/examples/log4j2.xml";

        String[] args = new String[4];
        args[0] = SCHEMA_PATH;
        args[1] = inputXml;
        args[2] = outputIni;
        args[3] = log4j;

        JadeXml2IniConverter.main(args);
    }
}