package com.sos.DataExchange;

import java.io.File;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.DataExchange.options.JADEOptions;
import com.sos.JSHelper.Basics.JSJobUtilitiesClass;
import com.sos.vfs.common.SOSVFSFactory;

public class JadeBaseEngine extends JSJobUtilitiesClass<JADEOptions> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JadeBaseEngine.class);
    private boolean isLoggerConfigured = false;

    @SuppressWarnings("deprecation")
    public JadeBaseEngine() {
    }

    public JadeBaseEngine(final JADEOptions opt) {
        super(opt);
    }

    public void setLogger() {
        if (isLoggerConfigured) {
            return;
        }
        SOSVFSFactory.setParentLogger(SOSDataExchangeEngine.JADE_LOGGER_NAME);
        LoggerContext context = getLoggerContext();
        Level level = checkLevel();
        if (level == null) {
            isLoggerConfigured = true;
            return;
        }

        Configuration configuration = context.getConfiguration();
        if (level.equals(Level.INFO)) {
            // Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.INFO);
            configuration.getRootLogger().setLevel(Level.INFO);
            context.updateLoggers();
        } else if (level.equals(Level.DEBUG)) {
            // Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.DEBUG);
            configuration.getRootLogger().setLevel(Level.DEBUG);
            context.updateLoggers();

            LOGGER.debug(String.format("set loglevel to DEBUG due to option verbose = %s", objOptions.verbose.value()));
        } else if (level.equals(Level.TRACE)) {
            // Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.TRACE);
            configuration.getRootLogger().setLevel(Level.TRACE);
            context.updateLoggers();

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
}
