package com.sos.DataExchange;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Basics.JSJobUtilitiesClass;
import com.sos.VirtualFileSystem.Factory.VFSFactory;

public class JadeBaseEngine extends JSJobUtilitiesClass<JADEOptions> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JadeBaseEngine.class);
    private static boolean IS_LOGGER_CONFIGURED = false;

    @SuppressWarnings("deprecation")
    public JadeBaseEngine() {
    }

    public JadeBaseEngine(final JADEOptions pobjOptions) {
        super(pobjOptions);
    }

    public void setLogger() {
        if (IS_LOGGER_CONFIGURED) {
            return;
        }
        VFSFactory.setParentLogger(SOSDataExchangeEngine.JADE_LOGGER_NAME);

        int verbose = objOptions.verbose.value();
        if (verbose <= 1) {
            Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.INFO);
        } else {
            if (verbose > 8) {
                Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.TRACE);
                LOGGER.debug("set loglevel to TRACE due to option verbose = " + verbose);
            } else {
                Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.DEBUG);
                LOGGER.debug("set loglevel to DEBUG due to option verbose = " + verbose);
            }
        }
        IS_LOGGER_CONFIGURED = true;
    }
}
