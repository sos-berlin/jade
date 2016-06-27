package com.sos.jade.job;

import com.sos.JSHelper.Basics.JSToolBox;
import org.apache.log4j.Logger;

/** @author Uwe Risse */
public class JadeDeleteHistoryMain extends JSToolBox {

    protected JadeDeleteHistoryOptions objOptions = null;
    private static final Logger LOGGER = Logger.getLogger(JadeDeleteHistoryMain.class);

    public final static void main(String[] pstrArgs) {
        final String methodName = "JadeDeleteHistoryMain::Main";
        LOGGER.info("JadeDeleteHistory - Main");
        try {
            JadeDeleteHistory objM = new JadeDeleteHistory();
            JadeDeleteHistoryOptions objO = objM.getOptions();
            objO.commandLineArgs(pstrArgs);
            objM.Execute();
        } catch (Exception e) {
            LOGGER.error(methodName + ": " + "Error occured ..." + e.getMessage());
            int intExitCode = 99;
            LOGGER.error(String.format("JSJ-E-105: %1$s - terminated with exit-code %2$d", methodName, intExitCode), e);
            System.exit(intExitCode);
        }
        LOGGER.info(String.format("JSJ-I-106: %1$s - ended without errors", methodName));
    }

}