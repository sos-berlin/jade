package com.sos.DataExchange;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.sos.DataExchange.converter.JadeXml2IniConverter;
import com.sos.JSHelper.Basics.JSJobUtilitiesClass;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.vfs.common.options.SOSBaseOptions;

public class JadeBaseEngine extends JSJobUtilitiesClass<SOSBaseOptions> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JadeBaseEngine.class);

    public static final String SCHEMA_RESSOURCE_NAME = "YADE_configuration_v1.12.xsd";

    private boolean isLoggerConfigured = false;

    @SuppressWarnings("deprecation")
    public JadeBaseEngine() {
    }

    public JadeBaseEngine(final SOSBaseOptions opt) {
        super(opt);
        setProvidersFromEnv();
        if (objOptions.settings.isDirty()) {
            CLIArgCache cache = new CLIArgCache();

            objOptions.setOptions(setOptionsFromFile());

            cache.restoreOptions();

            objOptions.settings.setNotDirty();
        } else {
            if (objOptions.operation.isDirty()) {
                objOptions.setChildClasses(objOptions.getSettings());
            }
        }
    }

    private void setProvidersFromEnv() {
        if (!objOptions.ssh_provider.isDirty()) {
            String val = SOSBaseOptions.getSSHProviderFromEnv();
            if (val != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(String.format("set ssh_provider=%s from environment", val));
                }
                objOptions.ssh_provider.setValue(val);
            }
        }
        if (!objOptions.webdav_provider.isDirty()) {
            String val = SOSBaseOptions.getWEBDAVProviderFromEnv();
            if (val != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(String.format("set webdav_provider=%s from environment", val));
                }
                objOptions.webdav_provider.setValue(val);
            }
        }
        if (!objOptions.smb_provider.isDirty()) {
            String val = SOSBaseOptions.getSMBProviderFromEnv();
            if (val != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(String.format("set smb_provider=%s from environment", val));
                }
                objOptions.smb_provider.setValue(val);
            }
        }
    }

    public void setLogger() {
        if (isLoggerConfigured) {
            return;
        }
        getLoggerContext();
        Level level = checkLevel();
        if (level == null) {
            isLoggerConfigured = true;
            return;
        }

        if (level.equals(Level.INFO)) {
            Configurator.setRootLevel(Level.INFO);
        } else if (level.equals(Level.DEBUG)) {
            Configurator.setRootLevel(Level.DEBUG);
            Configurator.setLevel("com.mchange", Level.INFO);
            Configurator.setLevel("org.hibernate", Level.INFO);
            Configurator.setLevel("org.hibernate.persister.entity.AbstractEntityPersister", Level.DEBUG);
            Configurator.setLevel("org.hibernate.SQL", Level.DEBUG);
            Configurator.setLevel("org.hibernate.loader.entity.plan.EntityLoader", Level.DEBUG);

            LOGGER.debug(String.format("set loglevel to DEBUG due to option verbose = %s", objOptions.verbose.value()));
        } else if (level.equals(Level.TRACE)) {
            Configurator.setRootLevel(Level.TRACE);
            Configurator.setLevel("com.mchange", Level.INFO);
            Configurator.setLevel("org.hibernate", Level.INFO);
            Configurator.setLevel("org.hibernate.persister.entity.AbstractEntityPersister", Level.DEBUG);
            Configurator.setLevel("org.hibernate.type.descriptor.sql", Level.TRACE);
            Configurator.setLevel("org.hibernate.SQL", Level.DEBUG);
            Configurator.setLevel("org.hibernate.loader.entity.plan.EntityLoader", Level.DEBUG);

            LOGGER.debug(String.format("set loglevel to TRACE due to option verbose = %s", objOptions.verbose.value()));
        }

        isLoggerConfigured = true;
    }

    private LoggerContext getLoggerContext() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        if (getOptions().log4jPropertyFileName.isDirty()) {
            File log4j = new File(getOptions().log4jPropertyFileName.getValue());
            if (log4j.isFile() && log4j.canRead()) {
                LOGGER.info(String.format("use log4j configuration file %s", getOptions().log4jPropertyFileName.getValue()));
                context.setConfigLocation(log4j.toURI());
                context.updateLoggers();
            } else {
                LOGGER.warn(String.format("log4j configuration file %s not found or is not readable", getOptions().log4jPropertyFileName.getValue()));
            }
        }
        return context;
    }

    private Level checkLevel() {
        Level level = null;
        int verbose = objOptions.verbose.value();
        if (verbose <= 1) {
            if (!LOGGER.isInfoEnabled()) {
                level = Level.INFO;
            }
        } else {
            if (verbose > 8) {
                if (!LOGGER.isTraceEnabled()) {
                    level = Level.TRACE;
                }
            } else {
                if (!LOGGER.isDebugEnabled()) {
                    level = Level.DEBUG;
                }
            }
        }
        return level;
    }

    private HashMap<String, String> setOptionsFromFile() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("[setOptionsFromFile]settings=%s", objOptions.settings.getValue()));
        }
        String config = objOptions.settings.getValue();
        objOptions.setOriginalSettingsFile(config);
        objOptions.setDeleteSettingsFileOnExit(false);
        if (config.toLowerCase().endsWith(".xml")) {
            Path iniFile = convertXml2Ini(config);
            objOptions.settings.setValue(iniFile.toString());
            objOptions.setDeleteSettingsFileOnExit(true);
        }
        return objOptions.readSettingsFile(null);
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

    private class CLIArgCache {

        private final String filePath;
        private final String fileSpec;
        private final String fileListName;
        private final String sourceDir;
        private final String sourceExcludedDirectories;
        private final String targetDir;
        private final String sshProvider;
        private final String webDAVProvider;
        private final String smbProvider;

        private CLIArgCache() {
            filePath = objOptions.filePath.isDirty() ? objOptions.filePath.getValue() : null;
            fileSpec = objOptions.fileSpec.isDirty() ? objOptions.fileSpec.getValue() : null;
            fileListName = objOptions.fileListName.isDirty() ? objOptions.fileListName.getValue() : null;
            sourceDir = objOptions.sourceDir.isDirty() ? objOptions.sourceDir.getValue() : null;
            sourceExcludedDirectories = objOptions.sourceExcludedDirectories.isDirty() ? objOptions.sourceExcludedDirectories.getValue() : null;
            targetDir = objOptions.targetDir.isDirty() ? objOptions.targetDir.getValue() : null;
            sshProvider = objOptions.ssh_provider.isDirty() ? objOptions.ssh_provider.getValue() : null;
            webDAVProvider = objOptions.webdav_provider.isDirty() ? objOptions.webdav_provider.getValue() : null;
            smbProvider = objOptions.smb_provider.isDirty() ? objOptions.smb_provider.getValue() : null;
        }

        private void restoreOptions() {
            if (filePath != null) {
                objOptions.filePath.setValue(filePath);
            }
            if (fileSpec != null) {
                objOptions.fileSpec.setValue(fileSpec);
            }
            if (fileListName != null) {
                objOptions.fileListName.setValue(fileListName);
            }
            if (sourceDir != null) {
                objOptions.sourceDir.setValue(sourceDir);
                try {
                    if (objOptions.getSource() != null) {
                        objOptions.getSource().directory.setValue(sourceDir);
                    }
                } catch (Throwable e) {
                }
            }

            if (sourceExcludedDirectories != null) {
                objOptions.sourceExcludedDirectories.setValue(sourceExcludedDirectories);
                try {
                    if (objOptions.getSource() != null) {
                        objOptions.getSource().excluded_directories.setValue(sourceExcludedDirectories);
                    }
                } catch (Throwable e) {
                }
            }

            if (targetDir != null) {
                objOptions.targetDir.setValue(targetDir);
                try {
                    if (objOptions.getTarget() != null) {
                        objOptions.getTarget().directory.setValue(targetDir);
                    }
                } catch (Throwable e) {
                }
            }
            if (sshProvider != null) {
                objOptions.ssh_provider.setValue(sshProvider);
            }
            if (webDAVProvider != null) {
                objOptions.webdav_provider.setValue(webDAVProvider);
            }
            if (smbProvider != null) {
                objOptions.smb_provider.setValue(smbProvider);
            }
        }
    }

}
