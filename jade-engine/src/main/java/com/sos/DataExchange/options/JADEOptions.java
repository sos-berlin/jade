package com.sos.DataExchange.options;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.sos.DataExchange.converter.JadeXml2IniConverter;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.JSHelper.Options.SOSOptionTransferType.TransferTypes;
import com.sos.i18n.annotation.I18NResourceBundle;
import com.sos.vfs.common.options.SOSBaseOptions;

@I18NResourceBundle(baseName = "SOSVirtualFileSystem", defaultLocale = "en")
public class JADEOptions extends SOSBaseOptions {

    private static final long serialVersionUID = -5788970501747521212L;
    private static final Logger LOGGER = LoggerFactory.getLogger(JADEOptions.class);
    public static final String SCHEMA_RESSOURCE_NAME = "YADE_configuration_v1.12.xsd";

    public JADEOptions() {
        super();
    }

    public JADEOptions(final HashMap<String, String> settings) throws Exception {
        super(settings);
    }

    public JADEOptions(final TransferTypes source, final TransferTypes target) throws Exception {
        super(source, target);
    }

    public HashMap<String, String> readSettingsFile() {
        String config = settings.getValue();
        this.setOriginalSettingsFile(config);
        this.setDeleteSettingsFileOnExit(false);
        if (config.toLowerCase().endsWith(".xml")) {
            Path iniFile = convertXml2Ini(config);
            this.settings.setValue(iniFile.toString());
            this.setDeleteSettingsFileOnExit(true);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("[readSettingsFile]settings=%s", settings.getValue()));
        }
        return super.readSettingsFile(null);
    }

    public Path convertXml2Ini(String xmlFile) {
        String method = "convertXml2Ini";
        LOGGER.debug(String.format("%s: xmlFile=%s", method, xmlFile));

        InputStream schemaStream = null;
        Path tmpIniFile = null;
        try {
            schemaStream = loadSchemaFromJar();
            if (schemaStream == null) {
                throw new Exception(String.format("schema(%s) stream from the jar file is null", SCHEMA_RESSOURCE_NAME));
            }

            tmpIniFile = Files.createTempFile("sos.yade_settings_", ".ini");
            JadeXml2IniConverter converter = new JadeXml2IniConverter();
            converter.process(new InputSource(schemaStream), xmlFile, tmpIniFile.toString());

            LOGGER.debug(String.format("%s: converted to %s", method, tmpIniFile.toString()));
            return tmpIniFile;
        } catch (Exception e) {
            LOGGER.error(String.format("%s: exception=%s", method, e.toString()), e);
            if (tmpIniFile != null) {
                try {
                    Files.deleteIfExists(tmpIniFile);
                } catch (IOException e1) {
                }
            }
            throw new JobSchedulerException(e);
        } finally {
            if (schemaStream != null) {
                try {
                    schemaStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private InputStream loadSchemaFromJar() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = Class.class.getClassLoader();
        }
        try {
            URL url = cl.getResource(SCHEMA_RESSOURCE_NAME);
            LOGGER.debug(String.format("loadSchemaFromJar: schema=%s", url.toString()));
        } catch (Exception ex) {
        }
        return cl.getResourceAsStream(SCHEMA_RESSOURCE_NAME);
    }

    public JADEOptions getClone() {
        JADEOptions options = new JADEOptions();
        options.commandLineArgs(this.getOptionsAsCommandLine());
        return options;
    }

}
