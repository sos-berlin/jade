package com.sos.jade.job;

import com.sos.JSHelper.Basics.JSToolBox;
import org.apache.log4j.Logger;

/** @author Uwe Risse */
public class SOSJadeHistoryMain extends JSToolBox {

    protected SOSJadeHistoryOptions objOptions = null;
    private static final Logger LOGGER = Logger.getLogger(SOSJadeHistoryMain.class);

    public final static void main(String[] pstrArgs) {
        final String methodName = "SOSJadeHistory::Main";
        LOGGER.info("SOSJadeHistory - Main");
        try {
            SOSJadeHistory objM = new SOSJadeHistory();
            SOSJadeHistoryOptions objO = objM.getOptions();
            objO.CommandLineArgs(pstrArgs);
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