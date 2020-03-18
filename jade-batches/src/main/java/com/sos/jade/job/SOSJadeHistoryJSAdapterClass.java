package com.sos.jade.job;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sos.scheduler.job.JobSchedulerJobAdapter;
import sos.spooler.Spooler;

import com.sos.JSHelper.Basics.IJSCommands;

public class SOSJadeHistoryJSAdapterClass extends JobSchedulerJobAdapter implements IJSCommands {

    private static final Logger LOGGER = LoggerFactory.getLogger(SOSJadeHistoryJSAdapterClass.class);

    @Override
    public boolean spooler_process() throws Exception {
        try {
            super.spooler_process();
            doProcessing();
            return getSpoolerProcess().getSuccess();
        } catch (Exception e) {
            return false;
        }
    }

    private void doProcessing() throws Exception {
        IJSCommands objJSCommands = this;

        Object objSp = objJSCommands.getSpoolerObject();
        Spooler objSpooler = (Spooler) objSp;
        SOSJadeHistory objR = new SOSJadeHistory();
        SOSJadeHistoryOptions objO = objR.getOptions();
        objO.setCurrentNodeName(getCurrentNodeName(getSpoolerProcess().getOrder(), true));

        objO.setAllOptions(getSchedulerParameterAsProperties(getSpoolerProcess().getOrder()));

        String configuration_file = "";
        if (objO.getItem("configuration_file") != null) {
            LOGGER.debug("configuration_file from param");
            configuration_file = objO.configuration_file.getValue();
        } else {
            LOGGER.debug("configuration_file from scheduler");
            File f = new File(new File(objSpooler.configuration_directory()).getParent(), "hibernate.cfg.xml");
            configuration_file = f.getAbsolutePath();
        }

        objO.configuration_file.setValue(configuration_file);

        objO.checkMandatory();
        objR.setJSJobUtilites(this);
        objR.Execute();
    } // doProcessing

}
