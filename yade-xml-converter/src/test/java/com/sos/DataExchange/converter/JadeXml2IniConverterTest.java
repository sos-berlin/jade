package com.sos.DataExchange.converter;

import java.io.StringReader;

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
        StringBuilder inputXml = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        inputXml.append("<Configurations>");
        inputXml.append("<Fragments><ProtocolFragments><SFTPFragment name=\"xxx\">");
        inputXml.append("<BasicConnection><Hostname><![CDATA[xxx]]></Hostname></BasicConnection>");
        inputXml.append("<SSHAuthentication><Account><![CDATA[xxx]]></Account></SSHAuthentication>");
        inputXml.append("</SFTPFragment></ProtocolFragments></Fragments>");
        inputXml.append("<Profiles><Profile profile_id=\"aaa\">");
        inputXml.append("<Operation><Copy>");
        inputXml.append("<CopySource><CopySourceFragmentRef><SFTPFragmentRef ref=\"xxx\" /></CopySourceFragmentRef><SourceFileOptions>");
        inputXml.append("<Selection><FilePathSelection><FilePath><![CDATA[sss]]></FilePath></FilePathSelection></Selection>");
        inputXml.append("<Directives><DisableErrorOnNoFilesFound>false</DisableErrorOnNoFilesFound></Directives>");
        inputXml.append("</SourceFileOptions></CopySource>");
        inputXml.append("<CopyTarget>");
        inputXml.append("<CopyTargetFragmentRef><LocalTarget /></CopyTargetFragmentRef><Directory><![CDATA[sss]]></Directory>");
        inputXml.append("</CopyTarget>");
        inputXml.append("</Copy></Operation>");
        inputXml.append("</Profile></Profiles>");
        inputXml.append("</Configurations>");

        JadeXml2IniConverter converter = new JadeXml2IniConverter();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(inputXml.toString()));

        StringBuilder header = new StringBuilder("#######################").append(JadeXml2IniConverter.NEW_LINE);
        header.append("# Header").append(JadeXml2IniConverter.NEW_LINE);
        header.append("#######################").append(JadeXml2IniConverter.NEW_LINE);
        byte[] result = converter.process(new InputSource(SCHEMA_PATH), is, header);

        LOGGER.info("result:" + new String(result));
    }
}